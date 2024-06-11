spool c:\temp\50_Grants.log

---------------------------------------------------------------------
--  ALM Task : 648015 - impl : Ruleset creation from ADMIN UI instead of using the stored procedure
---------------------------------------------------------------------

GRANT execute on pk_utils to DGS_ICDM_JPA;

---------------------------------------------------------------------
--  ALM Task : 649285 - impl : Server end and DB changes for adding users to Bosch Dept/Group
---------------------------------------------------------------------

GRANT SELECT, INSERT, UPDATE, DELETE ON T_A2L_RESPONSIBLITY_BSHGRP_USR TO DGS_ICDM_JPA;



------------------------------------------------------------------------------------------------------------------
--  ALM TaskId : 646662: Database design for ‘Coc Workpackages’ page of PIDC Editor.
------------------------------------------------------------------------------------------------------------------
GRANT SELECT, INSERT, UPDATE, DELETE ON T_PIDC_VERS_COC_WP TO DGS_ICDM_JPA;
GRANT SELECT, INSERT, UPDATE, DELETE ON T_PIDC_VARIANT_COC_WP TO DGS_ICDM_JPA;
GRANT SELECT, INSERT, UPDATE, DELETE ON T_PIDC_SUB_VAR_COC_WP TO DGS_ICDM_JPA;

------------------------------------------------------------------------------------------------------------------
--  ALM TaskId : 644742: Entity, Model, loader, command,service, service client class creation
------------------------------------------------------------------------------------------------------------------
GRANT SELECT, INSERT, UPDATE, DELETE ON T_WPML_WP_MASTERLIST TO DGS_ICDM_JPA;

spool off