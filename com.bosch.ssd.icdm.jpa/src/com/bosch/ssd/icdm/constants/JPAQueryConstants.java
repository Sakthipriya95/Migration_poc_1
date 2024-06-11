/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.ssd.icdm.constants;

/**
 * Constant Class that contains all the Native/Named Queries used with the entities
 *
 * @author SSN9COB
 */
public final class JPAQueryConstants {

  /*
   * ===================================================================================================================
   * Constant for the NamedQuery
   * ===================================================================================================================
   */
  /**
   * to create new labellist
   */
  public static final String CREATE_LABEL_LIST = "TempLabel.createLabelList";
  /**
   * to delete label from TempLabel
   */
  public static final String DELETE_LABEL = "TempLabel.deleteAll";
  /**
   * to delete rules from tempicdmfeaval
   */
  public static final String DELETE_FEAVAL = "TempIcdmFeaval.deleteAll";
  /**
   * to get dependency rule details for label
   */
  public static final String DEPENDENCY_RULE_DETAILS = "VLdb2Ssd2.getDependecyRuleDetailsForLabel";
  /**
   * To find default rule details for label
   */
  public static final String DEFAULT_RULE_DETAILS = "VLdb2Ssd2.getDefaultRuleDetailsForLabel";

  /**
   * to delete label from templabellist
   */
  public static final String DELETE_TEMP_TABLE = "TempLabelsList.deleteTempTables";
  /**
   * To insert into templabellist
   */
  public static final String INSERT_INTO_TEMP_TABLE = "TempLabelsList.insertInTemp";

  /**
   * To chek label is present or not
   */
  public static final String CHECK_LABEL = "VLdb2Pavast.checkLabel";

  /**
   * To find label id
   */
  public static final String GET_LAB_ID = "VLdb2Pavast.getLabId";
  /**
   * To find rule for labellist
   */
  public static final String GET_RULE_FOR_LABEL_LIST = "VLdb2Ssd2.getRuleForLabelList";
  /**
   * To find def rule for labellist
   */
  public static final String GET_DEF_RULE_FOR_LABEL_LIST = "VLdb2Ssd2.getDefRuleForLabelList";
  /**
   * To find rule for temp
   */
  public static final String GET_RULE_FROM_TEMP = "VLdb2Ssd2.getRuleFromTemp";
  /**
   * To find rule for temp varcode
   */
  public static final String GET_RULE_FROM_TEMP_VARCODE = "VLdb2Ssd2.getRuleFromTempVarCode";
  /**
   * To find rule for single label
   */
  public static final String GET_RULE_FOR_SINGLE_LABEL = "VLdb2Ssd2.getRuleForSingleLabel";
  /**
   * To find rule for single label
   */
  public static final String GET_RULE_FOR_LABOBJ_REVID = "VLdb2Ssd2.getRuleFromLabObjRevId";
  /**
   * To find rule for single node
   */
  public static final String GET_RULE_FOR_SINGLE_NODE = "VLdb2Ssd2.getRuleForSingleNode";
  /**
   * To find rule for def single label
   */
  public static final String GET_RULE_FOR_DEF_SINGLE_LABEL = "VLdb2Ssd2.getDefRuleForSingleLabel";
  /**
   * To find rule for version
   */
  public static final String GET_RULE_FOR_VERSION = "VLdb2Ssd2.getRuleForVersion";
  /**
   * To find rule for all version
   */
  public static final String GET_RULE_FOR_ALL_VERSION = "VLdb2Ssd2.getRuleForAllVersions";
  /**
   * To validate new label
   */
  public static final String VALIDATE_LABEL = "TempLabelsList.validateLabelsNew";
  /**
   * To insert labal into temp label
   */
  public static final String INSERT_LABELS = "TempLabelsList.insertLabels";
  /**
   * To get the error report
   */
  public static final String GET_ERROR_REPORT = "TempLabelsList.getErrorReport";
  /**
   * To get rule case history
   */
  public static final String GET_CASE_HISTORY = "VLdb2Comp.prcCaseHist";
  /**
   * To find customer node id
   */
  public static final String FIND_CUSTOMER_NODEID = "VLdb2Ssd2.findCustomerNodeId";
  /**
   * to delete data from Temp feaval
   */
  public static final String DELETE_TEMP_FEAVAL = "TSsd2TempFeaval.deleteAll";
  /**
   * to check if depenency is valid or not
   */
  public static final String CHECK_DEPENDENCY = "TSsd2TempFeaval.chkDependencyValid";
  /**
   * to find scope of rule
   */
  public static final String FIND_SCOPE = "VLdb2Ssd2.findScope";
  /**
   * to update record
   */
  public static final String UPDATE_RECORD = "VLdb2Ssd2.prcUpdateRec";
  /**
   * TO get rules for compli release
   */
  public static final String V_LDB2_SSD2_GET_RULES_FOR_COMPLI_RELEASE = "VLdb2Ssd2.getRulesForCompliRelease";
  /**
   * to get rules for Release
   */
  public static final String V_LDB2_SSD2_GET_RULES_FOR_RELEASE = "VLdb2Ssd2.getRulesForRelease";
  /**
   * get list of release by SW Version
   */
  public static final String V_LDB2_PROJECT_RELEASE_EDC17_GET_RELEASE_BY_SW_VERSION =
      "VLdb2ProjectReleaseEdc17.getReleaseBySwVersion";
  /**
   * Get feature value
   */
  public static final String V_LDB2_PROJECT_RELEASE_EDC17_FEATURE_VALUE = "VLdb2ProjectReleaseEdc17.FeatureValue";
  /**
   * get rule history for label
   */
  public static final String V_LDB2_SSD2_HISTORY_RULE_FOR_LABEL = "VLdb2Ssd2.historyOfRuleForLabel";
  /**
   * get rule history for CDRRULE
   */
  public static final String V_LDB2_SSD2_HISTORY_RULE_FOR_CDR_RULE = "VLdb2Ssd2.historyForCDRRule";

  /**
   * get rule history for CDRRULE
   */
  public static final String V_LDB2_SSD2_HISTORY_COMPLI_RULE_FOR_CDR_RULE = "VLdb2Ssd2.historyForCompliRule";
  /**
   * Procedure named query for SSDFileData
   */
  public static final String TEMP_SSD_FILE_SSD_FILE_DATA = "TempSsdFile.SSDFileData";
  /**
   * for CDF report
   */
  public static final String TEMP_SSD_FILE_CDF_REPORT_EXCEL = "TempSsdFile.CdfReportExcel";
  /**
   *
   */
  public static final String TEMP_SSD_FILE_GET_SSD_FILE = "TempSsdFile.getSSDFile";
  /**
   *
   */
  public static final String TEMP_SSD_FILE_DEL_WRITE_RELEASES_DATA = "TempSsdFile.Del_WriteReleases_Data";
  /**
   *
   */
  public static final String TEMP_DCMDATALIST_GET_DCM_FILE_DATA = "TempDcmdatalist.getDcmFileData";
  /**
   *
   */
  public static final String TEMP_CDFDATALIST_GET_CDF_FILE_DATA = "TempCdfdatalist.getCdfFileData";
  /**
   *
   */
  public static final String TEMP_RELEASES_ERROR_LBL_DTL_GET_TEMP_ERROR_LBL_DTLS =
      "TempReleasesErrorLblDtl.getTempErrorLblDtls";
  /**
   *
   */
  public static final String TEMP_SSD_FILE_SP_RELEASES_ERROR_TEMP = "TempSsdFile.SP_ReleasesErrorTemp";
  /**
   *
   */
  public static final String TEMP_LABELLIST_INTERFACE_GET_SSD_FILE = "TempLabellistInterface.getSSDFile";
  /**
   *
   */
  public static final String TEMP_LABELLIST_INTERFACE_GET_SSD_FILE_DEPENDENCY =
      "TempLabellistInterface.getSSDFileDependency";
  /**
   *
   */
  public static final String TEMP_LDB2_FEAVAL_TRUNC_TABLE = "TempLdb2Feaval.truncTable";
  /**
   *
   */
  public static final String V_LDB2_PROJECT_RELEASE_WRAPPER_CONFIG_RELEASE =
      "VLdb2ProjectRelease.wrapper_configRelease";
  /**
   *
   */
  public static final String V_LDB2_PROJECT_RELEASE_WRAPPER_CONFIG_RELEASE_COMPLI =
      "VLdb2ProjectRelease.wrapper_configReleaseCompli";

  /**
  *
  */
  public static final String V_LDB2_PROJECT_RELEASE_GET_NODES_FOR_NON_SDOM_SW_RELEASE =
      "VLdb2ProjectRelease.pr_check_nodes_for_release";
  
  /**
  *
  */
  public static final String TEMP_ICDM_NODE_TABLE_GET_NODE_LIST =
      "TempNonSDOMNodeList.findAllNodes";
  /**
  *
  */
  public static final String TEMP_ICDM_NODE_TABLE_DELETE_NODE_LIST =
      "TempNonSDOMNodeList.deleteEntries";
  /**
   *
   */
  public static final String V_LDB2_PROJECT_RELEASE_FIND_VALID_LIST = "VLdb2ProjectRelease.findValidList";
  /**
   *
   */
  public static final String V_LDB2_PROJECT_REVISION_GET_PRO_REV_ID = "VLdb2ProjectRevision.getProRevID";
  /**
   *
   */
  public static final String V_LDB2_PROG_PARAMETER_FIND_PARAMETER = "VLdb2ProgParameter.findParameter";
  /**
   *
   */
  public static final String V_SDOM_SSDNODE_VARIANTE_FIND_ALL = "VSdomSsdnodeVariante.findAll";
  /**
   *
   */
  public static final String V_LDB2_VILLA_SWVER_VILLA_SWVER = "VLdb2VillaSwver.VillaSwver";
  /**
   *
   */
  public static final String TEMP_RULE_ID_OEM_DELETE_TEMP_VALUES = "TempRuleIdOEM.deleteTempValues";
  /**
   *
   */
  public static final String V_LDB2_SSD2_GET_OEM_DESCRIPTION = "VLdb2Ssd2.getOEMDescription";
  /**
   *
   */
  public static final String V_LDB2_PROJECT_REVISION_VERS_ID = "VLdb2ProjectRevision.versId";
  /**
  *
  */
  public static final String DELETE_FROM_TEMP_SSD2_VARCODE = "delete from TEMP_SSD2_VARCODE";
  /**
  *
  */
  public static final String DELETE_FROM_TEMP_SSD2 = "delete from temp_ssd2";
  /**
  *
  */
  public static final String T_SSD2_TEMP_FEAVAL_POPULATE_TEMP_SSD = "TSsd2TempFeaval.populateTempSSD";
  /**
  *
  */
  public static final String V_LDB2_SSD2_CHECK_SSD_CHANGED = "VLdb2Ssd2.checkSSDChanged";
  /**
  *
  */
  public static final String SELECT_NEXTVAL_MAX_REV_ID = "SELECT K5ESK_LDB2.S_LDB2_REVID_PROJ.NEXTVAL FROM DUAL";

  /**
   * to check whether operator is valid for feature
   */
  public static final String CHECK_VALID_OPERATOR = "VLdb2Feature.checkValidOperator";
  /*
   * ===================================================================================================================
   * Constant for the actual query syntaxes
   * ===================================================================================================================
   */

  /**
   * The select statement for all the get rule method, for the resultset mapping [readRulesMapping] in VLdb2Ssd2 Class.
   * This is with the 'select' statement & 'From' clause. And does not contain 'where' clause. Order of Mapping as below
   * 1. VLDB2SSD2 2. VLDB2PAVAST 3. Label Name 4. DCM String 5. Feature Values 6. Maturity 7. Rule Owner 8. CoC
   */
  public static final String GET_RULE_SELECT_QUERY = "SELECT SSD2.*, PAVAST.*, " +
      "(select data from k5esk_ldb2.v_ldb2_dcm_clob_data where  lab_obj_id=SSD2.lab_obj_id and rev_id=SSD2.rev_id) AS dcmstring, " +
      "feavalTogether(SSD2.lab_obj_id,SSD2.rev_id) AS feaValues," +
      "(select maturity from v_ldb2_maturity_level where lab_obj_id=SSD2.lab_obj_id AND REV_ID=SSD2.rev_id ) maturity," +
      "(select NVL(contact.user_id, contact.department_id) from K5ESK_LDB2.T_LDB2_SSD2_CONTACT contact where contact.LAB_OBJ_ID = SSD2.lab_obj_id and contact.REV_ID = SSD2.rev_id and contact.TYP_ID = 1) ruleOwner," +
      "(select NVL(contact.user_id, contact.department_id) from K5ESK_LDB2.T_LDB2_SSD2_CONTACT contact where contact.LAB_OBJ_ID = SSD2.lab_obj_id and contact.REV_ID = SSD2.rev_id and contact.TYP_ID = 2) coc";


  /**
   * This is the where clause for the named query [VLdb2Ssd2.getRulesForRelease]
   */
  public static final String GET_RULE_FOR_RELEASE_WHERE_CLAUSE =
      ",PAVAST.LABEL LABEL1 FROM T_LDB2_SSD2 SSD2,V_LDB2_PAVAST PAVAST,T_LDB2_PROJECT_REL_LABELS PROJ WHERE SSD2.LAB_OBJ_ID = PROJ.LAB_OBJ_ID AND SSD2.REV_ID = PROJ.LAB_REV_ID AND SSD2.LAB_LAB_ID=PAVAST.LAB_ID AND PROJ.PRO_REL_ID=?";

  /**
   * This is the where clause for the named query [VLdb2Ssd2.getRulesForCompliRelease]
   */
  public static final String GET_RULE_FOR_COMPLI_RELEASE_WHERE_CLAUSE =
      ",PAVAST.LABEL LABEL1 FROM T_LDB2_SSD2 SSD2,V_LDB2_PAVAST PAVAST,T_LDB2_PROJECT_REL_LABELS PROJ WHERE UPPER(SSD2.CASES) in ('C-SSD','SSD2RV','Q-SSD') AND SSD2.LAB_OBJ_ID = PROJ.LAB_OBJ_ID AND SSD2.REV_ID = PROJ.LAB_REV_ID AND SSD2.LAB_LAB_ID=PAVAST.LAB_ID AND PROJ.PRO_REL_ID=?";

  /**
   * This is the where clause for the named query [VLdb2Ssd2.historyOfRuleForLabel]
   */
  public static final String GET_HISTORY_RULE_FOR_LABEL_WHERE_CLAUSE =
      ",PAVAST.LABEL LABEL1 FROM T_LDB2_SSD2 SSD2,V_LDB2_PAVAST PAVAST where SSD2.lab_lab_id = PAVAST.lab_id and SSD2.obj_id_1=? and SSD2.lab_lab_id in (select lab_Id from V_Ldb2_Pavast where upper_Label = ?) order by SSD2.rev_id desc";

  /**
   * This is the where clause for the named query [VLdb2Ssd2.historyForCDRRule]
   */
  public static final String GET_HISTORY_FOR_RULE_WHERE_CLAUSE =
      ",PAVAST.LABEL LABEL1 FROM T_LDB2_SSD2 SSD2,V_LDB2_PAVAST PAVAST where ssd2.lab_lab_id = pavast.lab_id and ssd2.obj_id_1=? and ssd2.lab_lab_id in (select lab_Id from V_Ldb2_Pavast where upper_Label = ?) and ssd2.lab_obj_id=? order by ssd2.rev_id desc";
  /**
   * This is the where clause for the named query [VLdb2Ssd2.historyForCompliRule]
   */
  public static final String GET_HISTORY_FOR_COMPLI_RULE_WHERE_CLAUSE =
      ",PAVAST.LABEL LABEL1 FROM T_LDB2_SSD2 SSD2,V_LDB2_PAVAST PAVAST where ssd2.lab_lab_id = pavast.lab_id and ssd2.lab_lab_id in (select lab_Id from V_Ldb2_Pavast where upper_Label = ?) and ssd2.lab_obj_id=? order by ssd2.rev_id desc";

  /**
   * This is the where clause for the named query ["VLdb2Ssd2.getRuleForLabelList"]
   */
  public static final String GET_RULE_FOR_LABEL_LIST_WHERE_CLAUSE =
      ",IFLABEL.LABEL LABEL1 FROM T_LDB2_SSD2 SSD2,V_LDB2_PAVAST PAVAST,K5ESK_LDB2.TEMP_LABELLIST_IF IFLABEL where SSD2.lab_lab_id=PAVAST.lab_id and  SSD2.obj_id_1= ? and SSD2.historie= 'N' and ssd2.scope in (1,3,5,7,8) and PAVAST.upper_label = IFLABEL.upper_label and IFLABEL.default_rule = 'N' ";
  /**
   * This is the where clause for the named query ["VLdb2Ssd2.getDefRuleForLabelList"] ALM-293404
   */
  public static final String GET_DEF_RULE_FOR_LABEL_LIST_WHERE_CLAUSE =
      ",IFLABEL.LABEL LABEL1 FROM T_LDB2_SSD2 SSD2,V_LDB2_PAVAST PAVAST, K5ESK_LDB2.TEMP_LABELLIST_IF IFLABEL where SSD2.lab_lab_id=PAVAST.lab_id and  SSD2.obj_id_1= ? and SSD2.historie='N' and SSD2.lab_lab_id in (select lab_Id from V_Ldb2_Pavast where upper_Label in " +
          " (SELECT DISTINCT(upper_label) from K5ESK_LDB2.TEMP_LABELLIST_IF where default_rule='Y'))" +
          "  and PAVAST.upper_label=IFLABEL.upper_label and SSD2.lab_obj_id not in (select distinct(lab_obj_id) from v_ldb2_ssd_comp) and IFLABEL.default_rule='Y'";

  /**
   * This is the where clause for the named query ["VLdb2Ssd2.getRuleFromTemp"]
   */
  public static final String GET_RULE_FROM_TEMP_WHERE_CLAUSE =
      " ,IFLABEL.LABEL LABEL1 FROM T_LDB2_SSD2 SSD2,V_LDB2_PAVAST PAVAST, K5ESK_LDB2.TEMP_LABELLIST_IF IFLABEL where SSD2.lab_lab_id=PAVAST.lab_id and " +
          " SSD2.obj_id_1= ?  and SSD2.historie='N' and SSD2.lab_lab_id in " +
          " (select lab_Id from V_Ldb2_Pavast where upper_Label in (SELECT DISTINCT(upper_label) from K5ESK_LDB2.TEMP_LABELLIST_IF  where default_rule='N' ) ) and SSD2.lab_obj_id in (select lab_obj_id from temp_ssd2) and PAVAST.upper_label=IFLABEL.upper_label";
  /**
   * This is the where clause for the named query ["VLdb2Ssd2.getRuleFromTempVarCode"]
   */


  public static final String GET_RULE_FROM_TEMP_VARCODE_WHERE_CLAUSE =
      ",IFLABEL.LABEL as label1 FROM T_LDB2_SSD2 SSD2,V_LDB2_PAVAST PAVAST, K5ESK_LDB2.TEMP_LABELLIST_IF IFLABEL  where SSD2.lab_lab_id=PAVAST.lab_id and  SSD2.obj_id_1= ? and SSD2.historie='N'  and SSD2.lab_obj_id in (select lab_obj_id from TEMP_SSD2_VARCODE) " +
          " and SSD2.lab_lab_id in (select lab_Id from V_Ldb2_Pavast where upper_Label in (SELECT DISTINCT(upper_label) from K5ESK_LDB2.TEMP_LABELLIST_IF where default_rule='Y') ) and PAVAST.upper_label=IFLABEL.upper_label ";


  /**
   * This is the where clause for the named query ["VLdb2Ssd2.getRuleForSingleLabel"]
   */
  public static final String GET_RULE_FOR_SINGLE_LABEL_WHERE_CLAUSE =
      ",PAVAST.LABEL LABEL1 FROM T_LDB2_SSD2 SSD2,V_LDB2_PAVAST PAVAST where SSD2.lab_lab_id=PAVAST.lab_id and SSD2.obj_id_1= ?  and SSD2.historie='N' and SSD2.lab_lab_id in  (select lab_Id from V_Ldb2_Pavast where upper_Label =?  )";

  /**
   * This is the where clause for the named query ["VLdb2Ssd2.getRuleForLabObjRevId"]
   */
  public static final String GET_RULE_FOR_LABOBJ_REVID_WHERE_CLAUSE =
      ",PAVAST.LABEL LABEL1 FROM T_LDB2_SSD2 SSD2,V_LDB2_PAVAST PAVAST where SSD2.lab_lab_id=PAVAST.lab_id and SSD2.lab_obj_id = ? and SSD2.rev_id = ? and SSD2.historie='N' ";

  /**
   * This is the where clause for the named query ["VLdb2Ssd2.getDefRuleForSingleLabel"]
   */
  public static final String GET_RULE_FOR_DEF_SINGLE_LABEL_WHERE_CLAUSE =
      ",PAVAST.LABEL LABEL1 FROM T_LDB2_SSD2 SSD2,V_LDB2_PAVAST PAVAST where SSD2.lab_lab_id=PAVAST.lab_id and SSD2.obj_id_1= ? and SSD2.historie='N' and SSD2.lab_lab_id in (select lab_Id from V_Ldb2_Pavast where upper_Label =? ) and SSD2.lab_obj_id not in (select lab_obj_id from v_ldb2_ssd_comp)";
  /**
   * This is the where clause for the named query ["VLdb2Ssd2.getRuleForVersion"]
   */
  public static final String GET_RULE_FOR_VERSION_WHERE_CLAUSE =
      ",PAVAST.LABEL LABEL1 FROM T_LDB2_SSD2 SSD2,V_LDB2_PAVAST PAVAST where SSD2.lab_lab_id=PAVAST.lab_id and SSD2.obj_id_1= ? and SSD2.historie='N' and SSD2.lab_lab_id in " +
          " (select lab_Id from V_Ldb2_Pavast where upper_Label in (SELECT DISTINCT(upper(f.defcharname)) from t_functionversions f  where  f.funcname = ? and  f.funcversion = ? ))";
  /**
   * This is the where clause for the named query ["VLdb2Ssd2.getRuleForAllVersions"]
   */
  public static final String GET_RULE_FOR_ALL_VERSION_WHERE_CLAUSE =
      ",PAVAST.LABEL LABEL1 FROM T_LDB2_SSD2 SSD2,V_LDB2_PAVAST PAVAST where SSD2.lab_lab_id=PAVAST.lab_id and SSD2.obj_id_1= ? and SSD2.historie='N' and SSD2.lab_lab_id in  " +
          " (select lab_Id from V_Ldb2_Pavast where upper_Label in (SELECT DISTINCT(upper(f.defcharname)) from t_functionversions f  where  f.funcname = ? ))";
  /**
   * This is the where clause for the named query ["VLdb2Ssd2.getRuleForAllVersions"]
   */
  public static final String GET_RULE_FOR_SINGLE_NODE_WHERE_CLAUSE =
      " ,PAVAST.LABEL LABEL1 FROM T_LDB2_SSD2 SSD2,V_LDB2_PAVAST PAVAST where SSD2.lab_lab_id= PAVAST.lab_id and SSD2.obj_id_1= ? and SSD2.historie='N'";

  private JPAQueryConstants() {
    // default private constructor
  }
}
