
/**
 * PwdWsSkeletonInterface.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.3  Built on : Jun 27, 2015 (11:17:49 BST)
 */
    package com.bosch.caltool.pwdservice;
    /**
     *  PwdWsSkeletonInterface java skeleton interface for the axisService
     */
    public interface PwdWsSkeletonInterface {
     
         
        /**
         * Auto generated method signature
         * 
                                    * @param passwordRequest
             * @throws PasswordFaultException : 
         */

        
                public com.bosch.caltool.pwdservice.PasswordResponse getPassword
                (
                  com.bosch.caltool.pwdservice.PasswordRequest passwordRequest
                 )
            throws PasswordFaultException;
        
         }
    