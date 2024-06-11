/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.jpa.bo;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.bosch.calcomp.caldatafromhex.CalDataPhyFactory;
import com.bosch.calmodel.a2ldata.A2LFileInfo;
import com.bosch.calmodel.caldata.CalData;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.exception.InvalidInputException;
import com.bosch.caltool.icdm.common.util.ZipUtils;
import com.bosch.caltool.icdm.logger.ParserLogger;

/**
 * @author rgo7cob
 */
public class HexFileHandler {


  private final A2LFileInfo a2lFileInfo;
  private final ParserLogger parserLogger;

  /**
   * @param a2lFileInfo Parse A2l file get the Cal data objects
   */
  public HexFileHandler(final A2LFileInfo a2lFileInfo) {
    this.a2lFileInfo = a2lFileInfo;
    this.parserLogger = ParserLogger.getInstance();
  }

  /**
   * @param hexFileStream hexFileStream
   * @param hexFileType hexFileType
   * @return the map of the cal data Objects
   * @throws IcdmException if invalid input given
   */
  public ConcurrentMap<String, CalData> parseHexFile(final InputStream hexFileStream, final String hexFileType)
      throws IcdmException {
    ConcurrentMap<String, CalData> calDataObjsMap;

    // construct the cal dat phy object
    CalDataPhyFactory calDataFactory = new CalDataPhyFactory(getLogger());
    calDataFactory.setInputStream(hexFileStream);
    calDataFactory.sethexFileType(hexFileType);

    // null has been passed for hex file path, since we have already given stream as input
    try {
      calDataObjsMap = new ConcurrentHashMap<>(calDataFactory.getCalData(this.a2lFileInfo, null));
    }
    catch (Exception exp) {
      throw new InvalidInputException("HEX file Read Error : " + exp.getMessage(), exp);
    }
    getLogger().debug(calDataObjsMap.size() + " characteristics found in hex file");
    return calDataObjsMap;
  }

  private ParserLogger getLogger() {
    return this.parserLogger;
  }

  /**
   * @param hexFileInputStreams Input hes Streams
   * @return un zipped hex FileInputStreams
   * @throws InvalidInputException if invalid input stream found
   */
  public static Map<String, InputStream> unZipIfZippedStream(final Map<String, InputStream> hexFileInputStreams)
      throws InvalidInputException {
    Map<String, InputStream> returnUnzippedStream = new HashMap<String, InputStream>();
    for (Entry<String, InputStream> entry : hexFileInputStreams.entrySet()) {
      try {
        Map<String, InputStream> unzippedStreamMap = ZipUtils.unzipIfZipped(entry.getValue(), entry.getKey());
        for (Entry<String, InputStream> unzippedStreamEntry : unzippedStreamMap.entrySet()) {
          populateHexStream(returnUnzippedStream, unzippedStreamEntry);
        }

      }
      catch (InvalidInputException exp) {
        throw new InvalidInputException("Error in validating HEX File", exp);
      }
    }
    return returnUnzippedStream;

  }

  /**
   * @param returnUnzippedStream
   * @param unzippedStreamEntry
   */
  private static void populateHexStream(final Map<String, InputStream> returnUnzippedStream,
      final Entry<String, InputStream> unzippedStreamEntry) {
    String hexName = unzippedStreamEntry.getKey();
    hexName = hexName.substring(hexName.indexOf('/') + 1);
    if (hexName.length() > 0) {
      returnUnzippedStream.put(hexName, unzippedStreamEntry.getValue());
    }
  }
}
