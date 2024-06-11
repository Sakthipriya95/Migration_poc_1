spool c:\temp\create_trigger.log
-----------------------------------
---2014-10-15
------------------------------------

--------------------------------------------------------
--  DDL for Trigger T_USECASE_FAVORITES - Before Insert
--- ICDM-1026
--------------------------------------------------------

CREATE OR REPLACE TRIGGER TRG_UC_FAV_INS
BEFORE INSERT ON T_USECASE_FAVORITES FOR EACH ROW
BEGIN
    IF :new.UC_FAV_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.UC_FAV_ID FROM DUAL;
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

ALTER TRIGGER TRG_UC_FAV_INS ENABLE;

--------------------------------------------------------
--  DDL for Trigger T_USECASE_FAVORITES - Before update
--- ICDM-1026
--------------------------------------------------------

CREATE OR REPLACE TRIGGER TRG_UC_FAV_UPD
BEFORE UPDATE ON T_USECASE_FAVORITES
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

ALTER TRIGGER TRG_UC_FAV_UPD ENABLE;


-----------------------------------
---2014-10-15
------------------------------------

--------------------------------------------------------
--  DDL for Trigger T_PARAM_ATTRS - Before Insert
--- ICDM-1032
--------------------------------------------------------

CREATE OR REPLACE TRIGGER TRG_PARAM_ATTR_INS
BEFORE INSERT ON T_PARAM_ATTRS FOR EACH ROW
BEGIN
    IF :new.PARAM_ATTR_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.PARAM_ATTR_ID FROM DUAL;
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

ALTER TRIGGER TRG_PARAM_ATTR_INS ENABLE;

--------------------------------------------------------
--  DDL for Trigger T_PARAM_ATTRS - Before update
--- ICDM-1032
--------------------------------------------------------
CREATE OR REPLACE TRIGGER TRG_PARAM_ATTR_UPDT
BEFORE UPDATE ON T_PARAM_ATTRS
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

ALTER TRIGGER TRG_PARAM_ATTR_UPDT ENABLE;
spool off