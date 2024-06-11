
/**
 * ProjectIdCardWithVersion.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.2  Built on : Apr 17, 2012 (05:34:40 IST)
 */

            
                package com.bosch.caltool.apic.ws;
            

            /**
            *  ProjectIdCardWithVersion bean class
            */
            @SuppressWarnings({"unchecked","unused"})
        
        public  class ProjectIdCardWithVersion
        implements org.apache.axis2.databinding.ADBBean{
        /* This type was generated from the piece of schema that had
                name = ProjectIdCardWithVersion
                Namespace URI = http://ws.apic.caltool.bosch.com
                Namespace Prefix = ns1
                */
            

                        /**
                        * field for ProjectIdCardDetails
                        */

                        
                                    protected com.bosch.caltool.apic.ws.ProjectIdCardVersInfoType localProjectIdCardDetails ;
                                

                           /**
                           * Auto generated getter method
                           * @return com.bosch.caltool.apic.ws.ProjectIdCardVersInfoType
                           */
                           public  com.bosch.caltool.apic.ws.ProjectIdCardVersInfoType getProjectIdCardDetails(){
                               return localProjectIdCardDetails;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param ProjectIdCardDetails
                               */
                               public void setProjectIdCardDetails(com.bosch.caltool.apic.ws.ProjectIdCardVersInfoType param){
                            
                                            this.localProjectIdCardDetails=param;
                                    

                               }
                            

                        /**
                        * field for Attributes
                        * This was an Array!
                        */

                        
                                    protected com.bosch.caltool.apic.ws.AttributeWithValueType[] localAttributes ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localAttributesTracker = false ;

                           public boolean isAttributesSpecified(){
                               return localAttributesTracker;
                           }

                           

                           /**
                           * Auto generated getter method
                           * @return com.bosch.caltool.apic.ws.AttributeWithValueType[]
                           */
                           public  com.bosch.caltool.apic.ws.AttributeWithValueType[] getAttributes(){
                               return localAttributes;
                           }

                           
                        


                               
                              /**
                               * validate the array for Attributes
                               */
                              protected void validateAttributes(com.bosch.caltool.apic.ws.AttributeWithValueType[] param){
                             
                              }


                             /**
                              * Auto generated setter method
                              * @param param Attributes
                              */
                              public void setAttributes(com.bosch.caltool.apic.ws.AttributeWithValueType[] param){
                              
                                   validateAttributes(param);

                               localAttributesTracker = param != null;
                                      
                                      this.localAttributes=param;
                              }

                               
                             
                             /**
                             * Auto generated add method for the array for convenience
                             * @param param com.bosch.caltool.apic.ws.AttributeWithValueType
                             */
                             public void addAttributes(com.bosch.caltool.apic.ws.AttributeWithValueType param){
                                   if (localAttributes == null){
                                   localAttributes = new com.bosch.caltool.apic.ws.AttributeWithValueType[]{};
                                   }

                            
                                 //update the setting tracker
                                localAttributesTracker = true;
                            

                               java.util.List list =
                            org.apache.axis2.databinding.utils.ConverterUtil.toList(localAttributes);
                               list.add(param);
                               this.localAttributes =
                             (com.bosch.caltool.apic.ws.AttributeWithValueType[])list.toArray(
                            new com.bosch.caltool.apic.ws.AttributeWithValueType[list.size()]);

                             }
                             

                        /**
                        * field for Variants
                        * This was an Array!
                        */

                        
                                    protected com.bosch.caltool.apic.ws.ProjectIdCardVariantType[] localVariants ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localVariantsTracker = false ;

                           public boolean isVariantsSpecified(){
                               return localVariantsTracker;
                           }

                           

                           /**
                           * Auto generated getter method
                           * @return com.bosch.caltool.apic.ws.ProjectIdCardVariantType[]
                           */
                           public  com.bosch.caltool.apic.ws.ProjectIdCardVariantType[] getVariants(){
                               return localVariants;
                           }

                           
                        


                               
                              /**
                               * validate the array for Variants
                               */
                              protected void validateVariants(com.bosch.caltool.apic.ws.ProjectIdCardVariantType[] param){
                             
                              }


                             /**
                              * Auto generated setter method
                              * @param param Variants
                              */
                              public void setVariants(com.bosch.caltool.apic.ws.ProjectIdCardVariantType[] param){
                              
                                   validateVariants(param);

                               localVariantsTracker = param != null;
                                      
                                      this.localVariants=param;
                              }

                               
                             
                             /**
                             * Auto generated add method for the array for convenience
                             * @param param com.bosch.caltool.apic.ws.ProjectIdCardVariantType
                             */
                             public void addVariants(com.bosch.caltool.apic.ws.ProjectIdCardVariantType param){
                                   if (localVariants == null){
                                   localVariants = new com.bosch.caltool.apic.ws.ProjectIdCardVariantType[]{};
                                   }

                            
                                 //update the setting tracker
                                localVariantsTracker = true;
                            

                               java.util.List list =
                            org.apache.axis2.databinding.utils.ConverterUtil.toList(localVariants);
                               list.add(param);
                               this.localVariants =
                             (com.bosch.caltool.apic.ws.ProjectIdCardVariantType[])list.toArray(
                            new com.bosch.caltool.apic.ws.ProjectIdCardVariantType[list.size()]);

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
                           namespacePrefix+":ProjectIdCardWithVersion",
                           xmlWriter);
                   } else {
                       writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","type",
                           "ProjectIdCardWithVersion",
                           xmlWriter);
                   }

               
                   }
               
                                            if (localProjectIdCardDetails==null){
                                                 throw new org.apache.axis2.databinding.ADBException("projectIdCardDetails cannot be null!!");
                                            }
                                           localProjectIdCardDetails.serialize(new javax.xml.namespace.QName("","projectIdCardDetails"),
                                               xmlWriter);
                                         if (localAttributesTracker){
                                       if (localAttributes!=null){
                                            for (int i = 0;i < localAttributes.length;i++){
                                                if (localAttributes[i] != null){
                                                 localAttributes[i].serialize(new javax.xml.namespace.QName("","attributes"),
                                                           xmlWriter);
                                                } else {
                                                   
                                                        // we don't have to do any thing since minOccures is zero
                                                    
                                                }

                                            }
                                     } else {
                                        
                                               throw new org.apache.axis2.databinding.ADBException("attributes cannot be null!!");
                                        
                                    }
                                 } if (localVariantsTracker){
                                       if (localVariants!=null){
                                            for (int i = 0;i < localVariants.length;i++){
                                                if (localVariants[i] != null){
                                                 localVariants[i].serialize(new javax.xml.namespace.QName("","variants"),
                                                           xmlWriter);
                                                } else {
                                                   
                                                        // we don't have to do any thing since minOccures is zero
                                                    
                                                }

                                            }
                                     } else {
                                        
                                               throw new org.apache.axis2.databinding.ADBException("variants cannot be null!!");
                                        
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
                                                                      "projectIdCardDetails"));
                            
                            
                                    if (localProjectIdCardDetails==null){
                                         throw new org.apache.axis2.databinding.ADBException("projectIdCardDetails cannot be null!!");
                                    }
                                    elementList.add(localProjectIdCardDetails);
                                 if (localAttributesTracker){
                             if (localAttributes!=null) {
                                 for (int i = 0;i < localAttributes.length;i++){

                                    if (localAttributes[i] != null){
                                         elementList.add(new javax.xml.namespace.QName("",
                                                                          "attributes"));
                                         elementList.add(localAttributes[i]);
                                    } else {
                                        
                                                // nothing to do
                                            
                                    }

                                 }
                             } else {
                                 
                                        throw new org.apache.axis2.databinding.ADBException("attributes cannot be null!!");
                                    
                             }

                        } if (localVariantsTracker){
                             if (localVariants!=null) {
                                 for (int i = 0;i < localVariants.length;i++){

                                    if (localVariants[i] != null){
                                         elementList.add(new javax.xml.namespace.QName("",
                                                                          "variants"));
                                         elementList.add(localVariants[i]);
                                    } else {
                                        
                                                // nothing to do
                                            
                                    }

                                 }
                             } else {
                                 
                                        throw new org.apache.axis2.databinding.ADBException("variants cannot be null!!");
                                    
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
        public static ProjectIdCardWithVersion parse(javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception{
            ProjectIdCardWithVersion object =
                new ProjectIdCardWithVersion();

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
                    
                            if (!"ProjectIdCardWithVersion".equals(type)){
                                //find namespace for the prefix
                                java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                                return (ProjectIdCardWithVersion)com.bosch.caltool.apic.ws.ExtensionMapper.getTypeObject(
                                     nsUri,type,reader);
                              }
                        

                  }
                

                }

                

                
                // Note all attributes that were handled. Used to differ normal attributes
                // from anyAttributes.
                java.util.Vector handledAttributes = new java.util.Vector();
                

                
                    
                    reader.next();
                
                        java.util.ArrayList list2 = new java.util.ArrayList();
                    
                        java.util.ArrayList list3 = new java.util.ArrayList();
                    
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("","projectIdCardDetails").equals(reader.getName())){
                                
                                                object.setProjectIdCardDetails(com.bosch.caltool.apic.ws.ProjectIdCardVersInfoType.Factory.parse(reader));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                else{
                                    // A start element we are not expecting indicates an invalid parameter was passed
                                    throw new org.apache.axis2.databinding.ADBException("Unexpected subelement " + reader.getName());
                                }
                            
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("","attributes").equals(reader.getName())){
                                
                                    
                                    
                                    // Process the array and step past its final element's end.
                                    list2.add(com.bosch.caltool.apic.ws.AttributeWithValueType.Factory.parse(reader));
                                                                
                                                        //loop until we find a start element that is not part of this array
                                                        boolean loopDone2 = false;
                                                        while(!loopDone2){
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
                                                                loopDone2 = true;
                                                            } else {
                                                                if (new javax.xml.namespace.QName("","attributes").equals(reader.getName())){
                                                                    list2.add(com.bosch.caltool.apic.ws.AttributeWithValueType.Factory.parse(reader));
                                                                        
                                                                }else{
                                                                    loopDone2 = true;
                                                                }
                                                            }
                                                        }
                                                        // call the converter utility  to convert and set the array
                                                        
                                                        object.setAttributes((com.bosch.caltool.apic.ws.AttributeWithValueType[])
                                                            org.apache.axis2.databinding.utils.ConverterUtil.convertToArray(
                                                                com.bosch.caltool.apic.ws.AttributeWithValueType.class,
                                                                list2));
                                                            
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("","variants").equals(reader.getName())){
                                
                                    
                                    
                                    // Process the array and step past its final element's end.
                                    list3.add(com.bosch.caltool.apic.ws.ProjectIdCardVariantType.Factory.parse(reader));
                                                                
                                                        //loop until we find a start element that is not part of this array
                                                        boolean loopDone3 = false;
                                                        while(!loopDone3){
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
                                                                loopDone3 = true;
                                                            } else {
                                                                if (new javax.xml.namespace.QName("","variants").equals(reader.getName())){
                                                                    list3.add(com.bosch.caltool.apic.ws.ProjectIdCardVariantType.Factory.parse(reader));
                                                                        
                                                                }else{
                                                                    loopDone3 = true;
                                                                }
                                                            }
                                                        }
                                                        // call the converter utility  to convert and set the array
                                                        
                                                        object.setVariants((com.bosch.caltool.apic.ws.ProjectIdCardVariantType[])
                                                            org.apache.axis2.databinding.utils.ConverterUtil.convertToArray(
                                                                com.bosch.caltool.apic.ws.ProjectIdCardVariantType.class,
                                                                list3));
                                                            
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
           
    