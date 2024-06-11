spool c:\temp\create_trigger.log
--------------------------------------------------------
--  Oct-21-2013  
--------------------------------------------------------

---------------------------------------------------------------------
--  Trigger TRG_PIDC_DET_STRUCTURE_INS - Before insert on table TABV_PIDC_DET_STRUCTURE
---------------------------------------------------------------------


CREATE OR REPLACE TRG_PIDC_DET_STRUCTURE_INS
  BEFORE INSERT ON TABV_PIDC_DET_STRUCTURE
  FOR EACH ROW
BEGIN
    IF :new.PDS_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.PDS_ID FROM DUAL;
    END IF;

    IF :new.Version is null THEN
        :new.Version := 1;
    END IF;

    IF :new.Created_Date is null THEN
        :new.Created_Date := sysdate;
    END IF;

    IF :new.Created_User is null THEN
        :new.Created_User := user;
    END IF;


END;
/
ALTER TRIGGER TRG_PIDC_DET_STRUCTURE_INS ENABLE;

--------------------------------------------------------------------------
--  Trigger TRG_PIDC_DET_STRUCTURE_UPD - Before update on table TABV_PIDC_DET_STRUCTURE
--------------------------------------------------------------------------


CREATE OR REPLACE TRIGGER TRG_PIDC_DET_STRUCTURE_UPD
  BEFORE UPDATE ON TABV_PIDC_DET_STRUCTURE
  FOR EACH ROW
BEGIN
    IF :new.Version = :old.Version THEN
        :new.Version := :old.Version + 1;
    END IF;

    IF :new.Modified_Date is null THEN
        :new.Modified_Date := sysdate;
    END IF;

    IF :new.Modified_User is null THEN
        :new.Modified_User := user;
    END IF;

END;
/
ALTER TRIGGER TRG_PIDC_DET_STRUCTURE_UPD ENABLE;
