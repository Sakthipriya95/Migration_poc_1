/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.commands;

import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.menus.UIElement;
import org.eclipse.ui.part.IPage;

import com.bosch.caltool.apic.ui.views.PIDCDetailsPage;
import com.bosch.caltool.icdm.common.ui.views.PIDCDetailsViewPart;
import com.bosch.rcputils.wbutils.WorkbenchUtils;


/**
 * The dropdown handler in the PIDC Details view
 *
 * @author adn1cob
 */
public class PIDCDetailsDropDownHandler extends AbstractHandler implements IHandler, IElementUpdater {


  /**
   * This method returns true , if the drop down option is applicable<br>
   * {@inheritDoc}
   */
  @Override
  public boolean isEnabled() {
    final PIDCDetailsViewPart viewPart = (PIDCDetailsViewPart) WorkbenchUtils.getView(PIDCDetailsViewPart.VIEW_ID);
    if (viewPart == null) {
      return false;
    }
    IPage currentPage = viewPart.getCurrentPage();
    // enabled for pidc details page
    return currentPage instanceof PIDCDetailsPage;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Object execute(final ExecutionEvent event) throws ExecutionException {


    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void updateElement(final UIElement element, final Map parameters) {
    /**
     * Not required now
     */

  }

}
