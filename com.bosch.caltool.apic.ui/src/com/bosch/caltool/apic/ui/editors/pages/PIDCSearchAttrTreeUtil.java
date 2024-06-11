/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.editors.pages;

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TreeItem;

import com.bosch.caltool.apic.ui.views.providers.PIDCSearchContentProvider;
import com.bosch.caltool.icdm.client.bo.apic.PidcSearchEditorDataHandler;
import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;


/**
 * This class contains methods realted to attributes tree in PIDC search editor
 *
 * @author bru2cob
 */
public class PIDCSearchAttrTreeUtil {

  /**
   * Search page instance
   */
  private final PIDCSearchPage searchPg;

  /**
   * @param searchPg pidc search page instance
   */
  public PIDCSearchAttrTreeUtil(final PIDCSearchPage searchPg) {
    super();
    this.searchPg = searchPg;
  }

  /**
   * sets the checked state of attr
   */
  public void setCheckedStateOnRefresh() {
    TreeItem[] items = this.searchPg.getAttrSection().getSummaryTreeViewer().getTree().getItems();
    // all the items in the tree's checked state is checked
    for (TreeItem item : items) {
      Object obj = item.getData();
      if (obj instanceof Attribute) {
        setAttrCheckState(item, obj);
      }
    }
  }

  /**
   * Sets attr checkbox state
   *
   * @param item tree item
   * @param obj selected attr
   */
  private void setAttrCheckState(final TreeItem item, final Object obj) {
    Attribute attr = (Attribute) obj;
    // check whether the attr val is already checked
    if (getDataHandler().isAttrValueSelected(attr)) {
      // if attr is in in partially checked map , it is set as partially checked
      item.setGrayed(getDataHandler().isAttrSelectedPartially(attr));
      // attr is checked (all values of it are included)
      item.setChecked(true);

      // check for attr values check state and maintain as it was set before.
      getDataHandler().getSelAttrValues(attr)
          .forEach(val -> this.searchPg.getAttrSection().getSummaryTreeViewer().setChecked(val, true));

    }
  }

  /**
   * Sets the used flag state of attrs when UI is refreshed.
   */
  public void setUsedFlagStateOnRefresh() {
    // intially all the checkbox are set to unckecked state
    for (TreeItem item : this.searchPg.getAttrSection().getSummaryTreeViewer().getTree().getItems()) {
      // used-flag - undef (col 1)
      item.setImage(CommonUIConstants.COLUMN_INDEX_1,
          ImageManager.getInstance().getRegisteredImage(ImageKeys.CHECKBOX_NO_16X16));
      // used-flag - yes (col 3)
      item.setImage(CommonUIConstants.COLUMN_INDEX_3,
          ImageManager.getInstance().getRegisteredImage(ImageKeys.CHECKBOX_NO_16X16));
      // used-flag - no (col 2)
      item.setImage(CommonUIConstants.COLUMN_INDEX_2,
          ImageManager.getInstance().getRegisteredImage(ImageKeys.CHECKBOX_NO_16X16));

      // if the attr is present in the map then the checkbox is set to checked state
      String usedType = getDataHandler().getSelAttrUsed((Attribute) item.getData());
      // get the used state of the attr
      if (ApicConstants.PROJ_ATTR_USED_FLAG.NOT_DEFINED.getUiType().equalsIgnoreCase(usedType)) {
        item.setImage(CommonUIConstants.COLUMN_INDEX_1,
            ImageManager.getInstance().getRegisteredImage(ImageKeys.CHECKBOX_YES_16X16));
      }
      else if (ApicConstants.PROJ_ATTR_USED_FLAG.NO.getUiType().equalsIgnoreCase(usedType)) {
        item.setImage(CommonUIConstants.COLUMN_INDEX_2,
            ImageManager.getInstance().getRegisteredImage(ImageKeys.CHECKBOX_YES_16X16));
      }
      else if (ApicConstants.PROJ_ATTR_USED_FLAG.YES.getUiType().equalsIgnoreCase(usedType)) {
        item.setImage(CommonUIConstants.COLUMN_INDEX_3,
            ImageManager.getInstance().getRegisteredImage(ImageKeys.CHECKBOX_YES_16X16));
      }
    }

  }

  private PidcSearchEditorDataHandler getDataHandler() {
    return this.searchPg.getDataHandler();
  }

  /**
   * Sets the checkbox state of attrs when user checks the chekbox
   */
  private void setUsedFlagCheckState(final TreeItem item, final int columnIndex) {
    // attr for which user has set the state
    Attribute attr = (Attribute) item.getData();
    String usedState = "";
    // if user checks the used flag for an attr
    if (item.getImage(columnIndex).equals(ImageManager.getInstance().getRegisteredImage(ImageKeys.CHECKBOX_NO_16X16))) {
      // get the used state of the attr
      if (columnIndex == CommonUIConstants.COLUMN_INDEX_1) {
        usedState = ApicConstants.PROJ_ATTR_USED_FLAG.NOT_DEFINED.getUiType();
      }
      else if (columnIndex == CommonUIConstants.COLUMN_INDEX_2) {
        usedState = ApicConstants.PROJ_ATTR_USED_FLAG.NO.getUiType();
      }
      else if (columnIndex == CommonUIConstants.COLUMN_INDEX_3) {
        usedState = ApicConstants.PROJ_ATTR_USED_FLAG.YES.getUiType();
      }
      /**
       * Setting no to other columns since only one checkbox should be checked
       */
      item.setImage(CommonUIConstants.COLUMN_INDEX_1,
          ImageManager.getInstance().getRegisteredImage(ImageKeys.CHECKBOX_NO_16X16));
      item.setImage(CommonUIConstants.COLUMN_INDEX_3,
          ImageManager.getInstance().getRegisteredImage(ImageKeys.CHECKBOX_NO_16X16));
      item.setImage(CommonUIConstants.COLUMN_INDEX_2,
          ImageManager.getInstance().getRegisteredImage(ImageKeys.CHECKBOX_NO_16X16));

      // uncheck the attr and attr values in the tree since used flag is set
      this.searchPg.getAttrSection().getSummaryTreeViewer().setSubtreeChecked(attr, false);

      // Add the attr to used map
      getDataHandler().setSelAttrUsed(attr, usedState);
      // set the checkbox to check state
      item.setImage(columnIndex, ImageManager.getInstance().getRegisteredImage(ImageKeys.CHECKBOX_YES_16X16));
    }
    else {
      // if attr checkbox is checked , uncheck checkbox image is replaced
      getDataHandler().removeSelAttrUsed(attr);
      item.setImage(columnIndex, ImageManager.getInstance().getRegisteredImage(ImageKeys.CHECKBOX_NO_16X16));
    }
  }


  /**
   * Add modify listener to the attr tree
   */
  void addModifyTextListener(final Text filterTxt, final AbstractViewerFilter filter,
      final CheckboxTreeViewer summaryTreeViewer2) {
    filterTxt.addModifyListener(new ModifyListener() {

      @Override
      public void modifyText(final ModifyEvent event) {
        String text = filterTxt.getText().trim();
        filter.setFilterText(text);
        summaryTreeViewer2.refresh();
      }
    });
  }

  /**
   * Setting the values once an attribute or attribute value is checked in attrs treeviewer
   *
   * @param checkstatechangedevent event
   * @param element element changed
   * @param tcp contenet provider
   */
  public void setCheckStateVals(final CheckStateChangedEvent checkstatechangedevent, final Object element,
      final PIDCSearchContentProvider tcp) {
    // check instanceof attr/attr-val
    if (element instanceof Attribute) {
      setAttrState(checkstatechangedevent, element);
    }
    if (element instanceof AttributeValue) {
      setAttrValState(element, tcp);
    }
  }

  /**
   * Setting the selected attr-val and its correspondng attr
   *
   * @param element element
   * @param tcp content provider
   */
  public void setAttrValState(final Object element, final PIDCSearchContentProvider tcp) {
    getDataHandler().selectAttributeValue((AttributeValue) element);

    Object parentElement = tcp.getParent(element);
    Attribute attr = (Attribute) parentElement;

    /**
     * If a attr val is choosed , its attr's used flag's should not be checked. Unselect the used flag
     */
    for (TreeItem item : this.searchPg.getAttrSection().getSummaryTreeViewer().getTree().getItems()) {
      Attribute currentAttr = (Attribute) item.getData();
      if (currentAttr.equals(attr)) {
        item.setImage(CommonUIConstants.COLUMN_INDEX_1,
            ImageManager.getInstance().getRegisteredImage(ImageKeys.CHECKBOX_NO_16X16));
        item.setImage(CommonUIConstants.COLUMN_INDEX_3,
            ImageManager.getInstance().getRegisteredImage(ImageKeys.CHECKBOX_NO_16X16));
        item.setImage(CommonUIConstants.COLUMN_INDEX_2,
            ImageManager.getInstance().getRegisteredImage(ImageKeys.CHECKBOX_NO_16X16));

        break;
      }
    }
    this.searchPg.getAttrSection().getSummaryTreeViewer().setChecked(parentElement, true);
    this.searchPg.getAttrSection().getSummaryTreeViewer().setGrayed(parentElement,
        getDataHandler().isAttrSelectedPartially(attr));

  }

  /**
   * Setting the attribute and adding the attr and its value to attr-val map
   *
   * @param checkstatechangedevent state
   * @param element attr
   */
  public void setAttrState(final CheckStateChangedEvent checkstatechangedevent, final Object element) {
    Attribute attr = (Attribute) element;
    /**
     * If a attr is choosed , its used flag's should not be checked. Unselect the used flag
     */
    for (TreeItem item : this.searchPg.getAttrSection().getSummaryTreeViewer().getTree().getItems()) {
      Attribute currentAttr = (Attribute) item.getData();
      if (currentAttr.equals(attr)) {
        item.setImage(CommonUIConstants.COLUMN_INDEX_1,
            ImageManager.getInstance().getRegisteredImage(ImageKeys.CHECKBOX_NO_16X16));
        item.setImage(CommonUIConstants.COLUMN_INDEX_3,
            ImageManager.getInstance().getRegisteredImage(ImageKeys.CHECKBOX_NO_16X16));
        item.setImage(CommonUIConstants.COLUMN_INDEX_2,
            ImageManager.getInstance().getRegisteredImage(ImageKeys.CHECKBOX_NO_16X16));
        break;
      }
    }

    getDataHandler().selectAllValsOfAttr(attr);


    this.searchPg.getAttrSection().getSummaryTreeViewer().setGrayed(checkstatechangedevent.getElement(), false);
    this.searchPg.getAttrSection().getSummaryTreeViewer().setSubtreeChecked(checkstatechangedevent.getElement(), true);
  }

  /**
   * Setting the values once an attribute or attribute value is unchecked in attrs treeviewer
   *
   * @param checkstatechangedevent event
   * @param element element changed
   * @param tcp contenet provider
   */
  public void setUnCheckStateVals(final CheckStateChangedEvent checkstatechangedevent, final Object element,
      final PIDCSearchContentProvider tcp) {
    // attr state changed
    if (element instanceof Attribute) {
      getDataHandler().uncheckAttribute((Attribute) element);
      this.searchPg.getAttrSection().getSummaryTreeViewer().setSubtreeChecked(checkstatechangedevent.getElement(),
          false);
    }
    // attr val state changed
    if (element instanceof AttributeValue) {
      getDataHandler().uncheckAttributeValue((AttributeValue) element);

      Object parentElement = tcp.getParent(element);
      Attribute attr = (Attribute) parentElement;

      this.searchPg.getAttrSection().getSummaryTreeViewer().setChecked(parentElement,
          getDataHandler().isAttrValueSelected(attr));
      this.searchPg.getAttrSection().getSummaryTreeViewer().setGrayed(parentElement,
          getDataHandler().isAttrSelectedPartially(attr));

    }
  }

  /**
   * When a checkbox is checked/unchecked it will be listened
   *
   * @param columnIndex column index
   * @param point position
   * @param item item
   */
  public void mouseDownOp(final int columnIndex, final Point point, final TreeItem item) {
    if ((item.getImage(columnIndex) != null) && !item.isDisposed()) {
      int colCount = this.searchPg.getAttrSection().getSummaryTreeViewer().getTree().getColumnCount();
      // Determine which column was selected
      for (int i = 0; i < colCount; i++) {
        final Rectangle rect = item.getBounds(i);
        if (rect.contains(point)) {
          // set the used flag state of the attr
          setUsedFlagCheckState(item, columnIndex);
          // update the count once the checkstate changes
          this.searchPg.updateCount();
          break;

        }
      }
    }
  }

}
