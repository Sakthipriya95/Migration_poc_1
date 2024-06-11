/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.utilitytools.general.blobcompression;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Path;

/**
 * @author bne4cob
 */
class Attachment {

  /**
   * Channel path
   */
  private Path path;
  /**
   * Data buffer
   */
  private ByteBuffer buffer;
  /**
   * Channel
   */
  private AsynchronousFileChannel asyncChannel;

  /**
   * @return the path
   */
  public Path getPath() {
    return this.path;
  }

  /**
   * @param path the path to set
   */
  public void setPath(final Path path) {
    this.path = path;
  }

  /**
   * @return the buffer
   */
  public ByteBuffer getBuffer() {
    return this.buffer;
  }

  /**
   * @param buffer the buffer to set
   */
  public void setBuffer(final ByteBuffer buffer) {
    this.buffer = buffer;
  }

  /**
   * @return the asyncChannel
   */
  public AsynchronousFileChannel getAsyncChannel() {
    return this.asyncChannel;
  }

  /**
   * @param asyncChannel the asyncChannel to set
   */
  public void setAsyncChannel(final AsynchronousFileChannel asyncChannel) {
    this.asyncChannel = asyncChannel;
  }

}