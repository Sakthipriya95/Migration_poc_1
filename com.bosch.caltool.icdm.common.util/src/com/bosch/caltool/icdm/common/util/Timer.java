/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Timer for time consumption calculations.
 *
 * <pre>
 * <code>
 * Timer timer = new Timer();
 * //Do stuff
 * LOG.debug("Time taken = {}", timer.finish());
 * </code>
 * </pre>
 *
 * OR, using check points, to identify intermediate timings
 *
 * <pre>
 * <code>
 * Timer timer = new Timer();
 * //Do stuff
 * LOG.debug("Time taken till now = {}", timer.addCheckPoint());
 * //Do stuff 2
 * LOG.debug("Time taken since previous check point = {}", timer.addCheckPoint());
 * //Do stuff 3
 * LOG.debug("Time taken = {}", timer.finish());
 * </code>
 * </pre>
 *
 * NOTE : This class is NOT thread safe.
 *
 * @author BNE4COB
 */
public class Timer {

  private static final long FINISHED_AT_DEFAULT = 0L;

  private static final String TIME_SEP = ":";

  private static final String UNIT_HRS = "hrs";
  private static final String UNIT_MINS = "mins";
  private static final String UNIT_SECS = "s";
  private static final String UNIT_MILLIS = "ms";

  private static final long CONVERTER_SEC_MIN_HR = 60L;
  private static final long CONVERTER_SEC_HR = 3600L;
  private static final long CONVERTER_MILLI_TO_SEC = 1000L;
  private static final long CONVERTER_NANO_MILLI = 1000000L;
  private static final long CONVERTER_NANO_SEC = 1000000000L;

  /**
   * Keeps the start nano time of this timer
   */
  private long startedAt = System.nanoTime();

  /**
   * Keeps the finished nano time, or 0L if running
   */
  private long finishedAt;

  /**
   * Key - stage name<br>
   * value - time (in ns) at stage
   */
  private final LinkedHashMap<String, Long> checkPointMap = new LinkedHashMap<>();

  /**
   * Automatic indexing of check points
   */
  private int cpIndex;

  /**
   * Resets and starts the timer again
   */
  public void reset() {
    this.startedAt = System.nanoTime();
    this.finishedAt = FINISHED_AT_DEFAULT;
    this.checkPointMap.clear();
    this.cpIndex = 0;
  }

  /**
   * @return elapsed time
   */
  public String getElapsedTime() {
    return formatTo(getElapsedTimeNanos());
  }

  /**
   * @return elapsed time in milli seconds
   */
  public long getElapsedTimeMs() {
    return getElapsedTimeNanos() / CONVERTER_NANO_MILLI;
  }


  /**
   * @return true, if timer is still running
   */
  public boolean isRunning() {
    return this.finishedAt == FINISHED_AT_DEFAULT;
  }

  /**
   * Finishes the timer
   *
   * @return time taken in milli seconds
   */
  public String finish() {
    long timeTaken = doFinish();
    return formatTo(timeTaken);
  }

  /**
   * @return time taken in milli seconds
   */
  public long getTimeTakenMs() {
    assertFinished();
    return getTimeTakenNs() / CONVERTER_NANO_MILLI;
  }

  /**
   * @return time taken in milli seconds
   */
  public String getTimeTaken() {
    assertFinished();
    return formatTo(getTimeTakenNs());
  }

  /**
   * Adds a check point with default name : "Check point &lt;number&gt";
   *
   * @return time taken (in ms) since timer start
   */
  public String addCheckPoint() {
    return addCheckPoint("Check point " + (++this.cpIndex));
  }

  /**
   * @param name name of the stage
   * @return time taken (in ms) since timer start
   */
  public String addCheckPoint(final String name) {
    long time = System.nanoTime();
    assertRunning();

    addStage(name, time);

    return formatTo(time);
  }

  /**
   * @param name check point name
   * @return time since timer start
   */
  public String getCheckPointTimeFromStart(final String name) {
    Long time = getCheckPointTimeFromStartNanos(name);
    return formatTo(time);
  }

  /**
   * @param name check point name
   * @return time in milli seconds, since timer start
   */
  public long getCheckPointTimeFromStartMs(final String name) {
    return (getCheckPointTimeAbsoluteNanos(name) - this.startedAt) / CONVERTER_NANO_MILLI;
  }


  /**
   * @param name check point name
   * @return time
   */
  public String getCheckPointTime(final String name) {
    Long time = getCheckPointTimeNanos(name);

    return formatTo(time);
  }


  /**
   * @param name check point name
   * @return time in milli seconds
   */
  public long getCheckPointTimeMs(final String name) {
    return getCheckPointTimeNanos(name) / CONVERTER_NANO_MILLI;
  }

  /**
   * @param checkPoint1 check point 1
   * @param checkPoint2 check point 2
   * @return time difference
   */
  public String getCheckPointTimeDiff(final String checkPoint1, final String checkPoint2) {
    long diff = getCheckPointTimeDiffNanos(checkPoint1, checkPoint2);
    return formatTo(diff);
  }


  /**
   * @param checkPoint1 check point 1
   * @param checkPoint2 check point 2
   * @return time difference in milli seconds
   */
  public long getCheckPointTimeDiffMs(final String checkPoint1, final String checkPoint2) {
    return getCheckPointTimeDiffNanos(checkPoint1, checkPoint2) / CONVERTER_NANO_MILLI;
  }

  /**
   * @return number of check points
   */
  public int getCheckPointCount() {
    return this.checkPointMap.size();
  }

  /**
   * @return check point names, in the order of creation
   */
  public List<String> getCheckPointNames() {
    return new ArrayList<>(this.checkPointMap.keySet());
  }

  /**
   * @return a brief info about the timer, its state etc.
   */
  public String getSummary() {
    StringBuilder ret = new StringBuilder("\nTimer : ");

    ret.append("\n  Status : ").append(getStatus());

    if (isRunning()) {
      ret.append("\n  Elapsed time : ").append(getElapsedTime());
    }
    else {
      ret.append("\n  Time taken : ").append(getTimeTaken());
    }

    if (hasCheckPoint()) {
      ret.append("\n  Check points : ").append(getCheckPointCount());
      ret.append("\n    Name\tTime\tTime Delta");
      this.checkPointMap.keySet().stream().forEach(cp -> ret.append("\n    ").append(cp).append('\t')
          .append(getCheckPointTimeFromStart(cp)).append('\t').append(getCheckPointTime(cp)));
    }

    return ret.toString();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    StringBuilder ret = new StringBuilder();

    ret.append(super.toString()).append(" [Started at = ").append(this.startedAt);
    if (!isRunning()) {
      ret.append(", Finished at = ").append(this.finishedAt);
    }

    ret.append(", Status = ").append(getStatus());
    ret.append(", Check points = ").append(getCheckPointCount());
    ret.append(']');

    return ret.toString();
  }


  private long getElapsedTimeNanos() {
    long timeTo = System.nanoTime();
    if (!isRunning()) {
      timeTo = this.finishedAt;
    }

    return timeTo - this.startedAt;
  }

  /**
   * @return true if check point is available
   */
  private boolean hasCheckPoint() {
    return !this.checkPointMap.isEmpty();
  }

  private Long getCheckPointTimeNanos(final String name) {
    Long time = getCheckPointTimeAbsoluteNanos(name);

    List<String> cpNameList = getCheckPointNames();

    int idx = cpNameList.indexOf(name);
    if (idx > 0) {
      String prevCp = cpNameList.get(idx - 1);
      time = time - getCheckPointTimeAbsoluteNanos(prevCp);
    }
    else {
      time = time - this.startedAt;
    }
    return time;
  }


  private String getStatus() {
    return isRunning() ? "Running" : "Finished";
  }


  private long getCheckPointTimeAbsoluteNanos(final String name) {
    Long time = this.checkPointMap.get(name);
    if (time == null) {
      throw new IllegalArgumentException("Invalid check point name '" + name + "'");
    }

    return time;
  }

  private void addStage(final String name, final long time) {
    if (this.checkPointMap.containsKey(name)) {
      throw new IllegalArgumentException("Check point name '" + name + "' already exists");
    }
    this.checkPointMap.put(name, time);
  }


  private void assertFinished() {
    if (isRunning()) {
      throw new IllegalStateException("Timer not finished yet");
    }
  }

  private void assertRunning() {
    if (!isRunning()) {
      throw new IllegalStateException("Timer already finished");
    }
  }

  /**
   * @return time taken in nano seconds
   */
  private long getTimeTakenNs() {
    return this.finishedAt - this.startedAt;
  }

  private long doFinish() {
    long now = System.nanoTime();

    assertRunning();

    this.finishedAt = now;

    return getTimeTakenNs();
  }

  /**
   * @param timeTakenNs time taken in nano seconds
   * @param opUnit
   * @return
   */
  private String formatTo(final long timeTakenNs) {
    return getTimeInHr(timeTakenNs);
  }


  private String getTimeInMillis(final long timeTaken) {
    return (timeTaken / CONVERTER_NANO_MILLI) + " " + UNIT_MILLIS;
  }

  /**
   * @param timeTaken
   * @return
   */
  private String getTimeInSec(final long timeTaken) {
    long ttMs = timeTaken / CONVERTER_NANO_MILLI;
    long ttSec = ttMs / CONVERTER_MILLI_TO_SEC;
    if (ttSec == 0L) {
      return getTimeInMillis(timeTaken);
    }
    return ttSec + UNIT_SECS + " " + (ttMs % CONVERTER_MILLI_TO_SEC) + UNIT_MILLIS;
  }

  /**
   * @param timeTaken
   * @return
   */
  private String getTimeInMin(final long timeTaken) {
    long ttSec = timeTaken / CONVERTER_NANO_SEC;
    long ttMin = ttSec / CONVERTER_SEC_MIN_HR;
    if (ttMin == 0L) {
      return getTimeInSec(timeTaken);
    }
    return ttMin + TIME_SEP + (ttSec % CONVERTER_SEC_MIN_HR) + " " + UNIT_MINS;
  }

  /**
   * @param timeTaken
   * @return
   */
  private String getTimeInHr(final long timeTaken) {
    long ttSec = timeTaken / CONVERTER_NANO_SEC;
    long ttHr = ttSec / CONVERTER_SEC_HR;
    if (ttHr == 0L) {
      return getTimeInMin(timeTaken);
    }

    // Remaining seconds
    long remSec = ttSec % CONVERTER_SEC_HR;

    return ttHr + TIME_SEP + (remSec / CONVERTER_SEC_MIN_HR) + TIME_SEP + (ttSec % CONVERTER_SEC_MIN_HR) + " " +
        UNIT_HRS;
  }


  private long getCheckPointTimeDiffNanos(final String checkPoint1, final String checkPoint2) {
    if (CommonUtils.isEqual(checkPoint1, checkPoint2)) {
      throw new IllegalArgumentException("Both inputs are same");
    }

    return getCheckPointTimeAbsoluteNanos(checkPoint1) - getCheckPointTimeAbsoluteNanos(checkPoint2);
  }


  private long getCheckPointTimeFromStartNanos(final String name) {
    return getCheckPointTimeAbsoluteNanos(name) - this.startedAt;
  }

}
