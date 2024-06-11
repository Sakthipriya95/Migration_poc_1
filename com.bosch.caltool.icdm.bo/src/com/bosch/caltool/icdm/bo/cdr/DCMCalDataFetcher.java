/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;

import com.bosch.calmodel.caldata.CalData;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.exception.InvalidInputException;
import com.bosch.caltool.icdm.common.util.CaldataFileParserHandler;
import com.bosch.caltool.icdm.common.util.CaldataFileParserHandler.CALDATA_FILE_TYPE;
import com.bosch.caltool.icdm.logger.ParserLogger;

/**
 * @author say8cob
 */
public class DCMCalDataFetcher {

  /**
   * @param filesStreamMap stream map
   * @return Map of CalData. Key is parameter name
   * @throws InvalidInputException when DCM file is invalid or empty
   */
  public Map<String, CalData> fectchCalData(final byte[] filesStreamMap) throws InvalidInputException {
    try {
      InputStream inputStream = new ByteArrayInputStream(filesStreamMap);

      CaldataFileParserHandler parserHandler = new CaldataFileParserHandler(ParserLogger.getInstance(), null);
      Map<String, CalData> calDataMap = parserHandler.getCalDataObjects(CALDATA_FILE_TYPE.DCM, inputStream);

      if (calDataMap.isEmpty()) {
        throw new InvalidInputException(MonicaReviewErrorCode.INVALID_DCM_FILE);
      }
      return calDataMap;
    }
    catch (IcdmException exp) {
      throw new InvalidInputException(exp.getMessage(), exp);
    }
  }
}
