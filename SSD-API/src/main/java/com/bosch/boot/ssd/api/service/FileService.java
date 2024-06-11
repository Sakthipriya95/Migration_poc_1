/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.boot.ssd.api.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.bosch.boot.ssd.api.config.ConfigProperties;
import com.bosch.boot.ssd.api.util.SSDAPIUtil;

/**
 * @author GDH9COB
 */
@Service
public class FileService {

  @Autowired
  private ConfigProperties configProperties;

  /**
   * This method is used to contruct the file path based on the parameters by appending with the master directory path
   * 
   * @param fileNameWithExtn File name with extension(pdf, doc, xls, etc.,)
   * @param toolName         Tool name (CheckSSd, SSD, iCDM, etc.,)
   * @param fileCategory     file category like Syntax, user doc, etc.,)
   * @return the file path
   * @throws IOException
   */
  public Resource getResource(final String fileNameWithExtn, final String toolName, final String fileCategory,
      Logger logger) throws IOException {

    String masterDirectory = "D:\\CalTools_Webservice\\file_service"; 
    String fileName = SSDAPIUtil.joinString(File.separator, masterDirectory, toolName, fileCategory, fileNameWithExtn);

    logger.info("file name=" + fileName);
    logger.error("file name=" + fileName);

    if (SSDAPIUtil.isValid(masterDirectory)) {
      return SSDAPIUtil.loadFileAsResource(Paths.get(masterDirectory).resolve(fileName), logger);

    }
    throw new IOException("Master Directory is not available");
  }

  /**
   * Compares the Checksum of file in server and the input checksum value
   * 
   * @param inputCheckSumValue user given checksum value
   * @param fileInServer       file available in server to get checksum
   * @return true if Checksum is same and false if different
   */
  public boolean checkIfIdenticalFiles(long inputCheckSumValue, File fileInServer) {

    // If CheckSum is not provided , return false
    if (inputCheckSumValue == 0) {
      return false;
    }

    // Compare checksum
    try {
      Checksum serverFileCheckSum = FileUtils.checksum(fileInServer, new CRC32());
      long serverFileCheckSumValue = serverFileCheckSum.getValue();
      return (inputCheckSumValue == serverFileCheckSumValue);
    }
    catch (IOException e2) {
      return false;
    }
  }
}
