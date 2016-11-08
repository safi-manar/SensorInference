Script to generate CSVs of measurement data as produced by firebase-dump.py

REQUIRES
- Python 2.7.x

USAGE
    Basic use case: generate CSVs for data from a user, output to <path to dump>/<user-uuid>/extract
    python data-extract.py <path to dump>/<user-uuid>

    Genereate CSVs and store in custom output path
    python data.extract <path to dump>/<user-uuid> --out-path <custom output path>
