----------------------------------
Read Me : OSS File installation
-----------------------------------

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

5. Before uploading the OSS document to DB, please refer this link in teams channel. 
    https://bosch.sharepoint.com/:w:/r/sites/msteams_5545073/Shared%20Documents/Integration/OSS/EditingDocumentTitleForOss.docx?d=w9f6205e86a2640b3a1a9bbcf9902b950&csf=1&web=1&e=YCzpmj
