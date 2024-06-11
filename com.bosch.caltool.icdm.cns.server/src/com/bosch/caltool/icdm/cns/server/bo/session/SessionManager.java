/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.cns.server.bo.session;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.icdm.cns.common.CnsCommonConstants;
import com.bosch.caltool.icdm.cns.common.model.SessionInfo;
import com.bosch.caltool.icdm.cns.server.bo.CnsObjectStore;
import com.bosch.caltool.icdm.cns.server.bo.CnsServiceData;
import com.bosch.caltool.icdm.cns.server.bo.events.ChangeEventManager;
import com.bosch.caltool.icdm.cns.server.exception.CnsServiceException;
import com.bosch.caltool.icdm.cns.server.utils.Utils;

/**
 * @author bne4cob
 */
public enum SessionManager {
                            /**
                             * Unique instance
                             */
                            INSTANCE;

  private static final long TIME_IN_ACTIVE_STATE =
      Long.parseLong(CnsObjectStore.getServerProperty("TIME_IN_ACTIVE_STATE"));
  private static final long TIME_IN_INACTIVE_STATE =
      Long.parseLong(CnsObjectStore.getServerProperty("TIME_IN_INACTIVE_STATE"));

  static {
    getLogger().debug(
        "Session cleanup settings : Time in active state = {} seconds, Time in inactive state = {} seconds",
        TIME_IN_ACTIVE_STATE, TIME_IN_INACTIVE_STATE);
  }


  private final Instant serverStartTime = Instant.now();

  private final ConcurrentMap<String, Session> activeSessionsMap = new ConcurrentHashMap<>();

  private final ConcurrentMap<String, Session> closedSessionsMap = new ConcurrentHashMap<>();

  private final ConcurrentMap<String, Session> inactiveSessionsMap = new ConcurrentHashMap<>();

  private int peakActiveSessionCount;

  private Instant peakActiveSessionTime = Instant.now();

  SessionManager() {
    doCreateSession(CnsCommonConstants.DEFAULT_SESSION_ID, "", "", 0);
  }

  /**
   * Create a session
   *
   * @param clientUser Client User
   * @param clientIp client IP
   * @param listenPort client listening port
   * @return session ID
   */
  public String createSession(final String clientUser, final String clientIp, final int listenPort) {
    String sessId = UUID.randomUUID().toString();
    doCreateSession(sessId, clientUser, clientIp, listenPort);
    return sessId;
  }

  private void doCreateSession(final String sessId, final String clientUser, final String clientIp,
      final int listenPort) {

    Session session = new Session();
    session.setClientUser(clientUser);
    session.setClientIp(clientIp);
    session.setListenPort(listenPort);
    session.setSessionId(sessId);

    this.activeSessionsMap.put(sessId, session);

    updatePeakActiveCount();

    getLogger().debug("CNS session created. ID = {}", sessId);
  }

  private void updatePeakActiveCount() {
    int activeSessCount = this.activeSessionsMap.size();

    if (activeSessCount > this.peakActiveSessionCount) {
      this.peakActiveSessionCount = activeSessCount;
      this.peakActiveSessionTime = Instant.now();

      getLogger().debug("Peak active session count updated. current count = {}", this.peakActiveSessionCount);
    }
  }

  /**
   * Close the session
   *
   * @param sessionId session ID
   */
  public void closeSession(final String sessionId) {
    Session sess = this.activeSessionsMap.get(sessionId);
    if (sess == null) {
      sess = this.inactiveSessionsMap.get(sessionId);
    }

    if (sess != null) {
      sess.close();
      this.closedSessionsMap.put(sessionId, sess);
      this.activeSessionsMap.remove(sessionId);
      this.inactiveSessionsMap.remove(sessionId);
    }

    getLogger().debug("CNS Session closed. ID = {}", sessionId);
  }

  /**
   * All sessions
   *
   * @return map of all sessions. Key session ID, value session
   */
  private Map<String, Session> getAllSessionsMap() {
    Map<String, Session> retMap = new HashMap<>(this.activeSessionsMap);
    retMap.putAll(this.closedSessionsMap);
    retMap.putAll(this.inactiveSessionsMap);
    return retMap;
  }

  /**
   * Mark the session as inactive
   *
   * @param sessionId sessionId
   */
  public void markInactive(final String sessionId) {
    Session sess = this.activeSessionsMap.get(sessionId);
    if (sess != null) {
      sess.deactivate();
      this.inactiveSessionsMap.put(sessionId, sess);
      this.activeSessionsMap.remove(sessionId);

      getLogger().debug("CNS Session deactivated. ID = {}", sessionId);
    }
  }

  private void deactivateUnusedSessions() {
    getLogger().debug("Looking for unused active sessions");

    new ArrayList<>(this.activeSessionsMap.values()).stream().filter(sess -> {
      Instant activeThreshold = Instant.now().minusSeconds(TIME_IN_ACTIVE_STATE);
      Instant checkInst = sess.getLastContactAt() == null ? sess.getCreatedAt() : sess.getLastContactAt();
      return activeThreshold.isAfter(checkInst);
    }).map(Session::getSessionId).filter(sessId -> !CnsCommonConstants.DEFAULT_SESSION_ID.equals(sessId))
        .forEach(this::markInactive);
  }

  private void closeInactiveSessions() {
    getLogger().debug("Looking for unused inactive sessions");

    new ArrayList<>(this.inactiveSessionsMap.values()).stream().filter(sess -> {
      Instant inactiveThreshold = Instant.now().minusSeconds(TIME_IN_INACTIVE_STATE);
      return inactiveThreshold.isAfter(sess.getInactiveAt());
    }).map(Session::getSessionId).forEach(this::closeSession);
  }

  /**
   * Check unused active/inactive sessions
   */
  public void checkUnusedSessions() {
    getLogger().debug("Checking unused sessions ...");

    try {
      deactivateUnusedSessions();
      closeInactiveSessions();

      getLogger().debug("Checking unused sessions COMPLETED");
    }
    catch (Exception e) {
      getLogger().error(e.getMessage(), e);
    }

  }

  /**
   * Mark the session as active
   *
   * @param sessionId sessionId
   */
  public void markActive(final String sessionId) {
    Session sess = this.inactiveSessionsMap.get(sessionId);
    if (sess != null) {
      sess.activate();
      this.inactiveSessionsMap.remove(sessionId);
      this.activeSessionsMap.put(sessionId, sess);
      updatePeakActiveCount();
    }
  }

  /**
   * @return active sessions
   */
  public Set<String> getActiveSessions() {
    return new HashSet<>(this.activeSessionsMap.keySet());
  }

  /**
   * @return inactive sessions
   */
  public Set<String> getInactiveSessions() {
    return new HashSet<>(this.inactiveSessionsMap.keySet());
  }

  /**
   * @return closed sessions
   */
  public Set<String> getClosedSessions() {
    return new HashSet<>(this.closedSessionsMap.keySet());
  }

  /**
   * @param serviceData Service Data
   * @return the startTime
   */
  public String getStartTimeStr(final CnsServiceData serviceData) {
    return Utils.instantToString(this.serverStartTime, serviceData.getTimeZoneId());
  }

  /**
   * @return session count
   */
  public int getActiveSessionCount() {
    return this.activeSessionsMap.size();
  }

  /**
   * @return the peakActiveSessionCount
   */
  public int getPeakActiveSessionCount() {
    return this.peakActiveSessionCount;
  }

  /**
   * @return session count
   */
  public int getClosedSessionCount() {
    return this.closedSessionsMap.size();
  }

  /**
   * @return session count
   */
  public int getInactiveSessionCount() {
    return this.inactiveSessionsMap.size();
  }

  /**
   * @return total session count
   */
  public int getTotalSessionCount() {
    return getActiveSessionCount() + getInactiveSessionCount() + getClosedSessionCount();
  }

  /**
   * @return logger
   */
  private static ILoggerAdapter getLogger() {
    return CnsObjectStore.getLogger();
  }

  /**
   * @param sessionId session Id
   * @return Session
   * @throws CnsServiceException when input session id is invalid
   */
  public Session get(final String sessionId) throws CnsServiceException {

    if (sessionId == null) {
      throw new CnsServiceException("CNS-2000", "Session ID cannot be null" + sessionId);
    }

    Session ret = getWithoutEx(sessionId);

    if (ret == null) {
      throw new CnsServiceException("CNS-2001", "Invalid session ID");
    }

    return ret;
  }

  /**
   * Checks whether the session ID is valid or not
   *
   * @param sessionId session ID
   * @return true, if session id is valid
   */
  public boolean isValidSession(final String sessionId) {
    Session ret = this.activeSessionsMap.get(sessionId);
    if (ret == null) {
      ret = this.inactiveSessionsMap.get(sessionId);
      if (ret == null) {
        ret = this.closedSessionsMap.get(sessionId);
      }
    }

    return ret != null;
  }

  /**
   * Checks whether the session ID is closed or not
   *
   * @param sessionId session ID
   * @return true, if session id is closed
   */
  public SESSION_STATE getState(final String sessionId) {
    SESSION_STATE ret;

    if (CnsCommonConstants.DEFAULT_SESSION_ID.equals(sessionId) || (sessionId == null) || sessionId.isEmpty()) {
      ret = SESSION_STATE.NO_SESSION;
    }
    else if (this.activeSessionsMap.containsKey(sessionId)) {
      ret = SESSION_STATE.ACTIVE;
    }
    else if (this.inactiveSessionsMap.containsKey(sessionId)) {
      ret = SESSION_STATE.INACTIVE;
    }
    else if (this.closedSessionsMap.containsKey(sessionId)) {
      ret = SESSION_STATE.CLOSED;
    }
    else {
      ret = SESSION_STATE.INVALID;
    }

    return ret;
  }

  /**
   * @param sessionId session Id
   * @throws CnsServiceException when input session id is invalid/closed
   */
  public void updateLastContactedAt(final String sessionId) throws CnsServiceException {
    get(sessionId).updateLastContactAt();
    markActive(sessionId);
  }

  /**
   * @param sessionId session Id
   * @param dataSize size of current event data
   */
  public void addToTotalDataSize(final String sessionId, final long dataSize) {
    getWithoutEx(sessionId).addToTotalDataSize(dataSize);
  }

  /**
   * @param sess
   * @return
   */
  private SessionInfo toSessionInfo(final Session sess, final CnsServiceData serviceData) {
    SessionInfo ret = new SessionInfo();

    ret.setSessionId(sess.getSessionId());
    ret.setClientIp(sess.getClientIp());
    ret.setListenPort(sess.getListenPort());
    ret.setUser(sess.getClientUser());
    ret.setState(sess.getState().name());
    ret.setCreatedAt(Utils.instantToString(sess.getCreatedAt(), serviceData.getTimeZoneId()));
    ret.setInactiveAt(Utils.instantToString(sess.getInactiveAt(), serviceData.getTimeZoneId()));
    ret.setClosedAt(Utils.instantToString(sess.getClosedAt(), serviceData.getTimeZoneId()));
    ret.setLastContactAt(Utils.instantToString(sess.getLastContactAt(), serviceData.getTimeZoneId()));
    ret.setTotalDataSize(sess.getTotalDataSize());
    ret.setEventCount(ChangeEventManager.INSTANCE.getEventCountSession(sess.getSessionId()));

    return ret;
  }

  /**
   * Get all Sessions
   *
   * @param serviceData Service Data
   * @return Map. key - session ID, value - session Info
   */
  public Map<String, SessionInfo> getAllSessionsInfo(final CnsServiceData serviceData) {
    Map<String, SessionInfo> retMap = new HashMap<>();
    getAllSessionsMap().forEach((sid, sess) -> retMap.put(sid, toSessionInfo(sess, serviceData)));
    return retMap;
  }

  /**
   * Get active Sessions
   *
   * @param serviceData Service Data
   * @return List of session Info
   */
  public List<SessionInfo> getActiveSessionsInfo(final CnsServiceData serviceData) {
    return this.activeSessionsMap.values().stream().map(sess -> toSessionInfo(sess, serviceData))
        .collect(Collectors.toList());
  }

  /**
   * Get inactive Sessions
   *
   * @param serviceData Service Data
   * @return List of session Info
   */
  public List<SessionInfo> getInactiveSessionsInfo(final CnsServiceData serviceData) {
    return this.inactiveSessionsMap.values().stream().map(sess -> toSessionInfo(sess, serviceData))
        .collect(Collectors.toList());
  }

  /**
   * Get closed Sessions
   *
   * @param serviceData Service Data
   * @return List of session Info
   */
  public List<SessionInfo> getClosedSessionsInfo(final CnsServiceData serviceData) {
    return this.closedSessionsMap.values().stream().map(sess -> toSessionInfo(sess, serviceData))
        .collect(Collectors.toList());
  }

  /**
   * @param sessIdSet set of session IDs
   * @param serviceData Service Data
   * @return map of session details
   */
  public Map<String, SessionInfo> getSessionInfoMap(final Set<String> sessIdSet, final CnsServiceData serviceData) {
    return sessIdSet.stream().map(this::getWithoutEx).filter(Objects::nonNull)
        .collect(Collectors.toMap(Session::getSessionId, sess -> toSessionInfo(sess, serviceData)));
  }

  private Session getWithoutEx(final String sessionId) {
    Session ret = this.activeSessionsMap.get(sessionId);
    if (ret == null) {
      ret = this.inactiveSessionsMap.get(sessionId);
      if (ret == null) {
        ret = this.closedSessionsMap.get(sessionId);
      }
    }
    return ret;
  }

  /**
   * @param sessionId session ID
   * @return session
   * @throws CnsServiceException if session is invalid or closed
   */
  public Session assertNotClosed(final String sessionId) throws CnsServiceException {
    Session sess = get(sessionId);
    if (sess.getState() == SESSION_STATE.CLOSED) {
      throw new CnsServiceException("CNS-2002", "Session is closed");
    }

    return sess;
  }

  /**
   * @return older created date of non closed sessions
   */
  public Instant getOldestSessionCreatedDate() {
    List<Session> sessList = new ArrayList<>(this.activeSessionsMap.values());
    sessList.addAll(this.inactiveSessionsMap.values());

    return sessList.stream().filter(sess -> !CnsCommonConstants.DEFAULT_SESSION_ID.equals(sess.getSessionId()))
        .map(Session::getCreatedAt).min((a, b) -> a.compareTo(b)).orElse(Instant.now());

  }

  /**
   * Get the Session info of the given Session ID
   *
   * @param serviceData Cns Service Data
   * @param sessionId Session ID
   * @return Event Info
   * @throws CnsServiceException if Session ID is invalid
   */
  public SessionInfo getSessionInfo(final CnsServiceData serviceData, final String sessionId)
      throws CnsServiceException {

    return toSessionInfo(get(sessionId), serviceData);

  }

  /**
   * Get the peak active session reached time as string, in requested timezone
   *
   * @param serviceData service data
   * @return peak active session time as string, in the given timezone
   */
  public String getPeakActiveSessionTime(final CnsServiceData serviceData) {
    return Utils.instantToString(this.peakActiveSessionTime, serviceData.getTimeZoneId());
  }


}
