
/**
 * PidcFocusMatrixType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.2  Built on : Apr 17, 2012 (05:34:40 IST)
 */

            
                package com.bosch.caltool.apic.ws;
            

            /**
            *  PidcFocusMatrixType bean class
            */
            @SuppressWarnings({"unchecked","unused"})
        
        public  class PidcFocusMatrixType
        implements org.apache.axis2.databinding.ADBBean{
        /* This type was generated from the piece of schema that had
                name = PidcFocusMatrixType
                Namespace URI = http://ws.apic.caltool.bosch.com
                Namespace Prefix = ns1
                */
            

                        /**
                        * field for FmId
                        */

                        
                                    protected long localFmId ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localFmIdTracker = false ;

                           public boolean isFmIdSpecified(){
                               return localFmIdTracker;
                           }

                           

                           /**
                           * Auto generated getter method
                           * @return long
                           */
                           public  long getFmId(){
                               return localFmId;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param FmId
                               */
                               public void setFmId(long param){
                            
                                       // setting primitive attribute tracker to true
                                       localFmIdTracker =
                                       param != java.lang.Long.MIN_VALUE;
                                   
                                            this.localFmId=param;
                                    

                               }
                            

                        /**
                        * field for FmUcpaId
                        */

                        
                                    protected long localFmUcpaId ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localFmUcpaIdTracker = false ;

                           public boolean isFmUcpaIdSpecified(){
                               return localFmUcpaIdTracker;
                           }

                           

                           /**
                           * Auto generated getter method
                           * @return long
                           */
                           public  long getFmUcpaId(){
                               return localFmUcpaId;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param FmUcpaId
                               */
                               public void setFmUcpaId(long param){
                            
                                       // setting primitive attribute tracker to true
                                       localFmUcpaIdTracker =
                                       param != java.lang.Long.MIN_VALUE;
                                   
                                            this.localFmUcpaId=param;
                                    

                               }
                            

                        /**
                        * field for OldFmColorCode
                        */

                        
                                    protected java.lang.String localOldFmColorCode ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localOldFmColorCodeTracker = false ;

                           public boolean isOldFmColorCodeSpecified(){
                               return localOldFmColorCodeTracker;
                           }

                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getOldFmColorCode(){
                               return localOldFmColorCode;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param OldFmColorCode
                               */
                               public void setOldFmColorCode(java.lang.String param){
                            localOldFmColorCodeTracker = param != null;
                                   
                                            this.localOldFmColorCode=param;
                                    

                               }
                            

                        /**
                        * field for NewFmColorCode
                        */

                        
                                    protected java.lang.String localNewFmColorCode ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localNewFmColorCodeTracker = false ;

                           public boolean isNewFmColorCodeSpecified(){
                               return localNewFmColorCodeTracker;
                           }

                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getNewFmColorCode(){
                               return localNewFmColorCode;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param NewFmColorCode
                               */
                               public void setNewFmColorCode(java.lang.String param){
                            localNewFmColorCodeTracker = param != null;
                                   
                                            this.localNewFmColorCode=param;
                                    

                               }
                            

                        /**
                        * field for OldComments
                        */

                        
                                    protected java.lang.String localOldComments ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localOldCommentsTracker = false ;

                           public boolean isOldCommentsSpecified(){
                               return localOldCommentsTracker;
                           }

                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getOldComments(){
                               return localOldComments;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param OldComments
                               */
                               public void setOldComments(java.lang.String param){
                            localOldCommentsTracker = param != null;
                                   
                                            this.localOldComments=param;
                                    

                               }
                            

                        /**
                        * field for NewComments
                        */

                        
                                    protected java.lang.String localNewComments ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localNewCommentsTracker = false ;

                           public boolean isNewCommentsSpecified(){
                               return localNewCommentsTracker;
                           }

                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getNewComments(){
                               return localNewComments;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param NewComments
                               */
                               public void setNewComments(java.lang.String param){
                            localNewCommentsTracker = param != null;
                                   
                                            this.localNewComments=param;
                                    

                               }
                            

                        /**
                        * field for CreatedUser
                        */

                        
                                    protected java.lang.String localCreatedUser ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localCreatedUserTracker = false ;

                           public boolean isCreatedUserSpecified(){
                               return localCreatedUserTracker;
                           }

                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getCreatedUser(){
                               return localCreatedUser;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param CreatedUser
                               */
                               public void setCreatedUser(java.lang.String param){
                            localCreatedUserTracker = param != null;
                                   
                                            this.localCreatedUser=param;
                                    

                               }
                            

                        /**
                        * field for CreatedDate
                        */

                        
                                    protected java.util.Calendar localCreatedDate ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localCreatedDateTracker = false ;

                           public boolean isCreatedDateSpecified(){
                               return localCreatedDateTracker;
                           }

                           

                           /**
                           * Auto generated getter method
                           * @return java.util.Calendar
                           */
                           public  java.util.Calendar getCreatedDate(){
                               return localCreatedDate;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param CreatedDate
                               */
                               public void setCreatedDate(java.util.Calendar param){
                            localCreatedDateTracker = param != null;
                                   
                                            this.localCreatedDate=param;
                                    

                               }
                            

                        /**
                        * field for ModifiedUser
                        */

                        
                                    protected java.lang.String localModifiedUser ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localModifiedUserTracker = false ;

                           public boolean isModifiedUserSpecified(){
                               return localModifiedUserTracker;
                           }

                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getModifiedUser(){
                               return localModifiedUser;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param ModifiedUser
                               */
                               public void setModifiedUser(java.lang.String param){
                            localModifiedUserTracker = param != null;
                                   
                                            this.localModifiedUser=param;
                                    

                               }
                            

                        /**
                        * field for ModifiedDate
                        */

                        
                                    protected java.util.Calendar localModifiedDate ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localModifiedDateTracker = false ;

                           public boolean isModifiedDateSpecified(){
                               return localModifiedDateTracker;
                           }

                           

                           /**
                           * Auto generated getter method
                           * @return java.util.Calendar
                           */
                           public  java.util.Calendar getModifiedDate(){
                               return localModifiedDate;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param ModifiedDate
                               */
                               public void setModifiedDate(java.util.Calendar param){
                            localModifiedDateTracker = param != null;
                                   
                                            this.localModifiedDate=param;
                                    

                               }
                            

                        /**
                        * field for FmVersion
                        */

                        
                                    protected long localFmVersion ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localFmVersionTracker = false ;

                           public boolean isFmVersionSpecified(){
                               return localFmVersionTracker;
                           }

                           

                           /**
                           * Auto generated getter method
                           * @return long
                           */
                           public  long getFmVersion(){
                               return localFmVersion;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param FmVersion
                               */
                               public void setFmVersion(long param){
                            
                                       // setting primitive attribute tracker to true
                                       localFmVersionTracker =
                                       param != java.lang.Long.MIN_VALUE;
                                   
                                            this.localFmVersion=param;
                                    

                               }
                            

                        /**
                        * field for OldFmLink
                        */

                        
                                    protected java.lang.String localOldFmLink ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localOldFmLinkTracker = false ;

                           public boolean isOldFmLinkSpecified(){
                               return localOldFmLinkTracker;
                           }

                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getOldFmLink(){
                               return localOldFmLink;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param OldFmLink
                               */
                               public void setOldFmLink(java.lang.String param){
                            localOldFmLinkTracker = param != null;
                                   
                                            this.localOldFmLink=param;
                                    

                               }
                            

                        /**
                        * field for NewFmLink
                        */

                        
                                    protected java.lang.String localNewFmLink ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localNewFmLinkTracker = false ;

                           public boolean isNewFmLinkSpecified(){
                               return localNewFmLinkTracker;
                           }

                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getNewFmLink(){
                               return localNewFmLink;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param NewFmLink
                               */
                               public void setNewFmLink(java.lang.String param){
                            localNewFmLinkTracker = param != null;
                                   
                                            this.localNewFmLink=param;
                                    

                               }
                            

                        /**
                        * field for UseCaseId
                        */

                        
                                    protected long localUseCaseId ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localUseCaseIdTracker = false ;

                           public boolean isUseCaseIdSpecified(){
                               return localUseCaseIdTracker;
                           }

                           

                           /**
                           * Auto generated getter method
                           * @return long
                           */
                           public  long getUseCaseId(){
                               return localUseCaseId;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param UseCaseId
                               */
                               public void setUseCaseId(long param){
                            
                                       // setting primitive attribute tracker to true
                                       localUseCaseIdTracker =
                                       param != java.lang.Long.MIN_VALUE;
                                   
                                            this.localUseCaseId=param;
                                    

                               }
                            

                        /**
                        * field for SectionId
                        */

                        
                                    protected long localSectionId ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localSectionIdTracker = false ;

                           public boolean isSectionIdSpecified(){
                               return localSectionIdTracker;
                           }

                           

                           /**
                           * Auto generated getter method
                           * @return long
                           */
                           public  long getSectionId(){
                               return localSectionId;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param SectionId
                               */
                               public void setSectionId(long param){
                            
                                       // setting primitive attribute tracker to true
                                       localSectionIdTracker =
                                       param != java.lang.Long.MIN_VALUE;
                                   
                                            this.localSectionId=param;
                                    

                               }
                            

                        /**
                        * field for AttrId
                        */

                        
                                    protected long localAttrId ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localAttrIdTracker = false ;

                           public boolean isAttrIdSpecified(){
                               return localAttrIdTracker;
                           }

                           

                           /**
                           * Auto generated getter method
                           * @return long
                           */
                           public  long getAttrId(){
                               return localAttrId;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param AttrId
                               */
                               public void setAttrId(long param){
                            
                                       // setting primitive attribute tracker to true
                                       localAttrIdTracker =
                                       param != java.lang.Long.MIN_VALUE;
                                   
                                            this.localAttrId=param;
                                    

                               }
                            

                        /**
                        * field for OldDeletedFlag
                        */

                        
                                    protected java.lang.String localOldDeletedFlag ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localOldDeletedFlagTracker = false ;

                           public boolean isOldDeletedFlagSpecified(){
                               return localOldDeletedFlagTracker;
                           }

                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getOldDeletedFlag(){
                               return localOldDeletedFlag;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param OldDeletedFlag
                               */
                               public void setOldDeletedFlag(java.lang.String param){
                            localOldDeletedFlagTracker = param != null;
                                   
                                            this.localOldDeletedFlag=param;
                                    

                               }
                            

                        /**
                        * field for NewDeletedFlag
                        */

                        
                                    protected java.lang.String localNewDeletedFlag ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localNewDeletedFlagTracker = false ;

                           public boolean isNewDeletedFlagSpecified(){
                               return localNewDeletedFlagTracker;
                           }

                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getNewDeletedFlag(){
                               return localNewDeletedFlag;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param NewDeletedFlag
                               */
                               public void setNewDeletedFlag(java.lang.String param){
                            localNewDeletedFlagTracker = param != null;
                                   
                                            this.localNewDeletedFlag=param;
                                    

                               }
                            

                        /**
                        * field for FmVersionId
                        */

                        
                                    protected long localFmVersionId ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localFmVersionIdTracker = false ;

                           public boolean isFmVersionIdSpecified(){
                               return localFmVersionIdTracker;
                           }

                           

                           /**
                           * Auto generated getter method
                           * @return long
                           */
                           public  long getFmVersionId(){
                               return localFmVersionId;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param FmVersionId
                               */
                               public void setFmVersionId(long param){
                            
                                       // setting primitive attribute tracker to true
                                       localFmVersionIdTracker =
                                       param != java.lang.Long.MIN_VALUE;
                                   
                                            this.localFmVersionId=param;
                                    

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
                           namespacePrefix+":PidcFocusMatrixType",
                           xmlWriter);
                   } else {
                       writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","type",
                           "PidcFocusMatrixType",
                           xmlWriter);
                   }

               
                   }
                if (localFmIdTracker){
                                    namespace = "";
                                    writeStartElement(null, namespace, "fmId", xmlWriter);
                             
                                               if (localFmId==java.lang.Long.MIN_VALUE) {
                                           
                                                         throw new org.apache.axis2.databinding.ADBException("fmId cannot be null!!");
                                                      
                                               } else {
                                                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localFmId));
                                               }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localFmUcpaIdTracker){
                                    namespace = "";
                                    writeStartElement(null, namespace, "fmUcpaId", xmlWriter);
                             
                                               if (localFmUcpaId==java.lang.Long.MIN_VALUE) {
                                           
                                                         throw new org.apache.axis2.databinding.ADBException("fmUcpaId cannot be null!!");
                                                      
                                               } else {
                                                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localFmUcpaId));
                                               }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localOldFmColorCodeTracker){
                                    namespace = "";
                                    writeStartElement(null, namespace, "oldFmColorCode", xmlWriter);
                             

                                          if (localOldFmColorCode==null){
                                              // write the nil attribute
                                              
                                                     throw new org.apache.axis2.databinding.ADBException("oldFmColorCode cannot be null!!");
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(localOldFmColorCode);
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localNewFmColorCodeTracker){
                                    namespace = "";
                                    writeStartElement(null, namespace, "newFmColorCode", xmlWriter);
                             

                                          if (localNewFmColorCode==null){
                                              // write the nil attribute
                                              
                                                     throw new org.apache.axis2.databinding.ADBException("newFmColorCode cannot be null!!");
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(localNewFmColorCode);
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localOldCommentsTracker){
                                    namespace = "";
                                    writeStartElement(null, namespace, "oldComments", xmlWriter);
                             

                                          if (localOldComments==null){
                                              // write the nil attribute
                                              
                                                     throw new org.apache.axis2.databinding.ADBException("oldComments cannot be null!!");
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(localOldComments);
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localNewCommentsTracker){
                                    namespace = "";
                                    writeStartElement(null, namespace, "newComments", xmlWriter);
                             

                                          if (localNewComments==null){
                                              // write the nil attribute
                                              
                                                     throw new org.apache.axis2.databinding.ADBException("newComments cannot be null!!");
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(localNewComments);
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localCreatedUserTracker){
                                    namespace = "";
                                    writeStartElement(null, namespace, "createdUser", xmlWriter);
                             

                                          if (localCreatedUser==null){
                                              // write the nil attribute
                                              
                                                     throw new org.apache.axis2.databinding.ADBException("createdUser cannot be null!!");
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(localCreatedUser);
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localCreatedDateTracker){
                                    namespace = "";
                                    writeStartElement(null, namespace, "createdDate", xmlWriter);
                             

                                          if (localCreatedDate==null){
                                              // write the nil attribute
                                              
                                                     throw new org.apache.axis2.databinding.ADBException("createdDate cannot be null!!");
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localCreatedDate));
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localModifiedUserTracker){
                                    namespace = "";
                                    writeStartElement(null, namespace, "modifiedUser", xmlWriter);
                             

                                          if (localModifiedUser==null){
                                              // write the nil attribute
                                              
                                                     throw new org.apache.axis2.databinding.ADBException("modifiedUser cannot be null!!");
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(localModifiedUser);
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localModifiedDateTracker){
                                    namespace = "";
                                    writeStartElement(null, namespace, "modifiedDate", xmlWriter);
                             

                                          if (localModifiedDate==null){
                                              // write the nil attribute
                                              
                                                     throw new org.apache.axis2.databinding.ADBException("modifiedDate cannot be null!!");
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localModifiedDate));
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localFmVersionTracker){
                                    namespace = "";
                                    writeStartElement(null, namespace, "fmVersion", xmlWriter);
                             
                                               if (localFmVersion==java.lang.Long.MIN_VALUE) {
                                           
                                                         throw new org.apache.axis2.databinding.ADBException("fmVersion cannot be null!!");
                                                      
                                               } else {
                                                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localFmVersion));
                                               }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localOldFmLinkTracker){
                                    namespace = "";
                                    writeStartElement(null, namespace, "oldFmLink", xmlWriter);
                             

                                          if (localOldFmLink==null){
                                              // write the nil attribute
                                              
                                                     throw new org.apache.axis2.databinding.ADBException("oldFmLink cannot be null!!");
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(localOldFmLink);
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localNewFmLinkTracker){
                                    namespace = "";
                                    writeStartElement(null, namespace, "newFmLink", xmlWriter);
                             

                                          if (localNewFmLink==null){
                                              // write the nil attribute
                                              
                                                     throw new org.apache.axis2.databinding.ADBException("newFmLink cannot be null!!");
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(localNewFmLink);
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localUseCaseIdTracker){
                                    namespace = "";
                                    writeStartElement(null, namespace, "useCaseId", xmlWriter);
                             
                                               if (localUseCaseId==java.lang.Long.MIN_VALUE) {
                                           
                                                         throw new org.apache.axis2.databinding.ADBException("useCaseId cannot be null!!");
                                                      
                                               } else {
                                                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localUseCaseId));
                                               }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localSectionIdTracker){
                                    namespace = "";
                                    writeStartElement(null, namespace, "sectionId", xmlWriter);
                             
                                               if (localSectionId==java.lang.Long.MIN_VALUE) {
                                           
                                                         throw new org.apache.axis2.databinding.ADBException("sectionId cannot be null!!");
                                                      
                                               } else {
                                                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localSectionId));
                                               }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localAttrIdTracker){
                                    namespace = "";
                                    writeStartElement(null, namespace, "attrId", xmlWriter);
                             
                                               if (localAttrId==java.lang.Long.MIN_VALUE) {
                                           
                                                         throw new org.apache.axis2.databinding.ADBException("attrId cannot be null!!");
                                                      
                                               } else {
                                                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localAttrId));
                                               }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localOldDeletedFlagTracker){
                                    namespace = "";
                                    writeStartElement(null, namespace, "oldDeletedFlag", xmlWriter);
                             

                                          if (localOldDeletedFlag==null){
                                              // write the nil attribute
                                              
                                                     throw new org.apache.axis2.databinding.ADBException("oldDeletedFlag cannot be null!!");
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(localOldDeletedFlag);
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localNewDeletedFlagTracker){
                                    namespace = "";
                                    writeStartElement(null, namespace, "newDeletedFlag", xmlWriter);
                             

                                          if (localNewDeletedFlag==null){
                                              // write the nil attribute
                                              
                                                     throw new org.apache.axis2.databinding.ADBException("newDeletedFlag cannot be null!!");
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(localNewDeletedFlag);
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localFmVersionIdTracker){
                                    namespace = "";
                                    writeStartElement(null, namespace, "fmVersionId", xmlWriter);
                             
                                               if (localFmVersionId==java.lang.Long.MIN_VALUE) {
                                           
                                                         throw new org.apache.axis2.databinding.ADBException("fmVersionId cannot be null!!");
                                                      
                                               } else {
                                                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localFmVersionId));
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

                 if (localFmIdTracker){
                                      elementList.add(new javax.xml.namespace.QName("",
                                                                      "fmId"));
                                 
                                elementList.add(
                                   org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localFmId));
                            } if (localFmUcpaIdTracker){
                                      elementList.add(new javax.xml.namespace.QName("",
                                                                      "fmUcpaId"));
                                 
                                elementList.add(
                                   org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localFmUcpaId));
                            } if (localOldFmColorCodeTracker){
                                      elementList.add(new javax.xml.namespace.QName("",
                                                                      "oldFmColorCode"));
                                 
                                        if (localOldFmColorCode != null){
                                            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localOldFmColorCode));
                                        } else {
                                           throw new org.apache.axis2.databinding.ADBException("oldFmColorCode cannot be null!!");
                                        }
                                    } if (localNewFmColorCodeTracker){
                                      elementList.add(new javax.xml.namespace.QName("",
                                                                      "newFmColorCode"));
                                 
                                        if (localNewFmColorCode != null){
                                            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localNewFmColorCode));
                                        } else {
                                           throw new org.apache.axis2.databinding.ADBException("newFmColorCode cannot be null!!");
                                        }
                                    } if (localOldCommentsTracker){
                                      elementList.add(new javax.xml.namespace.QName("",
                                                                      "oldComments"));
                                 
                                        if (localOldComments != null){
                                            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localOldComments));
                                        } else {
                                           throw new org.apache.axis2.databinding.ADBException("oldComments cannot be null!!");
                                        }
                                    } if (localNewCommentsTracker){
                                      elementList.add(new javax.xml.namespace.QName("",
                                                                      "newComments"));
                                 
                                        if (localNewComments != null){
                                            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localNewComments));
                                        } else {
                                           throw new org.apache.axis2.databinding.ADBException("newComments cannot be null!!");
                                        }
                                    } if (localCreatedUserTracker){
                                      elementList.add(new javax.xml.namespace.QName("",
                                                                      "createdUser"));
                                 
                                        if (localCreatedUser != null){
                                            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localCreatedUser));
                                        } else {
                                           throw new org.apache.axis2.databinding.ADBException("createdUser cannot be null!!");
                                        }
                                    } if (localCreatedDateTracker){
                                      elementList.add(new javax.xml.namespace.QName("",
                                                                      "createdDate"));
                                 
                                        if (localCreatedDate != null){
                                            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localCreatedDate));
                                        } else {
                                           throw new org.apache.axis2.databinding.ADBException("createdDate cannot be null!!");
                                        }
                                    } if (localModifiedUserTracker){
                                      elementList.add(new javax.xml.namespace.QName("",
                                                                      "modifiedUser"));
                                 
                                        if (localModifiedUser != null){
                                            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localModifiedUser));
                                        } else {
                                           throw new org.apache.axis2.databinding.ADBException("modifiedUser cannot be null!!");
                                        }
                                    } if (localModifiedDateTracker){
                                      elementList.add(new javax.xml.namespace.QName("",
                                                                      "modifiedDate"));
                                 
                                        if (localModifiedDate != null){
                                            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localModifiedDate));
                                        } else {
                                           throw new org.apache.axis2.databinding.ADBException("modifiedDate cannot be null!!");
                                        }
                                    } if (localFmVersionTracker){
                                      elementList.add(new javax.xml.namespace.QName("",
                                                                      "fmVersion"));
                                 
                                elementList.add(
                                   org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localFmVersion));
                            } if (localOldFmLinkTracker){
                                      elementList.add(new javax.xml.namespace.QName("",
                                                                      "oldFmLink"));
                                 
                                        if (localOldFmLink != null){
                                            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localOldFmLink));
                                        } else {
                                           throw new org.apache.axis2.databinding.ADBException("oldFmLink cannot be null!!");
                                        }
                                    } if (localNewFmLinkTracker){
                                      elementList.add(new javax.xml.namespace.QName("",
                                                                      "newFmLink"));
                                 
                                        if (localNewFmLink != null){
                                            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localNewFmLink));
                                        } else {
                                           throw new org.apache.axis2.databinding.ADBException("newFmLink cannot be null!!");
                                        }
                                    } if (localUseCaseIdTracker){
                                      elementList.add(new javax.xml.namespace.QName("",
                                                                      "useCaseId"));
                                 
                                elementList.add(
                                   org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localUseCaseId));
                            } if (localSectionIdTracker){
                                      elementList.add(new javax.xml.namespace.QName("",
                                                                      "sectionId"));
                                 
                                elementList.add(
                                   org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localSectionId));
                            } if (localAttrIdTracker){
                                      elementList.add(new javax.xml.namespace.QName("",
                                                                      "attrId"));
                                 
                                elementList.add(
                                   org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localAttrId));
                            } if (localOldDeletedFlagTracker){
                                      elementList.add(new javax.xml.namespace.QName("",
                                                                      "oldDeletedFlag"));
                                 
                                        if (localOldDeletedFlag != null){
                                            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localOldDeletedFlag));
                                        } else {
                                           throw new org.apache.axis2.databinding.ADBException("oldDeletedFlag cannot be null!!");
                                        }
                                    } if (localNewDeletedFlagTracker){
                                      elementList.add(new javax.xml.namespace.QName("",
                                                                      "newDeletedFlag"));
                                 
                                        if (localNewDeletedFlag != null){
                                            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localNewDeletedFlag));
                                        } else {
                                           throw new org.apache.axis2.databinding.ADBException("newDeletedFlag cannot be null!!");
                                        }
                                    } if (localFmVersionIdTracker){
                                      elementList.add(new javax.xml.namespace.QName("",
                                                                      "fmVersionId"));
                                 
                                elementList.add(
                                   org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localFmVersionId));
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
        public static PidcFocusMatrixType parse(javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception{
            PidcFocusMatrixType object =
                new PidcFocusMatrixType();

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
                    
                            if (!"PidcFocusMatrixType".equals(type)){
                                //find namespace for the prefix
                                java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                                return (PidcFocusMatrixType)com.bosch.caltool.apic.ws.ExtensionMapper.getTypeObject(
                                     nsUri,type,reader);
                              }
                        

                  }
                

                }

                

                
                // Note all attributes that were handled. Used to differ normal attributes
                // from anyAttributes.
                java.util.Vector handledAttributes = new java.util.Vector();
                

                
                    
                    reader.next();
                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("","fmId").equals(reader.getName())){
                                
                                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                    if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                        throw new org.apache.axis2.databinding.ADBException("The element: "+"fmId" +"  cannot be null");
                                    }
                                    

                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setFmId(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToLong(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                               object.setFmId(java.lang.Long.MIN_VALUE);
                                           
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("","fmUcpaId").equals(reader.getName())){
                                
                                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                    if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                        throw new org.apache.axis2.databinding.ADBException("The element: "+"fmUcpaId" +"  cannot be null");
                                    }
                                    

                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setFmUcpaId(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToLong(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                               object.setFmUcpaId(java.lang.Long.MIN_VALUE);
                                           
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("","oldFmColorCode").equals(reader.getName())){
                                
                                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                    if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                        throw new org.apache.axis2.databinding.ADBException("The element: "+"oldFmColorCode" +"  cannot be null");
                                    }
                                    

                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setOldFmColorCode(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("","newFmColorCode").equals(reader.getName())){
                                
                                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                    if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                        throw new org.apache.axis2.databinding.ADBException("The element: "+"newFmColorCode" +"  cannot be null");
                                    }
                                    

                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setNewFmColorCode(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("","oldComments").equals(reader.getName())){
                                
                                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                    if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                        throw new org.apache.axis2.databinding.ADBException("The element: "+"oldComments" +"  cannot be null");
                                    }
                                    

                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setOldComments(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("","newComments").equals(reader.getName())){
                                
                                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                    if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                        throw new org.apache.axis2.databinding.ADBException("The element: "+"newComments" +"  cannot be null");
                                    }
                                    

                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setNewComments(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("","createdUser").equals(reader.getName())){
                                
                                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                    if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                        throw new org.apache.axis2.databinding.ADBException("The element: "+"createdUser" +"  cannot be null");
                                    }
                                    

                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setCreatedUser(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("","createdDate").equals(reader.getName())){
                                
                                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                    if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                        throw new org.apache.axis2.databinding.ADBException("The element: "+"createdDate" +"  cannot be null");
                                    }
                                    

                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setCreatedDate(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToDateTime(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("","modifiedUser").equals(reader.getName())){
                                
                                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                    if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                        throw new org.apache.axis2.databinding.ADBException("The element: "+"modifiedUser" +"  cannot be null");
                                    }
                                    

                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setModifiedUser(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("","modifiedDate").equals(reader.getName())){
                                
                                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                    if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                        throw new org.apache.axis2.databinding.ADBException("The element: "+"modifiedDate" +"  cannot be null");
                                    }
                                    

                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setModifiedDate(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToDateTime(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("","fmVersion").equals(reader.getName())){
                                
                                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                    if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                        throw new org.apache.axis2.databinding.ADBException("The element: "+"fmVersion" +"  cannot be null");
                                    }
                                    

                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setFmVersion(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToLong(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                               object.setFmVersion(java.lang.Long.MIN_VALUE);
                                           
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("","oldFmLink").equals(reader.getName())){
                                
                                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                    if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                        throw new org.apache.axis2.databinding.ADBException("The element: "+"oldFmLink" +"  cannot be null");
                                    }
                                    

                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setOldFmLink(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("","newFmLink").equals(reader.getName())){
                                
                                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                    if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                        throw new org.apache.axis2.databinding.ADBException("The element: "+"newFmLink" +"  cannot be null");
                                    }
                                    

                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setNewFmLink(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("","useCaseId").equals(reader.getName())){
                                
                                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                    if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                        throw new org.apache.axis2.databinding.ADBException("The element: "+"useCaseId" +"  cannot be null");
                                    }
                                    

                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setUseCaseId(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToLong(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                               object.setUseCaseId(java.lang.Long.MIN_VALUE);
                                           
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("","sectionId").equals(reader.getName())){
                                
                                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                    if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                        throw new org.apache.axis2.databinding.ADBException("The element: "+"sectionId" +"  cannot be null");
                                    }
                                    

                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setSectionId(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToLong(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                               object.setSectionId(java.lang.Long.MIN_VALUE);
                                           
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("","attrId").equals(reader.getName())){
                                
                                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                    if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                        throw new org.apache.axis2.databinding.ADBException("The element: "+"attrId" +"  cannot be null");
                                    }
                                    

                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setAttrId(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToLong(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                               object.setAttrId(java.lang.Long.MIN_VALUE);
                                           
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("","oldDeletedFlag").equals(reader.getName())){
                                
                                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                    if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                        throw new org.apache.axis2.databinding.ADBException("The element: "+"oldDeletedFlag" +"  cannot be null");
                                    }
                                    

                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setOldDeletedFlag(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("","newDeletedFlag").equals(reader.getName())){
                                
                                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                    if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                        throw new org.apache.axis2.databinding.ADBException("The element: "+"newDeletedFlag" +"  cannot be null");
                                    }
                                    

                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setNewDeletedFlag(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("","fmVersionId").equals(reader.getName())){
                                
                                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                    if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                        throw new org.apache.axis2.databinding.ADBException("The element: "+"fmVersionId" +"  cannot be null");
                                    }
                                    

                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setFmVersionId(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToLong(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                               object.setFmVersionId(java.lang.Long.MIN_VALUE);
                                           
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
           
    