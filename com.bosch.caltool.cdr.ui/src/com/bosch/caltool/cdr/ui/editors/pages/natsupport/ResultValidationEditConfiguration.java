package com.bosch.caltool.cdr.ui.editors.pages.natsupport;

import org.eclipse.nebula.widgets.nattable.config.AbstractRegistryConfiguration;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.painter.cell.ICellPainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.ImagePainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.TextPainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.decorator.BeveledBorderDecorator;
import org.eclipse.nebula.widgets.nattable.painter.cell.decorator.CellPainterDecorator;
import org.eclipse.nebula.widgets.nattable.style.CellStyleAttributes;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.style.HorizontalAlignmentEnum;
import org.eclipse.nebula.widgets.nattable.style.IStyle;
import org.eclipse.nebula.widgets.nattable.style.Style;
import org.eclipse.nebula.widgets.nattable.style.VerticalAlignmentEnum;
import org.eclipse.nebula.widgets.nattable.ui.util.CellEdgeEnum;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;

import com.bosch.caltool.cdr.ui.util.CdrUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;

/**
 * CDR Result NAT page Style configuration class
 */
public class ResultValidationEditConfiguration extends AbstractRegistryConfiguration {


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
        CdrUIConstants.COL_HEADER_LABEL);
    // Changes to implement word wrapping so that multiple words in column header is completely visible in available
    // space
    ICellPainter valueHeaderCellPainter = new BeveledBorderDecorator(new TextPainter(true, true, false, true));
    // configure the painter
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, valueHeaderCellPainter,
        DisplayMode.NORMAL, CdrUIConstants.COL_HEADER_LABEL);
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, valueHeaderCellPainter,
        DisplayMode.NORMAL, CdrUIConstants.COL_HEADER_LABEL);


    // Reviewed params
    validationErrorStyle = new Style();
    validationErrorStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, GUIHelper.COLOR_WHITE);
    validationErrorStyle.setAttributeValue(CellStyleAttributes.FOREGROUND_COLOR,
        Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GREEN));
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, validationErrorStyle, DisplayMode.NORMAL,
        CdrUIConstants.REVIEWED_LABEL);

    // Not ok
    validationErrorStyle = new Style();
    validationErrorStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, GUIHelper.COLOR_WHITE);
    validationErrorStyle.setAttributeValue(CellStyleAttributes.FOREGROUND_COLOR,
        Display.getCurrent().getSystemColor(SWT.COLOR_DARK_RED));
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, validationErrorStyle, DisplayMode.NORMAL,
        CdrUIConstants.NOT_OK_LABEL);

    // Not reviewed
    validationErrorStyle = new Style();
    validationErrorStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, GUIHelper.COLOR_WHITE);
    validationErrorStyle.setAttributeValue(CellStyleAttributes.FOREGROUND_COLOR, GUIHelper.COLOR_BLACK);
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, validationErrorStyle, DisplayMode.NORMAL,
        CdrUIConstants.NOT_REVIEWED_LABEL);

    // Ref value
    validationErrorStyle = new Style();
    validationErrorStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, GUIHelper.COLOR_YELLOW);
    validationErrorStyle.setAttributeValue(CellStyleAttributes.FOREGROUND_COLOR, GUIHelper.COLOR_BLACK);
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, validationErrorStyle, DisplayMode.NORMAL,
        CdrUIConstants.REF_VALUE_DIFF_LABEL);

    // Check value
    validationErrorStyle = new Style();
    validationErrorStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, GUIHelper.COLOR_YELLOW);
    validationErrorStyle.setAttributeValue(CellStyleAttributes.FOREGROUND_COLOR, GUIHelper.COLOR_BLACK);
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, validationErrorStyle, DisplayMode.NORMAL,
        CdrUIConstants.CHK_VALUE_DIFF_LABEL);

    // Check value unit
    // iCDM-2151
    validationErrorStyle = new Style();
    validationErrorStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, GUIHelper.getColor(255, 165, 0));// orange
                                                                                                                  // color
    validationErrorStyle.setAttributeValue(CellStyleAttributes.FOREGROUND_COLOR, GUIHelper.COLOR_BLACK);
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, validationErrorStyle, DisplayMode.NORMAL,
        CdrUIConstants.CHK_VALUE_UNIT_DIFF_LABEL);

    // Result diff
    validationErrorStyle = new Style();
    validationErrorStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, GUIHelper.COLOR_YELLOW);
    validationErrorStyle.setAttributeValue(CellStyleAttributes.FOREGROUND_COLOR, GUIHelper.COLOR_BLACK);
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, validationErrorStyle, DisplayMode.NORMAL,
        CdrUIConstants.RESULT_DIFF_LABEL);

    // Task 236308
    // Secondary Result diff
    validationErrorStyle = new Style();
    validationErrorStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, GUIHelper.COLOR_YELLOW);
    validationErrorStyle.setAttributeValue(CellStyleAttributes.FOREGROUND_COLOR, GUIHelper.COLOR_BLACK);
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, validationErrorStyle, DisplayMode.NORMAL,
        CdrUIConstants.SEC_RESULT_DIFF_LABEL);

    // Lower limit
    validationErrorStyle = new Style();
    validationErrorStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, GUIHelper.COLOR_YELLOW);
    validationErrorStyle.setAttributeValue(CellStyleAttributes.FOREGROUND_COLOR, GUIHelper.COLOR_BLACK);
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, validationErrorStyle, DisplayMode.NORMAL,
        CdrUIConstants.LOW_LIMIT_DIFF_LABEL);

    // Upper limit
    validationErrorStyle = new Style();
    validationErrorStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, GUIHelper.COLOR_YELLOW);
    validationErrorStyle.setAttributeValue(CellStyleAttributes.FOREGROUND_COLOR, GUIHelper.COLOR_BLACK);
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, validationErrorStyle, DisplayMode.NORMAL,
        CdrUIConstants.UPPER_LIMIT_DIFF_LABEL);

    // bitwise limit
    validationErrorStyle = new Style();
    validationErrorStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, GUIHelper.COLOR_YELLOW);
    validationErrorStyle.setAttributeValue(CellStyleAttributes.FOREGROUND_COLOR, GUIHelper.COLOR_BLACK);
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, validationErrorStyle, DisplayMode.NORMAL,
        CdrUIConstants.BITWISE_LIMIT_DIFF_LABEL);
    // bitwise flag limit
    validationErrorStyle = new Style();
    validationErrorStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, GUIHelper.COLOR_YELLOW);
    validationErrorStyle.setAttributeValue(CellStyleAttributes.FOREGROUND_COLOR, GUIHelper.COLOR_BLACK);
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, validationErrorStyle, DisplayMode.NORMAL,
        CdrUIConstants.BITWISE_FLAG_DIFF_LABEL);
    // bitwise flag limit
    validationErrorStyle = new Style();
    validationErrorStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, GUIHelper.COLOR_YELLOW);
    validationErrorStyle.setAttributeValue(CellStyleAttributes.FOREGROUND_COLOR, GUIHelper.COLOR_BLACK);
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, validationErrorStyle, DisplayMode.NORMAL,
        CdrUIConstants.SCORE_DIFF_LABEL);
    // ready for series flag limit
    validationErrorStyle = new Style();
    validationErrorStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, GUIHelper.COLOR_YELLOW);
    validationErrorStyle.setAttributeValue(CellStyleAttributes.FOREGROUND_COLOR, GUIHelper.COLOR_BLACK);
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, validationErrorStyle, DisplayMode.NORMAL,
        CdrUIConstants.READY_FOR_SERIES_DIFF_LABEL);
    // exact match
    validationErrorStyle = new Style();
    validationErrorStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, GUIHelper.COLOR_YELLOW);
    validationErrorStyle.setAttributeValue(CellStyleAttributes.FOREGROUND_COLOR, GUIHelper.COLOR_BLACK);
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, validationErrorStyle, DisplayMode.NORMAL,
        CdrUIConstants.EXACT_MATCH_DIFF_LABEL);
    // History icon for checked value
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER,
        new CellPainterDecorator(new TextPainter(), CellEdgeEnum.LEFT,
            new ImagePainter(ImageManager.getInstance().getRegisteredImage(ImageKeys.SHOW_PARAM_HIST_16X16))),
        DisplayMode.NORMAL, CdrUIConstants.HISTORY_YES_LABEL);

    validationErrorStyle = new Style();
    validationErrorStyle.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.CENTER);

    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, validationErrorStyle, DisplayMode.NORMAL,
        CdrUIConstants.HISTORY_YES_LABEL);

  }
}