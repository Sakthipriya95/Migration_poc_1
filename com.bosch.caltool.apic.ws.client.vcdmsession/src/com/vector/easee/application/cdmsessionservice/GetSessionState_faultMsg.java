
/**
 * GetSessionState_faultMsg.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.2  Built on : Apr 17, 2012 (05:33:49 IST)
 */

package com.vector.easee.application.cdmsessionservice;

public class GetSessionState_faultMsg extends java.lang.Exception{

    private static final long serialVersionUID = 1401715645888L;
    
    private com.vector.easee.application.cdmsessionservice.CDMSessionServiceStub.ErrorType faultMessage;

    
        public GetSessionState_faultMsg() {
            super("GetSessionState_faultMsg");
        }

        public GetSessionState_faultMsg(java.lang.String s) {
           super(s);
        }

        public GetSessionState_faultMsg(java.lang.String s, java.lang.Throwable ex) {
          super(s, ex);
        }

        public GetSessionState_faultMsg(java.lang.Throwable cause) {
            super(cause);
        }
    

    public void setFaultMessage(com.vector.easee.application.cdmsessionservice.CDMSessionServiceStub.ErrorType msg){
       faultMessage = msg;
    }
    
    public com.vector.easee.application.cdmsessionservice.CDMSessionServiceStub.ErrorType getFaultMessage(){
       return faultMessage;
    }
}
    