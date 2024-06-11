spool c:\temp\create_trigger.log
--------------------------------------------------------
-- DDL for Trigger T_QUESTIONNAIRE - Before Insert
-- 
-- ICDM-1950
--------------------------------------------------------
CREATE OR REPLACE TRIGGER TRG_QUESTIONNAIRE_INS 
  BEFORE INSERT ON T_QUESTIONNAIRE
  FOR EACH ROW
Begin
    IF :new.QNAIRE_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.QNAIRE_ID FROM DUAL;
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

ALTER TRIGGER TRG_QUESTIONNAIRE_INS ENABLE;

--------------------------------------------------------
-- DDL for Trigger T_QUESTIONNAIRE - Before update
-- 
-- ICDM-1950
--------------------------------------------------------
CREATE OR REPLACE TRIGGER TRG_QUESTIONNAIRE_UPDT
BEFORE UPDATE ON T_QUESTIONNAIRE
FOR EACH ROW
BEGIN
    IF :new.VERSION = :old.VERSION THEN
        :new.VERSION := :old.VERSION + 1;
    END IF;

    if :new.MODIFIED_DATE IS NULL then
        :new.MODIFIED_DATE := sys_extract_utc(systimestamp);
    end if;

    if :new.MODIFIED_USER IS NULL then
        :new.MODIFIED_USER := user;
    end if;
 
END;
/

ALTER TRIGGER TRG_QUESTIONNAIRE_UPDT ENABLE;

--------------------------------------------------------
-- DDL for Trigger T_QUESTIONNAIRE_VERSION - Before Insert
-- 
-- ICDM-1950
--------------------------------------------------------
CREATE OR REPLACE TRIGGER TRG_QUESTIONNAIRE_VERS_INS 
  BEFORE INSERT ON T_QUESTIONNAIRE_VERSION
  FOR EACH ROW
Begin
    IF :new.QNAIRE_VERS_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.QNAIRE_VERS_ID FROM DUAL;
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

ALTER TRIGGER TRG_QUESTIONNAIRE_VERS_INS ENABLE;

--------------------------------------------------------
-- DDL for Trigger T_QUESTIONNAIRE_VERSION - Before update
-- 
-- ICDM-1950
--------------------------------------------------------
CREATE OR REPLACE TRIGGER TRG_QUESTIONNAIRE_VERS_UPDT
BEFORE UPDATE ON T_QUESTIONNAIRE_VERSION
FOR EACH ROW
BEGIN
    IF :new.VERSION = :old.VERSION THEN
        :new.VERSION := :old.VERSION + 1;
    END IF;

    if :new.MODIFIED_DATE IS NULL then
        :new.MODIFIED_DATE := sys_extract_utc(systimestamp);
    end if;

    if :new.MODIFIED_USER IS NULL then
        :new.MODIFIED_USER := user;
    end if;
 
END;
/

ALTER TRIGGER TRG_QUESTIONNAIRE_VERS_UPDT ENABLE;

--------------------------------------------------------
-- DDL for Trigger T_QUESTION - Before Insert
-- 
-- ICDM-1950
--------------------------------------------------------
CREATE OR REPLACE TRIGGER TRG_QUESTION_INS 
  BEFORE INSERT ON T_QUESTION
  FOR EACH ROW
Begin
    IF :new.Q_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.Q_ID FROM DUAL;
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

ALTER TRIGGER TRG_QUESTION_INS ENABLE;

--------------------------------------------------------
-- DDL for Trigger T_QUESTION - Before update
-- 
-- ICDM-1950
--------------------------------------------------------
CREATE OR REPLACE TRIGGER TRG_QUESTION_UPDT
BEFORE UPDATE ON T_QUESTION
FOR EACH ROW
BEGIN
    IF :new.VERSION = :old.VERSION THEN
        :new.VERSION := :old.VERSION + 1;
    END IF;

    if :new.MODIFIED_DATE IS NULL then
        :new.MODIFIED_DATE := sys_extract_utc(systimestamp);
    end if;

    if :new.MODIFIED_USER IS NULL then
        :new.MODIFIED_USER := user;
    end if;
 
END;
/

ALTER TRIGGER TRG_QUESTION_UPDT ENABLE;

--------------------------------------------------------
-- DDL for Trigger T_QUESTION_CONFIG - Before Insert
-- 
-- ICDM-1950
--------------------------------------------------------
CREATE OR REPLACE TRIGGER TRG_QUESTION_CONFIG_INS 
  BEFORE INSERT ON T_QUESTION_CONFIG
  FOR EACH ROW
Begin
    IF :new.QCONFIG_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.QCONFIG_ID FROM DUAL;
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

ALTER TRIGGER TRG_QUESTION_CONFIG_INS ENABLE;

--------------------------------------------------------
-- DDL for Trigger T_QUESTION_CONFIG - Before update
-- 
-- ICDM-1950
--------------------------------------------------------
CREATE OR REPLACE TRIGGER TRG_QUESTION_CONFIG_UPDT
BEFORE UPDATE ON T_QUESTION_CONFIG
FOR EACH ROW
BEGIN
    IF :new.VERSION = :old.VERSION THEN
        :new.VERSION := :old.VERSION + 1;
    END IF;

    if :new.MODIFIED_DATE IS NULL then
        :new.MODIFIED_DATE := sys_extract_utc(systimestamp);
    end if;

    if :new.MODIFIED_USER IS NULL then
        :new.MODIFIED_USER := user;
    end if;
 
END;
/

ALTER TRIGGER TRG_QUESTION_CONFIG_UPDT ENABLE;

--------------------------------------------------------
-- DDL for Trigger T_QUESTION_DEPEN_ATTRIBUTES - Before Insert
-- 
-- ICDM-1950
--------------------------------------------------------
CREATE OR REPLACE TRIGGER TRG_QUESTION_DEPEN_ATTR_INS 
  BEFORE INSERT ON T_QUESTION_DEPEN_ATTRIBUTES
  FOR EACH ROW
Begin
    IF :new.QATTR_DEPEN_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.QATTR_DEPEN_ID FROM DUAL;
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

ALTER TRIGGER TRG_QUESTION_DEPEN_ATTR_INS ENABLE;

--------------------------------------------------------
-- DDL for Trigger T_QUESTION_DEPEN_ATTRIBUTES - Before update
-- 
-- ICDM-1950
--------------------------------------------------------
CREATE OR REPLACE TRIGGER TRG_QUESTION_DEPEN_ATTR_UPDT
BEFORE UPDATE ON T_QUESTION_DEPEN_ATTRIBUTES
FOR EACH ROW
BEGIN
    IF :new.VERSION = :old.VERSION THEN
        :new.VERSION := :old.VERSION + 1;
    END IF;

    if :new.MODIFIED_DATE IS NULL then
        :new.MODIFIED_DATE := sys_extract_utc(systimestamp);
    end if;

    if :new.MODIFIED_USER IS NULL then
        :new.MODIFIED_USER := user;
    end if;
 
END;
/

ALTER TRIGGER TRG_QUESTION_DEPEN_ATTR_UPDT ENABLE;

--------------------------------------------------------
-- DDL for Trigger T_QUESTION_DEPEN_ATTR_VALUES - Before Insert
-- 
-- ICDM-1950
--------------------------------------------------------
CREATE OR REPLACE TRIGGER TRG_QUES_DEP_ATTR_VALS_INS 
  BEFORE INSERT ON T_QUESTION_DEPEN_ATTR_VALUES
  FOR EACH ROW
Begin
    IF :new.DEPEN_ATTR_VAL_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.DEPEN_ATTR_VAL_ID FROM DUAL;
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

ALTER TRIGGER TRG_QUES_DEP_ATTR_VALS_INS ENABLE;

--------------------------------------------------------
-- DDL for Trigger T_QUESTION_DEPEN_ATTR_VALUES - Before update
-- 
-- ICDM-1950
--------------------------------------------------------
CREATE OR REPLACE TRIGGER TRG_QUES_DEP_ATTR_VALS_UPDT
BEFORE UPDATE ON T_QUESTION_DEPEN_ATTR_VALUES
FOR EACH ROW
BEGIN
    IF :new.VERSION = :old.VERSION THEN
        :new.VERSION := :old.VERSION + 1;
    END IF;

    if :new.MODIFIED_DATE IS NULL then
        :new.MODIFIED_DATE := sys_extract_utc(systimestamp);
    end if;

    if :new.MODIFIED_USER IS NULL then
        :new.MODIFIED_USER := user;
    end if;
 
END;
/

ALTER TRIGGER TRG_QUES_DEP_ATTR_VALS_UPDT ENABLE;

--------------------------------------------------------
-- DDL for Trigger T_RVW_QUESTIONNAIRE - Before Insert
-- 
-- ICDM-1979
--------------------------------------------------------
CREATE OR REPLACE TRIGGER TRG_T_RVW_QNAIRE_INS
  BEFORE INSERT ON T_RVW_QUESTIONNAIRE
  FOR EACH ROW
Begin
    IF :new.RVW_QNAIRE_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.RVW_QNAIRE_ID FROM DUAL;
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

ALTER TRIGGER TRG_T_RVW_QNAIRE_INS ENABLE;
--------------------------------------------------------
-- DDL for Trigger T_RVW_QUESTIONNAIRE - Before update
-- 
-- ICDM-1979
--------------------------------------------------------
CREATE OR REPLACE TRIGGER TRG_T_RVW_QNAIRE_UPDT
BEFORE UPDATE ON T_RVW_QUESTIONNAIRE
FOR EACH ROW
BEGIN
    IF :new.VERSION = :old.VERSION THEN
        :new.VERSION := :old.VERSION + 1;
    END IF;

    if :new.MODIFIED_DATE IS NULL then
        :new.MODIFIED_DATE := sys_extract_utc(systimestamp);
    end if;

    if :new.MODIFIED_USER IS NULL then
        :new.MODIFIED_USER := user;
    end if;
 
END;
/

ALTER TRIGGER TRG_T_RVW_QNAIRE_UPDT ENABLE;

--------------------------------------------------------
-- DDL for Trigger T_RVW_QNAIRE_ANSWER - Before Insert
-- 
-- ICDM-1979
--------------------------------------------------------
CREATE OR REPLACE TRIGGER TRG_T_RVW_QNAIRE_ANS_INS
  BEFORE INSERT ON T_RVW_QNAIRE_ANSWER
  FOR EACH ROW
Begin
    IF :new.RVW_ANSWER_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.RVW_ANSWER_ID FROM DUAL;
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

ALTER TRIGGER TRG_T_RVW_QNAIRE_ANS_INS ENABLE;
--------------------------------------------------------
-- DDL for Trigger T_RVW_QNAIRE_ANSWER - Before update
-- 
-- ICDM-1979
--------------------------------------------------------
CREATE OR REPLACE TRIGGER TRG_T_RVW_QNAIRE_ANS_UPDT
BEFORE UPDATE ON T_RVW_QNAIRE_ANSWER
FOR EACH ROW
BEGIN
    IF :new.VERSION = :old.VERSION THEN
        :new.VERSION := :old.VERSION + 1;
    END IF;

    if :new.MODIFIED_DATE IS NULL then
        :new.MODIFIED_DATE := sys_extract_utc(systimestamp);
    end if;

    if :new.MODIFIED_USER IS NULL then
        :new.MODIFIED_USER := user;
    end if;
 
END;
/

ALTER TRIGGER TRG_T_RVW_QNAIRE_ANS_UPDT ENABLE;

--------------------------------------------------------
-- DDL for Trigger T_RVW_VARIANTS - Before insert
-- 
-- ICDM-2084
-------------------------------------------------------- 
  CREATE OR REPLACE TRIGGER TRG_T_RVW_VARIANTS_INS
  BEFORE INSERT ON T_RVW_VARIANTS
  FOR EACH ROW
Begin
    IF :new.RVW_VAR_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.RVW_VAR_ID FROM DUAL;
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

ALTER TRIGGER TRG_T_RVW_VARIANTS_INS ENABLE;

--------------------------------------------------------
-- DDL for Trigger T_RVW_VARIANTS - Before update
-- 
-- ICDM-2084
--------------------------------------------------------
CREATE OR REPLACE TRIGGER TRG_T_RVW_VARIANTS_UPDT
BEFORE UPDATE ON T_RVW_VARIANTS
FOR EACH ROW
BEGIN
    IF :new.VERSION = :old.VERSION THEN
        :new.VERSION := :old.VERSION + 1;
    END IF;

    if :new.MODIFIED_DATE IS NULL then
        :new.MODIFIED_DATE := sys_extract_utc(systimestamp);
    end if;

    if :new.MODIFIED_USER IS NULL then
        :new.MODIFIED_USER := user;
    end if;
 
END;
/

ALTER TRIGGER TRG_T_RVW_VARIANTS_UPDT ENABLE;
  
spool off