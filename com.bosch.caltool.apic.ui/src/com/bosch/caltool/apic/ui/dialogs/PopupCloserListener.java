/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Shell;


/**
 * @author mkl2cob
 */
public class PopupCloserListener implements Listener {

  // FocusMatrixEditDialog instance
  private final FocusMatrixEditDialog dialog;
  // Boolean to set whether scrollbar is used
  private boolean scrollbarClked;

  /**
   * constructor
   *
   * @param dialog FocusMatrixEditDialog instance
   */
  public PopupCloserListener(final FocusMatrixEditDialog dialog) {
    this.dialog = dialog;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void handleEvent(final Event event) {

    if (this.dialog.getErrorDialogDisplayed()) {
      return;
    }
    // when focus is out of widget or the field's shell is deactivating
    if (event.type == SWT.FocusOut) {
      this.scrollbarClked = false;
      /*
       * Ignore this event if it happens only because the focus is moving between the popup shells, their controls, or a
       * scrollbar.
       */

      // Keep in an async since the focus is not actually switched when this event is received.
      event.display.asyncExec(new Runnable() {

        @Override
        public void run() {
          // If the dialog exists (not disposed)
          if (isValid()) {
            if (PopupCloserListener.this.scrollbarClked || hasFocus()) {
              return;
            }
            // Check the active shell.
            Shell activeShell = event.display.getActiveShell();
            if (activeShell == PopupCloserListener.this.dialog.getShell()) {
              return;
            }

            PopupCloserListener.this.dialog.close();

          }
        }
      });
      return;
    }

    // Scroll bar clicked. Keep this for focus event processing.
    if (event.type == SWT.Selection) {
      this.scrollbarClked = true;
      return;
    }

    this.dialog.close();

  }

  /**
   * Set the listeners for events that is to be monitored for popup closure.
   */
  void installListeners() {
    // Listeners on this popup's table and scroll bar
    this.dialog.composite.addListener(SWT.FocusOut, this);

    // Listener for scrollbar
    addListenerOnScrollBar();

    // Listeners on this popup's shell
    // ICDM-1757
    // this_dialog_getShell()_addListener(SWT_Deactivate_this)
    this.dialog.getShell().addListener(SWT.Close, this);

    // Listener for target control
    addListenersToTargetControl();
    addListenersOnShell();

  }

  /**
   *
   */
  private void addListenerOnScrollBar() {
    ScrollBar scrollbar = this.dialog.composite.getVerticalBar();
    if (scrollbar != null) {
      scrollbar.addListener(SWT.Selection, this);
    }
  }

  /**
   *
   */
  private void addListenersOnShell() {
    // Listeners on the target control's shell
    Shell controlShell = this.dialog.getNatTable().getShell();
    controlShell.addListener(SWT.Move, this);
    controlShell.addListener(SWT.Resize, this);
  }

  /**
   *
   */
  private void addListenersToTargetControl() {
    // Listeners on the target control
    this.dialog.getNatTable().addListener(SWT.Dispose, this);
    this.dialog.getNatTable().addListener(SWT.FocusOut, this);
  }

  // Remove installed listeners
  void removeListeners() {
    if (isValid()) {
      this.dialog.composite.removeListener(SWT.FocusOut, this);
      removeListenerOnScrollBar();
      // ICDM-1757
      // this_dialog_getShell()_addListener(SWT_Deactivate_this)
      this.dialog.getShell().removeListener(SWT.Close, this);
    }

    if ((this.dialog.composite != null) && !this.dialog.composite.isDisposed()) {

      // Remove listeners
      removeListenersOnComposite();
      removeListenerOnShell();
    }
  }

  /**
   *
   */
  private void removeListenerOnShell() {
    Shell controlShell = this.dialog.composite.getShell();
    controlShell.removeListener(SWT.Move, this);
    controlShell.removeListener(SWT.Resize, this);
  }

  /**
   *
   */
  private void removeListenersOnComposite() {
    this.dialog.composite.removeListener(SWT.MouseDoubleClick, this);
    this.dialog.composite.removeListener(SWT.MouseDown, this);
    this.dialog.composite.removeListener(SWT.Dispose, this);
    this.dialog.composite.removeListener(SWT.FocusOut, this);
  }

  /**
   *
   */
  private void removeListenerOnScrollBar() {
    ScrollBar scrollbar = this.dialog.composite.getVerticalBar();
    if (scrollbar != null) {
      scrollbar.removeListener(SWT.Selection, this);
    }
  }

  /*
   * Return whether the receiver has focus.
   */
  private boolean hasFocus() {
    if (!isValid()) {
      return false;
    }
    return this.dialog.getShell().isFocusControl() || this.dialog.composite.isFocusControl();

  }

  /**
   * @return true if the popup is valid, means the table has been created and not disposed.
   */
  protected boolean isValid() {
    return (this.dialog.composite != null) && !this.dialog.composite.isDisposed();
  }


}
