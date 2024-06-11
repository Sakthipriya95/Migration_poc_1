spool c:\temp\03_1_Table_data_Common_Param.log

-----------------------------------------------------------------------------------------------
--  ALM TaskId : 335475: Add new common parameter to show file menu option based on the node access rights 
--  Insert query for TABV_COMMON_PARAMS
-----------------------------------------------------------------------------------------------

INSERT INTO TABV_COMMON_PARAMS (PARAM_ID,PARAM_DESC,PARAM_VALUE,VERSION) 
VALUES ('RISK_EVALUATION_ENABLED','Show or hide risk evaluation for PICDs without filled evaluation sheets','N',1);
COMMIT;
-----------------------------------------------------------------------------------------------
--  ALM TaskId : 334977: Add new common parameter to control displaying RiskEvaluation page 
--  Insert query for TABV_COMMON_PARAMS
-----------------------------------------------------------------------------------------------

INSERT INTO TABV_COMMON_PARAMS (PARAM_ID,PARAM_DESC,PARAM_VALUE) VALUES('CALDATAANALYZER_NODE_ID','A user needs special access rights to start CalDataAnalayzer in ICDM','-100');
COMMIT;

---------------------------------------------------------------------
--  ALM Story : 307426
--  Unique attributes for web flow job for multiple variants
--------------------------------------------------------------------- 
INSERT INTO TABV_COMMON_PARAMS
(PARAM_ID, PARAM_DESC, PARAM_VALUE,VERSION )
VALUES
('WEB_FLOW_UNIQUE_ATTR', 'Attribute Ids which needs to be validated in webflow job for multiple variants', '48,36,197,758860766', 1 );

---------------------------------------------------------------------
--  ALM Story : 307426
--  Flag to set whether web flow for multiple variants is active or not
--------------------------------------------------------------------- 
INSERT INTO TABV_COMMON_PARAMS
(PARAM_ID, PARAM_DESC, PARAM_VALUE,VERSION )
VALUES
('WEB_FLOW_JOB_MUL_VAR_ACTIVE', 'Flag to display Start webFLOW Job option for multiple variants', 'N', 1 );
COMMIT;

---------------------------------------------------------------------
--  ALM Task : 343531 - Add CDA Disclaimer dialog contents
---------------------------------------------------------------------
INSERT INTO TABV_COMMON_PARAMS (PARAM_ID,PARAM_DESC,PARAM_VALUE,VERSION) 
    SELECT 'CDA_DISCLAIMER_FILE_ID','File id for CDA disclaimer ',FILE_ID,1 FROM TABV_ICDM_FILES WHERE NODE_ID = -8;

-----------------------------------------------------------------------------------------------
--  ALM TaskId : 343874: Add new common parameter to add help link for caldata anayzer  
--  Insert query for TABV_COMMON_PARAMS
-----------------------------------------------------------------------------------------------

INSERT INTO TABV_COMMON_PARAMS (PARAM_ID,PARAM_DESC,PARAM_VALUE) VALUES('CALDATA_ANALYZER_HELP_LINK','Link to Caldata Analyzer Help','https://inside-docupedia.bosch.com/confluence/display/ATW/iCDM+-+intelligent+Calibration+Data+Management');
COMMIT;

------------------------
----Release version change
------------------------

delete from TABV_COMMON_PARAMS WHERE PARAM_ID = 'iCDM_CLIENT_VERSION';
Insert into TABV_COMMON_PARAMS (PARAM_ID,PARAM_DESC,PARAM_VALUE,VERSION) values ('iCDM_CLIENT_VERSION','iCDM Client''s current version','2.4.0',1); 
COMMIT;

spool off