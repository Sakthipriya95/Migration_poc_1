
/**
 * AttrDiffType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.2  Built on : Apr 17, 2012 (05:34:40 IST)
 */

            
                package com.bosch.caltool.apic.ws;
            

            /**
            *  AttrDiffType bean class
            */
            @SuppressWarnings({"unchecked","unused"})
        
        public  class AttrDiffType
        implements org.apache.axis2.databinding.ADBBean{
        /* This type was generated from the piece of schema that had
                name = AttrDiffType
                Namespace URI = http://ws.apic.caltool.bosch.com
                Namespace Prefix = ns1
                */
            

                        /**
                        * field for PidcId
                        */

                        
                                    protected long localPidcId ;
                                

                           /**
                           * Auto generated getter method
                           * @return long
                           */
                           public  long getPidcId(){
                               return localPidcId;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param PidcId
                               */
                               public void setPidcId(long param){
                            
                                            this.localPidcId=param;
                                    

                               }
                            

                        /**
                        * field for VariantId
                        */

                        
                                    protected long localVariantId ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localVariantIdTracker = false ;

                           public boolean isVariantIdSpecified(){
                               return localVariantIdTracker;
                           }

                           

                           /**
                           * Auto generated getter method
                           * @return long
                           */
                           public  long getVariantId(){
                               return localVariantId;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param VariantId
                               */
                               public void setVariantId(long param){
                            
                                       // setting primitive attribute tracker to true
                                       localVariantIdTracker =
                                       param != java.lang.Long.MIN_VALUE;
                                   
                                            this.localVariantId=param;
                                    

                               }
                            

                        /**
                        * field for SubVariantId
                        */

                        
                                    protected long localSubVariantId ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localSubVariantIdTracker = false ;

                           public boolean isSubVariantIdSpecified(){
                               return localSubVariantIdTracker;
                           }

                           

                           /**
                           * Auto generated getter method
                           * @return long
                           */
                           public  long getSubVariantId(){
                               return localSubVariantId;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param SubVariantId
                               */
                               public void setSubVariantId(long param){
                            
                                       // setting primitive attribute tracker to true
                                       localSubVariantIdTracker =
                                       param != java.lang.Long.MIN_VALUE;
                                   
                                            this.localSubVariantId=param;
                                    

                               }
                            

                        /**
                        * field for Level
                        */

                        
                                    protected java.lang.String localLevel ;
                                

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getLevel(){
                               return localLevel;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param Level
                               */
                               public void setLevel(java.lang.String param){
                            
                                            this.localLevel=param;
                                    

                               }
                            

                        /**
                        * field for Attribute
                        */

                        
                                    protected com.bosch.caltool.apic.ws.Attribute localAttribute ;
                                

                           /**
                           * Auto generated getter method
                           * @return com.bosch.caltool.apic.ws.Attribute
                           */
                           public  com.bosch.caltool.apic.ws.Attribute getAttribute(){
                               return localAttribute;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param Attribute
                               */
                               public void setAttribute(com.bosch.caltool.apic.ws.Attribute param){
                            
                                            this.localAttribute=param;
                                    

                               }
                            

                        /**
                        * field for ChangedItem
                        */

                        
                                    protected java.lang.String localChangedItem ;
                                

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getChangedItem(){
                               return localChangedItem;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param ChangedItem
                               */
                               public void setChangedItem(java.lang.String param){
                            
                                            this.localChangedItem=param;
                                    

                               }
                            

                        /**
                        * field for OldAttributeValue
                        */

                        
                                    protected com.bosch.caltool.apic.ws.AttributeValue localOldAttributeValue ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localOldAttributeValueTracker = false ;

                           public boolean isOldAttributeValueSpecified(){
                               return localOldAttributeValueTracker;
                           }

                           

                           /**
                           * Auto generated getter method
                           * @return com.bosch.caltool.apic.ws.AttributeValue
                           */
                           public  com.bosch.caltool.apic.ws.AttributeValue getOldAttributeValue(){
                               return localOldAttributeValue;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param OldAttributeValue
                               */
                               public void setOldAttributeValue(com.bosch.caltool.apic.ws.AttributeValue param){
                            localOldAttributeValueTracker = param != null;
                                   
                                            this.localOldAttributeValue=param;
                                    

                               }
                            

                        /**
                        * field for NewAttributeValue
                        */

                        
                                    protected com.bosch.caltool.apic.ws.AttributeValue localNewAttributeValue ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localNewAttributeValueTracker = false ;

                           public boolean isNewAttributeValueSpecified(){
                               return localNewAttributeValueTracker;
                           }

                           

                           /**
                           * Auto generated getter method
                           * @return com.bosch.caltool.apic.ws.AttributeValue
                           */
                           public  com.bosch.caltool.apic.ws.AttributeValue getNewAttributeValue(){
                               return localNewAttributeValue;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param NewAttributeValue
                               */
                               public void setNewAttributeValue(com.bosch.caltool.apic.ws.AttributeValue param){
                            localNewAttributeValueTracker = param != null;
                                   
                                            this.localNewAttributeValue=param;
                                    

                               }
                            

                        /**
                        * field for OldValue
                        */

                        
                                    protected java.lang.String localOldValue ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localOldValueTracker = false ;

                           public boolean isOldValueSpecified(){
                               return localOldValueTracker;
                           }

                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getOldValue(){
                               return localOldValue;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param OldValue
                               */
                               public void setOldValue(java.lang.String param){
                            localOldValueTracker = param != null;
                                   
                                            this.localOldValue=param;
                                    

                               }
                            

                        /**
                        * field for NewValue
                        */

                        
                                    protected java.lang.String localNewValue ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localNewValueTracker = false ;

                           public boolean isNewValueSpecified(){
                               return localNewValueTracker;
                           }

                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getNewValue(){
                               return localNewValue;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param NewValue
                               */
                               public void setNewValue(java.lang.String param){
                            localNewValueTracker = param != null;
                                   
                                            this.localNewValue=param;
                                    

                               }
                            

                        /**
                        * field for ModifiedUser
                        */

                        
                                    protected java.lang.String localModifiedUser ;
                                

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
                            
                                            this.localModifiedUser=param;
                                    

                               }
                            

                        /**
                        * field for ModifiedName
                        */

                        
                                    protected java.lang.String localModifiedName ;
                                

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getModifiedName(){
                               return localModifiedName;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param ModifiedName
                               */
                               public void setModifiedName(java.lang.String param){
                            
                                            this.localModifiedName=param;
                                    

                               }
                            

                        /**
                        * field for ModifiedOn
                        */

                        
                                    protected java.util.Calendar localModifiedOn ;
                                

                           /**
                           * Auto generated getter method
                           * @return java.util.Calendar
                           */
                           public  java.util.Calendar getModifiedOn(){
                               return localModifiedOn;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param ModifiedOn
                               */
                               public void setModifiedOn(java.util.Calendar param){
                            
                                            this.localModifiedOn=param;
                                    

                               }
                            

                        /**
                        * field for VersionId
                        */

                        
                                    protected long localVersionId ;
                                

                           /**
                           * Auto generated getter method
                           * @return long
                           */
                           public  long getVersionId(){
                               return localVersionId;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param VersionId
                               */
                               public void setVersionId(long param){
                            
                                            this.localVersionId=param;
                                    

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
                        * field for AttributeChange
                        */

                        
                                    protected boolean localAttributeChange ;
                                

                           /**
                           * Auto generated getter method
                           * @return boolean
                           */
                           public  boolean getAttributeChange(){
                               return localAttributeChange;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param AttributeChange
                               */
                               public void setAttributeChange(boolean param){
                            
                                            this.localAttributeChange=param;
                                    

                               }
                            

                        /**
                        * field for PidcVersVersId
                        */

                        
                                    protected long localPidcVersVersId ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localPidcVersVersIdTracker = false ;

                           public boolean isPidcVersVersIdSpecified(){
                               return localPidcVersVersIdTracker;
                           }

                           

                           /**
                           * Auto generated getter method
                           * @return long
                           */
                           public  long getPidcVersVersId(){
                               return localPidcVersVersId;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param PidcVersVersId
                               */
                               public void setPidcVersVersId(long param){
                            
                                       // setting primitive attribute tracker to true
                                       localPidcVersVersIdTracker =
                                       param != java.lang.Long.MIN_VALUE;
                                   
                                            this.localPidcVersVersId=param;
                                    

                               }
                            

                        /**
                        * field for FocusMatrixChange
                        */

                        
                                    protected boolean localFocusMatrixChange ;
                                

                           /**
                           * Auto generated getter method
                           * @return boolean
                           */
                           public  boolean getFocusMatrixChange(){
                               return localFocusMatrixChange;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param FocusMatrixChange
                               */
                               public void setFocusMatrixChange(boolean param){
                            
                                            this.localFocusMatrixChange=param;
                                    

                               }
                            

                        /**
                        * field for UseCaseSectionId
                        */

                        
                                    protected long localUseCaseSectionId ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localUseCaseSectionIdTracker = false ;

                           public boolean isUseCaseSectionIdSpecified(){
                               return localUseCaseSectionIdTracker;
                           }

                           

                           /**
                           * Auto generated getter method
                           * @return long
                           */
                           public  long getUseCaseSectionId(){
                               return localUseCaseSectionId;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param UseCaseSectionId
                               */
                               public void setUseCaseSectionId(long param){
                            
                                       // setting primitive attribute tracker to true
                                       localUseCaseSectionIdTracker =
                                       param != java.lang.Long.MIN_VALUE;
                                   
                                            this.localUseCaseSectionId=param;
                                    

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
                           namespacePrefix+":AttrDiffType",
                           xmlWriter);
                   } else {
                       writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","type",
                           "AttrDiffType",
                           xmlWriter);
                   }

               
                   }
               
                                    namespace = "";
                                    writeStartElement(null, namespace, "pidcId", xmlWriter);
                             
                                               if (localPidcId==java.lang.Long.MIN_VALUE) {
                                           
                                                         throw new org.apache.axis2.databinding.ADBException("pidcId cannot be null!!");
                                                      
                                               } else {
                                                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localPidcId));
                                               }
                                    
                                   xmlWriter.writeEndElement();
                              if (localVariantIdTracker){
                                    namespace = "";
                                    writeStartElement(null, namespace, "variantId", xmlWriter);
                             
                                               if (localVariantId==java.lang.Long.MIN_VALUE) {
                                           
                                                         throw new org.apache.axis2.databinding.ADBException("variantId cannot be null!!");
                                                      
                                               } else {
                                                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localVariantId));
                                               }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localSubVariantIdTracker){
                                    namespace = "";
                                    writeStartElement(null, namespace, "subVariantId", xmlWriter);
                             
                                               if (localSubVariantId==java.lang.Long.MIN_VALUE) {
                                           
                                                         throw new org.apache.axis2.databinding.ADBException("subVariantId cannot be null!!");
                                                      
                                               } else {
                                                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localSubVariantId));
                                               }
                                    
                                   xmlWriter.writeEndElement();
                             }
                                    namespace = "";
                                    writeStartElement(null, namespace, "level", xmlWriter);
                             

                                          if (localLevel==null){
                                              // write the nil attribute
                                              
                                                     throw new org.apache.axis2.databinding.ADBException("level cannot be null!!");
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(localLevel);
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                             
                                            if (localAttribute==null){
                                                 throw new org.apache.axis2.databinding.ADBException("attribute cannot be null!!");
                                            }
                                           localAttribute.serialize(new javax.xml.namespace.QName("","attribute"),
                                               xmlWriter);
                                        
                                    namespace = "";
                                    writeStartElement(null, namespace, "changedItem", xmlWriter);
                             

                                          if (localChangedItem==null){
                                              // write the nil attribute
                                              
                                                     throw new org.apache.axis2.databinding.ADBException("changedItem cannot be null!!");
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(localChangedItem);
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                              if (localOldAttributeValueTracker){
                                            if (localOldAttributeValue==null){
                                                 throw new org.apache.axis2.databinding.ADBException("oldAttributeValue cannot be null!!");
                                            }
                                           localOldAttributeValue.serialize(new javax.xml.namespace.QName("","oldAttributeValue"),
                                               xmlWriter);
                                        } if (localNewAttributeValueTracker){
                                            if (localNewAttributeValue==null){
                                                 throw new org.apache.axis2.databinding.ADBException("newAttributeValue cannot be null!!");
                                            }
                                           localNewAttributeValue.serialize(new javax.xml.namespace.QName("","newAttributeValue"),
                                               xmlWriter);
                                        } if (localOldValueTracker){
                                    namespace = "";
                                    writeStartElement(null, namespace, "oldValue", xmlWriter);
                             

                                          if (localOldValue==null){
                                              // write the nil attribute
                                              
                                                     throw new org.apache.axis2.databinding.ADBException("oldValue cannot be null!!");
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(localOldValue);
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                             } if (localNewValueTracker){
                                    namespace = "";
                                    writeStartElement(null, namespace, "newValue", xmlWriter);
                             

                                          if (localNewValue==null){
                                              // write the nil attribute
                                              
                                                     throw new org.apache.axis2.databinding.ADBException("newValue cannot be null!!");
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(localNewValue);
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                             }
                                    namespace = "";
                                    writeStartElement(null, namespace, "modifiedUser", xmlWriter);
                             

                                          if (localModifiedUser==null){
                                              // write the nil attribute
                                              
                                                     throw new org.apache.axis2.databinding.ADBException("modifiedUser cannot be null!!");
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(localModifiedUser);
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                             
                                    namespace = "";
                                    writeStartElement(null, namespace, "modifiedName", xmlWriter);
                             

                                          if (localModifiedName==null){
                                              // write the nil attribute
                                              
                                                     throw new org.apache.axis2.databinding.ADBException("modifiedName cannot be null!!");
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(localModifiedName);
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                             
                                    namespace = "";
                                    writeStartElement(null, namespace, "modifiedOn", xmlWriter);
                             

                                          if (localModifiedOn==null){
                                              // write the nil attribute
                                              
                                                     throw new org.apache.axis2.databinding.ADBException("modifiedOn cannot be null!!");
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localModifiedOn));
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                             
                                    namespace = "";
                                    writeStartElement(null, namespace, "versionId", xmlWriter);
                             
                                               if (localVersionId==java.lang.Long.MIN_VALUE) {
                                           
                                                         throw new org.apache.axis2.databinding.ADBException("versionId cannot be null!!");
                                                      
                                               } else {
                                                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localVersionId));
                                               }
                                    
                                   xmlWriter.writeEndElement();
                             
                                    namespace = "";
                                    writeStartElement(null, namespace, "pidcVersion", xmlWriter);
                             
                                               if (localPidcVersion==java.lang.Long.MIN_VALUE) {
                                           
                                                         throw new org.apache.axis2.databinding.ADBException("pidcVersion cannot be null!!");
                                                      
                                               } else {
                                                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localPidcVersion));
                                               }
                                    
                                   xmlWriter.writeEndElement();
                             
                                    namespace = "";
                                    writeStartElement(null, namespace, "attributeChange", xmlWriter);
                             
                                               if (false) {
                                           
                                                         throw new org.apache.axis2.databinding.ADBException("attributeChange cannot be null!!");
                                                      
                                               } else {
                                                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localAttributeChange));
                                               }
                                    
                                   xmlWriter.writeEndElement();
                              if (localPidcVersVersIdTracker){
                                    namespace = "";
                                    writeStartElement(null, namespace, "pidcVersVersId", xmlWriter);
                             
                                               if (localPidcVersVersId==java.lang.Long.MIN_VALUE) {
                                           
                                                         throw new org.apache.axis2.databinding.ADBException("pidcVersVersId cannot be null!!");
                                                      
                                               } else {
                                                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localPidcVersVersId));
                                               }
                                    
                                   xmlWriter.writeEndElement();
                             }
                                    namespace = "";
                                    writeStartElement(null, namespace, "focusMatrixChange", xmlWriter);
                             
                                               if (false) {
                                           
                                                         throw new org.apache.axis2.databinding.ADBException("focusMatrixChange cannot be null!!");
                                                      
                                               } else {
                                                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localFocusMatrixChange));
                                               }
                                    
                                   xmlWriter.writeEndElement();
                              if (localUseCaseSectionIdTracker){
                                    namespace = "";
                                    writeStartElement(null, namespace, "useCaseSectionId", xmlWriter);
                             
                                               if (localUseCaseSectionId==java.lang.Long.MIN_VALUE) {
                                           
                                                         throw new org.apache.axis2.databinding.ADBException("useCaseSectionId cannot be null!!");
                                                      
                                               } else {
                                                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localUseCaseSectionId));
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
                                                                      "pidcId"));
                                 
                                elementList.add(
                                   org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localPidcId));
                             if (localVariantIdTracker){
                                      elementList.add(new javax.xml.namespace.QName("",
                                                                      "variantId"));
                                 
                                elementList.add(
                                   org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localVariantId));
                            } if (localSubVariantIdTracker){
                                      elementList.add(new javax.xml.namespace.QName("",
                                                                      "subVariantId"));
                                 
                                elementList.add(
                                   org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localSubVariantId));
                            }
                                      elementList.add(new javax.xml.namespace.QName("",
                                                                      "level"));
                                 
                                        if (localLevel != null){
                                            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localLevel));
                                        } else {
                                           throw new org.apache.axis2.databinding.ADBException("level cannot be null!!");
                                        }
                                    
                            elementList.add(new javax.xml.namespace.QName("",
                                                                      "attribute"));
                            
                            
                                    if (localAttribute==null){
                                         throw new org.apache.axis2.databinding.ADBException("attribute cannot be null!!");
                                    }
                                    elementList.add(localAttribute);
                                
                                      elementList.add(new javax.xml.namespace.QName("",
                                                                      "changedItem"));
                                 
                                        if (localChangedItem != null){
                                            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localChangedItem));
                                        } else {
                                           throw new org.apache.axis2.databinding.ADBException("changedItem cannot be null!!");
                                        }
                                     if (localOldAttributeValueTracker){
                            elementList.add(new javax.xml.namespace.QName("",
                                                                      "oldAttributeValue"));
                            
                            
                                    if (localOldAttributeValue==null){
                                         throw new org.apache.axis2.databinding.ADBException("oldAttributeValue cannot be null!!");
                                    }
                                    elementList.add(localOldAttributeValue);
                                } if (localNewAttributeValueTracker){
                            elementList.add(new javax.xml.namespace.QName("",
                                                                      "newAttributeValue"));
                            
                            
                                    if (localNewAttributeValue==null){
                                         throw new org.apache.axis2.databinding.ADBException("newAttributeValue cannot be null!!");
                                    }
                                    elementList.add(localNewAttributeValue);
                                } if (localOldValueTracker){
                                      elementList.add(new javax.xml.namespace.QName("",
                                                                      "oldValue"));
                                 
                                        if (localOldValue != null){
                                            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localOldValue));
                                        } else {
                                           throw new org.apache.axis2.databinding.ADBException("oldValue cannot be null!!");
                                        }
                                    } if (localNewValueTracker){
                                      elementList.add(new javax.xml.namespace.QName("",
                                                                      "newValue"));
                                 
                                        if (localNewValue != null){
                                            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localNewValue));
                                        } else {
                                           throw new org.apache.axis2.databinding.ADBException("newValue cannot be null!!");
                                        }
                                    }
                                      elementList.add(new javax.xml.namespace.QName("",
                                                                      "modifiedUser"));
                                 
                                        if (localModifiedUser != null){
                                            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localModifiedUser));
                                        } else {
                                           throw new org.apache.axis2.databinding.ADBException("modifiedUser cannot be null!!");
                                        }
                                    
                                      elementList.add(new javax.xml.namespace.QName("",
                                                                      "modifiedName"));
                                 
                                        if (localModifiedName != null){
                                            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localModifiedName));
                                        } else {
                                           throw new org.apache.axis2.databinding.ADBException("modifiedName cannot be null!!");
                                        }
                                    
                                      elementList.add(new javax.xml.namespace.QName("",
                                                                      "modifiedOn"));
                                 
                                        if (localModifiedOn != null){
                                            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localModifiedOn));
                                        } else {
                                           throw new org.apache.axis2.databinding.ADBException("modifiedOn cannot be null!!");
                                        }
                                    
                                      elementList.add(new javax.xml.namespace.QName("",
                                                                      "versionId"));
                                 
                                elementList.add(
                                   org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localVersionId));
                            
                                      elementList.add(new javax.xml.namespace.QName("",
                                                                      "pidcVersion"));
                                 
                                elementList.add(
                                   org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localPidcVersion));
                            
                                      elementList.add(new javax.xml.namespace.QName("",
                                                                      "attributeChange"));
                                 
                                elementList.add(
                                   org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localAttributeChange));
                             if (localPidcVersVersIdTracker){
                                      elementList.add(new javax.xml.namespace.QName("",
                                                                      "pidcVersVersId"));
                                 
                                elementList.add(
                                   org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localPidcVersVersId));
                            }
                                      elementList.add(new javax.xml.namespace.QName("",
                                                                      "focusMatrixChange"));
                                 
                                elementList.add(
                                   org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localFocusMatrixChange));
                             if (localUseCaseSectionIdTracker){
                                      elementList.add(new javax.xml.namespace.QName("",
                                                                      "useCaseSectionId"));
                                 
                                elementList.add(
                                   org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localUseCaseSectionId));
                            } if (localUseCaseIdTracker){
                                      elementList.add(new javax.xml.namespace.QName("",
                                                                      "useCaseId"));
                                 
                                elementList.add(
                                   org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localUseCaseId));
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
        public static AttrDiffType parse(javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception{
            AttrDiffType object =
                new AttrDiffType();

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
                    
                            if (!"AttrDiffType".equals(type)){
                                //find namespace for the prefix
                                java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                                return (AttrDiffType)com.bosch.caltool.apic.ws.ExtensionMapper.getTypeObject(
                                     nsUri,type,reader);
                              }
                        

                  }
                

                }

                

                
                // Note all attributes that were handled. Used to differ normal attributes
                // from anyAttributes.
                java.util.Vector handledAttributes = new java.util.Vector();
                

                
                    
                    reader.next();
                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("","pidcId").equals(reader.getName())){
                                
                                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                    if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                        throw new org.apache.axis2.databinding.ADBException("The element: "+"pidcId" +"  cannot be null");
                                    }
                                    

                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setPidcId(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToLong(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                else{
                                    // A start element we are not expecting indicates an invalid parameter was passed
                                    throw new org.apache.axis2.databinding.ADBException("Unexpected subelement " + reader.getName());
                                }
                            
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("","variantId").equals(reader.getName())){
                                
                                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                    if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                        throw new org.apache.axis2.databinding.ADBException("The element: "+"variantId" +"  cannot be null");
                                    }
                                    

                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setVariantId(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToLong(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                               object.setVariantId(java.lang.Long.MIN_VALUE);
                                           
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("","subVariantId").equals(reader.getName())){
                                
                                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                    if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                        throw new org.apache.axis2.databinding.ADBException("The element: "+"subVariantId" +"  cannot be null");
                                    }
                                    

                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setSubVariantId(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToLong(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                               object.setSubVariantId(java.lang.Long.MIN_VALUE);
                                           
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("","level").equals(reader.getName())){
                                
                                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                    if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                        throw new org.apache.axis2.databinding.ADBException("The element: "+"level" +"  cannot be null");
                                    }
                                    

                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setLevel(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                else{
                                    // A start element we are not expecting indicates an invalid parameter was passed
                                    throw new org.apache.axis2.databinding.ADBException("Unexpected subelement " + reader.getName());
                                }
                            
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("","attribute").equals(reader.getName())){
                                
                                                object.setAttribute(com.bosch.caltool.apic.ws.Attribute.Factory.parse(reader));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                else{
                                    // A start element we are not expecting indicates an invalid parameter was passed
                                    throw new org.apache.axis2.databinding.ADBException("Unexpected subelement " + reader.getName());
                                }
                            
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("","changedItem").equals(reader.getName())){
                                
                                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                    if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                        throw new org.apache.axis2.databinding.ADBException("The element: "+"changedItem" +"  cannot be null");
                                    }
                                    

                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setChangedItem(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                else{
                                    // A start element we are not expecting indicates an invalid parameter was passed
                                    throw new org.apache.axis2.databinding.ADBException("Unexpected subelement " + reader.getName());
                                }
                            
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("","oldAttributeValue").equals(reader.getName())){
                                
                                                object.setOldAttributeValue(com.bosch.caltool.apic.ws.AttributeValue.Factory.parse(reader));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("","newAttributeValue").equals(reader.getName())){
                                
                                                object.setNewAttributeValue(com.bosch.caltool.apic.ws.AttributeValue.Factory.parse(reader));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("","oldValue").equals(reader.getName())){
                                
                                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                    if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                        throw new org.apache.axis2.databinding.ADBException("The element: "+"oldValue" +"  cannot be null");
                                    }
                                    

                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setOldValue(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("","newValue").equals(reader.getName())){
                                
                                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                    if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                        throw new org.apache.axis2.databinding.ADBException("The element: "+"newValue" +"  cannot be null");
                                    }
                                    

                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setNewValue(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                              
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
                                
                                else{
                                    // A start element we are not expecting indicates an invalid parameter was passed
                                    throw new org.apache.axis2.databinding.ADBException("Unexpected subelement " + reader.getName());
                                }
                            
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("","modifiedName").equals(reader.getName())){
                                
                                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                    if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                        throw new org.apache.axis2.databinding.ADBException("The element: "+"modifiedName" +"  cannot be null");
                                    }
                                    

                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setModifiedName(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                else{
                                    // A start element we are not expecting indicates an invalid parameter was passed
                                    throw new org.apache.axis2.databinding.ADBException("Unexpected subelement " + reader.getName());
                                }
                            
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("","modifiedOn").equals(reader.getName())){
                                
                                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                    if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                        throw new org.apache.axis2.databinding.ADBException("The element: "+"modifiedOn" +"  cannot be null");
                                    }
                                    

                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setModifiedOn(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToDateTime(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                else{
                                    // A start element we are not expecting indicates an invalid parameter was passed
                                    throw new org.apache.axis2.databinding.ADBException("Unexpected subelement " + reader.getName());
                                }
                            
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("","versionId").equals(reader.getName())){
                                
                                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                    if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                        throw new org.apache.axis2.databinding.ADBException("The element: "+"versionId" +"  cannot be null");
                                    }
                                    

                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setVersionId(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToLong(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                else{
                                    // A start element we are not expecting indicates an invalid parameter was passed
                                    throw new org.apache.axis2.databinding.ADBException("Unexpected subelement " + reader.getName());
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
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("","attributeChange").equals(reader.getName())){
                                
                                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                    if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                        throw new org.apache.axis2.databinding.ADBException("The element: "+"attributeChange" +"  cannot be null");
                                    }
                                    

                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setAttributeChange(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToBoolean(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                else{
                                    // A start element we are not expecting indicates an invalid parameter was passed
                                    throw new org.apache.axis2.databinding.ADBException("Unexpected subelement " + reader.getName());
                                }
                            
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("","pidcVersVersId").equals(reader.getName())){
                                
                                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                    if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                        throw new org.apache.axis2.databinding.ADBException("The element: "+"pidcVersVersId" +"  cannot be null");
                                    }
                                    

                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setPidcVersVersId(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToLong(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                               object.setPidcVersVersId(java.lang.Long.MIN_VALUE);
                                           
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("","focusMatrixChange").equals(reader.getName())){
                                
                                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                    if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                        throw new org.apache.axis2.databinding.ADBException("The element: "+"focusMatrixChange" +"  cannot be null");
                                    }
                                    

                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setFocusMatrixChange(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToBoolean(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                else{
                                    // A start element we are not expecting indicates an invalid parameter was passed
                                    throw new org.apache.axis2.databinding.ADBException("Unexpected subelement " + reader.getName());
                                }
                            
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("","useCaseSectionId").equals(reader.getName())){
                                
                                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                    if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                        throw new org.apache.axis2.databinding.ADBException("The element: "+"useCaseSectionId" +"  cannot be null");
                                    }
                                    

                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setUseCaseSectionId(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToLong(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                               object.setUseCaseSectionId(java.lang.Long.MIN_VALUE);
                                           
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
           
    