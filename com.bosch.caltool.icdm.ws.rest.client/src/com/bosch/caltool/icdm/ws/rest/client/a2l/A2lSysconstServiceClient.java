package com.bosch.caltool.icdm.ws.rest.client.a2l;

import java.util.List;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.a2l.A2lSysconst;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Service Client for A2lSyscon
 * 
 * @author pdh2cob
 * 
 */
public class A2lSysconstServiceClient
    extends AbstractRestServiceClient
{


    /**
     * Constructor
     * 
     */
    public A2lSysconstServiceClient() {
        super(WsCommonConstants.RWS_CONTEXT_A2L, WsCommonConstants.RWS_SYSTEM_CONSTANTS);
    }

    /**
     * Get A2lSyscon using its id
     * @param objId object's id
     * @return A2lSysconst object
     * @throws ApicWebServiceException exception while invoking service
     * 
     */
    public A2lSysconst get(final Long objId)
        throws ApicWebServiceException
    {
        WebTarget wsTarget = getWsBase().queryParam(WsCommonConstants.RWS_QP_OBJ_ID, objId);
        return get(wsTarget, A2lSysconst.class);
    }

    public List<String> getInvalidSystemConstants(List<String> systemConstantNames) throws ApicWebServiceException{

      WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_INVALID_SYSTEM_CONSTANTS);
      GenericType<List<String>> type = new GenericType<List<String>>() {};
      return (List<String>) post(wsTarget, systemConstantNames, type);
    }
    
}
