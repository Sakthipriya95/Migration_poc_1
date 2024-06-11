spool c:\temp\03_3_Table_data_Messages.log

-----------------------------------------------------------------------------------------------
--  ALM TaskId : 328281: Add new message to display if the user does not have sufficient access rights 
--  Insert query for T_MESSAGES
-----------------------------------------------------------------------------------------------



INSERT INTO T_MESSAGES (GROUP_NAME,NAME,MESSAGE_TEXT) VALUES ('ICDM_STARTUP','NO_ACCESS_TEXT','You are not allowed to use iCDM (may be that you are not using 2FA)!');
COMMIT;


spool off