/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.calcomp.externallink;

import com.bosch.calcomp.externallink.process.ILinkValidator;

/**
 * Link type definition
 *
 * @author bne4cob
 */
public interface ILinkType {

  /**
   * Key of the link type in the link url. For e.g. pidvid
   *
   * @return key
   */
  String getKey();

  /**
   * @return name of the
   */
  String getName();

  /**
   * Display text of the type
   *
   * @return Display text of the type
   */
  String getTypeDisplayText();

  /**
   * Additional validations during link processing
   *
   * @return validator
   */
  ILinkValidator getValidator();

  /**
   * @return object type of the link type's underlying object, that implements ILinkableObject
   */
  Class<?> getObjectType();

  /**
   * Creates a new link for the given linkable object
   * 
   * @param obj linkable object
   * @return link
   */
  <O extends ILinkableObject> ILink<O> createNewLink(O obj);


}
