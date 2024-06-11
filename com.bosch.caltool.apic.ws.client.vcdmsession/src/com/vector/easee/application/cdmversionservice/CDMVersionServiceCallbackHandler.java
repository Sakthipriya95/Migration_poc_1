
/**
 * CDMVersionServiceCallbackHandler.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.2  Built on : Apr 17, 2012 (05:33:49 IST)
 */

    package com.vector.easee.application.cdmversionservice;

    /**
     *  CDMVersionServiceCallbackHandler Callback class, Users can extend this class and implement
     *  their own receiveResult and receiveError methods.
     */
    public abstract class CDMVersionServiceCallbackHandler{



    protected Object clientData;

    /**
    * User can pass in any object that needs to be accessed once the NonBlocking
    * Web service call is finished and appropriate method of this CallBack is called.
    * @param clientData Object mechanism by which the user can pass in user data
    * that will be avilable at the time this callback is called.
    */
    public CDMVersionServiceCallbackHandler(Object clientData){
        this.clientData = clientData;
    }

    /**
    * Please use this constructor if you don't want to set any clientData
    */
    public CDMVersionServiceCallbackHandler(){
        this.clientData = null;
    }

    /**
     * Get the client data
     */

     public Object getClientData() {
        return clientData;
     }

        
           /**
            * auto generated Axis2 call back method for setElementAttributes method
            * override this method for handling normal response from setElementAttributes operation
            */
           public void receiveResultsetElementAttributes(
                    com.vector.easee.application.cdmversionservice.CDMVersionServiceStub.SetElementAttributesResponseType result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from setElementAttributes operation
           */
            public void receiveErrorsetElementAttributes(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getElementAttributes method
            * override this method for handling normal response from getElementAttributes operation
            */
           public void receiveResultgetElementAttributes(
                    com.vector.easee.application.cdmversionservice.CDMVersionServiceStub.GetElementAttributesResponseType result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getElementAttributes operation
           */
            public void receiveErrorgetElementAttributes(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getVersionAttributes method
            * override this method for handling normal response from getVersionAttributes operation
            */
           public void receiveResultgetVersionAttributes(
                    com.vector.easee.application.cdmversionservice.CDMVersionServiceStub.GetVersionAttributesResponseType result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getVersionAttributes operation
           */
            public void receiveErrorgetVersionAttributes(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for searchContent method
            * override this method for handling normal response from searchContent operation
            */
           public void receiveResultsearchContent(
                    com.vector.easee.application.cdmversionservice.CDMVersionServiceStub.SearchContentResponseType result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from searchContent operation
           */
            public void receiveErrorsearchContent(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getContentAttributes method
            * override this method for handling normal response from getContentAttributes operation
            */
           public void receiveResultgetContentAttributes(
                    com.vector.easee.application.cdmversionservice.CDMVersionServiceStub.GetContentAttributesResponseType result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getContentAttributes operation
           */
            public void receiveErrorgetContentAttributes(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for fetchArtifact method
            * override this method for handling normal response from fetchArtifact operation
            */
           public void receiveResultfetchArtifact(
                    com.vector.easee.application.cdmversionservice.CDMVersionServiceStub.FetchArtifactResponseType result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from fetchArtifact operation
           */
            public void receiveErrorfetchArtifact(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for checkOut method
            * override this method for handling normal response from checkOut operation
            */
           public void receiveResultcheckOut(
                    com.vector.easee.application.cdmversionservice.CDMVersionServiceStub.CheckOutResponseType result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from checkOut operation
           */
            public void receiveErrorcheckOut(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getConfigState method
            * override this method for handling normal response from getConfigState operation
            */
           public void receiveResultgetConfigState(
                    com.vector.easee.application.cdmversionservice.CDMVersionServiceStub.GetConfigStateResponseType result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getConfigState operation
           */
            public void receiveErrorgetConfigState(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getSystemAttributes method
            * override this method for handling normal response from getSystemAttributes operation
            */
           public void receiveResultgetSystemAttributes(
                    com.vector.easee.application.cdmversionservice.CDMVersionServiceStub.GetSystemAttributesResponseType result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getSystemAttributes operation
           */
            public void receiveErrorgetSystemAttributes(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for checkIn method
            * override this method for handling normal response from checkIn operation
            */
           public void receiveResultcheckIn(
                    com.vector.easee.application.cdmversionservice.CDMVersionServiceStub.CheckInResponseType result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from checkIn operation
           */
            public void receiveErrorcheckIn(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for insertIntoContainer method
            * override this method for handling normal response from insertIntoContainer operation
            */
           public void receiveResultinsertIntoContainer(
                    com.vector.easee.application.cdmversionservice.CDMVersionServiceStub.InsertIntoContainerResponseType result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from insertIntoContainer operation
           */
            public void receiveErrorinsertIntoContainer(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for setLifecycleState method
            * override this method for handling normal response from setLifecycleState operation
            */
           public void receiveResultsetLifecycleState(
                    com.vector.easee.application.cdmversionservice.CDMVersionServiceStub.SetLifecycleStateResponseType result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from setLifecycleState operation
           */
            public void receiveErrorsetLifecycleState(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for deleteObject method
            * override this method for handling normal response from deleteObject operation
            */
           public void receiveResultdeleteObject(
                    com.vector.easee.application.cdmversionservice.CDMVersionServiceStub.DeleteObjectResponseType result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from deleteObject operation
           */
            public void receiveErrordeleteObject(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for setContentAttributes method
            * override this method for handling normal response from setContentAttributes operation
            */
           public void receiveResultsetContentAttributes(
                    com.vector.easee.application.cdmversionservice.CDMVersionServiceStub.SetContentAttributesResponseType result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from setContentAttributes operation
           */
            public void receiveErrorsetContentAttributes(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for setVersionAttributes method
            * override this method for handling normal response from setVersionAttributes operation
            */
           public void receiveResultsetVersionAttributes(
                    com.vector.easee.application.cdmversionservice.CDMVersionServiceStub.SetVersionAttributesResponseType result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from setVersionAttributes operation
           */
            public void receiveErrorsetVersionAttributes(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for isCheckedIn method
            * override this method for handling normal response from isCheckedIn operation
            */
           public void receiveResultisCheckedIn(
                    com.vector.easee.application.cdmversionservice.CDMVersionServiceStub.IsCheckedInResponseType result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from isCheckedIn operation
           */
            public void receiveErrorisCheckedIn(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for removeFromContainer method
            * override this method for handling normal response from removeFromContainer operation
            */
           public void receiveResultremoveFromContainer(
                    com.vector.easee.application.cdmversionservice.CDMVersionServiceStub.RemoveFromContainerResponseType result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from removeFromContainer operation
           */
            public void receiveErrorremoveFromContainer(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for createObject method
            * override this method for handling normal response from createObject operation
            */
           public void receiveResultcreateObject(
                    com.vector.easee.application.cdmversionservice.CDMVersionServiceStub.CreateObjectResponseType result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from createObject operation
           */
            public void receiveErrorcreateObject(java.lang.Exception e) {
            }
                


    }
    