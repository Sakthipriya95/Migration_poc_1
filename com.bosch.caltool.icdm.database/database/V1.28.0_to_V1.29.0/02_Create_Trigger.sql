spool c:\temp\02_Create_Trigger.log

--------------------------------------------------------
----ICDM-2561 - focus matrix review Database scripts

-- Triggers for T_FOCUS_MATRIX_VERSION
--------------------------------------------------------

--Insert Trigger
CREATE OR REPLACE TRIGGER TRG_FOCUS_MATRIX_VERS_INS 
  BEFORE INSERT ON T_FOCUS_MATRIX_VERSION
  FOR EACH ROW
Begin
    IF :new.FM_VERS_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.FM_VERS_ID FROM DUAL;
    END IF;

    IF :new.VERSION is null THEN
        :new.VERSION := 1;
    END IF;

    IF :new.CREATED_DATE is null THEN
        :new.CREATED_DATE := sys_extract_utc(systimestamp);
    END IF;

    IF :new.CREATED_USER is null THEN
        :new.CREATED_USER := user;
    END IF;

END;
/
ALTER TRIGGER TRG_FOCUS_MATRIX_VERS_INS ENABLE;

--- Update trigger
CREATE OR REPLACE TRIGGER TRG_FOCUS_MATRIX_VERS_UPDT 
BEFORE UPDATE ON T_FOCUS_MATRIX_VERSION
FOR EACH ROW
BEGIN
    IF :new.VERSION = :old.VERSION THEN
        :new.VERSION := :old.VERSION + 1;
    END IF;

    if :new.MODIFIED_DATE IS NULL or NOT UPDATING('Modified_Date') then
        :new.MODIFIED_DATE := sys_extract_utc(systimestamp);
    end if;

    if :new.MODIFIED_USER IS NULL then
        :new.MODIFIED_USER := user;
    end if;
 
END;
/
ALTER TRIGGER TRG_FOCUS_MATRIX_VERS_UPDT ENABLE;

--------------------------------------------------------
-- Triggers for T_FOCUS_MATRIX_VERSION_ATTR
--------------------------------------------------------

--Insert Trigger
CREATE OR REPLACE TRIGGER TRG_FM_VERS_ATTR_INS 
  BEFORE INSERT ON T_FOCUS_MATRIX_VERSION_ATTR
  FOR EACH ROW
Begin
    IF :new.FMV_ATTR_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.FMV_ATTR_ID FROM DUAL;
    END IF;

    IF :new.VERSION is null THEN
        :new.VERSION := 1;
    END IF;

    IF :new.CREATED_DATE is null THEN
        :new.CREATED_DATE := sys_extract_utc(systimestamp);
    END IF;

    IF :new.CREATED_USER is null THEN
        :new.CREATED_USER := user;
    END IF;

END;
/
ALTER TRIGGER TRG_FM_VERS_ATTR_INS ENABLE;

--- Update trigger
CREATE OR REPLACE TRIGGER TRG_FM_VERS_ATTR_UPDT 
    BEFORE UPDATE ON T_FOCUS_MATRIX_VERSION_ATTR
    FOR EACH ROW
BEGIN
    IF :new.VERSION = :old.VERSION THEN
        :new.VERSION := :old.VERSION + 1;
    END IF;

    if :new.MODIFIED_DATE IS NULL or NOT UPDATING('Modified_Date') then
        :new.MODIFIED_DATE := sys_extract_utc(systimestamp);
    end if;

    if :new.MODIFIED_USER IS NULL then
        :new.MODIFIED_USER := user;
    end if;
 
END;
/
ALTER TRIGGER TRG_FM_VERS_ATTR_UPDT ENABLE;

--------------------------------------------------------
----ICDM-2600 - A2l WP Responsibilities

-- Triggers for T_A2L_RESP
--------------------------------------------------------

--Insert Trigger
CREATE OR REPLACE TRIGGER TRG_A2L_RESP_INS 
  BEFORE INSERT ON T_A2L_RESP
  FOR EACH ROW
Begin
    IF :new.A2L_RESP_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.A2L_RESP_ID FROM DUAL;
    END IF;

    IF :new.VERSION is null THEN
        :new.VERSION := 1;
    END IF;

    IF :new.CREATED_DATE is null THEN
        :new.CREATED_DATE := sys_extract_utc(systimestamp);
    END IF;

    IF :new.CREATED_USER is null THEN
        :new.CREATED_USER := user;
    END IF;

END;
/
ALTER TRIGGER TRG_A2L_RESP_INS ENABLE;

--Update trigger
CREATE OR REPLACE TRIGGER TRG_A2L_RESP_UPDT 
BEFORE UPDATE ON T_A2L_RESP
FOR EACH ROW
BEGIN
    IF :new.VERSION = :old.VERSION THEN
        :new.VERSION := :old.VERSION + 1;
    END IF;

    if :new.MODIFIED_DATE IS NULL or NOT UPDATING('Modified_Date') then
        :new.MODIFIED_DATE := sys_extract_utc(systimestamp);
    end if;

    if :new.MODIFIED_USER IS NULL then
        :new.MODIFIED_USER := user;
    end if;
 
END;
/
ALTER TRIGGER TRG_A2L_RESP_UPDT ENABLE;

--------------------------------------------------------
-- Triggers for T_WP_RESP
--------------------------------------------------------
--Insert Trigger
CREATE OR REPLACE TRIGGER TRG_WP_RESP_INS 
  BEFORE INSERT ON T_WP_RESP
  FOR EACH ROW
Begin
    IF :new.RESP_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.RESP_ID FROM DUAL;
    END IF;

    IF :new.VERSION is null THEN
        :new.VERSION := 1;
    END IF;

    IF :new.CREATED_DATE is null THEN
        :new.CREATED_DATE := sys_extract_utc(systimestamp);
    END IF;

    IF :new.CREATED_USER is null THEN
        :new.CREATED_USER := user;
    END IF;

END;
/
ALTER TRIGGER TRG_WP_RESP_INS ENABLE;

--Update trigger
CREATE OR REPLACE TRIGGER TRG_WP_RESP_UPDT 
BEFORE UPDATE ON T_WP_RESP
FOR EACH ROW
BEGIN
    IF :new.VERSION = :old.VERSION THEN
        :new.VERSION := :old.VERSION + 1;
    END IF;

    if :new.MODIFIED_DATE IS NULL or NOT UPDATING('Modified_Date') then
        :new.MODIFIED_DATE := sys_extract_utc(systimestamp);
    end if;

    if :new.MODIFIED_USER IS NULL then
        :new.MODIFIED_USER := user;
    end if;
 
END;
/
ALTER TRIGGER TRG_WP_RESP_UPDT ENABLE;

--------------------------------------------------------
-- Triggers for T_A2L_GROUP 
--------------------------------------------------------
--Insert Trigger
CREATE OR REPLACE TRIGGER TRG_A2L_GROUP_INS 
  BEFORE INSERT ON T_A2L_GROUP
  FOR EACH ROW
Begin
    IF :new.GROUP_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.GROUP_ID FROM DUAL;
    END IF;

    IF :new.VERSION is null THEN
        :new.VERSION := 1;
    END IF;

    IF :new.CREATED_DATE is null THEN
        :new.CREATED_DATE := sys_extract_utc(systimestamp);
    END IF;

    IF :new.CREATED_USER is null THEN
        :new.CREATED_USER := user;
    END IF;

END;
/
ALTER TRIGGER TRG_A2L_GROUP_INS ENABLE;

--Update trigger
CREATE OR REPLACE TRIGGER TRG_A2L_GROUP_UPDT 
BEFORE UPDATE ON T_A2L_GROUP
FOR EACH ROW
BEGIN
    IF :new.VERSION = :old.VERSION THEN
        :new.VERSION := :old.VERSION + 1;
    END IF;

    if :new.MODIFIED_DATE IS NULL or NOT UPDATING('Modified_Date') then
        :new.MODIFIED_DATE := sys_extract_utc(systimestamp);
    end if;

    if :new.MODIFIED_USER IS NULL then
        :new.MODIFIED_USER := user;
    end if;
 
END;
/
ALTER TRIGGER TRG_A2L_GROUP_UPDT ENABLE;

--------------------------------------------------------
-- Triggers for T_A2L_GRP_PARAM 
--------------------------------------------------------
--Insert Trigger
CREATE OR REPLACE TRIGGER TRG_A2L_GRP_PARAM_INS 
  BEFORE INSERT ON T_A2L_GRP_PARAM
  FOR EACH ROW
Begin
    IF :new.A2L_PARAM_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.A2L_PARAM_ID FROM DUAL;
    END IF;

    IF :new.VERSION is null THEN
        :new.VERSION := 1;
    END IF;

    IF :new.CREATED_DATE is null THEN
        :new.CREATED_DATE := sys_extract_utc(systimestamp);
    END IF;

    IF :new.CREATED_USER is null THEN
        :new.CREATED_USER := user;
    END IF;

END;
/
ALTER TRIGGER TRG_A2L_GRP_PARAM_INS ENABLE;

--Update trigger
CREATE OR REPLACE TRIGGER TRG_A2L_GRP_PARAM_UPDT 
BEFORE UPDATE ON T_A2L_GRP_PARAM
FOR EACH ROW
BEGIN
    IF :new.VERSION = :old.VERSION THEN
        :new.VERSION := :old.VERSION + 1;
    END IF;

    if :new.MODIFIED_DATE IS NULL or NOT UPDATING('Modified_Date') then
        :new.MODIFIED_DATE := sys_extract_utc(systimestamp);
    end if;

    if :new.MODIFIED_USER IS NULL then
        :new.MODIFIED_USER := user;
    end if;
 
END;
/
ALTER TRIGGER TRG_A2L_GRP_PARAM_UPDT ENABLE;

--------------------------------------------------------
-- Triggers for T_A2L_WP_RESP 
--------------------------------------------------------
--Insert Trigger
CREATE OR REPLACE TRIGGER TRG_A2L_WP_RESP_INS 
  BEFORE INSERT ON T_A2L_WP_RESP
  FOR EACH ROW
Begin
    IF :new.A2L_WP_RESP_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.A2L_WP_RESP_ID FROM DUAL;
    END IF;

    IF :new.VERSION is null THEN
        :new.VERSION := 1;
    END IF;

    IF :new.CREATED_DATE is null THEN
        :new.CREATED_DATE := sys_extract_utc(systimestamp);
    END IF;

    IF :new.CREATED_USER is null THEN
        :new.CREATED_USER := user;
    END IF;

END;
/
ALTER TRIGGER TRG_A2L_WP_RESP_INS ENABLE;

--Update trigger
CREATE OR REPLACE TRIGGER TRG_A2L_WP_RESP_UPDT 
BEFORE UPDATE ON T_A2L_WP_RESP
FOR EACH ROW
BEGIN
    IF :new.VERSION = :old.VERSION THEN
        :new.VERSION := :old.VERSION + 1;
    END IF;

    if :new.MODIFIED_DATE IS NULL or NOT UPDATING('Modified_Date') then
        :new.MODIFIED_DATE := sys_extract_utc(systimestamp);
    end if;

    if :new.MODIFIED_USER IS NULL then
        :new.MODIFIED_USER := user;
    end if;
 
END;
/
ALTER TRIGGER TRG_A2L_WP_RESP_UPDT ENABLE;

---------------------------------------------------------------
---ICDM-2613 Parent task ICDM-2468 
---Store changes in Focus matrix 
---------------------------------------------------------------
CREATE OR REPLACE TRIGGER TRG_FOCUS_MATRIX_VERS_INS 
  BEFORE INSERT ON T_FOCUS_MATRIX_VERSION
  FOR EACH ROW
  DECLARE 
        v_project_id number;
        v_pidc_version_version number;
        v_project_id_card_version number;
  
Begin
      IF :new.STATUS = 'W' THEN
      
        IF :new.FM_VERS_ID is null THEN
            SELECT SeqV_Attributes.nextval INTO :new.FM_VERS_ID FROM DUAL;
        END IF;
    
        IF :new.VERSION is null THEN
            :new.VERSION := 1;
        END IF;
    
        IF :new.CREATED_DATE is null THEN
            :new.CREATED_DATE := sys_extract_utc(systimestamp);
        END IF;
    
        IF :new.CREATED_USER is null THEN
            :new.CREATED_USER := user;
        END IF;
      
        SELECT tpv.PROJECT_ID , tpv.VERSION into v_project_id, v_pidc_version_version FROM T_PIDC_VERSION tpv WHERE tpv.PIDC_VERS_ID = :new.PIDC_VERS_ID;
        
        SELECT tp.VERSION INTO v_project_id_card_version FROM TABV_PROJECTIDCARD tp WHERE tp.PROJECT_ID = v_project_id;
            
        insert into t_pidc_change_history
            (
             ID,
             PIDC_ID,
            CHANGED_DATE,
            CHANGED_USER,
            PIDC_VERSION,
            NEW_DESCRIPTION,
            PIDC_VERS_ID,
            PIDC_VERS_VERS, 
            FM_VERS_ID,
            NEW_FM_VERS_NAME,
            FM_VERS_REV_NUM,
            NEW_FM_VERS_STATUS,
            NEW_FM_VERS_REVIEWED_USER,
            NEW_FM_VERS_REVIEWED_DATE,
            NEW_FM_VERS_LINK,
            NEW_FM_VERS_RVW_STATUS,
            FM_VERS_VERSION
             )
          values
            (
            SeqV_Attributes.nextval,
            v_project_id,
            :new.CREATED_DATE,
            :new.CREATED_USER,
            v_project_id_card_version,
            :new.REMARK,
            :new.PIDC_VERS_ID,
            v_pidc_version_version,
            :new.FM_VERS_ID,
            :new.NAME,
            :new.REV_NUM,
            :new.STATUS,
            :new.REVIEWED_USER,
            :new.REVIEWED_DATE,
            :new.LINK,
            :new.RVW_STATUS,
            :new.VERSION
            );
    END IF;
END;
/

CREATE OR REPLACE TRIGGER TRG_FOCUS_MATRIX_VERS_UPDT 
BEFORE UPDATE ON T_FOCUS_MATRIX_VERSION
FOR EACH ROW
        DECLARE 
        v_project_id number;
        v_pidc_version_version number;
        v_project_id_card_version number;
BEGIN
    IF :new.STATUS = 'W' THEN
    
        IF :new.VERSION = :old.VERSION THEN
            :new.VERSION := :old.VERSION + 1;
        END IF;
    
        if :new.MODIFIED_DATE IS NULL or NOT UPDATING('Modified_Date') then
            :new.MODIFIED_DATE := sys_extract_utc(systimestamp);
        end if;
    
        if :new.MODIFIED_USER IS NULL then
            :new.MODIFIED_USER := user;
        end if;
        
        
        SELECT tpv.PROJECT_ID , tpv.VERSION into v_project_id, v_pidc_version_version FROM T_PIDC_VERSION tpv WHERE tpv.PIDC_VERS_ID = :new.PIDC_VERS_ID;
        
        SELECT tvp.VERSION INTO v_project_id_card_version FROM TABV_PROJECTIDCARD tvp WHERE tvp.PROJECT_ID = v_project_id;
    
     insert into t_pidc_change_history
        (
         ID,    
         PIDC_ID,
        CHANGED_DATE,
        CHANGED_USER,
        PIDC_VERSION,
        OLD_DESCRIPTION,
        NEW_DESCRIPTION,
        PIDC_VERS_ID,
        PIDC_VERS_VERS,
        FM_VERS_ID,
        OLD_FM_VERS_NAME,
        NEW_FM_VERS_NAME,
        FM_VERS_REV_NUM,
        OLD_FM_VERS_STATUS,
        NEW_FM_VERS_STATUS,
        OLD_FM_VERS_REVIEWED_USER,
        NEW_FM_VERS_REVIEWED_USER,
        OLD_FM_VERS_REVIEWED_DATE,
        NEW_FM_VERS_REVIEWED_DATE,
        OLD_FM_VERS_LINK,
        NEW_FM_VERS_LINK,
        OLD_FM_VERS_RVW_STATUS,
        NEW_FM_VERS_RVW_STATUS,
        FM_VERS_VERSION
         )
      values
        (
        SeqV_Attributes.nextval,
        v_project_id,
        :new.MODIFIED_DATE,
        :new.MODIFIED_USER,
        v_project_id_card_version,
        :old.REMARK,
        :new.REMARK,
        :new.PIDC_VERS_ID,
        v_pidc_version_version,
        :new.FM_VERS_ID,
        :old.NAME,
        :new.NAME,
        :new.REV_NUM,
        :old.STATUS,
        :new.STATUS,
        :old.REVIEWED_USER,
        :new.REVIEWED_USER,
        :old.REVIEWED_DATE,
        :new.REVIEWED_DATE,
        :old.LINK,
        :new.LINK,
        :old.RVW_STATUS,
        :new.RVW_STATUS,
        :new.VERSION
        );
    END IF;
END;
/

CREATE OR REPLACE TRIGGER TRG_FOCUS_MATRIX_INS 
  BEFORE INSERT ON T_FOCUS_MATRIX
  FOR EACH ROW

DECLARE
    v_fmv_status varchar2(1);
    v_projct_id number;
    v_projct_id_version number;
    v_pidc_ver_id number;
    v_pidc_version_version number;

Begin
    select tfmv.STATUS, tfmv.PIDC_VERS_ID  into v_fmv_status, v_pidc_ver_id from T_FOCUS_MATRIX_VERSION tfmv where tfmv.FM_VERS_ID = :new.FM_VERS_ID;
    
    IF v_fmv_status = 'W' THEN
            IF :new.FM_ID is null THEN
                SELECT SeqV_Attributes.nextval INTO :new.FM_ID FROM DUAL;
            END IF;
        
            IF :new.VERSION is null THEN
                :new.VERSION := 1;
            END IF;
        
            IF :new.CREATED_DATE is null THEN
                :new.CREATED_DATE := sys_extract_utc(systimestamp);
            END IF;
        
            IF :new.CREATED_USER is null THEN
                :new.CREATED_USER := user;
            END IF;   
            
            SELECT tpv.PROJECT_ID, tpv.VERSION into v_projct_id, v_pidc_version_version from T_PIDC_VERSION tpv where tpv.PIDC_VERS_ID=v_pidc_ver_id;
            
            SELECT tvp.VERSION INTO v_projct_id_version FROM TABV_PROJECTIDCARD tvp WHERE tvp.PROJECT_ID = v_projct_id;
        
            insert into t_pidc_change_history
                (
                 ID,    
                 PIDC_ID,
                 PIDC_VERS_ID,
                 changed_date,
                 changed_user,  
                 PIDC_VERSION,
                 PIDC_VERS_VERS,
                 FM_ID ,
                 FM_UCPA_ID ,     
                 NEW_FM_COLOR_CODE ,     
                 NEW_FM_COMMENTS ,
                 FM_VERSION ,     
                 NEW_FM_LINK ,
                 USE_CASE_ID ,
                 SECTION_ID,
                 ATTR_ID,     
                 NEW_DELETED_FLAG,
                 FM_VERS_ID
                )
              values
                (
                 SeqV_Attributes.NEXTVAL,
                 v_projct_id,
                 v_pidc_ver_id,
                :new.CREATED_DATE,
                :new.CREATED_USER,
                v_projct_id_version,
                v_pidc_version_version,
                :new.FM_ID,
                :new.UCPA_ID,    
                :new.COLOR_CODE,    
                :new.COMMENTS,
                :new.VERSION,    
                :new.LINK,
                :new.USE_CASE_ID,
                :new.SECTION_ID,
                :new.ATTR_ID,    
                :new.IS_DELETED,
                :new.FM_VERS_ID
                 );
    END IF;
END;
/

CREATE OR REPLACE TRIGGER TRG_FOCUS_MATRIX_UPDT
BEFORE UPDATE ON T_FOCUS_MATRIX
FOR EACH ROW

DECLARE
    v_fmv_status varchar2(1);
    v_projct_id number;
    v_projct_id_version number;
    v_pidc_ver_id number;
    v_pidc_version_version number;    

BEGIN
select tfmv.STATUS, tfmv.PIDC_VERS_ID  into v_fmv_status, v_pidc_ver_id from T_FOCUS_MATRIX_VERSION tfmv where tfmv.FM_VERS_ID = :new.FM_VERS_ID;

    IF v_fmv_status = 'W' THEN
            IF :new.VERSION = :old.VERSION THEN
                :new.VERSION := :old.VERSION + 1;
            END IF;
        
            if :new.MODIFIED_DATE IS NULL or NOT UPDATING('Modified_Date') then
                :new.MODIFIED_DATE := sys_extract_utc(systimestamp);
            end if;
        
            if :new.MODIFIED_USER IS NULL then
                :new.MODIFIED_USER := user;
            end if;
                                    
            SELECT tpv.PROJECT_ID, tpv.VERSION into v_projct_id, v_pidc_version_version from T_PIDC_VERSION tpv where tpv.PIDC_VERS_ID=v_pidc_ver_id;
            
            SELECT tvp.VERSION INTO v_projct_id_version FROM TABV_PROJECTIDCARD tvp WHERE tvp.PROJECT_ID = v_projct_id;
      
            insert into t_pidc_change_history
                (
                 ID,
                 PIDC_ID,
                 PIDC_VERS_ID,
                 changed_date,
                 changed_user,
                 PIDC_VERSION,
                 PIDC_VERS_VERS,
                 FM_ID ,
                 FM_UCPA_ID ,
                 OLD_FM_COLOR_CODE ,
                 NEW_FM_COLOR_CODE ,     
                 OLD_FM_COMMENTS ,
                 NEW_FM_COMMENTS ,
                 FM_VERSION ,
                 OLD_FM_LINK ,
                 NEW_FM_LINK ,
                 USE_CASE_ID ,
                 SECTION_ID,
                 ATTR_ID,
                 OLD_DELETED_FLAG,
                 NEW_DELETED_FLAG,
                 FM_VERS_ID
                )
              values
                (
                 SeqV_Attributes.NEXTVAL,     
                 v_projct_id,
                 v_pidc_ver_id ,
                :new.MODIFIED_DATE,
                :new.MODIFIED_USER,
                v_projct_id_version,
                v_pidc_version_version,
                :new.FM_ID,
                :new.UCPA_ID,
                :old.COLOR_CODE,
                :new.COLOR_CODE,
                :old.COMMENTS,
                :new.COMMENTS,
                :new.VERSION,
                :old.LINK,
                :new.LINK,
                :new.USE_CASE_ID,
                :new.SECTION_ID,
                :new.ATTR_ID,
                :old.IS_DELETED,
                :new.IS_DELETED,
                :new.FM_VERS_ID
                 );
    END IF;
END;
/


--------------------------------------------------------
----ICDM-2296

-- Triggers for T_PREDEFINED_ATTR_VALUES 
--------------------------------------------------------

--Insert Trigger

CREATE OR REPLACE TRIGGER TRG_PREDEFINED_ATTR_VAL_INS BEFORE INSERT ON T_PREDEFINED_ATTR_VALUES
   FOR EACH ROW
Begin
    IF :new.PRE_ATTRVL_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.PRE_ATTRVL_ID FROM DUAL;
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
--Update trigger
CREATE OR REPLACE TRIGGER TRG_PREDEFINED_ATTR_VAL_UPD 
  BEFORE UPDATE ON T_PREDEFINED_ATTR_VALUES
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

--------------------------------------------------------
-- Triggers for T_PREDEFINED_ATTR_VALUES 
--------------------------------------------------------
--Insert Trigger
CREATE OR REPLACE TRIGGER TRG_T_PREDEF_VALIDITY_INS
   BEFORE INSERT ON T_PREDEFINED_VALIDITY
   FOR EACH ROW
Begin
    IF :new.GRP_ATTR_VAL_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.GRP_ATTR_VAL_ID FROM DUAL;
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
--Update trigger
CREATE OR REPLACE TRIGGER TRG_T_PREDEF_VALIDITY_UPD 
  BEFORE UPDATE ON T_PREDEFINED_VALIDITY
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

spool off