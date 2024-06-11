spool c:\temp\procedures.log

--------------------------------------------------------
--  April-30-2014
-------------------------------------------------------- 



----------------------------------------------
-- Function to get the current transaction ID
-- ICDM-728
----------------------------------------------

CREATE OR REPLACE Function F_Get_Transaction_ID
   RETURN varchar
IS

BEGIN
  RETURN dbms_transaction.local_transaction_id(TRUE);

EXCEPTION
  WHEN OTHERS THEN
      raise_application_error(-20001,'Error in transaction id retrieval - '||SQLCODE||' -ERROR- '||SQLERRM);
END;



spool off
