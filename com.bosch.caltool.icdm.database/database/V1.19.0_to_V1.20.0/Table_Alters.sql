spool c:\temp\table_alters.log

--------------------------------------------------------
--  2015-05-18
-------------------------------------------------------- 

--------------------------------------------------------
--- ICDM-1427 - Changes for adding a new Column in Review 
---Result table for storing rule set id.
--------------------------------------------------------
----------------------------------------------------------------------
--T_RVW_RESULTS--
----------------------------------------------------------------------
ALTER TABLE T_RVW_RESULTS ADD RSET_ID NUMBER(15);

ALTER TABLE T_RVW_RESULTS
ADD CONSTRAINT T_RVW_RESULTS_FK4
FOREIGN KEY (RSET_ID)
REFERENCES T_RULE_SET(RSET_ID);

commit;

-------------------------------------------------
--iCDM-1397
--Alter tabv_atribtues to add change_comment when an attribute is edited
----------------------------------------------------------
alter table TABV_ATTRIBUTES
add CHANGE_COMMENT varchar2(4000);

commit;
------------------------------------------------------------

-------------------------------------------------
--iCDM-1397
--Alter tabv_attr_values to add change_comment when an attribute value is edited
----------------------------------------------------------
alter table TABV_ATTR_VALUES
add CHANGE_COMMENT varchar2(4000);

commit;
------------------------------------------------------------

-------------------------------------------------
--iCDM-1397
--Alter tabv_attr_dependencies to add change_comment when an attribute/attribue value dependency is edited
----------------------------------------------------------
alter table TABV_ATTR_DEPENDENCIES
add CHANGE_COMMENT varchar2(4000);

commit;
------------------------------------------------------------

-------------------------------------------------
--iCDM-1397
--Alter tabv_attr_history to add change_comment when an attribute/attribue value/ dependency is edited
----------------------------------------------------------
alter table TABV_ATTR_HISTORY
add CHANGE_COMMENT varchar2(4000);

commit;
------------------------------------------------------------

spool off



