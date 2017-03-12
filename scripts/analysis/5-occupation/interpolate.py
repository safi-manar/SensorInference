##
 # A script for linearly interpolating accelerometer data.
 # @author: Manar Safi
##

import pandas as pd
import numpy as np
from multiprocessing import Pool # [2]

NUM_PARTITIONS = 8 # For parallelization

def interpolate(accel, PARALLEL=False, CORES=4):
    columns = accel.columns # backup the original columns
    accel = trim(accel)
    accel = cleanTime(accel)
    accel = L2(accel) # Add the L2 column.
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
    accel['time'] = accel['t'] # Duplicate the time
    accel['time'] = accel['time'].apply(normalize) # Apply normalize row-wise.
    print("PostTest")
    print(accel.columns.tolist())
    accel = accel[['t', 'time', 'x', 'y', 'z']]# TODO Delete
    return accel

# Round the time value to the nearest 20 [1]
def normalize(time):
    return int(round(float(time) / 20) * 20)


def L2(accel):
    accel[['x', 'y', 'z']] = np.square(accel[['x', 'y', 'z']])
    # Sum the rows into a new column
    accel['sum'] = accel['x'] + accel['y'] + accel['z']
    accel['sum'] = np.sqrt(accel['sum'])
    accel.columns = ['t', 'time', 'x', 'y', 'z', 'L2'] # Rename sum
    return accel

# The linear interpolation between two points.
def interp(x0, y0, x1, y1, x):
    return y0 + ((x - x0) * (y1 - y0) / (x1 - x0))




# [1] http://stackoverflow.com/questions/9810391/round-to-the-nearest-500-python
# [2] http://www.racketracer.com/2016/07/06/pandas-in-parallel/
