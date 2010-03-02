/* AWE - Amanzi Wireless Explorer
 * http://awe.amanzi.org
 * (C) 2008-2009, AmanziTel AB
 *
 * This library is provided under the terms of the Eclipse Public License
 * as described at http://www.eclipse.org/legal/epl-v10.html. Any use,
 * reproduction or distribution of the library constitutes recipient's
 * acceptance of this agreement.
 *
 * This library is distributed WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */
package org.amanzi.awe.neighbours.views;


import java.util.LinkedHashMap;

import org.amanzi.awe.neighbours.AnalyseModel;
import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.database.nodes.SpreadsheetNode;
import org.amanzi.neo.core.enums.NetworkRelationshipTypes;
import org.amanzi.neo.core.service.NeoServiceProvider;
import org.amanzi.neo.core.utils.ActionUtil;
import org.amanzi.neo.core.utils.NeoUtils;
import org.amanzi.splash.utilities.NeoSplashUtil;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;
import org.neo4j.api.core.Direction;
import org.neo4j.api.core.NeoService;
import org.neo4j.api.core.Node;
import org.neo4j.api.core.Relationship;
import org.neo4j.api.core.ReturnableEvaluator;
import org.neo4j.api.core.Transaction;
import org.neo4j.api.core.TraversalPosition;
import org.neo4j.api.core.Traverser;


public class NeighbourAnalyser extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "org.amanzi.awe.neighbours.views.NeighbourAnalyser"; //$NON-NLS-1$
    private Composite mainFrame;
    private Text text0;
    private Text text1;
    private Text text2;
    private Text text3;
    private Text text4;
    private Combo cGpeh;
    private Combo cNeighbour;
    private Button bStart;
    private LinkedHashMap<String, Node> gpeh;
    private final NeoService neo=NeoServiceProvider.getProvider().getService();
    private LinkedHashMap<String, Node> neighbour;

	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	@Override
    public void createPartControl(Composite parent) {
	    mainFrame = new Composite(parent, SWT.FILL);
	    mainFrame.setLayout(new GridLayout(3  ,false));
	    Label label=new Label(mainFrame,SWT.NONE);
	    label.setText(Messages.NeighbourAnalyser_0);
	    text0 = new Text(mainFrame, SWT.BORDER);
	    GridData layoutData = new GridData();	    
	    layoutData.minimumWidth=50;
	    text0.setLayoutData(layoutData);
	    label=new Label(mainFrame, SWT.LEFT);
	    label.setText(Messages.NeighbourAnalyser_0_d);
        
        label=new Label(mainFrame,SWT.NONE);
        label.setText(Messages.NeighbourAnalyser_1);
        text1 = new Text(mainFrame, SWT.BORDER);
        label=new Label(mainFrame, SWT.LEFT);
        label.setText(Messages.NeighbourAnalyser_1_d);
        
        label=new Label(mainFrame,SWT.NONE);
        label.setText(Messages.NeighbourAnalyser_2);
        text2 = new Text(mainFrame, SWT.BORDER);
        label=new Label(mainFrame, SWT.LEFT);
        label.setText(Messages.NeighbourAnalyser_2_d);
        
        label=new Label(mainFrame,SWT.NONE);
        label.setText(Messages.NeighbourAnalyser_3);
        label.setToolTipText(Messages.NeighbourAnalyser_3_1);
        text3 = new Text(mainFrame, SWT.BORDER);
        label=new Label(mainFrame, SWT.LEFT);
        label.setText(Messages.NeighbourAnalyser_3_d);
        
        label=new Label(mainFrame,SWT.NONE);
        label.setText(Messages.NeighbourAnalyser_4);
        text4 = new Text(mainFrame, SWT.BORDER);
        label=new Label(mainFrame, SWT.LEFT);
        label.setText(Messages.NeighbourAnalyser_4_d);
        
        label=new Label(mainFrame,SWT.NONE);
        label.setText(Messages.NeighbourAnalyser_5);
        cGpeh= new Combo(mainFrame,SWT.DROP_DOWN | SWT.READ_ONLY);
        layoutData=new GridData();
        layoutData.horizontalSpan=2;
        layoutData.grabExcessHorizontalSpace=true;
        layoutData.minimumWidth=200;
        cGpeh.setLayoutData(layoutData);
        
        label=new Label(mainFrame,SWT.NONE);
        label.setText(Messages.NeighbourAnalyser_6);
        cNeighbour= new Combo(mainFrame, SWT.DROP_DOWN | SWT.READ_ONLY);
        layoutData=new GridData();
        layoutData.horizontalSpan=2;
        layoutData.grabExcessHorizontalSpace=true;
        layoutData.minimumWidth=200;
        cNeighbour.setLayoutData(layoutData);
        
        bStart=new Button(mainFrame, SWT.PUSH);
        bStart.setText(Messages.NeighbourAnalyser_7);

        
        init();
        addListeners();
	}



	/**
     *initialize
     */
    private void init() {
        formGPEH();
        formNeighbour();
        validateStartButton();
    }
    /**
     * validate start button
     */
    private void validateStartButton(){
        bStart.setEnabled(isValidInput());
    }


    /**
     *is input valid?
     * @return result
     */
    private boolean isValidInput() {
        return gpeh.get(cGpeh.getText())!=null;
    }



    /**
     *forms Neighbour list
     */
    private void formNeighbour() {
        neighbour=new LinkedHashMap<String,Node>();
        Transaction tx = neo.beginTx();
        try{
            Traverser gisWithNeighbour = NeoUtils.getAllReferenceChild(neo, new ReturnableEvaluator() {
                
                @Override
                public boolean isReturnableNode(TraversalPosition currentPos) {
                    Node node= currentPos.currentNode();
                    return NeoUtils.isGisNode(node)&&node.hasRelationship(NetworkRelationshipTypes.NEIGHBOUR_DATA, Direction.OUTGOING);
                }
            });
            for (Node node:gisWithNeighbour){
                String id = (String)node.getProperty(INeoConstants.PROPERTY_NAME_NAME,"");
                for (Relationship ret : node.getRelationships(NetworkRelationshipTypes.NEIGHBOUR_DATA, Direction.OUTGOING)) {
                    Node neigh = ret.getOtherNode(node);
                    neighbour.put(id + ": " + neigh.getProperty(INeoConstants.PROPERTY_NAME_NAME), neigh);
                }
            }
        }finally{
            tx.finish();
        }
        cNeighbour.setItems(neighbour.keySet().toArray(new String[0]));
    }



    /**
     *
     */
    private void formGPEH() {
        gpeh=new LinkedHashMap<String,Node>();
        for (Node node:NeoUtils.getAllGpeh(neo)){
            gpeh.put(NeoUtils.getNodeType(node, ""), node);
        }
        cGpeh.setItems(gpeh.keySet().toArray(new String[0]));
    }



    /**
     *add listeners
     */
    private void addListeners() {
        bStart.addSelectionListener(new SelectionListener() {
            
            @Override
            public void widgetSelected(SelectionEvent e) {
                startAnalyse();
            }
            
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
        cGpeh.addSelectionListener(new SelectionListener() {
            
            @Override
            public void widgetSelected(SelectionEvent e) {
                validateStartButton();
            }
            
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
        cNeighbour.addSelectionListener(new SelectionListener() {
            
            @Override
            public void widgetSelected(SelectionEvent e) {
                validateStartButton();
            }
            
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
    }



    /**
     *
     */
    protected void startAnalyse() {
        final Node gpehNode=gpeh.get(cGpeh.getText());
        Job job=new Job("analyse") {
            
            @Override
            protected IStatus run(IProgressMonitor monitor) {
                analyse(gpehNode);
                return Status.OK_STATUS;
            }
        };
        job.schedule();
    }



    /**
     *
     * @param gpehNode
     * @return
     */
    protected void analyse(Node gpehNode) {
        AnalyseModel model=AnalyseModel.create(gpehNode,neo);
         final SpreadsheetNode spreadsheet = model.createSpreadSheet("event",neo);
         ActionUtil.getInstance().runTask(new Runnable() {
            
            @Override
            public void run() {
                NeoSplashUtil.openSpreadsheet(spreadsheet);
            }
        },true);

    }



    private void showMessage(String message) {
		MessageDialog.openInformation(
		        mainFrame.getShell(),
			"NeighbourAnalyser", //$NON-NLS-1$
			message);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	@Override
    public void setFocus() {
	}
	
}