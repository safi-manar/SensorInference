Setup scripts
=============

These are scripts for setting up the project.

Check account balance
---------------------
If you want to know how much money you have:
```bash
cd .. # Need to be in the same directory as mturkconfig.json
./create/check_balance
```

Create the HIT
--------------
```bash
cd .. # Need to be in the same directory as mturkconfig.json
export GDMS_QUALIFICATION_ID=… # the QualificationTypeID of the qualification given to those who have already taken this HIT (i.e., we have their GDMS)
./create/create_hit_gdms $GDMS_QUALIFICATION_ID
```
