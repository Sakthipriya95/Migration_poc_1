spool c:\temp\60_Synonyms.log

-- ********************************************************
--
-- IMPORTANT : a) To be executed in DGS_ICDM_JPA user
--             b) Set the correct user name
-- ********************************************************

---------------------------------------------------------------------
--  ALM Task : 648015 - impl : Ruleset creation from ADMIN UI instead of using the stored procedure
---------------------------------------------------------------------

CREATE OR REPLACE SYNONYM pk_utils FOR dgs_icdm.pk_utils;

---------------------------------------------------------------------
--  ALM Task : 649285 - impl : Server end and DB changes for adding users to Bosch Dept/Group
---------------------------------------------------------------------

CREATE OR REPLACE SYNONYM T_A2L_RESPONSIBLITY_BSHGRP_USR FOR DGS_ICDM.T_A2L_RESPONSIBLITY_BSHGRP_USR;

------------------------------------------------------------------------------------------------------------------
--  ALM TaskId : 646662: Database design for ‘Coc Workpackages’ page of PIDC Editor.
------------------------------------------------------------------------------------------------------------------
CREATE OR REPLACE SYNONYM T_PIDC_VERS_COC_WP FOR DGS_ICDM.T_PIDC_VERS_COC_WP; 
CREATE OR REPLACE SYNONYM T_PIDC_VARIANT_COC_WP FOR DGS_ICDM.T_PIDC_VARIANT_COC_WP; 
CREATE OR REPLACE SYNONYM T_PIDC_SUB_VAR_COC_WP FOR DGS_ICDM.T_PIDC_SUB_VAR_COC_WP; 

------------------------------------------------------------------------------------------------------------------
--  ALM TaskId : 644742: Entity, Model, loader, command,service, service client class creation
------------------------------------------------------------------------------------------------------------------
CREATE OR REPLACE SYNONYM T_WPML_WP_MASTERLIST FOR DGS_ICDM.T_WPML_WP_MASTERLIST; 

spool off