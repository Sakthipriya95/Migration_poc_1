/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.labfunparser.textparser;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

import com.bosch.caltool.labfunparser.exception.ParserException;

/**
 * @author say8cob
 */
public class CharSetIdentifier {

  /**
   * @param fileDataArr as Input
   * @param charSets list for verification
   * @return charsets
   * @throws ParserException as exception
   */
  public Charset charSetIdetifier(final byte[] fileDataArr, final Charset[] charSets) throws ParserException {

    Charset characterSet = null;

    for (Charset charset : charSets) {
      characterSet = identifyCharset(fileDataArr, charset);
      if (characterSet != null) {
        break;
      }
    }

    return characterSet;
  }


  private Charset identifyCharset(final byte[] fileDataArr, final Charset charSet) throws ParserException {
    try (ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(fileDataArr);
        BufferedInputStream bufferedInputStream = new BufferedInputStream(arrayInputStream)) {

      CharsetDecoder decoder = charSet.newDecoder();
      decoder.reset();

      byte[] buffer = new byte[512];
      boolean identified = false;
      while ((bufferedInputStream.read(buffer) != -1) && (!identified)) {
        identified = decoder(buffer, decoder);
        if (!identified) {
          break;
        }
      }
      if (identified) {
        return charSet;
      }
      return null;

    }
    catch (Exception e) {
      throw new ParserException("Error when idetifying CharSet " + e.getLocalizedMessage());
    }
  }

  private boolean decoder(final byte[] fileDataArr, final CharsetDecoder decoder) {
    try {
      decoder.decode(ByteBuffer.wrap(fileDataArr));
    }
    catch (CharacterCodingException e) {
      return false;
    }
    return true;
  }
}
