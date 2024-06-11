spool c:\temp\02_Create_Trigger.log

---------------------------------------------------------------
--  ALM TaskId : 333276
--  TRIGGER for table : T_REGION, T_WORKPACKAGE_DIVISION_CDL
---------------------------------------------------------------

CREATE OR REPLACE TRIGGER TRG_region_UPDT
BEFORE UPDATE ON t_region
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
ALTER TRIGGER TRG_region_UPDT ENABLE;

  CREATE OR REPLACE TRIGGER TRG_region_INS 
  BEFORE INSERT ON t_region
  FOR EACH ROW
Begin
    IF :new.region_id is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.region_id FROM DUAL;
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
ALTER TRIGGER TRG_region_INS ENABLE;  

		 CREATE OR REPLACE TRIGGER TRG_wp_div_cdl_UPDT
BEFORE UPDATE ON t_workpackage_division_cdl
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
ALTER TRIGGER TRG_wp_div_cdl_UPDT ENABLE;

--T_WORKPACKAGE_DIVISION_CDL

  CREATE OR REPLACE TRIGGER TRG_wp_div_cdl_INS 
  BEFORE INSERT ON t_workpackage_division_cdl
  FOR EACH ROW
Begin
    IF :new.wp_div_cdl_id is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.wp_div_cdl_id FROM DUAL;
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
ALTER TRIGGER TRG_wp_div_cdl_INS ENABLE;

---------------------------------------------------------------
--  ALM TaskId : 339648
--  TRIGGER for table : TABV_APIC_NODE_ACCESS
---------------------------------------------------------------

 CREATE OR REPLACE  TRIGGER TRG_APIC_NODE_ACC_INS BEFORE
    INSERT ON tabv_apic_node_access
    FOR EACH ROW
BEGIN
    IF
        :new.NODEACCESS_ID IS NULL
    THEN
        SELECT
            seqv_attributes.NEXTVAL
        INTO :new.NODEACCESS_ID
        FROM
            dual;

    END IF; 

    IF
        :new.version IS NULL
    THEN
        :new.version := 1;
    END IF;

    IF
        :new.created_date IS NULL
    THEN
        :new.created_date := sys_extract_utc(systimestamp);
    END IF;

    IF
        :new.created_user IS NULL
    THEN
        :new.created_user := user;
    END IF;

END;

/
ALTER TRIGGER TRG_APIC_NODE_ACC_INS ENABLE;

---------------------------------------------------------------------
--  ALM Story : 307426
---------------------------------------------------------------------
CREATE OR REPLACE  TRIGGER TRG_WEBFLOW_UPDT
BEFORE UPDATE ON T_WEBFLOW_ELEMENT
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
ALTER TRIGGER TRG_WEBFLOW_UPDT ENABLE;

  CREATE OR REPLACE TRIGGER TRG_WEBFLOW_INS
  BEFORE INSERT ON T_WEBFLOW_ELEMENT
  FOR EACH ROW
Begin
    IF :new.WEBFLOW_ID is null THEN
        SELECT SeqV_Attributes.nextval INTO :new.WEBFLOW_ID FROM DUAL;
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
ALTER TRIGGER TRG_WEBFLOW_INS ENABLE;

spool off