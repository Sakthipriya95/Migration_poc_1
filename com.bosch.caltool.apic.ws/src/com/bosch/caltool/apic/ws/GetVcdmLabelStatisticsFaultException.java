
/**
 * GetVcdmLabelStatisticsFaultException.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.2  Built on : Apr 17, 2012 (05:33:49 IST)
 */

package com.bosch.caltool.apic.ws;

public class GetVcdmLabelStatisticsFaultException extends java.lang.Exception{

    private static final long serialVersionUID = 1601232727512L;
    
    private com.bosch.caltool.apic.ws.GetVcdmLabelStatisticsFault faultMessage;

    
        public GetVcdmLabelStatisticsFaultException() {
            super("GetVcdmLabelStatisticsFaultException");
        }

        public GetVcdmLabelStatisticsFaultException(java.lang.String s) {
           super(s);
        }

        public GetVcdmLabelStatisticsFaultException(java.lang.String s, java.lang.Throwable ex) {
          super(s, ex);
        }

        public GetVcdmLabelStatisticsFaultException(java.lang.Throwable cause) {
            super(cause);
        }
    

    public void setFaultMessage(com.bosch.caltool.apic.ws.GetVcdmLabelStatisticsFault msg){
       faultMessage = msg;
    }
    
    public com.bosch.caltool.apic.ws.GetVcdmLabelStatisticsFault getFaultMessage(){
       return faultMessage;
    }
}
    