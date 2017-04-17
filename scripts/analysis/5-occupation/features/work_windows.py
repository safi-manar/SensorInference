import pandas as pd
import numpy as np
import time
import datetime

# Given a UUID, scans the daily.csv survey source, and return a DataFrame
# with the uuid and start and end timestamps for the user's work schedule.
#  @ Columns = ['uuid', 'start', 'end']
#
# The script will scan the daily.csv file, filtering for only the uuid and valid non-Null values.
# Note that the time_start and time_end columns must be pre-processed in 24 hour 00:00 format.


# Main Method
def getWindows(uuid, DAILY_PATH):
    daily = read_data(DAILY_PATH)
    daily = filterValid(daily, uuid)
    daily = calculate_day(daily)
    daily = calculate_windows(daily)
    daily = cleanColumns(daily)

    return daily

# Read in the given daily csv, and return only the relevant columns for filtering and timestamp processing.
def read_data(file):
    daily = pd.read_csv(file)
    # Get only the relevant columns
    date = 'Date Submitted'
    time_start = 'What time did (or will) your workday start?:Work'
    time_end = 'What time did (or will) your workday end?:Work'
    uuid = 'uuid'
    work_today = 'Did you work today?:Work'
    daily = daily.loc[:, [uuid, work_today, date, time_start, time_end]]
    daily.columns = ['uuid', 'work_today', 'date', 'time_start', 'time_end']
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
