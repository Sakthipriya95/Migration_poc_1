package com.bosch.caltool.nattable.configurations;

import org.eclipse.nebula.widgets.nattable.config.AbstractRegistryConfiguration;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.data.convert.DefaultDisplayConverter;
import org.eclipse.nebula.widgets.nattable.painter.cell.ICellPainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.TextPainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.decorator.LineBorderDecorator;
import org.eclipse.nebula.widgets.nattable.style.BorderStyle;
import org.eclipse.nebula.widgets.nattable.style.CellStyleAttributes;
import org.eclipse.nebula.widgets.nattable.style.HorizontalAlignmentEnum;
import org.eclipse.nebula.widgets.nattable.style.Style;
import org.eclipse.nebula.widgets.nattable.style.VerticalAlignmentEnum;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;


/**
 * Class which controls the style configuration in NatTable
 * 
 * @author jvi6cob
 */
public class CustomNatTableStyleConfiguration extends AbstractRegistryConfiguration {

  /**
   * 
   */
  private static final int CELL_SPACING = 1;
  private static final Color BGCOLOR = GUIHelper.COLOR_WHITE;
  private static final Color FGCOLOR = GUIHelper.COLOR_BLACK;
  private static final Color GRADIENTBGCOLOR = GUIHelper.COLOR_WHITE;
  private static final Color GRADIENTFGCOLOR = GUIHelper.getColor(136, 212, 215);
  private static final Font FONT = GUIHelper.DEFAULT_FONT;
  private static final HorizontalAlignmentEnum HALIGN = HorizontalAlignmentEnum.LEFT;
  private static final VerticalAlignmentEnum VALIGN = VerticalAlignmentEnum.MIDDLE;
  private static final BorderStyle BORDERSTYLE = null;
  private static ICellPainter CELLPAINTER ;
  private  boolean enableAutoResize;

  @Override
  public void configureRegistry(final IConfigRegistry configRegistry) {
    if(enableAutoResize){
      CELLPAINTER = new LineBorderDecorator(new TextPainter(true, true, CELL_SPACING, false,true)); 
    }else{
      CELLPAINTER = new LineBorderDecorator(new TextPainter());
 
    }
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER,
        CustomNatTableStyleConfiguration.CELLPAINTER);

    final Style cellStyle = new Style();
    cellStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, CustomNatTableStyleConfiguration.BGCOLOR);
    cellStyle.setAttributeValue(CellStyleAttributes.FOREGROUND_COLOR, CustomNatTableStyleConfiguration.FGCOLOR);
    cellStyle.setAttributeValue(CellStyleAttributes.GRADIENT_BACKGROUND_COLOR,
        CustomNatTableStyleConfiguration.GRADIENTBGCOLOR);
    cellStyle.setAttributeValue(CellStyleAttributes.GRADIENT_FOREGROUND_COLOR,
        CustomNatTableStyleConfiguration.GRADIENTFGCOLOR);
    cellStyle.setAttributeValue(CellStyleAttributes.FONT, CustomNatTableStyleConfiguration.FONT);
    cellStyle.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, CustomNatTableStyleConfiguration.HALIGN);
    cellStyle.setAttributeValue(CellStyleAttributes.VERTICAL_ALIGNMENT, CustomNatTableStyleConfiguration.VALIGN);
    cellStyle.setAttributeValue(CellStyleAttributes.BORDER_STYLE, CustomNatTableStyleConfiguration.BORDERSTYLE);

    
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle);

    configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER, new DefaultDisplayConverter());
  }

  
  /**
   * @param enableAutoResize the enableAutoResize to set
   */
  public void setEnableAutoResize(boolean enableAutoResize) {
    this.enableAutoResize = enableAutoResize;
  }
  
  
}
