/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.util;


import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.calcomp.cretastructureparser.CretaStructureParser;
import com.bosch.calcomp.fteparser.FteParser;
import com.bosch.calmodel.cretastructuredata.CretaStructureDataModel;
import com.bosch.calmodel.cretastructuredata.ICretaVariant;
import com.bosch.calmodel.ftedata.FteDataModel;
import com.bosch.caltool.icdm.logger.CDMLogger;


/**
 * This class handles the parsing of the creta and fte files with extn .csv and .fte
 *
 * @author dmo5cob
 */
public class CretaFteFilesParserHandler {

  /**
   * Parser Logger
   */
  private final ILoggerAdapter parserLogger;


  /**
   * @param parserLogger logger instance
   */
  public CretaFteFilesParserHandler(final ILoggerAdapter parserLogger) {
    this.parserLogger = parserLogger;


  }

  /**
   * This method parses the input files accordingly and returns a map of caldata objects
   *
   * @param filePath File Path
   * @return Set of variant names
   */
  public SortedSet<String> getVariantNames(final String filePath) {
    SortedSet<String> varNames = new TreeSet<String>();
    String filePathUpper = filePath.toUpperCase(Locale.getDefault());

    if (filePathUpper.endsWith(CommonUtilConstants.CRETA_EXTN)) {
      varNames = invokeCretaParser(filePath);
    }
    else if (filePathUpper.endsWith(CommonUtilConstants.FTE_EXTN)) {
      varNames = invokeFTEParser(filePath);
    }


    return varNames;
  }

  /**
   * @param filePath
   * @return
   */
  private SortedSet<String> invokeFTEParser(final String filePath) {
    SortedSet<String> varNames = new TreeSet<String>();
    try {
      FteParser fte = new FteParser(this.parserLogger, filePath);
      this.parserLogger.info("Parsing FTE file...");
      FteDataModel data = fte.parse();
      this.parserLogger.info("Parsing of FTE file completed.");
      for (Entry<String, List<String>> varInfo : data.getVarSwMap().entrySet()) {
        varNames.add(varInfo.getKey());
        this.parserLogger.info(varInfo.getKey());

      }
    }
    catch (Exception e) {
      CDMLogger.getInstance().errorDialog(
          "An error occured when trying to parse the FTE file : " + filePath +
              ". Please send a mail to the iCDM hotline including the file you wanted to parse and the name of the current PIDC.",
          e, Activator.PLUGIN_ID);
    }
    return varNames;
  }

  /**
   * @param filePath
   * @return
   */
  private SortedSet<String> invokeCretaParser(final String filePath) {

    SortedSet<String> varNames = new TreeSet<String>();
    try {
      HashMap<String, String> parserInitValues = new HashMap<String, String>();
      parserInitValues.put("treeDelimiter", "\\");
      parserInitValues.put("startDelimiter", "(");
      parserInitValues.put("endDelimiter", ")");
      parserInitValues.put("minVarLength", "4");
      parserInitValues.put("maxVarLength", "10");
      parserInitValues.put("regExp", "[_xA-Z0-9\\.]");
      parserInitValues.put("notAllowedChars", "\\;!");

      this.parserLogger.info("Create new CRETA structure parser...");
      CretaStructureParser csp = new CretaStructureParser(this.parserLogger, filePath, parserInitValues);
      this.parserLogger.info("Parsing CRETA file...");
      CretaStructureDataModel data = csp.Parse();
      this.parserLogger.info("Parsing of CRETA file completed.");
      for (Entry<String, ICretaVariant> varInfo : data.getVarCodesMap().entrySet()) {
        varNames.add(varInfo.getKey());
        this.parserLogger.info(varInfo.getKey() + "  -->  " + varInfo.getValue().variantPathInCreta());
      }

    }
    catch (Exception e) {
      CDMLogger.getInstance().errorDialog(
          "An error occured when trying to parse the CRETA file : " + filePath +
              ". Please send a mail to the iCDM hotline including the file you wanted to parse and the name of the current PIDC.",
          e, Activator.PLUGIN_ID);
    }
    return varNames;
  }

}
