/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.actions;

import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.ui.action.IMouseClickAction;
import org.eclipse.swt.events.MouseEvent;

import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.cdr.ui.dialogs.QuestionDialog;
import com.bosch.caltool.cdr.ui.editors.pages.QuestionDetailsPage;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.ui.utils.IMessageConstants;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.rcputils.message.dialogs.MessageDialogUtils;

/**
 * @author NIP4COB
 */
public class QuestionEditMouseClickAction implements IMouseClickAction {

  private final QuestionDetailsPage questionDetailsPage;

  /**
   * @param questionDetailsPage questionDetailsPage
   */
  public QuestionEditMouseClickAction(final QuestionDetailsPage questionDetailsPage) {
    this.questionDetailsPage = questionDetailsPage;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void run(final NatTable natTable, final MouseEvent event) {

    // check for access rights to modify the questionniare
    if (this.questionDetailsPage.getQnaireDefBo().isModifiable()) {
      if (null == this.questionDetailsPage.getSelectedQues()) {
        MessageDialogUtils.getErrorMessageDialog("Updation", "Please select a question to update");
      }
      else {
        // open edit question dialog
        QuestionDialog dialog = new QuestionDialog(CommonUiUtils.getInstance().getDisplay().getActiveShell(),
            this.questionDetailsPage.getSelectedQues(), this.questionDetailsPage.getQnaireDefBo(), true);
        dialog.open();
      }
      this.questionDetailsPage.refreshOutlineViewer();

    }
    else {
      CDMLogger.getInstance().warnDialog(IMessageConstants.INSUFFICIENT_PRIVILEDGE_MSG, Activator.PLUGIN_ID);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isExclusive() {
    return false;
  }

}