import argparse
import json

from surveygizmo import SurveyGizmo
from boto.mturk.connection import MTurkConnection

def parse_args():
    """Specify a parser for a single mandatory command-line argument"""
    parser = argparse.ArgumentParser(description='Examine survey submissions and pay participants')
    parser.add_argument('api_path', help='path to api.secret file')

    return parser.parse_args()

def read_json(path):
    """Return a dict representation of a JSON file"""
    json_data = open(path).read()
    return json.loads(json_data)

def get_uuid_mturkid_mapping(api_secrets):
    mtc = MTurkConnection(
        aws_access_key_id = api_secrets['mt_aws_key'],
        aws_secret_access_key = api_secrets['mt_aws_secret_key'],
        host = api_secrets['mt_host']
    )

    my_hit = None
    for hit in mtc.get_all_hits():
        if hit.Title == api_secrets['mt_hit_title']:
            my_hit = hit

    mapping = {}
    if my_hit is not None:
        id = my_hit.HITId
        for assignment in mtc.get_assignments(id):
            turk_id = assignment.WorkerId
            uuid = assignment.answers[0][0].fields[0]
            mapping[uuid] = turk_id

    return mapping

def process_survey_gizmo(api_secrets, id_mapping):
    client = SurveyGizmo(
        api_version = api_secrets['sm_api_version'],
        api_token = api_secrets['sm_api_key'],
        api_token_secret = api_secrets['sm_api_secret']
    )

    # Identify surveys that belong to this project
    surveys = client.api.survey.list()
    survey_data = surveys['data']
    entry_survey_id = [survey['id'] for survey in survey_data if survey['title'] == api_secrets['sm_entry_survey_title']][0]
    daily_survey_id = [survey['id'] for survey in survey_data if survey['title'] == api_secrets['sm_daily_survey_title']][0]

    valid_uuids = frozenset(id_mapping.keys())
    entry_payments = process_survey(entry_survey_id, valid_uuids, client)
    daily_payments = process_survey(daily_survey_id, valid_uuids, client)

    print entry_payments
    print daily_payments

def process_survey(survey_id, valid_uuids, client):
    """Return UUIDs that should be paid for submitting entry survey. Mark those as processed."""
    (uuid_id, processed_id) = identify_uuid_and_processed(survey_id, client)
    uuid_field = '[question(%d), option(0)]' % uuid_id
    processed_field = '[question(%d), option(0)]' % processed_id

    responses = client.api.surveyresponse \
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

        if short_uuid in valid_uuids:
            # kwargs = {'data[%d][0]' % processed_id: 'true'}     # change the "processed" field from false to true
            # client.api.surveyresponse.update(survey_id, response_id, **kwargs)
            processed.append(short_uuid)

    return processed

def identify_uuid_and_processed(survey_id, client):
    """Return the IDs for the hidden actions containing the "processed" flag and UUID """
    uuid_id = -1
    processed_id = -1

    questions = client.api.surveyquestion.list(survey_id)
    for question in questions['data']:
        title = question['title']['English']
        if title == 'uuid':
            uuid_id = question['id']
        elif title == 'processed':
            processed_id = question['id']

    return (uuid_id, processed_id)

# Get API information
args = parse_args()
api_secrets = read_json(args.api_path)

uuids_mturkids = get_uuid_mturkid_mapping(api_secrets)
process_survey_gizmo(api_secrets, uuids_mturkids)
