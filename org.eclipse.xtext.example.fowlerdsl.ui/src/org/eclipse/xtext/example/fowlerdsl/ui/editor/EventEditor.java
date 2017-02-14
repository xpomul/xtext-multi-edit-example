package org.eclipse.xtext.example.fowlerdsl.ui.editor;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.xtext.example.fowlerdsl.statemachine.Event;
import org.eclipse.xtext.example.fowlerdsl.statemachine.Statemachine;
import org.eclipse.xtext.example.fowlerdsl.statemachine.StatemachineFactory;
import org.eclipse.xtext.ui.editor.model.IXtextDocument;

public class EventEditor extends EditorPart
{
  private XtextDocumentBasedEditorInput xtextEditorInput;
  private ListViewer listViewer;

  @Override
  public void init(final IEditorSite site, final IEditorInput input) throws PartInitException
  {
    this.xtextEditorInput = (XtextDocumentBasedEditorInput) input;
    setSite(site);
  }

  // not called because handled in StatemachineMultiPageEditor
  @Override
  public boolean isDirty()
  {
    return false;
  }

  // not called because handled in StatemachineMultiPageEditor
  @Override
  public void doSave(final IProgressMonitor monitor)
  {
  }

  // not called because handled in StatemachineMultiPageEditor
  @Override
  public void doSaveAs()
  {
  }

  // not called because handled in StatemachineMultiPageEditor
  @Override
  public boolean isSaveAsAllowed()
  {
    return true;
  }

  @Override
  public void createPartControl(final Composite parent)
  {
    Composite mainComposite = new Composite(parent, SWT.NONE);
    mainComposite.setLayout(new GridLayout(2, false));

    Label titleLabel = new Label(mainComposite, SWT.NONE);
    titleLabel.setFont(JFaceResources.getBannerFont());
    titleLabel.setText("State Machine Events");
    titleLabel.setLayoutData(GridDataFactory.fillDefaults().span(2, 1).create());

    this.listViewer = new ListViewer(mainComposite, SWT.BORDER);
    this.listViewer.getControl().setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
    this.listViewer.setContentProvider(new ArrayContentProvider());
    this.listViewer.setLabelProvider(new LabelProvider()
    {
      @Override
      public String getText(final Object element)
      {
        if (element instanceof Event)
        {
          Event event = (Event) element;
          return event.getName() + " [" + event.getCode() + "]";
        }
        return super.getText(element);
      }
    });

    Composite buttonComposite = new Composite(mainComposite, SWT.NONE);
    buttonComposite.setLayout(new FillLayout(SWT.VERTICAL));
    buttonComposite.setLayoutData(GridDataFactory.swtDefaults().create());
    Button addButton = new Button(buttonComposite, SWT.PUSH);
    addButton.setText("Add");
    addButton.addListener(SWT.Selection, e -> addEvent());

    Button editButton = new Button(buttonComposite, SWT.PUSH);
    editButton.setText("Edit");
    editButton.addListener(SWT.Selection, e -> editEvent());

    Button removeButton = new Button(buttonComposite, SWT.PUSH);
    removeButton.setText("Remove");
    removeButton.addListener(SWT.Selection, e -> removeEvent());

    refreshInput();
  }

  /**
   * Add a new event.
   * 
   * This initializes a new Event object, opens the dialog to let the user specify the event information and if the user closes the dialog by clicking ok, the
   * new event is added to the Xtext document.
   */
  protected void addEvent()
  {
    Event event = StatemachineFactory.eINSTANCE.createEvent();
    event.setName("newEvent");
    event.setCode("CODE");

    boolean result = EditEventDialog.editEvent(getSite().getShell(), event);
    if (result)
    {
      IXtextDocument doc = this.xtextEditorInput.getDocument();
      doc.modify(res -> ((Statemachine) res.getContents().get(0)).getEvents().add(event));
    }

    refreshInput();
  }

  /**
   * Edit an event.
   * 
   * This method opens a dialog to let the user specify the new name and code for the event. If the user closes the dialog by clicking OK, we search for the
   * event in the XtextDocument (using the original name) and change its name and code.
   */
  protected void editEvent()
  {
    IStructuredSelection selection = (IStructuredSelection) this.listViewer.getSelection();
    Event event = (Event) selection.getFirstElement();
    String originalName = event.getName();

    boolean result = EditEventDialog.editEvent(getSite().getShell(), event);
    if (result)
    {
      IXtextDocument doc = this.xtextEditorInput.getDocument();
      doc.modify(res -> {
        Statemachine statemachine = ((Statemachine) res.getContents().get(0));
        Optional<Event> found = statemachine.getEvents().stream().filter(e -> Objects.equals(e.getName(), originalName)).findFirst();
        if (found.isPresent())
        {
          Event e = found.get();
          e.setName(event.getName());
          e.setCode(event.getCode());
        }
        return null;
      });
    }

    refreshInput();
  }

  /**
   * Delete an event.
   * 
   * This method searches for the selected event in the XtextDocument and deletes it.
   */
  protected void removeEvent()
  {
    IStructuredSelection selection = (IStructuredSelection) this.listViewer.getSelection();
    Event event = (Event) selection.getFirstElement();

    IXtextDocument doc = this.xtextEditorInput.getDocument();
    doc.modify(res -> {
      Statemachine statemachine = ((Statemachine) res.getContents().get(0));
      Optional<Event> found = statemachine.getEvents().stream().filter(e -> Objects.equals(e.getName(), event.getName())).findFirst();
      if (found.isPresent())
      {
        statemachine.getEvents().remove(found.get());
      }
      return null;
    });

    refreshInput();
  }

  /**
   * Recalculates the input to the list viewer, so it is in sync with the source in the Xtext editor.
   * 
   * This is called - when the page becomes visible - after performing an edit operation
   * 
   */
  public void refreshInput()
  {
    IXtextDocument doc = this.xtextEditorInput.getDocument();
    Collection<Event> events = doc.readOnly(res -> EcoreUtil.copyAll(((Statemachine) res.getContents().get(0)).getEvents()));
    this.listViewer.setInput(events.toArray());
  }

  @Override
  public void setFocus()
  {
    this.listViewer.getControl().setFocus();
  }
}
