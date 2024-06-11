spool c:\temp\create_trigger.log
-----------------------------------
---August-11-2014
------------------------------------

--------------------------------------------------------
--  DDL for Trigger TRG_TOP_LEVEL_ENTITIES_INS - Before insert
--------------------------------------------------------

-- ICDM - 933
CREATE OR REPLACE TRIGGER TRG_TOP_LEVEL_ENTITIES_INS
BEFORE INSERT ON TABV_TOP_LEVEL_ENTITIES
FOR EACH ROW
BEGIN
    IF :new.Version is null THEN
        :new.Version := 1;
    END IF;
    IF :new.LAST_MOD_DATE is null THEN
        :new.LAST_MOD_DATE := sys_extract_utc(systimestamp);
    END IF;

END;
/

ALTER TRIGGER TRG_TOP_LEVEL_ENTITIES_INS ENABLE;

--------------------------------------------------------
--  DDL for Trigger TRG_TOP_LEVEL_ENTITIES_UPD - Before update
--------------------------------------------------------

-- ICDM - 933
CREATE OR REPLACE TRIGGER TRG_TOP_LEVEL_ENTITIES_UPD
BEFORE UPDATE ON TABV_TOP_LEVEL_ENTITIES
FOR EACH ROW
BEGIN
    IF :new.Version = :old.Version THEN
        :new.Version := :old.Version + 1;
    END IF;

    IF :new.LAST_MOD_DATE is null THEN
        :new.LAST_MOD_DATE := sys_extract_utc(systimestamp);
    END IF;

END;
/

ALTER TRIGGER TRG_TOP_LEVEL_ENTITIES_UPD ENABLE;

--------------------------------------------------------
--  DDL for Trigger TRG_APIC_CHARACTERISTICS_UPD - Before update
--- Icdm-954
--------------------------------------------------------

CREATE OR REPLACE TRIGGER TRG_APIC_CHARACTERISTICS_UPD
BEFORE UPDATE ON T_CHARACTERISTICS
FOR EACH ROW
BEGIN
    IF :new.Version = :old.Version THEN
        :new.Version := :old.Version + 1;
    END IF;

    if :new.modified_date IS NULL then
        :new.modified_date := sys_extract_utc(systimestamp);
    end if;

    if :new.modified_user IS NULL then
        :new.modified_user := user;
    end if;

END;
/

ALTER TRIGGER TRG_APIC_CHARACTERISTICS_UPD ENABLE;

--------------------------------------------------------
--  DDL for Trigger T_CHARACTERISTIC_VALUES - Before update
--- Icdm-954
--------------------------------------------------------


CREATE OR REPLACE TRIGGER TRG_APIC_CHAR_VAL_UPD
BEFORE UPDATE ON T_CHARACTERISTIC_VALUES
FOR EACH ROW
BEGIN
    IF :new.Version = :old.Version THEN
        :new.Version := :old.Version + 1;
    END IF;

    if :new.modified_date IS NULL then
        :new.modified_date := sys_extract_utc(systimestamp);
    end if;

    if :new.modified_user IS NULL then
        :new.modified_user := user;
    end if;

 
END;
/

ALTER TRIGGER TRG_APIC_CHAR_VAL_UPD ENABLE;

--------------------------------------------------------
--  DDL for Trigger T_CHARACTERISTICS - Before Insert
--- Icdm-954
--------------------------------------------------------

CREATE OR REPLACE TRIGGER TRG_APIC_CHARACTERISTICS_INS
BEFORE INSERT ON T_CHARACTERISTICS FOR EACH ROW
BEGIN
    IF :new.CHAR_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.CHAR_ID FROM DUAL;
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

ALTER TRIGGER TRG_APIC_CHARACTERISTICS_INS ENABLE;
-------------------------------------------------------------
--  DDL for Trigger T_CHARACTERISTIC_VALUES - Before Insert
--- Icdm-954
-------------------------------------------------------------

CREATE OR REPLACE TRIGGER TRG_APIC_CHAR_VAL_INS
BEFORE INSERT ON T_CHARACTERISTIC_VALUES FOR EACH ROW
BEGIN
    IF :new.CHAR_VAL_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.CHAR_VAL_ID FROM DUAL;
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
ALTER TRIGGER TRG_APIC_CHAR_VAL_INS ENABLE;

spool off