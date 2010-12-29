package org.amanzi.awe.afp.wizards;

import org.amanzi.awe.afp.models.AfpModel;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;

public class AfpSummaryPage extends AfpWizardPage {
	
	private Text summaryText;
	
	public AfpSummaryPage(String pageName, AfpModel model, String desc) {
		super(pageName, model);
        setTitle(AfpImportWizard.title);
        setDescription(desc);
        setPageComplete (false);
	}
	
	@Override
	public void createControl(Composite parent) {
		Composite parentLocal = new Composite(parent, SWT.NONE);
   	 	parentLocal.setLayout(new GridLayout(2, false));
//		parent.setLayout(new GridLayout(2, false));
		
		Group stepsGroup = AfpWizardUtils.getStepsGroup(parentLocal, 7);
		
		Group main = new Group(parentLocal, SWT.NONE);
		main.setLayout(new GridLayout(1, false));
		main.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1 ,1));
		
		summaryText = new Text (main, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
		GridData gridData = new GridData(GridData.FILL, GridData.FILL, true, true, 1 ,1);
		gridData.heightHint = 300;
		gridData.minimumWidth = 100;
//		gridData.widthHint = 300;
		summaryText.setLayoutData(gridData);
		
		Button saveButton = new Button(main, SWT.PUSH);
		saveButton.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false, 1, 1));
		
		saveButton.setText("Save As");
		saveButton.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				//TODO do something here
			}
		});
		
		setPageComplete(true);
    	setControl (parentLocal);
		

	}
	
	protected boolean isValidPage() {
	      //TODO set this flag to true here only for testing purpose. Should be only done in summary page otherwise
	      //AfpImportWizard.isDone = true;
		  ((AfpImportWizard)this.getWizard()).setDone(true);
	      return true;
	}
	
	
	public void refreshPage(){
		summaryText.setText(model.toString());
		isValidPage();
	}

	@Override
	public IWizardPage getNextPage() {
		// TODO Auto-generated method stub
		return null;
	}

	
}