/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.editors.pages;

import org.eclipse.nebula.widgets.nattable.config.AbstractRegistryConfiguration;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.painter.cell.ImagePainter;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;

import com.bosch.caltool.cdr.ui.util.CdrUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.ruleseditor.utils.CustomNatMultiImagePainter;


/**
 * @author dja7cob
 */
public class RevResultSsdClassConfiguration extends AbstractRegistryConfiguration {

  CustomNatMultiImagePainter painter = new CustomNatMultiImagePainter(6, true);

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
        CdrUIConstants.COMPLIANCE_LABEL);
    this.painter.add(CdrUIConstants.COMPLIANCE_LABEL,
        ImageManager.getInstance().getRegisteredImage(ImageKeys.PARAM_TYPE_COMPLIANCE_16X16));

    // set the image for Shape check analysed parameters
    // Create the image painter for the label
    // Assign it to the label
    ImagePainter imgPainter2 =
        new ImagePainter(ImageManager.getInstance().getRegisteredImage(ImageKeys.SHAPE_CHECK_16X16));
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, imgPainter2, DisplayMode.NORMAL,
        CDRConstants.SHAPE_CHECK_LABEL);


    ImagePainter imgPainter3 =
        new ImagePainter(ImageManager.getInstance().getRegisteredImage(ImageKeys.READ_ONLY_16X16));
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, imgPainter3, DisplayMode.NORMAL,
        CdrUIConstants.READ_ONLY_LABEL);
    this.painter.add(CdrUIConstants.READ_ONLY_LABEL,
        ImageManager.getInstance().getRegisteredImage(ImageKeys.READ_ONLY_16X16));


    // image painter for dependent attributes
    ImagePainter imgPainter4 =
        new ImagePainter(ImageManager.getInstance().getRegisteredImage(ImageKeys.PARAM_DEPN_16X16));
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, imgPainter4, DisplayMode.NORMAL,
        CDRConstants.DEPENDENT_CHARACTERISTICS);
    this.painter.add(CDRConstants.DEPENDENT_CHARACTERISTICS,
        ImageManager.getInstance().getRegisteredImage(ImageKeys.PARAM_DEPN_16X16));

    ImagePainter imgPainter5 =
        new ImagePainter(ImageManager.getInstance().getRegisteredImage(ImageKeys.BLACK_LIST_LABEL));
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, imgPainter5, DisplayMode.NORMAL,
        CdrUIConstants.BLACK_LIST_LABEL);
    this.painter.add(CDRConstants.BLACK_LIST_LABEL,
        ImageManager.getInstance().getRegisteredImage(ImageKeys.BLACK_LIST_LABEL));

    ImagePainter imgPainter10 = new ImagePainter(ImageManager.getInstance().getRegisteredImage(ImageKeys.QSSD_LABEL));
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, imgPainter10, DisplayMode.NORMAL,
        CdrUIConstants.QSSD_LABEL);
    this.painter.add(CdrUIConstants.QSSD_LABEL, ImageManager.getInstance().getRegisteredImage(ImageKeys.QSSD_LABEL));
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, this.painter, DisplayMode.NORMAL,
        CdrUIConstants.MULTI_IMAGE_PAINTER);
  }
}
