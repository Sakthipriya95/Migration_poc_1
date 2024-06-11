spool c:\temp\create_trigger.log


--------------------------------------------------------
--  DDL for Trigger T_RULE_SET - Before Insert
--- ICDM-1364
--------------------------------------------------------

CREATE OR REPLACE TRIGGER TRG_T_RULE_SET_INS
BEFORE INSERT ON T_RULE_SET FOR EACH ROW
BEGIN
    IF :new.RSET_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.RSET_ID FROM DUAL;
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

ALTER TRIGGER TRG_T_RULE_SET_INS ENABLE;

--------------------------------------------------------
--  DDL for Trigger T_RULE_SET - Before update
--- ICDM-1364
--------------------------------------------------------

CREATE OR REPLACE TRIGGER TRG_T_RULE_SET_UPDT
BEFORE UPDATE ON T_RULE_SET
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

ALTER TRIGGER TRG_T_RULE_SET_UPDT ENABLE;

--------------------------------------------------------
--  DDL for Trigger T_RULE_SET_PARAMS - Before Insert
--- ICDM-1364
--------------------------------------------------------

CREATE OR REPLACE TRIGGER TRG_T_RULE_SET_PARAMS_INS
BEFORE INSERT ON T_RULE_SET_PARAMS FOR EACH ROW
BEGIN
    IF :new.RSET_PARAM_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.RSET_PARAM_ID FROM DUAL;
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

ALTER TRIGGER TRG_T_RULE_SET_PARAMS_INS ENABLE;

--------------------------------------------------------
--  DDL for Trigger T_RULE_SET_PARAMS - Before update
--- ICDM-1364
--------------------------------------------------------

CREATE OR REPLACE TRIGGER TRG_T_RULE_SET_PARAMS_UPDT
BEFORE UPDATE ON T_RULE_SET_PARAMS
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

ALTER TRIGGER TRG_T_RULE_SET_PARAMS_UPDT ENABLE;

--------------------------------------------------------
--  DDL for Trigger T_RULE_SET_PARAM_ATTR - Before Insert
--- ICDM-1364
--------------------------------------------------------


CREATE OR REPLACE TRIGGER TRG_T_RULE_SET_PARAM_ATTR_INS
BEFORE INSERT ON T_RULE_SET_PARAM_ATTR FOR EACH ROW
BEGIN
    IF :new.RSET_PAR_ATTR_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.RSET_PAR_ATTR_ID FROM DUAL;
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


ALTER TRIGGER TRG_T_RULE_SET_PARAM_ATTR_INS ENABLE;

--------------------------------------------------------
--  DDL for Trigger T_RULE_SET_PARAM_ATTR - Before update
--- ICDM-1364
--------------------------------------------------------

CREATE OR REPLACE TRIGGER TRG_T_RULE_SET_PARAM_ATTR_UPDT
BEFORE UPDATE ON T_RULE_SET_PARAM_ATTR
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

ALTER TRIGGER TRG_T_RULE_SET_PARAM_ATTR_UPDT ENABLE;

spool off
