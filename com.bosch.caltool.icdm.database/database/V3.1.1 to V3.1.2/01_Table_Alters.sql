spool c:\temp\01_Table_Alters.log

-----------------------------------------------------------------------------------------------------------------
--  ALM TaskId : 506178: Add new columns 
--  ALTER query for T_FC2WP_DEFINITION to add fc2wp name , desc eng and desc ger
------------------------------------------------------------------------------------------------------------------
ALTER TABLE T_FC2WP_DEFINITION ADD (
    FC2WP_NAME varchar2(100),
    DESC_ENG varchar2(4000),
    DESC_GER  varchar2(4000)
);

--  Migrate the name from tabv_attr_values, via name_value_id column 
update T_FC2WP_DEFINITION def
set 
  def.FC2WP_NAME =
    (select value.textvalue_eng  from tabv_attr_values value where value.value_id=def.name_value_id),
  def.DESC_ENG =
    (select value.VALUE_DESC_ENG  from tabv_attr_values value where value.value_id=def.name_value_id),
  def.DESC_GER =
    (select value.VALUE_DESC_GER  from tabv_attr_values value where value.value_id=def.name_value_id);

commit;

ALTER TABLE T_FC2WP_DEFINITION MODIFY (FC2WP_NAME NOT NULL);

-- Remove Constraints for the name_value_id column and remove the column
ALTER TABLE T_FC2WP_DEFINITION drop constraint T_FC2WP_DEFINITION__FK1;
ALTER TABLE T_FC2WP_DEFINITION drop constraint T_FC2WP_DEFINITION__UN;
-- added, as it was not automatically removed, during constraint drop
DROP INDEX T_FC2WP_DEFINITION__UN;
ALTER TABLE T_FC2WP_DEFINITION ADD CONSTRAINT T_FC2WP_DEFINITION__UN UNIQUE (DIV_VALUE_ID, FC2WP_NAME);

------------------------------------------------------------------------------------------------------------------
--  ALM TaskId : 506963: Modify column NODE_TYPE in T_LINKS TABLE 
--  ALTER query for T_LINKS
------------------------------------------------------------------------------------------------------------------
ALTER TABLE T_LINKS MODIFY NODE_TYPE VARCHAR2(100);

------------------------------------------------------------------------------------------------------------------
--  ALM TaskId : 513708: update constraint, other changes in T_A2L_RESPONSIBILITY 
------------------------------------------------------------------------------------------------------------------
-- increase the size of the Department column same as alias name
ALTER TABLE T_A2L_RESPONSIBILITY MODIFY L_DEPARTMENT VARCHAR2(255);

--remove T_PIDC_WP_RESP_ALIAS_UK
ALTER TABLE T_A2L_RESPONSIBILITY 
    DROP CONSTRAINT T_PIDC_WP_RESP_ALIAS_UK;


--Copy the alias names to department column in t_a2l_responsibility before enabling the new constraint
UPDATE T_A2L_RESPONSIBILITY 
    SET L_DEPARTMENT = ALIAS_NAME 
    where b.L_LAST_NAME is null and b.L_FIRST_NAME is null and b.L_DEPARTMENT is null;
commit;

--add new constraint T_PIDC_WP_RESP_UK
ALTER TABLE T_A2L_RESPONSIBILITY
ADD CONSTRAINT T_PIDC_WP_RESP_UK UNIQUE 
(
  PROJECT_ID 
, RESP_TYPE 
, L_DEPARTMENT 
, USER_ID 
, L_LAST_NAME 
, L_FIRST_NAME 
);

spool off

