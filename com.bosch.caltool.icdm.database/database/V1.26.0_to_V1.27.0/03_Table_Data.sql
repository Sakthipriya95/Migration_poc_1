spool c:\temp\03_Table_Data.log
---------------------------------------------------------------
----ICDM-2310
---------------------------------------------------------------
---------------------------------------------------------------
-- T_MESSGAES
---------------------------------------------------------------

insert into T_MESSAGES(GROUP_NAME,NAME,MESSAGE_TEXT,MESSAGE_TEXT_GER)  values ('CDR_RULE','READY_FOR_SERIES'
																					,'values matching the rule\nare ready for series'
																					,'Werte, die der Regel entsprechen\nsind serienreif'
																				);

Insert into T_MESSAGES (GROUP_NAME,NAME,MESSAGE_TEXT,MESSAGE_TEXT_GER,VERSION) 
	values ('CDFX_EXPORT_DIALOG','ALLPARAM_STATUS_FILE'
			,'All parameter with status from reviewed file'
			,'Alle Parameter mit Status gem. CDFx Status im Review File'
			,'1');
Insert into T_MESSAGES (GROUP_NAME,NAME,MESSAGE_TEXT,MESSAGE_TEXT_GER,VERSION) 
	values ('CDFX_EXPORT_DIALOG','ALLPARAM_STATUS_SCORE'
			,'All parameter with status from review score'
			,'Alle Parameter mit Status gem. dem Review-Score'
			,'1');
Insert into T_MESSAGES (GROUP_NAME,NAME,MESSAGE_TEXT,MESSAGE_TEXT_GER,VERSION) 
	values ('CDFX_EXPORT_DIALOG','REVIWED_PARAM_STATUS_100'
			,'Reviewed Parameter (score 8 and 9) with status 100%'
			,'Reviewed Parameter(Score 8 und 9) mit Status 100%'
			,'1');
Insert into T_MESSAGES (GROUP_NAME,NAME,MESSAGE_TEXT,MESSAGE_TEXT_GER,VERSION) 
	values ('CDFX_EXPORT_DIALOG','REVIWED_PARAM_STATUS_SCORE'
			,'Reviewed Parameter(score 8 and 9) with status from review score'
			,'Reviewed Parameter(Score 8 und 9) mit Status gem. dem Review-Score'
			,'1');

commit;

---------------------------------------------------------------
----ICDM-2376
---------------------------------------------------------------
insert into T_WORKPACKAGE (WP_NAME_E,WP_GROUP) 
(
    select wp_name_e, min(wp_group)
    from t_fc2wp 
    where fc2wp_type = 'FC2WP2'
    group by wp_name_e
);

commit;

---------------------------------------------------------
-- ICDM-2155
-- Create General Questionnaire
----------------------------------------------------------

DECLARE
  v_genqnaire_id NUMBER;

BEGIN
  --Generate Questionnaire ID
  SELECT seqv_attributes.nextval INTO v_genqnaire_id FROM dual;
  
  --INSERTING into TABV_COMMON_PARAMS
  INSERT INTO TABV_COMMON_PARAMS 
      ( PARAM_ID, PARAM_DESC, PARAM_VALUE, VERSION )
    VALUES
      ( 'GENERAL_QNAIRE_ID', 'General Questionnaire ID', TO_CHAR(v_genqnaire_id), 1);
    
  -- INSERTING into T_QUESTIONNAIRE
  Insert into T_QUESTIONNAIRE 
        (QNAIRE_ID, NAME_ENG, DESC_ENG, CREATED_USER, CREATED_DATE, DELETED_FLAG, VERSION) 
      values 
        (v_genqnaire_id, 'General Questions', 'General Questions', USER, sys_extract_utc(systimestamp), 'N', 1);

    
  -- INSERTING into T_QUESTIONNAIRE_VERSION
  Insert into T_QUESTIONNAIRE_VERSION 
        (
         QNAIRE_VERS_ID, QNAIRE_ID,
         ACTIVE_FLAG, INWORK_FLAG,
         RESULT_RELEVANT_FLAG, RESULT_HIDDEN_FLAG,
         MEASUREMENT_RELEVANT_FLAG, MEASUREMENT_HIDDEN_FLAG,
         SERIES_RELEVANT_FLAG, SERIES_HIDDEN_FLAG,
         LINK_RELEVANT_FLAG, LINK_HIDDEN_FLAG,
         OPEN_POINTS_RELEVANT_FLAG, OPEN_POINTS_HIDDEN_FLAG,
         REMARK_RELEVANT_FLAG, REMARKS_HIDDEN_FLAG,
         MAJOR_VERSION_NUM, MINOR_VERSION_NUM,
         DESC_ENG, DESC_GER,
         CREATED_USER, CREATED_DATE, VERSION,
         MEASURE_RELAVENT_FLAG, MEASURE_HIDDEN_FLAG,
         RESPONSIBLE_RELAVENT_FLAG, RESPONSIBLE_HIDDEN_FLAG,
         COMPLETION_DATE_RELAVENT_FLAG, COMPLETION_DATE_HIDDEN_FLAG
        ) 
      values 
        (
         seqv_attributes.nextval, v_genqnaire_id,
         null, 'Y',
         'Y', 'N',
         'Y', 'N',
         'Y', 'N',
         'Y', 'N',
         'Y', 'N',
         'Y', 'N',
         0, null,
         'Working Set', null,
         USER, sys_extract_utc(systimestamp), 1,
         'Y', 'N',
         'Y', 'N',
         'Y', 'N'
        );

  COMMIT;
  
END;
/

---------------------------------------------------------
-- ICDM-2438
-- Complaince node ID in SSD Database
----------------------------------------------------------

Insert into TABV_COMMON_PARAMS (PARAM_ID,PARAM_DESC,PARAM_VALUE,VERSION) 
    values ('SSD_COMPLI_PARAM_NODE_ID','SSD Node id for compliance parameters','3531996773',1);

commit;

---------------------------------------------------------
-- ICDM-2361
-- Create division attribute , values of attribute and insert the same in TABV_COMMON_PARAMS table
-- Commented, because we use an already existing attribute in the PRO database. The entry in the TABV_COMMON_PARAMS
-- table have been done already.
----------------------------------------------------------

/*DECLARE
  v_div_attr_id NUMBER;
  v_div_attr_grp_id NUMBER;
  v_attr_val_id1 NUMBER;
  v_attr_val_id2 NUMBER;
  v_attr_val_id3 NUMBER;

BEGIN
  --Generate attribute ID
  SELECT seqv_attributes.nextval INTO v_div_attr_id FROM dual;
  SELECT GROUP_ID INTO v_div_attr_grp_id FROM TABV_ATTR_GROUPS where GROUP_NAME_ENG = 'Project Information';
  
   --Generate attribute value ID
  SELECT seqv_attributes.nextval INTO v_attr_val_id1 FROM dual;
  SELECT seqv_attributes.nextval INTO v_attr_val_id2 FROM dual;
  SELECT seqv_attributes.nextval INTO v_attr_val_id3 FROM dual;
  
  
  --INSERTING into TABV_COMMON_PARAMS
  INSERT INTO TABV_COMMON_PARAMS 
      ( PARAM_ID, PARAM_DESC, PARAM_VALUE, VERSION )
    VALUES
      ( 'PIDC_DIVISION', 'Division attribute for PIDC', TO_CHAR(v_div_attr_id), 1);
      
  INSERT INTO TABV_COMMON_PARAMS 
      ( PARAM_ID, PARAM_DESC, PARAM_VALUE, VERSION )
    VALUES
      (   'DIVISIONS_WITH_QNAIRES'
        , 'Attribute Values of divisions for which questionnaries are applicable(comma separated)'
        , TO_CHAR(v_attr_val_id1) || ',' || TO_CHAR(v_attr_val_id2) || ',' ||TO_CHAR(v_attr_val_id3)
        , 1);
    
  -- INSERTING into TABV_ATTRIBUTES(Division attribute)
  Insert into TABV_ATTRIBUTES 
    (ATTR_ID,ATTR_NAME_ENG,ATTR_DESC_ENG,GROUP_ID,VALUE_TYPE_ID,NORMALIZED_FLAG,DELETED_FLAG,MANDATORY,UNITS,PART_NUMBER_FLAG,SPEC_LINK_FLAG,ATTR_SECURITY,VALUE_SECURITY,EADM_NAME)
    values (v_div_attr_id,'Division','Division for workpackage questionnaire',v_div_attr_grp_id,1,'Y','N','Y','-','N','N','E','E','Division');


  -- INSERTING into TABV_ATTR_VALUES(various divisions)
  Insert into TABV_ATTR_VALUES (VALUE_ID,ATTR_ID,VALUE_DESC_ENG,TEXTVALUE_ENG,DELETED_FLAG,CLEARING_STATUS) 
    values (v_attr_val_id1,v_div_attr_id,'BEG','BEG','N','Y');
  Insert into TABV_ATTR_VALUES (VALUE_ID,ATTR_ID,VALUE_DESC_ENG,TEXTVALUE_ENG,DELETED_FLAG,CLEARING_STATUS) 
    values (v_attr_val_id2,v_div_attr_id,'DGS','DGS','N','Y');
  Insert into TABV_ATTR_VALUES (VALUE_ID,ATTR_ID,VALUE_DESC_ENG,TEXTVALUE_ENG,DELETED_FLAG,CLEARING_STATUS) 
    values (v_attr_val_id3,v_div_attr_id,'DS','DS','N','Y');

  COMMIT;
  
END;
/
  
*/

---------------------------------------------------------------
--ICDM-2445
--Insert new Value in TABV_COMMON_PARAMS for ICDM v1.27.0
---------------------------------------------------------------


DELETE FROM TABV_COMMON_PARAMS WHERE PARAM_ID = 'iCDM_CLIENT_VERSION';
INSERT INTO TABV_COMMON_PARAMS (PARAM_ID,PARAM_DESC,PARAM_VALUE,VERSION) 
    values ('iCDM_CLIENT_VERSION','iCDM Client''s current version','1.27.0',1);
    
commit;


spool off
