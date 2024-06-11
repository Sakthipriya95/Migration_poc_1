--------------------------------------------------------
-- Copyright (c) Robert Bosch GmbH. All rights reserved.
--
--
--  DDL for Package Body PK_CREATE_WP_FROM_FUNC
--------------------------------------------------------
CREATE OR REPLACE PACKAGE BODY PK_CREATE_WP_FROM_FUNC
AS
  
  -- Update the ids in GTT_FUNCPARAMS with the actual param_ids from T_PARAMETER
  PROCEDURE p_update_param_ids
   AS 
   BEGIN  
    pk_log.info('Updating GTT_FUNCPARAMS Table with Param Ids started');
    UPDATE
        gtt_funcparams temp
    SET
        temp.id = ( SELECT 
                        id
                    FROM 
                        t_parameter
                    WHERE
                        name = temp.param_name
                        AND ptype = temp.type);
    pk_log.info('Updating GTT_FUNCPARAMS Table with Param Ids ended');
  END p_update_param_ids;
 
   -- Assign a2l wp responsibility to the parameters belonging to that function
  PROCEDURE p_update_param_mapping(
    p_pidc_vers_id NUMBER, 
    p_wp_defn_vers_id NUMBER)
  AS
     CURSOR cur_wp_resps_for_fun(p_pidc_vers_id NUMBER, p_wp_defn_vers_id NUMBER)
        IS
            SELECT
                wp_resp.wp_resp_id , wp.wp_name
            FROM
                t_a2l_wp_responsibility wp_resp, t_a2l_work_packages wp
            WHERE
                    wp_resp.a2l_wp_id = wp.a2l_wp_id
                AND wp.pidc_vers_id = p_pidc_vers_id
                AND wp_resp.wp_defn_vers_id = p_wp_defn_vers_id
                AND wp_resp.a2l_var_grp_id IS NULL
                AND wp.wp_name IN 
                    (SELECT DISTINCT
                        ( fun_name )
                    FROM 
                        gtt_funcparams);
          
        TYPE wp_resp_tab_type IS
            TABLE OF  cur_wp_resps_for_fun %ROWTYPE;
            
        v_wp_resp_data wp_resp_tab_type;
    
    BEGIN
    
        OPEN cur_wp_resps_for_fun(p_pidc_vers_id,p_wp_defn_vers_id);
        FETCH cur_wp_resps_for_fun BULK COLLECT INTO v_wp_resp_data;    
        CLOSE cur_wp_resps_for_fun;
                
        pk_log.info('Updation of wp param mappings with the wp resp with Function name started'); 
        FORALL indx IN 1..v_wp_resp_data.count     
            UPDATE
                t_a2l_wp_param_mapping
            SET
                wp_resp_id = v_wp_resp_data(indx).wp_resp_id           
            WHERE
                    wp_resp_id IN(
                        SELECT 
                            wp_resp_id 
                        FROM 
                            t_a2l_wp_responsibility
                        WHERE 
                            wp_defn_vers_id = p_wp_defn_vers_id)               
                AND param_id IN (
                        SELECT
                            id
                        FROM
                            gtt_funcparams 
                        WHERE
                             fun_name = v_wp_resp_data(indx).wp_name
                     );  
        pk_log.info('Updation of wp param mappings with the wp resp with Function name ended');
    END p_update_param_mapping;
    
   --Update the existing responsibilities to the newly created WPs with Function name
  PROCEDURE p_update_resp(
    p_wp_defn_vers_id IN NUMBER,
    p_def_wp_resp_id IN NUMBER,
    p_default_rb_resp_id IN NUMBER,
    p_pidc_vers_id IN NUMBER
  )
  AS
    
    CURSOR cur_wp_param_mapping (p_wp_defn_vers_id NUMBER) IS
        SELECT param_map.wp_param_map_id ,param_map.param_id, param_map.wp_resp_id, param_map.par_a2l_resp_id
        FROM   t_a2l_wp_param_mapping param_map, t_a2l_wp_responsibility wp_resp
        WHERE  param_map.wp_resp_id = wp_resp.wp_resp_id AND wp_resp.wp_defn_vers_id  = p_wp_defn_vers_id
            AND param_map.param_id IN (
                        SELECT id
                        FROM gtt_funcparams temp)
     ;

    CURSOR cur_wp_resp ( p_wp_resp_id NUMBER) IS
        SELECT *
        FROM t_a2l_wp_responsibility
        WHERE wp_resp_id = p_wp_resp_id;
     
     CURSOR cur_wp_par_resp_count IS   
        select wp_resp_id, par_a2l_resp_id    
            from (select wp_resp_id, par_a2l_resp_id, row_number() over (partition by wp_resp_id order by resp_count desc)  row_num 
                  from GTT_COUNT_RESP_FREQ
                  ) 
             where row_num = 1
             order by wp_resp_id
             ; 
    
    v_wp_param_mapping_rec_type cur_wp_param_mapping%ROWTYPE;
    v_wp_resp t_a2l_wp_responsibility%ROWTYPE;
    v_wp_resp_id NUMBER;
    
    TYPE respCountRecType IS RECORD (
      wp_resp_id   t_a2l_wp_param_mapping.wp_resp_id%TYPE, 
      par_a2l_resp_id   t_a2l_wp_param_mapping.par_a2l_resp_id%TYPE, 
      max_count NUMBER);
      
   TYPE respCountTabType IS TABLE OF respCountRecType;
   
    v_param_resp_count respCountTabType;
    
  BEGIN
    pk_log.start_new_job('pk_create_wp_from_func.p_updateResp');

    --Move the responsibilities from existing a2l wp responsbilities in WP level to param level, if no param level resp is assigned already      
    FOR v_wp_param_mapping_rec_type IN cur_wp_param_mapping(p_wp_defn_vers_id) LOOP
        OPEN cur_wp_resp(v_wp_param_mapping_rec_type.wp_resp_id);
        FETCH cur_wp_resp INTO v_wp_resp;
        CLOSE cur_wp_resp;
        --pk_log.debug('WP_Param_Map_ID,Resp_Id '|| v_wp_param_mapping_rec_type.wp_param_map_id ||  '-' || v_wp_resp.a2l_resp_id);
        UPDATE
            t_a2l_wp_param_mapping 
        SET
            par_a2l_resp_id = v_wp_resp.a2l_resp_id ,
            wp_resp_inherit_flag = 'N'
        WHERE
            wp_param_map_id = v_wp_param_mapping_rec_type.wp_param_map_id
            AND par_a2l_resp_id  is null;     
    END LOOP;    
    pk_log.info('Moved the responsibilities from wp level to param level');
    
    -- Update Wp Resp to the Parameters 
    p_update_param_mapping(p_pidc_vers_id,p_wp_defn_vers_id);   
    
    --insert into temp table
    INSERT INTO gtt_count_resp_freq(wp_resp_id, par_a2l_resp_id,resp_count)
        SELECT  param_map.wp_resp_id, param_map.par_a2l_resp_id, SUM(1) AS resp_count 
        FROM t_a2l_wp_param_mapping param_map , t_a2l_wp_responsibility wp_resp
        WHERE param_map.wp_resp_id = wp_resp.wp_resp_id AND wp_defn_vers_id = p_wp_defn_vers_id
        GROUP BY param_map.wp_resp_id, param_map.par_a2l_resp_id
    ;
    
    pk_log.info('Inserted (Wp - Responsibility - Count) into Temproray table');
    
    FOR respCountRecType IN cur_wp_par_resp_count LOOP
        -- pk_log.debug('(Wp - Responsibility - Count) '|| respCountRecType.wp_resp_id || '-'|| respCountRecType.par_a2l_resp_id);
        IF  
            respCountRecType.par_a2l_resp_id IS NOT NULL
        THEN
           
            UPDATE
                t_a2l_wp_responsibility
            SET
                a2l_resp_id = respCountRecType.par_a2l_resp_id
            WHERE
                wp_resp_id = respCountRecType.wp_resp_id;
                
            -- Update PAR_A2L_RESP_ID to null, because it is inherited from WP level.     
            UPDATE
                t_a2l_wp_param_mapping
            SET
                par_a2l_resp_id = null,
                 wp_resp_inherit_flag = 'Y'
            WHERE
                wp_resp_id = respCountRecType.wp_resp_id
                AND par_a2l_resp_id = respCountRecType.par_a2l_resp_id;
        END IF;
     END LOOP;
    pk_log.info('Param level responsibilities are updated successfully');
    pk_log.end_job;  
  
  EXCEPTION
        WHEN OTHERS THEN
            pk_log.error('Error in pk_create_wp_from_func.p_updateResp', sqlcode, sqlerrm);
            pk_log.end_job;
            RAISE;  
            
  END p_update_resp;
  
-------------------------------------------------------
-- Main procedure : create work packages from functions   
-------------------------------------------------------
PROCEDURE p_create_wp_from_func (
  p_wp_defn_vers_id IN NUMBER,
  p_delete_unused_wp IN VARCHAR2,
  p_keep_existing_resp IN VARCHAR2,
  p_created_user IN VARCHAR2
)
AS
    v_wp_id NUMBER;
    v_wp_resp_id NUMBER;
    
    v_pidc_a2l_id NUMBER;
    v_pidc_vers_id NUMBER;
    --Variable to store A2l wp responsibility Id with _DEFAULT_WP and Robert Bosch 
    v_default_wp_resp_id NUMBER;
    --Variable to store Responsibility Id for Default Robert Bosch Responsibility
    v_default_rb_resp_id NUMBER;
    --Variable to store Pidc A2l object
    vPidcA2L t_pidc_a2l%ROWTYPE;
    
    v_wp_exists VARCHAR(1);
    v_wp_resp_exists VARCHAR(1);
    
  BEGIN
  
    pk_log.start_new_job('pk_create_wp_from_func.p_create_wp_from_func');
    
    --update param ids in temp table
    p_update_param_ids;
    
    -- Get the pidc a2l id
    SELECT 
        pidc_a2l_id 
    INTO 
        v_pidc_a2l_id
    FROM 
         t_a2l_wp_defn_versions 
    WHERE 
         wp_defn_vers_id = p_wp_defn_vers_id;
         
    -- Get Pidc A2l 
    vPidcA2L := pk_group2pal.getPidcA2L(v_pidc_a2l_id);
    -- get/create default responsibility for Robert Bosch without ALIAS_NAME
    v_default_rb_resp_id := pk_group2pal.getDefaultA2lRespID(vPidcA2L.Project_ID, p_created_user);
    
    -- Get the default a2l wp responsibility id
    SELECT
        WP_RESP.wp_resp_id
    INTO 
        v_default_wp_resp_id
    FROM
        T_A2L_WP_RESPONSIBILITY WP_RESP,
        T_A2L_WORK_PACKAGES     WP
    WHERE
        WP_RESP.A2L_WP_ID = WP.A2L_WP_ID 
        AND WP_RESP.wp_defn_vers_id = p_wp_defn_vers_id 
        AND WP.wp_name = pk_group2pal.cDefaultWpName
        AND WP_RESP.A2L_VAR_GRP_ID IS NULL;
        
    v_pidc_vers_id := vPidcA2L.pidc_vers_id;
    
    FOR func IN (
        SELECT DISTINCT
             ( fun_name )
        FROM
            gtt_funcparams)
    LOOP
    -- If the A2L Work Package with the Function name already exists, use that wp_id to create A2L Wp Responsibility
    -- Else create A2l Work Package with the Function name and use the wp_id to create A2L Wp Responsibility
        --initialise v_wp_id, v_wp_resp_id to null
        v_wp_id := NULL;
        v_wp_resp_id:= NULL;       
         
       SELECT
            CASE
                WHEN EXISTS(
                    SELECT
                        a2l_wp_id 
                    FROM
                        t_a2l_work_packages
                    WHERE
                        wp_name = func.fun_name
                        AND pidc_vers_id = v_pidc_vers_id)
                THEN   'Y'
                ELSE   'N'
            END
        INTO v_wp_exists
        FROM DUAL;
        
        --Create WP with the function name, if the a2l work package with the function name is not available already   
        IF 
            (v_wp_exists = 'N')
        THEN
            INSERT INTO t_a2l_work_packages(
                wp_name,
                pidc_vers_id,
                created_user)
            VALUES(  func.fun_name, v_pidc_vers_id, p_created_user);
        END IF;
        
        --Fetch the a2l work package id into v_wp_id 
        SELECT
            a2l_wp_id 
        INTO  
            v_wp_id
        FROM
            t_a2l_work_packages
        WHERE
            wp_name = func.fun_name
            AND pidc_vers_id = v_pidc_vers_id;
                
        SELECT
            CASE
                WHEN EXISTS(
                    SELECT 
                        wp_resp_id 
                    FROM
                        t_a2l_wp_responsibility
                    WHERE
                            wp_defn_vers_id = p_wp_defn_vers_id
                        AND a2l_var_grp_id IS NULL
                        AND a2l_wp_id = v_wp_id)
                THEN   'Y'
                ELSE   'N'
            END
        INTO v_wp_resp_exists
        FROM DUAL;
        
        --Create WP with the function name, if the a2l work package with the function name is not available already   
        IF 
            (v_wp_resp_exists = 'N')
        THEN
            --After WP creation, create the a2l wp responsibility with wp id, default RB resp id if not available
            INSERT INTO t_a2l_wp_responsibility(
                wp_defn_vers_id,
                a2l_resp_id,
                a2l_var_grp_id,
                a2l_wp_id,
                created_user)
            VALUES( p_wp_defn_vers_id, v_default_rb_resp_id, NULL, v_wp_id, p_created_user); 
        END IF;
        --Fetch the wp resp id
        SELECT
            wp_resp_id
        INTO
            v_wp_resp_id
        FROM
            t_a2l_wp_responsibility
        WHERE
                wp_defn_vers_id = p_wp_defn_vers_id
            AND a2l_var_grp_id IS NULL
            AND a2l_wp_id = v_wp_id;
        
        END LOOP;
        pk_log.info('Creation of Wps, Wp-Resps with Function name for the pidc version ended');     
        
        --If user opts to keep the existing responsibilities in Param level
        IF 
            (p_keep_existing_resp = 'Y')
        THEN
            --Set the Param Level Mapping Change allowed flag to 'Y', if it is not allowed already.
            UPDATE 
                t_a2l_wp_defn_versions
            SET
                param_lvl_chg_allowd_flag = 'Y'
            WHERE
                wp_defn_vers_id = p_wp_defn_vers_id
                AND param_lvl_chg_allowd_flag != 'Y';
            pk_log.info('Updated the param level mapping change flag to "Y" ');    
            p_update_resp(p_wp_defn_vers_id, v_default_wp_resp_id,v_default_rb_resp_id, v_pidc_vers_id);
       ELSE
           p_update_param_mapping(v_pidc_vers_id,p_wp_defn_vers_id);                                   
       END IF;
    
    
    --If user opts to delete the WPs that are no longer neeeded after import
    IF 
        (p_delete_unused_wp = 'Y')
    THEN
         pk_log.info('Deletion of not used wp-resps after import started');  
        DELETE FROM 
            t_a2l_wp_responsibility wp_resp
        WHERE 
                wp_resp.wp_defn_vers_id = p_wp_defn_vers_id
            AND wp_resp.wp_resp_id != v_default_wp_resp_id
            AND NOT EXISTS ( select 1 from t_a2l_wp_param_mapping param_mapping where param_mapping.wp_resp_id = wp_resp.wp_resp_id );            
         pk_log.info('Deletion of not used wp-resps after import ended');  
    END IF;
    
    pk_log.info('Functions imported as Work Packages successfully');
    pk_log.end_job;
  
EXCEPTION
      WHEN OTHERS THEN
          pk_log.error('Error in pk_create_wp_from_func.p_create_wp_from_func', sqlcode, sqlerrm);
          pk_log.end_job;
          RAISE;
            
END p_create_wp_from_func;

END PK_CREATE_WP_FROM_FUNC;
/
