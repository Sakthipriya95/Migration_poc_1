/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client;

import java.util.regex.Pattern;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author BNE4COB
 */
public final class Matchers {

  /**
   * Private constructor to avoid instantiation
   */
  private Matchers() {
    // Private constructor
  }

  /**
   * Creates a matcher using java reg ex
   *
   * @param regex regular expresion
   * @return matcher
   */
  public static Matcher<String> matchesPattern(final String regex) {
    return new BaseMatcher<String>() {

      /**
       * Regex Pattern to match
       */
      protected String pattern = regex;

      @Override
      public boolean matches(final Object item) {
        // type check added, as the input is object
        return (item instanceof String) && Pattern.matches(this.pattern, (String) item);
      }

      @Override
      public void describeTo(final Description description) {
        description.appendText(this.pattern);
      }

    };
  }

  /**
   * @param errorCode errorCode of ApicWebServiceException
   * @return
   */
  public static TypeSafeMatcher<ApicWebServiceException> matchesErrorCode(final String errorCode) {
    return new TypeSafeMatcher<ApicWebServiceException>() {

      private final String expectedCode = errorCode;

      /**
       * {@inheritDoc}
       */
      @Override
      public void describeTo(final Description description) {
        description.appendText("exception with error code ").appendValue(this.expectedCode);
      }

      /**
       * {@inheritDoc}
       */
      @Override
      protected boolean matchesSafely(final ApicWebServiceException item) {
        return this.expectedCode.equals(item.getErrorCode());
      }

      /**
       * @param item match-item
       * @param mismatchDescription Description displayed when mismatch occurs
       */
      @Override
      protected void describeMismatchSafely(final ApicWebServiceException item, final Description mismatchDescription) {
        mismatchDescription.appendText("error code was ").appendValue(item.getErrorCode());
      }
    };
  }

}
