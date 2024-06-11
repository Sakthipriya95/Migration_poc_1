/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.apic.pidc.projcons;

import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentMap;

import com.bosch.caltool.icdm.bo.apic.pidc.projcons.data.PidcConsInfo;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.database.entity.apic.TPidcVersion;
import com.bosch.caltool.icdm.database.entity.apic.TabvProjectidcard;


/**
 * Project consistency evaluation
 *
 * @author bne4cob
 */
public class PidcEvaluator implements IValidator<PidcConsInfo> {

  /**
   * Project entity
   */
  private final TabvProjectidcard dbPidc;

  /**
   * Resultset
   */
  private final SortedSet<PidcConsInfo> resultSet = new TreeSet<>();

  /**
   * map of structure attributes
   */
  private final ConcurrentMap<Long, String> strAttrMap;

  /**
   * Constructor
   *
   * @param dbpidc Project entity
   * @param strAttrMap map of structure attributes
   */
  public PidcEvaluator(final TabvProjectidcard dbpidc, final ConcurrentMap<Long, String> strAttrMap) {
    this.dbPidc = dbpidc;
    this.strAttrMap = strAttrMap;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void validate() {

    // Validate PIDC. Looks for projects without any version
    if (this.dbPidc.getTPidcVersions().isEmpty()) {
      PidcConsInfo error = new PidcConsInfo();
      error.setProjectID(this.dbPidc.getProjectId());
      error.setProjectName(this.dbPidc.getTabvAttrValue().getTextvalueEng());
      error.setErrorType(ErrorType.PIDC_NO_VERS);
      error.setErrorLevel(ErrorLevel.VERSION);

      error.setCreatedUserParent(this.dbPidc.getCreatedUser());
      error.setCreatedDateParent(ApicUtil.timestamp2calendar(this.dbPidc.getCreatedDate()));
      error.setLastModifiedUserParent(this.dbPidc.getModifiedUser());
      error.setLastModifiedDateParent(ApicUtil.timestamp2calendar(this.dbPidc.getModifiedDate()));

      this.resultSet.add(error);
    }

    // Validate the project versions
    for (TPidcVersion dbVersion : this.dbPidc.getTPidcVersions()) {
      PidcVersionEvaluator versEval = new PidcVersionEvaluator(dbVersion, this.strAttrMap);
      versEval.validate();
      this.resultSet.addAll(versEval.getResult());
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SortedSet<PidcConsInfo> getResult() {
    return new TreeSet<>(this.resultSet);
  }

}
