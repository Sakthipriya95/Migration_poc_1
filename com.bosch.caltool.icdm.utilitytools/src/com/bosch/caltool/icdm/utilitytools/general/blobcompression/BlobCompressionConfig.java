/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.utilitytools.general.blobcompression;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.icdm.utilitytools.general.blobcompression.exception.BlobCompressionException;
import com.bosch.caltool.icdm.utilitytools.util.ToolLogger;

/**
 * @author bne4cob
 */
public class BlobCompressionConfig {

  static final String HISTORY_FILE_NAME = System.getProperty("user.home") + "/BlobCompressionHistory.config";

  private static final int PSTMT_BATCH_SIZE = 20;

  private final ConcurrentMap<String, Set<String>> updatedRowIdMap = new ConcurrentHashMap<>();

  private final Connection conn;

  private final String connectionKey;

  private final int rowsToProcess;

  private BlobCompressionConfigWriter histWriter;

  private Object curTabKey;


  /**
   * @param conn connection
   * @param connectionKey connection key
   * @param rowsToProcess rows To Process
   */
  public BlobCompressionConfig(final Connection conn, final String connectionKey, final int rowsToProcess) {
    this.conn = conn;
    this.connectionKey = connectionKey;
    this.rowsToProcess = rowsToProcess;
  }

  /**
   * @return the rowsToProcess
   */
  public int getRowsToProcess() {
    return this.rowsToProcess;
  }

  /**
   * Initialize the history, by reading the history file
   *
   * @throws BlobCompressionException error while reading history file
   */
  public void initialize() throws BlobCompressionException {
    getLogger().info("Initializing history from : " + HISTORY_FILE_NAME);
    File configFile = new File(HISTORY_FILE_NAME);
    if (!configFile.exists()) {
      initializeHistWriter();
      return;
    }
    getLogger().info("History file found");

    try (FileReader fReader = new FileReader(configFile); BufferedReader bReader = new BufferedReader(fReader)) {
      String line = bReader.readLine();
      Set<String> rowIdSet = null;
      while (line != null) {
        line = line.trim();
        if ("".equals(line)) {
          line = bReader.readLine();
          continue;
        }
        if (line.startsWith("[")) {
          String tableKey = findKeyFromFileLine(line);
          if (tableKey != null) {
            rowIdSet = getRowIDSet(tableKey);
          }
        }
        else {
          if (rowIdSet != null) {
            rowIdSet.add(line);
          }
        }
        line = bReader.readLine();
      }
      getLogger().info("History initialized");

    }
    catch (IOException e) {
      throw new BlobCompressionException("Error while initializing history. " + e.getMessage(), e);
    }
    initializeHistWriter();
  }

  /**
   * @throws BlobCompressionException
   */
  private void initializeHistWriter() throws BlobCompressionException {
    try {
      this.histWriter = BlobCompressionConfigWriter.start();
    }
    catch (IOException e) {
      throw new BlobCompressionException("Error while initializing history writer " + e.getMessage(), e);
    }
  }


  private String findKeyFromFileLine(final String line) {
    String key = null;
    if (line.startsWith("[Table=")) {
      key = line.substring("[Table=".length(), line.indexOf(']'));
    }
    return key;
  }

  /**
   * Reset the history
   */
  public void reset() {
    getLogger().info("Resetting history");
    this.updatedRowIdMap.clear();
  }


  /**
   * Save
   *
   * @throws BlobCompressionException error while writing
   */
  public void save() throws BlobCompressionException {
    try {
      this.histWriter.close();
    }
    catch (IOException e) {
      throw new BlobCompressionException("Error while closing history writer", e);
    }
  }


  /**
   * @param tableName table
   * @param columnSet set of columns being processed
   * @param rowID row ID
   */
  public void addRowId(final String tableName, final Set<String> columnSet, final String rowID) {
    getRowIDSet(tableName, columnSet).add(rowID);
    addTabKeyToHistWriter(tableName, columnSet);
    this.histWriter.add(rowID);
  }

  /**
   * @param tableName table name
   * @param columnSet set of columns
   * @param rowIDSet set of keys
   */
  public void addRowIds(final String tableName, final Set<String> columnSet, final Set<String> rowIDSet) {
    getRowIDSet(tableName, columnSet).addAll(rowIDSet);
    addTabKeyToHistWriter(tableName, columnSet);
    this.histWriter.add(rowIDSet);
  }

  /**
   * @param tableName
   * @param columnSet
   */
  private void addTabKeyToHistWriter(final String tableName, final Set<String> columnSet) {
    String newTabKey = getTableKey(tableName, columnSet);
    if (!newTabKey.equals(this.curTabKey)) {
      this.curTabKey = newTabKey;
      this.histWriter.add("\n" + this.curTabKey);
    }
  }

  /**
   * @param tableKey
   * @return
   */
  private Set<String> getRowIDSet(final String tableKey) {
    Set<String> rowSet = this.updatedRowIdMap.get(tableKey);
    if (rowSet == null) {
      rowSet = new HashSet<>();
      this.updatedRowIdMap.put(tableKey, rowSet);
    }
    return rowSet;
  }

  private Set<String> getRowIDSet(final String tableName, final Set<String> columnSet) {
    String key = getTableKey(tableName, columnSet);
    return getRowIDSet(key);
  }

  /**
   * @param tableName table name
   * @param columnSet set of columns
   * @throws BlobCompressionException error while loading history keys to temporary table
   */
  public void load(final String tableName, final Set<String> columnSet) throws BlobCompressionException {
    String sql = "insert into GTT_BLOB_UPD_UTIL_ROWS values(?)";

    String tableKey = getTableKey(tableName, columnSet);
    getLogger().info("Loading history to database for " + tableKey);

    Set<String> rowIDSet = getRowIDSet(tableKey);

    if (rowIDSet.isEmpty()) {
      getLogger().info("loading history skipped as no records available to load");
      return;
    }
    try (PreparedStatement pstmt = getConn().prepareStatement(sql)) {
      int idx = 0;
      for (String rowID : rowIDSet) {
        pstmt.setString(1, rowID);
        pstmt.addBatch();

        if (idx < PSTMT_BATCH_SIZE) {
          idx++;
        }
        else {
          pstmt.executeBatch();
          idx = 0;
        }
      }
      pstmt.executeBatch();
      getConn().commit();

      getLogger().info("Loading history to database completed");

    }
    catch (SQLException e) {
      throw new BlobCompressionException("Error while loading table history for " + tableKey + ". " + e.getMessage(),
          e);
    }

  }

  /**
   * @throws BlobCompressionException error while clearing temporary table
   */
  public void clear() throws BlobCompressionException {
    getLogger().info("clearing database history of previous table");
    try (Statement stmt = getConn().createStatement()) {
      stmt.executeUpdate("delete from GTT_BLOB_UPD_UTIL_ROWS");
      getLogger().info("clearing database history of previous table completed");
    }
    catch (SQLException e) {
      throw new BlobCompressionException("Error while cleaing database temporary table", e);
    }

  }

  private String getTableKey(final String tableName, final Set<String> columnSet) {
    StringBuilder key = new StringBuilder(tableName);
    for (String col : columnSet) {
      key.append('.').append(col);
    }
    key.append(';').append(this.connectionKey);

    return key.toString();
  }

  private ILoggerAdapter getLogger() {
    return ToolLogger.getLogger();
  }

  /**
   * @return the conn
   */
  public Connection getConn() {
    return this.conn;
  }
}
