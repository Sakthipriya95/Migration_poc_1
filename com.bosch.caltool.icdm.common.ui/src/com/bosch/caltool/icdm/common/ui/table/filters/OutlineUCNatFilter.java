/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.table.filters;


import com.bosch.caltool.icdm.client.bo.apic.AttributeClientBO;
import com.bosch.caltool.icdm.client.bo.fm.FocusMatrixAttributeClientBO;
import com.bosch.caltool.icdm.client.bo.uc.FavUseCaseItemNode;
import com.bosch.caltool.icdm.client.bo.uc.IUseCaseItemClientBO;
import com.bosch.caltool.icdm.client.bo.uc.OutLineUseCaseFilterHandler;
import com.bosch.caltool.icdm.client.bo.uc.OutLineViewDataHandler;
import com.bosch.caltool.icdm.client.bo.uc.UseCaseEditorRowAttr;
import com.bosch.caltool.icdm.model.apic.attr.AttrGroupModel;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;

import ca.odell.glazedlists.matchers.Matcher;


/**
 * @author jvi6cob
 */
public class OutlineUCNatFilter {


  // members to indicate if group filer or superGroup filter
  private boolean superGroupFlag;

  private boolean groupFlag;
  private boolean commonFlag;
  private boolean ucItemFlag;

  private String selectedNode;

  private IUseCaseItemClientBO ucItemObj;

  /**
   * ICDM-1035 Favourite use case item instance
   */
  private FavUseCaseItemNode favUcItem;

  /**
   * ICDM-1035 true if the selection in outline is fav node
   */
  private boolean isFavUcItem;

  /**
   * boolean to indicate whether it is use case editor
   */
  private final boolean isUseCaseEditor;

  private AttrGroupModel attrGroupModel;

  private final OutLineViewDataHandler outLineViewDataHandler;
  

  /**
   * Constructor
   *
   * @param isUseCaseEditor boolean
   * @param outLineViewDataHandler OutLineViewDataHandler
   */
  public OutlineUCNatFilter(final boolean isUseCaseEditor, final OutLineViewDataHandler outLineViewDataHandler) {
    this.isUseCaseEditor = isUseCaseEditor;
    this.outLineViewDataHandler = outLineViewDataHandler;
    this.attrGroupModel = this.outLineViewDataHandler.getAttrRootNode().getAttrGroupModel();
  }


  /**
   * setting all flags to false
   */
  private void clearAllFlags() {
    this.superGroupFlag = false;
    this.commonFlag = false;
    this.groupFlag = false;
    this.ucItemFlag = false;
  }

  /**
   * @return the isSuperGroup
   */
  public boolean isSuperGroup() {
    return this.superGroupFlag;
  }

  /**
   * @param isSuperGroup the isSuperGroup to set
   */
  public void setSuperGroup(final boolean isSuperGroup) {
    clearAllFlags();
    this.superGroupFlag = isSuperGroup;
  }

  /**
   * @return the isGroup
   */
  public boolean isGroup() {
    return this.groupFlag;
  }

  /**
   * @param isGroup the isGroup to set
   */
  public void setGroup(final boolean isGroup) {
    clearAllFlags();
    this.groupFlag = isGroup;
  }

  /**
   * @return the isCommon
   */
  public boolean isCommon() {
    return this.commonFlag;
  }

  /**
   * @param isCommon the isCommon to set
   */
  public void setCommon(final boolean isCommon) {
    clearAllFlags();
    this.commonFlag = isCommon;

  }

  /**
   * @param ucItem use case item
   */
  public void setUseCaseItem(final IUseCaseItemClientBO ucItem) {
    this.ucItemObj = ucItem;
    clearAllFlags();
    this.ucItemFlag = true;
    setSelectedNode(ucItem.getName());
  }

  /**
   * {@inheritDoc}
   */
  public void setSelectedNode(final String filterText) {
    // The filter text is same as the selected node in the Outline tree.
    this.selectedNode = filterText;
  }

  /**
   * {@inheritDoc} filter elements based on selection in outline view
   */
  protected boolean selectElement(final Object element) {
    // Check if Attribute object

    // Icdm-294
    Attribute attribute;
    if (element instanceof Attribute) {
      attribute = (Attribute) element;
      if (isAttrUnderSelectedSuperGroup(attribute) || isAttrUnderSelectedGroup(attribute) || this.commonFlag) {
        return true;
      }
      else if (this.ucItemFlag) {
        // if an usecase item is selected, check if the attribute is mapped to uc item
        return this.ucItemObj.isMapped(attribute);
      }
    }

    return false;

  }

  /**
   * @param attribute Attribute
   * @return true if the attribute is grouped under the selected group
   */
  private boolean isAttrUnderSelectedGroup(final Attribute attribute) {
    return this.groupFlag &&
        this.attrGroupModel.getAllGroupMap().get(attribute.getAttrGrpId()).getName().equals(this.selectedNode);
  }

  /**
   * @param attribute Attribute
   * @return true if the attribute is grouped under the selected super group
   */
  private boolean isAttrUnderSelectedSuperGroup(final Attribute attribute) {
    return this.superGroupFlag && this.attrGroupModel.getAllSuperGroupMap()
        .get(this.attrGroupModel.getAllGroupMap().get(attribute.getAttrGrpId()).getSuperGrpId()).getName()
        .equals(this.selectedNode);
  }

  private class UseCaseNatInputMatcher<E> implements Matcher<E> {

    /** Singleton instance of TrueMatcher. */

    /** {@inheritDoc} */
    @Override
    public boolean matches(final E element) {
      Attribute attribute = getAttributeFromTableRow(element);
      if (null != attribute && checkIfTableRowIsValid(element, attribute)) {
        // check with the selection in outline , whether the attribute is to be shown or not
        
        if (isAttrUnderSelectedSuperGroup(attribute) || isAttrUnderSelectedGroup(attribute) ||
            OutlineUCNatFilter.this.commonFlag) {
          return true;
        }
        
        else if (OutlineUCNatFilter.this.ucItemFlag) {
          return new OutLineUseCaseFilterHandler(OutlineUCNatFilter.this.ucItemObj,
              OutlineUCNatFilter.this.outLineViewDataHandler.getUcDataHandler()).isMapped(attribute);
        }
        else if (OutlineUCNatFilter.this.isFavUcItem) {
          return OutlineUCNatFilter.this.favUcItem.isMapped(attribute);
        }

      }
      

      return false;

    }

    /**
     * @param element E
     * @param attribute Attribute
     * @return true if the table row is a valid one
     */
    private boolean checkIfTableRowIsValid(final E element, final Attribute attribute) {
      return ((element instanceof UseCaseEditorRowAttr) || (element instanceof FocusMatrixAttributeClientBO)) &&
          (null != attribute);
    }

    /**
     * @param element E
     * @param attribute Attribute
     * @return
     */
    private Attribute getAttributeFromTableRow(final E element) {
      Attribute rowAttr = null;
      if (element instanceof UseCaseEditorRowAttr) {
        // get the attribute in case of usecase nat page input
        UseCaseEditorRowAttr useCaseNatInput = (UseCaseEditorRowAttr) element;
        AttributeClientBO rowAttrBO = useCaseNatInput.getAttributeBO();
        rowAttr = rowAttrBO.getAttribute();
      }
      else if (element instanceof FocusMatrixAttributeClientBO) {
        // get the attribute in case of focus matrix page input
        FocusMatrixAttributeClientBO focusMatrixAttr = (FocusMatrixAttributeClientBO) element;
        rowAttr = focusMatrixAttr.getAttribute();
      }
      return rowAttr;
    }
  }

  /**
   * @return the ucItem
   */
  public IUseCaseItemClientBO getUcItem() {
    return this.ucItemObj;
  }

  /**
   * @return Matcher
   */
  public Matcher getUcOutlineMatcher() {
    if (this.isUseCaseEditor) {
      return new UseCaseNatInputMatcher<UseCaseEditorRowAttr>();
    }

    return new UseCaseNatInputMatcher<FocusMatrixAttributeClientBO>();
  }


  /**
   * ICDM-1035
   *
   * @param first FavUseCaseItemNode
   */
  public void setFavUseCaseItem(final FavUseCaseItemNode first) {
    this.favUcItem = first;
    clearAllFlags();
    this.isFavUcItem = true;
    setSelectedNode(this.favUcItem.getName());

  }

}
