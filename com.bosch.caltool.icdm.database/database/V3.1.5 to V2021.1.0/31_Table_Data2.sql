spool c:\temp\31_Table_Data.log

------------------------------------------------------------------------------------------------------------------
-- ALM Task ID : 551878
-- iCDM Excel Export of Par2WP
------------------------------------------------------------------------------------------------------------------
UPDATE T_A2L_WP_IMPORT_PROFILE 
  SET PROFILE_DETAILS='{"headingRowNum":1,"sheetName":"PAL","importMode":"W","wpColumn":"WorkPackage","respColumn":"Responsibilities","respTypeColumn":"Responsibility Type","labelColumn":"Parameter Name","prefixForWp":"","prefixForResp":"","fileType":{"seperator":"","format":"xlsx,xls,xlsm","type":"Excel"}}' 
  WHERE PROFILE_NAME='iCDM Excel Export of Par2WP';

COMMIT;

------------------------------------------------------------------------------------------------------------------
--Story 556280
------------------------------------------------------------------------------------------------------------------
--Define new Node id for cdfx delivery special node access
INSERT INTO TABV_COMMON_PARAMS (PARAM_ID,PARAM_DESC,PARAM_VALUE) VALUES('CDFX_DELIVERY_NODE_ID','Node id for cdfx delivery special node access','-401');
COMMIT;

--Provide users with cdfx delivery access
DECLARE
    cdfx_del_node_id VARCHAR2(2000);
    user_row TABV_APIC_USERS%rowtype;

BEGIN
  --Generate node access ID 
  SELECT PARAM_VALUE INTO cdfx_del_node_id FROM TABV_COMMON_PARAMS where PARAM_ID = 'CDFX_DELIVERY_NODE_ID';   
    
  FOR user_row IN (
      SELECT * FROM TABV_APIC_USERS where USERNAME IN ('HEF2FE','IMI2SI','BNE4COB','GGE6COB','PDH2COB','RHM5COB','DJA7COB','EMS4KOR')
    )
  
  LOOP
        --INSERTING into TABV_APIC_NODE_ACCESS
        INSERT INTO TABV_APIC_NODE_ACCESS(NODE_ID,READRIGHT,WRITERIGHT,GRANTRIGHT,OWNER,NODE_TYPE,USER_ID,IS_SET_BY_SYSTEM)                           
        VALUES(                                 
              TO_NUMBER(cdfx_del_node_id),'Y','Y','Y','Y','CDFX_DELIVERY',user_row.user_id,'N'
            );
  END LOOP;
    
  COMMIT;  
END;
/



------------------------------------------------------------------------------------------------------------------
--551259: CATS - ALM hotline migration related changes in iCDM(EPIC)
--556665: Add prefix to the subject of the mails that are auto-generated from iCDM (STORY)
------------------------------------------------------------------------------------------------------------------
UPDATE tabv_common_params 
    SET PARAM_VALUE='#INFO - PIDC created'
    WHERE PARAM_ID='MAIL_NEW_PIDC';
COMMIT;

--Changing Param Id and param decription to indicate subject line is for attribute value clearing request mail
DELETE FROM TABV_COMMON_PARAMS WHERE PARAM_ID = 'ICDM_HOTLINE_SUBJECT';
INSERT INTO TABV_COMMON_PARAMS (PARAM_ID,PARAM_DESC,PARAM_VALUE,VERSION) 
    VALUES ('ICDM_ATTR_CLEARING_SUBJECT'
        ,'Subject content for iCDM Attribute Clearing request'
        ,'#CLEARING - iCDM Attribute Clearing request'
        ,1
    );

COMMIT;   
----------------------------------------------------------------------------------------------------------------------------   
 --   T-558089 HEX Compliance Report: Modify error message' Value not set for the following attribute(s)
------------------------------------------------------------------------------------------------------------------------- 
UPDATE T_MESSAGES SET MESSAGE_TEXT= 'Value not set for the following attribute(s) : {0} \nPlease contact the PIDC responsible for changes in the project.'
  WHERE NAME ='ATTR_VALUE_NOT_DEFINED' and GROUP_NAME ='FEAVAL' ;       
COMMIT;

------------------------------------------------------------------------------------------------------------------
-- ALM TaskId : 557942 : Version update to 2021.1.0 script
------------------------------------------------------------------------------------------------------------------      
delete from TABV_COMMON_PARAMS WHERE upper(PARAM_ID) = 'ICDM_CLIENT_VERSION';
Insert into TABV_COMMON_PARAMS (PARAM_ID,PARAM_DESC,PARAM_VALUE,VERSION) values ('ICDM_CLIENT_VERSION','iCDM Client''s current version','2021.1.0',1); 
COMMIT;

spool off
