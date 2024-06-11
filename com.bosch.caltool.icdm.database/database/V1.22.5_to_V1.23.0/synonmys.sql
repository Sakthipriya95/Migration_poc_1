spool c:\temp\synonyms.log

----------------------------------------------------------------------------------
--  ICDM-1844 create synonym for  T_ALIAS_DETAILS  and T_ALIAS_DEFINITION
---------------------------------------------------------------------------------

CREATE OR REPLACE SYNONYM T_ALIAS_DETAILS FOR DGS_ICDM.T_ALIAS_DETAILS;
CREATE OR REPLACE SYNONYM T_ALIAS_DEFINITION FOR DGS_ICDM.T_ALIAS_DEFINITION;

---------------------------------------------------------------------------------
--  ICDM-1836 create synonym for T_MANDATORY_ATTR
---------------------------------------------------------------------------------
CREATE OR REPLACE SYNONYM T_MANDATORY_ATTR FOR DGS_ICDM.T_MANDATORY_ATTR;

--
--
--
CREATE OR REPLACE SYNONYM TA2L_VCDM_VERSIONS FOR DGS_ICDM.TA2L_VCDM_VERSIONS;
CREATE OR REPLACE SYNONYM T_FUNCVERS_UNIQUE FOR DGS_ICDM.T_FUNCVERS_UNIQUE;
CREATE OR REPLACE SYNONYM SET_SDOM_INFO_FOR_A2L FOR DGS_ICDM.SET_SDOM_INFO_FOR_A2L;

spool off

