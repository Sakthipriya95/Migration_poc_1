/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.utilitytools.general.blobcompression;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.icdm.utilitytools.general.blobcompression.exception.BlobCompressionException;
import com.bosch.caltool.icdm.utilitytools.util.ToolLogger;
import com.bosch.caltool.icdm.utilitytools.util.UtilMethods;

/**
 * @author bne4cob
 */
class TableBlobCompression {


  /**
   * create initial byte buffer size for ziping the input files
   */
  private static final int CREATE_ZIP_BYTE_BUFFER_SIZE = 1024;


  private static final int PSTMT_BATCH_SIZE = 10;


  private final String tableName;
  private final Set<String> columnSet;
  private final Connection conn;

  private final BlobCompressionConfig config;

  private final int rowsToProcess;


  public TableBlobCompression(final BlobCompressionConfig config, final String tableName, final Set<String> columnSet) {
    this.config = config;
    this.conn = config.getConn();
    this.rowsToProcess = config.getRowsToProcess();

    this.tableName = tableName;
    this.columnSet = columnSet;

  }

  public void execute() throws BlobCompressionException {
    getLogger().info("Processing table " + this.tableName);

    this.config.load(this.tableName, this.columnSet);

    compress();

    this.config.clear();

    getLogger().info("Processing table " + this.tableName + " completed");
  }

  /**
   * @throws BlobCompressionException
   */
  private void compress() throws BlobCompressionException {

    String sqlRecords = createRowToUpdateQerySQL();
    String countQuery = createCountQuery(sqlRecords);
    String updSql = createUpdSQL();

    try (Statement stmtCount = this.conn.createStatement();
        ResultSet rsCount = stmtCount.executeQuery(countQuery);
        Statement stmt = this.conn.createStatement();
        ResultSet rs = stmt.executeQuery(sqlRecords);
        PreparedStatement pStmt = this.conn.prepareStatement(updSql);) {

      Blob colBlobOld;
      Blob colBlobNew;
      String rowID;
      int pStmtIdx = 1;
      int totRows = 0;
      int percentCounter = 0;


      if (rsCount.next()) {
        totRows = rsCount.getInt("row_count");
        getLogger().debug("Total rows to update = {}", totRows);
      }
      int rowInterval = totRows / 100;
      int percentCompl = 0;
      int pstmtBatchCounter = 0;
      Set<String> batchRowIDSet = new HashSet<String>();
      while (rs.next()) {
        rowID = rs.getString("row_id");
        getLogger().debug("Processing Key {}", rowID);
        boolean compressed = false;
        for (String col : this.columnSet) {
          colBlobOld = rs.getBlob(col);
          colBlobNew = compressBlob(colBlobOld);
          if (!UtilMethods.isEqual(colBlobOld, colBlobNew)) {
            compressed = true;
          }
          pStmt.setBlob(pStmtIdx, colBlobNew);
          pStmtIdx++;
        }
        pStmt.setString(pStmtIdx, rowID);
        if (compressed) {
          pStmt.addBatch();
          pstmtBatchCounter++;
          batchRowIDSet.add(rowID);
        }
        else {
          this.config.addRowId(this.tableName, this.columnSet, rowID);
        }

        pStmtIdx = 1;

        if (pstmtBatchCounter >= PSTMT_BATCH_SIZE) {
          pstmtBatchCounter = 0;
          commitBatch(pStmt, batchRowIDSet);
          batchRowIDSet.clear();
        }

        percentCounter++;
        if (percentCounter >= rowInterval) {
          percentCounter = 0;
          percentCompl++;
          getLogger().debug("Completed percentage = {}%", percentCompl);
        }

      }

      commitBatch(pStmt, batchRowIDSet);

    }
    catch (SQLException e) {
      try {
        this.conn.rollback();
      }
      catch (SQLException e1) {
        // TODO Auto-generated catch block
        e1.printStackTrace();
      }
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    catch (IOException e) {
      try {
        this.conn.rollback();
      }
      catch (SQLException e1) {
        // TODO Auto-generated catch block
        e1.printStackTrace();
      }
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

  private void commitBatch(final PreparedStatement pStmt, final Set<String> batchRowIDSet) throws SQLException {
    pStmt.executeBatch();
    this.conn.commit();
    getLogger().debug("Batch commited");
    this.config.addRowIds(this.tableName, this.columnSet, batchRowIDSet);
    getLogger().debug("----------");
  }


  /**
   * @param colBlobOld
   * @return
   * @throws SQLException
   * @throws IOException
   */
  private Blob compressBlob(final Blob colBlobOld) throws SQLException, IOException {
    if (colBlobOld == null) {
      return null;
    }
    byte[] unzippedData = colBlobOld.getBytes(1L, (int) colBlobOld.length());
    byte[] zippedData = createZip(unzippedData);
    Blob blobRet;
    if (UtilMethods.isEqual(unzippedData, zippedData)) {
      blobRet = colBlobOld;
    }
    else {
      blobRet = this.conn.createBlob();
      blobRet.setBytes(1L, zippedData);
    }
    return blobRet;
  }

  private String createCountQuery(final String rowSelectQuery) {
    return "Select count(1) as row_count from (" + rowSelectQuery + ")";
  }

  private String createRowToUpdateQerySQL() {
    StringBuilder sql = new StringBuilder("Select rowidtochar(t.rowid) as row_id,");
    int idx = 0;
    for (String col : this.columnSet) {
      sql.append(" t.").append(col).append(' ');
      if (idx < (this.columnSet.size() - 1)) {
        sql.append(',');
      }
      idx++;
    }
    sql.append(" from ").append(this.tableName).append(
        " t left outer join GTT_BLOB_UPD_UTIL_ROWS temp on temp.RECORD_ID = rowidtochar(t.rowid) where temp.record_id is null ");

    String sqlStr = sql.toString();
    if (this.rowsToProcess > 0) {
      sqlStr = "select * from (" + sqlStr + ") WHERE rownum <= " + this.rowsToProcess;
    }

    getLogger().debug("Row select SQL : {}", sqlStr);

    return sqlStr;

  }

  private String createUpdSQL() {
    StringBuilder sql = new StringBuilder("update ");

    sql.append(this.tableName).append(" set ");
    int idx = 0;
    for (String col : this.columnSet) {
      sql.append(col).append(" = ? ");
      if (idx < (this.columnSet.size() - 1)) {
        sql.append(',');
      }
      idx++;
    }
    sql.append(" where rowidtochar(rowid) = ? ");

    return sql.toString();

  }

  private ILoggerAdapter getLogger() {
    return ToolLogger.getLogger();
  }

  private byte[] createZip(final byte[] unzippedData) throws IOException {

    if (isAlreadyCompressed(unzippedData)) {
      getLogger().debug("Data already compressed");
      return unzippedData;
    }

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ByteArrayInputStream bais;
    // out put file
    ZipOutputStream out = new ZipOutputStream(baos);
    byte[] byteBuff;
    int count;
    try {
      bais = new ByteArrayInputStream(unzippedData);
      // name the file inside the zip file
      out.putNextEntry(new ZipEntry("D"));

      // buffer size
      byteBuff = new byte[CREATE_ZIP_BYTE_BUFFER_SIZE];

      while ((count = bais.read(byteBuff)) > 0) {
        out.write(byteBuff, 0, count);
      }

    }
    finally {
      out.close();
    }
    return baos.toByteArray();

  }

  private boolean isAlreadyCompressed(final byte[] unzippedData) {
    boolean compressed = false;
    if ((unzippedData != null) && (unzippedData.length >= 4)) {
      if ((unzippedData[0] == 80) && (unzippedData[1] == 75) && (unzippedData[2] == 3) && (unzippedData[3] == 4)) {
        compressed = true;
      }
    }
    return compressed;
  }
}
