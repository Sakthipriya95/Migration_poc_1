spool c:\temp\01_Table_Alters.log

------------------------------------------------------------------------------------------------------------------
--Task 749550: Impl: Azure SSO in iCDM Analysis and implementation Part - 1
------------------------------------------------------------------------------------------------------------------
CREATE TABLE T_USER_LOGIN_INFO
   (USER_LOGIN_INFO_ID NUMBER NOT NULL ENABLE, 
    USER_NT_ID VARCHAR2(30 CHAR) UNIQUE NOT NULL ENABLE,
    AZURE_LOGIN_COUNT NUMBER,
    LDAP_LOGIN_COUNT NUMBER, 
    CREATED_DATE TIMESTAMP (6) NOT NULL ENABLE, 
    CREATED_USER VARCHAR2(30 CHAR) NOT NULL ENABLE, 
    MODIFIED_DATE TIMESTAMP (6), 
    MODIFIED_USER VARCHAR2(30 CHAR), 
    VERSION NUMBER NOT NULL ENABLE,
    CONSTRAINT T_USER_LOGIN_INFO_PK PRIMARY KEY (USER_LOGIN_INFO_ID));
    
------------------------------------------------------------------------------------------------------------------
--Task 716513: Impl : Number of decimals in check value of review result
------------------------------------------------------------------------------------------------------------------

CREATE TABLE T_USER_PREFERENCES(
    USER_PREF_ID      NUMBER(15) NOT NULL,
    USER_ID           NUMBER(15) NOT NULL,
    USER_PREF_KEY     VARCHAR2(30 CHAR) NOT NULL,
    USER_PREF_VAL     VARCHAR2(30 CHAR) NOT NULL,
    CREATED_DATE      TIMESTAMP(6) NOT NULL,
    CREATED_USER      VARCHAR2(30 CHAR) NOT NULL,
    MODIFIED_DATE     TIMESTAMP(6),
    MODIFIED_USER     VARCHAR2(30 CHAR),
    VERSION           NUMBER(10) NOT NULL,

    CONSTRAINT USER_PREFERENCES_PK PRIMARY KEY (USER_PREF_ID),
    CONSTRAINT USER_PREFERENCES_FK1 FOREIGN KEY(USER_ID) REFERENCES TABV_APIC_USERS(USER_ID)
);

spool off

