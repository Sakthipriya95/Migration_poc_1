package com.bosch.caltool.cdr.ui.sorters;

import org.eclipse.jface.viewers.Viewer;

import com.bosch.caltool.cdr.ui.dialogs.CopyResToVarDialog;
import com.bosch.caltool.cdr.ui.util.CdrUIConstants;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.rcputils.sorters.AbstractViewerSorter;

public class CopyResToVarTabViewerSorter extends AbstractViewerSorter {

  private int index = 2;
  private static final int DESCENDING = 1;
  private static final int ASCENDING = 0;
  private int direction = ASCENDING;
  CopyResToVarDialog copyResToVarDialog;

  /**
   * @param copyResToVarDialog
   */
  public CopyResToVarTabViewerSorter(final CopyResToVarDialog copyResToVarDialog) {
    this.copyResToVarDialog = copyResToVarDialog;
  }

  @Override
  public void setColumn(final int index) {
    if (index == this.index) {
      this.direction = 1 - this.direction;
    }
    else {
      this.index = index;
      this.direction = ASCENDING;
    }
  }

  @Override
  public int compare(final Viewer viewer, final Object obj1, final Object obj2) {
    PidcVariant variant1 = (PidcVariant) obj1;
    PidcVariant variant2 = (PidcVariant) obj2;


    int result;
    switch (this.index) {
      case 0:
        result = ApicUtil.compare(variant1.getName(), variant2.getName());
        break;
      case 1:
        result = ApicUtil.compare(variant1.getDescription(), variant2.getDescription());
        break;
      case 2:
        result = caseVarName(variant1, variant2);
        break;
      default:
        result = 0;
    }
    // If descending order, flip the direction
    if (this.direction == DESCENDING) {
      result = -result;
    }
    return result;
  }

  /**
   * @param variant1
   * @param variant2
   * @return
   */
  private int caseVarName(final PidcVariant variant1, final PidcVariant variant2) {
    int result;
    String desc1 = "", desc2 = "";
    if (this.copyResToVarDialog.getVarWithoutDepMatch().contains(variant1)) {
      desc1 = CdrUIConstants.DEPENDANT_NOT_SET;
    }
    else if (this.copyResToVarDialog.getLinkedVars().contains(variant1)) {
      desc1 = CdrUIConstants.LINKED_RESULT;
    }
    else if (this.copyResToVarDialog.getVarWithDiffPver().contains(variant1)) {
      desc1 = CdrUIConstants.DIFFERENT_SDOM_PVER;
    }
    else if (this.copyResToVarDialog.getSameVarGrpStatusMap().containsKey(variant1.getId()) &&
        !this.copyResToVarDialog.getSameVarGrpStatusMap().get(variant1.getId())) {
      desc1 = CdrUIConstants.DIFFERENT_VAR_GROUP;
    }

    if (this.copyResToVarDialog.getVarWithoutDepMatch().contains(variant2)) {
      desc2 = CdrUIConstants.DEPENDANT_NOT_SET;
    }
    else if (this.copyResToVarDialog.getLinkedVars().contains(variant2)) {
      desc2 = CdrUIConstants.LINKED_RESULT;
    }

    else if (this.copyResToVarDialog.getVarWithDiffPver().contains(variant2)) {
      desc2 = CdrUIConstants.DIFFERENT_SDOM_PVER;
    }
    else if (this.copyResToVarDialog.getSameVarGrpStatusMap().containsKey(variant2.getId()) &&
        !this.copyResToVarDialog.getSameVarGrpStatusMap().get(variant2.getId())) {
      desc2 = CdrUIConstants.DIFFERENT_VAR_GROUP;
    }

    result = ApicUtil.compare(desc1, desc2);
    if (result == ApicConstants.OBJ_EQUAL_CHK_VAL) {
      result = ApicUtil.compare(variant1.getName(), variant2.getName());
    }
    return result;
  }

  @Override
  public int getDirection() {
    return this.direction;
  }
}