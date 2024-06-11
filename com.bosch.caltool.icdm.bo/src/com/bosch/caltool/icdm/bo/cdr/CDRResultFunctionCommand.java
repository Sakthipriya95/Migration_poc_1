/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr;

import java.util.HashSet;
import java.util.Set;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.a2l.FunctionLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwFunction;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwResult;
import com.bosch.caltool.icdm.model.cdr.CDRResultFunction;


/**
 * Command class for CDR Result Function
 *
 * @author BRU2COB
 */
public class CDRResultFunctionCommand extends AbstractCommand<CDRResultFunction, CDRResultFunctionLoader> {


  /**
   * Constructor
   *
   * @param input input data
   * @param isUpdate if true, update, else create
   * @param isDelete if true, delete mode, else as per 'isUpdate' parameter
   * @param serviceData service Data
   * @throws IcdmException error when initializing
   */
  public CDRResultFunctionCommand(final ServiceData serviceData, final CDRResultFunction input, final boolean isUpdate,
      final boolean isDelete) throws IcdmException {
    super(serviceData, input, new CDRResultFunctionLoader(serviceData),
        isDelete ? COMMAND_MODE.DELETE : (isUpdate ? COMMAND_MODE.UPDATE : COMMAND_MODE.CREATE));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {
    TRvwFunction entity = new TRvwFunction();

    TRvwResult resultEntity = new CDRReviewResultLoader(getServiceData()).getEntityObject(getInputData().getResultId());
    entity.setTRvwResult(resultEntity);

    Set<TRvwFunction> tRvwFunc = resultEntity.getTRvwFunctions();
    if (null == tRvwFunc) {
      tRvwFunc = new HashSet<TRvwFunction>();
    }
    tRvwFunc.add(entity);
    resultEntity.setTRvwFunctions(tRvwFunc);
    entity.setTFunction(new FunctionLoader(getServiceData()).getEntityObject(getInputData().getFunctionId()));
    entity.setTFuncVers(getInputData().getFunctionVers());

    setUserDetails(COMMAND_MODE.CREATE, entity);

    persistEntity(entity);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void update() throws IcdmException {
    CDRResultFunctionLoader loader = new CDRResultFunctionLoader(getServiceData());
    TRvwFunction entity = loader.getEntityObject(getInputData().getId());

    entity.setTRvwResult(new CDRReviewResultLoader(getServiceData()).getEntityObject(getInputData().getResultId()));

    entity.setTFunction(new FunctionLoader(getServiceData()).getEntityObject(getInputData().getFunctionId()));
    entity.setTFuncVers(getInputData().getFunctionVers());

    setUserDetails(COMMAND_MODE.UPDATE, entity);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void delete() throws IcdmException {
    CDRResultFunctionLoader loader = new CDRResultFunctionLoader(getServiceData());
    TRvwFunction entity = loader.getEntityObject(getInputData().getId());
    entity.getTRvwResult().getTRvwFunctions().remove(entity);
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
