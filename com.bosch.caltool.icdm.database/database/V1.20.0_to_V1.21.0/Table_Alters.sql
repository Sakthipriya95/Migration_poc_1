spool c:\temp\table_alters.log

--------------------------------------------------------
-- TABV_UCP_MATRIX_ATTRS
--
-- New table for Focus Matrix . The info of assignment of Colors and comments given against each mapped attr available. 
-- 
-- ICDM-1266
--
CREATE TABLE T_FOCUS_MATRIX
(
  FM_ID     NUMBER(15)   NOT NULL,
  UCPA_ID      NUMBER(15)   NOT NULL,
  PIDC_VERS_ID NUMBER(15)   NOT NULL,
  COLOR_CODE   VARCHAR2(3 BYTE),
  COMMENTS     VARCHAR2(4000 BYTE),
  CREATED_USER   VARCHAR2(30 BYTE)  NOT NULL,
  CREATED_DATE   TIMESTAMP(6)   NOT NULL,
  MODIFIED_DATE  TIMESTAMP(6),
  MODIFIED_USER  VARCHAR2(30 BYTE),
  VERSION        NUMBER NOT NULL,  
  
  CONSTRAINT T_FOCUS_MATRIX_PK PRIMARY KEY (FM_ID),
  CONSTRAINT T_FOCUS_MATRIX_FK1 FOREIGN KEY (UCPA_ID) REFERENCES tabv_ucp_attrs (UCPA_ID),
  CONSTRAINT T_FOCUS_MATRIX_FK2 FOREIGN KEY (PIDC_VERS_ID) REFERENCES T_PIDC_VERSION (PIDC_VERS_ID),
  CONSTRAINT T_FOCUS_MATRIX_UK1 UNIQUE (UCPA_ID,PIDC_VERS_ID)
);

COMMIT;
--------------------------------------------------------
--iCDM-1266
--Alter tabv_use_cases to add MATRIX_RELEVANT_YN when adding/editing a usecase
----------------------------------------------------------
alter table tabv_use_cases
add FOCUS_MATRIX_YN varchar2(1 BYTE)  ;

commit;
--------------------------------------------------------
--iCDM-1266
--Alter tabv_use_case_sections to add MATRIX_RELEVANT_YN when adding/editing a usecase section
----------------------------------------------------------
alter table tabv_use_case_sections
add FOCUS_MATRIX_YN varchar2(1 BYTE) ;

commit;
  
  
------------------------------------------------------------
--------------------------------------------------------
--  2015-04-02
-------------------------------------------------------- 

--------------------------------------------------------
--- ICDM-1406 - Data model changes in iCDM related to 
--- VCDM info change history storage in T_PIDC_CHANGE_HISTORY
--------------------------------------------------------
----------------------------------------------------------------------
--T_PIDC_CHANGE_HISTORY--
----------------------------------------------------------------------
ALTER TABLE T_PIDC_CHANGE_HISTORY ADD (OLD_APRJ_ID NUMBER(15,0),
                                       NEW_APRJ_ID NUMBER(15,0),
                                       PIDC_ACTION VARCHAR2(100));

commit;

--------------------------------------------------------
--iCDM-1611
--Alter T_CHARACTERISTICS to add FOCUS_MATRIX_YN to know the attribute classes that are relevant for the Focus Matrix
----------------------------------------------------------
alter table T_CHARACTERISTICS
add FOCUS_MATRIX_YN varchar2(1 BYTE) ;

commit;




---------------------------------------------------------------
--2015-09-18
---------------------------------------------------------------
--ICDM-1561 - Lock  a Review.
---------------------------------------------------------------
-- T_RVW_RESULTS
-----------------------------------------------------------------                                       

ALTER TABLE T_RVW_RESULTS ADD (LOCK_STATUS varchar2(1));

spool off



