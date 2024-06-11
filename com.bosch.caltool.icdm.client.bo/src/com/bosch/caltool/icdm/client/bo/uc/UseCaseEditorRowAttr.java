/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.uc;

import java.text.ParseException;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import com.bosch.caltool.icdm.client.bo.Activator;
import com.bosch.caltool.icdm.client.bo.apic.AttributeClientBO;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.UCP_ATTR_MAPPING_FLAGS;
import com.bosch.caltool.icdm.model.apic.attr.AttrNValueDependency;
import com.bosch.caltool.icdm.model.apic.attr.Characteristic;
import com.bosch.caltool.icdm.model.uc.UcpAttr;
import com.bosch.caltool.icdm.model.uc.UsecaseEditorModel;


/**
 * This Class wraps Attribute and Collection of useCaseItems which is used as an input for UseCaseEditor
 *
 * @author jvi6cob
 */
public class UseCaseEditorRowAttr implements Comparable<UseCaseEditorRowAttr> {

  // Attribute
  private AttributeClientBO attribute;

  private final UsecaseEditorModel ucEditorModel;


  /**
   * @param useCaseEditorModel UsecaseEditorModel
   */
  public UseCaseEditorRowAttr(final UsecaseEditorModel useCaseEditorModel) {
    this.ucEditorModel = useCaseEditorModel;
  }


  /**
   * @return the attribute
   */
  public AttributeClientBO getAttributeBO() {
    return this.attribute;
  }


  /**
   * @param attribute the attribute to set
   */
  public void setAttributeBO(final AttributeClientBO attribute) {
    this.attribute = attribute;
  }


  @Override
  public int compareTo(final UseCaseEditorRowAttr arg0) {

    return ApicUtil.compare(getAttributeBO().getName(), arg0.getAttributeBO().getName());
  }


  @Override
  public int hashCode() {
    return this.attribute.hashCode();
  }

  /**
   * {@inheritDoc} returns true if ID's of the attributes are same or both null
   */
  @Override
  public boolean equals(final Object obj) {

    if (this == obj) {
      return true;
    }
    // If the object is null
    if (obj == null) {
      return false;
    }
    // If the Class is not equal to the object class
    if (getClass() != obj.getClass()) {
      return false;
    }
    // Usecase Nat Input for compare
    UseCaseEditorRowAttr compareObj = (UseCaseEditorRowAttr) obj;
    return this.attribute.equals(compareObj.getAttributeBO());
  }

  /**
   * Provides the attribute's created date in dd-MMM-yyyy format
   *
   * @return formatted attribute creation datedate
   */
  public String getAttrCreatedDateFormatted() {
    String attrCreatedDate = this.attribute.getAttribute().getCreatedDate();
    try {
      return ApicUtil.formatDate(DateFormat.DATE_FORMAT_09, attrCreatedDate);
    }
    catch (ParseException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    return null;
  }


  /**
   * @return characteristic name
   */
  public String getAttrClassName() {
    Characteristic characteristic =
        this.ucEditorModel.getCharacteristicMap().get(getAttributeBO().getAttribute().getCharacteristicId());
    return null != characteristic ? characteristic.getName() : "";
  }

  /**
   * @return true if the attribute has dependencies
   */
  public boolean hasAttrDependencies() {
    Map<Long, Set<AttrNValueDependency>> attrRefDependenciesMap = this.ucEditorModel.getAttrRefDependenciesMap();
    Set<AttrNValueDependency> depenAttr = attrRefDependenciesMap.get(getAttributeBO().getAttribute().getId());
    if (CommonUtils.isNotEmpty(depenAttr)) {
      for (AttrNValueDependency attrDependency : depenAttr) {
        if ((attrDependency != null) && (attrDependency.getAttributeId() != null)) {
          return true;
        }
      }
    }
    return false;

  }

  /**
   * @return Attribute Description
   */
  public String getAttrDescription() {
    return this.attribute.getAttribute().getDescription();
  }


  /**
   * @param mappableUcItems uc items for a particular use case/section
   * @return state of the All check box
   */
  public boolean isMappedToAll(final SortedSet<IUseCaseItemClientBO> mappableUcItems) {
    // uncheck ALL check boxes if mappable items are empty
    if (mappableUcItems.isEmpty()) {
      return false;
    }
    boolean status = true;
    for (IUseCaseItemClientBO ucItem : mappableUcItems) {
      // Skip the deleted Items
      if (!ucItem.isDeleted() && !ucItem.isMapped(getAttributeBO().getAttribute())) {
        status = false;
        break;
      }
    }
    return status;

  }

  /**
   * @param mappableUcItems uc items for a particular use case/section
   * @return state of the None check box
   */
  public boolean isMappedToNone(final SortedSet<IUseCaseItemClientBO> mappableUcItems) {
    boolean status = true;
    for (IUseCaseItemClientBO ucItem : mappableUcItems) {
      // Skip the deleted Items
      if (!this.attribute.isDeleted() && !ucItem.isDeleted() && ucItem.isMapped(getAttributeBO().getAttribute())) {
        status = false;
        break;
      }
    }
    return status;
  }

  /**
   * @param mappableUcItems uc items for a particular use case/section
   * @return state of the Any check box
   */
  public boolean isMappedToAny(final SortedSet<IUseCaseItemClientBO> mappableUcItems) {
    boolean status = false;
    for (IUseCaseItemClientBO ucItem : mappableUcItems) {
      if (!getAttributeBO().isDeleted() && !ucItem.isDeleted() && ucItem.isMapped(getAttributeBO().getAttribute())) {
        status = true;
        break;
      }
    }
    return status;
  }

  /**
   * @param ucItem mapped UC item
   * @return state of the check box
   */
  public boolean isUCItemMapped(final IUseCaseItemClientBO ucItem) {
    return (null != ucItem) && ucItem.isMapped(getAttributeBO().getAttribute());
  }


  /**
   * @return attribute name
   */
  public String getAttrName() {
    return getAttributeBO().getName();
  }


  /**
   * @param ucItemSet
   * @return true if the attribute is quotation relevant
   */
  public boolean hasQuotRelevantAttribute(final SortedSet<IUseCaseItemClientBO> ucItemSet) {
    boolean quotationFlag = false;
    for (IUseCaseItemClientBO iUseCaseItemClientBO : ucItemSet) {
      if (isUCItemQuotRelevant(iUseCaseItemClientBO.getID())) {
        // if any one mapped ucpAttr is quotation relevant, then nattable row is considered as quotation relevant for
        // filtering
        quotationFlag = true;
        break;
      }
    }
    return quotationFlag;
  }


  /**
   * @param ucItemId use case item ID
   * @return true if the selected use case Item is quotation relevant
   */
  public boolean isUCItemQuotRelevant(final Long ucItemId) {
    Map<Long, Long> ucpAttrMap = this.ucEditorModel.getAttrToUcpAttrMap().get(getAttributeBO().getAttribute().getId());
    if (CommonUtils.isNullOrEmpty(ucpAttrMap)) {
      return false;
    }
    boolean quotationFlag = false;
    UcpAttr ucpAttr = this.ucEditorModel.getUcpAttr().get(ucpAttrMap.get(ucItemId));
    if (ucpAttr != null) {
      Long mappingFlags = ucpAttr.getMappingFlags();
      if (mappingFlags != null) {
        quotationFlag = UCP_ATTR_MAPPING_FLAGS.QUOTATION_RELEVANT.isSet(mappingFlags);
      }
    }
    return quotationFlag;
  }


  /**
   * @param clientBo IUseCaseItemClientBO
   * @return selected use case item ID
   */
  public Long getUcItemId(final IUseCaseItemClientBO clientBo) {
    Long ucItemId = null;
    if (clientBo instanceof UsecaseClientBO) {
      ucItemId = ((UsecaseClientBO) clientBo).getUseCase().getId();
    }
    else if (clientBo instanceof UseCaseSectionClientBO) {
      ucItemId = ((UseCaseSectionClientBO) clientBo).getUseCaseSection().getId();
    }
    return ucItemId;
  }


}
