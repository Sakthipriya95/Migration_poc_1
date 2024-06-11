/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.utilitytools.general.blobcompression;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

import com.bosch.caltool.icdm.utilitytools.util.ToolLogger;

/**
 * @author bne4cob
 */
public class BlobCompressionConfigWriterTest {


  /**
   * Initialize the logger
   */
  @BeforeClass
  public static void initialize() {
    ToolLogger.createLogger();
  }

  /**
   * @throws IOException any exception
   */
  @Test
  public void test1() throws IOException {
    BlobCompressionConfigWriter bccWriter = BlobCompressionConfigWriter.start();


    for (int i = 0; i < 100; i++) {

      bccWriter.add("hello");
      bccWriter.add("how");
      bccWriter.add("are you");
      bccWriter.add("are you1");
      bccWriter.add("are you1");
      bccWriter.add("are you2");
    }
    bccWriter.close();
  }

}
