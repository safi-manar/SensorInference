import pandas as pd
import numpy as np
import matplotlib
matplotlib.use('Agg')
import matplotlib.pyplot as plt
import interpolate as ip

# Shrinks an accelerometer.csv.pprc into just the first day of data,
# for visualization purposes.

def splitTime(accel):
    accel['readable'] = accel['readable'].str.split('-').str[3]
    accel['readable'] = accel['readable'].str.split('(').str[0]
    return accel


# Plots and saves a segment of accelerometer data to a file.
def plotter(data, day):
    total = len(data)
    interval = int(total/7)
    start = day*interval
    end = (day+1)*interval
    data = data[start:end]
    plt.figure(figsize=(10, 4))
    data.plot(x='readable', y='L2')
    plt.savefig('./data/days/day' + str(day) + '.png')
    plt.close()
    data.to_csv('./data/days_csv/day' + str(day) +'.csv')


accel = pd.read_csv('./data/in.csv.pprc')
timestamp = accel['timeReadable']
accel = ip.interpolate(accel)
accel['readable'] = timestamp
accel = splitTime(accel)


[plotter(accel, day) for day in range(0, 7)]





#total = len(accel)
#day = int(total / 7) # Approximately the number of rows of the first day.
#first = accel[0:day] # Get the first day.
# # Now, plot the first day.
# #
# plt.figure(figsize=(10, 4))
# first.plot(x='readable', y='L2')
# plt.savefig('./data/day.png')
