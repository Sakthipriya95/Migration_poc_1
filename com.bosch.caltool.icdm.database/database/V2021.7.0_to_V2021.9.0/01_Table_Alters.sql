spool c:\temp\01_Table_Alters.log

---------------------------------------------------------------------------------------------------------------------   
--Task 564953 : Unicode remarks - new tables
---------------------------------------------------------------------------------------------------------------------   

-- New Table to store unicode remarks for Functions/Rulesets
CREATE TABLE T_RULE_REMARKS (
    RULE_REMARK_ID  NUMBER NOT NULL,
    RULE_ID         NUMBER NOT NULL, 
    REV_ID          NUMBER NOT NULL, 
    REMARK          NCLOB, 
    CREATED_USER    VARCHAR2(30 BYTE) NOT NULL , 
    CREATED_DATE    TIMESTAMP (6) NOT NULL, 
    MODIFIED_USER   VARCHAR2(30 BYTE), 
    MODIFIED_DATE   TIMESTAMP (6), 
    VERSION         NUMBER NOT NULL,
    
    PRIMARY KEY (RULE_REMARK_ID),
    CONSTRAINT T_RULE_REMARKS_UNI UNIQUE (RULE_ID, REV_ID)
   );
   
-- Temporary table to fetch unicode remarks for Functions/Rulesets
CREATE GLOBAL TEMPORARY TABLE GTT_RULE_REMARKS
   ( ID          NUMBER NOT NULL,        
     RULE_ID     NUMBER NOT NULL, 
     REV_ID      NUMBER NOT NULL,
     PRIMARY KEY (ID)
   ) 
   ON COMMIT DELETE ROWS ;

------------------------------------------------------------------------------------------------------------------
--  ALM TaskId : 611788: Impl: Review Rule Editor: Field 'Unit' should be editable
------------------------------------------------------------------------------------------------------------------

ALTER TABLE T_UNITS ADD ID NUMBER ;

UPDATE T_UNITS SET ID = SeqV_Attributes.nextval ;
COMMIT;

ALTER TABLE T_UNITS MODIFY ID NOT NULL;

ALTER TABLE T_UNITS DROP PRIMARY KEY;

ALTER TABLE T_UNITS ADD CONSTRAINT T_UNITS_PK PRIMARY KEY (ID);

ALTER TABLE T_UNITS ADD CONSTRAINT T_UNITS_UK UNIQUE (UNIT) ;


spool off