/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.editors.pages.natsupport;

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

// ICDM-2439
/**
 * @author dja7cob
 */
public class ParamSsdClassConfiguration extends AbstractRegistryConfiguration {

  CustomNatMultiImagePainter painter = new CustomNatMultiImagePainter(5, true);

  /**
   * {@inheritDoc}
   */
  @Override
  public void configureRegistry(final IConfigRegistry configRegistry) {

    // Set image as per the label accumulated for compliance parameter
    // set the image for the 'COMPLIANCE' label

    // set the image for the 'Param not in calmemory' label
    ImagePainter imgPaint = new ImagePainter(ImageManager.getInstance().getRegisteredImage(ImageKeys.NOT_OK_16X16));
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, imgPaint, DisplayMode.NORMAL,
        CDRConstants.NOT_IN_CALMEMORY_LABEL);
    this.painter.add(CDRConstants.NOT_IN_CALMEMORY_LABEL,
        ImageManager.getInstance().getRegisteredImage(ImageKeys.NOT_OK_16X16));

    // image painter
    ImagePainter imgPainter3 =
        new ImagePainter(ImageManager.getInstance().getRegisteredImage(ImageKeys.PARAM_TYPE_COMPLIANCE_16X16));
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, imgPainter3, DisplayMode.NORMAL,
        CDRConstants.COMPLI);
    this.painter.add(CDRConstants.COMPLI,
        ImageManager.getInstance().getRegisteredImage(ImageKeys.PARAM_TYPE_COMPLIANCE_16X16));

    // image painter for read only parameter
    ImagePainter imgPainter4 =
        new ImagePainter(ImageManager.getInstance().getRegisteredImage(ImageKeys.READ_ONLY_16X16));
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, imgPainter4, DisplayMode.NORMAL,
        CDRConstants.READ_ONLY);
    this.painter.add(CDRConstants.READ_ONLY, ImageManager.getInstance().getRegisteredImage(ImageKeys.READ_ONLY_16X16));
    // image painter for dependent attributes
    ImagePainter imgPainter5 =
        new ImagePainter(ImageManager.getInstance().getRegisteredImage(ImageKeys.PARAM_DEPN_16X16));
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, imgPainter5, DisplayMode.NORMAL,
        CDRConstants.DEPENDENT_CHARACTERISTICS);
    this.painter.add(CDRConstants.DEPENDENT_CHARACTERISTICS,
        ImageManager.getInstance().getRegisteredImage(ImageKeys.PARAM_DEPN_16X16));
    ImagePainter imgPaint2 =
        new ImagePainter(ImageManager.getInstance().getRegisteredImage(ImageKeys.BLACK_LIST_LABEL));
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, imgPaint2, DisplayMode.NORMAL,
        CDRConstants.BLACK_LIST_LABEL);
    this.painter.add(CDRConstants.BLACK_LIST_LABEL,
        ImageManager.getInstance().getRegisteredImage(ImageKeys.BLACK_LIST_LABEL));

    ImagePainter imgPainter10 = new ImagePainter(ImageManager.getInstance().getRegisteredImage(ImageKeys.QSSD_LABEL));
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, imgPainter10, DisplayMode.NORMAL,
        RuleEditorConstants.QSSD_LABEL);
    this.painter.add(RuleEditorConstants.QSSD_LABEL,
        ImageManager.getInstance().getRegisteredImage(ImageKeys.QSSD_LABEL));
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, this.painter, DisplayMode.NORMAL,
        CDRConstants.MULTI_IMAGE_PAINTER);
  }

}
