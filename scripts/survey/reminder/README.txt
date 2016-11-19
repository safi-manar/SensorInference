Script to schedule email reminders to MTURK study participants

REQUIRES
- Python 2.7.x
- Boto module (https://github.com/boto/boto)

USAGE
    Basic use case: check for any people to be reminded over a 7-day observation period, schedule will be saved in script's dir
    python reminder.py <path to api.secrets>

    Custom 5-day observation period
    python reminder.py --days 5 <path to api.secrets>

    Custom save/load location for file keeping track of scheduled reminders
    python reminder.py --schedule-path <path to schedule.json> <path to api.secrets>
