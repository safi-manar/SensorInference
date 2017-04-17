import pandas as pd
import numpy as np
import interpolate as interp
import time

import work_windows as ww

import os


THRESHOLD = 2.0
ACCEL_PATH = './data/in.csv'

ONE_SECOND = 1000 # 1000 ms

DATA_PATH = '/home/ioreyes/wearables/data/full-1/extract/batched'
ACCEL_NAME = 'accelerometer.csv.pprc'
DAILY_PATH = '/home/manar/scratch/5-occupation/data/daily_modified.csv'

def inMotion(DATA_PATH, ACCEL_NAME):
    uuidsBatched = os.listdir(DATA_PATH) # Get all the uuid's that have batched accelerometer
    uuidsDaily = getDailyUUIDs(DAILY_PATH) # Get all uuid's that submitted surveys.
    uuids = [uuid for uuid in uuidsDaily if uuid in uuidsBatched] # Find the intersection
    uuids = sorted(uuids)
    # Get paths to the accelerometer data for each uuid.
    accel_paths = [DATA_PATH + '/' + uuid + '/' + ACCEL_NAME for uuid in uuids]
    # Create tuples of (uuid, accel_path)
    accelPairs = [ (uuids[i], accel_paths[i])  for i in range(0, len(uuids))]

    print("Beginning inMotion analysis for " + str(len(uuids)) + " users...\n")

    proportions = [processUser(pair[0], pair[1]) for pair in accelPairs]

    # for pair in accelPairs:
    #     print(pair)

def getDailyUUIDs(DAILY_PATH):
    daily = pd.read_csv(DAILY_PATH)
    return daily['uuid'].unique()



def processUser(uuid, accel_path):
    print("\nReading in data for user: \'" + uuid + "\' ...")
    accel = read_data(accel_path)
    print("Getting work windows...")
    daily = getWindows(uuid)
    print("Partitioning work days...")
    days = partitionWorkTimes(accel, daily)
    print("Analyzing all work days...")
    values = analyze_days(days)
    print("Computing proportion...")
    proportion = thresholdedProportion(values)
    print("Analysis complete!")
    print("\t Proportion of time that the user is at rest: " + str(proportion))
    print("\t Proportion of time that the user is in motion: " + str(1 - proportion))

    return proportion


def read_data(path):
    accel = pd.read_csv(path)
    # Crop out invalid times
    current_time = time.time() * 1000 # Current time in ms
    accel = accel[accel['timestamp'] < current_time]
    # Get the time and L2 values
    ip = interp.interpolate(accel)
    df = pd.DataFrame()
    df['t'] = ip.index
    df['L2'] = ip.values
    # interpolate.py returns df['t'] in Datetime Timestamp format. Convert it back to ms (int)
    df = convert_time_format(df)
    return df

# Convert pandas.to_datetime Timestamp format to an integer time in ms
def convert_time_format(df, columName='t'):
    # Convert datetime format to time in microseconds
    df[columName] = df[columName].astype(int)
    # Convert microsecond to milliseconds
    df[columName] = df[columName] / 1000000
    return df

def getWindows(uuid):
    daily = ww.getWindows(uuid, DAILY_PATH)
    daily = convert_time_format(daily, 'start')
    daily = convert_time_format(daily, 'end')
    return daily

# Returns a list of accelerometer DataFrames partitioned according to the start/end timestamps in daily.
def partitionWorkTimes(accel, daily):
    days = []
    workdayCount = 0 # Number of work days from survey
    acceldataCount = 0 # Number of corresponding days from accelerometer
    # Iterate through the rows in daily.
    for index, row in daily.iterrows():
        workdayCount += 1
        start = row['start']
        end = row['end']
        workday = accel[(accel['t'] >= start) & (accel['t'] <= end)]
        if (workday.shape[0] == 0):
            start_timestamp = pd.Timestamp(start*1000000)
            end_timestamp = pd.Timestamp(end*1000000)
            print("\tNo accelerometer data collected for start: " + str(start_timestamp) + " and end: " + str(end_timestamp) )
        else:
            acceldataCount += 1
            days.append(workday)

    print("\tOut of " + str(workdayCount) + " submitted work days, there were " + str(acceldataCount) + " corresponding days of accelerometer data collected.")

    return days

# Returns a pd.Series with the max(L2) - min(L2) values for 1 second intervals in the given workday.
def analyze_workday(wd):
    total_time = wd['t'].max() - wd['t'].min()
    num_intervals = int(total_time / 1000)
    values = pd.Series(index=[i for i in range(0, num_intervals)])

    start = wd['t'].min() # First entry in time column.
    end = start + ONE_SECOND
    index = 0
    # Iterate through 1s intervals until wd is empty.
    while (index < num_intervals):
        interval = wd[(wd['t'] >= start) & (wd['t'] < end)] # A 1 second interval.
        # Check that there were at least 60% data points collected.
        if (interval.shape[0] > 30):
            min = interval['L2'].min()
            max = interval['L2'].max()
            val = max - min
            values[index] = val

        # New interval
        start = end
        end = start + ONE_SECOND
        index += 1

    return values


# Given a list of DataFrames of accel data, return a Series of max(L2) - min(L2) for 1s intervals.
def analyze_days(days):
    valuesList = [analyze_workday(workday) for workday in days] # A list of Series for each workday.
    # Concatenate the series of workdays together.
    values = pd.Series()
    for series in valuesList:
        values = values.append(series)
    return values


# Given a series of values, compute the proportion of 1s interval values that indicate the user is at rest.
def thresholdedProportion(values):
    thresholded = values[values <= THRESHOLD] # Select threshold values
    proportion = float(thresholded.size) / float(values.size) # Use float division
    return proportion



# Process the single user, for testing.
#processUser(UUID, ACCEL_PATH)


# In motion script:
inMotion(DATA_PATH, ACCEL_NAME)
