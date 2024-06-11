spool c:\temp\01_Table_Alters.log

------------------------------------------------------------------------------------------------------------------
--  ALM TaskId : 482456: Add new columns to T_PICD_A2L TABLE 
--  ALTER query for T_PICD_A2L
------------------------------------------------------------------------------------------------------------------
ALTER TABLE T_PIDC_A2L ADD (ASSIGNED_DATE TIMESTAMP(6), ASSIGNED_USER VARCHAR2(30 BYTE) );

------------------------------------------------------------------------------------------------------------------
--  ALM TaskId : 505362: Add new columns 
--  ALTER query for T_COMPLI_RVW_A2L to add file id
-- Also Add Foreign Constraint to ICDM_FILE tabel
------------------------------------------------------------------------------------------------------------------
ALTER TABLE T_COMPLI_RVW_A2L ADD FILE_ID NUMBER;

ALTER TABLE T_COMPLI_RVW_A2L ADD CONSTRAINT 
   T_COMPLI_RVW_A2L_ICDM_FILE_FK FOREIGN KEY( FILE_ID ) REFERENCES TABV_ICDM_FILES ( FILE_ID );



spool off