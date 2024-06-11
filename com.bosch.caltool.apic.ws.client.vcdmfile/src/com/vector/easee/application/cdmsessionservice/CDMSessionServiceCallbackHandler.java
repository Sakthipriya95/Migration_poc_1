
/**
 * CDMSessionServiceCallbackHandler.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.2  Built on : Apr 17, 2012 (05:33:49 IST)
 */

    package com.vector.easee.application.cdmsessionservice;

    /**
     *  CDMSessionServiceCallbackHandler Callback class, Users can extend this class and implement
     *  their own receiveResult and receiveError methods.
     */
    public abstract class CDMSessionServiceCallbackHandler{



    protected Object clientData;

    /**
    * User can pass in any object that needs to be accessed once the NonBlocking
    * Web service call is finished and appropriate method of this CallBack is called.
    * @param clientData Object mechanism by which the user can pass in user data
    * that will be avilable at the time this callback is called.
    */
    public CDMSessionServiceCallbackHandler(Object clientData){
        this.clientData = clientData;
    }

    /**
    * Please use this constructor if you don't want to set any clientData
    */
    public CDMSessionServiceCallbackHandler(){
        this.clientData = null;
    }

    /**
     * Get the client data
     */

     public Object getClientData() {
        return clientData;
     }

        
           /**
            * auto generated Axis2 call back method for getSessionState method
            * override this method for handling normal response from getSessionState operation
            */
           public void receiveResultgetSessionState(
                    com.vector.easee.application.cdmsessionservice.CDMSessionServiceStub.GetSessionStateResponseType result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getSessionState operation
           */
            public void receiveErrorgetSessionState(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for setExtendedLogging method
            * override this method for handling normal response from setExtendedLogging operation
            */
           public void receiveResultsetExtendedLogging(
                    com.vector.easee.application.cdmsessionservice.CDMSessionServiceStub.SetExtendedLoggingResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from setExtendedLogging operation
           */
            public void receiveErrorsetExtendedLogging(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for login method
            * override this method for handling normal response from login operation
            */
           public void receiveResultlogin(
                    com.vector.easee.application.cdmsessionservice.CDMSessionServiceStub.LoginResponseType result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from login operation
           */
            public void receiveErrorlogin(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for logout method
            * override this method for handling normal response from logout operation
            */
           public void receiveResultlogout(
                    com.vector.easee.application.cdmsessionservice.CDMSessionServiceStub.LogoutResponseType result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from logout operation
           */
            public void receiveErrorlogout(java.lang.Exception e) {
            }
                


    }
    