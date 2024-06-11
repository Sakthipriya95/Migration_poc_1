/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.applicationlog.request;


/**
 * Represents an abstract textual output of the request of a users session. The request, which normally conatins
 * attributes with which the servers is told what the client wants to see is converted into a String. This String can be
 * used for logging purposes.<br>
 * Use in general class RequestFactory to create an instance of the objects you want to convert to an String <br>
 * <b>Usage notes</b>:
 * <ul>
 * <li>Create a new subclass of AbstractRequest</li>
 * <li>In the subclass pass the request object in the constructor. This is stored as type object.</li>
 * <li>Implement method getSingleRequestString. This method should return the String representation of a request object.
 * The former stored 'Object' object can be typecast without check to the original class.</li>
 * <li>This method can easily be implemented by going to the class of the request object (which is generated by Axis)
 * and create the toString method with help of Eclipse (Right Mouse -> SOurce -> Create toString()). The toString()
 * method call is not implemented in the Axis generated code.</li>
 * </ul>
 * 
 * @author imi2si
 * @since 1.19
 */
public abstract class AbstractRequest {

  /**
   * The StringBuilder which creates the String representation of the Request object
   */
  protected final transient StringBuilder requestBuilder = new StringBuilder();

  /**
   * The Webservice Request object. Because there's no common super class in the webservice, the request is always
   * stored as type object.
   */
  protected final transient Object[] request;

  /**
   * Default constructor which stores the request object of the webservice
   * 
   * @param request The web service Request object
   */
  public AbstractRequest(final Object... request) {
    this.request = request;
    createRequestString();
  }

  /**
   * Iterates through all the request objects and appends the String representation of each entry to the STring which is
   * returned by the <code>toString()</code> method.
   */
  private void createRequestString() {
    if (this.request == null) {
      return;
    }

    for (Object entry : this.request) {
      this.requestBuilder.append(getSingleRequestString(entry));
    }
  }

  /**
   * Takes an single Request Objects and returns the String-Representation of it. All attributes which make the object
   * identifiable should be included in the returning String.
   * 
   * @param requestObject the web service request object
   * @return the String representation of the Webservice Request Object
   */
  protected abstract String getSingleRequestString(final Object requestObject);

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return this.requestBuilder.toString();
  }
}
