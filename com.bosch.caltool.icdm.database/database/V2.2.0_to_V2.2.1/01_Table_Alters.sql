spool c:\temp\01_Table_Alters.log

---------------------------------------------------------------------
--  Task 274417
--  Alter Query for T_LINKS table to add the column ATTR_VALUE_ID which is a foreign key to TABV_ATTR_VALUES table
---------------------------------------------------------------------  
ALTER TABLE T_LINKS ADD ATTR_VALUE_ID NUMBER(15);
UPDATE T_LINKS SET ATTR_VALUE_ID = NODE_ID where NODE_TYPE='ATTRIB_VALUE';
ALTER TABLE T_LINKS ADD (CONSTRAINT T_LINKS_FK 
  FOREIGN KEY (ATTR_VALUE_ID) 
  REFERENCES TABV_ATTR_VALUES (VALUE_ID));
  
  spool off