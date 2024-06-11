
/**
 * InsertIntoContainerFault.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.2  Built on : Apr 17, 2012 (05:33:49 IST)
 */

package com.vector.easee.application.cdmversionservice;

public class InsertIntoContainerFault extends java.lang.Exception{

    private static final long serialVersionUID = 1401773391933L;
    
    private com.vector.easee.application.cdmversionservice.CDMVersionServiceStub.ErrorType faultMessage;

    
        public InsertIntoContainerFault() {
            super("InsertIntoContainerFault");
        }

        public InsertIntoContainerFault(java.lang.String s) {
           super(s);
        }

        public InsertIntoContainerFault(java.lang.String s, java.lang.Throwable ex) {
          super(s, ex);
        }

        public InsertIntoContainerFault(java.lang.Throwable cause) {
            super(cause);
        }
    

    public void setFaultMessage(com.vector.easee.application.cdmversionservice.CDMVersionServiceStub.ErrorType msg){
       faultMessage = msg;
    }
    
    public com.vector.easee.application.cdmversionservice.CDMVersionServiceStub.ErrorType getFaultMessage(){
       return faultMessage;
    }
}
    