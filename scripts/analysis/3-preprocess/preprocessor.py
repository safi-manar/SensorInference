import os
import csv

class Preprocessor:
    'Preprocessing operations for CVS\'d Firebase data'
    _csv_path = ''
    _filename = ''
    _columns_list = []

    def __init__(self, csv_path, columns_list):
        if os.path.isfile(csv_path):
            self._csv_path = csv_path
            self._filename = os.path.basename(csv_path)
            self._columns_list = columns_list
        else:
            raise IOError('File "%s" does not exist' % csv_path)

    def run(self, out_path):
        # TODO
        good_columns = self._step_1_column_check()
        if(good_columns):
            self._step_2_reorder_columns(os.path.join(out_path, self._filename))

    def _step_1_column_check(self):
        """Ensure that the source CSV has the expected number of columns with the right headers"""
        with open(self._csv_path, 'r') as csv_file:
            headers = str(csv_file.readline().strip())  # Remove newlines and encoding
            headers_list = headers.split(',')

            return len(headers_list) == len(self._columns_list) and len([x for x in headers_list if x not in self._columns_list]) == 0

    def _step_2_reorder_columns(self, out_path):
        """Reorder the column per the spec, from http://stackoverflow.com/questions/33001490/python-re-ordering-columns-in-a-csv"""
        with open(self._csv_path, 'r') as infile, open(out_path, 'w') as outfile:
            writer = csv.DictWriter(outfile, fieldnames=self._columns_list)

            writer.writeheader()
            for row in csv.DictReader(infile):
                writer.writerow(row)
