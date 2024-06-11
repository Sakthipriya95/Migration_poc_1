package com.bosch.caltool.nattable;

import org.eclipse.nebula.widgets.nattable.export.action.ExportAction;
import org.eclipse.nebula.widgets.nattable.export.config.DefaultExportBindings;
import org.eclipse.nebula.widgets.nattable.ui.binding.UiBindingRegistry;
import org.eclipse.nebula.widgets.nattable.ui.matcher.KeyEventMatcher;
import org.eclipse.swt.SWT;


/**
 * @author jvi6cob
 */
public class CustomNatExportBindings extends DefaultExportBindings {

  @Override
  public void configureUiBindings(final UiBindingRegistry uiBindRegistry) {
    uiBindRegistry.registerKeyBinding(new KeyEventMatcher(SWT.CTRL | SWT.ALT, 'e'), new ExportAction());
  }

}
