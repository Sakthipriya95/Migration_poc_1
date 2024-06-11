/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.reports;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

import com.bosch.calmodel.a2ldata.ref.concrete.DefCharacteristic;
import com.bosch.caltool.icdm.common.ui.Activator;
import com.bosch.caltool.icdm.logger.CDMLogger;


/**
 * iCDM-360 Icdm-521 Moved the class to common UI
 *
 * @author adn1cob
 */
public class CreateLISFile {

  /**
   * The progress monitor instance to which progess information is to be set.
   */
  private final IProgressMonitor monitor;
  /**
   * Default Character set
   */
  private static final String CHAR_SET_UTF_8 = "UTF-8";

  /**
   * Constructor
   *
   * @param monitor defines IProgressMonitor
   */
  public CreateLISFile(final IProgressMonitor monitor) {

    this.monitor = monitor;
  }

  /**
   * @param paramList defines Characteristics objects
   * @param filePath defines file path
   */
  public void createFile(final List<DefCharacteristic> paramList, final String filePath) {
    this.monitor.beginTask("Create LIS file: ", IProgressMonitor.UNKNOWN);
    PrintWriter writer = null;
    try {
      writer = new PrintWriter(filePath, CHAR_SET_UTF_8);
      // Write characteristic NAME and UNIT in this format
      // Airbg_nEngThresMin_C "rpm"
      for (DefCharacteristic defCharacteristic : paramList) {
        String unit = parseUnit(defCharacteristic.getObj().getUnit());
        // write def characteristic name and unit into writer
        writer.println(defCharacteristic.getObj().getName() + " " + "\"" + unit + "\"");
      }

      if (this.monitor.isCanceled()) {
        // when the progress monitor is cancelled
        CDMLogger.getInstance().info("Creation of LIS file failed", Activator.PLUGIN_ID);
      }
    }
    catch (FileNotFoundException fnfe) {
      // file not found exception
      CDMLogger.getInstance().error("Unable to create file " + filePath + "-" + fnfe.getLocalizedMessage(), fnfe,
          Activator.PLUGIN_ID);
      this.monitor.setCanceled(true);
    }

    catch (UnsupportedEncodingException usee) {
      // unsupported encoding exception
      CDMLogger.getInstance().error("Unsupported encoding " + CHAR_SET_UTF_8 + "-" + usee.getLocalizedMessage(), usee,
          Activator.PLUGIN_ID);
      this.monitor.setCanceled(true);
    }
    catch (Exception exp) {
      CDMLogger.getInstance().error(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
      this.monitor.setCanceled(true);
    }
    finally {
      if (writer != null) {
        writer.close();
      }
    }
  }

  /**
   * Removes character '[' and ']', if contains [ as the first character or ] as last character from the label unit
   *
   * @param unit
   * @return String
   */
  private String parseUnit(final String unit) {
    String retVal = unit;
    if ((unit != null) && (unit.length() > 0)) {
      // when there is atleast one character in unit
      if (unit.charAt(0) == '[') {
        // remove '['
        retVal = unit.substring(1);
      }
      int len = retVal.length();
      if (retVal.charAt(len - 1) == ']') {
        // remove ']'
        retVal = retVal.substring(0, len - 1);
      }
    }
    return retVal;
  }

}
