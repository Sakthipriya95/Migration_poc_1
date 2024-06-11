spool c:\temp\create_trigger.log
-----------------------------------
---July-3-2014
------------------------------------

--------------------------------------------------------
--  DDL for Trigger TRG_APIC_ACCESS_RIGHTS_INS - Before insert
--------------------------------------------------------

-- ICDM - 864
CREATE OR REPLACE TRIGGER TRG_APIC_ACCESS_RIGHTS_INS
BEFORE INSERT ON TabV_APIC_ACCESS_RIGHTS
FOR EACH ROW
BEGIN
    IF :new.ACCESSRIGHT_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.ACCESSRIGHT_ID FROM DUAL;
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

ALTER TRIGGER TRG_APIC_ACCESS_RIGHTS_INS ENABLE;

--------------------------------------------------------
--  DDL for Trigger TRG_APIC_ACCESS_RIGHTS_UPD - Before update
--------------------------------------------------------

-- ICDM - 864

CREATE OR REPLACE TRIGGER TRG_APIC_ACCESS_RIGHTS_UPD 
BEFORE UPDATE ON TabV_APIC_ACCESS_RIGHTS
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

ALTER TRIGGER TRG_APIC_ACCESS_RIGHTS_UPD ENABLE;

--------------------------------------------------------
--  DDL for Trigger TRG_APIC_NODE_ACC_INS - Before insert
--------------------------------------------------------

-- ICDM - 864

CREATE OR REPLACE TRIGGER TRG_APIC_NODE_ACC_INS 
BEFORE INSERT ON TabV_APIC_NODE_ACCESS
FOR EACH ROW
BEGIN
    IF :new.NODE_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.NODE_ID FROM DUAL;
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
ALTER TRIGGER TRG_APIC_NODE_ACC_INS ENABLE;

--------------------------------------------------------
--  DDL for Trigger TRG_APIC_NODE_ACC_UPD - Before update
--------------------------------------------------------

-- ICDM - 864

CREATE OR REPLACE TRIGGER TRG_APIC_NODE_ACC_UPD 
BEFORE UPDATE ON TabV_APIC_NODE_ACCESS
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

ALTER TRIGGER TRG_APIC_NODE_ACC_UPD ENABLE;

--------------------------------------------------------
--  DDL for Trigger TRG_APIC_ATTR_INS - Before insert
--------------------------------------------------------

-- ICDM - 864
CREATE OR REPLACE TRIGGER TRG_APIC_ATTR_INS
BEFORE INSERT ON DGS_ICDM.TABV_ATTRIBUTES FOR EACH ROW
BEGIN
    IF :new.ATTR_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.ATTR_ID FROM DUAL;
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

ALTER TRIGGER TRG_APIC_ATTR_INS ENABLE;

--------------------------------------------------------
--  DDL for Trigger TRG_APIC_ATTR_UPD - Before update
--------------------------------------------------------

-- ICDM - 864

CREATE OR REPLACE TRIGGER TRG_APIC_ATTR_UPD
BEFORE UPDATE ON TABV_ATTRIBUTES
FOR EACH ROW
BEGIN
    IF :new.Version = :old.Version THEN
        :new.Version := :old.Version + 1;
    END IF;

    if :new.modified_date IS NULL then
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
     operation_flag
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
     'M'
    );


END;
/

ALTER TRIGGER TRG_APIC_ATTR_UPD ENABLE;

--------------------------------------------------------
--  DDL for Trigger TRG_APIC_ATTR_DEP_INS - Before insert
--------------------------------------------------------

-- ICDM - 864

CREATE OR REPLACE TRIGGER TRG_APIC_ATTR_DEP_INS
BEFORE INSERT ON TabV_ATTR_DEPENDENCIES
FOR EACH ROW
BEGIN
    IF :new.DEPEN_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.DEPEN_ID FROM DUAL;
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

ALTER TRIGGER TRG_APIC_ATTR_DEP_INS ENABLE;

--------------------------------------------------------
--  DDL for Trigger TRG_APIC_ATTR_DEP_UPD - Before update
--------------------------------------------------------

-- ICDM - 864
CREATE OR REPLACE TRIGGER TRG_APIC_ATTR_DEP_UPD
BEFORE UPDATE ON TabV_ATTR_DEPENDENCIES
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
     operation_flag
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
     'M'
    );
END;
/

ALTER TRIGGER TRG_APIC_ATTR_DEP_UPD ENABLE;


--------------------------------------------------------
--  DDL for Trigger TRG_ATTR_GROUPS_INS - Before insert
--------------------------------------------------------

-- ICDM - 864

CREATE OR REPLACE TRIGGER TRG_ATTR_GROUPS_INS
BEFORE INSERT ON TabV_ATTR_GROUPS
FOR EACH ROW
BEGIN
    IF :new.GROUP_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.GROUP_ID FROM DUAL;
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

ALTER TRIGGER TRG_ATTR_GROUPS_INS ENABLE;

--------------------------------------------------------
--  DDL for Trigger TRG_ATTR_GROUPS_UPD - Before update
--------------------------------------------------------

-- ICDM - 864

CREATE OR REPLACE TRIGGER TRG_ATTR_GROUPS_UPD
BEFORE UPDATE ON TabV_ATTR_GROUPS
FOR EACH ROW
BEGIN

    IF :new.Version = :old.Version THEN
        :new.Version := :old.Version + 1;
    END IF;

    if :new.modified_date IS NULL then
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

ALTER TRIGGER TRG_ATTR_GROUPS_UPD ENABLE;

--------------------------------------------------------
--  DDL for Trigger TRG_ATTR_SUPER_GROUPS_INS - Before insert
--------------------------------------------------------

-- ICDM - 864

CREATE OR REPLACE TRIGGER TRG_ATTR_SUPER_GROUPS_INS
BEFORE INSERT ON TabV_ATTR_SUPER_GROUPS
FOR EACH ROW
BEGIN
    IF :new.SUPER_GROUP_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.SUPER_GROUP_ID FROM DUAL;
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

ALTER TRIGGER TRG_ATTR_SUPER_GROUPS_INS ENABLE;

--------------------------------------------------------
--  DDL for Trigger TRG_ATTR_SUPER_GROUPS_UPD - Before update
--------------------------------------------------------

-- ICDM - 864

CREATE OR REPLACE TRIGGER TRG_ATTR_SUPER_GROUPS_UPD
BEFORE UPDATE ON TabV_ATTR_SUPER_GROUPS
FOR EACH ROW
BEGIN

    IF :new.Version = :old.Version THEN
        :new.Version := :old.Version + 1;
    END IF;

    if :new.modified_date IS NULL then
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

ALTER TRIGGER TRG_ATTR_SUPER_GROUPS_UPD ENABLE;
--------------------------------------------------------
--  DDL for Trigger TRG_APIC_ATTR_VAL_INS - Before insert
--------------------------------------------------------
-- ICDM - 864

CREATE OR REPLACE TRIGGER TRG_APIC_ATTR_VAL_INS
BEFORE INSERT ON TabV_ATTR_VALUES
FOR EACH ROW
BEGIN
    IF :new.VALUE_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.VALUE_ID FROM DUAL;
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

ALTER TRIGGER TRG_APIC_ATTR_VAL_INS ENABLE;

--------------------------------------------------------
--  DDL for Trigger TRG_APIC_ATTR_VAL_UPD - Before update
--------------------------------------------------------
-- ICDM - 864

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
            var.pro_rev_id,
            pidc.version
        FROM
            tabv_project_variants var,
            tabv_projectidcard pidc
        WHERE
            pidc.project_id = var.project_id
            AND pidc.pro_rev_id = var.pro_rev_id
            AND var.value_id    = val_id;
    
    --Sub Variant Cursor
    CURSOR pid_svar_cur(val_id tabv_project_sub_variants.value_id%type)
    IS
        SELECT
            pidc.project_id,
            svar.sub_variant_id,
            svar.value_id,
            svar.pro_rev_id,
            pidc.version
        FROM
            tabv_project_sub_variants svar,
            tabv_projectidcard pidc
        WHERE
            pidc.project_id = svar.project_id
            AND pidc.pro_rev_id = svar.pro_rev_id
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
    * Attribute history insert
    *
    *
    */
    INSERT
    INTO
        tabv_attr_history
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
        VALUES
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
                VALUES
                (
                    SeqV_Attributes.NEXTVAL,
                    l_pid_cur.project_id,
                    l_pid_cur.pro_rev_id,
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
                VALUES
                (
                    SeqV_Attributes.NEXTVAL,
                    l_pid_var_cur.project_id,
                    l_pid_var_cur.variant_id,
                    l_pid_var_cur.pro_rev_id,
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
                VALUES
                (
                    SeqV_Attributes.NEXTVAL,
                    l_pid_svar_cur.project_id,
                    l_pid_svar_cur.sub_variant_id,
                    l_pid_svar_cur.pro_rev_id,
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

ALTER TRIGGER TRG_APIC_ATTR_VAL_INS ENABLE;

--------------------------------------------------------
--  DDL for Trigger TRG_ICDM_FILES_INS - Before insert
--------------------------------------------------------
-- ICDM - 864

CREATE OR REPLACE TRIGGER TRG_ICDM_FILES_INS
  BEFORE INSERT ON TABV_ICDM_FILES
  FOR EACH ROW
Begin
    IF :new.FILE_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.FILE_ID FROM DUAL;
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

ALTER TRIGGER TRG_ICDM_FILES_INS ENABLE;

--------------------------------------------------------
--  DDL for Trigger TRG_ICDM_FILES_UPD - Before update
--------------------------------------------------------
-- ICDM - 864

CREATE OR REPLACE TRIGGER TRG_ICDM_FILES_UPD 
  BEFORE UPDATE ON TABV_ICDM_FILES
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

ALTER TRIGGER TRG_ICDM_FILES_UPD ENABLE;

--------------------------------------------------------
--  DDL for Trigger TRG_PIDC_DET_STRUCTURE_INS - Before insert
--------------------------------------------------------
-- ICDM - 864

CREATE OR REPLACE TRIGGER TRG_PIDC_DET_STRUCTURE_INS
  BEFORE INSERT ON TABV_PIDC_DET_STRUCTURE
  FOR EACH ROW
BEGIN
    IF :new.PDS_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.PDS_ID FROM DUAL;
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

ALTER TRIGGER TRG_PIDC_DET_STRUCTURE_INS ENABLE;

--------------------------------------------------------
--  DDL for Trigger TRG_PIDC_DET_STRUCTURE_UPD - Before insert
--------------------------------------------------------
-- ICDM - 864

CREATE OR REPLACE TRIGGER TRG_PIDC_DET_STRUCTURE_UPD
  BEFORE UPDATE ON TABV_PIDC_DET_STRUCTURE
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

ALTER TRIGGER TRG_PIDC_DET_STRUCTURE_UPD ENABLE;

--------------------------------------------------------
--  DDL for Trigger TRG_PROJECTIDCARD_INS - Before insert
--------------------------------------------------------
-- ICDM - 864

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
--  DDL for Trigger TRG_PROJECTIDCARD_UPD - Before update
--------------------------------------------------------
-- ICDM - 864

CREATE OR REPLACE TRIGGER TRG_PROJECTIDCARD_UPD
BEFORE UPDATE ON TabV_PROJECTIDCARD
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
--  DDL for Trigger TRG_PROJECT_ATTR_INS - Before insert
--------------------------------------------------------
-- ICDM - 864

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
--  DDL for Trigger TRG_PROJECT_ATTR_UPD - Before update
--------------------------------------------------------
-- ICDM - 864

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
        :new.Modified_Date := sys_extract_utc(systimestamp);
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
--  DDL for Trigger TRG_PROJ_SUB_VAR_ATTR_INS - Before insert
--------------------------------------------------------
-- ICDM - 864

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

--------------------------------------------------------
--  DDL for Trigger TRG_PROJ_SUB_VAR_ATTR_UPD - Before update
--------------------------------------------------------
-- ICDM - 864

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
        :new.Modified_Date := sys_extract_utc(systimestamp);
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

--------------------------------------------------------
--  DDL for Trigger TRG_PROJECT_SUB_VARIANTS_INS - Before insert
--------------------------------------------------------
-- ICDM - 864

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
        :new.Created_Date := sys_extract_utc(systimestamp);
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
--  DDL for Trigger TRG_PROJECT_SUB_VARIANTS_UPD - Before update
--------------------------------------------------------
-- ICDM - 864

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
        :new.Modified_Date := sys_extract_utc(systimestamp);
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

--------------------------------------------------------
--  DDL for Trigger TRG_PROJECT_VARIANTS_UPD - Before update
--------------------------------------------------------
-- ICDM - 864

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
        :new.Modified_Date := sys_extract_utc(systimestamp);
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

--------------------------------------------------------
--  DDL for Trigger TRG_PROJECT_VARIANTS_INS - Before insert
--------------------------------------------------------
-- ICDM - 864

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
        :new.Created_Date := sys_extract_utc(systimestamp);
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

--------------------------------------------------------
--  DDL for Trigger TRG_UCP_ATTRS_INS - Before insert
--------------------------------------------------------
-- ICDM - 864

CREATE OR REPLACE TRIGGER TRG_UCP_ATTRS_INS
  BEFORE INSERT ON TABV_UCP_ATTRS
  FOR EACH ROW
BEGIN
    IF :new.UCPA_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.UCPA_ID FROM DUAL;
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

ALTER TRIGGER TRG_UCP_ATTRS_INS ENABLE;

--------------------------------------------------------
--  DDL for Trigger TRG_UCP_ATTRS_UPD - Before update
--------------------------------------------------------
-- ICDM - 864

CREATE OR REPLACE TRIGGER TRG_UCP_ATTRS_UPD
  BEFORE UPDATE ON TABV_UCP_ATTRS
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

ALTER TRIGGER TRG_UCP_ATTRS_UPD ENABLE;

--------------------------------------------------------
--  DDL for Trigger TRG_USE_CASES_INS - Before insert
--------------------------------------------------------
-- ICDM - 864

CREATE OR REPLACE TRIGGER TRG_USE_CASES_INS
  BEFORE INSERT ON TABV_USE_CASES
  FOR EACH ROW
BEGIN
    IF :new.USE_CASE_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.USE_CASE_ID FROM DUAL;
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

ALTER TRIGGER TRG_USE_CASES_INS ENABLE;

--------------------------------------------------------
--  DDL for Trigger TRG_USE_CASES_UPD - Before update
--------------------------------------------------------
-- ICDM - 864

CREATE OR REPLACE TRIGGER TRG_USE_CASES_UPD
  BEFORE UPDATE ON TABV_USE_CASES
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

ALTER TRIGGER TRG_USE_CASES_UPD ENABLE;

--------------------------------------------------------
--  DDL for Trigger TRG_UC_GROUP_INS - Before insert
--------------------------------------------------------
-- ICDM - 864

CREATE OR REPLACE TRIGGER TRG_UC_GROUP_INS
  BEFORE INSERT ON TABV_USE_CASE_GROUPS
  FOR EACH ROW
BEGIN
    IF :new.GROUP_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.GROUP_ID FROM DUAL;
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

ALTER TRIGGER TRG_UC_GROUP_INS ENABLE;

--------------------------------------------------------
--  DDL for Trigger TRG_UC_GROUP_UPD - Before update
--------------------------------------------------------
-- ICDM - 864

CREATE OR REPLACE TRIGGER TRG_UC_GROUP_UPD
  BEFORE UPDATE ON TABV_USE_CASE_GROUPS
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

ALTER TRIGGER TRG_UC_GROUP_UPD ENABLE;

--------------------------------------------------------
--  DDL for Trigger TRG_UC_SECTIONS_INS - Before insert
--------------------------------------------------------
-- ICDM - 864

CREATE OR REPLACE TRIGGER TRG_UC_SECTIONS_INS
  BEFORE INSERT ON TABV_USE_CASE_SECTIONS
  FOR EACH ROW
BEGIN
    IF :new.SECTION_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.SECTION_ID FROM DUAL;
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

ALTER TRIGGER TRG_UC_SECTIONS_INS ENABLE;

--------------------------------------------------------
--  DDL for Trigger TRG_UC_SECTIONS_UPD - Before update
--------------------------------------------------------
-- ICDM - 864

CREATE OR REPLACE TRIGGER TRG_UC_SECTIONS_UPD
  BEFORE UPDATE ON TABV_USE_CASE_SECTIONS
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

ALTER TRIGGER TRG_UC_SECTIONS_UPD ENABLE;

--------------------------------------------------------
--  DDL for Trigger TRG_VARIANTS_ATTR_INS - Before insert
--------------------------------------------------------
-- ICDM - 864

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
--  DDL for Trigger TRG_VARIANTS_ATTR_UPD - Before update
--------------------------------------------------------
-- ICDM - 864

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
        :new.Modified_Date := sys_extract_utc(systimestamp);
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
--  DDL for Trigger TRG_RVW_FILES_INS - Before insert
--------------------------------------------------------
-- ICDM - 864

CREATE OR REPLACE TRIGGER TRG_RVW_FILES_INS
  BEFORE INSERT ON T_RVW_FILES
  FOR EACH ROW
Begin
    IF :new.RVW_FILE_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.RVW_FILE_ID FROM DUAL;
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
ALTER TRIGGER TRG_RVW_FILES_INS ENABLE;

--------------------------------------------------------
--  DDL for Trigger TRG_RVW_FILES_UPD - Before update
--------------------------------------------------------
-- ICDM - 864

CREATE OR REPLACE TRIGGER TRG_RVW_FILES_UPD 
  BEFORE UPDATE ON T_RVW_FILES
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

ALTER TRIGGER TRG_RVW_FILES_UPD ENABLE;

--------------------------------------------------------
--  DDL for Trigger TRG_RVW_FUNCTIONS_INS - Before insert
--------------------------------------------------------
-- ICDM - 864
CREATE OR REPLACE TRIGGER TRG_RVW_FUNCTIONS_INS
  BEFORE INSERT ON T_RVW_FUNCTIONS
  FOR EACH ROW
Begin
    IF :new.RVW_FUN_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.RVW_FUN_ID FROM DUAL;
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

ALTER TRIGGER TRG_RVW_FUNCTIONS_INS ENABLE;

--------------------------------------------------------
--  DDL for Trigger TRG_RVW_FUNCTIONS_UPD - Before update
--------------------------------------------------------
-- ICDM - 864

CREATE OR REPLACE TRIGGER TRG_RVW_FUNCTIONS_UPD 
  BEFORE UPDATE ON T_RVW_FUNCTIONS
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

ALTER TRIGGER TRG_RVW_FUNCTIONS_UPD ENABLE;

--------------------------------------------------------
--  DDL for Trigger TRG_RVW_PARAMETERS_INS - Before insert
--------------------------------------------------------
-- ICDM - 864

CREATE OR REPLACE TRIGGER TRG_RVW_PARAMETERS_INS
  BEFORE INSERT ON T_RVW_PARAMETERS
  FOR EACH ROW
Begin
    IF :new.RVW_PARAM_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.RVW_PARAM_ID FROM DUAL;
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

ALTER TRIGGER TRG_RVW_PARAMETERS_INS ENABLE;

--------------------------------------------------------
--  DDL for Trigger TRG_RVW_PARAMETERS_UPD - Before update
--------------------------------------------------------
-- ICDM - 864

CREATE OR REPLACE TRIGGER TRG_RVW_PARAMETERS_UPD 
  BEFORE UPDATE ON T_RVW_PARAMETERS
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

ALTER TRIGGER TRG_RVW_PARAMETERS_UPD ENABLE;

--------------------------------------------------------
--  DDL for Trigger TRG_RVW_PARTICIPANTS_INS - Before insert
--------------------------------------------------------
-- ICDM - 864

CREATE OR REPLACE TRIGGER TRG_RVW_PARTICIPANTS_INS
  BEFORE INSERT ON T_RVW_PARTICIPANTS
  FOR EACH ROW
Begin
    IF :new.PARTICIPANT_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.PARTICIPANT_ID FROM DUAL;
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

ALTER TRIGGER TRG_RVW_PARTICIPANTS_INS ENABLE;

--------------------------------------------------------
--  DDL for Trigger TRG_RVW_PARTICIPANTS_UPD - Before update
--------------------------------------------------------
-- ICDM - 864

CREATE OR REPLACE TRIGGER TRG_RVW_PARTICIPANTS_UPD 
  BEFORE UPDATE ON T_RVW_PARTICIPANTS
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

ALTER TRIGGER TRG_RVW_PARTICIPANTS_UPD ENABLE;

--------------------------------------------------------
--  DDL for Trigger TRG_RVW_RESULTS_INS - Before insert
--------------------------------------------------------
-- ICDM - 864

CREATE OR REPLACE TRIGGER TRG_RVW_RESULTS_INS
  BEFORE INSERT ON T_RVW_RESULTS
  FOR EACH ROW
Begin
    IF :new.RESULT_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.RESULT_ID FROM DUAL;
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

ALTER TRIGGER TRG_RVW_RESULTS_INS ENABLE;

--------------------------------------------------------
--  DDL for Trigger TRG_RVW_RESULTS_UPD - Before update
--------------------------------------------------------
-- ICDM - 864

CREATE OR REPLACE TRIGGER TRG_RVW_RESULTS_UPD 
  BEFORE UPDATE ON T_RVW_RESULTS
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

ALTER TRIGGER TRG_RVW_RESULTS_UPD ENABLE;



--------------------------------------------------------
--  DDL for Trigger TRG_LINKS - Before insert
--------------------------------------------------------

-- ICDM-885

CREATE OR REPLACE TRIGGER TRG_LINKS_INS
  BEFORE INSERT ON T_LINKS   FOR EACH ROW
Begin
    IF :new.LINK_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.LINK_ID FROM DUAL;
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

ALTER TRIGGER TRG_LINKS_INS ENABLE;

--------------------------------------------------------
--  DDL for Trigger TRG_LINKS - Before update
--------------------------------------------------------

-- ICDM-885

CREATE OR REPLACE TRIGGER TRG_LINKS_UPD 
  BEFORE UPDATE ON T_LINKS   FOR EACH ROW
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

ALTER TRIGGER TRG_LINKS_UPD ENABLE;


spool off
