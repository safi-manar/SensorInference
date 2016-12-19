from Segments.__SegBase__ import SegBase

class Test(SegBase):
    _foo = None

    def __init__(self):
        super(Test, self).__init__()
        self._foo = 'foo'
        print self._foo

    def file_filter(self):
        return False

    def segment(self, files_list):
        print files_list

