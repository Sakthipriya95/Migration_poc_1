package com.bosch.caltool.nattable;

import ca.odell.glazedlists.matchers.AbstractMatcherEditorListenerSupport;
import ca.odell.glazedlists.matchers.Matcher;
import ca.odell.glazedlists.matchers.Matchers;


/**
 * @param <E> NatTable Input data type
 * @author jvi6cob
 */
public class CustomMatcherEditor<E> extends AbstractMatcherEditorListenerSupport<E> {

  /**
   * Message constant
   */
  private static final String MATCHER_NOT_DEFINED = "Matcher is null";
  /**
   * Current matcher
   */
  private Matcher<E> currMatcher;

  /**
   * Constructor
   * 
   * @param customMatcher Matcher
   */
  public CustomMatcherEditor(final Matcher<E> customMatcher) {
    super();
    this.currMatcher = customMatcher;
  }

  /** {@inheritDoc} */
  @Override
  public Matcher<E> getMatcher() {
    return this.currMatcher;
  }

  /**
   * Indicates that the filter matches all.
   */
  protected void fireMatchAll() {
    this.currMatcher = Matchers.trueMatcher();
    fireChangedMatcher(createMatchAllEvent(this.currMatcher));
  }


  /**
   * Indicates that the filter has changed to be less restrictive. This should only be called if all currently
   * unfiltered items will remain unfiltered.
   */
  /**
   * @param matcherObj Matcher<E>
   */
  protected void fireRelaxed(final Matcher<E> matcherObj) {
    if (matcherObj == null) {
      throw new IllegalArgumentException(MATCHER_NOT_DEFINED);
    }
    this.currMatcher = matcherObj;
    fireChangedMatcher(createRelaxedEvent(this.currMatcher));
  }

  /**
   * Indicates that the filter matches none.
   */
  protected void fireMatchNone() {
    this.currMatcher = Matchers.falseMatcher();
    fireChangedMatcher(createMatchNoneEvent(this.currMatcher));
  }

  /**
   * Indicates that the filter has changed in an indeterminate way.
   * 
   * @param matcher Matcher<E>
   */
  protected void fireChanged(final Matcher<E> matcher) {
    if (matcher == null) {
      throw new IllegalArgumentException(MATCHER_NOT_DEFINED);
    }
    this.currMatcher = matcher;
    fireChangedMatcher(createChangedEvent(this.currMatcher));
  }

  /**
   * Returns <tt>true</tt> if the current matcher will match everything.
   * 
   * @return boolean
   */
  protected boolean isCurrentlyMatchingAll() {
    return this.currMatcher == Matchers.trueMatcher();
  }

  /**
   * Returns <tt>true</tt> if the current matcher will match nothing.
   * 
   * @return boolean
   */
  protected boolean isCurrentlyMatchingNone() {
    return this.currMatcher == Matchers.falseMatcher();
  }

  /**
   * Indicates that the filter has changed to be more restrictive. This should only be called if all currently filtered
   * items will remain filtered.
   */
  /**
   * @param matcherObj Matcher<E>
   */
  protected void fireConstrained(final Matcher<E> matcherObj) {
    if (matcherObj == null) {
      throw new IllegalArgumentException(MATCHER_NOT_DEFINED);
    }
    this.currMatcher = matcherObj;
    fireChangedMatcher(createConstrainedEvent(this.currMatcher));
  }

}