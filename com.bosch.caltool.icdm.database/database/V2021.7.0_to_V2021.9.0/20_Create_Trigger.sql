spool c:\temp\20_Create_Trigger.log

---------------------------------------------------------------------
--  ALM Task : 564953 - Trigger for table to store unicode remarks for Functions/Rulesets
---------------------------------------------------------------------

CREATE OR REPLACE TRIGGER TRG_RULE_REMARKS_INS
BEFORE INSERT ON T_RULE_REMARKS FOR EACH ROW
BEGIN

    IF :new.RULE_REMARK_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.RULE_REMARK_ID FROM DUAL;
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

---------------------------------------------------------------------  

CREATE OR REPLACE TRIGGER TRG_RULE_REMARKS_UPD
BEFORE UPDATE ON T_RULE_REMARKS
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

------------------------------------------------------------------------------------------------------------------
--  ALM TaskId : 611788: Impl: Review Rule Editor: Field 'Unit' should be editable
------------------------------------------------------------------------------------------------------------------

-- Create trigger TRG_T_UNITS_INS ---
create or replace TRIGGER TRG_T_UNITS_INS 
  BEFORE INSERT ON T_UNITS 
  FOR EACH ROW  
BEGIN
   IF :new.VERSION IS NULL THEN
        :new.VERSION := 1;
    END IF;
    
    IF :new.ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.ID FROM DUAL;
    END IF;
    
    IF :new.CREATED_DATE IS NULL then
        :new.CREATED_DATE := sys_extract_utc(systimestamp);
    END IF;   
    
END;
/


spool off