spool c:\temp\create_trigger.log

--------------------------------------------------------
--  June-16-2014
--------------------------------------------------------

--------------------------------------------------------
--  DDL for Trigger TRG_COMP_PKG_INS - Before insert
--------------------------------------------------------

-- ICDM - 817 

CREATE OR REPLACE TRIGGER TRG_COMP_PKG_INS
  BEFORE INSERT ON T_COMP_PKG FOR EACH ROW
Begin
    IF :new.COMP_PKG_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.COMP_PKG_ID FROM DUAL;
    END IF;

    IF :new.Version is null THEN
        :new.Version := 1;
    END IF;

    IF :new.Created_Date is null THEN
        :new.Created_Date := sys_extract_utc(systimestamp);
    END IF;

    IF :new.Created_User is null THEN
        :new.Created_User := user;
    END IF;


END;
/

ALTER TRIGGER TRG_COMP_PKG_INS ENABLE;



--------------------------------------------------------
--  DDL for Trigger TRG_COMP_PKG_UPD - Before update
--------------------------------------------------------

-- ICDM - 817 

CREATE OR REPLACE TRIGGER TRG_COMP_PKG_UPD 
  BEFORE UPDATE ON T_COMP_PKG FOR EACH ROW
BEGIN
    IF :new.Version = :old.Version THEN
        :new.Version := :old.Version + 1;
    END IF;

    IF :new.Modified_Date is null THEN
        :new.Modified_Date := sys_extract_utc(systimestamp);
    END IF;

    IF :new.Modified_User is null THEN
        :new.Modified_User := user;
    END IF;

END;
/

ALTER TRIGGER TRG_COMP_PKG_UPD ENABLE;



--------------------------------------------------------
--  DDL for Trigger TRG_T_COMP_PKG_BC_INS - Before insert
--------------------------------------------------------

-- ICDM - 817 

CREATE OR REPLACE TRIGGER TRG_T_COMP_PKG_BC_INS
  BEFORE INSERT ON T_COMP_PKG_BC FOR EACH ROW
Begin
    IF :new.COMP_BC_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.COMP_BC_ID FROM DUAL;
    END IF;

    IF :new.Version is null THEN
        :new.Version := 1;
    END IF;

    IF :new.Created_Date is null THEN
        :new.Created_Date := sys_extract_utc(systimestamp);
    END IF;

    IF :new.Created_User is null THEN
        :new.Created_User := user;
    END IF;


END;
/

ALTER TRIGGER TRG_T_COMP_PKG_BC_INS ENABLE;



--------------------------------------------------------
--  DDL for Trigger TRG_T_COMP_PKG_BC_UPD - Before update
--------------------------------------------------------

-- ICDM - 817 

CREATE OR REPLACE TRIGGER TRG_T_COMP_PKG_BC_UPD 
  BEFORE UPDATE ON T_COMP_PKG_BC FOR EACH ROW
BEGIN
    IF :new.Version = :old.Version THEN
        :new.Version := :old.Version + 1;
    END IF;

    IF :new.Modified_Date is null THEN
        :new.Modified_Date := sys_extract_utc(systimestamp);
    END IF;

    IF :new.Modified_User is null THEN
        :new.Modified_User := user;
    END IF;

END;
/

ALTER TRIGGER TRG_T_COMP_PKG_BC_UPD ENABLE;


-------------------------------------------------------
--  DDL for Trigger TRG_T_COMP_PKG_BC_FC_INS - Before insert
--------------------------------------------------------

-- ICDM - 817 

CREATE OR REPLACE TRIGGER TRG_T_COMP_PKG_BC_FC_INS
  BEFORE INSERT ON T_COMP_PKG_BC_FC FOR EACH ROW
Begin
    IF :new.COMP_BC_FC_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.COMP_BC_FC_ID FROM DUAL;
    END IF;

    IF :new.Version is null THEN
        :new.Version := 1;
    END IF;

    IF :new.Created_Date is null THEN
        :new.Created_Date := sys_extract_utc(systimestamp);
    END IF;

    IF :new.Created_User is null THEN
        :new.Created_User := user;
    END IF;


END;
/

ALTER TRIGGER TRG_T_COMP_PKG_BC_FC_INS ENABLE;



--------------------------------------------------------
--  DDL for Trigger TRG_T_COMP_PKG_BC_FC_UPD - Before update
--------------------------------------------------------

-- ICDM - 817 

CREATE OR REPLACE TRIGGER TRG_T_COMP_PKG_BC_FC_UPD 
  BEFORE UPDATE ON T_COMP_PKG_BC_FC FOR EACH ROW
BEGIN
    IF :new.Version = :old.Version THEN
        :new.Version := :old.Version + 1;
    END IF;

    IF :new.Modified_Date is null THEN
        :new.Modified_Date := sys_extract_utc(systimestamp);
    END IF;

    IF :new.Modified_User is null THEN
        :new.Modified_User := user;
    END IF;

END;
/

ALTER TRIGGER TRG_T_COMP_PKG_BC_FC_UPD ENABLE;

spool off
