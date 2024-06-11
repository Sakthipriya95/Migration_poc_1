package com.bosch.caltool.apic.ui.editors.pages.natsupport;

import org.eclipse.nebula.widgets.nattable.config.AbstractRegistryConfiguration;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.style.CellStyleAttributes;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.style.HorizontalAlignmentEnum;
import org.eclipse.nebula.widgets.nattable.style.IStyle;
import org.eclipse.nebula.widgets.nattable.style.Style;
import org.eclipse.nebula.widgets.nattable.style.VerticalAlignmentEnum;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;

import com.bosch.caltool.apic.ui.util.ApicUiConstants;


/**
 * NAT page Style configuration class
 *
 * @author PDH2COB
 */
public class PidcCoCWpNattableEditConfiguration extends AbstractRegistryConfiguration {


  @Override
  public void configureRegistry(final IConfigRegistry configRegistry) {

    IStyle headerStyle = new Style();
    headerStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, GUIHelper.COLOR_WIDGET_BACKGROUND);
    headerStyle.setAttributeValue(CellStyleAttributes.FOREGROUND_COLOR, GUIHelper.COLOR_WIDGET_FOREGROUND);
    headerStyle.setAttributeValue(CellStyleAttributes.GRADIENT_BACKGROUND_COLOR, GUIHelper.COLOR_WHITE);
    headerStyle.setAttributeValue(CellStyleAttributes.GRADIENT_FOREGROUND_COLOR, GUIHelper.getColor(136, 212, 215));
    headerStyle.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.CENTER);
    headerStyle.setAttributeValue(CellStyleAttributes.VERTICAL_ALIGNMENT, VerticalAlignmentEnum.MIDDLE);
    headerStyle.setAttributeValue(CellStyleAttributes.BORDER_STYLE, null);
    headerStyle.setAttributeValue(CellStyleAttributes.FONT, GUIHelper.DEFAULT_FONT);
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, headerStyle, DisplayMode.NORMAL,
        ApicUiConstants.COC_WP_HEADER_LABEL);


    // Style for deleted attribute Values
    IStyle deletedWpDivStyle = new Style();
    deletedWpDivStyle.setAttributeValue(CellStyleAttributes.FOREGROUND_COLOR, GUIHelper.COLOR_RED);
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, deletedWpDivStyle, DisplayMode.NORMAL,
        ApicUiConstants.DELETED_COC_WP_LABEL);

  }

}
