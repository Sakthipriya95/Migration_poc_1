/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.action;

import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.ui.action.IMouseClickAction;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Display;

import com.bosch.caltool.icdm.client.bo.a2l.A2LWPInfoBO;
import com.bosch.caltool.icdm.client.bo.a2l.A2LWpParamInfo;
import com.bosch.caltool.icdm.ui.dialogs.UpdateWpParamDetailsDialog;
import com.bosch.caltool.icdm.ui.editors.pages.WPLabelAssignNatPage;


/**
 * @author nip4cob
 */
public class OpenParamEditDialogAction implements IMouseClickAction {

  private final WPLabelAssignNatPage wpLabelAssignNatPage;
  private final A2LWPInfoBO a2LWPInfoBO;

  /**
   * @param wpLabelAssignNatPage WPLabelAssignNatPage
   */
  public OpenParamEditDialogAction(final WPLabelAssignNatPage wpLabelAssignNatPage) {
    this.wpLabelAssignNatPage = wpLabelAssignNatPage;
    this.a2LWPInfoBO = this.wpLabelAssignNatPage.getA2lWPInfoBO();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void run(final NatTable natTable, final MouseEvent event) {
    if (this.a2LWPInfoBO.isParamEditAllowed()) {
      UpdateWpParamDetailsDialog editPar2WPDialog = new UpdateWpParamDetailsDialog(
          Display.getCurrent().getActiveShell(), (A2LWpParamInfo) this.wpLabelAssignNatPage.getSelectedObj(),
          this.a2LWPInfoBO.getA2lWpParamMappingModel().getSelectedWpDefnVersionId(), this.a2LWPInfoBO);
      editPar2WPDialog.open();
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
