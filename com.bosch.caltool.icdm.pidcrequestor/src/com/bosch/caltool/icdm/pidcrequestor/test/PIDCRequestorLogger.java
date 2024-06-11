/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.pidcrequestor.test;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.calcomp.adapter.logger.Log4JLoggerAdapterImpl;

/**
 * VaricoLogger represents a single Logger for the complete Varico application, represented as singelton
 */
public enum PIDCRequestorLogger 
{
    
    /**
     * Singleton logger object for the whole application
     */
    INSTANCE;
    
    // Default Temp Directory
    private final String DEFAULT_TEMP_DIR = "c:/temp/";
    
    // log file name
    private final String DEFAULT_LOG_FILE = "TestDcmParser_2.log";
    
    //log level
    private final int DEFAULT_LOG_LEVEL = ILoggerAdapter.LEVEL_INFO;

    private ILoggerAdapter logger;
    
    /**
     * Standard Constructor for the logger. Creates a new logger for the whole Varico application. 
     */
    private PIDCRequestorLogger()
    {
        String logFile = getUserTempDirectory() + DEFAULT_LOG_FILE;
        logger = new Log4JLoggerAdapterImpl(logFile, "%d [%-5t] [%-5p] %c:%L - %m%n");
        logger.setLogLevel(DEFAULT_LOG_LEVEL);
        logger.debug("logger initialized, log file: " + logFile);       
    }
    
    /**
     * Gets the user's temp directory from the environmental variable TEMP.
     * If the variable is not set, use the DEFAULT_TEMP_DIR
     * 
     * @return the String temp directory including trailing backslash
     */
    private String getUserTempDirectory()
    {
        String userTempDir = System.getenv("TEMP");
        
        if(userTempDir == null || userTempDir.trim().length() == 0)
        {
            userTempDir = DEFAULT_TEMP_DIR;
        }
        
        return userTempDir + "\\";
    }
    
    /**
     * Gets logger for the Varico application as singleton
     * 
     * @return the ILoggerAdapter for the Varico application
     */
    public ILoggerAdapter getLogger()
    {
        
        return this.logger;
    }
}