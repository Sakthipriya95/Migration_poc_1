/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.BeforeClass;
import org.junit.Test;

import com.bosch.calcomp.junittestframework.JUnitTest;


/**
 * @author BNE4COB
 */
public class TimerTest extends JUnitTest {

  private static final String CP1_COMMON_TIMER = "Check point 1";
  private static final String CP2_COMMON_TIMER = "Check point 2";
  private static final String CP_INVALID = "Invalid CP";

  private static final String OP_PATTERN_MILLIS = "\\d+ ms";
  private static final String OP_PATTERN_SECS = "\\d+s \\d+ms";
  private static final String OP_PATTERN_MINS = "\\d+:\\d+ mins";

  private static final String ASSERT_MSG_TIMER_FINISHED = "Check finish() output";
  private static final String ASSERT_MSG_STOPPED_AT_AFTER_FINISHED = "Stopped at after finished";
  private static final String ASSERT_MSG_TIMER_TIME_TAKEN = "Check getTimeTaken() output";
  private static final String ASSERT_MSG_CHECK_POINT_TIME = "Verify Check point time";

  private static final String EXMSG_TIMER_NOT_FINISHED = "Timer not finished yet";
  private static final String EXMSG_INVALID_CHECK_POINT_NAME = "Invalid check point name 'Invalid CP'";

  private static final String LOG_TIMER_TIMETAKEN_OUTPUT = "Timer.getTimeTaken() output : {}";
  private static final String LOG_TIMER_STR = "Timer : {}";
  private static final String LOG_TIMER_FINISH_OUTPUT = "Timer.finish() output : {}";
  private static final String LOG_TIMER_ELAPSED_OUTPUT = "Timer.getElapsedTime() output : {}";

  private static Timer commonTestTimer;

  /**
   * common test timer
   */
  @BeforeClass
  public static void createCommonTestTimer() {
    commonTestTimer = new Timer();
    TESTER_LOGGER.debug(LOG_TIMER_STR, commonTestTimer);

    // Add some check points
    doActivity(1000 * 20);
    commonTestTimer.addCheckPoint();
    TESTER_LOGGER.debug("Adding check point 1 (name: default)");

    doActivity(1000 * 20);
    TESTER_LOGGER.debug("Adding check point 2 (name: default)");
    commonTestTimer.addCheckPoint();

    doActivity(1000 * 20);
    TESTER_LOGGER.debug("Adding check point 3 (name: Almost finished)");
    commonTestTimer.addCheckPoint("Almost finished");

    doActivity(1000 * 20);

    String timeTaken = commonTestTimer.finish();

    TESTER_LOGGER.debug(LOG_TIMER_FINISH_OUTPUT, timeTaken);
    TESTER_LOGGER.debug(LOG_TIMER_STR, commonTestTimer);

    TESTER_LOGGER.debug("Common timer's time taken output : ");
    TESTER_LOGGER.debug("  getTimeTakenMs() \t: {}", commonTestTimer.getTimeTakenMs());
    TESTER_LOGGER.debug("  getTimeTaken() \t: {}", commonTestTimer.getTimeTaken());
  }

  /**
   * Test method for {@link com.bosch.caltool.icdm.common.util.Timer#isRunning()}.
   */
  @Test
  public void testIsRunningBeforeFinish() {
    Timer timer = new Timer();
    TESTER_LOGGER.debug(LOG_TIMER_STR, timer);

    doActivity();

    boolean running = timer.isRunning();
    TESTER_LOGGER.debug("Running at before finish() call : {}", running);

    assertTrue("Running check", running);
  }

  /**
   * Test method for {@link com.bosch.caltool.icdm.common.util.Timer#isRunning()}.
   */
  @Test
  public void testIsRunningAfterFinish() {
    Timer timer = new Timer();
    TESTER_LOGGER.debug(LOG_TIMER_STR, timer);

    doActivity();

    timer.finish();

    boolean running = timer.isRunning();
    TESTER_LOGGER.debug("Running at before finish() call : {}", running);

    assertFalse("Running check", running);
  }

  /**
   * Test method for {@link com.bosch.caltool.icdm.common.util.Timer#reset()}.
   */
  @Test
  public void testResetBeforeFinish() {
    Timer timer = new Timer();
    TESTER_LOGGER.debug(LOG_TIMER_STR, timer);

    doActivity();
    timer.addCheckPoint();
    doActivity();

    assertTrue("Running before reset", timer.isRunning());

    timer.reset();

    TESTER_LOGGER.debug(LOG_TIMER_STR, timer);

    assertTrue("Running after reset", timer.isRunning());
    assertEquals("Checkpoints cleared", 0, timer.getCheckPointCount());
  }

  /**
   * Test method for {@link com.bosch.caltool.icdm.common.util.Timer#reset()}.
   */
  @Test
  public void testResetAfterFinish() {
    Timer timer = new Timer();
    TESTER_LOGGER.debug(LOG_TIMER_STR, timer);

    doActivity();

    timer.finish();

    assertFalse("Running before reset", timer.isRunning());

    timer.reset();

    TESTER_LOGGER.debug(LOG_TIMER_STR, timer);

    assertTrue("Stopped at after reset", timer.isRunning());
  }

  /**
   * Test method for {@link com.bosch.caltool.icdm.common.util.Timer#getElapsedTime() }.
   */
  @Test
  public void testGetElapsedTime() {
    Timer timer = new Timer();
    TESTER_LOGGER.debug(LOG_TIMER_STR, timer);

    doActivity();

    String timeTakenStr = timer.getElapsedTime();

    TESTER_LOGGER.debug(LOG_TIMER_STR, timer);
    TESTER_LOGGER.debug(LOG_TIMER_ELAPSED_OUTPUT, timeTakenStr);

    assertTrue("Elapsed time", isMatch(timeTakenStr, OP_PATTERN_MILLIS));
    assertTrue("Not stopped after elapsed time call", timer.isRunning());
  }

  /**
   * Test method for {@link com.bosch.caltool.icdm.common.util.Timer#getElapsedTime() }.
   */
  @Test
  public void testGetElapsedTimeFinished() {
    Timer timer = new Timer();
    TESTER_LOGGER.debug(LOG_TIMER_STR, timer);

    doActivity();
    String finishedTimeStr = timer.finish();
    String elapsedTimeStr = timer.getElapsedTime();

    TESTER_LOGGER.debug(LOG_TIMER_STR, timer);
    TESTER_LOGGER.debug("Elapsed time : {}, finished time : {}", elapsedTimeStr, finishedTimeStr);

    assertTrue("Elapsed time matches", isMatch(elapsedTimeStr, OP_PATTERN_MILLIS));
    assertEquals("Elapsed time same as finished time", elapsedTimeStr, finishedTimeStr);
  }

  /**
   * Test method for {@link com.bosch.caltool.icdm.common.util.Timer#getElapsedTimeMs() }.
   */
  @Test
  public void testGetElapsedTimeMs() {
    Timer timer = new Timer();
    TESTER_LOGGER.debug(LOG_TIMER_STR, timer);

    doActivity();

    long timeTaken = timer.getElapsedTimeMs();

    TESTER_LOGGER.debug(LOG_TIMER_STR, timer);
    TESTER_LOGGER.debug(LOG_TIMER_ELAPSED_OUTPUT, timeTaken);

    assertTrue("Elapsed time", timeTaken > 0L);
    assertTrue("Not stopped after elapsed time call", timer.isRunning());
  }

  /**
   * Test method for {@link com.bosch.caltool.icdm.common.util.Timer#finish()}.
   */
  @Test
  public void testFinish() {
    Timer timer = new Timer();
    TESTER_LOGGER.debug(LOG_TIMER_STR, timer);

    doActivity();

    String timeTakenStr = timer.finish();
    TESTER_LOGGER.debug(LOG_TIMER_STR, timer);
    TESTER_LOGGER.debug(LOG_TIMER_FINISH_OUTPUT, timeTakenStr);

    assertTrue(ASSERT_MSG_TIMER_FINISHED, isMatch(timeTakenStr, OP_PATTERN_MILLIS));
    assertFalse(ASSERT_MSG_STOPPED_AT_AFTER_FINISHED, timer.isRunning());
  }

  /**
   * Test method for {@link com.bosch.caltool.icdm.common.util.Timer#finish()}.
   */
  @Test
  public void testFinishNegativeMultipleFinishCall() {
    Timer timer = new Timer();
    TESTER_LOGGER.debug(LOG_TIMER_STR, timer);

    doActivity();

    timer.finish();
    TESTER_LOGGER.debug(LOG_TIMER_STR, timer);

    this.thrown.expect(IllegalStateException.class);
    this.thrown.expectMessage("Timer already finished");
    timer.finish();
  }

  /**
   * Test method for {@link com.bosch.caltool.icdm.common.util.Timer#getTimeTakenMs()}.
   */
  @Test
  public void testGetTimeTakenMs() {
    Timer timer = new Timer();
    TESTER_LOGGER.debug(LOG_TIMER_STR, timer);

    doActivity();

    String timeTakenStr = timer.finish();
    TESTER_LOGGER.debug(LOG_TIMER_STR, timer);
    TESTER_LOGGER.debug(LOG_TIMER_FINISH_OUTPUT, timeTakenStr);

    long timeTaken = timer.getTimeTakenMs();
    TESTER_LOGGER.debug(LOG_TIMER_TIMETAKEN_OUTPUT, timeTaken);
    assertTrue(ASSERT_MSG_TIMER_TIME_TAKEN, isMatch(timeTakenStr, OP_PATTERN_MILLIS));
  }

  /**
   * Test method for {@link com.bosch.caltool.icdm.common.util.Timer#getTimeTaken()}.
   */
  @Test
  public void testGetTimeTaken() {
    Timer timer = new Timer();
    TESTER_LOGGER.debug(LOG_TIMER_STR, timer);

    doActivity();

    String timeTakenStr = timer.finish();
    TESTER_LOGGER.debug(LOG_TIMER_STR, timer);
    TESTER_LOGGER.debug(LOG_TIMER_FINISH_OUTPUT, timeTakenStr);

    String timeTaken = timer.getTimeTaken();
    TESTER_LOGGER.debug(LOG_TIMER_TIMETAKEN_OUTPUT, timeTaken);
    assertTrue(ASSERT_MSG_TIMER_TIME_TAKEN, isMatch(timeTakenStr, OP_PATTERN_MILLIS));
  }

  /**
   * Test method for {@link com.bosch.caltool.icdm.common.util.Timer#getTimeTaken()}.
   */
  @Test
  public void testGetTimeTakenMins() {
    String timeTakenStr = commonTestTimer.getTimeTaken();
    TESTER_LOGGER.debug(LOG_TIMER_TIMETAKEN_OUTPUT, timeTakenStr);
    assertTrue(ASSERT_MSG_TIMER_TIME_TAKEN, isMatch(timeTakenStr, OP_PATTERN_MINS));
  }

  /**
   * Test method for {@link com.bosch.caltool.icdm.common.util.Timer#getTimeTaken()}.
   */
  @Test
  public void testGetTimeTakenNegativeBeforeFinished() {
    Timer timer = new Timer();
    TESTER_LOGGER.debug(LOG_TIMER_STR, timer);

    doActivity();

    this.thrown.expect(IllegalStateException.class);
    this.thrown.expectMessage(EXMSG_TIMER_NOT_FINISHED);

    timer.getTimeTaken();
  }

  /**
   * Test method for {@link com.bosch.caltool.icdm.common.util.Timer#addCheckPoint(String) }.
   */
  @Test
  public void testAddCheckPointNegativeDuplicateName() {
    Timer timer = new Timer();
    TESTER_LOGGER.debug(LOG_TIMER_STR, timer);

    doActivity();

    timer.addCheckPoint("CP 1");

    this.thrown.expect(IllegalArgumentException.class);
    this.thrown.expectMessage("Check point name 'CP 1' already exists");

    timer.addCheckPoint("CP 1");
  }

  /**
   * Test method for {@link com.bosch.caltool.icdm.common.util.Timer#getCheckPointTimeFromStart(String) }.
   */
  @Test
  public void testGetCheckPointTimeFromStart() {
    Timer timer = new Timer();

    doActivity();

    timer.addCheckPoint();

    String checkPointTime = timer.getCheckPointTimeFromStart(CP1_COMMON_TIMER);

    TESTER_LOGGER.debug("Check point time : {}", checkPointTime);
    assertTrue(ASSERT_MSG_CHECK_POINT_TIME, isMatch(checkPointTime, OP_PATTERN_MILLIS));
  }

  /**
   * Test method for {@link com.bosch.caltool.icdm.common.util.Timer#getCheckPointTimeFromStart(String) }.
   */
  @Test
  public void testGetCheckPointTimeFromStartNegativeInvalidName() {
    this.thrown.expect(IllegalArgumentException.class);
    this.thrown.expectMessage(EXMSG_INVALID_CHECK_POINT_NAME);

    commonTestTimer.getCheckPointTimeFromStart(CP_INVALID);
  }

  /**
   * Test method for {@link com.bosch.caltool.icdm.common.util.Timer#getCheckPointTimeFromStart(String) }.
   */
  @Test
  public void testGetCheckPointTimeFromStartMs() {
    Timer timer = new Timer();

    doActivity();

    timer.addCheckPoint();

    long checkPointTime = timer.getCheckPointTimeFromStartMs(CP1_COMMON_TIMER);

    TESTER_LOGGER.debug("Check point time : {}", checkPointTime);
    assertTrue(ASSERT_MSG_CHECK_POINT_TIME, checkPointTime > 0L);
  }

  /**
   * Test method for {@link com.bosch.caltool.icdm.common.util.Timer#getCheckPointTimeDiff(String, String)}.
   */
  @Test
  public void testGetCheckPointTimeDiff() {
    String checkPointTime = commonTestTimer.getCheckPointTimeDiff(CP2_COMMON_TIMER, CP1_COMMON_TIMER);

    TESTER_LOGGER.debug("Check point time diff : {}", checkPointTime);
    assertTrue(ASSERT_MSG_CHECK_POINT_TIME, isMatch(checkPointTime, OP_PATTERN_SECS));
  }

  /**
   * Test method for {@link com.bosch.caltool.icdm.common.util.Timer#getCheckPointTimeDiff(String, String)}.
   */
  @Test
  public void testGetCheckPointTimeDiffNegativeSameName() {
    this.thrown.expect(IllegalArgumentException.class);
    this.thrown.expectMessage("Both inputs are same");

    commonTestTimer.getCheckPointTimeDiff(CP1_COMMON_TIMER, CP1_COMMON_TIMER);
  }

  /**
   * Test method for {@link com.bosch.caltool.icdm.common.util.Timer#getCheckPointTimeDiff(String, String) }.
   */
  @Test
  public void testGetCheckPointTimeDiffInvalidName1() {
    this.thrown.expect(IllegalArgumentException.class);
    this.thrown.expectMessage(EXMSG_INVALID_CHECK_POINT_NAME);

    commonTestTimer.getCheckPointTimeDiff(CP_INVALID, CP1_COMMON_TIMER);
  }

  /**
   * Test method for {@link com.bosch.caltool.icdm.common.util.Timer#getCheckPointTimeDiff(String, String) }.
   */
  @Test
  public void testGetCheckPointTimeDiffInvalidName2() {
    this.thrown.expect(IllegalArgumentException.class);
    this.thrown.expectMessage(EXMSG_INVALID_CHECK_POINT_NAME);

    commonTestTimer.getCheckPointTimeDiff(CP2_COMMON_TIMER, CP_INVALID);
  }

  /**
   * Test method for {@link com.bosch.caltool.icdm.common.util.Timer#getCheckPointTimeDiffMs(String, String)}.
   */
  @Test
  public void testGetCheckPointTimeDiffMs() {
    long checkPointTime = commonTestTimer.getCheckPointTimeDiffMs(CP2_COMMON_TIMER, CP1_COMMON_TIMER);

    TESTER_LOGGER.debug("Check point time diff : {}", checkPointTime);
    assertTrue(ASSERT_MSG_CHECK_POINT_TIME, checkPointTime > 0L);
  }

  /**
   * Test method for {@link com.bosch.caltool.icdm.common.util.Timer#getCheckPointTime(String) }.
   */
  @Test
  public void testGetCheckPointTime1() {
    String checkPointTime = commonTestTimer.getCheckPointTime(CP1_COMMON_TIMER);

    TESTER_LOGGER.debug("Check point time specific for Check point 1 : {}", checkPointTime);
    assertTrue(ASSERT_MSG_CHECK_POINT_TIME, isMatch(checkPointTime, OP_PATTERN_SECS));
  }

  /**
   * Test method for {@link com.bosch.caltool.icdm.common.util.Timer#getCheckPointTime(String) }.
   */
  @Test
  public void testGetCheckPointTime2() {
    String checkPointTime = commonTestTimer.getCheckPointTime(CP2_COMMON_TIMER);

    TESTER_LOGGER.debug("Check point time specific for Check point 2 : {}", checkPointTime);
    assertTrue(ASSERT_MSG_CHECK_POINT_TIME, isMatch(checkPointTime, OP_PATTERN_SECS));
  }

  /**
   * Test method for {@link com.bosch.caltool.icdm.common.util.Timer#getCheckPointTime(String) }.
   */
  @Test
  public void testGetCheckPointTimeInvalidName() {
    this.thrown.expect(IllegalArgumentException.class);
    this.thrown.expectMessage(EXMSG_INVALID_CHECK_POINT_NAME);

    commonTestTimer.getCheckPointTime(CP_INVALID);
  }

  /**
   * Test method for {@link com.bosch.caltool.icdm.common.util.Timer#getCheckPointNames() }.
   */
  @Test
  public void testGetCheckPointNames() {
    List<String> cpNames = commonTestTimer.getCheckPointNames();
    TESTER_LOGGER.debug("Check points : {}", cpNames);

    assertFalse("Check points not empty", cpNames.isEmpty());
    assertTrue("Check point names contains a check point", cpNames.contains(CP1_COMMON_TIMER));
  }


  /**
   * Test method for {@link com.bosch.caltool.icdm.common.util.Timer#getCheckPointTimeMs(String) }.
   */
  @Test
  public void testGetCheckPointTimeMs() {
    long checkPointTime = commonTestTimer.getCheckPointTimeMs(CP1_COMMON_TIMER);

    TESTER_LOGGER.debug("Check point time specific for Check point 1 : {}", checkPointTime);
    assertTrue(ASSERT_MSG_CHECK_POINT_TIME, checkPointTime > 0L);
  }

  /**
   * Test method for {@link com.bosch.caltool.icdm.common.util.Timer#getCheckPointCount() }.
   */
  @Test
  public void testGetCheckPointCount() {
    int cpCount = commonTestTimer.getCheckPointCount();
    TESTER_LOGGER.debug("Checkpoint count = {}", cpCount);
    assertEquals("Check points", 3, cpCount);
  }

  /**
   * Test method for {@link com.bosch.caltool.icdm.common.util.Timer#getSummary() }.
   */
  @Test
  public void testGetSummary() {
    Timer timer = new Timer();
    doActivity();
    timer.finish();

    String summary = timer.getSummary();
    TESTER_LOGGER.debug(summary);

    assertNotNull("Summary not null", summary);
  }

  /**
   * Test method for {@link com.bosch.caltool.icdm.common.util.Timer#getSummary() }.
   */
  @Test
  public void testGetSummaryWithCheckpoints() {
    String summary = commonTestTimer.getSummary();

    TESTER_LOGGER.debug(summary);

    assertNotNull("Summary with check points not null", summary);
  }

  /**
   * Test method for {@link com.bosch.caltool.icdm.common.util.Timer#getSummary() }.
   */
  @Test
  public void testGetSummaryBeforeFinish() {
    Timer timer = new Timer();

    doActivity();
    timer.addCheckPoint();
    String summary = timer.getSummary();

    TESTER_LOGGER.debug(summary);

    assertNotNull("Summary before finish not null", summary);
  }


  /**
   *
   */
  private static void doActivity() {
    doActivity(100);
  }


  /**
   * @param timeFor in milli seconds
   */
  private static void doActivity(final int timeFor) {
    try {
      TESTER_LOGGER.debug("Sleeping for {} ms ...", timeFor);
      Thread.sleep(timeFor);
      TESTER_LOGGER.debug("  Woke up from sleep");
    }
    catch (InterruptedException e) {
      TESTER_LOGGER.warn(e.getMessage(), e);
      Thread.currentThread().interrupt();
    }
  }

  private boolean isMatch(final String text, final String patternStr) {
    Pattern pattern = Pattern.compile(patternStr);
    Matcher matcher = pattern.matcher(text);

    boolean matches = matcher.matches();
    TESTER_LOGGER.debug("Input text : {}, pattern : {} : match = {}", text, patternStr, matches);

    return matches;
  }

}
