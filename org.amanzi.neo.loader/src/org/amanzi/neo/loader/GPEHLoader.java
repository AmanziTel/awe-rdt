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

package org.amanzi.neo.loader;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import org.amanzi.awe.l3messages.MessageDecoder;
import org.amanzi.awe.l3messages.rrc.CellMeasuredResults;
import org.amanzi.awe.l3messages.rrc.EventResults;
import org.amanzi.awe.l3messages.rrc.FrequencyInfo;
import org.amanzi.awe.l3messages.rrc.FrequencyInfoFDD;
import org.amanzi.awe.l3messages.rrc.FrequencyInfoTDD;
import org.amanzi.awe.l3messages.rrc.GSM_MeasuredResults;
import org.amanzi.awe.l3messages.rrc.IntegrityCheckInfo;
import org.amanzi.awe.l3messages.rrc.InterFreqCellMeasuredResultsList;
import org.amanzi.awe.l3messages.rrc.InterFreqMeasuredResults;
import org.amanzi.awe.l3messages.rrc.InterFreqMeasuredResultsList;
import org.amanzi.awe.l3messages.rrc.InterRATMeasuredResults;
import org.amanzi.awe.l3messages.rrc.InterRATMeasuredResultsList;
import org.amanzi.awe.l3messages.rrc.IntraFreqMeasuredResultsList;
import org.amanzi.awe.l3messages.rrc.MeasuredResults;
import org.amanzi.awe.l3messages.rrc.MeasuredResultsList;
import org.amanzi.awe.l3messages.rrc.MeasuredResultsOnRACH;
import org.amanzi.awe.l3messages.rrc.MeasurementIdentity;
import org.amanzi.awe.l3messages.rrc.MeasurementReport;
import org.amanzi.awe.l3messages.rrc.UE_InternalMeasuredResults;
import org.amanzi.awe.l3messages.rrc.UL_DCCH_Message;
import org.amanzi.awe.l3messages.rrc.UL_DCCH_MessageType;
import org.amanzi.awe.l3messages.rrc.CellMeasuredResults.ModeSpecificInfoChoiceType.FddSequenceType;
import org.amanzi.awe.l3messages.rrc.FrequencyInfo.ModeSpecificInfoChoiceType;
import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.database.services.events.UpdateViewEventType;
import org.amanzi.neo.core.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.core.enums.NodeTypes;
import org.amanzi.neo.core.enums.OssType;
import org.amanzi.neo.core.enums.gpeh.Events;
import org.amanzi.neo.core.enums.gpeh.Parameters;
import org.amanzi.neo.core.utils.NeoUtils;
import org.amanzi.neo.core.utils.Pair;
import org.amanzi.neo.loader.gpeh.GPEHEvent;
import org.amanzi.neo.loader.gpeh.GPEHMainFile;
import org.amanzi.neo.loader.gpeh.GPEHParser;
import org.amanzi.neo.loader.gpeh.GPEHEvent.Event;
import org.amanzi.neo.loader.internal.NeoLoaderPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;
import org.kc7bfi.jflac.io.BitInputStream;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;


/**
 * <p>
 * GPHEHLoader
 * </p>
 * 
 * @author Cinkel_A
 * @since 1.0.0
 */
public class GPEHLoader extends AbstractLoader {
	
	/**
	 * Prefixes for Property Names of Measurement Report values
	 */
    private static final String GPEH_RRC_MR_BSIC_PREFIX = "MR_BSIC";
	private static final String GPEH_RRC_MR_UE_TX_POWER_PREFIX = "MR_UE-TX-POWER";
	private static final String GPEH_RRC_MR_RSCP_PREFIX = "MR_RSCP";
	private static final String GPEH_RRC_MR_ECNO_PREFIX = "MR_ECNO";
	
	
	/** int KEY_EVENT field */
    private static final int KEY_EVENT = 1;
    private final static Pattern mainFilePattern = Pattern.compile("(^.*)(_Mp0\\.)(.*$)");
    private static final int COUNT_LEN = 1000;
    private Node ossRoot;
    private Pair<Boolean, Node> mainNode;
    private final Map<Integer, Node> cellMap;
    private long timestampOfDay;
    private Node eventLastNode;
    private Node cellRoot;
    private Node lastCellNode;
    private final LinkedHashMap<String, Header> headers;
    private int eventsCount;
    
    /*
     * Index of RSCP Property in Event
     */
    private int rscpIndex;
    
    /*
     * Index of Ec/N0 Property in Event
     */
    private int ecnoIndex;

    /**
     * Constructor
     * @param directory
     * @param datasetName
     * @param display
     */
    public GPEHLoader(String directory, String datasetName, Display display) {
        initialize("GPEH", null, directory, display);
        basename = datasetName;
        headers = getHeaderMap(KEY_EVENT).headers;
        cellRoot = null;
        lastCellNode = null;
        cellMap=new HashMap<Integer,Node>();
    }

    @Override
    protected Node getStoringNode(Integer key) {
        return ossRoot;
    }

    @Override
    public void run(IProgressMonitor monitor) throws IOException {
        if (monitor != null)
            monitor.subTask(basename);
        cellMap.clear();
        addIndex(NodeTypes.GPEH_EVENT.getId(), NeoUtils.getTimeIndexProperty(basename));

        Map<String, List<String>> fileList = getGPEHFile(filename);
        mainTx = neo.beginTx();
        NeoUtils.addTransactionLog(mainTx, Thread.currentThread(), "GPEHLoader");
        try {
            initializeIndexes();
            ossRoot = LoaderUtils.findOrCreateOSSNode(OssType.GPEH, basename, neo);
            Pair<Node,Node> pair= LoaderUtils.findOrCreateGPEHCellRootNode(ossRoot, neo);
            cellRoot=pair.getLeft();
            lastCellNode=pair.getRight();
            if (lastCellNode!=null){
                //TODO use index?
               for (Node cell:NeoUtils.getChildTraverser(cellRoot)){
                   cellMap.put((Integer)cell.getProperty(INeoConstants.PROPERTY_SECTOR_CI,null),cell); 
               }
            }
            eventsCount=0;
            int perc = 0;
            int count=0;
            for (Map.Entry<String, List<String>> entry : fileList.entrySet()) {
//                long len = new File(filename + File.separator + entry.getKey()).length();
                perc+= entry.getValue().size()+1;
            }
            monitor.beginTask("Load GPEH data", perc);
            for (Map.Entry<String, List<String>> entry : fileList.entrySet()) {
                try {
                    String mainFile = entry.getKey();
                    monitor.setTaskName(mainFile);;
                    String rootFile = filename + File.separator + mainFile;
                    GPEHMainFile root = GPEHParser.parseMainFile(new File(rootFile));
                    saveRoot(root);
                    monitor.worked(1);
                    eventLastNode = null;
                    long time;
                    for (String subFile : entry.getValue()) {
                        int cn=0;
                        long timeAll=System.currentTimeMillis();
                        long saveTime=0;
                        long parseTime=0;
                        monitor.setTaskName(subFile);
                        System.out.println(subFile);
                        GPEHEvent result = new GPEHEvent();
                        File file=new File(filename + File.separator + subFile);
                        InputStream in =new FileInputStream(file);
                        if (Pattern.matches("^.+\\.gz$",file.getName())){
                            in= new GZIPInputStream(in); 
                        }
                        BitInputStream input = new BitInputStream(in);
                            try {
                                while (true) {
                                    time=System.currentTimeMillis();
                                    int recordLen = input.readRawUInt(16)-3;
                                    int recordType = input.readRawUInt(8);
                                    if (recordType == 4) {
                                        GPEHParser.parseEvent(input, result,recordLen);
                                        eventsCount++;
                                        cn++;
                                    }else if (recordType == 7) {
                                        GPEHParser.pareseFooter(input, result);
                                    } else if (recordType == 6) {
                                        GPEHParser.pareseError(input, result);
                                    } else {
                                        // wrong file format!
                                        throw new IllegalArgumentException();
                                    }
                                    parseTime+=System.currentTimeMillis()-time;
                                    time=System.currentTimeMillis();
                                    saveEvent(result);
                                    saveTime+=System.currentTimeMillis()-time;
                                    result.clearEvent();
                                    count++;
                                    if (count>COUNT_LEN){
                                        commit(true);
                                        count=0;
                                    }
                                }
                            }catch (EOFException e) {
                                //normal behavior
                            } finally {
                                in.close();
                                monitor.worked(1);
                            }
                            timeAll=System.currentTimeMillis()-timeAll;
                            info(String.format("File %s: saved %s events",subFile,cn));                    
                            info(String.format("\ttotal time\t\t%s\n\t\tparce time\t%s\n\t\tsave time\t%s",timeAll,parseTime,saveTime));   
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    // TODO add more information
                    NeoLoaderPlugin.error(e.getLocalizedMessage());
                }
            }
            commit(true);
            saveProperties();
            finishUpIndexes();
            finishUp();
        } finally {
            commit(false);
        }
    }

@Override
public void printStats(boolean verbose) {
    info("Finished loading " + eventsCount + " events");
}
    /**
     * save event subfile
     * @param eventFile - event file
     */
    private void saveEvent(GPEHEvent eventFile) {
        for (Event event : eventFile.getEvents()) {
            //Lagutko: fake - save only RRC Measurement Reports!!!
//            if (event.getId() == 8) {
                saveSingleEvent(event);
//            }
        }
    }

    /**
     * save event
     * @param event - event
     */
    private void saveSingleEvent(Event event) {
        Transaction tx = neo.beginTx();
        try {
            Node eventNode = neo.createNode();
            NodeTypes.GPEH_EVENT.setNodeType(eventNode, neo);
            setIndexProperty(headers, eventNode, INeoConstants.PROPERTY_NAME_NAME, event.getType().name());
            eventNode.setProperty(INeoConstants.PROPERTY_EVENT_ID, event.getType().getId());
            for (Map.Entry<Parameters, Object> entry : event.getProperties().entrySet()) {
                //LN, 17.04.2010, if we parse a RRC Measurement Report that we should Decode Message
                if (entry.getKey() == Parameters.EVENT_PARAM_MESSAGE_CONTENTS) {
                    if (event.getId() == Events.RRC_MEASUREMENT_REPORT.getId()) {
                        byte[] messageContent = (byte[])entry.getValue();
                        UL_DCCH_Message message = MessageDecoder.getInstance().parseRRCMeasurementReport(messageContent);
                        saveMessageInfo(message, eventNode);
                    }
                }
                else {
                    setIndexProperty(headers, eventNode, entry.getKey().name(), entry.getValue());
                }
            }
            Long timestamp = event.getFullTime(timestampOfDay);
            updateTimestampMinMax(KEY_EVENT, timestamp);
            eventNode.setProperty(INeoConstants.PROPERTY_TIMESTAMP_NAME, timestamp);
            NeoUtils.addChild(mainNode.getRight(), eventNode, eventLastNode, neo);
            tx.success();
            index(eventNode);
            eventLastNode = eventNode;
            createCells(event,eventNode);
        } finally {
            tx.finish();
        }
    }
    
    /**
     * Save parsed result of RRC Measurement Report 
     * 
     * @param message parsed RRC Measurement Report 
     * @param eventNode node of Event
     */
    private void saveMessageInfo(UL_DCCH_Message message, Node eventNode) {
    	if (message == null) {
    		//message is null than stop
    		return;
    	}
    	
    	UL_DCCH_MessageType messageType = message.getMessage();
    	
    	if (messageType == null) {
    		//if message type is null than stop
    		return;
    	}
    	
    	//initialize indexes of fields
    	rscpIndex = 1;
    	ecnoIndex = 1;
    	int bsicIndex = 1;
    	
    	if (messageType.isMeasurementReportSelected()) {
    		//get a MeasurementReport
    		MeasurementReport report = messageType.getMeasurementReport();
    		
    		if (report.isMeasuredResultsPresent()) {
    			//get MeasuredResults
    			MeasuredResults result = report.getMeasuredResults();
    			
    			if (result.isInterFreqMeasuredResultsListSelected()) {
    				//process InterFreq Results
    				InterFreqMeasuredResultsList interFreqResultList = result.getInterFreqMeasuredResultsList();
    				
    				for (InterFreqMeasuredResults singleInterFreqResult : interFreqResultList.getValue()) {
    					if (singleInterFreqResult.isInterFreqCellMeasuredResultsListPresent()) {
    						for (CellMeasuredResults singleCellMeasuredResult : singleInterFreqResult.getInterFreqCellMeasuredResultsList().getValue()) {
    							saveCellMeasuredResults(singleCellMeasuredResult, eventNode);
    						}
    					}
    				}
    			}
    			else if (result.isInterRATMeasuredResultsListSelected()) {
    				//process InterRAT Results
    				InterRATMeasuredResultsList interRATResultList = result.getInterRATMeasuredResultsList();
    				for (InterRATMeasuredResults singleInterRatResults : interRATResultList.getValue()) {
    					if (singleInterRatResults.isGsmSelected()) {
    						for (GSM_MeasuredResults singleGSMResults : singleInterRatResults.getGsm().getValue()) {
    							if (singleGSMResults.getBsicReported().isVerifiedBSICSelected()) {
    								eventNode.setProperty(GPEH_RRC_MR_BSIC_PREFIX + bsicIndex++, singleGSMResults.getBsicReported().getVerifiedBSIC());
    							}
    						}
    					}
    				}
    			}
    			else if (result.isIntraFreqMeasuredResultsListSelected()) {
    				//process IntraFreq Results
    				IntraFreqMeasuredResultsList intraFreqResultList = result.getIntraFreqMeasuredResultsList();
    				for (CellMeasuredResults singleCellMeasuredResults : intraFreqResultList.getValue()) {
    					saveCellMeasuredResults(singleCellMeasuredResults, eventNode);
    				}
    			}
    			else if (result.isUe_InternalMeasuredResultsSelected()) {
    				//process UE Internal Results
    				UE_InternalMeasuredResults ueInternalResults = result.getUe_InternalMeasuredResults();
    				if (ueInternalResults.getModeSpecificInfo().isFddSelected()) {
    					eventNode.setProperty(GPEH_RRC_MR_UE_TX_POWER_PREFIX, ueInternalResults.getModeSpecificInfo().getFdd().getUe_TransmittedPowerFDD().getValue());
    				}
    			}
    			
    		}
    	}
    }
    
    /**
     * Saves CellMeasuredResults to Node
     * 
     * @param results Cell Measured Results to save
     * @param eventNode node of Event
     */
    private void saveCellMeasuredResults(CellMeasuredResults results, Node eventNode) {
    	if (results.getModeSpecificInfo().isFddSelected()) {
    		FddSequenceType fdd = results.getModeSpecificInfo().getFdd();
    		
    		if (fdd.isCpich_RSCPPresent()) {
    			eventNode.setProperty(GPEH_RRC_MR_RSCP_PREFIX + rscpIndex++, fdd.getCpich_RSCP().getValue());    			
    		}
    		if (fdd.isCpich_Ec_N0Present()) {
    			eventNode.setProperty(GPEH_RRC_MR_ECNO_PREFIX + ecnoIndex++, fdd.getCpich_Ec_N0().getValue());
    		}
    	}
    }

    /**
     *Create Cells 
     *finds or create cells and links with event
     * @param event - event 
     * @param eventNode - event node
     */
    private void createCells(Event event, Node eventNode) {
        Transaction tx = neo.beginTx();
        try {
            LinkedHashSet<Integer> cellsId = event.getCellId();
            for (Integer cid : cellsId) {
                Node cell = cellMap.get(cid);
                if (cell == null) {
                    cell = neo.createNode();
                    NodeTypes.GPEH_CELL.setNodeType(cell, neo);
                    cell.setProperty(INeoConstants.PROPERTY_SECTOR_CI, cid);
                    cell.setProperty(INeoConstants.PROPERTY_NAME_NAME, cid.toString());
                    NeoUtils.addChild(cellRoot, cell, lastCellNode, neo);
                    lastCellNode = cell;
                    cellMap.put(cid, cell);
                }
                cell.createRelationshipTo(eventNode, GeoNeoRelationshipTypes.EVENTS);
            }
        } finally {
            tx.finish();
        }
    }

    /**
     * save main file to node
     * 
     * @param root - main file
     */
    private void saveRoot(GPEHMainFile root) {
        mainNode = NeoUtils.findOrCreateChildNode(neo, ossRoot, root.getName());
        GPEHMainFile.Header header = root.getHeader();
        GregorianCalendar cl = new GregorianCalendar(header.getYear(), header.getMonth(), header.getDay());
        timestampOfDay = cl.getTimeInMillis();
        if (mainNode.getLeft()) {
            Transaction tx = neo.beginTx();
            try {
                Node node = mainNode.getRight();
                node.setProperty(INeoConstants.PROPERTY_TYPE_NAME, NodeTypes.OSS_MAIN.getId());
                setProperty(node, INeoConstants.GPEH_FILE_VER, header.getFileVer());
                setProperty(node, INeoConstants.GPEH_DAY, header.getDay());
                setProperty(node, INeoConstants.GPEH_MONTH, header.getMonth());
                setProperty(node, INeoConstants.GPEH_MINUTE, header.getMinute());
                setProperty(node, INeoConstants.GPEH_SECOND, header.getSecond());
                setProperty(node, INeoConstants.GPEH_YEAR, header.getYear());
                setProperty(node, INeoConstants.GPEH_LOGIC_NAME, header.getNeLogicalName());
                setProperty(node, INeoConstants.GPEH_USER_LABEL, header.getNeUserLabel());
                tx.success();
            } finally {
                tx.finish();
            }
        }
    }

    /**
     * Gets map of gpeh files
     * 
     * @param filename - directory
     * @return Map<main file, List of subfiles>
     */
    private Map<String, List<String>> getGPEHFile(String filename) {
        File dir = new File(filename);
        Map<String, List<String>> result = new HashMap<String, List<String>>();
        String[] mainFileList = dir.list(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                return mainFilePattern.matcher(name).matches();
            }
        });
        for (final String mainFile : mainFileList) {
            Matcher matcher = mainFilePattern.matcher(mainFile);
            matcher.find();
            final String mainPart = matcher.group(1);
            List<String> subFiles = new LinkedList<String>();
            for (String file : dir.list(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.startsWith(mainPart) && !name.equals(mainFile);
                };
            })) {
                subFiles.add(file);
            }
            result.put(mainFile, subFiles);
        }
        return result;
    }

    @Override
    protected boolean needParceHeaders() {
        return false;
    }

    @Override
    protected void parseLine(String line) {
    }
    @Override
    protected void finishUp() {
        super.finishUp();
        for (Map.Entry<Integer, Pair<Long, Long>> entry : timeStamp.entrySet()) {
            Node storeNode = getStoringNode(entry.getKey());
            if (storeNode != null) {
                Long minTimeStamp = entry.getValue().getLeft();
                if (minTimeStamp != null) {
                    storeNode.setProperty(INeoConstants.MIN_TIMESTAMP, minTimeStamp);
                }
                Long maxTimeStamp = entry.getValue().getRight();
                if (maxTimeStamp != null) {
                    storeNode.setProperty(INeoConstants.MAX_TIMESTAMP, maxTimeStamp);
                }
            }
        }

        super.cleanupGisNode();//(datasetNode == null ? file : datasetNode);
        sendUpdateEvent(UpdateViewEventType.OSS);
    }


}
