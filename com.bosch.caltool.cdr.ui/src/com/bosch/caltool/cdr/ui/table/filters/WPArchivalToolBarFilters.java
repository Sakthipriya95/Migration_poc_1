/*
 * Copyright (c) ETAS GmbH 2024. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.table.filters;

import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.WpArchival;

import ca.odell.glazedlists.matchers.Matcher;

/**
 *
 */
public class WPArchivalToolBarFilters {

  /**
   * In Progress WP Archival
   */
  private boolean inProgress = true;
  /**
   * Completed WP Archival
   */
  private boolean completed = true;
  /**
   * not Available WP Archival
   */
  private boolean notAvailable = true;
  /**
   * failed WP Archival
   */
  private boolean failed = true;

  /**
   * @return the inProgress
   */
  public boolean isInProgress() {
    return this.inProgress;
  }


  /**
   * @param completed the completed to set
   */
  public void setCompleted(final boolean completed) {
    this.completed = completed;
  }


  /**
   * @param notAvailable the notAvailable to set
   */
  public void setNotAvailable(final boolean notAvailable) {
    this.notAvailable = notAvailable;
  }


  /**
   * @param failed the failed to set
   */
  public void setFailed(final boolean failed) {
    this.failed = failed;
  }


  /**
   * @param inProgress the inProgress to set
   */
  public void setInProgress(final boolean inProgress) {
    this.inProgress = inProgress;
  }


  /**
   * @return the completed
   */
  public boolean isCompleted() {
    return this.completed;
  }


  /**
   * @return the notAvailable
   */
  public boolean isNotAvailable() {
    return this.notAvailable;
  }


  /**
   * @return the failed
   */
  public boolean isFailed() {
    return this.failed;
  }


  /**
   * @return
   */
  public Matcher getToolBarMatcher() {
    return new WPArchivalToolBarMatcher<>();
  }


  /**
   * @author mkl2cob
   * @param <E>
   */
  private class WPArchivalToolBarMatcher<E> implements Matcher<E> {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(final E arg0) {


      WpArchival cdrRes = ((WpArchival) arg0);
      return filterTypeStatusScope(cdrRes);

    }

    /**
     * @param cdrRes
     */
    private boolean filterTypeStatusScope(final WpArchival cdrRes) {

      return filterRvwStatus(cdrRes);
    }


    /**
     * @param cdrRes CDRResult
     * @return true if the row has to be displayed
     */
    private boolean filterRvwStatus1(final WpArchival cdrRes) {

      if (!WPArchivalToolBarFilters.this.completed &&
          CDRConstants.FILE_ARCHIVAL_STATUS.COMPLETED.getDbType().equals(cdrRes.getFileArchivalStatus())) {
        return false;
      }
      if (!WPArchivalToolBarFilters.this.inProgress &&
          CDRConstants.FILE_ARCHIVAL_STATUS.IN_PROGRESS.getDbType().equals(cdrRes.getFileArchivalStatus())) {
        return false;
      }
      if (!WPArchivalToolBarFilters.this.failed &&
          CDRConstants.FILE_ARCHIVAL_STATUS.FAILED.getDbType().equals(cdrRes.getFileArchivalStatus())) {
        return false;
      }
      if (!WPArchivalToolBarFilters.this.notAvailable &&
          CDRConstants.FILE_ARCHIVAL_STATUS.NOT_AVAILABLE.getDbType().equals(cdrRes.getFileArchivalStatus())) {
        return false;
      }
      return true;
    }

    /**
     * @param cdrRes CDRResult
     * @return true if the row has to be displayed
     */
    private boolean filterRvwStatus(final WpArchival cdrRes) {
      String fileArchivalStatus = cdrRes.getFileArchivalStatus();

      // Check if the current status should be displayed based on filter settings
      return !((!WPArchivalToolBarFilters.this.completed &&
          CDRConstants.FILE_ARCHIVAL_STATUS.COMPLETED.getDbType().equals(fileArchivalStatus)) ||
          (!WPArchivalToolBarFilters.this.inProgress &&
              CDRConstants.FILE_ARCHIVAL_STATUS.IN_PROGRESS.getDbType().equals(fileArchivalStatus)) ||
          (!WPArchivalToolBarFilters.this.failed &&
              CDRConstants.FILE_ARCHIVAL_STATUS.FAILED.getDbType().equals(fileArchivalStatus)) ||
          (!WPArchivalToolBarFilters.this.notAvailable &&
              CDRConstants.FILE_ARCHIVAL_STATUS.NOT_AVAILABLE.getDbType().equals(fileArchivalStatus)));
    }


  }

}
