--------------------------------------------------------
-- Copyright (c) Robert Bosch GmbH. All rights reserved.
--
--
--  DDL for Procedure PRC_UPDATE_A2L_FINISHED
--------------------------------------------------------

-- This procedure does not have a commit statement. commit is happening via the command. 
create or replace procedure PRC_UPDATE_A2L_FINISHED
  ( 
      p_source_wp_def_vers_id IN NUMBER
    , p_dest_wp_def_vers_id IN NUMBER
  )
IS
     -- selecting the Wp-Resp where param mapping is modified in the new version
     -- Getting 'newly added params for wp-resp' Union 'removed params for wp-resp'
     --only the variant group level parammappings are validated with the cursor
    cursor cur_param_resp_wp_vg_lvl
    IS
      ( select a.param_id, b.a2l_resp_id, b.a2l_wp_id,c.GROUP_NAME 
          from t_a2l_wp_param_mapping a, t_a2l_wp_responsibility b , T_A2L_VARIANT_GROUPS c
          where   a.wp_resp_id  = b.wp_resp_id 
              and b.A2L_VAR_GRP_ID = c.A2L_VAR_GRP_ID
              and b.wp_defn_vers_id = p_dest_wp_def_vers_id  
        minus
        select a.param_id, b.a2l_resp_id, b.a2l_wp_id,c.GROUP_NAME 
          from t_a2l_wp_param_mapping a, t_a2l_wp_responsibility b  , T_A2L_VARIANT_GROUPS c
          where   a.wp_resp_id  = b.wp_resp_id 
              and b.A2L_VAR_GRP_ID = c.A2L_VAR_GRP_ID
              and b.wp_defn_vers_id = p_source_wp_def_vers_id 
      ) 
      union
      ( select a.param_id, b.a2l_resp_id, b.a2l_wp_id,c.GROUP_NAME 
          from t_a2l_wp_param_mapping a, t_a2l_wp_responsibility b , T_A2L_VARIANT_GROUPS c
          where   a.wp_resp_id  = b.wp_resp_id 
              and b.A2L_VAR_GRP_ID = c.A2L_VAR_GRP_ID
              and b.wp_defn_vers_id = p_source_wp_def_vers_id 
        minus
        select a.param_id, b.a2l_resp_id, b.a2l_wp_id,c.GROUP_NAME 
          from t_a2l_wp_param_mapping a, t_a2l_wp_responsibility b , T_A2L_VARIANT_GROUPS c
          where   a.wp_resp_id  = b.wp_resp_id 
              and b.A2L_VAR_GRP_ID = c.A2L_VAR_GRP_ID
              and b.wp_defn_vers_id = p_dest_wp_def_vers_id 
       );
     --only the default level parammappings are validated with the cursor  
    cursor cur_param_resp_wp_default_lvl
    IS  
       (select a.param_id, b.a2l_resp_id, b.a2l_wp_id
          from t_a2l_wp_param_mapping a, t_a2l_wp_responsibility b
          where   a.wp_resp_id  = b.wp_resp_id 
              and b.A2L_VAR_GRP_ID is null
              and b.wp_defn_vers_id = p_dest_wp_def_vers_id
        minus
        select a.param_id, b.a2l_resp_id, b.a2l_wp_id
          from t_a2l_wp_param_mapping a, t_a2l_wp_responsibility b 
          where   a.wp_resp_id  = b.wp_resp_id 
              and b.A2L_VAR_GRP_ID is null
              and b.wp_defn_vers_id = p_source_wp_def_vers_id
      ) 
      union
      ( select a.param_id, b.a2l_resp_id, b.a2l_wp_id
          from t_a2l_wp_param_mapping a, t_a2l_wp_responsibility b 
          where   a.wp_resp_id  = b.wp_resp_id 
              and b.A2L_VAR_GRP_ID is null
              and b.wp_defn_vers_id = p_source_wp_def_vers_id
        minus
        select a.param_id, b.a2l_resp_id, b.a2l_wp_id
          from t_a2l_wp_param_mapping a, t_a2l_wp_responsibility b 
          where   a.wp_resp_id  = b.wp_resp_id
              and b.A2L_VAR_GRP_ID is null
              and b.wp_defn_vers_id = p_dest_wp_def_vers_id
       );
       
     -- selecting the Wp-Resp where customized resposibility is modified in the new version
     -- Getting 'newly added customized resp for param WP' Union 'removed customized resp for param WP'
     -- only the variant group level parammappings are validated with the cursor
    cursor cur_customized_resp_wp_vg_lvl
    IS
      ( select a.param_id, a.par_a2l_resp_id, b.a2l_resp_id, b.a2l_wp_id,c.GROUP_NAME 
          from t_a2l_wp_param_mapping a, t_a2l_wp_responsibility b , T_A2L_VARIANT_GROUPS c
          where   a.wp_resp_id  = b.wp_resp_id 
              and b.A2L_VAR_GRP_ID = c.A2L_VAR_GRP_ID
              and b.wp_defn_vers_id = p_dest_wp_def_vers_id
        minus
        select a.param_id, a.par_a2l_resp_id, b.a2l_resp_id, b.a2l_wp_id,c.GROUP_NAME 
          from t_a2l_wp_param_mapping a, t_a2l_wp_responsibility b  , T_A2L_VARIANT_GROUPS c
          where   a.wp_resp_id  = b.wp_resp_id 
              and b.A2L_VAR_GRP_ID = c.A2L_VAR_GRP_ID
              and b.wp_defn_vers_id = p_source_wp_def_vers_id
      ) 
      union
      ( select a.param_id, a.par_a2l_resp_id, b.a2l_resp_id, b.a2l_wp_id,c.GROUP_NAME 
          from t_a2l_wp_param_mapping a, t_a2l_wp_responsibility b , T_A2L_VARIANT_GROUPS c
          where   a.wp_resp_id  = b.wp_resp_id 
              and b.A2L_VAR_GRP_ID = c.A2L_VAR_GRP_ID
              and b.wp_defn_vers_id = p_source_wp_def_vers_id
        minus
        select a.param_id, a.par_a2l_resp_id, b.a2l_resp_id, b.a2l_wp_id,c.GROUP_NAME 
          from t_a2l_wp_param_mapping a, t_a2l_wp_responsibility b , T_A2L_VARIANT_GROUPS c
          where   a.wp_resp_id  = b.wp_resp_id 
              and b.A2L_VAR_GRP_ID = c.A2L_VAR_GRP_ID
              and b.wp_defn_vers_id = p_dest_wp_def_vers_id
       );
       
     -- selecting the Wp-Resp where customized resposibility is modified in the new version
     -- Getting 'newly added customized resp for param WP' Union 'removed customized resp for param WP'
     -- only the default level parammappings are validated with the cursor  
    cursor cur_cust_resp_wp_default_lvl
    IS  
       (select a.param_id, a.par_a2l_resp_id, b.a2l_resp_id, b.a2l_wp_id
          from t_a2l_wp_param_mapping a, t_a2l_wp_responsibility b
          where   a.wp_resp_id  = b.wp_resp_id 
              and b.A2L_VAR_GRP_ID is null
              and b.wp_defn_vers_id = p_dest_wp_def_vers_id
        minus
        select a.param_id, a.par_a2l_resp_id, b.a2l_resp_id, b.a2l_wp_id
          from t_a2l_wp_param_mapping a, t_a2l_wp_responsibility b 
          where   a.wp_resp_id  = b.wp_resp_id 
              and b.A2L_VAR_GRP_ID is null
              and b.wp_defn_vers_id = p_source_wp_def_vers_id
      ) 
      union
      ( select a.param_id, a.par_a2l_resp_id, b.a2l_resp_id, b.a2l_wp_id
          from t_a2l_wp_param_mapping a, t_a2l_wp_responsibility b 
          where   a.wp_resp_id  = b.wp_resp_id 
              and b.A2L_VAR_GRP_ID is null
              and b.wp_defn_vers_id = p_source_wp_def_vers_id
        minus
        select a.param_id, a.par_a2l_resp_id, b.a2l_resp_id, b.a2l_wp_id
          from t_a2l_wp_param_mapping a, t_a2l_wp_responsibility b 
          where   a.wp_resp_id  = b.wp_resp_id
              and b.A2L_VAR_GRP_ID is null
              and b.wp_defn_vers_id = p_dest_wp_def_vers_id
       );

BEGIN 
    -- update the finished status to 'N' of the WP-Resp mapping where param mapping is different in variant group level
    for param_resp_wp_vg_lvl in cur_param_resp_wp_vg_lvl 
    loop
        update T_A2L_WP_RESPONSIBILITY_STATUS
	        Set WP_RESP_FIN_STATUS ='N'
	            where WP_RESP_ID in (
	                select WP_RESP_ID from t_a2l_wp_responsibility a, T_A2L_VARIANT_GROUPS b
	                    WHERE a.wp_defn_vers_id = p_dest_wp_def_vers_id 
	                    and a.a2l_resp_id = param_resp_wp_vg_lvl.a2l_resp_id 
	                    and a.a2l_wp_id = param_resp_wp_vg_lvl.a2l_wp_id
	                    and a.A2L_VAR_GRP_ID = b.A2L_VAR_GRP_ID
	                    and b.GROUP_NAME = param_resp_wp_vg_lvl.GROUP_NAME)
	            and A2L_RESP_ID is null;
    end loop;
    --update the finished status to 'N' of the WP-Resp mapping where param mapping is different in default level
    for param_resp_wp_default_lvl in cur_param_resp_wp_default_lvl 
    loop
        update T_A2L_WP_RESPONSIBILITY_STATUS
            Set WP_RESP_FIN_STATUS ='N'
                where WP_RESP_ID in (
                    select WP_RESP_ID from t_a2l_wp_responsibility 
                        WHERE wp_defn_vers_id = p_dest_wp_def_vers_id 
                        and a2l_resp_id = param_resp_wp_default_lvl.a2l_resp_id 
                        and a2l_wp_id = param_resp_wp_default_lvl.a2l_wp_id
                        and A2L_VAR_GRP_ID is null)
                and A2L_RESP_ID is null;
    end loop;
    -- update the finished status to 'N' of the WP-Resp mapping where customized resposibility is different in variant group level
    for customized_resp_wp_vg_lvl in cur_customized_resp_wp_vg_lvl 
    loop
        update T_A2L_WP_RESPONSIBILITY_STATUS
	        Set WP_RESP_FIN_STATUS ='N'
	            where WP_RESP_ID in (
	                select WP_RESP_ID from t_a2l_wp_responsibility a, T_A2L_VARIANT_GROUPS b
	                    WHERE a.wp_defn_vers_id = p_dest_wp_def_vers_id 
	                    and a.a2l_resp_id = customized_resp_wp_vg_lvl.a2l_resp_id 
	                    and a.a2l_wp_id = customized_resp_wp_vg_lvl.a2l_wp_id
	                    and a.A2L_VAR_GRP_ID = b.A2L_VAR_GRP_ID
	                    and b.GROUP_NAME = customized_resp_wp_vg_lvl.GROUP_NAME)
	            and ((A2L_RESP_ID = customized_resp_wp_vg_lvl.par_a2l_resp_id)
	                or (A2L_RESP_ID is null and customized_resp_wp_vg_lvl.par_a2l_resp_id is null));
    end loop;
    --update the finished status to 'N' of the WP-Resp mapping where customized resposibility is different in default level
    for customized_resp_wp_default_lvl in cur_cust_resp_wp_default_lvl 
    loop
        update T_A2L_WP_RESPONSIBILITY_STATUS
            Set WP_RESP_FIN_STATUS ='N'
                where WP_RESP_ID in (
                    select WP_RESP_ID from t_a2l_wp_responsibility 
                        WHERE wp_defn_vers_id = p_dest_wp_def_vers_id 
                        and a2l_resp_id = customized_resp_wp_default_lvl.a2l_resp_id 
                        and a2l_wp_id = customized_resp_wp_default_lvl.a2l_wp_id
                        and A2L_VAR_GRP_ID is null)
                and ((A2L_RESP_ID = customized_resp_wp_default_lvl.par_a2l_resp_id)
                    or (A2L_RESP_ID is null and customized_resp_wp_default_lvl.par_a2l_resp_id is null));
    end loop;
END;

/
