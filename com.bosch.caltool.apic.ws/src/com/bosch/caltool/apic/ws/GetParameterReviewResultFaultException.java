
/**
 * GetParameterReviewResultFaultException.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.2  Built on : Apr 17, 2012 (05:33:49 IST)
 */

package com.bosch.caltool.apic.ws;

public class GetParameterReviewResultFaultException extends java.lang.Exception{

    private static final long serialVersionUID = 1601232727605L;
    
    private com.bosch.caltool.apic.ws.GetParameterReviewResultFault faultMessage;

    
        public GetParameterReviewResultFaultException() {
            super("GetParameterReviewResultFaultException");
        }

        public GetParameterReviewResultFaultException(java.lang.String s) {
           super(s);
        }

        public GetParameterReviewResultFaultException(java.lang.String s, java.lang.Throwable ex) {
          super(s, ex);
        }

        public GetParameterReviewResultFaultException(java.lang.Throwable cause) {
            super(cause);
        }
    

    public void setFaultMessage(com.bosch.caltool.apic.ws.GetParameterReviewResultFault msg){
       faultMessage = msg;
    }
    
    public com.bosch.caltool.apic.ws.GetParameterReviewResultFault getFaultMessage(){
       return faultMessage;
    }
}
    