spool c:\temp\create_trigger.log


--------------------------------------------------------
--  DDL for Trigger T_MANDATORY_ATTR - Before INSERT
--- ICDM-1836
--------------------------------------------------------
CREATE OR REPLACE TRIGGER T_MANDATORY_ATTR_INS 
  BEFORE INSERT ON T_MANDATORY_ATTR FOR EACH ROW
Begin
    IF :new.MA_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.MA_ID FROM DUAL;
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
--------------------------------------------------------
--  DDL for Trigger T_ALIAS_DEFINITION - Before INSERT
--- ICDM-1844
--------------------------------------------------------
CREATE OR REPLACE TRIGGER T_ALIAS_DEFINITION_INS 
  BEFORE INSERT ON T_ALIAS_DEFINITION FOR EACH ROW
Begin
    IF :new.AD_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.AD_ID FROM DUAL;
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
--------------------------------------------------------
--  DDL for Trigger T_ALIAS_DETAILS - Before INSERT
--- ICDM-1844
--------------------------------------------------------
CREATE OR REPLACE TRIGGER T_ALIAS_DETAILS_INS 
  BEFORE INSERT ON T_ALIAS_DETAILS FOR EACH ROW
Begin
    IF :new.ALIAS_DETAILS_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.ALIAS_DETAILS_ID FROM DUAL;
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

spool off