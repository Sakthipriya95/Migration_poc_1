spool c:\temp\03_Table_Data.log

---------------------------------------------------------------------
-- ALM ID : 277838
--  Insert query for T_RM_CATEGORY
---------------------------------------------------------------------  
declare
    v_category_id number; 
    v_risk_level_id number;
    v_prj_character_id  number;
    CURSOR c_proj_character is  SELECT PRJ_CHARACTER_ID FROM T_RM_PROJECT_CHARACTER;
 begin
    select seqv_attributes.nextval into v_category_id from dual;     
    select risk_level_id into v_risk_level_id from t_rm_risk_level where risk_lvl_code ='RISK_LVL_HIGH';
    
INSERT INTO T_RM_CATEGORY (CATEGORY_ID,CATEGORY_ENG,CATEGORY_GER,VERSION,CAT_TYPE,CATEGORY_CODE) VALUES (v_category_id,'New third state','New third state',1,'D','RB_INPUT_THIRD_STATE');
 commit;
 
 open c_proj_character;
 LOOP
  FETCH c_proj_character into v_prj_character_id;  
      EXIT WHEN c_proj_character%notfound;
 Insert into DGS_ICDM.T_RM_CHARACTER_CATEGORY_MATRIX
   ( CCM_ID,PRJ_CHARACTER_ID, CATEGORY_ID, RISK_LEVEL_ID, CREATED_USER, CREATED_DATE, VERSION)
 Values
   ( seqv_attributes.nextval, v_prj_character_id,v_category_id, v_risk_level_id, 'DGS_ICDM', 
    sys_extract_utc(systimestamp), 1);
    
    END LOOP;  
end;
/

update T_RM_CATEGORY
   set CATEGORY_ENG = 'Not by RB in not RB-SW or Black-Box'
     , CATEGORY_GER = 'Nicht von RB in nicht RB-SW bzw. Black Box'
  where CATEGORY_CODE='RB_INPUT_THIRD_STATE';
  
update T_RM_CATEGORY
   set CATEGORY_ENG = 'By RB in RB-SW/Not RB-SW'
     , CATEGORY_GER = 'Nicht von RB in RB-SW/Nicht RB-SW'
  where CATEGORY_CODE='RB_INPUT_YES';  
  
update T_RM_CATEGORY
   set CATEGORY_ENG = 'Not by RB in RB-SW'
     , CATEGORY_GER = 'Nicht von RB in RB-SW'
  where CATEGORY_CODE='RB_INPUT_NO';  


---------------------------------------------------------------------
-- Story 281626 - Data migration of compli_Result column
-- 
-- Existing compli (C) is changed to No rule(N)
-- Existing Not-ok (N) is changed to C-SSD type(C)
---------------------------------------------------------------------  
update t_rvw_parameters set COMPLI_RESULT='t' where COMPLI_RESULT='C';
update t_rvw_parameters set COMPLI_RESULT='C' where COMPLI_RESULT='N';
update t_rvw_parameters set COMPLI_RESULT='N' where COMPLI_RESULT='t';

--Story 245540
INSERT INTO TABV_COMMON_PARAMS 
      (PARAM_ID, PARAM_DESC, PARAM_VALUE )
VALUES ('USECASE_UP_TO_DATE_INTERVAL', 'The time interval in days for a usecase to be confirmed as up to date', '10' );
commit;

------------------------------------------------------------------------
--  ALM TaskId : 286162: Add link to Docupedia
--  Insert query for TABV_COMMON_PARAMS
------------------------------------------------------------------------

INSERT INTO TABV_COMMON_PARAMS (PARAM_ID,PARAM_DESC,PARAM_VALUE,VERSION) 
VALUES ('EMR_SHEET_WIKI_LINK','Link to EMR-Sheet in Wiki','https://inside-docupedia.bosch.com/confluence/display/dsapplication/iCDM+-+intelligent+Calibration+Data+Management',1);
COMMIT;

INSERT INTO TABV_ICDM_FILES (FILE_ID,NODE_ID,NODE_TYPE,FILE_NAME,CREATED_USER,CREATED_DATE,VERSION,FILE_COUNT) 
VALUES (SEQV_ATTRIBUTES.NEXTVAL,-6,'REPORT_LOGO','boschlogo_icon.png',user,sysdate,1,1);

INSERT INTO TABV_ICDM_FILE_DATA (FILE_DATA_ID,FILE_ID,FILE_DATA,VERSION) SELECT SEQV_ATTRIBUTES.NEXTVAL,FILE_ID,EMPTY_BLOB(),1 FROM TABV_ICDM_FILES WHERE NODE_ID = -6 and NODE_TYPE = 'REPORT_LOGO';

COMMIT;

--------------------------------------------------------
--  Version update script
--------------------------------------------------------      
delete from TABV_COMMON_PARAMS WHERE PARAM_ID = 'iCDM_CLIENT_VERSION';
Insert into TABV_COMMON_PARAMS (PARAM_ID,PARAM_DESC,PARAM_VALUE,VERSION) values ('iCDM_CLIENT_VERSION','iCDM Client''s current version','2.3.0',1);

COMMIT;

spool off