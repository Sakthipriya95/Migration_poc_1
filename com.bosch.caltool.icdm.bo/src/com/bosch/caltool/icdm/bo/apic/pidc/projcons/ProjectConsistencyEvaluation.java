/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.apic.pidc.projcons;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttribute;
import com.bosch.caltool.icdm.database.entity.apic.TabvProjectidcard;


/**
 * Check consistency of Project ID Cards
 *
 * @author bne4cob
 */
public class ProjectConsistencyEvaluation implements IValidator<IValidationResult> {

  /**
   * Entity Manager factory
   */
  private final EntityManagerFactory emf;

  /**
   * Validation results
   */
  private final SortedSet<IValidationResult> valResultSet = new TreeSet<>();

  /**
   * Logger
   */
  private final ILoggerAdapter logger;

  /**
   * Map of structure attributes
   */
  private final ConcurrentMap<Long, String> strAttrMap = new ConcurrentHashMap<>();

  /**
   * User input, projects against which consistency to be evaluated.
   */
  private final Long[] inputProjectIDs;

  /**
   * Constructor, to evaluate consistency
   *
   * @param emf EntityManagerFactory
   * @param logger ILoggerAdapter
   * @param projectIDs list of project IDs. If not given, the entire projects in the database will be analysed
   */
  public ProjectConsistencyEvaluation(final EntityManagerFactory emf, final ILoggerAdapter logger,
      final Long... projectIDs) {

    this.emf = emf;
    this.logger = logger;
    this.inputProjectIDs = projectIDs;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void validate() {

    this.logger.info("Project consistency evaluation started");

    findStuctureAttrs();
    EntityManager entMgr = null;

    try {
      // Use an independent entity manager for this activity
      entMgr = this.emf.createEntityManager();

      List<TabvProjectidcard> dbPidcList = getDbPidcList(entMgr);

      int totalPidc = dbPidcList.size();
      this.logger.info("Number of PID cards to evaluate = {}", totalPidc);

      // For logging
      int counter = 0;

      for (TabvProjectidcard dbpidc : dbPidcList) {
        PidcEvaluator pidcEval = new PidcEvaluator(dbpidc, this.strAttrMap);
        // The evaluation of children will be triggered by the parent.
        pidcEval.validate();

        counter++;
        this.logger.info("PID Card({}/{}): ID = {}; Number of problems found = {}", counter, totalPidc,
            dbpidc.getProjectId(), pidcEval.getResult().size());

        this.valResultSet.addAll(pidcEval.getResult());
      }

    }
    finally {
      if (entMgr != null) {
        entMgr.close();
      }
    }

    // If the collection valResultSet is empty, then there are no inconsistencies
    if (this.valResultSet.isEmpty()) {
      this.logger.info("Project consistency evaluation completed. No inconsistencies found");
    }
    else {
      this.logger.warn("Project consistency evaluation completed. Inconsistencies found = " + this.valResultSet.size());
    }

  }

  /**
   * Find the project entities
   *
   * @param entMgr entity manager
   * @return list of project entities
   */
  private List<TabvProjectidcard> getDbPidcList(final EntityManager entMgr) {
    List<TabvProjectidcard> dbPidcList;

    if ((this.inputProjectIDs == null) || (this.inputProjectIDs.length == 0)) {
      // Start with all project ID Cards
      TypedQuery<TabvProjectidcard> query =
          entMgr.createNamedQuery(TabvProjectidcard.NQ_GET_ALL_PIDCS, TabvProjectidcard.class);

      dbPidcList = query.getResultList();
    }
    else {
      dbPidcList = new ArrayList<>();
      for (Long projectID : this.inputProjectIDs) {
        TabvProjectidcard dbPidc = entMgr.find(TabvProjectidcard.class, projectID);
        if (dbPidc == null) {
          throw new IllegalArgumentException("Invalid project ID - " + projectID);
        }
        dbPidcList.add(dbPidc);
      }
    }
    return dbPidcList;
  }

  /**
   * Find structure attributes
   */
  private void findStuctureAttrs() {
    EntityManager entMgr = this.emf.createEntityManager();
    TypedQuery<TabvAttribute> query =
        entMgr.createNamedQuery(TabvAttribute.NQ_GET_PROJ_STRUCT_ATTRS, TabvAttribute.class);

    final List<TabvAttribute> dbAttrList = query.getResultList();
    for (TabvAttribute dbattr : dbAttrList) {
      this.strAttrMap.put(dbattr.getAttrId(), dbattr.getAttrNameEng());
    }

    entMgr.close();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SortedSet<IValidationResult> getResult() {
    return this.valResultSet;
  }

  /**
   * Log the results to the Logger. This will align the error inputs and messages to separate columns for easy
   * readability, copying to excel etc. The output is tab separated.
   */
  public void logResults() {
    ProjConsEvalLoggerReport rprt = new ProjConsEvalLoggerReport(this.logger, this.valResultSet);
    rprt.createReport();
  }


  /**
   * @return the logger
   */
  final ILoggerAdapter getLogger() {
    return this.logger;
  }
}
