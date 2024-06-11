spool c:\temp\20_Create_Trigger.log

---------------------------------------------------------------------
--  ALM Task : 649285 - impl : Server end and DB changes for adding users to Bosch Dept/Group
---------------------------------------------------------------------

CREATE OR REPLACE TRIGGER TRG_A2LRESP_BSHGRP_INS BEFORE INSERT ON T_A2L_RESPONSIBLITY_BSHGRP_USR 
FOR EACH ROW 
BEGIN
   IF :new.A2LRESP_BSHGRP_USR_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.A2LRESP_BSHGRP_USR_ID FROM DUAL;
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

ALTER TRIGGER TRG_A2LRESP_BSHGRP_INS ENABLE;

CREATE OR REPLACE TRIGGER TRG_A2LRESP_BSHGRP_UPD BEFORE UPDATE ON T_A2L_RESPONSIBLITY_BSHGRP_USR 
FOR EACH ROW 
BEGIN
    IF :new.Version = :old.Version THEN
        :new.Version := :old.Version + 1;
    END IF;

    if :new.MODIFIED_DATE IS NULL or NOT UPDATING('Modified_Date') then
        :new.Modified_Date := sys_extract_utc(systimestamp);
    END IF;

    IF :new.Modified_User is null THEN
        :new.Modified_User := user;
    END IF;
END;
/

ALTER TRIGGER TRG_A2LRESP_BSHGRP_UPD ENABLE;

---------------------------------------------------------------------
--  ALM TaskId : 646662: Database design for ‘Coc Workpackages’ page of PIDC Editor.
---------------------------------------------------------------------

CREATE OR REPLACE TRIGGER TRG_PIDC_VERS_COC_WP_INS 
  BEFORE INSERT ON T_PIDC_VERS_COC_WP
  FOR EACH ROW
Begin
    IF :new.PIDC_VERS_COC_WP_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.PIDC_VERS_COC_WP_ID FROM DUAL;
    END IF;
    
    IF :new.Version IS NULL THEN
        :new.Version  := 1;
    END IF;

    IF :new.CREATED_DATE is null THEN
        :new.CREATED_DATE := sys_extract_utc(systimestamp);
    END IF;

    IF :new.CREATED_USER is null THEN
        :new.CREATED_USER := user;
    END IF;

END;
/

ALTER TRIGGER TRG_PIDC_VERS_COC_WP_INS ENABLE;

CREATE OR REPLACE TRIGGER TRG_PIDC_VERS_COC_WP_UPD
  BEFORE UPDATE ON T_PIDC_VERS_COC_WP
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

ALTER TRIGGER TRG_PIDC_VERS_COC_WP_UPD ENABLE;

CREATE OR REPLACE TRIGGER TRG_PIDC_VAR_COC_WP_INS 
  BEFORE INSERT ON T_PIDC_VARIANT_COC_WP
  FOR EACH ROW
Begin
    IF :new.VAR_COC_WP_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.VAR_COC_WP_ID FROM DUAL;
    END IF;
    
    IF :new.Version IS NULL THEN
        :new.Version  := 1;
    END IF;

    IF :new.CREATED_DATE is null THEN
        :new.CREATED_DATE := sys_extract_utc(systimestamp);
    END IF;

    IF :new.CREATED_USER is null THEN
        :new.CREATED_USER := user;
    END IF;

END;
/

ALTER TRIGGER TRG_PIDC_VAR_COC_WP_INS ENABLE;

CREATE OR REPLACE TRIGGER TRG_PIDC_VAR_COC_WP_UPD
  BEFORE UPDATE ON T_PIDC_VARIANT_COC_WP
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

ALTER TRIGGER TRG_PIDC_VAR_COC_WP_UPD ENABLE;

CREATE OR REPLACE TRIGGER TRG_PIDC_SUB_VAR_COC_WP_INS 
  BEFORE INSERT ON T_PIDC_SUB_VAR_COC_WP
  FOR EACH ROW
Begin
    IF :new.SUB_VAR_COC_WP_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.SUB_VAR_COC_WP_ID FROM DUAL;
    END IF;
    
    IF :new.Version IS NULL THEN
        :new.Version  := 1;
    END IF;

    IF :new.CREATED_DATE is null THEN
        :new.CREATED_DATE := sys_extract_utc(systimestamp);
    END IF;

    IF :new.CREATED_USER is null THEN
        :new.CREATED_USER := user;
    END IF;

END;
/
    
ALTER TRIGGER TRG_PIDC_SUB_VAR_COC_WP_INS  ENABLE;

CREATE OR REPLACE TRIGGER TRG_PIDC_SUB_VAR_COC_WP_UPD
  BEFORE UPDATE ON T_PIDC_SUB_VAR_COC_WP
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

ALTER TRIGGER TRG_PIDC_SUB_VAR_COC_WP_UPD  ENABLE;


spool off
