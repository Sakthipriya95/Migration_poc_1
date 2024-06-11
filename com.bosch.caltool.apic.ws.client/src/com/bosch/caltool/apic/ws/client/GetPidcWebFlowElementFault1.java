
/**
 * GetPidcWebFlowElementFault1.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.2  Built on : Apr 17, 2012 (05:33:49 IST)
 */

package com.bosch.caltool.apic.ws.client;

public class GetPidcWebFlowElementFault1 extends java.lang.Exception{

    private static final long serialVersionUID = 1601232943604L;
    
    private com.bosch.caltool.apic.ws.client.APICStub.GetPidcWebFlowElementFault faultMessage;

    
        public GetPidcWebFlowElementFault1() {
            super("GetPidcWebFlowElementFault1");
        }

        public GetPidcWebFlowElementFault1(java.lang.String s) {
           super(s);
        }

        public GetPidcWebFlowElementFault1(java.lang.String s, java.lang.Throwable ex) {
          super(s, ex);
        }

        public GetPidcWebFlowElementFault1(java.lang.Throwable cause) {
            super(cause);
        }
    

    public void setFaultMessage(com.bosch.caltool.apic.ws.client.APICStub.GetPidcWebFlowElementFault msg){
       faultMessage = msg;
    }
    
    public com.bosch.caltool.apic.ws.client.APICStub.GetPidcWebFlowElementFault getFaultMessage(){
       return faultMessage;
    }
}
    