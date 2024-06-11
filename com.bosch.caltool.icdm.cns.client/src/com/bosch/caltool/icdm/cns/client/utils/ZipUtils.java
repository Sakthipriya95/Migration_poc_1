/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.cns.client.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import com.bosch.caltool.icdm.cns.client.CnsClientConfiguration;


/**
 * @author bne4cob
 */
// ICDM-2069
public final class ZipUtils {

  /**
   * create initial byte array size for unziping the input files
   */
  private static final int CREATE_UNZIP_BYTE_ARRAY_SIZE = 4096;

  /**
   * create initial byte buffer size for unziping the input files
   */
  private static final int CREATE_UNZIP_BYTE_BUFFER_SIZE = 4096;

  /**
   * create initial byte buffer size for ziping the input files
   */
  private static final int CREATE_ZIP_BYTE_BUFFER_SIZE = 1024;

  /**
   * Identifier of a zipped file.
   */
  private static final int[] ZIP_MAGIC_KEY = new int[] { 80, 75, 3, 4 };


  /**
   * Private constructor for utility class
   */
  private ZipUtils() {
    // Do nothing
  }


  /**
   * Zips the input files. The files should be given as a map, with key being the file name and value being the file as
   * byte array. The file name can be a simple name or the relative name, if the files are to be arranged in a directory
   * structure.
   * <p>
   * Note : File name should not be an absolute path.
   * <p>
   *
   * @param unzippedData unzipped data
   * @return the zipped file as a byte array
   * @throws IOException for any exceptions during zipping
   */
  public static byte[] zip(final byte[] unzippedData) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try (ZipOutputStream out = new ZipOutputStream(baos)) {
      addToZip(out, unzippedData);
    }
    return baos.toByteArray();
  }

  private static void addToZip(final ZipOutputStream out, final byte[] unzippedData) throws IOException {
    try (ByteArrayInputStream bais = new ByteArrayInputStream(unzippedData)) {
      // name the file inside the zip file
      out.putNextEntry(new ZipEntry("data"));

      // buffer size
      byte[] byteBuff = new byte[CREATE_ZIP_BYTE_BUFFER_SIZE];
      int count;
      while ((count = bais.read(byteBuff)) > 0) {
        out.write(byteBuff, 0, count);
      }
    }
  }


  /**
   * unzip the input byte arra
   *
   * @param input zipped file as byte array
   * @return the unzipped files as a map, with key as the file name including the relative path, value as the file as
   *         byte array
   */
  public static byte[] unzip(final byte[] input) {
    byte[] ret = null;
    try {
      ret = isZippedData(input) ? unzip2(input).values().iterator().next().getKey() : input;
    }
    catch (IOException e) {
      CnsClientConfiguration.getDefaultConfig().getLogger().error("Error while unzipping data : " + e.getMessage(), e);
    }
    return ret;
  }

  /**
   * Unzips the files stored in the zipped file. The method returns the unzipped files as a map, with key as the file
   * name including the relative path, value as entry with key as file byte array, value zip entry
   * <p>
   * For saving the files, the absolute path should be defined by appending the relative path to a base directory.
   * <p>
   * The method <code>CommonUtils.createFile()</code> can be used for each entries in the map to create the files.
   *
   * @param zippedFile zipped file as byte array
   * @return the unzipped files as a map, with key as the file name including the relative path, value as entry with key
   *         as file byte array, value zip entry
   * @throws IOException for any exceptions during unzipping
   */
  public static Map<String, Entry<byte[], ZipEntry>> unzip2(final byte[] zippedFile) throws IOException {
    ConcurrentMap<String, Entry<byte[], ZipEntry>> retMap = null;

    try (ByteArrayInputStream bais = new ByteArrayInputStream(zippedFile);
        ZipInputStream zipStream = new ZipInputStream(bais);) {

      retMap = doUnzip(zipStream);
      zipStream.closeEntry();
    }
    return retMap;
  }

  /**
   * @param retMap
   * @param zipStream
   * @return
   * @throws IOException
   */
  private static ConcurrentMap<String, Entry<byte[], ZipEntry>> doUnzip(final ZipInputStream zipStream)
      throws IOException {

    ConcurrentMap<String, Entry<byte[], ZipEntry>> retMap = new ConcurrentHashMap<>();

    byte[] byteBuff;
    int bytesRead;
    ZipEntry entry = zipStream.getNextEntry();

    while (entry != null) {

      try (ByteArrayOutputStream baos = new ByteArrayOutputStream(CREATE_UNZIP_BYTE_ARRAY_SIZE)) {

        byteBuff = new byte[CREATE_UNZIP_BYTE_BUFFER_SIZE];
        bytesRead = 0;
        while ((bytesRead = zipStream.read(byteBuff)) != -1) {
          baos.write(byteBuff, 0, bytesRead);
        }

        baos.flush();
        String name = entry.getName();
        SimpleEntry<byte[], ZipEntry> kvp = new SimpleEntry<>(baos.toByteArray(), entry);
        retMap.put(name, kvp);
      }

      entry = zipStream.getNextEntry();
    }
    return retMap;
  }

  /**
   * Checks whether the input data is in zipped format, based on pzip mode of compression
   *
   * @param data data to check
   * @return true, if the input is compressed
   */
  // ICDM-2069
  public static boolean isZippedData(final byte[] data) {
    boolean compressed = true;
    // Data is not relevant for compression check
    if ((data == null) || (data.length < ZIP_MAGIC_KEY.length)) {
      compressed = false;
    }
    else {
      // Checks whether the data begins with the magic key
      for (int pos = 0; pos < ZIP_MAGIC_KEY.length; pos++) {
        if (ZIP_MAGIC_KEY[pos] != data[pos]) {
          // File is not zipped
          compressed = false;
          break;
        }
      }
    }
    return compressed;
  }

}
