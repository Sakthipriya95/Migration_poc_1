/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.report.pdf;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.pdmodel.PDDocument;

import com.bosch.calcomp.adapter.logger.Activator;
import com.bosch.caltool.dmframework.bo.AbstractSimpleBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.a2l.A2lCompliParameterLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcA2lLoader;
import com.bosch.caltool.icdm.bo.cdr.DataAssessmentUtil;
import com.bosch.caltool.icdm.bo.comphex.CompHexWithCDFxProcess;
import com.bosch.caltool.icdm.bo.report.compli.A2lCompliCheckPdfReport;
import com.bosch.caltool.icdm.bo.report.compli.PdfUtil;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.A2lCompliParameterServiceResponse;
import com.bosch.caltool.icdm.model.cdr.WpArchival;
import com.bosch.caltool.icdm.model.dataassessment.DataAssessmentReport;

/**
 * @author MSP5COB
 */
public class CompliReviewPdfExport extends AbstractSimpleBusinessObject {


  /**
   * Path of the base folder structure where baseline files need to be exported
   */
  private final String filepath;

  private final DataAssessmentReport dataAssessmentReport;
  private final WpArchival wpArchival;

  /**
   * @param serviceData ServiceData
   * @param dataAssessmentReport DataAssessmentReport
   * @param wpArchival WpArchival
   * @param filepath base folder structure path
   */
  public CompliReviewPdfExport(final ServiceData serviceData, final DataAssessmentReport dataAssessmentReport,
      final WpArchival wpArchival, final String filepath) {
    super(serviceData);
    this.dataAssessmentReport = dataAssessmentReport;
    this.wpArchival = wpArchival;
    this.filepath = filepath;
  }


  /**
   * @throws IOException IOException
   * @throws IcdmException IcdmException
   */
  public void createCompliReviewPdfAReport() throws IOException, IcdmException {
    // Open created PDF report if available
    String filePath = CompHexWithCDFxProcess.COMP_HEX_WORK_DIR + CompHexWithCDFxProcess.FILE_DELIMITER +
        this.dataAssessmentReport.getDataAssmntCompHexData().getReferenceId();

    File pdfFile = getReportFile(new File(filePath));
    if ((pdfFile != null) && CommonUtils.isFileAvailable(pdfFile.getPath())) {

      PDDocument document = PDDocument.load(pdfFile);
      File file = DataAssessmentUtil.createFileDirectory(this.filepath, "Compliance Review");
      String fullPdfFilePath = file.getAbsolutePath() + File.separator +
          this.dataAssessmentReport.getDataAssmntCompHexData().getReferenceId() + ".pdf";

      // Delete the PDF report if it already exists
      File outputFile = new File(fullPdfFilePath);
      if (outputFile.exists()) {
        Files.delete(outputFile.toPath());
        getLogger().info("Existing PDF file deleted to create new report : {}", outputFile.getPath());
      }

      getLogger().debug("Updating PDF to PDF/A Standard");
      // Convert the PDF into PDF/A
      PdfUtil.setPdfAStandard(document);

      getLogger().debug("Writing data to PDF file");
      // Save pdf document
      document.save(fullPdfFilePath);

      // close document
      document.close();

      getLogger().info("Compliance Review PDFA created. Path : {}", fullPdfFilePath);
    }
    else {
      CDMLogger.getInstance().error("Compare Hex : Compliance PDF Report is not available!", Activator.PLUGIN_ID);
    }
  }


  /**
   * @throws IcdmException IcdmException
   * @throws IOException IOException
   */
  public void createCompliCheckPdfAReport() throws IcdmException, IOException {

    Long a2lFileId = (CommonUtils.isNotNull(this.dataAssessmentReport)) ? this.dataAssessmentReport.getA2lFileId()
        : new PidcA2lLoader(getServiceData()).getDataObjectByID(this.wpArchival.getPidcA2lId()).getA2lFileId();
    Long pidcA2lId = (CommonUtils.isNotNull(this.dataAssessmentReport)) ? this.dataAssessmentReport.getPidcA2lId()
        : this.wpArchival.getPidcA2lId();
    A2lCompliParameterServiceResponse a2lCompliParameterServiceResponse =
        new A2lCompliParameterLoader(getServiceData()).getCompliParametersForPdfAReport(a2lFileId, pidcA2lId);

    File file = DataAssessmentUtil.createFileDirectory(this.filepath, "Compliance Check");

    File pdfFile = new File(file.getAbsolutePath() + File.separator + "ComplianceReport.pdf");

    // Delete the PDF report if it already exists
    if (pdfFile.exists()) {
      Files.delete(pdfFile.toPath());
      getLogger().info("Existing PDF file deleted to create new report : {}", pdfFile.getPath());
    }

    final A2lCompliCheckPdfReport report =
        new A2lCompliCheckPdfReport(getServiceData(), a2lCompliParameterServiceResponse, pdfFile);
    report.constructPdfA();

    getLogger().info("Compliance Check PDFA created. Path : {}", pdfFile.getPath());
  }


  /**
   * Gets the report file.
   *
   * @param directory the directory
   * @return the report file
   */
  private File getReportFile(final File directory) {
    Iterator<?> fileIt = null;
    String[] extensions = { "pdf" };
    fileIt = FileUtils.iterateFiles(directory, extensions, false);

    if ((fileIt == null) || !fileIt.hasNext()) {
      return null;
    }
    while (fileIt.hasNext()) {
      return (File) fileIt.next();
    }
    return null;
  }


}
