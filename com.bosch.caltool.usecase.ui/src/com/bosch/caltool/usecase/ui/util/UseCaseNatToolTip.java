/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.usecase.ui.util;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.grid.GridRegion;
import org.eclipse.nebula.widgets.nattable.layer.LayerUtil;
import org.eclipse.nebula.widgets.nattable.layer.cell.CellDisplayConversionUtils;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.painter.cell.ICellPainter;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.tooltip.NatTableContentTooltip;
import org.eclipse.swt.widgets.Event;

import com.bosch.caltool.apic.ui.util.ApicUiConstants;
import com.bosch.caltool.icdm.client.bo.apic.AttributeClientBO;
import com.bosch.caltool.icdm.client.bo.uc.IUseCaseItemClientBO;
import com.bosch.caltool.icdm.client.bo.uc.UseCaseEditorRowAttr;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.AttrGroup;
import com.bosch.caltool.icdm.model.apic.attr.AttrGroupModel;
import com.bosch.caltool.icdm.model.apic.attr.AttrNValueDependency;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.nattable.CustomFilterGridLayer;
import com.bosch.caltool.usecase.ui.editors.UseCaseEditorInput;
import com.bosch.caltool.usecase.ui.editors.pages.UseCaseNatAttributesPage;


/**
 * @author rgo7cob Icdm-1208- Change the tool tip for Nat table Items.
 */
public class UseCaseNatToolTip extends NatTableContentTooltip {


  /**
   * Constant for Attribute
   */
  private static final String ATTRIBUTE = "Attribute";

  /** Constant for Use Case Items column heading **/
  private static final String USE_CASE_ITEMS = "Use Case Items";
  /**
   * Tooltip to be shown for ANY column
   */
  private static final String ANY_COL_TOOLTIP_VAL = "Mapped to ANY Use Case Item";

  private final SortedSet<AttributeClientBO> attributesInput;
  private final UseCaseEditorInput useCaseEditorInput;

  private Map<Long, String> ucItemWithHierarchyMap = new HashMap<>();

  private final UseCaseNatAttributesPage ucNatPage;

  /**
   * @param useCaseNatAttributesPage natTable
   * @param tooltipRegions tooltipRegions
   * @param attributesInput SortedSet<AttributeClientBO>
   * @param useCaseEditorInput useCaseEditorInput
   */
  public UseCaseNatToolTip(final UseCaseNatAttributesPage useCaseNatAttributesPage, final String[] tooltipRegions,
      final SortedSet<AttributeClientBO> attributesInput, final UseCaseEditorInput useCaseEditorInput) {
    super(useCaseNatAttributesPage.getNatTable(), tooltipRegions);
    this.ucNatPage = useCaseNatAttributesPage;
    this.attributesInput = attributesInput == null ? null : new TreeSet<>(attributesInput);
    this.useCaseEditorInput = useCaseEditorInput;
    this.ucItemWithHierarchyMap = useCaseNatAttributesPage.getUseCase().getUcItemIdsWithHierarchyMap();
  }


  /**
   * {@inheritDoc} Overriding the GetText method for Changing the Contants.
   */
  @Override
  protected String getText(final Event event) {

    int col = this.natTable.getColumnPositionByX(event.x);
    // get the actual column, row number of the NAT table
    int actualCol = LayerUtil.convertColumnPosition(this.natTable, this.natTable.getColumnPositionByX(event.x),
        ((CustomFilterGridLayer<UseCaseEditorRowAttr>) this.natTable.getLayer()).getDummyDataLayer());
    int row = LayerUtil.convertRowPosition(this.natTable, this.natTable.getRowPositionByY(event.y),
        ((CustomFilterGridLayer<UseCaseEditorRowAttr>) this.natTable.getLayer()).getDummyDataLayer());

    ILayerCell cell = this.natTable.getCellByPosition(this.natTable.getColumnPositionByX(event.x),
        this.natTable.getRowPositionByY(event.y));
    if (cell != null) {
      // if the registered cell painter is the PasswordCellPainter, there will be no tooltip
      ICellPainter painter = this.natTable.getConfigRegistry().getConfigAttribute(CellConfigAttributes.CELL_PAINTER,
          DisplayMode.NORMAL, cell.getConfigLabels().getLabels());
      if (isVisibleContentPainter(painter)) {
        String tooltipValue = CellDisplayConversionUtils.convertDataType(cell, this.natTable.getConfigRegistry());
        // show tooltip for the mandatory column
        boolean isColHeader = cell.getConfigLabels().getLabels().contains(GridRegion.COLUMN_HEADER);
        if ((actualCol == UseCaseNatAttributesPage.COLUMN_NUM_BALL) && (col != 0) && !isColHeader) {
          CustomFilterGridLayer<UseCaseEditorRowAttr> customFilterGridLayer =
              (CustomFilterGridLayer<UseCaseEditorRowAttr>) cell.getLayer();

          UseCaseEditorRowAttr useCaseNatInput = customFilterGridLayer.getBodyDataProvider().getRowObject(row);
          tooltipValue = getColumnTooltipValue(useCaseNatInput);
        }
        // For the Column number 2 change the Tool tip text.(For Attribute get the Super group and Group name)
        else if ((actualCol == UseCaseNatAttributesPage.ATTR_COLUMN_IDX) && !ATTRIBUTE.equals(tooltipValue)) {
          tooltipValue = modifyToolTip(tooltipValue);
        }
        // for ANY column modify tooltip
        else if ((actualCol == UseCaseNatAttributesPage.ANY_COLUMN_IDX) && isColHeader) {
          tooltipValue = ANY_COL_TOOLTIP_VAL;
        }
        // show complete parent hierarchy for all use case items
        else if ((actualCol >= UseCaseNatAttributesPage.STATIC_COLUMN_INDEX) && isColHeader &&
            !USE_CASE_ITEMS.equals(tooltipValue)) {
          tooltipValue = getTooltipForUCItems(actualCol);
        }
        if (tooltipValue.length() > 0) {
          return tooltipValue;
        }
      }
    }
    return null;
  }

  /**
   * @param row
   * @param col
   * @param cell
   * @return
   */
  private String getTooltipForUCItems(final int col) {
    IUseCaseItemClientBO ucItem = this.ucNatPage.getColumnUseCaseItemMapping().get(col);
    String ucWithHierarchyName = this.ucItemWithHierarchyMap.get(ucItem.getUcItem().getId());
    return ucWithHierarchyName != null ? ucWithHierarchyName : "";
  }

  /**
   * @param useCaseNatInput
   * @return
   */
  private String getColumnTooltipValue(final UseCaseEditorRowAttr useCaseNatInput) {
    StringBuilder toolipTextBuilder = new StringBuilder();

    // Summary tooltip
    toolipTextBuilder.append(createSummaryToolTip(useCaseNatInput));

    // Add dependencies tooltip
    appendDependenciesToolTip(useCaseNatInput, toolipTextBuilder);

    return toolipTextBuilder.toString();
  }

  /**
   * @param useCaseNatInput
   * @param tooltipText
   */
  private void appendDependenciesToolTip(final UseCaseEditorRowAttr useCaseNatInput, final StringBuilder tooltipText) {

    SortedSet<String> depAttrUsed = new TreeSet<>(new CustomComparator());
    SortedSet<String> depAttrVal = new TreeSet<>(new CustomComparator());

    Attribute attribute = useCaseNatInput.getAttributeBO().getAttribute();

    if (CommonUtils.isNotEmpty(
        this.useCaseEditorInput.getUseCaseEditorModel().getAttrRefDependenciesMap().get(attribute.getId()))) {
      if (!tooltipText.toString().isEmpty()) {
        tooltipText.append(System.lineSeparator());
      }

      Set<AttrNValueDependency> depenAttr =
          this.useCaseEditorInput.getUseCaseEditorModel().getAttrRefDependenciesMap().get(attribute.getId());

      if (CommonUtils.isNotEmpty(depenAttr)) {
        // Iterate all referenctial dependencies
        for (AttrNValueDependency attrDependency : depenAttr) {
          buildToolTipTextFromDepAttr(depAttrUsed, depAttrVal, attrDependency);
        }
      }
      // build tooltip for visible dependent attrs
      buildToolTipForDepnAttrs(tooltipText, depAttrUsed, depAttrVal);
    }

  }

  /**
   * @param tooltipText
   * @param depAttrUsed
   * @param depAttrVal
   */
  private void buildToolTipForDepnAttrs(final StringBuilder tooltipText, final SortedSet<String> depAttrUsed,
      final SortedSet<String> depAttrVal) {
    if (!depAttrUsed.isEmpty()) {
      StringBuilder usedAttr = new StringBuilder();
      for (String attrDependency : depAttrUsed) {
        usedAttr.append(attrDependency);
        usedAttr.append(System.lineSeparator());
      }
      tooltipText.append(CommonUiUtils.getMessage(ApicUiConstants.PIDC_EDITOR_GROUP, "USED_DEPENDENT_ATTR_TOOLTIP",
          usedAttr.toString()));
    }
    if (!depAttrVal.isEmpty()) {
      StringBuilder valAttr = new StringBuilder();
      for (String attrDependency : depAttrVal) {
        valAttr.append(attrDependency);
        valAttr.append(System.lineSeparator());
      }
      tooltipText.append(CommonUiUtils.getMessage(ApicUiConstants.PIDC_EDITOR_GROUP, "VAL_DEPENDENT_ATTR_TOOLTIP",
          valAttr.toString()));
    }
  }

  /**
   * @param depAttrUsed
   * @param depAttrVal
   * @param attrDependency
   */
  private void buildToolTipTextFromDepAttr(final SortedSet<String> depAttrUsed, final SortedSet<String> depAttrVal,
      final AttrNValueDependency attrDependency) {
    if ((attrDependency != null) && (attrDependency.getAttributeId() != null)) {
      Attribute depAttribute =
          this.useCaseEditorInput.getUseCaseEditorModel().getAttrMap().get(attrDependency.getAttributeId());
      if (null != depAttribute) {
        if (attrDependency.getDependentValueId() == null) {
          depAttrUsed.add("[" + depAttribute.getName() + "]");
        }
        else {
          AttributeValue attributeValue = this.useCaseEditorInput.getUseCaseEditorModel().getAttrDepValMap()
              .get(attrDependency.getDependentValueId());
          depAttrVal.add("[" + depAttribute.getName() + "] , if { value  = " +
              (null == attributeValue ? "" : attributeValue.getName()) + "}");
        }
      }
    }
  }

  /**
   * Compare Attribute names with collator
   */
  class CustomComparator implements Comparator<String> {

    @Override
    public int compare(final String attrName1, final String attrName2) {
      return com.bosch.caltool.icdm.common.util.ApicUtil.compare(attrName1, attrName2);
    }
  }

  /**
   * @param useCaseNatInput
   * @return
   */
  private String createSummaryToolTip(final UseCaseEditorRowAttr useCaseNatInput) {
    StringBuilder tooltip = new StringBuilder();
    // Tooltip for mandatory
    Attribute attribute = useCaseNatInput.getAttributeBO().getAttribute();
    if (attribute.isMandatory()) {
      tooltip.append(CommonUiUtils.getMessage(ApicUiConstants.PIDC_EDITOR_GROUP, "ATTR_MANDATORY"));
    }
    // Tooltip for dependent
    else if (useCaseNatInput.hasAttrDependencies()) {
      tooltip.append(CommonUiUtils.getMessage(ApicUiConstants.PIDC_EDITOR_GROUP, "ATTR_DEPENDENT"));
    }

    // Add hyperlinks tooltip
    if (!tooltip.toString().isEmpty() &&
        this.useCaseEditorInput.getUseCaseEditorModel().getLinkSet().contains(attribute.getId())) {
      tooltip.append(CommonUiUtils.getMessage(ApicUiConstants.PIDC_EDITOR_GROUP, "ATTR_ALSO_HYPERLINKS"));
    }
    else if (this.useCaseEditorInput.getUseCaseEditorModel().getLinkSet().contains(attribute.getId())) {
      tooltip.append(CommonUiUtils.getMessage(ApicUiConstants.PIDC_EDITOR_GROUP, "ATTR_HYPERLINKS"));
    }
    return tooltip.toString();
  }

  /**
   * @param col
   * @param tooltipValue
   * @return the modified too tip
   */
  private String modifyToolTip(final String tooltipValue) {
    String modifiedToolTip = "";
    // For the Column 1 alone. It is Attribute name.
    if (tooltipValue.length() > 0) {
      // Get all the Attribute from the Apic data provider.
      for (AttributeClientBO attr : this.attributesInput) {
        // If the Attribute name Eqauls the tool tip Text then get the Super Group and Group names
        if (ApicUtil.compare(tooltipValue, attr.getName()) == ApicConstants.OBJ_EQUAL_CHK_VAL) {
          // Get the Values from the formatter.
          AttrGroupModel attrGroupModel = this.useCaseEditorInput.getAttrGroupModel();
          AttrGroup attrGroup = attrGroupModel.getAllGroupMap().get(attr.getAttribute().getAttrGrpId());
          modifiedToolTip = CommonUiUtils.getMessage("USE_CASE", "ATTR_NAME", tooltipValue,
              attrGroupModel.getAllSuperGroupMap().get(attrGroup.getSuperGrpId()).getName(), attrGroup.getName());
          break;
        }
      }
    }
    return modifiedToolTip;
  }
}
