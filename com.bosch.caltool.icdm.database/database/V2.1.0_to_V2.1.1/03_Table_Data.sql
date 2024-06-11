spool c:\temp\03_Table_Data.log

-----------------------------------------------------------------------------------------------
--Task 260768 : open CheckSSD EXCEL report in HEX compare
-----------------------------------------------------------------------------------------------

insert into T_MESSAGES(GROUP_NAME,NAME,MESSAGE_TEXT) values ('HEX_COMPARE','HEX_COMPARE_COMPLI_CHECK','There are COMPLI failures in the HEX file! The CheckSSD report will be opened.');

commit;

spool off

     