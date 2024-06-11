/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.editors.pages.natsupport;

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

import com.bosch.caltool.cdr.ui.util.CdrUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.ReviewVariantModel;
import com.bosch.caltool.nattable.CustomFilterGridLayer;


/**
 * ICDM-1764
 *
 * @author mkl2cob
 */
public class PIDCRvwResEditConfiguration extends AbstractRegistryConfiguration {

  /**
   * CustomFilterGridLayer
   */
  private final CustomFilterGridLayer gridLayer;

  /**
   * @param dataRprtFilterGridLayer CustomFilterGridLayer
   */
  public PIDCRvwResEditConfiguration(final CustomFilterGridLayer dataRprtFilterGridLayer) {
    this.gridLayer = dataRprtFilterGridLayer;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void configureRegistry(final IConfigRegistry configRegistry) {
    // configure the validation error style
    IStyle validationErrorStyle = new Style();
    // creating the style for column header
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
        CdrUIConstants.COL_HEADER_LABEL);
    ICellPainter valueHeaderCellPainter = new BeveledBorderDecorator(new TextPainter());
    // configure the painter
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, valueHeaderCellPainter,
        DisplayMode.NORMAL, CdrUIConstants.COL_HEADER_LABEL);
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, valueHeaderCellPainter,
        DisplayMode.NORMAL, CdrUIConstants.COL_HEADER_LABEL);
    // configure the validation error style
    // TYPE
    IStyle cellStyle = new Style();
    // center the cell contents
    cellStyle.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.CENTER);

    // image painter
    ImagePainter imgPainter = new ImagePainter() {

      @Override
      protected Image getImage(final ILayerCell cell, final IConfigRegistry configRegistry) {
        // get the row object
        ReviewVariantModel cdrRes = (ReviewVariantModel) PIDCRvwResEditConfiguration.this.gridLayer.getBodyDataProvider()
            .getRowObject(cell.getRowIndex());
        Image image = null;
        if (!cdrRes.isDeleted()) {
          // if the cdrRes is not deleted
          // get the image for the review
          image = CommonUiUtils.getInstance().getImageForCDRReslt(cdrRes);
        }
        // if the image is not present, return null
        return image;
      }

    };

    // set the image for the 'TYPE' label
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, imgPainter, DisplayMode.NORMAL,
        CDRConstants.REVIEW_RESULTS_IMAGE);
    // set the cell style
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.NORMAL,
        CDRConstants.REVIEW_RESULTS_IMAGE);

  }


}
