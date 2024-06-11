spool c:\temp\01_Table_Alters.log

----------------------------------------------------------------
--Task 250888 - this will re-create all ROWIDs in the table which solves the DCN problem 
----------------------------------------------------------------
ALTER TABLE T_PIDC_VERSION MOVE;
alter index T_PIDC_VERSION_UK2 rebuild;
alter index T_PIDC_VERSION_UK1 rebuild;

----------------------------------------------------------------
--Task 242053  - Use new column LAST_CONFIRMATION_DATE to store the date in which the user confirmed that the PIDC is up to date
----------------------------------------------------------------
ALTER TABLE T_PIDC_VERSION ADD LAST_CONFIRMATION_DATE TIMESTAMP;

 ----------------------------------------------------------------
--Task 244427  -  Use new column DISCLMR_ACCEPTED_DATE to store the date in which the user confirmed the disclaimer acceptance.
----------------------------------------------------------------
ALTER TABLE TABV_APIC_USERS ADD DISCLMR_ACCEPTED_DATE TIMESTAMP;

 ----------------------------------------------------------------
--Task 243409  -  New Column to Store the SSD Software version
----------------------------------------------------------------

alter table T_PIDC_A2L add (SSD_SOFTWARE_VERSION VARCHAR2(4000));

 ----------------------------------------------------------------
--Task 249770  -  New Column to Store the SSD Software version
----------------------------------------------------------------

alter table T_PIDC_A2L add (SSD_SOFTWARE_VERSION_ID NUMBER(15));


 ----------------------------------------------------------------
--Task 249770  -  New Column to Store the SSD Software version
----------------------------------------------------------------
alter table t_pidc_a2l add (SSD_SOFTWARE_PROJ_ID   NUMBER(15));

 ----------------------------------------------------------------
--Task 243409  -  New Column to Store the SSD Release id
----------------------------------------------------------------

alter table T_RVW_RESULTS_SECONDARY add (SSD_RELEASE_ID  NUMBER(15));

 ----------------------------------------------------------------
--Task 243409  -  New Column to Store the SSD Release id
----------------------------------------------------------------

alter table T_RVW_RESULTS_SECONDARY add (SSD_VERSION_ID  NUMBER(15));


 -----------------------------------------------------------------------------------------------------------
-- Task 243409  - identify the source of rules  
-- Possible values : 
--  C - Common Rules 
--  F - SSD File
--  R - Rule-set rules 
--  S - SSD releases 
------------------------------------------------------------------------------------------------------------
alter table T_RVW_RESULTS_SECONDARY add (SOURCE  VARCHAR2(1)); 

--Set values for existing records
update T_RVW_RESULTS_SECONDARY set SOURCE='C' where rset_id is null;
update T_RVW_RESULTS_SECONDARY set SOURCE='R' where rset_id is not null;
commit;

-- Mark the column as mandatory
ALTER TABLE T_RVW_RESULTS_SECONDARY MODIFY (SOURCE NOT NULL);

-----------------------------------------------------------------------------------------------
--Task 249529  - New columns for shape analysis in review result
-----------------------------------------------------------------------------------------------

alter table T_RVW_PARAMETERS
  add (SR_RESULT VARCHAR2(1),
       SR_ERROR_DETAILS VARCHAR2(4000),
       SR_ACCEPTED_FLAG VARCHAR2(1),
       SR_ACCEPTED_USER VARCHAR2(20 BYTE),
       SR_ACCEPTED_DATE TIMESTAMP
      );
       
       
-----------------------------------------------------------------------------------------------
--Task 243409  - include Common and ssd rules as secondary
-------------------------------------------------------------------------------------------------
       
alter table T_RVW_RESULTS_SECONDARY drop CONSTRAINT T_RVW_RESULTS_SECONDARY_UQ1;

alter table T_RVW_RESULTS_SECONDARY add CONSTRAINT T_RVW_RESULTS_SECONDARY_UQ1  UNIQUE  (RESULT_ID,RSET_ID,SOURCE);

spool off
