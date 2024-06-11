spool c:\temp\create_trigger.log


--------------------------------------------------------
--  DDL for Trigger T_PIDC_A2L - Before UPDATE
--- ICDM-1585
--------------------------------------------------------

CREATE OR REPLACE TRIGGER TRG_T_PIDC_A2L_UPDT
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
    IF :new.Modified_Date is null THEN
        :new.Modified_Date := sys_extract_utc(systimestamp);
    END IF;

    IF :new.Modified_User is null THEN
        :new.Modified_User := user;
    END IF;
  -- when an a2l file is unmapped
    IF :new.PIDC_VERS_ID is null THEN
        new_pidc_ver_ver := null;
         select pidc.VERSION  into old_pidc_ver_ver  from T_PIDC_VERSION pidc where pidc.PIDC_VERS_ID = :old.PIDC_VERS_ID;
         --when an a2l is re-mapped
        else
       select pidc.VERSION  into new_pidc_ver_ver  from T_PIDC_VERSION pidc where pidc.PIDC_VERS_ID = :new.PIDC_VERS_ID;
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
END;
/


ALTER TRIGGER TRG_T_PIDC_A2L_UPDT ENABLE;
spool off
