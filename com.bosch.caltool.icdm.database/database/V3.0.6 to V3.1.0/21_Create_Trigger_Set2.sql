spool 'C:\Temp\21_Create_Trigger_Set2.log'

-------------------------------------------
-- Only modified_date updates
-- Task 462615
-------------------------------------------


--TRG_AM_T_SSD_VALUES_UPDATE
create or replace TRIGGER TRG_AM_T_SSD_VALUES_UPDATE 
  BEFORE UPDATE
  ON T_SSD_VALUES for each row
BEGIN
  if :new.MODIFIED_DATE IS NULL or NOT UPDATING('Modified_Date') then
        :new.MODIFIED_DATE := sys_extract_utc(systimestamp);
    end if;

    if :new.MODIFIED_USER IS NULL then
        :new.MODIFIED_USER := user;
    end if;
  
  if (:NEW.Version = :OLD.Version) then
    :NEW.Version := :OLD.Version + 1;
  end if;  
END;
/

--TRG_APIC_ACCESS_RIGHTS_UPD
create or replace TRIGGER TRG_APIC_ACCESS_RIGHTS_UPD
  BEFORE UPDATE ON TabV_APIC_ACCESS_RIGHTS
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


--TRG_APIC_CHAR_VAL_UPD
create or replace TRIGGER TRG_APIC_CHAR_VAL_UPD
  BEFORE UPDATE ON T_CHARACTERISTIC_VALUES
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

--TRG_APIC_CHARACTERISTICS_UPD
create or replace TRIGGER TRG_APIC_CHARACTERISTICS_UPD
  BEFORE UPDATE ON T_CHARACTERISTICS
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

--TRG_APIC_NODE_ACC_UPD
create or replace TRIGGER TRG_APIC_NODE_ACC_UPD
  BEFORE UPDATE ON TabV_APIC_NODE_ACCESS
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

--TRG_ATTR_GROUPS_UPD
create or replace TRIGGER TRG_ATTR_GROUPS_UPD
  BEFORE UPDATE ON TabV_ATTR_GROUPS
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

   insert into tabv_attr_history
    (
     id,
     table_name,
     group_id,
     name_eng,
     name_ger,
     desc_eng,
     desc_ger,
     super_group_id,
     created_date,
     modified_date,
     created_user,
     modified_user,
     deleted_flag,
     operation_flag
    )
  values
    (
     SEQV_ATTRIBUTES.NEXTVAL,
     'tabv_attr_groups',
     :old.group_id,
     :old.group_name_eng,
     :old.group_name_ger,
     :old.group_desc_eng,
     :old.group_desc_ger,
     :old.super_group_id,
     :old.created_date,
     :old.modified_date,
     :old.created_user,
     :old.modified_user,
     :old.deleted_flag,
     'M'
    );

END;
/

--TRG_ATTR_SUPER_GROUPS_UPD
create or replace TRIGGER TRG_ATTR_SUPER_GROUPS_UPD
  BEFORE UPDATE ON TabV_ATTR_SUPER_GROUPS
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

   insert into tabv_attr_history
    (
     id,
     table_name,
     super_group_id,
     name_eng,
     name_ger,
     desc_eng,
     desc_ger,
     created_date,
     modified_date,
     created_user,
     modified_user,
     deleted_flag,
     operation_flag
    )
  values
    (
     SEQV_ATTRIBUTES.NEXTVAL,
     'tabv_attr_super_groups',
     :old.super_group_id,
     :old.super_group_name_eng,
     :old.super_group_name_ger,
     :old.super_group_desc_eng,
     :old.super_group_desc_ger,
     :old.created_date,
     :old.modified_date,
     :old.created_user,
     :old.modified_user,
     :old.deleted_flag,
     'M'
     );
END;
/


--TRG_COMP_PKG_UPD
create or replace TRIGGER TRG_COMP_PKG_UPD
  BEFORE UPDATE ON T_COMP_PKG FOR EACH ROW
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

--TRG_CP_RULE_ATTRS_UPDT
create or replace TRIGGER TRG_CP_RULE_ATTRS_UPDT
  BEFORE UPDATE ON T_CP_RULE_ATTRS
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

--TRG_ICDM_FILES_UPD
create or replace TRIGGER TRG_ICDM_FILES_UPD
  BEFORE UPDATE ON TABV_ICDM_FILES
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


--TRG_LINKS_UPD
create or replace TRIGGER TRG_LINKS_UPD
  BEFORE UPDATE ON T_LINKS   FOR EACH ROW
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


--TRG_OPEN_POINTS_UPDT
create or replace TRIGGER TRG_OPEN_POINTS_UPDT
BEFORE UPDATE ON T_RVW_QNAIRE_ANSWER_OPL
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

--TRG_PARAM_ATTR_UPDT
create or replace TRIGGER TRG_PARAM_ATTR_UPDT
BEFORE UPDATE ON T_PARAM_ATTRS
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

--TRG_PIDC_DET_STRUCTURE_UPD
create or replace TRIGGER TRG_PIDC_DET_STRUCTURE_UPD
  BEFORE UPDATE ON TABV_PIDC_DET_STRUCTURE
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

--TRG_PIDC_RM_DEFINITION_UPD
create or replace TRIGGER TRG_PIDC_RM_DEFINITION_UPD 
BEFORE UPDATE ON T_PIDC_RM_DEFINITION
FOR EACH ROW
BEGIN
    IF :NEW.VERSION = :OLD.VERSION THEN
        :NEW.VERSION := :OLD.VERSION + 1;
    END IF;

    IF :NEW.MODIFIED_DATE IS NULL OR NOT UPDATING('Modified_Date') THEN
        :NEW.MODIFIED_DATE := SYS_EXTRACT_UTC(SYSTIMESTAMP);
    END IF;

    IF :NEW.MODIFIED_USER IS NULL THEN 
        :NEW.MODIFIED_USER := USER;
    END IF;
END;
/

--TRG_PIDC_RM_PROJECCHARACTE_UPD
create or replace TRIGGER TRG_PIDC_RM_PROJECCHARACTE_UPD 
BEFORE UPDATE ON T_PIDC_RM_PROJECT_CHARACTER
FOR EACH ROW
BEGIN
    IF :NEW.VERSION = :OLD.VERSION THEN
        :NEW.VERSION := :OLD.VERSION + 1;
    END IF;

    IF :NEW.MODIFIED_DATE IS NULL OR NOT UPDATING('Modified_Date') THEN
        :NEW.MODIFIED_DATE := SYS_EXTRACT_UTC(SYSTIMESTAMP);
    END IF;

    IF :NEW.MODIFIED_USER IS NULL THEN 
        :NEW.MODIFIED_USER := USER;
    END IF;
END;
/

--TRG_PIDC_VERSION_UPD
create or replace TRIGGER TRG_PIDC_VERSION_UPD
BEFORE UPDATE ON T_PIDC_VERSION
FOR EACH ROW
DECLARE
     pidc_ver number;
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

    select proj.VERSION into pidc_ver from TABV_PROJECTIDCARD proj where proj.PROJECT_ID = :new.PROJECT_ID;

    insert into t_pidc_change_history
    (
     id,
     pidc_id,
     pidc_vers_id,
     changed_date,
     changed_user,
     pidc_version,
     pidc_vers_vers,
     old_value_desc_eng,
     new_value_desc_eng,
     old_value_desc_ger,
     new_value_desc_ger,
     OLD_TEXTVALUE_ENG,
     NEW_TEXTVALUE_ENG,
     old_deleted_flag,
     new_deleted_flag
    )
  values
    (
     SeqV_Attributes.nextval,
     :new.project_id,
     :new.pidc_vers_id,
     :new.modified_date,
     :new.modified_user,
     pidc_ver,
     :new.version,
     :old.vers_desc_eng,
     :new.vers_desc_eng,
     :old.vers_desc_ger,
      :new.vers_desc_ger,
     :old.vers_name,
       :new.vers_name,
     :old.deleted_flag,
     :new.deleted_flag
     );
END;
/



--TRG_PREDEFINED_ATTR_VAL_UPD
create or replace TRIGGER TRG_PREDEFINED_ATTR_VAL_UPD 
  BEFORE UPDATE ON T_PREDEFINED_ATTR_VALUES
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


--TRG_PROJ_SUB_VAR_ATTR_UPD
create or replace TRIGGER TRG_PROJ_SUB_VAR_ATTR_UPD
BEFORE UPDATE ON TABV_PROJ_SUB_VARIANTS_ATTR
FOR EACH ROW
DECLARE
     pidc_ver number;
     pidc_ver_ver number ;
     proj_id number;
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

     select pidc.VERSION , pidc.PROJECT_ID into pidc_ver_ver,proj_id  from T_PIDC_VERSION pidc where pidc.PIDC_VERS_ID = :new.PIDC_VERS_ID;

    select proj.VERSION into pidc_ver from TABV_PROJECTIDCARD proj where proj.PROJECT_ID = proj_id;
   
    insert into t_pidc_change_history
    (
     id,
     var_id,
     svar_id,
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
     new_is_variant
    )
  values
    (
     SeqV_Attributes.NEXTVAL,
    :old.variant_id,
    :old.sub_variant_id,
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
    'Y',
    'Y'
     );

END;
/


--TRG_PROJECT_ATTR_UPD
create or replace TRIGGER TRG_PROJECT_ATTR_UPD 
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

    IF :new.Modified_Date is null or NOT UPDATING('Modified_Date') then
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


--TRG_PROJECT_SUB_VARIANTS_UPD
create or replace TRIGGER TRG_PROJECT_SUB_VARIANTS_UPD
BEFORE UPDATE ON TabV_PROJECT_SUB_VARIANTS
FOR EACH ROW
DECLARE
     pidc_ver number;
     pidc_ver_ver number ;
     proj_id number;
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

     select pidc.VERSION , pidc.PROJECT_ID into pidc_ver_ver,proj_id  from T_PIDC_VERSION pidc where pidc.PIDC_VERS_ID = :new.PIDC_VERS_ID;

    select proj.VERSION into pidc_ver from TABV_PROJECTIDCARD proj where proj.PROJECT_ID = proj_id;

    insert into t_pidc_change_history
    (
     id,
     pidc_id,
     VAR_ID,
     SVAR_ID,
     pidc_vers_id,
     changed_date,
     changed_user,
     pidc_version,
      pidc_vers_vers,
     old_value_id,
     new_value_id,
     OLD_DELETED_FLAG,
     NEW_DELETED_FLAG
    )
  values
    (
     SeqV_Attributes.NEXTVAL,
    proj_id,
    :old.VARIANT_ID,
    :old.SUB_VARIANT_ID,
    :old.pidc_vers_id,
    :new.modified_date,
    :new.modified_user,
    pidc_ver,
     pidc_ver_ver,
    :old.value_id,
    :new.value_id,
    :old.DELETED_FLAG,
    :new.DELETED_FLAG
     );
END;
/

--TRG_PROJECT_VARIANTS_UPD
create or replace TRIGGER TRG_PROJECT_VARIANTS_UPD
BEFORE UPDATE ON TabV_PROJECT_VARIANTS
FOR EACH ROW
DECLARE
     pidc_ver number;
     pidc_ver_ver number ;
     proj_id number;
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


  select pidc.VERSION , pidc.PROJECT_ID into pidc_ver_ver,proj_id  from T_PIDC_VERSION pidc where pidc.PIDC_VERS_ID = :new.PIDC_VERS_ID;

  select proj.VERSION into pidc_ver from TABV_PROJECTIDCARD proj where proj.PROJECT_ID = proj_id; 

    insert into t_pidc_change_history
    (
     id,
     pidc_id,
     VAR_ID,
      pidc_vers_id,
     changed_date,
     changed_user,
     pidc_version,
      pidc_vers_vers,
     old_value_id,
     new_value_id,
     OLD_DELETED_FLAG,
     NEW_DELETED_FLAG
    )
  values
    (
     SeqV_Attributes.NEXTVAL,
    proj_id,
    :old.VARIANT_ID,
    :new.pidc_vers_id,
    :new.modified_date,
    :new.modified_user,
    pidc_ver,
     pidc_ver_ver,
    :old.value_id,
    :new.value_id,
    :old.DELETED_FLAG,
    :new.DELETED_FLAG
     );
END;
/


--TRG_PROJECTIDCARD_UPD
create or replace TRIGGER TRG_PROJECTIDCARD_UPD
BEFORE UPDATE ON TabV_PROJECTIDCARD
FOR EACH ROW
DECLARE
  new_pidc_ver_id number ;
  old_pidc_ver_id number;
     pidc_ver_ver number;
     old_pro_rev_id number;     
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

    IF :new.pro_rev_id <> :old.pro_rev_id THEN  
     select pidc.VERSION , pidc.PIDC_VERS_ID into pidc_ver_ver,new_pidc_ver_id  from T_PIDC_VERSION pidc where pidc.PROJECT_ID = :new.project_id and pidc.PRO_REV_ID= :new.pro_rev_id;
     
     select pidc.PIDC_VERS_ID into old_pidc_ver_id from T_PIDC_VERSION pidc where pidc.PROJECT_ID = :new.project_id and pidc.PRO_REV_ID= :old.pro_rev_id;
   
     insert into t_pidc_change_history
    (
     id,
     pidc_id,
     pidc_vers_id,
     changed_date,
     changed_user,
     pidc_version,
     pidc_vers_vers,
     old_value_id,
     new_value_id,
     old_active_status,
     new_active_status
    )
  values
    (
     SeqV_Attributes.nextval,
     :new.project_id,
      new_pidc_ver_id,
     :new.modified_date,
     :new.modified_user,
     :new.version,   
     pidc_ver_ver,
     :old.value_id,
     :new.value_id,
     'N',
     'Y'
     );
     
    insert into t_pidc_change_history
    (
     id,
     pidc_id,
     pidc_vers_id,
     changed_date,
     changed_user,
     pidc_version,
     pidc_vers_vers,
     old_value_id,
     new_value_id,
     old_active_status,
     new_active_status
    )
  values
    (
     SeqV_Attributes.nextval,
     :new.project_id,
      old_pidc_ver_id,
     :new.modified_date,
     :new.modified_user,
     :new.version,   
     pidc_ver_ver,
     :old.value_id,
     :new.value_id,
     'Y',
     'N'
     );

   END IF;
     
     
    IF ((:new.VCDM_TRANSFER_DATE <> :old.VCDM_TRANSFER_DATE) or (:old.VCDM_TRANSFER_DATE is null and :new.VCDM_TRANSFER_DATE is not null)) THEN 
      select pidc.VERSION , pidc.PIDC_VERS_ID into pidc_ver_ver,new_pidc_ver_id  from T_PIDC_VERSION pidc where pidc.PROJECT_ID = :new.project_id and pidc.PRO_REV_ID= :new.pro_rev_id;
      insert into t_pidc_change_history
      (
      id,
      pidc_id,
      pidc_vers_id,
      changed_date,
      changed_user,
      pidc_version,
      pidc_vers_vers,
      old_value_id,
      new_value_id,
      old_aprj_id,
      new_aprj_id,
      pidc_action
      )
      values
      (
      SeqV_Attributes.nextval,
      :new.project_id,
       new_pidc_ver_id,
      :new.modified_date,
      :new.modified_user,
      :new.version,   
      pidc_ver_ver,
      :old.value_id,
      :new.value_id,
      :old.aprj_id,
      :new.aprj_id,
      'VCDM Transfer'
      );
    
    END IF;
END;
/

--TRG_QUES_DEP_ATTR_VALS_UPDT
create or replace TRIGGER TRG_QUES_DEP_ATTR_VALS_UPDT
BEFORE UPDATE ON T_QUESTION_DEPEN_ATTR_VALUES
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

--TRG_QUESTION_CONFIG_UPDT
create or replace TRIGGER TRG_QUESTION_CONFIG_UPDT
BEFORE UPDATE ON T_QUESTION_CONFIG
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

--TRG_QUESTION_DEPEN_ATTR_UPDT
create or replace TRIGGER TRG_QUESTION_DEPEN_ATTR_UPDT
BEFORE UPDATE ON T_QUESTION_DEPEN_ATTRIBUTES
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

--TRG_QUESTION_UPDT
create or replace TRIGGER TRG_QUESTION_UPDT
  BEFORE UPDATE ON T_QUESTION
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

--TRG_QUESTIONNAIRE_UPDT
create or replace TRIGGER TRG_QUESTIONNAIRE_UPDT
  BEFORE UPDATE ON T_QUESTIONNAIRE
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

--TRG_QUESTIONNAIRE_VERS_UPDT
create or replace TRIGGER TRG_QUESTIONNAIRE_VERS_UPDT
  BEFORE UPDATE ON T_QUESTIONNAIRE_VERSION
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


--TRG_RM_CATEGORY_MEASURES_UPD
create or replace TRIGGER TRG_RM_CATEGORY_MEASURES_UPD 
  BEFORE UPDATE ON T_RM_CATEGORY_MEASURES
  FOR EACH ROW
BEGIN
    IF :NEW.VERSION = :OLD.VERSION THEN
        :NEW.VERSION := :OLD.VERSION + 1;
    END IF;

    IF :NEW.MODIFIED_DATE IS NULL OR NOT UPDATING('Modified_Date') THEN
        :NEW.MODIFIED_DATE := SYS_EXTRACT_UTC(SYSTIMESTAMP);
    END IF;

    IF :NEW.MODIFIED_USER IS NULL THEN 
        :NEW.MODIFIED_USER := USER;
    END IF;
END;
/


--TRG_RM_CATEGORY_UPD
create or replace TRIGGER TRG_RM_CATEGORY_UPD 
  BEFORE UPDATE ON T_RM_CATEGORY
  FOR EACH ROW
BEGIN
    IF :NEW.VERSION = :OLD.VERSION THEN
        :NEW.VERSION := :OLD.VERSION + 1;
    END IF;

    IF :NEW.MODIFIED_DATE IS NULL OR NOT UPDATING('Modified_Date') THEN
        :NEW.MODIFIED_DATE := SYS_EXTRACT_UTC(SYSTIMESTAMP);
    END IF;

    IF :NEW.MODIFIED_USER IS NULL THEN 
        :NEW.MODIFIED_USER := USER;
    END IF;
END;
/


--TRG_RM_CHARACTER_CATEGORY_UPD
create or replace TRIGGER TRG_RM_CHARACTER_CATEGORY_UPD 
  BEFORE UPDATE ON T_RM_CHARACTER_CATEGORY_MATRIX
  FOR EACH ROW
BEGIN
    IF :NEW.VERSION = :OLD.VERSION THEN
        :NEW.VERSION := :OLD.VERSION + 1;
    END IF;

    IF :NEW.MODIFIED_DATE IS NULL OR NOT UPDATING('Modified_Date') THEN
        :NEW.MODIFIED_DATE := SYS_EXTRACT_UTC(SYSTIMESTAMP);
    END IF;

    IF :NEW.MODIFIED_USER IS NULL THEN 
        :NEW.MODIFIED_USER := USER;
    END IF;
END;
/

--TRG_RM_PROJECT_CHARACTER_UPD
create or replace TRIGGER TRG_RM_PROJECT_CHARACTER_UPD 
BEFORE UPDATE ON T_RM_PROJECT_CHARACTER
FOR EACH ROW
BEGIN
    IF :NEW.VERSION = :OLD.VERSION THEN
        :NEW.VERSION := :OLD.VERSION + 1;
    END IF;

    IF :NEW.MODIFIED_DATE IS NULL OR NOT UPDATING('Modified_Date') THEN
        :NEW.MODIFIED_DATE := SYS_EXTRACT_UTC(SYSTIMESTAMP);
    END IF;

    IF :NEW.MODIFIED_USER IS NULL THEN 
        :NEW.MODIFIED_USER := USER;
    END IF;
END;
/


--TRG_RM_RISK_LEVEL_UPD
create or replace TRIGGER TRG_RM_RISK_LEVEL_UPD 
BEFORE UPDATE ON T_RM_RISK_LEVEL
FOR EACH ROW
BEGIN
    IF :NEW.VERSION = :OLD.VERSION THEN
        :NEW.VERSION := :OLD.VERSION + 1;
    END IF;

    IF :NEW.MODIFIED_DATE IS NULL OR NOT UPDATING('Modified_Date') THEN
        :NEW.MODIFIED_DATE := SYS_EXTRACT_UTC(SYSTIMESTAMP);
    END IF;

    IF :NEW.MODIFIED_USER IS NULL THEN 
        :NEW.MODIFIED_USER := USER;
    END IF;
END;
/


--TRG_RVW_ATTR_VALUES_UPDT
create or replace TRIGGER TRG_RVW_ATTR_VALUES_UPDT
BEFORE UPDATE ON T_RVW_ATTR_VALUES
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

--TRG_RVW_FILES_UPD
create or replace TRIGGER TRG_RVW_FILES_UPD
  BEFORE UPDATE ON T_RVW_FILES
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

--TRG_RVW_FUNCTIONS_UPD
create or replace TRIGGER TRG_RVW_FUNCTIONS_UPD
  BEFORE UPDATE ON T_RVW_FUNCTIONS
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


--TRG_RVW_PARAMETERS_UPD
create or replace TRIGGER TRG_RVW_PARAMETERS_UPD
  BEFORE UPDATE ON T_RVW_PARAMETERS
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

--TRG_RVW_PARTICIPANTS_UPD
create or replace TRIGGER TRG_RVW_PARTICIPANTS_UPD
  BEFORE UPDATE ON T_RVW_PARTICIPANTS
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

--TRG_RVW_RESULTS_UPD
create or replace TRIGGER TRG_RVW_RESULTS_UPD
  BEFORE UPDATE ON T_RVW_RESULTS
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


--TRG_T_COMP_PKG_BC_FC_UPD
create or replace TRIGGER TRG_T_COMP_PKG_BC_FC_UPD
  BEFORE UPDATE ON T_COMP_PKG_BC_FC FOR EACH ROW
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

--TRG_T_COMP_PKG_BC_UPD
create or replace TRIGGER TRG_T_COMP_PKG_BC_UPD
  BEFORE UPDATE ON T_COMP_PKG_BC FOR EACH ROW
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


--TRG_T_PIDC_A2L_UPDT
create or replace TRIGGER TRG_T_PIDC_A2L_UPDT
  BEFORE UPDATE ON T_PIDC_A2L
  FOR EACH ROW

DECLARE
   pidc_ver number;
   old_pidc_ver_ver number ;
   new_pidc_ver_ver number ;

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

    -- PIDC History Entry
    begin
        -- when an a2l file is unmapped
        IF :new.PIDC_VERS_ID is null THEN
            new_pidc_ver_ver := null;
            if :old.pidc_vers_id is not null THEN
                select pidc.VERSION  into old_pidc_ver_ver  from T_PIDC_VERSION pidc where pidc.PIDC_VERS_ID = :old.PIDC_VERS_ID;
            end if;

        --when an a2l is re-mapped
        else
            IF :new.pidc_vers_id is not null THEN
                select pidc.VERSION  into new_pidc_ver_ver  from T_PIDC_VERSION pidc where pidc.PIDC_VERS_ID = :new.PIDC_VERS_ID;
            end if;
            IF :old.pidc_vers_id is not null THEN
               select pidc.VERSION  into old_pidc_ver_ver  from T_PIDC_VERSION pidc where pidc.PIDC_VERS_ID = :old.PIDC_VERS_ID;
            END IF;
        END IF;


    select proj.VERSION into pidc_ver from TABV_PROJECTIDCARD proj where proj.PROJECT_ID = :new.PROJECT_ID;

    IF :old.PIDC_VERS_ID is not null THEN
        insert into t_pidc_change_history
            (
              id,
             pidc_id,
             pidc_vers_id,  
              changed_date,
             changed_user,
             pidc_version,
             pidc_vers_vers,
            old_pidc_a2l_id,
            new_pidc_a2l_id
            )
          values
            (
             SeqV_Attributes.nextval,
             :new.project_id,
             :old.pidc_vers_id,
             :new.modified_date,
             :new.modified_user,
             pidc_ver,
             old_pidc_ver_ver,
             :old.pidc_a2l_id,
             null   
             );
    END IF;
    IF :new.PIDC_VERS_ID is not null THEN
        insert into t_pidc_change_history
            (
              id,
             pidc_id,
             pidc_vers_id,  
              changed_date,
             changed_user,
             pidc_version,
             pidc_vers_vers,
            old_pidc_a2l_id,
            new_pidc_a2l_id
            )
          values
            (
             SeqV_Attributes.nextval,
             :new.project_id,
             :new.pidc_vers_id,
             :new.modified_date,
             :new.modified_user,
             pidc_ver,
             new_pidc_ver_ver,
             null,
             :new.pidc_a2l_id   
             );

    END IF;
  exception
    when others then
      -- This exception block handles just the History Entry relevant records.
      -- If a problem happens here, this should not lead to a problem in the users session
      null;
  end;
END;
/


--TRG_T_PREDEF_VALIDITY_UPD
create or replace TRIGGER TRG_T_PREDEF_VALIDITY_UPD 
  BEFORE UPDATE ON T_PREDEFINED_VALIDITY
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

--TRG_T_RULE_SET_PARAM_ATTR_UPDT
create or replace TRIGGER TRG_T_RULE_SET_PARAM_ATTR_UPDT
  BEFORE UPDATE ON T_RULE_SET_PARAM_ATTR
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

--TRG_T_RULE_SET_PARAMS_UPDT
create or replace TRIGGER TRG_T_RULE_SET_PARAMS_UPDT
  BEFORE UPDATE ON T_RULE_SET_PARAMS
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

--TRG_T_RULE_SET_UPDT
create or replace TRIGGER TRG_T_RULE_SET_UPDT
  BEFORE UPDATE ON T_RULE_SET
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

--TRG_T_RVW_QNAIRE_ANS_UPDT
create or replace TRIGGER TRG_T_RVW_QNAIRE_ANS_UPDT
BEFORE UPDATE ON T_RVW_QNAIRE_ANSWER
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

--TRG_T_RVW_QNAIRE_UPDT
create or replace TRIGGER TRG_T_RVW_QNAIRE_UPDT
BEFORE UPDATE ON T_RVW_QNAIRE_RESULTS
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

--TRG_T_RVW_VARIANTS_UPDT
create or replace TRIGGER TRG_T_RVW_VARIANTS_UPDT
BEFORE UPDATE ON T_RVW_VARIANTS
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

--TRG_T_WORKPACKAGE_UPDT
create or replace TRIGGER TRG_T_WORKPACKAGE_UPDT
BEFORE UPDATE ON T_WORKPACKAGE
FOR EACH ROW
BEGIN
    IF :new.VERSION = :old.VERSION THEN
        :new.VERSION := :old.VERSION + 1;
    END IF;

   IF :new.MODIFIED_DATE is null or NOT UPDATING('Modified_Date') THEN
        :new.MODIFIED_DATE := sys_extract_utc(systimestamp);
    end if;
  
  IF :new.MODIFIED_USER is null THEN  
        :new.MODIFIED_USER := user;
    end if;
 
END;
/

--TRG_T_WRKPKG_DIVISION_UPDT
create or replace TRIGGER TRG_T_WRKPKG_DIVISION_UPDT
BEFORE UPDATE ON T_WORKPACKAGE_DIVISION
FOR EACH ROW
BEGIN
    IF :new.VERSION = :old.VERSION THEN
        :new.VERSION := :old.VERSION + 1;
    END IF;

   IF :new.MODIFIED_DATE is null or NOT UPDATING('Modified_Date') THEN
        :new.MODIFIED_DATE := sys_extract_utc(systimestamp);
    end if;
  
  IF :new.MODIFIED_USER is null THEN  
        :new.MODIFIED_USER := user;
    end if;
 
END;
/

--TRG_TOP_LEVEL_ENTITIES_UPD
create or replace TRIGGER TRG_TOP_LEVEL_ENTITIES_UPD
BEFORE UPDATE ON TABV_TOP_LEVEL_ENTITIES
FOR EACH ROW
BEGIN
    IF :new.Version = :old.Version THEN
        :new.Version := :old.Version + 1;
    END IF;

    IF :new.LAST_MOD_DATE is null or NOT UPDATING('last_mod_date') then
        :new.LAST_MOD_DATE := sys_extract_utc(systimestamp);
    END IF;

END;
/


--TRG_UC_FAV_UPD
create or replace TRIGGER TRG_UC_FAV_UPD
BEFORE UPDATE ON T_USECASE_FAVORITES
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

--TRG_UC_GROUP_UPD
create or replace TRIGGER TRG_UC_GROUP_UPD
  BEFORE UPDATE ON TABV_USE_CASE_GROUPS
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


--TRG_UC_SECTIONS_UPD
create or replace TRIGGER TRG_UC_SECTIONS_UPD
  BEFORE UPDATE ON TABV_USE_CASE_SECTIONS
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

--TRG_UCP_ATTRS_UPD
create or replace TRIGGER TRG_UCP_ATTRS_UPD
  BEFORE UPDATE ON TABV_UCP_ATTRS
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

--TRG_USE_CASES_UPD
create or replace TRIGGER TRG_USE_CASES_UPD
  BEFORE UPDATE ON TABV_USE_CASES
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

--TRG_VARIANTS_ATTR_UPD
create or replace TRIGGER TRG_VARIANTS_ATTR_UPD
BEFORE UPDATE ON TabV_VARIANTS_ATTR
FOR EACH ROW
DECLARE
     pidc_ver number;
     pidc_ver_ver number ;
     proj_id number;
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

  select pidc.VERSION , pidc.PROJECT_ID into pidc_ver_ver,proj_id  from T_PIDC_VERSION pidc where pidc.PIDC_VERS_ID = :new.PIDC_VERS_ID;

  select proj.VERSION into pidc_ver from TABV_PROJECTIDCARD proj where proj.PROJECT_ID = proj_id;
   
   
   insert into t_pidc_change_history
    (
     id,
     var_id,
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
     new_is_variant
    )
  values
    (
     SeqV_Attributes.NEXTVAL,
    :old.variant_id,
   proj_id,
    :old.attr_id,
    :old.pidc_vers_id,
    :new.Modified_Date,
    :new.Modified_User,
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
    :old.is_subvariant,
    :new.is_subvariant
     );
END;
/


--TRG_APIC_ATTR_UPD
create or replace TRIGGER TRG_APIC_ATTR_UPD
BEFORE UPDATE ON TABV_ATTRIBUTES
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

   insert into tabv_attr_history
    (
     id,
     table_name,
     name_eng,
     name_ger,
     desc_eng,
     desc_ger,
     group_id,
     attr_id,
     normalized_flag,
     value_type_id,
     created_date,
     modified_date,
     created_user,
     modified_user,
     deleted_flag,
     attr_level,
     units,
     operation_flag,
     change_comment
    )

  values
    (
     SEQV_ATTRIBUTES.NEXTVAL,
     'tabv_attributes',
     :old.attr_name_eng,
     :old.attr_name_ger,
     :old.attr_desc_eng,
     :old.attr_desc_ger,
     :old.group_id,
     :old.attr_id,
     :old.normalized_flag,
     :old.value_type_id,
     :old.created_date,
     :old.modified_date,
     :old.created_user,
     :old.modified_user,
     :old.deleted_flag,
     :old.attr_level,
     :old.units,
     'M',
     :old.change_comment
    );

END;
/



--TRG_APIC_ATTR_DEP_UPD
create or replace TRIGGER TRG_APIC_ATTR_DEP_UPD
BEFORE UPDATE ON TabV_ATTR_DEPENDENCIES
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

   insert into tabv_attr_history
    (
     id,
     table_name,
     depen_id,
     attr_id,
     value_id,
     depen_attr_id,
     depen_value_id,
     deleted_flag,
     operation_flag,
     change_comment
    )
  values
    (
     SEQV_ATTRIBUTES.NEXTVAL,
     'tabv_attr_dependencies',
     :old.depen_id,
     :old.attr_id,
     :old.value_id,
     :old.depen_attr_id,
     :old.depen_value_id,
     :old.deleted_flag,
     'M',
     :old.change_comment
    );
END;
/

spool off