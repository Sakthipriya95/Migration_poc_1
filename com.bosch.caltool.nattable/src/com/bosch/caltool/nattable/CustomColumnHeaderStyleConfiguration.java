package com.bosch.caltool.nattable;

import org.eclipse.nebula.widgets.nattable.config.AbstractRegistryConfiguration;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.grid.GridRegion;
import org.eclipse.nebula.widgets.nattable.painter.cell.ICellPainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.TextPainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.decorator.BeveledBorderDecorator;
import org.eclipse.nebula.widgets.nattable.style.BorderStyle;
import org.eclipse.nebula.widgets.nattable.style.CellStyleAttributes;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.style.HorizontalAlignmentEnum;
import org.eclipse.nebula.widgets.nattable.style.Style;
import org.eclipse.nebula.widgets.nattable.style.VerticalAlignmentEnum;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;


/**
 * @author jvi6cob
 */
public class CustomColumnHeaderStyleConfiguration extends AbstractRegistryConfiguration {

  /**
   * Degree of Blue color in RGB
   */
  private static final int BLUE_DEGREE = 215;
  /**
   * Degree of Green color in RGB
   */
  private static final int GREEN_DEGREE = 212;
  /**
   * Degree of red color in RGB
   */
  private static final int RED_DEGREE = 136;
  /**
   * font size constant
   */
  private static final int FONT_SIZE = 10;
  /**
   * Font instance
   */
  public Font font = GUIHelper.getFont(new FontData("Times New Roman", FONT_SIZE, SWT.BOLD | SWT.ITALIC));
  /**
   * COLOR_WIDGET_BACKGROUND
   */
  public Color bacgrndColor = GUIHelper.COLOR_WIDGET_BACKGROUND;
  /**
   * COLOR_WIDGET_FOREGROUND
   */
  public Color foregrndColor = GUIHelper.COLOR_WIDGET_FOREGROUND;
  /**
   * gradientBgColor
   */
  public Color gradintBgColor = GUIHelper.COLOR_WHITE;
  /**
   * gradientFgColor
   */
  public Color gradintFgColor = GUIHelper.getColor(RED_DEGREE, GREEN_DEGREE, BLUE_DEGREE);
  /**
   * HorizontalAlignmentEnum
   */
  public HorizontalAlignmentEnum horizontalAlign = HorizontalAlignmentEnum.CENTER;
  /**
   * VerticalAlignmentEnum
   */
  public VerticalAlignmentEnum verticalAlign = VerticalAlignmentEnum.MIDDLE;
  /**
   * BorderStyle
   */
  public BorderStyle bordrStyle;

  /**
   * ICellPainter
   */
  public ICellPainter cellPaintr = new BeveledBorderDecorator(new TextPainter());

  /**
   * Grid Lines
   */
  public Boolean rendrGridLines = Boolean.FALSE;

  /**
   * {@inheritDoc}
   */
  @Override
  public void configureRegistry(final IConfigRegistry configRegstry) {

    registerConfiguration(configRegstry);

    setStyle(configRegstry);
  }

  /**
   * @param configRegstry
   */
  private void setStyle(final IConfigRegistry configRegstry) {
    // configure the normal style
    Style cellStyl = new Style();
    cellStyl.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, this.bacgrndColor);
    cellStyl.setAttributeValue(CellStyleAttributes.FOREGROUND_COLOR, this.foregrndColor);
    cellStyl.setAttributeValue(CellStyleAttributes.GRADIENT_BACKGROUND_COLOR, this.gradintBgColor);
    cellStyl.setAttributeValue(CellStyleAttributes.GRADIENT_FOREGROUND_COLOR, this.gradintFgColor);
    cellStyl.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, this.horizontalAlign);
    cellStyl.setAttributeValue(CellStyleAttributes.VERTICAL_ALIGNMENT, this.verticalAlign);
    cellStyl.setAttributeValue(CellStyleAttributes.BORDER_STYLE, this.bordrStyle);
    cellStyl.setAttributeValue(CellStyleAttributes.FONT, this.font);

    configRegstry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyl, DisplayMode.NORMAL,
        GridRegion.COLUMN_HEADER);
    configRegstry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyl, DisplayMode.NORMAL,
        GridRegion.CORNER);
  }

  /**
   * @param configRegstry
   */
  private void registerConfiguration(final IConfigRegistry configRegstry) {
    // configure the painter
    configRegstry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, this.cellPaintr, DisplayMode.NORMAL,
        GridRegion.COLUMN_HEADER);
    configRegstry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, this.cellPaintr, DisplayMode.NORMAL,
        GridRegion.CORNER);

    // configure whether to render grid lines or not
    // e.g. for the BeveledBorderDecorator the rendering of the grid lines should be disabled
    configRegstry.registerConfigAttribute(CellConfigAttributes.RENDER_GRID_LINES, this.rendrGridLines,
        DisplayMode.NORMAL, GridRegion.COLUMN_HEADER);
    configRegstry.registerConfigAttribute(CellConfigAttributes.RENDER_GRID_LINES, this.rendrGridLines,
        DisplayMode.NORMAL, GridRegion.CORNER);
  }
}