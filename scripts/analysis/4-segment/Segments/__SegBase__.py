import os
import time
import pandas

from abc import ABCMeta, abstractmethod

class SegBase:
    __metaclass__ = ABCMeta

    @abstractmethod
    def file_filter(self, filename):
        """Return a boolean true for filenames that match the ones we want"""
        pass

    @abstractmethod
    def segment(self, files_path, files_list):
        """Return a dataframe containing the segmented data we want"""
        pass

    def run(self, files_path):
        """Return a dataframe containing the segmented data we want from the target path, None
        otherwise"""
        if os.path.isdir(files_path):
            target_files = [f for f in os.listdir(files_path) if self.file_filter(f)]

            if len(target_files) > 0:
                target_files.sort()
                return self.segment(files_path, target_files)

        return None

    # Utility method to remove data mislabeled as from the future
    def remove_future(self, df):
        columns = df.columns
        if 'timestamp' in columns:
            timestamp_s = time.time()
            timestamp_ms = timestamp_s * 1e3

            return df[df['timestamp'] < timestamp_ms]

        raise Error('No "timestamp" column in "%s"' % columns)
