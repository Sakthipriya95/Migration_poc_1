spool c:\temp\60_synonyms.log


---------------------------------------------------------------
--  ALM TaskId : 333276
--  Synonyms for table : T_REGION, T_WORKPACKAGE_DIVISION_CDL
---------------------------------------------------------------
CREATE OR REPLACE SYNONYM t_region FOR DGS_ICDM.t_region;
CREATE OR REPLACE SYNONYM t_workpackage_division_cdl for DGS_ICDM.t_workpackage_division_cdl;

---------------------------------------------------------------------
--  ALM Story : 307426
--------------------------------------------------------------------- 
create synonym T_WEBFLOW_ELEMENT for DGS_ICDM.T_WEBFLOW_ELEMENT;

spool off