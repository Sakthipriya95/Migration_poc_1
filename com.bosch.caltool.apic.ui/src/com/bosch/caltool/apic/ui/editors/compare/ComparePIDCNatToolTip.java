/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.editors.compare;

import java.util.Comparator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.nebula.widgets.nattable.NatTable;
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
import com.bosch.caltool.icdm.client.bo.apic.AbstractProjectAttributeBO;
import com.bosch.caltool.icdm.client.bo.apic.AttributeClientBO;
import com.bosch.caltool.icdm.client.bo.apic.ProjectAttributeUtil;
import com.bosch.caltool.icdm.client.bo.apic.pidc.ComparePidcHandler;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.attr.AttrNValueDependency;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectAttribute;
import com.bosch.caltool.icdm.ws.rest.client.apic.AttributeValueServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.nattable.CustomFilterGridLayer;


/**
 * This class is used for tooltip of pidc compare page
 *
 * @author jvi6cob
 */
public class ComparePIDCNatToolTip extends NatTableContentTooltip {

  /**
   * compare editor handler
   */
  private final ComparePidcHandler comparePidcHandler;

  /**
   * ProjectAttributeUtil instance
   */
  private final ProjectAttributeUtil compareEditorUtil = new ProjectAttributeUtil();

  /**
   * @param natTable natTable
   * @param tooltipRegions tooltipRegions
   */
  public ComparePIDCNatToolTip(final NatTable natTable, final String[] tooltipRegions,
      final ComparePidcHandler comparePidcHandler) {
    super(natTable, tooltipRegions);
    this.comparePidcHandler = comparePidcHandler;

  }

  /**
   * {@inheritDoc} Overriding the GetText method for Changing the Contants.
   */
  @Override
  protected String getText(final Event event) {
    // get the row and col numbers
    int col = this.natTable.getColumnPositionByX(event.x);
    int row = LayerUtil.convertRowPosition(this.natTable, this.natTable.getRowPositionByY(event.y),
        ((CustomFilterGridLayer<CompareRowObject>) this.natTable.getLayer()).getDummyDataLayer());
    // get the cell from nat table
    ILayerCell cell = this.natTable.getCellByPosition(col, this.natTable.getRowPositionByY(event.y));
    if ((cell != null) && !cell.getConfigLabels().getLabels().contains(GridRegion.COLUMN_HEADER)) {
      // if the registered cell painter is the PasswordCellPainter, there will be no tooltip
      ICellPainter painter = this.natTable.getConfigRegistry().getConfigAttribute(CellConfigAttributes.CELL_PAINTER,
          DisplayMode.NORMAL, cell.getConfigLabels().getLabels());
      if (isVisibleContentPainter(painter)) {
        String tooltipValue = CellDisplayConversionUtils.convertDataType(cell, this.natTable.getConfigRegistry());
        // For the Column number 1 change the Tool tip text.(For Attribute get the Super group and Group name)
        StringBuilder toolTipTextBuilder = new StringBuilder();
        CustomFilterGridLayer<CompareRowObject> customFilterGridLayer =
            (CustomFilterGridLayer<CompareRowObject>) cell.getLayer();
        CompareRowObject compareRowObject = customFilterGridLayer.getBodyDataProvider().getRowObject(row);

        com.bosch.caltool.icdm.model.apic.attr.Attribute attribute = compareRowObject.getAttribute();
        if (col == CommonUIConstants.COLUMN_INDEX_1) {
          boolean isAnyPIDCAttrInVisible = isAttributeVisible(compareRowObject);
          // Summary tooltip
          toolTipTextBuilder.append(createSummaryToolTip(attribute, isAnyPIDCAttrInVisible));
          // Add dependencies tooltip
          // ICDM-2636
          appendDependenciesToolTip(compareRowObject, toolTipTextBuilder, isAnyPIDCAttrInVisible);
          // add tooltip for grouped attribute
          appendGrpdAttrToolTip(compareRowObject.getColumnDataMapper().getIpidcAttrs(), toolTipTextBuilder);
          // add tooltip fro pre defined attribute
          appendPredefAttrToolTip(compareRowObject.getColumnDataMapper().getIpidcAttrs(), toolTipTextBuilder);

          tooltipValue = toolTipTextBuilder.toString();
        }
        else {
          // add some more info to the tooltip
          tooltipValue = modifyToolTip(col, tooltipValue, attribute);
        }
        if (tooltipValue.length() > 0) {
          // if there is some info appended to the tooltip , then return the value
          return tooltipValue;
        }
      }
    }
    return null;

  }

  /**
   * @param ipidcAttrs
   * @param toolTipTextBuilder
   */
  private void appendGrpdAttrToolTip(final Set<IProjectAttribute> ipidcAttrs, final StringBuilder toolTipTextBuilder) {
    // ICDM-2636
    for (IProjectAttribute pidcAttr : ipidcAttrs) {
      if (this.comparePidcHandler.getPidcVersionBO().getPidcDataHandler().getAttributeMap().get(pidcAttr.getAttrId())
          .isGroupedAttr()) {
        toolTipTextBuilder.append(CommonUiUtils.getMessage(ApicUiConstants.PIDC_EDITOR_GROUP, "GROUPED_ATTR"));
        break;
      }
    }
  }

  /**
   * @param set
   * @param toolTipTextBuilder
   */
  private void appendPredefAttrToolTip(final Set<IProjectAttribute> ipidcAttrs,
      final StringBuilder toolTipTextBuilder) {

    for (IProjectAttribute pidcAttr : ipidcAttrs) {
      if (this.comparePidcHandler.getPidcVersionBO().getPredefAttrGrpAttrMap().containsKey(pidcAttr)) {
        toolTipTextBuilder.append(CommonUiUtils.getMessage(ApicUiConstants.PIDC_EDITOR_GROUP, "PREDEFINED_ATTRS"));
        toolTipTextBuilder
            .append(this.comparePidcHandler.getPidcVersionBO().getPredefAttrGrpAttrMap().get(pidcAttr).getName());
        break;
      }
    }
  }

  /**
   * Method which checks whether all the pidcAttrs against a given attribute is visible
   *
   * @param compareRowObject
   * @return
   */
  private boolean isAttributeVisible(final CompareRowObject compareRowObject) {
    Set<IProjectAttribute> ipidcAttrs = compareRowObject.getColumnDataMapper().getIpidcAttrs();
    boolean isAnyIPIDCAttrInVisible = false;
    for (IProjectAttribute ipidcAttribute : ipidcAttrs) {
      AbstractProjectAttributeBO handler = this.compareEditorUtil.getProjectAttributeHandler(ipidcAttribute,
          this.comparePidcHandler.getCompareObjectsHandlerMap().get(this.compareEditorUtil.getID(ipidcAttribute)));
      if (!handler.isVisible()) {
        isAnyIPIDCAttrInVisible = true;
        break;
      }
    }
    return isAnyIPIDCAttrInVisible;
  }


  /**
   * @param isAnyIPIDCAttrInVisible
   * @param get
   * @return
   */
  private String createSummaryToolTip(final com.bosch.caltool.icdm.model.apic.attr.Attribute attribute,
      final boolean isAnyIPIDCAttrInVisible) {
    StringBuilder tooltip = new StringBuilder();
    // Tooltip for invisible attrs
    if (isAnyIPIDCAttrInVisible) {
      tooltip.append(CommonUiUtils.getMessage(ApicUiConstants.PIDC_EDITOR_GROUP, "ATTR_INVISIBLE"));
    }
    // Tooltip for mandatory
    else if (attribute.isMandatory()) {
      tooltip.append(CommonUiUtils.getMessage(ApicUiConstants.PIDC_EDITOR_GROUP, "ATTR_MANDATORY"));
    } // Tooltip for dependent
    else if (hasAttrDependencies(attribute)) {
      tooltip.append(CommonUiUtils.getMessage(ApicUiConstants.PIDC_EDITOR_GROUP, "ATTR_DEPENDENT"));
    }
    // Add hyperlinks tooltip
    if (!tooltip.toString().isEmpty() &&
        !this.comparePidcHandler.getPidcVersionBO().getPidcDataHandler().getLinks().contains(attribute.getId())) {
      tooltip.append(CommonUiUtils.getMessage(ApicUiConstants.PIDC_EDITOR_GROUP, "ATTR_ALSO_HYPERLINKS"));
    }
    else if (this.comparePidcHandler.getPidcVersionBO().getPidcDataHandler().getLinks().contains(attribute.getId())) {
      tooltip.append(CommonUiUtils.getMessage(ApicUiConstants.PIDC_EDITOR_GROUP, "ATTR_HYPERLINKS"));
    }

    return tooltip.toString();
  }


  private boolean hasAttrDependencies(final com.bosch.caltool.icdm.model.apic.attr.Attribute attribute) {
    Set<AttrNValueDependency> depenAttr = this.comparePidcHandler.getPidcVersionBO().getPidcDataHandler()
        .getAttrRefDependenciesMap().get(attribute.getId());
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
   * Append tooltip with dependent attributes
   *
   * @param attribute attribute
   * @param tooltipText tooltip
   * @param isAnyIPIDCAttrInVisible
   */
  private void appendDependenciesToolTip(final CompareRowObject compareRowObject, final StringBuilder tooltipText,
      final boolean isAnyIPIDCAttrInVisible) {
    SortedSet<String> depAttrUsed = new TreeSet<String>(new CustomComparator());
    SortedSet<String> depAttrVal = new TreeSet<String>(new CustomComparator());

    Attribute attribute = compareRowObject.getAttribute();

    // create tool tips for invisible attrs
    buildToolTipForInvisibleAttrs(attribute, tooltipText, isAnyIPIDCAttrInVisible);


    if (new AttributeClientBO(attribute).hasReferentialDependencies(this.comparePidcHandler.getPidcVersionBO(),
        attribute.getId())) {
      if (!tooltipText.toString().isEmpty()) {
        tooltipText.append(System.lineSeparator());
      }

      Set<AttrNValueDependency> depenAttr = this.comparePidcHandler.getPidcVersionBO().getPidcDataHandler()
          .getAttrRefDependenciesMap().get(attribute.getId());

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
   * @param depAttrUsed
   * @param depAttrVal
   * @param attrDependency
   */
  private void buildToolTipTextFromDepAttr(final SortedSet<String> depAttrUsed, final SortedSet<String> depAttrVal,
      final AttrNValueDependency attrDependency) {
    if ((attrDependency != null) && (attrDependency.getAttributeId() != null)) {
      if (attrDependency.getDependentValueId() == null) {
        depAttrUsed.add("[" + this.comparePidcHandler.getPidcVersionBO().getPidcDataHandler().getAttributeMap()
            .get(attrDependency.getAttributeId()).getName() + "]");
      }
      else {
        AttributeValue attrValue = null;
        if (this.comparePidcHandler.getPidcVersionBO().getPidcDataHandler().getAttributeValueMap()
            .get(attrDependency.getDependentValueId()) == null) {
          try {
            attrValue = new AttributeValueServiceClient().getById(attrDependency.getDependentValueId());
          }
          catch (ApicWebServiceException e) {
            CDMLogger.getInstance().error(e.getMessage(), e, com.bosch.caltool.apic.ui.Activator.PLUGIN_ID);
          }
        }
        else {
          attrValue = this.comparePidcHandler.getPidcVersionBO().getPidcDataHandler().getAttributeValueMap()
              .get(attrDependency.getDependentValueId());
        }
        if (attrValue != null) {
          depAttrVal
              .add("[" +
                  this.comparePidcHandler.getPidcVersionBO().getPidcDataHandler().getAttributeMap()
                      .get(attrDependency.getAttributeId()).getName() +
                  "] , if { value  = " + attrValue.getName() + "}");
        }
      }
    }
  }

  /**
   * @param pidcAttr
   * @param tooltipText
   * @param isAnyIPIDCAttrInVisible
   */
  private void buildToolTipForInvisibleAttrs(final com.bosch.caltool.icdm.model.apic.attr.Attribute attribute,
      final StringBuilder tooltipText, final boolean isAnyIPIDCAttrInVisible) {
    // Check if invisible
    if (isAnyIPIDCAttrInVisible) {
      // avoid unnecessary line feeds
      if (!tooltipText.toString().isEmpty()) {
        tooltipText.append(System.lineSeparator());
      }
      String attrDepen = new AttributeClientBO(attribute)
          .getDependencies(this.comparePidcHandler.getPidcVersionBO().getPidcDataHandler());
      if (!attrDepen.isEmpty()) {
        tooltipText
            .append(CommonUiUtils.getMessage(ApicUiConstants.PIDC_EDITOR_GROUP, "INVISIBLE_ATTR_TOOLTIP", attrDepen));
      }
    }
  }

  /**
   * Build tooltip for visible dependant attrs
   *
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
      // get tooltip message from t_messages table
      tooltipText.append(CommonUiUtils.getMessage(ApicUiConstants.PIDC_EDITOR_GROUP, "USED_DEPENDENT_ATTR_TOOLTIP",
          usedAttr.toString()));
    }
    if (!depAttrVal.isEmpty()) {
      StringBuilder valAttr = new StringBuilder();
      for (String attrDependency : depAttrVal) {
        valAttr.append(attrDependency);
        valAttr.append(System.lineSeparator());
      }
      // get tooltip message from t_messages table
      tooltipText.append(CommonUiUtils.getMessage(ApicUiConstants.PIDC_EDITOR_GROUP, "VAL_DEPENDENT_ATTR_TOOLTIP",
          valAttr.toString()));
    }
  }

  /**
   * @param col
   * @param tooltipValue
   * @return the modified too tip
   */
  private String modifyToolTip(final int col, final String tooltipValue,
      final com.bosch.caltool.icdm.model.apic.attr.Attribute attr) {
    String modifiedToolTip = tooltipValue;
    // For the Column 2 alone. It is Attribute name.
    if ((col == 2) && (tooltipValue.length() > 0)) {
      // Get all the Attribute from the Apic data provider.
      // Get the Values from the formatter.
      modifiedToolTip = CommonUiUtils.getMessage("USE_CASE", "ATTR_NAME", tooltipValue,
          this.comparePidcHandler.getPidcVersionBO().getPidcDataHandler().getAttributeSuperGroupMap()
              .get(this.comparePidcHandler.getPidcVersionBO().getPidcDataHandler().getAttributeGroupMap()
                  .get(attr.getAttrGrpId()).getSuperGrpId())
              .getName(),
          this.comparePidcHandler.getPidcVersionBO().getPidcDataHandler().getAttributeGroupMap()
              .get(attr.getAttrGrpId()).getName());
    }
    return modifiedToolTip;
  }

  /**
   * Compare Attribute names with collator
   */
  class CustomComparator implements Comparator<String> {

    @Override
    public int compare(final String attrName1, final String attrName2) {
      // compare the attribute names
      return com.bosch.caltool.icdm.common.util.ApicUtil.compare(attrName1, attrName2);
    }
  }
}
