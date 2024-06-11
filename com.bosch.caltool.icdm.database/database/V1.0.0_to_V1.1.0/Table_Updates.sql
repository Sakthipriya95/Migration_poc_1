spool c:\temp\table_updates.log

--------------------------------------------------------
--  August-02-2013  
--------------------------------------------------------

--
-- TABV_COMMON_PARAMS
--
-- New table for storing the database level common configurations
-- 
--

CREATE TABLE TABV_COMMON_PARAMS
  (
    "PARAM_ID"         VARCHAR2(30 BYTE) PRIMARY KEY,
    "PARAM_DESC"      VARCHAR2(100 BYTE),
    "PARAM_VALUE" VARCHAR2(50 BYTE) NOT NULL,
    "VERSION"      NUMBER NOT NULL
  )
;


spool off
