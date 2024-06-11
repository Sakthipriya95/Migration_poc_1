/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import com.bosch.caltool.apic.ws.common.WSErrorCodes;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

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
   * Identifier of a zipped file.
   */
  private static final int[] ZIP_MAGIC_KEY = new int[] { 80, 75, 3, 4 };

  private static final int BUFFER_SIZE = 1024;


  /**
   * Private constructor for utility class
   */
  private ZipUtils() {
    // Do nothing
  }

  /**
   * Unzips the files stored in the zipped file. The method returns the unzipped files as a map, with key as the file
   * name including the relative path, value as the file as byte array.
   * <p>
   * For saving the files, the absolute path should be defined by appending the relative path to a base directory.
   * <p>
   * The method <code>CommonUtils.createFile()</code> can be used for each entries in the map to create the files.
   *
   * @param zippedFile zipped file as byte array
   * @return the unzipped files as a map, with key as the file name including the relative path, value as the file as
   *         byte array
   * @throws IOException for any exceptions during unzipping
   */
  public static Map<String, byte[]> unzip(final byte[] zippedFile) throws IOException {
    ConcurrentMap<String, byte[]> retMap = new ConcurrentHashMap<>();
    for (Entry<String, Entry<byte[], ZipEntry>> entry : unzip2(zippedFile).entrySet()) {
      retMap.put(entry.getKey(), entry.getValue().getKey());
    }
    return retMap;
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
  private static boolean isZippedData(final byte[] data) {
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

  /**
   * Gets the unzipped byte array if zipped
   *
   * @param inputByteArray the unknow input byte array
   * @param fileName file name, if input is not zipped
   * @return the unzipped byte array
   * @throws ApicWebServiceException if array cannot be read
   */
  // Task 263282
  public static Map<String, byte[]> unzipIfZipped(final byte[] inputByteArray, final String fileName)
      throws ApicWebServiceException {

    Map<String, byte[]> unzipMap = new HashMap<>();

    if (ZipUtils.isZippedData(inputByteArray)) {

      try {
        unzipMap = ZipUtils.unzip(inputByteArray);
      }
      catch (IOException exp) {
        throw new ApicWebServiceException(WSErrorCodes.CLIENT_ERROR,
            "The given file is Corrupted : " + exp.getMessage(), exp);
      }

    }
    else {
      unzipMap.put(fileName, inputByteArray);

    }
    return unzipMap;
  }

  /**
   * @param inputStream input Stream
   * @param unzippedFileName unzipped file name
   * @return unzipped version of input stream
   * @throws ApicWebServiceException if invalid input stream found
   */
  public static Map<String, InputStream> unzipIfZipped(final InputStream inputStream, final String unzippedFileName)
      throws ApicWebServiceException {
    byte[] byteArray;

    try {
      byteArray = IOUtils.toByteArray(inputStream);
    }
    catch (IOException exp) {
      throw new ApicWebServiceException(WSErrorCodes.CLIENT_ERROR, exp.getMessage(), exp);
    }

    Map<String, InputStream> streamMap = new HashMap<>();
    Map<String, byte[]> unzipIfZipped = unzipIfZipped(byteArray, unzippedFileName);
    for (Entry<String, byte[]> unzipped : unzipIfZipped.entrySet()) {
      streamMap.put(unzipped.getKey(), new ByteArrayInputStream(unzipped.getValue()));
    }

    return streamMap;
  }

  /**
   * Gets the unzipped byte array if zipped, with details of zip contents. If input is not zipped, zip content details
   * will be null
   *
   * @param inputByteArray the unknow input byte array
   * @param fileName file name, if input is not zipped
   * @return the unzipped byte array, with details of zip contents as <code>ZipEntry</code>
   * @throws ApicWebServiceException if array cannot be read
   */
  public static Map<String, Entry<byte[], ZipEntry>> unzipIfZipped2(final byte[] inputByteArray, final String fileName)
      throws ApicWebServiceException {

    Map<String, Entry<byte[], ZipEntry>> unzipMap = new HashMap<>();

    if (ZipUtils.isZippedData(inputByteArray)) {

      try {
        unzipMap = ZipUtils.unzip2(inputByteArray);
      }
      catch (IOException exp) {
        throw new ApicWebServiceException(WSErrorCodes.CLIENT_ERROR, "The given file is Corrupted : ", exp);
      }

    }
    else {
      unzipMap.put(fileName, new SimpleEntry<>(inputByteArray, null));
    }
    return unzipMap;
  }


  /**
   * create a new Zip Fille to the the Zip path
   *
   * @param source source folder location
   * @param zipath zip folder path
   * @throws IOException IOException
   */
  public static void zip(final Path source, final Path zipath) throws IOException {

    try (OutputStream fos = new FileOutputStream(zipath.toFile()); ZipOutputStream zos = new ZipOutputStream(fos);) {

      // recursively navigate through directory and add the file
      Files.walkFileTree(source, new SimpleFileVisitor<Path>() {

        @Override
        public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
          zos.putNextEntry(new ZipEntry(source.relativize(file).toString()));
          Files.copy(file, zos);
          zos.closeEntry();
          return FileVisitResult.CONTINUE;
        }
      });
    }
  }

  /**
   * @param filePath incoming folder path
   * @return zip file path
   * @throws IOException IOException
   */
  private static String getZipFolderPath(final String filePath) throws IOException {
    Path fullPath = Paths.get(System.getProperty("java.io.tmpdir"));
    return fullPath + "\\" + FilenameUtils.getBaseName(filePath) + "_" + FilenameUtils.getExtension(filePath);
  }


  /**
   * @param filePath filePath
   * @return folder Path
   * @throws IOException IOException
   */
  public static String getZipPath(final String filePath) throws IOException {
    return getZipFolderPath(filePath) + ".zip";
  }

  /**
   * @param sourceFile source File
   * @param destinationFile destination File
   * @throws IOException IOException
   */
  public static void zip(final String sourceFile, final String destinationFile) throws IOException {
    File fileToZip = new File(sourceFile);

    try (FileOutputStream fos = new FileOutputStream(destinationFile);
        ZipOutputStream zipOut = new ZipOutputStream(fos);
        FileInputStream fis = new FileInputStream(fileToZip);) {

      ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
      zipOut.putNextEntry(zipEntry);
      final byte[] bytes = new byte[BUFFER_SIZE];
      int length;
      while ((length = fis.read(bytes)) >= 0) {
        zipOut.write(bytes, 0, length);
      }
    }
  }

  /**
   * Unzip.
   *
   * @param zipFilePath zipFilePath
   * @param destDirectory the dest directory
   * @throws IOException IOException
   */
  public static void unzip(final String zipFilePath, final String destDirectory) throws IOException {
    String destDir;
    if (destDirectory == null) {
      destDir = getZipFolderPath(zipFilePath);
    }
    else {
      destDir = destDirectory;
    }
    File dir = new File(destDir + File.separator + FilenameUtils.getBaseName(zipFilePath));
    if (!dir.exists()) {
      dir.mkdirs();
    }

    try (InputStream fis = new FileInputStream(zipFilePath); ZipInputStream zis = new ZipInputStream(fis);) {
      ZipEntry ze = zis.getNextEntry();
      while (ze != null) {
        String fileName = ze.getName();
        createFileFromZip(destDir + File.separator + FilenameUtils.getBaseName(zipFilePath), zis, fileName);
        zis.closeEntry();
        ze = zis.getNextEntry();
      }
      zis.closeEntry();
    }
  }

  /**
   * Creates the file from zip.
   *
   * @param destDir the dest dir
   * @param zis the zis
   * @param fileName the file name
   * @throws IOException Signals that an I/O exception has occurred.
   */
  private static void createFileFromZip(final String destDir, final ZipInputStream zis, final String fileName)
      throws IOException {

    File newFile = new File(destDir + File.separator + fileName);
    new File(newFile.getParent()).mkdirs();
    byte[] buffer = new byte[BUFFER_SIZE];
    try (FileOutputStream fos = new FileOutputStream(newFile)) {
      int len;
      while ((len = zis.read(buffer)) > 0) {
        fos.write(buffer, 0, len);
      }
    }
  }
}
