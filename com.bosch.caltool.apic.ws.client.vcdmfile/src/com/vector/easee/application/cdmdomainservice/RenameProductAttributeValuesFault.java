
/**
 * RenameProductAttributeValuesFault.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.2  Built on : Apr 17, 2012 (05:33:49 IST)
 */

package com.vector.easee.application.cdmdomainservice;

public class RenameProductAttributeValuesFault extends java.lang.Exception{

    private static final long serialVersionUID = 1402376928926L;
    
    private com.vector.easee.application.cdmdomainservice.CDMDomainServiceStub.ErrorType faultMessage;

    
        public RenameProductAttributeValuesFault() {
            super("RenameProductAttributeValuesFault");
        }

        public RenameProductAttributeValuesFault(java.lang.String s) {
           super(s);
        }

        public RenameProductAttributeValuesFault(java.lang.String s, java.lang.Throwable ex) {
          super(s, ex);
        }

        public RenameProductAttributeValuesFault(java.lang.Throwable cause) {
            super(cause);
        }
    

    public void setFaultMessage(com.vector.easee.application.cdmdomainservice.CDMDomainServiceStub.ErrorType msg){
       faultMessage = msg;
    }
    
    public com.vector.easee.application.cdmdomainservice.CDMDomainServiceStub.ErrorType getFaultMessage(){
       return faultMessage;
    }
}
    