import argparse
import json
import os

from boto.mturk.connection import MTurkConnection, MTurkRequestError
from boto.mturk.price import Price


def parse_args():
    """Specify a parser for a single mandatory command-line argument"""
    parser = argparse.ArgumentParser(description='Examine activity log and make bonus payments')
    parser.add_argument('api_path', help='path to api.secret file')
    parser.add_argument('log_path', help='path to survey-pay.py log file')

    return parser.parse_args()

def read_json(path):
    """Return a dict representation of a JSON file"""
    json_data = open(path).read()
    return json.loads(json_data)

class Payment():
    """Store information needed for payment"""
    def __init__(self, a_id=None, w_id=None, amount_usd=0.0, survey_type=None):
        self.a_id = a_id
        self.w_id = w_id
        self.amount_usd = amount_usd
        self.survey_type = survey_type

# Read in arguments and API keys
args = parse_args()
api_secrets = read_json(args.api_path)

# Set up MTURK connection
mtk = MTurkConnection(
    aws_access_key_id = api_secrets['mt_aws_key'],
    aws_secret_access_key = api_secrets['mt_aws_secret_key'],
    host = api_secrets['mt_host']
)

# Read in the log file
logfile = open(args.log_path, 'r')
all_successful = True
for line in logfile:
    line = line.strip()
    if line.startswith('(BONUS)'):
        # Example line: (BONUS) 14:50:29.790578 -- assignment_id=3URFVVM165HRF8024XP5YL7ISQ4ZU8,worker_id=A3S9CJQYDL3U2B,amount_usd=1.000000,survey_type=daily
        fields = {}
        info = line.split(' -- ')[1].strip().split(',')
        for field in info:
            (label, value) = field.split('=')
            fields[label] = value

        try:
            mtk.grant_bonus(fields['worker_id'], \
                            fields['assignment_id'], \
                            Price(float(fields['amount_usd'])), \
                            'Participation in a %s survey' % fields['survey_type'])
            print 'Successfully paid %s' % str(fields)
        except MTurkRequestError as e:
            print str(e)
            all_successful = False

# Rename the log file if it was all successfully processed
if all_successful:
    os.rename(args.log_path, args.log_path + '-processed')
