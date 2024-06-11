/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.general;

/**
 * @author BNE4COB
 */
public enum CommonParamKey {

                            /**
                             * Attribute ID which defines if the automatic nightly compliance check is enabled
                             */
                            AUTOMATIC_COMPLI_CHECK_ENABLED,
                            /**
                             * Attribute ID for recipients of automatic nightly compliance check
                             */
                            AUTOMATIC_COMPLI_CHECK_RCPT,
                            /**
                             * A user needs special access rights to start CalDataAnalayzer in ICDM
                             */
                            CALDATAANALYZER_NODE_ID,
                            /**
                             * A user needs special access rights to Access Admin Page in ICDM
                             */
                            ADMIN_ACCESS_NODE_ID,
                            /**
                             * File id for CDA disclaimer
                             */
                            CDA_DISCLAIMER_FILE_ID,
                            /**
                             * Mandatory Ruleset Attribute ID
                             */
                            CDR_MANDATORY_RULESET_ATTR_ID,
                            /**
                             * Maximum number of parameters that can be processed for CDR
                             */
                            // ICDM-715
                            CDR_MAX_PARAM_COUNT,
                            /**
                             * Base URL of CNS Server
                             */
                            CNS_SERVER_URL,
                            /**
                             * The compliance ssd classes of TParameter table in comma seperated values
                             */
                            // Task 263282
                            COMPLI_CLASS_TYPE,
                            /**
                             * A2L/HEX Download in Compliance Review Report: Link to APEX Application
                             */
                            COMPLI_FILES_APEX_LINK,

                            /**
                             * Link to PS Codex Reporting
                             */
                            COMPLI_REPORT_CODEX_LINK,

                            /**
                             * To address of users seperated by semicolon for sending Compliance review issue mail
                             */
                            COMPLI_ISSUE_REPORT_TO,

                            /**
                             * A special access rights for users to access Download and Import functionality in
                             * Compliance Review Dialog
                             */
                            COMPLI_REPORT_ACCESS_NODE_ID,

                            /**
                             * A2L/HEX Download in Compliance Review Report: Person that is responsible for the process
                             */
                            COMPLI_FILES_OWNER,
                            /**
                             * A2L/HEX Download in Compliance Review Report: Time to retain files in days
                             */
                            COMPLI_FILES_RETENTION_DAYS,
                            /**
                             * Maximum fetch size of database fetches
                             */
                            DB_SEARCH_MAX_RESULT_SIZE,
                            /**
                             * Template for notifying the Project owners when a attribute value is deleted
                             */
                            // ICDM-2217 , ICDM-2300
                            DELETE_ATTR_VAL_MAIL_TEMPLATE,
                            /**
                             * File id for iCDM disclaimer
                             */
                            DISCLAIMER_FILE_ID,
                            /**
                             * Validity period in days of acceptance of disclaimer by a user
                             */
                            // Task 244427
                            DISCLAIMER_VALID_INTERVAL,
                            /**
                             * Attribute Values of divisions for which questionnaries are applicable(comma separated)
                             */
                            DIVISIONS_WITH_QNAIRES,
                            /**
                             * A special access rights for users to access Emmisssion robustness Page
                             */
                            EMR_NODE_ID,
                            /**
                             * Show or hide focus matrix for PIDC without Focus Matrix entries.
                             */
                            FOCUS_MATRIX_ENABLED,
                            /**
                             * General Questionnaire ID
                             */
                            GENERAL_QNAIRE_ID,
                            /**
                             * A2L Group Mapping ID
                             */
                            GROUP_MAPPING_ID,
                            /**
                             * iCDM Client's current version
                             */
                            ICDM_CLIENT_VERSION,
                            /**
                             * Links to iCDM Contacts page
                             */
                            ICDM_CONTACTS_LINK,
                            /**
                             * iCDM Environment Code
                             */
                            ICDM_ENV_CODE,
                            /**
                             * Subject content for ICDM attribute clearing request Hotline mail
                             */
                            ICDM_ATTR_CLEARING_SUBJECT,
                            /**
                             * File id for ICDM clearing hotline mail template
                             */
                            ICDM_HOTLINE_TEMPL_FILE,
                            /**
                             * To address of ICDM Hotline
                             */
                            ICDM_HOTLINE_TO,
                            /**
                             * File id for ICDM PIDC Requestor file template
                             */
                            ICDM_PIDC_REQUESTOR_TEMPL_FILE,
                            /**
                             * Link to iCDM Wiki page
                             */
                            ICDM_WIKI_LINK,

                            /**
                             * Mail subject for new PIDC
                             */
                            // ICDM-1117
                            MAIL_NEW_PIDC,
                            /**
                             * Status of Automatic Mail Notifications from iCDM
                             */
                            // ICDM-946
                            MAIL_NOTIFICATION_ENABLED,
                            /**
                             * Status of Mail to address for invalid AD Grp information
                             */
                            ICDM_AD_GRP_MAIL_ENABLED,
                            /**
                             * Mail to address for invalid AD Grp information
                             */
                            ICDM_INVALID_AD_GRP_MAIL_TO,
                            /**
                             * Level attribute that defines mandatory attr list
                             */
                            MANDATORY_LEVEL_ATTR,
                            /**
                             * Icdm Questionnaire Config attribute for PIDC
                             */
                            ICDM_QNAIRE_CONFIG_ATTR,
                            /**
                            * Share Point Archival Url attribute for PIDC
                            */
                            SHAREPOINT_ARCHIVAL_URL_ATTR,
                            /**
                             * Value ID of 'BEG CAL project' in 'Project Organisation' attribute
                             */
                            BEG_CAL_PROJ_ATTR_VAL_ID,
                            /**
                             * Minimum version of iCDM that is allowed for PIDC import
                             */
                            // ICDM-2229
                            PIDC_IMPORT_VERSION,
                            /**
                             * Values IDs to be excluded from search for Level 1 Attribute during PIDC Search.(comma
                             * separated)
                             */
                            PIDC_SEARCH_EXCLUDE_VAL_LEVEL1,
                            /**
                             * Group id for the dummy attribute that will be created for showing up to date information
                             * in Web service
                             */
                            // Task 242055
                            PIDC_UP_TO_DATE_ATTR_GROUP_ID,
                            /**
                             * The time interval in days for a pidc version to be confirmed as up to date
                             */
                            // Task 242053
                            PIDC_UP_TO_DATE_INTERVAL,
                            /**
                             * Only Special Users with this ID can get the complete history information for
                             * Pre-Calibration data from Review Rules (Common Rules)
                             */
                            PRE_CAL_FULL,
                            /**
                             * This attribute defines the status of PIDC
                             */
                            QUOT_ATTR_ID,

                            /**
                             * This attribute defines value ids of all quotation related values
                             */
                            QUOT_VALUE_IDS,

                            /**
                             * Value-IDs for which the PIDC should not be shown
                             */
                            QUOT_VALUE_HIDDEN_STATUS,
                            /**
                             * Show or hide risk evaluation for PICDs without filled evaluation sheets
                             */
                            RISK_EVALUATION_ENABLED,
                            /**
                             * SSD NodeId for getting Component Package Data
                             */
                            SSD_COMP_PKG_NODE_ID,
                            /**
                             * SSD Node ID for compliance rules
                             */
                            // ICDM-2440
                            SSD_COMPLI_RULE_NODE_ID,
                            /**
                             * SSD NodeId to store review rules
                             */
                            SSD_NODE_ID,
                            /**
                             * Attribute to map SSD Software Project in PIDC
                             */
                            // Task 242053
                            SSD_PROJ_NODE_ATTR_ID,
                            /**
                             * Support DashBoard Link
                             */
                            SUPPORT_DASHBOARD_LINK,
                            /**
                             * Visibility of Support Dashboard link
                             */
                            SUPPORT_DASHBOARD_VISIBILITY,
                            /**
                             * some comment
                             */
                            TIP_OF_THE_DAY_FILE_ID,
                            /**
                             *
                             */
                            CDFX_100_DELIVERY_UC_IDS,
                            /**
                             * The time interval in days for a usecase to be confirmed as up to date
                             */
                            // Task 274996
                            USECASE_UP_TO_DATE_INTERVAL,
                            /**
                             * Variant in Customer Calibration Data Management System Attribute ID
                             */
                            VARIANT_IN_CUST_CDMS_ATTR_ID,
                            /**
                             * Defines if the trigger that checks if attribute values can be renamed is active.
                             */
                            VCDM_REN_TRIGGER_ACTIVE,
                            /**
                             * The URL for the web services that checks if an attribute/value is existing in vCDM and
                             * thus cannot be renamed
                             */
                            VCDM_REN_TRIGGER_URL,
                            /**
                             * Status of context menu for WebFlow Job in PIDC structure view
                             */
                            // ICDM-2408
                            WEB_FLOW_JOB_CREATION_ACTIVE,
                            /**
                             * Link for webflow job
                             */
                            // ICDM-2408
                            WEB_FLOW_JOB_LINK,
                            /**
                             * Flag to display Start webFLOW Job option for multiple variants
                             */
                            WEB_FLOW_JOB_MUL_VAR_ACTIVE,
                            /**
                             * Attribute Ids which needs to be validated in webflow job for multiple variants
                             */
                            WEB_FLOW_UNIQUE_ATTR,
                            /**
                             * Alias id for the web flow
                             */
                            WEBFLOW_ALIAS_ID,
                            /**
                             * File id for loading the startup page
                             */
                            WELCOME_FILE_ID,

                            /**
                             * File id of OSS document
                             */
                            OSS_FILE_ID,
                            /**
                             * Attribute to define WP root group
                             */
                            WP_ROOT_GROUP_ATTR_ID,
                            /**
                             * Work Package Type Attribute
                             */
                            WP_TYPE_ATTR_ID,
                            /**
                             * Standard root group for workpackages
                             */
                            WP_ROOT_GROUP,
                            /**
                             * Standard root group for responsibilities
                             */
                            RESP_ROOT_GROUP,
                            /**
                             * List of work directories of all servers in this server group(DEV/BETA/PRO etc.). Multiple
                             * paths are separated by semicolon(;)','\\si-cdm02.de.bosch.com\iCDM_WebService\ID_07\work
                             */
                            SERVER_GROUP_WORK_PATHS,
                            /**
                             * List of charset with key value pair seperated by ':'. Key is encoding available in
                             * notepad++. Value is proper encoding string.Each charset is seperated by ';'
                             */
                            FILE_ENCODING_CHARSET,

                            /**
                             * Relative path to vCDMStudio installation
                             */
                            VCDMSTUDIO_EXE_REL_PATH,

                            /**
                             * Users who have access to attribute update service
                             */
                            ATTR_UPDATE_ACCESS_USERS,

                            /**
                             * 'Review Document Subproject Calibration/PSR-C Link' attribute id
                             */
                            REVIEWDOC_PSRC_LINK_ATTR_ID,

                            /**
                             * Link to PS-EC Codex Reporting Tool for active C-SSD deviation with �json file�
                             */
                            COMPLI_REPORT_CSSD_CODEX_LINK,

                            /**
                             * Check the level of 2FA validation - whether to block the user or warn or not to validate
                             */
                            TWOFA_CHECK_LEVEL,

                            /**
                             * iCDM File Deutsch Node's ID for CDFx Readiness info in 100% CDFx export wizard
                             */
                            CDFX_READINESS_FILE_NODE_DE_ID,
                            /**
                             * iCDM File English Node's ID for CDFx Readiness info in 100% CDFx export wizard
                             */
                            CDFX_READINESS_FILE_NODE_EN_ID,

                            /**
                             * Attribute id of 'Load FC2WP assignments in A2L files'
                             */
                            LOAD_FC2WP_IN_A2L_ATTR,

                            /**
                             * Mail subject for unmapping A2L request
                             */
                            UNMAP_A2L_SUBJECT,
                            /**
                             * Division ids for which questionnaires need to be filled for review locking
                             */
                            DIV_IDS_FOR_REVIEW_LOCK_CHECK,

                            /**
                             * Visibility of Tool support Bot View (Citi Bot)
                             */
                            ICDM_TOOL_SUPPORT_VISIBILITY,
                            /**
                             * Allow exceptions when checking for same attribute values when linking reviews to other
                             * variants
                             */
                            RVW_ATTR_IDS_NOT_REL_FOR_LINK,

                            /**
                             * Attribute id of 'Fuel type'
                             */
                            FUELTYPE_ATTR_ID,

                            /**
                             * Attribute id of 'Customer/Brand'
                             */
                            CUSTOMER_OR_BRAND_ATTR_ID,
                            /**
                             * OBD General Questionnaire ID
                             */
                            OBD_GENERAL_QNAIRE_ID,
                            /**
                             * Division id's for which OBD option enabled in Review Wizard
                             */
                            DIVISIONS_WITH_OBD_OPTION,
                            /**
                             * Division id's for which simplified questionnaries are applicable
                             */
                            DIV_WITH_SIMPL_GEN_QNAIRE,
                            /**
                             * Node Id of Simplified General Questionnaire Declaration File in English
                             */
                            SIMP_QNAIRE_DECLAR_NODE_EN_ID,
                            /**
                             * Node Id of Simplified General Questionnaire Declaration File in German
                             */
                            SIMP_QNAIRE_DECLAR_NODE_ID,
                            /**
                             * WP Archival & Data Assessment Archival Folder
                             */
                            DATAASSESSMENT_ARCHIVAL_FOLDER,
                            /**
                             * Thread count for WP Archival Multi-Threading
                             */
                            WP_ARCHIVAL_THREAD_CNT,
                            /*
                             * Complex rule validation api url
                             */
                            COMPLEX_RULE_VALIDATION_URL,
                            /**
                             * Authorization URL for iCDM Azure AD
                             */
                            ICDM_AZURE_AUTH_URI,
                            /**
                             * Client Id of iCDM app Registration in Azure
                             */
                            ICDM_AZURE_AUTH_CLIENT_ID,
                            /**
                             * Grant type used in Azure
                             */
                            ICDM_AZURE_SCOPE,
                            /**
                             * Max no. of retry attempts to fetch the azure token
                             */
                            ICDM_AZURE_MAX_RETRY,
                            /**
                             * Retry interval for checking if token is available in milli seconds
                             */
                            ICDM_AZURE_RETRY_INTERVAL;


  private String paramName;

  CommonParamKey() {
    this.paramName = name();
  }

  /**
   * @return key
   */
  public String getParamName() {
    return this.paramName;
  }

  /**
   * Convert the parameter name to the enum constant
   *
   * @param name param key
   * @return enum value
   * @throws IllegalArgumentException if key is invalid
   */
  public static CommonParamKey getType(final String name) {
    for (CommonParamKey val : CommonParamKey.values()) {
      if (val.getParamName().equals(name)) {
        return val;
      }
    }

    throw new IllegalArgumentException("Invalid parameter name '" + name + "'");
  }

}

