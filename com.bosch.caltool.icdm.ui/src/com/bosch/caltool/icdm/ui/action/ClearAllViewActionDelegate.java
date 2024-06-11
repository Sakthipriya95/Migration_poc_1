package com.bosch.caltool.icdm.ui.action;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

import com.bosch.caltool.icdm.common.ui.views.ScratchPadViewPart;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.rcputils.wbutils.WorkbenchUtils;


/**
 * @author dmo5cob
 */
public class ClearAllViewActionDelegate implements IViewActionDelegate {

  @Override
  public void run(final IAction action) {

    try {
      // Get the scratchpad view
      ScratchPadViewPart scratchViewPartNew = (ScratchPadViewPart) WorkbenchUtils.getView(ScratchPadViewPart.PART_ID);
      // Get the scratchpad tableviewer
      // Empty the table
      scratchViewPartNew.getTableViewer().getTable().removeAll();
      // clear the node list
      scratchViewPartNew.getNodeList().clear();
    }
    // throw exception
    catch (Exception exp) {
      CDMLogger.getInstance().error("Error :" + exp.toString(), exp);

    }
  }

  @Override
  public void selectionChanged(final IAction action, final ISelection selection) {
    // TO-DO
  }

  @Override
  public void init(final IViewPart view) {
    // TO-DO
  }

}
