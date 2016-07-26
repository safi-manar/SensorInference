#!/usr/bin/env python3

import sys

import mturk

hitId = sys.argv[1]

options = {
    'HITId': hitId
}

m = mturk.MechanicalTurk()
r = m.request('DisableHIT', options)
print(r)
