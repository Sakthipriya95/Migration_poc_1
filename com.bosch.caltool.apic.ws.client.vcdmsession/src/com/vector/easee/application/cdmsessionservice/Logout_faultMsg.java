
/**
 * Logout_faultMsg.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.2  Built on : Apr 17, 2012 (05:33:49 IST)
 */

package com.vector.easee.application.cdmsessionservice;

public class Logout_faultMsg extends java.lang.Exception{

    private static final long serialVersionUID = 1401715645857L;
    
    private com.vector.easee.application.cdmsessionservice.CDMSessionServiceStub.ErrorType faultMessage;

    
        public Logout_faultMsg() {
            super("Logout_faultMsg");
        }

        public Logout_faultMsg(java.lang.String s) {
           super(s);
        }

        public Logout_faultMsg(java.lang.String s, java.lang.Throwable ex) {
          super(s, ex);
        }

        public Logout_faultMsg(java.lang.Throwable cause) {
            super(cause);
        }
    

    public void setFaultMessage(com.vector.easee.application.cdmsessionservice.CDMSessionServiceStub.ErrorType msg){
       faultMessage = msg;
    }
    
    public com.vector.easee.application.cdmsessionservice.CDMSessionServiceStub.ErrorType getFaultMessage(){
       return faultMessage;
    }
}
    