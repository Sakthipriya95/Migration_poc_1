spool c:\temp\02_Create_Trigger.log
--------------------------------------------------------
-- DDL for Trigger T_QNAIRE_ANS_OPEN_POINTS - Before Insert
-- 
-- ICDM-2189
--------------------------------------------------------

CREATE OR REPLACE TRIGGER TRG_OPEN_POINTS_INS
  BEFORE INSERT ON T_QNAIRE_ANS_OPEN_POINTS
  FOR EACH ROW
Begin
    IF :new.OPEN_POINTS_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.OPEN_POINTS_ID FROM DUAL;
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

ALTER TRIGGER TRG_OPEN_POINTS_INS ENABLE;

--------------------------------------------------------
-- DDL for Trigger T_QNAIRE_ANS_OPEN_POINTS - Before Update
-- 
-- ICDM-2189
--------------------------------------------------------

CREATE OR REPLACE TRIGGER TRG_OPEN_POINTS_UPDT
BEFORE UPDATE ON T_QNAIRE_ANS_OPEN_POINTS
FOR EACH ROW
BEGIN
    IF :new.VERSION = :old.VERSION THEN
        :new.VERSION := :old.VERSION + 1;
    END IF;

    if :new.MODIFIED_DATE IS NULL then
        :new.MODIFIED_DATE := sys_extract_utc(systimestamp);
    end if;

    if :new.MODIFIED_USER IS NULL then
        :new.MODIFIED_USER := user;
    end if;
 
END;
/

ALTER TRIGGER TRG_OPEN_POINTS_UPDT ENABLE;

--------------------------------------------------------
-- DDL for Trigger T_GROUP_ATTR_VALUES - Before Insert
-- 
-- ICDM-2295
--------------------------------------------------------


 CREATE OR REPLACE TRIGGER TRG_GROUP_ATTR_VAL_INS
   BEFORE INSERT ON T_GROUP_ATTR_VALUES
   FOR EACH ROW
   Begin
    IF :new.GAVL_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.GAVL_ID FROM DUAL;
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

ALTER TRIGGER TRG_GROUP_ATTR_VAL_INS ENABLE;

--------------------------------------------------------
-- DDL for Trigger T_GROUP_ATTR_VALIDITY - Before Insert
-- 
-- ICDM-2295
--------------------------------------------------------
CREATE OR REPLACE TRIGGER TRG_GROUP_ATTR_VALIDITY_INS
   BEFORE INSERT ON T_GROUP_ATTR_VALIDITY
   FOR EACH ROW
   Begin
    IF :new.GAVD_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.GAVD_ID FROM DUAL;
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

ALTER TRIGGER TRG_GROUP_ATTR_VALIDITY_INS ENABLE;


--------------------------------------------------------
-- DDL for Trigger T_GROUP_ATTR_VALUES - Before Update
-- 
-- ICDM-2295
--------------------------------------------------------
CREATE OR REPLACE TRIGGER TRG_GROUP_ATTR_VAL_UPD 
  BEFORE UPDATE ON T_GROUP_ATTR_VALUES
  FOR EACH ROW
BEGIN
    IF :new.Version = :old.Version THEN
        :new.Version := :old.Version + 1;
    END IF;

    IF :new.Modified_Date is null THEN
        :new.Modified_Date := sys_extract_utc(systimestamp);
    END IF;

    IF :new.Modified_User is null THEN
        :new.Modified_User := user;
    END IF;
END;
/

ALTER TRIGGER TRG_GROUP_ATTR_VAL_UPD ENABLE;

--------------------------------------------------------
-- DDL for Trigger T_GROUP_ATTR_VALIDITY - Before Update
-- 
-- ICDM-2295
--------------------------------------------------------
CREATE OR REPLACE TRIGGER TRG_GROUP_ATTR_VALIDITY_UPD 
  BEFORE UPDATE ON T_GROUP_ATTR_VALIDITY
  FOR EACH ROW
BEGIN
    IF :new.Version = :old.Version THEN
        :new.Version := :old.Version + 1;
    END IF;

    IF :new.Modified_Date is null THEN
        :new.Modified_Date := sys_extract_utc(systimestamp);
    END IF;

    IF :new.Modified_User is null THEN
        :new.Modified_User := user;
    END IF;
END;
/

ALTER TRIGGER TRG_GROUP_ATTR_VALIDITY_UPD ENABLE;

--------------------------------------------------------
-- DDL for Trigger TabV_PROJECT_ATTR - Before Insert
-- 
-- ICDM-1659, ICDM-2278
--------------------------------------------------------

CREATE OR REPLACE TRIGGER TRG_PROJECT_ATTR_INS
BEFORE INSERT ON TabV_PROJECT_ATTR
FOR EACH ROW
DECLARE
     pidc_ver number;
     pidc_ver_ver number ;
     proj_id number;
BEGIN

 select pidc.VERSION , pidc.PROJECT_ID into  pidc_ver_ver,proj_id  from T_PIDC_VERSION pidc where pidc.PIDC_VERS_ID = :new.PIDC_VERS_ID;

 select proj.VERSION into pidc_ver from TABV_PROJECTIDCARD proj where proj.PROJECT_ID = proj_id;
 
    IF :new.PRJ_ATTR_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.PRJ_ATTR_ID FROM DUAL;
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


   insert into t_pidc_change_history
    (
     id,
     pidc_id,
     attr_id,
     pidc_vers_id,
     changed_date,
     changed_user,
     pidc_version,
     pidc_vers_vers,
     new_value_id,
     new_used,
     new_part_number,
     new_spec_link,
     new_description,
     new_is_variant,
     new_focus_matrix_yn,
     new_transfer_vcdm_yn
    )
  values
    (
     SeqV_Attributes.NEXTVAL,
     proj_id,
    :new.attr_id,
    :new.pidc_vers_id,
    :new.Created_Date,
    :new.Created_User,
    pidc_ver,
    pidc_ver_ver,
    :new.value_id,
    :new.used,
    :new.part_number,
    :new.spec_link,
    :new.description,
    :new.is_variant,
    :new.focus_matrix_yn,
    :new.trnsfr_vcdm_yn
     );

END;
/

ALTER TRIGGER TRG_PROJECT_ATTR_INS ENABLE;

--------------------------------------------------------
-- DDL for Trigger TabV_PROJECT_ATTR - Before Update
-- 
-- ICDM-1659, ICDM-2278
--------------------------------------------------------

CREATE or REPLACE TRIGGER TRG_PROJECT_ATTR_UPD 
BEFORE UPDATE ON TabV_PROJECT_ATTR
FOR EACH ROW
DECLARE
     pidc_ver number;
     pidc_ver_ver number ;
     proj_id number;
BEGIN
    IF :new.Version = :old.Version THEN
        :new.Version := :old.Version + 1;
    END IF;

    IF :new.Modified_Date is null THEN
        :new.Modified_Date := sys_extract_utc(systimestamp);
    END IF;

    IF :new.Modified_User is null THEN
        :new.Modified_User := user;
    END IF;

   select pidc.VERSION , pidc.PROJECT_ID into  pidc_ver_ver,proj_id  from T_PIDC_VERSION pidc where pidc.PIDC_VERS_ID = :new.PIDC_VERS_ID;
  
   select proj.VERSION into pidc_ver from TABV_PROJECTIDCARD proj where proj.PROJECT_ID = proj_id;

 insert into t_pidc_change_history
    (
     id,
     pidc_id,
     attr_id,
     pidc_vers_id,
     changed_date,
     changed_user,
     pidc_version,
      pidc_vers_vers,
     old_value_id,
     new_value_id,
     old_used,
     new_used,
     old_part_number,
     new_part_number,
     old_spec_link,
     new_spec_link,
     old_description,
     new_description,
     old_is_variant,
     new_is_variant,
     old_focus_matrix_yn,
     new_focus_matrix_yn,
     old_transfer_vcdm_yn,
     new_transfer_vcdm_yn
    )
  values
    (
     SeqV_Attributes.NEXTVAL,
    proj_id,
    :old.attr_id,
    :old.pidc_vers_id,
    :new.modified_date,
    :new.modified_user,
    pidc_ver,
     pidc_ver_ver,
    :old.value_id,
    :new.value_id,
    :old.used,
    :new.used,
    :old.part_number,
    :new.part_number,
    :old.spec_link,
    :new.spec_link,
    :old.description,
    :new.description,
    :old.is_variant,
    :new.is_variant,
    :old.focus_matrix_yn,
    :new.focus_matrix_yn,
    :old.trnsfr_vcdm_yn,
    :new.trnsfr_vcdm_yn
     );

END;
/

ALTER TRIGGER TRG_PROJECT_ATTR_UPD ENABLE;

spool off