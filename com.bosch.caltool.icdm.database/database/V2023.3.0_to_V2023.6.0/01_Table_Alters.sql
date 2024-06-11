spool c:\temp\01_Table_Alters.log

------------------------------------------------------------------------------------------------------------------
--Task 716517 : Impl : The ER Diagram & DB changes & Entity should be created/updated to Store the Reviews OBD 
--related Flag in Review Result Table
------------------------------------------------------------------------------------------------------------------

-- Adding New Columns in t_rvw_results
ALTER TABLE T_RVW_RESULTS
ADD (
     OBD_FLAG VARCHAR2(1 CHAR),
     SIMP_QUES_RESP_FLAG VARCHAR2(1 CHAR),
     SIMP_QUES_REMARKS VARCHAR2(4000 CHAR)
    );

    ----------------------------------------------------------------------------------------------------------------------------
--Task:729570 : Review changes to handle the different cases in adding general Questions based on the division in PIDC Version
--------------------------------------------------------------------------------------------------------------------------------
--Altering Qnaire_Resp_id column nullable as for simplified Qnaire, th wp/resp Structure will be nullable

ALTER TABLE T_RVW_QNAIRE_RESP_VARIANTS MODIFY QNAIRE_RESP_ID NUMBER NULL;
   


------------------------------------------------------------------------------------------------------------------
--Task 729577 :Allow adding groups to access rights page of PIDCs

------------------------------------------------------------------------------------------------------------------
CREATE TABLE "T_ACTIVE_DIRECTORY_GROUPS" (
    "AD_GROUP_ID"   NUMBER,
    "GROUP_NAME"    VARCHAR2(4000 CHAR)
        NOT NULL ENABLE,
    "GROUP_SID"     VARCHAR2(100 CHAR)
        NOT NULL ENABLE,
    "CREATED_USER"  VARCHAR2(30 CHAR)
        NOT NULL ENABLE,
    "CREATED_DATE"  TIMESTAMP(6)
        NOT NULL ENABLE,
    "MODIFIED_DATE" TIMESTAMP(6),
    "MODIFIED_USER" VARCHAR2(30 CHAR),
    "VERSION"       NUMBER
        NOT NULL ENABLE
);

CREATE TABLE "T_ACTIVE_DIRECTORY_GROUP_NODE_ACCESS" (
    "GROUP_ACCESS_ID"  NUMBER,
    "NODE_ID"          NUMBER(15, 0)
        NOT NULL ENABLE,
    "NODE_TYPE"        VARCHAR2(15 CHAR)
        NOT NULL ENABLE,
    "AD_GROUP_ID"      NUMBER
        NOT NULL ENABLE,
    "READRIGHT"        VARCHAR2(1 CHAR) DEFAULT 'Y',
    "WRITERIGHT"       VARCHAR2(1 CHAR) DEFAULT 'N',
    "GRANTRIGHT"       VARCHAR2(1 CHAR) DEFAULT 'N',
    "OWNER"            VARCHAR2(1 CHAR) DEFAULT 'N',
    "IS_SET_BY_SYSTEM" VARCHAR2(1 CHAR) DEFAULT 'N'
        NOT NULL ENABLE,
    "CREATED_USER"     VARCHAR2(30 CHAR)
        NOT NULL ENABLE,
    "CREATED_DATE"     TIMESTAMP(6)
        NOT NULL ENABLE,
    "MODIFIED_DATE"    TIMESTAMP(6),
    "MODIFIED_USER"    VARCHAR2(30 CHAR),
    "VERSION"          NUMBER
        NOT NULL ENABLE  
);

CREATE TABLE "T_ACTIVE_DIRECTORY_GROUP_USERS" (
    "GROUP_USERS_ID"   NUMBER NOT NULL ENABLE,
    "AD_GROUP_ID"   NUMBER,
    "USERNAME"      VARCHAR2(30 CHAR) DEFAULT 'N'
        NOT NULL ENABLE,
    "IS_ICDM_USER"  VARCHAR2(1 CHAR) DEFAULT 'N',
    "CREATED_USER"  VARCHAR2(30 CHAR)
        NOT NULL ENABLE,
    "CREATED_DATE"  TIMESTAMP(6)
        NOT NULL ENABLE,
    "MODIFIED_DATE" TIMESTAMP(6),
    "MODIFIED_USER" VARCHAR2(30 CHAR),
    "VERSION"       NUMBER
        NOT NULL ENABLE
);

ALTER TABLE T_ACTIVE_DIRECTORY_GROUPS ADD CONSTRAINT T_ACTIVE_DIRECTORY_GROUPS_PK PRIMARY KEY ( ad_group_id );

ALTER TABLE T_ACTIVE_DIRECTORY_GROUPS ADD CONSTRAINT T_ACTIVE_DIRECTORY_GROUPS_UK UNIQUE ( group_sid );

ALTER TABLE T_ACTIVE_DIRECTORY_GROUP_NODE_ACCESS  ADD CONSTRAINT T_ACTIVE_DIRECTORY_GROUP_NODE_ACCESS_PK PRIMARY KEY(GROUP_ACCESS_ID);

ALTER TABLE T_ACTIVE_DIRECTORY_GROUP_NODE_ACCESS  ADD CONSTRAINT T_ACTIVE_DIRECTORY_GROUP_NODE_ACCESS_UK UNIQUE (AD_GROUP_ID,NODE_ID);

ALTER TABLE T_ACTIVE_DIRECTORY_GROUP_NODE_ACCESS  ADD CONSTRAINT T_ACTIVE_DIRECTORY_GROUP_NODE_ACCESS_FK FOREIGN KEY (AD_GROUP_ID) REFERENCES T_ACTIVE_DIRECTORY_GROUPS (AD_GROUP_ID);

ALTER TABLE T_ACTIVE_DIRECTORY_GROUP_USERS ADD CONSTRAINT T_ACTIVE_DIRECTORY_GROUP_USERS_PK PRIMARY KEY ( GROUP_USERS_ID );

ALTER TABLE T_ACTIVE_DIRECTORY_GROUP_USERS ADD CONSTRAINT T_ACTIVE_DIRECTORY_GROUP_USERS_UK UNIQUE ( AD_GROUP_ID,USERNAME );

ALTER TABLE T_ACTIVE_DIRECTORY_GROUP_USERS ADD CONSTRAINT T_ACTIVE_DIRECTORY_GROUP_USERS_FK FOREIGN KEY ( ad_group_id ) REFERENCES T_ACTIVE_DIRECTORY_GROUPS ( ad_group_id );



spool off

