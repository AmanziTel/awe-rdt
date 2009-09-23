package org.amanzi.neo.loader.dialogs;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.ui.ApplicationGIS;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.NeoCorePlugin;
import org.amanzi.neo.core.service.NeoServiceProvider;
import org.amanzi.neo.core.utils.NeoUtils;
import org.amanzi.neo.loader.LoadNetwork;
import org.amanzi.neo.loader.NetworkLoader;
import org.amanzi.neo.loader.TEMSLoader;
import org.amanzi.neo.loader.internal.NeoLoaderPlugin;
import org.amanzi.neo.loader.internal.NeoLoaderPluginMessages;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.neo4j.api.core.Node;
import org.neo4j.api.core.Transaction;
import org.neo4j.api.core.Traverser;

/**
 * Dialog for Loading TEMS data
 * 
 * @author Lagutko_N
 */
public class TEMSDialog {
    /*
     * Names of supported files for TEMS data
     */
    public static final String[] TEMS_FILE_NAMES = {"(*.FMT)"};
    
    /*
     * Extensions of supported files for TEMS data
     */
    public static final String[] TEMS_FILE_EXTENSIONS = {"*.FMT"};
	
    /*
     * Minimum height of Shell
     */
    private static final int MINIMUM_HEIGHT = 400;
    
    /*
     * Minimum width of Shell
     */
    private static final int MINIMUM_WIDTH = 600;

    /*
     * Layout for One column and Fixed Width
     */
    private final static GridLayout layoutOneColumnNotFixedWidth = new GridLayout(1, false); 
	
	/*
	 * Shell of this Dialog
	 */
	private Shell temsShell;
	
	/*
	 * Button for FileDialog
	 */
	private Button browseDialogButton;
	
	/*
	 * Button for adding to load files
	 */
	private Button addFilesToLoaded;
	
	/*
	 * Button for removing from load files
	 */
	private Button removeFilesFromLoaded;
	
	/*
	 * List for files to choose
	 */
	private List folderFilesList;
	
	/*
	 * List for files to load
	 */
	private List filesToLoadList;
	
	/*
	 * Cancel button
	 */
	private Button cancelButton;
	
	/*
	 * Load button
	 */
	private Button loadButton;
	
	/*
	 * Maps for storing name of file and path to file
	 */
	private HashMap<String, String> folderFiles = new HashMap<String, String>();
	private HashMap<String, String> loadedFiles = new HashMap<String, String>();

    private Button addAllFilesToLoaded;

    private Button removeAllFilesFromLoaded;

	private Combo combo;
	private String datasetName;
	/* 
	 * Default directory for file dialogs 
	 */
	private static String defaultDirectory = null;
    /**
     * wizard page if tems dialog was created from import wizard page
     */
    private WizardPage wizardPage = null;

	/**
	 * Creates a Shell and add GUI elements
	 * 
	 * @param shell shell
	 * @param createNewShell is true than create a child shell of given shell for dialog
	 */
	
	protected TEMSDialog(Shell shell, boolean createNewShell) {
		if (createNewShell) {
			temsShell = new Shell(shell);
		}
		else {
			temsShell = shell;
		}
		
		//TODO move to constants
		temsShell.setMinimumSize(MINIMUM_WIDTH, MINIMUM_HEIGHT);
		
		temsShell.setText(NeoLoaderPluginMessages.TEMSDialog_DialogTitle);
		
		createControl(temsShell);
		createActions(temsShell);
	}
	
	/**
	 * Creates a Dialog in parentShell
	 * 
	 * @param parentShell
	 */
	
	public TEMSDialog(Shell parentShell) {
		this(parentShell, true);
	}
	
	/**
	 * Create a Dialog in new Shell
	 * @param display
	 */
	
	public TEMSDialog(Display display) {
		this(new Shell(display), false);
	}

    /**
     * Constructor for launch from import wizards
     * 
     * @param parent - Composite
     * @param wizardPage wizards page
     */
    public TEMSDialog(Composite parent, WizardPage wizardPage) {
        this.wizardPage = wizardPage;
        temsShell = parent.getShell();
        temsShell.setText(NeoLoaderPluginMessages.TEMSDialog_DialogTitle);
        createControlForDialog(parent);
        createActions(temsShell);
    }
	/**
	 * Opens a Dialog
	 * 
	 */
	
	public void open() {		
		temsShell.pack();
		temsShell.open();
	}
	
	/**
	 * Creates controls in parent Composite
	 * 
	 * @param parent parent Composite
	 */
	
	private void createControl(Composite parent) {
		GridLayout layout = layoutOneColumnNotFixedWidth;
		parent.setLayout(layout);
		parent.setLayoutData(new GridData(SWT.FILL));
		
		createSelectFileGroup(parent);
		createFinishButtons(parent);
	}

    /**
     * Creates controls in parent Composite
     * 
     * @param parent parent Composite
     */

    public void createControlForDialog(Composite parent) {
        GridLayout layout = layoutOneColumnNotFixedWidth;
        parent.setLayout(layout);
        parent.setLayoutData(new GridData(SWT.FILL));
        loadButton = new Button(parent, SWT.NONE);
        cancelButton = loadButton;
        createSelectFileGroup(parent);
        cancelButton.moveBelow(null);
        cancelButton.setVisible(false);
    }

	/**
	 * Creates group for selecting files to load
	 * 
	 * @param parent
	 */
	
	private void createSelectFileGroup(Composite parent) {
		createDatasetRow(parent);
		Group group = new Group(parent, SWT.NONE);		
		group.setLayout(new GridLayout(3, false));
		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		createFolderSelectionComposite(group);
		createManipulationComposite(group);
		createFileToLoadComposite(group);
	}
/**
 * Creates group for selecting
 * @param parent
 */
	private void createDatasetRow(Composite parent) {
		Composite panel = new Composite(parent, SWT.NONE);
		panel.setLayout(new FormLayout());
		GridData data = new GridData(SWT.FILL, SWT.BOTTOM, true, false);
		panel.setLayoutData(data);

		Label ldataset=new Label(panel, SWT.NONE);
		ldataset.setText(NeoLoaderPluginMessages.TEMSDialog_DatasetLabel);
		combo = new Combo(panel, SWT.NONE);
        FormData dLabel = new FormData(); 
        dLabel.left = new FormAttachment(0, 5);
        dLabel.top = new FormAttachment(combo, 5, SWT.CENTER);
        ldataset.setLayoutData(dLabel);

        FormData dCombo = new FormData(); 
        dCombo.left = new FormAttachment(ldataset, 5);
        dCombo.top = new FormAttachment(0, 2);
        dCombo.right = new FormAttachment(40,3);
        combo.setLayoutData(dCombo);
        
        //TODO: Check if the following line is needed
        Transaction tx = NeoServiceProvider.getProvider().getService().beginTx();
        ArrayList<String> datasetList = new ArrayList<String>();
        Traverser allDatasetTraverser = NeoCorePlugin.getDefault().getProjectService().getAllDatasetTraverser(
                NeoServiceProvider.getProvider().getService().getReferenceNode());
        for (Node node : allDatasetTraverser) {
            datasetList.add((String)node.getProperty(INeoConstants.PROPERTY_NAME_NAME));
        }
        combo.setItems(datasetList.toArray(new String[]{}));
	}
	
	/**
	 * Creates List for choosing Files
	 * 
	 * @param parent
	 */
	
	private void createFolderSelectionComposite(Composite parent) {
		Composite panel = new Composite(parent, SWT.NONE);
		panel.setLayout(layoutOneColumnNotFixedWidth);		
		panel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		folderFilesList = createSelectionList(panel, NeoLoaderPluginMessages.TEMSDialog_FilesToChooseListLabel);
	}
	
	/**
	 * Creates Buttons for manipulations
	 * 
	 * @param parent
	 */
	
	private void createManipulationComposite(Composite parent) {
		Composite panel = new Composite(parent, SWT.NONE);
		panel.setLayout(layoutOneColumnNotFixedWidth);
		panel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
		
		Composite choosePanel = new Composite(panel, SWT.NONE);
		choosePanel.setLayout(layoutOneColumnNotFixedWidth);
		choosePanel.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));		
		browseDialogButton = createChooseButton(choosePanel, NeoLoaderPluginMessages.TEMSDialog_BrowseButtonText, SWT.TOP);
		
		Composite actionPanel = new Composite(panel, SWT.NONE);
		actionPanel.setLayout(layoutOneColumnNotFixedWidth);
		actionPanel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));
		
		addFilesToLoaded = createChooseButton(actionPanel, NeoLoaderPluginMessages.TEMSDialog_AddButtonText, SWT.CENTER);
        addAllFilesToLoaded = createChooseButton(actionPanel, NeoLoaderPluginMessages.TEMSDialog_AddAllButtonText, SWT.CENTER);
		removeFilesFromLoaded = createChooseButton(actionPanel, NeoLoaderPluginMessages.TEMSDialog_RemoveButtonText, SWT.CENTER);
        removeAllFilesFromLoaded = createChooseButton(actionPanel, NeoLoaderPluginMessages.TEMSDialog_RemoveAllButtonText, SWT.CENTER);
	}
	
	/**
	 * Creates List for files to load
	 * 
	 * @param parent
	 */
	
	private void createFileToLoadComposite(Composite parent) {
		Composite panel = new Composite(parent, SWT.NONE);
		panel.setLayout(layoutOneColumnNotFixedWidth);		
		panel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		filesToLoadList = createSelectionList(panel, NeoLoaderPluginMessages.TEMSDialog_FilesToLoadListLabel);
	}
	
	/**
	 * Creates 'Cancel' and 'Load' buttons
	 * 
	 * @param parent
	 */
	
	private void createFinishButtons(Composite parent) {
		Composite panel = new Composite(parent, SWT.NONE);
		panel.setLayout(new FormLayout());
		GridData data = new GridData(SWT.FILL, SWT.BOTTOM, true, false);	
		panel.setLayoutData(data);
		
		cancelButton = new Button(panel, SWT.CENTER);
		cancelButton.setText(NeoLoaderPluginMessages.TEMSDialog_CancelButtonText);
		FormData cancelButtonFormData = new FormData();
		cancelButtonFormData.right = new FormAttachment(100, -10);
		cancelButtonFormData.bottom = new FormAttachment(100, -10);
		cancelButtonFormData.top = new FormAttachment(0, 10);
		cancelButtonFormData.width = 100;
		cancelButton.setLayoutData(cancelButtonFormData);
		
		loadButton = new Button(panel, SWT.CENTER);
		loadButton.setText(NeoLoaderPluginMessages.TEMSDialog_LoadButtonText);
		loadButton.setEnabled(false);
		FormData loadButtonFormData = new FormData();
		loadButtonFormData.right = new FormAttachment(cancelButton, -10);
		loadButtonFormData.bottom = new FormAttachment(100, -10);
		loadButtonFormData.top = new FormAttachment(0, 10);
		loadButtonFormData.width = 100;
		loadButton.setLayoutData(loadButtonFormData);
	}
	
	/**
	 * Create button for manipulation
	 * 
	 * @param parent parent Composite
	 * @param label label of Button
	 * @param position position of Button
	 * @return created Button
	 */
	
	private Button createChooseButton(Composite parent, String label, int position) {
		Button button = new Button(parent, SWT.NONE);
		button.setText(label);
		button.setLayoutData(new GridData(SWT.FILL, position, true, true));
		
		return button;
	}
	
	/**
	 * Creates a List with Label
	 * 
	 * @param parent parent Composite
	 * @param label test of Label
	 * @return created List
	 */
	
	private List createSelectionList(Composite parent, String label) {
		Label listLabel = new Label(parent, SWT.NONE);
		listLabel.setText(label);
		listLabel.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
		
		List list = new List(parent, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.minimumWidth = 150;
		list.setLayoutData(gridData);		
		
		return list;
	}
	
	/**
	 * Creates actions for buttons
	 * 
	 * @param parentShell Dialog Shell
	 */
	
	private void createActions(final Shell parentShell) {
		
		//opens Dialog for choosing files
		browseDialogButton.addSelectionListener(new SelectionAdapter() {
			
			public void widgetSelected(SelectionEvent e) {
				// User has selected to open a single file
		        FileDialog dlg = new FileDialog(parentShell, SWT.OPEN | SWT.MULTI);
				dlg.setText(NeoLoaderPluginMessages.TEMSDialog_FileDialogTitle);
		        dlg.setFilterNames(TEMS_FILE_NAMES);
		        dlg.setFilterExtensions(TEMS_FILE_EXTENSIONS);
		        dlg.setFilterPath(getDefaultDirectory());
				
		        String fn = dlg.open();
		      
		        if (fn != null) {
		        	setDefaultDirectory(dlg.getFilterPath());
		        	for (String name : dlg.getFileNames()) {		        		
		        		addFileToLoad(name, dlg.getFilterPath(), true);
			        	if (combo.getText().isEmpty()){
			        		combo.setText(name);
			        	}
		        	}
		        	
		        	File[] listFiles = new File(getDefaultDirectory()).listFiles(new TEMFFileFilter());
                    for (File file : listFiles) {
		        		addFileToChoose(file.getName(), getDefaultDirectory(), true);
		        	}

		        }
			}
			
		});
		
		//adds selected files to files to load
		addFilesToLoaded.addSelectionListener(new SelectionAdapter() {
			
			public void widgetSelected(SelectionEvent e) {
				for (String fileName : folderFilesList.getSelection()) {
					addFileToLoad(fileName);
				}
			}
			
		});
		
        addAllFilesToLoaded.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                for (String fileName : folderFilesList.getItems()) {
                    addFileToLoad(fileName);
                }
            }

        });

		//removes selected files from files to load
		removeFilesFromLoaded.addSelectionListener(new SelectionAdapter() {
			
			public void widgetSelected(SelectionEvent e) {
				for (String fileName : filesToLoadList.getSelection()) {
					removeFileToLoad(fileName);
				}
			}
			
		});
        removeAllFilesFromLoaded.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                for (String fileName : filesToLoadList.getItems()) {
                    removeFileToLoad(fileName);
                }
            }

        });
		
		//closes dialog
		cancelButton.addSelectionListener(new SelectionAdapter() {
			
			public void widgetSelected(SelectionEvent e) {
				temsShell.close();
			}
			
		});
		
		//loads TEMS data from chosen files		
		loadButton.addSelectionListener(new SelectionAdapter() {
			
			public void widgetSelected(SelectionEvent e) {
				runLoadingJob();				
			}


			
		});
		combo.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				checkLoadButton();
			}
		});
		
	}
	public void runLoadingJob() {
		datasetName=combo.getText();
		LoadTEMSJob job = new LoadTEMSJob(temsShell.getDisplay());
		job.schedule(50);
	}	
	/**
	 * FileFilter for TEMS data files
	 * 
	 * @author Lagutko_N
	 *
	 */
	
	private class TEMFFileFilter implements FileFilter {

		public boolean accept(File pathname) {
		    for (String extension : TEMS_FILE_EXTENSIONS) {
                if (pathname.getName().toLowerCase().endsWith(extension.toLowerCase().substring(1))) {
		            return true;
		        }
		    }
		    return false;
		}				
	}
	
	/**
	 * Loads TEMS data from files
	 * 
	 */
	
	private void loadTemsData(Display display, IProgressMonitor monitor) {
		
		display.asyncExec(new Runnable() {
			public void run() {
				temsShell.close();
			}
		});
		
		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}
		monitor.beginTask("Importing " + loadedFiles.size() + " drive test files", loadedFiles.size() * TEMSLoader.WORKED_PER_FILE);
        TEMSLoader temsLoader = null;
		for (String filePath : loadedFiles.values()) {
			try {
                temsLoader = new TEMSLoader(filePath, display, datasetName);
				temsLoader.run(monitor);
				temsLoader.printStats(false);	// stats for this load
				if(monitor.isCanceled()) break;
			}
			catch (IOException e) {
				NeoLoaderPlugin.exception(e);
			}
		}

        if (datasetName != null) {
            Node gis = temsLoader != null ? temsLoader.getGis() : null;
            addGisToMap(gis);
        }

        monitor.done();
    }

    // TODO move to utility class
    /**
     * adds gis to active map
     * 
     * @param gis node
     */
    public static void addGisToMap(Node gis) {
        try {
            String databaseLocation = NeoServiceProvider.getProvider().getDefaultDatabaseLocation();
            URL url = new URL("file://" + databaseLocation);
            IService curService = CatalogPlugin.getDefault().getLocalCatalog().getById(IService.class, url, null);
            final IMap map = ApplicationGIS.getActiveMap();
            if (curService != null && gis != null && NetworkLoader.findLayerByNode(map, gis) == null
                    && NetworkLoader.confirmLoadNetworkOnMap(map, NeoUtils.getNodeName(gis))) {
                java.util.List<IGeoResource> listGeoRes = new ArrayList<IGeoResource>();
                java.util.List<ILayer> layerList = new ArrayList<ILayer>();
                for (IGeoResource iGeoResource : curService.resources(null)) {
                    if (iGeoResource.canResolve(Node.class)) {
                        if (iGeoResource.resolve(Node.class, null).equals(gis)) {
                            listGeoRes.add(iGeoResource);
                            layerList.addAll(ApplicationGIS.addLayersToMap(map, listGeoRes, 0));
                            break;
                        }
                    }
                };
                NetworkLoader.zoomToLayer(layerList);
            }
        } catch (MalformedURLException e) {
            // TODO Handle MalformedURLException
            throw (RuntimeException)new RuntimeException().initCause(e);
        } catch (IOException e) {
            // TODO Handle IOException
            throw (RuntimeException)new RuntimeException().initCause(e);
        } catch (Exception e) {
            e.printStackTrace();
            e.printStackTrace();
        }
    }


    /**
     * Add file to 'Choose File' list
     * 
     * @param name name of file
     * @param path path to file
     * @param isNeedToConvert if true than convert name to 'fileName (filePath)'
     */
	
	private void addFileToChoose(String name, String path, boolean isNeedToConvert) {		
		if (isNeedToConvert) {
			path = path + File.separator + name;
			
			name = name + " (" + path + ")";
		}
		
		//if list doesn't contain this item than add it
		if (!folderFiles.containsKey(name) && !loadedFiles.containsKey(name)) {
			folderFiles.put(name, path);
			
			folderFilesList.add(name);
		}
	}
	
	/**
	 * Add file to 'Files To Load' list
	 * 
	 * @param name name of file
	 * @param path path to file
	 * @param isNeedToConvert if true than convert name to 'fileName (filePath)'
	 */
	
	private void addFileToLoad(String name, String path, boolean isNeedToConvert) {
		if (isNeedToConvert) {
			path = path + File.separator + name;
			name = name + " (" + path + ")";
		}
		
		//if list doesn't contain this item than add it
		if (!loadedFiles.containsKey(name)) {
			loadedFiles.put(name, path);
			
			filesToLoadList.add(name);
		}
		
		//if added file already contains in FolderFilesList than remove it from FolderFilesList
		if (folderFiles.containsKey(name)) {
			folderFiles.remove(name);
			folderFilesList.remove(name);
		}
		checkLoadButton();
	}
	
	/**
	 * Replaces file from 'Files to Choose' list to 'Files to Load' list
	 * 
	 * @param name name of file
	 */
	
	private void addFileToLoad(String name) {
		String path = folderFiles.get(name);
		folderFiles.remove(name);
		folderFilesList.remove(name);
		
		addFileToLoad(name, path, false);
	}
	
	/**
	 * Replaces file from 'File to Load' list to 'Files to Choose'
	 * 
	 * @param name name of file
	 */
	
	private void removeFileToLoad(String name) {
		String path = loadedFiles.get(name);
		loadedFiles.remove(name);
		filesToLoadList.remove(name);
		
		addFileToChoose(name, path, false);
		
		checkLoadButton();
	}
	
	/**
	 * Checks is it possible to load files. If possible (e.g. 'File to Load' list is not empty)
	 * than enable 'Load' button.
	 * 
	 */
	
	private void checkLoadButton() {
		boolean enabled = filesToLoadList.getItemCount() > 0;
        loadButton.setEnabled(enabled/*&&combo.getText().length()>0*/);
        if (wizardPage!=null){
            wizardPage.setPageComplete(enabled);
        }
	}
	
	/**
	 * Returns Default Directory path for file dialogs in TEMSLoad and NetworkLoad
	 * 
	 * @return default directory
	 */
	
	public static String getDefaultDirectory() {
		if (defaultDirectory == null) {	
			if (LoadNetwork.hasDirectory()) {
				defaultDirectory = LoadNetwork.getDirectory();
			}
		}
		
		return defaultDirectory;
	}
	
	/**
	 * Sets Default Directory path for file dialogs in TEMSLoad and NetworkLoad
	 * 
	 * @param newDirectory new default directory
	 */
	
	public static void setDefaultDirectory(String newDirectory) {
		if (!newDirectory.equals(defaultDirectory)) {
			defaultDirectory = newDirectory;
			LoadNetwork.setDirectory(newDirectory);
		}
	}
	
	/**
	 * Is DefaultDirectory set?
	 *
	 * @return is default directory set?
	 */
	public static boolean hasDefaultDirectory() {
		return defaultDirectory != null;
	}
	
	/**
	 * Job for loading TEMS data
	 * 
	 * @author Lagutko_N
	 *
	 */

	private class LoadTEMSJob extends Job {
		
		private final Display jobDisplay;

		public LoadTEMSJob(Display jobDisplay) {
			super(NeoLoaderPluginMessages.TEMSDialog_MonitorName);
			this.jobDisplay = jobDisplay;					
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			try {
                loadTemsData(jobDisplay, monitor);
                
                return Status.OK_STATUS;
            } catch (Exception e) {
                e.printStackTrace();
                // TODO Handle Exception
                throw (RuntimeException) new RuntimeException( ).initCause( e );
            }
		}

				
	}
}
