spool c:\temp\synonyms.log

--------------------------------------------------------
--
-- To be executed in DGS_ICDM_JPA user
--
--------------------------------------------------------


--------------------------------------------------------
--  Jun-13-2014
--------------------------------------------------------

--ICDM-801 create the Synonym for the table in DGS_ICDM 

CREATE OR REPLACE SYNONYM GTT_OBJECT_NAMES FOR DGS_ICDM.GTT_OBJECT_NAMES;


--------------------------------------------------------
--  June-16-2014
--------------------------------------------------------

-- iCDM-817

CREATE OR REPLACE SYNONYM T_COMP_PKG FOR DGS_ICDM.T_COMP_PKG;

CREATE OR REPLACE SYNONYM T_COMP_PKG_BC FOR DGS_ICDM.T_COMP_PKG_BC;

CREATE OR REPLACE SYNONYM T_COMP_PKG_BC_FC FOR DGS_ICDM.T_COMP_PKG_BC_FC;


spool off
