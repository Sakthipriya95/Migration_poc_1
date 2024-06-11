/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ruleseditor.pages;

import org.eclipse.nebula.widgets.nattable.config.AbstractRegistryConfiguration;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.painter.cell.ImagePainter;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;

import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.ruleseditor.utils.CustomNatMultiImagePainter;
import com.bosch.caltool.icdm.ruleseditor.utils.RuleEditorConstants;


/**
 * @author bru2cob
 */
public class ParamClassConfiguration extends AbstractRegistryConfiguration {

  CustomNatMultiImagePainter painter = new CustomNatMultiImagePainter(5, true);

  /**
   * {@inheritDoc}
   */
  @Override
  public void configureRegistry(final IConfigRegistry configRegistry) {

    // set the image for the 'COMPLIANCE' label
    // Create the image painter for the label
    // Assign it to the label
    ImagePainter imgPainter =
        new ImagePainter(ImageManager.getInstance().getRegisteredImage(ImageKeys.PARAM_TYPE_COMPLIANCE_16X16));
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, imgPainter, DisplayMode.NORMAL,
        RuleEditorConstants.COMPLIANCE_TYPE_LABEL);
    this.painter.add(RuleEditorConstants.COMPLIANCE_TYPE_LABEL,
        ImageManager.getInstance().getRegisteredImage(ImageKeys.PARAM_TYPE_COMPLIANCE_16X16));


    ImagePainter imgPainter1 = new ImagePainter(ImageManager.getInstance().getRegisteredImage(ImageKeys.MAP_16X16));
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, imgPainter1, DisplayMode.NORMAL,
        RuleEditorConstants.MAP_TYPE_LABEL);

    ImagePainter imgPainter2 = new ImagePainter(ImageManager.getInstance().getRegisteredImage(ImageKeys.CURVE_16X16));
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, imgPainter2, DisplayMode.NORMAL,
        RuleEditorConstants.CURVE_TYPE_LABEL);

    ImagePainter imgPainter3 = new ImagePainter(ImageManager.getInstance().getRegisteredImage(ImageKeys.VALUE_16X16));
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, imgPainter3, DisplayMode.NORMAL,
        RuleEditorConstants.VALUE_TYPE_LABEL);

    ImagePainter imgPainter4 = new ImagePainter(ImageManager.getInstance().getRegisteredImage(ImageKeys.ASCII_16X16));
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, imgPainter4, DisplayMode.NORMAL,
        RuleEditorConstants.ASCII_TYPE_LABEL);

    ImagePainter imgPainter5 = new ImagePainter(ImageManager.getInstance().getRegisteredImage(ImageKeys.VALBLK_16X16));
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, imgPainter5, DisplayMode.NORMAL,
        RuleEditorConstants.VAL_BLK_TYPE_LABEL);

    ImagePainter imgPainter6 = new ImagePainter(ImageManager.getInstance().getRegisteredImage(ImageKeys.AXIS_16X16));
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, imgPainter6, DisplayMode.NORMAL,
        RuleEditorConstants.AXIS_PTS_TYPE_LABEL);


    ImagePainter imgPainter7 =
        new ImagePainter(ImageManager.getInstance().getRegisteredImage(ImageKeys.BLACK_LIST_LABEL));
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, imgPainter7, DisplayMode.NORMAL,
        RuleEditorConstants.BLACK_LIST_LABEL);
    this.painter.add(CDRConstants.BLACK_LIST_LABEL,
        ImageManager.getInstance().getRegisteredImage(ImageKeys.BLACK_LIST_LABEL));


    // image painter for qssd indicator
    ImagePainter imgPainter10 = new ImagePainter(ImageManager.getInstance().getRegisteredImage(ImageKeys.QSSD_LABEL));
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, imgPainter10, DisplayMode.NORMAL,
        RuleEditorConstants.QSSD_LABEL);
    this.painter.add(RuleEditorConstants.QSSD_LABEL,
        ImageManager.getInstance().getRegisteredImage(ImageKeys.QSSD_LABEL));
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, this.painter, DisplayMode.NORMAL,
        RuleEditorConstants.MULTI_IMAGE_PAINTER);
  }
}