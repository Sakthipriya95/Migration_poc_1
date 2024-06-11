/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.a2l;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.database.entity.cdr.TFunctionversion;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.a2l.FunctionVersion;

/**
 * @author rgo7cob
 */
public class FunctionVersionLoader extends AbstractBusinessObject<FunctionVersion, TFunctionversion> {


  /**
   * @param serviceData serviceData
   */
  public FunctionVersionLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.FUNCTION_VERSION, TFunctionversion.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected FunctionVersion createDataObject(final TFunctionversion tfuncVers) throws DataException {
    FunctionVersion funcVersion = new FunctionVersion();
    funcVersion.setId(tfuncVers.getId());
    funcVersion.setFuncName(tfuncVers.getFuncname());
    funcVersion.setFuncNameUpper(tfuncVers.getFuncNameUpper());
    funcVersion.setParamName(tfuncVers.getDefcharname());
    funcVersion.setVersion(tfuncVers.getVersion());
    return funcVersion;
  }


}
