spool c:\temp\create_trigger.log
--------------------------------------------------------
--  April-28-2014
--------------------------------------------------------

--------------------------------------------------------
--  DDL for Trigger TRG_APIC_ATTR_VAL_UPD - Before update
--------------------------------------------------------

-- ICDM - 688 

CREATE OR REPLACE TRIGGER TRG_APIC_ATTR_VAL_UPD
BEFORE UPDATE ON TabV_ATTR_VALUES
FOR EACH ROW
DECLARE
pidc_id number;
var_id number;
svar_id number;
attr_lev number;
var_pro_rev_id number;
ver number;
cnt number;
BEGIN
    IF :new.Version = :old.Version THEN
        :new.Version := :old.Version + 1;
    END IF;

    if :new.modified_date IS NULL then
        :new.modified_date := sysdate;
    end if;

    if :new.modified_user IS NULL then
        :new.modified_user := user;
    end if;

   insert into tabv_attr_history
    (
     id,
     table_name,
     value_id,
     attr_id,
     value_desc_eng,
     value_desc_ger,
     numValue,
     dateValue,
     textValue_eng,
     textValue_ger,
     boolValue,
     otherValue,
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
     'tabv_attr_values',
     :old.value_id,
     :old.attr_id,
     :old.value_desc_eng,
     :old.value_desc_ger,
     :old.numValue,
     :old.dateValue,
     :old.textValue_eng,
     :old.textValue_ger,
     :old.boolValue,
     :old.otherValue,
     :old.created_date,
     :old.modified_date,
     :old.created_user,
     :old.modified_user,
     :old.deleted_flag,
     'M'
    );


select attr_level into attr_lev from TABV_ATTRIBUTES where attr_id=:old.attr_id;

IF attr_lev = -1 THEN
select count(*) into cnt from TABV_PROJECTIDCARD where value_id = :old.value_id;
IF cnt = 1 THEN
select project_id into pidc_id from TABV_PROJECTIDCARD where value_id = :old.value_id;
END IF;
END IF;


IF attr_lev = -2 THEN
select count(*) into cnt from TABV_PROJECT_VARIANTS where value_id = :old.value_id; 
IF cnt = 1 THEN
select variant_id into var_id from TABV_PROJECT_VARIANTS where value_id = :old.value_id; 
END IF;
select  count(*) into cnt from TABV_PROJECT_VARIANTS where variant_id = var_id;
IF cnt = 1 THEN
select project_id into pidc_id from TABV_PROJECT_VARIANTS where variant_id = var_id; 
END IF;
END IF;


IF attr_lev = -3 THEN
select count(*) into cnt from TABV_PROJECT_SUB_VARIANTS where value_id = :old.value_id; 
IF cnt = 1 THEN
select sub_variant_id into svar_id from TABV_PROJECT_SUB_VARIANTS where value_id = :old.value_id; 
END IF;
select count(*) into cnt from TABV_PROJECT_SUB_VARIANTS where sub_variant_id = svar_id;
IF cnt = 1 THEN
select project_id into pidc_id from TABV_PROJECT_SUB_VARIANTS where sub_variant_id = svar_id;
END IF;
END IF;

IF pidc_id is not null THEN

select pro_rev_id,version into var_pro_rev_id,ver from TABV_PROJECTIDCARD where project_id = pidc_id;

insert into t_pidc_change_history
(
ID,
PIDC_ID,
VAR_ID,
SVAR_ID,
PRO_REV_ID,
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
values
(
SeqV_Attributes.NEXTVAL,
pidc_id,
var_id,
svar_id,
var_pro_rev_id,
:new.modified_date,
:new.modified_user,
ver,
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
END IF;

END;
/
ALTER TRIGGER TRG_APIC_ATTR_VAL_UPD ENABLE;
spool off;
