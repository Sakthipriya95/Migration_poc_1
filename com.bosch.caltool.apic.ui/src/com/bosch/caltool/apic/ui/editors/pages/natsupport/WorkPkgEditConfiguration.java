/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
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
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;

import com.bosch.caltool.apic.ui.util.ApicUiConstants;

/**
 * @author elm1cob
 */
public class WorkPkgEditConfiguration extends AbstractRegistryConfiguration {

  /**
   * {@inheritDoc}
   */
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
        "DUMMY_HEADER");


    // configuration for default workpackage in a2l view
    IStyle defaultWpStyle = new Style();
    defaultWpStyle.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.LEFT);
    defaultWpStyle.setAttributeValue(CellStyleAttributes.FOREGROUND_COLOR, GUIHelper.COLOR_DARK_GRAY);
    configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, defaultWpStyle, DisplayMode.NORMAL,
        ApicUiConstants.CONFIG_LABEL_DISABLE);
  }

}
