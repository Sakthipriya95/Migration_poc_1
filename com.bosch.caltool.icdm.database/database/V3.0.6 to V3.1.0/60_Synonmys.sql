spool c:\temp\60_Synonyms.log

-- ********************************************************
--
-- IMPORTANT : To be executed in DGS_ICDM_JPA user
--
-- ********************************************************


------------------------------------------------------------------------------------------------------------------
-- ALM Task ID : 463370
-- A2L Responsibility tables synonyms
------------------------------------------------------------------------------------------------------------------
CREATE OR REPLACE SYNONYM T_A2L_RESPONSIBILITY FOR DGS_ICDM.T_A2L_RESPONSIBILITY;
CREATE OR REPLACE SYNONYM T_A2L_WORK_PACKAGES FOR DGS_ICDM.T_A2L_WORK_PACKAGES;
CREATE OR REPLACE SYNONYM T_A2L_WP_DEFN_VERSIONS FOR DGS_ICDM.T_A2L_WP_DEFN_VERSIONS;
CREATE OR REPLACE SYNONYM T_A2L_VARIANT_GROUPS FOR DGS_ICDM.T_A2L_VARIANT_GROUPS;
CREATE OR REPLACE SYNONYM T_A2L_VARGRP_VARIANT_MAPPING FOR DGS_ICDM.T_A2L_VARGRP_VARIANT_MAPPING;
CREATE OR REPLACE SYNONYM T_A2L_WP_RESPONSIBILITY FOR DGS_ICDM.T_A2L_WP_RESPONSIBILITY;
CREATE OR REPLACE SYNONYM T_A2L_WP_PARAM_MAPPING FOR DGS_ICDM.T_A2L_WP_PARAM_MAPPING;
CREATE OR REPLACE SYNONYM T_RVW_WP_RESP FOR DGS_ICDM.T_RVW_WP_RESP;

------------------------------------------------------------------------------------------------------------------
-- ALM Task ID : 464549
-- T_COMPLI_RVW_HEX_PARAMS tables synonyms
------------------------------------------------------------------------------------------------------------------
CREATE OR REPLACE SYNONYM DGS_ICDM_JPA.T_COMPLI_RVW_HEX_PARAMS FOR DGS_ICDM.T_COMPLI_RVW_HEX_PARAMS;


------------------------------------------------------------------------------------------------------------------
--  ALM TaskId : 462621: ACL for services
------------------------------------------------------------------------------------------------------------------
CREATE OR REPLACE SYNONYM DGS_ICDM_JPA.T_WS_SERVICES FOR DGS_ICDM.T_WS_SERVICES;
CREATE OR REPLACE SYNONYM DGS_ICDM_JPA.T_WS_SYSTEM_SERVICES FOR DGS_ICDM.T_WS_SYSTEM_SERVICES;


------------------------------------------------------------------------------------------------------------------
--  ALM TaskId : 469617: Grant for PK_A2L_WP_DETAILS_COPY package 
------------------------------------------------------------------------------------------------------------------

CREATE OR REPLACE SYNONYM DGS_ICDM_JPA.PK_A2L_WP_DETAILS_COPY FOR DGS_ICDM.PK_A2L_WP_DETAILS_COPY;

------------------------------------------------------------------------------------------------------------------
--  ALM TaskId : 496581: Grant for PK_GROUP2PAL package 
------------------------------------------------------------------------------------------------------------------
CREATE OR REPLACE SYNONYM DGS_ICDM_JPA.PK_GROUP2PAL FOR DGS_ICDM.PK_GROUP2PAL;

spool off