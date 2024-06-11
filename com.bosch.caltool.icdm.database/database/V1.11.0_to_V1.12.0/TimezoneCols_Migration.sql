spool c:\temp\tz-migration.log

----------------------------------------------------------------------------------------------------------------------------------
-- Migration script to Store UTC dates in T_LINKS table programatically from client
--
-- Activities included 
-- a) Date columns (CREATED_DATE, MODIFIED_DATE) are converted to TIMESTAMP to make this uniform throughout all tables in the schema
-- b) Update the current values in the above columns to their UTC equivalent time using function sys_extract_utc(). 

----------------------------------------------------------------------------------------------------------------------------------


------------------------------------------------------------------------
--
-- IMPORTANT : Script is to be executed in DGS_ICDM schema
--
------------------------------------------------------------------------


------------------------------------------------------------------------
-- 
-- ICDM-885 - Store UTC dates in T_LINKS table programatically from client
------------------------------------------------------------------------

ALTER TRIGGER TRG_LINKS_INS DISABLE
/
ALTER TRIGGER TRG_LINKS_UPD DISABLE
/

ALTER TABLE T_LINKS ADD (CREATED_DT_OLD  TIMESTAMP WITH TIME ZONE, MODIFIED_DT_OLD  TIMESTAMP WITH TIME ZONE)
/
UPDATE T_LINKS SET CREATED_DT_OLD = CREATED_DATE, MODIFIED_DT_OLD = MODIFIED_DATE
/
commit
/
ALTER TABLE T_LINKS MODIFY (CREATED_DATE TIMESTAMP WITH TIME ZONE NULL) 
/
UPDATE T_LINKS SET CREATED_DATE = NULL, MODIFIED_DATE = NULL
/
COMMIT
/
ALTER TABLE T_LINKS MODIFY (CREATED_DATE TIMESTAMP(6), MODIFIED_DATE TIMESTAMP(6))
/
UPDATE T_LINKS SET CREATED_DATE = sys_extract_utc(CREATED_DT_OLD), MODIFIED_DATE = sys_extract_utc(MODIFIED_DT_OLD)
/
commit
/
ALTER TABLE T_LINKS MODIFY CREATED_DATE  TIMESTAMP(6) not null
/

ALTER TABLE T_LINKS DROP (CREATED_DT_OLD, MODIFIED_DT_OLD)
/


ALTER TRIGGER TRG_LINKS_INS ENABLE
/
ALTER TRIGGER TRG_LINKS_UPD ENABLE
/

spool off
