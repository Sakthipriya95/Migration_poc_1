spool 'C:\Temp\20_Create_Trigger.log'

------------------------------------------------------------------------------------------------------------------
-- ALM Task ID : 400432
-- TABV_PID_FAVORITES table triggers - updated based on the changes in table
------------------------------------------------------------------------------------------------------------------
CREATE OR REPLACE TRIGGER TRG_PID_FAV_INS 
  BEFORE INSERT ON TABV_PID_FAVORITES
  FOR EACH ROW
BEGIN
    IF :new.fav_id is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.fav_id FROM DUAL;
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

CREATE OR REPLACE TRIGGER TRG_PID_FAV_UPDT
  BEFORE UPDATE ON TABV_PID_FAVORITES
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


------------------------------------------------------------------------------------------------------------------
-- ALM Task ID : 463370
-- A2L Responsibility tables triggers
------------------------------------------------------------------------------------------------------------------

----------------------------------------------
-- Table : T_A2L_RESPONSIBILITY
----------------------------------------------
CREATE OR REPLACE EDITIONABLE TRIGGER TRG_A2L_RESPBLTY_INS 
  BEFORE INSERT ON T_A2L_RESPONSIBILITY 
  FOR EACH ROW 
BEGIN
   IF :new.A2L_RESP_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.A2L_RESP_ID FROM DUAL;
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

CREATE OR REPLACE EDITIONABLE TRIGGER TRG_A2L_RESPBLTY_UPD
  BEFORE UPDATE ON T_A2L_RESPONSIBILITY 
  FOR EACH ROW 
BEGIN
    IF :new.Version = :old.Version THEN
        :new.Version := :old.Version + 1;
    END IF;

    if :new.MODIFIED_DATE IS NULL or NOT UPDATING('Modified_Date') then
        :new.Modified_Date := sys_extract_utc(systimestamp);
    END IF;

    IF :new.Modified_User is null THEN
        :new.Modified_User := user;
    END IF;
END;
/

----------------------------------------------
-- Table : T_A2L_WORK_PACKAGES
----------------------------------------------

CREATE OR REPLACE EDITIONABLE TRIGGER TRG_A2L_WORK_PKG_INS 
  BEFORE INSERT ON T_A2L_WORK_PACKAGES
  FOR EACH ROW
BEGIN
    IF :new.A2L_WP_ID IS NULL THEN
        SELECT SeqV_Attributes.nextval INTO :new.A2L_WP_ID FROM DUAL;
    END IF;

    IF :new.VERSION IS NULL THEN
        :new.VERSION := 1;
    END IF;

    IF :new.CREATED_DATE IS NULL THEN
        :new.CREATED_DATE := sys_extract_utc(systimestamp);
    END IF;

    IF :new.CREATED_USER IS NULL THEN
        :new.CREATED_USER := USER;
    END IF;

END;
/

CREATE OR REPLACE EDITIONABLE TRIGGER TRG_A2L_WORK_PKG_UPDT 
  BEFORE UPDATE ON T_A2L_WORK_PACKAGES
  FOR EACH ROW
BEGIN
    IF :new.VERSION = :old.VERSION THEN
        :new.VERSION := :old.VERSION + 1;
    END IF;

    IF :new.MODIFIED_DATE  IS NULL or NOT UPDATING('Modified_Date') then
        :new.MODIFIED_DATE := sys_extract_utc(systimestamp);
    END IF;
    IF :new.MODIFIED_USER  IS NULL THEN
        :new.MODIFIED_USER := USER;
    END IF;   
 
END;
/
----------------------------------------------
-- Table : T_A2L_WP_DEFN_VERSIONS
----------------------------------------------
CREATE OR REPLACE EDITIONABLE TRIGGER TRG_A2L_WP_DEFN_VERS_INS 
  BEFORE INSERT ON T_A2L_WP_DEFN_VERSIONS
  FOR EACH ROW
BEGIN
    IF :new.WP_DEFN_VERS_ID IS NULL THEN
        SELECT SeqV_Attributes.nextval INTO :new.WP_DEFN_VERS_ID FROM DUAL;
    END IF;

    IF :new.VERSION IS NULL THEN
        :new.VERSION := 1;
    END IF;

    IF :new.CREATED_DATE IS NULL THEN
        :new.CREATED_DATE := sys_extract_utc(systimestamp);
    END IF;

    IF :new.CREATED_USER IS NULL THEN
        :new.CREATED_USER := USER;
    END IF;

END;
/

CREATE OR REPLACE EDITIONABLE TRIGGER TRG_A2L_WP_DEFN_VERS_UPD 
  BEFORE UPDATE ON T_A2L_WP_DEFN_VERSIONS
  FOR EACH ROW
BEGIN
    IF :new.VERSION = :old.VERSION THEN
        :new.VERSION := :old.VERSION + 1;
    END IF;

    IF :new.MODIFIED_DATE  IS NULL or NOT UPDATING('Modified_Date') then
        :new.MODIFIED_DATE := sys_extract_utc(systimestamp);
    END IF;
    IF :new.MODIFIED_USER  IS NULL THEN
        :new.MODIFIED_USER := USER;
    END IF;   
 
END;
/

----------------------------------------------
-- Table : T_A2L_VARIANT_GROUPS
----------------------------------------------
CREATE OR REPLACE EDITIONABLE TRIGGER TRG_A2L_VARIANT_GROUP_INS 
  BEFORE INSERT ON T_A2L_VARIANT_GROUPS
  FOR EACH ROW
BEGIN
    IF :new.A2L_VAR_GRP_ID IS NULL THEN
        SELECT SeqV_Attributes.nextval INTO :new.A2L_VAR_GRP_ID FROM DUAL;
    END IF;

    IF :new.VERSION IS NULL THEN
        :new.VERSION := 1;
    END IF;

    IF :new.CREATED_DATE IS NULL THEN
        :new.CREATED_DATE := sys_extract_utc(systimestamp);
    END IF;

    IF :new.CREATED_USER IS NULL THEN
        :new.CREATED_USER := USER;
    END IF;

END;
/

CREATE OR REPLACE EDITIONABLE TRIGGER TRG_A2L_VARIANT_GROUP_UPD 
  BEFORE UPDATE ON T_A2L_VARIANT_GROUPS
  FOR EACH ROW
BEGIN
    IF :new.VERSION = :old.VERSION THEN
        :new.VERSION := :old.VERSION + 1;
    END IF;

    IF :new.MODIFIED_DATE  IS NULL or NOT UPDATING('Modified_Date') then
        :new.MODIFIED_DATE := sys_extract_utc(systimestamp);
    END IF;
    IF :new.MODIFIED_USER  IS NULL THEN
        :new.MODIFIED_USER := USER;
    END IF;   
 
END;
/


----------------------------------------------
-- Table : T_A2L_VARGRP_VARIANT_MAPPING
----------------------------------------------
CREATE OR REPLACE EDITIONABLE TRIGGER TRG_A2L_VARGRP_VAR_MAP_INS 
  BEFORE INSERT ON T_A2L_VARGRP_VARIANT_MAPPING
  FOR EACH ROW
BEGIN
    IF :new.VARGRP_VAR_MAP_ID IS NULL THEN
        SELECT SeqV_Attributes.nextval INTO :new.VARGRP_VAR_MAP_ID FROM DUAL;
    END IF;

    IF :new.VERSION IS NULL THEN
        :new.VERSION := 1;
    END IF;

    IF :new.CREATED_DATE IS NULL THEN
        :new.CREATED_DATE := sys_extract_utc(systimestamp);
    END IF;

    IF :new.CREATED_USER IS NULL THEN
        :new.CREATED_USER := USER;
    END IF;

END;
/

CREATE OR REPLACE EDITIONABLE TRIGGER TRG_A2L_VARGRP_VAR_MAP_UPD
  BEFORE UPDATE ON T_A2L_VARGRP_VARIANT_MAPPING
  FOR EACH ROW
BEGIN
    IF :new.VERSION = :old.VERSION THEN
        :new.VERSION := :old.VERSION + 1;
    END IF;

    IF :new.MODIFIED_DATE  IS NULL or NOT UPDATING('Modified_Date') then
        :new.MODIFIED_DATE := sys_extract_utc(systimestamp);
    END IF;
    IF :new.MODIFIED_USER  IS NULL THEN
        :new.MODIFIED_USER := USER;
    END IF;   
 
END;
/

----------------------------------------------
-- Table : T_A2L_WP_RESPONSIBILITY
----------------------------------------------
CREATE OR REPLACE EDITIONABLE TRIGGER TRG_A2L_WP_RESPBLTY_INS 
  BEFORE INSERT ON T_A2L_WP_RESPONSIBILITY
  FOR EACH ROW
BEGIN
    IF :new.WP_RESP_ID IS NULL THEN
        SELECT SeqV_Attributes.nextval INTO :new.WP_RESP_ID FROM DUAL;
    END IF;

    IF :new.VERSION IS NULL THEN
        :new.VERSION := 1;
    END IF;

    IF :new.CREATED_DATE IS NULL THEN
        :new.CREATED_DATE := sys_extract_utc(systimestamp);
    END IF;

    IF :new.CREATED_USER IS NULL THEN
        :new.CREATED_USER := USER;
    END IF;

END;
/

CREATE OR REPLACE EDITIONABLE TRIGGER TRG_A2L_WP_RESPBLTY_UPD 
  BEFORE UPDATE ON T_A2L_WP_RESPONSIBILITY
  FOR EACH ROW
BEGIN
    IF :new.VERSION = :old.VERSION THEN
        :new.VERSION := :old.VERSION + 1;
    END IF;

    IF :new.MODIFIED_DATE  IS NULL or NOT UPDATING('Modified_Date') THEN
        :new.MODIFIED_DATE := sys_extract_utc(systimestamp);
    END IF;
    IF :new.MODIFIED_USER  IS NULL THEN
        :new.MODIFIED_USER := USER;
    END IF;   
 
END;
/

----------------------------------------------
-- Table : T_A2L_WP_PARAM_MAPPING
----------------------------------------------
CREATE OR REPLACE EDITIONABLE TRIGGER TRG_A2L_WP_PARAM_MAP_INS 
  BEFORE INSERT ON T_A2L_WP_PARAM_MAPPING
  FOR EACH ROW
BEGIN
    IF :new.WP_PARAM_MAP_ID IS NULL THEN
        SELECT SeqV_Attributes.nextval INTO :new.WP_PARAM_MAP_ID FROM DUAL;
    END IF;

    IF :new.VERSION IS NULL THEN
        :new.VERSION := 1;
    END IF;

    IF :new.CREATED_DATE IS NULL THEN
        :new.CREATED_DATE := sys_extract_utc(systimestamp);
    END IF;

    IF :new.CREATED_USER IS NULL THEN
        :new.CREATED_USER := USER;
    END IF;

END;
/

CREATE OR REPLACE EDITIONABLE TRIGGER TRG_A2L_WP_PARAM_MAP_UPD 
  BEFORE UPDATE ON T_A2L_WP_PARAM_MAPPING
  FOR EACH ROW
BEGIN
    IF :new.VERSION = :old.VERSION THEN
        :new.VERSION := :old.VERSION + 1;
    END IF;

    IF :new.MODIFIED_DATE  IS NULL or NOT UPDATING('Modified_Date') then
        :new.MODIFIED_DATE := sys_extract_utc(systimestamp);
    END IF;
    IF :new.MODIFIED_USER  IS NULL THEN
        :new.MODIFIED_USER := USER;
    END IF;   
 
END;
/

----------------------------------------------
-- Table : T_RVW_WP_RESP
----------------------------------------------
CREATE OR REPLACE EDITIONABLE TRIGGER TRG_T_RVW_WP_RESP_INS 
  BEFORE INSERT ON T_RVW_WP_RESP
  FOR EACH ROW
BEGIN
    IF :new.RVW_WP_RESP_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.RVW_WP_RESP_ID FROM DUAL;
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

CREATE OR REPLACE EDITIONABLE TRIGGER TRG_T_RVW_WP_RESP_UPDT 
  BEFORE UPDATE ON T_RVW_WP_RESP
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

----------------------------------------------
--  ALM TaskId : 464549
-- Table       : T_COMPLI_RVW_HEX_PARAMS
----------------------------------------------

CREATE OR REPLACE EDITIONABLE TRIGGER T_COMPLI_RVW_HEX_PARAMS_UPDT 
  BEFORE UPDATE ON T_COMPLI_RVW_HEX_PARAMS
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

CREATE OR REPLACE EDITIONABLE TRIGGER TRG_COMPLI_RVW_HEX_PARAMS_INS 
  BEFORE INSERT ON T_COMPLI_RVW_HEX_PARAMS
  FOR EACH ROW
Begin
    IF :new.HEX_PARAMS_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.HEX_PARAMS_ID FROM DUAL;
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



------------------------------------------------------------------------------------------------------------------
--  ALM TaskId : 462621: ACL for services
------------------------------------------------------------------------------------------------------------------

--------------------------------------------------------
--  DDL for Trigger TRG_WS_SYSTEM__INS
--------------------------------------------------------
CREATE OR REPLACE EDITIONABLE TRIGGER  TRG_WS_SYSTEM__INS 
  BEFORE INSERT ON T_WS_SYSTEMS FOR EACH ROW
BEGIN
    IF :new.SYSTEM_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.SYSTEM_ID FROM DUAL;
    END IF;

    IF :new.Version is null THEN
        :new.Version := 1;
    END IF;
    
    IF :new.CREATED_DATE is null THEN
        :new.CREATED_DATE := sys_extract_utc(systimestamp);
    END IF;

    IF :new.CREATED_USER is null THEN
        :new.CREATED_USER := user;
    END IF;
    
END;
/
 
--------------------------------------------------------
--  DDL for Trigger TRG_WS_SYSTEMS__UPDT
--------------------------------------------------------
CREATE OR REPLACE EDITIONABLE TRIGGER  TRG_WS_SYSTEMS__UPDT  
  BEFORE UPDATE ON T_WS_SYSTEMS
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


--------------------------------------------------------
--  DDL for Trigger TRG_WS_SERVICES__INS
--------------------------------------------------------
CREATE OR REPLACE EDITIONABLE TRIGGER  TRG_WS_SERVICES__INS 
  BEFORE INSERT ON T_WS_SERVICES FOR EACH ROW
BEGIN
    IF :new.WS_SERV_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.WS_SERV_ID FROM DUAL;
    END IF;

    IF :new.Version is null THEN
        :new.Version := 1;
    END IF;
    
    IF :new.CREATED_DATE is null THEN
        :new.CREATED_DATE := sys_extract_utc(systimestamp);
    END IF;

    IF :new.CREATED_USER is null THEN
        :new.CREATED_USER := user;
    END IF;
    
END;
/
 
--------------------------------------------------------
--  DDL for Trigger TRG_WS_SERVICES__UPDT
--------------------------------------------------------
CREATE OR REPLACE EDITIONABLE TRIGGER  TRG_WS_SERVICES__UPDT  
  BEFORE UPDATE ON T_WS_SERVICES
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

--------------------------------------------------------
--  DDL for Trigger TRG_WS_SYSTEM_SERVICES__INS
--------------------------------------------------------
CREATE OR REPLACE EDITIONABLE TRIGGER  TRG_WS_SYSTEM_SERVICES__INS 
  BEFORE INSERT ON T_WS_SYSTEM_SERVICES FOR EACH ROW
BEGIN
    IF :new.SERV_SYS_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.SERV_SYS_ID FROM DUAL;
    END IF;

    IF :new.Version is null THEN
        :new.Version := 1;
    END IF;
    
    IF :new.CREATED_DATE is null THEN
        :new.CREATED_DATE := sys_extract_utc(systimestamp);
    END IF;

    IF :new.CREATED_USER is null THEN
        :new.CREATED_USER := user;
    END IF;
    
END;
/
 
--------------------------------------------------------
--  DDL for Trigger TRG_WS_SYSTEM_SERVICES__UPDT
--------------------------------------------------------
CREATE OR REPLACE EDITIONABLE TRIGGER  TRG_WS_SYSTEM_SERVICES__UPDT  
  BEFORE UPDATE ON T_WS_SYSTEM_SERVICES
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

spool off
