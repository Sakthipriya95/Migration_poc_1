spool c:\temp\17.PidcVersioningRemoveObsoleteTables.log

/**
 * Drops the obsolete tables. 
 * 
 * IMPORTANT : Should be done only if Populating data to T_PIDC_VERSION is completed !!!
 */

drop table TABV_PID_HISTORY cascade constraints;

drop table TABV_PID_STATUS cascade constraints;

spool off;
