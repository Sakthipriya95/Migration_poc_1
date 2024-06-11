spool c:\temp\03_Table_Data.log

------------------------------------------------------------------------
--270108
--Product version update script
------------------------------------------------------------------------
DELETE FROM TABV_COMMON_PARAMS WHERE PARAM_ID = 'iCDM_CLIENT_VERSION';
INSERT INTO TABV_COMMON_PARAMS (PARAM_ID,PARAM_DESC,PARAM_VALUE,VERSION) 
    values ('iCDM_CLIENT_VERSION','iCDM Client''s current version','2.2.0',1);

-----------------------------------------------------------------------------
--Task 265415
-- migration script to add default risk definition to existing PIDC versions
------------------------------------------------------------------------------
BEGIN
    FOR v_pidc_vers_id IN 
	    (SELECT PIDC_VERS_ID  FROM T_PIDC_VERSION where PIDC_VERS_ID not in (select PIDC_VERS_ID from T_PIDC_RM_DEFINITION )) LOOP
		INSERT INTO T_PIDC_RM_DEFINITION (
		PIDC_RM_ID,   PIDC_VERS_ID,   RM_NAME_ENG,
		RM_NAME_GER,  RM_DESC_ENG,    RM_DESC_GER,
		IS_VARIANT,   CREATED_USER,   MODIFIED_USER, 
        CREATED_DATE, MODIFIED_DATE,  VERSION) 
		VALUES (
		SEQV_ATTRIBUTES.NEXTVAL,  v_pidc_vers_id.pidc_vers_id, 'PIDC',
		'', '', '',
		'N',user,'',
		SYS_EXTRACT_UTC(SYSTIMESTAMP),'',1);
		COMMIT;
	 END LOOP;
END;
/

------------------------------------------------------------------------
--  ALM TaskId : 269620: Include wiki link for Risk Evaluation page
--  Insert query for TABV_COMMON_PARAMS
------------------------------------------------------------------------

INSERT INTO TABV_COMMON_PARAMS (PARAM_ID,PARAM_DESC,PARAM_VALUE,VERSION) 
VALUES ('RISK_EVALUATION_WIKI_LINK','Link to Risk Evalution wiki page','https://inside-docupedia.bosch.com/confluence/display/dsapplication/iCDM+-+intelligent+Calibration+Data+Management',1);

----------------------------------------------------------------------------
--  ALM TaskId : 271214: German text for columns headers of risk evaluation
--  Insert query for  T_MESSAGES
----------------------------------------------------------------------------

INSERT INTO T_MESSAGES (GROUP_NAME,NAME,MESSAGE_TEXT,MESSAGE_TEXT_GER,VERSION) 
VALUES ('RISK_EVALUATION','TITLE_TEXT','The Risk Evaluation should only be used by some PS-EC at the moment.','The Risk Evaluation should only be used by some PS-EC at the moment.',1);

INSERT INTO T_MESSAGES (GROUP_NAME,NAME,MESSAGE_TEXT,MESSAGE_TEXT_GER,VERSION) 
VALUES ('RISK_EVALUATION','COLUMN_IS_RELEVANT','Is Relevant','Is Relevant',1);

INSERT INTO T_MESSAGES (GROUP_NAME,NAME,MESSAGE_TEXT,MESSAGE_TEXT_GER,VERSION) 
VALUES ('RISK_EVALUATION','COLUMN_PROJECT_CHAR','Project Characteristics','Project Characteristics',1);

INSERT INTO T_MESSAGES (GROUP_NAME,NAME,MESSAGE_TEXT,MESSAGE_TEXT_GER,VERSION) 
VALUES ('RISK_EVALUATION','COLUMN_RB_SW_SHARE','RB SW\n Share','RB SW\n Share',1);

INSERT INTO T_MESSAGES (GROUP_NAME,NAME,MESSAGE_TEXT,MESSAGE_TEXT_GER,VERSION) 
VALUES ('RISK_EVALUATION','COLUMN_RB_INPUT_DATA','RB Input\n Data','RB Input\n Data',1);


---------------------------------------------------------------------
--  Insert query for T_RM_CATEGORY
---------------------------------------------------------------------  
INSERT INTO T_RM_CATEGORY (CATEGORY_ENG,CATEGORY_GER,VERSION,CAT_TYPE) VALUES ('Monetary Risk pre-SOP','Risiko Monetär Pre-SOP',1,'N');
INSERT INTO T_RM_CATEGORY (CATEGORY_ENG,CATEGORY_GER,VERSION,CAT_TYPE) VALUES ('Monetary Risk post-SOP','Risiko Monetär Post-SOP',1,'N');
INSERT INTO T_RM_CATEGORY (CATEGORY_ENG,CATEGORY_GER,VERSION,CAT_TYPE) VALUES ('Legal Risk','Risiko Gesetz',1,'I');
INSERT INTO T_RM_CATEGORY (CATEGORY_ENG,CATEGORY_GER,VERSION,CAT_TYPE) VALUES ('Reputational Risk','Risiko Reputation',1,'I');
INSERT INTO T_RM_CATEGORY (CATEGORY_ENG,CATEGORY_GER,VERSION,CAT_TYPE) VALUES ('Safety Risk','Risiko Safety',1,'I');
INSERT INTO T_RM_CATEGORY (CATEGORY_ENG,CATEGORY_GER,VERSION,CAT_TYPE) VALUES ('100% RB-SW','100% RB-SW',1,'S');
INSERT INTO T_RM_CATEGORY (CATEGORY_ENG,CATEGORY_GER,VERSION,CAT_TYPE) VALUES ('x% RB-SW','x% RB-SW',1,'S');
INSERT INTO T_RM_CATEGORY (CATEGORY_ENG,CATEGORY_GER,VERSION,CAT_TYPE) VALUES ('0% RB-SW','0% RB-SW',1,'S');
INSERT INTO T_RM_CATEGORY (CATEGORY_ENG,CATEGORY_GER,VERSION,CAT_TYPE) VALUES ('Initial Data by RB','Eingangsdaten von RB',1,'D');
INSERT INTO T_RM_CATEGORY (CATEGORY_ENG,CATEGORY_GER,VERSION,CAT_TYPE) VALUES ('Initial Data not by RB','Eingangsdaten nicht von RB',1,'D');


COMMIT;

spool off