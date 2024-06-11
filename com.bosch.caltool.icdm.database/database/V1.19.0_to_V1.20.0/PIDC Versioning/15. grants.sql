spool c:\temp\15.grants.log

--
-- Grants for pidcversion table to DGS_ICDM_DEV_JPA user
--

GRANT select, insert, update, delete ON T_PIDC_VERSION TO DGS_ICDM_JPA;

--
-- Grants for pidcA2l table to DGS_ICDM_DEV_JPA user
--

GRANT select, insert, update, delete ON T_PIDC_A2L TO DGS_ICDM_JPA;

spool off
