/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.rcputils.jobs;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;


/**
 * Abstraction of child jobs
 * 
 * @author bne4cob
 */
public abstract class AbstractChildJob extends Job {

  /**
   * Total effort
   */
  public static final int JOB_TOTAL = 100;
  /**
   * Effort at start
   */
  public static final int JOB_BEGIN = 10;
  /**
   * Effort to add when finished
   */
  public static final int JOB_ADD_END = 90;

  /**
   * Unique identifier for this Job Family
   */
  private ChildJobFamily jobFamily;

  /**
   * @param name name of the job
   */
  public AbstractChildJob(final String name) {
    super("(Child Job) " + name);
  }

  /**
   * @param jobFamily the job Family to set
   */
  void setJobFamily(final ChildJobFamily jobFamily) {
    this.jobFamily = jobFamily;
  }

  /**
   * @return the job Family
   */
  public ChildJobFamily getJobFamily() {
    return this.jobFamily;
  }

  /**
   * If the family ID of this job and the other job are same, they belong to the same job family
   * <p>
   * {@inheritDoc}
   */
  @Override
  public boolean belongsTo(final Object otherFamilyID) {
    // Here the other object is the family ID as given in join(), sleep(), wakeUp() operations etc.,
    // not the family object.
    return this.jobFamily.getJobFamilyID().equals(otherFamilyID);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected IStatus run(final IProgressMonitor monitor) {
    //ICDM-2608
    IStatus status = doRun(monitor);
    return status;

  }

  /**
   * @return the parent job
   */
  protected final Job getParent() {
    return getJobFamily().getParent();
  }

  /**
   * Steps to run this job
   * 
   * @param monitor progress monitor
   * @return IStatus
   */
  protected abstract IStatus doRun(IProgressMonitor monitor);


}
