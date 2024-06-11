/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.table.filters;


import com.bosch.caltool.icdm.client.bo.apic.AttributeValueClientBO;
import com.bosch.caltool.icdm.client.bo.apic.AttributeValueWithDetails;
import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueDummy;


/**
 * @author dmo5cob
 */

public class PIDCAttrValueEditToolBarFilters extends AbstractViewerFilter {


  private boolean attrValNotClr = false;/* By default this flag will switched on */
  private boolean attrValClr = true;/* By default this flag will switched on */
  private boolean attrValRejected = false;/* By default this flag will switched on */
  private boolean notDeleted = true;/* By default this flag will switched on */
  private boolean deleted = false;/* By default this flag will switched on */
  private boolean notDepn = false;/* By default this flag will switched on */
  private boolean depn = true;/* By default this flag will switched on */
  private AttributeValueWithDetails validAttrValues;


  /**
   * @param validAttrValues the validAttrValues to set
   */
  public void setValidAttrValues(final AttributeValueWithDetails validAttrValues) {
    this.validAttrValues = validAttrValues;
  }

  /**
   * @return the validAttrValues
   */
  public AttributeValueWithDetails getValidAttrValues() {
    return this.validAttrValues;
  }

  /**
   * @return the attrValClr
   */
  public boolean isAttrValClr() {
    return this.attrValClr;
  }

  /**
   * @return the attrValNotClr
   */
  public boolean isAttrValNotClr() {
    return this.attrValNotClr;
  }

  /**
   * @param validAttrValues AttributeValueWithDetails
   */
  public PIDCAttrValueEditToolBarFilters(final AttributeValueWithDetails validAttrValues) {
    super();
    setFilterText(AbstractViewerFilter.DUMMY_FILTER_TXT, false);
    this.validAttrValues = validAttrValues;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setFilterText(final String filterText) {
    setFilterText(filterText, false);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean selectElement(final Object element) {

    final AttributeValue val = (AttributeValue) element;
    // for Dummy attr
    if (val instanceof AttributeValueDummy) {
      return true;
    }
    // for not cleared
    if (!filterNotClearedValues(val)) {
      return false;
    }
    // for not deleted
    if (!filterDeletedValues(val)) {
      return false;
    }
    // filter depvalues
    if (!filterDependentValues(val)) {
      return false;
    }
    // filter rejected values
    return filterRejectedValues(val);
  }

  /**
   * @param val
   * @return
   */
  private boolean filterRejectedValues(final AttributeValue val) {
    AttributeValueClientBO attributeValueClientBO = new AttributeValueClientBO(val);
    return !(!isAttrValRejected() && ((val == null) || (attributeValueClientBO.isRejected())));
  }

  /**
   * @param val
   * @return
   */
  private boolean filterDependentValues(final AttributeValue value) {
    if (!isNotDepn() && isValValid(value) && !this.validAttrValues.getValidValMap().get(value.getId())) {
      return false;
    }
    return !(!isDepn() && isValValid(value));
  }

  /**
   * @param value
   * @return
   */
  private boolean isValValid(final AttributeValue value) {
    return (value != null) && (this.validAttrValues != null) && (this.validAttrValues.getValidValMap() != null) &&
        (this.validAttrValues.getValidValMap().get(value.getId()) != null);
  }


  /**
   * @param value
   */
  private boolean filterNotClearedValues(final AttributeValue value) {
    AttributeValueClientBO attributeValueClientBO = new AttributeValueClientBO(value);
    if (!isAttrValNotClr() && attributeValueClientBO.isNotCleared()) {
      return false;
    }
    return !(!isAttrValClr() && ((value == null) || (attributeValueClientBO.isCleared())));
  }

  /**
   * @param value
   */
  private boolean filterDeletedValues(final AttributeValue value) {
    AttributeValueClientBO attributeValueClientBO = new AttributeValueClientBO(value);

    if (!isNotDeleted() && !attributeValueClientBO.isDeleted()) {
      return false;
    }
    return !(!isDeleted() &&
        ((value == null) || (attributeValueClientBO.isDeleted() && !attributeValueClientBO.isRejected())));
  }

  /**
   * *
   *
   * @param attrValNotClr attrValNotClr
   */
  public void setNotClearSel(final boolean attrValNotClr) {
    this.attrValNotClr = attrValNotClr;

  }

  /**
   * @param attrValClr attrValClr
   */
  public void setClearSel(final boolean attrValClr) {
    this.attrValClr = attrValClr;

  }


  /**
   * @return the notDeleted
   */
  public boolean isNotDeleted() {
    return this.notDeleted;
  }


  /**
   * @param notDeleted the notDeleted to set
   */
  public void setNotDeleted(final boolean notDeleted) {
    this.notDeleted = notDeleted;
  }


  /**
   * @return the deleted
   */
  public boolean isDeleted() {
    return this.deleted;
  }


  /**
   * @param deleted the deleted to set
   */
  public void setDeleted(final boolean deleted) {
    this.deleted = deleted;
  }


  /**
   * @return the notDepn
   */
  public boolean isNotDepn() {
    return this.notDepn;
  }


  /**
   * @param notDepn the notDepn to set
   */
  public void setNotDepn(final boolean notDepn) {
    this.notDepn = notDepn;
  }


  /**
   * @return the depn
   */
  public boolean isDepn() {
    return this.depn;
  }


  /**
   * @param depn the depn to set
   */
  public void setDepn(final boolean depn) {
    this.depn = depn;
  }


  /**
   * @return the attrValRejected
   */
  public boolean isAttrValRejected() {
    return this.attrValRejected;
  }


  /**
   * @param attrValRejected the attrValRejected to set
   */
  public void setAttrValRejected(final boolean attrValRejected) {
    this.attrValRejected = attrValRejected;
  }


}
