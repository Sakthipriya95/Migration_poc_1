
/**
 * PidcFocusMatrixVersType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.2  Built on : Apr 17, 2012 (05:34:40 IST)
 */

            
                package com.bosch.caltool.apic.ws;
            

            /**
            *  PidcFocusMatrixVersType bean class
            */
            @SuppressWarnings({"unchecked","unused"})
        
        public  class PidcFocusMatrixVersType
        implements org.apache.axis2.databinding.ADBBean{
        /* This type was generated from the piece of schema that had
                name = PidcFocusMatrixVersType
                Namespace URI = http://ws.apic.caltool.bosch.com
                Namespace Prefix = ns1
                */
            

                        /**
                        * field for FmVersId
                        */

                        
                                    protected long localFmVersId ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localFmVersIdTracker = false ;

                           public boolean isFmVersIdSpecified(){
                               return localFmVersIdTracker;
                           }

                           

                           /**
                           * Auto generated getter method
                           * @return long
                           */
                           public  long getFmVersId(){
                               return localFmVersId;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param FmVersId
                               */
                               public void setFmVersId(long param){
                            
                                       // setting primitive attribute tracker to true
                                       localFmVersIdTracker =
                                       param != java.lang.Long.MIN_VALUE;
                                   
                                            this.localFmVersId=param;
                                    

                               }
                            

                        /**
                        * field for OldFmVersName
                        */

                        
                                    protected java.lang.String localOldFmVersName ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localOldFmVersNameTracker = false ;

                           public boolean isOldFmVersNameSpecified(){
                               return localOldFmVersNameTracker;
                           }

                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getOldFmVersName(){
                               return localOldFmVersName;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param OldFmVersName
                               */
                               public void setOldFmVersName(java.lang.String param){
                            localOldFmVersNameTracker = param != null;
                                   
                                            this.localOldFmVersName=param;
                                    

                               }
                            

                        /**
                        * field for NewFmVersName
                        */

                        
                                    protected java.lang.String localNewFmVersName ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localNewFmVersNameTracker = false ;

                           public boolean isNewFmVersNameSpecified(){
                               return localNewFmVersNameTracker;
                           }

                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getNewFmVersName(){
                               return localNewFmVersName;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param NewFmVersName
                               */
                               public void setNewFmVersName(java.lang.String param){
                            localNewFmVersNameTracker = param != null;
                                   
                                            this.localNewFmVersName=param;
                                    

                               }
                            

                        /**
                        * field for FmRevNum
                        */

                        
                                    protected long localFmRevNum ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localFmRevNumTracker = false ;

                           public boolean isFmRevNumSpecified(){
                               return localFmRevNumTracker;
                           }

                           

                           /**
                           * Auto generated getter method
                           * @return long
                           */
                           public  long getFmRevNum(){
                               return localFmRevNum;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param FmRevNum
                               */
                               public void setFmRevNum(long param){
                            
                                       // setting primitive attribute tracker to true
                                       localFmRevNumTracker =
                                       param != java.lang.Long.MIN_VALUE;
                                   
                                            this.localFmRevNum=param;
                                    

                               }
                            

                        /**
                        * field for OldFmVersStatus
                        */

                        
                                    protected java.lang.String localOldFmVersStatus ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localOldFmVersStatusTracker = false ;

                           public boolean isOldFmVersStatusSpecified(){
                               return localOldFmVersStatusTracker;
                           }

                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getOldFmVersStatus(){
                               return localOldFmVersStatus;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param OldFmVersStatus
                               */
                               public void setOldFmVersStatus(java.lang.String param){
                            localOldFmVersStatusTracker = param != null;
                                   
                                            this.localOldFmVersStatus=param;
                                    

                               }
                            

                        /**
                        * field for NewFmVersStatus
                        */

                        
                                    protected java.lang.String localNewFmVersStatus ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localNewFmVersStatusTracker = false ;

                           public boolean isNewFmVersStatusSpecified(){
                               return localNewFmVersStatusTracker;
                           }

                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getNewFmVersStatus(){
                               return localNewFmVersStatus;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param NewFmVersStatus
                               */
                               public void setNewFmVersStatus(java.lang.String param){
                            localNewFmVersStatusTracker = param != null;
                                   
                                            this.localNewFmVersStatus=param;
                                    

                               }
                            

                        /**
                        * field for OldFmVersRvwUser
                        */

                        
                                    protected java.lang.String localOldFmVersRvwUser ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localOldFmVersRvwUserTracker = false ;

                           public boolean isOldFmVersRvwUserSpecified(){
                               return localOldFmVersRvwUserTracker;
                           }

                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getOldFmVersRvwUser(){
                               return localOldFmVersRvwUser;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param OldFmVersRvwUser
                               */
                               public void setOldFmVersRvwUser(java.lang.String param){
                            localOldFmVersRvwUserTracker = param != null;
                                   
                                            this.localOldFmVersRvwUser=param;
                                    

                               }
                            

                        /**
                        * field for NewFmVersRvwUser
                        */

                        
                                    protected java.lang.String localNewFmVersRvwUser ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localNewFmVersRvwUserTracker = false ;

                           public boolean isNewFmVersRvwUserSpecified(){
                               return localNewFmVersRvwUserTracker;
                           }

                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getNewFmVersRvwUser(){
                               return localNewFmVersRvwUser;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param NewFmVersRvwUser
                               */
                               public void setNewFmVersRvwUser(java.lang.String param){
                            localNewFmVersRvwUserTracker = param != null;
                                   
                                            this.localNewFmVersRvwUser=param;
                                    

                               }
                            

                        /**
                        * field for OldFmVersRvwDate
                        */

                        
                                    protected java.util.Calendar localOldFmVersRvwDate ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localOldFmVersRvwDateTracker = false ;

                           public boolean isOldFmVersRvwDateSpecified(){
                               return localOldFmVersRvwDateTracker;
                           }

                           

                           /**
                           * Auto generated getter method
                           * @return java.util.Calendar
                           */
                           public  java.util.Calendar getOldFmVersRvwDate(){
                               return localOldFmVersRvwDate;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param OldFmVersRvwDate
                               */
                               public void setOldFmVersRvwDate(java.util.Calendar param){
                            localOldFmVersRvwDateTracker = param != null;
                                   
                                            this.localOldFmVersRvwDate=param;
                                    

                               }
                            

                        /**
                        * field for OldFmVersRvwDateStr
                        */

                        
                                    protected java.lang.String localOldFmVersRvwDateStr ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localOldFmVersRvwDateStrTracker = false ;

                           public boolean isOldFmVersRvwDateStrSpecified(){
                               return localOldFmVersRvwDateStrTracker;
                           }

                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getOldFmVersRvwDateStr(){
                               return localOldFmVersRvwDateStr;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param OldFmVersRvwDateStr
                               */
                               public void setOldFmVersRvwDateStr(java.lang.String param){
                            localOldFmVersRvwDateStrTracker = param != null;
                                   
                                            this.localOldFmVersRvwDateStr=param;
                                    

                               }
                            

                        /**
                        * field for NewFmVersRvwDate
                        */

                        
                                    protected java.util.Calendar localNewFmVersRvwDate ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localNewFmVersRvwDateTracker = false ;

                           public boolean isNewFmVersRvwDateSpecified(){
                               return localNewFmVersRvwDateTracker;
                           }

                           

                           /**
                           * Auto generated getter method
                           * @return java.util.Calendar
                           */
                           public  java.util.Calendar getNewFmVersRvwDate(){
                               return localNewFmVersRvwDate;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param NewFmVersRvwDate
                               */
                               public void setNewFmVersRvwDate(java.util.Calendar param){
                            localNewFmVersRvwDateTracker = param != null;
                                   
                                            this.localNewFmVersRvwDate=param;
                                    

                               }
                            

                        /**
                        * field for NewFmVersRvwDateStr
                        */

                        
                                    protected java.lang.String localNewFmVersRvwDateStr ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localNewFmVersRvwDateStrTracker = false ;

                           public boolean isNewFmVersRvwDateStrSpecified(){
                               return localNewFmVersRvwDateStrTracker;
                           }

                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getNewFmVersRvwDateStr(){
                               return localNewFmVersRvwDateStr;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param NewFmVersRvwDateStr
                               */
                               public void setNewFmVersRvwDateStr(java.lang.String param){
                            localNewFmVersRvwDateStrTracker = param != null;
                                   
                                            this.localNewFmVersRvwDateStr=param;
                                    

                               }
                            

                        /**
                        * field for OldFmVersLink
                        */

                        
                                    protected java.lang.String localOldFmVersLink ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localOldFmVersLinkTracker = false ;

                           public boolean isOldFmVersLinkSpecified(){
                               return localOldFmVersLinkTracker;
                           }

                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getOldFmVersLink(){
                               return localOldFmVersLink;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param OldFmVersLink
                               */
                               public void setOldFmVersLink(java.lang.String param){
                            localOldFmVersLinkTracker = param != null;
                                   
                                            this.localOldFmVersLink=param;
                                    

                               }
                            

                        /**
                        * field for NewFmVersLink
                        */

                        
                                    protected java.lang.String localNewFmVersLink ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localNewFmVersLinkTracker = false ;

                           public boolean isNewFmVersLinkSpecified(){
                               return localNewFmVersLinkTracker;
                           }

                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getNewFmVersLink(){
                               return localNewFmVersLink;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param NewFmVersLink
                               */
                               public void setNewFmVersLink(java.lang.String param){
                            localNewFmVersLinkTracker = param != null;
                                   
                                            this.localNewFmVersLink=param;
                                    

                               }
                            

                        /**
                        * field for OldRemark
                        */

                        
                                    protected java.lang.String localOldRemark ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localOldRemarkTracker = false ;

                           public boolean isOldRemarkSpecified(){
                               return localOldRemarkTracker;
                           }

                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getOldRemark(){
                               return localOldRemark;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param OldRemark
                               */
                               public void setOldRemark(java.lang.String param){
                            localOldRemarkTracker = param != null;
                                   
                                            this.localOldRemark=param;
                                    

                               }
                            

                        /**
                        * field for NewRemark
                        */

                        
                                    protected java.lang.String localNewRemark ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localNewRemarkTracker = false ;

                           public boolean isNewRemarkSpecified(){
                               return localNewRemarkTracker;
                           }

                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getNewRemark(){
                               return localNewRemark;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param NewRemark
                               */
                               public void setNewRemark(java.lang.String param){
                            localNewRemarkTracker = param != null;
                                   
                                            this.localNewRemark=param;
                                    

                               }
                            

                        /**
                        * field for OldFmRvwStatus
                        */

                        
                                    protected java.lang.String localOldFmRvwStatus ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localOldFmRvwStatusTracker = false ;

                           public boolean isOldFmRvwStatusSpecified(){
                               return localOldFmRvwStatusTracker;
                           }

                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getOldFmRvwStatus(){
                               return localOldFmRvwStatus;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param OldFmRvwStatus
                               */
                               public void setOldFmRvwStatus(java.lang.String param){
                            localOldFmRvwStatusTracker = param != null;
                                   
                                            this.localOldFmRvwStatus=param;
                                    

                               }
                            

                        /**
                        * field for NewFmRvwStatus
                        */

                        
                                    protected java.lang.String localNewFmRvwStatus ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localNewFmRvwStatusTracker = false ;

                           public boolean isNewFmRvwStatusSpecified(){
                               return localNewFmRvwStatusTracker;
                           }

                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getNewFmRvwStatus(){
                               return localNewFmRvwStatus;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param NewFmRvwStatus
                               */
                               public void setNewFmRvwStatus(java.lang.String param){
                            localNewFmRvwStatusTracker = param != null;
                                   
                                            this.localNewFmRvwStatus=param;
                                    

                               }
                            

                        /**
                        * field for FmVersCreatedUser
                        */

                        
                                    protected java.lang.String localFmVersCreatedUser ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localFmVersCreatedUserTracker = false ;

                           public boolean isFmVersCreatedUserSpecified(){
                               return localFmVersCreatedUserTracker;
                           }

                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getFmVersCreatedUser(){
                               return localFmVersCreatedUser;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param FmVersCreatedUser
                               */
                               public void setFmVersCreatedUser(java.lang.String param){
                            localFmVersCreatedUserTracker = param != null;
                                   
                                            this.localFmVersCreatedUser=param;
                                    

                               }
                            

                        /**
                        * field for FmVersCreatedDate
                        */

                        
                                    protected java.util.Calendar localFmVersCreatedDate ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localFmVersCreatedDateTracker = false ;

                           public boolean isFmVersCreatedDateSpecified(){
                               return localFmVersCreatedDateTracker;
                           }

                           

                           /**
                           * Auto generated getter method
                           * @return java.util.Calendar
                           */
                           public  java.util.Calendar getFmVersCreatedDate(){
                               return localFmVersCreatedDate;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param FmVersCreatedDate
                               */
                               public void setFmVersCreatedDate(java.util.Calendar param){
                            localFmVersCreatedDateTracker = param != null;
                                   
                                            this.localFmVersCreatedDate=param;
                                    

                               }
                            

                        /**
                        * field for FmVersModifiedUser
                        */

                        
                                    protected java.lang.String localFmVersModifiedUser ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localFmVersModifiedUserTracker = false ;

                           public boolean isFmVersModifiedUserSpecified(){
                               return localFmVersModifiedUserTracker;
                           }

                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getFmVersModifiedUser(){
                               return localFmVersModifiedUser;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param FmVersModifiedUser
                               */
                               public void setFmVersModifiedUser(java.lang.String param){
                            localFmVersModifiedUserTracker = param != null;
                                   
                                            this.localFmVersModifiedUser=param;
                                    

                               }
                            

                        /**
                        * field for FmVersModifiedDate
                        */

                        
                                    protected java.util.Calendar localFmVersModifiedDate ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localFmVersModifiedDateTracker = false ;

                           public boolean isFmVersModifiedDateSpecified(){
                               return localFmVersModifiedDateTracker;
                           }

                           

                           /**
                           * Auto generated getter method
                           * @return java.util.Calendar
                           */
                           public  java.util.Calendar getFmVersModifiedDate(){
                               return localFmVersModifiedDate;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param FmVersModifiedDate
                               */
                               public void setFmVersModifiedDate(java.util.Calendar param){
                            localFmVersModifiedDateTracker = param != null;
                                   
                                            this.localFmVersModifiedDate=param;
                                    

                               }
                            

                        /**
                        * field for FmVersVersion
                        */

                        
                                    protected long localFmVersVersion ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localFmVersVersionTracker = false ;

                           public boolean isFmVersVersionSpecified(){
                               return localFmVersVersionTracker;
                           }

                           

                           /**
                           * Auto generated getter method
                           * @return long
                           */
                           public  long getFmVersVersion(){
                               return localFmVersVersion;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param FmVersVersion
                               */
                               public void setFmVersVersion(long param){
                            
                                       // setting primitive attribute tracker to true
                                       localFmVersVersionTracker =
                                       param != java.lang.Long.MIN_VALUE;
                                   
                                            this.localFmVersVersion=param;
                                    

                               }
                            

                        /**
                        * field for PidcVersChangeNum
                        */

                        
                                    protected long localPidcVersChangeNum ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localPidcVersChangeNumTracker = false ;

                           public boolean isPidcVersChangeNumSpecified(){
                               return localPidcVersChangeNumTracker;
                           }

                           

                           /**
                           * Auto generated getter method
                           * @return long
                           */
                           public  long getPidcVersChangeNum(){
                               return localPidcVersChangeNum;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param PidcVersChangeNum
                               */
                               public void setPidcVersChangeNum(long param){
                            
                                       // setting primitive attribute tracker to true
                                       localPidcVersChangeNumTracker =
                                       param != java.lang.Long.MIN_VALUE;
                                   
                                            this.localPidcVersChangeNum=param;
                                    

                               }
                            

                        /**
                        * field for PidcVersId
                        */

                        
                                    protected long localPidcVersId ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localPidcVersIdTracker = false ;

                           public boolean isPidcVersIdSpecified(){
                               return localPidcVersIdTracker;
                           }

                           

                           /**
                           * Auto generated getter method
                           * @return long
                           */
                           public  long getPidcVersId(){
                               return localPidcVersId;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param PidcVersId
                               */
                               public void setPidcVersId(long param){
                            
                                       // setting primitive attribute tracker to true
                                       localPidcVersIdTracker =
                                       param != java.lang.Long.MIN_VALUE;
                                   
                                            this.localPidcVersId=param;
                                    

                               }
                            

                        /**
                        * field for ChangeNumber
                        */

                        
                                    protected long localChangeNumber ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localChangeNumberTracker = false ;

                           public boolean isChangeNumberSpecified(){
                               return localChangeNumberTracker;
                           }

                           

                           /**
                           * Auto generated getter method
                           * @return long
                           */
                           public  long getChangeNumber(){
                               return localChangeNumber;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param ChangeNumber
                               */
                               public void setChangeNumber(long param){
                            
                                       // setting primitive attribute tracker to true
                                       localChangeNumberTracker =
                                       param != java.lang.Long.MIN_VALUE;
                                   
                                            this.localChangeNumber=param;
                                    

                               }
                            

     
     
        /**
        *
        * @param parentQName
        * @param factory
        * @return org.apache.axiom.om.OMElement
        */
       public org.apache.axiom.om.OMElement getOMElement (
               final javax.xml.namespace.QName parentQName,
               final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException{


        
               org.apache.axiom.om.OMDataSource dataSource =
                       new org.apache.axis2.databinding.ADBDataSource(this,parentQName);
               return factory.createOMElement(dataSource,parentQName);
            
        }

         public void serialize(final javax.xml.namespace.QName parentQName,
                                       javax.xml.stream.XMLStreamWriter xmlWriter)
                                throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException{
                           serialize(parentQName,xmlWriter,false);
         }

         public void serialize(final javax.xml.namespace.QName parentQName,
                               javax.xml.stream.XMLStreamWriter xmlWriter,
                               boolean serializeType)
            throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException{
            
                


                java.lang.String prefix = null;
                java.lang.String namespace = null;
                

                    prefix = parentQName.getPrefix();
                    namespace = parentQName.getNamespaceURI();
                    writeStartElement(prefix, namespace, parentQName.getLocalPart(), xmlWriter);
                
                  if (serializeType){
               

                   java.lang.String namespacePrefix = registerPrefix(xmlWriter,"http://ws.apic.caltool.bosch.com");
                   if ((namespacePrefix != null) && (namespacePrefix.trim().length() > 0)){
                       writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","type",
                           namespacePrefix+":PidcFocusMatrixVersType",
                           xmlWriter);
                   } else {
                       writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","type",
                           "PidcFocusMatrixVersType",
                           xmlWriter);
                   }

               
                   }
                if (localFmVersIdTracker){
                                    namespace = "";
                                    writeStartElement(null, namespace, "fmVersId", xmlWriter);
                             
                                               if (localFmVersId==java.lang.Long.MIN_VALUE) {
                                           
                                                         throw new org.apache.axis2.databinding.ADBException("fmVersId cannot be null!!");
                                                      
                                               } else {
                                                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localFmVersId));
                                               }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localOldFmVersNameTracker){
                                    namespace = "";
                                    writeStartElement(null, namespace, "oldFmVersName", xmlWriter);
                             

                                          if (localOldFmVersName==null){
                                              // write the nil attribute
                                              
                                                     throw new org.apache.axis2.databinding.ADBException("oldFmVersName cannot be null!!");
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(localOldFmVersName);
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localNewFmVersNameTracker){
                                    namespace = "";
                                    writeStartElement(null, namespace, "newFmVersName", xmlWriter);
                             

                                          if (localNewFmVersName==null){
                                              // write the nil attribute
                                              
                                                     throw new org.apache.axis2.databinding.ADBException("newFmVersName cannot be null!!");
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(localNewFmVersName);
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localFmRevNumTracker){
                                    namespace = "";
                                    writeStartElement(null, namespace, "fmRevNum", xmlWriter);
                             
                                               if (localFmRevNum==java.lang.Long.MIN_VALUE) {
                                           
                                                         throw new org.apache.axis2.databinding.ADBException("fmRevNum cannot be null!!");
                                                      
                                               } else {
                                                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localFmRevNum));
                                               }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localOldFmVersStatusTracker){
                                    namespace = "";
                                    writeStartElement(null, namespace, "oldFmVersStatus", xmlWriter);
                             

                                          if (localOldFmVersStatus==null){
                                              // write the nil attribute
                                              
                                                     throw new org.apache.axis2.databinding.ADBException("oldFmVersStatus cannot be null!!");
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(localOldFmVersStatus);
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localNewFmVersStatusTracker){
                                    namespace = "";
                                    writeStartElement(null, namespace, "newFmVersStatus", xmlWriter);
                             

                                          if (localNewFmVersStatus==null){
                                              // write the nil attribute
                                              
                                                     throw new org.apache.axis2.databinding.ADBException("newFmVersStatus cannot be null!!");
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(localNewFmVersStatus);
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localOldFmVersRvwUserTracker){
                                    namespace = "";
                                    writeStartElement(null, namespace, "oldFmVersRvwUser", xmlWriter);
                             

                                          if (localOldFmVersRvwUser==null){
                                              // write the nil attribute
                                              
                                                     throw new org.apache.axis2.databinding.ADBException("oldFmVersRvwUser cannot be null!!");
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(localOldFmVersRvwUser);
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localNewFmVersRvwUserTracker){
                                    namespace = "";
                                    writeStartElement(null, namespace, "newFmVersRvwUser", xmlWriter);
                             

                                          if (localNewFmVersRvwUser==null){
                                              // write the nil attribute
                                              
                                                     throw new org.apache.axis2.databinding.ADBException("newFmVersRvwUser cannot be null!!");
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(localNewFmVersRvwUser);
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localOldFmVersRvwDateTracker){
                                    namespace = "";
                                    writeStartElement(null, namespace, "oldFmVersRvwDate", xmlWriter);
                             

                                          if (localOldFmVersRvwDate==null){
                                              // write the nil attribute
                                              
                                                     throw new org.apache.axis2.databinding.ADBException("oldFmVersRvwDate cannot be null!!");
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localOldFmVersRvwDate));
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localOldFmVersRvwDateStrTracker){
                                    namespace = "";
                                    writeStartElement(null, namespace, "oldFmVersRvwDateStr", xmlWriter);
                             

                                          if (localOldFmVersRvwDateStr==null){
                                              // write the nil attribute
                                              
                                                     throw new org.apache.axis2.databinding.ADBException("oldFmVersRvwDateStr cannot be null!!");
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(localOldFmVersRvwDateStr);
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localNewFmVersRvwDateTracker){
                                    namespace = "";
                                    writeStartElement(null, namespace, "newFmVersRvwDate", xmlWriter);
                             

                                          if (localNewFmVersRvwDate==null){
                                              // write the nil attribute
                                              
                                                     throw new org.apache.axis2.databinding.ADBException("newFmVersRvwDate cannot be null!!");
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localNewFmVersRvwDate));
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localNewFmVersRvwDateStrTracker){
                                    namespace = "";
                                    writeStartElement(null, namespace, "newFmVersRvwDateStr", xmlWriter);
                             

                                          if (localNewFmVersRvwDateStr==null){
                                              // write the nil attribute
                                              
                                                     throw new org.apache.axis2.databinding.ADBException("newFmVersRvwDateStr cannot be null!!");
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(localNewFmVersRvwDateStr);
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localOldFmVersLinkTracker){
                                    namespace = "";
                                    writeStartElement(null, namespace, "oldFmVersLink", xmlWriter);
                             

                                          if (localOldFmVersLink==null){
                                              // write the nil attribute
                                              
                                                     throw new org.apache.axis2.databinding.ADBException("oldFmVersLink cannot be null!!");
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(localOldFmVersLink);
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localNewFmVersLinkTracker){
                                    namespace = "";
                                    writeStartElement(null, namespace, "newFmVersLink", xmlWriter);
                             

                                          if (localNewFmVersLink==null){
                                              // write the nil attribute
                                              
                                                     throw new org.apache.axis2.databinding.ADBException("newFmVersLink cannot be null!!");
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(localNewFmVersLink);
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localOldRemarkTracker){
                                    namespace = "";
                                    writeStartElement(null, namespace, "oldRemark", xmlWriter);
                             

                                          if (localOldRemark==null){
                                              // write the nil attribute
                                              
                                                     throw new org.apache.axis2.databinding.ADBException("oldRemark cannot be null!!");
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(localOldRemark);
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localNewRemarkTracker){
                                    namespace = "";
                                    writeStartElement(null, namespace, "newRemark", xmlWriter);
                             

                                          if (localNewRemark==null){
                                              // write the nil attribute
                                              
                                                     throw new org.apache.axis2.databinding.ADBException("newRemark cannot be null!!");
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(localNewRemark);
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localOldFmRvwStatusTracker){
                                    namespace = "";
                                    writeStartElement(null, namespace, "oldFmRvwStatus", xmlWriter);
                             

                                          if (localOldFmRvwStatus==null){
                                              // write the nil attribute
                                              
                                                     throw new org.apache.axis2.databinding.ADBException("oldFmRvwStatus cannot be null!!");
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(localOldFmRvwStatus);
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localNewFmRvwStatusTracker){
                                    namespace = "";
                                    writeStartElement(null, namespace, "newFmRvwStatus", xmlWriter);
                             

                                          if (localNewFmRvwStatus==null){
                                              // write the nil attribute
                                              
                                                     throw new org.apache.axis2.databinding.ADBException("newFmRvwStatus cannot be null!!");
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(localNewFmRvwStatus);
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localFmVersCreatedUserTracker){
                                    namespace = "";
                                    writeStartElement(null, namespace, "fmVersCreatedUser", xmlWriter);
                             

                                          if (localFmVersCreatedUser==null){
                                              // write the nil attribute
                                              
                                                     throw new org.apache.axis2.databinding.ADBException("fmVersCreatedUser cannot be null!!");
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(localFmVersCreatedUser);
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localFmVersCreatedDateTracker){
                                    namespace = "";
                                    writeStartElement(null, namespace, "fmVersCreatedDate", xmlWriter);
                             

                                          if (localFmVersCreatedDate==null){
                                              // write the nil attribute
                                              
                                                     throw new org.apache.axis2.databinding.ADBException("fmVersCreatedDate cannot be null!!");
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localFmVersCreatedDate));
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localFmVersModifiedUserTracker){
                                    namespace = "";
                                    writeStartElement(null, namespace, "fmVersModifiedUser", xmlWriter);
                             

                                          if (localFmVersModifiedUser==null){
                                              // write the nil attribute
                                              
                                                     throw new org.apache.axis2.databinding.ADBException("fmVersModifiedUser cannot be null!!");
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(localFmVersModifiedUser);
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localFmVersModifiedDateTracker){
                                    namespace = "";
                                    writeStartElement(null, namespace, "fmVersModifiedDate", xmlWriter);
                             

                                          if (localFmVersModifiedDate==null){
                                              // write the nil attribute
                                              
                                                     throw new org.apache.axis2.databinding.ADBException("fmVersModifiedDate cannot be null!!");
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localFmVersModifiedDate));
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localFmVersVersionTracker){
                                    namespace = "";
                                    writeStartElement(null, namespace, "fmVersVersion", xmlWriter);
                             
                                               if (localFmVersVersion==java.lang.Long.MIN_VALUE) {
                                           
                                                         throw new org.apache.axis2.databinding.ADBException("fmVersVersion cannot be null!!");
                                                      
                                               } else {
                                                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localFmVersVersion));
                                               }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localPidcVersChangeNumTracker){
                                    namespace = "";
                                    writeStartElement(null, namespace, "pidcVersChangeNum", xmlWriter);
                             
                                               if (localPidcVersChangeNum==java.lang.Long.MIN_VALUE) {
                                           
                                                         throw new org.apache.axis2.databinding.ADBException("pidcVersChangeNum cannot be null!!");
                                                      
                                               } else {
                                                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localPidcVersChangeNum));
                                               }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localPidcVersIdTracker){
                                    namespace = "";
                                    writeStartElement(null, namespace, "pidcVersId", xmlWriter);
                             
                                               if (localPidcVersId==java.lang.Long.MIN_VALUE) {
                                           
                                                         throw new org.apache.axis2.databinding.ADBException("pidcVersId cannot be null!!");
                                                      
                                               } else {
                                                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localPidcVersId));
                                               }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localChangeNumberTracker){
                                    namespace = "";
                                    writeStartElement(null, namespace, "changeNumber", xmlWriter);
                             
                                               if (localChangeNumber==java.lang.Long.MIN_VALUE) {
                                           
                                                         throw new org.apache.axis2.databinding.ADBException("changeNumber cannot be null!!");
                                                      
                                               } else {
                                                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localChangeNumber));
                                               }
                                    
                                   xmlWriter.writeEndElement();
                             }
                    xmlWriter.writeEndElement();
               

        }

        private static java.lang.String generatePrefix(java.lang.String namespace) {
            if(namespace.equals("http://ws.apic.caltool.bosch.com")){
                return "ns1";
            }
            return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
        }

        /**
         * Utility method to write an element start tag.
         */
        private void writeStartElement(java.lang.String prefix, java.lang.String namespace, java.lang.String localPart,
                                       javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            java.lang.String writerPrefix = xmlWriter.getPrefix(namespace);
            if (writerPrefix != null) {
                xmlWriter.writeStartElement(namespace, localPart);
            } else {
                if (namespace.length() == 0) {
                    prefix = "";
                } else if (prefix == null) {
                    prefix = generatePrefix(namespace);
                }

                xmlWriter.writeStartElement(prefix, localPart, namespace);
                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }
        }
        
        /**
         * Util method to write an attribute with the ns prefix
         */
        private void writeAttribute(java.lang.String prefix,java.lang.String namespace,java.lang.String attName,
                                    java.lang.String attValue,javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException{
            if (xmlWriter.getPrefix(namespace) == null) {
                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }
            xmlWriter.writeAttribute(namespace,attName,attValue);
        }

        /**
         * Util method to write an attribute without the ns prefix
         */
        private void writeAttribute(java.lang.String namespace,java.lang.String attName,
                                    java.lang.String attValue,javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException{
            if (namespace.equals("")) {
                xmlWriter.writeAttribute(attName,attValue);
            } else {
                registerPrefix(xmlWriter, namespace);
                xmlWriter.writeAttribute(namespace,attName,attValue);
            }
        }


           /**
             * Util method to write an attribute without the ns prefix
             */
            private void writeQNameAttribute(java.lang.String namespace, java.lang.String attName,
                                             javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {

                java.lang.String attributeNamespace = qname.getNamespaceURI();
                java.lang.String attributePrefix = xmlWriter.getPrefix(attributeNamespace);
                if (attributePrefix == null) {
                    attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
                }
                java.lang.String attributeValue;
                if (attributePrefix.trim().length() > 0) {
                    attributeValue = attributePrefix + ":" + qname.getLocalPart();
                } else {
                    attributeValue = qname.getLocalPart();
                }

                if (namespace.equals("")) {
                    xmlWriter.writeAttribute(attName, attributeValue);
                } else {
                    registerPrefix(xmlWriter, namespace);
                    xmlWriter.writeAttribute(namespace, attName, attributeValue);
                }
            }
        /**
         *  method to handle Qnames
         */

        private void writeQName(javax.xml.namespace.QName qname,
                                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            java.lang.String namespaceURI = qname.getNamespaceURI();
            if (namespaceURI != null) {
                java.lang.String prefix = xmlWriter.getPrefix(namespaceURI);
                if (prefix == null) {
                    prefix = generatePrefix(namespaceURI);
                    xmlWriter.writeNamespace(prefix, namespaceURI);
                    xmlWriter.setPrefix(prefix,namespaceURI);
                }

                if (prefix.trim().length() > 0){
                    xmlWriter.writeCharacters(prefix + ":" + org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
                } else {
                    // i.e this is the default namespace
                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
                }

            } else {
                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
            }
        }

        private void writeQNames(javax.xml.namespace.QName[] qnames,
                                 javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {

            if (qnames != null) {
                // we have to store this data until last moment since it is not possible to write any
                // namespace data after writing the charactor data
                java.lang.StringBuffer stringToWrite = new java.lang.StringBuffer();
                java.lang.String namespaceURI = null;
                java.lang.String prefix = null;

                for (int i = 0; i < qnames.length; i++) {
                    if (i > 0) {
                        stringToWrite.append(" ");
                    }
                    namespaceURI = qnames[i].getNamespaceURI();
                    if (namespaceURI != null) {
                        prefix = xmlWriter.getPrefix(namespaceURI);
                        if ((prefix == null) || (prefix.length() == 0)) {
                            prefix = generatePrefix(namespaceURI);
                            xmlWriter.writeNamespace(prefix, namespaceURI);
                            xmlWriter.setPrefix(prefix,namespaceURI);
                        }

                        if (prefix.trim().length() > 0){
                            stringToWrite.append(prefix).append(":").append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                        } else {
                            stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                        }
                    } else {
                        stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                    }
                }
                xmlWriter.writeCharacters(stringToWrite.toString());
            }

        }


        /**
         * Register a namespace prefix
         */
        private java.lang.String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter, java.lang.String namespace) throws javax.xml.stream.XMLStreamException {
            java.lang.String prefix = xmlWriter.getPrefix(namespace);
            if (prefix == null) {
                prefix = generatePrefix(namespace);
                javax.xml.namespace.NamespaceContext nsContext = xmlWriter.getNamespaceContext();
                while (true) {
                    java.lang.String uri = nsContext.getNamespaceURI(prefix);
                    if (uri == null || uri.length() == 0) {
                        break;
                    }
                    prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
                }
                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }
            return prefix;
        }


  
        /**
        * databinding method to get an XML representation of this object
        *
        */
        public javax.xml.stream.XMLStreamReader getPullParser(javax.xml.namespace.QName qName)
                    throws org.apache.axis2.databinding.ADBException{


        
                 java.util.ArrayList elementList = new java.util.ArrayList();
                 java.util.ArrayList attribList = new java.util.ArrayList();

                 if (localFmVersIdTracker){
                                      elementList.add(new javax.xml.namespace.QName("",
                                                                      "fmVersId"));
                                 
                                elementList.add(
                                   org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localFmVersId));
                            } if (localOldFmVersNameTracker){
                                      elementList.add(new javax.xml.namespace.QName("",
                                                                      "oldFmVersName"));
                                 
                                        if (localOldFmVersName != null){
                                            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localOldFmVersName));
                                        } else {
                                           throw new org.apache.axis2.databinding.ADBException("oldFmVersName cannot be null!!");
                                        }
                                    } if (localNewFmVersNameTracker){
                                      elementList.add(new javax.xml.namespace.QName("",
                                                                      "newFmVersName"));
                                 
                                        if (localNewFmVersName != null){
                                            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localNewFmVersName));
                                        } else {
                                           throw new org.apache.axis2.databinding.ADBException("newFmVersName cannot be null!!");
                                        }
                                    } if (localFmRevNumTracker){
                                      elementList.add(new javax.xml.namespace.QName("",
                                                                      "fmRevNum"));
                                 
                                elementList.add(
                                   org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localFmRevNum));
                            } if (localOldFmVersStatusTracker){
                                      elementList.add(new javax.xml.namespace.QName("",
                                                                      "oldFmVersStatus"));
                                 
                                        if (localOldFmVersStatus != null){
                                            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localOldFmVersStatus));
                                        } else {
                                           throw new org.apache.axis2.databinding.ADBException("oldFmVersStatus cannot be null!!");
                                        }
                                    } if (localNewFmVersStatusTracker){
                                      elementList.add(new javax.xml.namespace.QName("",
                                                                      "newFmVersStatus"));
                                 
                                        if (localNewFmVersStatus != null){
                                            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localNewFmVersStatus));
                                        } else {
                                           throw new org.apache.axis2.databinding.ADBException("newFmVersStatus cannot be null!!");
                                        }
                                    } if (localOldFmVersRvwUserTracker){
                                      elementList.add(new javax.xml.namespace.QName("",
                                                                      "oldFmVersRvwUser"));
                                 
                                        if (localOldFmVersRvwUser != null){
                                            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localOldFmVersRvwUser));
                                        } else {
                                           throw new org.apache.axis2.databinding.ADBException("oldFmVersRvwUser cannot be null!!");
                                        }
                                    } if (localNewFmVersRvwUserTracker){
                                      elementList.add(new javax.xml.namespace.QName("",
                                                                      "newFmVersRvwUser"));
                                 
                                        if (localNewFmVersRvwUser != null){
                                            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localNewFmVersRvwUser));
                                        } else {
                                           throw new org.apache.axis2.databinding.ADBException("newFmVersRvwUser cannot be null!!");
                                        }
                                    } if (localOldFmVersRvwDateTracker){
                                      elementList.add(new javax.xml.namespace.QName("",
                                                                      "oldFmVersRvwDate"));
                                 
                                        if (localOldFmVersRvwDate != null){
                                            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localOldFmVersRvwDate));
                                        } else {
                                           throw new org.apache.axis2.databinding.ADBException("oldFmVersRvwDate cannot be null!!");
                                        }
                                    } if (localOldFmVersRvwDateStrTracker){
                                      elementList.add(new javax.xml.namespace.QName("",
                                                                      "oldFmVersRvwDateStr"));
                                 
                                        if (localOldFmVersRvwDateStr != null){
                                            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localOldFmVersRvwDateStr));
                                        } else {
                                           throw new org.apache.axis2.databinding.ADBException("oldFmVersRvwDateStr cannot be null!!");
                                        }
                                    } if (localNewFmVersRvwDateTracker){
                                      elementList.add(new javax.xml.namespace.QName("",
                                                                      "newFmVersRvwDate"));
                                 
                                        if (localNewFmVersRvwDate != null){
                                            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localNewFmVersRvwDate));
                                        } else {
                                           throw new org.apache.axis2.databinding.ADBException("newFmVersRvwDate cannot be null!!");
                                        }
                                    } if (localNewFmVersRvwDateStrTracker){
                                      elementList.add(new javax.xml.namespace.QName("",
                                                                      "newFmVersRvwDateStr"));
                                 
                                        if (localNewFmVersRvwDateStr != null){
                                            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localNewFmVersRvwDateStr));
                                        } else {
                                           throw new org.apache.axis2.databinding.ADBException("newFmVersRvwDateStr cannot be null!!");
                                        }
                                    } if (localOldFmVersLinkTracker){
                                      elementList.add(new javax.xml.namespace.QName("",
                                                                      "oldFmVersLink"));
                                 
                                        if (localOldFmVersLink != null){
                                            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localOldFmVersLink));
                                        } else {
                                           throw new org.apache.axis2.databinding.ADBException("oldFmVersLink cannot be null!!");
                                        }
                                    } if (localNewFmVersLinkTracker){
                                      elementList.add(new javax.xml.namespace.QName("",
                                                                      "newFmVersLink"));
                                 
                                        if (localNewFmVersLink != null){
                                            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localNewFmVersLink));
                                        } else {
                                           throw new org.apache.axis2.databinding.ADBException("newFmVersLink cannot be null!!");
                                        }
                                    } if (localOldRemarkTracker){
                                      elementList.add(new javax.xml.namespace.QName("",
                                                                      "oldRemark"));
                                 
                                        if (localOldRemark != null){
                                            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localOldRemark));
                                        } else {
                                           throw new org.apache.axis2.databinding.ADBException("oldRemark cannot be null!!");
                                        }
                                    } if (localNewRemarkTracker){
                                      elementList.add(new javax.xml.namespace.QName("",
                                                                      "newRemark"));
                                 
                                        if (localNewRemark != null){
                                            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localNewRemark));
                                        } else {
                                           throw new org.apache.axis2.databinding.ADBException("newRemark cannot be null!!");
                                        }
                                    } if (localOldFmRvwStatusTracker){
                                      elementList.add(new javax.xml.namespace.QName("",
                                                                      "oldFmRvwStatus"));
                                 
                                        if (localOldFmRvwStatus != null){
                                            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localOldFmRvwStatus));
                                        } else {
                                           throw new org.apache.axis2.databinding.ADBException("oldFmRvwStatus cannot be null!!");
                                        }
                                    } if (localNewFmRvwStatusTracker){
                                      elementList.add(new javax.xml.namespace.QName("",
                                                                      "newFmRvwStatus"));
                                 
                                        if (localNewFmRvwStatus != null){
                                            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localNewFmRvwStatus));
                                        } else {
                                           throw new org.apache.axis2.databinding.ADBException("newFmRvwStatus cannot be null!!");
                                        }
                                    } if (localFmVersCreatedUserTracker){
                                      elementList.add(new javax.xml.namespace.QName("",
                                                                      "fmVersCreatedUser"));
                                 
                                        if (localFmVersCreatedUser != null){
                                            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localFmVersCreatedUser));
                                        } else {
                                           throw new org.apache.axis2.databinding.ADBException("fmVersCreatedUser cannot be null!!");
                                        }
                                    } if (localFmVersCreatedDateTracker){
                                      elementList.add(new javax.xml.namespace.QName("",
                                                                      "fmVersCreatedDate"));
                                 
                                        if (localFmVersCreatedDate != null){
                                            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localFmVersCreatedDate));
                                        } else {
                                           throw new org.apache.axis2.databinding.ADBException("fmVersCreatedDate cannot be null!!");
                                        }
                                    } if (localFmVersModifiedUserTracker){
                                      elementList.add(new javax.xml.namespace.QName("",
                                                                      "fmVersModifiedUser"));
                                 
                                        if (localFmVersModifiedUser != null){
                                            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localFmVersModifiedUser));
                                        } else {
                                           throw new org.apache.axis2.databinding.ADBException("fmVersModifiedUser cannot be null!!");
                                        }
                                    } if (localFmVersModifiedDateTracker){
                                      elementList.add(new javax.xml.namespace.QName("",
                                                                      "fmVersModifiedDate"));
                                 
                                        if (localFmVersModifiedDate != null){
                                            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localFmVersModifiedDate));
                                        } else {
                                           throw new org.apache.axis2.databinding.ADBException("fmVersModifiedDate cannot be null!!");
                                        }
                                    } if (localFmVersVersionTracker){
                                      elementList.add(new javax.xml.namespace.QName("",
                                                                      "fmVersVersion"));
                                 
                                elementList.add(
                                   org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localFmVersVersion));
                            } if (localPidcVersChangeNumTracker){
                                      elementList.add(new javax.xml.namespace.QName("",
                                                                      "pidcVersChangeNum"));
                                 
                                elementList.add(
                                   org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localPidcVersChangeNum));
                            } if (localPidcVersIdTracker){
                                      elementList.add(new javax.xml.namespace.QName("",
                                                                      "pidcVersId"));
                                 
                                elementList.add(
                                   org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localPidcVersId));
                            } if (localChangeNumberTracker){
                                      elementList.add(new javax.xml.namespace.QName("",
                                                                      "changeNumber"));
                                 
                                elementList.add(
                                   org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localChangeNumber));
                            }

                return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(), attribList.toArray());
            
            

        }

  

     /**
      *  Factory class that keeps the parse method
      */
    public static class Factory{

        
        

        /**
        * static method to create the object
        * Precondition:  If this object is an element, the current or next start element starts this object and any intervening reader events are ignorable
        *                If this object is not an element, it is a complex type and the reader is at the event just after the outer start element
        * Postcondition: If this object is an element, the reader is positioned at its end element
        *                If this object is a complex type, the reader is positioned at the end element of its outer element
        */
        public static PidcFocusMatrixVersType parse(javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception{
            PidcFocusMatrixVersType object =
                new PidcFocusMatrixVersType();

            int event;
            java.lang.String nillableValue = null;
            java.lang.String prefix ="";
            java.lang.String namespaceuri ="";
            try {
                
                while (!reader.isStartElement() && !reader.isEndElement())
                    reader.next();

                
                if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","type")!=null){
                  java.lang.String fullTypeName = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance",
                        "type");
                  if (fullTypeName!=null){
                    java.lang.String nsPrefix = null;
                    if (fullTypeName.indexOf(":") > -1){
                        nsPrefix = fullTypeName.substring(0,fullTypeName.indexOf(":"));
                    }
                    nsPrefix = nsPrefix==null?"":nsPrefix;

                    java.lang.String type = fullTypeName.substring(fullTypeName.indexOf(":")+1);
                    
                            if (!"PidcFocusMatrixVersType".equals(type)){
                                //find namespace for the prefix
                                java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                                return (PidcFocusMatrixVersType)com.bosch.caltool.apic.ws.ExtensionMapper.getTypeObject(
                                     nsUri,type,reader);
                              }
                        

                  }
                

                }

                

                
                // Note all attributes that were handled. Used to differ normal attributes
                // from anyAttributes.
                java.util.Vector handledAttributes = new java.util.Vector();
                

                
                    
                    reader.next();
                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("","fmVersId").equals(reader.getName())){
                                
                                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                    if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                        throw new org.apache.axis2.databinding.ADBException("The element: "+"fmVersId" +"  cannot be null");
                                    }
                                    

                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setFmVersId(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToLong(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                               object.setFmVersId(java.lang.Long.MIN_VALUE);
                                           
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("","oldFmVersName").equals(reader.getName())){
                                
                                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                    if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                        throw new org.apache.axis2.databinding.ADBException("The element: "+"oldFmVersName" +"  cannot be null");
                                    }
                                    

                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setOldFmVersName(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("","newFmVersName").equals(reader.getName())){
                                
                                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                    if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                        throw new org.apache.axis2.databinding.ADBException("The element: "+"newFmVersName" +"  cannot be null");
                                    }
                                    

                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setNewFmVersName(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("","fmRevNum").equals(reader.getName())){
                                
                                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                    if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                        throw new org.apache.axis2.databinding.ADBException("The element: "+"fmRevNum" +"  cannot be null");
                                    }
                                    

                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setFmRevNum(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToLong(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                               object.setFmRevNum(java.lang.Long.MIN_VALUE);
                                           
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("","oldFmVersStatus").equals(reader.getName())){
                                
                                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                    if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                        throw new org.apache.axis2.databinding.ADBException("The element: "+"oldFmVersStatus" +"  cannot be null");
                                    }
                                    

                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setOldFmVersStatus(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("","newFmVersStatus").equals(reader.getName())){
                                
                                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                    if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                        throw new org.apache.axis2.databinding.ADBException("The element: "+"newFmVersStatus" +"  cannot be null");
                                    }
                                    

                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setNewFmVersStatus(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("","oldFmVersRvwUser").equals(reader.getName())){
                                
                                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                    if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                        throw new org.apache.axis2.databinding.ADBException("The element: "+"oldFmVersRvwUser" +"  cannot be null");
                                    }
                                    

                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setOldFmVersRvwUser(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("","newFmVersRvwUser").equals(reader.getName())){
                                
                                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                    if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                        throw new org.apache.axis2.databinding.ADBException("The element: "+"newFmVersRvwUser" +"  cannot be null");
                                    }
                                    

                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setNewFmVersRvwUser(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("","oldFmVersRvwDate").equals(reader.getName())){
                                
                                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                    if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                        throw new org.apache.axis2.databinding.ADBException("The element: "+"oldFmVersRvwDate" +"  cannot be null");
                                    }
                                    

                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setOldFmVersRvwDate(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToDateTime(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("","oldFmVersRvwDateStr").equals(reader.getName())){
                                
                                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                    if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                        throw new org.apache.axis2.databinding.ADBException("The element: "+"oldFmVersRvwDateStr" +"  cannot be null");
                                    }
                                    

                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setOldFmVersRvwDateStr(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("","newFmVersRvwDate").equals(reader.getName())){
                                
                                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                    if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                        throw new org.apache.axis2.databinding.ADBException("The element: "+"newFmVersRvwDate" +"  cannot be null");
                                    }
                                    

                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setNewFmVersRvwDate(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToDateTime(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("","newFmVersRvwDateStr").equals(reader.getName())){
                                
                                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                    if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                        throw new org.apache.axis2.databinding.ADBException("The element: "+"newFmVersRvwDateStr" +"  cannot be null");
                                    }
                                    

                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setNewFmVersRvwDateStr(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("","oldFmVersLink").equals(reader.getName())){
                                
                                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                    if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                        throw new org.apache.axis2.databinding.ADBException("The element: "+"oldFmVersLink" +"  cannot be null");
                                    }
                                    

                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setOldFmVersLink(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("","newFmVersLink").equals(reader.getName())){
                                
                                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                    if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                        throw new org.apache.axis2.databinding.ADBException("The element: "+"newFmVersLink" +"  cannot be null");
                                    }
                                    

                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setNewFmVersLink(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("","oldRemark").equals(reader.getName())){
                                
                                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                    if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                        throw new org.apache.axis2.databinding.ADBException("The element: "+"oldRemark" +"  cannot be null");
                                    }
                                    

                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setOldRemark(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("","newRemark").equals(reader.getName())){
                                
                                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                    if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                        throw new org.apache.axis2.databinding.ADBException("The element: "+"newRemark" +"  cannot be null");
                                    }
                                    

                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setNewRemark(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("","oldFmRvwStatus").equals(reader.getName())){
                                
                                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                    if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                        throw new org.apache.axis2.databinding.ADBException("The element: "+"oldFmRvwStatus" +"  cannot be null");
                                    }
                                    

                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setOldFmRvwStatus(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("","newFmRvwStatus").equals(reader.getName())){
                                
                                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                    if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                        throw new org.apache.axis2.databinding.ADBException("The element: "+"newFmRvwStatus" +"  cannot be null");
                                    }
                                    

                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setNewFmRvwStatus(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("","fmVersCreatedUser").equals(reader.getName())){
                                
                                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                    if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                        throw new org.apache.axis2.databinding.ADBException("The element: "+"fmVersCreatedUser" +"  cannot be null");
                                    }
                                    

                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setFmVersCreatedUser(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("","fmVersCreatedDate").equals(reader.getName())){
                                
                                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                    if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                        throw new org.apache.axis2.databinding.ADBException("The element: "+"fmVersCreatedDate" +"  cannot be null");
                                    }
                                    

                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setFmVersCreatedDate(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToDateTime(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("","fmVersModifiedUser").equals(reader.getName())){
                                
                                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                    if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                        throw new org.apache.axis2.databinding.ADBException("The element: "+"fmVersModifiedUser" +"  cannot be null");
                                    }
                                    

                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setFmVersModifiedUser(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("","fmVersModifiedDate").equals(reader.getName())){
                                
                                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                    if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                        throw new org.apache.axis2.databinding.ADBException("The element: "+"fmVersModifiedDate" +"  cannot be null");
                                    }
                                    

                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setFmVersModifiedDate(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToDateTime(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("","fmVersVersion").equals(reader.getName())){
                                
                                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                    if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                        throw new org.apache.axis2.databinding.ADBException("The element: "+"fmVersVersion" +"  cannot be null");
                                    }
                                    

                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setFmVersVersion(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToLong(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                               object.setFmVersVersion(java.lang.Long.MIN_VALUE);
                                           
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("","pidcVersChangeNum").equals(reader.getName())){
                                
                                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                    if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                        throw new org.apache.axis2.databinding.ADBException("The element: "+"pidcVersChangeNum" +"  cannot be null");
                                    }
                                    

                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setPidcVersChangeNum(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToLong(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                               object.setPidcVersChangeNum(java.lang.Long.MIN_VALUE);
                                           
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("","pidcVersId").equals(reader.getName())){
                                
                                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                    if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                        throw new org.apache.axis2.databinding.ADBException("The element: "+"pidcVersId" +"  cannot be null");
                                    }
                                    

                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setPidcVersId(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToLong(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                               object.setPidcVersId(java.lang.Long.MIN_VALUE);
                                           
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("","changeNumber").equals(reader.getName())){
                                
                                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                    if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                        throw new org.apache.axis2.databinding.ADBException("The element: "+"changeNumber" +"  cannot be null");
                                    }
                                    

                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setChangeNumber(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToLong(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                               object.setChangeNumber(java.lang.Long.MIN_VALUE);
                                           
                                    }
                                  
                            while (!reader.isStartElement() && !reader.isEndElement())
                                reader.next();
                            
                                if (reader.isStartElement())
                                // A start element we are not expecting indicates a trailing invalid property
                                throw new org.apache.axis2.databinding.ADBException("Unexpected subelement " + reader.getName());
                            



            } catch (javax.xml.stream.XMLStreamException e) {
                throw new java.lang.Exception(e);
            }

            return object;
        }

        }//end of factory class

        

        }
           
    