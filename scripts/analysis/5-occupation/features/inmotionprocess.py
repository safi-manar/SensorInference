import inmotion

# A wrapper for running the inmotion script.

ACCEL_NAME = 'accelerometer.csv.pprc'
DATA_PATH = '/home/ioreyes/wearables/data/full-1/extract/batched'

# Begin the inmotion script:
inmotion.inMotion(DATA_PATH, ACCEL_NAME)
