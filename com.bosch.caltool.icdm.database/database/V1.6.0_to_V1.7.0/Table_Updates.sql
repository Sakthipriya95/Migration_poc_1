spool c:\temp\table_updates.log

--------------------------------------------------------
--  Feb-17-2014
-------------------------------------------------------- 

------------------------------------------
-- Alter T_RVW_PARAMETERS to add UNIT'S
-- ICDM-584
------------------------------------------
      
ALTER TABLE T_RVW_PARAMETERS
 ADD CHECK_UNIT VARCHAR2(255);

ALTER TABLE T_RVW_PARAMETERS
 RENAME COLUMN UNIT to REF_UNIT;


--------------------------------------------
-- Alter T_RVW_PARAMETERS to add CHANGE_FLAG
-- ICDM-601
--------------------------------------------

ALTER TABLE T_RVW_PARAMETERS 
 ADD (CHANGE_FLAG NUMBER(2) );

 
--------------------------------------------
-- Global TEMPORARY TABLE Gtt_Parameters
-- ICDM-592
--------------------------------------------

CREATE Global TEMPORARY TABLE Gtt_Parameters (ID NUMBER PRIMARY KEY, PARAM_NAME VARCHAR2(255), TYPE VARCHAR2(30) ) 
  ON COMMIT DELETE ROWS;


----------------------------------------------
-- Set the file type to 'R' for SSD rule files
-- ICDM-608
----------------------------------------------
UPDATE t_rvw_files 
 set file_type = 'R' 
 WHERE file_id in 
    (select file_id 
      from tabv_icdm_files 
      where lower(file_name) like '%.ssd'
     );

COMMIT;


--------------------------------------------------------
--  Inserts SSD_NODE_ID into TABV_COMMON_PARAMS table.
--------------------------------------------------------      
delete from TABV_COMMON_PARAMS WHERE PARAM_ID = 'SSD_NODE_ID';
Insert into TABV_COMMON_PARAMS (PARAM_ID,PARAM_DESC,PARAM_VALUE,VERSION) values ('SSD_NODE_ID','SSD NodeId to store review rules','3529966724',1);
COMMIT;

--------------------------------------------------------
--  Version update script
--------------------------------------------------------      
delete from TABV_COMMON_PARAMS WHERE PARAM_ID = 'iCDM_CLIENT_VERSION';
Insert into TABV_COMMON_PARAMS (PARAM_ID,PARAM_DESC,PARAM_VALUE,VERSION) values ('iCDM_CLIENT_VERSION','iCDM Client''s current version','1.7.0',1);
COMMIT;

spool off