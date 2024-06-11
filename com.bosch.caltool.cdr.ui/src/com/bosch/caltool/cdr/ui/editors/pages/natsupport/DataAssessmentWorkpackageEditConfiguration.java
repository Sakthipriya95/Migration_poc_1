/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.editors.pages.natsupport;

import org.eclipse.nebula.widgets.nattable.config.AbstractRegistryConfiguration;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.data.validate.IDataValidator;
import org.eclipse.nebula.widgets.nattable.painter.cell.ICellPainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.ImagePainter;
import org.eclipse.nebula.widgets.nattable.style.CellStyleAttributes;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.style.HorizontalAlignmentEnum;
import org.eclipse.nebula.widgets.nattable.style.IStyle;
import org.eclipse.nebula.widgets.nattable.style.Style;

import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.ruleseditor.utils.CustomNatMultiImagePainter;

/**
 * Registry Configuration class for data assessment report editor::questionnaire results tab which assigns style based
 * on the config labels registered on the cells
 *
 * @author NDV4KOR
 */
public class DataAssessmentWorkpackageEditConfiguration extends AbstractRegistryConfiguration {


  CustomNatMultiImagePainter painter = new CustomNatMultiImagePainter(5, true);

  /**
   * Constructor
   */
  public DataAssessmentWorkpackageEditConfiguration() {
    super();
  }

  /**
   * Configure NatTable's {@link IConfigRegistry} upon receiving this call back. A mechanism to plug-in custom
   * {@link ICellPainter}, {@link IDataValidator} etc.
   */
  @Override
  public void configureRegistry(final IConfigRegistry configRegistry) {


    IStyle cellStyle = new Style();
    // center the cell contents
    cellStyle.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.CENTER);
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.NORMAL,
        CommonUIConstants.LABEL_FOR_YES);
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.NORMAL,
        CommonUIConstants.LABEL_FOR_NO);
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.NORMAL,
        CommonUIConstants.NOT_APPLICABLE);

    ImagePainter imgPainterAll = new ImagePainter(ImageManager.getInstance().getRegisteredImage(ImageKeys.ALL_16X16));
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, imgPainterAll, DisplayMode.NORMAL,
        CommonUIConstants.LABEL_FOR_YES);
    this.painter.add(CommonUIConstants.LABEL_FOR_YES,
        ImageManager.getInstance().getRegisteredImage(ImageKeys.ALL_16X16));

    ImagePainter imgPainterCrossMark =
        new ImagePainter(ImageManager.getInstance().getRegisteredImage(ImageKeys.REMOVE_16X16));
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, imgPainterCrossMark, DisplayMode.NORMAL,
        CommonUIConstants.LABEL_FOR_NO);
    this.painter.add(CommonUIConstants.LABEL_FOR_NO,
        ImageManager.getInstance().getRegisteredImage(ImageKeys.REMOVE_16X16));

    ImagePainter imgForNoQnaire = new ImagePainter(ImageManager.getInstance().getRegisteredImage(ImageKeys.NO_QNAIRE));
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, imgForNoQnaire, DisplayMode.NORMAL,
        CommonUIConstants.NOT_APPLICABLE);
    this.painter.add(CommonUIConstants.NOT_APPLICABLE,
        ImageManager.getInstance().getRegisteredImage(ImageKeys.NO_QNAIRE));

  }

}
