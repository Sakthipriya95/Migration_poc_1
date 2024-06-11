/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.ssd.icdm.model;

/**
 * All positive numbers are success messages and all negative numbers are error messages
 *
 * @author HSU4COB
 */
public enum SSDMessage {
                        /**
                         * success message
                         */
                        SUCCESS(0, "Operation successful"),
                        /**
                         * Label is created successfully.
                         */
                        LABELCREATED(1, "Label created successfully"),
                        /**
                         * nodes identified for all bc varinats configured
                         */
                        NODECONFIGSUCCESS(2, "Node configured  successful"),
                        /**
                         * nodes identified for all bc varinats configured
                         */
                        CONTINUERELEASE(3, "Release can be continued.."),
                        /**
                         * nodes identified for all bc varinats configured
                         */
                        RELEASECREATED(4, "Release creation successful"),
                        /**
                         * For ICDM, when reading & fethcing SSDFile, when both are valid & available
                         */
                        RULE_AND_FILE_AVL(5, "Both Rules & File is fetched"),
                        /**
                         * if review rule already exist for given parameter
                         */
                        RULEEXIST(-1, "Review rule already exist."),
                        /**
                         * Label type provided is not valid
                         */
                        TYPEERROR(-2, "Parameter type is not supported."),
                        /**
                        *
                        */
                        NORULETOUPDATE(-3, "No rule present to update"),
                        /**
                        *
                        */
                        MISSINGVALUES(-4, "Label creation failed because Function or Value type not provided"),
                        /**
                        *
                        */
                        INVALIDLABEL(-5, "Invalid characters found in label name"),
                        /**
                         * Out dated rule
                         */
                        OUTDATEDRULE(-6, "Out dated rule. Latest version for the rule available"),
                        /**
                         * Field not supported (eg:- min.max are not applicable for val_blk, ascii, axis_pts)
                         */
                        INVALIDFIELD(-7, "Fields not supported for the label types (eg:-min.max)"),
                        /**
                         * Min value greater than max
                         */
                        MINMAXFAILURE(-8, "Min value greater than max value"),
                        /**
                         * Label already exists in SSD
                         */
                        LABELEXISTS(-9, "Label already exists in LDB"),
                        /**
                         * First letter should be a character in a label
                         */
                        FIRSTLETTERCHARACTER(-10, "First letter should be a character in a label"),
                        /**
                         * Label contains more number of underscores
                         */
                        UNDERSCORESLABEL(-11, "Label contains more number of underscores"),
                        /**
                         * Label has to be represented with a 'DOT' eg:Test.test1'
                         */
                        DOTLABEL(-12, "'Label has to be represented with a 'DOT' eg:Test.test1'"),
                        /**
                         * Label validation failed
                         */
                        LABELVALIDATIONFAILED(-13, "Label validation failed"),
                        /**
                         * only VALUE, VAL-BLK,ASCII, typ and subtyp provided
                         */
                        VALUETYPNOTSUPPORTED(-14, "New labels of the given type not supported"),
                        /**
                         * only VALUE, VAL-BLK,ASCII, typ and subtyp provided
                         */
                        SSDFIELDMISSING(-15, "When Unit is specified, min,max or dcm value has to be present"),
                        /**
                         * lock not available
                         */
                        FAILEDTOLOCK(-16, "Failed to acquire lock for release"),
                        /**
                         * ssd node could not be identified for bc version mentioned
                         */
                        SSDNODEMISSING(-17, "Mapping SSD node for BC version missing"),
                        /**
                         * componenet node on which release has to be done is not set
                         */
                        COMPNODENOTSET(-18, "Component node not set"),
                        /**
                         * label list creation failed
                         */
                        LABELLISTCREATIONFAILED(-19, "Label list creation failed"),
                        /**
                         * valid label list not found in one of the configured nodes
                         */
                        VALIDLISTNOTFOUND(-20, "Failed to find valid label list for configured BC version"),
                        /**
                         * Release can not be continued as previous operations failed
                         */
                        CANTCONTINUERELEASE(-21, "Release not cleared for processing"),
                        /**
                         * Rule can not be defined for given dependency as it exists
                         */
                        FEATUREVALUEEXISTS(-22, "Rule for the same combination of the dependency already exists"),
                        /**
                         * Default rule exists
                         */
                        DEFAULTRULEEXISTS(-23, "Default record configured already"),
                        /**
                         * Default rule exists
                         */
                        NOTLATESTRULE(-24, "Rule can not be deleted as a more latest revision exists"),
                        /**
                         * No rule exists
                         */
                        RULENOTEXISTS(-25, "No rule is available for deleting"),
                        /**
                         * maturity value is not valid
                         */
                        INVALIDMATURITYLEVEL(-26, "Maturity level is invalid"),
                        /**
                         * Feature cannot be repeated
                         */
                        FEATUREDUPLICATED(-27, "Feature has been already used"),
                        /**
                         * Value id present in v_ldb2_values
                         */
                        VALUEIDNOTPRESENT(-28, "Value is not present in SSD"),
                        /**
                         * Feature id present in v_ldb2_features
                         */
                        FEATUREIDNOTPRESENT(-29, "Feature is not present in SSD"),
                        /**
                         * Feature id present in v_ldb2_features
                         */
                        COMPRULENOTPOSSIBLE(-30, "Not possible to define Cal4Comp rule on selected node"),
                        /**
                         * unit is not set for label list creation
                         */
                        UNITNOTPRESENT(-31, "Unit not set for the rule"),
                        /**
                         * compliamnce node on which release has to be done is not set
                         */
                        COMPLINODENOTSET(-32, "Compliance node not set"),
                        /**
                         * To check if Structure or union desn not exist and create the label first to proceed
                         */
                        CREATESTRUCTURELABELANDPROCEED(-33, "Create Label and proceed"),
                        /**
                         * Featur Value Model is null 
                         */
                        NULLFEAVAL(-34, "Feature Value Model is Null"),
                        /**
                         * For ICDM, when reading & fethcing SSDFile, if either or both not available
                         */
                        NO_RULES_FETCHED(-35, "No Rules Fetched for given inputs"),
                        /**
                         * BC Nodes is null or empty
                         */
                        NULLBC(-36, "BCs List cannot be empty"),

                        /**
                         * General error when review creation fails
                         */
                        REVIEWRULECREATIONFAILED(-37, "Review Rule Creation Failed"),
                        /**
                         * Labellist is null or empty
                         */
                        EMPTYORNULLLABELLIST(-38, "Labellist is empty or Null"),
                        /**
                         * BC Nodes is null or empty
                         */
                        EMPTYORNULLBC(-39, "BC nodes is empty or Null");


  private final int code;
  private String description;

  private SSDMessage(final int code, final String description) {
    this.code = code;
    this.description = description;
  }

  /**
   * @param newDesc - to append
   * @return - desc
   */
  public SSDMessage appendDescription(final String newDesc) {
    if (newDesc != null) {
      this.description = this.description + "." + newDesc;
    }
    return this;
  }

  /**
   * @return - desc
   */
  public String getDescription() {
    return this.description;
  }

  /**
   * @return -
   */
  public int getCode() {
    return this.code;
  }

  @Override
  public String toString() {
    return this.code + ": " + this.description;
  }

}
