/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.editors.compare;

import java.util.Set;

import org.eclipse.nebula.widgets.nattable.config.AbstractRegistryConfiguration;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.data.validate.IDataValidator;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.painter.cell.ICellPainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.ImagePainter;
import org.eclipse.nebula.widgets.nattable.style.CellStyleAttributes;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.style.HorizontalAlignmentEnum;
import org.eclipse.nebula.widgets.nattable.style.IStyle;
import org.eclipse.nebula.widgets.nattable.style.Style;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
import org.eclipse.swt.graphics.Image;

import com.bosch.caltool.icdm.client.bo.apic.AbstractProjectAttributeBO;
import com.bosch.caltool.icdm.client.bo.apic.ProjectAttributeUtil;
import com.bosch.caltool.icdm.client.bo.apic.pidc.ComparePidcHandler;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.attr.AttrNValueDependency;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectAttribute;
import com.bosch.caltool.nattable.CustomFilterGridLayer;


/**
 * Registry Configuration class for compare editor which assigns style based on the config labels registered on the
 * cells
 *
 * @author jvi6cob
 */
public class ComparePIDCEditConfiguration extends AbstractRegistryConfiguration {

  /**
   * project attribute util instance
   */
  private final ProjectAttributeUtil editorUtil = new ProjectAttributeUtil();
  /**
   * compare pidc handler
   */

  private final ComparePidcHandler comparePidcHandler;

  /**
   * @param comparePidcHandler
   */
  public ComparePIDCEditConfiguration(final ComparePidcHandler comparePidcHandler) {
    super();
    this.comparePidcHandler = comparePidcHandler;
  }


  /**
   * Configure NatTable's {@link IConfigRegistry} upon receiving this call back. A mechanism to plug-in custom
   * {@link ICellPainter}, {@link IDataValidator} etc.
   */
  @Override
  public void configureRegistry(final IConfigRegistry configRegistry) {

    // configure the validation error style
    // Style for cells under column with BALL icon
    IStyle validationErrorStyle = new Style();
    validationErrorStyle.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.CENTER);
    validationErrorStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, GUIHelper.COLOR_WIDGET_LIGHT_SHADOW);
    // Get image for column
    ImagePainter imgPainter = new ImagePainter() {

      /**
       * Returns the image for a cell.{@inheritDoc}
       */
      @Override
      protected Image getImage(final ILayerCell cell, final IConfigRegistry configRegistry) {
        CustomFilterGridLayer<CompareRowObject> customFilterGridLayer =
            (CustomFilterGridLayer<CompareRowObject>) cell.getLayer();
        CompareRowObject compareRowObject =
            customFilterGridLayer.getBodyDataProvider().getRowObject(cell.getRowIndex());
        return getAttrImage(compareRowObject, compareRowObject.getAttribute());
      }
    };
    configCellStyle(configRegistry, validationErrorStyle, imgPainter);
  }


  /**
   * @param compareRowObject
   * @param attribute
   * @return
   */
  private Image getAttrImage(final CompareRowObject compareRowObject, final Attribute attribute) {
    Image image;
    // Invisible attrs
    Set<IProjectAttribute> ipidcAttrs = compareRowObject.getColumnDataMapper().getIpidcAttrs();
    // Check for invisible pidc attr
    boolean isAnyIPIDCAttrInvisible = isAnyPIDCAttrInvisible(ipidcAttrs);
    // Partially Invisible || All invisible
    if (isAnyIPIDCAttrInvisible) {
      image =
          CommonUiUtils.getInstance().getLinkOverLayedImage(ImageKeys.GREY_BALL_16X16, ImageKeys.LINK_DECORATOR_12X12,
              attribute, ComparePIDCEditConfiguration.this.comparePidcHandler.getPidcVersionBO());
    }
    // IF MANDATORY
    else if (attribute.isMandatory()) {
      // ICDM-931
      image =
          CommonUiUtils.getInstance().getLinkOverLayedImage(ImageKeys.RED_BALL_16X16, ImageKeys.LINK_DECORATOR_12X12,
              attribute, ComparePIDCEditConfiguration.this.comparePidcHandler.getPidcVersionBO());
    }
    // IF DEPENDENT ATTRIBUTE
    else if (hasAttrDependencies(compareRowObject, attribute)) {
      // attr with dependencies
      image =
          CommonUiUtils.getInstance().getLinkOverLayedImage(ImageKeys.DEPN_ATTR_28X30, ImageKeys.LINK_DECORATOR_12X12,
              attribute, ComparePIDCEditConfiguration.this.comparePidcHandler.getPidcVersionBO());
    }
    else if (attribute.isGroupedAttr()) {
      // ICDM-2636
      // grouped attr
      image = CommonUiUtils.getInstance().getLinkOverLayedImage(ImageKeys.BLUE_BALL_GRPD_16X16,
          ImageKeys.LINK_DECORATOR_12X12, attribute,
          ComparePIDCEditConfiguration.this.comparePidcHandler.getPidcVersionBO());
    }
    else {

      // predef attr available
      boolean isAnyIPIDCAttrPredefined = isAnyAttrPreDefnd(ipidcAttrs);
      if (isAnyIPIDCAttrPredefined) {
        // ICDM-2636
        image = CommonUiUtils.getInstance().getLinkOverLayedImage(ImageKeys.BLUE_BALL_PREDEF_16X16,
            ImageKeys.LINK_DECORATOR_12X12, attribute,
            ComparePIDCEditConfiguration.this.comparePidcHandler.getPidcVersionBO());
      }
      else {
        image = CommonUiUtils.getInstance().getLinkOverLayedImage(null, ImageKeys.LINK_DECORATOR_12X12, attribute,
            ComparePIDCEditConfiguration.this.comparePidcHandler.getPidcVersionBO());
      }

    }
    return image;
  }


  /**
   * @param configRegistry
   * @param validationErrorStyle
   * @param imgPainter
   */
  private void configCellStyle(final IConfigRegistry configRegistry, IStyle validationErrorStyle,
      final ImagePainter imgPainter) {
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, imgPainter, DisplayMode.NORMAL, "BALL");
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, validationErrorStyle, DisplayMode.NORMAL,
        "BALL");


    // Style for INVISIBLE attributes
    validationErrorStyle = new Style();
    validationErrorStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, GUIHelper.COLOR_WIDGET_LIGHT_SHADOW);
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, validationErrorStyle, DisplayMode.NORMAL,
        "INVISIBLE");
  }

  /**
   * @param ipidcAttrs
   * @param isAnyIPIDCAttrPredefined
   * @return
   */
  private boolean isAnyAttrPreDefnd(final Set<IProjectAttribute> ipidcAttrs) {
    boolean isAnyIPIDCAttrPredefined = false;
    for (IProjectAttribute pidcAttr : ipidcAttrs) {

      if (!this.comparePidcHandler.getPidcVersionBO().getPredefAttrGrpAttrMap().isEmpty() &&
          this.comparePidcHandler.getPidcVersionBO().getPredefAttrGrpAttrMap().containsKey(pidcAttr)) {
        isAnyIPIDCAttrPredefined = true;
      }
    }
    return isAnyIPIDCAttrPredefined;
  }

  /**
   * @param ipidcAttrs
   * @param isAnyIPIDCAttrInvisible
   * @return
   */
  private boolean isAnyPIDCAttrInvisible(final Set<IProjectAttribute> ipidcAttrs) {
    boolean isAnyIPIDCAttrInvisible = false;
    if (CommonUtils.isNotEmpty(ipidcAttrs)) {
      for (IProjectAttribute ipidcAttribute : ipidcAttrs) {

        AbstractProjectAttributeBO projAttrHandler = this.editorUtil.getProjectAttributeHandler(ipidcAttribute,
            this.comparePidcHandler.getCompareObjectsHandlerMap().get(this.editorUtil.getID(ipidcAttribute)));
        if ((projAttrHandler != null) && !projAttrHandler.isVisible()) {
          isAnyIPIDCAttrInvisible = true;
        }
      }
    }
    return isAnyIPIDCAttrInvisible;
  }

  /**
   * Method which checks whether the passed attribute has attribute dependencies
   *
   * @param attribute Attribute
   * @return boolean
   */
  private boolean hasAttrDependencies(final CompareRowObject compareRowObject, final Attribute attribute) {
    Set<AttrNValueDependency> depenAttr = compareRowObject.getColumnDataMapper().getProjectHandlerMap().values()
        .iterator().next().getPidcDataHandler().getAttrRefDependenciesMap().get(attribute.getId());
    if (CommonUtils.isNotEmpty(depenAttr)) {
      for (AttrNValueDependency attrDependency : depenAttr) {
        if ((attrDependency != null) && (attrDependency.getAttributeId() != null)) {
          return true;
        }
      }
    }
    return false;
  }
}
