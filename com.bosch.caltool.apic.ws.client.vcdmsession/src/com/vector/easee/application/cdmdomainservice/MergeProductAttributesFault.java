
/**
 * MergeProductAttributesFault.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.2  Built on : Apr 17, 2012 (05:33:49 IST)
 */

package com.vector.easee.application.cdmdomainservice;

public class MergeProductAttributesFault extends java.lang.Exception{

    private static final long serialVersionUID = 1402376928879L;
    
    private com.vector.easee.application.cdmdomainservice.CDMDomainServiceStub.ErrorType faultMessage;

    
        public MergeProductAttributesFault() {
            super("MergeProductAttributesFault");
        }

        public MergeProductAttributesFault(java.lang.String s) {
           super(s);
        }

        public MergeProductAttributesFault(java.lang.String s, java.lang.Throwable ex) {
          super(s, ex);
        }

        public MergeProductAttributesFault(java.lang.Throwable cause) {
            super(cause);
        }
    

    public void setFaultMessage(com.vector.easee.application.cdmdomainservice.CDMDomainServiceStub.ErrorType msg){
       faultMessage = msg;
    }
    
    public com.vector.easee.application.cdmdomainservice.CDMDomainServiceStub.ErrorType getFaultMessage(){
       return faultMessage;
    }
}
    