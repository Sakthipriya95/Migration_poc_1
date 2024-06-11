
/**
 * GetPidcDiffsResponseType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.2  Built on : Apr 17, 2012 (05:34:40 IST)
 */

            
                package com.bosch.caltool.apic.ws;
            

            /**
            *  GetPidcDiffsResponseType bean class
            */
            @SuppressWarnings({"unchecked","unused"})
        
        public  class GetPidcDiffsResponseType
        implements org.apache.axis2.databinding.ADBBean{
        /* This type was generated from the piece of schema that had
                name = GetPidcDiffsResponseType
                Namespace URI = http://ws.apic.caltool.bosch.com
                Namespace Prefix = ns1
                */
            

                        /**
                        * field for PidcID
                        */

                        
                                    protected long localPidcID ;
                                

                           /**
                           * Auto generated getter method
                           * @return long
                           */
                           public  long getPidcID(){
                               return localPidcID;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param PidcID
                               */
                               public void setPidcID(long param){
                            
                                            this.localPidcID=param;
                                    

                               }
                            

                        /**
                        * field for OldChangeNumber
                        */

                        
                                    protected long localOldChangeNumber ;
                                

                           /**
                           * Auto generated getter method
                           * @return long
                           */
                           public  long getOldChangeNumber(){
                               return localOldChangeNumber;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param OldChangeNumber
                               */
                               public void setOldChangeNumber(long param){
                            
                                            this.localOldChangeNumber=param;
                                    

                               }
                            

                        /**
                        * field for NewChangeNumber
                        */

                        
                                    protected long localNewChangeNumber ;
                                

                           /**
                           * Auto generated getter method
                           * @return long
                           */
                           public  long getNewChangeNumber(){
                               return localNewChangeNumber;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param NewChangeNumber
                               */
                               public void setNewChangeNumber(long param){
                            
                                            this.localNewChangeNumber=param;
                                    

                               }
                            

                        /**
                        * field for OldPidcVersionNumber
                        */

                        
                                    protected long localOldPidcVersionNumber ;
                                

                           /**
                           * Auto generated getter method
                           * @return long
                           */
                           public  long getOldPidcVersionNumber(){
                               return localOldPidcVersionNumber;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param OldPidcVersionNumber
                               */
                               public void setOldPidcVersionNumber(long param){
                            
                                            this.localOldPidcVersionNumber=param;
                                    

                               }
                            

                        /**
                        * field for NewPidcVersionNumber
                        */

                        
                                    protected long localNewPidcVersionNumber ;
                                

                           /**
                           * Auto generated getter method
                           * @return long
                           */
                           public  long getNewPidcVersionNumber(){
                               return localNewPidcVersionNumber;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param NewPidcVersionNumber
                               */
                               public void setNewPidcVersionNumber(long param){
                            
                                            this.localNewPidcVersionNumber=param;
                                    

                               }
                            

                        /**
                        * field for OldPidcStatus
                        */

                        
                                    protected java.lang.String localOldPidcStatus ;
                                

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getOldPidcStatus(){
                               return localOldPidcStatus;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param OldPidcStatus
                               */
                               public void setOldPidcStatus(java.lang.String param){
                            
                                            this.localOldPidcStatus=param;
                                    

                               }
                            

                        /**
                        * field for NewPidcStatus
                        */

                        
                                    protected java.lang.String localNewPidcStatus ;
                                

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getNewPidcStatus(){
                               return localNewPidcStatus;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param NewPidcStatus
                               */
                               public void setNewPidcStatus(java.lang.String param){
                            
                                            this.localNewPidcStatus=param;
                                    

                               }
                            

                        /**
                        * field for OldIsDeleted
                        */

                        
                                    protected java.lang.String localOldIsDeleted ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localOldIsDeletedTracker = false ;

                           public boolean isOldIsDeletedSpecified(){
                               return localOldIsDeletedTracker;
                           }

                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getOldIsDeleted(){
                               return localOldIsDeleted;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param OldIsDeleted
                               */
                               public void setOldIsDeleted(java.lang.String param){
                            localOldIsDeletedTracker = param != null;
                                   
                                            this.localOldIsDeleted=param;
                                    

                               }
                            

                        /**
                        * field for NewIsDeleted
                        */

                        
                                    protected java.lang.String localNewIsDeleted ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localNewIsDeletedTracker = false ;

                           public boolean isNewIsDeletedSpecified(){
                               return localNewIsDeletedTracker;
                           }

                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getNewIsDeleted(){
                               return localNewIsDeleted;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param NewIsDeleted
                               */
                               public void setNewIsDeleted(java.lang.String param){
                            localNewIsDeletedTracker = param != null;
                                   
                                            this.localNewIsDeleted=param;
                                    

                               }
                            

                        /**
                        * field for ModifyDate
                        */

                        
                                    protected java.util.Calendar localModifyDate ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localModifyDateTracker = false ;

                           public boolean isModifyDateSpecified(){
                               return localModifyDateTracker;
                           }

                           

                           /**
                           * Auto generated getter method
                           * @return java.util.Calendar
                           */
                           public  java.util.Calendar getModifyDate(){
                               return localModifyDate;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param ModifyDate
                               */
                               public void setModifyDate(java.util.Calendar param){
                            localModifyDateTracker = param != null;
                                   
                                            this.localModifyDate=param;
                                    

                               }
                            

                        /**
                        * field for ModifyUser
                        */

                        
                                    protected java.lang.String localModifyUser ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localModifyUserTracker = false ;

                           public boolean isModifyUserSpecified(){
                               return localModifyUserTracker;
                           }

                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getModifyUser(){
                               return localModifyUser;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param ModifyUser
                               */
                               public void setModifyUser(java.lang.String param){
                            localModifyUserTracker = param != null;
                                   
                                            this.localModifyUser=param;
                                    

                               }
                            

                        /**
                        * field for PidcVersion
                        */

                        
                                    protected long localPidcVersion ;
                                

                           /**
                           * Auto generated getter method
                           * @return long
                           */
                           public  long getPidcVersion(){
                               return localPidcVersion;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param PidcVersion
                               */
                               public void setPidcVersion(long param){
                            
                                            this.localPidcVersion=param;
                                    

                               }
                            

                        /**
                        * field for ChangedAttributes
                        * This was an Array!
                        */

                        
                                    protected com.bosch.caltool.apic.ws.ProjectIdCardChangedAttributeType[] localChangedAttributes ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localChangedAttributesTracker = false ;

                           public boolean isChangedAttributesSpecified(){
                               return localChangedAttributesTracker;
                           }

                           

                           /**
                           * Auto generated getter method
                           * @return com.bosch.caltool.apic.ws.ProjectIdCardChangedAttributeType[]
                           */
                           public  com.bosch.caltool.apic.ws.ProjectIdCardChangedAttributeType[] getChangedAttributes(){
                               return localChangedAttributes;
                           }

                           
                        


                               
                              /**
                               * validate the array for ChangedAttributes
                               */
                              protected void validateChangedAttributes(com.bosch.caltool.apic.ws.ProjectIdCardChangedAttributeType[] param){
                             
                              }


                             /**
                              * Auto generated setter method
                              * @param param ChangedAttributes
                              */
                              public void setChangedAttributes(com.bosch.caltool.apic.ws.ProjectIdCardChangedAttributeType[] param){
                              
                                   validateChangedAttributes(param);

                               localChangedAttributesTracker = param != null;
                                      
                                      this.localChangedAttributes=param;
                              }

                               
                             
                             /**
                             * Auto generated add method for the array for convenience
                             * @param param com.bosch.caltool.apic.ws.ProjectIdCardChangedAttributeType
                             */
                             public void addChangedAttributes(com.bosch.caltool.apic.ws.ProjectIdCardChangedAttributeType param){
                                   if (localChangedAttributes == null){
                                   localChangedAttributes = new com.bosch.caltool.apic.ws.ProjectIdCardChangedAttributeType[]{};
                                   }

                            
                                 //update the setting tracker
                                localChangedAttributesTracker = true;
                            

                               java.util.List list =
                            org.apache.axis2.databinding.utils.ConverterUtil.toList(localChangedAttributes);
                               list.add(param);
                               this.localChangedAttributes =
                             (com.bosch.caltool.apic.ws.ProjectIdCardChangedAttributeType[])list.toArray(
                            new com.bosch.caltool.apic.ws.ProjectIdCardChangedAttributeType[list.size()]);

                             }
                             

                        /**
                        * field for ChangedVariants
                        * This was an Array!
                        */

                        
                                    protected com.bosch.caltool.apic.ws.ProjectIdCardChangedVariantsType[] localChangedVariants ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localChangedVariantsTracker = false ;

                           public boolean isChangedVariantsSpecified(){
                               return localChangedVariantsTracker;
                           }

                           

                           /**
                           * Auto generated getter method
                           * @return com.bosch.caltool.apic.ws.ProjectIdCardChangedVariantsType[]
                           */
                           public  com.bosch.caltool.apic.ws.ProjectIdCardChangedVariantsType[] getChangedVariants(){
                               return localChangedVariants;
                           }

                           
                        


                               
                              /**
                               * validate the array for ChangedVariants
                               */
                              protected void validateChangedVariants(com.bosch.caltool.apic.ws.ProjectIdCardChangedVariantsType[] param){
                             
                              }


                             /**
                              * Auto generated setter method
                              * @param param ChangedVariants
                              */
                              public void setChangedVariants(com.bosch.caltool.apic.ws.ProjectIdCardChangedVariantsType[] param){
                              
                                   validateChangedVariants(param);

                               localChangedVariantsTracker = param != null;
                                      
                                      this.localChangedVariants=param;
                              }

                               
                             
                             /**
                             * Auto generated add method for the array for convenience
                             * @param param com.bosch.caltool.apic.ws.ProjectIdCardChangedVariantsType
                             */
                             public void addChangedVariants(com.bosch.caltool.apic.ws.ProjectIdCardChangedVariantsType param){
                                   if (localChangedVariants == null){
                                   localChangedVariants = new com.bosch.caltool.apic.ws.ProjectIdCardChangedVariantsType[]{};
                                   }

                            
                                 //update the setting tracker
                                localChangedVariantsTracker = true;
                            

                               java.util.List list =
                            org.apache.axis2.databinding.utils.ConverterUtil.toList(localChangedVariants);
                               list.add(param);
                               this.localChangedVariants =
                             (com.bosch.caltool.apic.ws.ProjectIdCardChangedVariantsType[])list.toArray(
                            new com.bosch.caltool.apic.ws.ProjectIdCardChangedVariantsType[list.size()]);

                             }
                             

                        /**
                        * field for ChangedFocusMatrixVersion
                        * This was an Array!
                        */

                        
                                    protected com.bosch.caltool.apic.ws.PidcFocusMatrixVersType[] localChangedFocusMatrixVersion ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localChangedFocusMatrixVersionTracker = false ;

                           public boolean isChangedFocusMatrixVersionSpecified(){
                               return localChangedFocusMatrixVersionTracker;
                           }

                           

                           /**
                           * Auto generated getter method
                           * @return com.bosch.caltool.apic.ws.PidcFocusMatrixVersType[]
                           */
                           public  com.bosch.caltool.apic.ws.PidcFocusMatrixVersType[] getChangedFocusMatrixVersion(){
                               return localChangedFocusMatrixVersion;
                           }

                           
                        


                               
                              /**
                               * validate the array for ChangedFocusMatrixVersion
                               */
                              protected void validateChangedFocusMatrixVersion(com.bosch.caltool.apic.ws.PidcFocusMatrixVersType[] param){
                             
                              }


                             /**
                              * Auto generated setter method
                              * @param param ChangedFocusMatrixVersion
                              */
                              public void setChangedFocusMatrixVersion(com.bosch.caltool.apic.ws.PidcFocusMatrixVersType[] param){
                              
                                   validateChangedFocusMatrixVersion(param);

                               localChangedFocusMatrixVersionTracker = param != null;
                                      
                                      this.localChangedFocusMatrixVersion=param;
                              }

                               
                             
                             /**
                             * Auto generated add method for the array for convenience
                             * @param param com.bosch.caltool.apic.ws.PidcFocusMatrixVersType
                             */
                             public void addChangedFocusMatrixVersion(com.bosch.caltool.apic.ws.PidcFocusMatrixVersType param){
                                   if (localChangedFocusMatrixVersion == null){
                                   localChangedFocusMatrixVersion = new com.bosch.caltool.apic.ws.PidcFocusMatrixVersType[]{};
                                   }

                            
                                 //update the setting tracker
                                localChangedFocusMatrixVersionTracker = true;
                            

                               java.util.List list =
                            org.apache.axis2.databinding.utils.ConverterUtil.toList(localChangedFocusMatrixVersion);
                               list.add(param);
                               this.localChangedFocusMatrixVersion =
                             (com.bosch.caltool.apic.ws.PidcFocusMatrixVersType[])list.toArray(
                            new com.bosch.caltool.apic.ws.PidcFocusMatrixVersType[list.size()]);

                             }
                             

                        /**
                        * field for ChangedFocusMatrix
                        * This was an Array!
                        */

                        
                                    protected com.bosch.caltool.apic.ws.PidcFocusMatrixType[] localChangedFocusMatrix ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localChangedFocusMatrixTracker = false ;

                           public boolean isChangedFocusMatrixSpecified(){
                               return localChangedFocusMatrixTracker;
                           }

                           

                           /**
                           * Auto generated getter method
                           * @return com.bosch.caltool.apic.ws.PidcFocusMatrixType[]
                           */
                           public  com.bosch.caltool.apic.ws.PidcFocusMatrixType[] getChangedFocusMatrix(){
                               return localChangedFocusMatrix;
                           }

                           
                        


                               
                              /**
                               * validate the array for ChangedFocusMatrix
                               */
                              protected void validateChangedFocusMatrix(com.bosch.caltool.apic.ws.PidcFocusMatrixType[] param){
                             
                              }


                             /**
                              * Auto generated setter method
                              * @param param ChangedFocusMatrix
                              */
                              public void setChangedFocusMatrix(com.bosch.caltool.apic.ws.PidcFocusMatrixType[] param){
                              
                                   validateChangedFocusMatrix(param);

                               localChangedFocusMatrixTracker = param != null;
                                      
                                      this.localChangedFocusMatrix=param;
                              }

                               
                             
                             /**
                             * Auto generated add method for the array for convenience
                             * @param param com.bosch.caltool.apic.ws.PidcFocusMatrixType
                             */
                             public void addChangedFocusMatrix(com.bosch.caltool.apic.ws.PidcFocusMatrixType param){
                                   if (localChangedFocusMatrix == null){
                                   localChangedFocusMatrix = new com.bosch.caltool.apic.ws.PidcFocusMatrixType[]{};
                                   }

                            
                                 //update the setting tracker
                                localChangedFocusMatrixTracker = true;
                            

                               java.util.List list =
                            org.apache.axis2.databinding.utils.ConverterUtil.toList(localChangedFocusMatrix);
                               list.add(param);
                               this.localChangedFocusMatrix =
                             (com.bosch.caltool.apic.ws.PidcFocusMatrixType[])list.toArray(
                            new com.bosch.caltool.apic.ws.PidcFocusMatrixType[list.size()]);

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
                           namespacePrefix+":GetPidcDiffsResponseType",
                           xmlWriter);
                   } else {
                       writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","type",
                           "GetPidcDiffsResponseType",
                           xmlWriter);
                   }

               
                   }
               
                                    namespace = "";
                                    writeStartElement(null, namespace, "pidcID", xmlWriter);
                             
                                               if (localPidcID==java.lang.Long.MIN_VALUE) {
                                           
                                                         throw new org.apache.axis2.databinding.ADBException("pidcID cannot be null!!");
                                                      
                                               } else {
                                                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localPidcID));
                                               }
                                    
                                   xmlWriter.writeEndElement();
                             
                                    namespace = "";
                                    writeStartElement(null, namespace, "oldChangeNumber", xmlWriter);
                             
                                               if (localOldChangeNumber==java.lang.Long.MIN_VALUE) {
                                           
                                                         throw new org.apache.axis2.databinding.ADBException("oldChangeNumber cannot be null!!");
                                                      
                                               } else {
                                                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localOldChangeNumber));
                                               }
                                    
                                   xmlWriter.writeEndElement();
                             
                                    namespace = "";
                                    writeStartElement(null, namespace, "newChangeNumber", xmlWriter);
                             
                                               if (localNewChangeNumber==java.lang.Long.MIN_VALUE) {
                                           
                                                         throw new org.apache.axis2.databinding.ADBException("newChangeNumber cannot be null!!");
                                                      
                                               } else {
                                                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localNewChangeNumber));
                                               }
                                    
                                   xmlWriter.writeEndElement();
                             
                                    namespace = "";
                                    writeStartElement(null, namespace, "oldPidcVersionNumber", xmlWriter);
                             
                                               if (localOldPidcVersionNumber==java.lang.Long.MIN_VALUE) {
                                           
                                                         throw new org.apache.axis2.databinding.ADBException("oldPidcVersionNumber cannot be null!!");
                                                      
                                               } else {
                                                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localOldPidcVersionNumber));
                                               }
                                    
                                   xmlWriter.writeEndElement();
                             
                                    namespace = "";
                                    writeStartElement(null, namespace, "newPidcVersionNumber", xmlWriter);
                             
                                               if (localNewPidcVersionNumber==java.lang.Long.MIN_VALUE) {
                                           
                                                         throw new org.apache.axis2.databinding.ADBException("newPidcVersionNumber cannot be null!!");
                                                      
                                               } else {
                                                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localNewPidcVersionNumber));
                                               }
                                    
                                   xmlWriter.writeEndElement();
                             
                                    namespace = "";
                                    writeStartElement(null, namespace, "oldPidcStatus", xmlWriter);
                             

                                          if (localOldPidcStatus==null){
                                              // write the nil attribute
                                              
                                                     throw new org.apache.axis2.databinding.ADBException("oldPidcStatus cannot be null!!");
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(localOldPidcStatus);
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                             
                                    namespace = "";
                                    writeStartElement(null, namespace, "newPidcStatus", xmlWriter);
                             

                                          if (localNewPidcStatus==null){
                                              // write the nil attribute
                                              
                                                     throw new org.apache.axis2.databinding.ADBException("newPidcStatus cannot be null!!");
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(localNewPidcStatus);
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                              if (localOldIsDeletedTracker){
                                    namespace = "";
                                    writeStartElement(null, namespace, "oldIsDeleted", xmlWriter);
                             

                                          if (localOldIsDeleted==null){
                                              // write the nil attribute
                                              
                                                     throw new org.apache.axis2.databinding.ADBException("oldIsDeleted cannot be null!!");
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(localOldIsDeleted);
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localNewIsDeletedTracker){
                                    namespace = "";
                                    writeStartElement(null, namespace, "newIsDeleted", xmlWriter);
                             

                                          if (localNewIsDeleted==null){
                                              // write the nil attribute
                                              
                                                     throw new org.apache.axis2.databinding.ADBException("newIsDeleted cannot be null!!");
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(localNewIsDeleted);
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localModifyDateTracker){
                                    namespace = "";
                                    writeStartElement(null, namespace, "modifyDate", xmlWriter);
                             

                                          if (localModifyDate==null){
                                              // write the nil attribute
                                              
                                                     throw new org.apache.axis2.databinding.ADBException("modifyDate cannot be null!!");
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localModifyDate));
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localModifyUserTracker){
                                    namespace = "";
                                    writeStartElement(null, namespace, "modifyUser", xmlWriter);
                             

                                          if (localModifyUser==null){
                                              // write the nil attribute
                                              
                                                     throw new org.apache.axis2.databinding.ADBException("modifyUser cannot be null!!");
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(localModifyUser);
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                             }
                                    namespace = "";
                                    writeStartElement(null, namespace, "pidcVersion", xmlWriter);
                             
                                               if (localPidcVersion==java.lang.Long.MIN_VALUE) {
                                           
                                                         throw new org.apache.axis2.databinding.ADBException("pidcVersion cannot be null!!");
                                                      
                                               } else {
                                                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localPidcVersion));
                                               }
                                    
                                   xmlWriter.writeEndElement();
                              if (localChangedAttributesTracker){
                                       if (localChangedAttributes!=null){
                                            for (int i = 0;i < localChangedAttributes.length;i++){
                                                if (localChangedAttributes[i] != null){
                                                 localChangedAttributes[i].serialize(new javax.xml.namespace.QName("","changedAttributes"),
                                                           xmlWriter);
                                                } else {
                                                   
                                                        // we don't have to do any thing since minOccures is zero
                                                    
                                                }

                                            }
                                     } else {
                                        
                                               throw new org.apache.axis2.databinding.ADBException("changedAttributes cannot be null!!");
                                        
                                    }
                                 } if (localChangedVariantsTracker){
                                       if (localChangedVariants!=null){
                                            for (int i = 0;i < localChangedVariants.length;i++){
                                                if (localChangedVariants[i] != null){
                                                 localChangedVariants[i].serialize(new javax.xml.namespace.QName("","changedVariants"),
                                                           xmlWriter);
                                                } else {
                                                   
                                                        // we don't have to do any thing since minOccures is zero
                                                    
                                                }

                                            }
                                     } else {
                                        
                                               throw new org.apache.axis2.databinding.ADBException("changedVariants cannot be null!!");
                                        
                                    }
                                 } if (localChangedFocusMatrixVersionTracker){
                                       if (localChangedFocusMatrixVersion!=null){
                                            for (int i = 0;i < localChangedFocusMatrixVersion.length;i++){
                                                if (localChangedFocusMatrixVersion[i] != null){
                                                 localChangedFocusMatrixVersion[i].serialize(new javax.xml.namespace.QName("","changedFocusMatrixVersion"),
                                                           xmlWriter);
                                                } else {
                                                   
                                                        // we don't have to do any thing since minOccures is zero
                                                    
                                                }

                                            }
                                     } else {
                                        
                                               throw new org.apache.axis2.databinding.ADBException("changedFocusMatrixVersion cannot be null!!");
                                        
                                    }
                                 } if (localChangedFocusMatrixTracker){
                                       if (localChangedFocusMatrix!=null){
                                            for (int i = 0;i < localChangedFocusMatrix.length;i++){
                                                if (localChangedFocusMatrix[i] != null){
                                                 localChangedFocusMatrix[i].serialize(new javax.xml.namespace.QName("","changedFocusMatrix"),
                                                           xmlWriter);
                                                } else {
                                                   
                                                        // we don't have to do any thing since minOccures is zero
                                                    
                                                }

                                            }
                                     } else {
                                        
                                               throw new org.apache.axis2.databinding.ADBException("changedFocusMatrix cannot be null!!");
                                        
                                    }
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

                
                                      elementList.add(new javax.xml.namespace.QName("",
                                                                      "pidcID"));
                                 
                                elementList.add(
                                   org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localPidcID));
                            
                                      elementList.add(new javax.xml.namespace.QName("",
                                                                      "oldChangeNumber"));
                                 
                                elementList.add(
                                   org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localOldChangeNumber));
                            
                                      elementList.add(new javax.xml.namespace.QName("",
                                                                      "newChangeNumber"));
                                 
                                elementList.add(
                                   org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localNewChangeNumber));
                            
                                      elementList.add(new javax.xml.namespace.QName("",
                                                                      "oldPidcVersionNumber"));
                                 
                                elementList.add(
                                   org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localOldPidcVersionNumber));
                            
                                      elementList.add(new javax.xml.namespace.QName("",
                                                                      "newPidcVersionNumber"));
                                 
                                elementList.add(
                                   org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localNewPidcVersionNumber));
                            
                                      elementList.add(new javax.xml.namespace.QName("",
                                                                      "oldPidcStatus"));
                                 
                                        if (localOldPidcStatus != null){
                                            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localOldPidcStatus));
                                        } else {
                                           throw new org.apache.axis2.databinding.ADBException("oldPidcStatus cannot be null!!");
                                        }
                                    
                                      elementList.add(new javax.xml.namespace.QName("",
                                                                      "newPidcStatus"));
                                 
                                        if (localNewPidcStatus != null){
                                            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localNewPidcStatus));
                                        } else {
                                           throw new org.apache.axis2.databinding.ADBException("newPidcStatus cannot be null!!");
                                        }
                                     if (localOldIsDeletedTracker){
                                      elementList.add(new javax.xml.namespace.QName("",
                                                                      "oldIsDeleted"));
                                 
                                        if (localOldIsDeleted != null){
                                            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localOldIsDeleted));
                                        } else {
                                           throw new org.apache.axis2.databinding.ADBException("oldIsDeleted cannot be null!!");
                                        }
                                    } if (localNewIsDeletedTracker){
                                      elementList.add(new javax.xml.namespace.QName("",
                                                                      "newIsDeleted"));
                                 
                                        if (localNewIsDeleted != null){
                                            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localNewIsDeleted));
                                        } else {
                                           throw new org.apache.axis2.databinding.ADBException("newIsDeleted cannot be null!!");
                                        }
                                    } if (localModifyDateTracker){
                                      elementList.add(new javax.xml.namespace.QName("",
                                                                      "modifyDate"));
                                 
                                        if (localModifyDate != null){
                                            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localModifyDate));
                                        } else {
                                           throw new org.apache.axis2.databinding.ADBException("modifyDate cannot be null!!");
                                        }
                                    } if (localModifyUserTracker){
                                      elementList.add(new javax.xml.namespace.QName("",
                                                                      "modifyUser"));
                                 
                                        if (localModifyUser != null){
                                            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localModifyUser));
                                        } else {
                                           throw new org.apache.axis2.databinding.ADBException("modifyUser cannot be null!!");
                                        }
                                    }
                                      elementList.add(new javax.xml.namespace.QName("",
                                                                      "pidcVersion"));
                                 
                                elementList.add(
                                   org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localPidcVersion));
                             if (localChangedAttributesTracker){
                             if (localChangedAttributes!=null) {
                                 for (int i = 0;i < localChangedAttributes.length;i++){

                                    if (localChangedAttributes[i] != null){
                                         elementList.add(new javax.xml.namespace.QName("",
                                                                          "changedAttributes"));
                                         elementList.add(localChangedAttributes[i]);
                                    } else {
                                        
                                                // nothing to do
                                            
                                    }

                                 }
                             } else {
                                 
                                        throw new org.apache.axis2.databinding.ADBException("changedAttributes cannot be null!!");
                                    
                             }

                        } if (localChangedVariantsTracker){
                             if (localChangedVariants!=null) {
                                 for (int i = 0;i < localChangedVariants.length;i++){

                                    if (localChangedVariants[i] != null){
                                         elementList.add(new javax.xml.namespace.QName("",
                                                                          "changedVariants"));
                                         elementList.add(localChangedVariants[i]);
                                    } else {
                                        
                                                // nothing to do
                                            
                                    }

                                 }
                             } else {
                                 
                                        throw new org.apache.axis2.databinding.ADBException("changedVariants cannot be null!!");
                                    
                             }

                        } if (localChangedFocusMatrixVersionTracker){
                             if (localChangedFocusMatrixVersion!=null) {
                                 for (int i = 0;i < localChangedFocusMatrixVersion.length;i++){

                                    if (localChangedFocusMatrixVersion[i] != null){
                                         elementList.add(new javax.xml.namespace.QName("",
                                                                          "changedFocusMatrixVersion"));
                                         elementList.add(localChangedFocusMatrixVersion[i]);
                                    } else {
                                        
                                                // nothing to do
                                            
                                    }

                                 }
                             } else {
                                 
                                        throw new org.apache.axis2.databinding.ADBException("changedFocusMatrixVersion cannot be null!!");
                                    
                             }

                        } if (localChangedFocusMatrixTracker){
                             if (localChangedFocusMatrix!=null) {
                                 for (int i = 0;i < localChangedFocusMatrix.length;i++){

                                    if (localChangedFocusMatrix[i] != null){
                                         elementList.add(new javax.xml.namespace.QName("",
                                                                          "changedFocusMatrix"));
                                         elementList.add(localChangedFocusMatrix[i]);
                                    } else {
                                        
                                                // nothing to do
                                            
                                    }

                                 }
                             } else {
                                 
                                        throw new org.apache.axis2.databinding.ADBException("changedFocusMatrix cannot be null!!");
                                    
                             }

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
        public static GetPidcDiffsResponseType parse(javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception{
            GetPidcDiffsResponseType object =
                new GetPidcDiffsResponseType();

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
                    
                            if (!"GetPidcDiffsResponseType".equals(type)){
                                //find namespace for the prefix
                                java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                                return (GetPidcDiffsResponseType)com.bosch.caltool.apic.ws.ExtensionMapper.getTypeObject(
                                     nsUri,type,reader);
                              }
                        

                  }
                

                }

                

                
                // Note all attributes that were handled. Used to differ normal attributes
                // from anyAttributes.
                java.util.Vector handledAttributes = new java.util.Vector();
                

                
                    
                    reader.next();
                
                        java.util.ArrayList list13 = new java.util.ArrayList();
                    
                        java.util.ArrayList list14 = new java.util.ArrayList();
                    
                        java.util.ArrayList list15 = new java.util.ArrayList();
                    
                        java.util.ArrayList list16 = new java.util.ArrayList();
                    
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("","pidcID").equals(reader.getName())){
                                
                                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                    if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                        throw new org.apache.axis2.databinding.ADBException("The element: "+"pidcID" +"  cannot be null");
                                    }
                                    

                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setPidcID(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToLong(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                else{
                                    // A start element we are not expecting indicates an invalid parameter was passed
                                    throw new org.apache.axis2.databinding.ADBException("Unexpected subelement " + reader.getName());
                                }
                            
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("","oldChangeNumber").equals(reader.getName())){
                                
                                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                    if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                        throw new org.apache.axis2.databinding.ADBException("The element: "+"oldChangeNumber" +"  cannot be null");
                                    }
                                    

                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setOldChangeNumber(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToLong(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                else{
                                    // A start element we are not expecting indicates an invalid parameter was passed
                                    throw new org.apache.axis2.databinding.ADBException("Unexpected subelement " + reader.getName());
                                }
                            
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("","newChangeNumber").equals(reader.getName())){
                                
                                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                    if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                        throw new org.apache.axis2.databinding.ADBException("The element: "+"newChangeNumber" +"  cannot be null");
                                    }
                                    

                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setNewChangeNumber(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToLong(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                else{
                                    // A start element we are not expecting indicates an invalid parameter was passed
                                    throw new org.apache.axis2.databinding.ADBException("Unexpected subelement " + reader.getName());
                                }
                            
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("","oldPidcVersionNumber").equals(reader.getName())){
                                
                                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                    if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                        throw new org.apache.axis2.databinding.ADBException("The element: "+"oldPidcVersionNumber" +"  cannot be null");
                                    }
                                    

                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setOldPidcVersionNumber(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToLong(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                else{
                                    // A start element we are not expecting indicates an invalid parameter was passed
                                    throw new org.apache.axis2.databinding.ADBException("Unexpected subelement " + reader.getName());
                                }
                            
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("","newPidcVersionNumber").equals(reader.getName())){
                                
                                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                    if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                        throw new org.apache.axis2.databinding.ADBException("The element: "+"newPidcVersionNumber" +"  cannot be null");
                                    }
                                    

                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setNewPidcVersionNumber(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToLong(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                else{
                                    // A start element we are not expecting indicates an invalid parameter was passed
                                    throw new org.apache.axis2.databinding.ADBException("Unexpected subelement " + reader.getName());
                                }
                            
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("","oldPidcStatus").equals(reader.getName())){
                                
                                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                    if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                        throw new org.apache.axis2.databinding.ADBException("The element: "+"oldPidcStatus" +"  cannot be null");
                                    }
                                    

                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setOldPidcStatus(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                else{
                                    // A start element we are not expecting indicates an invalid parameter was passed
                                    throw new org.apache.axis2.databinding.ADBException("Unexpected subelement " + reader.getName());
                                }
                            
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("","newPidcStatus").equals(reader.getName())){
                                
                                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                    if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                        throw new org.apache.axis2.databinding.ADBException("The element: "+"newPidcStatus" +"  cannot be null");
                                    }
                                    

                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setNewPidcStatus(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                else{
                                    // A start element we are not expecting indicates an invalid parameter was passed
                                    throw new org.apache.axis2.databinding.ADBException("Unexpected subelement " + reader.getName());
                                }
                            
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("","oldIsDeleted").equals(reader.getName())){
                                
                                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                    if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                        throw new org.apache.axis2.databinding.ADBException("The element: "+"oldIsDeleted" +"  cannot be null");
                                    }
                                    

                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setOldIsDeleted(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("","newIsDeleted").equals(reader.getName())){
                                
                                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                    if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                        throw new org.apache.axis2.databinding.ADBException("The element: "+"newIsDeleted" +"  cannot be null");
                                    }
                                    

                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setNewIsDeleted(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("","modifyDate").equals(reader.getName())){
                                
                                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                    if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                        throw new org.apache.axis2.databinding.ADBException("The element: "+"modifyDate" +"  cannot be null");
                                    }
                                    

                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setModifyDate(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToDateTime(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("","modifyUser").equals(reader.getName())){
                                
                                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                    if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                        throw new org.apache.axis2.databinding.ADBException("The element: "+"modifyUser" +"  cannot be null");
                                    }
                                    

                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setModifyUser(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("","pidcVersion").equals(reader.getName())){
                                
                                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                    if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                        throw new org.apache.axis2.databinding.ADBException("The element: "+"pidcVersion" +"  cannot be null");
                                    }
                                    

                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setPidcVersion(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToLong(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                else{
                                    // A start element we are not expecting indicates an invalid parameter was passed
                                    throw new org.apache.axis2.databinding.ADBException("Unexpected subelement " + reader.getName());
                                }
                            
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("","changedAttributes").equals(reader.getName())){
                                
                                    
                                    
                                    // Process the array and step past its final element's end.
                                    list13.add(com.bosch.caltool.apic.ws.ProjectIdCardChangedAttributeType.Factory.parse(reader));
                                                                
                                                        //loop until we find a start element that is not part of this array
                                                        boolean loopDone13 = false;
                                                        while(!loopDone13){
                                                            // We should be at the end element, but make sure
                                                            while (!reader.isEndElement())
                                                                reader.next();
                                                            // Step out of this element
                                                            reader.next();
                                                            // Step to next element event.
                                                            while (!reader.isStartElement() && !reader.isEndElement())
                                                                reader.next();
                                                            if (reader.isEndElement()){
                                                                //two continuous end elements means we are exiting the xml structure
                                                                loopDone13 = true;
                                                            } else {
                                                                if (new javax.xml.namespace.QName("","changedAttributes").equals(reader.getName())){
                                                                    list13.add(com.bosch.caltool.apic.ws.ProjectIdCardChangedAttributeType.Factory.parse(reader));
                                                                        
                                                                }else{
                                                                    loopDone13 = true;
                                                                }
                                                            }
                                                        }
                                                        // call the converter utility  to convert and set the array
                                                        
                                                        object.setChangedAttributes((com.bosch.caltool.apic.ws.ProjectIdCardChangedAttributeType[])
                                                            org.apache.axis2.databinding.utils.ConverterUtil.convertToArray(
                                                                com.bosch.caltool.apic.ws.ProjectIdCardChangedAttributeType.class,
                                                                list13));
                                                            
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("","changedVariants").equals(reader.getName())){
                                
                                    
                                    
                                    // Process the array and step past its final element's end.
                                    list14.add(com.bosch.caltool.apic.ws.ProjectIdCardChangedVariantsType.Factory.parse(reader));
                                                                
                                                        //loop until we find a start element that is not part of this array
                                                        boolean loopDone14 = false;
                                                        while(!loopDone14){
                                                            // We should be at the end element, but make sure
                                                            while (!reader.isEndElement())
                                                                reader.next();
                                                            // Step out of this element
                                                            reader.next();
                                                            // Step to next element event.
                                                            while (!reader.isStartElement() && !reader.isEndElement())
                                                                reader.next();
                                                            if (reader.isEndElement()){
                                                                //two continuous end elements means we are exiting the xml structure
                                                                loopDone14 = true;
                                                            } else {
                                                                if (new javax.xml.namespace.QName("","changedVariants").equals(reader.getName())){
                                                                    list14.add(com.bosch.caltool.apic.ws.ProjectIdCardChangedVariantsType.Factory.parse(reader));
                                                                        
                                                                }else{
                                                                    loopDone14 = true;
                                                                }
                                                            }
                                                        }
                                                        // call the converter utility  to convert and set the array
                                                        
                                                        object.setChangedVariants((com.bosch.caltool.apic.ws.ProjectIdCardChangedVariantsType[])
                                                            org.apache.axis2.databinding.utils.ConverterUtil.convertToArray(
                                                                com.bosch.caltool.apic.ws.ProjectIdCardChangedVariantsType.class,
                                                                list14));
                                                            
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("","changedFocusMatrixVersion").equals(reader.getName())){
                                
                                    
                                    
                                    // Process the array and step past its final element's end.
                                    list15.add(com.bosch.caltool.apic.ws.PidcFocusMatrixVersType.Factory.parse(reader));
                                                                
                                                        //loop until we find a start element that is not part of this array
                                                        boolean loopDone15 = false;
                                                        while(!loopDone15){
                                                            // We should be at the end element, but make sure
                                                            while (!reader.isEndElement())
                                                                reader.next();
                                                            // Step out of this element
                                                            reader.next();
                                                            // Step to next element event.
                                                            while (!reader.isStartElement() && !reader.isEndElement())
                                                                reader.next();
                                                            if (reader.isEndElement()){
                                                                //two continuous end elements means we are exiting the xml structure
                                                                loopDone15 = true;
                                                            } else {
                                                                if (new javax.xml.namespace.QName("","changedFocusMatrixVersion").equals(reader.getName())){
                                                                    list15.add(com.bosch.caltool.apic.ws.PidcFocusMatrixVersType.Factory.parse(reader));
                                                                        
                                                                }else{
                                                                    loopDone15 = true;
                                                                }
                                                            }
                                                        }
                                                        // call the converter utility  to convert and set the array
                                                        
                                                        object.setChangedFocusMatrixVersion((com.bosch.caltool.apic.ws.PidcFocusMatrixVersType[])
                                                            org.apache.axis2.databinding.utils.ConverterUtil.convertToArray(
                                                                com.bosch.caltool.apic.ws.PidcFocusMatrixVersType.class,
                                                                list15));
                                                            
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("","changedFocusMatrix").equals(reader.getName())){
                                
                                    
                                    
                                    // Process the array and step past its final element's end.
                                    list16.add(com.bosch.caltool.apic.ws.PidcFocusMatrixType.Factory.parse(reader));
                                                                
                                                        //loop until we find a start element that is not part of this array
                                                        boolean loopDone16 = false;
                                                        while(!loopDone16){
                                                            // We should be at the end element, but make sure
                                                            while (!reader.isEndElement())
                                                                reader.next();
                                                            // Step out of this element
                                                            reader.next();
                                                            // Step to next element event.
                                                            while (!reader.isStartElement() && !reader.isEndElement())
                                                                reader.next();
                                                            if (reader.isEndElement()){
                                                                //two continuous end elements means we are exiting the xml structure
                                                                loopDone16 = true;
                                                            } else {
                                                                if (new javax.xml.namespace.QName("","changedFocusMatrix").equals(reader.getName())){
                                                                    list16.add(com.bosch.caltool.apic.ws.PidcFocusMatrixType.Factory.parse(reader));
                                                                        
                                                                }else{
                                                                    loopDone16 = true;
                                                                }
                                                            }
                                                        }
                                                        // call the converter utility  to convert and set the array
                                                        
                                                        object.setChangedFocusMatrix((com.bosch.caltool.apic.ws.PidcFocusMatrixType[])
                                                            org.apache.axis2.databinding.utils.ConverterUtil.convertToArray(
                                                                com.bosch.caltool.apic.ws.PidcFocusMatrixType.class,
                                                                list16));
                                                            
                              }  // End of if for expected property start element
                                
                                    else {
                                        
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
           
    