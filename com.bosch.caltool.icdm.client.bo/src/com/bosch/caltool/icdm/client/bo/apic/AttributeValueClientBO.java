/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.apic;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import com.bosch.calcomp.adapter.logger.Activator;
import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.AttrNValueDependency;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.attr.PredefinedAttrValue;
import com.bosch.caltool.icdm.model.apic.attr.PredefinedValidity;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;
import com.bosch.caltool.icdm.model.general.Link;
import com.bosch.caltool.icdm.model.util.ModelUtil;
import com.bosch.caltool.icdm.ws.rest.client.apic.AttrNValueDependencyServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.AttributeServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.PredefinedAttrValueServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.PredefinedValidityServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.icdm.ws.rest.client.general.LinkServiceClient;

/**
 * @author dmo5cob
 */
public class AttributeValueClientBO implements Comparable<AttributeValueClientBO> {

  /**
   * Sort columns
   */
  public enum SortColumns {
                           /**
                            * Value column
                            */
                           SORT_ATTR_VAL,
                           /**
                            * Unit column
                            */
                           SORT_ATTR_VAL_UNIT,
                           /**
                            * Description column
                            */
                           SORT_ATTR_VAL_DESC,
                           /**
                            * Status column
                            */
                           SORT_ATTR_CLEAR_STATUS,

                           /**
                            * Icdm-955 sort char value
                            */
                           SORT_CHAR_VALUE
  }

  /**
   * Icdm-830 Data Model changes for New Column Clearing status
   *
   * @author rgo7cob enumeration for the Clearing status
   */
  public enum CLEARING_STATUS {
                               /**
                                * cleared Value
                                */
                               CLEARED("Y", "Cleared"),
                               /**
                                * not cleared Value
                                */
                               NOT_CLEARED("N", "Not Cleared"),
                               /**
                                * In clearing
                                */
                               IN_CLEARING("I", "In Clearing"),

                               /**
                                * Deleted Status - Icdm-1180 new Status
                                */
                               DELETED("D", "Deleted"),
                               /**
                                * Rejected Icdm-1180 new Status
                                */
                               REJECTED("R", "Rejected");

    /**
     * DB Text for clearing status
     */
    private final String dbText;

    /**
     * UI text for clearing status
     */
    private final String uiText;


    /**
     * Constructor
     *
     * @param dbText DB text
     * @param uiText UI display text
     */
    CLEARING_STATUS(final String dbText, final String uiText) {
      this.dbText = dbText;
      this.uiText = uiText;
    }

    /**
     * @return the text
     */
    public String getDBText() {
      return this.dbText;
    }

    /**
     * @return the ui text
     */
    public String getUiText() {
      return this.uiText;
    }

    /**
     * @param dbText dbType
     * @return the Enum For the Cleraing Status
     */
    public static CLEARING_STATUS getClearingStatus(final String dbText) {

      for (CLEARING_STATUS clrStatus : CLEARING_STATUS.values()) {
        if (clrStatus.getDBText().equals(dbText)) {
          return clrStatus;
        }
      }
      return null;

    }


  }

  /**
   * AttributeValue object
   */
  private final AttributeValue attrValue;


  /**
   * @param value AttributeValue obj
   */
  public AttributeValueClientBO(final AttributeValue value) {
    this.attrValue = value;

  }

  /**
   * returns attribute
   */
  public Attribute getAttribute() {
    AttributeServiceClient serviceClient = new AttributeServiceClient();
    Collections.synchronizedSortedSet(new TreeSet<AttrNValueDependency>());
    try {
      return serviceClient.get(getAttrValue().getAttributeId());
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }
    return null;
  }

  /**
   * @return boolean
   */
  public boolean isDeleted() {
    return this.attrValue.isDeleted();
  }

  /**
   * @return String
   */
  public String getName() {
    return this.attrValue.getName();
  }


  /**
   * @return
   */
  public SortedSet<AttrNValueDependency> getValueDependencies(final boolean includeDeleted) {

    AttrNValueDependencyServiceClient depnServiceClient = new AttrNValueDependencyServiceClient();
    SortedSet<AttrNValueDependency> depSet = Collections.synchronizedSortedSet(new TreeSet<AttrNValueDependency>());
    if (getAttrValue() != null) {
      try {
        Set<AttrNValueDependency> dependenciesByValue =
            depnServiceClient.getDependenciesByValue(getAttrValue().getId());
        if (includeDeleted) {

          depSet.addAll(dependenciesByValue);
        }
        else {
          for (AttrNValueDependency attrNValueDependency : dependenciesByValue) {
            // check if the dependency is deleted
            if (!attrNValueDependency.isDeleted()) {
              // add only not deleted dependencies to the results list
              depSet.add(attrNValueDependency);
            }
          }

        }

      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
      }
    }
    return depSet;


  }

  /**
   * @return whether the value is cleared
   */
  public boolean isCleared() {
    // Icdm-830 Data Model Changes
    return getClearingStatus() == CLEARING_STATUS.CLEARED;
  }

  /**
   * @return whether the value is cleared
   */
  public boolean isNotCleared() {
    // Icdm-830 Data Model Changes
    return (getClearingStatus() == CLEARING_STATUS.NOT_CLEARED) || (getClearingStatus() == CLEARING_STATUS.IN_CLEARING);
  }

  /**
   * ICdm-1180 - new method to check for Deleted or Cleared Value Status
   *
   * @return whether the value is cleared
   */
  public boolean isDelOrRej() {
    // Icdm-830 Data Model Changes
    return (getClearingStatus() == CLEARING_STATUS.DELETED) || (getClearingStatus() == CLEARING_STATUS.REJECTED);
  }


  /**
   * ICdm-1180 - new method to check for Deleted or Cleared Value Status
   *
   * @return whether the value is cleared
   */
  public boolean isRejected() {
    // Icdm-830 Data Model Changes
    return getClearingStatus() == CLEARING_STATUS.REJECTED;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    return (obj.getClass() == this.getClass()) &&
        ModelUtil.isEqual(getAttrValue(), ((AttributeValueClientBO) obj).getAttrValue());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return ModelUtil.generateHashCode(getAttrValue().getId());
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final AttributeValueClientBO obj) {
    return this.attrValue.compareTo(obj.getAttrValue());
  }


  public CLEARING_STATUS getClearingStatus() {
    return CLEARING_STATUS.getClearingStatus(this.attrValue.getClearingStatus());
  }

  /**
   * @param arg0 other
   * @param sortColumn sort column
   * @return compare result based on sort column
   */
  public int compareTo(final AttributeValueClientBO arg0, final SortColumns sortColumn) {

    int compareResult;

    switch (sortColumn) {
      case SORT_ATTR_VAL:
        compareResult = this.compareTo(arg0);
        break;
      case SORT_ATTR_VAL_UNIT:
        compareResult = getAttrValue().getUnit().compareTo(arg0.getAttrValue().getUnit());
        break;
      case SORT_ATTR_VAL_DESC:
        compareResult = getAttrValue().getDescription().compareTo(arg0.getAttrValue().getDescription());
        break;
      // Icdm-830 Data Model Changes
      case SORT_ATTR_CLEAR_STATUS:
        compareResult = getAttrValue().getClearingStatus().compareTo(arg0.getAttrValue().getClearingStatus());
        break;
      default:
        compareResult = 0;
        break;
    }

    if (compareResult == ApicConstants.OBJ_EQUAL_CHK_VAL) {
      // compare result is equal, compare the attribute name
      compareResult = compareTo(arg0);
    }

    return compareResult;
  }

  /**
   * @param attrMap all attrs
   * @return PredefinedAttrValuesValidityModelNew
   */
  public PredefinedAttrValuesValidityModel getValidity(final Map<Long, Attribute> attrMap) {

    PredefinedValidityServiceClient client = new PredefinedValidityServiceClient();
    Set<PredefinedValidity> setOfValidity;
    if (null != getAttrValue()) {
      try {
        setOfValidity = client.getByValueId(getAttrValue().getId());

        Map<PredfndAttrValsValidityClientBO, AttributeValue> validityVals = new ConcurrentHashMap<>();
        Attribute validityAttr = null;
        if (CommonUtils.isNotEmpty(setOfValidity)) {
          for (PredefinedValidity preDfndValidity : setOfValidity) {

            PredfndAttrValsValidityClientBO predefinedAttrValidity =
                new PredfndAttrValsValidityClientBO(preDfndValidity, attrMap);


            validityVals.put(predefinedAttrValidity, predefinedAttrValidity.getValidityAttributeValue());
            validityAttr = predefinedAttrValidity.getValidityAttribute();
          }

          // one attribute with one/multiple values
          return new PredefinedAttrValuesValidityModel(validityAttr, validityVals);

        }
      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
      }
    }
    return null;
  }

  /**
   * ICDM-2296 Get the predefined attr value Combination
   *
   * @return List<PredefinedAttrValue>
   */
  public SortedSet<PredefinedAttrValue> getPreDefinedAttrValues() {
    return CommonUtils.isNotEmpty(getPreDefinedAttrValueSet()) ? new TreeSet<>(getPreDefinedAttrValueSet()) : null;
  }

  /**
   * @return SortedSet<Link>
   */
  public SortedSet<Link> getLinks() {
    LinkServiceClient linkClient = new LinkServiceClient();
    try {
      Map<Long, com.bosch.caltool.icdm.model.general.Link> allLinksByNode =
          linkClient.getAllLinksByNode(getAttrValue().getId(), MODEL_TYPE.ATTRIB_VALUE);
      return new TreeSet<>(allLinksByNode.values());
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    return new TreeSet<>();
  }

  /**
   * ICDM-2296 Get the predefined attr value Combination
   *
   * @return List<PredefinedAttrValue>
   */
  public Set<PredefinedAttrValue> getPreDefinedAttrValueSet() {
    if (null != getAttrValue()) {
      try {
        PredefinedAttrValueServiceClient client = new PredefinedAttrValueServiceClient();
        Map<Long, PredefinedAttrValue> attrsMap;

        attrsMap = client.getByValueId(getAttrValue().getId());


        Set<PredefinedAttrValue> predfndAttrValuesSet = new HashSet<PredefinedAttrValue>();
        if (CommonUtils.isNotEmpty(attrsMap)) {


          predfndAttrValuesSet.addAll(attrsMap.values());
        }
        return predfndAttrValuesSet;
      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().error(e.getLocalizedMessage(), e);
      }
    }
    return null;

  }


  /**
   * @param allPredefValMap
   * @return map of predefined validity
   */
  public Map<Long, PredefinedValidity> getValuesByAttributeValue(final Map<Long, PredefinedValidity> allPredefValMap) {
    Map<Long, PredefinedValidity> predefValMap = new HashMap<Long, PredefinedValidity>();
    for (PredefinedValidity validity : allPredefValMap.values()) {
      if (this.attrValue.getId() == validity.getGrpAttrValId()) {
        predefValMap.put(validity.getId(), validity);
      }
    }
    return predefValMap;
  }


  /**
   * @return the attrValue
   */
  public AttributeValue getAttrValue() {
    return this.attrValue;
  }

  /**
   * ICDM-120 Check if the value is valid (visible) based on the value dependencies. This method assumes: - that an
   * value has only one dependency - that if an value depends on the used flag, only one dependency is defined
   *
   * @param refPidcAttributes the list of attributes defined in the PIDC
   * @param pidcDataHandler
   * @param valDepSet
   * @param valDepMap
   * @return boolean
   */
  public boolean isValidValue(final Map<Long, IProjectAttribute> refPidcAttributes,
      final PidcDataHandler pidcDataHandler, final Set<AttrNValueDependency> valDepSet) {

    // the list of all dependencies of this attribute
    Set<AttrNValueDependency> dependencies = valDepSet;

    if (CommonUtils.isNullOrEmpty(dependencies)) {
      // no dependencies
      return true;
    }
    // get the attribute on which this attribute depends on
    AttrNValueDependency depAttrNext = dependencies.iterator().next();
    Long dependencyAttrID = depAttrNext.getDependentAttrId();

    // check, if the dependency attribute is deleted
    if (pidcDataHandler.getAttributeMap().get(dependencyAttrID).isDeleted()) {
      // the dependency attribute is deleted
      // thus the attribute is visible
      return true;
    }
    boolean equalsProjName = pidcDataHandler.getAttributeMap().get(dependencyAttrID).getLevel()
        .equals(Long.valueOf(ApicConstants.PROJECT_NAME_ATTR));
    // ICDM-196 place a null check if the dependency is at higher level
    IProjectAttribute iProjectAttribute = refPidcAttributes.get(dependencyAttrID);


    if (((iProjectAttribute == null) || iProjectAttribute.isAtChildLevel()) && !equalsProjName) {
      return false;
    }

    if (iProjectAttribute != null) {

      // check, if attribute depends on the used flag
      if ((depAttrNext.getDependentValueId() == null) && isVisible(iProjectAttribute, pidcDataHandler)) {
        // attribute depends on the used flag
        // check the used flag of the dependency attribute in the PIDC
        return iProjectAttribute.getUsedFlag().equals(ApicConstants.CODE_YES);
      }
      Long referenceValueId;
      // If the dependent attr is Project Name - ICDM-2118
      if (equalsProjName && !refPidcAttributes.isEmpty()) {
        referenceValueId = (refPidcAttributes.values().iterator().next()).getValueId();
      }
      else {
        // get the value defined in the PIDC for the dependency attribute
        referenceValueId = iProjectAttribute.getValueId();
      }
      // check if a value is defined for the attribute
      if (referenceValueId == null) {
        // no value defined for dependency attribute
        // ==> attribute invisible
        return false;
      }

      // iterate over all dependencies
      for (AttrNValueDependency attrValDependency : dependencies) {
        // If the dependent attr is Project Name - ICDM-2118
        if (equalsProjName && (attrValDependency.getDependentValueId() != null) &&
            attrValDependency.getDependentValueId().equals(referenceValueId)) {
          return true;
        }
        // check the value
        if ((attrValDependency.getDependentValueId() != null) &&
            attrValDependency.getDependentValueId().equals(referenceValueId) &&
            isVisible(iProjectAttribute, pidcDataHandler)) {
          return true;
        }
      }
    }

    return false;

  }

  /**
   * @param iProjectAttribute
   * @return
   */
  private boolean isVisible(final IProjectAttribute iProjectAttribute, final PidcDataHandler pidcDataHandler) {
    if (iProjectAttribute instanceof PidcVersionAttribute) {
      return !pidcDataHandler.getPidcVersInvisibleAttrSet().contains(iProjectAttribute.getAttrId());
    }
    else if (iProjectAttribute instanceof PidcVariantAttribute) {

      if (pidcDataHandler.getVariantInvisbleAttributeMap()
          .get(((PidcVariantAttribute) iProjectAttribute).getVariantId()) != null) {
        return !pidcDataHandler.getVariantInvisbleAttributeMap()
            .get(((PidcVariantAttribute) iProjectAttribute).getVariantId()).contains(iProjectAttribute.getAttrId());
      }
      return true;
    }
    else if (iProjectAttribute instanceof PidcSubVariantAttribute) {

      if (pidcDataHandler.getVariantInvisbleAttributeMap()
          .get(((PidcSubVariantAttribute) iProjectAttribute).getSubVariantId()) != null) {
        return !pidcDataHandler.getVariantInvisbleAttributeMap()
            .get(((PidcSubVariantAttribute) iProjectAttribute).getSubVariantId())
            .contains(iProjectAttribute.getAttrId());
      }
      return true;

    }
    return false;
  }

  /**
   * @return is attr enabled
   */
  public boolean isGrantAccessEnabled() {
    CurrentUserBO currentUser = new CurrentUserBO();
    try {
      if (currentUser.hasApicWriteAccess() || currentUser.hasNodeGrantAccess(getAttrValue().getId())) {
        return true;
      }
    }
    catch (ApicWebServiceException ex) {
      CDMLogger.getInstance().errorDialog(ex.getMessage(), ex, com.bosch.caltool.icdm.client.bo.Activator.PLUGIN_ID);
    }

    return false;
  }
}
