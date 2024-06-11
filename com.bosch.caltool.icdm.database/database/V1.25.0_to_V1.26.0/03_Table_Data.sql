spool c:\temp\03_Table_Data.log
---------------------------------------------------------------
----ICDM-2189
---------------------------------------------------------------
---------------------------------------------------------------
-- T_QNAIRE_ANS_OPEN_POINTS
---------------------------------------------------------------

-- insert the open_point col values from t_rvw_qnaire_ans table to t_qnaire_ans_open_points table
insert into T_QNAIRE_ANS_OPEN_POINTS(OPEN_POINTS,RVW_ANSWER_ID)
select OPEN_POINTS,RVW_ANSWER_ID from T_RVW_QNAIRE_ANSWER;

---------------------------------------------------------------
----ICDM-2247
-- New messages for PIDC Statistics in T_MESSAGES
---------------------------------------------------------------
Insert into T_MESSAGES (MESSAGE_ID,GROUP_NAME,NAME,MESSAGE_TEXT,MESSAGE_TEXT_GER,VERSION) values (SEQV_ATTRIBUTES.nextval,'PIDC_EDITOR','PROJECT_STATISTICS_COMMON','Total attributes : {0}\t\t Used attributes : {1}\t\t Not Used attributes : {2}\t\t Undefined attributes : {3}\t\t Last modified date : {4}','',1);
Insert into T_MESSAGES (MESSAGE_ID,GROUP_NAME,NAME,MESSAGE_TEXT,MESSAGE_TEXT_GER,VERSION) values (SEQV_ATTRIBUTES.nextval,'PIDC_EDITOR','PROJECT_STATISTICS_PIDCVERS','Total attributes : {0}\t\t Used attributes : {1}\t\t Not Used attributes : {2}\t\t Undefined attributes : {3}\t\tNew attributes : {4}\nProject use case items : {5}\t\tCoverage PIDC : {6} of {7} attributes\t\tUnrated Focus Matrix attributes : {8} of {9}\t\tLast modified date : {10}','',1);

---------------------------------------------------------------
----ICDM-2229
-- Storing PIDC_IMPORT_VERSION in TABV_COMMON_PARAMS
---------------------------------------------------------------
Insert into TABV_COMMON_PARAMS(PARAM_ID,PARAM_DESC,PARAM_VALUE,VERSION) values ('PIDC_IMPORT_VERSION','Minimum version of iCDM that is allowed for PIDC import ','1.26.0',1);

---------------------------------------------------------------
--iCDM-2234
--Alter TABV_PROJECT_ATTR to make TRNSFR_VCDM_YN = Y for all variant attributes
---------------------------------------------------------------
 update TABV_PROJECT_ATTR
   set TRNSFR_VCDM_YN = 'Y'
 where IS_VARIANT = 'Y'; 
---------------------------------------------------------------

 ---------------------------------------------------------
--ICDM-2305
--Table Upates for review params score column
----------------------------------------------------------
 
 update  T_RVW_PARAMETERS set review_score='9' where rvw_param_id in 
(select a.rvw_param_id from T_RVW_PARAMETERS a, t_rvw_results b where a.result_id=b.result_id and a.REVIEWED_FLAG='Y' and b.review_type='O');


update  T_RVW_PARAMETERS set review_score='3' where rvw_param_id in 
(select a.rvw_param_id from T_RVW_PARAMETERS a, t_rvw_results b where a.result_id=b.result_id and a.REVIEWED_FLAG='Y' and b.review_type='S');

update  T_RVW_PARAMETERS set review_score='3' where rvw_param_id in 
(select a.rvw_param_id from T_RVW_PARAMETERS a, t_rvw_results b where a.result_id=b.result_id and a.REVIEWED_FLAG='Y' and b.review_type='T');

--- For monica file
 update  T_RVW_PARAMETERS set review_score='8' where rvw_param_id in (
select a.rvw_param_id from T_RVW_PARAMETERS a, t_rvw_results b where a.result_id=b.result_id and b.source_type='MONICA' and a.REVIEWED_FLAG='Y');

 update  T_RVW_PARAMETERS set review_score='0' where rvw_param_id in 
(select a.rvw_param_id from T_RVW_PARAMETERS a, t_rvw_results b where a.result_id=b.result_id and a.REVIEWED_FLAG='N');

commit;



---------------------------------------------------------
--ICDM-2217
--New rows to store the mail template file for deletion of attribute values
----------------------------------------------------------

DECLARE
  v_file_id NUMBER;
BEGIN
  SELECT seqv_attributes.nextval INTO v_file_id FROM dual;
  
  --INSERTING into TABV_COMMON_PARAMS
  INSERT
  INTO TABV_COMMON_PARAMS
    (
      PARAM_ID,
      PARAM_DESC,
      PARAM_VALUE,
      VERSION
    )
    VALUES
    (
      'DELETE_ATTR_VAL_MAIL_TEMPLATE',
      'Template for notifying the Project owners when a attribute value is deleted',
      TO_CHAR(v_file_id),
      1
    );
    
  --INSERTING into TABV_ICDM_FILES
  INSERT
  INTO TABV_ICDM_FILES
    (
      FILE_ID,
      NODE_ID,
      NODE_TYPE,
      FILE_NAME,
      FILE_COUNT,
      CREATED_USER,
      CREATED_DATE,
      VERSION
    )
    VALUES
    (
      v_file_id,
      -4,
      'TEMPLATES',
      'delAttrValMailTemplate.txt',
      1,
      USER,
      sys_extract_utc(systimestamp),
      1
    );
    
  --INSERTING into TABV_ICDM_FILE_DATA
  INSERT
  INTO TABV_ICDM_FILE_DATA
    (
      FILE_DATA_ID,
      FILE_ID,
      FILE_DATA,
      VERSION
    )
    VALUES
    (
      seqv_attributes.nextval,
      v_file_id,
      EMPTY_BLOB(),
      1
    );
  COMMIT;
END;
/

--------------------------------------------------------------------------------------
ICDM-2307 Review score description is stored in t_messages table
-------------------------------------------------------------------------------------
Insert into T_MESSAGES (GROUP_NAME,NAME,MESSAGE_TEXT,MESSAGE_TEXT_GER) values ('REVIEW_SCORE','SCORE_0','Not reviewed',null);
Insert into T_MESSAGES (GROUP_NAME,NAME,MESSAGE_TEXT,MESSAGE_TEXT_GER) values ('REVIEW_SCORE','SCORE_1','Value only for calibration, must be modified',null);
Insert into T_MESSAGES (GROUP_NAME,NAME,MESSAGE_TEXT,MESSAGE_TEXT_GER) values ('REVIEW_SCORE','SCORE_2','Value not o.k., need to be changed',null);
Insert into T_MESSAGES (GROUP_NAME,NAME,MESSAGE_TEXT,MESSAGE_TEXT_GER) values ('REVIEW_SCORE','SCORE_3','Value reviewed, calibration required',null);
Insert into T_MESSAGES (GROUP_NAME,NAME,MESSAGE_TEXT,MESSAGE_TEXT_GER) values ('REVIEW_SCORE','SCORE_4','Neutral calibration, calibration required',null);
Insert into T_MESSAGES (GROUP_NAME,NAME,MESSAGE_TEXT,MESSAGE_TEXT_GER) values ('REVIEW_SCORE','SCORE_5','Value calibrated, further test required',null);
Insert into T_MESSAGES (GROUP_NAME,NAME,MESSAGE_TEXT,MESSAGE_TEXT_GER) values ('REVIEW_SCORE','SCORE_6','TBD',null);
Insert into T_MESSAGES (GROUP_NAME,NAME,MESSAGE_TEXT,MESSAGE_TEXT_GER) values ('REVIEW_SCORE','SCORE_7','Defined in Start Review, ready for production',null);
Insert into T_MESSAGES (GROUP_NAME,NAME,MESSAGE_TEXT,MESSAGE_TEXT_GER) values ('REVIEW_SCORE','SCORE_8','Automated Review, ready for production',null);
Insert into T_MESSAGES (GROUP_NAME,NAME,MESSAGE_TEXT,MESSAGE_TEXT_GER) values ('REVIEW_SCORE','SCORE_9','Review completed, ready for production',null);
----------------------------------------------------------------------------------------

---------------------------------------------------------
--ICDM-2182
--Procedure to insert the Quotation Status Attribute and its values
--It also inserts these attr_id and value_id into TABV_COMMON_PARAMS table
----------------------------------------------------------

DECLARE
 v_attr_id NUMBER;
 v_val_id NUMBER;
BEGIN
  SELECT seqv_attributes.nextval INTO v_attr_id FROM dual;
  SELECT seqv_attributes.nextval INTO v_val_id FROM dual;
  
--INSERTING into TABV_ATTRIBUTES
INSERT INTO TABV_ATTRIBUTES (
   ATTR_ID, ATTR_NAME_ENG, ATTR_NAME_GER, 
   ATTR_DESC_ENG, ATTR_DESC_GER, GROUP_ID, 
   VALUE_TYPE_ID, CREATED_DATE, MODIFIED_DATE, 
   CREATED_USER, MODIFIED_USER, NORMALIZED_FLAG, 
   DELETED_FLAG, ATTR_LEVEL, MANDATORY, 
   UNITS, FORMAT, 
   PART_NUMBER_FLAG, SPEC_LINK_FLAG, ATTR_SECURITY, 
   VALUE_SECURITY, CHAR_ID, CHANGE_COMMENT, 
   EADM_NAME, GROUP_ATTR_FLAG) 
VALUES ( v_attr_id ,
 'Quotation Status',
 'Quotation Status',
 'Quotation Status',
 'Quotation Status',
 32,
 1,
 sys_extract_utc(systimestamp),
 null,
 USER,
  ' ',
 'N',
 'N',
 null,
'N',
'-',
'',
 'N',
 'N',
 'E',
'E',
 '',
 '',
 'Quotation Status',
 '');
    
 --INSERTING ATTR_ID into TABV_COMMON_PARAMS
  INSERT
  INTO TABV_COMMON_PARAMS
    (
      PARAM_ID,
      PARAM_DESC,
      PARAM_VALUE,
      VERSION
    )
    VALUES
    (
      'QUOT_ATTR_ID',
      'This attribute defines the status of PIDC',
      TO_CHAR(v_attr_id),
      1
    );
    
  --INSERTING into TABV_ATTR_VALUES_TABLE
  INSERT INTO TABV_ATTR_VALUES (
   VALUE_ID, ATTR_ID, VALUE_DESC_ENG, 
   TEXTVALUE_ENG,CREATED_DATE, 
   CREATED_USER, DELETED_FLAG, 
   CLEARING_STATUS) 
VALUES ( v_val_id,
  v_attr_id,
 'quotation (hidden)',
  'quotation (hidden)',
 sys_extract_utc(systimestamp),
 USER,
 'N',
'Y');

--INSERTING VALUE_ID into TABV_COMMON_PARAMS
  INSERT
  INTO TABV_COMMON_PARAMS
    (
      PARAM_ID,
      PARAM_DESC,
      PARAM_VALUE,
      VERSION
    )
    VALUES
    (
      'QUOT_VALUE_HIDDEN_STATUS',
      'Value-IDs for which the PIDC should not be shown',
      TO_CHAR(v_val_id),
      1
    );
    
 --INSERTING other values for attribute Quotation Status into TABV_ATTR_VALUES table
  INSERT INTO TABV_ATTR_VALUES (
   VALUE_ID, ATTR_ID, VALUE_DESC_ENG, 
   TEXTVALUE_ENG,CREATED_DATE, 
   CREATED_USER, DELETED_FLAG, 
   CLEARING_STATUS) 
VALUES (  seqv_attributes.nextval,
  v_attr_id,
 'finished',
  'finished',
 sys_extract_utc(systimestamp),
 USER,
 'N',
'Y');   
 
INSERT INTO TABV_ATTR_VALUES (
   VALUE_ID, ATTR_ID, VALUE_DESC_ENG, 
   TEXTVALUE_ENG,CREATED_DATE, 
   CREATED_USER, DELETED_FLAG, 
   CLEARING_STATUS) 
VALUES (  seqv_attributes.nextval,
  v_attr_id,
 'quotation',
  'quotation',
 sys_extract_utc(systimestamp),
 USER,
 'N',
'Y'); 

INSERT INTO TABV_ATTR_VALUES (
   VALUE_ID, ATTR_ID, VALUE_DESC_ENG, 
   TEXTVALUE_ENG,CREATED_DATE, 
   CREATED_USER, DELETED_FLAG, 
   CLEARING_STATUS) 
VALUES (  seqv_attributes.nextval,
  v_attr_id,
 'running',
  'running',
 sys_extract_utc(systimestamp),
 USER,
 'N',
'Y'); 

  COMMIT;
END;
/

---------------------------------------------------------
-- ICDM-2317 Changes : 
-- 1. Set the review score to 0, if check value is not available for a parameter in review result. 
-- 2. Mark those reviews as 'In progress' if they are already completed. 
--
----------------------------------------------------------

update
  t_rvw_results 
set
  rvw_status = 'I' 
where
  result_id in 
    (select  result_id  from t_rvw_parameters where checked_value is null and review_score in (8,9)) 
  and rvw_status = 'C';

update 
  t_rvw_parameters 
set 
  review_score = 0 
where 
  checked_value is null 
  and review_score in (8,9);


COMMIT;

---------------------------------------------------------
-- ICDM-2336 tooltip for review types 
----------------------------------------------------------
Insert into T_MESSAGES (GROUP_NAME,NAME,MESSAGE_TEXT,MESSAGE_TEXT_GER) values ('REVIEW_TYPE','TOOLTIP_START_RVW','Start Review',null);
Insert into T_MESSAGES (GROUP_NAME,NAME,MESSAGE_TEXT,MESSAGE_TEXT_GER) values ('REVIEW_TYPE','TOOLTIP_OFFICIAL_RVW','Official Review',null);
Insert into T_MESSAGES (GROUP_NAME,NAME,MESSAGE_TEXT,MESSAGE_TEXT_GER) values ('REVIEW_TYPE','TOOLTIP_TEST_RVW','Test Review',null); 

---------------------------------------------------------
-- ICDM-2188 Review questionnaire : Language based column labels 
---------------------------------------------------------

-- Existing columns
Insert into T_MESSAGES (MESSAGE_ID,GROUP_NAME,NAME,MESSAGE_TEXT,MESSAGE_TEXT_GER,VERSION) values (SEQV_ATTRIBUTES.nextval,'REVIEW_QUESTIONNAIRES','HINT','Notes Regarding Question','Hinweis zur Frage',1);
Insert into T_MESSAGES (MESSAGE_ID,GROUP_NAME,NAME,MESSAGE_TEXT,MESSAGE_TEXT_GER,VERSION) values (SEQV_ATTRIBUTES.nextval,'REVIEW_QUESTIONNAIRES','MEASURABLE_Y_N','Measurement Existing (Y/N)','Messung vorhanden (J/N)',1);
Insert into T_MESSAGES (MESSAGE_ID,GROUP_NAME,NAME,MESSAGE_TEXT,MESSAGE_TEXT_GER,VERSION) values (SEQV_ATTRIBUTES.nextval,'REVIEW_QUESTIONNAIRES','SERIES_MAT_Y_N','Ready For Mass Production (Y/N)','Serienreife (J/N)',1);
Insert into T_MESSAGES (MESSAGE_ID,GROUP_NAME,NAME,MESSAGE_TEXT,MESSAGE_TEXT_GER,VERSION) values (SEQV_ATTRIBUTES.nextval,'REVIEW_QUESTIONNAIRES','REMARK','Comment','Kommentar',1);
Insert into T_MESSAGES (MESSAGE_ID,GROUP_NAME,NAME,MESSAGE_TEXT,MESSAGE_TEXT_GER,VERSION) values (SEQV_ATTRIBUTES.nextval,'REVIEW_QUESTIONNAIRES','OPL_OPEN_POINTS','Open Issues','Offene Punkte',1);
Insert into T_MESSAGES (MESSAGE_ID,GROUP_NAME,NAME,MESSAGE_TEXT,MESSAGE_TEXT_GER,VERSION) values (SEQV_ATTRIBUTES.nextval,'REVIEW_QUESTIONNAIRES','RESULT','Result OK','Ergebnis in Ordnung',1);

-- New columns
Insert into T_MESSAGES (MESSAGE_ID,GROUP_NAME,NAME,MESSAGE_TEXT,MESSAGE_TEXT_GER,VERSION) values (SEQV_ATTRIBUTES.nextval,'REVIEW_QUESTIONNAIRES','OPL_MEASURE','Measure','Maﬂnahmen',1);
Insert into T_MESSAGES (MESSAGE_ID,GROUP_NAME,NAME,MESSAGE_TEXT,MESSAGE_TEXT_GER,VERSION) values (SEQV_ATTRIBUTES.nextval,'REVIEW_QUESTIONNAIRES','OPL_RESPONSIBLE','Responsible','Verantwortlicher',1);
Insert into T_MESSAGES (MESSAGE_ID,GROUP_NAME,NAME,MESSAGE_TEXT,MESSAGE_TEXT_GER,VERSION) values (SEQV_ATTRIBUTES.nextval,'REVIEW_QUESTIONNAIRES','OPL_DATE','Date','Termin',1);
Insert into T_MESSAGES (MESSAGE_ID,GROUP_NAME,NAME,MESSAGE_TEXT,MESSAGE_TEXT_GER,VERSION) values (SEQV_ATTRIBUTES.nextval,'REVIEW_QUESTIONNAIRES','OPL_STATUS','Completed','Vollendet',1);


---------------------------------------------------------------
----ICDM-2261
---------------------------------------------------------------
---------------------------------------------------------------
-- TABV_COMMON_PARAMS
---------------------------------------------------------------

--Insert new Value in TABV_COMMON_PARAMS for ICDM v1.26.0

DELETE FROM TABV_COMMON_PARAMS WHERE PARAM_ID = 'iCDM_CLIENT_VERSION';
INSERT INTO TABV_COMMON_PARAMS (PARAM_ID,PARAM_DESC,PARAM_VALUE,VERSION) values ('iCDM_CLIENT_VERSION','iCDM Client''s current version','1.26.0',1);
commit;


---------------------------------------------------------------
----ICDM-2212
---------------------------------------------------------------
---------------------------------------------------------------
-- T_MESSAGES
---------------------------------------------------------------

--Insert new Value in T_MESSAGES for configuring in the Cdfx Export dialog for ICDM v1.26.0

insert into T_MESSAGES(GROUP_NAME,NAME,MESSAGE_TEXT) values ('CDFX_EXPORT_DIALOG','REVIWED_PARAM_STATUS_SCORE','Reviewed Parameter(score 8 and 9) with status from review score');
insert into T_MESSAGES(GROUP_NAME,NAME,MESSAGE_TEXT) values ('CDFX_EXPORT_DIALOG','REVIWED_PARAM_STATUS_100','Reviewed Parameter (score 8 and 9) with status 100%');
insert into T_MESSAGES(GROUP_NAME,NAME,MESSAGE_TEXT) values ('CDFX_EXPORT_DIALOG','ALLPARAM_STATUS_FILE','All parameter for export status from review file');
insert into T_MESSAGES(GROUP_NAME,NAME,MESSAGE_TEXT) values ('CDFX_EXPORT_DIALOG','ALLPARAM_STATUS_SCORE','All parameter for export,status from review score');
commit;


---------------------------------------------------------------------------------------------
spool off
