/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.editors.compare;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.nebula.widgets.nattable.data.IRowDataProvider;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnOverrideLabelAccumulator;

import com.bosch.caltool.icdm.client.bo.apic.AttributeValueClientBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcDataHandler;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.ApicConstants.CLEARING_STATUS;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueType;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;


/**
 * This class extends {@link ColumnOverrideLabelAccumulator} which is used for registration/addition of labels for a
 * given column.
 *
 * @author jvi6cob
 */
public class PidcNattableLabelAccumulator extends ColumnOverrideLabelAccumulator {

  /**
   * Instance variable which holds a reference to bodyDataLayer
   */
  private final ILayer layer;
  /**
   * Instance variable which holds a reference to bodyDataProvider
   */
  private final IRowDataProvider<PidcNattableRowObject> bodyDataProvider;


  /**
   * @param bodyDataLayer ILayer instance
   * @param bodyDataProvider IRowDataProvider instance
   */
  public PidcNattableLabelAccumulator(final ILayer bodyDataLayer,
      final IRowDataProvider<PidcNattableRowObject> bodyDataProvider) {
    super(bodyDataLayer);
    this.layer = bodyDataLayer;
    this.bodyDataProvider = bodyDataProvider;


  }

  // to get temp map for finding valid attribute values
  private Map<Long, IProjectAttribute> getTempAttrMap(final Map<Long, PidcVersionAttribute> attrMap,
      final IProjectAttribute pidcAttr, final PidcNattableRowObject compareRowObject) {
    // temp map which overrides varaint and subvariant attr for invalid color updation
    Map<Long, IProjectAttribute> tempAttrMap = new ConcurrentHashMap<>();
    tempAttrMap.putAll(attrMap);
//    get pidc data handler
    PidcDataHandler pidcDataHndlr =
        compareRowObject.getProjectAttributeHandler().getProjectObjectBO().getPidcDataHandler();
//    check pidc attribute value id is not null
    if (pidcDataHndlr.getAttributeValue(pidcAttr.getValueId()) != null) {
//      pidc variant attribute
      if (pidcAttr instanceof PidcVariantAttribute) {
        tempAttrMap
            .putAll(pidcDataHndlr.getVariantAttributeMap().get(((PidcVariantAttribute) pidcAttr).getVariantId()));
      }
//      pidc sub-variant attribute
      else if (pidcAttr instanceof PidcSubVariantAttribute) {
        PidcSubVariantAttribute pidSubVarAttr = (PidcSubVariantAttribute) pidcAttr;
        tempAttrMap.putAll(pidcDataHndlr.getVariantAttributeMap().get(pidSubVarAttr.getVariantId()));
        tempAttrMap.putAll(pidcDataHndlr.getSubVariantAttributeMap().get(pidSubVarAttr.getSubVariantId()));
      }
    }
    return tempAttrMap;
  }

  /**
   * This method is used to add labels to a cell
   */
  @Override
  public void accumulateConfigLabels(final LabelStack configLabels, final int columnPosition, final int rowPosition) {

    int columnIndex = this.layer.getColumnIndexByPosition(columnPosition);
    List<String> overrides = getOverrides(Integer.valueOf(columnIndex));
    if (overrides != null) {
      for (String configLabel : overrides) {
        configLabels.addLabel(configLabel);
      }
    }
    // get the row object out of the dataprovider

    if (columnPosition == 0) {
      configLabels.addLabel("BALL");
    }
    if (columnPosition == 11) {
      configLabels.addLabel("HYPERLINK");
    }

    PidcNattableRowObject compareRowObject = this.bodyDataProvider.getRowObject(rowPosition);
    IProjectAttribute pidcAttribute =
        compareRowObject.getColumnDataMapper().getColumnIndexPIDCAttrMap().get(columnIndex);

    if (pidcAttribute != null) {

      // condition to apply the configuration only to column 6 PURPLE if the value is not visible due to attribute
      // dependencies
      if (!pidcAttribute.isAtChildLevel() && compareRowObject.getProjectAttributeHandler().getProjectObjectBO()
          .getPidcDataHandler().getAttributeValueMap().containsKey(pidcAttribute.getValueId())) {
        AttributeValueClientBO attrValclntBoObj =
            new AttributeValueClientBO(compareRowObject.getProjectAttributeHandler().getProjectObjectBO()
                .getPidcDataHandler().getAttributeValueMap().get(pidcAttribute.getValueId()));

        if ((columnPosition == 6) && !attrValclntBoObj.isValidValue(
            getTempAttrMap(compareRowObject.getProjectAttributeHandler().getProjectObjectBO().getPidcDataHandler()
                .getPidcVersAttrMap(), pidcAttribute, compareRowObject),
            compareRowObject.getProjectAttributeHandler().getProjectObjectBO().getPidcDataHandler(),
            compareRowObject.getProjectAttributeHandler().getProjectObjectBO().getPidcDataHandler()
                .getAttrDependenciesMapForAllAttr().get(pidcAttribute.getAttrId()).get(pidcAttribute.getValueId()))) {
          configLabels.addLabel("DEPATTRVAL");
        }
      }

      // condition to apply the configuration only to column 6 for deleted attribute value
      if (isValueColAndContainsAttrVal(columnPosition, compareRowObject, pidcAttribute) &&
          compareRowObject.getProjectAttributeHandler().getProjectObjectBO().getPidcDataHandler().getAttributeValueMap()
              .get(pidcAttribute.getValueId()).isDeleted()) {
        configLabels.addLabel("DELVALUE");
      }
      // condition to apply the configuration only to column 6 if the value is not cleared
      if (isValueColAndContainsAttrVal(columnPosition, compareRowObject, pidcAttribute) &&
          !pidcAttribute.isAtChildLevel() &&
          !compareRowObject.getProjectAttributeHandler().getProjectObjectBO().getPidcDataHandler()
              .getAttributeValueMap().get(pidcAttribute.getValueId()).getClearingStatus()
              .equals(CLEARING_STATUS.CLEARED.getDBText())) {
        configLabels.addLabel("CLEAREDVAL");
      }

      // condition to apply the configuration only to column 6
      if ((columnPosition == 6) && compareRowObject.getProjectAttributeHandler().getProjectObjectBO()
          .getPidcDataHandler().getAttValLinks().contains(pidcAttribute.getValueId())) {
        configLabels.addLabel("VALUE");
      }
      if (isValueColAndContainsAttrVal(columnPosition, compareRowObject, pidcAttribute) &&
          isAttrValPresentAndHyperlink(compareRowObject, pidcAttribute) && !pidcAttribute.isAtChildLevel()) {
        configLabels.addLabel("HYPERLINK");
      }

      // attr visible / invisible
      if (!compareRowObject.getProjectAttributeHandler().isVisible()) {
        configLabels.addLabel("INVISIBLE");
      }
      else if ((columnPosition == 6) || (columnPosition == 8) || (columnPosition == 9)) {

        if (pidcAttribute.isAttrHidden() && (compareRowObject.getProjectAttributeHandler().isReadable() ||
            compareRowObject.getProjectAttributeHandler().isModifiable())) {
          configLabels.addLabel("HIDDEN");
        }
      }

      else {
        configLabels.addLabel("VISIBLE");
      }
    }

//    config label used to display value edit icon in pidc nattable
    if (columnPosition == 7) {
      configLabels.addLabel(ApicConstants.CONFIG_LABEL_VALUE_EDIT);
    }

  }

  /**
   * @param compareRowObject
   * @param pidcAttribute
   * @return
   */
  private boolean isAttrValPresentAndHyperlink(final PidcNattableRowObject compareRowObject,
      final IProjectAttribute pidcAttribute) {
//    to check if attribute value type is hyper link
    return (null != compareRowObject.getProjectAttributeHandler().getProjectObjectBO().getPidcDataHandler()
        .getAttributeValueMap().get(pidcAttribute.getValueId()).getValueType()) &&
        compareRowObject.getProjectAttributeHandler().getProjectObjectBO().getPidcDataHandler().getAttributeValueMap()
            .get(pidcAttribute.getValueId()).getValueType().equals(AttributeValueType.HYPERLINK.toString());
  }

  /**
   * @param columnPosition
   * @param compareRowObject
   * @param pidcAttribute
   * @return
   */
  private boolean isValueColAndContainsAttrVal(final int columnPosition, final PidcNattableRowObject compareRowObject,
      final IProjectAttribute pidcAttribute) {
//    check for attribute value
    return (columnPosition == 6) && (null != pidcAttribute) && compareRowObject.getProjectAttributeHandler()
        .getProjectObjectBO().getPidcDataHandler().getAttributeValueMap().containsKey(pidcAttribute.getValueId());
  }
}