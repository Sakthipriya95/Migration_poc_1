/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.table.filters;

import java.util.SortedSet;

import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.CDR_SOURCE_TYPE;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.REVIEW_STATUS;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.REVIEW_TYPE;
import com.bosch.caltool.icdm.model.cdr.ReviewResultData;
import com.bosch.caltool.icdm.model.cdr.ReviewVariantModel;

import ca.odell.glazedlists.matchers.Matcher;


/**
 * @author mkl2cob
 */
public class PIDCRwResToolBarFilters {

  /**
   * official review
   */
  private boolean official = true;
  /**
   * test review
   */
  private boolean test = true;
  /**
   * start review
   */
  private boolean start = true;
  /**
   * open review
   */
  private boolean open = true;
  /**
   * In Progress review
   */
  private boolean inProgress = true;
  /**
   * closed reviewed
   */
  private boolean close = true;
  /**
   * work package
   */
  private boolean wrkPckg = true;
  /**
   * group
   */
  private boolean group = true;
  /**
   * function
   */
  private boolean fun = true;
  /**
   * lab
   */
  private boolean lab = true;

  /**
   * MoniCa
   */
  // ICDM-2138
  private boolean monica = true;

  /**
   * reviewed files
   */
  private boolean reviewedFiles = true;
  /**
   * not defined
   */
  private boolean notDef = true;

  /**
   * show compli reviews
   */
  private boolean compliRvws = true;
  /**
   * latest review
   */
  private boolean latestRvw = true;
  /**
   * old reviews
   */
  private boolean oldRvws = true;

  /**
   * Show locked reviews
   */
  private boolean lockedRvws = true;

  /**
   * Show unlocked reviews
   */
  private boolean unlockedRvws = true;
  /**
   * input set
   */
  private final SortedSet<ReviewVariantModel> dataRvwRprtNatInputs;
  /**
   * show linked reviews
   */
  private boolean linkedRvws = true;
  /**
   * show not linked reviews
   */
  private boolean notLinkedRvws = true;

  /**
   * @author mkl2cob
   * @param <E>
   */
  private class RvwReportToolBarMatcher<E> implements Matcher<E> {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(final E arg0) {


      if (!filterRvwLink((ReviewVariantModel) arg0)) {
        return false;
      }
      ReviewResultData cdrRes = ((ReviewVariantModel) arg0).getReviewResultData();
      boolean filterTypeStatusScope = filterTypeStatusScope(cdrRes);
      if (!filterTypeStatusScope) {
        return false;
      }
      // ICDM-2078
      // Filter review lock status
      return filterRvwLockStatus(cdrRes);
    }

    /**
     * @param cdrRes
     */
    private boolean filterTypeStatusScope(final ReviewResultData cdrRes) {
      if (!filterRvwTpe(cdrRes)) {
        return false;
      }
      if (!filterRvwStatus(cdrRes)) {
        return false;
      }
      return filterRvwScope(cdrRes);
    }

    /**
     * @param arg0
     * @return
     */
    private boolean filterRvwLink(final ReviewVariantModel rvwVar) {
      if (!PIDCRwResToolBarFilters.this.linkedRvws && CommonUtils.isNotNull(rvwVar.getRvwVariant()) &&
          (rvwVar.getRvwVariant().isLinkedVariant())) {
        return false;
      }
      return PIDCRwResToolBarFilters.this.notLinkedRvws || CommonUtils.isNull(rvwVar.getRvwVariant()) ||
          rvwVar.getRvwVariant().isLinkedVariant();
    }

    /**
     * @param cdrRes CDRResult
     * @return true if the row has to be displayed
     */
    private boolean filterRvwScope(final ReviewResultData cdrRes) {
      if (!PIDCRwResToolBarFilters.this.wrkPckg &&
          (CDRConstants.CDR_SOURCE_TYPE.getType(cdrRes.getCdrReviewResult().getSourceType()).getUIType()
              .equalsIgnoreCase(CDR_SOURCE_TYPE.WORK_PACKAGE.getUIType()) ||
              CDRConstants.CDR_SOURCE_TYPE.getType(cdrRes.getCdrReviewResult().getSourceType()).getUIType()
                  .equalsIgnoreCase(CDR_SOURCE_TYPE.WP.getUIType()))) {
        return false;
      }
      if (!PIDCRwResToolBarFilters.this.group &&
          (CDRConstants.CDR_SOURCE_TYPE.getType(cdrRes.getCdrReviewResult().getSourceType()).getUIType()
              .equalsIgnoreCase(CDR_SOURCE_TYPE.GROUP.getUIType()))) {
        return false;
      }
      if (!PIDCRwResToolBarFilters.this.fun &&
          (CDRConstants.CDR_SOURCE_TYPE.getType(cdrRes.getCdrReviewResult().getSourceType()).getUIType()
              .equalsIgnoreCase(CDR_SOURCE_TYPE.FUN_FILE.getUIType()))) {
        return false;
      }
      if (!PIDCRwResToolBarFilters.this.lab &&
          (CDRConstants.CDR_SOURCE_TYPE.getType(cdrRes.getCdrReviewResult().getSourceType()).getUIType()
              .equalsIgnoreCase(CDR_SOURCE_TYPE.LAB_FILE.getUIType()))) {
        return false;
      }
      // ICDM-2138
      if (!PIDCRwResToolBarFilters.this.monica &&
          (CDRConstants.CDR_SOURCE_TYPE.getType(cdrRes.getCdrReviewResult().getSourceType()).getUIType()
              .equalsIgnoreCase(CDR_SOURCE_TYPE.MONICA_FILE.getUIType()))) {
        return false;
      }
      // ICDM-2138
      if (!PIDCRwResToolBarFilters.this.fun &&
          (CDRConstants.CDR_SOURCE_TYPE.getType(cdrRes.getCdrReviewResult().getSourceType()).getUIType()
              .equalsIgnoreCase(CDR_SOURCE_TYPE.A2L_FILE.getUIType()))) {
        return false;
      }
      if (!PIDCRwResToolBarFilters.this.reviewedFiles &&
          (CDRConstants.CDR_SOURCE_TYPE.getType(cdrRes.getCdrReviewResult().getSourceType()).getUIType()
              .equalsIgnoreCase(CDR_SOURCE_TYPE.REVIEW_FILE.getUIType()))) {
        return false;
      }
      if (!PIDCRwResToolBarFilters.this.notDef &&
          (CDRConstants.CDR_SOURCE_TYPE.getType(cdrRes.getCdrReviewResult().getSourceType()).getUIType()
              .equalsIgnoreCase(CDR_SOURCE_TYPE.NOT_DEFINED.getUIType()))) {
        return false;
      }

      return isCompliRvws() || !(CDRConstants.CDR_SOURCE_TYPE.getType(cdrRes.getCdrReviewResult().getSourceType())
          .getUIType().equalsIgnoreCase(CDR_SOURCE_TYPE.COMPLI_PARAM.getUIType()));
    }

    /**
     * @param cdrRes CDRResult
     * @return true if the row has to be displayed
     */
    private boolean filterRvwStatus(final ReviewResultData cdrRes) {
      if (!PIDCRwResToolBarFilters.this.open &&
          (CDRConstants.REVIEW_STATUS.getType(cdrRes.getCdrReviewResult().getRvwStatus()).toString()
              .equalsIgnoreCase(REVIEW_STATUS.OPEN.toString()))) {
        return false;
      }
      if (!PIDCRwResToolBarFilters.this.inProgress &&
          (CDRConstants.REVIEW_STATUS.getType(cdrRes.getCdrReviewResult().getRvwStatus()).toString()
              .equalsIgnoreCase(REVIEW_STATUS.IN_PROGRESS.toString()))) {
        return false;
      }
      return PIDCRwResToolBarFilters.this.close ||
          !(CDRConstants.REVIEW_STATUS.getType(cdrRes.getCdrReviewResult().getRvwStatus()).toString()
              .equalsIgnoreCase(REVIEW_STATUS.CLOSED.toString()));
    }

    /**
     * @param a2lParam
     * @return
     */
    private boolean filterRvwTpe(final ReviewResultData cdrRes) {
      if (!PIDCRwResToolBarFilters.this.official &&
          (CDRConstants.REVIEW_TYPE.getType(cdrRes.getCdrReviewResult().getReviewType()).toString()
              .equalsIgnoreCase(REVIEW_TYPE.OFFICIAL.toString()))) {
        return false;
      }
      if (!PIDCRwResToolBarFilters.this.test &&
          (CDRConstants.REVIEW_TYPE.getType(cdrRes.getCdrReviewResult().getReviewType()).toString()
              .equalsIgnoreCase(REVIEW_TYPE.TEST.toString()))) {
        return false;
      }
      return PIDCRwResToolBarFilters.this.start ||
          !(CDRConstants.REVIEW_TYPE.getType(cdrRes.getCdrReviewResult().getReviewType()).toString()
              .equalsIgnoreCase(REVIEW_TYPE.START.toString()));
    }


    /**
     * Filter for review lock status
     *
     * @param cdrRes CDR Result
     * @return true/false
     */
    private boolean filterRvwLockStatus(final ReviewResultData cdrRes) {
      if (!PIDCRwResToolBarFilters.this.lockedRvws &&
          (CDRConstants.REVIEW_LOCK_STATUS.getType(cdrRes.getCdrReviewResult().getLockStatus()).toString()
              .equalsIgnoreCase(CDRConstants.REVIEW_LOCK_STATUS.YES.toString()))) {
        return false;
      }
      return PIDCRwResToolBarFilters.this.unlockedRvws ||
          CDRConstants.REVIEW_LOCK_STATUS.getType(cdrRes.getCdrReviewResult().getLockStatus()).toString()
              .equalsIgnoreCase(CDRConstants.REVIEW_LOCK_STATUS.YES.toString());
    }

  }


  /**
   * @param dataRvwRprtNatInputs table data
   */
  public PIDCRwResToolBarFilters(final SortedSet<ReviewVariantModel> dataRvwRprtNatInputs) {
    this.dataRvwRprtNatInputs = dataRvwRprtNatInputs;
  }

  /**
   * @return Matcher
   */
  public Matcher<ReviewResultData> getToolBarMatcher() {
    return new RvwReportToolBarMatcher<>();
  }

  /**
   * @param official button state
   */
  public void setOfficial(final boolean official) {
    this.official = official;
  }

  /**
   * @param test button state
   */
  public void setTest(final boolean test) {
    this.test = test;
  }

  /**
   * @param start button state
   */
  public void setStart(final boolean start) {
    this.start = start;

  }

  /**
   * @param open button state
   */
  public void setOpen(final boolean open) {
    this.open = open;
  }

  /**
   * @param progress button state
   */
  public void setInProgress(final boolean progress) {
    this.inProgress = progress;

  }

  /**
   * @param close button state
   */
  public void setClose(final boolean close) {
    this.close = close;

  }

  /**
   * @param wrkPckg button state
   */
  public void setWrkPkg(final boolean wrkPckg) {
    this.wrkPckg = wrkPckg;
  }

  /**
   * @param group button state
   */
  public void setGroup(final boolean group) {
    this.group = group;
  }

  /**
   * @param fun button state
   */
  public void setFun(final boolean fun) {
    this.fun = fun;

  }

  /**
   * @param lab button state
   */
  public void setLab(final boolean lab) {
    this.lab = lab;
  }

  /**
   * @param monicaLocal button state
   */
  // ICDM-2138
  public void setMonica(final boolean monicaLocal) {
    this.monica = monicaLocal;
  }

  /**
   * @param reviewedFiles button state
   */
  public void setReviewedFiles(final boolean reviewedFiles) {
    this.reviewedFiles = reviewedFiles;
  }

  /**
   * @param notDef button state
   */
  public void setNotDef(final boolean notDef) {
    this.notDef = notDef;

  }

  /**
   * @param latestRvw button state
   */
  public void setLatestRvws(final boolean latestRvw) {
    this.latestRvw = latestRvw;

  }

  /**
   * @param oldRvws button state
   */
  public void setOldRvws(final boolean oldRvws) {
    this.oldRvws = oldRvws;

  }


  /**
   * @param lockedRvws the lockedRvws to set
   */
  public final void setLockedRvws(final boolean lockedRvws) {
    this.lockedRvws = lockedRvws;
  }


  /**
   * @param unlockedRvws the unlockedRvws to set
   */
  public final void setUnlockedRvws(final boolean unlockedRvws) {
    this.unlockedRvws = unlockedRvws;
  }

  /**
   * @param linkedRvws linkedRvws
   */
  public void setLinked(final boolean linkedRvws) {
    this.linkedRvws = linkedRvws;

  }

  /**
   * @param notLinkedRvws notLinkedRvws
   */
  public void setNotLinked(final boolean notLinkedRvws) {
    this.notLinkedRvws = notLinkedRvws;

  }


  /**
   * @return the compliRvws
   */
  public boolean isCompliRvws() {
    return this.compliRvws;
  }


  /**
   * @param compliRvws the compliRvws to set
   */
  public void setCompliRvws(final boolean compliRvws) {
    this.compliRvws = compliRvws;
  }
}
