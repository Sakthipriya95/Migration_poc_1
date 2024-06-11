spool c:\temp\02_Create_Trigger.log

--------------------------------------------------------
-- DDL for Trigger T_FOCUS_MATRIX - Before Insert
-- 
-- ICDM-2249
--------------------------------------------------------
CREATE OR REPLACE TRIGGER TRG_FOCUS_MATRIX_INS 
  BEFORE INSERT ON T_FOCUS_MATRIX
  FOR EACH ROW
Begin
    IF :new.FM_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.FM_ID FROM DUAL;
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

ALTER TRIGGER TRG_FOCUS_MATRIX_INS ENABLE;

--------------------------------------------------------
-- DDL for Trigger T_FOCUS_MATRIX - Before update
-- 
-- ICDM-2249
--------------------------------------------------------
CREATE OR REPLACE TRIGGER TRG_FOCUS_MATRIX_UPDT
BEFORE UPDATE ON T_FOCUS_MATRIX
FOR EACH ROW
BEGIN
    IF :new.VERSION = :old.VERSION THEN
        :new.VERSION := :old.VERSION + 1;
    END IF;

    if :new.MODIFIED_DATE IS NULL or NOT UPDATING('Modified_Date') then
        :new.MODIFIED_DATE := sys_extract_utc(systimestamp);
    end if;

    if :new.MODIFIED_USER IS NULL then
        :new.MODIFIED_USER := user;
    end if;
 
END;
/

ALTER TRIGGER TRG_FOCUS_MATRIX_UPDT ENABLE;

--------------------------------------------------------
-- DDL for Trigger T_FOCUS_MATRIX_REVIEW - Before Insert
-- 
-- ICDM-2249
--------------------------------------------------------
CREATE OR REPLACE TRIGGER TRG_FOCUS_MATRIX_REVIEW_INS 
  BEFORE INSERT ON T_FOCUS_MATRIX_REVIEW
  FOR EACH ROW
BEGIN
    IF :new.RVW_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.RVW_ID FROM DUAL;
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

--------------------------------------------------------
-- DDL for Trigger T_FOCUS_MATRIX_REVIEW - Before update
-- 
-- ICDM-2249
--------------------------------------------------------
CREATE OR REPLACE TRIGGER TRG_FOCUS_MATRIX_REVIEW_UPDT
BEFORE UPDATE ON T_FOCUS_MATRIX_REVIEW
FOR EACH ROW
BEGIN
    IF :new.VERSION = :old.VERSION THEN
        :new.VERSION := :old.VERSION + 1;
    END IF;

    if :new.MODIFIED_DATE IS NULL or NOT UPDATING('Modified_Date') then
        :new.MODIFIED_DATE := sys_extract_utc(systimestamp);
    end if;

    if :new.MODIFIED_USER IS NULL then
        :new.MODIFIED_USER := user;
    end if;
 
END;
/

ALTER TRIGGER TRG_FOCUS_MATRIX_REVIEW_UPDT ENABLE;
--------------------------------------------------------
-- DDL for Trigger T_WORKPACKAGE - Before Insert
-- 
-- ICDM-2376
--------------------------------------------------------

CREATE OR REPLACE TRIGGER TRG_T_WORKPACKAGE_INS
  BEFORE INSERT ON T_WORKPACKAGE
  FOR EACH ROW
BEGIN
    IF :new.WP_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.WP_ID FROM DUAL;
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
 ALTER TRIGGER TRG_T_WORKPACKAGE_INS ENABLE; 

 --------------------------------------------------------
-- DDL for Trigger T_WORKPACKAGE - Before Update
-- 
-- ICDM-2376
--------------------------------------------------------
 
CREATE OR REPLACE TRIGGER TRG_T_WORKPACKAGE_UPDT
BEFORE UPDATE ON T_WORKPACKAGE
FOR EACH ROW
BEGIN
    IF :new.VERSION = :old.VERSION THEN
        :new.VERSION := :old.VERSION + 1;
    END IF;

   IF :new.MODIFIED_DATE is null or NOT UPDATING('MODIFIED_DATE') THEN
        :new.MODIFIED_DATE := sys_extract_utc(systimestamp);
    end if;
  
  IF :new.MODIFIED_USER is null or NOT UPDATING('MODIFIED_USER') THEN  
        :new.MODIFIED_USER := user;
    end if;
 
END;
/

ALTER TRIGGER TRG_T_WORKPACKAGE_UPDT ENABLE;

--------------------------------------------------------
-- DDL for Trigger T_WORKPACKAGE_DIVISION - Before Insert
-- 
-- ICDM-2376
--------------------------------------------------------

CREATE OR REPLACE TRIGGER TRG_T_WORKPACKAGE_DIVISION_INS
  BEFORE INSERT ON T_WORKPACKAGE_DIVISION
  FOR EACH ROW
Begin
    IF :new.WP_DIV_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.WP_DIV_ID FROM DUAL;
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

ALTER TRIGGER TRG_T_WORKPACKAGE_DIVISION_INS ENABLE;

--------------------------------------------------------
-- DDL for Trigger T_WORKPACKAGE_DIVISION - Before Update
-- 
-- ICDM-2376
--------------------------------------------------------

CREATE OR REPLACE TRIGGER TRG_T_WRKPKG_DIVISION_UPDT
BEFORE UPDATE ON T_WORKPACKAGE_DIVISION
FOR EACH ROW
BEGIN
    IF :new.VERSION = :old.VERSION THEN
        :new.VERSION := :old.VERSION + 1;
    END IF;

   IF :new.MODIFIED_DATE is null or NOT UPDATING('MODIFIED_DATE') THEN
        :new.MODIFIED_DATE := sys_extract_utc(systimestamp);
    end if;
  
  IF :new.MODIFIED_USER is null or NOT UPDATING('MODIFIED_USER') THEN  
        :new.MODIFIED_USER := user;
    end if;
 
END;
/

ALTER TRIGGER TRG_T_WRKPKG_DIVISION_UPDT ENABLE;


--------------------------------------------------------
--  DDL for Trigger T_WS_SYSTEMS - Before Insert
--- ICDM-1272
--------------------------------------------------------
CREATE OR REPLACE TRIGGER  TRG_WS_SYSTEM__INS
BEFORE INSERT ON T_WS_SYSTEMS FOR EACH ROW
BEGIN
    IF :new.SYSTEM_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.SYSTEM_ID FROM DUAL;
    END IF;

    IF :new.Version is null THEN
        :new.Version := 1;
    END IF;
END;
/

ALTER TRIGGER TRG_WS_SYSTEM__INS ENABLE;

--------------------------------------------------------
-- DDL for Trigger T_WS_SYSTEMS - Before Update
-- ICDM-1272
--------------------------------------------------------
CREATE OR REPLACE TRIGGER TRG_WS_SYSTEMS__UPDT
BEFORE UPDATE ON T_WS_SYSTEMS
FOR EACH ROW
BEGIN
	
    IF :new.VERSION = :old.VERSION THEN
        :new.VERSION := :old.VERSION + 1;
    END IF;
    
END;
/

ALTER TRIGGER TRG_WS_SYSTEMS__UPDT ENABLE;


spool off