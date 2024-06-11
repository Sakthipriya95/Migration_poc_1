spool c:\temp\create_trigger.log

-----------------------------------
--  2015-02-19
------------------------------------

--------------------------------------------------------
--  DDL for Trigger T_CP_RULE_ATTRS - Before Insert
--- ICDM-1292
--------------------------------------------------------

CREATE OR REPLACE TRIGGER TRG_CP_RULE_ATTRS_INS
BEFORE INSERT ON T_CP_RULE_ATTRS FOR EACH ROW
BEGIN
    IF :new.CP_RULE_ATTR_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.CP_RULE_ATTR_ID FROM DUAL;
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

ALTER TRIGGER TRG_CP_RULE_ATTRS_INS ENABLE;

--------------------------------------------------------
--  DDL for Trigger T_CP_RULE_ATTRS - Before update
--- ICDM-1292
--------------------------------------------------------

CREATE OR REPLACE TRIGGER TRG_CP_RULE_ATTRS_UPDT
BEFORE UPDATE ON T_CP_RULE_ATTRS
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

ALTER TRIGGER TRG_CP_RULE_ATTRS_UPDT ENABLE;

--------------------------------------------------------
--  DDL for Trigger T_MESSAGES - Before Insert
--- ICDM-1272
--------------------------------------------------------
CREATE OR REPLACE TRIGGER TRG_MESSAGE__INS
BEFORE INSERT ON T_MESSAGES FOR EACH ROW
BEGIN
    IF :new.MESSAGE_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.MESSAGE_ID FROM DUAL;
    END IF;

    IF :new.Version is null THEN
        :new.Version := 1;
    END IF;
END;
/
ALTER TRIGGER TRG_MESSAGE__INS ENABLE;

spool off
