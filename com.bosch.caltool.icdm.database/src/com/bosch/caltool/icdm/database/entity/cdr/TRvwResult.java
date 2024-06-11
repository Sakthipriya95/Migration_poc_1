package com.bosch.caltool.icdm.database.entity.cdr;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import org.eclipse.persistence.annotations.BatchFetch;
import org.eclipse.persistence.annotations.BatchFetchType;
import org.eclipse.persistence.annotations.OptimisticLocking;

import com.bosch.caltool.icdm.database.entity.a2l.TA2lWpDefnVersion;
import com.bosch.caltool.icdm.database.entity.apic.TPidcA2l;
import com.bosch.caltool.icdm.database.entity.apic.TWorkpackageDivision;
import com.bosch.caltool.icdm.database.entity.cdr.cdfx.TCDFxDeliveryParam;

/**
 * The persistent class for the T_RVW_RESULTS database table.
 */
@Entity
@OptimisticLocking(cascade = true)
@Table(name = "T_RVW_RESULTS")
// ICDM-2650
@NamedNativeQueries(value = {
    @NamedNativeQuery(name = TRvwResult.NNQ_GET_PRM_RVW_NO_VARIANT, query = "                                                                   " +
        "SELECT * FROM                                                                                                                          " +
        "     (                                                                                                                                 " +
        "     SELECT                                                                                                                            " +
        "           results.RESULT_ID                                                                                                           " +
        "         , results.CREATED_DATE as CREATED_DATE                                                                                        " +
        "         , decode(results.SOURCE_TYPE, 'RF', '<REVIEWED_FILE>', 'FUN', '<FUNCTION>', 'LAB', '<LAB>',  'MONICA', '<MONICA_REPORT>',      " +
        "             'GRP', results.GRP_WORK_PKG,                                                                                              " +
        "             ( select WP_NAME_E from T_WORKPACKAGE where WP_ID=(select WP_ID from T_WORKPACKAGE_DIVISION where WP_DIV_ID= results.WP_DIV_ID ) )       " +
        "          ) as RESULT_GROUP                                                                                                            " +
        "         , results.description                                                                                                         " +
        "         , rvw_variant.VARIANT_ID                                                                                                      " +
        "         , decode(results.org_result_id, NULL, 'N', 'Y') as DELTA_RVW                                                                  " +
        "         , pidcvers.vers_name                                                                                                          " +
        "         , PIDCA2L.PIDC_VERS_ID                                                                                                        " +
        "         , PIDCA2L.A2L_FILE_ID                                                                                                         " +
        "         , nvl(PIDCA2L.VCDM_A2L_NAME, pidca2lfile.filename) as filename                                                                " +
        "         , pidca2lfile.sdom_pver_variant                                                                                               " +
        "         , tempA2lParam.obj_name as parameter_name                                                                                     " +
        "         , row_number() over                                                                                                           " +
        "             (partition by rvw_param.param_id, pidca2l.project_id order by results.created_date desc) rev_number_for_label             " +
        "         , rvw_param.rvw_method                                                                                                        " +
        "         , rvw_param.result prm_rvw_result                                                                                             " +
        "         , rvw_param.match_ref_flag                                                                                                    " +
        "         , rvw_param.hint hint                                                                                                         " +
        "         , rvw_param.RVW_FUN_ID                                                                                                        " +
        "         , rvw_param.RVW_FILE_ID                                                                                                       " +
        "         ,(case ? when 1 then rvw_param.CHECKED_VALUE else null end) as Checked_value                                                  " +
        "         , NVL(rvw_param.review_score,0)                                                                                               " +
        "         , rvw_param.RVW_COMMENT                                                                                                       " +
        "         , rvw_param.SR_RESULT                                                                                                         " +
        "         , rvw_param.SR_ERROR_DETAILS                                                                                                  " +
        "         , rvw_param.SR_ACCEPTED_FLAG                                                                                                  " +
        "         , rvw_param.SR_ACCEPTED_USER                                                                                                  " +
        "         , rvw_param.SR_ACCEPTED_DATE                                                                                                  " +
        "         , null                                                                                                                        " +
        "         , results.CREATED_USER                                                                                                        " +
        "         , results.review_type                                                                                                         " +
        "         , results.lock_status                                                                                                         " +
        "         , tempA2lParam.id as parameter_id                                                                                             " +
        "         , rvw_param.ARC_RELEASED_FLAG                                                                                                 " +
        "         , rvw_param.rvw_param_ID                                                                                                      " +
        "     FROM                                                                                                                              " +
        "           t_rvw_results results                                                                                                       " +
        "         , t_rvw_variants rvw_variant                                                                                                  " +
        "         , t_rvw_parameters rvw_param                                                                                                  " +
        "         , t_pidc_a2l pidca2l                                                                                                          " +
        "         , t_pidc_version pidcvers                                                                                                     " +
        "         , GTT_OBJECT_NAMES tempA2lParam                                                                                               " +
        "         , ta2l_fileinfo pidca2lfile                                                                                                   " +
        "     WHERE                                                                                                                             " +
        "             results.result_id         =   rvw_param.result_id                                                                         " +
        "         AND results.result_id         =   rvw_variant.result_id(+)                                                                    " +
        "         AND results.PIDC_A2L_ID       =   pidca2l.PIDC_A2L_ID                                                                         " +
        "         AND pidca2l.PIDC_VERS_ID      =   pidcvers.PIDC_VERS_ID                                                                       " +
        "         AND pidca2l.A2L_FILE_ID       =   pidca2lfile.id                                                                              " +
        "         AND tempA2lParam.id           =   rvw_param.param_id                                                                          " +
        "         AND results.RVW_STATUS        !=  'O'                                                                                         " +
        "         AND results.review_type       IN  ('O', 'S')                                                                                  " +
        "         AND (rvw_param.CHECKED_VALUE IS NOT NULL OR NVL(rvw_param.review_score,0) = 8 )                                                      " + // Story_233268
        "         AND rvw_variant.VARIANT_ID    IS  NULL                                                                                        " +
        "         AND pidca2l.project_id = ?                                                                                                    " +
        "                 AND pidcvers.PIDC_VERS_ID  IN (                                                                                       " +
        "                       SELECT pidc_vers_id                                                                                             " +
        "                         FROM ( SELECT pidc_vers_id, -1 parent_pidc_vers_id from t_pidc_version where parent_pidc_vers_id IS NULL      " +
        "                                UNION ALL                                                                                              " +
        "                                SELECT pidc_vers_id, parent_pidc_vers_id from t_pidc_version where parent_pidc_vers_id IS NOT NULL     " +
        "                          )                                                                                                            " +
        "                          start with pidc_vers_id = ?                                                                                  " +
        "                          connect by NOCYCLE pidc_vers_id = prior parent_pidc_vers_id)                                                 " +
        "     )                                                                                                                                 " +
        "WHERE       rev_number_for_label       <=  ?                                                                                           " +
        "ORDER BY parameter_name, rev_number_for_label                                                                                          "),
    @NamedNativeQuery(name = TRvwResult.NNQ_GET_PRM_RVW_NO_VAR_WITHOUT_RVW_OF_OLDER_PIDCVERS, query = "                                                                   " +
        "SELECT * FROM                                                                                                                          " +
        "     (                                                                                                                                 " +
        "     SELECT                                                                                                                            " +
        "           results.RESULT_ID                                                                                                           " +
        "         , results.CREATED_DATE as CREATED_DATE                                                                                        " +
        "         , decode(results.SOURCE_TYPE, 'RF', '<REVIEWED_FILE>', 'FUN', '<FUNCTION>', 'LAB', '<LAB>',  'MONICA', '<MONICA_REPORT>',      " +
        "             'GRP', results.GRP_WORK_PKG,                                                                                              " +
        "             ( select WP_NAME_E from T_WORKPACKAGE where WP_ID=(select WP_ID from T_WORKPACKAGE_DIVISION where WP_DIV_ID= results.WP_DIV_ID ) )       " +
        "          ) as RESULT_GROUP                                                                                                            " +
        "         , results.description                                                                                                         " +
        "         , rvw_variant.VARIANT_ID                                                                                                      " +
        "         , decode(results.org_result_id, NULL, 'N', 'Y') as DELTA_RVW                                                                  " +
        "         , pidcvers.vers_name                                                                                                          " +
        "         , PIDCA2L.PIDC_VERS_ID                                                                                                        " +
        "         , PIDCA2L.A2L_FILE_ID                                                                                                         " +
        "         , nvl(PIDCA2L.VCDM_A2L_NAME, pidca2lfile.filename) as filename                                                                " +
        "         , pidca2lfile.sdom_pver_variant                                                                                               " +
        "         , tempA2lParam.obj_name as parameter_name                                                                                     " +
        "         , row_number() over                                                                                                           " +
        "             (partition by rvw_param.param_id, pidca2l.project_id order by results.created_date desc) rev_number_for_label             " +
        "         , rvw_param.rvw_method                                                                                                        " +
        "         , rvw_param.result prm_rvw_result                                                                                             " +
        "         , rvw_param.match_ref_flag                                                                                                    " +
        "         , rvw_param.hint hint                                                                                                         " +
        "         , rvw_param.RVW_FUN_ID                                                                                                        " +
        "         , rvw_param.RVW_FILE_ID                                                                                                       " +
        "         ,(case ? when 1 then rvw_param.CHECKED_VALUE else null end) as Checked_value                                                  " +
        "         , NVL(rvw_param.review_score,0)                                                                                               " +
        "         , rvw_param.RVW_COMMENT                                                                                                       " +
        "         , rvw_param.SR_RESULT                                                                                                         " +
        "         , rvw_param.SR_ERROR_DETAILS                                                                                                  " +
        "         , rvw_param.SR_ACCEPTED_FLAG                                                                                                  " +
        "         , rvw_param.SR_ACCEPTED_USER                                                                                                  " +
        "         , rvw_param.SR_ACCEPTED_DATE                                                                                                  " +
        "         , null                                                                                                                        " +
        "         , results.CREATED_USER                                                                                                        " +
        "         , results.review_type                                                                                                         " +
        "         , results.lock_status                                                                                                         " +
        "         , tempA2lParam.id as parameter_id                                                                                             " +
        "         , rvw_param.ARC_RELEASED_FLAG                                                                                                 " +
        "         , rvw_param.rvw_param_ID                                                                                                      " +
        "     FROM                                                                                                                              " +
        "           t_rvw_results results                                                                                                       " +
        "         , t_rvw_variants rvw_variant                                                                                                  " +
        "         , t_rvw_parameters rvw_param                                                                                                  " +
        "         , t_pidc_a2l pidca2l                                                                                                          " +
        "         , t_pidc_version pidcvers                                                                                                     " +
        "         , GTT_OBJECT_NAMES tempA2lParam                                                                                               " +
        "         , ta2l_fileinfo pidca2lfile                                                                                                   " +
        "     WHERE                                                                                                                             " +
        "             results.result_id         =   rvw_param.result_id                                                                         " +
        "         AND results.result_id         =   rvw_variant.result_id(+)                                                                    " +
        "         AND results.PIDC_A2L_ID       =   pidca2l.PIDC_A2L_ID                                                                         " +
        "         AND pidca2l.PIDC_VERS_ID      =   pidcvers.PIDC_VERS_ID                                                                       " +
        "         AND pidca2l.A2L_FILE_ID       =   pidca2lfile.id                                                                              " +
        "         AND tempA2lParam.id           =   rvw_param.param_id                                                                          " +
        "         AND results.RVW_STATUS        !=  'O'                                                                                         " +
        "         AND results.review_type       IN  ('O', 'S')                                                                                  " +
        "         AND (rvw_param.CHECKED_VALUE IS NOT NULL OR NVL(rvw_param.review_score,0) = 8 )                                                      " + // Story_233268
        "         AND rvw_variant.VARIANT_ID    IS  NULL                                                                                        " +
        "         AND pidca2l.project_id = ?                                                                                                    " +
        "         AND pidcvers.PIDC_VERS_ID  = ?                                                                                        " +
        "     )                                                                                                                                 " +
        "WHERE       rev_number_for_label       <=  ?                                                                                           " +
        "ORDER BY parameter_name, rev_number_for_label                                                                                          "),

    @NamedNativeQuery(name = TRvwResult.NNQ_GET_PRM_RVW_VAR_WITHOUT_RVW_OF_OLDER_PIDCVERS, query = "                                                                 " +
        "SELECT * FROM                                                                                                                          " +
        "     (                                                                                                                                 " +
        "     SELECT                                                                                                                            " +
        "           results.RESULT_ID                                                                                                           " +
        "         , results.CREATED_DATE as CREATED_DATE                                                                                        " +
        "         , decode(results.SOURCE_TYPE, 'RF', '<REVIEWED_FILE>', 'FUN', '<FUNCTION>', 'LAB', '<LAB>', 'MONICA', '<MONICA_REPORT>',      " +
        "             'GRP', results.GRP_WORK_PKG,                                                                                              " +
        "              ( select WP_NAME_E from T_WORKPACKAGE where WP_ID=(select WP_ID from T_WORKPACKAGE_DIVISION where WP_DIV_ID = results.WP_DIV_ID ) )                                                                " +
        "            ) as RESULT_GROUP                                                                                                           " +
        "         , results.description                                                                                                         " +
        "         , rvw_variant.VARIANT_ID                                                                                                      " +
        "         , decode(results.org_result_id, NULL, 'N', 'Y') as DELTA_RVW                                                                  " +
        "         , pidcvers.vers_name                                                                                                          " +
        "         , PIDCA2L.PIDC_VERS_ID                                                                                                        " +
        "         , PIDCA2L.A2L_FILE_ID                                                                                                         " +
        "         , nvl(PIDCA2L.VCDM_A2L_NAME, pidca2lfile.filename) as filename                                                                " +
        "         , pidca2lfile.sdom_pver_variant                                                                                               " +
        "         , tempA2lParam.obj_name as parameter_name                                                                                     " +
        "         , row_number() over                                                                                                           " +
        "             (partition by rvw_param.param_id, pidca2l.project_id order by results.created_date desc) rev_number_for_label             " +
        "         , rvw_param.rvw_method                                                                                                        " +
        "         , rvw_param.result prm_rvw_result                                                                                             " +
        "         , rvw_param.match_ref_flag                                                                                                    " +
        "         , rvw_param.hint hint                                                                                                         " +
        "         , rvw_param.RVW_FUN_ID                                                                                                        " +
        "         , rvw_param.RVW_FILE_ID                                                                                                       " +
        "         ,(case ? when 1 then rvw_param.CHECKED_VALUE else null end) as Checked_value                                                  " +
        "         , NVL(rvw_param.review_score,0)                                                                                                  " +
        "         , rvw_param.RVW_COMMENT                                                                                                       " +
        "         , rvw_param.SR_RESULT                                                                                                         " +
        "         , rvw_param.SR_ERROR_DETAILS                                                                                                  " +
        "         , rvw_param.SR_ACCEPTED_FLAG                                                                                                  " +
        "         , rvw_param.SR_ACCEPTED_USER                                                                                                  " +
        "         , rvw_param.SR_ACCEPTED_DATE                                                                                                  " +
        "         , attrval.TEXTVALUE_ENG                                                                                                       " +
        "         , results.CREATED_USER                                                                                                        " +
        "         , results.review_type                                                                                                         " +
        "         , results.lock_status                                                                                                         " +
        "         , tempA2lParam.id as parameter_id                                                                                             " +
        "         , rvw_param.ARC_RELEASED_FLAG                                                                                                 " +
        "         , rvw_param.rvw_param_ID                                                                                                      " +
        "     FROM                                                                                                                              " +
        "           t_rvw_results results                                                                                                       " +
        "         , t_rvw_variants rvw_variant                                                                                                  " +
        "         , t_rvw_parameters rvw_param                                                                                                  " +
        "         , t_pidc_a2l pidca2l                                                                                                          " +
        "         , t_pidc_version pidcvers                                                                                                     " +
        "         , GTT_OBJECT_NAMES tempA2lParam                                                                                               " +
        "         , ta2l_fileinfo pidca2lfile                                                                                                   " +
        "         , TABV_PROJECT_VARIANTS variant                                                                                               " +
        "         , TABV_ATTR_VALUES attrval                                                                                                    " +
        "     WHERE                                                                                                                             " +
        "             results.result_id         =   rvw_param.result_id                                                                         " +
        "         AND results.result_id         =   rvw_variant.result_id                                                                       " +
        "         AND results.PIDC_A2L_ID       =   pidca2l.PIDC_A2L_ID                                                                         " +
        "         AND pidca2l.PIDC_VERS_ID      =   pidcvers.PIDC_VERS_ID                                                                       " +
        "         AND pidca2l.A2L_FILE_ID       =   pidca2lfile.id                                                                              " +
        "         AND tempA2lParam.id           =   rvw_param.param_id                                                                          " +
        "         AND results.RVW_STATUS        !=  'O'                                                                                         " +
        "         AND results.review_type       IN  ('O', 'S')                                                                                  " +
        "         AND (rvw_param.CHECKED_VALUE IS NOT NULL OR NVL(rvw_param.review_score,0) = 8)                                                " + // Story_233268
        "         AND rvw_variant.VARIANT_ID    =   variant.VARIANT_ID                                                                          " +
        "         AND variant.value_id          =   attrval.value_id                                                                            " +
        "         AND attrval.value_id =                                                                                                        " +
        "         (                                                                                                                             " +
        "             SELECT variant.value_id                                                                                                   " +
        "             FROM TABV_PROJECT_VARIANTS variant                                                                                        " +
        "             WHERE variant.variant_id  =   ?                                                                                           " +
        "         )                                                                                                                             " +
        "         AND pidca2l.project_id = ?                                                                                                    " +
        "                 AND pidcvers.PIDC_VERS_ID  = ?                                                                                        " +
        "    )                                                                                                                                  " +
        "WHERE         rev_number_for_label     <=  ?                                                                                           " +
        "ORDER BY parameter_name, rev_number_for_label                                                                                          "),

    @NamedNativeQuery(name = TRvwResult.NNQ_GET_PRM_RVW_WITH_VARIANT, query = "                                                                 " +
        "SELECT * FROM                                                                                                                          " +
        "     (                                                                                                                                 " +
        "     SELECT                                                                                                                            " +
        "           results.RESULT_ID                                                                                                           " +
        "         , results.CREATED_DATE as CREATED_DATE                                                                                        " +
        "         , decode(results.SOURCE_TYPE, 'RF', '<REVIEWED_FILE>', 'FUN', '<FUNCTION>', 'LAB', '<LAB>', 'MONICA', '<MONICA_REPORT>',      " +
        "             'GRP', results.GRP_WORK_PKG,                                                                                              " +
        "              ( select WP_NAME_E from T_WORKPACKAGE where WP_ID=(select WP_ID from T_WORKPACKAGE_DIVISION where WP_DIV_ID = results.WP_DIV_ID ) )                                                                " +
        "            ) as RESULT_GROUP                                                                                                           " +
        "         , results.description                                                                                                         " +
        "         , rvw_variant.VARIANT_ID                                                                                                      " +
        "         , decode(results.org_result_id, NULL, 'N', 'Y') as DELTA_RVW                                                                  " +
        "         , pidcvers.vers_name                                                                                                          " +
        "         , PIDCA2L.PIDC_VERS_ID                                                                                                        " +
        "         , PIDCA2L.A2L_FILE_ID                                                                                                         " +
        "         , nvl(PIDCA2L.VCDM_A2L_NAME, pidca2lfile.filename) as filename                                                                " +
        "         , pidca2lfile.sdom_pver_variant                                                                                               " +
        "         , tempA2lParam.obj_name as parameter_name                                                                                     " +
        "         , row_number() over                                                                                                           " +
        "             (partition by rvw_param.param_id, pidca2l.project_id order by results.created_date desc) rev_number_for_label             " +
        "         , rvw_param.rvw_method                                                                                                        " +
        "         , rvw_param.result prm_rvw_result                                                                                             " +
        "         , rvw_param.match_ref_flag                                                                                                    " +
        "         , rvw_param.hint hint                                                                                                         " +
        "         , rvw_param.RVW_FUN_ID                                                                                                        " +
        "         , rvw_param.RVW_FILE_ID                                                                                                       " +
        "         ,(case ? when 1 then rvw_param.CHECKED_VALUE else null end) as Checked_value                                                  " +
        "         , NVL(rvw_param.review_score,0)                                                                                                  " +
        "         , rvw_param.RVW_COMMENT                                                                                                       " +
        "         , rvw_param.SR_RESULT                                                                                                         " +
        "         , rvw_param.SR_ERROR_DETAILS                                                                                                  " +
        "         , rvw_param.SR_ACCEPTED_FLAG                                                                                                  " +
        "         , rvw_param.SR_ACCEPTED_USER                                                                                                  " +
        "         , rvw_param.SR_ACCEPTED_DATE                                                                                                  " +
        "         , attrval.TEXTVALUE_ENG                                                                                                       " +
        "         , results.CREATED_USER                                                                                                        " +
        "         , results.review_type                                                                                                         " +
        "         , results.lock_status                                                                                                         " +
        "         , tempA2lParam.id as parameter_id                                                                                             " +
        "         , rvw_param.ARC_RELEASED_FLAG                                                                                                 " +
        "         , rvw_param.rvw_param_ID                                                                                                      " +
        "     FROM                                                                                                                              " +
        "           t_rvw_results results                                                                                                       " +
        "         , t_rvw_variants rvw_variant                                                                                                  " +
        "         , t_rvw_parameters rvw_param                                                                                                  " +
        "         , t_pidc_a2l pidca2l                                                                                                          " +
        "         , t_pidc_version pidcvers                                                                                                     " +
        "         , GTT_OBJECT_NAMES tempA2lParam                                                                                               " +
        "         , ta2l_fileinfo pidca2lfile                                                                                                   " +
        "         , TABV_PROJECT_VARIANTS variant                                                                                               " +
        "         , TABV_ATTR_VALUES attrval                                                                                                    " +
        "     WHERE                                                                                                                             " +
        "             results.result_id         =   rvw_param.result_id                                                                         " +
        "         AND results.result_id         =   rvw_variant.result_id                                                                       " +
        "         AND results.PIDC_A2L_ID       =   pidca2l.PIDC_A2L_ID                                                                         " +
        "         AND pidca2l.PIDC_VERS_ID      =   pidcvers.PIDC_VERS_ID                                                                       " +
        "         AND pidca2l.A2L_FILE_ID       =   pidca2lfile.id                                                                              " +
        "         AND tempA2lParam.id           =   rvw_param.param_id                                                                          " +
        "         AND results.RVW_STATUS        !=  'O'                                                                                         " +
        "         AND results.review_type       IN  ('O', 'S')                                                                                  " +
        "         AND (rvw_param.CHECKED_VALUE IS NOT NULL OR NVL(rvw_param.review_score,0) = 8)                                                      " + // Story_233268
        "         AND rvw_variant.VARIANT_ID    =   variant.VARIANT_ID                                                                          " +
        "         AND variant.value_id          =   attrval.value_id                                                                            " +
        "         AND variant.deleted_flag      !=  'Y'                                                                                         " +
        "         AND attrval.value_id =                                                                                                        " +
        "         (                                                                                                                             " +
        "             SELECT variant.value_id                                                                                                   " +
        "             FROM TABV_PROJECT_VARIANTS variant                                                                                        " +
        "             WHERE variant.variant_id  =   ?                                                                                           " +
        "         )                                                                                                                             " +
        "         AND pidca2l.project_id = ?                                                                                                    " +
        "                 AND pidcvers.PIDC_VERS_ID  IN (                                                                                       " +
        "                       SELECT pidc_vers_id                                                                                             " +
        "                         FROM ( select pidc_vers_id, -1 parent_pidc_vers_id from t_pidc_version where parent_pidc_vers_id IS NULL      " +
        "                                union all                                                                                              " +
        "                                select pidc_vers_id, parent_pidc_vers_id from t_pidc_version where parent_pidc_vers_id IS NOT NULL     " +
        "                          )                                                                                                            " +
        "                          start with pidc_vers_id = ?                                                                                  " +
        "                          connect by NOCYCLE pidc_vers_id = prior parent_pidc_vers_id)                                                 " +
        "    )                                                                                                                                  " +
        "WHERE         rev_number_for_label     <=  ?                                                                                           " +
        "ORDER BY parameter_name, rev_number_for_label                                                                                          ") })
@NamedQueries(value = {
    @NamedQuery(name = TRvwResult.GET_ALL, query = "SELECT t FROM TRvwResult t"),
    @NamedQuery(name = TRvwResult.GET_ALL_BY_PIDC_A2L_ID, query = "SELECT t FROM TRvwResult t where t.TPidcA2l.pidcA2lId = :pidcA2lId"),
    @NamedQuery(name = TRvwResult.GET_RES_BY_PIDC_VER_ID, query = "SELECT t FROM TRvwResult t where t.TPidcA2l.TPidcVersion.pidcVersId  = :pidcverid"),
    @NamedQuery(name = TRvwResult.GET_RES_BY_REVIEW_ID, query = "SELECT t FROM TRvwResult t where t.resultId  = :resultId"),
    @NamedQuery(name = TRvwResult.GET_COUNT_OF_RVW_BY_A2L_AND_VARID, query = "SELECT count(t.resultId) FROM TRvwResult t, TRvwVariant v where t.resultId  = v.TRvwResult.resultId and v.tabvProjectVariant.variantId = :pidcVarId and t.TPidcA2l.pidcA2lId = :pidcA2lId") })

public class TRvwResult implements Serializable {

  private static final long serialVersionUID = 1L;

  /** The Constant GET_ALL. */
  public static final String GET_ALL = "TRvwResult.findAll";

  /** The Constant GET_ALL. */
  public static final String GET_ALL_BY_PIDC_A2L_ID = "TRvwResult.getAllByPidcA2lId";

  public static final String GET_COUNT_OF_RVW_BY_A2L_AND_VARID = "TRvwResult.getCountOFRvwByPidcA2lIdAndVarId";
  /**
   * Named Native query fetch review details of parameters, when project version does not have variants. Parameters are
   * retrieved from temporary table.
   *
   * @param projectID PIDC ID
   * @param maxResults maximum review results to be considered
   * @return list of columns queried
   */
  public static final String NNQ_GET_PRM_RVW_NO_VARIANT = "TRvwResults.A2lResultParamsNoVariant";

  /**
   * Named Native query fetch review details of parameters, when project version does not have variants. The Older PIDC
   * Versions RVW are not considered
   */
  public static final String NNQ_GET_PRM_RVW_NO_VAR_WITHOUT_RVW_OF_OLDER_PIDCVERS =
      "TRvwResults.A2lResultParamsForLatestPidVersWithNoVariant";
  /**
   * Named Native query fetch review details of parameters, when project version does have variants. The Older PIDC
   * Versions RVW are not considered
   */
  public static final String NNQ_GET_PRM_RVW_VAR_WITHOUT_RVW_OF_OLDER_PIDCVERS =
      "TRvwResults.A2lResultParamsForLatestPidVersWithVariant";;

  /**
   * Named Native query fetch review details of parameters, when variant is available in the project version. Parameters
   * are retrieved from temporary table.
   *
   * @param variantID Project Variant ID
   * @param projectID PIDC ID
   * @param maxResults maximum review results to be considered
   * @return list of columns queried
   */
  public static final String NNQ_GET_PRM_RVW_WITH_VARIANT = "TRvwResults.A2LResultParamsWithVariant";

  /**
   * Named query to fetch review results, using ids stored in GttObjectName temporary table
   *
   * @return list of TRvwResult entities
   */
  public static final String NQ_GET_RESULT_WITH_TEMP = "TRvwResults.ResultWithTemp";
  /**
   * Named query to fetch Review results by Pidc version Id
   */
  public static final String GET_RES_BY_PIDC_VER_ID = "TRvwResults.ResultByPidcVerId";
  /**
   * Named query to fetch Review results by Review Result Id
   */
  public static final String GET_RES_BY_REVIEW_ID = "TRvwResults.ResultByReviewId";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CDR_SEQ_GENERATOR")
  @Column(name = "RESULT_ID")
  private long resultId;

  @Column(name = "CREATED_DATE", nullable = false)
  private Timestamp createdDate;

  @Column(name = "CREATED_USER", nullable = false, length = 20)
  private String createdUser;

  @Column(name = "GRP_WORK_PKG", length = 100)
  private String grpWorkPkg;

  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  @Column(name = "MODIFIED_USER", length = 20)
  private String modifiedUser;

  @Column(name = "REVIEW_TYPE", nullable = false, length = 1)
  private String reviewType;

  @Column(name = "RVW_STATUS", nullable = false, length = 1)
  private String rvwStatus;

  @Column(name = "DESCRIPTION", length = 200)
  private String description;

  @Column(name = "COMMENTS", length = 4000)
  private String comments;


  // bi-directional many-to-one association to TWorkpackageDivision
  @ManyToOne
  @JoinColumn(name = "WP_DIV_ID")
  private TWorkpackageDivision TWorkpackageDivision;

  @Column(name = "\"VERSION\"", nullable = false)
  @Version
  private Long version;
  // Icdm-729 DB Changes
  @Column(name = "SOURCE_TYPE", nullable = false, length = 10)
  private String sourceType;

  // bi-directional many-to-one association to TRvwFile
  @OneToMany(mappedBy = "TRvwResult", fetch = FetchType.LAZY)
  private Set<TRvwFile> TRvwFiles;

  // bi-directional many-to-one association to TRvwFunction
  @OneToMany(mappedBy = "TRvwResult", fetch = FetchType.LAZY)
  private Set<TRvwAttrValue> TRvwAttrValue;

  // bi-directional many-to-one association to TRvwParameter
  @OneToMany(mappedBy = "TRvwResult", fetch = FetchType.LAZY)
  private Set<TRvwParameter> TRvwParameters;

  // bi-directional many-to-one association to TRvwParticipant
  @OneToMany(mappedBy = "TRvwResult", fetch = FetchType.LAZY)
  private Set<TRvwParticipant> TRvwParticipants;

  // bi-directional many-to-one association to TRvwResult
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "ORG_RESULT_ID")
  @BatchFetch(value = BatchFetchType.JOIN)
  private TRvwResult tRvwResult;

  // bi-directional many-to-one association to TRvwResult
  @OneToMany(mappedBy = "tRvwResult", fetch = FetchType.LAZY)
  private Set<TRvwResult> TRvwResults;


  // bi-directional many-to-one association to TRvwFunction
  @OneToMany(mappedBy = "TRvwResult", fetch = FetchType.LAZY)
  private Set<TRvwFunction> TRvwFunctions;

  // bi-directional many-to-one association to TCDFxDelWpResp
  @OneToMany(mappedBy = "rvwResult", fetch = FetchType.LAZY)
  @BatchFetch(value = BatchFetchType.JOIN)
  private List<TCDFxDeliveryParam> tCDFxDelParamList;


  @Column(name = "LOCK_STATUS", length = 1)
  private String lockStatus;

  // bi-directional many-to-one association to TRuleSet
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "RSET_ID")
  private TRuleSet tRuleSet;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "PIDC_A2L_ID", nullable = false)
  private TPidcA2l TPidcA2l;

  // bi-directional many-to-one association to TRvwVariant
  @OneToMany(mappedBy = "TRvwResult", fetch = FetchType.LAZY)
  @BatchFetch(value = BatchFetchType.JOIN)
  private Set<TRvwVariant> TRvwVariants;

  // ICDM-2183
  @Column(name = "DELTA_REVIEW_TYPE")
  private String deltaReviewType;


  // bi-directional many-to-one association to TRvwResultsSecondary
  @OneToMany(mappedBy = "TRvwResult")
  private Set<TRvwResultsSecondary> TRvwResultsSecondaries;

  // bi-directional many-to-one association to TA2lWpDefinitionVersion
  @ManyToOne
  @JoinColumn(name = "WP_DEFN_VERS_ID")
  private TA2lWpDefnVersion tA2lWpDefnVersion;

  // bi-directional many-to-one association to TRvwWpResp
  @OneToMany(mappedBy = "tRvwResult")
  @BatchFetch(value = BatchFetchType.JOIN)
  private Set<TRvwWpResp> tRvwWpResps;

  @Column(name = "OBD_FLAG")
  private String obdFlag;

  @Column(name = "SIMP_QUES_REMARKS")
  private String simpQuesRemarks;

  @Column(name = "SIMP_QUES_RESP_FLAG")
  private String simpQuesRespFlag;

  /**
   * @return the isDeltaReview
   */
  public String getDeltaReviewType() {
    return this.deltaReviewType;
  }


  /**
   * @param deltaReviewType the isDeltaReview to set
   */
  public void setDeltaReviewType(final String deltaReviewType) {
    this.deltaReviewType = deltaReviewType;
  }

  public TRvwResult() {
    // NA
  }

  public long getResultId() {
    return this.resultId;
  }

  public void setResultId(final long resultId) {
    this.resultId = resultId;
  }

  public Timestamp getCreatedDate() {
    return this.createdDate;
  }

  public void setCreatedDate(final Timestamp createdDate) {
    this.createdDate = createdDate;
  }

  public String getCreatedUser() {
    return this.createdUser;
  }

  public void setCreatedUser(final String createdUser) {
    this.createdUser = createdUser;
  }


  public String getGrpWorkPkg() {
    return this.grpWorkPkg;
  }

  public void setGrpWorkPkg(final String grpWorkPkg) {
    this.grpWorkPkg = grpWorkPkg;
  }

  public Timestamp getModifiedDate() {
    return this.modifiedDate;
  }

  public void setModifiedDate(final Timestamp modifiedDate) {
    this.modifiedDate = modifiedDate;
  }

  public String getModifiedUser() {
    return this.modifiedUser;
  }

  public void setModifiedUser(final String modifiedUser) {
    this.modifiedUser = modifiedUser;
  }

  public String getReviewType() {
    return this.reviewType;
  }

  public void setReviewType(final String reviewType) {
    this.reviewType = reviewType;
  }

  public String getRvwStatus() {
    return this.rvwStatus;
  }

  public void setRvwStatus(final String rvwStatus) {
    this.rvwStatus = rvwStatus;
  }

  public Long getVersion() {
    return this.version;
  }

  public void setVersion(final Long version) {
    this.version = version;
  }

  public Set<TRvwFile> getTRvwFiles() {
    return this.TRvwFiles;
  }

  public void setTRvwFiles(final Set<TRvwFile> TRvwFiles) {
    this.TRvwFiles = TRvwFiles;
  }

  public Set<TRvwFunction> getTRvwFunctions() {
    return this.TRvwFunctions;
  }

  public void setTRvwFunctions(final Set<TRvwFunction> TRvwFunctions) {
    this.TRvwFunctions = TRvwFunctions;
  }

  public Set<TRvwParameter> getTRvwParameters() {
    return this.TRvwParameters;
  }

  public void setTRvwParameters(final Set<TRvwParameter> TRvwParameters) {
    this.TRvwParameters = TRvwParameters;
  }

  public Set<TRvwParticipant> getTRvwParticipants() {
    return this.TRvwParticipants;
  }

  public void setTRvwParticipants(final Set<TRvwParticipant> TRvwParticipants) {
    this.TRvwParticipants = TRvwParticipants;
  }

  public TRvwResult getTRvwResult() {
    return this.tRvwResult;
  }

  public void setTRvwResult(final TRvwResult TRvwResult) {
    this.tRvwResult = TRvwResult;
  }

  public Set<TRvwResult> getTRvwResults() {
    return this.TRvwResults;
  }

  public void setTRvwResults(final Set<TRvwResult> TRvwResults) {
    this.TRvwResults = TRvwResults;
  }

  /**
   * @return the comments
   */
  public String getComments() {
    return this.comments;
  }

  /**
   * @param comments the comments to set
   */
  public void setComments(final String comments) {
    this.comments = comments;
  }

  public String getDescription() {
    return this.description;
  }

  public void setDescription(final String description) {
    this.description = description;
  }

  // Icdm-729 DB Changes
  public String getSourceType() {
    return this.sourceType;
  }

  public void setSourceType(final String sourceType) {
    this.sourceType = sourceType;
  }

  public Set<TRvwAttrValue> getTRvwAttrValue() {
    return this.TRvwAttrValue;
  }

  public void setTRvwAttrValue(final Set<TRvwAttrValue> tRvwAttrValue) {
    this.TRvwAttrValue = tRvwAttrValue;
  }

  public TRuleSet gettRuleSet() {
    return this.tRuleSet;
  }


  public void settRuleSet(final TRuleSet tRuleSet) {
    this.tRuleSet = tRuleSet;
  }

  public TPidcA2l getTPidcA2l() {
    return this.TPidcA2l;
  }

  public void setTPidcA2l(final TPidcA2l TPidcA2l) {
    this.TPidcA2l = TPidcA2l;
  }

  /**
   * @return the lockStatus
   */
  public String getLockStatus() {
    return this.lockStatus;
  }


  /**
   * @param lockStatus the lockStatus to set
   */
  public void setLockStatus(final String lockStatus) {
    this.lockStatus = lockStatus;
  }

  /**
   * @return the tRvwVariants
   */
  public Set<TRvwVariant> getTRvwVariants() {
    return this.TRvwVariants;
  }


  /**
   * @param tRvwVariants the tRvwVariants to set
   */
  public void setTRvwVariants(final Set<TRvwVariant> tRvwVariants) {
    this.TRvwVariants = tRvwVariants;
  }

  public Set<TRvwResultsSecondary> getTRvwResultsSecondaries() {
    return this.TRvwResultsSecondaries;
  }

  public void setTRvwResultsSecondaries(final Set<TRvwResultsSecondary> TRvwResultsSecondaries) {
    this.TRvwResultsSecondaries = TRvwResultsSecondaries;
  }


  public TWorkpackageDivision getTWorkpackageDivision() {
    return this.TWorkpackageDivision;
  }

  public void setTWorkpackageDivision(final TWorkpackageDivision TWorkpackageDivision) {
    this.TWorkpackageDivision = TWorkpackageDivision;
  }


  /**
   * @return the tA2lWpDefnVersion
   */
  public TA2lWpDefnVersion gettA2lWpDefnVersion() {
    return this.tA2lWpDefnVersion;
  }


  /**
   * @param tA2lWpDefnVersion the tA2lWpDefnVersion to set
   */
  public void settA2lWpDefnVersion(final TA2lWpDefnVersion tA2lWpDefnVersion) {
    this.tA2lWpDefnVersion = tA2lWpDefnVersion;
  }


  /**
   * @return the tRvwWpResps
   */
  public Set<TRvwWpResp> getTRvwWpResps() {
    return this.tRvwWpResps;
  }

  /**
   * @param tRvwWpResps the trvwwpresp to set
   */
  public void setTRvwWpResps(final Set<TRvwWpResp> tRvwWpResps) {
    this.tRvwWpResps = tRvwWpResps;
  }


  /**
   * @return the tCDFxDelParamList
   */
  public List<TCDFxDeliveryParam> gettCDFxDelParamList() {
    return this.tCDFxDelParamList;
  }


  /**
   * @param tCDFxDelParamList the tCDFxDelParamList to set
   */
  public void settCDFxDelParamList(final List<TCDFxDeliveryParam> tCDFxDelParamList) {
    this.tCDFxDelParamList = tCDFxDelParamList;
  }

  /**
   * @param tcdFxDeliveryParam as input
   * @return TCDFxDeliveryParam
   */
  public TCDFxDeliveryParam addTCDFXDeliveryParam(final TCDFxDeliveryParam tcdFxDeliveryParam) {
    if (gettCDFxDelParamList() == null) {
      settCDFxDelParamList(new java.util.ArrayList<>());
    }
    gettCDFxDelParamList().add(tcdFxDeliveryParam);
    tcdFxDeliveryParam.setRvwResult(this);

    return tcdFxDeliveryParam;
  }


  /**
   * @return the obdFlag
   */
  public String getObdFlag() {
    return this.obdFlag;
  }


  /**
   * @param obdFlag the obdFlag to set
   */
  public void setObdFlag(final String obdFlag) {
    this.obdFlag = obdFlag;
  }


  /**
   * @return the simpQuesRemarks
   */
  public String getSimpQuesRemarks() {
    return this.simpQuesRemarks;
  }


  /**
   * @param simpQuesRemarks the simpQuesRemarks to set
   */
  public void setSimpQuesRemarks(final String simpQuesRemarks) {
    this.simpQuesRemarks = simpQuesRemarks;
  }


  /**
   * @return the simpQuesRespFlag
   */
  public String getSimpQuesRespFlag() {
    return this.simpQuesRespFlag;
  }


  /**
   * @param simpQuesRespFlag the simpQuesRespFlag to set
   */
  public void setSimpQuesRespFlag(final String simpQuesRespFlag) {
    this.simpQuesRespFlag = simpQuesRespFlag;
  }


}