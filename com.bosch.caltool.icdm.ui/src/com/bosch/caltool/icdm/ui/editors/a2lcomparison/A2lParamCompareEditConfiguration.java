/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.editors.a2lcomparison;

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
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import com.bosch.caltool.icdm.client.bo.a2l.A2lParamCompareRowObject;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.ruleseditor.utils.CustomNatMultiImagePainter;
import com.bosch.caltool.icdm.ruleseditor.utils.RuleEditorConstants;
import com.bosch.caltool.icdm.ui.util.IUIConstants;
import com.bosch.caltool.nattable.CustomFilterGridLayer;


/**
 * Registry Configuration class for compare editor which assigns style based on the config labels registered on the
 * cells
 *
 * @author bru2cob
 */
class A2lParamCompareEditConfiguration extends AbstractRegistryConfiguration {


  CustomNatMultiImagePainter painter = new CustomNatMultiImagePainter(5, true);


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

    // Configuration for validation error style
    IStyle validationErrorStyle2 = new Style();
    validationErrorStyle2.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.LEFT);
    validationErrorStyle2.setAttributeValue(CellStyleAttributes.FOREGROUND_COLOR, GUIHelper.COLOR_DARK_GRAY);

    // Add it to registry
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, validationErrorStyle2, DisplayMode.NORMAL,
        "PIDC_LEVEL_ASSIGNMENT");

    IStyle colorYellow;
    colorYellow = new Style();
    // set light yellow color if there is difference
    Device device = Display.getCurrent();
    Color lightYellow = new Color(device, 255, 255, 153);
    // Set Background color
    colorYellow.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, lightYellow);
    // Set Foreground color
    colorYellow.setAttributeValue(CellStyleAttributes.FOREGROUND_COLOR, GUIHelper.COLOR_BLACK);
    // add it to registry
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, colorYellow, DisplayMode.NORMAL,
        IUIConstants.STYLE_DIFF);
    // image painter for compliance parameter
    ImagePainter imgPainter = new ImagePainter() {

      @Override
      protected Image getImage(final ILayerCell cell, final IConfigRegistry configRegistry) {
        // get the row object
        CustomFilterGridLayer<A2lParamCompareRowObject> customFilterGridLayer =
            (CustomFilterGridLayer<A2lParamCompareRowObject>) cell.getLayer();
        A2lParamCompareRowObject compareRowObject =
            customFilterGridLayer.getBodyDataProvider().getRowObject(cell.getRowIndex());

        // if the image is not present, return null
        if (compareRowObject.getParamInfo(cell.getColumnIndex()).isComplianceParam()) {
          return ImageManager.getInstance().getRegisteredImage(ImageKeys.PARAM_TYPE_COMPLIANCE_16X16);
        }
        return null;
      }

    };

    // Add compliance label to registry
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, imgPainter, DisplayMode.NORMAL,
        CDRConstants.COMPLIANCE_LABEL);
    this.painter.add(CDRConstants.COMPLIANCE_LABEL,
        ImageManager.getInstance().getRegisteredImage(ImageKeys.PARAM_TYPE_COMPLIANCE_16X16));

    // image Painter for Black list label
    ImagePainter imgPaint2 =
        new ImagePainter(ImageManager.getInstance().getRegisteredImage(ImageKeys.BLACK_LIST_LABEL));
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, imgPaint2, DisplayMode.NORMAL,
        CDRConstants.BLACK_LIST_LABEL);
    this.painter.add(CDRConstants.BLACK_LIST_LABEL,
        ImageManager.getInstance().getRegisteredImage(ImageKeys.BLACK_LIST_LABEL));

    // image Painter for Read only label
    ImagePainter imgPainter4 =
        new ImagePainter(ImageManager.getInstance().getRegisteredImage(ImageKeys.READ_ONLY_16X16));
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, imgPainter4, DisplayMode.NORMAL,
        CDRConstants.READ_ONLY);
    this.painter.add(CDRConstants.READ_ONLY, ImageManager.getInstance().getRegisteredImage(ImageKeys.READ_ONLY_16X16));

    // image Painter for QSSD label
    ImagePainter imgPainter10 = new ImagePainter(ImageManager.getInstance().getRegisteredImage(ImageKeys.QSSD_LABEL));
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, imgPainter10, DisplayMode.NORMAL,
        RuleEditorConstants.QSSD_LABEL);
    this.painter.add(RuleEditorConstants.QSSD_LABEL,
        ImageManager.getInstance().getRegisteredImage(ImageKeys.QSSD_LABEL));

    // add multi image painter to registry
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, this.painter, DisplayMode.NORMAL,
        CDRConstants.MULTI_IMAGE_PAINTER);
  }

}
