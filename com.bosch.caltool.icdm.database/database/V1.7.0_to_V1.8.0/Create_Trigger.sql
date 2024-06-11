spool c:\temp\create_trigger.log
--------------------------------------------------------
--  March-20-2014
--------------------------------------------------------

--------------------------------------------------------
--  DDL for Trigger TRG_ICDM_FILE_DATA_INS - Before insert
--------------------------------------------------------

CREATE OR REPLACE TRIGGER TRG_ICDM_FILE_DATA_INS
  BEFORE INSERT ON TABV_ICDM_FILE_DATA
  FOR EACH ROW
Begin
    IF :new.FILE_DATA_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.FILE_DATA_ID FROM DUAL;
    END IF;

    IF :new.Version is null THEN
        :new.Version := 1;
    END IF;

END;
/
ALTER TRIGGER TRG_ICDM_FILE_DATA_INS ENABLE;


--------------------------------------------------------
--  March-26-2014
--------------------------------------------------------

--------------------------------------------------------
--  DDL for Trigger TRG_TABV_PROJECTIDCARD_INS - Before insert
--------------------------------------------------------

-- ICDM-678

CREATE OR REPLACE TRIGGER TRG_PROJECTIDCARD_INS
BEFORE INSERT ON TabV_PROJECTIDCARD
FOR EACH ROW
BEGIN
    IF :new.PROJECT_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.PROJECT_ID FROM DUAL;
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
  
   if :new.modified_date IS NULL then
        :new.modified_date := sysdate;
   end if;
    
   if :new.modified_user IS NULL then
        :new.modified_user := user;
   end if;
    
   insert into t_pidc_change_history
    (
     id,
     pidc_id,
     pro_rev_id,
     changed_date,
     changed_user,
     pidc_version,
     new_value_id
    )
  values
    (
     SeqV_Attributes.nextval,
     :new.project_id,
     :new.pro_rev_id,
     :new.modified_date,
     :new.modified_user,
     :new.version,   
     :new.value_id
     );
END;
/
ALTER TRIGGER TRG_PROJECTIDCARD_INS ENABLE;
--------------------------------------------------------
--  March-26-2014
--------------------------------------------------------

--------------------------------------------------------
--  DDL for Trigger TRG_TABV_PROJECTIDCARD_UPD - Before update
--------------------------------------------------------

-- ICDM-678

CREATE OR REPLACE TRIGGER TRG_PROJECTIDCARD_UPD
BEFORE UPDATE ON TabV_PROJECTIDCARD
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

   insert into tabv_attr_history
    (
     id,
     table_name,
     project_id,
      pro_rev_id,
     value_id,
     created_date,
     modified_date,
     created_user,
     modified_user,
     operation_flag
    )

  values
    (
     SEQV_ATTRIBUTES.NEXTVAL,
     'TABV_PROJECTIDCARD',
     :old.project_id,
     :old.pro_rev_id,
     :old.value_id ,
     :old.created_date,
     :old.modified_date,
     :old.created_user,
     :old.modified_user,
     'M'
     );
     
      insert into t_pidc_change_history
    (
     id,
     pidc_id,
     pro_rev_id,
     changed_date,
     changed_user,
     pidc_version,
     old_value_id,
     new_value_id
    )
  values
    (
       SEQV_ATTRIBUTES.NEXTVAL,
     :old.project_id,
     :new.pro_rev_id,
     :new.modified_date,
     :new.modified_user,
     :new.version,
     :old.value_id,
     :new.value_id
     );
     
END;
/
ALTER TRIGGER TRG_PROJECTIDCARD_UPD ENABLE;

--------------------------------------------------------
--  March-26-2014
--------------------------------------------------------

--------------------------------------------------------
--  DDL for Trigger TRG_TABV_PROJECT_ATTR_INS - Before insert
--------------------------------------------------------

-- ICDM-678

CREATE OR REPLACE TRIGGER TRG_PROJECT_ATTR_INS
BEFORE INSERT ON TabV_PROJECT_ATTR
FOR EACH ROW
DECLARE
    pid_ver number;
BEGIN

 select pidc.VERSION into pid_ver from TabV_PROJECTIDCARD pidc where pidc.PROJECT_ID = :new.PROJECT_ID;
 
    IF :new.PRJ_ATTR_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.PRJ_ATTR_ID FROM DUAL;
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
    
    
   insert into t_pidc_change_history
    (
     id,
     pidc_id,
     attr_id,
     pro_rev_id,
     changed_date,
     changed_user,
     pidc_version,
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
    :new.project_id,
    :new.attr_id,
    :new.pro_rev_id,
   :new.Created_Date,
   :new.Created_User,
    pid_ver,
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


--------------------------------------------------------
--  March-26-2014
--------------------------------------------------------

--------------------------------------------------------
--  DDL for Trigger TRG_TABV_PROJECT_ATTR_UPD - Before update
--------------------------------------------------------
-- ICDM-678

CREATE OR REPLACE TRIGGER TRG_PROJECT_ATTR_UPD
BEFORE UPDATE ON TabV_PROJECT_ATTR
FOR EACH ROW
DECLARE
    pid_version number;
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

     select pidc.VERSION into pid_version from TabV_PROJECTIDCARD pidc where pidc.PROJECT_ID = :old.project_id;
   insert into tabv_attr_history
    (
     id,
     table_name,
     prj_attr_id,
      project_id,
       pro_rev_id,
      attr_id,
     value_id,
     operation_flag
    )

  values
    (
     SEQV_ATTRIBUTES.NEXTVAL,
     'TABV_PROJECT_ATTR',
     :old.prj_attr_id,
     :old.project_id,
     :old.pro_rev_id,
     :old.attr_id,
     :old.value_id,
     'M'
     );

 insert into t_pidc_change_history
    (
     id,
     pidc_id,
     attr_id,
     pro_rev_id,
     changed_date,
     changed_user,
     pidc_version,
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
    :old.project_id,
    :old.attr_id,
    :old.pro_rev_id,
    :new.modified_date,
    :new.modified_user,
    pid_version,
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

--------------------------------------------------------
--  April-1-2014
--------------------------------------------------------

--------------------------------------------------------
--  DDL for Trigger TRG_TABV_PID_HISTORY - Before insert
--------------------------------------------------------

-- ICDM-678

CREATE OR REPLACE TRIGGER TRG_PID_HISTORY_INS
BEFORE INSERT ON TabV_PID_HISTORY
FOR EACH ROW
DECLARE
 pid_ver number;
 old_status_id number;

 BEGIN

	 select version into pid_ver from TABV_PROJECTIDCARD where project_id = :new.project_id;
     select max(pid_status_id) into old_status_id from TABV_PID_HISTORY where project_id=:new.project_id and pro_rev_id= :new.pro_rev_id;

   insert into t_pidc_change_history
    (
     id,
     pidc_id,
     pro_rev_id,
     changed_date,
     changed_user,
     pidc_version,
     old_status_id,
     new_status_id
    )
  values
    (
    SeqV_Attributes.NEXTVAL,
     :new.project_id,
     :new.pro_rev_id,
     :new.created_date,
     :new.created_user,
     pid_ver,
     old_status_id,
     :new.pid_status_id
     );
END;
/
ALTER TRIGGER TRG_PID_HISTORY_INS ENABLE;


--------------------------------------------------------
--  April-2-2014
--------------------------------------------------------

--------------------------------------------------------
--  DDL for Trigger TabV_VARIANTS_ATTR - Before insert
--------------------------------------------------------

-- ICDM-682

CREATE OR REPLACE TRIGGER TRG_VARIANTS_ATTR_INS
BEFORE INSERT ON TabV_VARIANTS_ATTR
FOR EACH ROW
DECLARE
    pid_ver number;
BEGIN

select pidc.VERSION into pid_ver from TabV_PROJECTIDCARD pidc where pidc.PROJECT_ID = :new.PROJECT_ID;

    IF :new.VAR_ATTR_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.VAR_ATTR_ID FROM DUAL;
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

  insert into t_pidc_change_history
    (
     id,
     var_id,
     pidc_id,
     attr_id,
     pro_rev_id,
     changed_date,
     changed_user,
     pidc_version,
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
    :new.project_id,
    :new.attr_id,
    :new.pro_rev_id,
    :new.Created_Date,
    :new.Created_User,
     pid_ver,
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


--------------------------------------------------------
--  DDL for Trigger TabV_VARIANTS_ATTR - Before Update
--------------------------------------------------------

-- ICDM-682

CREATE OR REPLACE TRIGGER TRG_VARIANTS_ATTR_UPD
BEFORE UPDATE ON TabV_VARIANTS_ATTR
FOR EACH ROW
DECLARE
    pid_version number;
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

   insert into tabv_attr_history
    (
     id,
     table_name,
     var_attr_id,
     variant_id,
      project_id,
       pro_rev_id,
      attr_id,
     value_id,
     operation_flag
    )

  values
    (
     SEQV_ATTRIBUTES.NEXTVAL,
     'TABV_VARIANTS_ATTR ',
     :old.var_attr_id,
     :old.variant_id,
     :old.project_id,
     :old.pro_rev_id,
     :old.attr_id,
     :old.value_id ,
     'M'
     );     
     
     
   select pidc.VERSION into pid_version from TabV_PROJECTIDCARD pidc where pidc.PROJECT_ID = :old.project_id;
   insert into t_pidc_change_history
    (
     id,
     var_id,
     pidc_id,
     attr_id,
     pro_rev_id,
     changed_date,
     changed_user,
     pidc_version,
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
    :old.project_id,
    :old.attr_id,
    :old.pro_rev_id,
    :new.Modified_Date,
    :new.Modified_User,
    pid_version,
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

--------------------------------------------------------
--  DDL for Trigger TabV_VARIANTS_ATTR - Before Delete
--------------------------------------------------------

-- ICDM-682

CREATE OR REPLACE TRIGGER TRG_VARIANTS_ATTR_DEL
  BEFORE DELETE
  on TABV_VARIANTS_ATTR
  for each row
DECLARE
    pid_version number;  
BEGIN
   insert into tabv_attr_history
    (
     id,
     table_name,
     var_attr_id,
     variant_id,
     project_id,
     pro_rev_id,
     attr_id,
     value_id,
     operation_flag
    )

  values
    (
     SEQV_ATTRIBUTES.NEXTVAL,
     'TABV_VARIANTS_ATTR ',
     :old.var_attr_id,
     :old.variant_id,
     :old.project_id,
     :old.pro_rev_id,
     :old.attr_id,
     :old.value_id ,
     'D'
     );
     
    select pidc.VERSION into pid_version from TabV_PROJECTIDCARD pidc where pidc.PROJECT_ID = :old.project_id;  
   
    insert into t_pidc_change_history
    (
     id,
     var_id,     
     pidc_id,
     attr_id,
     pro_rev_id,
     changed_date,
     changed_user,
     pidc_version,
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
     :old.project_id,
     :old.attr_id,
     :old.pro_rev_id,
     sysdate,
     user,
     pid_version,
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


-------------------------------------------------------
--  April-3-2014
--------------------------------------------------------

--------------------------------------------------------
--  DDL for Trigger TRG_PROJ_SUB_VAR_ATTR_INS - Before insert
--------------------------------------------------------
--ICDM-679  

CREATE OR REPLACE TRIGGER TRG_PROJ_SUB_VAR_ATTR_INS
BEFORE INSERT ON TABV_PROJ_SUB_VARIANTS_ATTR
FOR EACH ROW
DECLARE
    pid_ver number;
BEGIN

 select pidc.VERSION into pid_ver from TabV_PROJECTIDCARD pidc where pidc.PROJECT_ID = :new.PROJECT_ID;
 
    IF :new.SUB_VAR_ATTR_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.SUB_VAR_ATTR_ID FROM DUAL;
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
    
    
   insert into t_pidc_change_history
    (
     id,
     var_id,
     svar_id,
     pidc_id,
     attr_id,
     pro_rev_id,
     changed_date,
     changed_user,
     pidc_version,
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
    :new.project_id,
    :new.attr_id,
    :new.pro_rev_id,
    :new.Created_Date,
    :new.Created_User,
     pid_ver,
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
-------------------------------------------------------
--  April-3-2014
--------------------------------------------------------

--------------------------------------------------------
--  DDL for Trigger TRG_PROJ_SUB_VAR_ATTR_UPD - Before update
--------------------------------------------------------
--ICDM-679  

CREATE OR REPLACE TRIGGER TRG_PROJ_SUB_VAR_ATTR_UPD
BEFORE UPDATE ON TABV_PROJ_SUB_VARIANTS_ATTR
FOR EACH ROW
DECLARE
    pid_version number;
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

     select pidc.VERSION into pid_version from TabV_PROJECTIDCARD pidc where pidc.PROJECT_ID = :old.project_id;
   insert into t_pidc_change_history
    (
     id,
     var_id,
     svar_id,
     pidc_id,
     attr_id,
     pro_rev_id,
     changed_date,
     changed_user,
     pidc_version,
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
    :old.project_id,
    :old.attr_id,
    :old.pro_rev_id,
    :new.modified_date,
    :new.modified_user,
    pid_version,
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

-------------------------------------------------------
--  April-3-2014
--------------------------------------------------------

--------------------------------------------------------
--  DDL for Trigger TRG_PROJ_SUB_VAR_ATTR_DEL - Before delete
--------------------------------------------------------
--ICDM-679  

CREATE OR REPLACE TRIGGER TRG_PROJ_SUB_VAR_ATTR_DEL
BEFORE DELETE ON TABV_PROJ_SUB_VARIANTS_ATTR
FOR EACH ROW
DECLARE
    pid_version number;   
 BEGIN
    select pidc.VERSION into pid_version from TabV_PROJECTIDCARD pidc where pidc.PROJECT_ID = :old.project_id;
 
   insert into t_pidc_change_history
    (
     id,
     var_id,
     svar_id,
     pidc_id,
     attr_id,
     pro_rev_id,
     changed_date,
     changed_user,
     pidc_version,
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
     null,
    :old.project_id,
    :old.attr_id,
    :old.pro_rev_id,
    sysdate,
    user,
    pid_version,
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

--------------------------------------------------------
--  DDL for Trigger TRG_PROJECT_SUB_VARIANTS_INS - Before Insert
--------------------------------------------------------
--ICDM-680

CREATE OR REPLACE TRIGGER TRG_PROJECT_SUB_VARIANTS_INS
BEFORE INSERT ON TabV_PROJECT_SUB_VARIANTS
FOR EACH ROW
DECLARE
    pid_ver number;
BEGIN
    IF :new.SUB_VARIANT_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.SUB_VARIANT_ID FROM DUAL;
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
select pidc.version into pid_ver from TabV_PROJECTIDCARD pidc where pidc.project_id = :new.project_id;
 insert into t_pidc_change_history
    (
     id,
     pidc_id,
     var_id,
     svar_id,
     pro_rev_id,
     changed_date,
     changed_user,
     pidc_version,
     new_value_id,
     NEW_DELETED_FLAG  
    )
  values
    (
     SeqV_Attributes.NEXTVAL,
    :new.project_id,
    :new.VARIANT_ID,
    :new.SUB_VARIANT_ID,
    :new.pro_rev_id,
    :new.Created_Date,
    :new.Created_User,
     pid_ver,
    :new.value_id,
    :new.DELETED_FLAG
      );
END;
/
ALTER TRIGGER TRG_PROJECT_SUB_VARIANTS_INS ENABLE;

--------------------------------------------------------
--  DDL for Trigger TRG_PROJECT_SUB_VARIANTS_UPD - Before Update
--------------------------------------------------------
--ICDM-680

CREATE OR REPLACE TRIGGER TRG_PROJECT_SUB_VARIANTS_UPD
BEFORE UPDATE ON TabV_PROJECT_SUB_VARIANTS
FOR EACH ROW
DECLARE
    pid_version number;
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
    
    select pidc.VERSION into pid_version from TabV_PROJECTIDCARD pidc where pidc.PROJECT_ID = :old.project_id;
    
    insert into t_pidc_change_history
    (
     id,
     pidc_id,
     VAR_ID,
     SVAR_ID,
     pro_rev_id,
     changed_date,
     changed_user,
     pidc_version,
     old_value_id,
     new_value_id,
     OLD_DELETED_FLAG,
     NEW_DELETED_FLAG
    )
  values
    (
     SeqV_Attributes.NEXTVAL,
    :old.project_id,
    :old.VARIANT_ID,
    :old.SUB_VARIANT_ID,
    :old.pro_rev_id,
    :new.modified_date,
    :new.modified_user,
    pid_version,
    :old.value_id,
    :new.value_id,
    :old.DELETED_FLAG,
    :new.DELETED_FLAG
     );
END;
/

ALTER TRIGGER TRG_PROJECT_SUB_VARIANTS_UPD ENABLE;

-------------------------------------------------------
--  April-3-2014
--------------------------------------------------------
--------------------------------------------------------
--  DDL for Trigger TRG_PROJECT_VARIANTS_INS - Before Insert
--------------------------------------------------------
--ICDM-681
CREATE OR REPLACE TRIGGER TRG_PROJECT_VARIANTS_INS
BEFORE INSERT ON TabV_PROJECT_VARIANTS
FOR EACH ROW
DECLARE
    pid_ver number;
BEGIN
    IF :new.VARIANT_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.VARIANT_ID FROM DUAL;
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


select pidc.version into pid_ver from TabV_PROJECTIDCARD pidc where pidc.project_id = :new.project_id;
 insert into t_pidc_change_history
    (
     id,
     pidc_id,
     var_id,
     pro_rev_id,
     changed_date,
     changed_user,
     pidc_version,
     new_value_id,
     NEW_DELETED_FLAG  
    )
  values
    (
     SeqV_Attributes.NEXTVAL,
    :new.project_id,
    :new.VARIANT_ID,
    :new.pro_rev_id,
    :new.Created_Date,
    :new.Created_User,
     pid_ver,
    :new.value_id,
    :new.DELETED_FLAG
      );
END;
/
ALTER TRIGGER TRG_PROJECT_VARIANTS_INS ENABLE;

-------------------------------------------------------
--  April-3-2014
--------------------------------------------------------
--------------------------------------------------------
--  DDL for Trigger TRG_PROJECT_VARIANTS_UPD - Before Update
--------------------------------------------------------
--ICDM-681
CREATE OR REPLACE TRIGGER TRG_PROJECT_VARIANTS_UPD
BEFORE UPDATE ON TabV_PROJECT_VARIANTS
FOR EACH ROW
DECLARE
    pid_version number;
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

   insert into tabv_attr_history
    (
     id,
     table_name,
     variant_id,
     value_id,
     project_id,
     pro_rev_id,
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
     :old.project_id,
     :old.pro_rev_id,
     :old.created_date,
     :old.modified_date,
     :old.created_user,
     :old.modified_user,
     :old.deleted_flag ,
     'M'
     );
     
   select pidc.VERSION into pid_version from TabV_PROJECTIDCARD pidc where pidc.PROJECT_ID = :old.project_id;
    
    insert into t_pidc_change_history
    (
     id,
     pidc_id,
     VAR_ID,
     pro_rev_id,
     changed_date,
     changed_user,
     pidc_version,
     old_value_id,
     new_value_id,
     OLD_DELETED_FLAG,
     NEW_DELETED_FLAG
    )
  values
    (
     SeqV_Attributes.NEXTVAL,
    :old.project_id,
    :old.VARIANT_ID,
    :old.pro_rev_id,
    :new.modified_date,
    :new.modified_user,
    pid_version,
    :old.value_id,
    :new.value_id,
    :old.DELETED_FLAG,
    :new.DELETED_FLAG
     );
END;
/
ALTER TRIGGER TRG_PROJECT_VARIANTS_UPD ENABLE;


--The following trigger can be dropped (to avoid confusions) as it is handled via command.
--
DROP TRIGGER TRG_PROJECTIDCARD_INSAFTER;
--

spool off;
