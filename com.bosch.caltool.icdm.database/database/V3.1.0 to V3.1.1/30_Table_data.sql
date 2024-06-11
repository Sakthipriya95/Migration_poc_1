spool c:\temp\30_Table_data.log

------------------------------------------------------------------------------------------------------------------
--  ALM TaskId : 482459: Add new columns to T_PIDC_A2L TABLE 
--  UPDATE statement for T_PIDC_A2L to set ASSIGNED_DATE, ASSIGNED_USER for existing records in T_PIDC_A2L
------------------------------------------------------------------------------------------------------------------



update T_PIDC_A2L set ASSIGNED_DATE = MODIFIED_DATE , ASSIGNED_USER = MODIFIED_USER where PIDC_VERS_ID is not null and MODIFIED_DATE is not null;
update T_PIDC_A2L set ASSIGNED_DATE = CREATED_DATE , ASSIGNED_USER = CREATED_USER where PIDC_VERS_ID is not null and MODIFIED_DATE is null;

------------------------------------------------------------------------------------------------------------------
--  ALM TaskId : 501189: Remove the wrong variants associated with NO-VARIANT results
--  DELETE statement for T_RVW_VARIANTS to delete review results in NO-VARIANT pidc but has entries in T_RVW_VARIANTS
------------------------------------------------------------------------------------------------------------------
/* select the records */
select * from T_RVW_VARIANTS where result_id in 
(select res.result_id from T_PIDC_A2L pidca2l,T_RVW_RESULTS res 
where pidca2l.pidc_a2l_id=res.pidc_a2l_id and pidca2l.pidc_vers_id in 
(select pidc_vers_id from T_PIDC_VERSION MINUS select pidc_vers_id from TABV_PROJECT_VARIANTS));

/* delete the records */
delete from T_RVW_VARIANTS  where result_id in 
(select res.result_id from T_PIDC_A2L pidca2l,T_RVW_RESULTS res 
where pidca2l.pidc_a2l_id=res.pidc_a2l_id and pidca2l.pidc_vers_id in 
(select pidc_vers_id from T_PIDC_VERSION MINUS select pidc_vers_id from TABV_PROJECT_VARIANTS));

COMMIT;

------------------------------------------------------------------------------------------------------------------
-- ALM TaskId : 503762 Add new value to T_MESSAGES TABLE
------------------------------------------------------------------------------------------------------------------
INSERT INTO T_MESSAGES (GROUP_NAME, NAME, MESSAGE_TEXT) VALUES ('RVW_WORK_PKG_SEL_DIALOG', 'INFO_MSG', 'To define/import work packages and parameter mappings, open the A2L Editor.');
COMMIT;

------------------------------------------------------------------------------------------------------------------
-- ALM TaskId : 504656 Add new value to T_MESSAGES TABLE
------------------------------------------------------------------------------------------------------------------

INSERT INTO T_MESSAGES (GROUP_NAME, NAME, MESSAGE_TEXT, MESSAGE_TEXT_GER) VALUES ('A2L_IMPORT', 'A2L_GROUP_IMPORT_INFO_MSG', 'Import Work Package-Responsibility and mappings from A2L Groups...\na) After import a new active version will be created.\nb) A2L Editor and PIDC Editor should be reopened to see the changes.\n\n Click Yes to Proceed.', 'Import Work Package-Responsibility and mappings from A2L Groups...\na) After import a new active version will be created.\nb) A2L Editor and PIDC Editor should be reopened to see the changes.\n\nClick Yes to Proceed..');

INSERT INTO T_MESSAGES (GROUP_NAME, NAME, MESSAGE_TEXT, MESSAGE_TEXT_GER) VALUES ('A2L_IMPORT', 'A2L_GROUP_IMPORT_SUCCESS_MSG', 'Work Package-Responsibility and mappings from A2L Groups are imported successfully!\na) A new version is created with the imported data and is made as the active version.\nb) User should reopen the A2L editor and PIDC Editor before any further changes.', 'Work Package-Responsibility and mappings from A2L Groups are imported successfully!\na) A new version is created with the imported data and is made as the active version.\nb) User should reopen the A2L editor and PIDC Editor before any further changes.');

COMMIT;

------------------------------------------------------------------------------------------------------------------
-- ALM TaskId : 502102 Add new value to T_MESSAGES TABLE
------------------------------------------------------------------------------------------------------------------
INSERT INTO T_MESSAGES (GROUP_NAME, NAME, MESSAGE_TEXT) VALUES ('A2L', 'NO_WP_DEF_VERSION', 'No Work Package Definition versions are available yet.');
COMMIT;

------------------------------------------------------------------------------------------------------------------
--  ALM TaskId : 506297 : Add new value to TABV_COMMON_PARAMS TABLE 
--  INSERT  for TABV_COMMON_PARAMS  to add new value as RULE_IMPORTER_WIKI_LINK
------------------------------------------------------------------------------------------------------------------
INSERT INTO TABV_COMMON_PARAMS (PARAM_ID,PARAM_DESC,PARAM_VALUE,VERSION) VALUES ('RULE_IMPORTER_WIKI_LINK' , 'Link to Rule Importer in Wiki', 'https://inside-docupedia.bosch.com/confluence/display/ATW/2.3.2+Concept+of+iCDM+Review+Rules#id-2.3.2ConceptofiCDMReviewRules-RuleSetImporter',1);
COMMIT;

------------------------------------------------------------------------------------------------------------------
--  ALM TaskId : 505388 : Add link and hint for compli review report 
------------------------------------------------------------------------------------------------------------------
INSERT INTO TABV_COMMON_PARAMS (PARAM_ID,PARAM_DESC,PARAM_VALUE) VALUES
('COMPLI_REPORT_CODEX_LINK','Link to PS Codex Reporting','https://ps-codex.bosch.com/');


INSERT INTO TABV_COMMON_PARAMS (PARAM_ID,PARAM_DESC,PARAM_VALUE) VALUES
('COMPLI_REPORT_HINT','Hint for C-SSD labels table in compli pdf report','Critical behaviour NOT active (function not active due to other calibration variables).NOT active trigger assessment and release from 2x Experts (Project + CoC)
Critical behaviour Active (Disclosed to authorities OR not disclosed).Active trigger an evaluation via codex case.Release 1x BST + Signatures according to ALM CES Process
In general, all descriptions shall be done in clear text to enable to understand for signature. A link to codex case shall be added as reference when already available.');

COMMIT;


------------------------------------------------------------------------------------------------------------------
-- ALM TaskId : 508104 : Version update script
------------------------------------------------------------------------------------------------------------------      
delete from TABV_COMMON_PARAMS WHERE upper(PARAM_ID) = 'ICDM_CLIENT_VERSION';
Insert into TABV_COMMON_PARAMS (PARAM_ID,PARAM_DESC,PARAM_VALUE,VERSION) values ('ICDM_CLIENT_VERSION','iCDM Client''s current version','3.1.1',1); 
COMMIT;

spool off;