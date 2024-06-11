spool c:\temp\04_grants_fc2wp.log


-------------------------------------------------
--Task 231531 Schema for FC2WP definition
-------------------------------------------------
GRANT select, insert, update, delete ON T_BASE_COMPONENTS TO DGS_ICDM_JPA;
GRANT select, insert, update, delete ON T_FC2WP_DEFINITION TO DGS_ICDM_JPA;
GRANT select, insert, update, delete ON T_FC2WP_DEF_VERSION TO DGS_ICDM_JPA;
GRANT select, insert, update, delete ON T_FC2WP_MAPPING TO DGS_ICDM_JPA;
GRANT select, insert, update, delete ON T_FC2WP_PT_TYPE_RELV TO DGS_ICDM_JPA;
GRANT select, insert, update, delete ON T_FC2WP_MAP_PT_TYPES TO DGS_ICDM_JPA;
GRANT select, insert, update, delete ON T_POWER_TRAIN_TYPE TO DGS_ICDM_JPA;
GRANT select, insert, update, delete ON T_WP_RESOURCE TO DGS_ICDM_JPA;

spool off