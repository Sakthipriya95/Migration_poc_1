spool c:\temp\20_Create_Trigger.log

------------------------------------------------------------------------------------------------------------------
--Task 729577 :Allow adding groups to access rights page of PIDCs

------------------------------------------------------------------------------------------------------------------

-- Trigger for Insert & Update on table t_active_directory_groups
CREATE OR REPLACE TRIGGER trg_active_directory_groups_ins BEFORE
    INSERT ON t_active_directory_groups
    FOR EACH ROW
BEGIN
    IF :new.ad_group_id IS NULL THEN
        SELECT
            seqv_attributes.NEXTVAL
        INTO :new.ad_group_id
        FROM
            dual;

    END IF;

    IF :new.version IS NULL THEN
        :new.version := 1;
    END IF;

    IF :new.created_date IS NULL THEN
        :new.created_date := sys_extract_utc(systimestamp);
    END IF;

    IF :new.created_user IS NULL THEN
        :new.created_user := user;
    END IF;

END;
/


CREATE OR REPLACE TRIGGER trg_active_directory_groups_upd BEFORE
    UPDATE ON t_active_directory_groups
    FOR EACH ROW
BEGIN
    IF :new.version = :old.version THEN
        :new.version := :old.version + 1;
    END IF;

    IF :new.modified_date IS NULL OR NOT updating('Modified_Date') THEN
        :new.modified_date := sys_extract_utc(systimestamp);
    END IF;

    IF :new.modified_user IS NULL THEN
        :new.modified_user := user;
    END IF;

END;
/

-- Trigger for insert & update on table t_active_directory_group_users
CREATE OR REPLACE TRIGGER trg_ad_grp_users_ins BEFORE
    INSERT ON t_active_directory_group_users
    FOR EACH ROW
DECLARE
    t_user_id NUMBER;
BEGIN
    IF :new.GROUP_USERS_ID IS NULL THEN
        SELECT
            seqv_attributes.NEXTVAL
        INTO :new.GROUP_USERS_ID
        FROM
            dual;

    END IF;

    IF :new.created_date IS NULL THEN
        :new.created_date := sys_extract_utc(systimestamp);
    END IF;

    IF :new.created_user IS NULL THEN
        :new.created_user := user;
    END IF;

    IF :new.version IS NULL THEN
        :new.version := 1;
    END IF;

END;
/

CREATE OR REPLACE TRIGGER trg_ad_grp_user_upd BEFORE
    UPDATE ON t_active_directory_group_users
    FOR EACH ROW
BEGIN
    IF :new.version = :old.version THEN
        :new.version := :old.version + 1;
    END IF;

    IF :new.modified_date IS NULL OR NOT updating('Modified_Date') THEN
        :new.modified_date := sys_extract_utc(systimestamp);
    END IF;

    IF :new.modified_user IS NULL THEN
        :new.modified_user := user;
    END IF;

END;
/

-- On INSERT Trigger for Table T_ACTIVE_DIRECTORY_GROUP_NODE_ACCESS  

CREATE OR REPLACE TRIGGER "TRG_AD_GRP_ACC_INS" BEFORE
    INSERT ON T_ACTIVE_DIRECTORY_GROUP_NODE_ACCESS 
    FOR EACH ROW
BEGIN
    IF :new.GROUP_ACCESS_ID IS NULL THEN
        SELECT
            seqv_attributes.NEXTVAL
        INTO :new.GROUP_ACCESS_ID
        FROM
            dual;

    END IF;

    IF :new.version IS NULL THEN
        :new.version := 1;
    END IF;

    IF :new.created_date IS NULL THEN
        :new.created_date := sys_extract_utc(systimestamp);
    END IF;

    IF :new.created_user IS NULL THEN
        :new.created_user := user;
    END IF;

END;
/


-- On UPDATE Trigger for Table T_ACTIVE_DIRECTORY_GROUP_NODE_ACCESS  
CREATE OR REPLACE TRIGGER "TRG_AD_GRP_ACC_UPD" BEFORE
    UPDATE ON T_ACTIVE_DIRECTORY_GROUP_NODE_ACCESS 
    FOR EACH ROW
BEGIN
IF :new.version = :old.version THEN
    :new.version := :old.version + 1;
END IF;

IF :new.modified_date IS NULL OR NOT updating('Modified_Date') THEN
    :new.modified_date := sys_extract_utc(systimestamp);
END IF;

IF :new.modified_user IS NULL THEN
    :new.modified_user := user;
END IF;

end;
/

spool off
