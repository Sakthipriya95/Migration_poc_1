spool c:\temp\create_trigger.log
--------------------------------------------------------
--  August-15-2013  
--------------------------------------------------------

---------------------------------------------------------------------
--  Trigger TRG_UCP_ATTRS_INS - Before insert on table TABV_UCP_ATTRS
---------------------------------------------------------------------


CREATE OR REPLACE TRIGGER TRG_UCP_ATTRS_INS
  BEFORE INSERT ON TABV_UCP_ATTRS
  FOR EACH ROW
BEGIN
	IF :new.UCPA_ID is null THEN
		SELECT SeqV_Attributes.nextval INTO :new.UCPA_ID FROM DUAL;
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
ALTER TRIGGER TRG_UCP_ATTRS_INS ENABLE;

--------------------------------------------------------------------------
--  Trigger TRG_UCP_ATTRS_UPD - Before update on table TABV_UCP_ATTRS
--------------------------------------------------------------------------
CREATE OR REPLACE TRIGGER TRG_UCP_ATTRS_UPD
  BEFORE UPDATE ON TABV_UCP_ATTRS
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
ALTER TRIGGER TRG_UCP_ATTRS_UPD ENABLE;



---------------------------------------------------------------------
--  Trigger TRG_UC_GROUP_INS - Before insert on table TABV_USE_CASE_GROUPS
---------------------------------------------------------------------


CREATE OR REPLACE TRIGGER TRG_UC_GROUP_INS
  BEFORE INSERT ON TABV_USE_CASE_GROUPS
  FOR EACH ROW
BEGIN
	IF :new.GROUP_ID is null THEN
		SELECT SeqV_Attributes.nextval INTO :new.GROUP_ID FROM DUAL;
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
ALTER TRIGGER TRG_UC_GROUP_INS ENABLE;

--------------------------------------------------------------------------
--  Trigger TRG_UC_GROUP_UPD - Before update on table TABV_USE_CASE_GROUPS
--------------------------------------------------------------------------


CREATE OR REPLACE TRIGGER TRG_UC_GROUP_UPD
  BEFORE UPDATE ON TABV_USE_CASE_GROUPS
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
ALTER TRIGGER TRG_UC_GROUP_UPD ENABLE;



---------------------------------------------------------------------
--  Trigger TRG_UC_POINTS_INS - Before insert on table TABV_USE_CASE_POINTS
---------------------------------------------------------------------


CREATE OR REPLACE TRIGGER TRG_UC_POINTS_INS
  BEFORE INSERT ON TABV_USE_CASE_POINTS
  FOR EACH ROW
BEGIN
	IF :new.POINT_ID is null THEN
		SELECT SeqV_Attributes.nextval INTO :new.POINT_ID FROM DUAL;
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
ALTER TRIGGER TRG_UC_POINTS_INS ENABLE;

--------------------------------------------------------------------------
--  Trigger TRG_UC_POINTS_UPD - Before update on table TABV_USE_CASE_POINTS
--------------------------------------------------------------------------


CREATE OR REPLACE TRIGGER TRG_UC_POINTS_UPD
  BEFORE UPDATE ON TABV_USE_CASE_POINTS
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
ALTER TRIGGER TRG_UC_POINTS_UPD ENABLE;



---------------------------------------------------------------------
--  Trigger TRG_UC_SECTIONS_INS - Before insert on table TABV_USE_CASE_SECTIONS
---------------------------------------------------------------------


CREATE OR REPLACE TRIGGER TRG_UC_SECTIONS_INS
  BEFORE INSERT ON TABV_USE_CASE_SECTIONS
  FOR EACH ROW
BEGIN
	IF :new.SECTION_ID is null THEN
		SELECT SeqV_Attributes.nextval INTO :new.SECTION_ID FROM DUAL;
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
ALTER TRIGGER TRG_UC_SECTIONS_INS ENABLE;

--------------------------------------------------------------------------
--  Trigger TRG_UC_SECTIONS_UPD - Before update on table TABV_USE_CASE_SECTIONS
--------------------------------------------------------------------------


CREATE OR REPLACE TRIGGER TRG_UC_SECTIONS_UPD
  BEFORE UPDATE ON TABV_USE_CASE_SECTIONS
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
ALTER TRIGGER TRG_UC_SECTIONS_UPD ENABLE;


---------------------------------------------------------------------
--  Trigger TRG_USE_CASES_INS - Before insert on table TABV_USE_CASES
---------------------------------------------------------------------


CREATE OR REPLACE TRIGGER TRG_USE_CASES_INS
  BEFORE INSERT ON TABV_USE_CASES
  FOR EACH ROW
BEGIN
	IF :new.USE_CASE_ID is null THEN
		SELECT SeqV_Attributes.nextval INTO :new.USE_CASE_ID FROM DUAL;
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
ALTER TRIGGER TRG_USE_CASES_INS ENABLE;

--------------------------------------------------------------------------
--  Trigger TRG_USE_CASES_UPD - Before update on table TABV_USE_CASES
--------------------------------------------------------------------------


CREATE OR REPLACE TRIGGER TRG_USE_CASES_UPD
  BEFORE UPDATE ON TABV_USE_CASES
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
ALTER TRIGGER TRG_USE_CASES_UPD ENABLE;

