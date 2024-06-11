
/**
 * GetPidcWebFlowDataFault1Exception.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.2  Built on : Apr 17, 2012 (05:33:49 IST)
 */

package com.bosch.caltool.apic.ws.client;

public class GetPidcWebFlowDataFault1Exception extends java.lang.Exception{

    private static final long serialVersionUID = 1518698935309L;
    
    private com.bosch.caltool.apic.ws.client.APICStub.GetPidcWebFlowDataFault faultMessage;

    
        public GetPidcWebFlowDataFault1Exception() {
            super("GetPidcWebFlowDataFault1Exception");
        }

        public GetPidcWebFlowDataFault1Exception(java.lang.String s) {
           super(s);
        }

        public GetPidcWebFlowDataFault1Exception(java.lang.String s, java.lang.Throwable ex) {
          super(s, ex);
        }

        public GetPidcWebFlowDataFault1Exception(java.lang.Throwable cause) {
            super(cause);
        }
    

    public void setFaultMessage(com.bosch.caltool.apic.ws.client.APICStub.GetPidcWebFlowDataFault msg){
       faultMessage = msg;
    }
    
    public com.bosch.caltool.apic.ws.client.APICStub.GetPidcWebFlowDataFault getFaultMessage(){
       return faultMessage;
    }
}
    