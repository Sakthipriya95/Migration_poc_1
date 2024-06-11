
/**
 * CDMDomainServiceCallbackHandler.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.2  Built on : Apr 17, 2012 (05:33:49 IST)
 */

    package com.vector.easee.application.cdmdomainservice;

    /**
     *  CDMDomainServiceCallbackHandler Callback class, Users can extend this class and implement
     *  their own receiveResult and receiveError methods.
     */
    public abstract class CDMDomainServiceCallbackHandler{



    protected Object clientData;

    /**
    * User can pass in any object that needs to be accessed once the NonBlocking
    * Web service call is finished and appropriate method of this CallBack is called.
    * @param clientData Object mechanism by which the user can pass in user data
    * that will be avilable at the time this callback is called.
    */
    public CDMDomainServiceCallbackHandler(Object clientData){
        this.clientData = clientData;
    }

    /**
    * Please use this constructor if you don't want to set any clientData
    */
    public CDMDomainServiceCallbackHandler(){
        this.clientData = null;
    }

    /**
     * Get the client data
     */

     public Object getClientData() {
        return clientData;
     }

        
           /**
            * auto generated Axis2 call back method for searchObjectsByExpr method
            * override this method for handling normal response from searchObjectsByExpr operation
            */
           public void receiveResultsearchObjectsByExpr(
                    com.vector.easee.application.cdmdomainservice.CDMDomainServiceStub.SearchObjectsByExprResponseType result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from searchObjectsByExpr operation
           */
            public void receiveErrorsearchObjectsByExpr(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for mergeProductAttributes method
            * override this method for handling normal response from mergeProductAttributes operation
            */
           public void receiveResultmergeProductAttributes(
                    com.vector.easee.application.cdmdomainservice.CDMDomainServiceStub.MergeProductAttributesResponseType result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from mergeProductAttributes operation
           */
            public void receiveErrormergeProductAttributes(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for deleteProductAttributesAndValues method
            * override this method for handling normal response from deleteProductAttributesAndValues operation
            */
           public void receiveResultdeleteProductAttributesAndValues(
                    com.vector.easee.application.cdmdomainservice.CDMDomainServiceStub.DeleteProductAttributesAndValuesResponseType result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from deleteProductAttributesAndValues operation
           */
            public void receiveErrordeleteProductAttributesAndValues(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for createProductAttributesAndValues method
            * override this method for handling normal response from createProductAttributesAndValues operation
            */
           public void receiveResultcreateProductAttributesAndValues(
                    com.vector.easee.application.cdmdomainservice.CDMDomainServiceStub.CreateProductAttributesAndValuesResponseType result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from createProductAttributesAndValues operation
           */
            public void receiveErrorcreateProductAttributesAndValues(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for createPRD method
            * override this method for handling normal response from createPRD operation
            */
           public void receiveResultcreatePRD(
                    com.vector.easee.application.cdmdomainservice.CDMDomainServiceStub.CreatePRDResponseType result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from createPRD operation
           */
            public void receiveErrorcreatePRD(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for createPVD method
            * override this method for handling normal response from createPVD operation
            */
           public void receiveResultcreatePVD(
                    com.vector.easee.application.cdmdomainservice.CDMDomainServiceStub.CreatePVDResponseType result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from createPVD operation
           */
            public void receiveErrorcreatePVD(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for createPST method
            * override this method for handling normal response from createPST operation
            */
           public void receiveResultcreatePST(
                    com.vector.easee.application.cdmdomainservice.CDMDomainServiceStub.CreatePSTResponseType result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from createPST operation
           */
            public void receiveErrorcreatePST(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for searchObjects method
            * override this method for handling normal response from searchObjects operation
            */
           public void receiveResultsearchObjects(
                    com.vector.easee.application.cdmdomainservice.CDMDomainServiceStub.SearchObjectsResponseType result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from searchObjects operation
           */
            public void receiveErrorsearchObjects(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for renameProductAttributeValues method
            * override this method for handling normal response from renameProductAttributeValues operation
            */
           public void receiveResultrenameProductAttributeValues(
                    com.vector.easee.application.cdmdomainservice.CDMDomainServiceStub.RenameProductAttributeValuesResponseType result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from renameProductAttributeValues operation
           */
            public void receiveErrorrenameProductAttributeValues(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for renameProductAttribute method
            * override this method for handling normal response from renameProductAttribute operation
            */
           public void receiveResultrenameProductAttribute(
                    com.vector.easee.application.cdmdomainservice.CDMDomainServiceStub.RenameProductAttributeResponseType result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from renameProductAttribute operation
           */
            public void receiveErrorrenameProductAttribute(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getProductAttributeValues method
            * override this method for handling normal response from getProductAttributeValues operation
            */
           public void receiveResultgetProductAttributeValues(
                    com.vector.easee.application.cdmdomainservice.CDMDomainServiceStub.GetProductAttributeValuesResponseType result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getProductAttributeValues operation
           */
            public void receiveErrorgetProductAttributeValues(java.lang.Exception e) {
            }
                


    }
    