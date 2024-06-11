spool c:\temp\20_Create_Trigger.log

------------------------------------------------------------------------------------------------------------------
--Task 749550: Impl: Azure SSO in iCDM Analysis and implementation Part - 1
------------------------------------------------------------------------------------------------------------------
CREATE OR REPLACE EDITIONABLE TRIGGER TRGR_USER_LOGIN_INFO_INS
  BEFORE INSERT ON T_USER_LOGIN_INFO
  FOR EACH ROW
Begin
    IF :new.USER_LOGIN_INFO_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.USER_LOGIN_INFO_ID FROM DUAL;
    END IF;
    
    IF :new.VERSION is null THEN
        :new.VERSION := 1;
    END IF;

    IF :new.CREATED_DATE is null THEN
        :new.CREATED_DATE := sys_extract_utc(systimestamp);
    END IF;

    IF :new.CREATED_USER is null THEN
        :new.CREATED_USER := user;
    END IF;

END;
/
ALTER TRIGGER TRGR_USER_LOGIN_INFO_INS ENABLE;

CREATE OR REPLACE EDITIONABLE TRIGGER TRGR_USER_LOGIN_INFO_UPD 
  BEFORE UPDATE ON T_USER_LOGIN_INFO
  FOR EACH ROW
BEGIN
    IF :new.VERSION = :old.VERSION THEN
        :new.VERSION := :old.VERSION + 1;
    END IF;

    if :new.MODIFIED_DATE IS NULL or NOT UPDATING('Modified_Date') then
        :new.MODIFIED_DATE := sys_extract_utc(systimestamp);
    end if;

    if :new.MODIFIED_USER IS NULL then
        :new.MODIFIED_USER := user;
    end if;
 
END;
/
ALTER TRIGGER TRGR_USER_LOGIN_INFO_UPD ENABLE;

------------------------------------------------------------------------------------------------------------------
--Task 716513: Impl : Number of decimals in check value of review result
------------------------------------------------------------------------------------------------------------------

-- Trigger for Insert & Update on table T_USER_PREFERENCES
CREATE OR REPLACE TRIGGER TRG_USER_PREFERENCES
  BEFORE INSERT ON T_USER_PREFERENCES FOR EACH ROW
BEGIN
    
    IF :new.Version is null THEN
        :new.Version := 1;
    END IF;

    IF :new.Created_Date is null THEN
        :new.Created_Date := sys_extract_utc(systimestamp);
    END IF;

    IF :new.Created_User is null THEN
        :new.Created_User := user;
    END IF;

    IF :new.USER_PREF_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.USER_PREF_ID FROM T_USER_PREFERENCES;
    END IF;
END;
/

ALTER TRIGGER TRG_USER_PREFERENCES ENABLE;

CREATE OR REPLACE TRIGGER TRG_USER_PREFERENCES_UPD BEFORE
    UPDATE ON T_USER_PREFERENCES
    FOR EACH ROW
BEGIN
    IF :new.version = :old.version THEN
        :new.version := :old.version + 1;
    END IF;

    IF :new.modified_date IS NULL OR NOT updating('Modified_Date') THEN
        :new.modified_date := sys_extract_utc(systimestamp);
    END IF;

    IF :new.modified_user IS NULL THEN
        :new.modified_user := user;
    END IF;

END;
/

ALTER TRIGGER TRG_USER_PREFERENCES_UPD ENABLE;

spool off
