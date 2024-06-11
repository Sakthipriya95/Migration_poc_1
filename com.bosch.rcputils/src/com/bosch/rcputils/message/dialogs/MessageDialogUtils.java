/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.rcputils.message.dialogs;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;


/**
 * @author mga1cob
 */
public final class MessageDialogUtils {


  /**
   * Constant for YES button being pressed in confirm dialog
   */
  private static final int YES_PRESSED = 0;
  /**
   * boolean return value
   */
  private static boolean confirm;
  /**
   * MessageDialogUtils instance
   */
  private static MessageDialogUtils attrUtil;

  /**
   * Private Constructor
   */
  private MessageDialogUtils() {
    // Private Constructor
  }

  /**
   * This method returns MessageDialogUtils instance
   * 
   * @return MessageDialogUtils instance
   */
  public static MessageDialogUtils getInstance() {
    if (attrUtil == null) {
      attrUtil = new MessageDialogUtils();
    }
    return attrUtil;
  }

  /**
   * Invokes the information message dialog
   * 
   * @param title defines the title message dialog
   * @param message defines meesage to dispaly in the dialog
   */
  public static void getInfoMessageDialog(final String title, final String message) {
    final Display display = Display.getCurrent();
    if (display == null) {
      // If display is null, show message in async mode
      Display.getDefault().asyncExec(new Runnable() {

        @Override
        public void run() {
          MessageDialog.openInformation(Display.getCurrent().getActiveShell(), title, message);
        }
      });

    }
    else {
      MessageDialog.openInformation(Display.getCurrent().getActiveShell(), title, message);
    }
  }

  /**
   * Invokes the error message dialog
   * 
   * @param title defines the title message dialog
   * @param message defines meesage to dispaly in the dialog
   */
  public static void getErrorMessageDialog(final String title, final String message) {
    final Display display = Display.getCurrent();
    if (display == null) {
      // If display is null, show message in async mode
      Display.getDefault().asyncExec(new Runnable() {

        @Override
        public void run() {
          MessageDialog.openError(Display.getCurrent().getActiveShell(), title, message);
        }
      });

    }
    else {
      MessageDialog.openError(Display.getCurrent().getActiveShell(), title, message);
    }
  }

  /**
   * Invokes the question message dialog
   * 
   * @param title defines the title message dialog
   * @param message defines meesage to dispaly in the dialog
   * @return boolean
   */
  public static boolean getQuestionMessageDialog(final String title, final String message) {
    final Display display = Display.getCurrent();
    if (display == null) {
      // If display is null, show message in sync mode
      Display.getDefault().syncExec(new Runnable() {

        @Override
        public void run() {
          confirm = MessageDialog.openQuestion(Display.getCurrent().getActiveShell(), title, message);
        }
      });

    }
    else {
      confirm = MessageDialog.openQuestion(Display.getCurrent().getActiveShell(), title, message);
    }
    return confirm;

  }

  /**
   * Invokes the warning message dialog
   * 
   * @param title defines the title message dialog
   * @param message defines meesage to dispaly in the dialog
   */
  public static void getWarningMessageDialog(final String title, final String message) {
    final Display display = Display.getCurrent();
    if (display == null) {
      // If display is null, show message in async mode
      Display.getDefault().asyncExec(new Runnable() {

        @Override
        public void run() {
          MessageDialog.openWarning(Display.getCurrent().getActiveShell(), title, message);
        }
      });

    }
    else {
      MessageDialog.openWarning(Display.getCurrent().getActiveShell(), title, message);
    }
  }

  /**
   * Invokes the confirm message dialog
   * 
   * @param title defines the title message dialog
   * @param message defines meesage to dispaly in the dialog
   * @return boolean
   */
  public static boolean getConfirmMessageDialog(final String title, final String message) {
    final Display display = Display.getCurrent();
    if (display == null) {
      // If display is null, show message in sync mode
      Display.getDefault().syncExec(new Runnable() {

        @Override
        public void run() {
          MessageDialogUtils.confirm = MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), title, message);
        }
      });
    }
    else {
      confirm = MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), title, message);
    }
    return confirm;
  }

  /**
   * ICDM-1494 Invokes the confirm message dialog
   * 
   * @param title defines the title message dialog
   * @param message defines meesage to dispaly in the dialog
   * @return boolean
   */
  public static boolean getConfirmMessageDialogWithYesNo(final String title, final String message) {
    final Display display = Display.getCurrent();
    if (display == null) {
      // If display is null, show message in sync mode
      Display.getDefault().syncExec(new Runnable() {

        @Override
        public void run() {
          MessageDialog msgDialog =
              new MessageDialog(Display.getCurrent().getActiveShell(), title, null, message, MessageDialog.CONFIRM,
                  new String[] { "Yes", "No" }, 0);
          if (msgDialog.open() == YES_PRESSED) {
            confirm = true;
          }
          else {
            confirm = false;
          }
        }
      });

    }
    else {
      MessageDialog msgDialog =
          new MessageDialog(Display.getCurrent().getActiveShell(), title, null, message, MessageDialog.CONFIRM,
              new String[] { "Yes", "No" }, 0);
      if (msgDialog.open() == YES_PRESSED) {
        confirm = true;
      }
      else {
        confirm = false;
      }
    }
    return confirm;
  }
}
