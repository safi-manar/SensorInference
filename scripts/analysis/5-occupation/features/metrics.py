import pandas as pd
import numpy as np
import time
import interpolate as interp
import datetime

# An experimental script for reading in a user's data and reporting some metrics
# on a window in which the user's device is at rest.
# Note that the start and end time for the window is programmer-defined-- get these
# values by manually observing a plot of a user's data from visualizer.py.


FILE = './data/in.csv'
# ***Set this for proper get_sleep() function behavior.
SLEEP_START = 1480955400000 # 12-05-16 8:30 AM
SLEEP_END = 1480962600000 # 12-05-16 10:30 AM
ONE_SECOND = 1000 # 1000 ms

def read_data(file):
    accel = pd.read_csv(file)
    # Crop out invalid times
    current_time = time.time() * 1000 # Current time in ms
    accel = accel[accel['timestamp'] < current_time]
    # Get the time and L2 values
    ip = interp.interpolate(accel)
    df = pd.DataFrame()
    df['t'] = ip.index
    df['L2'] = ip.values
    return df

# interpolate.py returns df['t'] in Datetime Timestamp format. Convert it back to ms (int)
def convert_time_format(df):
    # Convert datetime format to time in microseconds
    df['t'] = df['t'].astype(int)
    # Convert microsecond to milliseconds
    df['t'] = df['t'] / 1000000
    return df

# Gets a subsection of the accel time
# Note that this requires setting values above ***
def get_sleep(df):
    df = df[df['t'] < SLEEP_END]
    df = df[df['t'] > SLEEP_START]
    if (df.shape[0] == 0):
        raise ValueError("Invalid START / END times.")
    return df


# Finds the max value - min value for 1 second intervals.
def analyze_intervals(df):
    total_time = df['t'].max() - df['t'].min()
    num_intervals = int(total_time / 1000)
    values = pd.Series(index=[i for i in range(0, num_intervals)])

    start = df['t'].iloc[0] # First value of column t
    end = start + ONE_SECOND
    index = 0
    # Iterate through 1s intervals until df is empty.
    while (index < num_intervals):
        interval = df[(df['t'] >= start) & (df['t'] < end)]
        if (interval.shape[0] > 30):
            min = interval['L2'].min()
            max = interval['L2'].max()
            val = max - min
            values[index] = val
            updates[index] = val

        # New interval
        start = end
        end = start + ONE_SECOND
        index += 1

    return values


def print_info(vals):
    mx = vals.max()
    mn = vals.min()
    avg = vals.mean()

    print("Max: " + str(mx))
    print("Min: " + str(mn))
    print("Avg: " + str(avg))
    print("3 x Avg: " + str(3 * avg))


updates = [0 for i in range(0, 7200)]

print("Reading in data...")
df = read_data(FILE)
print("Converting time format...")
df = convert_time_format(df)
print("Extracting sleep times...")
df = get_sleep(df)
print("Analyzing intervals...")
vals = analyze_intervals(df)
print("Done!")
print_info(vals)
