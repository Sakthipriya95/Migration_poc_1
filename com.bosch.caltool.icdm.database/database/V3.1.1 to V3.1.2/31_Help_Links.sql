spool c:\temp\31_Help_Links.log

------------------------------------------------------------------------------------------------------------------
--  ALM TaskId : 511149: Delete duplicate links from TABV_COMMON_PARAMS TABLE 
--  INSERT statement for T_LINKS from Duplicate links in TABV_COMMON_PARAMS
------------------------------------------------------------------------------------------------------------------
--EMR page in pidc editor
INSERT INTO T_LINKS(node_id,node_type,desc_eng,link_url) values(-100,'HELP_EDITOR_PIDCEditor_EMRNatPage','Link to EMR-Sheet in Wiki',(SELECT PARAM_VALUE FROM TABV_COMMON_PARAMS WHERE PARAM_ID='EMR_SHEET_WIKI_LINK'));
--Focus matrix page in pidc editor
INSERT INTO T_LINKS(node_id,node_type,desc_eng,link_url) values(-100,'HELP_EDITOR_PIDCEditor_FocusMatrixPage','Link to Focus Matrix for Cal-Projects in Wiki',(SELECT PARAM_VALUE FROM TABV_COMMON_PARAMS WHERE PARAM_ID='FOCUS_MATRIX_ASPICE_LINK'));
-- Rule import dialog
INSERT INTO T_LINKS(node_id,node_type,desc_eng,link_url) values(-100,'HELP_WIZARD_CalDataFileImpWizardDialog','Link to Rule Importer in Wiki',(SELECT PARAM_VALUE FROM TABV_COMMON_PARAMS WHERE PARAM_ID='RULE_IMPORTER_WIKI_LINK'));
-- Risk evaluation page
INSERT INTO T_LINKS(node_id,node_type,desc_eng,link_url) values(-100,'HELP_EDITOR_PIDCEditor_RiskEvaluationPage','Link to Risk Evaluation in Wiki',(SELECT PARAM_VALUE FROM TABV_COMMON_PARAMS WHERE PARAM_ID='RISK_EVALUATION_WIKI_LINK'));
-- Cal data Analyser
INSERT INTO T_LINKS(node_id,node_type,desc_eng,link_url) values(-100,'HELP_EDITOR_CaldataAnalyzerEditor_CaldataAnalyzerPage','Help link for Cal data analyser',(SELECT PARAM_VALUE FROM TABV_COMMON_PARAMS WHERE PARAM_ID='CALDATA_ANALYZER_HELP_LINK'));
-- delete duplicate entries in TABV_COMMON_PARAMS
DELETE FROM TABV_COMMON_PARAMS where PARAM_ID in ('EMR_SHEET_WIKI_LINK','FOCUS_MATRIX_ASPICE_LINK','CALDATA_ANALYZER_HELP_LINK','RULE_IMPORTER_WIKI_LINK','RISK_EVALUATION_WIKI_LINK');
------------------------------------------------------------------------------------------------------------------
--  ALM TaskId : 506963: Add new columns to T_LINKS TABLE 
--  INSERT statement for T_LINKS for pidc editor attributes page help link
------------------------------------------------------------------------------------------------------------------
insert into T_LINKS(node_id,node_type,desc_eng,link_url) values(-100,'HELP_PIDCEditor_PIDCAttrPage','Help link for Pidc editor Attributes page','https://inside-docupedia.bosch.com/confluence/display/ATW/2.1+iCDM+Project+ID+Card+Navigator#id-2.1iCDMProjectIDCardNavigator-AtaGlance:PIDCeditor');

------------------------------------------------------------------------------------------------------------------
--  ALM TaskId : 508674: Add new columns to T_LINKS TABLE 
--  INSERT statement for T_LINKS for pidc scout editor help link
------------------------------------------------------------------------------------------------------------------
insert into T_LINKS(node_id,node_type,desc_eng,link_url) values(-100,'HELP_PIDCSearchEditor_PIDCSearchPage','Help link for Pidc Scout editor','https://inside-docupedia.bosch.com/confluence/display/ATW/2.2+iCDM+Data+Acquisition+and+PreCalibration+Features#id-2.2iCDMDataAcquisitionandPreCalibrationFeatures-PIDCScout');

------------------------------------------------------------------------------------------------------------------
--  ALM TaskId : 508647: Add new columns to T_LINKS TABLE 
--  INSERT statement for T_LINKS for series statistics view  help link
------------------------------------------------------------------------------------------------------------------
insert into T_LINKS(node_id,node_type,desc_eng,link_url) values(-100,'HELP_View_SeriesStatisticsViewPart','Help link for Series Statistics view','https://inside-docupedia.bosch.com/confluence/display/ATW/2.2+iCDM+Data+Acquisition+and+PreCalibration+Features#id-2.2iCDMDataAcquisitionandPreCalibrationFeatures-ShowingSeriesStatistics');

------------------------------------------------------------------------------------------------------------------
--  ALM TaskId : 508647: Add new columns to T_LINKS TABLE 
--  INSERT statement for T_LINKS for scratch pad view  help link
------------------------------------------------------------------------------------------------------------------
insert into T_LINKS(node_id,node_type,desc_eng,link_url) values(-100,'HELP_VIEW_ScratchPadViewPart','Help link for Scratch Pad view','https://inside-docupedia.bosch.com/confluence/display/ATW/2.2+iCDM+Data+Acquisition+and+PreCalibration+Features#id-2.2iCDMDataAcquisitionandPreCalibrationFeatures-ScratchPad');
------------------------------------------------------------------------------------------------------------------
--  ALM TaskId : 508647: Add new columns to T_LINKS TABLE 
--  INSERT statement for T_LINKS for CDR Wizard
------------------------------------------------------------------------------------------------------------------
insert into T_LINKS(node_id,node_type,desc_eng,link_url) values(-100,'HELP_WIZARD_CalDataReviewWizardDialog','Help link for Calibration Data Review','https://inside-docupedia.bosch.com/confluence/display/ATW/2.3+iCDM+Calibration+Data+Review#id-2.3iCDMCalibrationDataReview-CalibrationDataReview(CDR)');

------------------------------------------------------------------------------------------------------------------
--  ALM TaskId : 511149: Add new columns to T_LINKS TABLE 
--  INSERT statement for T_LINKS for Emr page, focus matirx page, rule importer
------------------------------------------------------------------------------------------------------------------

COMMIT;

spool off;