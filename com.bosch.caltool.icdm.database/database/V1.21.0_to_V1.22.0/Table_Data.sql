---------------------------------------------------------------

--  2015-10-19
---------------------------------------------------------------

---------------------------------------------------------------
----ICDM-1566  - 
----welcome page loading dynamically
---- note : manually need to upload the zipped file (FILE DATA - BLOB)
---------------------------------------------------------------
---------------------------------------------------------------
--TABV_ICDM_FILES, TABV_ICDM_FILE_DATA, TABV_COMMON_PARAMS
---------------------------------------------------------------



--Insert TABv ICDM file record for welcome page
declare
	file_id number;
begin
	select seqv_attributes.nextval into file_id from dual;
	
	Insert into TABV_ICDM_FILES 
		(FILE_ID, NODE_ID, NODE_TYPE, FILE_NAME, FILE_COUNT, CREATED_USER, CREATED_DATE, VERSION) 
	values 
		(file_id,-3,'TEMPLATES','welcome_page.html',16,'HEF2FE',SYSDATE,1);

	Insert into TABV_ICDM_FILE_DATA (FILE_DATA_ID, FILE_ID, FILE_DATA, VERSION) values (seqv_attributes.nextval, file_id, empty_blob(), 1);


	Insert into TABV_COMMON_PARAMS (PARAM_ID, PARAM_DESC, PARAM_VALUE, VERSION) values ('WELCOME_FILE_ID','File id for loading the startup page',file_id,1);

	commit;
end;
/


INSERT INTO TABV_COMMON_PARAMS (PARAM_ID, PARAM_DESC, PARAM_VALUE) VALUES ('FOCUS_MATRIX_ASPICE_LINK', 'Link to Focus Matrix for Cal-Projects in Wiki', 'https://inside-docupedia.bosch.com/confluence/x/4DLsCQ');
commit;

delete from TABV_COMMON_PARAMS WHERE PARAM_ID = 'iCDM_CLIENT_VERSION';
Insert into TABV_COMMON_PARAMS (PARAM_ID,PARAM_DESC,PARAM_VALUE,VERSION) values ('iCDM_CLIENT_VERSION','iCDM Client''s current version','1.22.0',1);
commit;
