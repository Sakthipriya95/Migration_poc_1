spool C:\Temp\20_Create_Trigger.log


------------------------------------------------------------------------------------------------------------------
--  ALM TaskId : 553722: DB changes - new table has to be created to store the assessment and 
--result for every individual questions and also data has to be inserted for existing questions
------------------------------------------------------------------------------------------------------------------
-- create TRIGGER TRG_QUESTION_RESULT_OPT_INS


create TRIGGER TRG_QUESTION_RESULT_OPT_INS 
  BEFORE INSERT ON T_QUESTION_RESULT_OPTIONS
  FOR EACH ROW
Begin
    IF :new.Q_RESULT_OPT_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.Q_RESULT_OPT_ID FROM DUAL;
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

-- create TRIGGER TRG_QUESTION_RESULT_OPT_UPDT

create TRIGGER TRG_QUESTION_RESULT_OPT_UPDT
    BEFORE UPDATE ON T_QUESTION_RESULT_OPTIONS
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
--  ALM TaskId : 555532: DB changes for 100% CDFx delivery documentation
------------------------------------------------------------------------------------------------------------------
-- create TRIGGER TRG_CDFX_DELIVERY_INS
create or replace TRIGGER TRG_CDFX_DELIVERY_INS
  BEFORE INSERT ON T_CDFX_DELIVERY  
  FOR EACH ROW
Begin
    IF :new.CDFX_DELIVERY_ID IS NULL THEN
        SELECT SeqV_Attributes.nextval INTO :new.CDFX_DELIVERY_ID FROM DUAL;
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

-- create TRIGGER TRG_CDFX_DELIVERY_UPD
create or replace TRIGGER TRG_CDFX_DELIVERY_UPD 
  BEFORE UPDATE ON T_CDFX_DELIVERY
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

--TRG_CDFX_DEL_WP_RESP_INS
create or replace TRIGGER TRG_CDFX_DEL_WP_RESP_INS 
  BEFORE INSERT ON T_CDFX_DELVRY_WP_RESP
  FOR EACH ROW
Begin
    IF :new.CDFX_DEL_WP_RESP_ID IS NULL THEN
        SELECT SeqV_Attributes.nextval INTO :new.CDFX_DEL_WP_RESP_ID FROM DUAL;
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

--TRG_CDFX_DELVRY_WP_RESP_UPD
create or replace TRIGGER TRG_CDFX_DELVRY_WP_RESP_UPD 
  BEFORE UPDATE ON T_CDFX_DELVRY_WP_RESP
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

--TRG_CDFX_DELVRY_PARAM_INS
create or replace TRIGGER TRG_CDFX_DELVRY_PARAM_INS BEFORE INSERT ON T_CDFX_DELVRY_PARAM
  FOR EACH ROW
Begin
    IF :new.CDFX_DELVRY_PARAM_ID IS NULL THEN
        SELECT SeqV_Attributes.nextval INTO :new.CDFX_DELVRY_PARAM_ID FROM DUAL;
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

--TRG_CDFX_DELVRY_PARAM_UPD
create or replace TRIGGER TRG_CDFX_DELVRY_PARAM_UPD 
  BEFORE UPDATE ON T_CDFX_DELVRY_PARAM
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

spool off
