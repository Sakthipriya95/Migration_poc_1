spool c:\temp\JpaUserScripts.log

--------------------------------------------------------
--
-- To be executed in DGS_ICDM_JPA user
--
--------------------------------------------------------


--------------------------------------------------------
--  2014-10-16
--create the Synonym for the objects in DGS_ICDM 


CREATE OR REPLACE SYNONYM TEST_PARENT_TABLE for DGS_ICDM.TEST_PARENT_TABLE;
CREATE OR REPLACE SYNONYM TEST_CHILD_TABLE for DGS_ICDM.TEST_CHILD_TABLE;
CREATE OR REPLACE SYNONYM TEST_WOKEYREF_TABLE for DGS_ICDM.TEST_WOKEYREF_TABLE;

CREATE OR REPLACE SYNONYM SEQ_TEMP for DGS_ICDM.SEQ_TEMP;



spool off
