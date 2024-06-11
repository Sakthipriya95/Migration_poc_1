ALTER TABLE T_SSD_FEATURES 
ADD (ICDMSYNC VARCHAR2(1) )
;

create or replace TRIGGER TRG_APIC_ATTR_VAL_INS
BEFORE INSERT ON TabV_ATTR_VALUES
FOR EACH ROW

DECLARE

	cursor getAttrSSDMapping (attrID IN NUMBER) IS
		select ssdf.Feature_ID
		     , ssdf.iCDMSync
			   , attr.VALUE_TYPE_ID
		  from T_SSD_FEATURES  ssdf
         , TABV_ATTRIBUTES attr
		 where APIC_ATTR_ID = attrID
       and ssdf.APIC_ATTR_ID = attr.ATTR_ID
	;
	
	ssdFeatureInfo getAttrSSDMapping%ROWTYPE;

BEGIN
	--
	-- set default values
	--
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

	--
	-- insert value into T_SSD_VALUES if required
	--
	open getAttrSSDMapping(:new.ATTR_ID);
	fetch getAttrSSDMapping into ssdFeatureInfo;
	close getAttrSSDMapping;
	
	if (nvl(ssdFeatureInfo.iCDMSync, 'N') = 'Y') then
		insert into T_SSD_VALUES
			(  FEATURE_ID
			 , VALUE_TEXT
			 , APIC_VALUE_ID
			 , ICDM_ONLY
			 , VERSION
			)
		values
			(  ssdFeatureInfo.Feature_ID
			 , decode(ssdFeatureInfo.VALUE_TYPE_ID, 1, :new.TEXTVALUE_ENG, 2, :new.NUMVALUE, '--')
			 , :new.VALUE_ID
			 , 'N'
			 , 1
			)
		;
	end if;
	
END;
/

create or replace TRIGGER TRG_APIC_ATTR_VAL_UPD
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

	-- SSD Feature mapping cursor
	cursor getAttrSSDMapping (attrID IN NUMBER) IS
		select ssdf.Feature_ID
		     , ssdf.iCDMSync
			   , attr.VALUE_TYPE_ID
		  from T_SSD_FEATURES  ssdf
         , TABV_ATTRIBUTES attr
		 where APIC_ATTR_ID = attrID
       and ssdf.APIC_ATTR_ID = attr.ATTR_ID
	;
	
	-- SSD Value mapping cursor
	cursor getAttrValueSSDMapping (attrValueID IN NUMBER) IS
		select VALUE_ID
		  from T_SSD_VALUES
		 where APIC_VALUE_ID = attrValueID
	;
	
    l_pid_cur pid_cur%ROWTYPE;
    l_pid_var_cur pid_var_cur%ROWTYPE;
    l_pid_svar_cur pid_svar_cur%ROWTYPE;

	ssdFeatureInfo getAttrSSDMapping%ROWTYPE;
	
	ssdValueID NUMBER;
	
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

	--
	-- update value in SSD database if required
	--
	open getAttrValueSSDMapping(:old.VALUE_ID);
	fetch getAttrValueSSDMapping into ssdValueID;
	close getAttrValueSSDMapping;
	
	if (nvl(ssdValueID, '0') > 0) then
		-- value is mapped to SSD database
		
		-- get the attribute information
		open getAttrSSDMapping(:old.ATTR_ID);
		fetch getAttrSSDMapping into ssdFeatureInfo;
		close getAttrSSDMapping;
		
		if ((ssdFeatureInfo.VALUE_TYPE_ID = 1) AND (:New.TEXTVALUE_ENG is not null)) then
			update T_SSD_VALUES
				set VALUE_TEXT = :New.TEXTVALUE_ENG
			  where VALUE_ID = ssdValueID
			;  

		elsif ((ssdFeatureInfo.VALUE_TYPE_ID = 2) AND (:New.NUMVALUE is not null)) then
			update T_SSD_VALUES
				set VALUE_TEXT = :New.NUMVALUE
			  where VALUE_ID = ssdValueID
			;  

		end if;
	
	end if;
	

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
