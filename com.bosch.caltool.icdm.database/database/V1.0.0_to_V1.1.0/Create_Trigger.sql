spool c:\temp\create_trigger.log
--------------------------------------------------------
--  August-02-2013  
--------------------------------------------------------

--------------------------------------------------------
--  DDL for Trigger TRG_TABV_COMMON_PARAMS_INS - Before insert
--------------------------------------------------------

CREATE OR REPLACE TRIGGER TRG_TABV_COMMON_PARAMS_INS
  BEFORE INSERT 
  ON TABV_COMMON_PARAMS 
  FOR EACH ROW 
BEGIN 
  IF :new.Version IS NULL THEN
    :new.Version  := 1;
  END IF;
END;
/
ALTER TRIGGER "TRG_TABV_COMMON_PARAMS_INS" ENABLE;

spool off;