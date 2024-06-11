spool c:\temp\01_Table_Alters.log

---------------------------------------------------------------------
--  ALM Task : 624755 - Server side and DB changes for WP should be locked from the data review
--
--  To add a new column in T_A2L_WP_RESPONSIBILITY 
---------------------------------------------------------------------
    
ALTER TABLE T_A2L_WP_RESPONSIBILITY 
    ADD (WP_RESP_STATUS VARCHAR2(1) DEFAULT 'N' );

-- To be executed in Development DB
ALTER TABLE T_A2L_WP_RESPONSIBILITY  
    MODIFY (WP_RESP_STATUS NOT NULL);


    

spool off