spool c:\temp\20_Create_Trigger.log

---------------------------------------------------------------------
--  ALM Task : 708996 - Trigger for table to store Links for Rulesets
---------------------------------------------------------------------

--Insert Trigger
CREATE OR REPLACE TRIGGER TRG_RULE_LINKS_INS
  BEFORE INSERT ON T_RULE_LINKS FOR EACH ROW
BEGIN

    IF :new.RULE_LINK_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.RULE_LINK_ID FROM DUAL;
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


--Update Trigger
CREATE OR REPLACE TRIGGER TRG_RULE_LINKS_UPD
  BEFORE UPDATE ON T_RULE_LINKS
  FOR EACH ROW
BEGIN
    IF :new.Version = :old.Version THEN
        :new.Version := :old.Version + 1;
    END IF;

    if :new.modified_date IS NULL or NOT UPDATING('Modified_Date') then
        :new.modified_date := sys_extract_utc(systimestamp);
    end if;

    if :new.modified_user IS NULL then
        :new.modified_user := user;
    end if;

END;
/


------------------------------------------------------------------------------------------------------------------------------
---708988: Add New Columns in RuleSet Editor
------------------------------------------------------------------------------------------------------------------------------

-- Table : T_RULESET_PARAM_TYPE
-- Insert Trigger
CREATE OR REPLACE TRIGGER TRG_RULESET_PARAM_TYPE_INS
  BEFORE INSERT ON T_RULESET_PARAM_TYPE FOR EACH ROW
BEGIN

    IF :new.PARAM_TYPE_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.PARAM_TYPE_ID FROM DUAL;
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

-- Table : T_RULESET_PARAM_RESP
-- Insert Trigger
CREATE OR REPLACE TRIGGER TRG_RULESET_PARAM_RESP_INS
  BEFORE INSERT ON T_RULESET_PARAM_RESP FOR EACH ROW
BEGIN

    IF :new.PARAM_RESP_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.PARAM_RESP_ID FROM DUAL;
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

-- Table : T_RULESET_SYS_ELEMENT
-- Insert Trigger
CREATE OR REPLACE TRIGGER TRG_RULESET_SYS_ELEMENT_INS
  BEFORE INSERT ON T_RULESET_SYS_ELEMENT FOR EACH ROW
BEGIN

    IF :new.SYS_ELEMENT_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.SYS_ELEMENT_ID FROM DUAL;
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

-- Table : T_RULESET_HW_COMPONENT
-- Insert Trigger
CREATE OR REPLACE TRIGGER TRG_RULESET_HW_COMPONENT_INS
  BEFORE INSERT ON T_RULESET_HW_COMPONENT FOR EACH ROW
BEGIN

    IF :new.HW_COMPONENT_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.HW_COMPONENT_ID FROM DUAL;
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

-- Table : T_RULESET_PARAM_TYPE
-- Update Trigger
CREATE OR REPLACE TRIGGER TRG_RULESET_PARAM_TYPE_UPD
  BEFORE UPDATE ON T_RULESET_PARAM_TYPE
  FOR EACH ROW
BEGIN
    IF :new.Version = :old.Version THEN
        :new.Version := :old.Version + 1;
    END IF;

    if :new.modified_date IS NULL or NOT UPDATING('Modified_Date') then
        :new.modified_date := sys_extract_utc(systimestamp);
    end if;

    if :new.modified_user IS NULL then
        :new.modified_user := user;
    end if;

END;
/

-- Table : T_RULESET_PARAM_RESP
-- Update Trigger
CREATE OR REPLACE TRIGGER TRG_RULESET_PARAM_RESP_UPD
  BEFORE UPDATE ON T_RULESET_PARAM_RESP
  FOR EACH ROW
BEGIN
    IF :new.Version = :old.Version THEN
        :new.Version := :old.Version + 1;
    END IF;

    if :new.modified_date IS NULL or NOT UPDATING('Modified_Date') then
        :new.modified_date := sys_extract_utc(systimestamp);
    end if;

    if :new.modified_user IS NULL then
        :new.modified_user := user;
    end if;

END;
/

-- Table : T_RULESET_SYS_ELEMENT
-- Update Trigger
CREATE OR REPLACE TRIGGER TRG_RULESET_SYS_ELEMENT_UPD
  BEFORE UPDATE ON T_RULESET_SYS_ELEMENT
  FOR EACH ROW
BEGIN
    IF :new.Version = :old.Version THEN
        :new.Version := :old.Version + 1;
    END IF;

    if :new.modified_date IS NULL or NOT UPDATING('Modified_Date') then
        :new.modified_date := sys_extract_utc(systimestamp);
    end if;

    if :new.modified_user IS NULL then
        :new.modified_user := user;
    end if;

END;
/

-- Table : T_RULESET_HW_COMPONENT
-- Update Trigger
CREATE OR REPLACE TRIGGER TRG_RULESET_HW_COMPONENT_UPD
  BEFORE UPDATE ON T_RULESET_HW_COMPONENT
  FOR EACH ROW
BEGIN
    IF :new.Version = :old.Version THEN
        :new.Version := :old.Version + 1;
    END IF;

    if :new.modified_date IS NULL or NOT UPDATING('Modified_Date') then
        :new.modified_date := sys_extract_utc(systimestamp);
    end if;

    if :new.modified_user IS NULL then
        :new.modified_user := user;
    end if;

END;
/

spool off
