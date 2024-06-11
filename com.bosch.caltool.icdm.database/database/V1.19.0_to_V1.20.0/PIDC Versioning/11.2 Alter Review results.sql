spool c:\temp\11.2pidcVersioning.log

ALTER TABLE T_RVW_RESULTS drop column A2L_ID;

ALTER TABLE T_RVW_RESULTS drop column PROJECT_ID;

spool off;