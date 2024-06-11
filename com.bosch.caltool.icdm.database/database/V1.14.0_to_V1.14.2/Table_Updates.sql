spool c:\temp\table_updates.log

--------------------------------------------------------
--  2014-11-13
-------------------------------------------------------- 

--ICDM-1105
--Increase the width of description columns
alter table TABV_ATTRIBUTES modify (
  ATTR_DESC_ENG VARCHAR2(4000),  
  ATTR_DESC_GER VARCHAR2(4000) 
);


spool off


