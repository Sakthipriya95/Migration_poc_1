/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.bosch.caltool.icdm.common.exception.InvalidInputException;
import com.bosch.caltool.icdm.common.util.ZipUtils;

/**
 * @author rgo7cob
 */
public class HexFileHandler {

  private HexFileHandler() {
    // Private constructor
  }

  /**
   * @param hexFileInputStreams Input hes Streams
   * @return un zipped hex FileInputStreams
   * @throws InvalidInputException if invalid input stream found
   */
  public static Map<String, InputStream> unZipIfZippedStream(final Map<String, InputStream> hexFileInputStreams)
      throws InvalidInputException {
    Map<String, InputStream> returnUnzippedStream = new HashMap<>();
    for (Entry<String, InputStream> entry : hexFileInputStreams.entrySet()) {
      try {
        Map<String, InputStream> unzippedStreamMap = ZipUtils.unzipIfZipped(entry.getValue(), entry.getKey());
        for (Entry<String, InputStream> unzippedStreamEntry : unzippedStreamMap.entrySet()) {
          populateHexStream(returnUnzippedStream, unzippedStreamEntry);
        }

      }
      catch (InvalidInputException exp) {
        throw new InvalidInputException("HEX.PARSE_ERROR", exp);
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
