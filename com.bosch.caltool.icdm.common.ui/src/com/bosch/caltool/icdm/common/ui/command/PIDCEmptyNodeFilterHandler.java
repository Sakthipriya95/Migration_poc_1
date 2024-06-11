/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.command;

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

import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.views.PIDTreeViewPart;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.rcputils.wbutils.WorkbenchUtils;


/**
 * Handler class for filters in pidc tree view display
 *
 * @author bru2cob
 */
public class PIDCEmptyNodeFilterHandler extends AbstractHandler implements IHandler, IElementUpdater {

  /**
   * Get the eclipse preference store
   */
  private final IPreferenceStore preference = PlatformUI.getPreferenceStore();


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
    this.preference.setValue(CommonUIConstants.PIDC_TREE_HIDE_EMPTY_NODES, CommonUtils.getBooleanCode(newValue));
    // refresh ui elements
    final ICommandService service =
        HandlerUtil.getActiveWorkbenchWindowChecked(event).getService(ICommandService.class);
    service.refreshElements(event.getCommand().getId(), null);

    final PIDTreeViewPart pidcViewPart = (PIDTreeViewPart) WorkbenchUtils.getView(PIDTreeViewPart.VIEW_ID);
    if (pidcViewPart != null) {
      pidcViewPart.refreshPage();
    }

    return null;
  }


  /**
   * {@inheritDoc}
   */
  @SuppressWarnings("rawtypes")
  @Override
  public void updateElement(final UIElement element, final Map parameters) {
    element.setChecked(PIDTreeViewPart.hideEmptyNodes());
  }

}
