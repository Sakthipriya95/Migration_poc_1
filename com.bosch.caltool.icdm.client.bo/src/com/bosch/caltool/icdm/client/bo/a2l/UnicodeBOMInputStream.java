/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.a2l;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;

/**
 * @author dmr1cob
 */
public class UnicodeBOMInputStream extends InputStream {

  private final PushbackInputStream pushbackinput;
  private final String encoding;

  /**
   * @param inputStream
   * @throws NullPointerException
   * @throws IOException
   */
  public UnicodeBOMInputStream(final InputStream inputStream) throws IOException

  {
    if (inputStream == null) {
      throw new NullPointerException("invalid input stream: null is not allowed");
    }

    this.pushbackinput = new PushbackInputStream(inputStream, 4);

    final byte[] input = new byte[4];
    final int data = this.pushbackinput.read(input);

    if ((input[0] == (byte) 0xEF) && (input[1] == (byte) 0xBB) && (input[2] == (byte) 0xBF)) {
      this.encoding = "UTF-8";
    }
    else if ((input[0] == (byte) 0xFF) && (input[1] == (byte) 0xFE)) {
      this.encoding = "UTF-16LE";
    }
    else if ((input[0] == (byte) 0xFE) && (input[1] == (byte) 0xFF)) {
      this.encoding = "UTF-16BE";
    }
    else if ((input[0] == (byte) 0x00) && (input[1] == (byte) 0x00) && (input[2] == (byte) 0xFE) &&
        (input[3] == (byte) 0xFF)) {
      this.encoding = "UTF-32BE";
    }
    else if ((input[0] == (byte) 0xFF) && (input[1] == (byte) 0xFE) && (input[2] == (byte) 0x00) &&
        (input[3] == (byte) 0x00)) {
      this.encoding = "UTF-32BE";
    }
    else {
      this.encoding = null;
    }


    if (data > 0) {
      this.pushbackinput.unread(input, 0, data);
    }
  }


  public String getEncoding() {
    return this.encoding;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int read() throws IOException {
    return this.pushbackinput.read();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int read(final byte[] b) throws IOException {
    return this.pushbackinput.read(b, 0, b.length);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int read(final byte[] b, final int off, final int len) throws IOException {
    return this.pushbackinput.read(b, off, len);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public long skip(final long n) throws IOException {
    return this.pushbackinput.skip(n);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int available() throws IOException {
    return this.pushbackinput.available();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void close() throws IOException {
    this.pushbackinput.close();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public synchronized void mark(final int readlimit) {
    this.pushbackinput.mark(readlimit);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public synchronized void reset() throws IOException {
    this.pushbackinput.reset();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean markSupported() {
    return this.pushbackinput.markSupported();
  }
}
