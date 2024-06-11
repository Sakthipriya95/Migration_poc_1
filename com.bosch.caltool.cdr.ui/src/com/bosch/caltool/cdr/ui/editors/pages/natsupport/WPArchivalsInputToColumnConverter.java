/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.editors.pages.natsupport;

import com.bosch.caltool.cdr.ui.editors.WPArchivalsEditorConstants;
import com.bosch.caltool.icdm.client.bo.cdr.WPArchivalsListEditorDataHandler;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.WpArchival;
import com.bosch.caltool.nattable.AbstractNatInputToColumnConverter;


/**
 * This class converts the input into column data
 *
 * @author ukt1cob
 */
public class WPArchivalsInputToColumnConverter extends AbstractNatInputToColumnConverter {

  private final WPArchivalsListEditorDataHandler dataHandler;

  /**
   * @param dataHandler WPArchivalsListEditorDataHandler
   */
  public WPArchivalsInputToColumnConverter(final WPArchivalsListEditorDataHandler dataHandler) {
    this.dataHandler = dataHandler;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Object getColumnValue(final Object evaluateObj, final int colIndex) {
    String result = null;
    if (evaluateObj instanceof WpArchival) {
      WpArchival wpArchival = (WpArchival) evaluateObj;
      switch (colIndex) {
        case WPArchivalsEditorConstants.COLNDX_WP_ARCHIVAL_DATE:
          // date and time of archival
          result = this.dataHandler.getFormattedDate(wpArchival.getCreatedDate());
          break;
        case WPArchivalsEditorConstants.COLNDX_BASELINE_NAME:
          result = wpArchival.getBaselineName();
          break;
        case WPArchivalsEditorConstants.COLNDX_WP_DEF_VERS_NAME:
          result = wpArchival.getWpDefnVersName();
          break;
        case WPArchivalsEditorConstants.COLNDX_A2L_FILE_NAME:
          result = wpArchival.getA2lFilename();
          break;
        case WPArchivalsEditorConstants.COLNDX_PIDC_VERSION_NAME:
          result = wpArchival.getPidcVersFullname();
          break;
        case WPArchivalsEditorConstants.COLNDX_VARIANT_NAME:
          result = wpArchival.getVariantName();
          break;
        case WPArchivalsEditorConstants.COLNDX_WP_NAME:
          result = wpArchival.getWpName();
          break;
        case WPArchivalsEditorConstants.COLNDX_RESP_NAME:
          result = wpArchival.getRespName();
          break;
        case WPArchivalsEditorConstants.COLNDX_STATUS:
          result = getFileStatus(wpArchival);
          break;
        default:
          result = "";
          break;
      }
    }
    return result;

  }

  /**
   * @param wpArchival
   * @return
   */
  private String getFileStatus(final WpArchival wpArchival) {

    if (CDRConstants.FILE_ARCHIVAL_STATUS.IN_PROGRESS.getDbType().equals(wpArchival.getFileArchivalStatus())) {
      return CDRConstants.FILE_ARCHIVAL_STATUS.IN_PROGRESS.getUiType();
    }
    else if (CDRConstants.FILE_ARCHIVAL_STATUS.COMPLETED.getDbType().equals(wpArchival.getFileArchivalStatus())) {
      return CDRConstants.FILE_ARCHIVAL_STATUS.COMPLETED.getUiType();
    }
    else if (CDRConstants.FILE_ARCHIVAL_STATUS.FAILED.getDbType().equals(wpArchival.getFileArchivalStatus())) {
      return CDRConstants.FILE_ARCHIVAL_STATUS.FAILED.getUiType();
    }
    else {
      return CDRConstants.FILE_ARCHIVAL_STATUS.NOT_AVAILABLE.getUiType();
    }
  }
}
