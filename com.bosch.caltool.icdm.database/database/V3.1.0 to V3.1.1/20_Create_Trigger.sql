spool 'C:\Temp\20_Create_Trigger.log'

------------------------------------------------------------------------------------------------------------------
-- ALM Task ID : 495894
--modified trigger to handle modified date 
------------------------------------------------------------------------------------------------------------------

CREATE OR REPLACE TRIGGER TRG_RVW_PARTICIPANTS_UPD
  BEFORE UPDATE ON T_RVW_PARTICIPANTS
  FOR EACH ROW
BEGIN
    IF :new.Version = :old.Version THEN
        :new.Version := :old.Version + 1;
    END IF;

    IF :new.MODIFIED_DATE IS NULL OR :new.MODIFIED_DATE = :old.MODIFIED_DATE THEN
        :new.Modified_Date := sys_extract_utc(systimestamp);
    END IF;

    IF :new.Modified_User is null THEN
        :new.Modified_User := user;
    END IF;

END;

--TRG_RVW_RESULTS_UPD
CREATE OR REPLACE TRIGGER TRG_RVW_RESULTS_UPD
  BEFORE UPDATE ON T_RVW_RESULTS
  FOR EACH ROW
BEGIN
    IF :new.Version = :old.Version THEN
        :new.Version := :old.Version + 1;
    END IF;

    IF :new.MODIFIED_DATE IS NULL OR :new.MODIFIED_DATE = :old.MODIFIED_DATE THEN
        :new.Modified_Date := sys_extract_utc(systimestamp);
    END IF;

    IF :new.Modified_User is null THEN
        :new.Modified_User := user;
    END IF;

END;

spool off
