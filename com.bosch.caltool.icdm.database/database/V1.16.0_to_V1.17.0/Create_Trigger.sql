spool c:\temp\create_trigger.log
-----------------------------------
---2014-12-30
------------------------------------

--------------------------------------------------------
--  DDL for Trigger T_RVW_ATTR_VALUES - Before Insert
--- ICDM-1026
--------------------------------------------------------

CREATE OR REPLACE TRIGGER TRG_RVW_ATTR_VALUES_INS
BEFORE INSERT ON T_RVW_ATTR_VALUES FOR EACH ROW
BEGIN
    IF :new.RVW_ATTRVAL_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.RVW_ATTRVAL_ID FROM DUAL;
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

ALTER TRIGGER TRG_RVW_ATTR_VALUES_INS ENABLE;

--------------------------------------------------------
--  DDL for Trigger T_RVW_ATTR_VALUES - Before update
--- ICDM-1214
--------------------------------------------------------

CREATE OR REPLACE TRIGGER TRG_RVW_ATTR_VALUES_UPDT
BEFORE UPDATE ON T_RVW_ATTR_VALUES
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

--------------------------------------------------------
--  DDL for Trigger T_FC_GRP_WP_TYPE - Before Insert
--- ICDM-1179
--------------------------------------------------------

CREATE OR REPLACE TRIGGER TRG_FC_GRP_WP_TYPE_INS
BEFORE INSERT ON T_FC_GRP_WP_TYPE FOR EACH ROW
BEGIN
    IF :new.TYPE_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.TYPE_ID FROM DUAL;
    END IF;

    IF :new.Version is null THEN
        :new.Version := 1;
    END IF;
   
END;
/

ALTER TRIGGER TRG_FC_GRP_WP_TYPE_INS ENABLE;

--------------------------------------------------------
--  DDL for Trigger T_FC_GRP_WP_TYPE - Before Update
--- ICDM-1179
--------------------------------------------------------
CREATE OR REPLACE TRIGGER TRG_FC_GRP_WP_TYPE_UPDT
BEFORE UPDATE ON T_FC_GRP_WP_TYPE
FOR EACH ROW
BEGIN
    IF :new.Version = :old.Version THEN
        :new.Version := :old.Version + 1;
    END IF;
 
END;
/
ALTER TRIGGER TRG_FC_GRP_WP_TYPE_UPDT ENABLE;

spool off