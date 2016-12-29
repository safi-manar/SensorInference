import os
import pandas
import numpy

from Segments.__SegBase__ import SegBase

class AccelSteps(SegBase):
    _target_prefix1 = 'accelerometer'
    _target_prefix2 = 'steps'
    _target_suffix = '.pprc'

    def file_filter(self, filename):
        has_prefix = filename.startswith(self._target_prefix1) or filename.startswith(self._target_prefix2)
        has_suffix = filename.endswith(self._target_suffix)

        return has_prefix and has_suffix

    def segment(self, files_path, files_list):
        # Expect exactly 2 files, one for the accelerometer, another for the step counter
        if len(files_list) == 2:
            accel_file = files_list[0]
            steps_file = files_list[1]

            # Calculate step rates to find when the user was walking
            steps_df = pandas.read_csv(os.path.join(files_path, steps_file))
            steps_df = super(AccelSteps, self).remove_future(steps_df)  # Remove mislabeled future data
            steps_df['timestamp-prev'] = steps_df['timestamp'].shift(1)
            steps_df['delta-t'] = steps_df['timestamp'].diff().multiply(1e-3)   # ms to s
            steps_df['delta-steps'] = steps_df['cumulative-steps'].diff()
            steps_df = steps_df[steps_df['delta-t'] > 1]    # Remove non-elapsed times
            steps_df['steps-per-second'] = steps_df['delta-steps'].divide(steps_df['delta-t'])  # Compute rate
            steps_df = steps_df[(steps_df['steps-per-second'] > 1) & (steps_df['steps-per-second'] < 4)]    # Filter out non-movements and inhuman speeds 
            steps_df['timestamp-prev'] = steps_df['timestamp-prev'].astype(int)

            walking_windows = steps_df[['timestamp-prev','timestamp']]
            walking_windows.columns = ['start-ms', 'end-ms']

            # Get the accelerometer data in those windows
            accel_df = pandas.read_csv(os.path.join(files_path, accel_file))
            window_num = 0
            combined = None
            for index, row in walking_windows.iterrows():
                start_ms = row['start-ms']
                end_ms = row['end-ms']

                accel_window = accel_df[(accel_df['timestamp'] >= start_ms) & (accel_df['timestamp'] <= end_ms)].copy()
                accel_components = accel_window[['x-minus-gx','y-minus-gy','z-minus-gz']]
                accel_window['l2-norm'] = numpy.sqrt(numpy.square(accel_components).sum(axis=1))
                accel_window['window-num'] = [window_num] * len(accel_window.index)
                window_num += 1

                combined = pandas.concat([combined, accel_window]) if combined is not None else accel_window

            # Return a dataframe containing all accelerometer readings during walking, None if no such readings
            return combined 

        Exception('"%s" needs to contain exactly 2 files, one for the accelerometer and another for the step counter' % files_list)


