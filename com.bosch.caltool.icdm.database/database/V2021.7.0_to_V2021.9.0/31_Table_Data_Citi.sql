spool c:\temp\31_Table_Data_Citi.log

--------------------------------------------------------------------------------------------------------------------
--594489: show citibot component in iCDM based on common parameter configuration
--------------------------------------------------------------------------------------------------------------------
Insert into TABV_COMMON_PARAMS 
    (PARAM_ID,PARAM_DESC,PARAM_VALUE,VERSION) 
values 
    ('ICDM_TOOL_SUPPORT_VISIBILITY','Configure visibility of iCDM Tool Support View(CITI Bot). Possible values : N/P/A','N',1);
    
commit;

spool off
