/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.action;

import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.ui.action.IMouseClickAction;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Display;

import com.bosch.caltool.icdm.client.bo.a2l.A2LWPInfoBO;
import com.bosch.caltool.icdm.common.ui.dialogs.CreateEditWpRespDialog;
import com.bosch.caltool.icdm.model.a2l.A2lWpResponsibility;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.ui.editors.pages.A2lWPDefinitionPage;


/**
 * @author nip4cob
 */
public class OpenEditWpRespDialogAction implements IMouseClickAction {

  private final A2LWPInfoBO a2lWpInfoBO;
  private final A2lWPDefinitionPage a2lWPDefinitionPage;

  /**
   * @param a2lWpInfoBO a2lWpInfoBO
   * @param a2lWPDefinitionPage a2lWPDefinitionPage
   */
  public OpenEditWpRespDialogAction(final A2LWPInfoBO a2lWpInfoBO, final A2lWPDefinitionPage a2lWPDefinitionPage) {
    this.a2lWpInfoBO = a2lWpInfoBO;
    this.a2lWPDefinitionPage = a2lWPDefinitionPage;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void run(final NatTable natTable, final MouseEvent event) {

    if (this.a2lWpInfoBO.isEditable()) {
      A2lWpResponsibility selectedRow = this.a2lWPDefinitionPage.getSelectedRow();
      if (!selectedRow.getName().equals(ApicConstants.DEFAULT_A2L_WP_NAME) &&
          this.a2lWpInfoBO.isMappedToSelectedVarGrp(selectedRow)) {
        CreateEditWpRespDialog wpRespDialog =
            new CreateEditWpRespDialog(Display.getCurrent().getActiveShell(), this.a2lWpInfoBO, selectedRow, true);
        wpRespDialog.open();
        if (null != wpRespDialog.getNew2lWpRespObj()) {
          this.a2lWPDefinitionPage.setSelectedRow(wpRespDialog.getNew2lWpRespObj());
        }
      }
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
