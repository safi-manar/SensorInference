import os
import csv

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
            # Read in file
            infile = open(self._csv_path, 'r')
            data = csv.DictReader(infile)

            # Preprocessing operations
            sorted_data = self._sort_by_first_col(data)

            # Write out the file
            out_file = os.path.join(out_path, self._filename)
            if os.path.isfile(out_file):
                out_file = out_file + '.pprc'   # Add extension to avoid overwrites
            self._write_out(sorted_data, out_file)

    def _column_check(self, in_path=None):
        """Ensure that the source CSV has the expected number of columns with the right headers"""
        if(in_path == None):
            in_path = self._csv_path

        with open(in_path, 'r') as csv_file:
            headers = str(csv_file.readline().strip())  # Remove newlines and encoding
            headers_list = headers.split(',')

            return len(headers_list) == len(self._columns_list) and len([x for x in headers_list if x not in self._columns_list]) == 0

    def _sort_by_first_col(self, csv_data):
        """Sort CSV data by whatever's in the first row of the desired columns ordering"""
        col_label = self._columns_list[0]

        return sorted(csv_data, key=lambda row: row[col_label])

    def _write_out(self, csv_data, out_path):
        """Write out the file in the ordering specified by the columns list"""

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

        with open(out_path, 'w') as outfile:
            # Write the headers first
            outfile.write(','.join(headers) + '\n')

            # Then the data
            writer = csv.DictWriter(outfile, fieldnames=self._columns_list)
            for row in csv_data:
                writer.writerow(row)
