package com.bosch.caltool.apic.ui.sorter;

import org.eclipse.jface.viewers.Viewer;

import com.bosch.caltool.icdm.client.bo.apic.PIDCVersionsDataHandler;
import com.bosch.caltool.icdm.client.bo.apic.PidcVersionStatus;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.rcputils.sorters.AbstractViewerSorter;

/**
 * @author bru2cob
 */
public class PIDCVersTabViewerSorter extends AbstractViewerSorter {

  private int index = 0;
  /**
   * Constant for descending
   */
  private static final int DESCENDING = 1;
  /**
   * Constant for ascending
   */
  private static final int ASCENDING = 0;
  // Default diraction ascending
  private int direction = ASCENDING;
  private final PIDCVersionsDataHandler pidcVersionsDataHandler;

  /**
   * @param index defines grid tableviewercolumn index
   */
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

  /**
   * @param pidcVersionsDataHandler pidcVersionsDataHandler
   */
  public PIDCVersTabViewerSorter(final PIDCVersionsDataHandler pidcVersionsDataHandler) {
    super();
    this.pidcVersionsDataHandler = pidcVersionsDataHandler;
  }

  @Override
  public int compare(final Viewer viewer, final Object obj1, final Object obj2) {
    PidcVersion pidcVers1 = (PidcVersion) obj1;
    PidcVersion pidcVers2 = (PidcVersion) obj2;
    int compare;
    switch (this.index) {
      case CommonUIConstants.COLUMN_INDEX_0:
        // version name
        compare = ApicUtil.compare(pidcVers1.getVersionName(), pidcVers2.getVersionName());
        break;

      case CommonUIConstants.COLUMN_INDEX_1:
        compare = ApicUtil.compare(pidcVers1.getDescription(), pidcVers2.getDescription());
        break;

      case CommonUIConstants.COLUMN_INDEX_2:
        compare = ApicUtil.compare(PidcVersionStatus.getStatus(pidcVers1.getPidStatus()).getUiStatus(),
            PidcVersionStatus.getStatus(pidcVers2.getPidStatus()).getUiStatus());
        break;

      case CommonUIConstants.COLUMN_INDEX_3:
        compare = comparePidcVersions(pidcVers1, pidcVers2);
        break;

      case CommonUIConstants.COLUMN_INDEX_4:
        compare = ApicUtil.compare(this.pidcVersionsDataHandler.getCreatedDateFormatted(pidcVers1.getCreatedDate()),
            this.pidcVersionsDataHandler.getCreatedDateFormatted(pidcVers2.getCreatedDate()));
        break;

      case CommonUIConstants.COLUMN_INDEX_5:
        compare = ApicUtil.compare(pidcVers1.getCreatedUser(), pidcVers2.getCreatedUser());
        break;
      default:
        compare = 0;
    }
    // If descending order, flip the direction
    if (this.direction == DESCENDING) {
      compare = -compare;
    }
    return compare;
  }

  /**
   * @param pidcVers1
   * @param pidcVers2
   * @return
   */
  private int comparePidcVersions(PidcVersion pidcVers1, PidcVersion pidcVers2) {
    int compare;
    Long activeVersId = this.pidcVersionsDataHandler.getActivePidcVersn().getId();
    boolean vers1 = false, vers2 = false;
    if (pidcVers1.getId().equals(activeVersId)) {
      vers1 = true;
    }
    if (pidcVers2.getId().equals(activeVersId)) {
      vers2 = true;
    }
    compare = ApicUtil.compareBoolean(vers1, vers2);
    return compare;
  }

  /**
   * @return defines
   */
  @Override
  public int getDirection() {
    return this.direction;
  }
}