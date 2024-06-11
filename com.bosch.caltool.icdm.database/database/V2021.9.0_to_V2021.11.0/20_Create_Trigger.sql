spool c:\temp\20_Create_Trigger.log

---------------------------------------------------------------------
--  ALM Task : 618045 - impl : Add context menu option 'Templates for Comments' to Review Results
---------------------------------------------------------------------

CREATE OR REPLACE TRIGGER TRG_RVW_COMMENT_TEMPLATE_INS
  BEFORE INSERT ON T_RVW_COMMENT_TEMPLATES   
  FOR EACH ROW
BEGIN
	
    IF :new.COMMENT_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.COMMENT_ID FROM DUAL;
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

ALTER TRIGGER TRG_RVW_COMMENT_TEMPLATE_INS ENABLE;

---------------------------------------------------------------------  

CREATE OR REPLACE TRIGGER TRG_RVW_COMMENT_TEMPLATE_UPD
  BEFORE UPDATE ON T_RVW_COMMENT_TEMPLATES   
  FOR EACH ROW
BEGIN
    IF :new.Version = :old.Version THEN
        :new.Version := :old.Version + 1;
    END IF;

    IF :new.Modified_Date is null or NOT UPDATING('Modified_Date') then
        :new.Modified_Date := sys_extract_utc(systimestamp);
    END IF;

    IF :new.Modified_User is null THEN
        :new.Modified_User := user;
    END IF;

END;
/

ALTER TRIGGER TRG_RVW_COMMENT_TEMPLATE_UPD ENABLE;

---------------------------------------------------------------------  


--------------------------------------------------------------------------------------------------------------------------------
--  ALM Task : 618047 - impl : Impl - Add additional context menu option 'Last comments' from which the user can select.
--------------------------------------------------------------------------------------------------------------------------------
--------------------------------------------------------------------------------------------------------------------------------
--ALM Task : 622652 - Impl - Review comment history - Improvements
--------------------------------------------------------------------------------------------------------------------------------
   
CREATE OR REPLACE TRIGGER TRG_RVW_CMNT_HISTORY_INS
  BEFORE INSERT ON T_RVW_USER_CMNT_HISTORY
  FOR EACH ROW
BEGIN

    IF :NEW.RVW_USER_CMNT_HISTORY_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :NEW.RVW_USER_CMNT_HISTORY_ID FROM DUAL;
    END IF;

    IF :NEW.VERSION is null THEN
        :NEW.VERSION := 1;
    END IF;

    IF :NEW.CREATED_DATE is null THEN
        :NEW.CREATED_DATE := sys_extract_utc(systimestamp);
    END IF;

    IF :NEW.CREATED_USER is null THEN
        :NEW.CREATED_USER := user;
    END IF;
END;
/

ALTER TRIGGER TRG_RVW_CMNT_HISTORY_INS ENABLE;

---------------------------------------------------

CREATE OR REPLACE TRIGGER TRG_RVW_CMNT_HISTORY_UPD
  BEFORE UPDATE ON T_RVW_USER_CMNT_HISTORY
  FOR EACH ROW
BEGIN
    IF :new.Version = :old.Version THEN
        :new.Version := :old.Version + 1;
    END IF;

    IF :new.Modified_Date is null or NOT UPDATING('Modified_Date') then
        :new.Modified_Date := sys_extract_utc(systimestamp);
    END IF;

    IF :new.Modified_User is null THEN
        :new.Modified_User := user;
    END IF;

END;
/

ALTER TRIGGER TRG_RVW_CMNT_HISTORY_UPD ENABLE;

spool off
