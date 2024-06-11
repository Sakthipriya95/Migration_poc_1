package com.bosch.caltool.icdm.common.ui.sorter;

import org.eclipse.jface.viewers.Viewer;

import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.model.a2l.A2lRespBoschGroupUser;
import com.bosch.caltool.icdm.model.a2l.A2lResponsibilityModel;
import com.bosch.caltool.icdm.model.user.User;
import com.bosch.rcputils.sorters.AbstractViewerSorter;


/**
 * Sorter Class
 *
 * @author pdh2cob
 */
public class BoschUserGridTableSorter extends AbstractViewerSorter {


  /**
   * index for Sorting
   */
  private int index;
  /**
   * Constant for descending
   */
  private static final int DESCENDING = 1;
  /**
   * Constant for ascending
   */
  private static final int ASCENDING = 0;
  /**
   * Default ascending
   */
  private int direction = ASCENDING;

  private final A2lResponsibilityModel respModel;


  /**
   * @param respModel - A2lResponsibilityModel
   */
  public BoschUserGridTableSorter(final A2lResponsibilityModel respModel) {
    super();
    this.respModel = respModel;
  }

  /**
   * {@inheritDoc}
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
   * {@inheritDoc}
   */
  @Override
  public int compare(final Viewer viewer, final Object obj1, final Object obj2) {

    User user1 = this.respModel.getUserMap().get(((A2lRespBoschGroupUser) obj1).getUserId());
    User user2 = this.respModel.getUserMap().get(((A2lRespBoschGroupUser) obj2).getUserId());

    int compare;

    switch (this.index) {

      case 0:
        compare = ApicUtil.compare(user1.getFirstName(), user2.getFirstName());
        break;
      case 1:
        compare = ApicUtil.compare(user1.getName(), user2.getName());
        break;
      case 2:
        compare = ApicUtil.compare(user1.getDepartment(), user2.getDepartment());
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
   * {@inheritDoc}
   */
  @Override
  public int getDirection() {
    return this.direction;
  }

}
