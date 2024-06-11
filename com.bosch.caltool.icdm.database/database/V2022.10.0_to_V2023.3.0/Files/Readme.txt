-----------------------------------
Read Me : Welcome file and OSS File installation
-----------------------------------


Steps to replace the welcome file
---------------------------------

Note : This file upload has to be done directly in SQL developer


1. Identify WELCOME_FILE_ID in TABV_COMMON_PARAMS using select * from TABV_COMMON_PARAMS where PARAM_ID = 'WELCOME_FILE_ID' 

2. Backup the current file, identified file data using the file id from tabv_common_params 

    select * from TABV_ICDM_FILE_DATA 
      where file_id = 
        (select param_value 
            from TABV_COMMON_PARAMS 
            where PARAM_ID = 'WELCOME_FILE_ID'
        )


3. From SQL Developer, Tables > Data view, from table TABV_ICDM_FILE_DATA, indentify the row using the file ID identified in step 1

4. Upload the new file using the option 'Load data' for '(BLOB)' field value, in column FILE_DATA


Steps to replace the OSS file
---------------------------------

Note : This file upload has to be done directly in SQL developer


1. Identify OSS_FILE_ID in TABV_COMMON_PARAMS using select * from TABV_COMMON_PARAMS where PARAM_ID = 'OSS_FILE_ID' 

2. Backup the current file, identified file data using the file id from tabv_common_params 

    select * from TABV_ICDM_FILE_DATA 
      where file_id = 
        (select param_value 
            from TABV_COMMON_PARAMS 
            where PARAM_ID = 'OSS_FILE_ID'
        )


3. From SQL Developer, Tables > Data view, from table TABV_ICDM_FILE_DATA, indentify the row using the file ID identified in step 1

4. Upload the new file using the option 'Load data' for '(BLOB)' field value, in column FILE_DATA
