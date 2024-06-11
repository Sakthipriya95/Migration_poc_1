spool c:\temp\30_Table_Data.log

------------------------------------------------------------------------------------------------------------------
--Task 737496 : Impl : Complex Rule Validation using SSD API in RuleSet Editor
--SSD API Url to validate the complex rule in Ruleset Editor
------------------------------------------------------------------------------------------------------------------
Insert into TABV_COMMON_PARAMS (PARAM_ID,PARAM_DESC,PARAM_VALUE,VERSION) values ('COMPLEX_RULE_VALIDATION_URL','SSD Api url to validate complex rule in Ruleset editor','https://si-cdm01.de.bosch.com:8643/ssdapiservice/rulevalidationservice',1);

COMMIT;
------------------------------------------------------------------------------------------------------------------
--Task 749550: Impl: Azure SSO in iCDM Analysis and implementation Part - 1
------------------------------------------------------------------------------------------------------------------
insert into tabv_common_params(param_id,param_desc,param_value) values('ICDM_AZURE_AUTH_URI','Authorization URL for iCDM Azure AD','https://login.microsoftonline.com/0ae51e19-07c8-4e4b-bb6d-648ee58410f4/oauth2/v2.0');
insert into tabv_common_params(param_id,param_desc,param_value) values('ICDM_AZURE_AUTH_CLIENT_ID','Client Id of iCDM app Registration in Azure','139419d7-b644-4230-978f-68c4af5c7edc');
insert into tabv_common_params(param_id,param_desc,param_value) values('ICDM_AZURE_SCOPE','Scopes to be used in Azure AD','openid+offline_access');
insert into tabv_common_params(param_id,param_desc,param_value) values('ICDM_AZURE_MAX_RETRY','Max no. of retry attempts to fetch the azure token','20');
insert into tabv_common_params(param_id,param_desc,param_value) values('ICDM_AZURE_RETRY_INTERVAL','Retry interval for checking if token is available in milli seconds','2000');

commit;


------------------------------------------------------------------------------------------------------------------
--Task 763475: Impl : <HIDDEN> attributes values should be displayed in OpenAPI
------------------------------------------------------------------------------------------------------------------
INSERT into TABV_APIC_USERS (USER_ID,USERNAME,LASTNAME,DEPARTMENT,Created_User, Created_Date, version) values (SEQV_ATTRIBUTES.NEXTVAL,'ICDM_API_USER','iCDM API User','PS-EC/EBT3',user, sysdate, 1);

INSERT INTO TABV_APIC_ACCESS_RIGHTS (MODULE_NAME,ACCESS_RIGHT,USER_ID,CREATED_USER,CREATED_DATE,VERSION) SELECT 'APIC','APIC_WRITE',USER_ID,user, sysdate, 1 from TABV_APIC_USERS where USERNAME = 'ICDM_API_USER';

commit;


------------------------------------------------------------------------------------------------------------------
--Task 763791: Impl: vCDM Service Lib Component Upgrade & Testing - Update vCDMStudio4vCDM64 exe version in icdm DB
------------------------------------------------------------------------------------------------------------------
update TABV_COMMON_PARAMS
    set PARAM_VALUE = 'Vector vCDM 21.1 SP2\vCDMstudio\Exec64\vCDMStudio4vCDM64.exe'
    where PARAM_ID = 'VCDMSTUDIO_EXE_REL_PATH';

COMMIT;

 ------------------------------------------------------------------------------------------------------------------
--Version update to 2023.10.0 script 
------------------------------------------------------------------------------------------------------------------
UPDATE TABV_COMMON_PARAMS SET PARAM_VALUE = '2023.10.0' WHERE PARAM_ID='ICDM_CLIENT_VERSION';
COMMIT;

spool off