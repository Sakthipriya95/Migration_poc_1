spool c:\temp\60_Synonyms.log


-- ********************************************************
--
-- IMPORTANT : a) To be executed in DGS_ICDM_JPA user
--             b) Set the correct user name
-- ********************************************************


------------------------------------------------------------------------------------------------------------------
--  ALM TaskId : 539935: Create store procedure for copying wp assignments from one a2l to other 
------------------------------------------------------------------------------------------------------------------
CREATE OR REPLACE SYNONYM PK_PAR2WP_COPY FOR DGS_ICDM.PK_PAR2WP_COPY;

spool off
