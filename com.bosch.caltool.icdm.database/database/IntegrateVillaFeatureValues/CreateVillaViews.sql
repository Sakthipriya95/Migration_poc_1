--------------------------------------------------------
--  DDL for View V_LDB2_FEATURES
--------------------------------------------------------

CREATE OR REPLACE FORCE VIEW "DGS_ICDM"."V_LDB2_FEATURES" ("FEATURE_ID", "FEATURE_TEXT") AS 
  SELECT "FEATURE_ID", "FEATURE_TEXT"
 from V_LDB2_FEATURES@DGSPRO.WORLD@K5ESK_VILLA_RO
;

--------------------------------------------------------
--  DDL for View V_LDB2_VALUES
--------------------------------------------------------

CREATE OR REPLACE FORCE VIEW "DGS_ICDM"."V_LDB2_VALUES" ("VALUE_ID", "FEATURE_ID", "VALUE_TEXT") AS 
  SELECT "VALUE_ID", "FEATURE_ID", "VALUE_TEXT"
 from V_LDB2_VALUES@DGSPRO.WORLD@K5ESK_VILLA_RO
;

--------------------------------------------------------
--  DDL for View V_LDB2_VILLA_CUSTOMER
--------------------------------------------------------

CREATE OR REPLACE FORCE VIEW "DGS_ICDM"."V_LDB2_VILLA_CUSTOMER" ("ID", "NAME") AS 
  SELECT "ID", "NAME"
 from V_LDB2_VILLA_CUSTOMER@DGSPRO.WORLD@K5ESK_VILLA_RO
;

--------------------------------------------------------
--  DDL for View V_LDB2_VILLA_PROJECTS
--------------------------------------------------------

CREATE OR REPLACE FORCE VIEW "DGS_ICDM"."V_LDB2_VILLA_PROJECTS" ("ID", "NAME", "CODE", "ECUTYPE", "ECUTYPESEQNR") AS 
  SELECT "ID", "NAME", "CODE", "ECUTYPE", "ECUTYPESEQNR"
 from V_LDB2_VILLA_PROJECTS@DGSPRO.WORLD@K5ESK_VILLA_RO
;

--------------------------------------------------------
--  DDL for View V_LDB2_VILLA_SWVERS
--------------------------------------------------------

CREATE OR REPLACE FORCE VIEW "DGS_ICDM"."V_LDB2_VILLA_SWVERS" ("ID", "VERSIONNUMBER", "DESCRIPTION", "FK_PROJECTID") AS 
  SELECT "ID", "VERSIONNUMBER", "DESCRIPTION", "FK_PROJECTID"
 from V_LDB2_VILLA_SWVERS@DGSPRO.WORLD@K5ESK_VILLA_RO
;

--------------------------------------------------------
--  DDL for View V_LDB2_VILLA_SYSTEM
--------------------------------------------------------

CREATE OR REPLACE FORCE VIEW "DGS_ICDM"."V_LDB2_VILLA_SYSTEM" ("ID", "NAME") AS 
  SELECT "ID", "NAME"
 from V_LDB2_VILLA_SYSTEM@DGSPRO.WORLD@K5ESK_VILLA_RO
;

