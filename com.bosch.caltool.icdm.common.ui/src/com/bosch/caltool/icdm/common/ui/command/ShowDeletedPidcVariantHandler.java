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
import com.bosch.caltool.icdm.common.ui.views.FavoritesViewPart;
import com.bosch.caltool.icdm.common.ui.views.PIDTreeViewPart;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.rcputils.wbutils.WorkbenchUtils;

/**
 * @author say8cob
 *
 */
public class ShowDeletedPidcVariantHandler extends AbstractHandler implements IHandler, IElementUpdater {

  private final IPreferenceStore preference = PlatformUI.getPreferenceStore();

  /** 
   * {@inheritDoc}
   */
  @Override
  public void updateElement(UIElement arg0, Map arg1) {
    arg0.setChecked(PIDTreeViewPart.isDisplayDeletedPIDCVariantEnabled());
    arg0.setChecked(FavoritesViewPart.isDisplayDeletedPIDCVariantEnabled());
    final PIDTreeViewPart pidcViewPart = (PIDTreeViewPart) WorkbenchUtils.getView(PIDTreeViewPart.VIEW_ID);
    if (pidcViewPart != null) {
      pidcViewPart.editorActivated(pidcViewPart.getViewSite().getPage().getActiveEditor());
    }
    FavoritesViewPart favViewPart = (FavoritesViewPart) WorkbenchUtils.getView(FavoritesViewPart.VIEW_ID);
    if (favViewPart != null) {
      favViewPart.editorActivated(favViewPart.getViewSite().getPage().getActiveEditor());
    }

  }

  /** 
   * {@inheritDoc}
   */
  @Override
  public Object execute(ExecutionEvent event) throws ExecutionException {
    // Get the command
    final Command command = event.getCommand();
    // Get the old value
    boolean oldValue = HandlerUtil.toggleCommandState(command);
    boolean newValue = !oldValue;

    // store preferences
    this.preference.setValue(CommonUIConstants.PIDC_TREE_DISPLAY_DELETED_PIDC_VARIANT,
        CommonUtils.getBooleanCode(newValue));
    // refresh ui elements
    final ICommandService service =
        HandlerUtil.getActiveWorkbenchWindowChecked(event).getService(ICommandService.class);
    service.refreshElements(event.getCommand().getId(), null);
    final FavoritesViewPart viewPart = (FavoritesViewPart) WorkbenchUtils.getView(FavoritesViewPart.VIEW_ID);
    if (viewPart != null) {
      viewPart.refreshPage();
    }
    final PIDTreeViewPart pidcViewPart = (PIDTreeViewPart) WorkbenchUtils.getView(PIDTreeViewPart.VIEW_ID);
    if (pidcViewPart != null) {
      pidcViewPart.refreshPage();
    }
    return null;
  }
  
  
}
