package com.bosch.caltool.apic.ui.jobs;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.apic.ui.editors.AttributesEditor;
import com.bosch.caltool.icdm.common.ui.jobs.rules.MutexRule;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.attr.AttrExportModel;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.report.excel.AttributesExport;
import com.bosch.caltool.icdm.ws.rest.client.apic.AttributeServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author dmo5cob
 */
public class AttributesExportJob extends Job {

  private final String filePath;
  private final String fileExtn;
  private final boolean openAutomatically;

  /**
   * Creates an instance of this class
   *
   * @param rule mutex rule
   * @param filePath export destination
   * @param fileExtn export file type
   * @param openAutomatically flag, if set, will open the output automatically
   */
  public AttributesExportJob(final MutexRule rule, final String filePath, final String fileExtn,
      final boolean openAutomatically) {
    super("Exporting Attributes");
    setRule(rule);
    this.filePath = filePath;
    this.fileExtn = fileExtn;
    this.openAutomatically = openAutomatically;


  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected final IStatus run(final IProgressMonitor monitor) {

    monitor.beginTask("Exporting Attributes", IProgressMonitor.UNKNOWN);
    // Call for Exporting Attributes

    monitor.subTask("Retrieving attribute details");
    AttrExportModel attrExpModel = getAttrExpModel();
    Map<Long, Attribute> attrsMap = getFilteredAttrsFromTableViewer();

    try {
      final AttributesExport attrsExport = new AttributesExport(monitor);
      attrsExport.exportAttributes(this.filePath, this.fileExtn, attrsMap, attrExpModel);
    }
    catch (IOException exp) {
      CDMLogger.getInstance().error(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
      return Status.CANCEL_STATUS;
    }

    if (monitor.isCanceled()) {
      final File file = new File(this.filePath + "." + this.fileExtn);
      file.delete();
      CDMLogger.getInstance().info("Exporting of attributes is cancelled", Activator.PLUGIN_ID);
      return Status.CANCEL_STATUS;
    }
    monitor.done();
    if (this.openAutomatically) {
      CommonUiUtils.openFile(FilenameUtils.removeExtension(this.filePath) + "." + this.fileExtn);
    }

    CDMLogger.getInstance().info("Attributes  exported successfully to file  " + this.filePath, Activator.PLUGIN_ID);
    return Status.OK_STATUS;
  }

  /**
   * @return Returns AttrExportModel Object
   */
  private AttrExportModel getAttrExpModel() {
    AttrExportModel ret = new AttrExportModel();
    try {
      ret = new AttributeServiceClient().getAttrExportModel();
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().info(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }
    return ret;
  }

  /**
   * @return Returns a map of attributes currently displayed in the tableviewer(with or without filters applied)
   */
  private Map<Long, Attribute> getFilteredAttrsFromTableViewer() {

    final Map<Long, Attribute> filteredAttrsMap = new HashMap<>();

    Display.getDefault().syncExec(() -> {
      for (IWorkbenchWindow window : PlatformUI.getWorkbench().getWorkbenchWindows()) {
        for (IWorkbenchPage page : window.getPages()) {
          for (IEditorReference editorRef : page.getEditorReferences()) {
            IEditorPart editorPart = editorRef.getEditor(false);
            if (editorPart instanceof AttributesEditor) {
              filteredAttrsMap.putAll(doGetSelectedAttrs(editorPart));
              break;
            }

          }
        }
      }

    });
    return filteredAttrsMap;
  }

  private Map<Long, Attribute> doGetSelectedAttrs(final IEditorPart editorPart) {
    final Map<Long, Attribute> filteredAttrsMap = new HashMap<>();
    GridTableViewer attrTabViewer = ((AttributesEditor) editorPart).getAttrPage().getAttrTableViewer();
    GridItem[] items = attrTabViewer.getGrid().getItems();
    for (GridItem gridItem : items) {
      if (gridItem.getData() instanceof Attribute) {
        Attribute attr = (Attribute) gridItem.getData();
        filteredAttrsMap.put(attr.getId(), attr);
      }
    }
    return filteredAttrsMap;
  }
}
