/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.editors.pages;

import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.IConfiguration;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.painter.cell.ICellPainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.TextPainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.decorator.BeveledBorderDecorator;
import org.eclipse.nebula.widgets.nattable.style.CellStyleAttributes;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.style.HorizontalAlignmentEnum;
import org.eclipse.nebula.widgets.nattable.style.IStyle;
import org.eclipse.nebula.widgets.nattable.style.Style;
import org.eclipse.nebula.widgets.nattable.style.VerticalAlignmentEnum;
import org.eclipse.nebula.widgets.nattable.ui.binding.UiBindingRegistry;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;

import com.bosch.caltool.cdr.ui.util.CdrUIConstants;


/**
 * Questionnaire response NAT page Style configuration class
 *
 * @author bru2cob
 */
public class QnaireResptValidationEditConfiguration implements IConfiguration {

  /**
   * {@inheritDoc}
   */
  @Override
  public void configureLayer(final ILayer arg0) {
    // TODO Auto-generated method stub

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void configureRegistry(final IConfigRegistry configRegistry) {
    IStyle validationErrorStyle = new Style();
    // background and foreground color
    validationErrorStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, GUIHelper.COLOR_WIDGET_BACKGROUND);
    validationErrorStyle.setAttributeValue(CellStyleAttributes.FOREGROUND_COLOR, GUIHelper.COLOR_WIDGET_FOREGROUND);
    validationErrorStyle.setAttributeValue(CellStyleAttributes.GRADIENT_BACKGROUND_COLOR, GUIHelper.COLOR_WHITE);
    validationErrorStyle.setAttributeValue(CellStyleAttributes.GRADIENT_FOREGROUND_COLOR,
        GUIHelper.getColor(136, 212, 215));
    // allingment style
    validationErrorStyle.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.CENTER);
    validationErrorStyle.setAttributeValue(CellStyleAttributes.VERTICAL_ALIGNMENT, VerticalAlignmentEnum.MIDDLE);
    // border style
    validationErrorStyle.setAttributeValue(CellStyleAttributes.BORDER_STYLE, null);
    // font style
    validationErrorStyle.setAttributeValue(CellStyleAttributes.FONT,
        GUIHelper.getFont(new FontData("Segoe UI", 9, SWT.NONE)));
    // register the attribute config
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, validationErrorStyle, DisplayMode.NORMAL,
        CdrUIConstants.COL_HEADER_LABEL);
    ICellPainter valueHeaderCellPainter = new BeveledBorderDecorator(new TextPainter());
    // configure the painter
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, valueHeaderCellPainter,
        DisplayMode.NORMAL, CdrUIConstants.COL_HEADER_LABEL);

    // Style for finished with positive and nutral answer
    IStyle positiveStyle = new Style();
    positiveStyle.setAttributeValue(CellStyleAttributes.FOREGROUND_COLOR,
        Display.getDefault().getSystemColor(SWT.COLOR_DARK_GREEN));
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, positiveStyle, DisplayMode.NORMAL,
        CdrUIConstants.FINISHED_POSITIVE_ANS_LABEL);
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, positiveStyle, DisplayMode.NORMAL,
        CdrUIConstants.FINISHED_NEUTRAL_ANS_LABEL);

    // Style for finished with Negative answer
    IStyle neutralStyle = new Style();
    neutralStyle.setAttributeValue(CellStyleAttributes.FOREGROUND_COLOR,
        Display.getDefault().getSystemColor(SWT.COLOR_DARK_YELLOW));
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, neutralStyle, DisplayMode.NORMAL,
        CdrUIConstants.FINISHED_NEGATIVE_ANS_LABEL);

    // Style for finished with Negative answer, where no Negative Answer Allowed to finish WP
    IStyle noNegAnsStyle = new Style();
    noNegAnsStyle.setAttributeValue(CellStyleAttributes.FOREGROUND_COLOR, GUIHelper.COLOR_RED);
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, noNegAnsStyle, DisplayMode.NORMAL,
        CdrUIConstants.FINISHED_NO_NEG_ANS_ALLOWED);


  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void configureUiBindings(final UiBindingRegistry uibindingregistry) {
    // TODO Auto-generated method stub

  }

}
