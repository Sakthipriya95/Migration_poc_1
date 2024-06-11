/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.client.output;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * @author imi2si
 */
public abstract class AbstractStringOutput implements IOutput {

  /**
   * end index value - 3 for truncation
   */
  private static final int END_INDEX_VALUE_THREE = 3;

  /**
   * Indent value 5 for appending detail.
   */
  private static final int INDENT_VALUE_FIVE = 5;

  /**
   * column length value - 35
   */
  protected static final int COL_LENGTH = 35;

  protected StringBuilder output = new StringBuilder();

  private int noOfRowsToInclude = -1;


  public abstract String getOutput();

  protected abstract void createHeader();

  protected abstract void createRows();

  protected abstract boolean outputAvailable();

  public void createOutput() {
    if (outputAvailable()) {
      createHeader();
      createRows();
    }
  }

  protected final void append(final Calendar value) {
    if (value == null) {
      return;
    }

    append(new SimpleDateFormat("dd.MM.yyyy HH:mm:ss Z").format(value.getTime()));
  }

  protected final void append(final Date value) {
    append(new SimpleDateFormat().format(value));
  }

  protected final void append(final int value) {
    append(String.valueOf(value));
  }

  protected final void append(final boolean value) {
    append(String.valueOf(value));
  }

  protected final void append(final Long value) {
    append(handleNullObject(value));
  }

  protected final void append(final String value) {
    this.output.append(createColumn(handleNullObject(value)));
  }

  protected final void append(final AbstractStringOutput param) {
    AbstractStringOutput value = param;
    value.createOutput();
    this.output.append(value.getOutput());
  }

  protected final void appendDetail(final String value) {
    // Every Detail levelshould be indented to show the level difference
    String format = "%1$" + INDENT_VALUE_FIVE + "s";
    String indent = String.format(format, "");

    // The text beginning and all the line breaks have to be replaced with the indent
    String text = indent + value.replaceAll("\\u000A", "\n" + indent);

    this.output.append(handleNullObject(text));
  }

  protected final void lineBreak() {
    this.output.append("\n");
  }

  protected final String createColumn(final String columnText) {
    String text = columnText.replace("\r\n", "/");

    if (text.length() < COL_LENGTH) {
      String format = "%1$" + (COL_LENGTH - text.length()) + "s";
      return text + String.format(format, "") + "|";
    }

    return text.substring(0, COL_LENGTH - END_INDEX_VALUE_THREE) + "...|";
  }

  private <V> String handleNullObject(final V text) {
    return text == null ? new String("") : text.toString();
  }

  /**
   * @return the noOfRowsToInclude
   */
  protected final int getNoOfRowsToInclude() {
    return this.noOfRowsToInclude;
  }

  /**
   * @param noOfRowsToInclude the noOfRowsToInclude to set
   */
  public final void setNoOfRowsToInclude(final int noOfRowsToInclude) {
    this.noOfRowsToInclude = noOfRowsToInclude;
  }
}
