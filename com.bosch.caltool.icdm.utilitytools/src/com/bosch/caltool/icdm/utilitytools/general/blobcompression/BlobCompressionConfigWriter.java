/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.utilitytools.general.blobcompression;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.AbstractQueue;
import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.icdm.utilitytools.util.ToolLogger;

/**
 * @author bne4cob
 */
public class BlobCompressionConfigWriter implements Runnable {

  /**
   * history file path
   */
  private static final Path HISTORY_FILE_PATH = Paths.get(BlobCompressionConfig.HISTORY_FILE_NAME);

  /**
   * Instance
   */
  private static BlobCompressionConfigWriter INSTANCE;
  /**
   * Writing thread
   */
  private static Thread writerThread;

  /**
   * Sleep interval
   */
  private static final long SLEEP_INTERVAL = 1000;

  /**
   * Data queue
   */
  private final AbstractQueue<String> dataQueue = new ConcurrentLinkedQueue<>();

  /**
   * Current write position to channel
   */
  private int writePos = 0;

  /**
   * Stop writing flag
   */
  private boolean stop;

  /**
   * @throws IOException while trying to delete existing file
   */
  private BlobCompressionConfigWriter() throws IOException {
    File file = new File(BlobCompressionConfig.HISTORY_FILE_NAME);
    if (file.exists()) {
      file.delete();
    }
  }

  /**
   * @return Logger
   */
  private ILoggerAdapter getLogger() {
    return ToolLogger.getLogger();
  }

  /**
   * Add a text message to write
   *
   * @param text to write
   */
  public void add(final String text) {
    if (this.dataQueue.add(text)) {
      this.stop = false;
    }

  }

  /**
   * Add a collection of texts to write
   *
   * @param textCollection collection
   */
  public void add(final Collection<String> textCollection) {
    if (this.dataQueue.addAll(textCollection)) {
      this.stop = false;
    }
  }

  /**
   * Write all pending messages and finish
   *
   * @throws IOException when finishing
   */
  public void close() throws IOException {
    this.stop = true;
    try {
      writerThread.join();
    }
    catch (InterruptedException exp) {
      getLogger().error(exp.getMessage(), exp);
    }
  }

  /**
   * Convert messages in data queue to byte array
   *
   * @return data as byte array
   */
  private byte[] getDataAsBytes() {
    String lineSeparator = System.getProperty("line.separator");
    StringBuilder sb = new StringBuilder();
    while (!this.dataQueue.isEmpty()) {
      sb.append(this.dataQueue.poll());
      sb.append(lineSeparator);
    }
    String str = sb.toString();
    Charset cs = Charset.forName("UTF-8");
    return str.getBytes(cs);
  }


  /**
   * @return unique instance of this writer
   * @throws IOException when instance cannot be created
   */
  public static BlobCompressionConfigWriter getInstance() throws IOException {
    if (INSTANCE == null) {
      INSTANCE = new BlobCompressionConfigWriter();
    }
    return INSTANCE;
  }

  /**
   * Start the write process
   *
   * @return unique instance of writer
   * @throws IOException when instance cannot be created or writing cannot be started
   */
  public static BlobCompressionConfigWriter start() throws IOException {

    BlobCompressionConfigWriter writer = getInstance();

    if (!writer.stop) {
      writerThread = new Thread(writer);
      writerThread.start();
    }

    return writer;


  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void run() {
    while (true) {
      if (!this.dataQueue.isEmpty()) {
        AsynchronousFileChannel afc = null;
        try {
          afc = AsynchronousFileChannel.open(HISTORY_FILE_PATH, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
          WriteHandler handler = new WriteHandler();
          byte[] dataByteArr = getDataAsBytes();
          ByteBuffer dataBuffer = ByteBuffer.wrap(dataByteArr);
          Attachment attach = new Attachment();
          attach.setAsyncChannel(afc);
          attach.setBuffer(dataBuffer);
          attach.setPath(HISTORY_FILE_PATH);

          afc.write(dataBuffer, this.writePos, attach, handler);
          afc.close();
          this.writePos = this.writePos + dataByteArr.length;
        }
        catch (IOException exp) {
          getLogger().error(exp.getMessage(), exp);
        }

      }
      if (this.stop) {
        break;
      }
      try {
        Thread.sleep(SLEEP_INTERVAL);
      }
      catch (InterruptedException exp) {
        getLogger().error(exp.getMessage(), exp);
      }

    }
  }


}
