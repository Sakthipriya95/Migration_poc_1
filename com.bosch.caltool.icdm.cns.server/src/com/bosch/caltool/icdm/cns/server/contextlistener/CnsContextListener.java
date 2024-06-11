/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.cns.server.contextlistener;

import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.icdm.cns.server.bo.CnsObjectStore;
import com.bosch.caltool.icdm.cns.server.bo.events.ChangeEventManager;
import com.bosch.caltool.icdm.cns.server.bo.session.SessionManager;


/**
 * @author bne4cob
 */
public class CnsContextListener implements ServletContextListener {

  private final boolean clearOldData = "Y".equals(CnsObjectStore.getServerProperty("CLEAR_OLD_DATA_YN"));

  /**
   * Job scheduler
   */
  private ScheduledExecutorService jobScheduler;

  @Override
  public void contextInitialized(final ServletContextEvent sce) {
    this.jobScheduler = Executors.newSingleThreadScheduledExecutor();

    long jobInterval = Long.parseLong(CnsObjectStore.getServerProperty("SESSION_CLEANUP_JOB_INTERVAL"));
    long jobStartDelay = Long.parseLong(CnsObjectStore.getServerProperty("SESSION_CLEANUP_JOB_START_DELAY"));

    // Run the job every minute
    this.jobScheduler.scheduleAtFixedRate(this::checkSessions, jobStartDelay, jobInterval, TimeUnit.SECONDS);

    getLogger().debug(
        "Session cleanup Job scheduled. Start Delay = {} seconds, Execution Interval = {} seconds, clear old data = {}",
        jobStartDelay, jobInterval, this.clearOldData);
  }

  private void checkSessions() {
    SessionManager.INSTANCE.checkUnusedSessions();
    if (this.clearOldData) {
      Instant olderThan = SessionManager.INSTANCE.getOldestSessionCreatedDate();
      ChangeEventManager.INSTANCE.clearOldEventData(olderThan);
    }
  }

  @Override
  public void contextDestroyed(final ServletContextEvent event) {
    this.jobScheduler.shutdownNow();
  }

  /**
   * @return logger
   */
  private ILoggerAdapter getLogger() {
    return CnsObjectStore.getLogger();
  }

}
