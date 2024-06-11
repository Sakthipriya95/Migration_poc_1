spool c:\temp\01_Table_Alters.log

-----------------------------------------------------------------------------------------------------------------
--  ALM TaskId : 518935:  ALTER statement for T_A2L_WP_DEFN_VERSIONS to add ANYTIME_ACTIVE_FLAG
------------------------------------------------------------------------------------------------------------------
ALTER TABLE T_A2L_WP_DEFN_VERSIONS ADD ( ANYTIME_ACTIVE_FLAG VARCHAR2(1 BYTE)  DEFAULT 'N' NOT NULL );

------------------------------------------------------------------------------------------------------------------
--  ALM TaskId : 517702: Create T_A2L_WP_IMPORT_PROFILE table  
------------------------------------------------------------------------------------------------------------------
CREATE TABLE T_A2L_WP_IMPORT_PROFILE (
    profile_id        NUMBER NOT NULL PRIMARY KEY,
    profile_name      VARCHAR2(100) NOT NULL UNIQUE,
    profile_order     NUMBER NOT NULL UNIQUE,
    profile_details   VARCHAR2(4000) NOT NULL,
    CREATED_DATE    TIMESTAMP(6) NOT NULL,
    CREATED_USER    VARCHAR2(30 BYTE) NOT NULL,
    MODIFIED_DATE   TIMESTAMP(6),
    MODIFIED_USER   VARCHAR2(30 BYTE),
    VERSION           NUMBER NOT NULL,
    CONSTRAINT ensure_json CHECK ( profile_details IS JSON )
);

-----------------------------------------------------------------------------------------------------------------
--  ALM TaskId : 525508:  ALTER statement for T_RVW_PARAMETERS to add qssd related columns
------------------------------------------------------------------------------------------------------------------
ALTER TABLE T_RVW_PARAMETERS ADD  QSSD_RESULT VARCHAR2(1);
ALTER TABLE T_RVW_PARAMETERS ADD  QSSD_LAB_OBJ_ID NUMBER;
ALTER TABLE T_RVW_PARAMETERS ADD QSSD_REV_ID NUMBER;

-----------------------------------------------------------------------------------------------------------------
--  ALM TaskId : 525026:  ALTER statement for T_RVW_PARAMETERS to add MATURITY_LVL
------------------------------------------------------------------------------------------------------------------
ALTER TABLE T_RVW_PARAMETERS ADD MATURITY_LEVEL VARCHAR2(10);

-------------------------------------------------------------------------------------------------------------------
--  ALM TaskId : 525909:  ALTER statement for T_FC2WP_MAPPING to Add column IS_FC_IN_SDOM
-------------------------------------------------------------------------------------------------------------------
ALTER TABLE T_FC2WP_MAPPING ADD IS_FC_IN_SDOM_FLAG VARCHAR2(1 BYTE) ;


-------------------------------------------------------------------------------------------------------------------
--  ALM TaskId : 529126:  DB Changes for handling Q-SSD Parameters in compliance report 
-------------------------------------------------------------------------------------------------------------------

--T_COMPLI_RVW_A2L :
--  New column - NUM_QSSD
-------------------------
ALTER TABLE T_COMPLI_RVW_A2L ADD NUM_QSSD NUMBER;

--Setting value as 0 for empty columns, as column needs to be not null
UPDATE T_COMPLI_RVW_A2L SET NUM_QSSD=0 WHERE NUM_QSSD IS NULL;
COMMIT;

ALTER TABLE T_COMPLI_RVW_A2L MODIFY NUM_QSSD NOT NULL;

--T_COMPLI_RVW_HEX :
--  New columns - QSSD_OK, QSSD_FAIL, QSSD_NO_RULE
--------------------------------------------------
ALTER TABLE T_COMPLI_RVW_HEX ADD QSSD_OK NUMBER;
ALTER TABLE T_COMPLI_RVW_HEX ADD QSSD_FAIL NUMBER;
ALTER TABLE T_COMPLI_RVW_HEX ADD QSSD_NO_RULE NUMBER;

--Setting value as 0 for empty columns, as column needs to be not null
UPDATE T_COMPLI_RVW_HEX SET QSSD_OK=0 WHERE QSSD_OK IS NULL;
UPDATE T_COMPLI_RVW_HEX SET QSSD_FAIL=0 WHERE QSSD_FAIL IS NULL;
UPDATE T_COMPLI_RVW_HEX SET QSSD_NO_RULE=0 WHERE QSSD_NO_RULE IS NULL;
COMMIT;

ALTER TABLE T_COMPLI_RVW_HEX MODIFY QSSD_OK NOT NULL;
ALTER TABLE T_COMPLI_RVW_HEX MODIFY QSSD_FAIL NOT NULL;
ALTER TABLE T_COMPLI_RVW_HEX MODIFY QSSD_NO_RULE NOT NULL;

--T_COMPLI_RVW_HEX_PARAMS :
--   a) New column - QSSD_RESULT 
--   b) Rename columns COMPLI_FAIL TO COMPLI_NO_RULE, RESULT TO COMPLI_RESULT
-----------------------------------------------------------------------------
ALTER TABLE T_COMPLI_RVW_HEX_PARAMS ADD QSSD_RESULT VARCHAR(1);

UPDATE T_COMPLI_RVW_HEX_PARAMS SET QSSD_RESULT='-' WHERE QSSD_RESULT IS NULL;
COMMIT;

ALTER TABLE T_COMPLI_RVW_HEX_PARAMS MODIFY QSSD_RESULT NOT NULL;

--Rename columns
ALTER TABLE T_COMPLI_RVW_HEX RENAME COLUMN COMPLI_FAIL TO COMPLI_NO_RULE;
ALTER TABLE T_COMPLI_RVW_HEX_PARAMS RENAME COLUMN RESULT TO COMPLI_RESULT;


spool off