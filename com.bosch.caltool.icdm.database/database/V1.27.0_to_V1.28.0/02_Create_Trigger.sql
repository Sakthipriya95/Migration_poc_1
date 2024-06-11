spool c:\temp\02_Create_Trigger.log

--------------------------------------------------------
-- DDL for Trigger T_FOCUS_MATRIX_REVIEW - Before Insert
-- 
-- Trigger updated since the column RVW_ID renamed to FM_RVW_ID
--------------------------------------------------------
CREATE OR REPLACE TRIGGER TRG_FOCUS_MATRIX_REVIEW_INS 
  BEFORE INSERT ON T_FOCUS_MATRIX_REVIEW
  FOR EACH ROW
BEGIN
    IF :new.FM_RVW_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.FM_RVW_ID FROM DUAL;
    END IF;

    IF :new.VERSION is null THEN
        :new.VERSION := 1;
    END IF;

    IF :new.CREATED_DATE is null THEN
        :new.CREATED_DATE := sys_extract_utc(systimestamp);
    END IF;

    IF :new.CREATED_USER is null THEN
        :new.CREATED_USER := user;
    END IF;

END;
/

ALTER TRIGGER TRG_FOCUS_MATRIX_REVIEW_INS ENABLE;

ALTER TRIGGER TRG_FOCUS_MATRIX_REVIEW_UPDT COMPILE;

spool off