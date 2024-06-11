spool c:\temp\grants.log

--------------------------------------------------------
--  Aug-20-2014
--------------------------------------------------------

-- ICDM-954  provide grant in ICDM JPA

GRANT select, insert, update, delete ON T_CHARACTERISTICS TO DGS_ICDM_JPA;

GRANT select, insert, update, delete ON T_CHARACTERISTIC_VALUES TO DGS_ICDM_JPA;


GRANT select, insert, update, delete ON T_SSD_FEATURES TO DGS_ICDM_JPA;

GRANT select, insert, update, delete ON T_SSD_VALUES TO DGS_ICDM_JPA;

spool off
