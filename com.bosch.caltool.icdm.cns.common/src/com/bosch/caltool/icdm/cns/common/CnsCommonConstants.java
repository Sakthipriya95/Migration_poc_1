/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.cns.common;


/**
 * @author bne4cob
 */
public final class CnsCommonConstants {

  /**
   * Request parameter : Change Event ID
   */
  public static final String REQ_CHANGE_EVENT_ID = "ChangeEventId";
  /**
   * Request parameter : Service ID
   */
  public static final String REQ_SERVICE_ID = "ServiceId";
  /**
   * Request parameter : Session ID
   */
  public static final String REQ_SESSION_ID = "SessionId";
  /**
   * Request parameter : User
   */
  public static final String REQ_USER_ID = "User";

  /**
   * Request header : timezone
   */
  public static final String REQHDR_TIMEZONE = "TimeZoneOffSet";
  /**
   * Request header : producer port
   */
  public static final String REQHDR_PRODUCER_PORT = "ProducerPort";

  /**
   * Default Session ID
   */
  public static final String DEFAULT_SESSION_ID = "DEFAULT";

  /**
   * URL Path - data producer
   */
  public static final String RWS_DATA_PRODUCER = "dataproducer";
  /**
   * URL Path - event
   */
  public static final String RWS_EVENT = "event";
  /**
   * URL Path - data consumer
   */
  public static final String RWS_DATA_CONSUMER = "dataconsumer";
  /**
   * URL Path - Event after ID
   */
  public static final String RWS_EVENTS_AFTER = "eventsafterid";
  /**
   * URL Path - Latest event ID
   */
  public static final String RWS_LATEST_EVENT_ID = "latesteventid";
  /**
   * URL Path - events of current session
   */
  public static final String RWS_EVENTS_CUR_SESSION = "eventsofcurrentsession";

  /**
   * URL Path - events of current session
   */
  public static final String RWS_SESSION = "session";

  /**
   * URL Path - Admin
   */
  public static final String RWS_ADMIN = "admin";
  /**
   * URL Path - Statistics
   */
  public static final String RWS_STATISTICS = "stats";
  /**
   * URL Path - Statistics with details
   */
  public static final String RWS_STATISTICS_DET = "statsdet";
  /**
   * URL Path - Statistics of session
   */
  public static final String RWS_STATISTICS_SESSION = "statssession";
  /**
   * URL Path - Statistics of producer
   */
  public static final String RWS_STATISTICS_PRODUCER = "statsproducer";
  /**
   * URL Path - data consumers
   */
  public static final String RWS_PRODUCERS = "producers";

  /**
   * URL Path - data consumers
   */
  public static final String RWS_CONSUMERS = "consumers";

  /**
   * URL Path - Events
   */
  public static final String RWS_EVENTS = "events";
  /**
   * URL Path - Event details
   */
  public static final String RWS_EVENT_DET = "eventdetails";

  /**
   * URL Path - Events for the given session
   */
  public static final String RWS_SESSION_EVENTS = "sessionevents";

  /**
   * URL Path - Events for the given producer
   */
  public static final String RWS_PRODUCER_EVENTS = "producerevents";
  /**
   * URL Path - Events for the given source state
   */
  public static final String RWS_EVENTS_BY_SOURCE_STATE = "eventsbysourcestate";


  /**
   * Query parameter - Event ID
   */
  public static final String QP_EVENT_ID = "eventid";

  /**
   * Start Event ID
   */
  public static final String QP_EVENT_ID_START = "eventidstart";

  /**
   * Query parameter - return record size limit
   */
  public static final String QP_RET_SIZE = "size";
  /**
   * Query parameter - Return record start at index
   */
  public static final String QP_RET_START_AT = "startat";
  /**
   * Query parameter - Session ID
   */
  public static final String QP_SESSION_ID = "sessionid";
  /**
   * Query parameter - Producer ID
   */
  public static final String QP_PRODUCER_ID = "producerid";
  /**
   * Query parameter - Data Source State
   */
  public static final String QP_SOURCE_STATE = "sourcestate";

  /**
   * Query parameter - Session Types (separated by comma)
   */
  public static final String QP_SESSION_TYPES = "sessiontypes";
  /**
   * Query parameter - Event Details
   */
  public static final String QP_EVENT_DETAILS = "eventdetails";
  /**
   * Query parameter - Client's CNS listener port
   */
  public static final String QP_LISTENING_PORT = "listenerport";


  /**
   * Default date format "yyyy-MM-dd HH:mm:ss.SSS Z"
   */
  public static final String DEFALUT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

  /**
   * Encoding with GZIP
   */
  public static final String ENCODING_GZIP = "gzip";


  private CnsCommonConstants() {
    // Private constructor
  }
}
