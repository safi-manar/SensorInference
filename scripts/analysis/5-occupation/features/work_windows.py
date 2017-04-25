import pandas as pd
import numpy as np
import time
import datetime as dt

# Given a UUID, scans the daily_coded.csv survey source, and return a DataFrame
# with the uuid and start and end timestamps for the user's work schedule.
#  @ Columns = ['uuid', 'start', 'end']
#
# The script will scan the daily_coded.csv file, filtering for only the uuid and valid non-Null values.
# Note that the time_start and time_end columns must be pre-processed in 24 hour 00:00 format.*
# *This assumption is satisfied by using the daily_coded.csv (manually coded version of daily.csv)


DAILY_PATH = '/home/manar/scratch/5-occupation/data/daily_coded.csv'
TZ_PATH = '/home/manar/scratch/5-occupation/data/timezones.csv'

# Main Method
def getWindows(uuid, DAILY_PATH=DAILY_PATH, TZ_PATH=TZ_PATH):
    daily = read_data(DAILY_PATH)
    daily = fix_timezones(daily, TZ_PATH)
    daily = filterValid(daily, uuid)
    daily = calculate_day(daily)
    daily = calculate_windows(daily)
    daily = cleanColumns(daily)
    daily = check24Hour(daily)

    return daily

# Read in the given daily csv, and return only the relevant columns for filtering and timestamp processing.
# Use the coded start / end times.
def read_data(file):
    daily = pd.read_csv(file)
    # Get only the relevant columns
    date = 'Date Submitted'
    time_start = 'code-start'
    time_end = 'code-end'
    uuid = 'uuid'
    work_today = 'Did you work today?:Work'
    daily = daily.loc[:, [uuid, work_today, date, time_start, time_end]]
    daily.columns = ['uuid', 'work_today', 'date', 'time_start', 'time_end']
    return daily

def calculateOffset(tz):
    if tz == 'PST':
        return dt.timedelta(0)
    elif tz == 'MST':
        return dt.timedelta(hours=1)
    elif tz == 'CST':
        return dt.timedelta(hours=2)
    elif tz == 'EST':
        return dt.timedelta(hours=3)
    elif tz == 'AST':
        return dt.timedelta(hours=4)
    elif tz == 'GMT+08:00':
        return dt.timedelta(hours=15)

# The 'date' submitted data from Daily.csv is fixed in PST. This function adjusts the times
# by shifting the times according to the
def fix_timezones(daily, TZ_PATH):

    tz = pd.read_csv(TZ_PATH)
    daily = daily.sort(['uuid']) # Sort by uuid
    tz = tz.sort(['uuid'])
    tz['offset'] = tz['zone'].apply(calculateOffset)

    # Now, merge, keeping the uuid's of daily (the left table).
    daily = pd.merge(daily, tz, how='left', on='uuid')

    # Convert to Timestamp objects and add the offset
    daily['date'] = pd.to_datetime(daily['date'], infer_datetime_format=True)
    daily['date'] = daily['date'] + daily['offset']

    # Convert back to String.
    daily['date'] = daily['date'].apply(lambda date: str(date))

    # To maintain the previously enforced invariant
    daily = daily.loc[:, ['uuid', 'work_today', 'date', 'time_start', 'time_end']]


    return daily


# Applies the following filters:
#   1. Filter by matching UUID.
#   2. Filter by entries where subjet answered the questions.
#   3. Filter by instances where the subject actually worked that day.
def filterValid(daily, uuid):
    # Filter by uuid
    daily = daily[daily['uuid'] == uuid]
    # Filter only those subjects that answered the work_today, time_start / time_end questions. (All other columns must have entries automatically.)
    daily = daily.dropna(how='any')
    # Filter only the days in which the subject worked
    daily = daily[daily.work_today.str.contains('Yes', regex=True)]
    return daily


# Based on the survey Date Submitted, calculate the day of the date.
def calculate_day(daily):
    # Conver to Timestamp objects
    daily['date'] = pd.to_datetime(daily['date'], infer_datetime_format=True)
    # Extract only the day (Normalize timestamps to midnight)
    daily['date'] = daily['date'].apply(lambda date: pd.to_datetime(date.date()))
    return daily

# Given the date of the survey submission and start/end work times, create start and end timestamp columns.
def calculate_windows(daily):
    daily['start_stamp'] = daily['date']
    # Convert datetime to string.
    daily['start_stamp'] = daily['start_stamp'].apply(lambda date: str(date.date()))
    # Concatenate the start times.
    daily['start_stamp'] = daily['start_stamp'] + ' ' + daily['time_start']
    # Convert new string to datetime
    daily['start_stamp'] = pd.to_datetime(daily['start_stamp'], infer_datetime_format=True)

    daily['end_stamp'] = daily['date']
    daily['end_stamp'] = daily['end_stamp'].apply(lambda date: str(date.date()))
    daily['end_stamp'] = daily['end_stamp'] + ' ' + daily['time_end']
    # Convert new string to datetime
    daily['end_stamp'] = pd.to_datetime(daily['end_stamp'], infer_datetime_format=True)
    return daily


# Return only the relevant columns.
def cleanColumns(daily):
    daily = daily.loc[:, ['uuid', 'start_stamp', 'end_stamp']]
    daily.columns = ['uuid', 'start', 'end']
    return daily



# Check the case in which the user has worked through the day, past midnight. In this case,
#   the end timestamp will appear to be less than the start timestamp (ie, for a night shift, from start: 22:00 to end: 4:00)
#   So, assume that if the end_stamp is less than start_stamp, then end_stamp actually belongs to the next day.
def check24Hour(daily):
    ONE_DAY = dt.timedelta(days=1)

    # Then, select nly the 'end' Series of the cases where end < start, and edit those to add 1 Day.
    daily['end'][daily['end'] < daily['start']] = daily['start'] + ONE_DAY

    return daily
