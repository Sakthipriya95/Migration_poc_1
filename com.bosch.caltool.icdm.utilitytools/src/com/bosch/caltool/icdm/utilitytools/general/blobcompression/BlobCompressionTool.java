/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.utilitytools.general.blobcompression;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.icdm.utilitytools.general.blobcompression.exception.BlobCompressionException;
import com.bosch.caltool.icdm.utilitytools.util.ToolLogger;

/**
 * @author bne4cob
 */
public class BlobCompressionTool {


  private static final int APP_ARG_MIN = 6;

  /**
   * Application argument index - User Name
   */
  private static final int APRGINDX_USER = 0;
  /**
   * Application argument index - Password
   */
  private static final int APRGINDX_PASSWORD = 1;
  /**
   * Application argument index - Server
   */
  private static final int APRGINDX_SERVER = 2;
  /**
   * Application argument index - Port
   */
  private static final int APRGINDX_PORT = 3;
  /**
   * Application argument index - DB Instance
   */
  private static final int APRGINDX_DB_INSTANCE = 4;


  private BlobCompressionConfig config;

  private String connectionKey;

  /**
   * Main Method
   *
   * @param args command line arguments
   * @throws BlobCompressionException any error while running the tool
   */
  public static void main(final String... args) throws BlobCompressionException {

    // Logger creation
    ToolLogger.createLogger();

    getLogger().info("Tool execution started");

    BlobCompressionTool tool = new BlobCompressionTool();
    tool.run(args);

    getLogger().info("Tool execution finished");
  }

  /**
   * Run the ProjectConsistencyEvaluation with the user inputs
   *
   * @param args tool arguments
   * @throws BlobCompressionException
   */
  private void run(final String... args) throws BlobCompressionException {

    if (args.length < APP_ARG_MIN) {
      showUsage();
      return;
    }

    String[] loginCred = new String[5];
    System.arraycopy(args, 0, loginCred, 0, 5);

    String temp = args[5];
    int tabColArgPos = loginCred.length;
    int rowsToProcess = 0;// 0 = Consider all rows
    try {
      rowsToProcess = Integer.parseInt(temp);
      if (args.length == APP_ARG_MIN) {
        showUsage();
        return;
      }
      tabColArgPos++;
    }
    catch (NumberFormatException exp) {
      // Nothing to do
    }

    String[] toolArgsCols = new String[args.length - tabColArgPos];
    System.arraycopy(args, tabColArgPos, toolArgsCols, 0, (args.length - tabColArgPos));


    ConcurrentMap<String, Set<String>> colMap = consolidateColumns(toolArgsCols);
    getLogger().debug("column inputs : {}", colMap);
    if (colMap == null) {
      showUsage();
      return;
    }

    try (Connection connection = createConnection(loginCred)) {

      this.config = new BlobCompressionConfig(connection, this.connectionKey, rowsToProcess);
      this.config.initialize();

      for (Entry<String, Set<String>> entry : colMap.entrySet()) {
        TableBlobCompression compressor = new TableBlobCompression(this.config, entry.getKey(), entry.getValue());
        compressor.execute();
      }
    }
    catch (SQLException exp) {
      showUsage();
      getLogger().error(exp.getMessage(), exp);
    }
    catch (ClassNotFoundException exp) {
      showUsage();
      getLogger().error(exp.getMessage(), exp);
    }
    finally {
      this.config.save();
    }
  }


  /**
   * @return
   */
  private ConcurrentMap<String, Set<String>> consolidateColumns(final String... toolArgsCols) {
    ConcurrentMap<String, Set<String>> colMap = new ConcurrentHashMap<>();
    for (String toolArgCol : toolArgsCols) {
      String arr[] = toolArgCol.split("\\.");
      if (arr.length != 2) {
        return null;
        // Input is invalid
      }
      String tabName = arr[0];
      String colName = arr[1];

      if ((tabName == null) || "".equals(tabName.trim()) || (colName == null) || "".equals(colName.trim())) {
        getLogger().error("Invalid column input" + toolArgCol);
        return null;
      }

      tabName = tabName.trim();
      colName = colName.trim();

      Set<String> tabColSet = colMap.get(tabName);
      if (tabColSet == null) {
        tabColSet = new HashSet<>();
        colMap.put(tabName, tabColSet);
      }
      tabColSet.add(colName);
    }

    return colMap;
  }

  /**
   * @param loginCred
   * @return
   * @throws ClassNotFoundException
   * @throws SQLException
   */
  private Connection createConnection(final String[] loginCred) throws ClassNotFoundException, SQLException {

    final String user = loginCred[APRGINDX_USER];
    final String password = loginCred[APRGINDX_PASSWORD];
    final String server = loginCred[APRGINDX_SERVER];
    final String port = loginCred[APRGINDX_PORT];
    final String dbinstance = loginCred[APRGINDX_DB_INSTANCE];

    this.connectionKey = server + ":" + port + ":" + dbinstance + ":" + user;

    getLogger().debug("connecting database to {}:{}:{} as {}", server, port, dbinstance, user);

    Class.forName("oracle.jdbc.driver.OracleDriver");
    Connection connection =
        DriverManager.getConnection("jdbc:oracle:thin:@" + server + ":" + port + ":" + dbinstance, user, password);
    connection.setAutoCommit(false);

    getLogger().debug("Database connection successful");

    return connection;
  }

  /**
   * Show usage of the tool, in case of error inputs
   */
  private void showUsage() {
    getLogger().error("Error : Invalid arguments");
    getLogger().error("Usage : ");
    getLogger().error("    java " + BlobCompressionTool.class.getSimpleName() +
        " <user> <password> <server> <port> <sid> <rows to process> <table1>.<column1> <table1>.<column2> <table2>.<column1> ...");
    getLogger().error("      OR");
    getLogger().error("    java " + BlobCompressionTool.class.getSimpleName() +
        " <user> <password> <server> <port> <sid> <table1>.<column1> <table1>.<column2> <table2>.<column1> ...");
  }

  private static ILoggerAdapter getLogger() {
    return ToolLogger.getLogger();
  }

}
