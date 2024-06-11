
/**
 * CalDataPhy.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.2  Built on : Apr 17, 2012 (05:34:40 IST)
 */

            
                package com.bosch.caltool.apic.ws;
            

            /**
            *  CalDataPhy bean class
            */
            @SuppressWarnings({"unchecked","unused"})
        
        public  class CalDataPhy
        implements org.apache.axis2.databinding.ADBBean{
        /* This type was generated from the piece of schema that had
                name = CalDataPhy
                Namespace URI = http://ws.apic.caltool.bosch.com
                Namespace Prefix = ns1
                */
            

                        /**
                        * field for Name
                        */

                        
                                    protected java.lang.String localName ;
                                

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getName(){
                               return localName;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param Name
                               */
                               public void setName(java.lang.String param){
                            
                                            this.localName=param;
                                    

                               }
                            

                        /**
                        * field for Type
                        */

                        
                                    protected java.lang.String localType ;
                                

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getType(){
                               return localType;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param Type
                               */
                               public void setType(java.lang.String param){
                            
                                            this.localType=param;
                                    

                               }
                            

                        /**
                        * field for Unit
                        */

                        
                                    protected java.lang.String localUnit ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localUnitTracker = false ;

                           public boolean isUnitSpecified(){
                               return localUnitTracker;
                           }

                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getUnit(){
                               return localUnit;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param Unit
                               */
                               public void setUnit(java.lang.String param){
                            localUnitTracker = param != null;
                                   
                                            this.localUnit=param;
                                    

                               }
                            

                        /**
                        * field for IsText
                        */

                        
                                    protected java.lang.String localIsText ;
                                

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getIsText(){
                               return localIsText;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param IsText
                               */
                               public void setIsText(java.lang.String param){
                            
                                            this.localIsText=param;
                                    

                               }
                            

                        /**
                        * field for AtomicValuePhy
                        * This was an Array!
                        */

                        
                                    protected com.bosch.caltool.apic.ws.AtomicValuePhy[] localAtomicValuePhy ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localAtomicValuePhyTracker = false ;

                           public boolean isAtomicValuePhySpecified(){
                               return localAtomicValuePhyTracker;
                           }

                           

                           /**
                           * Auto generated getter method
                           * @return com.bosch.caltool.apic.ws.AtomicValuePhy[]
                           */
                           public  com.bosch.caltool.apic.ws.AtomicValuePhy[] getAtomicValuePhy(){
                               return localAtomicValuePhy;
                           }

                           
                        


                               
                              /**
                               * validate the array for AtomicValuePhy
                               */
                              protected void validateAtomicValuePhy(com.bosch.caltool.apic.ws.AtomicValuePhy[] param){
                             
                              }


                             /**
                              * Auto generated setter method
                              * @param param AtomicValuePhy
                              */
                              public void setAtomicValuePhy(com.bosch.caltool.apic.ws.AtomicValuePhy[] param){
                              
                                   validateAtomicValuePhy(param);

                               localAtomicValuePhyTracker = param != null;
                                      
                                      this.localAtomicValuePhy=param;
                              }

                               
                             
                             /**
                             * Auto generated add method for the array for convenience
                             * @param param com.bosch.caltool.apic.ws.AtomicValuePhy
                             */
                             public void addAtomicValuePhy(com.bosch.caltool.apic.ws.AtomicValuePhy param){
                                   if (localAtomicValuePhy == null){
                                   localAtomicValuePhy = new com.bosch.caltool.apic.ws.AtomicValuePhy[]{};
                                   }

                            
                                 //update the setting tracker
                                localAtomicValuePhyTracker = true;
                            

                               java.util.List list =
                            org.apache.axis2.databinding.utils.ConverterUtil.toList(localAtomicValuePhy);
                               list.add(param);
                               this.localAtomicValuePhy =
                             (com.bosch.caltool.apic.ws.AtomicValuePhy[])list.toArray(
                            new com.bosch.caltool.apic.ws.AtomicValuePhy[list.size()]);

                             }
                             

                        /**
                        * field for AtomicValuePhy2NdDim
                        * This was an Array!
                        */

                        
                                    protected com.bosch.caltool.apic.ws.AtomicValuePhy[] localAtomicValuePhy2NdDim ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localAtomicValuePhy2NdDimTracker = false ;

                           public boolean isAtomicValuePhy2NdDimSpecified(){
                               return localAtomicValuePhy2NdDimTracker;
                           }

                           

                           /**
                           * Auto generated getter method
                           * @return com.bosch.caltool.apic.ws.AtomicValuePhy[]
                           */
                           public  com.bosch.caltool.apic.ws.AtomicValuePhy[] getAtomicValuePhy2NdDim(){
                               return localAtomicValuePhy2NdDim;
                           }

                           
                        


                               
                              /**
                               * validate the array for AtomicValuePhy2NdDim
                               */
                              protected void validateAtomicValuePhy2NdDim(com.bosch.caltool.apic.ws.AtomicValuePhy[] param){
                             
                              }


                             /**
                              * Auto generated setter method
                              * @param param AtomicValuePhy2NdDim
                              */
                              public void setAtomicValuePhy2NdDim(com.bosch.caltool.apic.ws.AtomicValuePhy[] param){
                              
                                   validateAtomicValuePhy2NdDim(param);

                               localAtomicValuePhy2NdDimTracker = param != null;
                                      
                                      this.localAtomicValuePhy2NdDim=param;
                              }

                               
                             
                             /**
                             * Auto generated add method for the array for convenience
                             * @param param com.bosch.caltool.apic.ws.AtomicValuePhy
                             */
                             public void addAtomicValuePhy2NdDim(com.bosch.caltool.apic.ws.AtomicValuePhy param){
                                   if (localAtomicValuePhy2NdDim == null){
                                   localAtomicValuePhy2NdDim = new com.bosch.caltool.apic.ws.AtomicValuePhy[]{};
                                   }

                            
                                 //update the setting tracker
                                localAtomicValuePhy2NdDimTracker = true;
                            

                               java.util.List list =
                            org.apache.axis2.databinding.utils.ConverterUtil.toList(localAtomicValuePhy2NdDim);
                               list.add(param);
                               this.localAtomicValuePhy2NdDim =
                             (com.bosch.caltool.apic.ws.AtomicValuePhy[])list.toArray(
                            new com.bosch.caltool.apic.ws.AtomicValuePhy[list.size()]);

                             }
                             

                        /**
                        * field for AtomicValuePhy3RdDim
                        * This was an Array!
                        */

                        
                                    protected com.bosch.caltool.apic.ws.AtomicValuePhy[] localAtomicValuePhy3RdDim ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localAtomicValuePhy3RdDimTracker = false ;

                           public boolean isAtomicValuePhy3RdDimSpecified(){
                               return localAtomicValuePhy3RdDimTracker;
                           }

                           

                           /**
                           * Auto generated getter method
                           * @return com.bosch.caltool.apic.ws.AtomicValuePhy[]
                           */
                           public  com.bosch.caltool.apic.ws.AtomicValuePhy[] getAtomicValuePhy3RdDim(){
                               return localAtomicValuePhy3RdDim;
                           }

                           
                        


                               
                              /**
                               * validate the array for AtomicValuePhy3RdDim
                               */
                              protected void validateAtomicValuePhy3RdDim(com.bosch.caltool.apic.ws.AtomicValuePhy[] param){
                             
                              }


                             /**
                              * Auto generated setter method
                              * @param param AtomicValuePhy3RdDim
                              */
                              public void setAtomicValuePhy3RdDim(com.bosch.caltool.apic.ws.AtomicValuePhy[] param){
                              
                                   validateAtomicValuePhy3RdDim(param);

                               localAtomicValuePhy3RdDimTracker = param != null;
                                      
                                      this.localAtomicValuePhy3RdDim=param;
                              }

                               
                             
                             /**
                             * Auto generated add method for the array for convenience
                             * @param param com.bosch.caltool.apic.ws.AtomicValuePhy
                             */
                             public void addAtomicValuePhy3RdDim(com.bosch.caltool.apic.ws.AtomicValuePhy param){
                                   if (localAtomicValuePhy3RdDim == null){
                                   localAtomicValuePhy3RdDim = new com.bosch.caltool.apic.ws.AtomicValuePhy[]{};
                                   }

                            
                                 //update the setting tracker
                                localAtomicValuePhy3RdDimTracker = true;
                            

                               java.util.List list =
                            org.apache.axis2.databinding.utils.ConverterUtil.toList(localAtomicValuePhy3RdDim);
                               list.add(param);
                               this.localAtomicValuePhy3RdDim =
                             (com.bosch.caltool.apic.ws.AtomicValuePhy[])list.toArray(
                            new com.bosch.caltool.apic.ws.AtomicValuePhy[list.size()]);

                             }
                             

                        /**
                        * field for XAxis
                        * This was an Array!
                        */

                        
                                    protected com.bosch.caltool.apic.ws.CalDataAxis[] localXAxis ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localXAxisTracker = false ;

                           public boolean isXAxisSpecified(){
                               return localXAxisTracker;
                           }

                           

                           /**
                           * Auto generated getter method
                           * @return com.bosch.caltool.apic.ws.CalDataAxis[]
                           */
                           public  com.bosch.caltool.apic.ws.CalDataAxis[] getXAxis(){
                               return localXAxis;
                           }

                           
                        


                               
                              /**
                               * validate the array for XAxis
                               */
                              protected void validateXAxis(com.bosch.caltool.apic.ws.CalDataAxis[] param){
                             
                              }


                             /**
                              * Auto generated setter method
                              * @param param XAxis
                              */
                              public void setXAxis(com.bosch.caltool.apic.ws.CalDataAxis[] param){
                              
                                   validateXAxis(param);

                               localXAxisTracker = param != null;
                                      
                                      this.localXAxis=param;
                              }

                               
                             
                             /**
                             * Auto generated add method for the array for convenience
                             * @param param com.bosch.caltool.apic.ws.CalDataAxis
                             */
                             public void addXAxis(com.bosch.caltool.apic.ws.CalDataAxis param){
                                   if (localXAxis == null){
                                   localXAxis = new com.bosch.caltool.apic.ws.CalDataAxis[]{};
                                   }

                            
                                 //update the setting tracker
                                localXAxisTracker = true;
                            

                               java.util.List list =
                            org.apache.axis2.databinding.utils.ConverterUtil.toList(localXAxis);
                               list.add(param);
                               this.localXAxis =
                             (com.bosch.caltool.apic.ws.CalDataAxis[])list.toArray(
                            new com.bosch.caltool.apic.ws.CalDataAxis[list.size()]);

                             }
                             

                        /**
                        * field for YAxis
                        * This was an Array!
                        */

                        
                                    protected com.bosch.caltool.apic.ws.CalDataAxis[] localYAxis ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localYAxisTracker = false ;

                           public boolean isYAxisSpecified(){
                               return localYAxisTracker;
                           }

                           

                           /**
                           * Auto generated getter method
                           * @return com.bosch.caltool.apic.ws.CalDataAxis[]
                           */
                           public  com.bosch.caltool.apic.ws.CalDataAxis[] getYAxis(){
                               return localYAxis;
                           }

                           
                        


                               
                              /**
                               * validate the array for YAxis
                               */
                              protected void validateYAxis(com.bosch.caltool.apic.ws.CalDataAxis[] param){
                             
                              }


                             /**
                              * Auto generated setter method
                              * @param param YAxis
                              */
                              public void setYAxis(com.bosch.caltool.apic.ws.CalDataAxis[] param){
                              
                                   validateYAxis(param);

                               localYAxisTracker = param != null;
                                      
                                      this.localYAxis=param;
                              }

                               
                             
                             /**
                             * Auto generated add method for the array for convenience
                             * @param param com.bosch.caltool.apic.ws.CalDataAxis
                             */
                             public void addYAxis(com.bosch.caltool.apic.ws.CalDataAxis param){
                                   if (localYAxis == null){
                                   localYAxis = new com.bosch.caltool.apic.ws.CalDataAxis[]{};
                                   }

                            
                                 //update the setting tracker
                                localYAxisTracker = true;
                            

                               java.util.List list =
                            org.apache.axis2.databinding.utils.ConverterUtil.toList(localYAxis);
                               list.add(param);
                               this.localYAxis =
                             (com.bosch.caltool.apic.ws.CalDataAxis[])list.toArray(
                            new com.bosch.caltool.apic.ws.CalDataAxis[list.size()]);

                             }
                             

                        /**
                        * field for SimpleDisplayValue
                        */

                        
                                    protected java.lang.String localSimpleDisplayValue ;
                                

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getSimpleDisplayValue(){
                               return localSimpleDisplayValue;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param SimpleDisplayValue
                               */
                               public void setSimpleDisplayValue(java.lang.String param){
                            
                                            this.localSimpleDisplayValue=param;
                                    

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
                           namespacePrefix+":CalDataPhy",
                           xmlWriter);
                   } else {
                       writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","type",
                           "CalDataPhy",
                           xmlWriter);
                   }

               
                   }
               
                                    namespace = "";
                                    writeStartElement(null, namespace, "name", xmlWriter);
                             

                                          if (localName==null){
                                              // write the nil attribute
                                              
                                                     throw new org.apache.axis2.databinding.ADBException("name cannot be null!!");
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(localName);
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                             
                                    namespace = "";
                                    writeStartElement(null, namespace, "type", xmlWriter);
                             

                                          if (localType==null){
                                              // write the nil attribute
                                              
                                                     throw new org.apache.axis2.databinding.ADBException("type cannot be null!!");
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(localType);
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                              if (localUnitTracker){
                                    namespace = "";
                                    writeStartElement(null, namespace, "unit", xmlWriter);
                             

                                          if (localUnit==null){
                                              // write the nil attribute
                                              
                                                     throw new org.apache.axis2.databinding.ADBException("unit cannot be null!!");
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(localUnit);
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                             }
                                    namespace = "";
                                    writeStartElement(null, namespace, "isText", xmlWriter);
                             

                                          if (localIsText==null){
                                              // write the nil attribute
                                              
                                                     throw new org.apache.axis2.databinding.ADBException("isText cannot be null!!");
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(localIsText);
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                              if (localAtomicValuePhyTracker){
                                       if (localAtomicValuePhy!=null){
                                            for (int i = 0;i < localAtomicValuePhy.length;i++){
                                                if (localAtomicValuePhy[i] != null){
                                                 localAtomicValuePhy[i].serialize(new javax.xml.namespace.QName("","atomicValuePhy"),
                                                           xmlWriter);
                                                } else {
                                                   
                                                        // we don't have to do any thing since minOccures is zero
                                                    
                                                }

                                            }
                                     } else {
                                        
                                               throw new org.apache.axis2.databinding.ADBException("atomicValuePhy cannot be null!!");
                                        
                                    }
                                 } if (localAtomicValuePhy2NdDimTracker){
                                       if (localAtomicValuePhy2NdDim!=null){
                                            for (int i = 0;i < localAtomicValuePhy2NdDim.length;i++){
                                                if (localAtomicValuePhy2NdDim[i] != null){
                                                 localAtomicValuePhy2NdDim[i].serialize(new javax.xml.namespace.QName("","atomicValuePhy2ndDim"),
                                                           xmlWriter);
                                                } else {
                                                   
                                                        // we don't have to do any thing since minOccures is zero
                                                    
                                                }

                                            }
                                     } else {
                                        
                                               throw new org.apache.axis2.databinding.ADBException("atomicValuePhy2ndDim cannot be null!!");
                                        
                                    }
                                 } if (localAtomicValuePhy3RdDimTracker){
                                       if (localAtomicValuePhy3RdDim!=null){
                                            for (int i = 0;i < localAtomicValuePhy3RdDim.length;i++){
                                                if (localAtomicValuePhy3RdDim[i] != null){
                                                 localAtomicValuePhy3RdDim[i].serialize(new javax.xml.namespace.QName("","atomicValuePhy3rdDim"),
                                                           xmlWriter);
                                                } else {
                                                   
                                                        // we don't have to do any thing since minOccures is zero
                                                    
                                                }

                                            }
                                     } else {
                                        
                                               throw new org.apache.axis2.databinding.ADBException("atomicValuePhy3rdDim cannot be null!!");
                                        
                                    }
                                 } if (localXAxisTracker){
                                       if (localXAxis!=null){
                                            for (int i = 0;i < localXAxis.length;i++){
                                                if (localXAxis[i] != null){
                                                 localXAxis[i].serialize(new javax.xml.namespace.QName("","xAxis"),
                                                           xmlWriter);
                                                } else {
                                                   
                                                        // we don't have to do any thing since minOccures is zero
                                                    
                                                }

                                            }
                                     } else {
                                        
                                               throw new org.apache.axis2.databinding.ADBException("xAxis cannot be null!!");
                                        
                                    }
                                 } if (localYAxisTracker){
                                       if (localYAxis!=null){
                                            for (int i = 0;i < localYAxis.length;i++){
                                                if (localYAxis[i] != null){
                                                 localYAxis[i].serialize(new javax.xml.namespace.QName("","yAxis"),
                                                           xmlWriter);
                                                } else {
                                                   
                                                        // we don't have to do any thing since minOccures is zero
                                                    
                                                }

                                            }
                                     } else {
                                        
                                               throw new org.apache.axis2.databinding.ADBException("yAxis cannot be null!!");
                                        
                                    }
                                 }
                                    namespace = "";
                                    writeStartElement(null, namespace, "simpleDisplayValue", xmlWriter);
                             

                                          if (localSimpleDisplayValue==null){
                                              // write the nil attribute
                                              
                                                     throw new org.apache.axis2.databinding.ADBException("simpleDisplayValue cannot be null!!");
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(localSimpleDisplayValue);
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                             
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
                                                                      "name"));
                                 
                                        if (localName != null){
                                            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localName));
                                        } else {
                                           throw new org.apache.axis2.databinding.ADBException("name cannot be null!!");
                                        }
                                    
                                      elementList.add(new javax.xml.namespace.QName("",
                                                                      "type"));
                                 
                                        if (localType != null){
                                            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localType));
                                        } else {
                                           throw new org.apache.axis2.databinding.ADBException("type cannot be null!!");
                                        }
                                     if (localUnitTracker){
                                      elementList.add(new javax.xml.namespace.QName("",
                                                                      "unit"));
                                 
                                        if (localUnit != null){
                                            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localUnit));
                                        } else {
                                           throw new org.apache.axis2.databinding.ADBException("unit cannot be null!!");
                                        }
                                    }
                                      elementList.add(new javax.xml.namespace.QName("",
                                                                      "isText"));
                                 
                                        if (localIsText != null){
                                            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localIsText));
                                        } else {
                                           throw new org.apache.axis2.databinding.ADBException("isText cannot be null!!");
                                        }
                                     if (localAtomicValuePhyTracker){
                             if (localAtomicValuePhy!=null) {
                                 for (int i = 0;i < localAtomicValuePhy.length;i++){

                                    if (localAtomicValuePhy[i] != null){
                                         elementList.add(new javax.xml.namespace.QName("",
                                                                          "atomicValuePhy"));
                                         elementList.add(localAtomicValuePhy[i]);
                                    } else {
                                        
                                                // nothing to do
                                            
                                    }

                                 }
                             } else {
                                 
                                        throw new org.apache.axis2.databinding.ADBException("atomicValuePhy cannot be null!!");
                                    
                             }

                        } if (localAtomicValuePhy2NdDimTracker){
                             if (localAtomicValuePhy2NdDim!=null) {
                                 for (int i = 0;i < localAtomicValuePhy2NdDim.length;i++){

                                    if (localAtomicValuePhy2NdDim[i] != null){
                                         elementList.add(new javax.xml.namespace.QName("",
                                                                          "atomicValuePhy2ndDim"));
                                         elementList.add(localAtomicValuePhy2NdDim[i]);
                                    } else {
                                        
                                                // nothing to do
                                            
                                    }

                                 }
                             } else {
                                 
                                        throw new org.apache.axis2.databinding.ADBException("atomicValuePhy2ndDim cannot be null!!");
                                    
                             }

                        } if (localAtomicValuePhy3RdDimTracker){
                             if (localAtomicValuePhy3RdDim!=null) {
                                 for (int i = 0;i < localAtomicValuePhy3RdDim.length;i++){

                                    if (localAtomicValuePhy3RdDim[i] != null){
                                         elementList.add(new javax.xml.namespace.QName("",
                                                                          "atomicValuePhy3rdDim"));
                                         elementList.add(localAtomicValuePhy3RdDim[i]);
                                    } else {
                                        
                                                // nothing to do
                                            
                                    }

                                 }
                             } else {
                                 
                                        throw new org.apache.axis2.databinding.ADBException("atomicValuePhy3rdDim cannot be null!!");
                                    
                             }

                        } if (localXAxisTracker){
                             if (localXAxis!=null) {
                                 for (int i = 0;i < localXAxis.length;i++){

                                    if (localXAxis[i] != null){
                                         elementList.add(new javax.xml.namespace.QName("",
                                                                          "xAxis"));
                                         elementList.add(localXAxis[i]);
                                    } else {
                                        
                                                // nothing to do
                                            
                                    }

                                 }
                             } else {
                                 
                                        throw new org.apache.axis2.databinding.ADBException("xAxis cannot be null!!");
                                    
                             }

                        } if (localYAxisTracker){
                             if (localYAxis!=null) {
                                 for (int i = 0;i < localYAxis.length;i++){

                                    if (localYAxis[i] != null){
                                         elementList.add(new javax.xml.namespace.QName("",
                                                                          "yAxis"));
                                         elementList.add(localYAxis[i]);
                                    } else {
                                        
                                                // nothing to do
                                            
                                    }

                                 }
                             } else {
                                 
                                        throw new org.apache.axis2.databinding.ADBException("yAxis cannot be null!!");
                                    
                             }

                        }
                                      elementList.add(new javax.xml.namespace.QName("",
                                                                      "simpleDisplayValue"));
                                 
                                        if (localSimpleDisplayValue != null){
                                            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localSimpleDisplayValue));
                                        } else {
                                           throw new org.apache.axis2.databinding.ADBException("simpleDisplayValue cannot be null!!");
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
        public static CalDataPhy parse(javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception{
            CalDataPhy object =
                new CalDataPhy();

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
                    
                            if (!"CalDataPhy".equals(type)){
                                //find namespace for the prefix
                                java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                                return (CalDataPhy)com.bosch.caltool.apic.ws.ExtensionMapper.getTypeObject(
                                     nsUri,type,reader);
                              }
                        

                  }
                

                }

                

                
                // Note all attributes that were handled. Used to differ normal attributes
                // from anyAttributes.
                java.util.Vector handledAttributes = new java.util.Vector();
                

                
                    
                    reader.next();
                
                        java.util.ArrayList list5 = new java.util.ArrayList();
                    
                        java.util.ArrayList list6 = new java.util.ArrayList();
                    
                        java.util.ArrayList list7 = new java.util.ArrayList();
                    
                        java.util.ArrayList list8 = new java.util.ArrayList();
                    
                        java.util.ArrayList list9 = new java.util.ArrayList();
                    
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("","name").equals(reader.getName())){
                                
                                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                    if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                        throw new org.apache.axis2.databinding.ADBException("The element: "+"name" +"  cannot be null");
                                    }
                                    

                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setName(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                else{
                                    // A start element we are not expecting indicates an invalid parameter was passed
                                    throw new org.apache.axis2.databinding.ADBException("Unexpected subelement " + reader.getName());
                                }
                            
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("","type").equals(reader.getName())){
                                
                                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                    if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                        throw new org.apache.axis2.databinding.ADBException("The element: "+"type" +"  cannot be null");
                                    }
                                    

                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setType(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                else{
                                    // A start element we are not expecting indicates an invalid parameter was passed
                                    throw new org.apache.axis2.databinding.ADBException("Unexpected subelement " + reader.getName());
                                }
                            
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("","unit").equals(reader.getName())){
                                
                                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                    if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                        throw new org.apache.axis2.databinding.ADBException("The element: "+"unit" +"  cannot be null");
                                    }
                                    

                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setUnit(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("","isText").equals(reader.getName())){
                                
                                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                    if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                        throw new org.apache.axis2.databinding.ADBException("The element: "+"isText" +"  cannot be null");
                                    }
                                    

                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setIsText(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                else{
                                    // A start element we are not expecting indicates an invalid parameter was passed
                                    throw new org.apache.axis2.databinding.ADBException("Unexpected subelement " + reader.getName());
                                }
                            
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("","atomicValuePhy").equals(reader.getName())){
                                
                                    
                                    
                                    // Process the array and step past its final element's end.
                                    list5.add(com.bosch.caltool.apic.ws.AtomicValuePhy.Factory.parse(reader));
                                                                
                                                        //loop until we find a start element that is not part of this array
                                                        boolean loopDone5 = false;
                                                        while(!loopDone5){
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
                                                                loopDone5 = true;
                                                            } else {
                                                                if (new javax.xml.namespace.QName("","atomicValuePhy").equals(reader.getName())){
                                                                    list5.add(com.bosch.caltool.apic.ws.AtomicValuePhy.Factory.parse(reader));
                                                                        
                                                                }else{
                                                                    loopDone5 = true;
                                                                }
                                                            }
                                                        }
                                                        // call the converter utility  to convert and set the array
                                                        
                                                        object.setAtomicValuePhy((com.bosch.caltool.apic.ws.AtomicValuePhy[])
                                                            org.apache.axis2.databinding.utils.ConverterUtil.convertToArray(
                                                                com.bosch.caltool.apic.ws.AtomicValuePhy.class,
                                                                list5));
                                                            
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("","atomicValuePhy2ndDim").equals(reader.getName())){
                                
                                    
                                    
                                    // Process the array and step past its final element's end.
                                    list6.add(com.bosch.caltool.apic.ws.AtomicValuePhy.Factory.parse(reader));
                                                                
                                                        //loop until we find a start element that is not part of this array
                                                        boolean loopDone6 = false;
                                                        while(!loopDone6){
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
                                                                loopDone6 = true;
                                                            } else {
                                                                if (new javax.xml.namespace.QName("","atomicValuePhy2ndDim").equals(reader.getName())){
                                                                    list6.add(com.bosch.caltool.apic.ws.AtomicValuePhy.Factory.parse(reader));
                                                                        
                                                                }else{
                                                                    loopDone6 = true;
                                                                }
                                                            }
                                                        }
                                                        // call the converter utility  to convert and set the array
                                                        
                                                        object.setAtomicValuePhy2NdDim((com.bosch.caltool.apic.ws.AtomicValuePhy[])
                                                            org.apache.axis2.databinding.utils.ConverterUtil.convertToArray(
                                                                com.bosch.caltool.apic.ws.AtomicValuePhy.class,
                                                                list6));
                                                            
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("","atomicValuePhy3rdDim").equals(reader.getName())){
                                
                                    
                                    
                                    // Process the array and step past its final element's end.
                                    list7.add(com.bosch.caltool.apic.ws.AtomicValuePhy.Factory.parse(reader));
                                                                
                                                        //loop until we find a start element that is not part of this array
                                                        boolean loopDone7 = false;
                                                        while(!loopDone7){
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
                                                                loopDone7 = true;
                                                            } else {
                                                                if (new javax.xml.namespace.QName("","atomicValuePhy3rdDim").equals(reader.getName())){
                                                                    list7.add(com.bosch.caltool.apic.ws.AtomicValuePhy.Factory.parse(reader));
                                                                        
                                                                }else{
                                                                    loopDone7 = true;
                                                                }
                                                            }
                                                        }
                                                        // call the converter utility  to convert and set the array
                                                        
                                                        object.setAtomicValuePhy3RdDim((com.bosch.caltool.apic.ws.AtomicValuePhy[])
                                                            org.apache.axis2.databinding.utils.ConverterUtil.convertToArray(
                                                                com.bosch.caltool.apic.ws.AtomicValuePhy.class,
                                                                list7));
                                                            
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("","xAxis").equals(reader.getName())){
                                
                                    
                                    
                                    // Process the array and step past its final element's end.
                                    list8.add(com.bosch.caltool.apic.ws.CalDataAxis.Factory.parse(reader));
                                                                
                                                        //loop until we find a start element that is not part of this array
                                                        boolean loopDone8 = false;
                                                        while(!loopDone8){
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
                                                                loopDone8 = true;
                                                            } else {
                                                                if (new javax.xml.namespace.QName("","xAxis").equals(reader.getName())){
                                                                    list8.add(com.bosch.caltool.apic.ws.CalDataAxis.Factory.parse(reader));
                                                                        
                                                                }else{
                                                                    loopDone8 = true;
                                                                }
                                                            }
                                                        }
                                                        // call the converter utility  to convert and set the array
                                                        
                                                        object.setXAxis((com.bosch.caltool.apic.ws.CalDataAxis[])
                                                            org.apache.axis2.databinding.utils.ConverterUtil.convertToArray(
                                                                com.bosch.caltool.apic.ws.CalDataAxis.class,
                                                                list8));
                                                            
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("","yAxis").equals(reader.getName())){
                                
                                    
                                    
                                    // Process the array and step past its final element's end.
                                    list9.add(com.bosch.caltool.apic.ws.CalDataAxis.Factory.parse(reader));
                                                                
                                                        //loop until we find a start element that is not part of this array
                                                        boolean loopDone9 = false;
                                                        while(!loopDone9){
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
                                                                loopDone9 = true;
                                                            } else {
                                                                if (new javax.xml.namespace.QName("","yAxis").equals(reader.getName())){
                                                                    list9.add(com.bosch.caltool.apic.ws.CalDataAxis.Factory.parse(reader));
                                                                        
                                                                }else{
                                                                    loopDone9 = true;
                                                                }
                                                            }
                                                        }
                                                        // call the converter utility  to convert and set the array
                                                        
                                                        object.setYAxis((com.bosch.caltool.apic.ws.CalDataAxis[])
                                                            org.apache.axis2.databinding.utils.ConverterUtil.convertToArray(
                                                                com.bosch.caltool.apic.ws.CalDataAxis.class,
                                                                list9));
                                                            
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("","simpleDisplayValue").equals(reader.getName())){
                                
                                    nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                    if ("true".equals(nillableValue) || "1".equals(nillableValue)){
                                        throw new org.apache.axis2.databinding.ADBException("The element: "+"simpleDisplayValue" +"  cannot be null");
                                    }
                                    

                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setSimpleDisplayValue(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                else{
                                    // A start element we are not expecting indicates an invalid parameter was passed
                                    throw new org.apache.axis2.databinding.ADBException("Unexpected subelement " + reader.getName());
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
           
    