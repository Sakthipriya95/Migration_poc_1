spool c:\temp\13.pidcVersioning.log


----------

--  DDL for Trigger T_PIDC_VERSION - Before Insert
--- ICDM-1442
----------

CREATE OR REPLACE TRIGGER TRG_PIDC_VERSION_INS 
BEFORE INSERT ON T_PIDC_VERSION
FOR EACH ROW
DECLARE
    pidc_ver number;
BEGIN
    IF :new.PIDC_VERS_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.PIDC_VERS_ID FROM DUAL;
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
     new_value_desc_eng,
     new_value_desc_ger,
     NEW_TEXTVALUE_ENG,
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
     :new.vers_desc_eng,
     :new.vers_desc_ger,
     :new.vers_name,
     :new.deleted_flag
     );
END;
/

ALTER TRIGGER TRG_PIDC_VERSION_INS ENABLE;
----------

--  DDL for Trigger T_PIDC_VERSION - Before Update
--- ICDM-1442
----------

CREATE OR REPLACE TRIGGER TRG_PIDC_VERSION_UPD
BEFORE UPDATE ON T_PIDC_VERSION
FOR EACH ROW
DECLARE
     pidc_ver number;
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

ALTER TRIGGER TRG_PIDC_VERSION_UPD ENABLE;



--------------------------------------------------------
--  DDL for Trigger TABV_PIDC_A2L - Before Insert
--- ICDM-1463
--------------------------------------------------------

CREATE OR REPLACE TRIGGER TRG_T_PIDC_A2L_INS
BEFORE INSERT ON T_PIDC_A2L FOR EACH ROW
DECLARE
pidc_ver number;
     pidc_ver_ver number ;
BEGIN
    IF :new.PIDC_A2L_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.PIDC_A2L_ID FROM DUAL;
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


  select pidc.VERSION  into pidc_ver_ver  from T_PIDC_VERSION pidc where pidc.PIDC_VERS_ID = :new.PIDC_VERS_ID;
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
    new_pidc_a2l_id
    )
  values
    (
     SeqV_Attributes.nextval,
     :new.project_id,
     :new.pidc_vers_id,
       :new.Created_Date,
     :new.Created_User,
     pidc_ver,
     pidc_ver_ver,
     :new.pidc_a2l_id   
     );
END;
/


ALTER TRIGGER TRG_T_PIDC_A2L_INS ENABLE;

--------------------------------------------------------
--  DDL for Trigger TABV_PIDC_A2L - Before Update
--- ICDM-1463
--------------------------------------------------------

CREATE OR REPLACE TRIGGER TRG_T_PIDC_A2L_UPDT
BEFORE UPDATE ON T_PIDC_A2L
FOR EACH ROW
DECLARE
pidc_ver number;
     pidc_ver_ver number ;
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
    
    IF :new.PIDC_VERS_ID is null THEN
        pidc_ver_ver := null;
        else
       select pidc.VERSION  into pidc_ver_ver  from T_PIDC_VERSION pidc where pidc.PIDC_VERS_ID = :new.PIDC_VERS_ID;
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
     pidc_ver_ver,
     :old.pidc_a2l_id,
     null   
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
     pidc_ver_ver,
     null,
     :new.pidc_a2l_id   
     );


END;
/
ALTER TRIGGER TRG_T_PIDC_A2L_UPDT ENABLE;

--------------------------------------------------------


spool off