spool c:\temp\create_trigger.log

--------------------------------------------------------
--  May-20-2014
--------------------------------------------------------

--------------------------------------------------------
--  DDL for Trigger TRG_LINKS - Before insert
--------------------------------------------------------

-- ICDM - 763 

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
        :new.Created_Date := sysdate;
    END IF;

    IF :new.Created_User is null THEN
        :new.Created_User := user;
    END IF;


END;
/

ALTER TRIGGER TRG_LINKS_INS ENABLE;

--------------------------------------------------------
--  May-20-2014
--------------------------------------------------------

--------------------------------------------------------
--  DDL for Trigger TRG_LINKS - Before update
--------------------------------------------------------

-- ICDM - 763 

CREATE OR REPLACE TRIGGER TRG_LINKS_UPD 
  BEFORE UPDATE ON T_LINKS   FOR EACH ROW
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

END;
/

ALTER TRIGGER TRG_LINKS_UPD ENABLE;


--------------------------------------------------------
--  June-02-2014
--------------------------------------------------------

--------------------------------------------------------
--  DDL for Trigger TabV_ATTR_VALUES - Before update
--------------------------------------------------------

--  ICDM-793 

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
        :new.modified_date := sysdate;
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

ALTER TRIGGER TRG_APIC_ATTR_VAL_UPD ENABLE;

spool off;