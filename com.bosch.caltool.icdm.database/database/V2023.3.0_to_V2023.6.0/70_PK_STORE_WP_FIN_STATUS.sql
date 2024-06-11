------------------CREATE PACKAGE WITH PROCEDURE TO STORE WP FIN STATUS-------------------------------------------------------------------


CREATE OR REPLACE PACKAGE PK_STORE_WP_FIN_STATUS AS

    TYPE MAPPED_VARIANT_ID_LIST IS TABLE OF NUMBER;

    PROCEDURE PRC_INSERT_WP_FIN_STATUS(p_source_wp_def_vers_id IN NUMBER
                                     ,p_dest_wp_def_vers_id IN NUMBER
                                    );
    FUNCTION F_GET_A2L_MAPPED_VARIANT_LIST(p_pidcA2l_id IN NUMBER ) RETURN MAPPED_VARIANT_ID_LIST;
    
    PROCEDURE PRC_INS_RECORD_INTO_STATUS_TBL(p_dest_wp_resp_id IN NUMBER
                                            ,p_dest_wp_def_vers_id IN NUMBER
                                            ,p_dest_A2L_var_Grp_Id IN NUMBER
                                            ,p_src_WP_Resp_Id IN NUMBER
                                            ,pidcA2l_mapped_variant_id_list IN MAPPED_VARIANT_ID_LIST);
    
END PK_STORE_WP_FIN_STATUS;
/

-------------------------------------------CREATE PACKAGE BODY WITH PROCEDURE TO STORE WP FIN STATUS----------------------------------------------------------------------------------------------------------

CREATE OR REPLACE PACKAGE BODY PK_STORE_WP_FIN_STATUS AS

PROCEDURE PRC_INSERT_WP_FIN_STATUS ( 
      p_source_wp_def_vers_id IN NUMBER
     ,p_dest_wp_def_vers_id IN NUMBER
  )
  IS
  pidcA2l_mapped_variant_id_list MAPPED_VARIANT_ID_LIST := MAPPED_VARIANT_ID_LIST();
  CURSOR  CUR_SRC_WP_RESP_LIST IS SELECT  a2lWpResp.WP_RESP_ID,a2lWpResp.A2L_RESP_ID, a2lWpResp.A2L_WP_ID,varGrp.GROUP_NAME
                                  FROM t_a2l_wp_responsibility a2lWpResp 
                                  LEFT JOIN T_A2L_VARIANT_GROUPS varGrp ON (a2lWpResp.A2L_VAR_GRP_ID = varGrp.A2L_VAR_GRP_ID)
                                  WHERE a2lWpResp.wp_defn_vers_id = p_source_wp_def_vers_id;                                    
                                    
  CURSOR  CUR_DEST_WP_RESP_LIST IS SELECT  a2lWpResp.WP_RESP_ID,a2lWpResp.A2L_RESP_ID, a2lWpResp.A2L_WP_ID,varGrp.A2L_VAR_GRP_ID,varGrp.GROUP_NAME
                                  FROM t_a2l_wp_responsibility a2lWpResp 
                                  LEFT JOIN T_A2L_VARIANT_GROUPS varGrp ON (a2lWpResp.A2L_VAR_GRP_ID = varGrp.A2L_VAR_GRP_ID)
                                  WHERE a2lWpResp.wp_defn_vers_id = p_dest_wp_def_vers_id;

  
  v_pidc_a2l_id NUMBER;
  src_Loop_count NUMBER := 0; 
  src_wp_resp_list_count NUMBER := 0;
  
  dest_WP_Resp_Id T_A2L_WP_RESPONSIBILITY.WP_RESP_ID%TYPE;
  dest_A2L_WP_Id T_A2L_WP_RESPONSIBILITY.A2L_WP_ID%TYPE;
  dest_A2L_Resp_Id T_A2L_WP_RESPONSIBILITY.A2L_RESP_ID%TYPE;
  dest_A2L_var_Grp_Id T_A2L_WP_RESPONSIBILITY.A2L_VAR_GRP_ID%TYPE;
  dest_A2L_var_Grp_Name T_A2L_VARIANT_GROUPS.GROUP_NAME%TYPE := NULL;
  
  src_WP_Resp_Id T_A2L_WP_RESPONSIBILITY.WP_RESP_ID%TYPE;
  src_A2L_WP_Id T_A2L_WP_RESPONSIBILITY.A2L_WP_ID%TYPE;
  src_A2L_Resp_Id T_A2L_WP_RESPONSIBILITY.A2L_VAR_GRP_ID%TYPE;
  src_A2L_var_Grp_Id T_A2L_WP_RESPONSIBILITY.A2L_RESP_ID%TYPE;
  src_A2L_var_Grp_Name T_A2L_VARIANT_GROUPS.GROUP_NAME%TYPE := NULL;
                                    
  BEGIN 
    pk_log.start_new_job('PK_STORE_WP_FIN_STATUS');

    select pidc_a2l_id INTO v_pidc_a2l_id from T_A2L_WP_DEFN_VERSIONS WHERE WP_DEFN_VERS_ID = p_dest_wp_def_vers_id;
    PK_LOG.INFO('PIDC A2L ID:  ' || v_pidc_a2l_id);
    
    /* Get Mapped Variant Id list using Function F_GET_A2L_MAPPED_VARIANT_LIST*/
    pidcA2l_mapped_variant_id_list := F_GET_A2L_MAPPED_VARIANT_LIST(v_pidc_a2l_id);
    PK_LOG.INFO('MAPPED VARIANT COUNT:  ' || pidcA2l_mapped_variant_id_list.COUNT);
    IF p_source_wp_def_vers_id IS NULL THEN 
         /* Loop through WP_RESP_ID IN New(Dest) WP DEFN VERSION*/
         OPEN CUR_DEST_WP_RESP_LIST;
         LOOP
              FETCH CUR_DEST_WP_RESP_LIST INTO dest_WP_Resp_Id,dest_A2L_Resp_Id,dest_A2L_WP_Id,dest_A2L_var_Grp_Id,dest_A2L_var_Grp_Name;
              EXIT WHEN CUR_DEST_WP_RESP_LIST%NOTFOUND;
              PK_LOG.INFO('   DEST_WP_RESP_ID:  ' || dest_WP_Resp_Id);
              PRC_INS_RECORD_INTO_STATUS_TBL(dest_WP_Resp_Id,p_dest_wp_def_vers_id,dest_A2L_var_Grp_Id,null,pidcA2l_mapped_variant_id_list);
          END LOOP;
          CLOSE CUR_DEST_WP_RESP_LIST;
    ELSE
       /* Loop through WP_RESP_ID IN New(Dest) WP DEFN VERSION*/
       OPEN CUR_DEST_WP_RESP_LIST;
       LOOP
           FETCH CUR_DEST_WP_RESP_LIST INTO dest_WP_Resp_Id,dest_A2L_Resp_Id,dest_A2L_WP_Id,dest_A2L_var_Grp_Id,dest_A2L_var_Grp_Name;
           EXIT WHEN CUR_DEST_WP_RESP_LIST%NOTFOUND;
           src_Loop_count := 0;
           PK_LOG.INFO('   DEST_WP_RESP_ID:  ' || dest_WP_Resp_Id);
             /*Loop through WP_RESP_ID IN SRC WP DEFN VERSION*/
             OPEN CUR_SRC_WP_RESP_LIST;
             LOOP
                  FETCH CUR_SRC_WP_RESP_LIST INTO src_WP_Resp_Id,src_A2L_Resp_Id,src_A2L_WP_Id,src_A2L_var_Grp_Name;
                  EXIT WHEN CUR_SRC_WP_RESP_LIST%NOTFOUND;
                  src_wp_resp_list_count := CUR_SRC_WP_RESP_LIST%ROWCOUNT;
                  PK_LOG.INFO('src_wp_resp_list_count:  ' || src_wp_resp_list_count);
                  PK_LOG.INFO('     src_WP_Resp_Id:  ' || src_WP_Resp_Id);
                  PK_LOG.INFO('      src_Loop_count:  ' ||  src_Loop_count);
                  src_Loop_count := src_Loop_count + 1;
                  IF src_A2L_Resp_Id = dest_A2L_Resp_Id AND src_A2L_WP_Id = dest_A2L_WP_Id AND ((src_A2L_var_Grp_Name IS NULL AND dest_A2L_var_Grp_Name IS NULL) OR src_A2L_var_Grp_Name = dest_A2L_var_Grp_Name) THEN
                        PK_LOG.INFO('       Matching src_WP_Resp_Id:  ' || src_WP_Resp_Id);
   
                                  /*Call procedure to insert record into status table with param DEST_WP_RESP_ID,p_dest_wp_def_vers_id,dest_A2L_var_Grp_Id,src_WP_Resp_Id*/
                                   PRC_INS_RECORD_INTO_STATUS_TBL(dest_WP_Resp_Id,p_dest_wp_def_vers_id,dest_A2L_var_Grp_Id,src_WP_Resp_Id,pidcA2l_mapped_variant_id_list);
                        /*AFTER successfully takeover the status of matching src a2lWpResp, exit from src_WP_Resp_Id loop*/
                        EXIT;
                  ELSIF  src_Loop_count = src_wp_resp_list_count THEN
                                /*After looping through all the src wp resp id and found that no source, then come inside this condition*/
                                /* If there is no Src, then insert WPResp with default status as 'N'*/
                                /*Call procedure to insert record into status table with param DEST_WP_RESP_ID,p_dest_wp_def_vers_id,dest_A2L_var_Grp_Id,src_WP_Resp_Id*/
                                PRC_INS_RECORD_INTO_STATUS_TBL(dest_WP_Resp_Id,p_dest_wp_def_vers_id,dest_A2L_var_Grp_Id,src_WP_Resp_Id,pidcA2l_mapped_variant_id_list);
                        /*exit from src_WP_Resp_Id loop*/
                        EXIT;
                END IF;
            END LOOP;
            CLOSE CUR_SRC_WP_RESP_LIST;
      END LOOP;
      CLOSE CUR_DEST_WP_RESP_LIST;
    END IF;
    pk_log.end_job();
    EXCEPTION
          WHEN OTHERS THEN
             pk_log.error('Error while inserting WP Finished status- PK_STORE_WP_FIN_STATUS', 'iCDM', 'iCDM_PIDCA2L',sqlcode,sqlerrm); 
             pk_log.error('Error occurred at ' || DBMS_UTILITY.FORMAT_ERROR_BACKTRACE);
             rollback;
             pk_log.end_job();
  END PRC_INSERT_WP_FIN_STATUS;
  
  
FUNCTION F_GET_A2L_MAPPED_VARIANT_LIST(p_pidcA2l_id IN NUMBER)
RETURN MAPPED_VARIANT_ID_LIST 
IS

is_At_Child_Lvl tabv_PROJECT_Attr.IS_VARIANT%TYPE;
mappedVarIdList MAPPED_VARIANT_ID_LIST := MAPPED_VARIANT_ID_LIST();-- create an empty table
a2l_pidc_Vers_Id T_pidc_a2l.PIDC_VERS_ID%TYPE;
pidc_A2L_SDOM_PVER_Name T_pidc_a2l.SDOM_PVER_NAME%TYPE;
                                 
TYPE PIDC_VERS_VAR_ID_LIST IS TABLE OF Tabv_project_variants.VARIANT_ID%TYPE;
pidcVersVarIdList PIDC_VERS_VAR_ID_LIST;

varId Tabv_project_variants.VARIANT_ID%TYPE;
proj_SDOM_PVER_Name TABV_ATTR_VALUES.TEXTVALUE_ENG%TYPE;
var_SDOM_PVER_Name TABV_ATTR_VALUES.TEXTVALUE_ENG%TYPE;

BEGIN
     DBMS_OUTPUT.PUT_LINE('INSIDE FUNC:  ');
     select prjattr.is_variant, 
            attrval.TEXTVALUE_ENG,
            pidcA2l.PIDC_VERS_ID,
            pidcA2l.SDOM_PVER_NAME 
     INTO 
            is_At_Child_Lvl,
            proj_SDOM_PVER_Name,
            a2l_pidc_Vers_Id,
            pidc_A2L_SDOM_PVER_Name
     from tabv_PROJECT_Attr prjattr
     LEFT JOIN TABV_ATTR_VALUES attrval ON (prjattr.VALUE_ID = attrval.VALUE_ID) 
     LEFT JOIN T_pidc_a2l pidcA2l ON(pidcA2l.PIDC_VERS_ID = prjattr.PIDC_VERS_ID)
     where prjattr.attr_id = '2459' and pidcA2l.pidc_a2l_id = p_pidcA2l_id ;

     IF is_At_Child_Lvl = 'N' AND pidc_A2L_SDOM_PVER_Name = proj_SDOM_PVER_Name THEN
             PK_LOG.INFO('IS_NOT_AT_CHILD_LEVEL ');
             PK_LOG.INFO(' PIDC_VERS_ID '||a2l_pidc_Vers_Id);
             SELECT VARIANT_ID BULK COLLECT INTO mappedVarIdList FROM Tabv_project_variants WHERE pidc_vers_id = a2l_pidc_Vers_Id;
     ELSIF is_At_Child_Lvl = 'Y' THEN 
            SELECT 
                        prjvar.VARIANT_ID
            BULK COLLECT INTO
                        mappedVarIdList
            from        Tabv_project_variants prjvar
                        INNER JOIN tabv_Variants_Attr varAttr ON (prjvar.VARIANT_ID = varAttr.VARIANT_ID)
                        INNER JOIN  TABV_ATTR_VALUES attrVal  ON (varAttr.value_id = attrVal.value_id)
            where   
                        varAttr.attr_id = '2459'
                        and attrVal.TEXTVALUE_ENG = pidc_A2L_SDOM_PVER_Name
                        and prjvar.pidc_vers_id = a2l_pidc_Vers_Id;
     END IF;
RETURN mappedVarIdList;

EXCEPTION
  WHEN NO_DATA_FOUND THEN
    DBMS_OUTPUT.PUT_LINE('No VARIANT');
    RETURN mappedVarIdList;

END F_GET_A2L_MAPPED_VARIANT_LIST;
  
  PROCEDURE PRC_INS_RECORD_INTO_STATUS_TBL(p_dest_wp_resp_id IN NUMBER
                                          ,p_dest_wp_def_vers_id IN NUMBER
                                          ,p_dest_A2L_var_Grp_Id IN NUMBER
                                          ,p_src_WP_Resp_Id IN NUMBER
                                          ,pidcA2l_mapped_variant_id_list IN MAPPED_VARIANT_ID_LIST)
  IS
  TYPE PAR_A2L_RESP_ID_LIST IS TABLE OF T_A2l_Wp_Param_Mapping.PAR_A2L_RESP_ID%TYPE;
  parA2lRespIdList PAR_A2L_RESP_ID_LIST := PAR_A2L_RESP_ID_LIST();-- create an empty table
  
  TYPE VAR_GRP_ID_LIST IS TABLE OF T_A2L_VARIANT_GROUPS.A2L_VAR_GRP_ID%TYPE;
  varGrpidlist VAR_GRP_ID_LIST := VAR_GRP_ID_LIST();
  
  dest_variant_id TABV_PROJECT_VARIANTS.VARIANT_ID%TYPE;
  dest_wp_resp_fin_status  T_A2L_WP_RESPONSIBILITY_STATUS.WP_RESP_FIN_STATUS%TYPE := 'N';
  dest_cust_a2l_resp_id T_A2l_Wp_Param_Mapping.PAR_A2L_RESP_ID%TYPE;
  
  mappedVarGrpId T_A2L_VARGRP_VARIANT_MAPPING.A2L_VAR_GRP_ID%TYPE := NULL;
  mappedVarGrpIdCount NUMBER(15);
  statusAvailCount NUMBER(15) := 0;
  
  BEGIN
                                  /* CHECKING If the p_dest_wp_resp_id is Cutomized resp or Not*/
                                  select DISTINCT PAR_A2L_RESP_ID BULK COLLECT INTO parA2lRespIdList from T_A2l_Wp_Param_Mapping where WP_RESP_ID = p_dest_wp_resp_id AND WP_RESP_INHERIT_FLAG = 'N';
                                  IF pidcA2l_mapped_variant_id_list.COUNT <> 0 THEN
                                             /*INSERT STATUS FOR ALL a2l mapped VARIANT*/
                                             FOR variant_id_index IN pidcA2l_mapped_variant_id_list.FIRST .. pidcA2l_mapped_variant_id_list.LAST LOOP
                                                                        dest_variant_id := pidcA2l_mapped_variant_id_list(variant_id_index);                                         
                                                                        PK_LOG.INFO('            DEST_VAR_ID:  ' || dest_variant_id);
                                                                         /*TO CHECK WHETHER the dest var has the WPresp to be inserted*/
                                                                         SELECT count(*) into mappedVarGrpIdCount
                                                                         FROM T_A2L_VARGRP_VARIANT_MAPPING varGrpMap 
                                                                                 INNER JOIN T_A2L_VARIANT_GROUPS varGrps ON(varGrpMap.A2L_VAR_GRP_ID = varGrps.A2L_VAR_GRP_ID)
                                                                         WHERE varGrpMap.VARIANT_ID = dest_variant_id AND varGrps.wp_defn_vers_id = p_dest_wp_def_vers_id;
                                                                         
                                                                         IF mappedVarGrpIdCount > 0 THEN
                                                                              SELECT varGrpMap.A2L_VAR_GRP_ID into mappedVarGrpId
                                                                              FROM T_A2L_VARGRP_VARIANT_MAPPING varGrpMap 
                                                                                   INNER JOIN T_A2L_VARIANT_GROUPS varGrps ON(varGrpMap.A2L_VAR_GRP_ID = varGrps.A2L_VAR_GRP_ID)
                                                                              WHERE varGrpMap.VARIANT_ID = dest_variant_id AND varGrps.wp_defn_vers_id = p_dest_wp_def_vers_id;
                                                                         END IF;
                                                                         
                                                                         IF (mappedVarGrpId IS NULL AND p_dest_A2L_var_Grp_Id IS NULL) OR (mappedVarGrpId = p_dest_A2L_var_Grp_Id) THEN
                                                                                /*Fetching Status from src*/
                                                                                SELECT count(*) INTO statusAvailCount FROM t_a2l_wp_responsibility_status WHERE variant_id = dest_variant_id AND A2L_RESP_ID IS NULL and wp_resp_id = p_src_WP_Resp_Id;
                                                                                IF statusAvailCount > 0 THEN    
                                                                                    SELECT wp_resp_Fin_status INTO dest_wp_resp_fin_status FROM t_a2l_wp_responsibility_status WHERE variant_id = dest_variant_id AND A2L_RESP_ID IS NULL and wp_resp_id = p_src_WP_Resp_Id;
                                                                                END IF;
                                                                                IF parA2lRespIdList.COUNT <> 0 THEN
                                                                                          /*Insert the status for original wp resp id*/
                                                                                          PK_LOG.INFO('            RESP_ID: NULL ');
                                                                                          PK_LOG.INFO('            INSERT STATEMENT 1 ');
                                                                                          INSERT INTO T_A2L_WP_RESPONSIBILITY_STATUS (VARIANT_ID,WP_RESP_ID,A2L_RESP_ID,WP_RESP_FIN_STATUS)
                                                                                              VALUES(dest_variant_id,p_dest_wp_resp_id,NULL,NVL(dest_wp_resp_fin_status,'N'));
                                                                                           FOR parA2lRespIdIndex IN 1..parA2lRespIdList.COUNT LOOP
                                                                                                   dest_cust_a2l_resp_id := parA2lRespIdList(parA2lRespIdIndex);
                                                                                                   PK_LOG.INFO('            dest_cust_a2l_resp_id:  ' || dest_cust_a2l_resp_id);
                                                                                                  /*Insert the status for all customized responsiiity*/
                                                                                                  /*Fetching Status from src*/
                                                                                                  SELECT count(*) INTO statusAvailCount FROM t_a2l_wp_responsibility_status WHERE variant_id = dest_variant_id AND A2L_RESP_ID = dest_cust_a2l_resp_id and wp_resp_id = p_src_WP_Resp_Id;
                                                                                                  IF statusAvailCount > 0 THEN
                                                                                                      SELECT wp_resp_Fin_status INTO dest_wp_resp_fin_status FROM t_a2l_wp_responsibility_status WHERE variant_id = dest_variant_id AND A2L_RESP_ID = dest_cust_a2l_resp_id and wp_resp_id = p_src_WP_Resp_Id;
                                                                                                  END IF;
                                                                                                  PK_LOG.INFO('            INSERT STATEMENT 2 ');
                                                                                                  INSERT INTO T_A2L_WP_RESPONSIBILITY_STATUS (VARIANT_ID,WP_RESP_ID,A2L_RESP_ID,WP_RESP_FIN_STATUS)
                                                                                                       VALUES(dest_variant_id,p_dest_wp_resp_id,dest_cust_a2l_resp_id,NVL(dest_wp_resp_fin_status,'N'));
                                                                                           END LOOP;
                                                                                ELSE
                                                                                           /*if selected p_dest_wp_resp_id has no customized responsibility*/
                                                                                           /*Fetching Status from src*/
                                                                                           PK_LOG.INFO('            dest_cust_a2l_resp_id:  NULL' );
                                                                                           SELECT count(*) INTO statusAvailCount FROM t_a2l_wp_responsibility_status WHERE variant_id = dest_variant_id AND A2L_RESP_ID IS NULL and wp_resp_id = p_src_WP_Resp_Id;
                                                                                           IF statusAvailCount > 0 THEN
                                                                                               SELECT wp_resp_Fin_status INTO dest_wp_resp_fin_status FROM t_a2l_wp_responsibility_status WHERE variant_id = dest_variant_id AND A2L_RESP_ID IS NULL and wp_resp_id = p_src_WP_Resp_Id;
                                                                                           END IF;
                                                                                           /*Insert the status*/
                                                                                           PK_LOG.INFO('            INSERT STATEMENT 3 ');
                                                                                           INSERT INTO T_A2L_WP_RESPONSIBILITY_STATUS (VARIANT_ID,WP_RESP_ID,A2L_RESP_ID,WP_RESP_FIN_STATUS)
                                                                                                    VALUES(dest_variant_id,p_dest_wp_resp_id,NULL,NVL(dest_wp_resp_fin_status,'N'));
                                                                                END IF;
                                                                        END IF;
                                              END LOOP;
                                  ELSE
                                              PK_LOG.INFO('            DEST_VAR_ID: NULL');
                                                /*INSERT STATUS FOR NO_VARIANT AND NOT_CUST RESP*/
                                                                  PK_LOG.INFO('           dest_cust_a2l_resp_id:  NULL');
                                                                  SELECT count(*) INTO statusAvailCount FROM t_a2l_wp_responsibility_status WHERE variant_id IS NULL AND A2L_RESP_ID IS NULL and wp_resp_id = p_src_WP_Resp_Id;
                                                                  IF statusAvailCount > 0 THEN
                                                                       SELECT wp_resp_Fin_status INTO dest_wp_resp_fin_status FROM t_a2l_wp_responsibility_status WHERE variant_id IS NULL AND A2L_RESP_ID IS NULL and wp_resp_id = p_src_WP_Resp_Id;
                                                                  END IF;
                                                                  PK_LOG.INFO('            INSERT STATEMENT 4 ');
                                                                  INSERT INTO T_A2L_WP_RESPONSIBILITY_STATUS (WP_RESP_ID,WP_RESP_FIN_STATUS)
                                                                                VALUES(p_dest_wp_resp_id,NVL(dest_wp_resp_fin_status,'N'));

                                   END IF;
  END PRC_INS_RECORD_INTO_STATUS_TBL;
END PK_STORE_WP_FIN_STATUS;
------------------------------------------------------------------------------------------------------------------
--  ALM Task : 722927 - impl :Create Procedure to store 'WP Fin Status' of all WP-Resp under all variants in A2L while creating new Active Version
------------------------------------------------------------------------------------------------------------------
GRANT EXECUTE ON PK_STORE_WP_FIN_STATUS TO DGS_ICDM_JPA;

------------------------------------------------------------------------------------------------------------------
--  ALM Task : 722927 - impl :Create Procedure to store 'WP Fin Status' of all WP-Resp under all variants in A2L while creating new Active Version
------------------------------------------------------------------------------------------------------------------

------------------To be executed from DGS_ICDM_JPA SCHEMA--------------------------------
CREATE OR REPLACE SYNONYM DGS_ICDM_JPA.PK_STORE_WP_FIN_STATUS FOR DGS_ICDM.PK_STORE_WP_FIN_STATUS;

----------------------------configure 'PK_STORE_WP_FIN_STATUS' to Log errors and trace in T_log_messages table-----------------
INSERT INTO T_JOBS (JOB_NAME, SEND_MAIL_ON_ERROR, SEND_MAIL_ON_FINISH, SAVE_LOG_ON_ERROR_ONLY, LOG_DEBUG_MESSAGES, LOG_TRACE_MESSAGES, JOB_RUNS_REGULARLY, SHOW_ERR_AFTER_NUM_DAYS, EXPECTED_RUNTIME_IN_HOURS) VALUES ('PK_STORE_WP_FIN_STATUS', 'N', 'N', 'N', 'Y', 'Y', 'N', '1', '5')