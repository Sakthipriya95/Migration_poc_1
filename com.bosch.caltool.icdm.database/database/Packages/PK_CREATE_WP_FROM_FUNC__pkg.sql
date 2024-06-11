--------------------------------------------------------
-- Copyright (c) Robert Bosch GmbH. All rights reserved.
--
--
-- DDL for Package PK_CREATE_WP_FROM_FUNC
--------------------------------------------------------
CREATE OR REPLACE PACKAGE PK_CREATE_WP_FROM_FUNC
AS

  --
  -- Procedure to create work packages from functions
  --
  PROCEDURE p_create_wp_from_func (
    p_wp_defn_vers_id IN NUMBER,
    p_delete_unused_wp IN VARCHAR2,
    p_keep_existing_resp IN VARCHAR2,
    p_created_user IN VARCHAR2
  );

END PK_CREATE_WP_FROM_FUNC;
/
