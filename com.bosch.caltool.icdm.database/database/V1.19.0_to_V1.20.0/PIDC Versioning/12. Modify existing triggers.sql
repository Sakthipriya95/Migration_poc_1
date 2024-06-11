spool c:\temp\12.pidcVersioning.log

--TABV_PROJECT_SUB_VARIANTS


--TABV_PROJ_SUB_VARIANTS_ATTR


--TABV_PROJECT_VARIANTS


--TABV_VARIANTS_ATTR


--TABV_PROJECT_ATTR


--TABV_PIDC_DET_STRUCTURE


--TABV_PROJECTIDCARD Check if this is required 


----------

--  DDL for Trigger TabV_PROJECT_ATTR - Before Insert
--- ICDM-1442
----------
CREATE OR REPLACE TRIGGER TRG_PROJECT_ATTR_INS 
BEFORE INSERT ON TabV_PROJECT_ATTR
FOR EACH ROW
DISABLE
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
     new_is_variant
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
    :new.is_variant
     );

END;
/
ALTER TRIGGER TRG_PROJECT_ATTR_INS ENABLE;
----------

--  DDL for Trigger TabV_PROJECT_ATTR - Before Update
--- ICDM-1442
----------

CREATE OR REPLACE TRIGGER TRG_PROJECT_ATTR_UPD 
BEFORE UPDATE ON TabV_PROJECT_ATTR
FOR EACH ROW
DISABLE
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
     new_is_variant
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
    :new.is_variant
     );

END;
/

ALTER TRIGGER TRG_PROJECT_ATTR_UPD ENABLE;
----------

--  DDL for Trigger TabV_VARIANTS_ATTR - Before Insert
--- ICDM-1442
----------
CREATE OR REPLACE TRIGGER TRG_VARIANTS_ATTR_INS
BEFORE INSERT ON TabV_VARIANTS_ATTR
FOR EACH ROW
DISABLE
DECLARE
     pidc_ver number;
     pidc_ver_ver number ;
     proj_id number;
BEGIN

 select pidc.VERSION , pidc.PROJECT_ID into pidc_ver_ver,proj_id  from T_PIDC_VERSION pidc where pidc.PIDC_VERS_ID = :new.PIDC_VERS_ID;

  select proj.VERSION into pidc_ver from TABV_PROJECTIDCARD proj where proj.PROJECT_ID = proj_id;
  
    IF :new.VAR_ATTR_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.VAR_ATTR_ID FROM DUAL;
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
     var_id,
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
     new_is_variant
    )
  values
    (
     SeqV_Attributes.NEXTVAL,
    :new.variant_id,
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
    :new.is_subvariant
     );

END;
/

ALTER TRIGGER TRG_VARIANTS_ATTR_INS ENABLE;
----------

--  DDL for Trigger TabV_VARIANTS_ATTR - Before Update
--- ICDM-1442
----------
CREATE OR REPLACE TRIGGER TRG_VARIANTS_ATTR_UPD
BEFORE UPDATE ON TabV_VARIANTS_ATTR
FOR EACH ROW
DISABLE
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
ALTER TRIGGER TRG_VARIANTS_ATTR_UPD ENABLE;
----------

--  DDL for Trigger TABV_VARIANTS_ATTR - Before Delete
--- ICDM-1442
----------
CREATE OR REPLACE TRIGGER TRG_VARIANTS_ATTR_DEL
  BEFORE DELETE
  on TABV_VARIANTS_ATTR
  for each row
DISABLE
DECLARE
     pidc_ver number;
     pidc_ver_ver number ;
     proj_id number;
BEGIN
     
  select pidc.VERSION , pidc.PROJECT_ID into pidc_ver_ver,proj_id  from T_PIDC_VERSION pidc where pidc.PIDC_VERS_ID = :old.PIDC_VERS_ID;

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
     old_used,     
     old_part_number,     
     old_spec_link,    
     old_description,     
     old_is_variant     
    )
  values
    (
     SeqV_Attributes.NEXTVAL,
     :old.variant_id,    
     proj_id,
     :old.attr_id,
     :old.pidc_vers_id,
     sysdate,
     user,
     pidc_ver,
      pidc_ver_ver,
     :old.value_id,    
     :old.used,   
     :old.part_number,    
     :old.spec_link,    
     :old.description,    
     :old.is_subvariant
     );
END;
/
ALTER TRIGGER TRG_VARIANTS_ATTR_DEL ENABLE;
----------

--  DDL for Trigger TabV_PROJECT_VARIANTS - Before Insert
--- ICDM-1442
----------

CREATE OR REPLACE TRIGGER TRG_PROJECT_VARIANTS_INS
BEFORE INSERT ON TabV_PROJECT_VARIANTS
FOR EACH ROW
DISABLE
DECLARE
     pidc_ver number;
     pidc_ver_ver number ;
     proj_id number;
BEGIN
    IF :new.VARIANT_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.VARIANT_ID FROM DUAL;
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


  select pidc.VERSION , pidc.PROJECT_ID into pidc_ver_ver,proj_id  from T_PIDC_VERSION pidc where pidc.PIDC_VERS_ID = :new.PIDC_VERS_ID;

  select proj.VERSION into pidc_ver from TABV_PROJECTIDCARD proj where proj.PROJECT_ID = proj_id; 
  
 insert into t_pidc_change_history
    (
     id,
     pidc_id,
     var_id,
     pidc_vers_id,
     changed_date,
     changed_user,
     pidc_version,
      pidc_vers_vers,
     new_value_id,
     NEW_DELETED_FLAG
    )
  values
    (
     SeqV_Attributes.NEXTVAL,
    proj_id,
    :new.VARIANT_ID,
    :new.pidc_vers_id,
    :new.Created_Date,
    :new.Created_User,
     pidc_ver,
      pidc_ver_ver,
    :new.value_id,
    :new.DELETED_FLAG
      );
END;
/

ALTER TRIGGER TRG_PROJECT_VARIANTS_INS ENABLE;
----------

--  DDL for Trigger TabV_PROJECT_VARIANTS - Before Update
--- ICDM-1442
----------

CREATE OR REPLACE TRIGGER TRG_PROJECT_VARIANTS_UPD
BEFORE UPDATE ON TabV_PROJECT_VARIANTS
FOR EACH ROW
DISABLE
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
ALTER TRIGGER TRG_PROJECT_VARIANTS_UPD ENABLE;
----------

--  DDL for Trigger TABV_PROJ_SUB_VARIANTS_ATTR - Before Insert
--- ICDM-1442
----------

CREATE OR REPLACE TRIGGER TRG_PROJ_SUB_VAR_ATTR_INS
BEFORE INSERT ON TABV_PROJ_SUB_VARIANTS_ATTR
FOR EACH ROW
DISABLE
DECLARE
     pidc_ver number;
     pidc_ver_ver number ;
     proj_id number;
BEGIN

 select pidc.VERSION , pidc.PROJECT_ID into pidc_ver_ver,proj_id  from T_PIDC_VERSION pidc where pidc.PIDC_VERS_ID = :new.PIDC_VERS_ID;

  select proj.VERSION into pidc_ver from TABV_PROJECTIDCARD proj where proj.PROJECT_ID = proj_id;

    IF :new.SUB_VAR_ATTR_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.SUB_VAR_ATTR_ID FROM DUAL;
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
     var_id,
     svar_id,
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
     new_is_variant
    )
  values
    (
     SeqV_Attributes.NEXTVAL,
    :new.variant_id,
    :new.sub_variant_id,
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
    'Y'
     );

END;
/
ALTER TRIGGER TRG_PROJ_SUB_VAR_ATTR_INS ENABLE;

----------

--  DDL for Trigger TABV_PROJ_SUB_VARIANTS_ATTR - Before Update
--- ICDM-1442
----------

CREATE OR REPLACE TRIGGER TRG_PROJ_SUB_VAR_ATTR_UPD
BEFORE UPDATE ON TABV_PROJ_SUB_VARIANTS_ATTR
FOR EACH ROW
DISABLE
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
ALTER TRIGGER TRG_PROJ_SUB_VAR_ATTR_UPD ENABLE;
----------

--  DDL for Trigger TABV_PROJ_SUB_VARIANTS_ATTR - Before Delete
--- ICDM-1442
----------


CREATE OR REPLACE TRIGGER TRG_PROJ_SUB_VAR_ATTR_DEL
BEFORE DELETE ON TABV_PROJ_SUB_VARIANTS_ATTR
FOR EACH ROW
DISABLE
DECLARE
    pidc_ver number;
     pidc_ver_ver number ;
     proj_id number;
 BEGIN
    select pidc.VERSION , pidc.PROJECT_ID into pidc_ver_ver,proj_id  from T_PIDC_VERSION pidc where pidc.PIDC_VERS_ID = :old.PIDC_VERS_ID;

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
     old_used,     
     old_part_number,     
     old_spec_link,    
     old_description,     
     old_is_variant     
    )
  values
    (
     SeqV_Attributes.NEXTVAL,
     :old.variant_id,
     :old.sub_variant_id,
    proj_id,
    :old.attr_id,
    :old.pidc_vers_id,
    sysdate,
    user,
    pidc_ver,
     pidc_ver_ver,
    :old.value_id,    
    :old.used,   
    :old.part_number,    
    :old.spec_link,    
    :old.description,    
    'Y'
     );
END;
/
ALTER TRIGGER TRG_PROJ_SUB_VAR_ATTR_DEL ENABLE;
----------

--  DDL for Trigger TabV_PROJECT_SUB_VARIANTS - Before Insert
--- ICDM-1442
----------
CREATE OR REPLACE TRIGGER TRG_PROJECT_SUB_VARIANTS_INS
BEFORE INSERT ON TabV_PROJECT_SUB_VARIANTS
FOR EACH ROW
DISABLE
DECLARE
    pidc_ver number;
     pidc_ver_ver number ;
     proj_id number;
BEGIN
    IF :new.SUB_VARIANT_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.SUB_VARIANT_ID FROM DUAL;
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

     select pidc.VERSION , pidc.PROJECT_ID into pidc_ver_ver,proj_id  from T_PIDC_VERSION pidc where pidc.PIDC_VERS_ID = :new.PIDC_VERS_ID;

    select proj.VERSION into pidc_ver from TABV_PROJECTIDCARD proj where proj.PROJECT_ID = proj_id;
    
 insert into t_pidc_change_history
    (
     id,
     pidc_id,
     var_id,
     svar_id,
    pidc_vers_id,
     changed_date,
     changed_user,
     pidc_version,
      pidc_vers_vers,
     new_value_id,
     NEW_DELETED_FLAG
    )
  values
    (
     SeqV_Attributes.NEXTVAL,
    proj_id,
    :new.VARIANT_ID,
    :new.SUB_VARIANT_ID,
    :new.pidc_vers_id,
    :new.Created_Date,
    :new.Created_User,
     pidc_ver,
      pidc_ver_ver,
    :new.value_id,
    :new.DELETED_FLAG
      );
END;
/
ALTER TRIGGER TRG_PROJECT_SUB_VARIANTS_INS ENABLE;
----------

--  DDL for Trigger TabV_PROJECT_SUB_VARIANTS - Before Update
--- ICDM-1442
----------


CREATE OR REPLACE TRIGGER TRG_PROJECT_SUB_VARIANTS_UPD
BEFORE UPDATE ON TabV_PROJECT_SUB_VARIANTS
FOR EACH ROW
DISABLE
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

ALTER TRIGGER TRG_PROJECT_SUB_VARIANTS_UPD ENABLE;
----------

--  DDL for Trigger TabV_PROJECTIDCARD - Before Insert
--- ICDM-1442
----------

CREATE OR REPLACE TRIGGER TRG_PROJECTIDCARD_INS
BEFORE INSERT ON TabV_PROJECTIDCARD
FOR EACH ROW
DISABLE
BEGIN
    IF :new.PROJECT_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.PROJECT_ID FROM DUAL;
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

   if :new.modified_date IS NULL then
        :new.modified_date := sys_extract_utc(systimestamp);
   end if;

   if :new.modified_user IS NULL then
        :new.modified_user := user;
   end if;

     insert into t_pidc_change_history
    (
     id,
     pidc_id,
     pidc_vers_id,
     changed_date,
     changed_user,
     pidc_version,
     pidc_vers_vers,
     new_value_id
    )
  values
    (
     SeqV_Attributes.nextval,
     :new.project_id,
      null,
     :new.modified_date,
     :new.modified_user,
     :new.version,   
     null,
     :new.value_id
     );

END;
/


ALTER TRIGGER TRG_PROJECTIDCARD_INS ENABLE;
----------

--  DDL for Trigger TabV_PROJECTIDCARD - Before Update
--- ICDM-1442
----------

CREATE OR REPLACE TRIGGER TRG_PROJECTIDCARD_UPD
BEFORE UPDATE ON TabV_PROJECTIDCARD
FOR EACH ROW
DISABLE
DECLARE
  new_pidc_ver_id number ;
  old_pidc_ver_id number;
     pidc_ver_ver number;
     old_pro_rev_id number;
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
END;
/


ALTER TRIGGER TRG_PROJECTIDCARD_UPD ENABLE;

-----------------------------------------------------------------------------------

CREATE OR REPLACE TRIGGER TRG_PROJECT_ATTR_DEL
  BEFORE DELETE
  on TABV_PROJECT_ATTR
  for each row
DISABLE
DECLARE
     proj_id number;
BEGIN

select pidc.PROJECT_ID into proj_id  from T_PIDC_VERSION pidc where pidc.PIDC_VERS_ID = :old.PIDC_VERS_ID;
   insert into tabv_attr_history
    (
     id,
     table_name,
     prj_attr_id,
      project_id,
      pidc_vers_id,
      attr_id,
     value_id,
     operation_flag
    )

  values
    (
     SEQV_ATTRIBUTES.NEXTVAL,
     'TABV_PROJECT_ATTR',
     :old.prj_attr_id,
    proj_id,
     :old.pidc_vers_id,
     :old.attr_id,
     :old.value_id ,
     'D'
     );
END;
/

ALTER TRIGGER TRG_PROJECT_ATTR_DEL ENABLE;
-----------------------------------------------------------------------------------

CREATE OR REPLACE TRIGGER TRG_PROJECT_VARIANTS_DEL 
  BEFORE DELETE
  on TABV_PROJECT_VARIANTS
  for each row
DISABLE
DECLARE
     proj_id number;
BEGIN

select pidc.PROJECT_ID into proj_id  from T_PIDC_VERSION pidc where pidc.PIDC_VERS_ID = :old.PIDC_VERS_ID;

   insert into tabv_attr_history
    (
     id,
     table_name,
     variant_id,
     value_id,
     project_id,
     pidc_vers_id,
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
     'TABV_PROJECT_VARIANTS',
     :old.variant_id,
     :old.value_id ,
     proj_id,
     :old.pidc_vers_id,
     :old.created_date,
     :old.modified_date,
     :old.created_user,
     :old.modified_user,
     :old.deleted_flag ,
     'D'
     );
END;
/

ALTER TRIGGER TRG_PROJECT_VARIANTS_DEL ENABLE;

--------------------------------------------------------
--  DDL for Trigger TRG_APIC_ATTR_VAL_UPD - Before update
--------------------------------------------------------

CREATE OR REPLACE TRIGGER TRG_APIC_ATTR_VAL_UPD
BEFORE UPDATE ON TabV_ATTR_VALUES FOR EACH ROW
DECLARE
    l_attr_lev tabv_attributes.attr_level%type;

    --PIDC Cursor
    CURSOR pid_cur(val_id tabv_projectidcard.value_id%type)
    IS
        SELECT *
        FROM tabv_projectidcard
        WHERE value_id = val_id;

    --Variant Cursor
    CURSOR pid_var_cur(val_id tabv_project_variants.value_id%type)
    IS
        SELECT
            pidc.project_id,
            var.variant_id,
            var.value_id,
            pidcVer.pidc_vers_id,
            pidc.version
        FROM
            tabv_project_variants var,
            tabv_projectidcard pidc,
            t_pidc_version pidcVer
        WHERE
            pidc.project_id = pidcVer.project_id
            AND pidcVer.pidc_vers_id = var.pidc_vers_id
            AND var.value_id    = val_id;

    --Sub Variant Cursor
    CURSOR pid_svar_cur(val_id tabv_project_sub_variants.value_id%type)
    IS
        SELECT
            pidc.project_id,
            svar.sub_variant_id,
            svar.value_id,
             pidcVer.pidc_vers_id,
            pidc.version
        FROM
            tabv_project_sub_variants svar,
            tabv_projectidcard pidc,
             t_pidc_version pidcVer
        WHERE
            pidc.project_id = pidcVer.project_id
            AND pidcVer.project_id = svar.pidc_vers_id
            AND svar.value_id   = val_id;

    l_pid_cur pid_cur%ROWTYPE;
    l_pid_var_cur pid_var_cur%ROWTYPE;
    l_pid_svar_cur pid_svar_cur%ROWTYPE;

BEGIN
    /*
    *Set default value for columns
    *
    *
    */
    IF :new.Version   = :old.Version THEN
        :new.Version := :old.Version + 1;
    END IF;
    IF :new.modified_date  IS NULL THEN
        :new.modified_date := sys_extract_utc(systimestamp);
    END IF;
    IF :new.modified_user  IS NULL THEN
        :new.modified_user := USER;
    END IF;   

    /*
    *
    * PIDC History insert
    *
    *
    */
    SELECT attr_level
    INTO l_attr_lev
    FROM TABV_ATTRIBUTES
    WHERE attr_id=:old.attr_id;

    /*
    *PIDC Name
    */
    IF l_attr_lev = -1 THEN
        OPEN pid_cur(:old.value_id);
        LOOP
            FETCH pid_cur INTO l_pid_cur;
            EXIT WHEN pid_cur%NOTFOUND;

            --Insert into the history table
            INSERT
            INTO
                t_pidc_change_history
                (
                    ID,
                    PIDC_ID,
                    PIDC_VERS_ID,
                    CHANGED_DATE,
                    CHANGED_USER,
                    PIDC_VERSION,
                    OLD_VALUE_ID,
                    NEW_VALUE_ID,
                    old_value_desc_eng,
                    new_value_desc_eng,
                    old_value_desc_ger,
                    new_value_desc_ger,
                    OLD_TEXTVALUE_ENG,
                    NEW_TEXTVALUE_ENG,
                    OLD_TEXTVALUE_GER,
                    NEW_TEXTVALUE_GER,
                    old_deleted_flag,
                    new_deleted_flag
                )
                VALUES
                (
                    SeqV_Attributes.NEXTVAL,
                    l_pid_cur.project_id,
                    null,
                    :new.modified_date,
                    :new.modified_user,
                    l_pid_cur.version,
                    :old.value_id,
                    :new.value_id,
                    :old.value_desc_eng,
                    :new.value_desc_eng,
                    :old.value_desc_ger,
                    :new.value_desc_ger,
                    :old.textvalue_eng,
                    :new.textvalue_eng,
                    :old.textvalue_ger,
                    :new.textvalue_ger,
                    :old.deleted_flag,
                    :new.deleted_flag
                );
        END LOOP;
        CLOSE pid_cur;
    END IF;

    /*
    *Variant Name
    */
    IF l_attr_lev = -2 THEN
        OPEN pid_var_cur(:old.value_id);
        LOOP
            FETCH pid_var_cur INTO l_pid_var_cur;
            EXIT WHEN pid_var_cur%NOTFOUND;

            --Insert into the history table
            INSERT
            INTO
                t_pidc_change_history
                (
                    ID,
                    PIDC_ID,
                    VAR_ID,
                     PIDC_VERS_ID,
                    CHANGED_DATE,
                    CHANGED_USER,
                    PIDC_VERSION,
                    OLD_VALUE_ID,
                    NEW_VALUE_ID,
                    old_value_desc_eng,
                    new_value_desc_eng,
                    old_value_desc_ger,
                    new_value_desc_ger,
                    OLD_TEXTVALUE_ENG,
                    NEW_TEXTVALUE_ENG,
                    OLD_TEXTVALUE_GER,
                    NEW_TEXTVALUE_GER,
                    old_deleted_flag,
                    new_deleted_flag
                )
                VALUES
                (
                    SeqV_Attributes.NEXTVAL,
                    l_pid_var_cur.project_id,
                    l_pid_var_cur.variant_id,
                   null,
                    :new.modified_date,
                    :new.modified_user,
                    l_pid_var_cur.version,
                    :old.value_id,
                    :new.value_id,
                    :old.value_desc_eng,
                    :new.value_desc_eng,
                    :old.value_desc_ger,
                    :new.value_desc_ger,
                    :old.textvalue_eng,
                    :new.textvalue_eng,
                    :old.textvalue_ger,
                    :new.textvalue_ger,
                    :old.deleted_flag,
                    :new.deleted_flag
                );
        END LOOP;
        CLOSE pid_var_cur;
    END IF;

    /*
    *Sub Variant Name
    */
    IF l_attr_lev = -3 THEN
        OPEN pid_svar_cur(:old.value_id);
        LOOP
            FETCH pid_svar_cur INTO l_pid_svar_cur;
            EXIT WHEN pid_svar_cur%NOTFOUND;

            --Insert into the history table
            INSERT
            INTO
                t_pidc_change_history
                (
                    ID,
                    PIDC_ID,
                    SVAR_ID,
                     PIDC_VERS_ID,
                    CHANGED_DATE,
                    CHANGED_USER,
                    PIDC_VERSION,
                    OLD_VALUE_ID,
                    NEW_VALUE_ID,
                    old_value_desc_eng,
                    new_value_desc_eng,
                    old_value_desc_ger,
                    new_value_desc_ger,
                    OLD_TEXTVALUE_ENG,
                    NEW_TEXTVALUE_ENG,
                    OLD_TEXTVALUE_GER,
                    NEW_TEXTVALUE_GER,
                    old_deleted_flag,
                    new_deleted_flag
                )
                VALUES
                (
                    SeqV_Attributes.NEXTVAL,
                    l_pid_svar_cur.project_id,
                    l_pid_svar_cur.sub_variant_id,
                   null,
                    :new.modified_date,
                    :new.modified_user,
                    l_pid_svar_cur.version,
                    :old.value_id,
                    :new.value_id,
                    :old.value_desc_eng,
                    :new.value_desc_eng,
                    :old.value_desc_ger,
                    :new.value_desc_ger,
                    :old.textvalue_eng,
                    :new.textvalue_eng,
                    :old.textvalue_ger,
                    :new.textvalue_ger,
                    :old.deleted_flag,
                    :new.deleted_flag
                );
        END LOOP;
        CLOSE pid_svar_cur;
    END IF;
END;
/
ALTER TRIGGER TRG_APIC_ATTR_VAL_UPD ENABLE;


spool off