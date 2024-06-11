/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.rest.serviceloader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.dmframework.bo.AbstractSimpleCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.general.WsServiceCommand;
import com.bosch.caltool.icdm.bo.general.WsServiceLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.general.WsService;
import com.bosch.caltool.icdm.rest.serviceloader.common.ServiceLoaderException;
import com.bosch.caltool.icdm.rest.serviceloader.report.ServiceReport;
import com.bosch.caltool.icdm.rest.serviceloader.report.ServiceReportException;
import com.bosch.caltool.icdm.rest.wadl.parser.WadlParser;
import com.bosch.caltool.icdm.rest.wadl.parser.WadlParserException;

/**
 * @author bne4cob
 */
public class ServiceLoaderMaster {

  private final ILoggerAdapter logger;

  private final String wadlFilePath;

  private Map<String, WsService> wadlServicesMap;

  private final SortedSet<WsService> unChangedServices = new TreeSet<>();

  private final SortedSet<WsService> modifiedServices = new TreeSet<>();

  private final Map<String, WsService> oldWsServiceDataMap = new HashMap<>();

  /**
   * Service Data for uploading to the Database
   */
  private final ServiceData servData;

  /**
   * @param logger logger
   * @param wadlFilePath WADL file path
   */
  public ServiceLoaderMaster(final ILoggerAdapter logger, final String wadlFilePath) {
    this.logger = logger;
    this.wadlFilePath = wadlFilePath;
    this.servData = new ServiceData();
  }

  /**
   * run the service loader
   *
   * @throws ServiceLoaderException error while loading service
   */
  public void run() throws ServiceLoaderException {
    try {
      fetchServiceDetails();

      // Display the list of services to console.
      logServiceDetails(this.wadlServicesMap);

      // Upload the service changes to database.
      uploadServicesToDB();

      // Write the web service changes into the excel sheet.
      createServiceReport();
    }
    catch (WadlParserException | IcdmException | ServiceReportException e) {
      throw new ServiceLoaderException(e.getMessage(), e);
    }
  }

  /**
   * Parse the WADL file and fetch the results to service map
   *
   * @throws WadlParserException error while parsing wadl
   */
  private void fetchServiceDetails() throws WadlParserException {
    // parse the WADL file
    WadlParser parser = new WadlParser(this.logger, this.wadlFilePath);
    parser.parse();
    this.wadlServicesMap = parser.getServices();
  }

  /**
   * Create or update the service changes
   *
   * @throws IcdmException
   */
  private void uploadServicesToDB() throws IcdmException {

    List<AbstractSimpleCommand> cmdList = new ArrayList<>();

    // Get the list of existing web services from the data base.
    Map<Long, WsService> servicesInDB = getExistingServicesInDB();

    if (servicesInDB != null) {
      servicesInDB.forEach((id, obj) -> checkForServiceChange(obj));
    }
    // Update the existing web services.
    for (WsService servObj : this.modifiedServices) {
      WsServiceCommand wsCmd = new WsServiceCommand(getServiceData(), servObj, false);
      cmdList.add(wsCmd);
    }

    if (!cmdList.isEmpty()) {
      executeCommand(cmdList);
      this.logger.info("Total services details updated/deleted = {}", cmdList.size());
    }

    cmdList.clear();

    // Create the new web services
    for (Map.Entry<String, WsService> entry : this.wadlServicesMap.entrySet()) {
      WsServiceCommand wsCmd = new WsServiceCommand(getServiceData(), entry.getValue(), true);
      cmdList.add(wsCmd);
    }

    if (!cmdList.isEmpty()) {
      executeCommand(cmdList);
      this.logger.info("Total services details added = {}", cmdList.size());
    }
  }

  private void createServiceReport() throws ServiceReportException {
    ServiceReport report = new ServiceReport(new TreeSet<>(this.wadlServicesMap.values()), this.modifiedServices,
        this.unChangedServices, this.oldWsServiceDataMap, this.logger);
    report.generate();
  }

  /**
   * Check for any change in the web service object.
   *
   * @param dbServObj WsService
   */
  private void checkForServiceChange(final WsService dbServObj) {

    WsService wadlServObj = this.wadlServicesMap.get(dbServObj.getName());

    if (wadlServObj != null) {
      if (!wadlServObj.equals(dbServObj)) {
        this.oldWsServiceDataMap.put(dbServObj.getName(), dbServObj);
        dbServObj.setServiceScope(wadlServObj.getServiceScope());
        dbServObj.setDescription(wadlServObj.getDescription());
        dbServObj.setDeleted(wadlServObj.isDeleted());
        this.modifiedServices.add(dbServObj);
      }
      else {
        this.unChangedServices.add(dbServObj);
      }
      this.wadlServicesMap.remove(dbServObj.getName());
    }
    else if (!dbServObj.isDeleted()) {
      this.oldWsServiceDataMap.put(dbServObj.getName(), dbServObj);
      dbServObj.setDeleted(true);
      this.modifiedServices.add(dbServObj);
    }
    else {
      this.unChangedServices.add(dbServObj);
    }
  }


  /**
   * Gets all the existing services from the database.
   *
   * @return Map<Long -id, WsService>
   */
  private Map<Long, WsService> getExistingServicesInDB() {
    Map<Long, WsService> allWsServices = null;
    try {
      allWsServices = new WsServiceLoader(getServiceData()).getAllWsServices();
    }
    catch (DataException e) {
      this.logger.error("Error while getting the existing services from the database" + e.getMessage(), e);
    }
    return allWsServices;
  }


  /**
   * Displays the list of services from WADL file to the console.
   *
   * @param serviceMap Map<String -UniqueID, WsService>
   */
  private void logServiceDetails(final Map<String, WsService> serviceMap) {
    if (!this.logger.isDebugEnabled()) {
      return;
    }

    StringBuilder sb = new StringBuilder("Services found in WADL :\nKey\tMethod Name\tURI\tModule\tDescription");

    for (Map.Entry<String, WsService> entry : serviceMap.entrySet()) {
      WsService servObj = entry.getValue();
      sb.append('\n').append(entry.getKey()).append('\t').append(servObj.getServMethod()).append('\t')
          .append(servObj.getServUri()).append('\t').append(servObj.getModule()).append('\t');
      String desc = servObj.getDescription();
      if (desc != null) {
        desc = desc.replace('\n', ' ').replace('\r', ' ').replace('\t', ' ');
      }
      sb.append(desc);
    }

    this.logger.debug(sb.toString());
  }

  /**
   * Returns the service data object
   *
   * @return ServiceData
   */
  private ServiceData getServiceData() {
    return this.servData;
  }

  /**
   * Executes the command.
   *
   * @param commandList to execute
   * @throws IcdmException error in executing command
   */
  private final void executeCommand(final List<AbstractSimpleCommand> commandList) throws IcdmException {
    getServiceData().getCommandExecutor().execute(commandList);
  }

}
