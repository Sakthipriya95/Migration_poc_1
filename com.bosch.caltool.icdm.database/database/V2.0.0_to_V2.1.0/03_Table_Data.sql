spool c:\temp\03_Table_Data.log


---------------------------------------------------------------
--246681
--Product version update script
---------------------------------------------------------------
DELETE FROM TABV_COMMON_PARAMS WHERE PARAM_ID = 'iCDM_CLIENT_VERSION';
INSERT INTO TABV_COMMON_PARAMS (PARAM_ID,PARAM_DESC,PARAM_VALUE,VERSION) 
    values ('iCDM_CLIENT_VERSION','iCDM Client''s current version','2.1.0',1);

-----------------------------------------------------------------------------------------------
--Task 242053 - STORE THE TIME INTERVAL IN TABV_COMMON_PARAMS
-----------------------------------------------------------------------------------------------
INSERT INTO TABV_COMMON_PARAMS(PARAM_ID,PARAM_DESC,PARAM_VALUE) VALUES('PIDC_UP_TO_DATE_INTERVAL','The time interval in days for a pidc version to be confirmed as up to date','10');

-----------------------------------------------------------------------------------------------
--Task 242055 - Share last conformation date via PIDC Web services
-----------------------------------------------------------------------------------------------
insert into T_MESSAGES(GROUP_NAME,NAME,MESSAGE_TEXT) values ('PIDC_EDITOR','CONFIRMATION_ATTR_TEXT_ENG','Last Confirmation Date');
insert into T_MESSAGES(GROUP_NAME,NAME,MESSAGE_TEXT) values ('PIDC_EDITOR','CONFIRMATION_ATTR_TEXT_GER','Last Confirmation Date');
insert into T_MESSAGES(GROUP_NAME,NAME,MESSAGE_TEXT) values ('PIDC_EDITOR','CONFIRMATION_ATTR_DESC_ENG','Last Confirmation Date on which the owner has confirmed that the pidc version is up to date');
insert into T_MESSAGES(GROUP_NAME,NAME,MESSAGE_TEXT) values ('PIDC_EDITOR','CONFIRMATION_ATTR_DESC_GER','Last Confirmation Date on which the owner has confirmed that the pidc version is up to date');

--Group : Project Information
INSERT INTO TABV_COMMON_PARAMS(PARAM_ID,PARAM_DESC,PARAM_VALUE) VALUES('PIDC_UP_TO_DATE_ATTR_GROUP_ID','Group id for the dummy attribute that will be created for showing up to date information in Web service','32');


-----------------------------------------------------------------------------------------------
--Task 244427 - STORE THE TIME INTERVAL IN TABV_COMMON_PARAMS
-----------------------------------------------------------------------------------------------
INSERT INTO TABV_COMMON_PARAMS(PARAM_ID,PARAM_DESC,PARAM_VALUE) VALUES('DISCLAIMER_VALID_INTERVAL','Validity period in days of acceptance of disclaimer by a user','10');

Insert into TABV_ICDM_FILES (FILE_ID,NODE_ID,NODE_TYPE,FILE_NAME,CREATED_USER,CREATED_DATE,VERSION,FILE_COUNT) 
    values (SEQV_ATTRIBUTES.NEXTVAL,-5,'TEMPLATES','Disclaimer.html',user,sysdate,1,1);

Insert into TABV_ICDM_FILE_DATA (FILE_DATA_ID,FILE_ID,FILE_DATA,VERSION) 
    SELECT SEQV_ATTRIBUTES.NEXTVAL,FILE_ID,EMPTY_BLOB(),1 FROM TABV_ICDM_FILES WHERE NODE_ID = -5;


Insert into TABV_COMMON_PARAMS (PARAM_ID,PARAM_DESC,PARAM_VALUE,VERSION) 
    SELECT 'DISCLAIMER_FILE_ID','File id for iCDM disclaimer ',FILE_ID,1 FROM TABV_ICDM_FILES WHERE NODE_ID = -5;

commit;

----------------------------------------------------------------
--Task 243409  -  New Attribute for Project node in SSD.
----------------------------------------------------------------

declare
    v_attr_id number;
    v_attr_grp_id number;
begin
    select seqv_attributes.nextval into v_attr_id from dual;   
    SELECT GROUP_ID INTO v_attr_grp_id FROM TABV_ATTR_GROUPS where GROUP_NAME_ENG = 'Project Information';
    
 INSERT INTO TABV_ATTRIBUTES(
   ATTR_ID, ATTR_NAME_ENG, ATTR_NAME_GER, 
   ATTR_DESC_ENG, ATTR_DESC_GER, GROUP_ID, 
   VALUE_TYPE_ID, CREATED_DATE, MODIFIED_DATE, 
   CREATED_USER, MODIFIED_USER, NORMALIZED_FLAG, 
   DELETED_FLAG, ATTR_LEVEL, MANDATORY, 
   UNITS, FORMAT,  
   PART_NUMBER_FLAG, SPEC_LINK_FLAG, ATTR_SECURITY, 
   VALUE_SECURITY, CHAR_ID, CHANGE_COMMENT, 
   EADM_NAME, GROUP_ATTR_FLAG, MOVE_DOWN_YN) 
 VALUES (v_attr_id,
  'SW-Project-Node','Software-Projektknoten', 'Software Project node in SSD','Software-Projektknoten in SSD',
  v_attr_grp_id,1,sys_extract_utc(systimestamp),null, USER, null, 'N', 'N',null, 'N', '-', null,  'N','N','E','E', null,' ', 'SW-Project',null, 'N');
    
 INSERT INTO TABV_COMMON_PARAMS (PARAM_ID,PARAM_DESC,PARAM_VALUE,VERSION) 
    values ('SSD_PROJ_NODE_ATTR_ID','Attribute to map SSD Software Project in PIDC', TO_CHAR(v_attr_id),1);
 
 commit;
 
end;
/


commit;

spool off