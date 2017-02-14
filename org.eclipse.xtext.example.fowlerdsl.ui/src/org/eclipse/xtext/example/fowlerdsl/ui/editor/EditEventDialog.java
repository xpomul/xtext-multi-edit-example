package org.eclipse.xtext.example.fowlerdsl.ui.editor;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.xtext.example.fowlerdsl.statemachine.Event;

public class EditEventDialog extends TitleAreaDialog
{
  private Event eventToEdit;

  public EditEventDialog(final Shell parentShell)
  {
    super(parentShell);
    setBlockOnOpen(true);
  }

  @Override
  protected Control createDialogArea(final Composite parent)
  {
    Control control = super.createDialogArea(parent);

    setTitle("Enter event data");
    setMessage("Please enter the information for the event");

    Composite area = new Composite((Composite) control, SWT.NONE);
    area.setLayout(new GridLayout(2, false));
    area.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());

    Label nameLabel = new Label(area, SWT.NONE);
    nameLabel.setText("Name:");
    nameLabel.setLayoutData(GridDataFactory.swtDefaults().create());

    Text nameText = new Text(area, SWT.BORDER);
    nameText.setText(this.eventToEdit.getName());
    nameText.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
    nameText.addModifyListener(e -> this.eventToEdit.setName(nameText.getText()));

    Label codeLabel = new Label(area, SWT.NONE);
    codeLabel.setText("Code:");
    codeLabel.setLayoutData(GridDataFactory.swtDefaults().create());

    Text codeText = new Text(area, SWT.BORDER);
    codeText.setText(this.eventToEdit.getCode());
    codeText.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
    codeText.addModifyListener(e -> this.eventToEdit.setCode(codeText.getText()));

    return control;
  }

  public static boolean editEvent(final Shell s, final Event e)
  {
    EditEventDialog dialog = new EditEventDialog(s);

    // copy the event, so the original event remains unchanged unless the user clicks OK
    dialog.eventToEdit = EcoreUtil.copy(e);

    if (dialog.open() == OK)
    {
      e.setName(dialog.eventToEdit.getName());
      e.setCode(dialog.eventToEdit.getCode());
      return true;
    }
    return false;
  }
}
