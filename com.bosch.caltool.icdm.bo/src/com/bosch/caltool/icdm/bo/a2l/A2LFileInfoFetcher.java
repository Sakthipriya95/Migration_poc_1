/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.a2l;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.bosch.calcomp.a2lparser.a2l.A2LParser;
import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.calcomp.parser.a2l.exception.A2lParserException;
import com.bosch.calmodel.a2ldata.A2LFileInfo;
import com.bosch.caltool.apic.vcdminterface.VCDMInterface;
import com.bosch.caltool.icdm.common.exception.InvalidInputException;
import com.bosch.caltool.icdm.logger.A2LLogger;

/**
 * New class to fetch the a2l file info from the input stream and byte array
 *
 * @author rgo7cob
 */
public enum A2LFileInfoFetcher {


                                /**
                                 * Singleton instance.
                                 */
                                INSTANCE;


  /**
   * Gets the a2l file info for the given byte array
   *
   * @param ioStream ioStream
   * @return a2l file info
   * @throws IOException
   */
  private A2LFileInfo getA2lFileInfo(final InputStream ioStream) throws InvalidInputException {
    synchronized (VCDMInterface.A2LSYNC_LOCK) {
      A2LParser a2lParser = null;
      try {
        a2lParser = new A2LParser(A2LLogger.getInstance());
        a2lParser.setFileStream(ioStream);
        a2lParser.parse();
      }
      catch (A2lParserException exp) {
        throw new InvalidInputException("A2L.PARSE_ERROR", exp, exp.getMessage());
      }
      return a2lParser.getA2LFileInfo();
    }
  }

  /**
   * Gets the a2l file info for the given byte array.
   *
   * @param a2lContentArray a2lContentArray
   * @param logger the logger
   * @return a2l file info
   * @throws InvalidInputException InvalidInputException
   */
  public A2LFileInfo getA2lFileInfo(final byte[] a2lContentArray, final ILoggerAdapter logger)
      throws InvalidInputException {
    A2LFileInfo a2lFileInfo = null;
    logger.debug("Fetching of A2l File Info started ...");
    try (InputStream ioStream = new ByteArrayInputStream(a2lContentArray)) {
      a2lFileInfo = getA2lFileInfo(ioStream);
    }
    catch (IOException exp) {
      throw new InvalidInputException("A2L.PARSE_ERROR", exp, exp.getMessage());
    }
    logger.debug("Fetching of A2l File Info completed ...");
    return a2lFileInfo;
  }
}
