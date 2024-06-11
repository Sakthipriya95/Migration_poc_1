/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.applicationlog.request.factory;

import com.bosch.caltool.apic.ws.AllPidcDiffType;
import com.bosch.caltool.apic.ws.GetAllProjectIdCardVersions;
import com.bosch.caltool.apic.ws.GetAllProjectIdCards;
import com.bosch.caltool.apic.ws.GetAttributeValues;
import com.bosch.caltool.apic.ws.GetPidcDiffsType;
import com.bosch.caltool.apic.ws.GetProjectIdCard;
import com.bosch.caltool.apic.ws.GetProjectIdCardForVersion;
import com.bosch.caltool.apic.ws.GetProjectIdCardV2;
import com.bosch.caltool.apic.ws.PidcAccessRightType;
import com.bosch.caltool.apic.ws.PidcSearchConditionType;
import com.bosch.caltool.apic.ws.applicationlog.request.AbstractRequest;
import com.bosch.caltool.apic.ws.applicationlog.request.AllPidcDiffRequest;
import com.bosch.caltool.apic.ws.applicationlog.request.GetAllProjectIdCardVersionsRequest;
import com.bosch.caltool.apic.ws.applicationlog.request.GetAllProjectIdCardsRequest;
import com.bosch.caltool.apic.ws.applicationlog.request.GetAttributeValuesRequest;
import com.bosch.caltool.apic.ws.applicationlog.request.GetProjectIdCardForVersionRequest;
import com.bosch.caltool.apic.ws.applicationlog.request.GetProjectIdCardRequest;
import com.bosch.caltool.apic.ws.applicationlog.request.GetProjectIdCardV2Request;
import com.bosch.caltool.apic.ws.applicationlog.request.NullRequest;
import com.bosch.caltool.apic.ws.applicationlog.request.PidcAccessRightRequest;
import com.bosch.caltool.apic.ws.applicationlog.request.PidcDiffRequest;
import com.bosch.caltool.apic.ws.applicationlog.request.PidcSearchRequest;


/**
 * Factory class for all Sub-Classes of AbstractRequest. For each subclass there's one method which accespts an web
 * service request object and returns an AbstractRequest object with the String representation of the request.
 *
 * @author imi2si
 * @since 1.19
 */
public final class RequestFactory {

  /**
   * @param searchCond the PidcSearchConditionType web service request
   * @return an AbstractRequest which contains the String-Representation of the request
   */
  public static AbstractRequest getRequest(final PidcSearchConditionType[] searchCond) {
    return new PidcSearchRequest(searchCond);
  }

  /**
   * @param searchCond the GetPidcDiffsType web service request
   * @return an AbstractRequest which contains the String-Representation of the request
   */
  public static AbstractRequest getRequest(final GetPidcDiffsType searchCond) {
    return new PidcDiffRequest(searchCond);
  }

  /**
   * @param searchCond the PidcAccessRightType web service request
   * @return an AbstractRequest which contains the String-Representation of the request
   */
  public static AbstractRequest getRequest(final PidcAccessRightType searchCond) {
    return new PidcAccessRightRequest(searchCond);
  }

  /**
   * @param searchCond the AllPidcDiffType web service request
   * @return an AbstractRequest which contains the String-Representation of the request
   */
  public static AbstractRequest getRequest(final AllPidcDiffType searchCond) {
    return new AllPidcDiffRequest(searchCond);
  }

  /**
   * @param searchCond the Object web service request
   * @return an AbstractRequest which returns an empty object
   */
  public static AbstractRequest getRequest(final GetAttributeValues searchCond) {
    return new GetAttributeValuesRequest(searchCond);
  }

  /**
   * @param searchCond the Object web service request
   * @return an AbstractRequest which returns an empty object
   */
  public static AbstractRequest getRequest(final GetAllProjectIdCards searchCond) {
    return new GetAllProjectIdCardsRequest(searchCond);
  }

  /**
   * @param searchCond the Object web service request
   * @return an AbstractRequest which returns an empty object
   */
  public static AbstractRequest getRequest(final GetProjectIdCard searchCond) {
    return new GetProjectIdCardRequest(searchCond);
  }

  /**
   * @param searchCond the Object web service request
   * @return an AbstractRequest which returns an empty object
   */
  public static AbstractRequest getRequest(final GetProjectIdCardV2 searchCond) {
    return new GetProjectIdCardV2Request(searchCond);
  }

  /**
   * @param searchCond the Object web service request
   * @return an AbstractRequest which returns an empty object
   */
  public static AbstractRequest getRequest(final GetAllProjectIdCardVersions searchCond) {
    return new GetAllProjectIdCardVersionsRequest(searchCond);
  }

  /**
   * @param searchCond the Object web service request
   * @return an AbstractRequest which returns an empty object
   */
  public static AbstractRequest getRequest(final GetProjectIdCardForVersion searchCond) {
    return new GetProjectIdCardForVersionRequest(searchCond);
  }

  /**
   * @return an AbstractRequest which returns an empty object
   */
  public static AbstractRequest getRequest() {
    return new NullRequest();
  }

  /**
   * Private constructor. An instantiation of this class makes no sense.
   */
  private RequestFactory() {}
}
