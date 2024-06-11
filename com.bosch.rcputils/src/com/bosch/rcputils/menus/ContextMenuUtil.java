/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.rcputils.menus;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.StructuredViewer;


/**
 * @author mga1cob
 */
public final class ContextMenuUtil {

  /**
   * ContextMenuUtil instance
   */
  private static ContextMenuUtil contextMenuUtil;

  /**
   * The private constructor
   */
  private ContextMenuUtil() {
    // The private constructor
  }

  /**
   * This method returns ContextMenuUtil instance
   * 
   * @return ContextMenuUtil instance
   */
  public static ContextMenuUtil getInstance() {
    if (contextMenuUtil == null) {
      contextMenuUtil = new ContextMenuUtil();
    }
    return contextMenuUtil;

  }

  /**
   * This method adds right click menu for TreeViewer or TableViewer or GridTableViewer
   * 
   * @param viewer instance
   * @return MenuManager instance
   */
  public MenuManager addRightClickMenu(final StructuredViewer viewer) {
    final MenuManager menuMgr = new MenuManager();
    menuMgr.setRemoveAllWhenShown(true);
    viewer.getControl().setMenu(menuMgr.createContextMenu(viewer.getControl()));
    return menuMgr;
  }
}
