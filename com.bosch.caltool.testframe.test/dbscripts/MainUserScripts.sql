spool c:\temp\MainUserScripts.log

--------------------------------------------------------
--
-- To be executed in DGS_ICDM user
--
--------------------------------------------------------


--------------------------------------------------------
--  2014-10-16
--create the tables in DGS_ICDM 


--Drop existing objects
drop TABLE TEST_CHILD_TABLE cascade constraints;
drop TABLE TEST_PARENT_TABLE cascade constraints;
drop TABLE TEST_WOKEYREF_TABLE cascade constraints;

drop SEQUENCE  SEQ_TEMP;

--Create objects
CREATE TABLE TEST_PARENT_TABLE
    (
        PAR_ID       NUMBER PRIMARY KEY,
        PAR_ADDR     VARCHAR2(200 BYTE),
        CREATED_DATE TIMESTAMP (6),
        PAR_NAME     VARCHAR2(50 BYTE) NOT NULL ,
        VERSION      NUMBER
    );
    
CREATE TABLE TEST_CHILD_TABLE
    (
        CHILD_ID   NUMBER PRIMARY KEY,
        CHILD_NAME VARCHAR2(50 BYTE) NOT NULL ,
        PAR_ID     NUMBER NOT NULL ,
        
        CONSTRAINT FK_TEST_CHILD_TABLE_PAR_ID FOREIGN KEY (PAR_ID) REFERENCES TEST_PARENT_TABLE (PAR_ID)
    ) ;
    
CREATE TABLE TEST_WOKEYREF_TABLE
    (
        WKRT_ID       NUMBER PRIMARY KEY,
        REC_TYPE     VARCHAR2(200 BYTE) NOT NULL,
        NODE_ID NUMBER NOT NULL,
        REF_NAME     VARCHAR2(50 BYTE) ,
        VERSION      NUMBER
    );

CREATE SEQUENCE  SEQ_TEMP  INCREMENT BY 10;
    
--provide grant to DGS_ICDM_JPA user 

GRANT select, insert, update, delete ON TEST_PARENT_TABLE TO DGS_ICDM_JPA;
GRANT select, insert, update, delete ON TEST_CHILD_TABLE TO DGS_ICDM_JPA;
GRANT select, insert, update, delete ON TEST_WOKEYREF_TABLE TO DGS_ICDM_JPA;

GRANT select ON SEQ_TEMP TO DGS_ICDM_JPA;

spool off

