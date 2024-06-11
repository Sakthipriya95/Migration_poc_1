/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.usecase.ui.table.filters;

import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.caltool.icdm.client.bo.uc.IUseCaseItemClientBO;
import com.bosch.caltool.icdm.client.bo.uc.UseCaseEditorRowAttr;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;

import ca.odell.glazedlists.matchers.Matcher;


/**
 * @author jvi6cob
 */
public class UseCaseAttrPageNatToolBarFilters {

  /**
   * Defines quotation relevant attribute filter is selected or not
   */
  private boolean quotationRelevantUcAttr = true /* By default this flag will be switched on */;
  /**
   * Defines non quotation relevant attribute filter is selected or not
   */
  private boolean quotationNotRelevantUcAttr = true /* By default this flag will be switched on */;
  /**
   * Defines usecase attribute mandatory filter is selected or not
   */
  private boolean attrMandatorySel = true /* By default this flag will be switched on */;
  /**
   * Defines usecase attribute non-mandatory filter is selected or not
   */
  private boolean attrNonMandatorySel = true /* By default this flag will be switched on */;
  /**
   * Defines usecase dependent attribute filter is selected or not
   */
  private boolean attrDepSel = true /* By default this flag will be switched on */;
  /**
   * Defines usecase dependent attribute filter is selected or not
   */
  private boolean attrNonDepSel = true /* By default this flag will be switched on */;
  /**
   * Defines usecase attribute All selected flag filter is selected or not
   */
  private boolean attrAllFlag = true /* By default this flag will be switched on */;
  /**
   * Defines usecase attribute Any selected flag filter is selected or not
   */
  private boolean attrAnyFlag = true /* By default this flag will be switched on */;
  /**
   * Defines usecase attribute None selected flag filter is selected or not
   */
  private boolean attrNoneFlag = true /* By default this flag will be switched on */;

  /**
   * Defines the mappable usecase items
   */
  private SortedSet<IUseCaseItemClientBO> ucItemSet;


  /**
   * Constructor
   *
   * @param mappableUcItems - mappable usecase items
   */
  public UseCaseAttrPageNatToolBarFilters(final SortedSet<IUseCaseItemClientBO> mappableUcItems) {
    super();
    this.ucItemSet = mappableUcItems == null ? null : new TreeSet<>(mappableUcItems);
  }


  /**
   * @param ucItems the ucItemsList to set
   */
  public void setUcItemsList(final SortedSet<IUseCaseItemClientBO> ucItems) {
    this.ucItemSet = ucItems == null ? null : new TreeSet<>(ucItems);
  }

  /**
   * @return the attrAllSelectedFlag
   */
  public boolean isAttrAllSelectedFlag() {
    return this.attrAllFlag;
  }


  /**
   * @param flag the attrAllSelectedFlag to set
   */
  public void setAttrAllSelectedFlag(final boolean flag) {
    this.attrAllFlag = flag;
  }


  /**
   * @return the attrAnySelectedFlag
   */
  public boolean isAttrAnySelectedFlag() {
    return this.attrAnyFlag;
  }


  /**
   * @param flag the attrAnySelectedFlag to set
   */
  public void setAttrAnySelectedFlag(final boolean flag) {
    this.attrAnyFlag = flag;
  }


  /**
   * @return the attrNoneSelectedFlag
   */
  public boolean isAttrNoneSelectedFlag() {
    return this.attrNoneFlag;
  }


  /**
   * @param flag the attrNoneSelectedFlag to set
   */
  public void setAttrNoneSelectedFlag(final boolean flag) {
    this.attrNoneFlag = flag;
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
   * @return the attrNonDepSel
   */
  public boolean isAttrDepSel() {
    return this.attrDepSel;
  }

  /**
   * @param attrDepSel the attrDepSel to set
   */
  public void setAttrDepSel(final boolean attrDepSel) {
    this.attrDepSel = attrDepSel;
  }


  /**
   * @return the attrNonDepSel
   */
  public boolean isAttrNonDepSel() {
    return this.attrNonDepSel;
  }


  /**
   * @param attrNonDepSel the attrNonDepSel to set
   */
  public void setAttrNonDepSel(final boolean attrNonDepSel) {
    this.attrNonDepSel = attrNonDepSel;
  }

  /**
   * @return the quotationRelevantUcAttr
   */
  public boolean isQuotationRelevantUcAttrSel() {
    return this.quotationRelevantUcAttr;
  }


  /**
   * @param quotationRelevantUcAttr the quotationRelevantUcAttr to set
   */
  public void setQuotationRelevantUcAttr(final boolean quotationRelevantUcAttr) {
    this.quotationRelevantUcAttr = quotationRelevantUcAttr;
  }


  /**
   * @return the quotationNotRelevantUcAttr
   */
  public boolean isQuotationNotRelevantUcAttrSel() {
    return this.quotationNotRelevantUcAttr;
  }


  /**
   * @param quotationNotRelevantUcAttr the quotationNotRelevantUcAttr to set
   */
  public void setQuotationNotRelevantUcAttr(final boolean quotationNotRelevantUcAttr) {
    this.quotationNotRelevantUcAttr = quotationNotRelevantUcAttr;
  }


  /**
   * @param filterText filter Text
   */
  // @Override
  public void setFilterText(final String filterText) {
    // TO-DO
  }

  private class UCToolBarParamMatcher<E> implements Matcher<E> {


    @Override
    public boolean matches(final E element) {
      if (element instanceof UseCaseEditorRowAttr) {
        return selectElement(element);
      }
      return true;
    }
  }

  /**
   * @param element Usecase Nat Row Object
   * @return true if the attribute can be displayed in the usecase editor
   */
  // @Override
  protected boolean selectElement(final Object element) {

    if (!(element instanceof UseCaseEditorRowAttr)) {
      return false;
    }

    UseCaseEditorRowAttr useCaseEditorRowAttr = (UseCaseEditorRowAttr) element;
    if (!isAttrQuotationRelevant(useCaseEditorRowAttr)) {
      return false;
    }

    if (!isAttrDepOrNonDep(useCaseEditorRowAttr)) {
      return false;
    }

    Attribute attribute = useCaseEditorRowAttr.getAttributeBO().getAttribute();
    if (!isAttrMandatoryOrNonMandatory(attribute)) {
      return false;
    }

    boolean[] mappingType = getMappingType(attribute);
    return isUCItemMappedToAllAnyNone(mappingType);
  }

  /**
   * @param useCaseEditorRowAttr
   * @return
   */
  private boolean isAttrQuotationRelevant(final UseCaseEditorRowAttr useCaseEditorRowAttr) {
    // if quotation relevant attributes filter is selected
    if (!isQuotationRelevantUcAttrSel() && useCaseEditorRowAttr.hasQuotRelevantAttribute(this.ucItemSet)) {
      return false;
    }
    // if non quotation relevant attributes filter is selected
    return isQuotationNotRelevantUcAttrSel() || useCaseEditorRowAttr.hasQuotRelevantAttribute(this.ucItemSet);
  }


  /**
   * @param attribute
   * @return
   */
  private boolean isAttrDepOrNonDep(final UseCaseEditorRowAttr useCaseEditorRowAttr) {
    // if dependent attributes filter is selected
    if (!isAttrDepSel() && useCaseEditorRowAttr.hasAttrDependencies()) {
      return false;
    }
    // if non-dependent attributes filter is selected
    return isAttrNonDepSel() || useCaseEditorRowAttr.hasAttrDependencies();
  }


  /**
   * @param mappingType
   * @return
   */
  private boolean isUCItemMappedToAllAnyNone(final boolean[] mappingType) {
    if (this.attrAllFlag && mappingType[0]) {
      return true;
    }
    if (this.attrAnyFlag && mappingType[1]) {
      return true;
    }
    return this.attrNoneFlag && mappingType[2];
  }


  /**
   * @param attribute
   * @return
   */
  private boolean isAttrMandatoryOrNonMandatory(final Attribute attribute) {
    // If the attribute mandatory filter is selected
    if (!isAttrMandatorySel() && attribute.isMandatory()) {
      return false;
    }
    // If the attribute not mandatory filter is selected
    return (isAttrNonMandatorySel() || attribute.isMandatory());
  }


  /**
   * Get the type of mapping for the attribute in the UC item set
   *
   * @param attribute Attribute
   * @return boolean array of size 3; item [0] - AllItemMappd, item [1] - AnyItemMappd, item [2] - NoItemMappd
   */
  private boolean[] getMappingType(final com.bosch.caltool.icdm.model.apic.attr.Attribute attribute) {
    boolean isAllItemMappd = false;
    boolean isAnyItemMappd = false;
    boolean isNoItemMappd = true;

    // count to know how many ucItems are mapped to the attribute
    int chkdItemsCount = 0;

    for (IUseCaseItemClientBO ucItem : this.ucItemSet) {

      if (ucItem.isMapped(attribute)) {
        // set true even if it is mapped to one ucItem
        isAnyItemMappd = true;
        // set false even if it is mapped to one ucItem
        isNoItemMappd = false;
        chkdItemsCount++;
      }

    }
    if (chkdItemsCount == this.ucItemSet.size()) {
      isAllItemMappd = true;
    }

    return new boolean[] { isAllItemMappd, isAnyItemMappd, isNoItemMappd };

  }

  /**
   * @return matcher
   */
  public Matcher getToolBarMatcher() {
    return new UCToolBarParamMatcher<UseCaseEditorRowAttr>();
  }


}
