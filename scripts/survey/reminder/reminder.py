import argparse
import json
import os

from datetime import datetime, timedelta

from boto.mturk.connection import MTurkConnection, MTurkRequestError

def parse_args():
    """Specify a parser for a single mandatory command-line argument"""
    parser = argparse.ArgumentParser(description='Examine MTURK and issue out email reminders')
    parser.add_argument('api_path', help='path to api.secret file')
    parser.add_argument('-d', '--days', help='the number of in the observation period (default: 7)', type=int, default=7)

    script_dir = os.path.dirname(os.path.realpath(__file__))
    default_sched = os.path.join(script_dir, 'schedule.json')
    parser.add_argument('-s', '--schedule-path', help='path to load/save a file containing the reminder schedule (default: <script-dir>/schedule.json', default=default_sched)

    return parser.parse_args()

def read_json(path):
    """Return a dict representation of a JSON file"""
    json_data = open(path).read()
    return json.loads(json_data)

# Get and check args
args = parse_args()

schedule_path = args.schedule_path
work_days = max(0, args.days - 1)   # Subtract becasue no need to remind on the first day
api_path = args.api_path if args.api_path != None and os.path.isfile(args.api_path) else None
if api_path != None:
    print 'Using API file "%s"' % api_path
else:
    print 'ERROR: API file argument "%s" does not exist' % api_path
    exit(1)

# Set up MTURK connection
api_secrets = read_json(api_path)
mtk = MTurkConnection(
    aws_access_key_id = api_secrets['mt_aws_key'],
    aws_secret_access_key = api_secrets['mt_aws_secret_key'],
    host = api_secrets['mt_host']
)

# Get our HIT
target_hit_title = api_secrets['mt_hit_title']
matching_hits = [hit for hit in mtk.get_all_hits() if hit.Title == target_hit_title and hit.HITStatus == 'Assignable']
hit = matching_hits[0] if len(matching_hits) > 0 else None
if hit != None:
    print 'Found HIT "%s" (ID# %s)' % (hit.Title, hit.HITId)
else:
    print 'ERROR: No matching HIT "%s" found' % target_hit_title
    exit(1)

# Find all approved assignments
hit_id = hit.HITId
approved = [asgn for asgn in mtk.get_assignments(hit_id, page_size=100) if asgn.AssignmentStatus == 'Approved']

# Load an existing schedule, if it exists
schedule = read_json(schedule_path) if os.path.isfile(schedule_path) else {}

# Issue out reminders 12 hours after submission, then every 24 after that, until the end of the observation period
current = datetime.now()
submission_offset = timedelta(hours=12)
reminder_period = timedelta(hours=24)
for appr in approved:
    worker = appr.WorkerId
    assignment = appr.AssignmentId

    if worker in schedule.keys(): # Read and process any existing schedule
        worker_schedule = schedule[worker]
        for rem_str in worker_schedule.keys():
            if worker_schedule[rem_str] == 0 and datetime.strptime(rem_str, '%Y-%m-%dT%H:%M:%S') < current:
                worker_schedule[rem_str] = 1

                # Issue reminder
                print 'Reminding %s' % worker
                mtk.notify_workers(worker, 'Reminder for "%s"' % target_hit_title, \
                                   '''This is a friendly reminder from the ICSI/UC Berkeley Sensors Inference team to fill out the activity survey, available through our app at approximately 8:00 PM in your timezone.  Please don\'t hesitate to contact our team at sensor-inference@icsi.berkeley.edu if you have any questions. Many thanks!''')
                break

    else:    # Generate a reminder schedule if there isn't already one
        submit_time = datetime.strptime(appr.SubmitTime, '%Y-%m-%dT%H:%M:%SZ')
        reminder = submit_time + submission_offset

        worker_schedule = {}
        for n in range(work_days):
            worker_schedule[reminder.isoformat()] = 0 if reminder > current else 1  # 1 if already past due, 0 otherwise
            reminder = reminder + reminder_period

        schedule[worker] = worker_schedule

# Write out the (new or edited) schedule
with open(schedule_path, 'w') as outfile:
    json.dump(schedule, outfile, sort_keys=True, indent=4)
    print 'Writing schedule to %s' % schedule_path

