package org.eclipse.xtext.example.fowlerdsl.ui.editor;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.xtext.ui.editor.model.IXtextDocument;

public class XtextDocumentBasedEditorInput implements IEditorInput
{
  private IXtextDocument document;

  private IFile file;

  public XtextDocumentBasedEditorInput(final IXtextDocument document)
  {
    this.document = document;
    this.file = document.getAdapter(IFile.class);
  }

  @Override
  public boolean exists()
  {
    return this.file.exists();
  }

  @Override
  public ImageDescriptor getImageDescriptor()
  {
    return this.file.getAdapter(IWorkbenchAdapter.class).getImageDescriptor(this.file);
  }

  @Override
  public String getName()
  {
    return this.file.getAdapter(IWorkbenchAdapter.class).getLabel(this.file);
  }

  @Override
  public IPersistableElement getPersistable()
  {
    return null;
  }

  @Override
  public String getToolTipText()
  {
    return getName();
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T extends Object> T getAdapter(final Class<T> adapter)
  {
    return (this.document.getAdapter(adapter));
  }

  public IXtextDocument getDocument()
  {
    return this.document;
  }

  public IFile getFile()
  {
    return this.file;
  }
}
