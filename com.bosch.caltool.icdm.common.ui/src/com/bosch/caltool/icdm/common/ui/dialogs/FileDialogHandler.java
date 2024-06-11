/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.dialogs;

import java.util.Locale;

import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import com.bosch.caltool.icdm.logger.CDMLogger;


/**
 * // ICDM-1048
 *
 * @author dmo5cob
 */
public class FileDialogHandler {

  private final FileDialog fileDialog;

  /**
   * @param parent composite
   * @param style SWT style
   */
  public FileDialogHandler(final Shell parent, final int style) {
    this.fileDialog = new FileDialog(parent, style);
  }

  /**
   * @return index
   */
  public int getFilterIndex() {
    return setFilterIndex();
  }

  /**
   * @return string
   */
  public String open() {

    String[] filterExtns = this.fileDialog.getFilterExtensions();
    String selFilterExtn = filterExtns[this.fileDialog.getFilterIndex()];
    // Multiple files selected
    if (getFileNames().length > 1) {
      for (String filename : getFileNames()) {
        // ICDM-1311
        String[] fileNameExtn = filename.split("\\.");
        if (!(selFilterExtn.toLowerCase(Locale.getDefault()))
            .contains(fileNameExtn[fileNameExtn.length - 1].toLowerCase(Locale.getDefault()))) {
          CDMLogger.getInstance().errorDialog(
              "The file extensions are not matching the file type chosen. Please correct.",
              com.bosch.calcomp.adapter.logger.Activator.PLUGIN_ID);
          return "";
        }
      }
    }
    return this.fileDialog.open();
  }

  /**
   * This method overrides the filterindex if one file is selected and the file extension doesn't equal the extension
   * filter of the file chooser, use the extension of the file to determine the file type (because in this case, the
   * user must have pasted the file name in the file dialog).
   */
  private int setFilterIndex() {
    String[] filterExtns = this.fileDialog.getFilterExtensions();
    String selFilterExtn = filterExtns[this.fileDialog.getFilterIndex()];
    for (String extns : filterExtns) {
      if (this.fileDialog.getFileName().contains(extns.substring(extns.indexOf("."), extns.length()))) {
        String fileNameExtn = this.fileDialog.getFileName().substring(this.fileDialog.getFileName().indexOf("."),
            this.fileDialog.getFileName().length());
        if (!fileNameExtn
            .equalsIgnoreCase(selFilterExtn.substring(selFilterExtn.indexOf("."), selFilterExtn.length()))) {
          for (int i = 0; i < filterExtns.length; i++) {
            if (filterExtns[i].contains(fileNameExtn)) {
              return i;
            }
          }
        }
      }
    }
    return this.fileDialog.getFilterIndex();
  }

  /**
   * @return FileName
   */
  public String getFileName() {
    return this.fileDialog.getFileName();
  }

  /**
   * @return String[]
   */
  public String[] getFileNames() {
    return this.fileDialog.getFileNames();
  }

  /**
   * @return String[]
   */
  public String[] getFilterExtensions() {
    return this.fileDialog.getFilterExtensions();
  }


  /**
   * @return String[]
   */
  public String[] getFilterNames() {
    return this.fileDialog.getFilterNames();
  }

  /**
   * @return FilterPath
   */
  public String getFilterPath() {
    return this.fileDialog.getFilterPath();
  }

  /**
   * @return boolean
   */
  public boolean getOverwrite() {
    return this.fileDialog.getOverwrite();
  }

  /**
   * @param string filename
   */
  public void setFileName(final String string) {
    this.fileDialog.setFileName(string);
  }

  /**
   * @param extensions filterextns
   */
  public void setFilterExtensions(final String extensions[]) {
    this.fileDialog.setFilterExtensions(extensions);
  }

  /**
   * @param index filterindex
   */
  public void setFilterIndex(final int index) {
    this.fileDialog.setFilterIndex(index);
  }

  /**
   * @param names filternames
   */
  public void setFilterNames(final String names[]) {
    this.fileDialog.setFilterNames(names);
  }

  /**
   * @param string filterpath
   */
  public void setFilterPath(final String string) {
    this.fileDialog.setFilterPath(string);
  }

  /**
   * @param string filterpath
   */
  public void setText(final String string) {
    this.fileDialog.setText(string);
  }

  /**
   * @param overwrite true/false
   */
  public void setOverwrite(final boolean overwrite) {
    this.fileDialog.setOverwrite(overwrite);
  }

}
