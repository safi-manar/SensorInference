import argparse
import importlib, inspect

import os.path,pkgutil
import Segments

def list_available_segments():
    """List modules under the Segments package"""
    pkgpath = os.path.dirname(Segments.__file__)
    return [name for _, name, _ in pkgutil.iter_modules([pkgpath]) if not name.startswith('_')]

def parse_args():
    seg_list = list_available_segments()
    seg_str = None
    if len(seg_list) > 1:
        seg_str = reduce(lambda x,y: '%s | %s' % (x,y), seg_list)
    elif len(seg_list) == 1:
        seg_str = seg_list[0]
    seg_help = 'segment selection, from [%s]' % seg_str

    parser = argparse.ArgumentParser(description='Segment and transform data for various analyses')
    parser.add_argument('pprc_path', help='path to a single device\'s preprocessed data, filenames ending with .csv.pprc')
    parser.add_argument('segment', help=seg_help)
    parser.add_argument('-o', '--out-path', help='path to the output directory, defaults to device data path if unspecified')

    return parser.parse_args()

def instantiate_seg_class(segment):
    load = importlib.import_module('Segments.' + segment)
    cls = [c for (n,c) in inspect.getmembers(load) if inspect.isclass(c) and n != 'SegBase'][0]

    return cls()

# Read arguments
args = parse_args()
pprc_path = args.pprc_path
segment = args.segment
out_path = args.out_path if args.out_path is not None else pprc_path

# Proceed with the segmentation if the arguments are valid
is_good_pprc = os.path.isdir(pprc_path)
is_good_segment = segment in list_available_segments()
is_good_out = os.path.isdir(out_path)

if is_good_pprc and is_good_segment and is_good_out:
    sg = instantiate_seg_class(segment)
    sg.run(pprc_path)
else:
    if not is_good_pprc:
        print 'ERROR: Preprocessed data folder "%s" does not exist' % pprc_path
    if not is_good_segment:
        print 'ERROR: Invalid segment selection "%s"' % segment
    if not is_good_out:
        print 'ERROR: Output folder "%s" does not exist' % out_path
