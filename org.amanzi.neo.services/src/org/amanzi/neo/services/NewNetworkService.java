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

package org.amanzi.neo.services;

import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.exceptions.IllegalNodeDataException;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;

/**
 * <p>
 * This class manages access to network elements
 * </p>
 * 
 * @author grigoreva_a
 * @since 1.0.0
 */
public class NewNetworkService extends NewAbstractService {

    private static Logger LOGGER = Logger.getLogger(NewNetworkService.class);

    private Transaction tx;
    private NewDatasetService datasetService;

    /**
     * <p>
     * This enum describes types of netork elements.
     * </p>
     * 
     * @author grigoreva_a
     * @since 1.0.0
     */
    protected enum NetworkElementNodeType implements INodeType {
        NETWORK, BSC, SITE, SECTOR;
        @Override
        public String getId() {
            return name();
        }

    }

    public NewNetworkService() {
        super();
        datasetService = new NewDatasetService();
    }

    public NewNetworkService(GraphDatabaseService graphDb) {
        super(graphDb);
        datasetService = new NewDatasetService(graphDb);
    }

    /**
     * Creates a new network element of defined <code>elementType</code>, sets its <code>name</code>
     * property, adds this element to index <code>indexName</code>, and attaches this element to
     * <code>parent</code> node.
     * 
     * @param parent
     * @param indexName
     * @param name
     * @param elementType
     * @return the newly created network element node
     */
    public Node createNetworkElement(Node parent, String indexName, String name, INodeType elementType)
            throws IllegalNodeDataException, DatabaseException {
        // validate parameters
        if (parent == null) {
            throw new IllegalArgumentException("Parent is null.");
        }
        if ((indexName == null) || (indexName.equals(""))) {
            throw new IllegalArgumentException("indexName is null or empty");
        }
        if ((name == null) || (name.equals(""))) {
            throw new IllegalNodeDataException("Name cannot be empty");
        }

        Node result = createNode(elementType);
        datasetService.addChild(parent, result);
        setNameProperty(result, name);
        addNodeToIndex(result, indexName, NAME, name);

        return result;
    }

    /**
     * Finds a network element by <code>name</code> in index <code>indexNAme</code>
     * 
     * @param indexName name of index, where to look for the element. Call
     *        {@link NewAbstractService#getIndexKey(Node, INodeType)} to generate this name.
     * @param name the value of NAME property to look for
     * @return a network element node or <code>null</code>, if nothing found
     */
    public Node findNetworkElement(String indexName, String name) {
        return null;
    }

    /**
     * Looks for a network element in <code>indexName</code> by <code>name</code>, or creates a new
     * network element and attaches it to <code>parent</code> node, and adds to index
     * 
     * @param parent is used only if the element is not found in index
     * @param indexName name of index
     * @param name the value of indexed NAME property
     * @param elementType is used only if element was not found
     * @return found or created node
     */
    public Node getNetworkElement(Node parent, String indexName, String name, INodeType elementType) {
        return null;
    }

    /**
     * Create a sector node with specified parameters, attaches it with HILD relationship to
     * <code>parent</code>, sets its properties, and adds it to index
     * 
     * @param parent
     * @param indexName
     * @param name the value of NAME property
     * @param ci the value of CELL_INDEX property
     * @param lac the value of LOCATION_AREA_CODE property
     * @return the newly created sector node
     */
    public Node createSector(Node parent, String indexName, String name, String ci, String lac) {
        return null;
    }

    /**
     * Looks for a sector in <code>indexName</code> by the specified parameters. At least one
     * parameter must be not <code>null</code>
     * 
     * @param indexName the nme of index
     * @param name the value of NAME property
     * @param ci the value of CELL_INDEX property
     * @param lac the value of LOCATION_AREA_CODE property
     * @return a sector node or <code>null</code> if nothing was found
     */
    public Node findSector(String indexName, String name, String ci, String lac) {
        return null;
    }

    /**
     * Looks up for a sector by the defined parameters, creates a new one if nothing was found,
     * indexes its properties and attaches it o <code>parent</code>
     * 
     * @param used only if sector was not found
     * @param name the value of NAME property
     * @param ci the value of CELL_INDEX property
     * @param lac the value of LOCATION_AREA_CODE property@param indexName
     * @return found or created sector
     */
    public Node getSector(Node parent, String indexName, String name, String c, String lac) {
        return null;
    }

    /**
     * Traverses database to find all network elements of defined type
     * 
     * @param elementType
     * @return an <code>Iterable</code> over found nodes
     */
    public Iterable<Node> findAllNetworkElements(INodeType elementType) {
        return null;
    }
}