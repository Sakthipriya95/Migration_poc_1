------------------------------------------------------------------------------------------------------------------
--  ALM TaskId : 539935: Create store procedure for copying wp assignments from one a2l to other 
--  Package to copy a2l wp defiontion version from one a2l to other
------------------------------------------------------------------------------------------------------------------

create or replace PACKAGE PK_PAR2WP_COPY AS 

  /* Procedure to copy a2l wp definition version from one a2l to other */ 
  PROCEDURE p_par2wp_copy (
    p_source_wp_def_vers_id    IN NUMBER,
    p_dest_wp_def_vers_id      IN NUMBER,
    p_override_only_defaults   VARCHAR2,
    p_derive_from_func         VARCHAR2,
    p_created_user_name        VARCHAR2
);

END PK_PAR2WP_COPY;
/
