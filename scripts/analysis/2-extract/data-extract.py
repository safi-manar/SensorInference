import argparse
import os
import shutil
import json
import csv

from zipfile import ZipFile
from zipfile import BadZipfile

def parse_args():
    """Specify a parser for a single mandatory command-line argument"""
    parser = argparse.ArgumentParser(description='generate of measurement data as produced by firebase-dump.py')
    parser.add_argument('dump_path', help='path to a single device\'s data dump')
    parser.add_argument('-o', '--out-path', help='path to the output directory, defaults to dump_path/extract')

    return parser.parse_args()

def sensor_headers():
    """Return a list of valid expected sensor headers in a database.json file"""
    return ['light', 'location', 'magmetic', 'power', 'pressure', 'proximity', 'rotation', 'screen', 'steps']

def process_batch(dump_path, out_path, batch_list):
    """Extract compressed batched files to dump_path/temp and generate CSVs for them"""
    temp_path = os.path.join(dump_path, 'temp')
    if not os.path.exists(temp_path):
        os.makedirs(temp_path)

    # Extract
    sensor = None
    for filename in batch_list:
        zip_path = os.path.join(dump_path, filename)

        if sensor is None:
            sensor = filename.split('--')[0]

        try:
            zip = ZipFile(zip_path, 'r')
            zip.extractall(temp_path)
            zip.close()
        except BadZipfile:
            print 'Encountered bad zip file "%s", attempting to fix' % zip_path

            try:
                fixBadZipfile(zip_path)
                zip = ZipFile(zip_path, 'r')
                zip.extractall(temp_path)
                zip.close()
            except BadZipfile:
                print '"%s" can\'t be fixed, skipping' % zip_path

    # Generate CSV
    csv_file = open(os.path.join(out_path, '%s.csv' % sensor), 'wb')
    csv_writer = csv.writer(csv_file)
    extracted = [filename for filename in os.listdir(temp_path) if filename.startswith(sensor) and filename.endswith('json')]
    batch_headers = None
    for filename in extracted:
        batch = read_json(os.path.join(temp_path, filename))
        for batch_num in batch.keys():
            batch_data = batch[batch_num]
            if batch_headers is None:
                batch_headers = list(batch_data.keys())
                csv_writer.writerow(batch_headers)
            data_row = [batch_data[header] for header in batch_headers]
            csv_writer.writerow(data_row)

    # Clean up
    shutil.rmtree(temp_path)


def fixBadZipfile(zipFile):  
    """Attempt to fix corrupt zip files, 
       from https://stackoverflow.com/questions/3083235/unzipping-file-results-in-badzipfile-file-is-not-a-zip-file"""

    if not os.path.exists(zipFile + '.original'):
        # Make a copy of the original file before operating on it
        shutil.copyfile(zipFile, zipFile + '.original')

    f = open(zipFile, 'r+b')  
    data = f.read()  
    pos = data.find('\x50\x4b\x05\x06') # End of central directory signature  
    if (pos > 0):  
        self._log("Trancating file at location " + str(pos + 22)+ ".")  
        f.seek(pos + 22)   # size of 'ZIP end of central directory record' 
        f.truncate()  
        f.close()  
    else:  
        # raise error, file is truncated  
        raise BadZipfile

def read_json(path):
    """Return a dict representation of a JSON file"""
    json_data = open(path).read()
    return json.loads(json_data)


# Get the target directory and do some sanity checks on it
args = parse_args()
dump_path = args.dump_path
out_path = args.out_path if args.out_path is not None else os.path.join(dump_path, 'extract')
db_path = os.path.join(dump_path, 'database.json')

is_folder = os.path.isdir(dump_path)
has_database = is_folder and os.path.exists(db_path) and os.path.isfile(db_path)
is_valid = is_folder and has_database

if not os.path.exists(out_path):
    os.makedirs(out_path)

# Process valid folders
if is_valid:
    # Check for any batched data
    sensor_headers = sensor_headers()
    file_list = os.listdir(dump_path)
    batched_accel = [filename for filename in file_list if filename.startswith('accelerometer') and filename.endswith('zip')]
    batched_gyro = [filename for filename in file_list if filename.startswith('gyroscope') and filename.endswith('zip')]

    if len(batched_accel) == 0:
        sensor_headers.append('accelerometer')
    else:
        process_batch(dump_path, out_path, batched_accel)
    if len(batched_gyro) == 0:
        sensor_headers.append('gyroscope')
    else:
        process_batch(dump_path, out_path, batched_gyro)

    # Read in the database and generate CSVs for the sensors
    db = read_json(db_path)
    present_sensors = [sensor for sensor in sensor_headers if sensor in db.keys()]
    for sensor in present_sensors:
        sensor_data = db[sensor]

        sensor_headers = None
        csv_file = open(os.path.join(out_path, '%s.csv' % sensor), 'wb')
        csv_writer = csv.writer(csv_file)

        for readable_time in sensor_data.keys():
            data_point = sensor_data[readable_time]
            if sensor_headers is None:
                sensor_headers = list(data_point.keys())
                csv_writer.writerow(sensor_headers)
            data_row = [data_point[header] for header in sensor_headers]
            csv_writer.writerow(data_row)

    # Generate a CSV for generic system-wide events
    generic_key = 'generic'
    generic_headers = ['timestamp', 'timeReadable', 'intent']
    if generic_key in db.keys():
        generic_data = db[generic_key]

        generic_file = open(os.path.join(out_path, 'generic.csv'), 'wb')
        csv_writer = csv.writer(generic_file)
        csv_writer.writerow(generic_headers)

        for readable_time in generic_data.keys():
            data_point = generic_data[readable_time]
            for timestamp in data_point.keys():
                data_row = [timestamp, readable_time, data_point[timestamp]]
                csv_writer.writerow(data_row)
            
