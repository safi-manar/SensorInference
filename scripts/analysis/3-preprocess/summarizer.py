import time
import datetime
import re

import pandas

tz = re.compile('[A-Z]{3}')
def _tzinfos():
    """Generate timezone info for 3-letter codes, from 
       https://stackoverflow.com/questions/1703546/parsing-date-time-string-with-timezone-abbreviated-name-in-python"""
    tz_str = '''-12 Y
    -11 X NUT SST
    -10 W CKT HAST HST TAHT TKT
    -9 V AKST GAMT GIT HADT HNY
    -8 U AKDT CIST HAY HNP PST PT
    -7 T HAP HNR MST PDT
    -6 S CST EAST GALT HAR HNC MDT
    -5 R CDT COT EASST ECT EST ET HAC HNE PET
    -4 Q AST BOT CLT COST EDT FKT GYT HAE HNA PYT
    -3 P ADT ART BRT CLST FKST GFT HAA PMST PYST SRT UYT WGT
    -2 O BRST FNT PMDT UYST WGST
    -1 N AZOT CVT EGT
    0 Z EGST GMT UTC WET WT
    1 A CET DFT WAT WEDT WEST
    2 B CAT CEDT CEST EET SAST WAST
    3 C EAT EEDT EEST IDT MSK
    4 D AMT AZT GET GST KUYT MSD MUT RET SAMT SCT
    5 E AMST AQTT AZST HMT MAWT MVT PKT TFT TJT TMT UZT YEKT
    6 F ALMT BIOT BTT IOT KGT NOVT OMST YEKST
    7 G CXT DAVT HOVT ICT KRAT NOVST OMSST THA WIB
    8 H ACT AWST BDT BNT CAST HKT IRKT KRAST MYT PHT SGT ULAT WITA WST
    9 I AWDT IRKST JST KST PWT TLT WDT WIT YAKT
    10 K AEST ChST PGT VLAT YAKST YAPT
    11 L AEDT LHDT MAGT NCT PONT SBT VLAST VUT
    12 M ANAST ANAT FJT GILT MAGST MHT NZST PETST PETT TVT WFT
    13 FJST NZDT
    11.5 NFT
    10.5 ACDT LHST
    9.5 ACST
    6.5 CCT MMT
    5.75 NPT
    5.5 SLT
    4.5 AFT IRDT
    3.5 IRST
    -2.5 HAT NDT
    -3.5 HNT NST NT
    -4.5 HLV VET
    -9.5 MART MIT'''

    tzd = {}
    for tz_descr in map(str.split, tz_str.split('\n')):
        tz_offset = int(float(tz_descr[0]) * 3600 * 1000)
        for tz_code in tz_descr[1:]:
            tzd[tz_code] = tz_offset
    
    return tzd


class Summarizer:
    """Generates summary about data completeness"""
    _data = None
    _time_key = None
    _gap_key = 'preceding-gap'

    # Store results to avoid recomputation
    _start = None
    _end = None
    _duration = None
    _max_gap = None
    _median_gap = None
    _mean_gap = None
    _above_average_gaps = None
    _is_batched = None

    def __init__(self, dataframe, timestamp_key = 'timestamp', readable_key = 'timeReadable'):
        if timestamp_key in dataframe.columns and readable_key in dataframe.columns:
            times = dataframe[[timestamp_key, readable_key]]                                    # Get the timestamp and readable time columns
            times = times[times[timestamp_key] <= time.time() * 1000]                           # Filter out timestamps that occur in the future
            times[readable_key] = times[readable_key].str.extract('([A-Z]{3})', expand=False)   # Get the 3-letter timezone code

            offset_key = 'offset'
            times[offset_key] = times[readable_key].map(_tzinfos())                             # Compute the offset
            times[timestamp_key] = times[timestamp_key] + times[offset_key]                     # Offset the timestamps

            times[self._gap_key] = times[timestamp_key].diff()

            # Keep timezone-corrected timestamps in milliseconds and gaps between consecutive timestamps
            self._time_key = timestamp_key
            self._data = times[[self._time_key, self._gap_key]]

        else:
            raise IOError('Input dataframe requires columns "%s" and "%s"' % (timestamp_key, readable_key))

    def get_start(self):
        if self._start is None:
            self._start = _to_datetime(self._data[self._time_key].min())
        return self._start

    def get_end(self):
        if self._end is None:
            self._end = _to_datetime(self._data[self._time_key].max())
        return self._end

    def get_duration(self):
        if self._duration is None:
            self._duration = self.get_end() - self.get_start()
        return self._duration

    def _get_preceding_gap(self, rowidx):
        if rowidx > 1:
            gap_start = _to_datetime(self._data.ix[rowidx - 1][self._time_key])
            gap_end = _to_datetime(self._data.ix[rowidx][self._time_key])
            gap = gap_end - gap_start

            return (gap_start, gap_end, gap)

    def get_max_gap(self):
        if self._max_gap is None:
            maxidx = self._data[self._gap_key].idxmax()
            self._max_gap = self._get_preceding_gap(maxidx)
        return self._max_gap

    def get_median_gap(self):
        if self._median_gap is None:
            median_gap_ms = self._data[self._gap_key].median()
            self._median_gap = datetime.timedelta(milliseconds=median_gap_ms)
        return self._median_gap

    def get_mean_gap(self):
        if self._mean_gap is None:
            mean_gap_ms = self._data[self._gap_key].mean()
            self._mean_gap = datetime.timedelta(milliseconds=mean_gap_ms)
        return self._mean_gap

    def get_above_average_gaps(self):
        if self._above_average_gaps is None:
            mean_gap_ms = self.get_mean_gap().total_seconds() * 1e3    # s to ms
            above_avg_idxs = self._data[self._data[self._gap_key] > mean_gap_ms].index.tolist()
            self._above_average_gaps = [self._get_preceding_gap(idx) for idx in above_avg_idxs]
        return self._above_average_gaps

    def is_batched(self):
        return self.get_median_gap().total_seconds() < 1           # When batching, most measurements are less than 1 second after the previous one

    def get_full_report(self):
        max_gap = self.get_max_gap()


        return { 'observation-start' : self.get_start().isoformat(),
                 'observation-seconds' : self.get_duration().total_seconds(),
                 'observation-end' : self.get_end().isoformat(),
                 'max-gap-start' : max_gap[0].isoformat(),
                 'max-gap-seconds' : str(max_gap[2].total_seconds()),
                 'max-gap-end' : max_gap[1].isoformat(),
                 'median-gap-seconds' : str(self.get_median_gap().total_seconds()),
                 'mean-gap-seconds' : str(self.get_mean_gap().total_seconds()),
                 'is-batched' : str(self.is_batched())
               }

def _to_datetime(timestamp_ms):
    return datetime.datetime.utcfromtimestamp(timestamp_ms / 1e3)   # ms to s

