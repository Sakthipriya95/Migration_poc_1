/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.table.filters;

import com.bosch.caltool.apic.ui.editors.PIDCSearchEditorInput;
import com.bosch.caltool.icdm.client.bo.apic.PidcSearchEditorDataHandler;
import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueType;


/**
 * @author bru2cob ICDM-1158
 */
public class PIDCSearchToolBarFilters extends AbstractViewerFilter {

  /**
   * Defines pidc attribute mandatory filter is selected or not
   */
  private boolean attrMandatorySel = true;
  /**
   * Defines pidc attribute non-mandatory filter is selected or not
   */
  private boolean attrNonMandatorySel = true;
  /**
   * Defines pidc attribute text value type filter is selected or not
   */
  private boolean attrTextSel = true;
  /**
   * Defines pidc attribute boolean value type filter is selected or not
   */
  private boolean attrBoolSel = true;

  /**
   * Defines pidc attribute number type filter is selected or not
   */
  private boolean attrNumberSel = true;
  /**
   * Defines pidc attribute hyperlink type filter is selected or not
   */
  private boolean attrLinkSel = true;
  /**
   * Defines pidc attribute date type filter is selected or not
   */
  private boolean attrDateSel = true;

  /**
   * ICDM-1291 defines pidc attribute checked filter is selected or not
   */
  private boolean attrCheckedSel = true;

  /**
   * ICDM-1291 defines pidc attribute checked filter is selected or not
   */
  private boolean attrUnCheckedSel = true;


  /**
   * ICDM-1291 editor input to get attribute value map & attribute used map
   */
  private final PidcSearchEditorDataHandler pidcSearchEditorInput;


  /**
   * @param editorInput PIDCSearchEditorInput
   */
  public PIDCSearchToolBarFilters(final PIDCSearchEditorInput editorInput) {
    super();
    this.pidcSearchEditorInput = editorInput.getDataHandler();
    setFilterText(AbstractViewerFilter.DUMMY_FILTER_TXT, false);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setFilterText(final String filterText) {
    setFilterText(filterText, false);
  }

  /**
   * @return the attrTextSel
   */
  public boolean isAttrTextSel() {
    return this.attrTextSel;
  }


  /**
   * @param attrTextSel the attrTextSel to set
   */
  public void setAttrTextSel(final boolean attrTextSel) {
    this.attrTextSel = attrTextSel;
  }


  /**
   * @return the attrBoolSel
   */
  public boolean isAttrBoolSel() {
    return this.attrBoolSel;
  }


  /**
   * @param attrBoolSel the attrBoolSel to set
   */
  public void setAttrBoolSel(final boolean attrBoolSel) {
    this.attrBoolSel = attrBoolSel;
  }

  /**
   * @return the attrNumberSel
   */
  public boolean isAttrNumberSel() {
    return this.attrNumberSel;
  }


  /**
   * @param attrNumberSel the attrNumberSel to set
   */
  public void setAttrNumberSel(final boolean attrNumberSel) {
    this.attrNumberSel = attrNumberSel;
  }


  /**
   * @return the attrLinkSel
   */
  public boolean isAttrLinkSel() {
    return this.attrLinkSel;
  }


  /**
   * @param attrLinkSel the attrLinkSel to set
   */
  public void setAttrLinkSel(final boolean attrLinkSel) {
    this.attrLinkSel = attrLinkSel;
  }


  /**
   * @return the attrDateSel
   */
  public boolean isAttrDateSel() {
    return this.attrDateSel;
  }


  /**
   * @param attrDateSel the attrDateSel to set
   */
  public void setAttrDateSel(final boolean attrDateSel) {
    this.attrDateSel = attrDateSel;
  }


  /**
   * @return the attrMandatorySel
   */
  public boolean isAttrMandatorySel() {
    return this.attrMandatorySel;
  }

  /**
   * @param attrMandatorySel the attrMandatorySel to set
   */
  public void setAttrMandatorySel(final boolean attrMandatorySel) {
    this.attrMandatorySel = attrMandatorySel;
  }

  /**
   * @return the attrNonMandatorySel
   */
  public boolean isAttrNonMandatorySel() {
    return this.attrNonMandatorySel;
  }

  /**
   * @param attrNonMandatorySel the attrNonMandatorySel to set
   */
  public void setAttrNonMandatorySel(final boolean attrNonMandatorySel) {
    this.attrNonMandatorySel = attrNonMandatorySel;
  }

  /**
   * @return the attrCheckedSel
   */
  public boolean isAttrCheckedSel() {
    return this.attrCheckedSel;
  }


  /**
   * @param attrCheckedSel the attrCheckedSel to set
   */
  public void setAttrCheckedSel(final boolean attrCheckedSel) {
    this.attrCheckedSel = attrCheckedSel;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean selectElement(final Object element) {
    if (element instanceof Attribute) {
      final Attribute attr = (Attribute) element;
      if (!filterSelectedAttr(attr)) {
        return false;
      }
      if (!filterMandatValues(attr)) {
        return false;
      }
      if (!filterTextValues(attr)) {
        return false;
      }
      if (!filterNumberValues(attr)) {
        return false;
      }
      if (!filterDateValues(attr)) {
        return false;
      }
      if (!filterLinkValues(attr)) {
        return false;
      }
      if (!filterBoolValues(attr)) {
        return false;
      }
    }
    return true;
  }


  /**
   * ICDM-1291
   *
   * @param attr Attribute
   * @return true if the attribute needs to be shown
   */
  private boolean filterSelectedAttr(final Attribute attr) {
    if (!isAttrCheckedSel() && this.pidcSearchEditorInput.isAttrSelected(attr)) {
      return false;
    }
    if (!isAttrUnCheckedSel() && !this.pidcSearchEditorInput.isAttrSelected(attr)) {
      return false;
    }
    return true;
  }

  /**
   * @param pidcAttr
   */
  private boolean filterMandatValues(final Attribute attr) {
    if (!isAttrMandatorySel() && attr.isMandatory()) {
      return false;
    }
    if (!isAttrNonMandatorySel() && !attr.isMandatory()) {
      return false;
    }
    return true;
  }

  /**
   * @param pidcAttr
   */
  private boolean filterTextValues(final Attribute attr) {
    if (!isAttrTextSel() && CommonUtils.isEqual(attr.getValueType(), AttributeValueType.TEXT.getDisplayText())) {
      return false;
    }
    return true;
  }

  /**
   * @param attr
   * @return
   */
  private boolean filterBoolValues(final Attribute attr) {
    if (!isAttrBoolSel() && CommonUtils.isEqual(attr.getValueType(), AttributeValueType.BOOLEAN.getDisplayText())) {
      return false;
    }
    return true;
  }

  /**
   * @param attr
   * @return
   */
  private boolean filterLinkValues(final Attribute attr) {
    if (!isAttrLinkSel() && CommonUtils.isEqual(attr.getValueType(), AttributeValueType.HYPERLINK.getDisplayText())) {
      return false;
    }
    return true;
  }

  /**
   * @param attr
   * @return
   */
  private boolean filterDateValues(final Attribute attr) {
    if (!isAttrDateSel() && CommonUtils.isEqual(attr.getValueType(), AttributeValueType.DATE.getDisplayText())) {
      return false;
    }
    return true;
  }

  /**
   * @param attr
   * @return
   */
  private boolean filterNumberValues(final Attribute attr) {
    if (!isAttrNumberSel() && CommonUtils.isEqual(attr.getValueType(), AttributeValueType.NUMBER.getDisplayText())) {
      return false;
    }
    return true;
  }

  /**
   * @return the attrUnCheckedSel
   */
  public boolean isAttrUnCheckedSel() {
    return this.attrUnCheckedSel;
  }


  /**
   * @param attrUnCheckedSel the attrUnCheckedSel to set
   */
  public void setAttrUnCheckedSel(final boolean attrUnCheckedSel) {
    this.attrUnCheckedSel = attrUnCheckedSel;
  }
}
