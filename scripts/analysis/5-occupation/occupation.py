##
 # A script for segmenting accelerometer data for inferring occupation.
 # @author: Manar Safi
##


import pandas as pd
import numpy as np
import interpolate as ip

PARALLEL = True
CORES = 4

accel = pd.read_csv('./data/in.csv.pprc')
accel = ip.interpolate(accel, PARALLEL, CORES)
accel.to_csv('./data/out.csv.pprc', index=False)
