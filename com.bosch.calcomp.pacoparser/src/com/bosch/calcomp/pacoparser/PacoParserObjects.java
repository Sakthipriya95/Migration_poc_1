/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.calcomp.pacoparser;

import com.bosch.calcomp.pacoparser.pacomodelcollection.PacoModelCollection;

/**
 * @author QRK1COB
 *
 */
public class PacoParserObjects {
  
  private final PacoModelCollection pacoModelCollection = new PacoModelCollection();
  
  private  PacoParser pacoParser;
  
  /**
   *
   */
  public PacoParserObjects(final PacoParser pacoParser) {
    this.pacoParser = pacoParser;
  }
  
  /**
   * @return the pacoModelCollection
   */
  public PacoModelCollection getPacoModelCollection() {
    return this.pacoModelCollection;
  }

  
  /**
   * @return the pacoParser
   */
  public PacoParser getPacoParser() {
    return this.pacoParser;
  }


}
