spool c:\temp\table_alters.log

-------------------------------------------------
-- iCDM-1804
-- Add an index on T_RVW_Parameters.RVW_FUN_ID to improve the performance when deleting a review
----------------------------------------------------------
CREATE INDEX IDX_RVW_PAR_FUNID ON T_RVW_PARAMETERS (RVW_FUN_ID) 
	TABLESPACE DGS_ICDM_TS 
;

spool off



