#!/usr/bin/env python3

import sys

import mturk

m = mturk.MechanicalTurk()
r = m.request('SearchHITs')
print(r)
