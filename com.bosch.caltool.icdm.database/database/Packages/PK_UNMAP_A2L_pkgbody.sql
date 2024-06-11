------------------------------------------------------------------------------------------------------------------
--  ALM TaskId : 594541: Server side changes - for deletion of A2L related DB entries
------------------------------------------------------------------------------------------------------------------

/* 
* Package to remove all PIDC-A2L references, before unmapping A2L from PIDC
*/
create or replace PACKAGE BODY PK_UNMAP_A2L
AS

    /* 
    * Procedure to delete A2L related entries and unmap A2L from PIDC Version
    */
    PROCEDURE P_UNMAP_A2L
        (
            p_pidc_a2l_id NUMBER 
        ) 
    AS

    BEGIN
        pk_log.start_new_job('pk_unmap_a2l.P_UNMAP_A2L');
        
        pk_log.info('Deletion of Review Result related entries started');
        
        --delete variant on which review is conducted or linked with    
        pk_log.debug('Deletion of entries in T_RVW_VARIANTS');
        DELETE FROM T_RVW_VARIANTS
        WHERE RESULT_ID IN
            (
                SELECT RESULT_ID
                FROM T_RVW_RESULTS
                WHERE PIDC_A2L_ID = p_pidc_a2l_id
            );

        --delete the participants of review
        pk_log.debug('Deletion of entries in T_RVW_PARTICIPANTS');
        DELETE FROM T_RVW_PARTICIPANTS
        WHERE RESULT_ID IN
            (
                SELECT RESULT_ID
                FROM T_RVW_RESULTS
                WHERE PIDC_A2L_ID = p_pidc_a2l_id
            );

        --delete the review parameters of secondary results  
        pk_log.debug('Deletion of entries in T_RVW_PARAMETERS_SECONDARY'); 
        DELETE FROM T_RVW_PARAMETERS_SECONDARY 
        WHERE RVW_PARAM_ID IN
            (
                SELECT RVW_PARAM_ID 
                FROM T_RVW_PARAMETERS
                WHERE RESULT_ID IN
                (
                    SELECT RESULT_ID
                    FROM T_RVW_RESULTS
                    WHERE PIDC_A2L_ID = p_pidc_a2l_id
                )
            );

        --delete review file entries created, when additional files are added to parameter from review param detais page   
        pk_log.debug('Deletion of entries in T_RVW_FILES created when additional files are added to parameter from review param detais page started');
        DELETE FROM T_RVW_FILES
        WHERE RVW_PARAM_ID IN
            (
                SELECT RVW_PARAM_ID
                FROM T_RVW_PARAMETERS
                WHERE RESULT_ID IN
                (
                    SELECT RESULT_ID
                    FROM T_RVW_RESULTS
                    WHERE PIDC_A2L_ID = p_pidc_a2l_id
                )
            );

        --Since parent reviews of project delta reviews will be deleted , 
        --the child project delta reviews will be converted to normal reviews.
        --Hence Delta review type is updated with null 
        pk_log.debug('Updating the DELTA_REVIEW_TYPE of child reviews of reviews which are to be deleted');
        UPDATE T_RVW_RESULTS
        SET DELTA_REVIEW_TYPE = null
        WHERE RESULT_ID IN
            (
                SELECT DISTINCT RESULT_ID 
                FROM T_RVW_PARAMETERS
                WHERE PARENT_PARAM_ID IN
                    (
                        SELECT RVW_PARAM_ID
                        FROM T_RVW_PARAMETERS
                        WHERE RESULT_ID IN
                            (
                                SELECT RESULT_ID
                                FROM T_RVW_RESULTS
                                WHERE PIDC_A2L_ID = p_pidc_a2l_id
                            )
                    )
            );      

        --scenario : When project delta review is conducted with different A2L , while parent review is conducted with the A2L being deleted
        --In  this case, update the parent review parameter id as null and reset the change flag for the review parameters 
        pk_log.debug('Update the parent review param id as null , in case of project delta review');    
        UPDATE T_RVW_PARAMETERS 
        SET PARENT_PARAM_ID = null, CHANGE_FLAG = 0
        WHERE PARENT_PARAM_ID IN
            (
                SELECT RVW_PARAM_ID 
                FROM T_RVW_PARAMETERS
                WHERE RESULT_ID IN
                    (
                        SELECT RESULT_ID
                        FROM T_RVW_RESULTS
                        WHERE PIDC_A2L_ID = p_pidc_a2l_id
                    )
            );    

        --reset the change flag of review parameter in case of delta review
        pk_log.debug('Update the change flag of review parameter to 0, in case of delta review'); 
        UPDATE T_RVW_PARAMETERS 
        SET CHANGE_FLAG = 0
        WHERE CHANGE_FLAG != 0
            and RESULT_ID IN
            (
                SELECT RESULT_ID 
                FROM T_RVW_RESULTS
                WHERE ORG_RESULT_ID IN
                    (
                        SELECT RESULT_ID
                        FROM T_RVW_RESULTS
                        WHERE PIDC_A2L_ID = p_pidc_a2l_id
                    )
            ); 

        --delete the reviewed parameters
        pk_log.debug('Deletion of entries in T_RVW_PARAMETERS');    
        DELETE FROM T_RVW_PARAMETERS
        WHERE RESULT_ID IN
            (
                SELECT RESULT_ID
                FROM T_RVW_RESULTS
                WHERE PIDC_A2L_ID = p_pidc_a2l_id
            );

        --delete the review files     
        pk_log.debug('Deletion of entries in T_RVW_FILES');
        DELETE FROM T_RVW_FILES
        WHERE RESULT_ID IN
            (
                SELECT RESULT_ID
                FROM T_RVW_RESULTS
                WHERE PIDC_A2L_ID = p_pidc_a2l_id
            );

        --delete the review functions  
        pk_log.debug('Deletion of entries in T_RVW_FUNCTIONS');     
        DELETE FROM T_RVW_FUNCTIONS
        WHERE RESULT_ID IN
            (
                SELECT RESULT_ID
                FROM T_RVW_RESULTS
                WHERE PIDC_A2L_ID = p_pidc_a2l_id
            );

        --delete the review attribute values
        pk_log.debug('Deletion of entries in T_RVW_ATTR_VALUES');
        DELETE FROM T_RVW_ATTR_VALUES
        WHERE RESULT_ID IN
            (
                SELECT RESULT_ID
                FROM T_RVW_RESULTS
                WHERE PIDC_A2L_ID = p_pidc_a2l_id
            );

        --delete wp - resp used in review
        pk_log.debug('Deletion of entries in T_RVW_WP_RESP'); 
        DELETE FROM T_RVW_WP_RESP
        WHERE RESULT_ID IN
            (
                SELECT RESULT_ID
                FROM T_RVW_RESULTS
                WHERE PIDC_A2L_ID = p_pidc_a2l_id
            ); 

        --delete the review results with secondary rulesets    
        pk_log.debug('Deletion of entries in T_RVW_RESULTS_SECONDARY');    
        DELETE FROM T_RVW_RESULTS_SECONDARY
        WHERE RESULT_ID IN
            (
                SELECT RESULT_ID
                FROM T_RVW_RESULTS
                WHERE PIDC_A2L_ID = p_pidc_a2l_id
            );

        --unlink the review result from its delta review (update the parent result id of delta reviews and delta review type to null)
        pk_log.debug('Unlink delta review from parent review');    
        UPDATE T_RVW_RESULTS
        SET ORG_RESULT_ID = null, DELTA_REVIEW_TYPE = null
        WHERE ORG_RESULT_ID IN
            (
                SELECT RESULT_ID
                FROM T_RVW_RESULTS
                WHERE PIDC_A2L_ID = p_pidc_a2l_id
            ); 

        --delete the review results     
        pk_log.debug('Deletion of entries in T_RVW_RESULTS');
        DELETE FROM T_RVW_RESULTS
        WHERE PIDC_A2L_ID = p_pidc_a2l_id ;

        --delete the a2l contents 
        pk_log.info('Deletion of A2L related entries started');

        --delete wp - parameter mappings
        pk_log.debug('Deletion of entries in T_A2L_WP_PARAM_MAPPING');
        DELETE FROM T_A2L_WP_PARAM_MAPPING
        WHERE WP_RESP_ID IN (
                SELECT WP_RESP_ID
                FROM T_A2L_WP_RESPONSIBILITY
                WHERE WP_DEFN_VERS_ID IN
                    (
                        SELECT WP_DEFN_VERS_ID
                        FROM T_A2L_WP_DEFN_VERSIONS
                        WHERE PIDC_A2L_ID = p_pidc_a2l_id
                    )
            );
    --Delete WP Resp Status entries        
    pk_log.debug('Deletion of entries in T_A2L_WP_RESPONSIBILITY_STATUS');
    DELETE FROM T_A2L_WP_RESPONSIBILITY_STATUS
    WHERE WP_RESP_ID IN (
        SELECT WP_RESP_ID
        FROM T_A2L_WP_RESPONSIBILITY
        WHERE WP_DEFN_VERS_ID IN (
            SELECT WP_DEFN_VERS_ID
            FROM T_A2L_WP_DEFN_VERSIONS
            WHERE PIDC_A2L_ID = p_pidc_a2l_id));
   
        pk_log.debug('Deletion of entries in T_A2L_WP_RESPONSIBILITY');        
        DELETE FROM T_A2L_WP_RESPONSIBILITY
        WHERE WP_DEFN_VERS_ID IN
            (
                SELECT WP_DEFN_VERS_ID
                FROM T_A2L_WP_DEFN_VERSIONS
                WHERE PIDC_A2L_ID = p_pidc_a2l_id
            );

        --delete wp resp created from resp out of Groups import 
        pk_log.debug('Deletion of entries in T_A2L_WP_RESP');     
        DELETE FROM T_A2L_WP_RESP
        WHERE A2L_RESP_ID IN
            (
                SELECT A2L_RESP_ID
                FROM T_A2L_RESP
                WHERE PIDC_A2L_ID = p_pidc_a2l_id
            );

        --delete resp created from Groups import   
        pk_log.debug('Deletion of entries in T_A2L_RESP'); 
        DELETE FROM T_A2L_RESP 
        WHERE PIDC_A2L_ID = p_pidc_a2l_id;

        --delete variant group and variants mappings
        pk_log.debug('Deletion of entries in T_A2L_VARGRP_VARIANT_MAPPING');
        DELETE FROM T_A2L_VARGRP_VARIANT_MAPPING
        WHERE A2L_VAR_GRP_ID IN
            (
                SELECT A2L_VAR_GRP_ID
                FROM T_A2L_VARIANT_GROUPS
                WHERE WP_DEFN_VERS_ID IN
                    (
                        SELECT WP_DEFN_VERS_ID
                        FROM T_A2L_WP_DEFN_VERSIONS
                        WHERE PIDC_A2L_ID = p_pidc_a2l_id
                    )
            );

        --delete variant groups        
        pk_log.debug('Deletion of entries in T_A2L_VARIANT_GROUPS');
        DELETE FROM T_A2L_VARIANT_GROUPS
        WHERE WP_DEFN_VERS_ID IN
            (
                SELECT WP_DEFN_VERS_ID
                FROM T_A2L_WP_DEFN_VERSIONS
                WHERE PIDC_A2L_ID = p_pidc_a2l_id
            );

        --delete a2l wp definition versions 
        pk_log.debug('Deletion of entries in T_A2L_WP_DEFN_VERSIONS'); 
        DELETE FROM T_A2L_WP_DEFN_VERSIONS
        WHERE PIDC_A2L_ID = p_pidc_a2l_id;    

        pk_log.info('Deletion of A2L related entries completed');
        
        pk_log.info('pk_unmap_a2l.P_UNMAP_A2L completed');
        
        pk_log.end_job;

    EXCEPTION
        WHEN OTHERS THEN
            pk_log.error('Error in pk_unmap_a2l.P_UNMAP_A2L', sqlcode, sqlerrm);
            pk_log.end_job;
            RAISE;
    

    END P_UNMAP_A2L;

END PK_UNMAP_A2L;

/
