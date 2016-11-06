import argparse
import json
import datetime

from surveygizmo import SurveyGizmo
from boto.mturk.connection import MTurkConnection, MTurkRequestError
from boto.mturk.price import Price

def parse_args():
    """Specify a parser for a single mandatory command-line argument"""
    parser = argparse.ArgumentParser(description='Examine survey submissions and pay participants')
    parser.add_argument('api_path', help='path to api.secret file')

    return parser.parse_args()

def read_json(path):
    """Return a dict representation of a JSON file"""
    json_data = open(path).read()
    return json.loads(json_data)

def get_uuid_mturk_mapping():
    """Retrieve the mappings from the (short) UUID to the MTURK ID, and the MTURK ID to the assignment ID. Returned
       as a tuple in that order."""
    my_hit = None
    for hit in mtk.get_all_hits():
        if hit.Title == api_secrets['mt_hit_title']:
            my_hit = hit

    uuid_mapping = {}
    mturk_mapping = {}
    if my_hit is not None:
        id = my_hit.HITId
        for assignment in mtk.get_assignments(id, page_size=100):
            assignment_id = assignment.AssignmentId
            turk_id = assignment.WorkerId
            uuid = assignment.answers[0][0].fields[0]

            mturk_mapping[turk_id] = assignment_id
            uuid_mapping[uuid] = turk_id

    return (uuid_mapping, mturk_mapping)

def process_survey_gizmo(id_mapping):
    """Identify survey submissions associated with MTURK workers, mark those as processed, and return lists of 
       MTURK IDs to pay"""
    # Identify surveys that belong to this project
    surveys = sg.api.survey.list()
    survey_data = surveys['data']
    entry_survey_id = [survey['id'] for survey in survey_data if survey['title'] == api_secrets['sm_entry_survey_title']][0]
    daily_survey_id = [survey['id'] for survey in survey_data if survey['title'] == api_secrets['sm_daily_survey_title']][0]
    exit_survey_id = [survey['id'] for survey in survey_data if survey['title'] == api_secrets['sm_exit_survey_title']][0]

    # Find UUIDs present in both MTURK and SurveyGizmo
    entry_payment_uuids = process_survey(entry_survey_id, id_mapping)
    daily_payment_uuids = process_survey(daily_survey_id, id_mapping)
    exit_payment_uuids = process_survey(exit_survey_id, id_mapping)

    # Map those UUIDs back to the MTURK IDs for payment
    entry_payment_mturkers = [id_mapping[uuid] for uuid in entry_payment_uuids]
    daily_payment_mturkers = [id_mapping[uuid] for uuid in daily_payment_uuids]
    exit_payment_mturkers = [id_mapping[uuid] for uuid in exit_payment_uuids]

    return (entry_payment_mturkers, daily_payment_mturkers, exit_payment_mturkers)

def process_survey(survey_id, id_mapping):
    """Return UUIDs that should be paid for submitting entry survey. Mark those as processed."""
    (uuid_id, processed_id) = identify_uuid_and_processed(survey_id)
    uuid_field = '[question(%d), option(0)]' % uuid_id
    processed_field = '[question(%d), option(0)]' % processed_id

    responses = sg.api.surveyresponse \
                .filter('status', '!=', 'Deleted') \
                .filter(processed_field, '=', 'false') \
                .filter(uuid_field, 'IS NOT NULL', '') \
                .list(survey_id)
    response_data = responses['data']

    processed = []
    for response in response_data:
        response_id = response['id']
        uuid = response[uuid_field]
        short_uuid = uuid.split('-')[0]

        if short_uuid in id_mapping.keys():
            kwargs = {'data[%d][0]' % processed_id: 'true'}     # change the "processed" field from false to true
            sg.api.surveyresponse.update(survey_id, response_id, **kwargs)
            processed.append(short_uuid)

    return processed

def identify_uuid_and_processed(survey_id):
    """Return the IDs for the hidden actions containing the "processed" flag and UUID """
    uuid_id = -1
    processed_id = -1

    questions = sg.api.surveyquestion.list(survey_id)
    for question in questions['data']:
        title = question['title']['English']
        if title == 'uuid':
            uuid_id = question['id']
        elif title == 'processed':
            processed_id = question['id']

    return (uuid_id, processed_id)

def pay_worker(worker_ids, assignment_mapping, is_bonus=False, amount_usd=0.0, survey_type='unspecified'):
    for worker_id in worker_ids:
        assignment_id = assignment_mapping[worker_id]
        if is_bonus:
            # Log bonuses for daily and exit surveys
            log_message('Issued $%f bonus to worker %s for %s survey' % (amount_usd, worker_id, survey_type))
            log_message('assignment_id=%s,worker_id=%s,amount_usd=%f,survey_type=%s' % (assignment_id, worker_id, amount_usd, survey_type), level='BONUS')

        else:
            # Log assignment approval upon submission of the entry survey and app code
            try:
                mtk.approve_assignment(assignment_id, \
                                       feedback='Thank you for completing the %s survey! Please participate in the daily and exit surveys to receive bonuses.' % survey_type)

                log_message('Approved assignment %s given to worker %s for %s survey' % (assignment_id, worker_id, survey_type))
                log_message('assignment_id=%s,worker_id=%s,survey_type=%s' % (assignment_id, worker_id, amount_usd, survey_type), level='APPROVE')
            except MTurkRequestError as e:
                log_message('Failed to approve assignment %s to worker %s for %s survey' % (assignment_id, worker_id, survey_type), level='ERROR')
                log_message('Exception "%s"' % str(e).replace('\r','').replace('\n',''), level='ERROR')

def log_message(message, level='INFO'):
    """Print a message to the console with the timestamp"""
    current_time = datetime.datetime.now()
    formatted_time = current_time.isoformat()

    print '(%s) %s -- %s' % (level, formatted_time, message)

# Get API information
args = parse_args()
api_secrets = read_json(args.api_path)
sg = SurveyGizmo(
    api_version = api_secrets['sm_api_version'],
    api_token = api_secrets['sm_api_key'],
    api_token_secret = api_secrets['sm_api_secret']
)
mtk = MTurkConnection(
    aws_access_key_id = api_secrets['mt_aws_key'],
    aws_secret_access_key = api_secrets['mt_aws_secret_key'],
    host = api_secrets['mt_host']
)

(uuids_mturkids, mturkids_assignments) = get_uuid_mturk_mapping()
mturkers_to_pay = process_survey_gizmo(uuids_mturkids)

pay_worker(mturkers_to_pay[0], mturkids_assignments, survey_type = 'entry')
pay_worker(mturkers_to_pay[1], mturkids_assignments, is_bonus=True, amount_usd=1.00, survey_type = 'daily')
pay_worker(mturkers_to_pay[2], mturkids_assignments, is_bonus=True, amount_usd=2.00, survey_type = 'exit')
