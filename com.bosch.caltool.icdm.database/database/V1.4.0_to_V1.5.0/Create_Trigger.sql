spool c:\temp\create_trigger.log
--------------------------------------------------------
--  November-25-2013  
--------------------------------------------------------

--------------------------------------------------------
--  DDL for Trigger TRG_ICDM_FILES_INS - Before insert
--------------------------------------------------------

CREATE OR REPLACE TRIGGER TRG_ICDM_FILES_INS
  BEFORE INSERT ON TABV_ICDM_FILES
  FOR EACH ROW
Begin
	IF :new.FILE_ID is null THEN
		SELECT SeqV_Attributes.nextval INTO :new.FILE_ID FROM DUAL;
	END IF;

	IF :new.Version is null THEN
		:new.Version := 1;
	END IF;

	IF :new.Created_Date is null THEN
		:new.Created_Date := sysdate;
	END IF;

	IF :new.Created_User is null THEN
		:new.Created_User := user;
	END IF;


END;
/
ALTER TRIGGER TRG_ICDM_FILES_INS ENABLE;


--------------------------------------------------------
--  DDL for Trigger TRG_ICDM_FILES_UPD - Before update
--------------------------------------------------------


CREATE OR REPLACE TRIGGER TRG_ICDM_FILES_UPD 
  BEFORE UPDATE ON TABV_ICDM_FILES
  FOR EACH ROW
BEGIN
	IF :new.Version = :old.Version THEN
		:new.Version := :old.Version + 1;
	END IF;

	IF :new.Modified_Date is null THEN
		:new.Modified_Date := sysdate;
	END IF;

	IF :new.Modified_User is null THEN
		:new.Modified_User := user;
	END IF;

END;
/
ALTER TRIGGER TRG_ICDM_FILES_UPD ENABLE;


--------------------------------------------------------
--  DDL for Trigger TRG_ICDM_FILE_DATA_INS - Before insert
--------------------------------------------------------

CREATE OR REPLACE TRIGGER TRG_ICDM_FILE_DATA_INS
  BEFORE INSERT ON TABV_ICDM_FILE_DATA
  FOR EACH ROW
Begin
	IF :new.FILE_ID is null THEN
		SELECT SeqV_Attributes.nextval INTO :new.FILE_DATA_ID FROM DUAL;
	END IF;

	IF :new.Version is null THEN
		:new.Version := 1;
	END IF;

END;
/
ALTER TRIGGER TRG_ICDM_FILE_DATA_INS ENABLE;


--------------------------------------------------------
--  DDL for Trigger TRG_ICDM_FILE_DATA_UPD - Before update
--------------------------------------------------------


CREATE OR REPLACE TRIGGER TRG_ICDM_FILE_DATA_UPD 
  BEFORE UPDATE ON TABV_ICDM_FILE_DATA
  FOR EACH ROW
BEGIN
	IF :new.Version = :old.Version THEN
		:new.Version := :old.Version + 1;
	END IF;
END;
/
ALTER TRIGGER TRG_ICDM_FILE_DATA_UPD ENABLE;


--------------------------------------------------------
--  DDL for Trigger TRG_TOP_LEVEL_ENTITIES_INS - Before insert
--------------------------------------------------------

CREATE OR REPLACE TRIGGER TRG_TOP_LEVEL_ENTITIES_INS 
BEFORE INSERT ON TABV_TOP_LEVEL_ENTITIES
FOR EACH ROW
BEGIN
    IF :new.Version is null THEN
        :new.Version := 1;
    END IF; 
    IF :new.LAST_MOD_DATE is null THEN
        :new.LAST_MOD_DATE := sysdate;
    END IF;

END;
/
ALTER TRIGGER TRG_TOP_LEVEL_ENTITIES_INS ENABLE;
--------------------------------------------------------
--  DDL for Trigger TRG_TOP_LEVEL_ENTITIES_INS - Before update
--------------------------------------------------------

CREATE OR REPLACE TRIGGER TRG_TOP_LEVEL_ENTITIES_UPD 
BEFORE UPDATE ON TABV_TOP_LEVEL_ENTITIES
FOR EACH ROW
BEGIN
    IF :new.Version = :old.Version THEN
        :new.Version := :old.Version + 1;
    END IF;

    IF :new.LAST_MOD_DATE is null THEN
        :new.LAST_MOD_DATE := sysdate;
    END IF;
    
END;
/
ALTER TRIGGER TRG_TOP_LEVEL_ENTITIES_UPD ENABLE;
spool off;
