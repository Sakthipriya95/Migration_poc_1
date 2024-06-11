package com.bosch.calcomp.adapter.logger.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.calcomp.adapter.logger.JavaLoggerAdapterImpl;

/**
 * REVISION HISTORY<br>
 * Version Date Name Description<br>
 * 0.1 23-Jun-2008 Madhu Samuel K <SAC-82> First Draft <br>
 * 0.2 27-Jun-2008 Parvathy <SAC-109> Removed Console Hanlder from getDefaultJavaLogger<br>
 * to avoid duplicate console logging <br>
 */


/**
 * Utility class for the Logger.
 * <p>
 * Consists of utility methods used by the logger.
 *
 * @author mad4kor
 */
public class LoggerUtil {

  /**
   * Place holder for message parameters
   */
  private static final String MSG_PARAM_PLACE_HOLDER = "{}";
  /**
   * Length of message parameter place holder
   */
  private static final int MSG_PARAM_PLACE_HOLDER_LEN = MSG_PARAM_PLACE_HOLDER.length();

  @Deprecated
  private static ILoggerAdapter logger = null;

  private LoggerUtil() {
    // Private constructor
  }

  /**
   * Gets the default java logger for the specified file location.
   *
   * @param logLocation path
   * @return java logger
   */
  public static Logger getDefaultJavaLogger(final String logLocation) {
    Logger fileJavaLogger = null;
    try {
      fileJavaLogger = Logger.getLogger(ILoggerAdapter.class.getName());
      File log = new File(logLocation);
      if ((log.exists()) && (log.isFile())) {
        log.delete();
      }
      // Create filehandler
      Handler fileHandler = createFileHandler(logLocation);
      fileJavaLogger.addHandler(fileHandler);

      fileJavaLogger.setLevel(Level.INFO);
    }
    catch (Exception excep) {
      excep.printStackTrace();
    }
    return fileJavaLogger;
  }

  /**
   * Create FileHandler for JavaLogger
   *
   * @param fileLocation file path
   * @return Handler
   */
  public static Handler createFileHandler(final String fileLocation) {
    Handler fileHandler = null;

    try {
      fileHandler = new FileHandler(fileLocation);
      fileHandler.setFormatter(new SimpleFormatter());
    }
    catch (SecurityException e) {
      e.printStackTrace();
    }
    catch (IOException e) {
      e.printStackTrace();
    }

    return fileHandler;
  }

  /**
   * Create ConsoleHandler for Java Logger.
   *
   * @return Handler
   */
  public static Handler createConsoleHandler() {
    Handler consoleHandler = new ConsoleHandler();
    consoleHandler.setFormatter(new SimpleFormatter());
    return consoleHandler;
  }

  /**
   * The client application must implement ILoggerAdapter and set its instance using this method.
   *
   * @param logger the logger to set
   * @deprecated keep the logger instance locally. Keeping it here will impact other components, when using getLogger()
   */
  @Deprecated
  public static void setLogger(final ILoggerAdapter logger) {
    LoggerUtil.logger = logger;
  }

  /**
   * Retrieves the ILoggerAdapter instance.
   * <p>
   * If the client application has set a ILoggerAdapter to the application, it will retrieve the same.
   * <p>
   * Else if its null, it will create and retrieve the default instance of a JavaLoggerAdapterImpl instance. The default
   * implementation is using a Java Util Logger.
   *
   * @return ILoggerAdapter
   * @deprecated use the local logger
   */
  @Deprecated
  public static ILoggerAdapter getLogger() {
    if (LoggerUtil.logger == null) {
      LoggerUtil.logger = new JavaLoggerAdapterImpl();
    }
    return LoggerUtil.logger;
  }

  /**
   * Sets the log file path to a default implementation of logger.
   *
   * @param filePath file path
   * @deprecated results in creation of a new java logger, even when using 'log4j' logging
   */
  @Deprecated
  public static void setLogFile(final String filePath) {
    LoggerUtil.setLogger(new JavaLoggerAdapterImpl(filePath));
  }

  /**
   * Build the log message for the given message and the parameters
   *
   * @param message message with placeholders
   * @param params arguments to be put to placeholders
   * @return log message
   */
  public static String buildLogMessage(final String message, final Object... params) {
    if ((message == null) || (message.indexOf(MSG_PARAM_PLACE_HOLDER) < 0)) {
      return message;
    }
    boolean msgNotFinished;
    StringBuilder newMessage = new StringBuilder(100);

    SplittedMessage splitMsg = splitMessage(message);
    String[] msgItems = splitMsg.getMsgItems();

    int idx;
    if ((params == null)) {
      msgNotFinished = true;
      idx = -1;
    }
    else {
      int phCount = splitMsg.getPlaceHolderCount();
      for (idx = 0; idx < phCount; idx++) {
        if (idx >= params.length) {
          break;
        }
        newMessage.append(msgItems[idx]).append(params[idx]);
      }

      msgNotFinished = (phCount > params.length) || (phCount < msgItems.length);
      if (msgNotFinished) {
        idx--;
      }
    }
    if (msgNotFinished) {
      for (int idxBal = idx + 1; idxBal < msgItems.length; idxBal++) {
        newMessage.append(msgItems[idxBal]);
      }
    }

    return newMessage.toString();


  }

  /**
   * Properties of a message splitted based on the place holders
   *
   * @author bne4cob
   */
  private static class SplittedMessage {

    private final int placeHolderCount;
    private final String[] msgItems;

    /**
     * @param placeHolderCount
     * @param msgItems
     */
    SplittedMessage(final int placeHolderCount, final String[] msgItems) {
      super();
      this.placeHolderCount = placeHolderCount;
      this.msgItems = msgItems;
    }


    /**
     * @return the placeHolderCount
     */
    final int getPlaceHolderCount() {
      return this.placeHolderCount;
    }


    /**
     * @return the msgItems
     */
    final String[] getMsgItems() {
      return this.msgItems;
    }

  }

  /**
   * @param message
   * @return
   */
  private static SplittedMessage splitMessage(final String message) {
    String temp = message;
    List<String> msgPartList = new ArrayList<>();
    int phCount = 0;
    int itemEndPos = temp.indexOf(MSG_PARAM_PLACE_HOLDER);
    if (itemEndPos == -1) {
      msgPartList.add(temp);
    }
    else {
      while (true) {
        phCount++;
        msgPartList.add(temp.substring(0, itemEndPos));
        if (temp.length() <= (itemEndPos + MSG_PARAM_PLACE_HOLDER_LEN)) {
          break;
        }
        temp = temp.substring(itemEndPos + MSG_PARAM_PLACE_HOLDER_LEN);

        itemEndPos = temp.indexOf(MSG_PARAM_PLACE_HOLDER);
        if (itemEndPos < 0) {
          msgPartList.add(temp);
          break;
        }
      }
    }
    String[] retArr = new String[msgPartList.size()];
    return new SplittedMessage(phCount, msgPartList.toArray(retArr));
  }


}
