/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.a2l;

import com.bosch.caltool.icdm.model.cdr.IParameterAttribute;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author rgo7cob
 */
public interface IParamAttrServiceClient<D extends IParameterAttribute> {


  public D create(D iparamAttr) throws ApicWebServiceException;

  public void delete(D iparamAttr) throws ApicWebServiceException;


}
