import pandas as pd

# Add all of the names of extracted features here.
proportions_15_path = 'proportions_15.csv'
proportions_20_path = 'proportions_20.csv'
stepRates_path = 'steprates.csv'


proportions_15 = pd.read_csv(proportions_15_path)
proportions_20 = pd.read_csv(proportions_20_path)
stepRates = pd.read_csv(stepRates_path)

# Ignore the index
proportions_15 = proportions_15.loc[:, ['uuid', 'proportion_rest', 'proportion_motion']]
proportions_20 = proportions_20.loc[:, ['uuid', 'proportion_rest', 'proportion_motion']]
stepRates = stepRates.loc[:, ['uuid', 'steps_per_hour']]

# Rename headers for proportions thresholds
proportions_15.columns = ['uuid', 'proportion_rest_15', 'proportion_motion_15']
proportions_20.columns = ['uuid', 'proportion_rest_20', 'proportion_motion_20']


proportions_join = pd.merge(proportions_15, proportions_20, how='outer', on='uuid')
# Even though it's an outer join, there should still be the same number of uuid's in _15 as in _20.
assert(len(proportions_15) == len(proportions_join))

features = pd.merge(proportions_join, stepRates, how='outer', on='uuid')

# Write out to 'features.csv'
features.to_csv('features.csv', sheet_name='Sheet1')
