##
 # A script for linearly interpolating accelerometer data.
 # @author: Manar Safi
##

import pandas as pd
import numpy as np
from multiprocessing import Pool # [2]

NUM_PARTITIONS = 8 # For parallelization


def interpolate(accel, PARALLEL=False, CORES=4):
    accel = trim(accel)
    accel = L2(accel)
    accel = accel.loc[:, ['t', 'L2']] # More trimming.
    ts = getTimeSeries(accel) # A time series for L2 data.
    rs = getResampledSeries(accel) # A time index
    ip = interpFromIndex(ts, rs)
    return ip



# For efficiency, keep only the relevant columns.
def trim(accel):
    accel = accel.loc[:, ['timestamp', 'x-minus-gx', 'y-minus-gy', 'z-minus-gz']]
    accel.columns = ['t', 'x', 'y', 'z']
    return accel

def L2(accel):
    accel[['x', 'y', 'z']] = np.square(accel[['x', 'y', 'z']])
    # Sum the rows into a new column
    accel['sum'] = accel['x'] + accel['y'] + accel['z']
    accel['sum'] = np.sqrt(accel['sum'])
    accel.columns = ['t', 'x', 'y', 'z', 'L2'] # Rename sum
    return accel

# Returns a time series for the L2 data, using time as the index.
def getTimeSeries(accel):
    times = pd.to_datetime(accel['t'], unit='ms') # Create a DateTimeIndex series from 't'
    data = accel['L2'] # Create a series from the L2 data.
    return pd.Series(data.values, times) # Create a Series from L2 data and using times as the index

# Given the accelerometer data, return a time seriess index with the times snapped to the nearest 20ms.
def getResampledSeries(accel):
    snap = 20 # Interval in milliseconds.
    accel['t'] = accel['t'].apply(normalize) # Normalize the times.
    rts = getTimeSeries(accel)
    rs = pd.Series(index=rts.index) # Replaces L2 values with NaN, keeping only the time.
    return rs # return only the index.

# Round the time value to the nearest 20 [1]
def normalize(time):
    return int(round(float(time) / 20) * 20)

# Given time series ts = with L2 data and time as the index, and
# time series index rs, return the interpolated values of rs. From [3]
def interpFromIndex(ts, rs):
    ip = pd.concat([ts, rs]).sort_index()
    ip = ip.interpolate()
    return ip[rs.index] # Return only those values that were in rs.





# [1] http://stackoverflow.com/questions/9810391/round-to-the-nearest-500-python
# [2] http://www.racketracer.com/2016/07/06/pandas-in-parallel/
# [3] http://stackoverflow.com/questions/18955250/interpolating-one-time-series-onto-another-in-pandas
