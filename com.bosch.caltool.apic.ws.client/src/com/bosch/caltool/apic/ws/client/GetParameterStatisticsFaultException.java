
/**
 * GetParameterStatisticsFaultException.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.2  Built on : Apr 17, 2012 (05:33:49 IST)
 */

package com.bosch.caltool.apic.ws.client;

public class GetParameterStatisticsFaultException extends java.lang.Exception{

    private static final long serialVersionUID = 1601232943714L;
    
    private com.bosch.caltool.apic.ws.client.APICStub.GetParameterStatisticsFault faultMessage;

    
        public GetParameterStatisticsFaultException() {
            super("GetParameterStatisticsFaultException");
        }

        public GetParameterStatisticsFaultException(java.lang.String s) {
           super(s);
        }

        public GetParameterStatisticsFaultException(java.lang.String s, java.lang.Throwable ex) {
          super(s, ex);
        }

        public GetParameterStatisticsFaultException(java.lang.Throwable cause) {
            super(cause);
        }
    

    public void setFaultMessage(com.bosch.caltool.apic.ws.client.APICStub.GetParameterStatisticsFault msg){
       faultMessage = msg;
    }
    
    public com.bosch.caltool.apic.ws.client.APICStub.GetParameterStatisticsFault getFaultMessage(){
       return faultMessage;
    }
}
    