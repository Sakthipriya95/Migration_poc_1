package com.bosch.caltool.apic.ui.sorter;

import org.eclipse.jface.viewers.Viewer;

import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueModel;
import com.bosch.rcputils.sorters.AbstractViewerSorter;


/**
 * Icdm-1245- New Sorter for Attr Dep in Rule History.
 *
 * @author rgo7cob New Sorter for Dependency in Rule History
 */
public class DependencyGridTabViewerSorter extends AbstractViewerSorter {

  /**
   * Column index
   */
  private int index;


  // Default is Ascending Order
  private int direction = ApicConstants.ASCENDING;

  /**
   * {@inheritDoc} set the Column index.
   */
  @Override
  public void setColumn(final int index) {
    if (index == this.index) {
      this.direction = 1 - this.direction;
    }
    else {
      this.index = index;
      this.direction = ApicConstants.ASCENDING;
    }
  }

  @Override
  public int compare(final Viewer viewer, final Object object1, final Object object2) {
    // Compare two Attr Value model Objets.
    AttributeValueModel attr1 = (AttributeValueModel) object1;
    AttributeValueModel attr2 = (AttributeValueModel) object2;
    int result;
    switch (this.index) {
      // Compare attr Name
      case 0:
        result = ApicUtil.compare(attr1.getAttr().getName(), attr2.getAttr().getName());
        break;
      // Compare attr Value
      case 1:
        result = ApicUtil.compare(attr1.getValue().getNameRaw(), attr2.getValue().getNameRaw());
        break;
      default:
        // Defualt result is Equal.
        result = 0;

    }
    // If descending order, flip the direction
    if (this.direction == ApicConstants.DESCENDING) {
      result = -result;
    }
    return result;
  }

  /**
   * {@inheritDoc} get the Direction
   */
  @Override
  public int getDirection() {
    return this.direction;
  }
}