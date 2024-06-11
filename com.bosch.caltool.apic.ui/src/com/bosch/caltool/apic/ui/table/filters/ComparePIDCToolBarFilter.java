/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.table.filters;

import java.util.Set;

import com.bosch.caltool.apic.ui.editors.compare.CompareRowObject;
import com.bosch.caltool.icdm.client.bo.apic.AbstractProjectAttributeBO;
import com.bosch.caltool.icdm.client.bo.apic.ProjectAttributeUtil;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;

import ca.odell.glazedlists.matchers.Matcher;


/**
 * @author bru2cob
 */
public class ComparePIDCToolBarFilter {

  /**
   * Defines pidc attribute non-mandatory filter is selected or not
   */
  private boolean attrNonMandatorySel = true /* By default this flag will switched on */;
  /**
   * Defines pidc attribute mandatory filter is selected or not
   */
  private boolean attrMandatorySel = true /* By default this flag will switched on */;
  /**
   * Defines pidc attribute Dependent filter is selected or not
   */
  private boolean attrDepen = true;/* By default this flag will switched on */
  /**
   * Defines pidc attribute invisible filter is selected or not
   */
  private boolean attrVisibility = true;/* By default this flag will switched on */
  /**
   * Defines pidc attribute Dependent filter is selected or not
   */
  private boolean attrNonDep = true;
  private boolean attrNotDiff = true;
  private boolean attrDiff = true;

  private final ProjectAttributeUtil compareEditorUtil = new ProjectAttributeUtil();

  private class ComparePIDCToolBarParamMatcher<E> implements Matcher<E> {

    /** Singleton instance of TrueMatcher. */


    /** {@inheritDoc} */
    @Override
    public boolean matches(final E element) {
      if (element instanceof CompareRowObject) {
        return selectElement(element);
      }
      return true;
    }
  }

  /**
   * {@inheritDoc}
   */
  // @Override
  protected boolean selectElement(final Object element) {
    CompareRowObject compareRowObject = (CompareRowObject) element;
    final com.bosch.caltool.icdm.model.apic.attr.Attribute attr = compareRowObject.getAttribute();
    if (!filterMandatValues(attr)) {
      return false;
    }
    if (!filterRefDepVal(compareRowObject)) {
      return false;
    }
    if (!filterDiffAttr((boolean) ((CompareRowObject) element).getComputedIsDiff())) {
      return false;
    }

    return filterInvisibleAttr(compareRowObject);
  }

  /**
   * @param isDiff
   * @return
   */
  private boolean filterDiffAttr(final boolean isDiff) {
    // If the isDiff filter is selected
    if (!isAttrDiffSel() && isDiff) {
      return false;
    }
    // If the not Diff filter is selected
    return !(!isAttrNotDiffSel() && !isDiff);
  }


  /**
   * filter Reference
   *
   * @param attr
   */
  private boolean filterRefDepVal(final CompareRowObject compareRowObject) {

    AbstractProjectAttributeBO handler = this.compareEditorUtil.getProjectAttributeHandler(
        compareRowObject.getColumnDataMapper().getIpidcAttrs().iterator().next(),
        compareRowObject.getColumnDataMapper().getProjectHandlerMap().get(
            this.compareEditorUtil.getID(compareRowObject.getColumnDataMapper().getIpidcAttrs().iterator().next())));

    // Predefined filter for attributes which are controlling the Visibility
    if (!isAttrDepen() && handler.hasAttrDependencies()) {
      return false;
    }

    // Predefined filter for attributes which are controlling the Visibility
    return !(!isAttrNonDep() && !handler.hasAttrDependencies());
  }


  /**
   * @param attr
   */
  private boolean filterMandatValues(final Attribute attr) {
    // If the PIDC attribute mandatory filter is selected
    if (!isAttrMandatorySel() && attr.isMandatory()) {
      return false;
    }
    // If the PIDC attribute not mandatory filter is selected
    return !(!isAttrNonMandatorySel() && !attr.isMandatory());
  }

  /**
   * @param compareRowObject
   * @return
   */
  private boolean filterInvisibleAttr(final CompareRowObject compareRowObject) {
    if (!isAttrVisibility()) {
      Set<IProjectAttribute> ipidcAttrs = compareRowObject.getColumnDataMapper().getIpidcAttrs();
      boolean isAllIPIDCAttrVisible = false;
      for (IProjectAttribute ipidcAttribute : ipidcAttrs) {
        if (isVisible(ipidcAttribute, compareRowObject)) {
          isAllIPIDCAttrVisible = true;
          break;
        }
      }
      return isAllIPIDCAttrVisible;
    }
    return true;
  }

  /**
   * @param attrId
   * @return
   */
  private boolean isVisible(final IProjectAttribute ipidcAttribute, final CompareRowObject compareRowObject) {

    if (ipidcAttribute instanceof PidcVersionAttribute) {
      return !compareRowObject.getColumnDataMapper().getProjectHandlerMap()
          .get(this.compareEditorUtil.getID(ipidcAttribute)).getPidcDataHandler().getPidcVersInvisibleAttrSet()
          .contains(ipidcAttribute.getAttrId());
    }
    else if (ipidcAttribute instanceof PidcVariantAttribute) {
      return !compareRowObject.getColumnDataMapper().getProjectHandlerMap()
          .get(this.compareEditorUtil.getID(ipidcAttribute)).getPidcDataHandler().getVariantInvisbleAttributeMap()
          .values().contains(ipidcAttribute.getAttrId());
    }
    else if (ipidcAttribute instanceof PidcSubVariantAttribute) {
      return !compareRowObject.getColumnDataMapper().getProjectHandlerMap()
          .get(this.compareEditorUtil.getID(ipidcAttribute)).getPidcDataHandler().getSubVariantInvisbleAttributeMap()
          .values().contains(ipidcAttribute.getAttrId());
    }
    return true;
  }

  /**
   * @return
   */
  private boolean isAttrNotDiffSel() {
    return this.attrNotDiff;
  }

  /**
   * @param attrNotDiffSel the attrNotDiffSel to set
   */
  public void setAttrNotDiffSel(final boolean attrNotDiffSel) {
    this.attrNotDiff = attrNotDiffSel;
  }

  /**
   * @return
   */
  private boolean isAttrDiffSel() {
    return this.attrDiff;
  }

  /**
   * @param attrDiffSel the attrNotDiffSel to set
   */
  public void setAttrDiffSel(final boolean attrDiffSel) {
    this.attrDiff = attrDiffSel;
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
   * @param attrDepen attrDepen
   */
  public void setAttrDependency(final boolean attrDepen) {
    this.attrDepen = attrDepen;

  }

  /**
   * @return the attrDepen
   */
  public boolean isAttrDepen() {
    return this.attrDepen;
  }

  /**
   * @param attrNonDep attrNonDep
   */
  public void setAttrNonDep(final boolean attrNonDep) {
    this.attrNonDep = attrNonDep;

  }


  /**
   * @return the attrNonDep
   */
  public boolean isAttrNonDep() {
    return this.attrNonDep;
  }

  /**
   * @return
   */
  public Matcher getToolBarMatcher() {
    return new ComparePIDCToolBarParamMatcher<CompareRowObject>();
  }


  /**
   * @return the attrVisibility
   */
  public boolean isAttrVisibility() {
    return this.attrVisibility;
  }


  /**
   * @param attrVisibility the attrVisibility to set
   */
  public void setAttrVisibility(final boolean attrVisibility) {
    this.attrVisibility = attrVisibility;
  }

}
