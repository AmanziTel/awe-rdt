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
package org.amanzi.neo.loader.ui.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.project.ui.internal.actions.ZoomToLayer;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.NeoCorePlugin;
import org.amanzi.neo.core.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.core.enums.NetworkFileType;
import org.amanzi.neo.core.enums.NodeTypes;
import org.amanzi.neo.core.enums.OssType;
import org.amanzi.neo.core.service.NeoServiceProvider;
import org.amanzi.neo.core.utils.ActionUtil;
import org.amanzi.neo.core.utils.ActionUtil.RunnableWithResult;
import org.amanzi.neo.core.utils.CSVParser;
import org.amanzi.neo.core.utils.NeoUtils;
import org.amanzi.neo.core.utils.Pair;
import org.amanzi.neo.loader.core.preferences.DataLoadPreferences;
import org.amanzi.neo.loader.ui.NeoLoaderPlugin;
import org.amanzi.neo.loader.ui.NeoLoaderPluginMessages;
import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.hsqldb.lib.StringUtil;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

public class LoaderUtils {
    /**
     * return AWE project name of active map
     * 
     * @return
     */
    public static String getAweProjectName() {
        IMap map = ApplicationGIS.getActiveMap();
        return map == ApplicationGIS.NO_MAP ? ApplicationGIS.getActiveProject().getName() : map.getProject().getName();
    }

    /**
     * Convert dBm values to milliwatts
     * 
     * @param dbm
     * @return milliwatts
     */
    public static final double dbm2mw(int dbm) {
        return Math.pow(10.0, ((dbm) / 10.0));
    }

    /**
     * Convert milliwatss values to dBm
     * 
     * @param milliwatts
     * @return dBm
     */
    public static final float mw2dbm(double mw) {
        return (float)(10.0 * Math.log10(mw));
    }

    /**
     * get type of network files
     * 
     * @param fileName file name
     * @return Pair<NetworkFiles, Exception> : <NetworkFiles if file was correctly parsed, else
     *         null,Exception if exception appears else null>
     */
    public static Pair<NetworkFileType, Exception> getFileType(String fileName) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
            String line;
            if (getFileExtension(fileName).equalsIgnoreCase(".xml")){
                int c=0;
                while ((line = reader.readLine()) != null && c<5) {
                    if (line.contains("configData")){
                        reader.close();
                        return new Pair<NetworkFileType, Exception>(NetworkFileType.UTRAN, null);
                    }
                };
                reader.close();
                return new Pair<NetworkFileType, Exception>(NetworkFileType.NOKIA_TOPOLOGY, null);
            }
            while ((line = reader.readLine()) != null && line.length() < 2) {
                // find header
            };
            reader.close();
            if (line == null) {
                return new Pair<NetworkFileType, Exception>(null, null);
            }
            int maxMatch = 0;
            String[] possibleFieldSepRegexes = new String[] {"\t", ",", ";"};
            String fieldSepRegex = "\t";
            for (String regex : possibleFieldSepRegexes) {
                String[] fields = line.split(regex);
                if (fields.length > maxMatch) {
                    maxMatch = fields.length;
                    fieldSepRegex = regex;
                }
            }
            CSVParser parser = new CSVParser(fieldSepRegex.charAt(0));
            List<String> headers = parser.parse(line);
            for (String header : getPossibleHeaders(DataLoadPreferences.PR_NAME)) {
                if (headers.contains(header)) {
                    return new Pair<NetworkFileType, Exception>(NetworkFileType.PROBE, null);
                }
            }
            for (String header : getPossibleHeaders(DataLoadPreferences.NE_ADJ_BTS)) {
                if (headers.contains(header)) {
                    return new Pair<NetworkFileType, Exception>(NetworkFileType.NEIGHBOUR, null);
                }
            }
            for (String header : getPossibleHeaders(DataLoadPreferences.NH_SECTOR)) {
                if (headers.contains(header)) {
                    return new Pair<NetworkFileType, Exception>(NetworkFileType.RADIO_SECTOR, null);
                }
            }
            for (String header : getPossibleHeaders(DataLoadPreferences.NH_SITE)) {
                if (headers.contains(header)) {
                    return new Pair<NetworkFileType, Exception>(NetworkFileType.RADIO_SITE, null);
                }
            }
            List<String>possibleHeaders=new LinkedList<String>();
            possibleHeaders.addAll(Arrays.asList(getPossibleHeaders(DataLoadPreferences.TR_SITE_ID_SERV)));
            possibleHeaders.addAll(Arrays.asList(getPossibleHeaders(DataLoadPreferences.TR_SITE_NO_SERV)));
            possibleHeaders.addAll(Arrays.asList(getPossibleHeaders(DataLoadPreferences.TR_ITEM_NAME_SERV)));
            possibleHeaders.addAll(Arrays.asList(getPossibleHeaders(DataLoadPreferences.TR_SITE_ID_NEIB)));
            possibleHeaders.addAll(Arrays.asList(getPossibleHeaders(DataLoadPreferences.TR_SITE_NO_NEIB)));
            possibleHeaders.addAll(Arrays.asList(getPossibleHeaders(DataLoadPreferences.TR_ITEM_NAME_NEIB)));
            for (String header : possibleHeaders) {
                if (headers.contains(header)) {
                    return new Pair<NetworkFileType, Exception>(NetworkFileType.TRANSMISSION, null);
                }
            }
            return new Pair<NetworkFileType, Exception>(null, null);
        } catch (Exception e) {
            return new Pair<NetworkFileType, Exception>(null, e);
        }
    }

    /**
     * @param key -key of value from preference store
     * @return array of possible headers
     */
    public static String[] getPossibleHeaders(String key) {
        String text = NeoLoaderPlugin.getDefault().getPreferenceStore().getString(key);
        if (text == null) {
            return new String[0];
        }
        String[] array = text.split(",");
        List<String> result = new ArrayList<String>();
        for (String string : array) {
            String value = string.trim();
            if (!value.isEmpty()) {
                result.add(value);
            }
        }
        return result.toArray(new String[0]);
    }

    /**
     * Confirm load network on map
     * 
     * @param map map
     * @param fileName name of loaded file
     * @return true or false
     */
    public static boolean confirmAddToMap(final IMap map, final String fileName) {
    
        final IPreferenceStore preferenceStore = NeoLoaderPlugin.getDefault().getPreferenceStore();
        return (Integer)ActionUtil.getInstance().runTaskWithResult(new RunnableWithResult<Integer>() {
            int result;
    
            @Override
            public void run() {
                boolean boolean1 = preferenceStore.getBoolean(DataLoadPreferences.ZOOM_TO_LAYER);
                String message = String.format(NeoLoaderPluginMessages.ADD_LAYER_MESSAGE, fileName, map.getName());
                if (map == ApplicationGIS.NO_MAP) {
                    message = String.format(NeoLoaderPluginMessages.ADD_NEW_MAP_MESSAGE, fileName);
                }
                // MessageBox msg = new
                // MessageBox(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                // SWT.YES | SWT.NO);
                // msg.setText(NeoLoaderPluginMessages.ADD_LAYER_TITLE);
                // msg.setMessage(message);
                MessageDialogWithToggle dialog = MessageDialogWithToggle.openYesNoQuestion(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                        NeoLoaderPluginMessages.ADD_LAYER_TITLE, message, NeoLoaderPluginMessages.TOGLE_MESSAGE, boolean1, preferenceStore,
                        DataLoadPreferences.ZOOM_TO_LAYER);
                result = dialog.getReturnCode();
                if (result == IDialogConstants.YES_ID) {
                    preferenceStore.putValue(DataLoadPreferences.ZOOM_TO_LAYER, String.valueOf(dialog.getToggleState()));
                }
            }
    
            @Override
            public Integer getValue() {
                return result;
            }
        }) == IDialogConstants.YES_ID;
    }

    /**
     * @param firstDataset
     */
    public static void addGisNodeToMap(String dataName, Node... gisNodes) {
        try {
            IService curService = getMapService();
            IMap map = ApplicationGIS.getActiveMap();
            if (confirmAddToMap(map, dataName)) {
                List<ILayer> layerList = new ArrayList<ILayer>();
                List<IGeoResource> listGeoRes = new ArrayList<IGeoResource>();
                for (Node gis : gisNodes) {
                    map = ApplicationGIS.getActiveMap();
                    IGeoResource iGeoResource = getResourceForGis(curService, map, gis);
                    if (iGeoResource!=null){
                        listGeoRes.add(iGeoResource);
                    }
                }
                layerList.addAll(ApplicationGIS.addLayersToMap(map, listGeoRes, 0));
    
                IPreferenceStore preferenceStore = NeoLoaderPlugin.getDefault().getPreferenceStore();
                if (preferenceStore.getBoolean(DataLoadPreferences.ZOOM_TO_LAYER)) {
                    LoaderUtils.zoomToLayer(layerList);
                }
            }
        } catch (Exception e) {
            NeoCorePlugin.error(null, e);
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
    
    }

    public static IService getMapService() throws MalformedURLException {
        String databaseLocation = NeoServiceProvider.getProvider().getDefaultDatabaseLocation();
        URL url = new URL("file://" + databaseLocation);
        IService curService = CatalogPlugin.getDefault().getLocalCatalog().getById(IService.class, url, null);
        return curService;
    }

    /**
     * Zoom To 1st layers in list
     * 
     * @param layers list of layers
     */
    public static void zoomToLayer(final List< ? extends ILayer> layers) {
        ActionUtil.getInstance().runTask(new Runnable() {
            @Override
            public void run() {
                ZoomToLayer zoomCommand = new ZoomToLayer();
                zoomCommand.selectionChanged(null, new StructuredSelection(layers));
                zoomCommand.runWithEvent(null, null);
            }
        }, true);
    }
    
    public static boolean confirmAddAmsToMap(final IMap map, final String fileName) {
        final IPreferenceStore preferenceStore = NeoLoaderPlugin.getDefault().getPreferenceStore();
        return (Integer)ActionUtil.getInstance().runTaskWithResult(new RunnableWithResult<Integer>() {
            int result;
    
            @Override
            public void run() {
               String message = String.format(NeoLoaderPluginMessages.ADD_LAYER_MESSAGE, fileName, map.getName());
                if (map == ApplicationGIS.NO_MAP) {
                    message = String.format(NeoLoaderPluginMessages.ADD_NEW_MAP_MESSAGE, fileName);
                }
                AmsMessageDialog dialog = AmsMessageDialog.openYesNoQuestion(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                                                                             NeoLoaderPluginMessages.ADD_LAYER_TITLE, message, preferenceStore);
                result = dialog.getReturnCode();
                if (result == IDialogConstants.OK_ID) {
                    preferenceStore.putValue(DataLoadPreferences.ZOOM_TO_LAYER, String.valueOf(dialog.getToggleState(DataLoadPreferences.ZOOM_TO_LAYER)));
                    preferenceStore.putValue(DataLoadPreferences.ADD_AMS_PROBES_TO_MAP, String.valueOf(dialog.getToggleState(DataLoadPreferences.ADD_AMS_PROBES_TO_MAP)));
                    preferenceStore.putValue(DataLoadPreferences.ADD_AMS_EVENTS_TO_MAP, String.valueOf(dialog.getToggleState(DataLoadPreferences.ADD_AMS_EVENTS_TO_MAP)));
                    preferenceStore.putValue(DataLoadPreferences.ADD_AMS_CALLS_TO_MAP, String.valueOf(dialog.getToggleState(DataLoadPreferences.ADD_AMS_CALLS_TO_MAP)));
                }
            }
    
            @Override
            public Integer getValue() {
                return result;
            }
        }) == IDialogConstants.OK_ID;
    }
    
    public static void addAmsGisNodesToMap(String dataName, Node probeGis,Node eventsGis, Node callGis) {
        try {
            IService curService = getMapService();
            IMap map = ApplicationGIS.getActiveMap();
            if(confirmAddAmsToMap(map, dataName)){
                List<ILayer> layerList = new ArrayList<ILayer>();
                List<IGeoResource> listGeoRes = new ArrayList<IGeoResource>();
                IPreferenceStore preferenceStore = NeoLoaderPlugin.getDefault().getPreferenceStore();
                if (preferenceStore.getBoolean(DataLoadPreferences.ADD_AMS_PROBES_TO_MAP)) {
                    map = ApplicationGIS.getActiveMap();
                    IGeoResource iGeoResource = getResourceForGis(curService, map, probeGis);
                    if (iGeoResource!=null){
                        listGeoRes.add(iGeoResource);
                    }
                }
                if (preferenceStore.getBoolean(DataLoadPreferences.ADD_AMS_EVENTS_TO_MAP)) {
                    map = ApplicationGIS.getActiveMap();
                    IGeoResource iGeoResource = getResourceForGis(curService, map, eventsGis);
                    if (iGeoResource!=null){
                        listGeoRes.add(iGeoResource);
                    }
                }
                if (preferenceStore.getBoolean(DataLoadPreferences.ADD_AMS_CALLS_TO_MAP)) {
                    map = ApplicationGIS.getActiveMap();
                    IGeoResource iGeoResource = getResourceForGis(curService, map, callGis);
                    if (iGeoResource!=null){
                        listGeoRes.add(iGeoResource);
                    }
                }
                layerList.addAll(ApplicationGIS.addLayersToMap(map, listGeoRes, 0));
                if (preferenceStore.getBoolean(DataLoadPreferences.ZOOM_TO_LAYER)) {
                    LoaderUtils.zoomToLayer(layerList);
                }
            }
        } catch (Exception e) {
            NeoCorePlugin.error(null, e);
            throw (RuntimeException)new RuntimeException().initCause(e);
        }
        
    }
    
    public static IGeoResource getResourceForGis(IService service, IMap map, Node gis) throws IOException{
        if (service != null && findLayerByNode(map, gis) == null) {
            for (IGeoResource iGeoResource : service.resources(null)) {
                if (iGeoResource.canResolve(Node.class)) {
                    if (iGeoResource.resolve(Node.class, null).equals(gis)) {
                        return iGeoResource;
                    }
                }
            }
        }
        return null;
    }
    /**
     * Returns Default Directory path for file dialogs in DriveLoad and NetworkLoad
     * 
     * @return default directory
     */
    
    public static String getDefaultDirectory() {
        return NeoLoaderPlugin.getDefault().getPluginPreferences().getString(DataLoadPreferences.DEFAULT_DIRRECTORY_LOADER);
    }
    
    /**
     * Sets Default Directory path for file dialogs in DriveLoad and NetworkLoad
     * 
     * @param newDirectory new default directory
     */
    
    public static void setDefaultDirectory(String newDirectory) {
        NeoLoaderPlugin.getDefault().getPluginPreferences().setValue(DataLoadPreferences.DEFAULT_DIRRECTORY_LOADER, newDirectory);
    }
    public static ILayer findLayerByNode(IMap map, Node gisNode) {
        try {
            for (ILayer layer : map.getMapLayers()) {
                IGeoResource resource = layer.findGeoResource(Node.class);
                if (resource != null && resource.resolve(Node.class, null).equals(gisNode)) {
                    return layer;
                }
            }
            return null;
        } catch (IOException e) {
            // TODO Handle IOException
            e.printStackTrace();
            return null;
        }
    }  
    /**
     * Calculates list of files 
     *
     * @param directoryName directory to import
     * @param filter - filter (if filter teturn true for directory this directory will be handled also  )
     * @return list of files to import
     */
    public static List<File> getAllFiles(String directoryName, FileFilter filter) {
        File directory = new File(directoryName);
        return getAllFiles(directory,filter);
    }
    /**
     * Calculates list of files 
     *
     * @param directory -  directory to import
     * @param filter - filter (if filter teturn true for directory this directory will be handled also  )
     * @return list of files to import
     */
    public static List<File> getAllFiles(File directory, FileFilter filter) {
        LinkedList<File> result = new LinkedList<File>();
        for (File childFile : directory.listFiles(filter)) {
            if (childFile.isDirectory()) {
                result.addAll(getAllFiles(childFile,filter));
            }
            else  {
                result.add(childFile);
            }
        }
        return result;
    }
    /**
     *find or create OSS node
     * 
     * @return
     */
    public static Node findOrCreateOSSNode(OssType ossType,String ossName,GraphDatabaseService neo) {
        Node oss;
        Transaction tx = neo.beginTx();
        try {
            oss = NeoUtils.findRootNodeByName(ossName, neo);
            if (oss == null) {
                oss = neo.createNode();
                oss.setProperty(INeoConstants.PROPERTY_TYPE_NAME, NodeTypes.OSS.getId());
                oss.setProperty(INeoConstants.PROPERTY_NAME_NAME, ossName);
                ossType.setOssType(oss, neo);
                String aweProjectName = LoaderUtils.getAweProjectName();
                NeoCorePlugin.getDefault().getProjectService().addDataNodeToProject(aweProjectName, oss);
                //TODO remove this relation!
                neo.getReferenceNode().createRelationshipTo(oss, GeoNeoRelationshipTypes.CHILD);
            }
            assert NodeTypes.OSS.checkNode(oss);
            tx.success();
        } finally {
            tx.finish();
        }
        return oss;
    }

    /**
     *find or create Cell root node
     * @param ossRoot oss_gpeh root node
     * @param neo neoservice
     * @return Pair<cell_root node, last child or null if no child found>
     */
    public static Pair<Node, Node> findOrCreateGPEHCellRootNode(Node ossRoot, GraphDatabaseService neo) {
        Transaction tx = neo.beginTx();
        try {
            Relationship relation = ossRoot.getSingleRelationship(GeoNeoRelationshipTypes.CELLS, Direction.OUTGOING);
            Node cellNode;
            if (relation!=null){
                cellNode=relation.getOtherNode(ossRoot); 
            }else{
                cellNode=neo.createNode();
                //TODO check name
                cellNode.setProperty(INeoConstants.PROPERTY_NAME_NAME, "CELL ROOT");
                NodeTypes.GPEH_CELL_ROOT.setNodeType(cellNode, neo);
                ossRoot.createRelationshipTo(cellNode,GeoNeoRelationshipTypes.CELLS);
            }
            Node child=NeoUtils.findLastChild(cellNode, neo);
            tx.success();
            return new Pair<Node,Node>(cellNode,child);
        } finally {
            tx.finish();
        }
    }
    /**
     * get file extension
     *
     * @param fileName - file name
     * @return file extension
     */
    public static String getFileExtension(String fileName) {
        int idx = fileName.lastIndexOf(".");
        return idx < 1 ? "" : fileName.substring(idx);
    }



    public static File getFirstFile(String dirName) {
        File file = new File(dirName);
        if (file.isFile()){
            return file;
        }
        File[] list = file.listFiles();
        if (list.length>0){
            return list[0];
        }else{
            //TODO optimize
          List<File> all = getAllFiles(dirName, new FileFilter() {
                
                @Override
                public boolean accept(File pathname) {
                    return true;
                }
            });
          if (all.isEmpty()){
              return null;
          }else{
              return all.iterator().next();
          }
        }
    }
    
    /**
     * Gets the selected nodes.
     *
     * @param service the service
     * @return the selected nodes
     */
    public static LinkedHashSet<Node>getSelectedNodes(GraphDatabaseService service){
        LinkedHashSet<Node> selectedNode = new LinkedHashSet<Node>();
        String storedId = NeoLoaderPlugin.getDefault().getPreferenceStore().getString(DataLoadPreferences.SELECTED_DATA);
        if (!StringUtil.isEmpty(storedId)) {
            Transaction tx = service.beginTx();
            try {
                StringTokenizer st = new StringTokenizer(storedId, DataLoadPreferences.CRS_DELIMETERS);
                while (st.hasMoreTokens()) {
                    String nodeId = st.nextToken();
                    try {
                        Node node = service.getNodeById(Long.parseLong(nodeId));
                        if (NeoUtils.isRoootNode(node)) {
                            selectedNode.add(node);
                        }
                    } catch (Exception e) {
                        Logger.getLogger(LoaderUtils.class).error("not loaded id " + nodeId, e);
                    }
                    
                }
            } finally {
                tx.finish();
            }
        }
        return selectedNode;
    }
    
    /**
     * Store selected nodes.
     *
     * @param selectedNodes the selected nodes
     */
    public static void storeSelectedNodes(Set<Node>selectedNodes){
        StringBuilder st = new StringBuilder();
        for (Node selNode : selectedNodes) {
            st.append(DataLoadPreferences.CRS_DELIMETERS).append(selNode.getId());
        }
        String value = st.length() < 1 ? "" : st.substring(DataLoadPreferences.CRS_DELIMETERS.length());
        NeoLoaderPlugin.getDefault().getPreferenceStore().setValue(DataLoadPreferences.SELECTED_DATA, value);
    }
    
    private static class AmsMessageDialog extends MessageDialog{
        
        private Map<String, Boolean> toggles;
        private Map<String, String> messages;
        private IPreferenceStore prefStore = null;

        /**
         * Constructor.
         * @param parentShell
         * @param dialogTitle
         * @param dialogTitleImage
         * @param dialogMessage
         * @param dialogImageType
         * @param dialogButtonLabels
         * @param defaultIndex
         */
        public AmsMessageDialog(Shell parentShell, String dialogTitle, Image dialogTitleImage, String dialogMessage,
                int dialogImageType, String[] dialogButtonLabels, int defaultIndex, IPreferenceStore store) {
            super(parentShell, dialogTitle, dialogTitleImage, dialogMessage, dialogImageType, dialogButtonLabels, defaultIndex);
            prefStore = store;
            toggles = new HashMap<String, Boolean>();
            messages = new HashMap<String, String>();
            toggles.put(DataLoadPreferences.ADD_AMS_PROBES_TO_MAP, prefStore.getBoolean(DataLoadPreferences.ADD_AMS_PROBES_TO_MAP));
            messages.put(DataLoadPreferences.ADD_AMS_PROBES_TO_MAP, NeoLoaderPluginMessages.TOGLE_MESSAGE_ADD_PROBE);
            
            toggles.put(DataLoadPreferences.ADD_AMS_CALLS_TO_MAP, prefStore.getBoolean(DataLoadPreferences.ADD_AMS_CALLS_TO_MAP));
            messages.put(DataLoadPreferences.ADD_AMS_CALLS_TO_MAP, NeoLoaderPluginMessages.TOGLE_MESSAGE_ADD_CALLS);
            
            toggles.put(DataLoadPreferences.ADD_AMS_EVENTS_TO_MAP, prefStore.getBoolean(DataLoadPreferences.ADD_AMS_EVENTS_TO_MAP));
            messages.put(DataLoadPreferences.ADD_AMS_EVENTS_TO_MAP, NeoLoaderPluginMessages.TOGLE_MESSAGE_ADD_EVENTS);
            
            toggles.put(DataLoadPreferences.ZOOM_TO_LAYER, prefStore.getBoolean(DataLoadPreferences.ZOOM_TO_LAYER));
            messages.put(DataLoadPreferences.ZOOM_TO_LAYER, NeoLoaderPluginMessages.TOGLE_MESSAGE);
        }
        
        public static AmsMessageDialog openYesNoQuestion(Shell parent,
                String title, String message, IPreferenceStore store) {
            AmsMessageDialog dialog = new AmsMessageDialog(parent,
                    title, null, message, QUESTION, new String[] { IDialogConstants.YES_LABEL,
                            IDialogConstants.NO_LABEL }, 0, store);
            dialog.prefStore = store;            
            dialog.open();
            return dialog;
        }
        
        protected Control createDialogArea(Composite parent) {
            Composite dialogArea = (Composite) super.createDialogArea(parent);
            addToggleButton(dialogArea, DataLoadPreferences.ADD_AMS_PROBES_TO_MAP);
            addToggleButton(dialogArea, DataLoadPreferences.ADD_AMS_CALLS_TO_MAP);
            addToggleButton(dialogArea, DataLoadPreferences.ADD_AMS_EVENTS_TO_MAP);
            GridData data = new GridData(SWT.NONE);
            data.horizontalSpan = 2;
            Label empty = new Label(dialogArea, SWT.NONE);
            empty.setLayoutData(data);
            addToggleButton(dialogArea, DataLoadPreferences.ZOOM_TO_LAYER);
            return dialogArea;
        }
        
        protected void addToggleButton(Composite parent,final String key) {
            final Button button = new Button(parent, SWT.CHECK | SWT.LEFT);

            GridData data = new GridData(SWT.NONE);
            data.horizontalSpan = 2;
            button.setLayoutData(data);
            button.setFont(parent.getFont());

            button.addSelectionListener(new SelectionAdapter() {

                public void widgetSelected(SelectionEvent e) {
                    toggles.put(key, button.getSelection());
                }

            });
            
            button.setText(messages.get(key));
            button.setSelection(toggles.get(key));

        }
        
        public boolean getToggleState(String key){
            return toggles.get(key);
        }
        
    }
    
}