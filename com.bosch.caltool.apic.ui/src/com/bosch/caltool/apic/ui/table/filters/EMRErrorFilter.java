/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.table.filters;

import java.util.Map.Entry;

import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;
import com.bosch.caltool.icdm.model.emr.EmrFile;
import com.bosch.caltool.icdm.model.emr.EmrUploadError;

/**
 * @author mkl2cob
 */
public class EMRErrorFilter extends AbstractViewerFilter {

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean selectElement(final Object element) {
    if (element instanceof Entry<?, ?>) {
      final Entry<EmrFile, EmrUploadError> errorEntry = (Entry<EmrFile, EmrUploadError>) element;

      // Check for file name match
      if (checkEMRFileName(errorEntry.getKey())) {
        return true;
      }
      if (checkEMRErrorRowCatMsg(errorEntry.getValue())) {
        return true;
      }


    }
    return false;
  }

  /**
   * @param value EmrUploadError
   * @return boolean
   */
  private boolean checkEMRErrorRowCatMsg(final EmrUploadError value) {
    return matchText(value.getRowNumber().toString()) || matchText(value.getErrorCategory()) ||
        matchText(value.getErrorMessage()) || matchText(value.getErrorData());
  }

  /**
   * @param key
   * @return
   */
  private boolean checkEMRFileName(final EmrFile emrFile) {
    return matchText(emrFile.getName());
  }


}
