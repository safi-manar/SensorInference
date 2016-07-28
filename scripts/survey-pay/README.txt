Script to identify successful survey submissions and pay those users through MTURK

REQUIRES
- Python 2.7.x
- SurveyGizmo module (https://github.com/ITNG/SurveyGizmo/)
- Boto module (https://github.com/boto/boto)

SURVEY ASSUMPTIONS
- Surveys contain "uuid" and "processed" hidden actions
- Amazon MTURK HIT page contains a single FreeText input for the app code (as configured by scripts/survey-setup/)
