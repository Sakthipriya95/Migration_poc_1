spool c:\temp\create_trigger.log

--------------------------------------------------------
--  DDL for Trigger TRG_PROJECTIDCARD_UPD - Before Update
--- ICDM-1542
--------------------------------------------------------
CREATE OR REPLACE TRIGGER TRG_PROJECTIDCARD_UPD
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

ALTER TRIGGER TRG_PROJECTIDCARD_UPD ENABLE;

--------------------------------------------------------
--  DDL for Trigger TRG_PROJECTIDCARD_DEL - Before Delete
--- ICDM-1617
--------------------------------------------------------
CREATE OR REPLACE TRIGGER TRG_PROJECTIDCARD_DEL
  BEFORE DELETE
  on TABV_PROJECTIDCARD
  for each row

BEGIN

  delete from tabv_attr_history
    where project_id = :old.project_id;

  delete from t_pidc_a2l
    where project_id = :old.project_id;

  delete from t_pidc_version
    where project_id = :old.project_id;

END;
/
ALTER TRIGGER TRG_PROJECTIDCARD_DEL ENABLE;

spool off
