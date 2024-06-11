/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.command;

import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.menus.UIElement;


/**
 * The dropdown handler in the Favourites tree view
 * 
 * @author bru2cob
 */
public class FavTreeDropDownHandler extends AbstractHandler implements IHandler, IElementUpdater {


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
