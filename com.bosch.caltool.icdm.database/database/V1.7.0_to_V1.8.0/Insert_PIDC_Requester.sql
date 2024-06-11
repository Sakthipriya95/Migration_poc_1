--
-- Insert File meta data
--
Insert into TABV_ICDM_FILES (FILE_ID,
							NODE_ID,
							NODE_TYPE,
							FILE_NAME,
							CREATED_USER,
							CREATED_DATE,
							MODIFIED_USER,
							MODIFIED_DATE,
							VERSION,
							FILE_COUNT) 
values (SeqV_Attributes.NEXTVAL,-2,'TEMPLATES','PIDC-Requester_V_4.0_Template.xlsm',user,sysdate,null,null,1,1);

--
-- insert FILE_ID in Common Parameters
--  ** MUST be done here, otherwise CURRVAL of the sequence is not correct!
--
Insert into TABV_COMMON_PARAMS (PARAM_ID,
								PARAM_DESC,
								PARAM_VALUE,
								VERSION) 
values ('ICDM_PIDC_REQUESTOR_TEMPL_FILE','File id for ICDM PIDC Requestor file template',SeqV_Attributes.CURRVAL,1);

--
-- Insert Row with empty BLOB in File Data table
--  ** File MUST be loaded via SQL-Developer
--
Insert into TABV_ICDM_FILE_DATA (FILE_DATA_ID,
								FILE_ID,
								FILE_DATA,
								VERSION) 
select SEQV_ATTRIBUTES.NEXTVAL, FILE_ID, EMPTY_BLOB(), 1 FROM TABV_ICDM_FILES WHERE NODE_ID = -2;

