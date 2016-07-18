#import pyrebase
import argparse
import json
import os
import pyrebase

def parse_args():
    parser = argparse.ArgumentParser(description='Dump Firebase database and storage to local computer')
    parser.add_argument('servicesJson', help='path to google-services.json file')
    parser.add_argument('serviceCredentials', help='path to app service credentials json file')
    parser.add_argument('--output', help='path to output directory')

    return parser.parse_args()

def read_json(path):
    json_data = open(path).read()
    return json.loads(json_data)

# Get API information
args = parse_args()
services_json_data = read_json(args.servicesJson)
creds_json_path = args.serviceCredentials

# Configure and start Firebase instance
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

firebase = pyrebase.initialize_app(config)

# Dump database
db = firebase.database()

user_data = 'userData'
uuids = db.child(user_data).shallow().get()
# for uuid in uuids.each():
#     if not os.path.exists(uuid):
#         os.makedirs(uuid)
#
#     file_out = open(os.path.join(uuid, 'database.json'), 'w')
#
#     data = db.child(user_data).child(uuid).get()
#     json_dump = json.dumps(data.val(), sort_keys=True, indent=4, separators=(',', ': '))
#
#     file_out.write(json_dump)
#     file_out.close()

# Dump storage
# TODO
storage = firebase.storage()

for uuid in uuids.each():
    zips = storage.child(uuid)
    for zip in zips.list_files():
        print zip
