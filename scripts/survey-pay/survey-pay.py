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

def get_valid_uuids(api_secrets):
    mtc = MTurkConnection(
        aws_access_key_id = api_secrets['mt_aws_key'],
        aws_secret_access_key = api_secrets['mt_aws_secret_key'],
        host = api_secrets['mt_host']
    )

    print mtc.get_account_balance()

    return

def process_survey_gizmo(api_secrets):
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

    process_entry_survey(entry_survey_id, client)

def process_entry_survey(entry_id, client):
    """Return UUIDs that should be paid for submitting the entry survey. Mark those as processed."""
    (uuid_id, processed_id) = identify_uuid_and_processed(entry_id, client)
    uuid_field = '[question(%d), option(0)]' % uuid_id
    processed_field = '[question(%d), option(0)]' % processed_id

    responses = client.api.surveyresponse \
                .filter('status', '!=', 'Deleted') \
                .filter(processed_field, '=', 'false') \
                .filter(uuid_field, 'IS NOT NULL', '') \
                .list(entry_id)
    uuids = [response[uuid_field] for response in responses['data']]

    response_ids = [response['id'] for response in responses['data']]
    for response_id in response_ids:
        kwargs = {'data[%d][0]' % processed_id: 'true'}     # change the "processed" field from false to true
        client.api.surveyresponse.update(entry_id, response_id, **kwargs)

    print uuids

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

get_valid_uuids(api_secrets)
#process_survey_gizmo(api_secrets)
