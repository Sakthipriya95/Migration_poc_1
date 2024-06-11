spool c:\temp\50_grants.log

-- ICDM-2561
GRANT select, insert, update, delete ON T_FOCUS_MATRIX_VERSION TO DGS_ICDM_JPA;
GRANT select, insert, update, delete ON T_FOCUS_MATRIX_VERSION_ATTR TO DGS_ICDM_JPA;

--ICDM-2296
GRANT DELETE, INSERT, SELECT, UPDATE ON T_PREDEFINED_ATTR_VALUES TO DGS_ICDM_JPA;
GRANT DELETE, INSERT, SELECT, UPDATE ON T_PREDEFINED_VALIDITY TO DGS_ICDM_JPA;

--ICDM-2600
GRANT DELETE, INSERT, SELECT, UPDATE ON T_A2L_RESP TO DGS_ICDM_JPA;
GRANT DELETE, INSERT, SELECT, UPDATE ON T_WP_RESP TO DGS_ICDM_JPA;
GRANT DELETE, INSERT, SELECT, UPDATE ON T_A2L_GROUP TO DGS_ICDM_JPA;
GRANT DELETE, INSERT, SELECT, UPDATE ON T_A2L_GRP_PARAM TO DGS_ICDM_JPA;
GRANT DELETE, INSERT, SELECT, UPDATE ON T_A2L_WP_RESP TO DGS_ICDM_JPA;

---------------------------------------------------------------
--ICDM-2646
---------------------------------------------------------------
GRANT DELETE, INSERT, SELECT, UPDATE ON T_DIV_FC2WP_TYPE TO DGS_ICDM_JPA


spool off
