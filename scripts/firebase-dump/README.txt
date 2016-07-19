Script to save data from Firebase Database and Storage

REQUIRES
- Python 2.7.x
- Pyrebase module (https://github.com/thisbejim/Pyrebase)
- google-services.json file (generate using Firebase "Project settings" screen)
- Service account credentials json file (generate using Firebase "Permissions" screen)

USAGE
    Basic use case: save Firebase data to current working directory, users separated by folder
    python firebase-dump.py <path to google-services.json> <path to credentials.json>

    Custom output directory, users still separated by folder
    python firebase-dump.py <path to google-services.json> <path to credentials.json> --output <path to output dir>

    Clear Firebase data, with prompt
    python firebase-dump.py <path to google-services.json> <path to credentials.json> --clear

    Custom output directory, clear Firebase data unprompted
    python firebase-dump.py <path to google-services.json> <path to credentials.json> --output <path to output dir> --clear-no-prompt
