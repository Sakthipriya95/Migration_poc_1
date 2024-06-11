/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.editors.pages.natsupport;


import org.eclipse.nebula.widgets.nattable.config.AbstractRegistryConfiguration;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.painter.cell.ICellPainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.ImagePainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.TextPainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.decorator.BeveledBorderDecorator;
import org.eclipse.nebula.widgets.nattable.style.CellStyleAttributes;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.style.HorizontalAlignmentEnum;
import org.eclipse.nebula.widgets.nattable.style.IStyle;
import org.eclipse.nebula.widgets.nattable.style.Style;
import org.eclipse.nebula.widgets.nattable.style.VerticalAlignmentEnum;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;

import com.bosch.caltool.icdm.client.bo.a2l.A2LWpParamInfo;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.ruleseditor.utils.CustomNatMultiImagePainter;
import com.bosch.caltool.icdm.ruleseditor.utils.RuleEditorConstants;
import com.bosch.caltool.icdm.ui.util.IUIConstants;
import com.bosch.caltool.nattable.CustomFilterGridLayer;


/**
 * NAT page Style configuration class
 *
 * @author apj4cob
 */
public class WPLabelAssignNatPageEditConfiguration extends AbstractRegistryConfiguration {

  private final CustomFilterGridLayer gridLayer;
  CustomNatMultiImagePainter painter = new CustomNatMultiImagePainter(5, true);


  /**
   *
   */
  private static final String DUMMY_HEADER = "DUMMY_HEADER";

  /**
   * @param gridLayer CustomFilterGridLayer
   */
  public WPLabelAssignNatPageEditConfiguration(final CustomFilterGridLayer gridLayer) {
    super();
    this.gridLayer = gridLayer;
  }


  @Override
  public void configureRegistry(final IConfigRegistry configRegistry) {

    // configure the validation error style
    IStyle validationErrorStyle = new Style();
    validationErrorStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, GUIHelper.COLOR_WIDGET_BACKGROUND);
    validationErrorStyle.setAttributeValue(CellStyleAttributes.FOREGROUND_COLOR, GUIHelper.COLOR_WIDGET_FOREGROUND);
    validationErrorStyle.setAttributeValue(CellStyleAttributes.GRADIENT_BACKGROUND_COLOR, GUIHelper.COLOR_WHITE);
    validationErrorStyle.setAttributeValue(CellStyleAttributes.GRADIENT_FOREGROUND_COLOR,
        GUIHelper.getColor(136, 212, 215));
    validationErrorStyle.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.CENTER);
    validationErrorStyle.setAttributeValue(CellStyleAttributes.VERTICAL_ALIGNMENT, VerticalAlignmentEnum.MIDDLE);
    validationErrorStyle.setAttributeValue(CellStyleAttributes.BORDER_STYLE, null);
    validationErrorStyle.setAttributeValue(CellStyleAttributes.FONT,
        GUIHelper.getFont(new FontData("Segoe UI", 9, SWT.NONE)));
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, validationErrorStyle, DisplayMode.NORMAL,
        DUMMY_HEADER);
    ICellPainter valueHeaderCellPainter = new BeveledBorderDecorator(new TextPainter());

    // configure the painter
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, valueHeaderCellPainter,
        DisplayMode.NORMAL, DUMMY_HEADER);
    // Style for Inherited Responsibility and Workpackage Name at customer
    IStyle validationErrorStyle1 = new Style();
    validationErrorStyle1.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.LEFT);
    validationErrorStyle1.setAttributeValue(CellStyleAttributes.FOREGROUND_COLOR, GUIHelper.COLOR_DARK_GRAY);
    validationErrorStyle1.setAttributeValue(CellStyleAttributes.FONT,
        GUIHelper.getFont(new FontData("Arial", 8, SWT.ITALIC)));

    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, validationErrorStyle1, DisplayMode.NORMAL,
        "INHERITED_VAL");
    IStyle validationErrorStyle2 = new Style();
    validationErrorStyle2.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.LEFT);
    validationErrorStyle2.setAttributeValue(CellStyleAttributes.FOREGROUND_COLOR, GUIHelper.COLOR_DARK_GRAY);
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, validationErrorStyle2, DisplayMode.NORMAL,
        "PIDC_LEVEL_ASSIGNMENT");

    // Style for deleted responsibles
    IStyle deletedRespStyle = new Style();
    deletedRespStyle.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.LEFT);
    deletedRespStyle.setAttributeValue(CellStyleAttributes.FOREGROUND_COLOR, GUIHelper.COLOR_RED);
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, deletedRespStyle, DisplayMode.NORMAL,
        IUIConstants.CONFIG_LABEL_DEL_WP_RESPONSIBLE);

    // image painter for compliance parameter
    ImagePainter imgPainter = new ImagePainter() {

      @Override
      protected Image getImage(final ILayerCell cell, final IConfigRegistry configRegistry) {
        // get the row object

        A2LWpParamInfo rowObject = (A2LWpParamInfo) WPLabelAssignNatPageEditConfiguration.this.gridLayer
            .getBodyDataProvider().getRowObject(cell.getRowIndex());

        // if the image is not present, return null
        if (rowObject.isComplianceParam()) {
          return ImageManager.getInstance().getRegisteredImage(ImageKeys.PARAM_TYPE_COMPLIANCE_16X16);
        }
        return null;
      }

    };

    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, imgPainter, DisplayMode.NORMAL,
        CDRConstants.COMPLIANCE_LABEL);
    this.painter.add(CDRConstants.COMPLIANCE_LABEL,
        ImageManager.getInstance().getRegisteredImage(ImageKeys.PARAM_TYPE_COMPLIANCE_16X16));

    ImagePainter imgPaint2 =
        new ImagePainter(ImageManager.getInstance().getRegisteredImage(ImageKeys.BLACK_LIST_LABEL));
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, imgPaint2, DisplayMode.NORMAL,
        CDRConstants.BLACK_LIST_LABEL);
    this.painter.add(CDRConstants.BLACK_LIST_LABEL,
        ImageManager.getInstance().getRegisteredImage(ImageKeys.BLACK_LIST_LABEL));

    // read-only parameter image painter
    ImagePainter imgPaint3 = new ImagePainter(ImageManager.getInstance().getRegisteredImage(ImageKeys.READ_ONLY_16X16));
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, imgPaint3, DisplayMode.NORMAL,
        CDRConstants.READ_ONLY);
    this.painter.add(CDRConstants.READ_ONLY, ImageManager.getInstance().getRegisteredImage(ImageKeys.READ_ONLY_16X16));

    // Dependent characteristics
    ImagePainter imgPaint4 =
        new ImagePainter(ImageManager.getInstance().getRegisteredImage(ImageKeys.PARAM_DEPN_16X16));
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, imgPaint4, DisplayMode.NORMAL,
        CDRConstants.DEPENDENT_CHARACTERISTICS);
    this.painter.add(CDRConstants.DEPENDENT_CHARACTERISTICS,
        ImageManager.getInstance().getRegisteredImage(ImageKeys.PARAM_DEPN_16X16));

    ImagePainter imgPainter7 =
        new ImagePainter(ImageManager.getInstance().getRegisteredImage(ImageKeys.VIRTUAL_RECORD_16X16));
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, imgPainter7, DisplayMode.NORMAL,
        CDRConstants.VIRTUAL_INDICATOR);
    this.painter.add(CDRConstants.VIRTUAL_INDICATOR,
        ImageManager.getInstance().getRegisteredImage(ImageKeys.VIRTUAL_RECORD_16X16));
    ImagePainter imgPainter10 = new ImagePainter(ImageManager.getInstance().getRegisteredImage(ImageKeys.QSSD_LABEL));
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, imgPainter10, DisplayMode.NORMAL,
        RuleEditorConstants.QSSD_LABEL);
    this.painter.add(RuleEditorConstants.QSSD_LABEL,
        ImageManager.getInstance().getRegisteredImage(ImageKeys.QSSD_LABEL));
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, this.painter, DisplayMode.NORMAL,
        CDRConstants.MULTI_IMAGE_PAINTER);

  }


}

