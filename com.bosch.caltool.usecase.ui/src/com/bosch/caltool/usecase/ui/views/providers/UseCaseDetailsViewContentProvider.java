/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.usecase.ui.views.providers;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.bosch.caltool.icdm.client.bo.uc.UseCaseSectionClientBO;
import com.bosch.caltool.icdm.client.bo.uc.UsecaseClientBO;


/**
 * @author adn1cob
 */
public class UseCaseDetailsViewContentProvider implements ITreeContentProvider {


  /**
   * Use case instance
   */
  private final UsecaseClientBO useCase;

  /**
   * UseCaseTreeViewContentProvider - Constructor
   *
   * @param editorUseCase UsecaseClientBO
   */
  public UseCaseDetailsViewContentProvider(final UsecaseClientBO editorUseCase) {
    this.useCase = editorUseCase;
  }

  /*
   * (non-Javadoc)
   * @see org.eclipse.jface.viewers.IContentProvider#dispose()
   */
  @Override
  public void dispose() {
    // Not applicable

  }

  /*
   * (non-Javadoc)
   * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface .viewers.Viewer, java.lang.Object,
   * java.lang.Object)
   */
  @Override
  public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
    // Not applicable

  }

  /*
   * (non-Javadoc)
   * @see org.eclipse.jface.viewers.ITreeContentProvider#getElements(java.lang. Object)
   */
  @Override
  public Object[] getElements(final Object inputElement) {
    // useCase is the root node
    if (inputElement instanceof Long) {
      return new Object[] { this.useCase };
    }
    // Display usecase sections
    if (inputElement instanceof UsecaseClientBO) {
      return ((UsecaseClientBO) inputElement).getUseCaseSections(true).toArray();
    }
    else if (inputElement instanceof UseCaseSectionClientBO) {
      return ((UseCaseSectionClientBO) inputElement).getChildSectionSet(true).toArray();
    }
    return new Object[] {};
  }

  /*
   * (non-Javadoc)
   * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang. Object)
   */
  @Override
  public Object[] getChildren(final Object parentElement) {
    // Get children of Use case
    if (parentElement instanceof UsecaseClientBO) {
      return ((UsecaseClientBO) parentElement).getUseCaseSectionSet(true).toArray();
    }
    // Generate children for usecase section
    if (parentElement instanceof UseCaseSectionClientBO) {
      return ((UseCaseSectionClientBO) parentElement).getChildSectionSet(true).toArray();
    }

    return new Object[] {};
  }

  /*
   * (non-Javadoc)
   * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object )
   */
  @Override
  public Object getParent(final Object element) {
    // Not applicable
    return null;
  }

  /*
   * (non-Javadoc)
   * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang. Object)
   */
  @Override
  public boolean hasChildren(final Object element) {
    // check children for UseCase
    if (element instanceof UsecaseClientBO) {
      return !((UsecaseClientBO) element).getUseCaseSections(true).isEmpty();
    }

    // check children for UseCaseSection
    if (element instanceof UseCaseSectionClientBO) {
      return !((UseCaseSectionClientBO) element).getChildSections(true).isEmpty();
    }
    return false;
  }

}
