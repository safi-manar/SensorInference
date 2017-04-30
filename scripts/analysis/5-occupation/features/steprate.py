import pandas as pd
import datetime as dt
import work_windows as ww
import inmotion as im
import os
import time

BATCH_PATH = '/home/ioreyes/wearables/data/full-1/extract/batched'
UNBATCH_PATH = '/home/ioreyes/wearables/data/full-1/extract/unbatched'


def stepRate():
    # Get a list of tuples (uuid, 'path/to/uuid')
    uuidData = getUuidData()
    # Get all of the filtered uuid's available from the Daily.csv
    uuidsDaily = ww.getMasterUuids()

    # pair[0] = 'uuid', and pair[1] = 'path/to/uuid'
    stepRates = [processUuid(pair[0], pair[1]) for pair in uuidData if pair[0] in uuidsDaily]
    stepUuids = [pair[0] for pair in uuidData if pair[0] in uuidsDaily] # Get just the processed uuid's.
    print("\n\n StepRate analysis complete!")
    print(stepRates)
    print('\n Number of uuids processed: ' + str(len(stepRates)))
    nonNulls = [rate for rate in stepRates if rate != -1]
    print(" Number of uuid\'s with valid data: " + str(len(nonNulls)))
    print("Writing results to file \'steprates.csv\'...")
    writeRatesToFile(stepUuids, stepRates, 'steprates.csv')
    print("Done!")





# Given the tuple (uuids, uuidsPaths) of lists from getUuidsFromPath() for both batched and unbatched,
# return a list of the the corresponding tuples (uuid, uuidPath)
def getUuidData():
    batched, batchedPaths = getUuidsFromPath(BATCH_PATH)
    unbatched, unbatchedPaths = getUuidsFromPath(UNBATCH_PATH)
    print("batched.length: ", len(batched))
    print("batchedPaths.length: ", len(batchedPaths))
    print("unbatched.length: ", len(unbatched))
    print("unbatchedPaths.length: ", len(unbatchedPaths))

    uuidData = []
    for i in range(0, len(batched)):
        uuidTuple = (batched[i], batchedPaths[i])
        uuidData.append(uuidTuple)

    for i in range(0, len(unbatched)):
        uuidTuple = (unbatched[i], unbatchedPaths[i])
        uuidData.append(uuidTuple)

    return uuidData


def getUuidsFromPath(PATH):
    # Now, filter out for all the uuid's that have steps.
    uuids = [] # Represents all the UUID's with steps.
    uuidsPaths = [] # Represents the file paths to those uuid's.
    for subdir, dirs, files in os.walk(PATH):
        # Look for the steps data file in each UUID folder.
        for data in files:
            if data == "steps.csv.pprc":
                start_index = len(PATH)+1 # The location at which the UUID starts in the subdir.
                uuid = subdir[start_index:] # Splice only the uuid
                uuids.append(uuid)

                steps_path = os.path.join(subdir, data)
                uuidsPaths.append(steps_path)

    return (uuids, uuidsPaths)

# Master flow for processing a single uuid
def processUuid(uuid, step_path):
    print("\nReading in step data for user: \'" + uuid + "\' ...")
    step = read_data(step_path)
    print("Getting work windows...")
    daily = im.getWindows(uuid) # Get the daily data using the inMotion script.
    step = im.convert_time_format(step, columName='t') # Convert the 't' column to an integer, for comparisons in partitionWorkTimes()
    print("Partitioning work days...")
    days = partitionWorkTimes(step, daily)
    # Ensure that accel data exists.
    if (days == -1):
        print("No corresponding stepcounter data for work times.")
        print("\t*Skipping user: " + str(uuid))
        return -1
    print("Analyzing all work days and computing steprates...")
    stepRate = analyze_days(days)
    print("Analysis complete!")
    print("\t The user\'s number of steps per hour: " + str(stepRate))

    return stepRate



# Returns step with the following columns
#   'ts' = timestamp in milliseconds (integer)
#   't' = timestamp in pandas.datetime format.
#   'steps' = cumulative number of steps
def read_data(STEP_PATH):
    step = pd.read_csv(STEP_PATH)
    step = filterRelevant(step)
    # Crop out invalid times
    current_time = time.time() * 1000 # Current time in ms
    step = step[step['ts'] < current_time]
    # Add in the timestamp in datetime format.
    step['t'] = pd.to_datetime(step['ts'], unit='ms')
    return step

# Return only the columns 'ts' (timestamp in ms) and 'steps'
def filterRelevant(step):
    step = step.loc[:, ['timestamp', 'cumulative-steps']]
    step.columns = ['ts', 'steps']
    return step



# Given the daily data and step data for a user, return a list of stepcounter DataFrames
# partitioned according to the start/end times from daily.
def partitionWorkTimes(step, daily):
    days = []
    workdayCount = 0 # Number of work days from survey (as a count, not an index)
    stepdataCount = 0 # Number of corresponding days from accelerometer
    # Iterate through the rows in daily
    for index, row in daily.iterrows():
        workdayCount += 1
        start = row['start']
        end = row['end']
        workday = step[(step['t'] >= start) & (step['t'] <= end)]
        start_timestamp = pd.Timestamp(start*1000000) # Calculate timestamps from ms.
        end_timestamp = pd.Timestamp(end*1000000)
        if (workday.shape[0] == 0):
            print("\t- No stepcounter data collected for start: " + str(start_timestamp) + " and end: " + str(end_timestamp) )
        elif (workday.shape[0] == 1):
            # If there is only one and exactly one measurement for this day, then also toss out this day,
            # because a number of steps cannot be found for the "delta" of just one measurement (ie, delta = 0, so rate = numSteps / 0 = inf.)
            print("\t- Not enough stepcounter data collected for start: " + str(start_timestamp) + " and end: " + str(end_timestamp) )
        else:
            stepdataCount += 1
            days.append(workday)
            print("\tFound workday:  start: " + str(start_timestamp) + "  end: " + str(end_timestamp) )


    print("\tOut of " + str(workdayCount) + " submitted work days, there were " + str(stepdataCount) + " corresponding days of stepcounter data collected.")

    # If no stepcounter data was collected, signal to skip to the next subject.
    if (stepdataCount == 0):
        return -1
    return days


# Given a wd, which is a DataFrame of stepcounter data for a particular workday,
# Returns a tuple, (sumSteps, delta) corresponding to the total number of steps for the amount of time in this workday.
def analyze_workday(wd):
    deltaInt = wd['t'].max() - wd['t'].min() # Delta in milliseconds
    delta = dt.timedelta(milliseconds=deltaInt)
    wd['upshift'] = wd['steps'].shift(-1) # Push all values in 'steps' up a row and paste in 'upshift'
    wd['sub'] = wd['upshift'] - wd['steps']
    # Since the step counter measurement is supposed to be monotonically increasing, we must check for all cases in which it
    # resets and aggregate the counts.
    # Filter out those values for which 'upshift' < 'steps'. That is, for which, the stepcounter reset, and also
    # include the very last measurement (where 'steps' has a value, but 'upshift' is NaN.)
    counts = wd[(wd['sub'] < 0) | (wd['sub'].isnull())]
    sumSteps = counts['steps'].sum()
    return (sumSteps, delta)


# Given a list of workdays (each is a DataFrame of stepcounter data for a workday)
#  return the total number of steps / total number of hours
def analyze_days(days):
    valuesTuples = [analyze_workday(workday) for workday in days]
    totalDelta = dt.timedelta(hours=0)
    totalSteps = 0
    for valueTuple in valuesTuples:
        totalSteps = totalSteps + valueTuple[0]
        totalDelta = totalDelta + valueTuple[1]

    days = totalDelta.days
    seconds = totalDelta.seconds
    hours = (days*24) + (float(seconds) / 3600)

    return (totalSteps / hours)


def writeRatesToFile(stepUuids, stepRates, name='steprates.csv'):
    sr = pd.DataFrame()
    sr['uuid'] = stepUuids
    sr['steps_per_hour'] = stepRates
    sr.to_csv(name, sheet_name='Sheet1')
