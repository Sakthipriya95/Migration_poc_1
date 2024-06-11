/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.rcputils.jobs;

import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;


/**
 * Child Job family. Manages execution of child jobs
 * 
 * @author bne4cob
 */
public class ChildJobFamily {

  /**
   * JOB family ID. A unique ID for this job family
   */
  private final String jobFamilyID = System.nanoTime() + "_" + (new Random()).nextInt(100);

  /**
   * Sub jobs in this family
   */
  // Implementation is linked hash set, to maintain the order of insertion and uniquenes
  private final Set<AbstractChildJob> childJobList = new LinkedHashSet<>();

  /**
   * Parent job
   */
  private final Job parent;

  /**
   * Constructor
   * 
   * @param parent parent Job
   */
  public ChildJobFamily(Job parent) {
    this.parent = parent;
  }


  /**
   * @return the parent
   */
  final Job getParent() {
    return parent;
  }


  /**
   * @return the jobFamilyID
   */
  public String getJobFamilyID() {
    return jobFamilyID;
  }

  /**
   * Add a new sub job to this family
   * 
   * @param childJob AbstractSubJob
   */
  public void add(final AbstractChildJob childJob) {
    childJob.setJobFamily(this);
    this.childJobList.add(childJob);

  }

  /**
   * Schedule all jobs in this family
   */
  public void schedule() {
    for (AbstractChildJob subJob : this.childJobList) {
      subJob.schedule();
    }
  }

  /**
   * Schedules all jobs and waits until completed. This is a combination of schedule() and join() methods.
   * 
   * @param monitor IProgressMonitor
   * @throws OperationCanceledException on operation cancel
   * @throws InterruptedException on interruption
   */
  public void execute(final IProgressMonitor monitor) throws OperationCanceledException, InterruptedException {
    schedule();
    join(monitor);
  }

  /**
   * Wait until all jobs in this family are completed
   * 
   * @param monitor IProgressMonitor
   * @throws OperationCanceledException on operation cancel
   * @throws InterruptedException on interruption
   */
  public void join(final IProgressMonitor monitor) throws OperationCanceledException, InterruptedException {
    Job.getJobManager().join(this.getJobFamilyID(), monitor);
  }


  /**
   * Sleep all pending jobs in this family
   */
  public void sleep() {
    Job.getJobManager().sleep(this.getJobFamilyID());
  }

  /**
   * Wake up all sleeping jobs, set to sleep mode by sleep() call.
   */
  public void wakeUp() {
    Job.getJobManager().wakeUp(this.getJobFamilyID());
  }


  /**
   * Checks whether all jobs have completed successfully. This method should be called only after invoking join()
   * 
   * @return true, if all jobs have returned Status.OK_STATUS
   */
  public boolean isResultOK() {
    boolean result = true;
    for (AbstractChildJob subJob : this.childJobList) {
      if (!Status.OK_STATUS.equals(subJob.getResult())) {
        result = false;
        break;
      }
    }
    return result;
  }

}
