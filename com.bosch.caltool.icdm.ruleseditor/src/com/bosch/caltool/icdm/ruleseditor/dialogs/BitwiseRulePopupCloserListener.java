/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ruleseditor.dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Shell;


/**
 * @author mkl2cob
 */
public class BitwiseRulePopupCloserListener implements Listener {

  /**
   * Instance of BitWiseLimitConfigDialog
   */
  private final BitWiseLimitConfigDialog dialog;
  /**
   * true if scrollbar is clicked
   */
  private boolean scrollbarClickStatus;


  /**
   * @param dialog instance of BitWiseLimitConfigDialog
   */
  public BitwiseRulePopupCloserListener(final BitWiseLimitConfigDialog dialog) {
    this.dialog = dialog;
  }

  @Override
  public void handleEvent(final Event event) {

    // On focus out an important widget is deactivating
    if (event.type == SWT.FocusOut) {
      this.scrollbarClickStatus = false;
      /*
       * This event can be ignored when it only happens because of focus moving between the popup shells, their controls
       * or scrollbar.
       */
      doAsyncExecution(event);
      return;
    }

    // Scroll bar is clicked. This can be used for focus event processing.
    if (event.type == SWT.Selection) {
      this.scrollbarClickStatus = true;
      return;
    }

    this.dialog.close();

  }

  /**
   * @param event
   */
  private void doAsyncExecution(final Event event) {
    event.display.asyncExec(new Runnable() {

      @Override
      public void run() {
        if (isValid()) {
          if (BitwiseRulePopupCloserListener.this.scrollbarClickStatus || hasFocus()) {
            return;
          }

          // Check the active shell.
          Shell activeShell = event.display.getActiveShell();
          if (activeShell == BitwiseRulePopupCloserListener.this.dialog.getShell()) {
            return;
          }

          BitwiseRulePopupCloserListener.this.dialog.close();

        }
      }
    });
  }

  // listeners are installed for events that should be monitored for popup closure.
  void installAllListeners() {
    // Listeners on this popup's table
    this.dialog.composite.addListener(SWT.FocusOut, this);
    // On scrollbar
    addListenerToScrollBar();

    // Listeners on this popup's shell
    addListenerToShell();
    // Listeners on the target control
    addListenersToTargetControl();

  }

  /**
   * Listeners on the target control
   */
  private void addListenersToTargetControl() {
    Shell controlShell;
    if (null == this.dialog.getRuleInfoSection().getEditRuleDialog()) {
      controlShell = this.dialog.getRuleInfoSection().getWizardPage().getShell();
    }
    else {
      controlShell = this.dialog.getRuleInfoSection().getEditRuleDialog().getShell();
    }
    controlShell.addListener(SWT.Dispose, this);
    controlShell.addListener(SWT.FocusOut, this);
    controlShell.addListener(SWT.Move, this);
    controlShell.addListener(SWT.Resize, this);
  }

  /**
   * Listener on shell
   */
  private void addListenerToShell() {
    this.dialog.getShell().addListener(SWT.Close, this);
  }

  /**
   * Listener on scroll bar
   */
  private void addListenerToScrollBar() {
    ScrollBar scrollbar = this.dialog.composite.getVerticalBar();
    if (scrollbar != null) {
      scrollbar.addListener(SWT.Selection, this);
    }
  }

  // Remove all the installed listeners
  void removeAllListeners() {
    if (isValid()) {
      this.dialog.composite.removeListener(SWT.FocusOut, this);
      removeListenerOnScrollBar();
      this.dialog.getShell().removeListener(SWT.Close, this);
    }

    if ((this.dialog.composite != null) && !this.dialog.composite.isDisposed()) {

      removeListenerOnDialog();
    }
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

  /**
   *
   */
  private void removeListenerOnDialog() {
    this.dialog.composite.removeListener(SWT.MouseDoubleClick, this);
    this.dialog.composite.removeListener(SWT.MouseDown, this);
    this.dialog.composite.removeListener(SWT.Dispose, this);
    this.dialog.composite.removeListener(SWT.FocusOut, this);

    Shell controlShell = this.dialog.composite.getShell();
    controlShell.removeListener(SWT.Move, this);
    controlShell.removeListener(SWT.Resize, this);
  }

  /*
   * Return if the receiver has focus. From 3.4, this includes a check for whether the info popup has focus.
   */
  private boolean hasFocus() {
    if (!isValid()) {
      return false;
    }
    return this.dialog.getShell().isFocusControl() || this.dialog.composite.isFocusControl();
  }


  /**
   * @return true if the popup is valid, which means the table has been created and not disposed.
   */
  protected boolean isValid() {
    return (this.dialog.composite != null) && !this.dialog.composite.isDisposed();
  }


}
