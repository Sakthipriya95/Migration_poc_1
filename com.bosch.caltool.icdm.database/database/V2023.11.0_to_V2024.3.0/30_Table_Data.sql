spool c:\temp\30_Table_Data.log


 ------------------------------------------------------------------------------------------------------------------
-- Story - 772984 DRT: Handling of disabled labels
------------------------------------------------------------------------------------------------------------------
INSERT into T_RVW_COMMENT_TEMPLATES 
    (COMMENT_DESC) 
    values ('Functionality disabled by parameter value, verified');
INSERT into T_RVW_COMMENT_TEMPLATES 
    (COMMENT_DESC) 
    values ('Label not relevant for current release, still to be calibrated');
COMMIT;


------------------------------------------------------------------------------------------------------------------
--Task 782851: Impl: Optimize Active directory Group users sync Job in iCDM Web services
------------------------------------------------------------------------------------------------------------------
-- ICDM_AD_GRP_MAIL_ENABLED value should be 'Y' only for production
-- ICDM_AD_GRP_MAIL_ENABLED value should be 'N' for beta and dev
insert into tabv_common_params(param_id,param_desc,param_value) 
                values('ICDM_AD_GRP_MAIL_ENABLED','Flag to enable/disable Mail notifications for Invalid AD Groups','Y');
insert into tabv_common_params(param_id,param_desc,param_value) 
                values('ICDM_INVALID_AD_GRP_MAIL_TO','Mail Id to notify for Invalid AD Groups','Michael.Heinrich2@de.bosch.com');

commit;

------------------------------------------------------------------------------------------------------------------
--Task 781455: impl : Archive a WP when marked as finished
------------------------------------------------------------------------------------------------------------------
insert into tabv_common_params(param_id,param_desc,param_value) 
                values('WP_ARCHIVAL_THREAD_CNT','Thread count for WP Archival Multi-Threading','4');
commit;

------------------------------------------------------------------------------------------------------------------
--Task 787233: Fix : Questionnaires are getting added to the Default WP at the A2L structure Questionnaire node.
------------------------------------------------------------------------------------------------------------------
insert into t_messages (group_name,name,message_text) values ('REVIEW_QUESTIONNAIRES','NO_DEFAULT_WP_LINK','Questionnaires cannot be linked to Default Work Package');
commit;

------------------------------------------------------------------------------------------------------------------
--Task 787208: impl : impl :Store data assessment zip file in SharePoint
--Important! : The attribute id is passed as param value in the below query
--The Attribute to hold Share point Archival Urls to be created in Beta and Pro iCDM
--The Attribute Id to be taken from TABV_ATTRIBUTES table after creation
------------------------------------------------------------------------------------------------------------------
insert into tabv_common_params(param_id,param_desc,param_value) 
                values('SHAREPOINT_ARCHIVAL_URL_ATTR','Attribute Id of Share Point Archival Url','40967437028');
commit;

-- Updating the Display messages for Data Assessment with correct Casing

update t_messages 
    set message_text = 'Creation of Baseline files failed and hence the file could not be downloaded or uploaded.'
    where group_name = 'DATA_ASSESSMENT' and
    name = 'FILE_STATUS_FAILED';
    
commit;

update t_messages 
    set message_text = 'Creation of Baseline files is still in progress. Please try again after some time.'
    where group_name = 'DATA_ASSESSMENT' and
    name = 'FILE_STATUS_IN_PROGRESS';
    
commit;

update t_messages 
    set message_text = 'Baseline files do not exist for this Data Assessment Baseline.'
    where group_name = 'DATA_ASSESSMENT' and
    name = 'FILE_STATUS_NOT_AVAILABLE';
    
commit;


 ------------------------------------------------------------------------------------------------------------------
--Version update to 2024.3.0 script 
------------------------------------------------------------------------------------------------------------------
UPDATE TABV_COMMON_PARAMS SET PARAM_VALUE = '2024.3.0' WHERE PARAM_ID='ICDM_CLIENT_VERSION';
COMMIT;


spool off