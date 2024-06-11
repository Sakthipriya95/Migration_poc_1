/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.table.filters;

import com.bosch.caltool.apic.ui.editors.compare.CompareRowObject;
import com.bosch.caltool.icdm.client.bo.apic.PidcDataHandler;
import com.bosch.caltool.icdm.client.bo.uc.IUseCaseItemClientBO;
import com.bosch.caltool.icdm.client.bo.uc.OutLineUseCaseFilterHandler;
import com.bosch.caltool.icdm.client.bo.uc.OutLineViewDataHandler;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;

import ca.odell.glazedlists.matchers.Matcher;


/**
 * @author bru2cob
 */
public class ComparePIDCOutlineFilter {


  /**
   * To indicate whether super group is selected or not
   */
  private boolean isSuperGrp;
  /**
   * To indicate whether group is selected or not
   */
  private boolean isGrp;
  /**
   * To indicate whether common is selected or not
   */
  private boolean isCommn;
  /**
   * Selected node
   */
  private String selectedNode;
  /**
   * instance of ucecase item
   */
  private IUseCaseItemClientBO ucItem;

  /**
   * FavUseCaseItemNode instance
   */
  private com.bosch.caltool.icdm.client.bo.uc.FavUseCaseItemNode favUCItemNode;

  /**
   * true if selection is FavUseCaseItemNode
   */
  private boolean isFavUcItem;
  /**
   * true if selection is UseCaseItemNode
   */
  private boolean isUcItem;

  private final PidcDataHandler pidcDataHandler;

  private final OutLineViewDataHandler outlineDataHandler;

  /**
   * @param pidcVer PIDC version
   * @param pidcDataHandler PidcDataHandler
   * @param outlineDataHandler OutLineViewDataHandler
   */
  public ComparePIDCOutlineFilter(final PidcDataHandler pidcDataHandler,
      final OutLineViewDataHandler outlineDataHandler) {
    this.pidcDataHandler = pidcDataHandler;
    this.outlineDataHandler = outlineDataHandler;

  }


  /**
   * @return the isSuperGroup
   */
  public boolean isSuperGroup() {
    return this.isSuperGrp;
  }

  /**
   * @param isSuperGroup the isSuperGroup to set
   */
  public void setSuperGroup(final boolean isSuperGroup) {
    clearAllFlags();
    this.isSuperGrp = isSuperGroup;
  }

  /**
   * @return the isGroup
   */
  public boolean isGroup() {
    return this.isGrp;
  }

  /**
   * @param isGroup the isGroup to set
   */
  public void setGroup(final boolean isGroup) {
    clearAllFlags();
    this.isGrp = isGroup;
  }

  /**
   * @return the isCommon
   */
  public boolean isCommon() {
    return this.isCommn;
  }

  /**
   * @param isCommon the isCommon to set
   */
  public void setCommon(final boolean isCommon) {
    clearAllFlags();
    this.isCommn = isCommon;
  }


  /**
   * Set selected node {@inheritDoc}
   */
  public void setSelectedNode(final String filterText) {
    this.selectedNode = filterText;
  }

  /**
   * Clear all the flags
   */
  private void clearAllFlags() {
    this.isSuperGrp = false;
    this.isCommn = false;
    this.isGrp = false;
    this.isUcItem = false;
    this.isFavUcItem = false;
  }

  /**
   * @param ucItem use case item
   */
  public void setUseCaseItem(final IUseCaseItemClientBO ucItem) {
    this.ucItem = ucItem;
    clearAllFlags();
    this.isUcItem = true;
    setSelectedNode(ucItem.getName());
  }

  /**
   * icdm-1030
   *
   * @param favUcItem favourite use case item
   */
  public void setFavUseCaseItem(final com.bosch.caltool.icdm.client.bo.uc.FavUseCaseItemNode favUcItem) {
    this.favUCItemNode = favUcItem;
    clearAllFlags();
    this.isFavUcItem = true;
    setSelectedNode(favUcItem.getName());
  }

  /**
   * {@inheritDoc}
   */
  protected boolean selectElement(final Object element) {
    // Check if Attribute object
    if (element instanceof com.bosch.caltool.icdm.model.apic.attr.Attribute) {
      com.bosch.caltool.icdm.model.apic.attr.Attribute attribute =
          (com.bosch.caltool.icdm.model.apic.attr.Attribute) element;
      // if SuperGroup selected, apply filter to super group
      if (this.isSuperGrp) {
        return checkSuperGrpName(attribute);
      }
      // if Group selected, apply filter to group
      else if (this.isGrp) {
        return checkGrpName(attribute);
      }
      else if (this.isCommn) {
        return true;
      }
      else if (this.isUcItem) {
        return this.ucItem.isMapped(attribute);
      }
      else if (this.isFavUcItem) {
        return this.favUCItemNode.isMapped(attribute);
      }
    }
    return false;

  }


  /**
   * @return the ucItem
   */
  public IUseCaseItemClientBO getUcItem() {
    return this.ucItem;
  }

  private class ComparePIDCNatInputMatcher<E> implements Matcher<E> {

    /** Singleton instance of TrueMatcher. */


    /** {@inheritDoc} */
    @Override
    public boolean matches(final E element) {
      com.bosch.caltool.icdm.model.apic.attr.Attribute attribute = null;
      if (element instanceof CompareRowObject) {
        CompareRowObject rowObj = (CompareRowObject) element;
        attribute = rowObj.getAttribute();
      }
      if ((element instanceof CompareRowObject) && (null != attribute)) {
        if (ComparePIDCOutlineFilter.this.isSuperGrp) {
          return checkSuperGrpName(attribute);
        }

        // if Group selected, apply filter to group
        else if (ComparePIDCOutlineFilter.this.isGrp) {
          return checkGrpName(attribute);
        }
        else if (ComparePIDCOutlineFilter.this.isCommn) {
          return true;
        }
        else if (ComparePIDCOutlineFilter.this.isUcItem) {
          return new OutLineUseCaseFilterHandler(ComparePIDCOutlineFilter.this.ucItem,
              ComparePIDCOutlineFilter.this.outlineDataHandler.getUcDataHandler()).isMapped(attribute);
        }
        else if (ComparePIDCOutlineFilter.this.isFavUcItem) {
          return ComparePIDCOutlineFilter.this.favUCItemNode.isMapped(attribute);
        }

      }

      return false;

    }
  }

  /**
   * Retruns outline matcher instance
   *
   * @return Matcher
   */
  public Matcher getOutlineMatcher() {
    return new ComparePIDCNatInputMatcher<Attribute>();

  }

  /**
   * @param attribute
   */
  private boolean checkSuperGrpName(final com.bosch.caltool.icdm.model.apic.attr.Attribute attribute) {
    return this.pidcDataHandler.getAttributeSuperGroupMap()
        .get(this.pidcDataHandler.getAttributeGroupMap().get(attribute.getAttrGrpId()).getSuperGrpId()).getName()
        .equals(ComparePIDCOutlineFilter.this.selectedNode);
  }

  /**
   * @param attribute
   */
  private boolean checkGrpName(final com.bosch.caltool.icdm.model.apic.attr.Attribute attribute) {
    return this.pidcDataHandler.getAttributeGroupMap().get(attribute.getAttrGrpId()).getName()
        .equals(ComparePIDCOutlineFilter.this.selectedNode);
  }

}
