package com.bosch.caltool.icdm.product.perspectives;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import com.bosch.caltool.icdm.common.ui.views.OutlineViewPart;
import com.bosch.caltool.icdm.product.util.ICDMConstants;
import com.bosch.citi.lite.client.util.CITIConstants;
import com.bosch.citi.lite.client.util.CITIUtil;

/**
 * Class iCDMPerspective This class the main perspective window
 *
 * @author adn1cob
 */
public class CDMPerspective implements IPerspectiveFactory {

  @Override
  public void createInitialLayout(final IPageLayout layout) {
    String editorArea = layout.getEditorArea();
    
    IFolderLayout right = layout.createFolder(ICDMConstants.RIGHT_FOLDER, IPageLayout.RIGHT, 0.75f, editorArea);
    right.addView(OutlineViewPart.PART_ID);
    
    //Set how CITI is invoked from iCDM
    CITIUtil.getInstance().setCalledFrom(CITIConstants.NATIVE_METHOD);
    right.addView(ICDMConstants.CITI_VIEW_ID);
  }
}
