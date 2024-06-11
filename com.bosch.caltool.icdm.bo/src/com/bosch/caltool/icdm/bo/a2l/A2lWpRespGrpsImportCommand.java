/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.a2l;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import com.bosch.caltool.dmframework.bo.AbstractSimpleCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcA2lLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lGroup;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lWpDefnVersion;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lWpParamMapping;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lWpResponsibility;
import com.bosch.caltool.icdm.database.entity.apic.TPidcA2l;
import com.bosch.caltool.icdm.model.a2l.A2lWpDefnVersion;
import com.bosch.caltool.icdm.model.a2l.ImportA2lWpRespGrpsResponse;

/**
 * @author and4cob
 */
public class A2lWpRespGrpsImportCommand extends AbstractSimpleCommand {

  private final ImportA2lWpRespGrpsResponse response;
  private final Long wpDefVersId;
  private Long pidcA2lId;

  /**
   * @param serviceData ServiceData
   * @param wpDefVersId work package definition version ID
   * @param response ImportA2lWpRespGrpsResponse
   * @throws IcdmException service error
   */
  public A2lWpRespGrpsImportCommand(final ServiceData serviceData, final Long wpDefVersId,
      final ImportA2lWpRespGrpsResponse response) throws IcdmException {
    super(serviceData);
    this.wpDefVersId = wpDefVersId;
    this.response = response;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void execute() throws IcdmException {

    A2lWpDefnVersion a2lWpDefnVersion =
        new A2lWpDefnVersionLoader(getServiceData()).getDataObjectByID(this.wpDefVersId);
    if (!a2lWpDefnVersion.isWorkingSet()) {
      throw new IcdmException("Import is possible only in Working Set WP Definition version.");
    }

    final Query query = getEm().createNamedQuery(TA2lGroup.NNP_IMPORT_WP_RESP_GRPS_FROM_A2L);
    this.pidcA2lId = a2lWpDefnVersion.getPidcA2lId();
    query.setParameter(1, this.pidcA2lId);
    query.setParameter(2, getServiceData().getUsername());
    query.executeUpdate();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() throws IcdmException {
    refreshEntity();
  }

  /**
   * @throws DataException
   */
  private void refreshEntity() throws DataException {

    TPidcA2l tPidcA2l = (new PidcA2lLoader(getServiceData())).getEntityObject(this.pidcA2lId);
    getEm().refresh(tPidcA2l);

    TA2lWpDefnVersion entity = new A2lWpDefnVersionLoader(getServiceData()).getEntityObject(this.wpDefVersId);
    getEm().refresh(entity);


    TypedQuery<TA2lWpResponsibility> typedQuery =
        getEm().createNamedQuery(TA2lWpResponsibility.GET_MAPPINGS_FROM_A2L, TA2lWpResponsibility.class);
    typedQuery.setParameter("wpdefid", this.wpDefVersId);


    for (TA2lWpResponsibility ta2lWpResponsibility : typedQuery.getResultList()) {

      for (TA2lWpParamMapping ta2lWpParamMapping : ta2lWpResponsibility.getTA2lWpParamMappings()) {
        ta2lWpParamMapping.getTA2lResponsibility();
        ta2lWpParamMapping.getTA2lWpResponsibility();
        ta2lWpParamMapping.getTParameter();
        getEm().refresh(ta2lWpParamMapping);
        this.response.getA2lWpParamMappingSet()
            .add(new A2lWpParamMappingLoader(getServiceData()).createDataObject(ta2lWpParamMapping));
      }

      this.response.getRespSet().add(
          new A2lResponsibilityLoader(getServiceData()).createDataObject(ta2lWpResponsibility.getA2lResponsibility()));
      this.response.getWrkPkgSet()
          .add(new A2lWorkPackageLoader(getServiceData()).createDataObject(ta2lWpResponsibility.getA2lWp()));
      this.response.getWpRespPalSet()
          .add(new A2lWpResponsibilityLoader(getServiceData()).createDataObject(ta2lWpResponsibility));
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean hasPrivileges() throws IcdmException {
    return true;
  }

}
