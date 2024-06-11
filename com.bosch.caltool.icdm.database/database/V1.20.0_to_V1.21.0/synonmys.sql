spool c:\temp\synonyms.log

--------------------------------------------------------
--  ICDM-1577 
--------------------------------------------------------

CREATE OR REPLACE SYNONYM T_FOCUS_MATRIX FOR DGS_ICDM.T_FOCUS_MATRIX;


---------------------------------------------------------------
--  ICDM-1617
---------------------------------------------------------------
DROP SYNONYM TABV_PID_HISTORY;
DROP SYNONYM TABV_PID_STATUS;

spool off

