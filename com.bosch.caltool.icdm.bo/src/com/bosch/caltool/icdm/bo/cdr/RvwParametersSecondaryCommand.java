/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr;

import java.util.HashSet;
import java.util.Set;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwParameter;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwParametersSecondary;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwResultsSecondary;
import com.bosch.caltool.icdm.model.cdr.RvwParametersSecondary;


/**
 * Command class for Review Parameters Secondary
 *
 * @author bru2cob
 */
public class RvwParametersSecondaryCommand
    extends AbstractCommand<RvwParametersSecondary, RvwParametersSecondaryLoader> {


  /**
   * Constructor
   *
   * @param input input data
   * @param isUpdate if true, update, else create
   * @param isDelete if true, delete mode, else as per 'isUpdate' parameter
   * @param serviceData service Data
   * @throws IcdmException error when initializing
   */
  public RvwParametersSecondaryCommand(final ServiceData serviceData, final RvwParametersSecondary input,
      final boolean isUpdate, final boolean isDelete) throws IcdmException {
    super(serviceData, input, new RvwParametersSecondaryLoader(serviceData),
        isDelete ? COMMAND_MODE.DELETE : (isUpdate ? COMMAND_MODE.UPDATE : COMMAND_MODE.CREATE));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {
    TRvwParametersSecondary entity = new TRvwParametersSecondary();

    TRvwParameter paramEntity =
        new CDRResultParameterLoader(getServiceData()).getEntityObject(getInputData().getRvwParamId());
    entity.setTRvwParameter(paramEntity);
    Set<TRvwParametersSecondary> tRvwParametersSecondaries2 = paramEntity.getTRvwParametersSecondaries();
    if (null == tRvwParametersSecondaries2) {
      tRvwParametersSecondaries2 = new HashSet<>();
    }
    tRvwParametersSecondaries2.add(entity);
    paramEntity.setTRvwParametersSecondaries(tRvwParametersSecondaries2);
    TRvwResultsSecondary secResultsEntity =
        new RvwResultsSecondaryLoader(getServiceData()).getEntityObject(getInputData().getSecReviewId());
    entity.setTRvwResultsSecondary(secResultsEntity);
    Set<TRvwParametersSecondary> tRvwParametersSecondaries = secResultsEntity.getTRvwParametersSecondaries();
    if (null == tRvwParametersSecondaries) {
      tRvwParametersSecondaries = new HashSet<>();
    }
    tRvwParametersSecondaries.add(entity);
    secResultsEntity.setTRvwParametersSecondaries(tRvwParametersSecondaries);
    entity.setLowerLimit(getInputData().getLowerLimit());
    entity.setUpperLimit(getInputData().getUpperLimit());
    entity.setRefValue(getInputData().getRefValue());
    entity.setRefUnit(getInputData().getRefUnit());
    entity.setResult(getInputData().getResult());
    entity.setChangeFlag(getInputData().getChangeFlag());
    entity.setMatchRefFlag(getInputData().getMatchRefFlag());
    entity.setBitwiseLimit(getInputData().getBitwiseLimit());
    entity.setIsbitwise(getInputData().getIsbitwise());
    entity.setLabObjId(getInputData().getLabObjId());
    entity.setRevId(getInputData().getRevId());
    entity.setRvwMethod(getInputData().getRvwMethod());

    setUserDetails(COMMAND_MODE.CREATE, entity);

    persistEntity(entity);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void update() throws IcdmException {
    // TODO Auto-generated method stub
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void delete() throws IcdmException {
    RvwParametersSecondaryLoader loader = new RvwParametersSecondaryLoader(getServiceData());
    TRvwParametersSecondary entity = loader.getEntityObject(getInputData().getId());
    entity.getTRvwParameter().getTRvwParametersSecondaries().remove(entity);
    entity.getTRvwResultsSecondary().getTRvwParametersSecondaries().remove(entity);
    getEm().remove(entity);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() throws IcdmException {
    // TODO Auto-generated method stub
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean dataChanged() throws IcdmException {
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean hasPrivileges() throws IcdmException {
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void validateInput() throws IcdmException {
    // TODO Auto-generated method stub
  }

}
