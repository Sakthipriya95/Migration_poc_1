/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.editors;

import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

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

import com.bosch.caltool.apic.ui.editors.compare.PidcNattableRowObject;
import com.bosch.caltool.apic.ui.util.ApicUiConstants;
import com.bosch.caltool.icdm.client.bo.apic.AbstractProjectAttributeBO;
import com.bosch.caltool.icdm.client.bo.apic.AbstractProjectObjectBO;
import com.bosch.caltool.icdm.client.bo.apic.AttributeClientBO;
import com.bosch.caltool.icdm.client.bo.apic.AttributeValueClientBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcDataHandler;
import com.bosch.caltool.icdm.client.bo.apic.PidcVersionBO;
import com.bosch.caltool.icdm.client.bo.apic.ProjectAttributeUtil;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.ApicConstants.CLEARING_STATUS;
import com.bosch.caltool.icdm.model.apic.attr.AttrNValueDependency;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;
import com.bosch.caltool.nattable.CustomFilterGridLayer;


/**
 * @author jvi6cob
 */
public class PIDCNatToolTip extends NatTableContentTooltip {

  /**
   * compare editor handler
   */
  private AbstractProjectObjectBO projObjBO;
  /**
   * pidc version bo instance
   */
  private final PidcVersionBO pidcVersionBO;
  /**
   * project attribuet util instance
   */
  private final ProjectAttributeUtil projAttrUtil = new ProjectAttributeUtil();

  private static final String STATE_TT_LABEL = "\nState : ";

  /**
   * @param natTable natTable
   * @param tooltipRegions tooltipRegions
   * @param projObjBO project object BO
   * @param pidcVersionBO pidc version BO
   */
  public PIDCNatToolTip(final NatTable natTable, final String[] tooltipRegions, final AbstractProjectObjectBO projObjBO,
      final PidcVersionBO pidcVersionBO) {
    super(natTable, tooltipRegions);
    this.projObjBO = projObjBO;
    this.pidcVersionBO = pidcVersionBO;
  }

  /**
   * {@inheritDoc} Overriding the GetText method for Changing the Contants.
   */
  @Override
  protected String getText(final Event event) {

    int col = this.natTable.getColumnPositionByX(event.x);
    int row = LayerUtil.convertRowPosition(this.natTable, this.natTable.getRowPositionByY(event.y),
        ((CustomFilterGridLayer<PidcNattableRowObject>) this.natTable.getLayer()).getDummyDataLayer());
    ILayerCell cell = this.natTable.getCellByPosition(col, this.natTable.getRowPositionByY(event.y));

    if ((cell != null) && !cell.getConfigLabels().getLabels().contains(GridRegion.COLUMN_HEADER)) {
      // if the registered cell painter is the PasswordCellPainter, there will be no tooltip
      ICellPainter painter = this.natTable.getConfigRegistry().getConfigAttribute(CellConfigAttributes.CELL_PAINTER,
          DisplayMode.NORMAL, cell.getConfigLabels().getLabels());

      if (isVisibleContentPainter(painter)) {

        String tooltipValue = CellDisplayConversionUtils.convertDataType(cell, this.natTable.getConfigRegistry());
        // For the Column number 1 change the Tool tip text.(For Attribute get the Super group and Group name)

        CustomFilterGridLayer<PidcNattableRowObject> customFilterGridLayer =
            (CustomFilterGridLayer<PidcNattableRowObject>) cell.getLayer();

        PidcNattableRowObject pidcNattableRowObject = customFilterGridLayer.getBodyDataProvider().getRowObject(row);
        com.bosch.caltool.icdm.model.apic.attr.Attribute attribute = pidcNattableRowObject.getAttribute();
        this.projObjBO = pidcNattableRowObject.getProjectAttributeHandler().getProjectObjectBO();
        tooltipValue = getColumnTooltipValue(col, pidcNattableRowObject, attribute, tooltipValue);
        // return tooltip if not null
        if (tooltipValue.length() > 0) {
          return tooltipValue;
        }
      }
    }
    return null;

  }

  /**
   * @param col
   * @param pidcNattableRowObject
   * @param attribute
   * @param text
   * @return String
   */
  private String getColumnTooltipValue(final int col, final PidcNattableRowObject pidcNattableRowObject,
      final Attribute attribute, final String text) {

    StringBuilder toolipTextBuilder = new StringBuilder();
    String tooltipValue = text;

    if (col == CommonUIConstants.COLUMN_INDEX_1) {
      boolean isAnyPIDCAttrInVisible = isAttributeVisible(pidcNattableRowObject);
      // Summary tooltip
      toolipTextBuilder.append(createSummaryToolTip(pidcNattableRowObject, isAnyPIDCAttrInVisible));
      // Add dependencies tooltip
      // ICDM-2636
      appendDependenciesToolTip(pidcNattableRowObject, toolipTextBuilder, isAnyPIDCAttrInVisible);
      appendGrpdAttrToolTip(pidcNattableRowObject.getColumnDataMapper().getIpidcAttrs(), toolipTextBuilder);
      appendPredefAttrToolTip(pidcNattableRowObject.getColumnDataMapper().getIpidcAttrs(), toolipTextBuilder);

      tooltipValue = toolipTextBuilder.toString();
    }
    else if (col == CommonUIConstants.COLUMN_INDEX_2) {
      tooltipValue = modifyToolTip(col, tooltipValue, attribute);
    }
    else if (col == CommonUIConstants.COLUMN_INDEX_4) {
      tooltipValue = ApicConstants.TOOLTIP_USED_NOT_DEFINED;
    }
    else if (col == CommonUIConstants.COLUMN_INDEX_5) {
      tooltipValue = ApicConstants.TOOLTIP_USED_NO;
    }
    else if (col == CommonUIConstants.COLUMN_INDEX_6) {
      tooltipValue = ApicConstants.TOOLTIP_USED_YES;
    }
    else if (col == CommonUIConstants.COLUMN_INDEX_7) {
      String valueText = getValueColTooltipText(pidcNattableRowObject);
      tooltipValue = valueText == null ? "" : valueText;
    }
    else if (col == CommonUIConstants.COLUMN_INDEX_8) {
      tooltipValue = ApicUiConstants.TOOLTIP_PROJ_ATTR_EDIT;
    }

    return tooltipValue;
  }

  /**
   * @param element pidc nattable row object
   * @return String
   */
  public String getValueColTooltipText(final PidcNattableRowObject element) {
    // iCDM-957
    AbstractProjectAttributeBO projectAttributeHandler = element.getProjectAttributeHandler();
    if (null != projectAttributeHandler.getValueTooltip()) {
      // ICDM-1044
      IProjectAttribute pidcAttr = projectAttributeHandler.getProjectAttr();
      StringBuilder tooltip = new StringBuilder(projectAttributeHandler.getValueTooltip());
      return getAttrValTooltip(this.projObjBO.getPidcDataHandler().getPidcVersAttrMap(), pidcAttr, tooltip);
    }
    return projectAttributeHandler.getValueTooltip();
  }

  /**
   * Returns toolTip for attribute value
   *
   * @param attrMap attr map
   * @param pidcAttr
   * @param tooltip
   * @return String
   */
  private String getAttrValTooltip(final Map<Long, PidcVersionAttribute> attrMap, final IProjectAttribute pidcAttr,
      final StringBuilder tooltip) {

    if (isAttrValDeleted(pidcAttr)) {
      return tooltip.append(STATE_TT_LABEL).append("Marked as deleted").toString();
    }

    PidcDataHandler pidcDataHandler = this.projObjBO.getPidcDataHandler();
    Map<Long, AttributeValue> attributeValueMap = pidcDataHandler.getAttributeValueMap();
    AttributeValueClientBO attrValclntBoObj = new AttributeValueClientBO(attributeValueMap.get(pidcAttr.getValueId()));
    if (!pidcAttr.isAtChildLevel() && !attrValclntBoObj.isValidValue(getTempAttrMap(attrMap, pidcAttr), pidcDataHandler,
        attrValclntBoObj.getValueDependencies(false))) {
      return tooltip.append(STATE_TT_LABEL).append("Not visible due to value dependencies").toString();
    }

    if (!pidcAttr.isAtChildLevel() && (pidcAttr.getValueId() != null) && !(attributeValueMap.get(pidcAttr.getValueId())
        .getClearingStatus().equals(CLEARING_STATUS.CLEARED.getDBText()))) {
      return tooltip.append(STATE_TT_LABEL).append("Not cleared / In clearing").toString();
    }
    return tooltip.toString();
  }

  /**
   * Checks whether pidc attr value (not variant/sub-variant) is deleted or not
   *
   * @param pidcAttr attr
   * @return true if deleted
   */
  private boolean isAttrValDeleted(final IProjectAttribute pidcAttr) {
    Map<Long, AttributeValue> attributeValueMap = this.projObjBO.getPidcDataHandler().getAttributeValueMap();

    return (attributeValueMap.get(pidcAttr.getValueId()) != null) &&
        attributeValueMap.get(pidcAttr.getValueId()).isDeleted() && !pidcAttr.isAtChildLevel();
  }

  /**
   * @param attrMap
   * @param pidcAttr
   * @return
   */
  private Map<Long, IProjectAttribute> getTempAttrMap(final Map<Long, PidcVersionAttribute> attrMap,
      final IProjectAttribute pidcAttr) {
    // temp map which overrides varaint and subvariant attr for invalid color updation
    Map<Long, IProjectAttribute> tempAttrMap = new ConcurrentHashMap<>();
    tempAttrMap.putAll(attrMap);
    PidcDataHandler pidcDataHndlr = this.projObjBO.getPidcDataHandler();

    if (pidcDataHndlr.getAttributeValue(pidcAttr.getValueId()) != null) {
      if (pidcAttr instanceof PidcVariantAttribute) {
        tempAttrMap
            .putAll(pidcDataHndlr.getVariantAttributeMap().get(((PidcVariantAttribute) pidcAttr).getVariantId()));
      }
      else if (pidcAttr instanceof PidcSubVariantAttribute) {
        PidcSubVariantAttribute pidSubVarAttr = (PidcSubVariantAttribute) pidcAttr;
        tempAttrMap.putAll(pidcDataHndlr.getVariantAttributeMap().get(pidSubVarAttr.getVariantId()));
        tempAttrMap.putAll(pidcDataHndlr.getSubVariantAttributeMap().get(pidSubVarAttr.getSubVariantId()));
      }
    }
    return tempAttrMap;
  }


  /**
   * @param ipidcAttrs
   * @param toolTipTextBuilder
   */
  private void appendGrpdAttrToolTip(final Set<IProjectAttribute> ipidcAttrs, final StringBuilder toolTipTextBuilder) {
    // ICDM-2636
    for (IProjectAttribute pidcAttr : ipidcAttrs) {
      if (this.projObjBO.getPidcDataHandler().getAttributeMap().get(pidcAttr.getAttrId()).isGroupedAttr()) {
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
      if (this.pidcVersionBO.getPredefAttrGrpAttrMap().containsKey(pidcAttr)) {
        toolTipTextBuilder.append(CommonUiUtils.getMessage(ApicUiConstants.PIDC_EDITOR_GROUP, "PREDEFINED_ATTRS"));
        toolTipTextBuilder.append(this.pidcVersionBO.getPredefAttrGrpAttrMap().get(pidcAttr).getName());
        break;
      }
    }
  }

  /**
   * Method which checks whether all the pidcAttrs against a given attribute is visible
   *
   * @param pidcNattableRowObject
   * @return
   */
  private boolean isAttributeVisible(final PidcNattableRowObject pidcNattableRowObject) {
    Set<IProjectAttribute> ipidcAttrs = pidcNattableRowObject.getColumnDataMapper().getIpidcAttrs();
    boolean isAnyIPIDCAttrInVisible = false;
    for (IProjectAttribute ipidcAttribute : ipidcAttrs) {
      AbstractProjectAttributeBO handler = this.projAttrUtil.getProjectAttributeHandler(ipidcAttribute, this.projObjBO);
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
  private String createSummaryToolTip(final PidcNattableRowObject pidcNattableRowObject,
      final boolean isAnyIPIDCAttrInVisible) {
    Attribute attribute = pidcNattableRowObject.getAttribute();
    StringBuilder tooltip = new StringBuilder();
    // Tooltip for invisible attrs
    if (isAnyIPIDCAttrInVisible) {
      tooltip.append(CommonUiUtils.getMessage(ApicUiConstants.PIDC_EDITOR_GROUP, "ATTR_INVISIBLE"));
    }
    // Tooltip for mandatory
    else if (pidcNattableRowObject.getProjectAttributeHandler().isMandatory()) {
      tooltip.append(CommonUiUtils.getMessage(ApicUiConstants.PIDC_EDITOR_GROUP, "ATTR_MANDATORY"));
    } // Tooltip for dependent
    else if (hasAttrDependencies(attribute)) {
      tooltip.append(CommonUiUtils.getMessage(ApicUiConstants.PIDC_EDITOR_GROUP, "ATTR_DEPENDENT"));
    }
    // Add hyperlinks tooltip
    if (!tooltip.toString().isEmpty() && this.projObjBO.getPidcDataHandler().getLinks().contains(attribute.getId())) {
      tooltip.append(CommonUiUtils.getMessage(ApicUiConstants.PIDC_EDITOR_GROUP, "ATTR_ALSO_HYPERLINKS"));
    }
    else if (this.projObjBO.getPidcDataHandler().getLinks().contains(attribute.getId())) {
      tooltip.append(CommonUiUtils.getMessage(ApicUiConstants.PIDC_EDITOR_GROUP, "ATTR_HYPERLINKS"));
    }

    return tooltip.toString();
  }


  private boolean hasAttrDependencies(final com.bosch.caltool.icdm.model.apic.attr.Attribute attribute) {
    Set<AttrNValueDependency> depenAttr =
        this.projObjBO.getPidcDataHandler().getAttrRefDependenciesMap().get(attribute.getId());
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
  private void appendDependenciesToolTip(final PidcNattableRowObject pidcNattableRowObject,
      final StringBuilder tooltipText, final boolean isAnyIPIDCAttrInVisible) {
    SortedSet<String> depAttrUsed = new TreeSet<>(new CustomComparator());
    SortedSet<String> depAttrVal = new TreeSet<>(new CustomComparator());

    Attribute attribute = pidcNattableRowObject.getAttribute();
    // create tool tips for invisible attrs
    buildToolTipForInvisibleAttrs(attribute, tooltipText, isAnyIPIDCAttrInVisible);

    if (new AttributeClientBO(attribute).hasReferentialDependencies(this.projObjBO, attribute.getId())) {
      if (!tooltipText.toString().isEmpty()) {
        tooltipText.append(System.lineSeparator());
      }

      Set<AttrNValueDependency> depenAttr =
          this.projObjBO.getPidcDataHandler().getAttrRefDependenciesMap().get(attribute.getId());

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
        depAttrUsed.add("[" +
            this.projObjBO.getPidcDataHandler().getAttributeMap().get(attrDependency.getAttributeId()).getName() + "]");
      }
      else {
        AttributeValue attributeValue =
            this.projObjBO.getPidcDataHandler().getAttributeDepValueMap().get(attrDependency.getDependentValueId());
        depAttrVal.add(
            "[" + this.projObjBO.getPidcDataHandler().getAttributeMap().get(attrDependency.getAttributeId()).getName() +
                "] , if { value  = " + (null == attributeValue ? "" : attributeValue.getName()) + "}");
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
      String attrDepen = new AttributeClientBO(attribute).getDependencies(this.projObjBO.getPidcDataHandler());
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
          this.projObjBO.getPidcDataHandler().getAttributeSuperGroupMap()
              .get(this.projObjBO.getPidcDataHandler().getAttributeGroupMap().get(attr.getAttrGrpId()).getSuperGrpId())
              .getName(),
          this.projObjBO.getPidcDataHandler().getAttributeGroupMap().get(attr.getAttrGrpId()).getName());
    }
    return modifiedToolTip;
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
   * @return the projObjBO
   */
  public AbstractProjectObjectBO getProjObjBO() {
    return this.projObjBO;
  }


  /**
   * @param projObjBO the projObjBO to set
   */
  public void setProjObjBO(final AbstractProjectObjectBO projObjBO) {
    this.projObjBO = projObjBO;
  }


}
