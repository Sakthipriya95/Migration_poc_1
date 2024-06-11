spool c:\temp\03_Table_Alter_pidc_fav.log
------------------------------------------------------------------------------------------------------------------
--  ALM TaskId : 400432: 
--  ALTER query for TABV_PID_FAVORITES
------------------------------------------------------------------------------------------------------------------
-- rename user_id column to user_id_old
ALTER TABLE TABV_PID_FAVORITES RENAME COLUMN USER_ID TO USER_ID_OLD;

--Drop the unique constraint
ALTER TABLE TABV_PID_FAVORITES DROP CONSTRAINT TABV_PID_FAVORITES_UNI;

-- add the user_id number column
alter table tabv_pid_favorites add user_id number;

-- set user id to the new column
update tabv_pid_favorites fav set fav.user_id = (select user_id from tabv_apic_users where upper(username) = upper(fav.user_id_old));
commit;
--  Verify the records (should not return any records)
select * from tabv_pid_favorites where user_id is null;

--  Add foreign key
ALTER TABLE TABV_PID_FAVORITES ADD CONSTRAINT TABV_PID_FAVORITES_FK2 FOREIGN KEY (USER_ID) REFERENCES TABV_APIC_USERS ( USER_ID ) ;

--Make column not null
ALTER TABLE TABV_PID_FAVORITES MODIFY USER_ID NUMBER NOT NULL;

--Re-create unique constraint for pidc_id + user_id
drop index TABV_PID_FAVORITES_UNI;
ALTER TABLE TABV_PID_FAVORITES ADD CONSTRAINT TABV_PID_FAVORITES_UNI UNIQUE(PROJECT_ID,USER_ID);

-- In case alter statement not possible due to uniqe key constraint
/*select PROJECT_ID,USER_ID, count(*) from TABV_PID_FAVORITES group by PROJECT_ID,USER_ID having count(*) > 1;
delete from TABV_PID_FAVORITES where user_id = 224265 and project_id = 2747 and rownum = 1;*/

--Add  new columns CREATED_USER, MODIFIED_USER, MODIFIED_DATE to TabvPidFavorite TABLE
ALTER TABLE TABV_PID_FAVORITES ADD (CREATED_USER VARCHAR2(100 BYTE),MODIFIED_DATE TIMESTAMP (6),MODIFIED_USER VARCHAR2(100 BYTE),VERSION NUMBER);

-- set values for version column 
update tabv_pid_favorites fav set fav.version = 1;

-- set values for created user column
update tabv_pid_favorites fav set fav.CREATED_USER = upper(user_id_old);
COMMIT;

--make version and created user columns not null
ALTER TABLE TABV_PID_FAVORITES MODIFY VERSION NUMBER NOT NULL;

ALTER TABLE TABV_PID_FAVORITES MODIFY CREATED_USER VARCHAR2(100 BYTE) NOT NULL;

spool off
