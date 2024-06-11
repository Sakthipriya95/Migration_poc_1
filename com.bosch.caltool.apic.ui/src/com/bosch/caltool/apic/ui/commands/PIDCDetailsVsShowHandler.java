/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.commands;

import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.menus.UIElement;

import com.bosch.caltool.icdm.common.ui.views.PIDCDetailsViewPart;
import com.bosch.rcputils.wbutils.WorkbenchUtils;

/**
 * Shows/hides virtual PIDC structure
 *
 * @author bne4cob
 */
public class PIDCDetailsVsShowHandler extends AbstractHandler implements IElementUpdater {

  /**
   * Flag to check if this selection is done for the first time in a session
   */
  private static boolean firstInvocation = true;

  // ICDM-1119
  /**
   * Set the flag to display/hide virtual PIDC structure in PIDCDetailsViewPart
   * <p>
   * {@inheritDoc}
   */
  @Override
  public Object execute(final ExecutionEvent event) throws ExecutionException {
    // Get the command
    final Command command = event.getCommand();
    // Get the old value
    boolean oldValue = HandlerUtil.toggleCommandState(command);

    if (firstInvocation) {
      // Nullifies the effect of persisting the state by eclipse.
      // Every new session will show the virtual structure.
      // Without this check, the first selection will not change the state of the flag

      if (!oldValue) {
        oldValue = HandlerUtil.toggleCommandState(command);
      }
      firstInvocation = false;
    }


    IWorkbenchPart part = WorkbenchUtils.getView(PIDCDetailsViewPart.VIEW_ID);

    if (part instanceof PIDCDetailsViewPart) {
      PIDCDetailsViewPart pidcDetView = (PIDCDetailsViewPart) part;

      // Toggle the current value
      boolean newValue = !oldValue;

      // set the flag
      pidcDetView.setVirStructDispEnbld(newValue);

      // refresh details view of all opened PIDC Editors
      pidcDetView.refreshPages();
    }

    return null;
  }

  /**
   * Set the checked status of the option to the flag's value
   * <p>
   * {@inheritDoc}
   */
  @SuppressWarnings("rawtypes")
  @Override
  public void updateElement(final UIElement element, final Map parameters) {
    element.setChecked(PIDCDetailsViewPart.isVirtualStructureDisplayEnabled());
  }
}
