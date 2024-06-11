/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.rest.wadl.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.bosch.caltool.icdm.model.general.WsService;

/**
 * @author elm1cob
 */
public class ParserHandler extends DefaultHandler {

  private final Map<String, WsService> wadlWebServiceMap;

  private WsService serviceObj;

  private String previousTag = "";

  private boolean isMultiParam = false;

  private List<String> uriPathList;

  private StringBuilder servDesc;

  private int resourceTagCnt = -1;
  /**
   * Service Scope - Internal
   */
  private static final String SCOPE_INTERNAL = "I";

  private static final String RESOURCE_TAG = "Resource";

  private static final String METHOD_TAG = "Method";

  private static final String REQUEST_TAG = "Request";

  private static final String RESPONSE_TAG = "Response";

  private static final String REPRESENT_TAG = "Representation";

  private static final String PARAM_TAG = "Param";

  /**
   * Constructor
   */
  public ParserHandler() {
    super();
    this.wadlWebServiceMap = new HashMap<>();
    this.servDesc = new StringBuilder();
  }

  @Override
  public void startElement(final String uri, final String localName, final String qName, final Attributes attributes)
      throws SAXException {

    if (qName.equalsIgnoreCase(RESOURCE_TAG)) {
      this.resourceTagCnt++;
      resolveServiceURI(attributes);
    }

    else if (qName.equalsIgnoreCase(METHOD_TAG)) {
      this.servDesc.append("Method : " + attributes.getValue("id"));
      createServiceObj(attributes);
    }

    else if (qName.equalsIgnoreCase(REQUEST_TAG)) {
      this.previousTag = qName;
    }

    else if (qName.equalsIgnoreCase(RESPONSE_TAG)) {
      this.previousTag = qName;
    }

    else if (qName.equalsIgnoreCase(REPRESENT_TAG)) {
      this.servDesc.append('\n');
      if (this.previousTag.equalsIgnoreCase(ParserHandler.REQUEST_TAG)) {
        this.servDesc.append("Request Type : ");
      }
      else if (this.previousTag.equalsIgnoreCase(ParserHandler.RESPONSE_TAG)) {
        this.servDesc.append("Response Type : ");
      }

      String representation = attributes.getValue("mediaType").replace("application/", "").replace("multipart/", "");
      this.servDesc.append(representation);

    }

    else if (qName.equalsIgnoreCase(PARAM_TAG)) {
      if (this.previousTag.equalsIgnoreCase(ParserHandler.REQUEST_TAG) && !this.isMultiParam) {
        this.servDesc.append('\n').append("Request parameters : ");
        this.isMultiParam = true;
      }
      this.servDesc.append('\n').append('\t').append(attributes.getValue("name")).append(" (")
          .append(attributes.getValue("style")).append(" : ").append(attributes.getValue("type").split(":")[1])
          .append(")");
    }

  }

  /**
   * Constructs the unique Id for a service
   *
   * @return String - Unique ID for the URI
   */
  private String getUniqueServiceName() {
    return this.serviceObj.getServMethod().concat(":").concat((String.join("/", this.uriPathList)));
  }

  /**
   * Creates the Web Service object
   *
   * @param attributes Attributes
   */
  private void createServiceObj(final Attributes attributes) {
    this.serviceObj = new WsService();

    this.serviceObj.setModule(this.uriPathList.get(0).toUpperCase());
    this.serviceObj.setServUri(String.join("/", this.uriPathList));
    this.serviceObj.setServMethod(attributes.getValue("name"));
    this.serviceObj.setDeleted(false);
    this.serviceObj.setServiceScope(SCOPE_INTERNAL);
  }

  /**
   * Maintains the URI for web service
   *
   * @param attributes
   */
  private void resolveServiceURI(final Attributes attributes) {
    if (this.resourceTagCnt == 0) {
      this.uriPathList = new ArrayList<>(Arrays.asList(attributes.getValue("path").split("/")));
      this.uriPathList.remove(0);
    }
    else {
      this.uriPathList.add(attributes.getValue("path"));
    }
  }

  /**
   * Returns the wadl map of services
   *
   * @return the restWebServicesMap
   */
  public Map<String, WsService> getRestWebServicesMap() {
    return this.wadlWebServiceMap;
  }

  @Override
  public void endElement(final String uri, final String localName, final String qName) throws SAXException {

    if (qName.equalsIgnoreCase(RESOURCE_TAG)) {
      this.resourceTagCnt--;
      this.uriPathList.remove(this.uriPathList.size() - 1);
    }

    if (qName.equalsIgnoreCase(METHOD_TAG)) {
      String uniqueServiceName = getUniqueServiceName();

      this.serviceObj.setName(uniqueServiceName);
      this.serviceObj.setDescription(this.servDesc.toString());
      this.wadlWebServiceMap.put(uniqueServiceName, this.serviceObj);

      this.servDesc = new StringBuilder();
      this.isMultiParam = false;
    }

    if (qName.equalsIgnoreCase(REPRESENT_TAG)) {
      // No Implementation
    }

    if (qName.equalsIgnoreCase(PARAM_TAG)) {
      // No Implementation
    }

    if (qName.equalsIgnoreCase(REQUEST_TAG)) {
      // No Implementation
    }

    if (qName.equalsIgnoreCase(RESPONSE_TAG)) {
      // No Implementation
    }

  }

  @Override
  public void characters(final char[] ch, final int start, final int length) throws SAXException {
    // No implementation
  }
}
