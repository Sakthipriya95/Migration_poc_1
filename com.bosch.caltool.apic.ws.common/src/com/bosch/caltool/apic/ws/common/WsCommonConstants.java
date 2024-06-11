/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.common;

/**
 * @author bne4cob
 */
public final class WsCommonConstants {

  /**
   * HTTP Content Encoding key
   */
  public static final String CONTENT_ENCODING = "Content-Encoding";

  /**
   * Encoding with GZIP
   */
  public static final String ENCODING_GZIP = "gzip";

  /*
   * Rest Web service paths
   */

  /**
   * Web Context root of REST services
   */
  public static final String RWS_CONTEXT_ROOT = "rest";

  /**
   * Path for System Rest web services
   */
  public static final String RWS_CONTEXT_SYS = "system";

  /**
   * Path for CDR Rest web services
   */
  public static final String RWS_CONTEXT_CDR = "cdr";


  /**
   * Path for CDR Rest web services
   */
  public static final String RWS_CONTEXT_COMPHEX = "comparehex";

  /**
   * Path for SSD Rest web services
   */
  public static final String RWS_CONTEXT_SSD = "ssd";

  /**
   * Path for CDR Secondary Review Result Rest web services
   */
  public static final String RWS_RVWRESULTSSECONDARY = "rvwresultsecondary";


  /**
   * Path for Review comment history
   */
  public static final String RWS_RVWCMNTHISTORY = "rvwcmnthistory";

  /**
   * Path to get review comment history by user
   */
  public static final String RWS_GET_RVW_CMNT_HISTORY_BY_USER = "getrvwcmnthistorybyuser";

  /**
   * rule set related
   */
  public static final String RWS_RULE_SET = "ruleset";

  /**
   * Comment template
   */
  public static final String RWS_COMMENT_TEMPLATE = "commenttemplate";

  /**
   * Path for General Rest web services
   */
  public static final String RWS_CONTEXT_GEN = "general";
  /**
   * Path for OAuth Rest web services
   */
  public static final String RWS_CONTEXT_OAUTH = "oauth";

  /**
   * Path for General Rest web services
   */
  public static final String RWS_CONTEXT_UC = "uc";

  /**
   * Path for vCDM Rest web services
   */
  public static final String RWS_CONTEXT_VCDM = "vcdm";

  /**
   * Path for WP Archival Rest web services
   */
  public static final String RWS_WP_ARCHIVAL = "wparchival";

  /**
   * Path to Get WP Archivals by Ids Rest web services
   */
  public static final String RWS_WP_ARCHIVALS_BY_IDS = "wparchivalsbyids";

  /**
   * Path for WP Archival Baseline Rest web services
   */
  public static final String RWS_WP_ARCHIVAL_BASELINE = "wparchivalbaseline";

  /**
   * Path for WP Archival Baseline Filtered Rest web services
   */
  public static final String RWS_WP_ARCHIVAL_BASELINE_FILTERED = "wparchivalbaselinefiltered";

  /**
   * Path for WP Archival Baseline Filtered pidc Rest web services
   */
  public static final String RWS_WP_ARCHIVAL_BASELINE_FILTERED_PIDC = "wparchivalbaselinefilteredpidc";

  /**
   * Path for WP Archival file download Rest web services
   */
  public static final String RWS_WP_FILES_DOWNLOAD = "wpfilesdownload";

  /**
   * Query Param for Wp Archival ID
   */
  public static final String RWS_QP_WP_ARCHIVAL_ID = "wpArchivalId";

  /**
   * Path for Data Assessment Rest web services
   */
  public static final String RWS_DA_DATA_ASSESSMENT = "dataassessment";

  /**
   * Path for Data Assessment file download Rest web services
   */
  public static final String RWS_DA_DATA_ASSESSMENT_DOWNLOAD = "dataassessmentdownload";

  /**
   * Path for Data Assessment Share point Upload Rest web services
   */
  public static final String RWS_DATA_ASSESSMENT_SHARE_POINT_UPLOAD = "dataassessmentsharepointupload";

  /**
   * Path for Data Assessment Baseline creation
   */
  public static final String RWS_CREATE_BASELINE = "baselines";

  /**
   * Path for Data Assessment archival File status
   */
  public static final String RWS_CHECK_STATUS = "filestatus";

  /**
   * Path for the creation of data assessment baseline files
   */
  public static final String RWS_CREATE_BASELINE_FILES = "baselinefiles";

  /**
   * Data Assessment/Baseline ID
   */
  public static final String RWS_BASELINE_ID = "baselineid";

  /**
   * Path for data refresh service
   */
  public static final String RWS_DATA_REFRESH = "datarefresh";

  /**
   * Path for General Rest web services
   */
  public static final String RWS_USER_FAV = "userfavourites";

  /**
   * Path to fetch information by user's nt id
   */
  public static final String RWS_GET_BY_USER_NT_ID = "getbyuserntid";


  /**
   * Path to fetch information by user's nt id
   */
  public static final String RWS_GET_BY_USER_ID = "getbyuserid";

  /**
   * Rest web service path of CDR Report
   */
  public static final String RWS_CDR_REPORT = "cdrreport";

  /**
   * Rest web service path of CDR Report
   */
  public static final String RWS_RESP_REPORT = "respReport";


  /**
   * Rest web service path of Copy variant to result
   */
  public static final String RWS_COPY_VAR_TO_RESULT = "copyvartoresult";
  /**
   * Rest web service path of check result modifiable
   */
  public static final String RWS_CAN_MODIFY_RESULT = "canmodifyresult";
  /**
   * Rest web service path of review result delta review
   */
  public static final String RWS_RVW_RES_DELTA = "reviewresultdelta";

  /**
   * Rest web service path of Review Result
   */
  public static final String RWS_RVW_CAN_DELETE_VAL = "deletevalidation";

  /**
   * Rest web service path of Review Result
   */
  public static final String RWS_RVW_ASYN_DELETE = "asyndelete";

  /**
   * Rest web service path of Review Result
   */
  public static final String RWS_RVW_VAR_MAP = "reviewvarmap";

  /**
   * Rest web service path of Review Result
   */
  public static final String RWS_RVW_VAR_MODEL_SET = "reviewvarmodelset";

  /**
   * Rest web service path of Review variant
   *
   * @deprecated not used
   */
  @Deprecated
  public static final String RWS_RVW_VARIANT = "reviewvariant";

  /**
   * Rest web service path of Review variant
   *
   * @deprecated not used
   */
  @Deprecated
  public static final String RWS_RVW_VARIANT_SET = "reviewvarset";

  /**
   * Rest web service path of Review variant
   *
   * @deprecated not used
   */
  @Deprecated
  public static final String RWS_CDR_WRKPKG = "cdrwrkpkg";

  /**
   * Rest web service path of Review Result Functions
   */
  public static final String RWS_RVW_FUN = "reviewresultfunction";

  /**
   * Rest web service path of Review Result Functions
   */
  public static final String RWS_RVW_QNAIRE = "reviewresultquestionnaire";

  /**
   * Rest web service path for RvwQnaireRespVariant
   */
  public static final String RWS_RVWQNAIRERESPVARIANT = "rvwqnairerespvariant";

  /**
   * Rest web service path for RvwQnaireRespVersion
   */
  public static final String RWS_RVWQNAIRERESPVERSION = "rvwqnairerespversion";

  /**
   * Rest web service path for RvwQnaireRespCopyPaste service
   */
  public static final String RWS_RVW_QNAIRE_RESP_COPY = "rvwqnairerespcopy";
  /**
   * Url path for getting data needed for validation before calling copy/paste qnaire resp service
   */
  public static final String RWS_RETREIVE_DATA_FOR_COPY_VALIDATION = "retreivedataforcopyvalidation";
  /**
   * Query parameter name - Source PIDC Version ID
   */
  public static final String RWS_QP_SRC_PIDC_VERS_ID = "srcpidcversid";
  /**
   * Url path for creating copied qnaire resp in selected destination
   */
  public static final String RWS_COPY_QNAIRE_RESP_TO_DEST_WP = "copyqnaireresptodestwp";
  /**
   * Url path for updating the dest qnaire resp with copied qnaire resp
   */
  public static final String RWS_UPDATE_QNAIRE_RESP_IN_DEST_WP = "updateqnairerespindestwp";

  /**
   * Url path for updating the qnaire version with the active version
   */
  public static final String RWS_UPDATE_QNAIRE_VERSION = "updateqnaireversion";
  /**
   * Url path for RvwQnaireRespVersion
   */
  public static final String RVW_QNAIRE_RESP_ID = "qnairerespid";

  /**
   * Query parameter RvwQnaireRespVersion
   */
  public static final String RVW_QP_QNAIRE_RESP_ID = "qnairerespid";

  /**
   * Url path for RvwQnaireRespVersion
   */
  public static final String RWS_QNAIRE_RESP_VERS_BY_RESPID = "qniarerespverbyrespid";


  /**
   * Url path for RvwQnaireRespVersion
   */
  public static final String RWS_LATEST_RVW_QNAIRE_RESP_VERS = "latestrvwqnairerespversions";

  /**
   * Rest web service path of Questionaire Attibute Value Service
   */
  public static final String RWS_RVW_QNAIRE_ATTRIBUTE_VALUE = "getqnaireattrval";

  /**
   * Rest web service path of Review Result Participants
   */
  public static final String RWS_RVW_PARTICIPANT = "reviewresultparticipant";

  /**
   * url path - vCDM data set
   */
  public static final String RWS_VCDM_DATASET = "vcdmdataset";

  /**
   * URL Path - vCDM dataset wp stats
   */
  public static final String RWS_VCDM_DATASET_WP_STATS = "vcdmdatasetwpstats";

  /**
   * URL Path - attribute dependency
   */
  public static final String RWS_ATTR_DEPN = "attrdepn";

  /**
   * URL Path - value dependency
   */
  public static final String RWS_VALUE_DEPN = "valuedepn";

  /**
   * Query parameter time period
   */
  public static final String RWS_QP_TIME_PERIOD = "timeperiod";

  /**
   * Query parameter DST ID
   */
  public static final String RWS_QP_DSTID = "dstid";

  /**
   * path - get data by PIDC ID
   */
  public static final String RWS_DATA_BY_PIDCID = "databypidcid";

  /**
   * Rest web service path of Review Result File
   */
  public static final String RWS_RVW_FILE = "reviewresultfile";

  /**
   * Rest web service path of Review file - reviewed
   */
  public static final String RWS_FILE_REVIEWD_BY_RESULT_ID = "filereviewed";

  /**
   * Rest web service path of file attached to review
   */
  public static final String RWS_FILE_ATTACHED_BY_RESULT_ID = "fileattached";

  /**
   * Rest web service path of Review internal files
   */
  public static final String RWS_FILE_INTERNAL_BY_RESULT_ID = "fileinternal";

  /**
   * Rest web service path of Review input files
   */
  public static final String RWS_FILE_INPUT_BY_RESULT_ID = "fileinput";

  /**
   * Rest web service path of file attached to review parameter
   */
  public static final String RWS_FILE_ATTACHED_BY_RVW_PARAM_ID = "fileparamattached";

  /**
   * Rest web service path of Review creator
   */
  public static final String RWS_RVW_CREATOR_BY_RESULT_ID = "creator";

  /**
   * Rest web service path of Review calibrationengineer
   */
  public static final String RWS_RVW_CAL_ENGG_BY_RESULT_ID = "calibrationengineer";

  /**
   * Rest web service path of Review auditor
   */
  public static final String RWS_RVW_AUDITOR_BY_RESULT_ID = "auditor";

  /**
   * Rest web service path of Review additional Participants
   */
  public static final String RWS_RVW_ADDL_PARTICIPANTS_BY_RESULT_ID = "addlparticipants";

  /**
   * Rest web service path of Review Result Parameter service
   */
  public static final String RWS_RVW_PARAM = "reviewresultparam";

  /**
   * Rest web service path of Review Result parameter, by result ID
   */
  public static final String RWS_RVW_DET_BY_RESULT_ID = "detailsbyresult";


  /**
   * Rest web service path for loading the WP types
   */
  public static final String RWS_RVW_LOAD_WP_TYPES = "loadwptypes";

  /**
   * Path for CDR Rest web services
   */
  public static final String RWS_CONTEXT_APIC = "apic";

  /**
   * Path for CDR Rest web services
   */
  public static final String RWS_UNIT = "unit";

  /**
   * Rest web service path of CDR Report
   */
  public static final String RWS_PIDC_A2L = "pidca2lwithresults";

  /**
   * Rest web service path for PidcChangeHistory
   */
  public static final String RWS_PIDCCHANGEHISTORY = "pidcchangehistory";

  /**
   * Rest web service path of pidc details structure
   */
  public static final String RWS_PIDC_DET_STRUCTURE = "pidcdetstructure";

  /**
   * Rest web service path of pidc details structure
   */
  public static final String RWS_PIDC_DET_STRUCTURE_VERS = "pidcdetstructureforpicversion";


  /**
   * Rest web service path of CDR Report
   */
  public static final String RWS_GRP_PARAM = "grpParam";

  /**
   * Rest web service path of PIDC Search(Scout)
   */

  // ICDM-2326
  public static final String RWS_PIDC_SCOUT = "pidcscout";

  /**
   * Rest web service path of PIDC
   */
  public static final String RWS_PIDC = "pidc";

  /**
   * Rest web service path of User
   */
  public static final String RWS_USER = "user";

  /**
   * Rest web service path for Alias Definition
   */
  public static final String RWS_ALIASDEF = "aliasdef";

  /**
   * Rest web service path for Alias Detail
   */
  public static final String RWS_ALIASDETAIL = "aliasdetail";
  /**
   * Relative path - get by Alias Definition ID
   */
  public static final String RWS_BY_AD_ID = "byadid";
  /**
   * Query parameter Alias Definition ID
   */
  public static final String RWS_QP_AD_ID = "adid";

  /**
   * Rest web service path of Atributes
   */
  public static final String RWS_ATTRIBUTE = "attribute";

  /**
   * Rest web service path of AtributeValue
   */
  public static final String RWS_ATTRIBUTE_VALUE = "attributevalue";

  /**
   * Rest web service path of AttributeExportModel
   */
  public static final String RWS_ATTRIBUTE_EXPORT_MODEL = "attributeexportmodel";

  /**
   * Rest web service path of Predefined AtributeValue
   */
  public static final String RWS_PREDEFINEDATTRVALUE = "predefinedattrvalue";

  /**
   * Rest web service path of Predefined AtributeValue and validity
   */
  public static final String RWS_PREDEFINED_ATTRVALUE_VALIDITY_FOR_VALUE_SET = "predefinedattrvaluevaliditybyvalue";


  /**
   * Rest web service path of BC value
   */
  public static final String RWS_CONTEXT_BC = "bc";
  /**
   * Path for SDOMBC Rest web services
   */
  public static final String RWS_SDOMBC = "sodombc";
  /**
   * Path for SDOMFC2BC Rest web services
   */
  public static final String RWS_SDOMFC2BC = "sodomfc2bc";
  /**
   * Path for SDOMFC Rest web services
   */
  public static final String RWS_SDOMFC = "sodomfc";
  /**
   * Path for SDOMFC Rest web services
   */
  public static final String RWS_SDOMFCBYBCNAME = "sodomfcbybcname";

  /**
   * Query parameter for SDOMBC Rest web services
   */
  public static final String RWS_QP_BC_NAME = "bcname";
  /**
   * Query parameter name - FC Name
   */
  public static final String RWS_QP_FC_NAME = "fcname";
  /**
   * Query parameter name - vCDM A2L File Id
   */
  public static final String RWS_QP_VCDM_A2LFILE_ID = "vcdma2lfileid";

  /**
   * Rest web service path of Atribute super group
   */
  public static final String RWS_ATTRIBUTE_SUPER_GROUP = "attributesupergroup";

  /**
   * Rest web service path of Atribute group
   */
  public static final String RWS_ATTRIBUTE_GROUP = "attributegroup";

  /**
   * Rest web service path of Atribute group model
   */
  public static final String RWS_GET_ATTR_GRP_MODEL = "attributegroupmodel";

  /**
   * Rest web service path of PIDC with attributes
   */
  public static final String RWS_PIDC_WITH_ATTRS = "pidcwithattributes";

  /**
   * Rest web service path of PIDC Type V2
   */
  public static final String RWS_PIDC_TYPE_V2 = "pidctypev2";

  /**
   * Rest web service path of External PIDC with attributes
   */
  public static final String RWS_EXT_PIDC_WITH_ATTRS = "externalpidcwithattributes";
  /**
   * Rest web service path of PIDC with attributes V2
   */
  public static final String RWS_PIDC_WITH_ATTRS_V2 = "pidcwithattributesV2";

  /**
   * Rest web service path of PIDC Version
   */
  public static final String RWS_PIDC_VERS = "pidcversion";

  /**
   * Rest web service path of PIDC Version with details
   */
  public static final String RWS_PIDC_VERS_WITH_DETAILS = "pidcversionwithdetails";

  /**
   * Rest web service optional query param of PIDC Version with details
   */
  public static final String RWS_QP_QUOTATION_RELEVANT_FLAG = "quotationRelevant";

  /**
   * Rest web service optional query param of PIDC Version with details
   */
  public static final String RWS_QP_USE_CASE_GROUP_IDS = "usecaseGroupIds";

  /**
   * Rest web service path of PIDC Version with details
   */
  public static final String COMPLI_PIDC_VERS_WITH_DETAILS = "complipidcversionwithdetails";
  /**
   * Rest web service path of PIDC Version
   *
   * @deprecated Use {@link #RWS_PIDC_VERS_GET_ACTIVE_VERS_WITH_STRUCT_ATTRS} instead
   */
  @Deprecated
  public static final String RWS_PIDC_VERS_GET_ALL_ACTIVE_VERS_WITH_STRUCT_ATTRS = "getallwithstructure";

  /**
   * Rest web service path of PIDC Version
   */
  public static final String RWS_PIDC_VERS_GET_ACTIVE_VERS_WITH_STRUCT_ATTRS = "getactiveverswithstructure";

  /**
   * Rest web service path of External PIDC Version
   */
  public static final String RWS_EXT_PIDC_VERS_GET_ACTIVE_VERS_WITH_STRUCT_ATTRS = "activeexternalverswithstructure";

  /**
   * Rest web service path of PIDC Version
   */
  public static final String RWS_PIDC_VERS_GET_VERS_WITH_STRUCT_ATTRS = "getversionswithstructure";

  /**
   * Rest web service path of WebFlow
   */
  public static final String RWS_PIDC_WEBFLOW = "pidcWebFlow";

  /**
   * Rest web service path of Pidc version stat
   */
  public static final String RWS_PIDC_STAT = "pidcVersionStat";

  /**
   * Rest web service path of CQN State
   */
  public static final String RWS_CQN_STATE = "cqnstate";

  /**
   * Path for PIDC Rest web services
   */
  public static final String RWS_CONTEXT_PIDC = "pidc";

  /**
   * Rest web service path of All PIDCs
   */
  public static final String RWS_ALL_PIDC_REPORT = "allpidc";

  /**
   * Path for A2L Rest web services
   */
  public static final String RWS_CONTEXT_A2L = "a2l";

  /**
   * Path for A2L Rest web services
   */
  public static final String RWS_CONTEXT_COMPLI = "compli";

  /**
   * Path for QSSD Rest web services
   */
  public static final String RWS_QSSD = "qssd";

  /**
   * Rest web service path of a2l compliance parameters
   */

  // Task 263282
  public static final String RWS_COMPLI_PARAM = "a2lcompliparams";
  public static final String RWS_COMPLI_PARAM_PDF = "a2lcompliparamspdf";
  public static final String RWS_COMPLI_REVIEW = "complireview";

  public static final String RWS_RESULT_EDITOR_DATA = "combinedrvweditordata";

  /**
   * Files to be reviewed path
   */
  public static final String RWS_FILES_TO_BE_REVIEWED = "reviewfiles";
  /**
   * Path of the attached file
   */
  public static final String RWS_FILE_PATH = "reviewfilepath";
  /**
   * a2l file field in multipart form data
   */
  public static final String A2L_FILE_MULTIPART = "a2lfile";

  /**
   * Hex file multipart data
   */
  public static final String HEX_FILE_MUTIPART = "hexfile";

  /**
   * meta data
   */
  public static final String META_DATA = "metadata";

  /**
   * Parameter Web flow iD
   */
  public static final String RWS_QP_WEB_FLOW_ID = "webflowid";

  public static final String RWS_RVW_FILE_OBJ = "rvwfileobject";

  /**
   * Parameter A2L file
   */
  public static final String RWS_QP_A2L_FILE = "a2lfile";

  /**
   * Node access
   */
  public static final String RWS_NODE_ACCESS = "nodeaccess";
  /**
   * iCDM client start up
   */
  public static final String RWS_ICDM_CLIENT_STARTUP = "icdmclientstartup";
  /**
   * Service path iCDM version
   */
  public static final String RWS_ICDM_VERSION = "icdmversion";
  /**
   * @deprecated to be removed after 2021.5.0 release
   */
  @Deprecated
  public static final String RWS_ICDM_CLIENT_VERSION = "icdmclientversion";

  /**
   * Apic access
   */
  public static final String RWS_APIC_ACCESS = "apicaccess";

  /**
   * Get all data by parent
   */
  public static final String RWS_GET_ALL_BY_PARENT = "getallbyparent";

  /**
   * Get all data by parent
   */
  public static final String RWS_GET_PARAM_RULES = "getparamrules";

  /**
   * Add Multiple Rule Set Param
   */
  public static final String RWS_CREATE_MULTIPLE_PARAM = "createMultiple";
  /**
   * Add Multiple Rule Set Param
   */
  public static final String RWS_CREATE_MULTIPLE_RULESET_PARAM_USING_A2L = "multirulesetparam";

  /**
   * manage multiple node access
   */
  public static final String RWS_MULTIPLE_NODE_ACCESS_MGMT = "multinodeaccessmanagement";

  /**
   * manage multiple node access
   */
  public static final String RWS_GET_AD_GROUP_DETAILS = "getadgroupdetails";

  /**
   * Delete Multiple Rule Set Param
   */
  public static final String RWS_DELETE_MULTIPLE_PARAM = "deleteMultiple";

  /**
   * Get all data by parent with more details
   */
  public static final String RWS_GET_ALL_BY_PARENT_EXT = "getallbyparentext";


  /**
   * Get node access info for the user and node name combination
   */
  public static final String RWS_GET_NODE_BY_USER_NODENAME = "getnodeaccbyuserandnodename";

  /**
   * Get node access info for the user and node name combination
   */
  public static final String RWS_GET_NODE_BY_FUNFILE = "nodeaccbyfunfile";

  /**
   * ICDM Link
   */
  public static final String RWS_ICDM_LINKS = "link";

  /**
   *
   */
  public static final String RWS_QUESTION = "question";
  /**
   *
   */
  public static final String RWS_QUESTION_CONFIG = "questionconfig";


  /**
   * Get nodes by type
   */
  public static final String RWS_NODES_BY_TYPE = "nodesbytype";

  /**
   * Parameter user
   */
  public static final String RWS_QP_USER = "user";

  /**
   * Parameter user id
   */
  public static final String RWS_QP_USER_ID = "userid";

  /**
   * Parameter level
   */
  public static final String RWS_QP_LEVEL = "level";

  /**
   * Rest web service path of parameter properties
   */
  public static final String RWS_PARAM_PROPS = "paramprops";

  /**
   * System constant service
   */
  public static final String RWS_SYSTEM_CONSTANTS = "systemconstants";

  /**
   * System constant service
   */
  public static final String RWS_INVALID_SYSTEM_CONSTANTS = "invalidsyscons";

  /**
   * Rest web service path of functions
   */
  public static final String RWS_FUNCTIONS = "function";

  /**
   * Rest web service path of fcbcusage
   */
  public static final String RWS_FCBC_USAGE = "fcbcusage";
  /**
   * Rest web service path of functions
   */
  public static final String RWS_RISK_DEFINTION = "risk";

  /**
   * Rest web service path to get all functions in icdm
   */
  public static final String RWS_SEARCH_FUNCTIONS = "search";

  /**
   * Rest web service path to get list of ssd release
   */
  public static final String RWS_SSD_RELEASE = "ssdreleaselist";

  /**
   * Rest web service path to get list of SSDFeatureICDMAttrModel
   */
  public static final String RWS_SSD_FEATURE_ATTR_MODEL = "ssdfeatureicdmattrmodel";

  /**
   * Rest web service path for invalid functions
   */
  public static final String RWS_INVALID_FUNCTIONS = "invalidfunctions";

  /**
   * Rest web service path to get all functions in icdm
   */
  public static final String RWS_GET_METADATA = "metadata";

  /**
   * Rest web service path to get all functions in icdm
   */
  public static final String RWS_GET_CAT_MEASURE = "categorymeasure";

  /**
   * Rest web service path to get all functions in icdm
   */
  public static final String RWS_GET_PID_RM_DEF = "getpidcrmdef";

  /**
   * Rest web service path to get all functions in icdm
   */
  public static final String RWS_PID_RM_DEF = "pidcrmdef";

  /**
   * Rest web service path to get all functions in icdm
   */
  public static final String RWS_PID_RM_PROJ_CHAR = "pidcrmprojchar";

  /**
   * Rest web service path to check if pidc Risk Evaluation is empty
   */
  public static final String RWS_GET_IS_PID_RM_EMPTY = "ispidcrmempty";

  /**
   * Rest web service path to get all functions in icdm
   */
  public static final String RWS_CONSOLIDATED_RISK = "consolidatedrisks";

  /**
   * Rest web service path to get all functions in icdm
   */
  public static final String RWS_PID_RM_PROJ_CHAR_UPDT = "pidcrmprojcharupdt";

  /**
   * Rest web service path of FC to WP Mapping
   */
  public static final String RWS_FC2WP_MAPPING = "fc2wpmapping";

  /**
   * Rest web service path of FC to WP Definition
   */
  public static final String RWS_FC2WP_DEF = "fc2wpdefinition";

  /**
   * Rest web service path of FC to WP Version
   */
  public static final String RWS_FC2WP_VERS = "fc2wpversion";

  /**
   * Rest web service path of FC to WP Version
   */
  public static final String RWS_GET_ALL_BY_DEF_ID = "findbyfc2wpdefid";

  /**
   * Rest web service path of FC to WP Next possible Version
   */
  public static final String RWS_GET_LATEST_MAJ_MIN_VERS_BY_DEF_ID = "findlatestmajorminorversion";

  /**
   * Parameter name - FC to WP Defintion ID
   */
  public static final String RWS_QP_FC2WP_DEF_ID = "fc2wpdefId";

  /**
   * Parameter name - Primary Key
   */
  public static final String RWS_QP_OBJ_ID = "id";

  /**
   * Parameter name - Name
   */
  public static final String RWS_QP_OBJ_NAME = "objname";
  /**
   * Parameter id - attribute value id
   */
  public static final String RWS_QP_ATTR_VALUE_ID = "attrvalueid";

  /**
   * Rest web service path for cdfx export
   */
  public static final String RWS_CDFX_EXPORT = "cdfxexport";

  /**
   * Parameter name - FocusMatrix List
   */
  public static final String RWS_QP_FOCUS_MATRIX_LIST = "focusmatrixlist";

  /**
   * Parameter name - CompPkgId
   */
  public static final String RWS_QP_COMP_PKG_ID = "comppkgid";

  /**
   * Parameter name - CompPkgId
   */
  public static final String RWS_QP_COMP_ID = "compid";
  /**
   * Parameter name - Primary Key
   */
  public static final String RWS_GET_ALL_UNCLEARED_ATTRIDS = "unclearedattrids";

  /**
   * Parameter name - Primary Key
   */
  public static final String RWS_EXECUTE_REVIEW = "executereview";
  /**
   * Parameter name - Primary Key
   */
  public static final String RWS_QP_WP_ID = "wpId";

  /**
   * Parameter name - Primary Key
   */
  public static final String RWS_QP_NODE_NAME = "nodeName";

  /**
   *
   */
  public static final String RWS_QP_WP_SET = "wpSet";

  /**
   * Query Parameter-WP DIV ID
   */
  public static final String RWS_QP_WP_DIV_ID = "wpdivId";

  /**
   * Rest web service path of FC to WP Mapping
   */
  public static final String RWS_FC2WP_MAPPING_BY_VERSION = "findbyfcwpversion";

  /**
   * Rest web service path of FC to WP Mapping
   */
  public static final String RWS_FC2WP_MAPPING_BY_ID = "findbyid";

  /**
   * Query parameter name - A2L File ID
   */
  public static final String RWS_QP_A2L_FILE_ID = "a2lfileID";


  /**
   * Query parameter name for a2l system constant list
   */
  public static final String RWS_QP_A2L_SYSCON_LIST = "a2lsysconlist";

  /**
   * Query parameter name - A2L File ID
   */
  public static final String RWS_QP_FETCH_CHECKVAL = "loadCheckValue";

  /**
   * Query parameter name - PIDC Version ID
   */
  public static final String RWS_QP_PIDC_VERS_ID = "pidcVerID";

  /**
   * Query parameter name - Use Case ID
   */
  public static final String RWS_QP_UC_ID = "usecaseid";

  /**
   * CDR Report - Query parameter name - Variant ID
   */
  public static final String RWS_QP_VARIANT_ID = "variantID";

  /**
   *
   */
  public static final String RWS_QP_PIDC_VARIANT_ID = "pidcvariantID";

  /**
   * CDR Report - Query parameter name - Maximum Reviews
   */
  public static final String RWS_CDRRPT_QP_MAX_REVIEWS = "maxReviews";

  /**
   * Pidc a2l - Query parameter name - Project(PIDC) ID
   *
   * @deprecated use {@link #RWS_QP_PIDC_ID} instead
   */
  @Deprecated
  public static final String RWS_QP_PROJECT_ID = "projectID";

  /**
   * element id
   */
  public static final String RWS_QP_ELEMENT_ID = "elementID";

  /**
   * element type
   */
  public static final String RWS_QP_TYPE = "type";

  /**
   * element type
   */
  public static final String WEBFLOW_VARIANT = "Variant";

  /**
   * element type
   */
  public static final String WEBFLOW_PIDC = "pidc";

  /**
   * passWord in request Icdm-2572
   */
  public static final int REFRESH_TIME_OUT = 30;

  /**
   * ICDM-2603
   */
  public static final String RWS_COPY_A2L_RESP = "copyResp";

  /**
   * Query parameter - FC to WP Version ID
   */
  public static final String RWS_QP_FC2WP_VERS_ID = "fc2wpVersID";

  /**
   * Query parameter - FC to WP Mapping ID
   */
  public static final String RWS_QP_FC2WP_MAPPING_ID = "fc2wpMappingID";

  /**
   * Query parameter - Power Train type
   */
  public static final String RWS_POWER_TRAIN_TYPE = "powertraintypes";

  /**
   * Query parameter - Error code
   */
  public static final String RWS_QP_ERRORCODE = "errorcode";

  /**
   * Query parameter - FC to WP Mapping ID
   */
  public static final String RWS_RELEVANT_PT_TYPE = "findrelevantpttypes";
  public static final String REQ_USERNAME = "userName";
  public static final String REQ_PASSWRD = "passWord";
  public static final String REQ_LANGUAGE = "language";
  public static final String REQ_TIMEZONE = "timezone";
  public static final String REQ_CNS_SESSION_ID = "CnsSessionID";
  public static final String RWS_REQUEST_ATTR_COMMON_SER_DATA = "CommonServiceData";
  public static final String RWS_GET_ALL = "getall";
  public static final String RWS_GET_PIDC_DIFFS = "pidcdiffs";
  public static final String RWS_GET_AD_GRP_USERS_BY_GROUP_ID = "getUsersByGroupId";
  public static final String RWS_GET_AD_GRP_ACCESS_BY_NODE_ID = "getGrpAccessByNodeId";
  public static final String RWS_MAP_UNMAP = "mapunmap";
  public static final String RWS_GET_ALL_DISTINCT = "getalldistinct";
  public static final String RWS_GET_APIC_USER_BY_USERNAME = "getapicuserbyusername";
  public static final String RWS_GET_WS_VERS_BY_DEF_ID = "wsversion";
  public static final String RWS_GET_ACTIVE_VERS_BY_DEF_ID = "activeversion";
  public static final String RWS_GET_ACTIVE_VERS_BY_VALUE_ID = "activeversionbynamendiv";
  // new qp for fc2wp name
  public static final String RWS_QP_FC2_WP_NAME = "fc2wpname";
  public static final String RWS_QP_DIV_VALUE_ID = "divvalueid";
  public static final String RWS_WP = "wp";
  public static final String RWS_WP_RESPONSE_MAP = "wpresponsemap";

  /**
   * Rest service path for service to fetch list of a2l wp defn versions for given pidca2l id
   */
  public static final String RWS_WP_DEFN_VERS_BY_PIDC_A2L_ID = "getwpdefnversbypidca2lid";

  /**
   * Rest service path for service to fetch list of a2l wp responsibilities for given wp defn vers id
   */
  public static final String RWS_WP_RESP_BY_WP_DEFN_VERS_ID = "getwprespbywpdefnversid";


  /**
   * Rest service path to fetch list of pidc wp resp for given pidc id
   */
  public static final String RWS_A2L_RESPONSIBILITY_BY_PIDC = "a2lrespbypidc";

  /**
   * Rest service path to create/update a2l responsibility
   */
  public static final String RWS_A2L_RESPONSIBILITY_MAINTENANCE = "a2lrespmaintenance";

  /**
   * Rest service path to merge a2l responsibility
   */
  public static final String RWS_MERGE_A2L_RESPONSIBILITY = "mergea2lresponsibility";

  /**
   * Rest service path to create json file from the selected qnaire response details
   */
  public static final String RWS_PARSE_A2L_RESP_DETAILS_TO_JSON = "parsea2lrespdetailstojson";

  /**
   * Rest service path fetch merge input data saved in server (as JSON file)
   */
  public static final String RWS_A2L_RESP_MERGE_INPUT_FETCH = "respmergeinputdatafetch";

  /**
   * Rest service path for service to find active version of a2l wp defn version is available for given pidca2l id
   */
  public static final String RWS_ACTIVE_WP_DEFN_VERS_BY_PIDC_A2L_ID = "hasactivewpdefnversbypidca2lid";
  /**
   *
   */
  public static final String RWS_WP_DIV_CDL = "wpdivcdl";

  public static final String RWS_GET_WORKPKG = "workpkg";

  public static final String RWS_GET_WORKPKG_NAME = "workpkgname";

  /**
   *
   */
  public static final String RWS_REGION = "region";


  public static final String RWS_WP_DIV = "wpdivision";
  public static final String RWS_WP_RES = "wpresource";
  public static final String RWS_GET_WP_DIV_BY_DIV_ID = "wpdivbydivid";
  public static final String RWS_GET_WP_DIV_BY_WP_ID = "wpdivbywpid";
  /**
   * Rest web service path for fetching cdl data
   */

  public static final String RWS_GET_ALL_BY_WP_DIV_ID = "getallbywpdivid";
  /**
   * Rest web service path for fetching region
   */

  public static final String RWS_GET_ALL_REGION = "getAllRegion";
  public static final String RWS_FC2WP_MAPPING_BY_A2L_N_NAME_N_DIV = "fc2wpmappingbya2l";
  public static final String RWS_FC2WP_MAPPING_BY_PIDC_A2L = "fc2wpmappingbypidca2l";
  public static final String RWS_QP_PIDC_A2L_ID = "pidca2lid";
  public static final String RWS_GET_PIDC_A2L_BY_PIDC_VERS_ID = "pidca2lbypidcversion";
  public static final String RWS_GET_ALL_A2L_BY_PIDC_ID = "getalla2lbypidc";
  public static final String RWS_GET_ALL_SDOM_FOR_PIDC = "getallpvernamesforpidc";
  public static final String RWS_GET_SSD_PROJECT_VERSIONS = "getssdprojectversions";
  public static final String RWS_QP_SW_PROJ_NODE_ID = "softwareprojectnodeid";

  public static final String RWS_GET_ALL_PIDC_VERSIONS_FOR_PIDC = "getallPidcversionforpidc";

  public static final String RWS_GET_ALL_SDOM_FOR_PIDC_VERSION = "getallpvernamesforpidcversion";

  public static final String RWS_SDOMPVER_NAMES_BY_PIDC = "pvernamesbypidc";
  public static final String RWS_GET_PIDC_VER_MAPPED_TO_SDOM_PVER = "getPidcVerMappedToSdomPver";
  public static final String RWS_QP_SEL_A2L_FILES = "selecteda2lfiles";


  public static final String RWS_GET_VCDM_A2L_FILE_DETAIL = "getvcdma2lfiledetail";

  public static final String RWS_QP_A2L_FILE_CHECKSUM = "a2lfilechecksum";
  public static final String RWS_GET_CDRRESULTS_BY_PIDCA2L = "getcdrresultsbypidca2l";


  public static final String RWS_Q_FC2WP_MAPPING_BY_DIV_ID = "qnairewpdivbydivid";
  public static final String RWS_GET_ALL_WP_RES = "wpres";
  public static final String RWS_GET_A2LRESP_BY_PIDCA2L_WP = "pidca2landwproottype";
  /**
   * Path for A2L Rest web services
   */
  public static final String RWS_GET_A2L_EDITOR_MODEL = "a2leditormodel";
  /**
   * Path for A2L Rest web services
   */
  public static final String RWS_GET_A2L_SYS_CONSTANTS = "getalla2lsysconstants";

  /**
   * Path for A2L Rest web services
   */
  public static final String RWS_GET_A2L_BASE_COMP = "geta2lbc";

  /**
   * Execution id used to get input files used for compli rvw
   */
  public static final String RWS_QP_EXECUTION_ID = "executionid";

  public static final String PVER_NAME = "pvername";
  /**
   * Pidc variant/version details used for compli rvw
   */
  public static final String RWS_PIDC_VARIANTS_INPUT_DATA = "pidcvariantinputdata";

  /**
   * Rest web service path of fetching CDR Pre-Calibrated data
   */

  // Task 243510
  public static final String RWS_CDR_PRECALDATA = "precalibrationdata";
  public static final String RWS_QP_FUNC_SEARCH = "criteria";
  public static final String RWS_QP_FUNC_LIST = "functionlist";

  public static final String RWS_QP_VERS_ID = "pidversid";
  public static final String RWS_QP_PIDC_RM_ID = "pidrmid";
  public static final String RWS_QP_FEATURE_ID = "featureid";
  public static final String RWS_RISK_CONS_CAT_RISK = "consilidatedrisks";
  public static final String RWS_RISK_CONS_VERSION = "consilidatedriskspidc";
  public static final String RWS_GET_PID_RM_OUTPUT = "pidcrmoutput";

  public static final String RWS_GET_EMR_FILE_VARIANTS = "filevariantmap";
  public static final String RWS_QP_USERNAME = "username";
  /**
   * Software version id
   */
  public static final String RWS_QP_SW_VERSION_ID = "softwareversionid";
  /**
   * Query param for attribute
   */
  public static final String RWS_QP_ATTRIBUTE_ID = "attributeid";

  /**
   * Rest web service path of EMR
   */
  public static final String RWS_EMISSION_ROBUSTNESS = "emr";

  /**
   * Rest web service path for file
   */
  public static final String RWS_HANDLE_FILE = "file";

  /**
   * Rest web service path for file
   */
  public static final String RWS_EMR_FILE_VARIANT = "filevariant";

  /**
   * Rest web service path for emr file data
   */
  public static final String RWS_EMR_DATA_EXTERNAL = "emrdataexternal";

  /**
   *
   */
  public static final String RWS_GET_EMS_VARIANT_MAP = "emsfilevariantmap";

  /**
   * constant for upload result path
   */
  public static final String RWS_EMR_FILE_UPLOAD_RESULT = "emrfileuploadresult";

  /**
   * Rest web service path for CDR Result Parameter
   */
  public static final String RWS_CDRRESULTPARAMETER = "cdrresultparameter";
  /**
   * Rest web service path for CDR Review process
   */
  public static final String RWS_REVIEW = "review";

  /**
   * Rest web service path for CDR Review process
   */
  public static final String RWS_QP_REVIEW_DATA = "reviewdata";

  /**
   * Rest web service path for questionnaire
   */
  public static final String RWS_QP_REVIEW_FUNCTIONS = "reviewfunctions";
  /**
   * Rest web service path for CDR Review process
   */
  public static final String RWS_QP_REVIEW_TYPE = "reviewtype";
  /**
   * Base64 encoding format
   */
  public static final String CHARACTER_ENCODING_FORMAT = "UTF-8";
  public static final String RWS_GET_EMR_FILE = "file";
  public static final String RWS_EMR_FILE_ID = "emrfileId";
  public static final String RVW_FILE_ID = "rvwfileId";
  /**
   * constant for the upload files path
   */
  public static final String EMR_UPLOAD_FILES = "emrfile";

  /**
   * CONSTANT for EMR file description
   */
  public static final String EMR_FILE_DESC = "emrfiledesc";

  /**
   * constant for emr file scope
   */
  public static final String EMR_FILE_SCOPE = "emrfilescope";

  /**
   * constant for emr pidc version id
   */
  public static final String EMR_PIDC_VERSION_ID = "emrpidcversionid";
  /**
   * constant for emr file path
   */
  public static final String EMR_FILE_PATH = "emrfilepath";

  /**
   * constant for emr file name
   */
  public static final String EMR_FILE_NAME = "emrfilename";
  public static final String RWS_URL_DELIMITER = "/";

  /**
   * constant for reloading EMR file
   */
  public static final String RWS_EMR_FILE_RELOAD = "reloademrfile";

  /**
   * constant for pidc version link
   */
  public static final String RWS_PIDC_VERSION_LINK = "pidcversionlink";

  /**
   * Path to SDOM PVER
   */
  public static final String RWS_SDOM_PVER = "sdompver";

  /**
   * Path to SDOM PVER
   */
  public static final String RWS_PIDC_VERSION_ATTR_FOR_ATTR = "getPidcVersionAtributeForAttr";

  public static final String RWS_PIDC_VARIANT_ATTR_FOR_ATTR = "getPidcVariantAtributeForAttr";

  /**
   * Pidc Attribute Difference for version service
   */
  public static final String RWS_PIDC_ATTR_DIFF_FOR_VERSION = "pidcAttrDiffForVers";

  /**
   * Pidc Difference for version service
   */
  public static final String RWS_PIDC_DIFF_FOR_VERSION = "pidcDiffForVers";

  /**
   * Path to PVER names
   */
  public static final String RWS_PVER_NAMES = "pvernames";

  /**
   * Path to PVER variants
   */
  public static final String RWS_PVER_VARS = "pvervariants";

  /**
   * Path to Pver versions
   */
  public static final String RWS_PVER_VAR_VERSIONS = "pvervariantrevisions";

  /**
   * Query parameter - variant
   */
  public static final String RWS_QP_NAME = "name";

  /**
   * Query parameter - variant
   */
  public static final String RWS_QP_NAME_SET = "nameset";


  /**
   * FUNCTION NAME
   */
  public static final String RWS_QP_FUNCTION_NAME = "funcName";

  /**
   * Rule Set Id
   */
  public static final String RWS_QP_RULESET_ID = "ruleSetId";

  /**
   * Query parameter - variant
   */
  public static final String RWS_QP_VARIANT = "variant";

  /**
   * Query Parameter - pidc id
   */
  public static final String RWS_QP_PIDC_ID = "pidcId";
  /**
   *
   */
  public static final String RWS_APRJ_PIDCS = "aprjpidcs";
  /**
   *
   */
  public static final String RWS_CREATE_NEW_REVISION = "createnewrevison";

  /**
   * Query Parameter - selected APRJ NAME
   */

  public static final String RWS_QP_APRJ_NAME = "aprjname";

  public static final String RWS_QP_VERS_DETAILS = "newrevisiondetails";

  /**
   * Path to Pidc Web flow element service
   */
  public static final String RWS_WEBFLOWELEMENT = "WebflowElement";

  /**
   * rest web service path for parameter service
   */
  public static final String RWS_QP_PARAMETER_NAMES = "parameterNames";

  /**
   * rest web service path for parameter service
   */
  public static final String RWS_PARAMETER = "parameter";

  /**
   * rest web service path for parameter service
   */
  public static final String RWS_INVALID_PARAMETER = "invalidParameters";


  public static final String RWS_CONTEXT_CDA = "cda";

  /**
   * rest web service path for CDA service
   */
  public static final String RWS_CAL_DATA_ANALYSIS = "caldataanalysis";

  public static final String RWS_CDA_FILTER = "cdafilter";


  /**
   * review rule.
   */
  public static final String RWS_REVIEW_RULE = "rule";


  /**
   * review rule.
   */
  public static final String RWS_REVIEW_RULE_SET_RULE = "rulesetrule";
  /**
   * Path for COMP Rest web services
   */
  public static final String RWS_CONTEXT_COMP = "comp";

  /**
   * all functions
   */
  public static final String RWS_ALL_FUNCTIONS = "allfunctions";

  /**
   * Rest web service path for CdrResultParameter
   */
  public static final String RWS_GET_CDR_RESULT_PARAMETER = "getdcrresultparameter";

  /**
   * Rest web service path for result_id
   */
  public static final String RWS_GET_BY_RESULT_ID = "byresultid";

  /**
   * Rest web service path for a2l details structure
   */
  public static final String RWS_GET_BY_A2L_WP_DEF_VER_ID = "bya2lwpdefversid";


  /**
   * Rest web service path for a2l details structure
   */
  public static final String RWS_GET_VAR_GRP_A2L_WP_DEF_VER_ID = "vargrpbya2lwpdefversid";
  /**
   * Rest web service path for result_id
   */
  public static final String RWS_QP_RESULT_ID = "resultid";

  /**
   * Rest web service path for result_id
   */
  public static final String RWS_A2L_WP_DEF_VER_ID = "a2lwpdefversid";

  public static final String RWS_QP_PARAM_NAME = "paramname";

  public static final String RWS_QP_PARAM_FIRST_NAME = "paramfirstname";

  public static final String RWS_QP_PARAM_LAST_NAME = "paramlastname";

  /**
   * Query parameter 'version'
   */
  public static final String RWS_QP_VERSION = "version";

  public static final String RWS_QP_BY_VAR = "byvariant";
  public static final String RWS_QP_PARAM_TYPE = "paramtype";

  /**
   * current user
   */
  public static final String RWS_APIC_CURRENT_USER = "currentuser";

  /**
   * Rest web service path for Usecase Group
   */
  public static final String RWS_USECASEGROUP = "usecasegroup";

  /**
   * Rest web service path for Usecase
   */
  public static final String RWS_USECASE = "usecase";

  /**
   * Rest web service path for Usecase Section
   */
  public static final String RWS_USECASESECTION = "usecasesection";
  /**
   * Rest web service path for Ucp Attr
   */
  public static final String RWS_UCPATTR = "ucpattr";
  /**
   * Rest web service path for usecase
   */
  public static final String RWS_GET_UC_TREE_DATA = "usecasetreeviewdata";
  /**
   * Rest web service path for usecase details
   */
  public static final String RWS_GET_UC_DETAILS_DATA = "usecasedetailsdata";

  /**
   * web service path to get the objects by primary, for multiple objects.
   */
  public static final String RWS_MULTIPLE = "multiple";

  /**
   * Rest web service path of messages
   */
  public static final String RWS_MESSAGE = "message";

  /**
   * Rest web service path of errorcode
   */
  public static final String RWS_ERRORCODE = "errorcode";

  /**
   * Rest web service path of Common Parametes
   */
  public static final String RWS_COMMON_PARAM = "commonparam";

  /**
   * Rest web service path of includemonicaauditor
   */
  public static final String RWS_INCLUDE_MONICA_AUDITOR = "includemonicaauditor";

  /**
   * Rest web service path of WS Attr/Value dependecy
   */
  public static final String RWS_ATTRNVALUEDEPENDENCY = "wsattrvaldependency";
  public static final String RWS_FUNCTION_VERSIONS = "funcver";

  /**
   * Rest web service path of component packages
   */
  public static final String RWS_COMP_PKG = "comppkg";

  public static final String RWS_COMP_PKG_OBJ = "comppkgobj";
  /**
   * Rest web service path of component packages bc fc
   */
  public static final String RWS_COMP_BC_FC = "comppkgbcfc";

  public static final String DELETE = "delete";

  /**
   * Rest web service path of component packages bc
   */
  public static final String RWS_COMPPKGBC = "compPkgBC";

  /**
   * Rest web service path of component packages bc Id
   */
  public static final String RWS_GET_BY_COMP_BC_ID = "compPkgBCId";
  /**
   * Rest web service path of component packages bc Id
   */
  public static final String RWS_GET_TITLE_BY_ID = "compPkgBCIdforPropTitle";
  /**
   * Rest web service path of fcbcusage for bc
   */
  public static final String RWS_GET_FCBC_USAGE_BY_BC = "fcbcusagebybc";
  /**
   * Rest web service path of fcbcusage for fc
   */
  public static final String RWS_GET_FCBC_USAGE_BY_FC = "fcbcusagebyfc";


  /**
   * Rest web service path for datasets
   */
  public static final String RWS_GET_VCDM_DATASETS = "getvcdmdatasets";
  /**
   * Query parameter for CompPkgBc Put method
   */
  public static final String RWS_QP_IS_UP = "isUp";

  /**
   * Rest webservice query parameter for bcId
   */
  public static final String RWS_QP_COMP_BC_ID = "bcid";
  public static final String RWS_QP_FUNC_VERS_SEARCH = "verssearch";
  public static final String RWS_GET_PARAM = "param";
  public static final String RWS_GET_PARAMS_ONLY_BY_NAME = "paramsonlybyname";

  public static final String RWS_GET_PARAMS_BY_NAME = "paramsbyname";

  public static final String RWS_GET_PARAMS_BY_FUNC_NAME = "parambyFuncName";

  public static final String RWS_GET_FUNCTIONS_BY_NAME = "functionsbyname";

  public static final String RWS_GET_FUNCTIONS_BY_PARAMNAME = "functionsbyparamname";

  public static final String RWS_FUNCTION_MISMATCH = "functionmismatch";

  public static final String RWS_LABEL_MISMATCH = "labelmismatch";

  public static final String RWS_PARAM_COUNT = "paramcountbyfuncionnames";

  public static final String PARAMS_USING_IDS = "paramsusingids";

  public static final String RWS_SEARCH_PARAMETERS = "searchparameters";

  public static final String RWS_VAR_CODED_PARAM_ID = "varcodedparamid";

  public static final String RWS_GET_BY_SSD_NODE_ID = "ssdnodeid";

  public static final String RWS_GET_COMP_PKG_PARAM = "comppkgparam";

  public static final String RWS_FUNCTION_PARAM = "funcparam";

  /**
   * For PIDCA2lTreeStructure
   */
  public static final String RWS_PIDC_A2L_TREE_STRUCT = "pidca2ltreestructure";

  public static final String RWS_PARAM_ATTR = "paramattr";

  public static final String RWS_RULEST_PARAM_ATTR = "rulesetparamattr";

  public static final String RWS_RULEST_PARAM = "rulesetparams";
  /**
   * pidc tree
   */
  public static final String RWS_PIDC_TREE = "pidctree";
  /**
  *
  */
  public static final String RWS_PIDC_MAX_STRUCT_ATTR_LVL = "pidcstructattrmaxlvl";
  /**
  *
  */
  public static final int PIDC_ROOT_LEVEL = 0;

  public static final String RWS_GET_UC_EDITOR_DATA = "usecaseeditordata";
  /**
   * Attribute Characteristics
   */
  public static final String RWS_CHARACTERISTIC = "attrcharacteristics";
  public static final String RWS_CHARACTERISTIC_VALUE = "characteristicsvalue";

  public static final String RWS_UNDELETE = "undelete";

  public static final String RWS_GET_ALL_BY_NODE = "getallbynode";

  public static final String RWS_GET_NODE_WITH_LINK = "getnodewithlink";
  /**
   * Query parameter - to get all node ids associated with nodetype
   */
  public static final String RWS_GET_ALL_NODE_ID_BY_TYPE = "getallnodeidbynodetype";

  public static final String RWS_QP_INCLUDE_APIC_WRITE_USERS = "includeapicwriteusers";

  public static final String RWS_QP_NODE_ID = "nodeid";

  public static final String RWS_QP_COMP_PKG = "componentpackage";

  public static final String RWS_QP_NODE_TYPE = "nodetype";

  public static final String RWS_PREDEFINEDVALIDITY = "predefinedvalidity";

  /**
   * Query parameter - include deleted items
   */
  public static final String RWS_QP_INCLUDE_DELETED = "includedeleted";

  /**
   * Query parameter - ICC Relevant Flag
   */
  public static final String ICC_RELEVANT_FLAG = "iccrelevantflag";

  /**
   * Query parameter - include qnaire without question flag
   */
  public static final String RWS_QP_INCLUDE_QNAIRE_WITHOUT_QUEST = "includeqnairewithoutquest";

  /**
   * Rest web service path for Questionnaire
   */
  public static final String RWS_QUESTIONNAIRE = "questionnaire";
  /**
   * Rest web service path for CDRReviewResult
   */
  public static final String RWS_CDRREVIEWRESULT = "cdrreviewresult";

  /**
   * Rest web service path for CDRReviewVariant
   */
  public static final String RWS_CDR_RVW_VARIANT = "cdrreviewvariant";

  /**
   * Rest web service path for Review Files
   */
  public static final String RWS_RVWFILE = "rvwfile";

  /**
   * Usecase favorite for user
   */
  public static final String RWS_USECASEFAVORITE = "usecasefavorite";

  /**
   * Usecase favorite for project
   */
  public static final String RWS_PROJUSECASEFAVORITE = "projusecasefavorite";

  /**
   * Rest web service path to fetch map of all level attributes with attribute id as key
   */
  public static final String RWS_ALL_LVL_ATTR_ATTRID = "levlattrwithattridkey";

  /**
   * Rest web service path to fetch map of all level attributes with attribute level as key
   */
  public static final String RWS_ALL_LVL_ATTR_LEVEL = "levlattrwithlevelkey";

  /**
   * Rest web service path to fetch map of all level attribute values with attribute id as key and set of attribute
   * values as value
   */
  public static final String RWS_ALL_LVL_ATTR_VALUES_SET = "levlattrwithvalueset";
  /**
   * Rest web service path for Review Paticipants
   */
  public static final String RWS_RVWPARTICIPANT = "rvwparticipant";
  /**
   * Rest web service path to fetch map of all non-active pidc versions for a pidc
   */
  public static final String RWS_NONACTIVE_PIDC_VERSIONS = "nonactivepidcversions";

  /**
   * Context parameter - pidc id to fetch a2l file info
   */
  public static final String RWS_FILE_INFO = "fileinfo";

  /**
   * Context parameter - A2L_FILE_EXPORT
   */
  public static final String A2L_FILE_EXP = "a2lexport";

  /**
   * Context parameter - pidc id to fetch a2l file info
   */
  public static final String RWS_FILE_UPLOAD = "uploadfile";
  /**
   * Query parameter - pidc id to check presence of other pidc versions
   */
  public static final String RWS_QP_PIDC_ID_OTHVER = "pidcidforothver";

  /**
   * Rest web service path to GET active pidc version
   */
  public static final String RWS_ACTIVE_PIDC_VERSION = "activepidcversion";

  /**
   *
   */
  public static final String RWS_IS_OTHER_PIDC_VERSIONS = "isotherpidcverpresent";

  /**
   * Rest web service path to update last confirmation date
   */
  public static final String RWS_UPDATE_CONFRM_DATE = "updateLastConfrmDate";

  /**
   * Rest webservice for MoniCa Review
   */
  public static final String RWS_MONICA_REVIEW = "monicareview";

  /**
   * dcm file field in multipart form data
   */
  public static final String DCM_FILE_MULTIPART = "dcmfile";

  /**
   * MoniCa file field in multipart form data
   */
  public static final String MONICA_FILE_MULTIPART = "monicafile";

  /**
   * parent result id
   */
  public static final String PARENT_RESULT_ID = "parentresultId";

  /**
   * Parameter name - Primary Key
   */
  public static final String RWS_QP_MONICA_REVIEW_OBJECT = "metadata";

  /**
   * Rest web service path for A2L Responsibility
   */
  public static final String RWS_A2L_RESPONSIBILITY = "a2lresponsibility";

  /**
   * Rest web service path for A2L WP Import Prifile
   */
  public static final String RWS_A2L_WP_IMPORT_PROFILE = "a2lwpimportprofile";

  /**
   * Rest web service path for PidcA2lId
   */
  public static final String RWS_PIDC_A2L_ID = "pidca2lid";

  /**
   * Rest web service path for A2L Responsibility (old structure)
   *
   * @deprecated not used
   */
  @Deprecated
  public static final String RWS_A2L_RESP = "a2lresp";

  /**
   * Query param for A2L Responsibility Id
   */
  public static final String RWS_A2L_RESP_ID = "a2lrespid";

  /**
   * Query param for Has Questionnaire Access service
   */
  public static final String RWS_HAS_QNAIRE_ACCESS = "qnaireaccess";

  /**
   * Query param for A2L Responsibility - wp root Id
   */
  public static final String RWS_QP_WP_ROOT_ID = "wprootid";
  /**
   * Query param for A2L Responsibility - wp type Id
   */
  public static final String RWS_QP_WP_TYPE_ID = "wptypeid";

  /**
   *
   */
  public static final String RWS_SDOM_PVER_FOR_VER = "sdompverforver";

  /**
   *
   */
  public static final String RWS_QP_PIDC_VER_ID_SDOM = "pidcveridsdom";

  /**
   * @deprecated use {@link #RWS_QP_PIDC_VERS_ID} instead
   */
  @Deprecated
  public static final String RWS_QP_PIDC_VER_ID = "pidcverid";

  /**
   *
   */
  public static final String RWS_PIDC_VER_CHILD = "pidcnodechildavailability";
  /**
  *
  */
  public static final String RWS_GET_PIDC_A2L_AVAILBLTY = "pidca2lavailbility";
  /**
   * To validate pidc a2l assignment to different pidc version
   */
  public static final String RWS_GET_PIDC_A2L_ASSIGN_VALIDATION = "pidca2lassignmentvalidation";

  /**
   *
   */
  public static final String RWS_PIDC_A2L_DETAILS_BY_APRJ = "pidca2ldetailsbyaprj";

  /**
   *
   */
  public static final String RWS_QP_APRJ_VARIANT = "variantname";

  /**
   *
   */
  public static final String RWS_GET_SDOM_PROJ_NODE_ID = "getsdomprojnodeid";


  /**
   * Query parameter for Sdom PVer name
   */
  public static final String RWS_QP_SDOM_PVER_NAME = "sdompvername";

  /**
   * Query parameter for Sdom PVer variant
   */
  public static final String RWS_QP_SDOM_PVER_VARIANT = "sdompvervariant";

  /**
   * Query parameter for Sdom PVer variant revision
   */
  public static final String RWS_QP_SDOM_PVER_VARIANT_REV = "sdompvervariantrev";

  /**
   * Rest web service path for Workpackage Responsibility
   */
  public static final String RWS_WP_RESPONSIBILITY = "wpresp";
  /**
   *
   */
  public static final String RWS_GET_PIDCVERSION_BY_PIDCVERSIONID = "getPidcVersionByPidcVersionId";


  /**
   * Rest web service path for Get attribute value by pidcVersion
   */
  public static final String RWS_GET_ATTR_VALUE_BY_PIDCVERSION = "getattrvaluebypidcvers";


  /**
   * Rest web service path to check if level attribute value is used
   */
  public static final String RWS_LEVEL_ATTR_VALUE_USED = "levelattrvalueused";


  /**
   * Rest web service path for Get review results by pidc Version id
   */
  public static final String RWS_PIDC_REV_RESULTS = "pidcrevresults";

  /**
   *
   */
  public static final String RWS_PIDC_GRP_WP_NAMES = "pidcgrpwpnames";

  /**
   *
   */
  public static final String RWS_HAS_PIDC_REV_RESULTS = "haspidcrevresults";

  /**
   * Rest Webservice path for checking whether the the review is used in CDFX delivery
   */
  public static final String RWS_CDFX_DELIVERY_USAGE_CHECK = "cdfxdeliveryusagecheck";
  /**
   * Rest web service path for focus matrix
   */
  public static final String RWS_FOCUSMATRIX = "focusmatirx";

  /**
   * Rest web service path for focus matrix version
   */
  public static final String RWS_FOCUSMATRIXVERSION = "focusmatirxversion";

  /**
   * Rest web service path for focus matrix version attr
   */
  public static final String RWS_FOCUSMATRIXVERSIONATTR = "focusmatirxversionattr";

  /**
   * get the mapped attr
   */
  public static final String RWS_GET_MAPPED_ATTR = "mappedattr";

  /**
  *
  */
  public static final String RWS_RVWQNAIRERESPONSE = "rvwqnaireresp";

  /**
   * Review Questionnaire response variant service
   */
  public static final String RWS_RVW_QNAIRE_RESPONSE_VARIANT = "rvwqnairerespvariant";

  /**
   * Review Questionnaire response variant service
   */
  public static final String RWS_DEFINE_QNAIRERESP_INPUT_DATA = "defineqnairerespinputdata";

  /**
   * Review Questionnaire response variant service
   */
  public static final String RWS_RVW_QNAIRE_RESPONSE_VARIANT_LIST = "rvwqnairerespvariantlist";

  /**
  *
  */
  public static final String RWS_RVWQNAIRE = "rvwqnaire";
  /**
  *
  */
  public static final String RWS_GET_PIDC_QNAIRE_RESP = "getpidcqnaireresponse";
  /**
  *
  */
  public static final String RWS_GET_QNAIRERESP_FOR_WPRESP = "getqnairerespforwpresp";
  /**
  *
  */
  public static final String RWS_HAS_PIDC_QNAIRE_VAR = "getpidcqnaireVariants";

  /**
   *
   */
  public static final String RWS_GET_PIDC_VAR_REV_RES = "getpidcvarrevres";

  /**
   * Rest web service path for Get workpackage mapping by pidc Version id
   */
  public static final String RWS_GET_A2L_WP_MAPPING = "geta2lworkpackagemapping";

  /**
   * Rest web service path to resolve a2l wp responsibility based on a2l groups
   */
  public static final String RWS_RESOLVE_A2L_WP_RESP = "resolvea2lwpresp";
  /**
   * Rest web service path to update pidc name attribute value
   */
  public static final String RWS_UPD_PIDC_NAME = "updatePidcName";


  public static final String REVIEW_FILE_MULTIPART = "reviewfile";

  public static final String SSD_RULE_FILE_MULTIPART = "ssdrulefile";

  public static final String LAB_FUN_FILE_MULTIPART = "labfunfile";

  public static final String RWS_QP_REVIEW_OBJECT = "reviewobject";

  public static final String RWS_REVIEW_OBJECT = "reviewobj";
  public static final String REVIEW_FILE_NAME = "reviewfilename";
  public static final String LAB_FUN_FILE_NAME = "labfunfilename";
  public static final String SSD_RELEASE_FILE_NAME = "ssdreleasefilename";
  /**
   * Rest web service path for Get focus matrix for version
   */
  public static final String RWS_GET_FOCUS_MATRIX_FOR_VERSION = "getFocusMatrixForVersion";

  /**
   * Rest web service path for Get focus matrix attributes for version
   */
  public static final String RWS_GET_FOCUS_MATRIX_ATTR_FOR_VERSION = "getFocusMatrixAttrForVersion";

  /**
   * Rest web service path for Get focus matrix versions for pidc version
   */
  public static final String RWS_GET_FOCUS_MATRIX_VERSION_FOR_PIDC = "getFocusMatrixVersionsForPidc";

  /**
   * Rest web service path for multiple focus matrix update
   */
  public static final String RWS_MULTIPLE_UPDATE_FOCUS_MATRIX = "multipleUpdateFocusMatrix";

  /**
   * Rest Web service path for creating multiple focus matrix record
   */
  public static final String RWS_MULTIPLE_CREATE_FOCUS_MATRIX = "multipleCreateFocusMatrix";

  /**
   * Rest web service query param to identify whether to a2l files
   */
  public static final String RWS_QP_LOAD_A2L = "loadA2l";

  /**
   * Rest web service query param to identify whether to load review results
   */
  public static final String RWS_QP_LOAD_CDR = "loadCdr";

  /**
   * Rest web service query param to identify whether to load Variants
   */
  public static final String RWS_QP_LOAD_VAR = "loadVariants";

  /**
   * Rest web service query param to identify whether to load Variants
   */
  public static final String RWS_QP_LOAD_QNAIRE = "loadQnaire";

  /**
   * ICDM-2603
   */
  public static final String RWS_A2L_WP_RESP = "a2lwpresp";

  public static final String RWS_GET_ALL_FEATURES = "allfeatures";
  /**
   * Rest web service path for delete / undelete Pidc
   */
  public static final String RWS_DEL_UNDEL_PIDC = "deleteUndeletePidc";

  /**
   * Rest web service path for fetching the existing Pidc names
   */
  public static final String RWS_GET_PIDC_CREATION_DETAILS = "getPidcCreationDetails";

  /**
   * Rest web service query param for project name attr level
   */
  public static final String RWS_QP_PROJ_NAME_ATTR_LVL = "projNameAttrLvl";

  /**
   * Rest web service query param for pidc creation
   */
  public static final String RWS_PIDC_CREATION = "pidcCreation";
  /**
   * the ATTR_LEVEL used for the project name
   */
  public static final Long PROJECT_NAME_ATTR_LVL = -1L;

  /**
   * Rest web service path of Atributes
   */
  public static final String RWS_FEATURE = "feature";


  /**
   * Rest web service path of Atributes
   */
  public static final String RWS_FEATURE_VALUE = "featurevalue";

  public static final String RWS_GET_BY_FEA_ID = "getbyfeatureid";

  /**
   * Rest web service path for fetching icdmfiles
   */
  public static final String RWS_GET_BY_FILE_ID = "getbyfileid";

  /**
   * Rest web service path for fetching welcomepagefiles
   */
  public static final String RWS_GET_WELCOME_PAGE_FILES = "getwelcomepagefiles";
  /**
   * Rest web service path for iCDM OSS Document download
   */
  public static final String RWS_ICDM_OSS_DOCUMENT = "icdmossdocument";
  /**
   * Rest web service path for fetching icdm disclaimer file
   */
  public static final String RWS_GET_DISCLAIMER_FILE = "getdisclaimerfile";

  /**
   * Rest web service path for fetching icdm disclaimer file
   */
  public static final String RWS_GET_MAIL_TEMPLATE_FILE = "mailtemplatefile";

  /**
   * Rest web service path for fetching icdm disclaimer file
   */
  public static final String RWS_CDA_DISCLAIMER_FILE = "cdadisclaimerfile";

  /**
   * Rest web service path for pidc attribute updation
   */
  public static final String RWS_PIDC_ATTRS_CREATION = "projectattributecreation";
  /**
   * Rest web service path for pidc attribute movement
   */
  public static final String RWS_PIDC_ATTRS_MOVEMENT = "projectattributemovement";


  /**
   * Rest web service path for PIDC Variant ID
   */
  public static final String RWS_PIDC_VERSION_ID = "pidcversionid";

  /**
   * Rest web service path for PIDC a2l file ID
   */
  public static final String RWS_A2L_FILE_ID = "a2lfileid";

  /**
   * Rest web service path for get pidc variant
   */
  public static final String RWS_GET_PIDC_VARIANT_FOR_PIDC_VERS_N_A2L_ID = "pidcvariantbypidcversanda2lid";

  public static final String RWS_HAS_PIDC_VARIANT = "hasvariant";

  public static final String RWS_CONTEXT_RULE_DESC = "ruledescription";

  public static final String RWS_SSD_RULE_VALIDATION = "ssdrulevalidation";

  public static final String RWS_CONTEXT_SSD_RELEASE = "ssdrelease";

  public static final String RWS_CONTEXT_REVIEW_DATA = "reviewdata";

  public static final String RWS_QP_RULE_ID = "ruleid";

  public static final String RWS_GET_SINGLE_PARAM_RULES = "singleparamrule";
  public static final String RWS_GET_MANDATORY_RULE_SET_BY_PIDC = "mandatoryrulesetbypidcelement";
  public static final String RWS_GET_SSD_FILE_BY_PIDC = "ssdfilebypidcelement";

  /**
   *
   */
  public static final String RWS_GET_QNAIRE_VER_WITH_DETAILS = "allmappingdetails";
  /**
   *
   */
  public static final String RWS_GET_QUESTION_WITH_DETAILS = "getquestionwithdetails";

  /**
   *
   */
  public static final String RWS_QP_QNAIRE_VERS_ID = "qnaireversid";

  public static final String RWS_QP_QNAIRE_ID = "qnaireid";
  /**
   *
   */
  public static final String RWS_QP_QUESTION_ID = "questionid";

  /**
   *
   */
  public static final String RWS_QNAIRE_VERSION = "qnairevers";

  /**
   *
   */
  public static final String RWS_QNAIRE_ANSWER = "qnaireanswer";
  /**
   *
   */
  public static final String RWS_QNAIRE_ANSWER_OPL = "qnaireansweropl";

  /**
   *
   */
  public static final String RWS_GET_WORKING_SET = "getworkingset";
  /**
  *
  */
  public static final String RWS_CREATE_QNAIRE_MODEL = "newqnairemodel";

  /**
   *
   */
  public static final String RWS_GET_ALL_VERSIONS = "getallversions";


  /**
   * Query parameter constant for common params
   */
  public static final String RWS_QP_PARAM_ID = "paramId";


  /** The Constant RWS_GET_ALL_MAPPING. */
  public static final String RWS_GET_ALL_MAPPING = "getallmapping";

  /**
  *
  */
  public static final String RWS_QNAIRE_RESP_FROM_WP = "qnairerespfromwp";

  /**
  *
  */
  public static final String RWS_WP_ID_FROM_QNAIRE_RESP_ID = "wpidfromqnairerespid";


  /**
  *
  */
  public static final String RWS_RVW_QNAIRE_DETAILS_FROM_RESP_ID = "rvwqnairedetailsfromrespid";

  /**
   *
   */
  public static final String RWS_GET_QUEST_DEP_ATTR = "getquestdepattr";

  /**
  *
  */
  public static final String RWS_GET_QUEST_VERSION_MODEL_BY_QUES_ID = "getversionmodelbyquesid";

  /**
   *
   */
  public static final String RWS_GET_ALL_QUESTIONS_FOR_WORKING_SET = "getallquestionsforworkingset";

  /**
   *
   */
  public static final String RWS_GET_QUEST_DEP_ATTR_MAP = "getquestdepattrmap";
  /**
   * path for sub variant
   */
  public static final String RWS_PIDCVARIANT = "pidcVariant";
  /**
   * path for sub variant
   */
  public static final String RWS_PIDCSUBVARIANT = "pidcSubVariant";

  /**
   * path for getting variants for pidc version
   */
  public static final String RWS_VAR_FOR_VERSION = "getVariantsForVersion";

  /**
   * path for pidc variant attributes service
   */
  public static final String RWS_PIDCVARIANTATTRIBUTE = "pidcVarAttr";

  /**
   * path for getting variant attribute with variant id
   */
  public static final String RWS_GET_VAR_ATTR_FOR_VARIANT = "getVarAttrForVariant";

  /**
   * path for pidc sub var attribute
   */
  public static final String RWS_PIDCSUBVARIANTATTRIBUTE = "pidcsubvarAttr";

  public static final String RWS_PIDCVERSIONATTRIBUTE = "pidcverAttr";


  public static final String RWS_PARSE_FILE = "parsefile";

  public static final String RWS_WORKPACKAGE_STATUS = "workpackagestatus";

  public static final String RWS_ARCHIVE_WORKPACKAGE = "archivecompletedworkpackage";

  public static final String RWS_COMPLI_REVIEW_PIDC_A2LID = "complireviewwithpidca2lid";

  public static final String RWS_FP_PARAM_COL_ID = "parametercollectionid";

  public static final String RWS_FP_PARAM_COL_TYPE = "paramcollectiontype";


  public static final String RWS_QP_IMPORT_FILE = "importfile";

  public static final String RWS_CAL_DATA_IMPORT = "caldataimport";

  public static final String RWS_PIDC_IMPORT = "pidcimport";

  public static final String RWS_COMPARE_EXCEL_PIDC = "compareexcelpidc";

  public static final String PIDC_IMPORT_DATA = "pidcimportdata";

  /**
   * path for getting pidc details structure for pidc version
   */
  public static final String RWS_GET_DET_STRUCT_FOR_VERSION = "pidcDetStructForVersion";

  public static final String RWS_SAVE_CANCELLED_REVIEW = "savecancelledreview";

  public static final String RWS_UPDATE_CANCELLED_REVIEW = "updatecancelledreview";


  public static final String RWS_PERFORM_REVIEW = "performreview";

  public static final String RWS_UPDATE_REVIEW = "updatereview";

  public static final String RWS_PIDC_BY_NAMEVAL_ID = "pidcbynamevalid";

  public static final String RWS_PIDCUSERS_BY_ATTR_VAL_ID = "pidcuserbyattvalueid";

  public static final String RWS_A2L_MAPPED_VAR_FOR_VERSION = "a2lmappedvariants";

  public static final String RWS_GET_CAL_COMPARE_OBJ = "caldatacompare";

  public static final String RWS_SAVE_PARAMS_RULES = "saveparamrules";

  public static final String RWS_GET_QN_DEP_ATTR_MAP = "questdepattr";

  public static final String RWS_GET_ALL_QN_DEP_ATTR_VAL_MODEL = "allqndepattrvalmodel";

  public static final String RWS_GET_QN_DEP_ATTR_VAL_MODEL = "qndepattrvalmodel";

  public static final String RWS_VAR_COPY = "varCopy";

  public static final String RWS_VARNSUBVAR_COPY = "varNSubVarCopy";

  public static final String RWS_VAR_HAS_REVIEWS = "varHasReviews";

  public static final String RWS_LOAD_HEX_FILE = "loadhexfile";

  public static final String RWS_QP_PIDC_VERSION_ID = "pidcversionid";

  /**
   * Rest URL - external link
   */
  public static final String RWS_EXT_LINK = "externallink";

  /**
   * Protocol of external link
   */
  public static final String EXT_LINK_PROTOCOL = "icdm";

  /**
   * CDR service URL to get parameter instances for the given name
   */
  public static final String RWS_GET_PARAM_BY_NAME_ONLY = "parambynameonly";

  public static final String RWS_PARAM_NAMES = "paramnames";

  public static final String RWS_PRECAL_ATTR_VAL = "attrvals";

  public static final String RWS_PRECAL = "precal";
  /**
   * url path - vCDM data transfer
   */
  public static final String RWS_TRANSFER_VCDM_DATA = "pidcvcdmtransfer";

  public static final String RWS_PIDC_RVW_RESULT_INFO = "pidcrvwresultinfo";

  public static final String RWS_RESOLVE_FC2WP_NAME = "resolvefc2wpname";

  public static final String RWS_PIDC_DATA = "pidcdata";

  public static final String RWS_SUB_VAR_COPY = "subvarcopy";


  public static final String RWS_PIDC_VARIANTS_WITH_ATTRS = "variantswithattributes";

  public static final String RWS_RVW_VARIANT_BY_RESULT_N_VAR = "rvwvarbyresultnvar";

  public static final String RWS_VCDM_PST_CONTENT = "vcdmpstcontent";

  public static final String RWS_BY_A2L = "bya2lFile";

  public static final String RWS_GET_UC_EXPORT_DATA = "usecaseExportData";

  public static final String RWS_QP_HEX_FILE_NAME = "hexfilename";

  public static final String RWS_QP_VCDM_DST = "vcdmdst";

  public static final String RWS_QP_VCDM_VERS_ID = "vcdmversid";

  public static final String RWS_QP_REF_ID = "refid";

  public static final String RWS_QP_REPORT_TYPE = "reporttype";

  /** The Constant RWS_DOWNLOAD_REPORT. */
  public static final String RWS_DOWNLOAD_REPORT = "downloadreport";
  public static final String RWS_DOWNLOAD_ALL = "downloadall";
  public static final String CHECK_IS_NON_SDOM = "isnonsdom";


  /** The Constant RWS_QP_HEX_FILE_PATH. */
  public static final String RWS_QP_HEX_FILE_PATH = "hexfilepath";

  public static final String RWS_MAIL_TEMPLATE = "deletemailtemplate";

  public static final String RWS_CHECKSSD_COMPLI_FILE = "downloadcomplifile";

  public static final String RWS_COMPLI_FILE_PATH = "complifilepath";

  public static final String RWS_FILE_DOWNLOAD = "filedownload";

  public static final String RWS_IS_FM_WHILE_MAPPING = "fmmapwhileMapping";

  public static final String RWS_IS_FM_WHILE_UNMAPPING = "fmmapwhileUnMapping";

  public static final String RWS_ICDM_PIDC_REQUESTOR_FILE = "pidcrequestorfile";

  public static final String RWS_PIDC_REQUESTOR = "pidcrequestor";
  public static final String RWS_VALUE_DEP = "getvaluedependency";


  public static final String RWS_ALL_UNITS = "units";

  public static final String RWS_WEB_FLOW_DATA = "webflowdata";
  public static final String RWS_WEB_FLOW_ELEMENT = "webflowelement";

  public static final String RWS_GET_VAL_DEPN_LVL_ATTRS = "valdepnlvlattrs";


  public static final String RWS_COMP_HEX_META_DATA = "comphexmetadata";
  public static final String RWS_GET_MULTIPLE = "getmultiple";

  public static final String RWS_GET_ACTIVE_VERS_WITH_STRUCT_BY_OTHER_VER_IDS = "getactiveverswithstructurebyverid";

  public static final String RWS_GET_PIDC_VERSIONS_STATISTICS = "getpidcversionstatisticsreport";

  public static final String RWS_GET_PIDC_VARIANT_STATISTICS = "pidcvariantstatisticsreport";

  public static final String RWS_GET_ALL_PIDC_VERSIONS = "getallprojectidcardversions";

  public static final String RWS_PIDC_SDOM_PVER = "getPidcSdomPver";

  public static final String ICDM_SERVER_CONNECT_ERROR_MSG = "Could not connect to iCDM server";


  public static final String RWS_FP_PARAM_FUNC_VER = "funcationversion";

  /**
   * Rest web service path for A2lVariantGroup
   */
  public static final String RWS_A2L_VARIANT_GROUP = "a2lvariantgroup";

  /**
   * Rest web service path for A2lWpDefinitionVersion
   */
  public static final String RWS_A2L_WP_DEFINITION_VERSION = "a2lwpdefinitionversion";

  /**
   * Rest web service path for A2lVarGrpVarMapping
   */
  public static final String RWS_A2L_VAR_GRP_VAR_MAPPING = "a2lvargrpvarmapping";

  /**
   * Rest web service path for A2lWpResponsibility
   */
  public static final String RWS_A2L_WP_RESPONSIBILITY = "a2lwpresponsibility";

  public static final String RWS_A2L_WORK_PACKAGE = "a2lworkpackage";
  /**
   * Rest web service path for A2lWpResponsibility
   */
  public static final String RWS_WP_RESP = "wpresp";


  /**
   * Rest web service path for A2lWpParamMapping
   */
  public static final String RWS_A2L_WP_PARAM_MAPPING = "a2lwpparammapping";

  /**
   * Primary key of Workpackage Definition Version
   */
  public static final String RWS_QP_WP_DEFN_VERS_ID = "wpdefversid";
  /**
   * Rest web service path for A2lWpPAramMapping
   */
  public static final String RWS_GET_WP_PARAM_MAPPING_BY_WP_DEFN_VERS_ID = "wpparammappingbywpdefversid";

  /**
   * Rest Web Service path to update A2lWpPAramMapping
   */
  public static final String RWS_UPDATE_A2L_WP_PARAM_MAPPING = "updatea2lwpparammapping";

  /**
   * Rest Web Service path to delete A2lWpResponsibility
   */
  public static final String RWS_DELETE_A2L_WP_RESPONSIBILITY = "deletea2lwpreponsibility";

  /**
   * Rest Web Service path to delete A2lWpResponsibility
   */
  public static final String RWS_RESET_A2L_WP_PARAMS = "reseta2lwpparams";
  /**
   * Rest web service path for A2lWpPAramMapping
   */
  public static final String RWS_GET_ACTIVE_VERS_BY_PIDC = "activeversionbypidc";
  public static final String RWS_GET_PARAMETER_REVIEW_RESULT = "getparameterreviewresult";
  /**
   * Primary key of A2lVariantGroup
   */
  public static final String RWS_QP_A2L_WP_VARIANT_GROUP_ID = "a2lwpvariantgroupid";
  /**
   * Rest web service path for wp-resp import in PAL
   */
  public static final String RWS_A2LWPRESP_IMPORT = "importa2lwpresp";
  /**
   * Rest web service path for FC2WP import in PAL
   */
  public static final String RWS_IMPORT_FROM_FC2WP = "fc2wp";
  /**
   * Rest web service path for EXCEL import in PAL
   */
  public static final String RWS_IMPORT_FROM_EXCEL = "excel";
  /**
   * Rest web service path for EXCEL import in PAL
   */
  public static final String RWS_IMPORT_FROM_A2L_GROUP = "a2lgroup";

  public static final String RWS_GET_USECASES = "getUseCases";

  public static final String RWS_GET_USECASE_WITH_SECTION = "getUseCaseWithSectionTree";

  public static final String RWS_COPY_PAR2WP = "copypar2wp";

  public static final String RWS_TAKE_OVER_A2L = "takeovera2l";

  public static final String RWS_DEFAULT_WP_RESP_ASSIGN_EXIST = "defaultwpresplabelassignmentexist";

  public static final String RWS_PIDC_UC_ATTR = "pidcucmodel";

  public static final String RWS_WP_BY_PIDC_VERS = "wpbypidcversion";

  public static final String RWS_NEW_REVIEW_DETAILS = "newRvwDetails";
  /**
   * Delete items, when the internal operation is update
   */
  public static final String RWS_DELETE = "delete";
  /**
   * Fetch rules for parameter
   */
  public static final String RWS_RULES_FOR_PARAM = "rulesforparam";
  /**
   * Fetch rules for parameter with dependencies
   */
  public static final String RWS_RULES_FOR_PARAM_WITH_DEP = "rulesforparamwithdep";
  /**
   * Search
   */
  public static final String RWS_SEARCH = "rulesearch";
  /**
   * Rule history
   */
  public static final String RWS_RULE_HISTORY = "rulehistory";
  /**
   * Compli Rule history
   */
  public static final String RWS_COMPLI_RULE_HISTORY = "complirulehistory";
  /**
   * Check value rule
   */
  public static final String RWS_CHECK_VALUE_RULE = "checkvaluerule";

  /**
   * A2l Workpackage Id
   */
  public static final String A2L_WP_ID = "a2lwpid";
  /**
   * Variants mapped to sdom pver
   */
  public static final String RWS_SDOM_MAPPED_VAR_FOR_VERSION = "sdomMappedVariants";
  /**
   * Copy comments from another review result
   */
  public static final String RWS_COPY_COMMENT_FROM_RESULT = "copycommentfromresult";
  /**
   * Response header for compli review service to store additional info
   */
  public static final String RESP_HEADER_COMPLI_RVW_ID = "Compliance-Review-ID";

  public static final String RWS_GET_HELP_LINKS = "getHelpLinks";

  public static final String RESP_HEADER_SERVICE_ID = "Service-ID";

  public static final String RWS_QP_DETAIL = "detail";
  /**
   * Get link by pidc version uc id
   */
  public static final String RWS_GET_LINK_BY_PIDC_VER_UC_ID = "getlinkbypidvucid";

  public static final String RWS_PIDC_ATTR_UPDATE_EXTERNAL = "pidcAttrUpdateExternal";

  public static final String RWS_QP_PRJ_USE_CASE_CHECK = "projectUseCase";

  public static final String QP_SEPARATOR = "___";

  public static final String RWS_GET_ALL_FOR_USER = "getallforuser";

  public static final String RWS_GET_ALL_SPECIAL_ACCESS = "specialaccess";

  /**
   * Rest web service path for fetching cdfx export readiness condition file
   */
  public static final String RWS_CDFX_READINESS_FILE = "cdfxreadinessfile";

  /**
   * Rest web service path for getting serialized A2L file
   */
  public static final String RWS_SERIALIZED_A2L_FILE_INFO = "a2lfileinfoserialized";

  /**
   * URL path - A2L download
   */
  public static final String RWS_A2L_DOWNLOAD = "a2ldownload";

  /**
   * Rest web service for getting Review Result Editor data
   */
  public static final String RWS_GET_RVW_EDITOR_DATA = "editordata";

  /**
   * Rest web service for getting Combined Review Result Editor data for excel export
   */
  public static final String RWS_GET_COMBINED_RVW_EDITOR_DATA = "combinededitordata";

  /**
   * Rest web service path for getting cal data set for an a2l
   */
  public static final String RWS_VCDM_CALDATA = "vcdmcaldata";

  /**
   * Rest web service path for vcdm dst id
   */
  public static final String RWS_VCDM_DST_ID = "vcdmdstid";

  /**
   * Rest web service path for checking vCDM availability
   */
  public static final String RWS_VCDM_AVAILABILITY = "vcdmavailability";

  /**
   * Rest web service path for fetching vCDM APRJ
   */
  public static final String RWS_APRJ = "aprj";

  /**
   * Rest web service path for fetching serialized cal data from vCDM
   */
  public static final String RWS_SERIALIZED = "serialized";

  /**
   * REST path for Rule remarks services
   */
  public static final String RWS_RULEREMARKS = "ruleremarks";

  /**
   * Rest web service path for qnaire config val id
   */
  public static final String RWS_QNAIRE_CONFIG_VAL_ID = "qnaireconfigattrvalueid";

  /**
   * Rest web service path for validating coc wp mappings for pidc version
   */
  public static final String RWS_COC_WP_AVAILABLE = "cocwpavailableforpidcversion";

  /**
   * Rest web service path for qnaire config attr
   */
  public static final String RWS_QNAIRE_CONFIG_ATTR = "qnaireconfigattr";

  /**
   * Rest web service path to get Pidc Vers Attribute by Attribute Id
   */
  public static final String RWS_PIDC_VERS_ATTR_BY_ID = "pidcversattrbyid";

  /**
   * Query Parameter Pidc Vers Attribute Id
   */
  public static final String RWS_QP_PIDC_VERS_ATTR_ID = "pidcVersAttrId";

  /**
   * review variant id
   */
  public static final String RWS_QP_RVW_VAR_ID = "rvwvarid";

  /**
   * URL path - attach review result with questionnaire
   */
  public static final String RWS_ATTACH_RVW_RES_WITH_QNAIRE = "attachrvwresultwithqnaire";

  /**
   * Path to get A2L related objects
   */
  public static final String RWS_A2L_RELATED_OBJECTS = "unmapa2ldata";

  /**
   * Path to delete the entries that are related to a2l
   */
  public static final String RWS_DELETE_A2L_RELATED_OBJECTS = "deletea2ldata";

  /**
   * Path to know if general questionnaire is not required
   */
  public static final String RWS_IS_GEN_QUES_NOT_REQ = "isGenQuesReq";

  /**
   * Path to know if questionnaire version update is required
   */
  public static final String RWS_IS_QUES_VERSION_UPDATE_REQ = "qnaireversionupdatereq";

  public static final String RWS_QP_RESP_ID = "respId";

  public static final String RWS_DELETE_UNDELETE_QUES_RESP = "deleteundeletequesresp";

  /**
   * Rest path to import Work Packages from Functions
   */
  public static final String RWS_WP_IMPORT_FROM_FUNC = "wpimportfromfunctions";

  /**
   * Rest web service path to fetch CDR Details
   */
  public static final String RWS_CDR_DETAILS = "cdrdetails";

  public static final String RWS_CDR_LINK = "cdrlink";
  /**
   * Rest web service path for fetching CoC WP at PIDC level
   */
  public static final String RWS_PIDCVERSCOCWP = "pidcverscocwp";
  /**
   * Rest web service path for fetching CoC WP at Variant level
   */
  public static final String RWS_PIDCVARIANTCOCWP = "pidcvariantcocwp";
  /**
   * Rest web service path for fetching CoC WP at Sub Variant level
   */
  public static final String RWS_PIDCSUBVARCOCWP = "pidcsubvarcocwp";
  /**
   * Rest web service path for fetch all CoC WP by Pidc version Id
   */
  public static final String RWS_GET_ALL_COC_WP_BY_PIDC_VERS = "cocwpbypidcversid";
  /**
   * Rest web service path to handle update actions on COC WP UI page
   */
  public static final String RWS_UPDATE_COC_WP = "cocwpupdate";
  /**
   * Rest reusable relative path for external serices
   */
  public static final String RWS_EXTERNAL = "external";

  /**
   * Rest web service path for A2L Responsibility Bosch Group User
   */
  public static final String RWS_A2LRESPBOSCHGROUPUSER = "a2lrespboschgroupuser";

  /**
   * Rest web service path for A2L Responsibility Bosch Group User
   */
  public static final String RWS_CREATE_A2LRESP_BSHGRP_USERS = "createa2lrespboschgroupusers";

  /**
   * Rest web service path for A2L Responsibility Bosch Group User
   */
  public static final String RWS_A2LRESP_BSHGRP_USER_FOR_RESP = "a2lrespboschgroupuserforresp";

  /**
   * Rest web service path for A2L Responsibility Bosch Group User list deletion
   */
  public static final String RWS_DELETE_A2LRESP_BSHGRP_USERS = "deletea2lrespboschgroupusers";

  /**
   * List of IDs
   */
  public static final String RWS_QP_ID_LIST = "idlist";

  public static final String RWS_DEL_UCFAV_AND_UPD_COC_WP = "delucfavandupdcocwpusedflg";

  public static final String RWS_STRUCTURE_VALIDATION = "structurevalidation";

  public static final String RWS_QP_PIDC_NAME = "pidcName";

  public static final String RWS_QP_ACCESS_TYPE = "accessType";

  /**
   * Rest path to update the finished status of WP - Resp when new active version of WP definition is created for an A2l
   */
  public static final String RWS_UPDATE_WP_STATUS_FOR_WP_DEF_VER = "wpstatusforwpdefversmodification";

  /**
   * A2L Wp definision - Previous active version
   */
  public static final String RWS_PREVIOUS_WP_DEF_ACTIVE_VERS_ID = "previousWpDefnActiveVersId";

  /**
   * A2L Wp definision - New active version
   */
  public static final String RWS_NEW_WP_DEF_ACTIVE_VERS_ID = "newWpDefnActiveVersId";

  public static final String RWS_UPDATE_A2L_WP_RESPONSIBILITY_STATUS = "a2lwprespfinishedstatus";

  public static final String RWS_QP_ACTIVEFLAG = "activeFlag";
  /**
   * Rest path to Update status of selected wp resp from tree view
   */
  public static final String RWS_UPDATE_SEL_WP_STATUS = "updateselwprespstatus";
  /**
   * Rest path for creating emr data webservice
   */
  public static final String RWS_EMR_DATA = "emrdata";

  /**
   * Rest web service path for A2LWPResponsibilityStatus
   */
  public static final String RWS_A2LWPRESPONSIBILITYSTATUS = "a2lwpresponsibilitystatus";

  public static final String RWS_EXPORT_RULESET_AS_CDFX = "exportrulesetascdfxfile";

  /**
   * Rest web service path for getting Da Baselines
   */
  public static final String RWS_DA_BASELINES = "dabaselines";

  /**
   * Rest web service path for Data Assessment Baseline Id
   */
  public static final String RWS_DA_BASELINE_ID = "dabaselineid";

  public static final String RWS_DA_BASELINE = "dataassessmentbaseline";

  public static final String RWS_WPRESP_MODEL_FOR_VARGRP_DEFNVERSID = "wprespmodelforvargrpdefnversid";

  /**
   * Rest web service path to check if pidc focus matrix is empty
   */
  public static final String RWS_FOCUSMATRIX_EMPTY = "focusmatrixempty";

  public static final String RWS_SIMPLIFIED_QNAIRE_DECLARATION_FILE = "simplifiedqnairedeclarationfile";


  public static final String RWS_APICCREATEADGROUPACCESS = "createpidcgroupaccess";

  public static final String RWS_PIDCADGRPDETAIL = "createpidcaccessgroupname";
  public static final String RWS_PIDCADGRPSID = "createpidcaccessgroupsid";
  public static final String RWS_PIDCADGRPACCESSNODEID = "createpidcaccessnodeid";
  public static final String RWS_PIDCADGRPACCESSNODETYPE = "createpidcaccessnodetype";
  public static final String RWS_QP_AD_GROUP_ID = "adgroupid";

  public static final String RWS_ACTIVEDIRECTORYGROUP = "adgrp";
  /**
   * Rest web service path for TabvApicAdGrpNodeAccess
   */
  public static final String RWS_ACTIVEDIRECTORYGROUPNODEACCESS = "adgrpnodeaccess";
  /**
   * Rest web service path for TabvApicAdGrpUsersDetail
   */
  public static final String RWS_ACTIVEDIRECTORYGROUPUSER = "adgrpusers";
  /**
   * Rest web service path for AzureAuthService
   */
  public static final String RWS_OAUTH_ACCEPT_TOKEN = "acceptToken";
  /**
   * Rest web service path to Get User Token
   */
  public static final String RWS_OAUTH_USER_TOKEN = "usertoken";
  /**
   * Query param for Azure Authorization Code
   */
  public static final String RWS_AZURE_AUTH_CODE = "code";
  /**
   * Query param for client state
   */
  public static final String RWS_CLIENT_STATE = "state";
  /**
   * Rest web service path to store UserLoginInfo
   */
  public static final String RWS_USERLOGININFO = "userlogininfo";
  /**
   * Rest web service path to get Quotation relevant attributes
   */
  public static final String RWS_GET_QUO_REL_ATTR_BY_MCR_ID = "getquotationrelattrbymcrid";
  /**
   * Rest web service Query param mcrId
   */
  public static final String RWS_QP_MCR_ID = "mcrid";
  /**
   * Rest web service path to Sync all ActiveDirectoryGroups with LDAP Data
   */
  public static final String RWS_SYNC_ACTIVEDIRECTORYGROUPUSERS = "syncgroupusers";

  /**
   * Rest Web service path to get CDR Report Model
   */
  public static final String RWS_CDR_REPORT_MODEL = "cdrreportmodel";

  /**
   * Rest Web service path to get user preferences by user id
   */
  public static final String RWS_USER_PREF_BY_USER_ID = "userprefbyuserid";

  /**
   * Rest web service Query param userid
   */
  public static final String RWS_USER_ID = "userid";

  /**
   * Rest Web service path to get user preferences
   */
  public static final String RWS_USERPREFERENCE = "userpreference";

  /**
   * Rest Web service path to get multiple review result validation
   */

  public static final String RWS_RVW_CAN_MUL_DELETE_VAL = "multiplereviewresultdeletevalidation";

  /**
   * Rest Web service path to delete multiple review result validation
   */

  public static final String RWS_RVW_MUL_DEL = "mutiplereviewresultdelete";

  /**
   * Private constructor
   */

  private WsCommonConstants() {
    // Private constructor
  }
}
