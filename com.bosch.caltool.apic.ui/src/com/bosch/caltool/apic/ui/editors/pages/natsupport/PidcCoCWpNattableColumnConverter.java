package com.bosch.caltool.apic.ui.editors.pages.natsupport;

import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.cocwp.CoCWPUsedFlag;
import com.bosch.caltool.icdm.model.apic.cocwp.IProjectCoCWP;
import com.bosch.caltool.nattable.AbstractNatInputToColumnConverter;


/**
 * Column Converter class for nattable
 *
 * @author PDH2COB
 */
public class PidcCoCWpNattableColumnConverter extends AbstractNatInputToColumnConverter {

  /**
   * {@inheritDoc}
   */
  @Override
  public Object getColumnValue(final Object evaluateObj, final int colIndex) {
    Object value = null;

    if (evaluateObj instanceof IProjectCoCWP) {
      IProjectCoCWP projectCocWp = (IProjectCoCWP) evaluateObj;
      switch (colIndex) {
        case 0:
          value = projectCocWp.getName();
          break;
        case 1:
          value = getUsedFlagData(projectCocWp.getUsedFlag(), CoCWPUsedFlag.NOT_DEFINED.getDbType());
          break;
        case 2:
          value = getUsedFlagData(projectCocWp.getUsedFlag(), CoCWPUsedFlag.NO.getDbType());
          break;
        case 3:
          value = getUsedFlagData(projectCocWp.getUsedFlag(), CoCWPUsedFlag.YES.getDbType());
          break;
        case 4:
          value = projectCocWp.isAtChildLevel() ? Boolean.TRUE : Boolean.FALSE;
          break;
        default:
          break;
      }

    }

    return value;
  }


  /**
   * @param usedFlagStr
   * @param flagString
   * @return
   */
  private Object getUsedFlagData(final String usedFlagStr, final String cocWPUsedFlagRef) {
    if (CommonUtils.isEqual(usedFlagStr, cocWPUsedFlagRef)) {
      return Boolean.TRUE;
    }
    return Boolean.FALSE;
  }
}
