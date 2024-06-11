spool c:\temp\03_3_Table_data_Messages.log

-----------------------------------------------------------------------------------------------
--  ALM TaskId : 337163
-----------------------------------------------------------------------------------------------


INSERT INTO TABV_ICDM_FILES (FILE_ID,NODE_ID,NODE_TYPE,FILE_NAME,CREATED_USER,CREATED_DATE,VERSION,FILE_COUNT) 
VALUES (SEQV_ATTRIBUTES.NEXTVAL,-7,'CHECKSSD_LOGO','disclosure.jpg',user,sysdate,1,1);

INSERT INTO TABV_ICDM_FILE_DATA (FILE_DATA_ID,FILE_ID,FILE_DATA,VERSION) SELECT SEQV_ATTRIBUTES.NEXTVAL,FILE_ID,EMPTY_BLOB(),1 FROM TABV_ICDM_FILES WHERE NODE_ID = -7 and NODE_TYPE = 'CHECKSSD_LOGO';

COMMIT;

-----------------------------------------------------------------------------------------------
--  ALM Task : 343531 - Add CDA Disclaimer dialog contents
-----------------------------------------------------------------------------------------------
INSERT INTO TABV_ICDM_FILES (FILE_ID,NODE_ID,NODE_TYPE,FILE_NAME,CREATED_USER,CREATED_DATE,VERSION,FILE_COUNT) 
VALUES (SEQV_ATTRIBUTES.NEXTVAL,-8,'TEMPLATES','cda_disclaimer.html',user,sysdate,1,1);

INSERT INTO TABV_ICDM_FILE_DATA (FILE_DATA_ID,FILE_ID,FILE_DATA,VERSION) SELECT SEQV_ATTRIBUTES.NEXTVAL,FILE_ID,EMPTY_BLOB(),1 FROM TABV_ICDM_FILES WHERE NODE_ID = -8 and NODE_TYPE = 'TEMPLATES';

COMMIT;

spool off