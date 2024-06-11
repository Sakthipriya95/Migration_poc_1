/**********************************************************
 * Copyright (c) 2009, Robert Bosch GmbH All rights reserved.
 ***********************************************************/
package com.bosch.calcomp.pacotocaldata.test;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.calcomp.adapter.logger.Log4JLoggerAdapterImpl;
import com.bosch.calcomp.pacoparser.PacoParser;
import com.bosch.calcomp.pacoparser.exception.PacoParserException;
import com.bosch.calcomp.pacotocaldata.factory.impl.CalDataModelAdapterFactory;
import com.bosch.calmodel.caldata.CalData;
import com.bosch.calmodel.caldata.history.CalDataHistory;
import com.bosch.calmodel.caldata.history.HistoryEntry;
import com.bosch.calmodel.caldataphy.AtomicValuePhy;
import com.bosch.calmodel.caldataphy.CalDataAxis;
import com.bosch.calmodel.caldataphy.CalDataPhy;
import com.bosch.calmodel.caldataphy.CalDataPhyAscii;
import com.bosch.calmodel.caldataphy.CalDataPhyAxisPts;
import com.bosch.calmodel.caldataphy.CalDataPhyCurve;
import com.bosch.calmodel.caldataphy.CalDataPhyMap;
import com.bosch.calmodel.caldataphy.CalDataPhyValBlk;
import com.bosch.calmodel.caldataphy.CalDataPhyValue;

/**
 * @author dec1kor
 *
 *         <pre>
 * Version 	Date			Modified by			Changes
 * ----------------------------------------------------------------------------
 * 0.1		29-Jan-2010		Deepa				First draft<br>
 * 0.2		25-Oct-2010		Jagan		        PACP-2,6; Added function version <br>
 * 0.3		12-Oct-2011		Dikshita		    PACP-17; Created a new method displayPhysicalValBlk()
 * 												and modified checkLabelValueBlock(). <br>
 *         </pre>
 */
/**
 * Test class to parse the PaCo file and populates the CalData model.
 * 
 * @author dec1kor
 */
public class TestPacoParserWithCalData {

  private static ILoggerAdapter logger = new Log4JLoggerAdapterImpl(LogManager.getLogger("PacoParser"));


  /**
   * Main class to invoke.
   *
   * @param args
   */
  public static void main(final String[] args) {

    Scanner reader = new Scanner(System.in); // Reading from System.in
    logger.info("Enter a Paco File Path: ");
    String n = reader.nextLine();
    // once finished
    reader.close();
    try {
      TestPacoParserWithCalData obj = new TestPacoParserWithCalData();

      obj.parsePaCo(n);

    }
    catch (Exception e) {
      logger.error("Error while parsing the CDF file: " + e.getMessage(), e);
    }
  }

  /**
   * @param obj
   * @throws PacoParserException
   */
  private void parsePaCo(final String pacoFilePath) throws PacoParserException {

    PacoParser pacoParser = new PacoParser(logger, pacoFilePath);

    ClassLoader classLoader = CalDataModelAdapterFactory.class.getClassLoader();
    pacoParser.setTargetModelClassName(CalDataModelAdapterFactory.class.getName());
    pacoParser.setTargetModelClassLoader(classLoader);
    logger.info("Invoking paco parser...");
    Map<String, CalData> pacoCalDataObjects;
    pacoCalDataObjects = pacoParser.parse();

    if (pacoCalDataObjects != null) {
      logger.info("no. of labels = " + pacoCalDataObjects.size());
      checkForLabel(pacoCalDataObjects);
    }
  }

  /**
   * Check for label details.
   *
   * @param pacoCalDataObjects
   */
  private void checkForLabel(final Map<String, CalData> pacoCalDataObjects) {
    for (CalData caldata : pacoCalDataObjects.values()) {
      if (caldata != null) {
        logger.info("************************");
        logger.info("short name = " + caldata.getShortName());
        logger.info("long name = " + caldata.getLongName());
        logger.info("func name = " + caldata.getFunctionName());
        logger.info("func Version = " + caldata.getFunctionVersion()); // PACP-2,6
        CalDataPhy caldataPhy = caldata.getCalDataPhy();
        logger.info("caldataphy name = " + caldataPhy.getName());
        logger.info("caldataphy type = " + caldataPhy.getType());
        logger.info("caldataphy unit = " + caldataPhy.getUnit());
        logger.info("caldataphy checksum = " + caldataPhy.getChecksum());

        checkLabelValue(caldataPhy);
        checkLabelValueBlock(caldataPhy);
        checkLabelAscii(caldataPhy);
        checkLabelCurve(caldataPhy);
        checkLabelAxisPts(caldataPhy);
        checkLabelMap(caldataPhy);

        checkLabelHistory(caldata);
      }
    }
  }

  /**
   * Check for the label of type map.
   *
   * @param caldataPhy
   */
  private void checkLabelMap(final CalDataPhy caldataPhy) {
    if (caldataPhy instanceof CalDataPhyMap) {
      CalDataPhyMap value = (CalDataPhyMap) caldataPhy;

      CalDataAxis xAxis = value.getCalDataAxisX();
      displayXAxisValues(xAxis);

      CalDataAxis yAxis = value.getCalDataAxisY();
      AtomicValuePhy[] atYPhy = yAxis.getAtomicValuePhy();
      for (AtomicValuePhy phy : atYPhy) {
        logger.info("y axis = " + phy.getSValue());
      }
      logger.info("max y-axis value = " + yAxis.getMaxAxisValue().getSValue());
      logger.info("min y-axis value = " + yAxis.getMinAxisValue().getSValue());
      logger.info("y-axis no. of pts = " + yAxis.getNoOfAxisPts());
      logger.info("y-axis unit = " + yAxis.getUnit());

      AtomicValuePhy[][] atValPhy = value.getAtomicValuePhy();
      for (int i = 0; i < xAxis.getNoOfAxisPts(); i++) {
        for (int j = 0; j < yAxis.getNoOfAxisPts(); j++) {
          AtomicValuePhy phy = atValPhy[j][i];
          logger.info("z value phy = " + phy.getSValue());
        }
      }
      logger.info("map max x-axis value = " + value.getMaxXAxisValue().getSValue());
      logger.info("map min x-axis value = " + value.getMinXAxisValue().getSValue());
      logger.info("map x-axis no. of pts = " + value.getNoOfXAxisPts());
      logger.info("map x-axis unit = " + value.getXAxisUnit());
      logger.info("map y-axis no. of pts = " + value.getNoOfYAxisPts());
      logger.info("map y-axis unit = " + value.getYAxisUnit());
      logger.info("map y-axis max value = " + value.getMaxYAxisValue().getSValue());
      logger.info("map y-axis min value = " + value.getMinYAxisValue().getSValue());
      AtomicValuePhy[] xValPhy = value.getXAxisPts();
      displayPhysicalValues(xValPhy);
      AtomicValuePhy[] yValPhy = value.getYAxisPts();
      displayPhysicalValues(yValPhy);
    }
  }

  /**
   * Displays the x-axis values of label of type curve, map and axis points.
   *
   * @param xAxis
   */
  private void displayXAxisValues(final CalDataAxis xAxis) {
    AtomicValuePhy[] atXPhy = xAxis.getAtomicValuePhy();
    for (AtomicValuePhy phy : atXPhy) {
      logger.info("x axis = " + phy.getSValue());
    }
    logger.info("max x-axis value = " + xAxis.getMaxAxisValue().getSValue());
    logger.info("min x-axis value = " + xAxis.getMinAxisValue().getSValue());
    logger.info("x-axis no. of pts = " + xAxis.getNoOfAxisPts());
    logger.info("x-axis unit = " + xAxis.getUnit());
  }

  /**
   * Check for the label of type axis points.
   *
   * @param caldataPhy
   */
  private void checkLabelAxisPts(final CalDataPhy caldataPhy) {
    if (caldataPhy instanceof CalDataPhyAxisPts) {
      CalDataPhyAxisPts value = (CalDataPhyAxisPts) caldataPhy;

      CalDataAxis axis = value.getCalDataAxis();
      displayXAxisValues(axis);

      AtomicValuePhy[] atValPhy = value.getAtomicValuePhy();
      displayPhysicalValues(atValPhy);
      logger.info("axis pts no. of pts = " + value.getNoOfAxisPts());

    }
  }

  /**
   * Displays the physical values of a label.
   *
   * @param atValPhy
   */
  private void displayPhysicalValues(final AtomicValuePhy[] atValPhy) {
    for (AtomicValuePhy phy : atValPhy) {
      logger.info("value phy = " + phy.getSValue());
    }
  }

  /**
   * Displays the physical values of a Value Block.
   *
   * @param atValPhy
   */// PACP-17 - Changed to 3D array
  private void displayPhysicalValBlk(final AtomicValuePhy[][][] atValPhy) {
    for (AtomicValuePhy[][] phy : atValPhy) {
      for (AtomicValuePhy[] atomicValuePhies : phy) {
        for (AtomicValuePhy atomicValuePhy : atomicValuePhies) {
          logger.info("value phy = " + atomicValuePhy.getSValue());
        }
      }
    }
  }

  /**
   * Check for the label of type curve.
   *
   * @param caldataPhy
   */
  private void checkLabelCurve(final CalDataPhy caldataPhy) {
    if (caldataPhy instanceof CalDataPhyCurve) {
      CalDataPhyCurve value = (CalDataPhyCurve) caldataPhy;

      CalDataAxis axis = value.getCalDataAxisX();
      displayXAxisValues(axis);

      AtomicValuePhy[] atValPhy = value.getAtomicValuePhy();
      displayPhysicalValues(atValPhy);
      logger.info("curve max x-axis value = " + value.getMaxXAxisValue().getSValue());
      logger.info("curve min x-axis value = " + value.getMinXAxisValue().getSValue());
      logger.info("curve x-axis no. of pts = " + value.getNoOfXAxisPts());
      logger.info("curve x-axis unit = " + value.getXAxisUnit());
    }
  }

  /**
   * Check for the label of type ascii.
   *
   * @param caldataPhy
   */
  private void checkLabelAscii(final CalDataPhy caldataPhy) {
    if (caldataPhy instanceof CalDataPhyAscii) {
      CalDataPhyAscii value = (CalDataPhyAscii) caldataPhy;
      AtomicValuePhy[] atValPhy = value.getAtomicValuePhy();
      displayPhysicalValues(atValPhy);
    }
  }

  /**
   * Check for the label of type value block.
   *
   * @param caldataPhy
   */
  private void checkLabelValueBlock(final CalDataPhy caldataPhy) {
    if (caldataPhy instanceof CalDataPhyValBlk) {
      CalDataPhyValBlk value = (CalDataPhyValBlk) caldataPhy;
      // PACP-17 - Changed to 3D array
      AtomicValuePhy[][][] atValPhy = value.getAtomicValuePhy();
      displayPhysicalValBlk(atValPhy);
    }
  }

  /**
   * Check for the label of type value.
   *
   * @param caldataPhy
   */
  private void checkLabelValue(final CalDataPhy caldataPhy) {
    if (caldataPhy instanceof CalDataPhyValue) {
      CalDataPhyValue value = (CalDataPhyValue) caldataPhy;
      AtomicValuePhy atValPhy = value.getAtomicValuePhy();
      logger.info("atomic value phy = " + atValPhy.getSValue());
    }
  }

  /**
   * Checks for the label history.
   *
   * @param caldata
   */
  private void checkLabelHistory(final CalData caldata) {
    CalDataHistory history = caldata.getCalDataHistory();
    if (history != null) {
      List<HistoryEntry> list = history.getHistoryEntryList();
      if (list != null) {
        for (HistoryEntry entry : list) {
          if (entry.getContext() != null) {
            logger.info("context = " + entry.getContext().getValue());
          }
          if (entry.getPerformedBy() != null) {
            logger.info("performed by = " + entry.getPerformedBy().getValue());
          }
          if (entry.getDataIdentifier() != null) {
            logger.info("Data Id = " + entry.getDataIdentifier().getValue());
          }
          if (entry.getProject() != null) {
            logger.info("project = " + entry.getProject().getValue());
          }
          if (entry.getProgramIdentifier() != null) {
            logger.info("program Id = " + entry.getProgramIdentifier().getValue());
          }
          if (entry.getTargetVariant() != null) {
            logger.info("target variant = " + entry.getTargetVariant().getValue());
          }
          if (entry.getRemark() != null) {
            logger.info("Remark = " + entry.getRemark().getValue());
          }
          if (entry.getDate() != null) {
            logger.info("Date = " + entry.getDate().getValue());
          }
        }
      }
    }
  }

}
