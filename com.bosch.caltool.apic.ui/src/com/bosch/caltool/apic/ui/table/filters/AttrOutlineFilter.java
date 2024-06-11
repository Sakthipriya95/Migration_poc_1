/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.table.filters;


import com.bosch.caltool.icdm.client.bo.uc.FavUseCaseItemNode;
import com.bosch.caltool.icdm.client.bo.uc.IUseCaseItemClientBO;
import com.bosch.caltool.icdm.client.bo.uc.OutLineUseCaseFilterHandler;
import com.bosch.caltool.icdm.client.bo.uc.OutLineViewDataHandler;
import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.attr.AttrGroup;
import com.bosch.caltool.icdm.model.apic.attr.AttrGroupModel;
import com.bosch.caltool.icdm.model.apic.attr.AttrSuperGroup;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;


/**
 * @author adn1cob
 */
public class AttrOutlineFilter extends AbstractViewerFilter {

  // members to indicate if group filer or superGroup filter
  private boolean superGroup;

  private boolean group;
  private boolean common;

  private String selectedNode;

  private String parentSuperGroupName = "";
  // Icdm-295
  private IUseCaseItemClientBO ucItem;

  /**
   * ICDM-1029 favorite usecase item node
   */
  private FavUseCaseItemNode favUcItem;

  private boolean isUcItem;

  /**
   * Icdm-1029 true if selection is FavUseCaseItemNode
   */
  private boolean isFavUcItem;

  /**
   * AttrGroupModel instance whilch has groups /supergroups info
   */
  private final AttrGroupModel attrGrpModel;

  private final OutLineViewDataHandler outLineDataHandler;

  /**
   * @param outLineViewDataHandler OutLineViewDataHandler
   */
  public AttrOutlineFilter(final OutLineViewDataHandler outLineViewDataHandler) {
    this.attrGrpModel = outLineViewDataHandler.getAttrRootNode().getAttrGroupModel();
    this.outLineDataHandler = outLineViewDataHandler;

  }

  @Override
  public void setFilterText(final String filterText) {
    // The filter text is same as the selected node in the Outline tree.
    super.setFilterText(filterText, false);
    this.selectedNode = filterText;
  }

  /**
   * @return the isSuperGroup
   */
  public boolean isSuperGroup() {
    return this.superGroup;
  }


  /**
   * @param isSuperGroup the isSuperGroup to set
   */
  public void setSuperGroup(final boolean isSuperGroup) {
    this.superGroup = isSuperGroup;
  }

  /**
   * @return the isGroup
   */
  public boolean isGroup() {
    return this.group;
  }

  /**
   * @param isGroup the isGroup to set
   */
  public void setGroup(final boolean isGroup) {
    this.group = isGroup;
  }

  /**
   * To be used in case of group to set the Parent Super group in order to Differentiate between groups with same name
   * but different Supergroups
   *
   * @param parentSuperGroupName the parent supergroup of the selected group
   */
  public void setParentSuperGroup(final String parentSuperGroupName) {
    this.parentSuperGroupName = parentSuperGroupName;
  }

  /**
   * @return the isCommon
   */
  public boolean isCommon() {
    return this.common;
  }

  /**
   * @param isCommon the isCommon to set
   */
  public void setCommon(final boolean isCommon) {
    this.common = isCommon;
  }

  // Icdm-295
  private void clearAllFlags() {
    this.superGroup = false;
    this.common = false;
    this.group = false;
    this.isUcItem = false;
    this.isFavUcItem = false;
  }


  /**
   * Icdm-295
   *
   * @param ucItem use case item
   */
  public void setUseCaseItem(final IUseCaseItemClientBO ucItem) {
    this.ucItem = ucItem;
    clearAllFlags();
    this.isUcItem = true;
    setFilterText(ucItem.getName());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean selectElement(final Object element) {
    if (element instanceof Attribute) {
      Attribute attr = (Attribute) element;
      // if SuperGroup selected, apply filter to super group
      if (this.superGroup) {
        AttrGroup attributeGroup = this.attrGrpModel.getAllGroupMap().get(attr.getAttrGrpId());
        if (this.attrGrpModel.getAllSuperGroupMap().get(attributeGroup.getSuperGrpId()).getName()
            .equals(this.selectedNode)) {
          return true;
        }
        // if Group selected, apply filter to group
      }
      else if (this.group) {
        AttrGroup attrGroup = this.attrGrpModel.getAllGroupMap().get(attr.getAttrGrpId());
        AttrSuperGroup supergrp = this.attrGrpModel.getAllSuperGroupMap().get(attrGroup.getSuperGrpId());
        if (CommonUtils.isEqual(attrGroup.getName(), this.selectedNode) &&
            !(CommonUtils.isEmptyString(this.parentSuperGroupName)) &&
            CommonUtils.isEqual(supergrp.getName(), this.parentSuperGroupName)) {
          return true;
        }
      }
      else if (this.common) {
        return true;
      }
      // Icdm-295
      else if (this.isUcItem) {
        return new OutLineUseCaseFilterHandler(this.ucItem, this.outLineDataHandler.getUcDataHandler()).isMapped(attr);
      }
      // ICDM-1029
      else if (this.isFavUcItem) {
        return this.favUcItem.isMapped(attr);
      }
    }
    // ICDM-1135 To display the child elements
    else if (element instanceof AttributeValue) {
      return true;
    }
    return false;

  }


  /**
   * @return the ucItem
   */
  public IUseCaseItemClientBO getUcItem() {
    return this.ucItem;
  }

  /**
   * @param favUcItem FavUseCaseItemNode
   */
  public void setFavUseCaseItem(final FavUseCaseItemNode favUcItem) {

    this.favUcItem = favUcItem;
    clearAllFlags();
    this.isFavUcItem = true;
    setFilterText(this.favUcItem.getName());

  }


}
