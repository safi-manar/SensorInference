import argparse
import json

from surveygizmo import SurveyGizmo

def parse_args():
    parser = argparse.ArgumentParser(description='Examine survey submissions and pay participants')
    parser.add_argument('apiPath', help='path to api.secret file')

    return parser.parse_args()

def read_json(path):
    json_data = open(path).read()
    return json.loads(json_data)

# Get API information
args = parse_args()
apiPath = args.apiPath

apiSecret = read_json(apiPath)

# Retrieve survey data
client = SurveyGizmo(
    api_version = apiSecret['sm_api_version'],
    api_token = apiSecret['sm_api_key'],
    api_token_secret = apiSecret['sm_api_secret']
)

response = client.api.survey.list()
data = response['data']
our_surveys = [survey for survey in data if survey['title'].lower().startswith('sensors inference')]

print our_surveys