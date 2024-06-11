spool c:\temp\01_Table_Alters.log

--585073: Impl :Edit flag in attributes editor
--added flag in attributes table which will be used to 
--restrict users from adding values for the attribute
ALTER TABLE TABV_ATTRIBUTES ADD (ADD_VALUES_BY_USERS_FLAG VARCHAR2(1) DEFAULT 'Y' NOT NULL);

--------------------------------------------------------------------------------------------------------------------
--589930: Impl - Performance improvement in PK_PAR2WP_PKG
--------------------------------------------------------------------------------------------------------------------
CREATE GLOBAL TEMPORARY TABLE GTT_WP_PARAM_MAPPING_DETAILS
   (
        PARAM_ID NUMBER(15,0),
        WP_NAME_CUST VARCHAR2(130 BYTE),
        WP_RESP_INHERIT_FLAG VARCHAR2(1 BYTE),
        WP_NAME_CUST_INHERIT_FLAG VARCHAR2(1 BYTE),
        PAR_A2L_RESP_ID NUMBER(15,0),
        WP_RESP_ID NUMBER(15,0)
   ) ON COMMIT DELETE ROWS ;

--------------------------------------------------------------------------------------------------------------------
--591124: Impl: Import WP-Responsibility and mappings from A2L groups gives an error message
--------------------------------------------------------------------------------------------------------------------
alter table T_A2L_WORK_PACKAGES modify WP_DESC varchar2(512);

--------------------------------------------------------------------------------------------------------------------
--593959 : Impl : New iCDM Service to retrieve error codes and their details
--------------------------------------------------------------------------------------------------------------------
ALTER TABLE T_MESSAGES ADD ERROR_YN VARCHAR2(1);
ALTER TABLE T_MESSAGES ADD CAUSE VARCHAR2(4000);
ALTER TABLE T_MESSAGES ADD SOLUTION VARCHAR2(4000);

spool off

