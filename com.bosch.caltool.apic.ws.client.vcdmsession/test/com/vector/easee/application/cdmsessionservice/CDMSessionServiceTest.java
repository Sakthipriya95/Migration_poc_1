

/**
 * CDMSessionServiceTest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.2  Built on : Apr 17, 2012 (05:33:49 IST)
 */
    package com.vector.easee.application.cdmsessionservice;

    /*
     *  CDMSessionServiceTest Junit test case
    */

    public class CDMSessionServiceTest extends junit.framework.TestCase{

     
        /**
         * Auto generated test method
         */
        public  void testgetSessionState() throws java.lang.Exception{

        com.vector.easee.application.cdmsessionservice.CDMSessionServiceStub stub =
                    new com.vector.easee.application.cdmsessionservice.CDMSessionServiceStub();//the default implementation should point to the right endpoint

           com.vector.easee.application.cdmsessionservice.CDMSessionServiceStub.GetSessionStateRequestType getSessionStateRequestType16=
                                                        (com.vector.easee.application.cdmsessionservice.CDMSessionServiceStub.GetSessionStateRequestType)getTestObject(com.vector.easee.application.cdmsessionservice.CDMSessionServiceStub.GetSessionStateRequestType.class);
                    // TODO : Fill in the getSessionStateRequestType16 here
                
                        assertNotNull(stub.getSessionState(
                        getSessionStateRequestType16));
                  



        }
        
         /**
         * Auto generated test method
         */
        public  void testStartgetSessionState() throws java.lang.Exception{
            com.vector.easee.application.cdmsessionservice.CDMSessionServiceStub stub = new com.vector.easee.application.cdmsessionservice.CDMSessionServiceStub();
             com.vector.easee.application.cdmsessionservice.CDMSessionServiceStub.GetSessionStateRequestType getSessionStateRequestType16=
                                                        (com.vector.easee.application.cdmsessionservice.CDMSessionServiceStub.GetSessionStateRequestType)getTestObject(com.vector.easee.application.cdmsessionservice.CDMSessionServiceStub.GetSessionStateRequestType.class);
                    // TODO : Fill in the getSessionStateRequestType16 here
                

                stub.startgetSessionState(
                         getSessionStateRequestType16,
                    new tempCallbackN65548()
                );
              


        }

        private class tempCallbackN65548  extends com.vector.easee.application.cdmsessionservice.CDMSessionServiceCallbackHandler{
            public tempCallbackN65548(){ super(null);}

            public void receiveResultgetSessionState(
                         com.vector.easee.application.cdmsessionservice.CDMSessionServiceStub.GetSessionStateResponseType result
                            ) {
                
            }

            public void receiveErrorgetSessionState(java.lang.Exception e) {
                fail();
            }

        }
      
        /**
         * Auto generated test method
         */
        public  void testsetExtendedLogging() throws java.lang.Exception{

        com.vector.easee.application.cdmsessionservice.CDMSessionServiceStub stub =
                    new com.vector.easee.application.cdmsessionservice.CDMSessionServiceStub();//the default implementation should point to the right endpoint

           com.vector.easee.application.cdmsessionservice.CDMSessionServiceStub.SetExtendedLoggingType setExtendedLoggingType18=
                                                        (com.vector.easee.application.cdmsessionservice.CDMSessionServiceStub.SetExtendedLoggingType)getTestObject(com.vector.easee.application.cdmsessionservice.CDMSessionServiceStub.SetExtendedLoggingType.class);
                    // TODO : Fill in the setExtendedLoggingType18 here
                
                        assertNotNull(stub.setExtendedLogging(
                        setExtendedLoggingType18));
                  



        }
        
         /**
         * Auto generated test method
         */
        public  void testStartsetExtendedLogging() throws java.lang.Exception{
            com.vector.easee.application.cdmsessionservice.CDMSessionServiceStub stub = new com.vector.easee.application.cdmsessionservice.CDMSessionServiceStub();
             com.vector.easee.application.cdmsessionservice.CDMSessionServiceStub.SetExtendedLoggingType setExtendedLoggingType18=
                                                        (com.vector.easee.application.cdmsessionservice.CDMSessionServiceStub.SetExtendedLoggingType)getTestObject(com.vector.easee.application.cdmsessionservice.CDMSessionServiceStub.SetExtendedLoggingType.class);
                    // TODO : Fill in the setExtendedLoggingType18 here
                

                stub.startsetExtendedLogging(
                         setExtendedLoggingType18,
                    new tempCallbackN65612()
                );
              


        }

        private class tempCallbackN65612  extends com.vector.easee.application.cdmsessionservice.CDMSessionServiceCallbackHandler{
            public tempCallbackN65612(){ super(null);}

            public void receiveResultsetExtendedLogging(
                         com.vector.easee.application.cdmsessionservice.CDMSessionServiceStub.SetExtendedLoggingResponse result
                            ) {
                
            }

            public void receiveErrorsetExtendedLogging(java.lang.Exception e) {
                fail();
            }

        }
      
        /**
         * Auto generated test method
         */
        public  void testlogin() throws java.lang.Exception{

        com.vector.easee.application.cdmsessionservice.CDMSessionServiceStub stub =
                    new com.vector.easee.application.cdmsessionservice.CDMSessionServiceStub();//the default implementation should point to the right endpoint

           com.vector.easee.application.cdmsessionservice.CDMSessionServiceStub.LoginRequestType loginRequestType20=
                                                        (com.vector.easee.application.cdmsessionservice.CDMSessionServiceStub.LoginRequestType)getTestObject(com.vector.easee.application.cdmsessionservice.CDMSessionServiceStub.LoginRequestType.class);
                    // TODO : Fill in the loginRequestType20 here
                
                        assertNotNull(stub.login(
                        loginRequestType20));
                  



        }
        
         /**
         * Auto generated test method
         */
        public  void testStartlogin() throws java.lang.Exception{
            com.vector.easee.application.cdmsessionservice.CDMSessionServiceStub stub = new com.vector.easee.application.cdmsessionservice.CDMSessionServiceStub();
             com.vector.easee.application.cdmsessionservice.CDMSessionServiceStub.LoginRequestType loginRequestType20=
                                                        (com.vector.easee.application.cdmsessionservice.CDMSessionServiceStub.LoginRequestType)getTestObject(com.vector.easee.application.cdmsessionservice.CDMSessionServiceStub.LoginRequestType.class);
                    // TODO : Fill in the loginRequestType20 here
                

                stub.startlogin(
                         loginRequestType20,
                    new tempCallbackN65676()
                );
              


        }

        private class tempCallbackN65676  extends com.vector.easee.application.cdmsessionservice.CDMSessionServiceCallbackHandler{
            public tempCallbackN65676(){ super(null);}

            public void receiveResultlogin(
                         com.vector.easee.application.cdmsessionservice.CDMSessionServiceStub.LoginResponseType result
                            ) {
                
            }

            public void receiveErrorlogin(java.lang.Exception e) {
                fail();
            }

        }
      
        /**
         * Auto generated test method
         */
        public  void testlogout() throws java.lang.Exception{

        com.vector.easee.application.cdmsessionservice.CDMSessionServiceStub stub =
                    new com.vector.easee.application.cdmsessionservice.CDMSessionServiceStub();//the default implementation should point to the right endpoint

           com.vector.easee.application.cdmsessionservice.CDMSessionServiceStub.LogoutRequestType logoutRequestType22=
                                                        (com.vector.easee.application.cdmsessionservice.CDMSessionServiceStub.LogoutRequestType)getTestObject(com.vector.easee.application.cdmsessionservice.CDMSessionServiceStub.LogoutRequestType.class);
                    // TODO : Fill in the logoutRequestType22 here
                
                        assertNotNull(stub.logout(
                        logoutRequestType22));
                  



        }
        
         /**
         * Auto generated test method
         */
        public  void testStartlogout() throws java.lang.Exception{
            com.vector.easee.application.cdmsessionservice.CDMSessionServiceStub stub = new com.vector.easee.application.cdmsessionservice.CDMSessionServiceStub();
             com.vector.easee.application.cdmsessionservice.CDMSessionServiceStub.LogoutRequestType logoutRequestType22=
                                                        (com.vector.easee.application.cdmsessionservice.CDMSessionServiceStub.LogoutRequestType)getTestObject(com.vector.easee.application.cdmsessionservice.CDMSessionServiceStub.LogoutRequestType.class);
                    // TODO : Fill in the logoutRequestType22 here
                

                stub.startlogout(
                         logoutRequestType22,
                    new tempCallbackN65740()
                );
              


        }

        private class tempCallbackN65740  extends com.vector.easee.application.cdmsessionservice.CDMSessionServiceCallbackHandler{
            public tempCallbackN65740(){ super(null);}

            public void receiveResultlogout(
                         com.vector.easee.application.cdmsessionservice.CDMSessionServiceStub.LogoutResponseType result
                            ) {
                
            }

            public void receiveErrorlogout(java.lang.Exception e) {
                fail();
            }

        }
      
        //Create an ADBBean and provide it as the test object
        public org.apache.axis2.databinding.ADBBean getTestObject(java.lang.Class type) throws java.lang.Exception{
           return (org.apache.axis2.databinding.ADBBean) type.newInstance();
        }

        
        

    }
    