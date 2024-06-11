spool c:\temp\02_Create_Trigger.log

-----------------------------------------------------------------------
--  ALM TaskId : 275697 : CODEX : Emission Robustness related triggers
-----------------------------------------------------------------------
-------------------------------------------------------------------
----------TRIGGER for table: t_emr_upload_error
-------------------------------------------------------------------

  CREATE OR REPLACE TRIGGER "TRG_T_emr_upload_error_UPDT" 
  BEFORE UPDATE ON t_emr_upload_error
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
ALTER TRIGGER "TRG_T_emr_upload_error_UPDT" ENABLE;

  CREATE OR REPLACE TRIGGER "TRG_T_emr_upload_error_INS" 
  BEFORE INSERT ON t_emr_upload_error
  FOR EACH ROW
Begin
    IF :new.error_id is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.error_id FROM DUAL;
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
ALTER TRIGGER "TRG_T_emr_upload_error_INS" ENABLE;

-------------------------------------------------------------------
-----TRIGGER for table: t_emr_pidc_variant
-------------------------------------------------------------------
  CREATE OR REPLACE TRIGGER "TRG_T_emr_pidc_variant_UPDT" 
  BEFORE UPDATE ON t_emr_pidc_variant
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
ALTER TRIGGER "TRG_T_emr_pidc_variant_UPDT" ENABLE;

  CREATE OR REPLACE TRIGGER "TRG_T_emr_pidc_variant_INS" 
  BEFORE INSERT ON t_emr_pidc_variant
  FOR EACH ROW
Begin
    IF :new.emr_pv_id is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.emr_pv_id FROM DUAL;
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
ALTER TRIGGER "TRG_T_emr_pidc_variant_INS" ENABLE;

-------------------------------------------------------------------
-----TRIGGER for table: t_emr_file
-------------------------------------------------------------------

  CREATE OR REPLACE TRIGGER "TRG_T_emr_file_UPDT" 
  BEFORE UPDATE ON t_emr_file
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
ALTER TRIGGER "TRG_T_emr_file_UPDT" ENABLE;

  CREATE OR REPLACE TRIGGER "TRG_T_emr_file_INS" 
  BEFORE INSERT ON t_emr_file
  FOR EACH ROW
Begin
    IF :new.emr_file_id is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.emr_file_id FROM DUAL;
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
ALTER TRIGGER "TRG_T_emr_file_INS" ENABLE;

-------------------------------------------------------------------
-----TRIGGER for table: t_emr_category
-------------------------------------------------------------------

  CREATE OR REPLACE TRIGGER "TRG_T_emr_category_UPDT" 
  BEFORE UPDATE ON t_emr_category
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
ALTER TRIGGER "TRG_T_emr_category_UPDT" ENABLE;

  CREATE OR REPLACE TRIGGER "TRG_T_emr_category_INS" 
  BEFORE INSERT ON t_emr_category
  FOR EACH ROW
Begin
    IF :new.cat_id is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.cat_id FROM DUAL;
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
ALTER TRIGGER "TRG_T_emr_category_INS" ENABLE;

-------------------------------------------------------------------
-----TRIGGER for table: t_emr_file_data
-------------------------------------------------------------------

  CREATE OR REPLACE TRIGGER "TRG_T_emr_file_data_UPDT" 
  BEFORE UPDATE ON t_emr_file_data
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
ALTER TRIGGER "TRG_T_emr_file_data_UPDT" ENABLE;

  CREATE OR REPLACE TRIGGER "TRG_T_emr_file_data_INS" 
  BEFORE INSERT ON t_emr_file_data
  FOR EACH ROW
Begin
    IF :new.file_data_id is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.file_data_id FROM DUAL;
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
ALTER TRIGGER "TRG_T_emr_file_data_INS" ENABLE;

-------------------------------------------------------------------
-----TRIGGER for table: t_emr_emission_standard
-------------------------------------------------------------------

  CREATE OR REPLACE TRIGGER "TRG_T_emr_ems_std_UPDT" 
  BEFORE UPDATE ON t_emr_emission_standard
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
ALTER TRIGGER "TRG_T_emr_ems_std_UPDT" ENABLE;

  CREATE OR REPLACE TRIGGER "TRG_T_emr_ems_std_INS" 
  BEFORE INSERT ON t_emr_emission_standard
  FOR EACH ROW
Begin
    IF :new.ems_id is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.ems_id FROM DUAL;
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
ALTER TRIGGER "TRG_T_emr_ems_std_INS" ENABLE;

-------------------------------------------------------------------
-----TRIGGER for table: t_emr_measure_unit
-------------------------------------------------------------------

  CREATE OR REPLACE TRIGGER "TRG_T_emr_measure_unit_UPDT" 
  BEFORE UPDATE ON t_emr_measure_unit
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
ALTER TRIGGER "TRG_T_emr_measure_unit_UPDT" ENABLE;

  CREATE OR REPLACE TRIGGER "TRG_T_emr_measure_unit_INS" 
  BEFORE INSERT ON t_emr_measure_unit
  FOR EACH ROW
Begin
    IF :new.mu_id is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.mu_id FROM DUAL;
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
ALTER TRIGGER "TRG_T_emr_measure_unit_INS" ENABLE;

-------------------------------------------------------------------
-----TRIGGER for table: t_emr_column
-------------------------------------------------------------------

  CREATE OR REPLACE TRIGGER "TRG_T_emr_column_UPDT" 
  BEFORE UPDATE ON t_emr_column
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
ALTER TRIGGER "TRG_T_emr_column_UPDT" ENABLE;

  CREATE OR REPLACE TRIGGER "TRG_T_emr_column_INS" 
  BEFORE INSERT ON t_emr_column
  FOR EACH ROW
Begin
    IF :new.col_id is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.col_id FROM DUAL;
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
ALTER TRIGGER "TRG_T_emr_column_INS" ENABLE;

-------------------------------------------------------------------
-----TRIGGER for table: t_emr_column_value
-------------------------------------------------------------------

  CREATE OR REPLACE TRIGGER "TRG_T_emr_column_value_UPDT" 
  BEFORE UPDATE ON t_emr_column_value
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
ALTER TRIGGER "TRG_T_emr_column_value_UPDT" ENABLE;

  CREATE OR REPLACE TRIGGER "TRG_T_emr_column_value_INS" 
  BEFORE INSERT ON t_emr_column_value
  FOR EACH ROW
Begin
    IF :new.col_value_id is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.col_value_id FROM DUAL;
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
ALTER TRIGGER "TRG_T_emr_column_value_INS" ENABLE;

-------------------------------------------------------------------
-----TRIGGER for table: t_emr_excel_mapping
-------------------------------------------------------------------
  CREATE OR REPLACE TRIGGER "TRG_T_emr_excel_mapping_UPDT" 
  BEFORE UPDATE ON t_emr_excel_mapping
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
ALTER TRIGGER "TRG_T_emr_excel_mapping_UPDT" ENABLE;

  CREATE OR REPLACE TRIGGER "TRG_T_emr_excel_mapping_INS" 
  BEFORE INSERT ON t_emr_excel_mapping
  FOR EACH ROW
Begin
    IF :new.excel_mapping_Id is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.excel_mapping_Id FROM DUAL;
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
ALTER TRIGGER "TRG_T_emr_excel_mapping_INS" ENABLE;

-------------------------------------------------------------------
----------TRIGGER for table: T_COMPLI_RVW_A2L
-------------------------------------------------------------------

  CREATE OR REPLACE TRIGGER "TRG_T_COMPLI_RVW_A2L_UPDT" 
  BEFORE UPDATE ON T_COMPLI_RVW_A2L
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
ALTER TRIGGER "TRG_T_COMPLI_RVW_A2L_UPDT" ENABLE;

 CREATE OR REPLACE TRIGGER "TRG_T_COMPLI_RVW_A2L_INS" 
  BEFORE INSERT ON T_COMPLI_RVW_A2L
  FOR EACH ROW
Begin
    IF :new.RESULT_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.RESULT_ID FROM DUAL;
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
ALTER TRIGGER "TRG_T_COMPLI_RVW_A2L_INS" ENABLE;
-------------------------------------------------------------------
----------TRIGGER for table: T_COMPLI_RVW_HEX
-------------------------------------------------------------------

  CREATE OR REPLACE TRIGGER "TRG_T_COMPLI_RVW_HEX_UPDT" 
  BEFORE UPDATE ON T_COMPLI_RVW_HEX
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
ALTER TRIGGER "TRG_T_COMPLI_RVW_HEX_UPDT" ENABLE;

  CREATE OR REPLACE TRIGGER "TRG_T_COMPLI_RVW_HEX_INS" 
  BEFORE INSERT ON T_COMPLI_RVW_HEX
  FOR EACH ROW
Begin
    IF :new.COMPLI_RVW_HEX_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.COMPLI_RVW_HEX_ID FROM DUAL;
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
ALTER TRIGGER "TRG_T_COMPLI_RVW_HEX_INS" ENABLE;




spool off