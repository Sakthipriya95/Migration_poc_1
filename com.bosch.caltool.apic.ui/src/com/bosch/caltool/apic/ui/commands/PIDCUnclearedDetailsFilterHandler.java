/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.commands;

import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.menus.UIElement;

import com.bosch.caltool.apic.ui.util.ApicUiConstants;
import com.bosch.caltool.icdm.common.ui.views.PIDCDetailsViewPart;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.rcputils.wbutils.WorkbenchUtils;

// ICDM-2198
/**
 * Handler class for filters in pidc details menu variants with uncleared / missing mandatory values required or not
 *
 * @author dja7cob
 */
public class PIDCUnclearedDetailsFilterHandler extends AbstractHandler implements IHandler, IElementUpdater {

  /**
   * Get the eclipse preference store
   */
  private final transient IPreferenceStore preference = PlatformUI.getPreferenceStore();


  /**
   * Set the new status to preference store. Refresh the view pages
   * <p>
   * {@inheritDoc}
   */
  @Override
  public Object execute(final ExecutionEvent event) throws ExecutionException {
    // Get the command
    final Command command = event.getCommand();
    // Get the old value
    boolean oldValue = HandlerUtil.toggleCommandState(command);
    boolean newValue = !oldValue;

    // store preferences
    this.preference.setValue(ApicUiConstants.PIDC_DETAILS_SHOW_UNCLEARED,
        newValue ? ApicConstants.CODE_YES : ApicConstants.CODE_NO);
    // refresh ui elements
    final ICommandService service =
        HandlerUtil.getActiveWorkbenchWindowChecked(event).getService(ICommandService.class);
    service.refreshElements(event.getCommand().getId(), null);

    // all opened pidc details pages needs to be refreshed
    final PIDCDetailsViewPart viewPart = (PIDCDetailsViewPart) WorkbenchUtils.getView(PIDCDetailsViewPart.VIEW_ID);
    if (viewPart != null) {
      viewPart.refreshPages();
    }

    return null;
  }


  /**
   * {@inheritDoc}
   */
  @SuppressWarnings("rawtypes")
  @Override
  public void updateElement(final UIElement element, final Map parameters) {
    element.setChecked(PIDCDetailsViewPart.isUnclearedNodesDisplayEnabled());
  }

}
