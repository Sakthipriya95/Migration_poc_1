spool c:\temp\02_Create_Trigger_fc2wp.log

-------------------------------------------------
--Task 231531 Schema for FC2WP definition
-------------------------------------------------
CREATE OR REPLACE TRIGGER TRG_BASE_COMPONENTS_INS 
  BEFORE INSERT ON T_BASE_COMPONENTS
  FOR EACH ROW
Begin
    IF :new.BC_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.BC_ID FROM DUAL;
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

CREATE OR REPLACE TRIGGER TRG_BASE_COMPONENTS_UPDT 
  BEFORE UPDATE ON T_BASE_COMPONENTS
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

-------------------T_FC2WP_DEFINITION
CREATE OR REPLACE TRIGGER TRG_FC2WP_DEFINITION_INS 
  BEFORE INSERT ON T_FC2WP_DEFINITION
  FOR EACH ROW
Begin
    IF :new.FCWP_DEF_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.FCWP_DEF_ID FROM DUAL;
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

CREATE OR REPLACE TRIGGER TRG_FC2WP_DEFINITION_UPDT 
  BEFORE UPDATE ON T_FC2WP_DEFINITION
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

-------------------T_FC2WP_DEF_VERSION
CREATE OR REPLACE TRIGGER TRG_FC2WP_DEF_VERSION_INS 
  BEFORE INSERT ON T_FC2WP_DEF_VERSION
  FOR EACH ROW
Begin
    IF :new.FCWP_VER_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.FCWP_VER_ID FROM DUAL;
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

CREATE OR REPLACE TRIGGER TRG_FC2WP_DEF_VERSION_UPDT 
  BEFORE UPDATE ON T_FC2WP_DEF_VERSION
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

-------------------T_FC2WP_MAPPING
CREATE OR REPLACE TRIGGER TRG_FC2WP_MAPPING_INS 
  BEFORE INSERT ON T_FC2WP_MAPPING
  FOR EACH ROW
Begin
    IF :new.FCWP_MAP_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.FCWP_MAP_ID FROM DUAL;
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

CREATE OR REPLACE TRIGGER TRG_FC2WP_MAPPING_UPDT 
  BEFORE UPDATE ON T_FC2WP_MAPPING
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

-------------------T_FC2WP_PT_TYPE_RELV
CREATE OR REPLACE TRIGGER TRG_FC2WP_PT_TYPE_RELV_INS 
  BEFORE INSERT ON T_FC2WP_PT_TYPE_RELV
  FOR EACH ROW
Begin
    IF :new.FCWP_PT_TYPE_RELV_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.FCWP_PT_TYPE_RELV_ID FROM DUAL;
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

CREATE OR REPLACE TRIGGER TRG_FC2WP_PT_TYPE_RELV_UPDT 
  BEFORE UPDATE ON T_FC2WP_PT_TYPE_RELV
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

-------------------T_FC2WP_MAP_PT_TYPES
CREATE OR REPLACE TRIGGER TRG_FC2WP_MAP_PT_TYPES_INS 
  BEFORE INSERT ON T_FC2WP_MAP_PT_TYPES
  FOR EACH ROW
Begin
    IF :new.FCWP_MAP_PT_TYPE_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.FCWP_MAP_PT_TYPE_ID FROM DUAL;
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

CREATE OR REPLACE TRIGGER TRG_FC2WP_MAP_PT_TYPES_UPDT 
  BEFORE UPDATE ON T_FC2WP_MAP_PT_TYPES
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

-------------------T_POWER_TRAIN_TYPE
CREATE OR REPLACE TRIGGER TRG_POWER_TRAIN_TYPE_INS 
  BEFORE INSERT ON T_POWER_TRAIN_TYPE
  FOR EACH ROW
Begin
    IF :new.PT_TYPE_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.PT_TYPE_ID FROM DUAL;
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

CREATE OR REPLACE TRIGGER TRG_POWER_TRAIN_TYPE_UPDT 
  BEFORE UPDATE ON T_POWER_TRAIN_TYPE
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

-------------------T_WP_RESOURCE
CREATE OR REPLACE TRIGGER TRG_WP_RESOURCE_INS 
  BEFORE INSERT ON T_WP_RESOURCE
  FOR EACH ROW
Begin
    IF :new.WP_RES_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.WP_RES_ID FROM DUAL;
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

CREATE OR REPLACE TRIGGER TRG_WP_RESOURCE_UPDT 
  BEFORE UPDATE ON T_WP_RESOURCE
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

spool off

