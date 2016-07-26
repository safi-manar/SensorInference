#!/usr/bin/env python3

import sys

import mturk

hitId = sys.argv[1]

options = {
    'HITId': hitId,
    'PageSize': 100
}

m = mturk.MechanicalTurk()
r = m.request('GetAssignmentsForHIT', options)
print(r)
