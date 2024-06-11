spool c:\temp\create_trigger.log
--------------------------------------------------------
--  January-17-2014
--------------------------------------------------------

--------------------------------------------------------
--  DDL for Trigger TRG_RVW_RESULTS_INS - Before insert
--------------------------------------------------------

CREATE OR REPLACE TRIGGER TRG_RVW_RESULTS_INS
  BEFORE INSERT ON T_RVW_RESULTS
  FOR EACH ROW
Begin
	IF :new.RESULT_ID is null THEN
		SELECT SeqV_Attributes.nextval INTO :new.RESULT_ID FROM DUAL;
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
ALTER TRIGGER TRG_RVW_RESULTS_INS ENABLE;


--------------------------------------------------------
--  DDL for Trigger TRG_RVW_RESULTS_UPD - Before update
--------------------------------------------------------


CREATE OR REPLACE TRIGGER TRG_RVW_RESULTS_UPD 
  BEFORE UPDATE ON T_RVW_RESULTS
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
ALTER TRIGGER TRG_RVW_RESULTS_UPD ENABLE;


--------------------------------------------------------
--  DDL for Trigger TRG_RVW_PARTICIPANTS_INS - Before insert
--------------------------------------------------------

CREATE OR REPLACE TRIGGER TRG_RVW_PARTICIPANTS_INS
  BEFORE INSERT ON T_RVW_PARTICIPANTS
  FOR EACH ROW
Begin
	IF :new.PARTICIPANT_ID is null THEN
		SELECT SeqV_Attributes.nextval INTO :new.PARTICIPANT_ID FROM DUAL;
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
ALTER TRIGGER TRG_RVW_PARTICIPANTS_INS ENABLE;


--------------------------------------------------------
--  DDL for Trigger TRG_RVW_PARTICIPANTS_UPD - Before update
--------------------------------------------------------


CREATE OR REPLACE TRIGGER TRG_RVW_PARTICIPANTS_UPD 
  BEFORE UPDATE ON T_RVW_PARTICIPANTS
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
ALTER TRIGGER TRG_RVW_PARTICIPANTS_UPD ENABLE;


--------------------------------------------------------
--  DDL for Trigger TRG_RVW_FUNCTIONS_INS - Before insert
--------------------------------------------------------

CREATE OR REPLACE TRIGGER TRG_RVW_FUNCTIONS_INS
  BEFORE INSERT ON T_RVW_FUNCTIONS
  FOR EACH ROW
Begin
	IF :new.RVW_FUN_ID is null THEN
		SELECT SeqV_Attributes.nextval INTO :new.RVW_FUN_ID FROM DUAL;
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
ALTER TRIGGER TRG_RVW_FUNCTIONS_INS ENABLE;


--------------------------------------------------------
--  DDL for Trigger TRG_RVW_FUNCTIONS_UPD - Before update
--------------------------------------------------------


CREATE OR REPLACE TRIGGER TRG_RVW_FUNCTIONS_UPD 
  BEFORE UPDATE ON T_RVW_FUNCTIONS
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
ALTER TRIGGER TRG_RVW_FUNCTIONS_UPD ENABLE;


--------------------------------------------------------
--  DDL for Trigger TRG_RVW_PARAMETERS_INS - Before insert
--------------------------------------------------------

CREATE OR REPLACE TRIGGER TRG_RVW_PARAMETERS_INS
  BEFORE INSERT ON T_RVW_PARAMETERS
  FOR EACH ROW
Begin
	IF :new.RVW_PARAM_ID is null THEN
		SELECT SeqV_Attributes.nextval INTO :new.RVW_PARAM_ID FROM DUAL;
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
ALTER TRIGGER TRG_RVW_PARAMETERS_INS ENABLE;


--------------------------------------------------------
--  DDL for Trigger TRG_RVW_PARAMETERS_UPD - Before update
--------------------------------------------------------


CREATE OR REPLACE TRIGGER TRG_RVW_PARAMETERS_UPD 
  BEFORE UPDATE ON T_RVW_PARAMETERS
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
ALTER TRIGGER TRG_RVW_PARAMETERS_UPD ENABLE;


--------------------------------------------------------
--  DDL for Trigger TRG_RVW_FILES_INS - Before insert
--------------------------------------------------------

CREATE OR REPLACE TRIGGER TRG_RVW_FILES_INS
  BEFORE INSERT ON T_RVW_FILES
  FOR EACH ROW
Begin
	IF :new.RVW_FILE_ID is null THEN
		SELECT SeqV_Attributes.nextval INTO :new.RVW_FILE_ID FROM DUAL;
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
ALTER TRIGGER TRG_RVW_FILES_INS ENABLE;


--------------------------------------------------------
--  DDL for Trigger TRG_RVW_FILES_UPD - Before update
--------------------------------------------------------


CREATE OR REPLACE TRIGGER TRG_RVW_FILES_UPD 
  BEFORE UPDATE ON T_RVW_FILES
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
ALTER TRIGGER TRG_RVW_FILES_UPD ENABLE;


Spool Off;
