spool c:\temp\03_Table_data_Common_Param.log

--------------------------------------------------------------------------------------
--  ALM TaskId : 364955: Add new common parameter to acces Emmission robustness page 
--  Insert query for TABV_COMMON_PARAMS
-------------------------------------------------------------------------------------


INSERT INTO TABV_COMMON_PARAMS (PARAM_ID,PARAM_DESC,PARAM_VALUE) VALUES('EMR_NODE_ID','A special access rights for users to access Emmisssion robustness Page','-101');
COMMIT;

spool off