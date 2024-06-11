/**
 * 
 */
package com.bosch.caltool.icdm.product.perspectives;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import com.bosch.caltool.icdm.common.ui.views.OutlineViewPart;
import com.bosch.caltool.icdm.product.util.ICDMConstants;
import com.bosch.citi.lite.client.util.CITIConstants;
import com.bosch.citi.lite.client.util.CITIUtil;

/**
 * @author adn1cob
 *
 */
public class PIDCPerspective implements IPerspectiveFactory {

  /* (non-Javadoc)
   * @see org.eclipse.ui.IPerspectiveFactory#createInitialLayout(org.eclipse.ui.IPageLayout)
   */
  @Override
  public void createInitialLayout(IPageLayout layout) {

    String editorArea = layout.getEditorArea();

    IFolderLayout right = layout.createFolder(ICDMConstants.RIGHT_FOLDER, IPageLayout.RIGHT, 0.75f, editorArea);
    right.addView(OutlineViewPart.PART_ID);

    //Set how CITI is invoked from iCDM
    CITIUtil.getInstance().setCalledFrom(CITIConstants.NATIVE_METHOD);
    right.addView(ICDMConstants.CITI_VIEW_ID);
  }
}
