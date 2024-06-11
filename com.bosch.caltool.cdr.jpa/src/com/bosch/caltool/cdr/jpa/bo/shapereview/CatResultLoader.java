/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.jpa.bo.shapereview;

import java.io.File;

import javax.xml.bind.JAXBException;

import com.bosch.calcomp.adapter.logger.Activator;
import com.bosch.calcomp.catdata.catdatamodel.PVER;
import com.bosch.calcomp.catdataparser.parser.CATDataParser;
import com.bosch.calcomp.catdataparser.parser.CATParserException;
import com.bosch.caltool.icdm.logger.CDMLogger;

/**
 * @author bne4cob
 */
public class CatResultLoader {

  /**
   * Shape review data
   */
  private final ShapeReviewData data;

  /**
   * @param data ShapeReviewData
   */
  public CatResultLoader(final ShapeReviewData data) {
    this.data = data;
  }

  /**
   * Downloads the CAT file from vCDM and converts to java model
   *
   * @param catXmlPath
   * @return
   */
  public PVER fetchCatResults(final String catXmlPath) {

    downloadCatFile();

    PVER catResultPver = parseCatFile(catXmlPath);

    // Delete the temporary file
    File catFile = new File(catXmlPath);
    catFile.delete();
    return catResultPver;
  }

  /**
   * Downloads the CAT file from vCDM
   */
  private void downloadCatFile() {
    // call vCDM getEaseeFile

  }


  /**
   * Converts CAT file to java model
   *
   * @param catXmlPath
   * @return
   */
  private PVER parseCatFile(final String catXmlPath) {
    // Use the new parser to convert CAT file to java model

    // Call the CAT data parser
    CATDataParser catDataParser = new CATDataParser();

    PVER pver = null;
    try {
      pver = catDataParser.parseCATData(catXmlPath);
    }
    catch (JAXBException | CATParserException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), Activator.PLUGIN_ID);
    }

    return pver;
  }
}
