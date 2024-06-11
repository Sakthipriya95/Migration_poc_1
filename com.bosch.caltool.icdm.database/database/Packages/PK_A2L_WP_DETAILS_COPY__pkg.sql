--------------------------------------------------------
-- Copyright (c) Robert Bosch GmbH. All rights reserved.
--
--
--  DDL for Package PK_A2L_WP_DETAILS_COPY
--------------------------------------------------------

create or replace PACKAGE PK_A2L_WP_DETAILS_COPY AS

    --
    -- Procedure to insert the a2l wp deatils of one wp definition to another version 
    -- This procedure does NOT have a commit statement. commit should be executed outside. 
    -- The T_A2L_WP_DEFN_VERSIONS record is already created
    --
    procedure InsertA2lWpDetails(p_source_wp_def_vers_id IN NUMBER, p_dest_wp_def_vers_id IN NUMBER, p_created_user_name VARCHAR2 );

END PK_A2L_WP_DETAILS_COPY;

/
