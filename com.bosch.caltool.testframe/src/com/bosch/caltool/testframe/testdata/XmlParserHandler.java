/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.testframe.testdata;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.bosch.caltool.testframe.TestUtils;

/**
 * @author bne4cob
 */
final class XmlParserHandler extends DefaultHandler {

  /**
   * Parsed test data records
   */
  private final List<InputData> testData = new ArrayList<>();

  /**
   * Current data being created
   */
  private InputData curTestData;

  /**
   * Current parameter being processed
   */
  private String curParam;

  /**
   * data flag
   */
  boolean data;

  /**
   * Item flag
   */
  boolean item;

  /**
   * Param flag
   */
  boolean param;

  /**
   * Start element
   * <p>
   * {@inheritDoc}
   */
  @Override
  public void startElement(final String uri, final String localName, final String qName, final Attributes attributes)
      throws SAXException {

    if (TestUtils.isEqualIgnoreCase(qName, "DATA")) {
      this.data = true;
    }
    else if (TestUtils.isEqualIgnoreCase(qName, "ITEM")) {
      this.curTestData = new InputData(attributes.getValue("class"));
      this.testData.add(this.curTestData);
      this.item = true;
    }
    else {
      this.curParam = qName;
      this.param = true;
    }

  }

  /**
   * End element
   * <p>
   * {@inheritDoc}
   */
  @Override
  public void endElement(final String uri, final String localName, final String qName) throws SAXException {
    // Nothing to do
  }

  /**
   * Set characters
   * <p>
   * {@inheritDoc}
   */
  @Override
  public void characters(final char chrs[], final int start, final int length) throws SAXException {
    if (this.data) {
      this.data = false;
    }
    if (this.item) {
      this.item = false;
    }
    if (this.param) {
      this.curTestData.getParams().put(this.curParam, new String(chrs, start, length));
      this.param = false;
    }
  }

  /**
   * @return the testData
   */
  public List<InputData> getTestData() {
    return this.testData;
  }
}