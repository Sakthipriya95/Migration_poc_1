spool c:\temp\grants.log

--------------------------------------------------------
--  2014-11-2
--------------------------------------------------------

-- ICDM-2491  provide grant in ICDM JPA
GRANT select, insert, update, delete ON T_RVW_QNAIRE_ANSWER_OPL TO DGS_ICDM_JPA;

GRANT select, insert, update, delete ON T_RVW_QNAIRE_RESULTS TO DGS_ICDM_JPA;

--------------------------------------------------------
--  2017-02-03
--------------------------------------------------------

-- ICDM-2469  provide grant in ICDM JPA, since it is a view, I have added only select access
 GRANT select ON v_vcdm_datasets_workpkg_stat TO DGS_ICDM_JPA;

spool off
