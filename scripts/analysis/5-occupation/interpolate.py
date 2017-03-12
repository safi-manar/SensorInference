##
 # A script for linearly interpolating accelerometer data.
 # @author: Manar Safi
##

import pandas as pd
import numpy as np
from multiprocessing import Pool # [2]

NUM_PARTITIONS = 8

def interpolate(accel, PARALLEL=False, CORES=4):
    columns = accel.columns # backup the original columns
    accel = trim(accel)
    if (PARALLEL):
        accel = cleanTimeParallel(accel, CORES)
    else:
        accel = cleanTime(accel)
    #accel = L2(accel) # Add the L2 column.
    return accel





# Keep only the relevant columns for this script for efficiency
def trim(accel):
    accel = accel.loc[:, ['timestamp', 'x-minus-gx', 'y-minus-gy', 'z-minus-gz']]
    accel.columns = ['t', 'x', 'y', 'z']
    return accel


# At 50Hz, there should be an event every 20ms.
def cleanTime(accel):
    print("PreTest")
    print(accel.columns.tolist())
    accel.apply(normalize, axis=1) # Apply normalize row-wise.
    print("PostTest")
    print(accel.columns.tolist())
    accel = accel[['t', 'time', 'x', 'y', 'z']]# TODO Delete
    return accel

# At 50Hz, there should be an event every 20ms.
def cleanTimeParallel(accel, CORES):
    print("PreTest: Parallel")
    print(accel.columns.tolist())
    accel_split = np.array_split(accel, NUM_PARTITIONS)
    pool = Pool(CORES)
    accel = pd.concat(pool.map(normalizeFunc, accel_split))
    pool.close()
    pool.join()
    print("PostTest")
    print(accel.columns.tolist())
    accel = accel[['t', 'time', 'x', 'y', 'z']]# TODO Delete
    return accel

def normalizeFunc(df):
    return df.apply(normalize, axis=1)

# Round the time value to the nearest 20 [1]
def normalize(row):
    time = row['t']
    row['time'] = round(time / 20) * 20 # TODO Change 'time' --> 't'
    return row



def L2(accel):
    np.square(accel['x-minus-gx', 'y-minus-gy', 'z-minus-gz'])
    # Sum the rows into a new column
    accel['sum'] = accel['x'] + accel['y'] + accel['z']
    np.sqrt(accel['sum'])
    accel.columns = ['t', 'x', 'y', 'z', 'L2'] # Rename sum
    return accel

# The linear interpolation between two points.
def interp(x0, y0, x1, y1, x):
    return y0 + ((x - x0) * (y1 - y0) / (x1 - x0))




# [1] http://stackoverflow.com/questions/9810391/round-to-the-nearest-500-python
# [2] http://www.racketracer.com/2016/07/06/pandas-in-parallel/
