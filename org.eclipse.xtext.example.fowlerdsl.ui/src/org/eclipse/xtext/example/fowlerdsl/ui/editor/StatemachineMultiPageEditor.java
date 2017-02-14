package org.eclipse.xtext.example.fowlerdsl.ui.editor;

import javax.inject.Inject;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.xtext.ui.editor.XtextEditor;

public class StatemachineMultiPageEditor extends MultiPageEditorPart
{
  @Inject
  private XtextEditor sourceEditor;

  @Inject
  private EventEditor formEditor;

  private int sourcePageIndex;

  private int formPageIndex;

  @Override
  public void doSave(final IProgressMonitor monitor)
  {
    this.sourceEditor.doSave(monitor);
  }

  @Override
  public void doSaveAs()
  {
    this.sourceEditor.doSaveAs();
  }

  @Override
  public boolean isDirty()
  {
    return this.sourceEditor.isDirty();
  }

  @Override
  public boolean isSaveAsAllowed()
  {
    return this.sourceEditor.isSaveAsAllowed();
  }

  @Override
  protected void createPages()
  {
    try
    {
      this.sourcePageIndex = addPage(this.sourceEditor, getEditorInput());
      setPageText(this.sourcePageIndex, "Source");
    }
    catch (PartInitException e)
    {
      throw new IllegalStateException("Failed to add Xtext editor", e);
    }

    try
    {
      this.formPageIndex = addPage(this.formEditor, new XtextEditorBasedEditorInput(this.sourceEditor));
      setPageText(this.formPageIndex, "States");
    }
    catch (PartInitException e)
    {
      throw new IllegalStateException("Failed to add State editor", e);
    }
  }

  @Override
  protected void pageChange(final int newPageIndex)
  {
    if (newPageIndex == this.formPageIndex)
    {
      this.formEditor.refreshInput();
    }
    super.pageChange(newPageIndex);
  }
}
