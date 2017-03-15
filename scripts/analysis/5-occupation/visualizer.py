import pandas as pd
import numpy as np
import matplotlib
matplotlib.use('Agg')
import matplotlib.pyplot as plt
import interpolate as ip
import time

# Shrinks an accelerometer.csv.pprc into just the first day of data,
# for visualization purposes.

def splitTime(df):
    df['t'] = df['t'].str.split('-').str[3]
    return df

# Plots and saves a segment of accelerometer data to a file.
def plotter(df):
    one_day = pd.DateOffset(hours=24)
    #one_day = pd.DateOffset(seconds=5) #TODO Delete
    start = df.loc[0][0] # First timestamp
    end = start + one_day
    rest = df
    day_index = 0
    while (len(rest) > 0):
        day = rest[(rest['t'] > start) & (rest['t'] < end)]
        if (len(day) > 0):
            plot(day, day_index) # Only plot if there's data.
        rest = rest[rest['t'] > end] # Slice the rest
        start = end
        end = start + one_day
        day_index += 1

def plot(day, index):
    plt.figure(figsize=(10, 4))
    day.plot(x='t', y='L2')
    plt.savefig('./data/days/day' + str(index) + '.png')
    plt.close()
    day.to_csv('./data/days_csv/day' + str(index) +'.csv')


accel = pd.read_csv('./data/in.csv')
current_time = time.time() * 1000 # Current time in ms
accel = accel[accel['timestamp'] < current_time]
ip = ip.interpolate(accel)
df = pd.DataFrame()
df['t'] = ip.index
df['L2'] = ip.values
#df = splitTime(df)
df['t'].apply(lambda time: time.strftime('%H:%M:%S'))


plotter(df)





#total = len(accel)
#day = int(total / 7) # Approximately the number of rows of the first day.
#first = accel[0:day] # Get the first day.
# # Now, plot the first day.
# #
# plt.figure(figsize=(10, 4))
# first.plot(x='readable', y='L2')
# plt.savefig('./data/day.png')
