
/**
 * CreatePST_faultMsg.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.2  Built on : Apr 17, 2012 (05:33:49 IST)
 */

package com.vector.easee.application.cdmdomainservice;

public class CreatePST_faultMsg extends java.lang.Exception{

    private static final long serialVersionUID = 1402376928957L;
    
    private com.vector.easee.application.cdmdomainservice.CDMDomainServiceStub.ErrorType faultMessage;

    
        public CreatePST_faultMsg() {
            super("CreatePST_faultMsg");
        }

        public CreatePST_faultMsg(java.lang.String s) {
           super(s);
        }

        public CreatePST_faultMsg(java.lang.String s, java.lang.Throwable ex) {
          super(s, ex);
        }

        public CreatePST_faultMsg(java.lang.Throwable cause) {
            super(cause);
        }
    

    public void setFaultMessage(com.vector.easee.application.cdmdomainservice.CDMDomainServiceStub.ErrorType msg){
       faultMessage = msg;
    }
    
    public com.vector.easee.application.cdmdomainservice.CDMDomainServiceStub.ErrorType getFaultMessage(){
       return faultMessage;
    }
}
    