/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.editors.pages.natsupport;

import org.eclipse.nebula.widgets.nattable.config.AbstractRegistryConfiguration;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.painter.cell.ImagePainter;
import org.eclipse.nebula.widgets.nattable.style.CellStyleAttributes;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.style.HorizontalAlignmentEnum;
import org.eclipse.nebula.widgets.nattable.style.IStyle;
import org.eclipse.nebula.widgets.nattable.style.Style;
import org.eclipse.nebula.widgets.nattable.style.TextDecorationEnum;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
import org.eclipse.swt.graphics.Image;

import com.bosch.caltool.icdm.client.bo.a2l.A2LParameter;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.ruleseditor.utils.CustomNatMultiImagePainter;
import com.bosch.caltool.icdm.ruleseditor.utils.RuleEditorConstants;
import com.bosch.caltool.nattable.CustomFilterGridLayer;


/**
 * ICDM-1700
 *
 * @author mkl2cob
 */
public class RvwReportEditConfiguration extends AbstractRegistryConfiguration {


  CustomNatMultiImagePainter painter = new CustomNatMultiImagePainter(5, true);

  /**
   * @param dataRprtFilterGridLayer dataRprtFilterGridLayer
   */
  public RvwReportEditConfiguration(final CustomFilterGridLayer dataRprtFilterGridLayer) {}

  /**
   * {@inheritDoc}
   */
  @Override
  public void configureRegistry(final IConfigRegistry configRegistry) {
    // configure the validation error style
    // TYPE
    IStyle cellStyle = new Style();
    // center the cell contents
    cellStyle.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.CENTER);

    // image painter
    ImagePainter imgPainter = new ImagePainter() {

      @Override
      protected Image getImage(final ILayerCell cell, final IConfigRegistry configRegistry) {
        // get the image based on the parameter type
        CustomFilterGridLayer<A2LParameter> customFilterGridLayer =
            (CustomFilterGridLayer<A2LParameter>) cell.getLayer();
        A2LParameter a2lParam = customFilterGridLayer.getBodyDataProvider().getRowObject(cell.getRowIndex());
        String paramType = a2lParam.getType();

        return CommonUiUtils.getInstance().getParamTypeImage(paramType);
      }

    };

    // set the image for the 'TYPE' label
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, imgPainter, DisplayMode.NORMAL,
        CDRConstants.PARAM_TYPE);
    // set the cell style
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.NORMAL,
        CDRConstants.PARAM_TYPE);


    ImagePainter imgPainter12 =
        new ImagePainter(ImageManager.getInstance().getRegisteredImage(ImageKeys.PARAM_TYPE_COMPLIANCE_16X16));
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, imgPainter12, DisplayMode.NORMAL,
        CDRConstants.COMPLI);
    this.painter.add(CDRConstants.COMPLI,
        ImageManager.getInstance().getRegisteredImage(ImageKeys.PARAM_TYPE_COMPLIANCE_16X16));
    ImagePainter imgPainter11 =
        new ImagePainter(ImageManager.getInstance().getRegisteredImage(ImageKeys.READ_ONLY_16X16));
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, imgPainter11, DisplayMode.NORMAL,
        CDRConstants.READ_ONLY);
    this.painter.add(CDRConstants.READ_ONLY, ImageManager.getInstance().getRegisteredImage(ImageKeys.READ_ONLY_16X16));

    ImagePainter imgPainterDependantChar =
        new ImagePainter(ImageManager.getInstance().getRegisteredImage(ImageKeys.PARAM_DEPN_16X16));
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, imgPainterDependantChar,
        DisplayMode.NORMAL, CDRConstants.DEPENDENT_CHARACTERISTICS);
    this.painter.add(CDRConstants.DEPENDENT_CHARACTERISTICS,
        ImageManager.getInstance().getRegisteredImage(ImageKeys.PARAM_DEPN_16X16));

    ImagePainter imgPainter10 = new ImagePainter(ImageManager.getInstance().getRegisteredImage(ImageKeys.QSSD_LABEL));
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, imgPainter10, DisplayMode.NORMAL,
        CDRConstants.QSSD_LABEL);
    this.painter.add(CDRConstants.QSSD_LABEL, ImageManager.getInstance().getRegisteredImage(ImageKeys.QSSD_LABEL));
    ImagePainter imgPainter7 =
        new ImagePainter(ImageManager.getInstance().getRegisteredImage(ImageKeys.BLACK_LIST_LABEL));
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, imgPainter7, DisplayMode.NORMAL,
        RuleEditorConstants.BLACK_LIST_LABEL);
    this.painter.add(CDRConstants.BLACK_LIST_LABEL,
        ImageManager.getInstance().getRegisteredImage(ImageKeys.BLACK_LIST_LABEL));

    // set the image for the 'TICK' label
    imgPainter = new ImagePainter(ImageManager.getInstance().getRegisteredImage(ImageKeys.TICK_16X16));
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, imgPainter, DisplayMode.NORMAL,
        CDRConstants.TICK);
    // Style for tick image
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.NORMAL,
        CDRConstants.TICK);
    // set the image for the 'TICK' label
    imgPainter = new ImagePainter(ImageManager.getInstance().getRegisteredImage(ImageKeys.DELETE_16X16));
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, imgPainter, DisplayMode.NORMAL,
        CDRConstants.REVIEW_FLAG_NO);
    // set the cell style
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.NORMAL,
        CDRConstants.REVIEW_FLAG_NO);
    // configure multi image painter to display multiple image in a single cell of nat table
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, this.painter, DisplayMode.NORMAL,
        CDRConstants.MULTI_IMAGE_PAINTER);
    // Back ground red
    IStyle validationErrorStyle = new Style();
    validationErrorStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, GUIHelper.COLOR_RED);
    validationErrorStyle.setAttributeValue(CellStyleAttributes.FOREGROUND_COLOR, GUIHelper.COLOR_BLACK);
    // add it to registry
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, validationErrorStyle, DisplayMode.NORMAL,
        CDRConstants.REVIEW_NO_BACKGROUND);

    // Zero Rating
    IStyle colorRed = new Style();
    colorRed.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, GUIHelper.COLOR_RED);
    colorRed.setAttributeValue(CellStyleAttributes.FOREGROUND_COLOR, GUIHelper.COLOR_BLACK);
    // add it to registry
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, colorRed, DisplayMode.NORMAL,
        CDRConstants.SCORE_RATING_0);
    // 25 % rating
    IStyle colorYellow = new Style();
    colorYellow.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, GUIHelper.COLOR_YELLOW);
    colorYellow.setAttributeValue(CellStyleAttributes.FOREGROUND_COLOR, GUIHelper.COLOR_BLACK);
    // add it to registry
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, colorYellow, DisplayMode.NORMAL,
        CDRConstants.SCORE_RATING_25);

    // 50 % rating
    IStyle colorBlue = new Style();
    colorBlue.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, GUIHelper.COLOR_BLUE);
    colorBlue.setAttributeValue(CellStyleAttributes.FOREGROUND_COLOR, GUIHelper.COLOR_BLACK);
    // add it to registry
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, colorBlue, DisplayMode.NORMAL,
        CDRConstants.SCORE_RATING_50);
    // 75% rating
    IStyle maturityComplete = new Style();
    maturityComplete.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, GUIHelper.COLOR_GREEN);
    maturityComplete.setAttributeValue(CellStyleAttributes.FOREGROUND_COLOR, GUIHelper.COLOR_BLACK);
    // add it to registry
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, maturityComplete, DisplayMode.NORMAL,
        CDRConstants.SCORE_RATING_75);
    // image configuration for rvw questionnaire status
    ImagePainter imgPainter13 = new ImagePainter(ImageManager.getInstance().getRegisteredImage(ImageKeys.ALL_16X16));
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, imgPainter13, DisplayMode.NORMAL,
        CDRConstants.RVW_QNAIRE_RESP_STATUS_ALL);
    this.painter.add(CDRConstants.RVW_QNAIRE_RESP_STATUS_ALL,
        ImageManager.getInstance().getRegisteredImage(ImageKeys.ALL_16X16));

    ImagePainter imgPainter14 =
        new ImagePainter(ImageManager.getInstance().getRegisteredImage(ImageKeys.ORANGE_EXCLAMATION_ICON_16X16));
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, imgPainter14, DisplayMode.NORMAL,
        CDRConstants.RVW_QNAIRE_RESP_STATUS_EXCLAMATION);
    this.painter.add(CDRConstants.RVW_QNAIRE_RESP_STATUS_EXCLAMATION,
        ImageManager.getInstance().getRegisteredImage(ImageKeys.EXCLAMATION_ICON_16X16));

    ImagePainter imgPainter15 = new ImagePainter(ImageManager.getInstance().getRegisteredImage(ImageKeys.REMOVE_16X16));
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, imgPainter15, DisplayMode.NORMAL,
        CDRConstants.RVW_QNAIRE_RESP_STATUS_REMOVE);
    this.painter.add(CDRConstants.RVW_QNAIRE_RESP_STATUS_REMOVE,
        ImageManager.getInstance().getRegisteredImage(ImageKeys.REMOVE_16X16));

    ImagePainter imgPainter16 = new ImagePainter(ImageManager.getInstance().getRegisteredImage(ImageKeys.NO_QNAIRE));
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, imgPainter16, DisplayMode.NORMAL,
        CDRConstants.RVW_QNAIRE_RESP_STATUS_OK);
    this.painter.add(CDRConstants.RVW_QNAIRE_RESP_STATUS_OK,
        ImageManager.getInstance().getRegisteredImage(ImageKeys.NO_QNAIRE));

    // set the cell style to displaz in center of the column
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.NORMAL,
        CDRConstants.RVW_QNAIRE_RESP_STATUS_REMOVE);
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.NORMAL,
        CDRConstants.RVW_QNAIRE_RESP_STATUS_EXCLAMATION);
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.NORMAL,
        CDRConstants.RVW_QNAIRE_RESP_STATUS_ALL);
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.NORMAL,
        CDRConstants.RVW_QNAIRE_RESP_STATUS_NA);
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.NORMAL,
        CDRConstants.RVW_QNAIRE_RESP_STATUS_OK);

    IStyle hyperLinkStyle = new Style();
    hyperLinkStyle.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.LEFT);
    hyperLinkStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, GUIHelper.COLOR_WHITE);
    hyperLinkStyle.setAttributeValue(CellStyleAttributes.FOREGROUND_COLOR, GUIHelper.COLOR_BLUE);
    hyperLinkStyle.setAttributeValue(CellStyleAttributes.TEXT_DECORATION, TextDecorationEnum.UNDERLINE);
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, hyperLinkStyle, DisplayMode.NORMAL,
        "REVIEW_RESULT_HYPERLINK");


  }


}
