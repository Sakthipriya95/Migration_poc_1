/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.boot.ssd.api.client.test;

import org.junit.Assert;
import org.junit.Test;

import com.bosch.calcomp.ssd.api.service.client.FileServiceClient;



/**
 * @author TAB1JA
 *
 */
public class FileServiceClientTest {

  
  
  /**
   * test method for file download service
   */
  @Test
  public void testGetFile() {
    
    FileServiceClient client = new FileServiceClient();
    
    boolean isCopied=client.getFileIfDifferent("ssd", "oss", "OSS_Latest.pdf",
        System.getenv("APPDATA").concat("\\SSD\\").concat("OSS_Latest.pdf"));
    
    Assert.assertTrue(isCopied);
  }
}
