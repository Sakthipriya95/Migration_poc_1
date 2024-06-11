spool c:\temp\create_trigger.log
--------------------------------------------------------
--  Datei erstellt -Freitag-Juni-28-2013   
--------------------------------------------------------
--------------------------------------------------------
--  DDL for Trigger TRG_APIC_ACCESS_RIGHTS_INS
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "TRG_APIC_ACCESS_RIGHTS_INS" 
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
		:new.Created_Date := sysdate;
	END IF;

	IF :new.Created_User is null THEN
		:new.Created_User := user;
	END IF;


END;
/
ALTER TRIGGER "TRG_APIC_ACCESS_RIGHTS_INS" ENABLE;
--------------------------------------------------------
--  DDL for Trigger TRG_APIC_ACCESS_RIGHTS_UPD
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "TRG_APIC_ACCESS_RIGHTS_UPD" 
BEFORE UPDATE ON TabV_APIC_ACCESS_RIGHTS
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
END;
/
ALTER TRIGGER "TRG_APIC_ACCESS_RIGHTS_UPD" ENABLE;
--------------------------------------------------------
--  DDL for Trigger TRG_APIC_ATTR_DEL
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "TRG_APIC_ATTR_DEL" 
  BEFORE DELETE
  on tabv_attributes
  for each row

BEGIN
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
     'D'
    );
END;
/
ALTER TRIGGER "TRG_APIC_ATTR_DEL" ENABLE;
--------------------------------------------------------
--  DDL for Trigger TRG_APIC_ATTR_DEP_DEL
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "TRG_APIC_ATTR_DEP_DEL" 
  BEFORE DELETE
  on tabv_attr_dependencies
  for each row

BEGIN
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
     'D'
    );
END;
/
ALTER TRIGGER "TRG_APIC_ATTR_DEP_DEL" ENABLE;
--------------------------------------------------------
--  DDL for Trigger TRG_APIC_ATTR_DEP_INS
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "TRG_APIC_ATTR_DEP_INS" 
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
		:new.Created_Date := sysdate;
	END IF;

	IF :new.Created_User is null THEN
		:new.Created_User := user;
	END IF;


END;
/
ALTER TRIGGER "TRG_APIC_ATTR_DEP_INS" ENABLE;
--------------------------------------------------------
--  DDL for Trigger TRG_APIC_ATTR_DEP_UPD
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "TRG_APIC_ATTR_DEP_UPD" 
BEFORE UPDATE ON TabV_ATTR_DEPENDENCIES
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
ALTER TRIGGER "TRG_APIC_ATTR_DEP_UPD" ENABLE;
--------------------------------------------------------
--  DDL for Trigger TRG_APIC_ATTR_INS
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "TRG_APIC_ATTR_INS" 
BEFORE INSERT ON TABV_ATTRIBUTES
FOR EACH ROW
BEGIN
	IF :new.ATTR_ID is null THEN
		SELECT SeqV_Attributes.nextval INTO :new.ATTR_ID FROM DUAL;
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
ALTER TRIGGER "TRG_APIC_ATTR_INS" ENABLE;
--------------------------------------------------------
--  DDL for Trigger TRG_APIC_ATTR_UPD
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "TRG_APIC_ATTR_UPD" 
BEFORE UPDATE ON TABV_ATTRIBUTES
FOR EACH ROW
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
ALTER TRIGGER "TRG_APIC_ATTR_UPD" ENABLE;
--------------------------------------------------------
--  DDL for Trigger TRG_APIC_ATTR_VAL_DEL
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "TRG_APIC_ATTR_VAL_DEL" 
  BEFORE DELETE
  on tabv_attr_values
  for each row

BEGIN
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
     'D'
    );
END;
/
ALTER TRIGGER "TRG_APIC_ATTR_VAL_DEL" ENABLE;
--------------------------------------------------------
--  DDL for Trigger TRG_APIC_ATTR_VAL_INS
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "TRG_APIC_ATTR_VAL_INS" 
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
		:new.Created_Date := sysdate;
	END IF;

	IF :new.Created_User is null THEN
		:new.Created_User := user;
	END IF;


END;
/
ALTER TRIGGER "TRG_APIC_ATTR_VAL_INS" ENABLE;
--------------------------------------------------------
--  DDL for Trigger TRG_APIC_ATTR_VAL_UPD
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "TRG_APIC_ATTR_VAL_UPD" 
BEFORE UPDATE ON TabV_ATTR_VALUES
FOR EACH ROW
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

END;
/
ALTER TRIGGER "TRG_APIC_ATTR_VAL_UPD" ENABLE;
--------------------------------------------------------
--  DDL for Trigger TRG_APIC_NODE_ACC_INS
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "TRG_APIC_NODE_ACC_INS" 
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
		:new.Created_Date := sysdate;
	END IF;

	IF :new.Created_User is null THEN
		:new.Created_User := user;
	END IF;


END;
/
ALTER TRIGGER "TRG_APIC_NODE_ACC_INS" ENABLE;
--------------------------------------------------------
--  DDL for Trigger TRG_APIC_NODE_ACC_UPD
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "TRG_APIC_NODE_ACC_UPD" 
BEFORE UPDATE ON TabV_APIC_NODE_ACCESS
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
END;
/
ALTER TRIGGER "TRG_APIC_NODE_ACC_UPD" ENABLE;
--------------------------------------------------------
--  DDL for Trigger TRG_ATTR_GROUPS_DEL
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "TRG_ATTR_GROUPS_DEL" 
  BEFORE DELETE
  on tabv_attr_groups
  for each row

BEGIN
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
     'D'
    );
END;
/
ALTER TRIGGER "TRG_ATTR_GROUPS_DEL" ENABLE;
--------------------------------------------------------
--  DDL for Trigger TRG_ATTR_GROUPS_INS
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "TRG_ATTR_GROUPS_INS" 
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
		:new.Created_Date := sysdate;
	END IF;

	IF :new.Created_User is null THEN
		:new.Created_User := user;
	END IF;


END;
/
ALTER TRIGGER "TRG_ATTR_GROUPS_INS" ENABLE;
--------------------------------------------------------
--  DDL for Trigger TRG_ATTR_GROUPS_UPD
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "TRG_ATTR_GROUPS_UPD" 
BEFORE UPDATE ON TabV_ATTR_GROUPS
FOR EACH ROW
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
ALTER TRIGGER "TRG_ATTR_GROUPS_UPD" ENABLE;
--------------------------------------------------------
--  DDL for Trigger TRG_ATTR_SUPER_GROUPS_DEL
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "TRG_ATTR_SUPER_GROUPS_DEL" 
  BEFORE DELETE
  on tabv_attr_super_groups
  for each row

BEGIN
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
     'D'
     );
END;
/
ALTER TRIGGER "TRG_ATTR_SUPER_GROUPS_DEL" ENABLE;
--------------------------------------------------------
--  DDL for Trigger TRG_ATTR_SUPER_GROUPS_INS
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "TRG_ATTR_SUPER_GROUPS_INS" 
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
		:new.Created_Date := sysdate;
	END IF;

	IF :new.Created_User is null THEN
		:new.Created_User := user;
	END IF;


END;
/
ALTER TRIGGER "TRG_ATTR_SUPER_GROUPS_INS" ENABLE;
--------------------------------------------------------
--  DDL for Trigger TRG_ATTR_SUPER_GROUPS_UPD
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "TRG_ATTR_SUPER_GROUPS_UPD" 
BEFORE UPDATE ON TabV_ATTR_SUPER_GROUPS
FOR EACH ROW
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
ALTER TRIGGER "TRG_ATTR_SUPER_GROUPS_UPD" ENABLE;
--------------------------------------------------------
--  DDL for Trigger TRG_A2L_BC_INS
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "TRG_A2L_BC_INS" 
BEFORE INSERT ON TA2L_BCS 
FOR EACH ROW
BEGIN
	IF :new.ID IS NULL THEN 
    SELECT SeqV_TA2L.nextval INTO :new.ID FROM DUAL; 
  END IF;
END;
/
ALTER TRIGGER "TRG_A2L_BC_INS" ENABLE;
--------------------------------------------------------
--  DDL for Trigger TRG_PROJECT_ATTR_DEL
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "TRG_PROJECT_ATTR_DEL" 
  BEFORE DELETE
  on TABV_PROJECT_ATTR
  for each row

BEGIN
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
     :old.value_id ,
     'D'
     );
END;
/
ALTER TRIGGER "TRG_PROJECT_ATTR_DEL" ENABLE;
--------------------------------------------------------
--  DDL for Trigger TRG_PROJECT_ATTR_INS
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "TRG_PROJECT_ATTR_INS" 
BEFORE INSERT ON TabV_PROJECT_ATTR
FOR EACH ROW
BEGIN
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


END;
/
ALTER TRIGGER "TRG_PROJECT_ATTR_INS" ENABLE;
--------------------------------------------------------
--  DDL for Trigger TRG_PROJECT_ATTR_UPD
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "TRG_PROJECT_ATTR_UPD" 
BEFORE UPDATE ON TabV_PROJECT_ATTR
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

END;
/
ALTER TRIGGER "TRG_PROJECT_ATTR_UPD" ENABLE;
--------------------------------------------------------
--  DDL for Trigger TRG_PROJECTIDCARD_DEL
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "TRG_PROJECTIDCARD_DEL" 
  BEFORE DELETE
  on TABV_PROJECTIDCARD
  for each row

BEGIN

  delete from tabv_attr_history
    where project_id = :old.project_id;
  
  delete from tabv_pid_history
    where project_id = :old.project_id;
    
END;
/
ALTER TRIGGER "TRG_PROJECTIDCARD_DEL" ENABLE;
--------------------------------------------------------
--  DDL for Trigger TRG_PROJECTIDCARD_INS
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "TRG_PROJECTIDCARD_INS" 
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

END;
/
ALTER TRIGGER "TRG_PROJECTIDCARD_INS" ENABLE;

  CREATE OR REPLACE TRIGGER "TRG_PROJECTIDCARD_INSAFTER" 
AFTER INSERT ON TabV_PROJECTIDCARD
FOR EACH ROW
BEGIN

	insert into tabv_pid_history
	 (
	  project_id,
	  pro_rev_id,
	  PID_STATUS_ID,
	  created_date,
	  created_user,
	  pidhist_id
	 )
	values
	 (
	   :new.project_id,
	   :new.pro_rev_id,
	   1,
	   :new.created_date,
	   :new.created_user,
	   SeqV_Attributes.nextval
	 );
END;
/
ALTER TRIGGER "TRG_PROJECTIDCARD_INSAFTER" ENABLE;
--------------------------------------------------------
--  DDL for Trigger TRG_PROJECTIDCARD_UPD
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "TRG_PROJECTIDCARD_UPD" 
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
END;
/
ALTER TRIGGER "TRG_PROJECTIDCARD_UPD" ENABLE;
--------------------------------------------------------
--  DDL for Trigger TRG_PROJECT_SUB_VARIANTS_INS
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "TRG_PROJECT_SUB_VARIANTS_INS" 
BEFORE INSERT ON TabV_PROJECT_SUB_VARIANTS
FOR EACH ROW
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


END;
/
ALTER TRIGGER "TRG_PROJECT_SUB_VARIANTS_INS" ENABLE;
--------------------------------------------------------
--  DDL for Trigger TRG_PROJECT_SUB_VARIANTS_UPD
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "TRG_PROJECT_SUB_VARIANTS_UPD" 
BEFORE UPDATE ON TabV_PROJECT_SUB_VARIANTS
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
END;
/
ALTER TRIGGER "TRG_PROJECT_SUB_VARIANTS_UPD" ENABLE;
--------------------------------------------------------
--  DDL for Trigger TRG_PROJECT_VARIANTS_DEL
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "TRG_PROJECT_VARIANTS_DEL" 
  BEFORE DELETE
  on TABV_PROJECT_VARIANTS
  for each row

BEGIN
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
     'D'
     );
END;
/
ALTER TRIGGER "TRG_PROJECT_VARIANTS_DEL" ENABLE;
--------------------------------------------------------
--  DDL for Trigger TRG_PROJECT_VARIANTS_INS
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "TRG_PROJECT_VARIANTS_INS" 
BEFORE INSERT ON TabV_PROJECT_VARIANTS
FOR EACH ROW
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


END;
/
ALTER TRIGGER "TRG_PROJECT_VARIANTS_INS" ENABLE;
--------------------------------------------------------
--  DDL for Trigger TRG_PROJECT_VARIANTS_UPD
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "TRG_PROJECT_VARIANTS_UPD" 
BEFORE UPDATE ON TabV_PROJECT_VARIANTS
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
END;
/
ALTER TRIGGER "TRG_PROJECT_VARIANTS_UPD" ENABLE;
--------------------------------------------------------
--  DDL for Trigger TRG_PROJ_SUB_VAR_ATTR_INS
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "TRG_PROJ_SUB_VAR_ATTR_INS" 
BEFORE INSERT ON TabV_PROJ_SUB_VARIANTS_ATTR
FOR EACH ROW
BEGIN
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


END;
/
ALTER TRIGGER "TRG_PROJ_SUB_VAR_ATTR_INS" ENABLE;
--------------------------------------------------------
--  DDL for Trigger TRG_PROJ_SUB_VAR_ATTR_UPD
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "TRG_PROJ_SUB_VAR_ATTR_UPD" 
BEFORE UPDATE ON TabV_PROJ_SUB_VARIANTS_ATTR
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
END;
/
ALTER TRIGGER "TRG_PROJ_SUB_VAR_ATTR_UPD" ENABLE;
--------------------------------------------------------
--  DDL for Trigger TRG_VARIANTS_ATTR_DEL
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "TRG_VARIANTS_ATTR_DEL" 
  BEFORE DELETE
  on TABV_VARIANTS_ATTR
  for each row

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
END;
/
ALTER TRIGGER "TRG_VARIANTS_ATTR_DEL" ENABLE;
--------------------------------------------------------
--  DDL for Trigger TRG_VARIANTS_ATTR_INS
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "TRG_VARIANTS_ATTR_INS" 
BEFORE INSERT ON TabV_VARIANTS_ATTR
FOR EACH ROW
BEGIN
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


END;
/
ALTER TRIGGER "TRG_VARIANTS_ATTR_INS" ENABLE;
--------------------------------------------------------
--  DDL for Trigger TRG_VARIANTS_ATTR_UPD
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "TRG_VARIANTS_ATTR_UPD" 
BEFORE UPDATE ON TabV_VARIANTS_ATTR
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
END;
/
ALTER TRIGGER "TRG_VARIANTS_ATTR_UPD" ENABLE;

spool off;