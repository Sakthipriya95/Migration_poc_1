/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.rules.bo;

import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import com.bosch.caltool.apic.jpa.bo.Attribute;
import com.bosch.caltool.apic.jpa.bo.ISortColumn;
import com.bosch.caltool.apic.jpa.bo.ParameterClass;
import com.bosch.caltool.icdm.common.exception.SsdInterfaceException;
import com.bosch.ssd.icdm.model.CDRRule;


/**
 * Interface for Parameter
 *
 * @author adn1cob
 * @param <P> obj extending AbstractParameterAttribute
 */
public interface IParameter<P extends IParameterAttribute> {

  /**
   * Constant for exact match-yes
   */
  static final String EXACT_MATCH_YES = "Yes";
  /**
   * Constant for exact match-no
   */
  static final String EXACT_MATCH_NO = "No";

  /**
   * Parameter properties columns
   */
  public enum SortColumns implements ISortColumn {
                                                  /**
                                                   * Parameter Name
                                                   */
                                                  SORT_PARAM_NAME,
                                                  /**
                                                   * Parameter Long Name
                                                   */
                                                  SORT_PARAM_LONGNAME,
                                                  /**
                                                   * Class
                                                   */
                                                  SORT_PARAM_CLASS,
                                                  /**
                                                   * CodeWord
                                                   */
                                                  SORT_PARAM_CODEWORD,
                                                  /**
                                                   * LowerLimit
                                                   */
                                                  SORT_PARAM_LOWERLIMIT,
                                                  /**
                                                   * Reference Value
                                                   */
                                                  SORT_PARAM_REFVALUE,
                                                  /**
                                                   * UpperLimit
                                                   */
                                                  SORT_PARAM_UPPERLIMIT,
                                                  /**
                                                   * UpperLimit
                                                   */
                                                  SORT_PARAM_BITWISE,
                                                  /**
                                                   * Ready for series
                                                   */
                                                  SORT_PARAM_READY_SERIES,

                                                  /**
                                                   * Unit
                                                   */
                                                  SORT_PARAM_UNIT,
                                                  // ICDM-1173
                                                  /**
                                                   * Exact match
                                                   */
                                                  SORT_EXACT_MATCH,

                                                  // ICDM-2152
                                                  /**
                                                   * Remarks
                                                   */
                                                  SORT_REMARKS;
  }


  // new enumeration for compliance
  public enum SSD_CLASS {

                         /**
                          * compliance
                          */
                         COMPLIANCE("COMPLIANCE"),
                         /**
                          * monitoring
                          */
                         MONITORING("MONITORING"),
                         /**
                          * component
                          */
                         COMPONENT("COMPONENT"),
                         /**
                          * cust spec
                          */
                         CUSTSPEC("CUSTSPEC"),
                         /**
                          * not in ssd
                          */
                         NOT_IN_SSD("NOT_IN_SSD");

    private final String dbType;


    /**
     * @param dbType
     * @param compliant
     */
    private SSD_CLASS(final String dbType) {
      this.dbType = dbType;
    }

    /**
     * default not in ssd
     *
     * @param dbType dbType
     * @return the ssd class
     */
    public static SSD_CLASS getSsdClass(final String dbType) {
      for (SSD_CLASS ssdclass : SSD_CLASS.values()) {
        if ((dbType != null) && ssdclass.dbType.equals(dbType)) {
          return ssdclass;
        }
      }
      return NOT_IN_SSD;
    }

    /**
     * @return true if it is compliant
     */
    public boolean isCompliant() {
      return this == SSD_CLASS.COMPLIANCE;
    }

  }


  /**
   * @return the param-attrs for a Cdr paramter
   * @throws SsdInterfaceException
   */
  Set<P> getParamAttrs() throws SsdInterfaceException;

  /**
   * @return Attributes set
   */
  Set<Attribute> getAttributes() throws SsdInterfaceException;

  /**
   * @return German name
   */
  String getLongNameGer();

  /**
   * @return English name
   */
  String getLongNameEng();

  /**
   * @return PClass
   */
  ParameterClass getpClass();

  /**
   * @return code word
   */
  boolean isCodeWord();

  /**
   * @return bitwise
   */
  boolean isBitWise();

  /**
   * @return CDRRules
   * @throws SsdInterfaceException
   */
  List<CDRRule> getReviewRuleList() throws SsdInterfaceException;

  /**
   * @return paramater type
   */
  String getType();

  /**
   * @return param hint
   */
  String getParamHint();

  /**
   * @return the Code Word String
   */
  String getCodeWord();

  /**
   * @return the bitwise string
   */
  String getBitWiseRule();

  /**
   * @return Description
   */
  String getDescription();

  /**
   * Get the review rule of this parameter. This will return a rule if exists and satisfies the below conditions <br>
   * 1. parameter has no dependencies <br>
   * 2. Rule is present <br>
   * 3. Rule has no dependencies
   *
   * @return CDRRule rule object
   * @throws SsdInterfaceException
   */
  CDRRule getReviewRule() throws SsdInterfaceException;

  /**
   * @return the default Rule if the param has dependency and the rule does not have dependency.
   * @throws SsdInterfaceException
   */
  CDRRule getDefaultRule() throws SsdInterfaceException;

  /**
   * @return This method is used to check if there already exists a rule with an attribute mapping
   * @throws SsdInterfaceException
   */
  boolean canModifyAttributeMapping() throws SsdInterfaceException;

  /**
   * @return true if parameter has dependencies
   * @throws SsdInterfaceException
   */
  boolean hasDependencies() throws SsdInterfaceException;

  /**
   * ICDM-1113
   *
   * @return the longName
   */
  String getLongName();

  /**
   * @return the text representation of class
   */
  String getpClassText();

  /**
   * Get sorted rules (sorting based on attr) for given parameter
   *
   * @return sorted rule set
   */
  SortedSet<CDRRule> getRulesSet();

  /**
   * @param param2 param2
   * @param sortColumn sortColumn
   * @return int
   * @throws SsdInterfaceException
   */
  int compareTo(final IParameter<P> param2, final SortColumns sortColumn) throws SsdInterfaceException;

  /**
   * @return name
   */
  String getName();

  /**
   * Get rules model for this parameter.
   *
   * @return rules model for this parameter
   */
  ParamRulesModel getParamRulesModel();


  /**
   * @return the ssd class
   */
  String getSsdClass();

  /**
   * @return the ssd class enum
   */
  SSD_CLASS getSsdClassEnum();

  /**
   * @return if the param is compliant
   */
  boolean isComplianceParameter();


  /**
   * @return the id
   */
  Long getID();


}
