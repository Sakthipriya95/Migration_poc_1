/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.statistics.text;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.icdm.logger.WSLogger;


/**
 * Handles queries, using a Connection of class DatabaseConnection. Provides methods for loading a SQL statement out of
 * external SQL-File, executing this file and retruns the Result Set to the user. The SQL-statement should be stored
 * within this package and can have any filename.<br>
 * <b>The only purpose of this class is executing SQL-Statements for JUnit test.</b><br>
 * <br>
 * <b>Usage notes</b>:
 * <ul>
 * <li>Put the SQL-statements into a file within this package. Filename should be like <code>filename.sql</code></li>
 * <li>Create an instance of this class</li>
 * <li>Use {@link DatabaseQuery#getResultSet(String, int, int, String...) getResultSet(String, int, int, String...)} to
 * return the result of the statement</li>
 * </ul>
 * 
 * @author imi2si
 * @since 1.15
 */
public class TextHandler {

  /**
   * Logger for this class
   */
  private static final ILoggerAdapter LOG = WSLogger.getInstance();

  /**
   * Default constructor which initiallizes the database connection
   */
  public TextHandler() {
    // TO-DO
  }

  /**
   * Reads a textfile that contains a comment, text, etc. out of a text file
   * 
   * @param fileName the filename to load
   * @return a String represenattion of the content of the file
   */
  public final String getText(final String fileName) {

    StringBuffer text = new StringBuffer();
    byte[] byteArray;
    int bytesRead;

    try (InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);)

    {
      while ((bytesRead = stream.read(byteArray = new byte[16384], 0, 16384)) != -1) {
        text.append(new String(Arrays.copyOfRange(byteArray, 0, bytesRead)));
      }

      LOG.debug("Statement loaded: {}", text);
    }
    catch (IOException e) {
      LOG.error("Statement not loadable: " + text, e);
    }

    return text.toString();
  }
}
