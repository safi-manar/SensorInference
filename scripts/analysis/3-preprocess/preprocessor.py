import os
import csv
import pandas

class Preprocessor:
    'Preprocessing operations for CVS\'d Firebase data'
    _csv_path = ''
    _filename = ''
    _columns_list = []
    _rename_map = None

    def __init__(self, csv_path, columns_list, rename_map=None):
        if os.path.isfile(csv_path):
            self._csv_path = csv_path
            self._filename = os.path.basename(csv_path)
            self._columns_list = columns_list
            self._rename_map = rename_map
        else:
            raise IOError('File "%s" does not exist' % csv_path)

    def run(self, out_path):
        good_columns = self._column_check()
        if(good_columns):
            # Read in file and desired headers
            data = pandas.read_csv(self._csv_path)
            headers = self._get_renamed_headers()

            # Preprocessing operations
            data = data[self._columns_list]                             # Reorder columns
            data.columns = headers                                      # Rename columns
            data.sort_values(by=headers[0], inplace=True)               # Sort by first column (timestamp)

            return data

    def _column_check(self, in_path=None):
        """Ensure that the source CSV has the expected number of columns with the right headers"""
        if(in_path == None):
            in_path = self._csv_path

        with open(in_path, 'r') as csv_file:
            headers = str(csv_file.readline().strip())  # Remove newlines and encoding
            headers_list = headers.split(',')

            return len(headers_list) == len(self._columns_list) and len([x for x in headers_list if x not in self._columns_list]) == 0

    def _get_renamed_headers(self):
        # Get the updated headers, if available
        headers = []
        if self._rename_map != None:
            for header in self._columns_list:
                if header in self._rename_map.keys():
                    headers.append(self._rename_map[header])
                else:
                    headers.append(header)
        else:
            headers = self._columns_list

        return headers
