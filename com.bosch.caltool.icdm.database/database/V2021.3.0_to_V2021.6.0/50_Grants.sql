spool c:\temp\50_Grants.log

------------------------------------------------------------------------------------------------------------------
--  ALM TaskId : 600278 - Create web service for getting EMR Sheet data
------------------------------------------------------------------------------------------------------------------

--V_PIDC_VARIANTS
GRANT SELECT ON V_PIDC_VARIANTS TO DGS_ICDM_JPA;

--V_EMR_SHEETS_ALL
GRANT SELECT ON V_EMR_SHEETS_ALL TO DGS_ICDM_JPA;

--V_EMR_DETAILS_ALL
GRANT SELECT ON V_EMR_DETAILS_ALL TO DGS_ICDM_JPA;

spool off
