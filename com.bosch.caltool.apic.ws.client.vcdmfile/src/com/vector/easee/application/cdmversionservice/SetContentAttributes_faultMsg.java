
/**
 * SetContentAttributes_faultMsg.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.2  Built on : Apr 17, 2012 (05:33:49 IST)
 */

package com.vector.easee.application.cdmversionservice;

public class SetContentAttributes_faultMsg extends java.lang.Exception{

    private static final long serialVersionUID = 1401773391948L;
    
    private com.vector.easee.application.cdmversionservice.CDMVersionServiceStub.ErrorType faultMessage;

    
        public SetContentAttributes_faultMsg() {
            super("SetContentAttributes_faultMsg");
        }

        public SetContentAttributes_faultMsg(java.lang.String s) {
           super(s);
        }

        public SetContentAttributes_faultMsg(java.lang.String s, java.lang.Throwable ex) {
          super(s, ex);
        }

        public SetContentAttributes_faultMsg(java.lang.Throwable cause) {
            super(cause);
        }
    

    public void setFaultMessage(com.vector.easee.application.cdmversionservice.CDMVersionServiceStub.ErrorType msg){
       faultMessage = msg;
    }
    
    public com.vector.easee.application.cdmversionservice.CDMVersionServiceStub.ErrorType getFaultMessage(){
       return faultMessage;
    }
}
    