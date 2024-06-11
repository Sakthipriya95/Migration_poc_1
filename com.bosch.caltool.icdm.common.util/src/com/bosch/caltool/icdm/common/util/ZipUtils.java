/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.util;

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

import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.exception.InvalidInputException;

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

  private static final int BUFFER_SIZE = 1024;


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
   * The method <code>CommonUtils.getFileAsByteArray()</code> can be used to convert a file to byte array.
   *
   * @param unzippedFileMap map of unzipped files
   * @return the zipped file as a byte array
   * @throws IOException for any exceptions during zipping
   */
  public static byte[] createZip(final Map<String, byte[]> unzippedFileMap) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try (ZipOutputStream out = new ZipOutputStream(baos)) {
      for (Entry<String, byte[]> item : unzippedFileMap.entrySet()) {
        addToZip(out, item);
      }
    }
    return baos.toByteArray();
  }

  private static void addToZip(final ZipOutputStream out, final Entry<String, byte[]> item) throws IOException {
    try (ByteArrayInputStream bais = new ByteArrayInputStream(item.getValue())) {
      // name the file inside the zip file
      out.putNextEntry(new ZipEntry(item.getKey()));

      // buffer size
      byte[] byteBuff = new byte[CREATE_ZIP_BYTE_BUFFER_SIZE];
      int count;
      while ((count = bais.read(byteBuff)) > 0) {
        out.write(byteBuff, 0, count);
      }
    }
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

  /**
   * Gets the unzipped byte array if zipped
   *
   * @param inputByteArray the unknow input byte array
   * @param fileName file name, if input is not zipped
   * @return the unzipped byte array
   * @throws IcdmException if array cannot be read
   */
  // Task 263282
  public static Map<String, byte[]> unzipIfZipped(final byte[] inputByteArray, final String fileName)
      throws IcdmException {

    Map<String, byte[]> unzipMap = new HashMap<>();

    if (ZipUtils.isZippedData(inputByteArray)) {

      try {
        unzipMap = ZipUtils.unzip(inputByteArray);
      }
      catch (IOException exp) {
        throw new IcdmException("The given file is Corrupted : ", exp);
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
   * @throws InvalidInputException if invalid input stream found
   */
  public static Map<String, InputStream> unzipIfZipped(final InputStream inputStream, final String unzippedFileName)
      throws InvalidInputException {
    byte[] byteArray;

    try {
      byteArray = IOUtils.toByteArray(inputStream);
    }
    catch (IOException exp) {
      throw new InvalidInputException(exp.getMessage(), exp);
    }
    Map<String, InputStream> streamMap = new HashMap<>();
    try {
      Map<String, byte[]> unzipIfZipped = ZipUtils.unzipIfZipped(byteArray, unzippedFileName);
      for (Entry<String, byte[]> unzipped : unzipIfZipped.entrySet()) {
        streamMap.put(unzipped.getKey(), new ByteArrayInputStream(unzipped.getValue()));
      }

    }
    catch (IcdmException exp) {
      throw new InvalidInputException(exp.getMessage(), exp);
    }
    return streamMap;
  }


  /**
   * Provide the unzipped version of input stream. If input is not zipped, the same will be given as a Byte Array input
   * stream
   *
   * @param inputStream input Stream
   * @return unzipped version of input stream, as a byte array input stream
   * @throws InvalidInputException if invalid input stream found
   */
  public static InputStream unzipIfZipped(final InputStream inputStream) throws InvalidInputException {
    byte[] byteArray;

    try {
      byteArray = IOUtils.toByteArray(inputStream);
    }
    catch (IOException exp) {
      throw new InvalidInputException(exp.getMessage(), exp);
    }

    try {
      Map<String, byte[]> unzipIfZipped = ZipUtils.unzipIfZipped(byteArray, "DUMMY");
      byte[] unzipped = unzipIfZipped.values().iterator().next();
      return new ByteArrayInputStream(unzipped);
    }
    catch (IcdmException exp) {
      throw new InvalidInputException(exp.getMessage(), exp);
    }

  }

  /**
   * Gets the unzipped byte array if zipped, with details of zip contents. If input is not zipped, zip content details
   * will be null
   *
   * @param inputByteArray the unknow input byte array
   * @param fileName file name, if input is not zipped
   * @return the unzipped byte array, with details of zip contents as <code>ZipEntry</code>
   * @throws IcdmException if array cannot be read
   */
  public static Map<String, Entry<byte[], ZipEntry>> unzipIfZipped2(final byte[] inputByteArray, final String fileName)
      throws IcdmException {

    Map<String, Entry<byte[], ZipEntry>> unzipMap = new HashMap<>();

    if (ZipUtils.isZippedData(inputByteArray)) {

      try {
        unzipMap = ZipUtils.unzip2(inputByteArray);
      }
      catch (IOException exp) {
        throw new IcdmException("The given file is Corrupted : ", exp);
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
  public static String getZipFolderPath(final String filePath) throws IOException {
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
   * @param zipFilePath zipFilePath
   * @param destDirectory output Directory
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
    File dir = new File(destDir);
    if (!dir.exists()) {
      dir.mkdirs();
    }

    try (InputStream fis = new FileInputStream(zipFilePath); ZipInputStream zis = new ZipInputStream(fis);) {
      ZipEntry ze = zis.getNextEntry();
      while (ze != null) {
        String fileName = ze.getName();
        createFileFromZip(destDir, zis, fileName);
        zis.closeEntry();
        ze = zis.getNextEntry();
      }
      zis.closeEntry();
    }

  }


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
