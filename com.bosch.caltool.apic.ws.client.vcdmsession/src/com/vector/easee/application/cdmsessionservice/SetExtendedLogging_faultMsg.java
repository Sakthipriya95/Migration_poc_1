
/**
 * SetExtendedLogging_faultMsg.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.2  Built on : Apr 17, 2012 (05:33:49 IST)
 */

package com.vector.easee.application.cdmsessionservice;

public class SetExtendedLogging_faultMsg extends java.lang.Exception{

    private static final long serialVersionUID = 1401715645872L;
    
    private com.vector.easee.application.cdmsessionservice.CDMSessionServiceStub.ErrorType faultMessage;

    
        public SetExtendedLogging_faultMsg() {
            super("SetExtendedLogging_faultMsg");
        }

        public SetExtendedLogging_faultMsg(java.lang.String s) {
           super(s);
        }

        public SetExtendedLogging_faultMsg(java.lang.String s, java.lang.Throwable ex) {
          super(s, ex);
        }

        public SetExtendedLogging_faultMsg(java.lang.Throwable cause) {
            super(cause);
        }
    

    public void setFaultMessage(com.vector.easee.application.cdmsessionservice.CDMSessionServiceStub.ErrorType msg){
       faultMessage = msg;
    }
    
    public com.vector.easee.application.cdmsessionservice.CDMSessionServiceStub.ErrorType getFaultMessage(){
       return faultMessage;
    }
}
    