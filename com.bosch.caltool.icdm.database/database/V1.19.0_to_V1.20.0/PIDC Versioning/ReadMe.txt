

General :
--------
1. Follow steps 01 to 17 in the same sequence
2. commit statements can be disabled, and executed separately after verifying the point


Checkpoints
-----------

Checkpoint 1 AFTER executing STEP 5 before STEP-5.1
------------
There might Entries in TABV_PROJECTIDCARD table which are missing in TABV_PID_HISTORY tables.
These entries can be deleted.
In the PRO DB we found these 2 PIDCs(769052017,772790017) which we deleted.
This is required to avoid problems while adding constraints in STEP 6

The below query can be used to check for such entries
--select * from TABV_PROJECTIDCARD opidc where opidc.PROJECT_ID not in (select distinct(pidc.PROJECT_ID) from TABV_PROJECTIDCARD pidc , TABV_PID_HISTORY phist where phist.PROJECT_ID = pidc.PROJECT_ID and phist.PRO_REV_ID = pidc.PRO_REV_ID);

Checkpoint 2 (OPTIONAL) AFTER executing STEP-5.1
------------
After step 05.1 whether PIDC_VERS_ID is populated in child tables can be confirmed in the 
6th step where constraints are added for this new column in the child tables


Checkpoint 3 (OPTIONAL) AFTER executing STEP-7 
------------
There is a chance for LOSS OF DATA after STEP-8 so care should be taken that before
removing the columns in STEP-8 all required data is filled. This is mostly taken care 
in STEP-7 but other checks can also be done(if any) here before dropping the columns


Checkpoint 4 AFTER executing STEP-11 before STEP11.1
------------
After step 11, there might be a possibility that all the A2L_FILEID's in T_RVW_RESULTS
cannot be mapped to T_PIDC_A2L table. Such results can either be deleted or T_PIDC_A2L entries can be made 
to point to the currect PIDC version of the review result provided the PIDC_A2L being unmapped does'nt have
any other review results.
Handling of Checkpoint 4 can be verified in Review_result_migration_invalid_data.txt


Checkpoint 5 (OPTIONAL) After executing STEP-11.1
------------
After step 11.1 whether PIDC_A2L_ID is populated in T_RVW_RESULTS table can be confirmed by the following query

select * from T_RVW_RESULTS where PIDC_A2L_ID is null;
