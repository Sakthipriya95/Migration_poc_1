/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.calcomp.tulservice.internal.model;


/**
 * ENUM that contains the list of possible tool events
 *
 * @author SSN9COB
 */
public enum ToolEvents {

                        /**
                         *
                         */
                        INSTALLED("installed"),

                        /**
                         *
                         */
                        UNINSTALLED("uninstalled"),

                        /**
                         *
                         */
                        UPDATED("updated"),

                        /**
                         *
                         */
                        LICENSE_ASSIGNED("license assigned"),

                        /**
                         *
                         */
                        LICENSE_REVOKED("license revoked"),

                        /**
                         *
                         */
                        OPENED("opened"),

                        /**
                         *
                         */
                        STARTED("started"),

                        /**
                         *
                         */
                        PROCESSING("processing"),

                        /**
                         *
                         */
                        CLOSED("closed"),

                        /**
                         *
                         */
                        PAUSED("paused"),

                        /**
                         *
                         */
                        CONTINUED("continued"),

                        /**
                         *
                         */
                        CLICKED("clicked"),

                        /**
                         *
                         */
                        COMPILED_WITH_ERRORS("compiled with errors"),

                        /**
                         *
                         */
                        COMPILED_WITHOUT_ERRORS("compiled without errors"),

                        /**
                         *
                         */
                        USER_ABORTED("user-aborted"),

                        /**
                         *
                         */
                        DOWN_UNAVAILABLE("down/unavailable"),

                        /**
                         *
                         */
                        AVAILABLE("available"),

                        /**
                         *
                         */
                        WORKFLOW_NEW("workflow: new"),

                        /**
                         *
                         */
                        WORKFLOW_ASSIGNED("workflow: assigned"),

                        /**
                         *
                         */
                        WORKFLOW_INPROGRESS("workflow: in progress"),

                        /**
                         *
                         */
                        WORKFLOW_CANCELLED("workflow: cancelled"),

                        /**
                         *
                         */
                        WORKFLOW_CLOSED("workflow: closed"),

                        /**
                         *
                         */
                        WORKFLOW_REWORK("workflow: rework");

  private final String eventType;


  /**
   * Constructor
   */
  private ToolEvents(final String type) {
    this.eventType = type;
  }

  /**
   * @return String - For Event as required by TUL Server
   */
  public String getToolEvent() {
    return this.eventType;
  }
}
