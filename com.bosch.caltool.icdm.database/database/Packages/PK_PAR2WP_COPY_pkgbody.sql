------------------------------------------------------------------------------------------------------------------
--  ALM TaskId : 539935: Create store procedure for copying wp assignments from one a2l to other 
--  Package to copy a2l wp defiontion version from one a2l to other
------------------------------------------------------------------------------------------------------------------
create or replace PACKAGE BODY pk_par2wp_copy AS

    --Default WP name

    c_default_wp_name       CONSTANT VARCHAR2(12) := pk_group2pal.cdefaultwpname;

    --Flag to check whether src and dest has simliar wp definition version
    v_copy_mapping_valid    BOOLEAN := false;
    
    -- Bulk collect limit to update the destination param mapping
    c_param_mapping_blk_col_limit   CONSTANT NUMBER := 20000;
    
    --
    -- Get Pidc Version ID for selected wp definition version
    --
    FUNCTION get_pidc_vers_id (
        p_wp_defn_vers_id IN NUMBER)
    RETURN NUMBER IS

        CURSOR cur_get_pidc_vers_id (
            p_cur_wp_defn_vers_id IN NUMBER) 
        IS 
            SELECT
                pidc_vers_id
            FROM
                t_pidc_a2l
            WHERE
                pidc_a2l_id = (SELECT
                                    pidc_a2l_id
                                FROM
                                    t_a2l_wp_defn_versions
                                WHERE
                                    wp_defn_vers_id = p_cur_wp_defn_vers_id);

        v_pidc_vers_id   NUMBER;
    BEGIN
        OPEN cur_get_pidc_vers_id(p_wp_defn_vers_id);
            FETCH cur_get_pidc_vers_id INTO v_pidc_vers_id;
            CLOSE cur_get_pidc_vers_id;
        RETURN v_pidc_vers_id;
    END get_pidc_vers_id;

    --
    -- Get default wp resp id for given wp definition version
    --
    FUNCTION get_default_wp_resp_id (
        p_wp_defn_vers_id   IN NUMBER,
        p_pidc_vers_id      IN NUMBER) 
    RETURN NUMBER IS

        CURSOR cur_default_wp_resp_id (
            p_cur_wp_defn_vers_id   IN NUMBER,
            p_cur_pidc_vers_id      IN NUMBER) 
        IS 
            SELECT
                wp_resp_id
            FROM
                t_a2l_wp_responsibility a2l_wp_resp,
                t_a2l_work_packages wp
            WHERE
                a2l_wp_resp.wp_defn_vers_id = p_cur_wp_defn_vers_id
            AND a2l_wp_resp.a2l_wp_id = wp.a2l_wp_id
            AND wp.pidc_vers_id = p_cur_pidc_vers_id
            AND wp.wp_name = c_default_wp_name;

        v_default_wp_resp_id   NUMBER;
    BEGIN
        OPEN cur_default_wp_resp_id(p_wp_defn_vers_id,p_pidc_vers_id);
            FETCH cur_default_wp_resp_id INTO v_default_wp_resp_id;
            CLOSE cur_default_wp_resp_id;
        RETURN v_default_wp_resp_id;
    END get_default_wp_resp_id;
    
    --
    -- If source and destination has same wp definition version then throw an error
    --
    PROCEDURE compare_wp_defn_vers (
        p_source_wp_def_vers_id    IN NUMBER,
        p_dest_wp_def_vers_id      IN NUMBER,
        p_src_pidc_vers_id         IN NUMBER,
        p_dest_pidc_vers_id        IN NUMBER,
        p_override_only_defaults   VARCHAR2) 
    AS
        v_exist      VARCHAR(1);
        src_count    NUMBER;
        dest_count   NUMBER;
    BEGIN
        pk_log.debug('Check whether src and dest wp defn version has similar variant group');
        SELECT
            CASE
                WHEN EXISTS(
                    SELECT
                        a2l_var_grp_id,
                        group_name
                    FROM
                        t_a2l_variant_groups
                    WHERE
                        wp_defn_vers_id = p_source_wp_def_vers_id
                    AND group_name NOT IN ( SELECT
                                                group_name
                                            FROM
                                                t_a2l_variant_groups
                                            WHERE
                                                wp_defn_vers_id = p_dest_wp_def_vers_id)) 
                THEN 'Y'
                ELSE 'N'
            END
        INTO v_exist
        FROM
            dual;

        IF
            ( v_exist = 'Y' )
        THEN
            pk_log.debug('Changes available in variant group');
            v_copy_mapping_valid := true;
            return;
        END IF;

        IF
            ( p_override_only_defaults = 'Y' )
        THEN
            pk_log.info('Compare Source and destination wp definition version Started');
            pk_log.debug('Check whether src and dest has similar variant group variant mapping for override only default');
            SELECT
                CASE
                    WHEN EXISTS (
                        SELECT
                            src_var_grp.a2l_var_grp_id,
                            var_mapping.variant_id
                        FROM
                            t_a2l_variant_groups src_var_grp,
                            t_a2l_variant_groups dest_var_grp,
                            t_a2l_vargrp_variant_mapping var_mapping
                        WHERE
                                src_var_grp.wp_defn_vers_id = p_source_wp_def_vers_id
                            AND dest_var_grp.wp_defn_vers_id = p_dest_wp_def_vers_id
                            AND dest_var_grp.group_name IN (
                                SELECT
                                    obj_name
                                FROM
                                    gtt_object_names)
                            AND dest_var_grp.group_name = src_var_grp.group_name
                            AND var_mapping.a2l_var_grp_id = dest_var_grp.a2l_var_grp_id
                            AND var_mapping.variant_id NOT IN ( 
                                SELECT
                                    variant_id
                                FROM
                                    t_a2l_vargrp_variant_mapping
                                WHERE
                                    a2l_var_grp_id IN (
                                        SELECT
                                            a2l_var_grp_id
                                        FROM
                                            t_a2l_variant_groups
                                        WHERE
                                            wp_defn_vers_id = p_dest_wp_def_vers_id))) 
                        THEN 'Y'
                        ELSE 'N'
                    END
            INTO v_exist
            FROM
                dual;

            IF
                ( v_exist = 'Y' )
            THEN
                pk_log.debug('Changes available in variant group variant mapping');
                v_copy_mapping_valid := true;
                return;
            END IF;

            pk_log.debug('check whether src and dest has similar wp resp in var grp level');
            SELECT
                CASE
                    WHEN EXISTS (
                        SELECT
                            p_dest_wp_def_vers_id,
                            src_wp_resp.a2l_resp_id,
                            dest_wp.a2l_wp_id,
                            src_var_grp.a2l_var_grp_id
                        FROM
                            t_a2l_variant_groups src_var_grp,
                            t_a2l_variant_groups dest_var_grp,
                            t_a2l_wp_responsibility src_wp_resp,
                            t_a2l_work_packages src_wp,
                            t_a2l_work_packages dest_wp
                        WHERE
                                dest_var_grp.wp_defn_vers_id = p_source_wp_def_vers_id
                            AND src_var_grp.wp_defn_vers_id = p_dest_wp_def_vers_id
                            AND src_wp_resp.wp_defn_vers_id = p_source_wp_def_vers_id
                            AND src_wp_resp.a2l_var_grp_id = dest_var_grp.a2l_var_grp_id
                            AND dest_var_grp.group_name IN (
                                SELECT
                                    obj_name
                                FROM
                                    gtt_object_names)
                            AND src_wp_resp.a2l_wp_id = src_wp.a2l_wp_id
                            AND src_wp.wp_name NOT IN (
                                SELECT
                                    wp_name
                                FROM
                                    t_a2l_work_packages
                                WHERE
                                    a2l_wp_id IN (
                                        SELECT
                                            a2l_wp_id
                                        FROM
                                            t_a2l_wp_responsibility
                                        WHERE
                                                wp_defn_vers_id = p_dest_wp_def_vers_id
                                            AND a2l_var_grp_id IS NOT NULL))
                            AND dest_wp.wp_name = src_wp.wp_name
                            AND dest_wp.pidc_vers_id = p_dest_pidc_vers_id
                            AND dest_var_grp.group_name = src_var_grp.group_name) 
                    THEN 'Y'
                    ELSE 'N'
                END
            INTO v_exist
            FROM
                dual;

            IF
                ( v_exist = 'Y' )
            THEN
                pk_log.debug('Changes availbel in wp resp - Variant group level');
                v_copy_mapping_valid := true;
                return;
            END IF;

            pk_log.debug('check whether src and dest has similar wp resp in default level');
            SELECT
                CASE
                    WHEN EXISTS (
                        SELECT
                            dest_wp.a2l_wp_id,
                            p_dest_wp_def_vers_id,
                            src_wp_resp.a2l_resp_id
                        FROM
                            t_a2l_wp_responsibility src_wp_resp,
                            t_a2l_work_packages src_wp,
                            t_a2l_work_packages dest_wp
                        WHERE
                                src_wp_resp.wp_defn_vers_id = p_source_wp_def_vers_id
                            AND src_wp.a2l_wp_id = src_wp_resp.a2l_wp_id
                            AND src_wp.wp_name != c_default_wp_name
                            AND src_wp.wp_name NOT IN (
                                SELECT
                                    wp_name
                                FROM
                                    t_a2l_work_packages
                                WHERE
                                    a2l_wp_id IN (
                                        SELECT
                                            a2l_wp_id
                                        FROM
                                            t_a2l_wp_responsibility
                                        WHERE
                                                wp_defn_vers_id = p_dest_wp_def_vers_id
                                            AND a2l_var_grp_id IS NULL))
                            AND dest_wp.wp_name = src_wp.wp_name
                            AND dest_wp.pidc_vers_id = p_dest_pidc_vers_id
                            AND src_wp_resp.a2l_var_grp_id IS NULL )
                    THEN 'Y'
                    ELSE 'N'
                END
            INTO v_exist
            FROM
                dual;

            IF
                ( v_exist = 'Y' )
            THEN
                pk_log.debug('Changes availbel in wp resp - Default level');
                v_copy_mapping_valid := true;
                return;
            END IF;

        ELSE
            pk_log.debug('Check whether src and dest has similar variant group variant mapping');
            FOR src_var_grp IN (
                SELECT
                    a2l_var_grp_id,
                    group_name
                FROM
                    t_a2l_variant_groups
                WHERE
                    wp_defn_vers_id = p_source_wp_def_vers_id)
                LOOP
                    SELECT
                        COUNT(*)
                    INTO src_count
                    FROM
                        t_a2l_vargrp_variant_mapping
                    WHERE
                        a2l_var_grp_id = src_var_grp.a2l_var_grp_id;

                    SELECT
                        COUNT(*)
                    INTO dest_count
                    FROM
                        t_a2l_vargrp_variant_mapping dest_var_mapping,
                        t_a2l_variant_groups dest_var_grp
                    WHERE
                            dest_var_grp.wp_defn_vers_id = p_dest_wp_def_vers_id
                        AND dest_var_grp.group_name = src_var_grp.group_name
                        AND dest_var_mapping.a2l_var_grp_id = dest_var_grp.a2l_var_grp_id;

                    IF
                        ( src_count <> dest_count )
                    THEN
                        pk_log.debug('Changes available in variant group variant mapping count');
                        v_copy_mapping_valid := true;
                        return;
                    END IF;

                    SELECT
                        CASE
                            WHEN NOT EXISTS (
                                SELECT
                                    *
                                FROM
                                    t_a2l_vargrp_variant_mapping dest_var_mapping,
                                    t_a2l_vargrp_variant_mapping src_var_mapping,
                                    t_a2l_variant_groups dest_var_grp
                                WHERE
                                        dest_var_grp.wp_defn_vers_id = p_dest_wp_def_vers_id
                                    AND dest_var_grp.group_name = src_var_grp.group_name
                                    AND dest_var_mapping.a2l_var_grp_id = dest_var_grp.a2l_var_grp_id
                                    AND src_var_mapping.a2l_var_grp_id = src_var_grp.a2l_var_grp_id
                                    AND src_var_mapping.variant_id = dest_var_mapping.variant_id)
                                    AND src_count != 0 
                            THEN 'Y'
                            ELSE 'N'
                        END
                    INTO v_exist
                    FROM
                        dual;

                IF
                    ( v_exist = 'Y' )
                THEN
                    pk_log.debug('Changes available in variant group variant mapping');
                    v_copy_mapping_valid := true;
                    return;
                END IF;

            END LOOP;

            pk_log.debug('check whether src and dest has similar wp resp in default level');
            SELECT
                COUNT(*)
            INTO src_count
            FROM
                t_a2l_wp_responsibility
            WHERE
                    wp_defn_vers_id = p_source_wp_def_vers_id
                AND a2l_var_grp_id IS NULL;

            SELECT
                COUNT(*)
            INTO dest_count
            FROM
                t_a2l_wp_responsibility
            WHERE
                    wp_defn_vers_id = p_dest_wp_def_vers_id
                AND a2l_var_grp_id IS NULL;

            IF
                ( src_count <> dest_count )
            THEN
                pk_log.debug('Changes in wp resp count - Default level');
                v_copy_mapping_valid := true;
                return;
            END IF;

            SELECT
                CASE
                    WHEN EXISTS (
                        SELECT
                            src_wp.wp_name,
                            src_wp_resp.a2l_resp_id
                        FROM
                            t_a2l_wp_responsibility src_wp_resp,
                            t_a2l_work_packages src_wp
                        WHERE
                                src_wp_resp.wp_defn_vers_id = p_source_wp_def_vers_id
                            AND src_wp_resp.a2l_var_grp_id IS NULL
                            AND src_wp.a2l_wp_id = src_wp_resp.a2l_wp_id
                        MINUS
                        SELECT
                            dest_wp.wp_name,
                            dest_wp_resp.a2l_resp_id
                        FROM
                            t_a2l_wp_responsibility dest_wp_resp,
                            t_a2l_work_packages dest_wp
                        WHERE
                                dest_wp_resp.wp_defn_vers_id = p_dest_wp_def_vers_id
                            AND dest_wp_resp.a2l_var_grp_id IS NULL
                            AND dest_wp.a2l_wp_id = dest_wp_resp.a2l_wp_id) 
                    THEN 'Y'
                    ELSE 'N'
                END
            INTO v_exist
            FROM
                dual;

            IF
                ( v_exist = 'Y' )
            THEN
                pk_log.debug('Changes in wp resp - Default level');
                v_copy_mapping_valid := true;
                return;
            END IF;

            pk_log.debug('check whether src and dest has similar wp resp in var grp level');
            FOR src_var_grp IN (
                SELECT
                    a2l_var_grp_id,
                    group_name
                FROM
                    t_a2l_variant_groups
                WHERE
                    wp_defn_vers_id = p_source_wp_def_vers_id) 
            LOOP
                SELECT
                    COUNT(*)
                INTO src_count
                FROM
                    t_a2l_wp_responsibility
                WHERE
                    a2l_var_grp_id = src_var_grp.a2l_var_grp_id;

                SELECT
                    COUNT(*)
                INTO dest_count
                FROM
                    t_a2l_wp_responsibility dest_wp_resp,
                    t_a2l_variant_groups dest_var_grp
                WHERE
                        dest_var_grp.wp_defn_vers_id = p_dest_wp_def_vers_id
                    AND dest_var_grp.group_name = src_var_grp.group_name
                    AND dest_wp_resp.a2l_var_grp_id = dest_var_grp.a2l_var_grp_id;

                IF
                    ( src_count <> dest_count )
                THEN
                    pk_log.debug('Changes in wp resp count - Var grp level');
                    v_copy_mapping_valid := true;
                    return;
                END IF;

                SELECT
                    CASE
                        WHEN EXISTS (
                            SELECT
                                src_wp.wp_name,
                                src_wp_resp.a2l_resp_id
                            FROM
                                t_a2l_wp_responsibility src_wp_resp,
                                t_a2l_work_packages src_wp
                            WHERE
                                    src_wp_resp.wp_defn_vers_id = p_source_wp_def_vers_id
                                AND src_wp_resp.a2l_var_grp_id = src_var_grp.a2l_var_grp_id
                                AND src_wp.a2l_wp_id = src_wp_resp.a2l_wp_id
                            MINUS
                            SELECT
                                dest_wp.wp_name,
                                dest_wp_resp.a2l_resp_id
                            FROM
                                t_a2l_wp_responsibility dest_wp_resp,
                                t_a2l_variant_groups dest_var_grp,
                                t_a2l_work_packages dest_wp
                            WHERE
                                    dest_wp_resp.wp_defn_vers_id = p_dest_wp_def_vers_id
                                AND dest_var_grp.wp_defn_vers_id = p_dest_wp_def_vers_id
                                AND dest_var_grp.group_name = src_var_grp.group_name
                                AND dest_var_grp.a2l_var_grp_id = dest_wp_resp.a2l_var_grp_id
                                AND dest_wp.a2l_wp_id = dest_wp_resp.a2l_wp_id) 
                        THEN 'Y'
                        ELSE 'N'
                    END
                INTO v_exist
                FROM
                    dual;

                IF
                    ( v_exist = 'Y' )
                THEN
                    pk_log.debug('Changes in wp resp - Var grp level');
                    v_copy_mapping_valid := true;
                    return;
                END IF;

            END LOOP;

        END IF;

        pk_log.debug('check whether src and dest has similar wp param mapping');
        SELECT
            CASE
                WHEN EXISTS (
                    SELECT
                        src_wp_prm.param_id,
                        src_wp_resp.a2l_resp_id,
                        src_wp.wp_name,
                        src_wp_prm.wp_name_cust,
                        src_wp_prm.wp_resp_inherit_flag,
                        src_wp_prm.wp_name_cust_inherit_flag,
                        src_wp_prm.par_a2l_resp_id
                    FROM
                        t_a2l_wp_param_mapping src_wp_prm,
                        t_a2l_wp_responsibility src_wp_resp,
                        t_a2l_work_packages src_wp
                    WHERE
                            src_wp_resp.wp_defn_vers_id = p_source_wp_def_vers_id
                        AND src_wp_prm.wp_resp_id = src_wp_resp.wp_resp_id
                        AND src_wp.a2l_wp_id = src_wp_resp.a2l_wp_id
                        AND src_wp.pidc_vers_id = p_src_pidc_vers_id
                        AND src_wp_prm.param_id IN (
                            SELECT
                                param_id
                            FROM
                                gtt_param_mapping)
                    MINUS
                    SELECT
                        param_id,
                        dest_wp_resp.a2l_resp_id,
                        dest_wp.wp_name,
                        dest_wp_prm.wp_name_cust,
                        dest_wp_prm.wp_resp_inherit_flag,
                        dest_wp_prm.wp_name_cust_inherit_flag,
                        dest_wp_prm.par_a2l_resp_id
                    FROM
                        t_a2l_wp_param_mapping dest_wp_prm,
                        t_a2l_wp_responsibility dest_wp_resp,
                        t_a2l_work_packages dest_wp
                    WHERE
                            dest_wp_resp.wp_defn_vers_id = p_dest_wp_def_vers_id
                        AND dest_wp_prm.wp_resp_id = dest_wp_resp.wp_resp_id
                        AND dest_wp.a2l_wp_id = dest_wp_resp.a2l_wp_id
                        AND dest_wp.pidc_vers_id = p_dest_pidc_vers_id) 
                THEN 'Y'
                ELSE 'N'
            END
        INTO v_exist
        FROM
            dual;

        IF
            ( v_exist = 'Y' )
        THEN
            pk_log.debug('Changes in wp param mapping');
            v_copy_mapping_valid := true;
            return;
        END IF;

        pk_log.info('Compare Source and destination wp definition version Ended');
    END compare_wp_defn_vers;

    --
    -- Delete all variant grp, variant grp mapping, wp responsibility and param mapping
    --
    PROCEDURE reset_wp_defn_vers (
        p_dest_wp_def_vers_id       IN NUMBER,
        p_override_only_defaults    VARCHAR2,
        p_dest_default_wp_resp_id   IN NUMBER)
    AS
    BEGIN
        IF
            p_override_only_defaults <> 'Y'
        THEN
            pk_log.info('Reset wp definition version started');
            -- Update all param mapping to default workpackage mapping
            UPDATE t_a2l_wp_param_mapping
            SET
                wp_resp_id = p_dest_default_wp_resp_id,
                wp_name_cust = NULL,
                wp_resp_inherit_flag = 'Y',
                wp_name_cust_inherit_flag = 'Y',
                par_a2l_resp_id = NULL
            WHERE
                wp_resp_id IN (
                    SELECT
                        wp_resp_id
                    FROM
                        t_a2l_wp_responsibility
                    WHERE
                            wp_defn_vers_id = p_dest_wp_def_vers_id
                        AND a2l_var_grp_id IS NULL);

            pk_log.debug('delete wp mapping from variant group level');
            DELETE FROM t_a2l_wp_param_mapping
            WHERE
                wp_resp_id IN (
                    SELECT
                        wp_resp_id
                    FROM
                        t_a2l_wp_responsibility
                    WHERE
                            wp_defn_vers_id = p_dest_wp_def_vers_id
                        AND a2l_var_grp_id IS NOT NULL);
 
            pk_log.debug('delete all wp, responsibility mapping from destination wp defn version');
            DELETE FROM t_a2l_wp_responsibility
            WHERE
                    wp_defn_vers_id = p_dest_wp_def_vers_id
                AND wp_resp_id != p_dest_default_wp_resp_id;

            pk_log.debug('delete all variant grp variant mapping from dest wp defn version');
            DELETE FROM t_a2l_vargrp_variant_mapping
            WHERE
                a2l_var_grp_id IN (
                    SELECT
                        a2l_var_grp_id
                    FROM
                        t_a2l_variant_groups
                    WHERE
                        wp_defn_vers_id = p_dest_wp_def_vers_id);

            pk_log.debug('delete all variant grp from destination wp defn vers');
            DELETE FROM t_a2l_variant_groups
            WHERE
                wp_defn_vers_id = p_dest_wp_def_vers_id;
        END IF;
        pk_log.info('Reset wp definition version ended');
    END reset_wp_defn_vers;

    --
    -- Copy variant group from one wp definition version to other
    --
    PROCEDURE copy_variant_grp (
        p_source_wp_def_vers_id    IN NUMBER,
        p_dest_wp_def_vers_id      IN NUMBER,
        p_override_only_defaults   VARCHAR2,
        p_created_user_name        VARCHAR2)
    AS
    BEGIN
        pk_log.info('Copy variant group started');
        IF
            p_override_only_defaults = 'Y'
        THEN
        
            /*
            Override only defaults copu all vargroup available only in src and not in a2l and copy corresponding variant mapping for the copied variant grp.
            Copy all wp and resp mapping and corresponding varint grp mapping
            Copy param mapping from src to destination for parameter with default work package in destination.
            */
        
            /*
            If override only default then param mapping should be done only for parameter with default workpackage.
             All Workpackage, variant group, responsibility can be copied from source to destination if it is not already available.
            */

            pk_log.debug('Insert into variant groups table - Insert only variant group not available in destination wp definition version');
            INSERT INTO t_a2l_variant_groups (
                group_name,
                group_desc,
                wp_defn_vers_id,
                created_user)
                SELECT
                    group_name,
                    group_desc,
                    p_dest_wp_def_vers_id,
                    p_created_user_name
                FROM
                    t_a2l_variant_groups
                WHERE
                        wp_defn_vers_id = p_source_wp_def_vers_id
                    AND group_name NOT IN (
                        SELECT
                            group_name
                        FROM
                            t_a2l_variant_groups
                        WHERE
                            wp_defn_vers_id = p_dest_wp_def_vers_id);
            pk_log.debug('Inserted rows: ' || SQL%rowcount);
        ELSE
             /*
                Override all variant grp, variant grp variant mapping, wp, resp and param mapping from src to dest
            */
            pk_log.debug('Copy all variant group from src to dest');
            INSERT INTO t_a2l_variant_groups (
                group_name,
                group_desc,
                wp_defn_vers_id,
                created_user)
                SELECT
                    group_name,
                    group_desc,
                    p_dest_wp_def_vers_id,
                    p_created_user_name
                FROM
                    t_a2l_variant_groups
                WHERE
                    wp_defn_vers_id = p_source_wp_def_vers_id;
            pk_log.debug('Inserted rows: ' || SQL%rowcount);
        END IF;
        pk_log.info('Copy variant group ended');
    END copy_variant_grp;

    --
    -- Copy variant group variant mapping from one wp definition version to other
    --
    PROCEDURE copy_var_grp_var_mapping (
        p_source_wp_def_vers_id    IN NUMBER,
        p_dest_wp_def_vers_id      IN NUMBER,
        p_override_only_defaults   VARCHAR2,
        p_created_user_name        VARCHAR2)
    AS
    BEGIN
        pk_log.info('Copy variant group variant mapping started');
        IF
            p_override_only_defaults = 'Y'
        THEN
            pk_log.debug('insert into variant group mapping table  - Create variant grp variant mapping only for variant not available in destination wp defn version'); 
            INSERT INTO t_a2l_vargrp_variant_mapping (
                a2l_var_grp_id,
                variant_id,
                created_user)
                SELECT
                    dest_var_grp.a2l_var_grp_id,
                    var_mapping.variant_id,
                    p_created_user_name
                FROM
                    t_a2l_variant_groups src_var_grp,
                    t_a2l_variant_groups dest_var_grp,
                    t_a2l_vargrp_variant_mapping var_mapping
                WHERE
                        dest_var_grp.wp_defn_vers_id = p_dest_wp_def_vers_id
                    AND src_var_grp.wp_defn_vers_id = p_source_wp_def_vers_id
                    AND src_var_grp.group_name IN (
                        SELECT
                            obj_name
                        FROM
                            gtt_object_names)
                    AND src_var_grp.group_name = dest_var_grp.group_name
                    AND var_mapping.a2l_var_grp_id = src_var_grp.a2l_var_grp_id
                    AND var_mapping.variant_id NOT IN (
                        SELECT
                            variant_id
                        FROM
                            t_a2l_vargrp_variant_mapping
                        WHERE
                            a2l_var_grp_id IN (
                                SELECT
                                    a2l_var_grp_id
                                FROM
                                    t_a2l_variant_groups
                                WHERE
                                    wp_defn_vers_id = p_dest_wp_def_vers_id));
            pk_log.debug('Inserted rows: ' || SQL%rowcount);
        ELSE
            -- insert into variant group mapping table   
            pk_log.debug('Copy all variant grp variant mapping from src to dest by comparing the variant grp name');
            INSERT INTO t_a2l_vargrp_variant_mapping (
                a2l_var_grp_id,
                variant_id,
                created_user)
                SELECT
                    src_var_grp.a2l_var_grp_id,
                    var_mapping.variant_id,
                    p_created_user_name
                FROM
                    t_a2l_variant_groups src_var_grp,
                    t_a2l_variant_groups dest_var_grp,
                    t_a2l_vargrp_variant_mapping var_mapping
                WHERE
                        src_var_grp.wp_defn_vers_id = p_dest_wp_def_vers_id
                    AND dest_var_grp.wp_defn_vers_id = p_source_wp_def_vers_id
                    AND dest_var_grp.group_name = src_var_grp.group_name
                    AND var_mapping.a2l_var_grp_id = dest_var_grp.a2l_var_grp_id;
            pk_log.debug('Inserted rows: ' || SQL%rowcount);
        END IF;

        pk_log.info('Copy variant group variant mapping started');
    END copy_var_grp_var_mapping;

    --
    -- Copy a2l workpackage from source pidc version to dest pidc version
    --
    PROCEDURE copy_a2l_wp (
        p_src_pidc_vers_id    IN NUMBER,
        p_dest_pidc_vers_id   IN NUMBER)
    AS
    BEGIN
        pk_log.info('Copy a2l work package started');
        INSERT INTO t_a2l_work_packages (
            wp_name,
            wp_desc,
            wp_name_cust,
            pidc_vers_id,
            parent_a2l_wp_id)
            SELECT
                wp_name,
                wp_desc,
                wp_name_cust,
                p_dest_pidc_vers_id,
                a2l_wp_id
            FROM
                t_a2l_work_packages
            WHERE
                    pidc_vers_id = p_src_pidc_vers_id
                AND wp_name NOT IN (
                    SELECT
                        wp_name
                    FROM
                        t_a2l_work_packages
                    WHERE
                        pidc_vers_id = p_dest_pidc_vers_id);
        pk_log.debug('Inserted rows: ' || SQL%rowcount);
        pk_log.info('Copy a2l work package ended');
    END copy_a2l_wp;

    --
    -- Copy a2l workpackage responsibility from one wp definition version to other
    --
    PROCEDURE copy_a2l_wp_resp (
        p_source_wp_def_vers_id    IN NUMBER,
        p_dest_wp_def_vers_id      IN NUMBER,
        p_override_only_defaults   VARCHAR2,
        p_created_user_name        VARCHAR2,
        p_src_default_wp_resp_id   IN NUMBER,
        p_dest_pidc_vers_id        IN NUMBER)
    AS
    BEGIN
        pk_log.info('Copy a2l responsibility started');
        IF
            p_override_only_defaults = 'Y'
        THEN
            -- Insert into wp Responsibilities
            pk_log.debug('a) Insert workpackage and responsibility from src to dest if workpackage is not already available in variant grp level mapping');
            INSERT INTO t_a2l_wp_responsibility (
                wp_defn_vers_id,
                a2l_resp_id,
                a2l_wp_id,
                a2l_var_grp_id,
                created_user)
                SELECT
                    p_dest_wp_def_vers_id,
                    src_wp_resp.a2l_resp_id,
                    dest_wp.a2l_wp_id,
                    dest_var_grp.a2l_var_grp_id,
                    p_created_user_name
                FROM
                    t_a2l_variant_groups src_var_grp,
                    t_a2l_variant_groups dest_var_grp,
                    t_a2l_wp_responsibility src_wp_resp,
                    t_a2l_work_packages src_wp,
                    t_a2l_work_packages dest_wp
                WHERE
                        src_var_grp.wp_defn_vers_id = p_source_wp_def_vers_id
                    AND dest_var_grp.wp_defn_vers_id = p_dest_wp_def_vers_id
                    AND src_wp_resp.wp_defn_vers_id = p_source_wp_def_vers_id
                    AND src_wp_resp.a2l_var_grp_id = src_var_grp.a2l_var_grp_id
                    AND src_var_grp.group_name IN (
                        SELECT
                            obj_name
                        FROM
                            gtt_object_names)
                    AND src_wp_resp.a2l_wp_id = src_wp.a2l_wp_id
                    AND src_wp.wp_name NOT IN (
                        SELECT
                            wp_name
                        FROM
                            t_a2l_work_packages
                        WHERE
                            a2l_wp_id IN (
                                SELECT
                                    a2l_wp_id
                                FROM
                                    t_a2l_wp_responsibility
                                WHERE
                                        wp_defn_vers_id = p_dest_wp_def_vers_id
                                    AND a2l_var_grp_id IS NOT NULL))
                    AND dest_wp.wp_name = src_wp.wp_name
                    AND dest_wp.pidc_vers_id = p_dest_pidc_vers_id
                    AND src_var_grp.group_name = dest_var_grp.group_name;
            pk_log.debug('Inserted rows: ' || SQL%rowcount);

            pk_log.debug('b) Insert workpackage and responsibility from src to dest if workpackage is not already available in default level mapping');
            INSERT INTO t_a2l_wp_responsibility (
                a2l_wp_id,
                wp_defn_vers_id,
                a2l_resp_id,
                a2l_var_grp_id,
                created_user)
                SELECT DISTINCT
                    ( dest_wp.a2l_wp_id ),
                    p_dest_wp_def_vers_id,
                    src_wp_resp.a2l_resp_id,
                    NULL,
                    p_created_user_name
                FROM
                    t_a2l_wp_responsibility src_wp_resp,
                    t_a2l_work_packages src_wp,
                    t_a2l_work_packages dest_wp
                WHERE
                        src_wp_resp.wp_defn_vers_id = p_source_wp_def_vers_id
                    AND src_wp.a2l_wp_id = src_wp_resp.a2l_wp_id
                    AND src_wp.wp_name != c_default_wp_name
                    AND src_wp.wp_name NOT IN (
                        SELECT
                            wp_name
                        FROM
                            t_a2l_work_packages
                        WHERE
                            a2l_wp_id IN (
                                SELECT
                                    a2l_wp_id
                                FROM
                                    t_a2l_wp_responsibility
                                WHERE
                                        wp_defn_vers_id = p_dest_wp_def_vers_id
                                    AND a2l_var_grp_id IS NULL))
                    AND dest_wp.wp_name = src_wp.wp_name
                    AND dest_wp.pidc_vers_id = p_dest_pidc_vers_id
                    AND src_wp_resp.a2l_var_grp_id IS NULL;
            pk_log.debug('Inserted rows: ' || SQL%rowcount);
        ELSE
            pk_log.debug('a) Copy all wp resp mapping from src to dest in variant grp level');
            INSERT INTO t_a2l_wp_responsibility (
                wp_defn_vers_id,
                a2l_resp_id,
                a2l_wp_id,
                a2l_var_grp_id,
                created_user)
                SELECT
                    p_dest_wp_def_vers_id,
                    src_wp_resp.a2l_resp_id,
                    dest_wp.a2l_wp_id,
                    dest_var_grp.a2l_var_grp_id,
                    p_created_user_name
                FROM
                    t_a2l_variant_groups src_var_grp,
                    t_a2l_variant_groups dest_var_grp,
                    t_a2l_wp_responsibility src_wp_resp,
                    t_a2l_work_packages src_wp,
                    t_a2l_work_packages dest_wp
                WHERE
                        src_var_grp.wp_defn_vers_id = p_source_wp_def_vers_id
                    AND dest_var_grp.wp_defn_vers_id = p_dest_wp_def_vers_id
                    AND src_wp_resp.wp_defn_vers_id = p_source_wp_def_vers_id
                    AND src_wp_resp.a2l_wp_id = src_wp.a2l_wp_id
                    AND dest_wp.wp_name = src_wp.wp_name
                    AND dest_wp.pidc_vers_id = p_dest_pidc_vers_id
                    AND src_wp_resp.a2l_var_grp_id = src_var_grp.a2l_var_grp_id
                    AND dest_var_grp.group_name = src_var_grp.group_name;
            pk_log.debug('Inserted rows: ' || SQL%rowcount);
            
            pk_log.debug('b) Copy all wp resp mapping from src to dest in default level');
            INSERT INTO t_a2l_wp_responsibility (
                wp_defn_vers_id,
                a2l_resp_id,
                a2l_wp_id,
                a2l_var_grp_id,
                created_user)
                SELECT DISTINCT
                    p_dest_wp_def_vers_id,
                    src_wp_resp.a2l_resp_id,
                    dest_wp.a2l_wp_id,
                    NULL,
                    p_created_user_name
                FROM
                    t_a2l_wp_responsibility src_wp_resp,
                    t_a2l_wp_responsibility dest_wp_resp,
                    t_a2l_work_packages src_wp,
                    t_a2l_work_packages dest_wp
                WHERE
                        src_wp_resp.wp_defn_vers_id = p_source_wp_def_vers_id
                    AND dest_wp_resp.wp_defn_vers_id = p_dest_wp_def_vers_id
                    AND src_wp_resp.wp_resp_id != p_src_default_wp_resp_id
                    AND src_wp_resp.a2l_wp_id != dest_wp_resp.a2l_wp_id
                    AND src_wp_resp.a2l_wp_id = src_wp.a2l_wp_id
                    AND dest_wp.wp_name = src_wp.wp_name
                    AND dest_wp.pidc_vers_id = p_dest_pidc_vers_id
                    AND src_wp_resp.a2l_var_grp_id IS NULL;
            pk_log.debug('Inserted rows: ' || SQL%rowcount);
        END IF;
        pk_log.info('Copy a2l responsibility ended');
    END copy_a2l_wp_resp;

    --
    -- Copy a2l param mapping from src to destination for override only default
    --
    PROCEDURE copy_param_mapping_default (
        p_source_wp_def_vers_id     IN NUMBER,
        p_dest_wp_def_vers_id       IN NUMBER,
        p_override_only_defaults    VARCHAR2,
        p_src_default_wp_resp_id    IN NUMBER,
        p_dest_default_wp_resp_id   IN NUMBER,
        p_dest_pidc_vers_id         IN NUMBER) 
    AS

        CURSOR cur_default_src_par_map IS 
            SELECT
                param_id,
                wp_name_cust,
                wp_resp_inherit_flag,
                wp_name_cust_inherit_flag,
                par_a2l_resp_id,
                wp_resp_id
            FROM
                gtt_wp_param_mapping_details;

        TYPE default_src_par_map_rectype IS RECORD ( 
            param_id                    t_a2l_wp_param_mapping.param_id%TYPE,
            wp_name_cust                t_a2l_wp_param_mapping.wp_name_cust%TYPE,
            wp_resp_inherit_flag        t_a2l_wp_param_mapping.wp_resp_inherit_flag%TYPE,
            wp_name_cust_inherit_flag   t_a2l_wp_param_mapping.wp_name_cust_inherit_flag%TYPE,
            par_a2l_resp_id             t_a2l_wp_param_mapping.par_a2l_resp_id%TYPE,
            wp_resp_id                  t_a2l_wp_param_mapping.wp_resp_id%TYPE );

        -- table type with source param mapping details for default level
        TYPE default_src_par_map_tabtype IS
            TABLE OF default_src_par_map_rectype;

        --variable declaration for default level
        default_src_par_map_data    default_src_par_map_tabtype;
    BEGIN
        pk_log.info('Copy param mapping started');
        pk_log.debug('Filling temp table gtt_param_mapping_details');
        INSERT INTO gtt_wp_param_mapping_details (
            param_id,
            wp_name_cust,
            wp_resp_inherit_flag,
            wp_name_cust_inherit_flag,
            par_a2l_resp_id,
            wp_resp_id)
            SELECT
                src_wp_prm.param_id,
                src_wp_prm.wp_name_cust,
                src_wp_prm.wp_resp_inherit_flag,
                src_wp_prm.wp_name_cust_inherit_flag,
                src_wp_prm.par_a2l_resp_id,
                dest_wp_resp.wp_resp_id
            FROM
                t_a2l_wp_param_mapping src_wp_prm,
                t_a2l_wp_responsibility src_wp_resp,
                t_a2l_wp_responsibility dest_wp_resp,
                t_a2l_work_packages src_wp,
                t_a2l_work_packages dest_wp
            WHERE
                    src_wp_resp.wp_defn_vers_id = p_source_wp_def_vers_id
                AND src_wp_resp.a2l_var_grp_id IS NULL
                AND src_wp_prm.wp_resp_id = src_wp_resp.wp_resp_id
                AND dest_wp_resp.wp_defn_vers_id = p_dest_wp_def_vers_id
                AND src_wp.a2l_wp_id = src_wp_resp.a2l_wp_id
                AND dest_wp.wp_name = src_wp.wp_name
                AND dest_wp.pidc_vers_id = p_dest_pidc_vers_id
                AND dest_wp_resp.a2l_wp_id = dest_wp.a2l_wp_id
                AND dest_wp_resp.a2l_resp_id = src_wp_resp.a2l_resp_id
                AND dest_wp_resp.a2l_var_grp_id IS NULL;

        pk_log.debug('Inserted rows: ' || SQL%rowcount);    

        pk_log.debug('a) Insert into mapping table for parameter with default workpackage mapping in default level');
        OPEN cur_default_src_par_map;
        LOOP
            FETCH cur_default_src_par_map BULK COLLECT INTO default_src_par_map_data LIMIT c_param_mapping_blk_col_limit;
            EXIT WHEN default_src_par_map_data.count = 0;
            FORALL indx IN 1..default_src_par_map_data.count
                UPDATE t_a2l_wp_param_mapping
                SET
                    wp_resp_id = default_src_par_map_data(indx).wp_resp_id,
                    wp_name_cust = default_src_par_map_data(indx).wp_name_cust,
                    wp_resp_inherit_flag = default_src_par_map_data(indx).wp_resp_inherit_flag,
                    wp_name_cust_inherit_flag = default_src_par_map_data(indx).wp_name_cust_inherit_flag,
                    par_a2l_resp_id = default_src_par_map_data(indx).par_a2l_resp_id
                WHERE
                        wp_resp_id = p_dest_default_wp_resp_id
                    AND param_id = default_src_par_map_data(indx).param_id;
            pk_log.debug('Updated rows: ' || SQL%rowcount);
        END LOOP;

        CLOSE cur_default_src_par_map;

        pk_log.debug('b) Insert into mapping table for parameter with default workpackage mapping in variant group level');
        INSERT INTO t_a2l_wp_param_mapping (
            param_id,
            wp_resp_id,
            wp_name_cust,
            wp_resp_inherit_flag,
            wp_name_cust_inherit_flag,
            par_a2l_resp_id)
            SELECT
                src_wp_param.param_id,
                dest_wp_resp.wp_resp_id,
                src_wp_param.wp_name_cust,
                src_wp_param.wp_resp_inherit_flag,
                src_wp_param.wp_name_cust_inherit_flag,
                src_wp_param.par_a2l_resp_id
            FROM
                t_a2l_wp_param_mapping src_wp_param,
                t_a2l_wp_responsibility src_wp_resp,
                t_a2l_variant_groups src_var_grp,
                t_a2l_wp_responsibility dest_wp_resp,
                gtt_param_mapping dest_wp_param,
                t_a2l_work_packages src_wp,
                t_a2l_work_packages dest_wp
            WHERE
                    src_wp_resp.wp_defn_vers_id = p_source_wp_def_vers_id
                AND src_wp_param.wp_resp_id = src_wp_resp.wp_resp_id
                AND src_wp_resp.wp_resp_id != p_src_default_wp_resp_id
                AND src_var_grp.group_name IN (
                    SELECT
                        obj_name
                    FROM
                        gtt_object_names)
                AND src_var_grp.wp_defn_vers_id = p_source_wp_def_vers_id
                AND src_wp_resp.a2l_var_grp_id = src_var_grp.a2l_var_grp_id
                AND dest_wp_resp.wp_defn_vers_id = p_dest_wp_def_vers_id
                AND src_wp.a2l_wp_id = src_wp_resp.a2l_wp_id
                AND dest_wp.wp_name = src_wp.wp_name
                AND dest_wp.pidc_vers_id = p_dest_pidc_vers_id
                AND dest_wp_resp.a2l_wp_id = dest_wp.a2l_wp_id
                AND dest_wp_resp.a2l_resp_id = src_wp_resp.a2l_resp_id
                AND dest_wp_param.wp_resp_id = p_dest_default_wp_resp_id
                AND dest_wp_param.param_id = src_wp_param.param_id
                AND dest_wp_resp.a2l_var_grp_id = (
                    SELECT
                        a2l_var_grp_id
                    FROM
                        t_a2l_variant_groups
                    WHERE
                            group_name = src_var_grp.group_name
                        AND wp_defn_vers_id = p_dest_wp_def_vers_id)
                AND dest_wp_resp.wp_resp_id NOT IN (
                    SELECT
                        wp_resp_id
                    FROM
                        gtt_param_mapping);
        pk_log.debug('Inserted rows: ' || SQL%rowcount);
        pk_log.info('Copy param mapping ended');
    END copy_param_mapping_default;

    --
    -- Copy a2l param mapping from src to destination for override all
    --
    PROCEDURE copy_param_mapping_all (
        p_source_wp_def_vers_id     IN NUMBER,
        p_dest_wp_def_vers_id       IN NUMBER,
        p_override_only_defaults    VARCHAR2,
        p_src_default_wp_resp_id    IN NUMBER,
        p_dest_default_wp_resp_id   IN NUMBER,
        p_dest_pidc_vers_id         IN NUMBER) 
    AS

        CURSOR cur_default_src_par_map IS 
            SELECT
                param_id,
                wp_name_cust,
                wp_resp_inherit_flag,
                wp_name_cust_inherit_flag,
                par_a2l_resp_id,
                wp_resp_id
            FROM
                gtt_wp_param_mapping_details;

        TYPE default_src_par_map_rectype IS RECORD ( 
        param_id                    t_a2l_wp_param_mapping.param_id%TYPE,
        wp_name_cust                t_a2l_wp_param_mapping.wp_name_cust%TYPE,
        wp_resp_inherit_flag        t_a2l_wp_param_mapping.wp_resp_inherit_flag%TYPE,
        wp_name_cust_inherit_flag   t_a2l_wp_param_mapping.wp_name_cust_inherit_flag%TYPE,
        par_a2l_resp_id             t_a2l_wp_param_mapping.par_a2l_resp_id%TYPE,
        wp_resp_id                  t_a2l_wp_param_mapping.wp_resp_id%TYPE );

        -- table type with source param mapping details for default level
        TYPE default_src_par_map_tabtype IS
            TABLE OF default_src_par_map_rectype;

        --variable declaration for default level
        default_src_par_map_data    default_src_par_map_tabtype;
    BEGIN
        pk_log.info('Copy param mapping started');
        pk_log.debug('Filling temp table gtt_param_mapping_details');
        INSERT INTO gtt_wp_param_mapping_details (
            param_id,
            wp_name_cust,
            wp_resp_inherit_flag,
            wp_name_cust_inherit_flag,
            par_a2l_resp_id,
            wp_resp_id)
            SELECT
                src_wp_param.param_id,
                src_wp_param.wp_name_cust,
                src_wp_param.wp_resp_inherit_flag,
                src_wp_param.wp_name_cust_inherit_flag,
                src_wp_param.par_a2l_resp_id,
                dest_wp_resp.wp_resp_id
            FROM
                t_a2l_wp_param_mapping src_wp_param,
                t_a2l_wp_responsibility src_wp_resp,
                t_a2l_wp_responsibility dest_wp_resp,
                t_a2l_work_packages src_wp,
                t_a2l_work_packages dest_wp
            WHERE
                    src_wp_param.wp_resp_id = src_wp_resp.wp_resp_id
                AND src_wp_resp.wp_defn_vers_id = p_source_wp_def_vers_id
                AND src_wp_resp.a2l_var_grp_id IS NULL
                AND dest_wp_resp.wp_defn_vers_id = p_dest_wp_def_vers_id
                AND src_wp.a2l_wp_id = src_wp_resp.a2l_wp_id
                AND dest_wp.wp_name = src_wp.wp_name
                AND dest_wp.pidc_vers_id = p_dest_pidc_vers_id
                AND dest_wp_resp.a2l_wp_id = dest_wp.a2l_wp_id
                AND dest_wp_resp.a2l_resp_id = src_wp_resp.a2l_resp_id
                AND dest_wp_resp.a2l_var_grp_id IS NULL;

        pk_log.debug('Inserted rows: ' || SQL%rowcount);
        pk_log.debug('a) Insert into mapping table for parameter in default level');
        OPEN cur_default_src_par_map;
        LOOP
            FETCH cur_default_src_par_map BULK COLLECT INTO default_src_par_map_data LIMIT c_param_mapping_blk_col_limit;
            EXIT WHEN default_src_par_map_data.count = 0;
            FORALL indx IN 1..default_src_par_map_data.count
                UPDATE t_a2l_wp_param_mapping
                SET
                    wp_resp_id = default_src_par_map_data(indx).wp_resp_id,
                    wp_name_cust = default_src_par_map_data(indx).wp_name_cust,
                    wp_resp_inherit_flag = default_src_par_map_data(indx).wp_resp_inherit_flag,
                    wp_name_cust_inherit_flag = default_src_par_map_data(indx).wp_name_cust_inherit_flag,
                    par_a2l_resp_id = default_src_par_map_data(indx).par_a2l_resp_id
                WHERE
                        wp_resp_id IN (
                            SELECT
                                wp_resp_id
                            FROM
                                t_a2l_wp_responsibility
                            WHERE
                                    wp_defn_vers_id = p_dest_wp_def_vers_id
                                AND a2l_var_grp_id IS NULL)
                    AND param_id = default_src_par_map_data(indx).param_id;
            pk_log.debug('Updated rows: ' || SQL%rowcount);
        END LOOP;

        CLOSE cur_default_src_par_map;
            
       
        pk_log.debug('b) Insert into mapping table for parameter in variant group level');
        INSERT INTO t_a2l_wp_param_mapping (
            param_id,
            wp_resp_id,
            wp_name_cust,
            wp_resp_inherit_flag,
            wp_name_cust_inherit_flag,
            par_a2l_resp_id)
            SELECT
                src_wp_param.param_id,
                dest_wp_resp.wp_resp_id,
                src_wp_param.wp_name_cust,
                src_wp_param.wp_resp_inherit_flag,
                src_wp_param.wp_name_cust_inherit_flag,
                src_wp_param.par_a2l_resp_id
            FROM
                t_a2l_wp_param_mapping src_wp_param,
                t_a2l_wp_responsibility src_wp_resp,
                t_a2l_variant_groups src_var_grp,
                t_a2l_wp_responsibility dest_wp_resp,
                gtt_param_mapping dest_wp_param,
                t_a2l_work_packages src_wp,
                t_a2l_work_packages dest_wp
            WHERE
                    src_wp_param.wp_resp_id = src_wp_resp.wp_resp_id
                AND src_wp_resp.wp_defn_vers_id = p_source_wp_def_vers_id
                AND src_wp_resp.wp_resp_id != p_src_default_wp_resp_id
                AND src_var_grp.wp_defn_vers_id = p_source_wp_def_vers_id
                AND src_wp_resp.a2l_var_grp_id = src_var_grp.a2l_var_grp_id
                AND dest_wp_resp.wp_defn_vers_id = p_dest_wp_def_vers_id
                AND src_wp.a2l_wp_id = src_wp_resp.a2l_wp_id
                AND dest_wp.wp_name = src_wp.wp_name
                AND dest_wp.pidc_vers_id = p_dest_pidc_vers_id
                AND dest_wp_resp.a2l_wp_id = dest_wp.a2l_wp_id
                AND dest_wp_resp.a2l_resp_id = src_wp_resp.a2l_resp_id
                AND dest_wp_resp.a2l_var_grp_id = (
                    SELECT
                        a2l_var_grp_id
                    FROM
                        t_a2l_variant_groups
                    WHERE
                            group_name = src_var_grp.group_name
                        AND wp_defn_vers_id = p_dest_wp_def_vers_id)
                AND dest_wp_param.param_id = src_wp_param.param_id
                AND dest_wp_resp.wp_resp_id NOT IN (
                    SELECT
                        wp_resp_id
                    FROM
                        gtt_param_mapping);

        pk_log.debug('Inserted rows: ' || SQL%rowcount);
    END copy_param_mapping_all;

    --
    -- copy param mappings for new labels in destination from functions in source A2L
    --
    PROCEDURE copy_param_map_from_func (
        p_source_wp_def_vers_id     IN NUMBER,
        p_dest_wp_def_vers_id       IN NUMBER,
        p_override_only_defaults    VARCHAR2,
        p_src_default_wp_resp_id    IN NUMBER,
        p_dest_default_wp_resp_id   IN NUMBER,
        p_dest_pidc_vers_id         IN NUMBER) 
    AS

        fun_wp_resp_id              NUMBER;
        fun_count                   NUMBER := 0;
                    
        --record type for source param mapping details
        TYPE src_par_map_data_rectype IS RECORD ( 
            par_a2l_resp_id             t_a2l_wp_param_mapping.par_a2l_resp_id%TYPE,
            wp_name_cust                t_a2l_wp_param_mapping.wp_name_cust%TYPE,
            wp_resp_inherit_flag        t_a2l_wp_param_mapping.wp_resp_inherit_flag%TYPE,
            wp_name_cust_inherit_flag   t_a2l_wp_param_mapping.wp_name_cust_inherit_flag%TYPE,
            wp_id                       t_a2l_wp_responsibility.a2l_wp_id%TYPE );

        -- table type with source param mapping details
        TYPE src_par_map_data_tabtype IS
            TABLE OF src_par_map_data_rectype;

        --variable declaration
        src_par_map_data            src_par_map_data_tabtype;
    BEGIN
        pk_log.info('Copy param mapping using function started');
        pk_log.debug('iterate through source functions in temp table gtt_funcparams');
        FOR src_func IN (
            SELECT DISTINCT
                ( fun_name )
            FROM
                gtt_funcparams
            WHERE
                type = 'S') 
        LOOP
        
            --for default level mappings
       
            --collect unique wp_resp_id , par_a2l_resp_id combinations 
            --for the given function in source A2L
            SELECT DISTINCT
                wpparmap.par_a2l_resp_id,
                wpparmap.wp_name_cust,
                wpparmap.wp_resp_inherit_flag,
                wpparmap.wp_name_cust_inherit_flag,
                wpresp.a2l_wp_id
            BULK COLLECT
            INTO src_par_map_data
            FROM
                gtt_funcparams funparam,
                t_parameter param,
                t_a2l_wp_param_mapping wpparmap,
                t_a2l_wp_responsibility wpresp
            WHERE
                    funparam.param_name = param.name
                AND param.id = wpparmap.param_id
                AND wpparmap.wp_resp_id = wpresp.wp_resp_id
                AND funparam.fun_name = src_func.fun_name
                AND wpresp.wp_defn_vers_id = p_source_wp_def_vers_id
                AND wpresp.a2l_var_grp_id IS NULL
                AND funparam.type = 'S';

            pk_log.debug('Default level - mapping count: ' || src_par_map_data.count);
            IF
                ( src_par_map_data.count = 1 )
            THEN
                --if the count is 1, then a distinct combination is available
                --for all parameters in this function
                pk_log.debug('Default level - Distinct mapping Function name: ' || src_func.fun_name);
                IF
                    ( p_override_only_defaults = 'Y' )
                THEN
                    -- override only default mappings
                    -- for all parameters in destination belonging to the function
                    -- update param mappings in default level with the identified mapping from source 
                    UPDATE t_a2l_wp_param_mapping
                    SET
                        wp_resp_id = (
                            SELECT
                                destwpresp.wp_resp_id
                            FROM
                                t_a2l_work_packages srcwp,
                                t_a2l_wp_responsibility destwpresp,
                                t_a2l_work_packages destwp
                            WHERE
                                    srcwp.a2l_wp_id = src_par_map_data(1).wp_id
                                AND destwpresp.a2l_wp_id = destwp.a2l_wp_id
                                AND destwp.wp_name = srcwp.wp_name
                                AND destwpresp.wp_defn_vers_id = p_dest_wp_def_vers_id
                                AND destwpresp.a2l_var_grp_id IS NULL),
                        par_a2l_resp_id = src_par_map_data(1).par_a2l_resp_id,
                        wp_name_cust = src_par_map_data(1).wp_name_cust,
                        wp_resp_inherit_flag = src_par_map_data(1).wp_resp_inherit_flag,
                        wp_name_cust_inherit_flag = src_par_map_data(1).wp_name_cust_inherit_flag
                    WHERE
                        EXISTS (
                            SELECT
                                param.id
                            FROM
                                t_parameter param,
                                gtt_funcparams temp
                            WHERE
                                    param.name = temp.param_name
                                AND temp.fun_name = src_func.fun_name
                                AND temp.type = 'D'
                                AND param_id = param.id)
                        AND wp_resp_id = p_dest_default_wp_resp_id;

                ELSE
                    -- override all wp_resp
                    -- for all parameters in destination belonging to the function
                    -- update param mappings in default level with the identified mapping from source 
                    UPDATE t_a2l_wp_param_mapping parmapping
                    SET
                        wp_resp_id = (
                            SELECT
                                destwpresp.wp_resp_id
                            FROM
                                t_a2l_work_packages srcwrkpckg,
                                t_a2l_wp_responsibility destwpresp,
                                t_a2l_work_packages destwrkpckg
                            WHERE
                                    srcwrkpckg.a2l_wp_id = src_par_map_data(1).wp_id
                                AND destwpresp.a2l_wp_id = destwrkpckg.a2l_wp_id
                                AND destwrkpckg.wp_name = srcwrkpckg.wp_name
                                AND destwpresp.wp_defn_vers_id = p_dest_wp_def_vers_id
                                AND destwpresp.a2l_var_grp_id IS NULL),
                        par_a2l_resp_id = src_par_map_data(1).par_a2l_resp_id,
                        wp_name_cust = src_par_map_data(1).wp_name_cust,
                        wp_resp_inherit_flag = src_par_map_data(1).wp_resp_inherit_flag,
                        wp_name_cust_inherit_flag = src_par_map_data(1).wp_name_cust_inherit_flag
                    WHERE
                        EXISTS (
                            SELECT
                                param.id
                            FROM
                                t_parameter param,
                                gtt_funcparams temp
                            WHERE
                                    param.name = temp.param_name
                                AND temp.fun_name = src_func.fun_name
                                AND temp.type = 'D'
                                AND param.id = parmapping.param_id)
                        AND EXISTS (
                            SELECT
                                desta2lwpresp.wp_resp_id
                            FROM
                                t_a2l_wp_responsibility desta2lwpresp
                            WHERE
                                    desta2lwpresp.wp_defn_vers_id = p_dest_wp_def_vers_id
                                AND desta2lwpresp.a2l_var_grp_id IS NULL
                                AND desta2lwpresp.wp_resp_id = parmapping.wp_resp_id);
                --end of override all mappings
                END IF;
            --end of distinct mapping if
            END IF;
        
            --for var group level param mappings
            -- iterate over the variant groups which has distinct mapping in source 
            -- for the given function

            FOR vargroup IN (
                SELECT
                    group_name,
                    a2l_var_grp_id,
                    COUNT(*)
                FROM
                    (
                        SELECT DISTINCT
                            parammapping.wp_resp_id,
                            parammapping.par_a2l_resp_id,
                            vargrp.group_name,
                            vargrp.a2l_var_grp_id
                        FROM
                            gtt_funcparams funparam,
                            t_parameter param,
                            t_a2l_wp_param_mapping parammapping,
                            t_a2l_wp_responsibility wpresp,
                            t_a2l_variant_groups vargrp
                        WHERE
                            funparam.param_name = param.name
                            AND param.id = parammapping.param_id
                            AND parammapping.wp_resp_id = wpresp.wp_resp_id
                            AND funparam.fun_name = src_func.fun_name
                            AND wpresp.wp_defn_vers_id = p_source_wp_def_vers_id
                            AND vargrp.a2l_var_grp_id = wpresp.a2l_var_grp_id)
                GROUP BY
                    group_name,
                    a2l_var_grp_id
                HAVING
                    COUNT(*) = 1) 
            LOOP
                --for each variant group name with distinct param mapping combination
                pk_log.debug(' Distinct mapping var group name: ' || vargroup.group_name);
                --collect the param mapping details from source for the variant group name
                SELECT DISTINCT
                    wpparam.par_a2l_resp_id,
                    wpparam.wp_name_cust,
                    wpparam.wp_resp_inherit_flag,
                    wpparam.wp_name_cust_inherit_flag,
                    wpresp.a2l_wp_id
                BULK COLLECT
                INTO src_par_map_data
                FROM
                    gtt_funcparams funparam,
                    t_parameter param,
                    t_a2l_wp_param_mapping wpparam,
                    t_a2l_wp_responsibility wpresp
                WHERE
                        funparam.param_name = param.name
                    AND param.id = wpparam.param_id
                    AND wpparam.wp_resp_id = wpresp.wp_resp_id
                    AND funparam.fun_name = src_func.fun_name
                    AND wpresp.wp_defn_vers_id = p_source_wp_def_vers_id
                    AND wpresp.a2l_var_grp_id = vargroup.a2l_var_grp_id;

                IF
                    ( p_override_only_defaults = 'Y' )
                THEN
                    -- override only default mappings
                    --INSERT param mappings
                    --for all parameters in destination
                    INSERT INTO t_a2l_wp_param_mapping (
                        param_id,
                        wp_resp_id,
                        par_a2l_resp_id,
                        wp_name_cust,
                        wp_resp_inherit_flag,
                        wp_name_cust_inherit_flag)
                        SELECT DISTINCT
                            param.id,
                            destwpresp.wp_resp_id,
                            src_par_map_data(1).par_a2l_resp_id,
                            src_par_map_data(1).wp_name_cust,
                            src_par_map_data(1).wp_resp_inherit_flag,
                            src_par_map_data(1).wp_name_cust_inherit_flag
                        FROM
                            t_a2l_work_packages srcwrkpckg,
                            t_a2l_wp_responsibility destwpresp,
                            t_a2l_work_packages destwrkpckg,
                            t_a2l_variant_groups destvargrp,
                            t_parameter param,
                            gtt_funcparams temp
                        WHERE
                                param.name = temp.param_name
                            AND temp.fun_name = src_func.fun_name
                            AND temp.type = 'D'
                            AND srcwrkpckg.a2l_wp_id = src_par_map_data(1).wp_id
                            AND destwrkpckg.wp_name = srcwrkpckg.wp_name
                            AND destwpresp.a2l_wp_id = destwrkpckg.a2l_wp_id
                            AND destwpresp.wp_defn_vers_id = p_dest_wp_def_vers_id
                            AND destwpresp.a2l_var_grp_id = destvargrp.a2l_var_grp_id
                            AND destvargrp.group_name = vargroup.group_name
                            AND EXISTS (
                                SELECT
                                    oldparmap.param_id
                                FROM
                                    gtt_param_mapping oldparmap
                                WHERE
                                    oldparmap.wp_resp_id = p_dest_default_wp_resp_id
                                    AND oldparmap.param_id = param.id);
                ELSE
                    -- override all in variant group level
                    INSERT INTO t_a2l_wp_param_mapping (
                        param_id,
                        wp_resp_id,
                        par_a2l_resp_id,
                        wp_name_cust,
                        wp_resp_inherit_flag,
                        wp_name_cust_inherit_flag)
                        SELECT DISTINCT
                            param.id,
                            destwpresp.wp_resp_id,
                            src_par_map_data(1).par_a2l_resp_id,
                            src_par_map_data(1).wp_name_cust,
                            src_par_map_data(1).wp_resp_inherit_flag,
                            src_par_map_data(1).wp_name_cust_inherit_flag
                        FROM
                            t_a2l_work_packages srcwrkpckg,
                            t_a2l_variant_groups destvargrp,
                            t_a2l_wp_responsibility destwpresp,
                            t_a2l_work_packages destwrkpckg,
                            t_parameter param,
                            gtt_funcparams temp
                        WHERE
                                param.name = temp.param_name
                            AND temp.fun_name = src_func.fun_name
                            AND temp.type = 'D'
                            AND srcwrkpckg.a2l_wp_id = src_par_map_data(1).wp_id
                            AND destwpresp.a2l_wp_id = destwrkpckg.a2l_wp_id
                            AND destwrkpckg.wp_name = srcwrkpckg.wp_name
                            AND destwpresp.wp_defn_vers_id = p_dest_wp_def_vers_id
                            AND destvargrp.a2l_var_grp_id = destwpresp.a2l_var_grp_id
                            AND destvargrp.group_name = vargroup.group_name;   
                --end if for override only defaults condition
                END IF;
            --end loop for iterating over variant group
            END LOOP;
            fun_count := fun_count + 1;
        --end loop for iterating over function names
        END LOOP;

        pk_log.debug('Function count:' || fun_count);
        pk_log.info('Copy param mapping using function ended');
    END copy_param_map_from_func;
    
    --
    -- Main procedure to copy wp definition version from one a2l to other
    --

    PROCEDURE p_par2wp_copy (
        p_source_wp_def_vers_id    IN NUMBER,
        p_dest_wp_def_vers_id      IN NUMBER,
        p_override_only_defaults   VARCHAR2,
        p_derive_from_func         VARCHAR2,
        p_created_user_name        VARCHAR2) 
    AS

        -- Wp_Resp_Id of the default workpackage and responsibility of destination wp defining version
        v_dest_default_wp_resp_id   NUMBER;
        -- Wp_Resp_Id of the default workpackage and responsibility of source wp defining version       
        v_src_default_wp_resp_id    NUMBER;
        
        --- variable declaration only used for validation purpose
        v_src_pidc_vers_id          NUMBER;
        v_dest_pidc_vers_id         NUMBER;
    BEGIN
        pk_log.start_new_job('pk_par2wp_copy');
        v_copy_mapping_valid := false;

        -- Get PIDC Version of source and destination a2l
        v_src_pidc_vers_id := get_pidc_vers_id(p_source_wp_def_vers_id);
        v_dest_pidc_vers_id := get_pidc_vers_id(p_dest_wp_def_vers_id);
        pk_log.info('Source Wp Definition Version ID - '||p_source_wp_def_vers_id || ' Source PIDC Version ID - ' || v_src_pidc_vers_id);
        pk_log.info('Destination Wp Definition Version ID - '||p_dest_wp_def_vers_id ||' Destination PIDC Version ID - ' || v_dest_pidc_vers_id);

        -- Get default wp definition version for given wp definition version
        v_src_default_wp_resp_id := get_default_wp_resp_id(p_source_wp_def_vers_id,v_src_pidc_vers_id);
        v_dest_default_wp_resp_id := get_default_wp_resp_id(p_dest_wp_def_vers_id,v_dest_pidc_vers_id);
 
        -- insert variant group available only in source a2l wp definition version which are not available in destination a2l
        pk_log.debug('Insert variant grp name available only in src into gtt_object_names');
        INSERT INTO gtt_object_names (
            id,
            obj_name)
            SELECT
                a2l_var_grp_id,
                group_name
            FROM
                t_a2l_variant_groups
            WHERE
                    wp_defn_vers_id = p_source_wp_def_vers_id
                AND group_name NOT IN (
                    SELECT
                        group_name
                    FROM
                        t_a2l_variant_groups
                    WHERE
                        wp_defn_vers_id = p_dest_wp_def_vers_id);
        pk_log.debug('Inserted rows: ' || SQL%rowcount);       

        -- insert destination parameter id and wp resp id in temp table
        pk_log.debug('Insert existing destination parameter mapping into gtt_param_mapping');
        INSERT INTO gtt_param_mapping (
            param_id,
            wp_resp_id)
            SELECT DISTINCT
                param_id,
                wp_resp_id
            FROM
                t_a2l_wp_param_mapping
            WHERE
                wp_resp_id IN (
                    SELECT
                        wp_resp_id
                    FROM
                        t_a2l_wp_responsibility
                    WHERE
                        wp_defn_vers_id = p_dest_wp_def_vers_id);
        pk_log.debug('Inserted rows: ' || SQL%rowcount);  
        -- If source and destination has same wp definition version then throw an error
        compare_wp_defn_vers(p_source_wp_def_vers_id,p_dest_wp_def_vers_id,v_src_pidc_vers_id,v_dest_pidc_vers_id,p_override_only_defaults);

        --check whether src and dest has simliar wp definition version
        IF
            ( NOT v_copy_mapping_valid )
        THEN
            raise_application_error(-20005,'Work Package details are not copied as both source and destination A2L files have similar work package definition');
        ELSE       
            -- delete wp definition versions mapping in case of override all
            reset_wp_defn_vers(p_dest_wp_def_vers_id,p_override_only_defaults,v_dest_default_wp_resp_id);
            -- Copy variant grp
            copy_variant_grp(p_source_wp_def_vers_id,p_dest_wp_def_vers_id,p_override_only_defaults,p_created_user_name);
            -- Copy variant grp variant mapping
            copy_var_grp_var_mapping(p_source_wp_def_vers_id,p_dest_wp_def_vers_id,p_override_only_defaults,p_created_user_name);
            -- Copy wp from source pidc version to destination pidc version
            copy_a2l_wp(p_source_wp_def_vers_id,p_dest_wp_def_vers_id);
            -- Copy wp responsibility
            copy_a2l_wp_resp(p_source_wp_def_vers_id,p_dest_wp_def_vers_id,p_override_only_defaults,p_created_user_name,v_src_default_wp_resp_id
,v_dest_pidc_vers_id);
            -- Copy param mapping
            IF
                p_override_only_defaults = 'Y'
            THEN
                copy_param_mapping_default(p_source_wp_def_vers_id,p_dest_wp_def_vers_id,p_override_only_defaults,v_src_default_wp_resp_id,v_dest_default_wp_resp_id
,v_dest_pidc_vers_id);
            ELSE
                copy_param_mapping_all(p_source_wp_def_vers_id,p_dest_wp_def_vers_id,p_override_only_defaults,v_src_default_wp_resp_id,v_dest_default_wp_resp_id
,v_dest_pidc_vers_id);
            END IF;

            IF
                ( p_derive_from_func = 'Y' )
            THEN
                -- derive WP-param mappings from matching functions with unique WP-resp definitions
                copy_param_map_from_func(p_source_wp_def_vers_id,p_dest_wp_def_vers_id,p_override_only_defaults,v_src_default_wp_resp_id,v_dest_default_wp_resp_id
,v_dest_pidc_vers_id);
            END IF;

        END IF;

        pk_log.end_job;
    EXCEPTION
        WHEN OTHERS THEN
            pk_log.error('Error in pk_par2wp_copy.p_par2wp_copy',sqlcode,sqlerrm);
            pk_log.end_job;
            RAISE;
    END;

END pk_par2wp_copy;
/