import argparse
import json
import os
import sys

import pyrebase

def parse_args():
    parser = argparse.ArgumentParser(description='Dump Firebase database and storage to local computer')
    parser.add_argument('servicesJson', help='path to google-services.json file')
    parser.add_argument('serviceCredentials', help='path to app service credentials json file')
    parser.add_argument('-o', '--output', help='path to output directory')
    parser.add_argument('-c', '--clear', help='clear Firebase database and storage', action='store_true')
    parser.add_argument('--clear-no-prompt', help='clear Firebase database and storage without a warning prompt', action='store_true')

    return parser.parse_args()

def read_json(path):
    json_data = open(path).read()
    return json.loads(json_data)

# Get API information
args = parse_args()
services_json_data = read_json(args.servicesJson)
creds_json_path = args.serviceCredentials
clear_fb = args.clear or args.clear_no_prompt

outdir = os.getcwd()
if args.output != None:
    outdir = os.path.abspath(args.output)

#Configure and start Firebase instance
api_key = services_json_data['client'][0]['api_key'][0]['current_key']
auth_domain = services_json_data['project_info']['project_id'] + '.firebaseapp.com'
database_url = services_json_data['project_info']['firebase_url']
storage_bucket = services_json_data['project_info']['storage_bucket']
service_account = os.path.abspath(creds_json_path)

config = {
    "apiKey": api_key,
    "authDomain": auth_domain,
    "databaseURL": database_url,
    "storageBucket": storage_bucket,
    "serviceAccount": service_account
}

# Confirm clear
if clear_fb and not args.clear_no_prompt:
    print '\n***********************'
    print 'WARNING: THE FIREBASE DATABASE AND STORAGE AT ' + database_url + ' WILL BE CLEARED. TYPE accept TO CONTINUE, exit TO CANCEL'
    print '***********************'

    user_input = raw_input('accept or exit? ')

    if user_input.strip().lower() != 'accept':
        sys.exit(-1)

# Set up Firebase and output directory
firebase = pyrebase.initialize_app(config)
if not os.path.exists(outdir):
    os.makedirs(outdir)

# Dump database (and clear if specified)
db = firebase.database()
user_data = 'userData'
uuids = db.child(user_data).shallow().get()
if uuids.each() != None:
    for uuid in uuids.each():
        out_subdir = os.path.join(outdir, uuid)
        if not os.path.exists(out_subdir):
            os.makedirs(out_subdir)

        file_out = open(os.path.join(out_subdir, 'database.json'), 'w')

        data = db.child(user_data).child(uuid).get()
        json_dump = json.dumps(data.val(), sort_keys=True, indent=4, separators=(',', ': '))

        file_out.write(json_dump)
        file_out.close()

        if clear_fb:
            db.child(user_data).child(uuid).remove()


# Dump storage (and clear if specified)
storage = firebase.storage()
for file in storage.list_files():
    filepath = file.name
    tokens = filepath.split('/')

    if len(tokens) == 2:
        uuid = tokens[0]
        filename = tokens[1]

        out_subdir = os.path.join(outdir, uuid)
        if not os.path.exists(out_subdir):
            os.makedirs(out_subdir)

        file.download_to_filename(os.path.join(out_subdir, filename))

    if clear_fb:
        file.delete()

