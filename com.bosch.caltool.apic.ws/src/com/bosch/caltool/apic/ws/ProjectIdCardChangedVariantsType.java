
/**
 * ProjectIdCardChangedVariantsType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.2  Built on : Apr 17, 2012 (05:34:40 IST)
 */

            
                package com.bosch.caltool.apic.ws;
            

            /**
            *  ProjectIdCardChangedVariantsType bean class
            */
            @SuppressWarnings({"unchecked","unused"})
        
        public  class ProjectIdCardChangedVariantsType
        implements org.apache.axis2.databinding.ADBBean{
        /* This type was generated from the piece of schema that had
                name = ProjectIdCardChangedVariantsType
                Namespace URI = http://ws.apic.caltool.bosch.com
                Namespace Prefix = ns1
                */
            

                        /**
                        * field for VariantID
                        */

                        
                                    protected long localVariantID ;
                                

                           /**
                           * Auto generated getter method
                           * @return long
                           */
                           public  long getVariantID(){
                               return localVariantID;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param VariantID
                               */
                               public void setVariantID(long param){
                            
                                            this.localVariantID=param;
                                    

                               }
                            

                        /**
                        * field for OldValueID
                        */

                        
                                    protected long localOldValueID ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localOldValueIDTracker = false ;

                           public boolean isOldValueIDSpecified(){
                               return localOldValueIDTracker;
                           }

                           

                           /**
                           * Auto generated getter method
                           * @return long
                           */
                           public  long getOldValueID(){
                               return localOldValueID;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param OldValueID
                               */
                               public void setOldValueID(long param){
                            
                                       // setting primitive attribute tracker to true
                                       localOldValueIDTracker =
                                       param != java.lang.Long.MIN_VALUE;
                                   
                                            this.localOldValueID=param;
                                    

                               }
                            

                        /**
                        * field for NewValueID
                        */

                        
                                    protected long localNewValueID ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localNewValueIDTracker = false ;

                           public boolean isNewValueIDSpecified(){
                               return localNewValueIDTracker;
                           }

                           

                           /**
                           * Auto generated getter method
                           * @return long
                           */
                           public  long getNewValueID(){
                               return localNewValueID;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param NewValueID
                               */
                               public void setNewValueID(long param){
                            
                                       // setting primitive attribute tracker to true
                                       localNewValueIDTracker =
                                       param != java.lang.Long.MIN_VALUE;
                                   
                                            this.localNewValueID=param;
                                    

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
                        * field for ChangedSubVariants
                        * This was an Array!
                        */

                        
                                    protected com.bosch.caltool.apic.ws.ProjectIdCardChangedSubVarType[] localChangedSubVariants ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localChangedSubVariantsTracker = false ;

                           public boolean isChangedSubVariantsSpecified(){
                               return localChangedSubVariantsTracker;
                           }

                           

                           /**
                           * Auto generated getter method
                           * @return com.bosch.caltool.apic.ws.ProjectIdCardChangedSubVarType[]
                           */
                           public  com.bosch.caltool.apic.ws.ProjectIdCardChangedSubVarType[] getChangedSubVariants(){
                               return localChangedSubVariants;
                           }

                           
                        


                               
                              /**
                               * validate the array for ChangedSubVariants
                               */
                              protected void validateChangedSubVariants(com.bosch.caltool.apic.ws.ProjectIdCardChangedSubVarType[] param){
                             
                              }


                             /**
                              * Auto generated setter method
                              * @param param ChangedSubVariants
                              */
                              public void setChangedSubVariants(com.bosch.caltool.apic.ws.ProjectIdCardChangedSubVarType[] param){
                              
                                   validateChangedSubVariants(param);

                               localChangedSubVariantsTracker = param != null;
                                      
                                      this.localChangedSubVariants=param;
                              }

                               
                             
                             /**
                             * Auto generated add method for the array for convenience
                             * @param param com.bosch.caltool.apic.ws.ProjectIdCardChangedSubVarType
                             */
                             public void addChangedSubVariants(com.bosch.caltool.apic.ws.ProjectIdCardChangedSubVarType param){
                                   if (localChangedSubVariants == null){
                                   localChangedSubVariants = new com.bosch.caltool.apic.ws.ProjectIdCardChangedSubVarType[]{};
                                   }

                            
                                 //update the setting tracker
                                localChangedSubVariantsTracker = true;
                            

                               java.util.List list =
                            org.apache.axis2.databinding.utils.ConverterUtil.toList(localChangedSubVariants);
                               list.add(param);
                               this.localChangedSubVariants =
                             (com.bosch.caltool.apic.ws.ProjectIdCardChangedSubVarType[])list.toArray(
                            new com.bosch.caltool.apic.ws.ProjectIdCardChangedSubVarType[list.size()]);

                             }
                             

                        /**
                        * field for PidcVersionChangeNum
                        */

                        
                                    protected long localPidcVersionChangeNum ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localPidcVersionChangeNumTracker = false ;

                           public boolean isPidcVersionChangeNumSpecified(){
                               return localPidcVersionChangeNumTracker;
                           }

                           

                           /**
                           * Auto generated getter method
                           * @return long
                           */
                           public  long getPidcVersionChangeNum(){
                               return localPidcVersionChangeNum;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param PidcVersionChangeNum
                               */
                               public void setPidcVersionChangeNum(long param){
                            
                                       // setting primitive attribute tracker to true
                                       localPidcVersionChangeNumTracker =
                                       param != java.lang.Long.MIN_VALUE;
                                   
                                            this.localPidcVersionChangeNum=param;
                                    

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
                           namespacePrefix+":ProjectIdCardChangedVariantsType",
                           xmlWriter);
                   } else {
                       writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","type",
                           "ProjectIdCardChangedVariantsType",
                           xmlWriter);
                   }

               
                   }
               
                                    namespace = "";
                                    writeStartElement(null, namespace, "variantID", xmlWriter);
                             
                                               if (localVariantID==java.lang.Long.MIN_VALUE) {
                                           
                                                         throw new org.apache.axis2.databinding.ADBException("variantID cannot be null!!");
                                                      
                                               } else {
                                                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localVariantID));
                                               }
                                    
                                   xmlWriter.writeEndElement();
                              if (localOldValueIDTracker){
                                    namespace = "";
                                    writeStartElement(null, namespace, "oldValueID", xmlWriter);
                             
                                               if (localOldValueID==java.lang.Long.MIN_VALUE) {
                                           
                                                         throw new org.apache.axis2.databinding.ADBException("oldValueID cannot be null!!");
                                                      
                                               } else {
                                                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localOldValueID));
                                               }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localNewValueIDTracker){
                                    namespace = "";
                                    writeStartElement(null, namespace, "newValueID", xmlWriter);
                             
                                               if (localNewValueID==java.lang.Long.MIN_VALUE) {
                                           
                                                         throw new org.apache.axis2.databinding.ADBException("newValueID cannot be null!!");
                                                      
                                               } else {
                                                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localNewValueID));
                                               }
                                    
                                   xmlWriter.writeEndElement();
                             }
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
                                 } if (localChangedSubVariantsTracker){
                                       if (localChangedSubVariants!=null){
                                            for (int i = 0;i < localChangedSubVariants.length;i++){
                                                if (localChangedSubVariants[i] != null){
                                                 localChangedSubVariants[i].serialize(new javax.xml.namespace.QName("","changedSubVariants"),
                                                           xmlWriter);
                                                } else {
                                                   
                                                        // we don't have to do any thing since minOccures is zero
                                                    
                                                }

                                            }
                                     } else {
                                        
                                               throw new org.apache.axis2.databinding.ADBException("changedSubVariants cannot be null!!");
                                        
                                    }
                                 } if (localPidcVersionChangeNumTracker){
                                    namespace = "";
                                    writeStartElement(null, namespace, "pidcVersionChangeNum", xmlWriter);
                             
                                               if (localPidcVersionChangeNum==java.lang.Long.MIN_VALUE) {
                                           
                                                         throw new org.apache.axis2.databinding.ADBException("pidcVersionChangeNum cannot be null!!");
                                                      
                                               } else {
                                                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localPidcVersionChangeNum));
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

                
                                      elementList.add(new javax.xml.namespace.QName("",
                                                                      "variantID"));
                                 
                                elementList.add(
                                   org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localVariantID));
                             if (localOldValueIDTracker){
                                      elementList.add(new javax.xml.namespace.QName("",
                                                                      "oldValueID"));
                                 
                                elementList.add(
                                   org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localOldValueID));
                            } if (localNewValueIDTracker){
                                      elementList.add(new javax.xml.namespace.QName("",
                                                                      "newValueID"));
                                 
                                elementList.add(
                                   org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localNewValueID));
                            }
                                      elementList.add(new javax.xml.namespace.QName("",
                                                                      "oldChangeNumber"));
                                 
                                elementList.add(
                                   org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localOldChangeNumber));
                            
                                      elementList.add(new javax.xml.namespace.QName("",
                                                                      "newChangeNumber"));
                                 
                                elementList.add(
                                   org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localNewChangeNumber));
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

                        } if (localChangedSubVariantsTracker){
                             if (localChangedSubVariants!=null) {
                                 for (int i = 0;i < localChangedSubVariants.length;i++){

                                    if (localChangedSubVariants[i] != null){
                                         elementList.add(new javax.xml.namespace.QName("",
                                                                          "changedSubVariants"));
                                         elementList.add(localChangedSubVariants[i]);
                                    } else {
                                        
                                                // nothing to do
                                            
                                    }

                                 }
                             } else {
                                 
                                        throw new org.apache.axis2.databinding.ADBException("changedSubVariants cannot be null!!");
                                    
                             }

                        } if (localPidcVersionChangeNumTracker){
                                      elementList.add(new javax.xml.namespace.QName("",
                                                                      "pidcVersionChangeNum"));
                                 
                                elementList.add(
                                   org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localPidcVersionChangeNum));
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
        public static ProjectIdCardChangedVariantsType parse(javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception{
            ProjectIdCardChangedVariantsType object =
                new ProjectIdCardChangedVariantsType();

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
                    
                            if (!"ProjectIdCardChangedVariantsType".equals(type)){
                                //find namespace for the prefix
                                java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                                return (ProjectIdCardChangedVariantsType)com.bosch.caltool.apic.ws.ExtensionMapper.getTypeObject(
                                     nsUri,type,reader);
                              }
                        

                  }
                

                }

                

                
                // Note all attributes that were handled. Used to differ normal attributes
                // from anyAttributes.
                java.util.Vector handledAttributes = new java.util.Vector();
                

                
                    
                    reader.next();
                
                        java.util.ArrayList list11 = new java.util.ArrayList();
                    
                        java.util.ArrayList list12 = new java.util.ArrayList();
                    
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("","variantID").equals(reader.getName())){
                                
                                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                    if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                        throw new org.apache.axis2.databinding.ADBException("The element: "+"variantID" +"  cannot be null");
                                    }
                                    

                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setVariantID(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToLong(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                else{
                                    // A start element we are not expecting indicates an invalid parameter was passed
                                    throw new org.apache.axis2.databinding.ADBException("Unexpected subelement " + reader.getName());
                                }
                            
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("","oldValueID").equals(reader.getName())){
                                
                                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                    if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                        throw new org.apache.axis2.databinding.ADBException("The element: "+"oldValueID" +"  cannot be null");
                                    }
                                    

                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setOldValueID(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToLong(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                               object.setOldValueID(java.lang.Long.MIN_VALUE);
                                           
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("","newValueID").equals(reader.getName())){
                                
                                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                    if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                        throw new org.apache.axis2.databinding.ADBException("The element: "+"newValueID" +"  cannot be null");
                                    }
                                    

                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setNewValueID(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToLong(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                               object.setNewValueID(java.lang.Long.MIN_VALUE);
                                           
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
                                    list11.add(com.bosch.caltool.apic.ws.ProjectIdCardChangedAttributeType.Factory.parse(reader));
                                                                
                                                        //loop until we find a start element that is not part of this array
                                                        boolean loopDone11 = false;
                                                        while(!loopDone11){
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
                                                                loopDone11 = true;
                                                            } else {
                                                                if (new javax.xml.namespace.QName("","changedAttributes").equals(reader.getName())){
                                                                    list11.add(com.bosch.caltool.apic.ws.ProjectIdCardChangedAttributeType.Factory.parse(reader));
                                                                        
                                                                }else{
                                                                    loopDone11 = true;
                                                                }
                                                            }
                                                        }
                                                        // call the converter utility  to convert and set the array
                                                        
                                                        object.setChangedAttributes((com.bosch.caltool.apic.ws.ProjectIdCardChangedAttributeType[])
                                                            org.apache.axis2.databinding.utils.ConverterUtil.convertToArray(
                                                                com.bosch.caltool.apic.ws.ProjectIdCardChangedAttributeType.class,
                                                                list11));
                                                            
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("","changedSubVariants").equals(reader.getName())){
                                
                                    
                                    
                                    // Process the array and step past its final element's end.
                                    list12.add(com.bosch.caltool.apic.ws.ProjectIdCardChangedSubVarType.Factory.parse(reader));
                                                                
                                                        //loop until we find a start element that is not part of this array
                                                        boolean loopDone12 = false;
                                                        while(!loopDone12){
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
                                                                loopDone12 = true;
                                                            } else {
                                                                if (new javax.xml.namespace.QName("","changedSubVariants").equals(reader.getName())){
                                                                    list12.add(com.bosch.caltool.apic.ws.ProjectIdCardChangedSubVarType.Factory.parse(reader));
                                                                        
                                                                }else{
                                                                    loopDone12 = true;
                                                                }
                                                            }
                                                        }
                                                        // call the converter utility  to convert and set the array
                                                        
                                                        object.setChangedSubVariants((com.bosch.caltool.apic.ws.ProjectIdCardChangedSubVarType[])
                                                            org.apache.axis2.databinding.utils.ConverterUtil.convertToArray(
                                                                com.bosch.caltool.apic.ws.ProjectIdCardChangedSubVarType.class,
                                                                list12));
                                                            
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("","pidcVersionChangeNum").equals(reader.getName())){
                                
                                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                    if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                        throw new org.apache.axis2.databinding.ADBException("The element: "+"pidcVersionChangeNum" +"  cannot be null");
                                    }
                                    

                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setPidcVersionChangeNum(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToLong(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                               object.setPidcVersionChangeNum(java.lang.Long.MIN_VALUE);
                                           
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
           
    