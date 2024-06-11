--------------------------------------------------------
-- Copyright (c) Robert Bosch GmbH. All rights reserved.
--
--
--  DDL for Package Body PK_A2L_WP_DETAILS_COPY
--------------------------------------------------------

create or replace PACKAGE BODY PK_A2L_WP_DETAILS_COPY AS
    
        
	-- this procedure does not have a commit statement. commit is happening via the command. 
	-- Also the T_A2L_WP_DEFN_VERSIONS record is already created
    procedure InsertA2lWpDetails(p_source_wp_def_vers_id IN NUMBER, p_dest_wp_def_vers_id IN NUMBER, p_created_user_name VARCHAR2) 
    AS
      
        --- variable declaration only used for validation purpose
        v_old_pidc_a2l_id number := 0;
        v_new_pidc_a2l_id number := 0;
        v_a2l_wp_resp_count number :=0;
        
    BEGIN 
    
        -- Check if both the wp defintion versions belong to the same Pidc a2l.
        select pidc_a2l_id into v_old_pidc_a2l_id 
          from T_A2L_WP_DEFN_VERSIONS 
          where WP_DEFN_VERS_ID = p_source_wp_def_vers_id;
        select pidc_a2l_id into v_new_pidc_a2l_id 
          from T_A2L_WP_DEFN_VERSIONS 
          where WP_DEFN_VERS_ID = p_dest_wp_def_vers_id;
        
        if(v_old_pidc_a2l_id <> v_new_pidc_a2l_id) then 
            raise_application_error(-20001,'Both source and destination WP defintion versions should have same PIDC A2L ID');
        end if;
            
        -- Check if the values are already available in the variant groups table.
        select count(1) into v_a2l_wp_resp_count 
          from t_a2l_wp_responsibility 
          where WP_DEFN_VERS_ID = p_dest_wp_def_vers_id;
        
        if(v_a2l_wp_resp_count > 0) then
            raise_application_error(-30001,'Child records are already available for the new WP definition version');
        end if;
        
        -- Insert into variant groups table
        -- get all the values from the source t_a2l_variant_groups table with the old wp def version id
        insert into t_a2l_variant_groups (group_name, group_desc, wp_defn_vers_id,created_user) 
            select group_name,group_desc, p_dest_wp_def_vers_id, p_created_user_name
            from t_a2l_variant_groups where wp_defn_vers_id = p_source_wp_def_vers_id;
          
        
          
        -- insert into variant group mapping table   
        -- get all the values from the source t_a2l_vargrp_variant_mapping table with the old variant group name and new varaint group name having same values
        insert into t_a2l_vargrp_variant_mapping (a2l_var_grp_id, variant_id, created_user) 
            select new.a2l_var_grp_id, var_mapping.variant_id, p_created_user_name
            from t_a2l_variant_groups new, t_a2l_variant_groups old, t_a2l_vargrp_variant_mapping var_mapping  
            where new.wp_defn_vers_id = p_dest_wp_def_vers_id
                and old.wp_defn_vers_id = p_source_wp_def_vers_id
                and old.group_name = new.group_name 
                and var_mapping.a2l_var_grp_id = old.a2l_var_grp_id;
           
    
        -- Insert into wp Responsibilities

        -- a) All wp responsibilities having variant groups mapping  the old and new varaint groups
        insert into t_a2l_wp_responsibility (wp_defn_vers_id,a2l_resp_id, a2l_wp_id,a2l_var_grp_id, created_user) 
            select p_dest_wp_def_vers_id, resp.a2l_resp_id,resp.a2l_wp_id, new.a2l_var_grp_id, p_created_user_name
            from t_a2l_variant_groups new, t_a2l_variant_groups old, t_a2l_wp_responsibility resp
            where old.wp_defn_vers_id = p_source_wp_def_vers_id
                and new.wp_defn_vers_id = p_dest_wp_def_vers_id
                and resp.wp_defn_vers_id= p_source_wp_def_vers_id
                and resp.a2l_var_grp_id = old.a2l_var_grp_id
                and new.group_name = old.group_name;

         -- b) All Wp responsibilities having variant group not mapped with a2l var grp id null
        insert into t_a2l_wp_responsibility (wp_defn_vers_id, a2l_resp_id,a2l_wp_id, a2l_var_grp_id, created_user) 
            select p_dest_wp_def_vers_id , resp.a2l_resp_id,resp.a2l_wp_id, null, p_created_user_name
            from t_a2l_wp_responsibility resp
            where resp.wp_defn_vers_id = p_source_wp_def_vers_id 
                and resp.a2l_var_grp_id is null;
    
    
        --- Insert into param mappings 
        
        -- a) Insert into mapping table modified with the mapped variant groups 
        insert into t_a2l_wp_param_mapping (param_id, wp_resp_id,wp_name_cust,wp_resp_inherit_flag,wp_name_cust_inherit_flag,par_a2l_resp_id ,created_user)
            select distinct mapping.param_id, resp_new.wp_resp_id,
                mapping.wp_name_cust, mapping.wp_resp_inherit_flag, mapping.wp_name_cust_inherit_flag, mapping.par_a2l_resp_id, p_created_user_name
            from t_a2l_wp_param_mapping mapping, t_a2l_wp_responsibility resp_old, t_a2l_wp_responsibility resp_new,
                t_a2l_variant_groups var_old, t_a2l_variant_groups var_new 
            where  resp_old.wp_defn_vers_id = p_source_wp_def_vers_id
                and resp_new.wp_defn_vers_id = p_dest_wp_def_vers_id
                and var_old.group_name = var_new.group_name
                and resp_old.a2l_var_grp_id = var_old.a2l_var_grp_id
                and resp_new.a2l_var_grp_id = var_new.a2l_var_grp_id
                and mapping.wp_resp_id = resp_old.wp_resp_id
                and resp_old.a2l_wp_id = resp_new.a2l_wp_id
                and resp_old.a2l_resp_id = resp_new.a2l_resp_id;
        
        -- b) Insert into mapping table modified with the variant group null.
        insert into t_a2l_wp_param_mapping (param_id, wp_resp_id,wp_name_cust,wp_resp_inherit_flag,wp_name_cust_inherit_flag,par_a2l_resp_id ,created_user)
            select distinct mapping.param_id,resp_new.wp_resp_id,
                mapping.wp_name_cust,mapping.wp_resp_inherit_flag,mapping.wp_name_cust_inherit_flag, mapping.par_a2l_resp_id,p_created_user_name
            from t_a2l_wp_param_mapping mapping , t_a2l_wp_responsibility resp_old , t_a2l_wp_responsibility resp_new 
            where  resp_old.wp_defn_vers_id=p_source_wp_def_vers_id 
                and  resp_new.wp_defn_vers_id =p_dest_wp_def_vers_id
                and resp_old.a2l_var_grp_id is null
                and resp_new.a2l_var_grp_id is null
                and mapping.wp_resp_id=resp_old.wp_resp_id
                and resp_old.a2l_wp_id=resp_new.a2l_wp_id
                and resp_old.a2l_resp_id=resp_new.a2l_resp_id ;
     
      
    END InsertA2lWpDetails;

END PK_A2L_WP_DETAILS_COPY;

/