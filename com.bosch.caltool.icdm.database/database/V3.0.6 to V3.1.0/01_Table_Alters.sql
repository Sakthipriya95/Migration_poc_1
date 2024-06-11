spool c:\temp\01_Table_Alters.log

------------------------------------------------------------------------------------------------------------------
--  ALM TaskId : 276370: Add new column to T_RVW_PARTICIPANTS TABLE 
--  ALTER query for T_RVW_PARTICIPANTS
------------------------------------------------------------------------------------------------------------------
ALTER TABLE T_RVW_PARTICIPANTS ADD EDIT_FLAG VARCHAR2(1) ;


------------------------------------------------------------------------------------------------------------------
--  ALM TaskId :421599 : Add new column to TABV_ATTR_VALUES TABLE 
--  ALTER query for TABV_ATTR_VALUES to add USER_ID column
------------------------------------------------------------------------------------------------------------------
ALTER TABLE TABV_ATTR_VALUES ADD USER_ID NUMBER(15,0);


------------------------------------------------------------------------------------------------------------------
--  ALM TaskId :421599 : Add new column to TABV_ATTR_VALUES TABLE 
--  ALTER  query for TABV_ATTR_VALUES to Add a Foreign key
------------------------------------------------------------------------------------------------------------------
ALTER TABLE TABV_ATTR_VALUES ADD CONSTRAINT TABV_ATTR_VAL_USERS_FK FOREIGN KEY(USER_ID) REFERENCES TABV_APIC_USERS(USER_ID);


------------------------------------------------------------------------------------------------------------------
--  ALM TaskId : 221730: Add new column to T_RVW_QNAIRE_RESPONSE TABLE 
--  ALTER query for T_RVW_QNAIRE_RESPONSE  
------------------------------------------------------------------------------------------------------------------

ALTER TABLE T_RVW_QNAIRE_RESPONSE 
ADD (REVIEWED_FLAG VARCHAR2(1) DEFAULT 'N' NOT NULL);

ALTER TABLE T_RVW_QNAIRE_RESPONSE 
ADD (REVIEWED_DATE TIMESTAMP );

ALTER TABLE T_RVW_QNAIRE_RESPONSE 
ADD (REVIEWED_USER VARCHAR2(30));

------------------------------------------------------------------------------------------------------------------
--  ALM TaskId : 425649: Add new column to TABV_PROJECT_ATTR TABLE 
--  ALTER query for TABV_PROJECT_ATTR  
------------------------------------------------------------------------------------------------------------------
ALTER TABLE TABV_PROJECT_ATTR ADD FM_ATTR_REMARK VARCHAR2(4000 BYTE);

------------------------------------------------------------------------------------------------------------------
--  ALM TaskId : 432961: Add new column to TABV_PROJECT_ATTR TABLE 
--  ALTER query for TABV_PROJECT_ATTR  
------------------------------------------------------------------------------------------------------------------
ALTER TABLE T_FOCUS_MATRIX_VERSION_ATTR ADD FM_ATTR_REMARK VARCHAR2(4000 BYTE);

------------------------------------------------------------------------------------------------------------------
--  ALM TaskId : 464549: Add new column to T_COMPLI_RVW_HEX TABLE 
--  ALTER query for T_COMPLI_RVW_HEX  
------------------------------------------------------------------------------------------------------------------
ALTER TABLE T_COMPLI_RVW_HEX
ADD (
    SSD_FILE_NAME VARCHAR2(200 BYTE),
    CHECK_SSD_REPORT_NAME VARCHAR2(200 BYTE),
    RELEASE_ID NUMBER(15,0)
);

------------------------------------------------------------------------------------------------------------------
--  ALM TaskId : 464549: Create table  t_compli_rvw_hex_params
--  ALTER query for t_compli_rvw_hex_params  
------------------------------------------------------------------------------------------------------------------
CREATE TABLE t_compli_rvw_hex_params (
    hex_params_id       NUMBER(15,0) PRIMARY KEY,
    compli_rvw_hex_id   NUMBER(15,0) NOT NULL,
    param_id            NUMBER(15,0) NOT NULL,
    check_value         BLOB NOT NULL,
    result              VARCHAR2(1 BYTE) NOT NULL,
    lab_obj_id          NUMBER(15,0),
    rev_id              NUMBER(15,0),
    created_user        VARCHAR2(30 BYTE) NOT NULL,
    created_date        TIMESTAMP(6) NOT NULL,
    modified_user       VARCHAR2(30 BYTE),
    modified_date       TIMESTAMP(6),
    version             NUMBER(15,0) NOT NULL
);

COMMENT ON COLUMN t_compli_rvw_hex_params.result IS
    'O-OK, N-No Rule, C-CSSD, S-SSD2Rv';

ALTER TABLE t_compli_rvw_hex_params ADD CONSTRAINT t_compli_rvw_hex_params__un UNIQUE ( compli_rvw_hex_id,
                                                                                                 param_id );
ALTER TABLE t_compli_rvw_hex_params ADD CONSTRAINT t_compli_rvw_hex_params_fk1 FOREIGN KEY ( compli_rvw_hex_id )
    REFERENCES t_compli_rvw_hex ( compli_rvw_hex_id ) NOT DEFERRABLE;

ALTER TABLE t_compli_rvw_hex_params ADD CONSTRAINT t_compli_rvw_hex_params_fk2 FOREIGN KEY ( param_id )
    REFERENCES t_parameter ( id ) NOT DEFERRABLE;


------------------------------------------------------------------------------------------------------------------
--  ALM TaskId : 462621: ACL for services
------------------------------------------------------------------------------------------------------------------

---------------------------------------------------
-- new columns in T_WS_SYSTEMS
ALTER TABLE  T_WS_SYSTEMS ADD
(
    SERV_ACCESS_TYPE VARCHAR2 (1) DEFAULT 'R' NOT NULL,
    CREATED_DATE     TIMESTAMP  ,
    CREATED_USER     VARCHAR2 (30)   ,
    MODIFIED_DATE    TIMESTAMP ,
    MODIFIED_USER    VARCHAR2 (30)
) ;
  
COMMENT ON COLUMN T_WS_SYSTEMS.SERV_ACCESS_TYPE IS 'Values : A-All, I-All Internal Services, R-Restricted';

--Update existing records
update T_WS_SYSTEMS set SERV_ACCESS_TYPE = 'I' WHERE SYSTEM_TYPE = 'ICDM';
update T_WS_SYSTEMS set CREATED_DATE = sys_extract_utc(systimestamp);
update T_WS_SYSTEMS set CREATED_USER = 'DGS_ICDM';

-- Enable not null constraints
ALTER TABLE T_WS_SYSTEMS MODIFY (CREATED_DATE NOT NULL ENABLE);
ALTER TABLE T_WS_SYSTEMS MODIFY (CREATED_USER NOT NULL ENABLE);

---------------------------------------------------
-- new table T_WS_SERVICES to store iCDM service list
CREATE TABLE T_WS_SERVICES
  (
    WS_SERV_ID    NUMBER (15) NOT NULL ,
    SERV_METHOD   VARCHAR2 (10) NOT NULL ,
    SERV_URI      VARCHAR2 (200) NOT NULL ,
    MODULE        VARCHAR2 (10) NOT NULL ,
    SERV_DESC     VARCHAR2 (2000) ,
    SERVICE_SCOPE VARCHAR2 (1) NOT NULL ,
    DELETE_FLAG   VARCHAR2 (1) ,
    CREATED_DATE  TIMESTAMP NOT NULL ,
    CREATED_USER  VARCHAR2 (30) NOT NULL ,
    MODIFIED_DATE TIMESTAMP ,
    MODIFIED_USER VARCHAR2 (30) ,
    VERSION       NUMBER (15) NOT NULL
  ) ;
COMMENT ON COLUMN T_WS_SERVICES.SERVICE_SCOPE
IS  'Values : E-Exteral, I-Internal, Default - I' ;

ALTER TABLE T_WS_SERVICES ADD CONSTRAINT T_WS_SERVICES_PK PRIMARY KEY ( WS_SERV_ID ) ;
ALTER TABLE T_WS_SERVICES ADD CONSTRAINT T_WS_SERV__UN UNIQUE ( SERV_METHOD , SERV_URI ) ;


---------------------------------------------------
-- new table T_WS_SYSTEM_SERVICES to maintain list of services allowed for client systems
CREATE TABLE T_WS_SYSTEM_SERVICES
  (
    SERV_SYS_ID   NUMBER (15) NOT NULL ,
    SYSTEM_ID     NUMBER (15) NOT NULL ,
    WS_SERV_ID    NUMBER (15) NOT NULL ,
    CREATED_DATE  TIMESTAMP NOT NULL ,
    CREATED_USER  VARCHAR2 (30) NOT NULL ,
    MODIFIED_DATE TIMESTAMP ,
    MODIFIED_USER VARCHAR2 (30) ,
    VERSION       NUMBER (10) NOT NULL
  ) ;

ALTER TABLE T_WS_SYSTEM_SERVICES ADD CONSTRAINT T_WS_SYSTEM_SERVICES_PK PRIMARY KEY ( SERV_SYS_ID ) ;
ALTER TABLE T_WS_SYSTEM_SERVICES ADD CONSTRAINT T_WS_SYS_SERV__UN UNIQUE ( SYSTEM_ID , WS_SERV_ID ) ;
ALTER TABLE T_WS_SYSTEM_SERVICES ADD CONSTRAINT T_WS_SYS_SERV_FK1 FOREIGN KEY ( SYSTEM_ID ) REFERENCES T_WS_SYSTEMS ( SYSTEM_ID ) ;
ALTER TABLE T_WS_SYSTEM_SERVICES ADD CONSTRAINT T_WS_SYS_SERV_FK2 FOREIGN KEY ( WS_SERV_ID ) REFERENCES T_WS_SERVICES ( WS_SERV_ID ) ;


-------------------------------------------------------
----ALM Task Id : 477616 : New Column for QssdFlag 
----ALTER query for T_PARAMETER
ALTER TABLE T_PARAMETER ADD (QSSD_FLAG VARCHAR2(1) DEFAULT 'N' NOT NULL);


-------------------------------------------------------
-- ALM Task 478378 : Missing column in DB script
ALTER TABLE T_PIDC_A2L ADD (IS_ACTIVE VARCHAR2(1) default 'Y' NOT NULL);
ALTER TABLE T_PARAMETER ADD (IS_BLACK_LIST_LABEL VARCHAR2(1) DEFAULT 'N' NOT NULL);
-------------------------------------------------------
-- ALM Task 479186 :PIDC Editor > A2L page, Active flag is set, to yes, even if A2L file is not mapped to a PIDC version
----update existing record
UPDATE T_PIDC_A2L  set IS_ACTIVE = 'N' WHERE PIDC_VERS_ID IS NULL;

COMMIT;

------------------------------------------------------------------------------------------------------------------
--  ALM TaskId : 484274: Add new column to T_COMPLI_RVW_HEX TABLE 
--  ALTER query for T_COMPLI_RVW_HEX  
------------------------------------------------------------------------------------------------------------------
ALTER TABLE T_COMPLI_RVW_HEX
ADD (
    HEX_CHECKSUM VARCHAR2(30)
);



spool off