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

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.core.enums.INodeType;
import org.amanzi.neo.core.enums.NetworkRelationshipTypes;
import org.amanzi.neo.core.enums.NodeTypes;
import org.amanzi.neo.core.enums.SplashRelationshipTypes;
import org.amanzi.neo.services.indexes.MultiPropertyIndex;
import org.amanzi.neo.services.indexes.MultiPropertyIndex.MultiDoubleConverter;
import org.apache.commons.lang.StringUtils;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.ReturnableEvaluator;
import org.neo4j.graphdb.StopEvaluator;
import org.neo4j.graphdb.TraversalPosition;
import org.neo4j.graphdb.Traverser;
import org.neo4j.graphdb.Traverser.Order;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.graphdb.traversal.Uniqueness;
import org.neo4j.helpers.Predicate;
import org.neo4j.kernel.Traversal;

/**
 * <p>
 * Utility class
 * </p>
 * 
 * @author tsinkel_a
 * @since 1.0.0
 */
public class Utils {
    /** The Constant TIMESTAMP_INDEX_NAME. */
    private static final String TIMESTAMP_INDEX_NAME = "Index-timestamp-";

    /** The Constant LOCATION_INDEX_NAME. */
    private static final String LOCATION_INDEX_NAME = "Index-location-";

    private Utils() {
        // hide constructor
    }
    
    /**
     * gets name of timestamp index.
     * 
     * @param datasetName - dataset name
     * @return index name
     */
    public static String getTimeIndexName(String datasetName) {
        return TIMESTAMP_INDEX_NAME + datasetName;
    }

    /**
     * gets name of location index.
     * 
     * @param datasetName - dataset name
     * @return index name
     */
    public static String getLocationIndexName(String datasetName) {
        return LOCATION_INDEX_NAME + datasetName;
    }
    /**
     * Gets the number value.
     * 
     * @param <T> the generic type
     * @param klass the klass
     * @param value the value
     * @return the number value
     * @throws SecurityException the security exception
     * @throws NoSuchMethodException the no such method exception
     * @throws IllegalArgumentException the illegal argument exception
     * @throws IllegalAccessException the illegal access exception
     * @throws InvocationTargetException the invocation target exception
     */
    @SuppressWarnings("unchecked")
    public static <T extends Number> T getNumberValue(Class<T> klass, String value) throws SecurityException, NoSuchMethodException, IllegalArgumentException,
            IllegalAccessException, InvocationTargetException {
        if (StringUtils.isEmpty(value)) {
            return null;
        }
        String methodName = klass == Integer.class ? "parseInt" : "parse" + klass.getSimpleName();

        Method metod = klass.getMethod(methodName, String.class);
        return (T)metod.invoke(null, value);
    }
    /**
     * Gets the relations set.
     * 
     * @param from the 'from' node
     * @param to the 'to' node
     * @param relationType the relation type
     * @return the relations
     */
    public static Set<Relationship> getRelations(Node from, Node to, RelationshipType relationType) {
        Set<Relationship> result = new HashSet<Relationship>();
        for (Relationship relation : from.getRelationships(relationType, Direction.OUTGOING)) {
            if (relation.getOtherNode(from).equals(to)) {
                result.add(relation);
            }
        }
        return result;
    }
    /**
     * Gets the neighbour relation.
     * 
     * @param server the server
     * @param neighbour the neighbour
     * @param neighbourName the neighbour name
     * @return the neighbour relation
     */
    public static Relationship getNeighbourRelation(Node server, Node neighbour, String neighbourName) {
            Set<Relationship> allRelations = getRelations(server, neighbour, NetworkRelationshipTypes.NEIGHBOUR);
            for (Relationship relation : allRelations) {
                if (relation.getProperty(INeoConstants.NEIGHBOUR_NAME, "").equals(neighbourName)) {
                    return relation;
                }
            }
            return null;
    }
    /**
     * Gets the root nodes.
     * 
     * @param additionalFilter the additional filter
     * @return the root nodes
     */
    public static TraversalDescription getTDRootNodes(Predicate<Path> additionalFilter) {
        FilterAND filter = new FilterAND();
        filter.addFilter(new Predicate<Path>() {

            @Override
            public boolean accept(Path paramT) {
                if (paramT.length() != 2) {
                    return false;
                }
                return NodeTypes.AWE_PROJECT.checkNode(paramT.lastRelationship().getStartNode());
            }
        });
        filter.addFilter(additionalFilter);
        return Traversal.description().depthFirst().uniqueness(Uniqueness.NONE).prune(Traversal.pruneAfterDepth(2)).filter(filter)
                .relationships(SplashRelationshipTypes.AWE_PROJECT, Direction.OUTGOING).relationships(GeoNeoRelationshipTypes.CHILD, Direction.OUTGOING);
    }

    public static TraversalDescription getTDRootNodesOfProject(final String projectName, Predicate<Path> additionalFilter) {
        FilterAND filter = new FilterAND();
        filter.addFilter(

        new Predicate<Path>() {

            @Override
            public boolean accept(Path item) {
                return projectName.equals(item.lastRelationship().getStartNode().getProperty(INeoConstants.PROPERTY_NAME_NAME, null));
            }
        });
        filter.addFilter(additionalFilter);
        return getTDRootNodes(filter);
    }

    /**
     * Gets the tD project nodes.
     * 
     * @param additionalFilter the additional filter
     * @return the tD project nodes
     */
    public static TraversalDescription getTDProjectNodes(Predicate<Path> additionalFilter) {
        FilterAND filter = new FilterAND();
        filter.addFilter(new Predicate<Path>() {

            @Override
            public boolean accept(Path paramT) {
                return paramT.length() == 1;
            }
        });
        filter.addFilter(additionalFilter);
        return Traversal.description().depthFirst().uniqueness(Uniqueness.NONE).prune(Traversal.pruneAfterDepth(1)).filter(filter)
                .relationships(SplashRelationshipTypes.AWE_PROJECT, Direction.OUTGOING);
    }
    /**
     * <p>
     * Wrapper for set of filters
     * </p>
     * 
     * @author tsinkel_a
     * @since 1.0.0
     */
    public static class FilterAND implements Predicate<Path> {
        public LinkedHashSet<Predicate<Path>> filters = new LinkedHashSet<Predicate<Path>>();

        /**
         * Adds the filter.
         * 
         * @param filter the filter
         */
        public void addFilter(Predicate<Path> filter) {
            if (filter != null) {
                filters.add(filter);
            }
        }

        @Override
        public boolean accept(Path paramT) {
            for (Predicate<Path> filter : filters) {
                if (!filter.accept(paramT)) {
                    return false;
                }
            }
            return true;
        }

    }
    /**
     * Find a parent node of the specified type, following the NEXT relations back up the chain
     * 
     * @param node subnode
     * @return parent node of specified type or null
     */
    public static Node getParentNode(Node node, final String type) {
        Traverser traverse = node.traverse(Order.DEPTH_FIRST, StopEvaluator.END_OF_GRAPH, new ReturnableEvaluator() {

            @Override
            public boolean isReturnableNode(TraversalPosition currentPos) {
                return currentPos.currentNode().getProperty(INeoConstants.PROPERTY_TYPE_NAME, "").equals(type);
            }
        }, NetworkRelationshipTypes.CHILD, Direction.INCOMING);
        return traverse.iterator().hasNext() ? traverse.iterator().next() : null;
    }
    /**
     * Checks if is rooot node.
     * 
     * @param node the node
     * @return true, if is rooot node (OR its VIRTUAL dataset)
     */
    public static boolean isRoootNode(Node node) {
        if (node == null || !node.hasProperty(INeoConstants.PROPERTY_TYPE_NAME) || node.hasRelationship(GeoNeoRelationshipTypes.VIRTUAL_DATASET, Direction.INCOMING)) {
            return false;
        }
        String typeId = (String)node.getProperty(INeoConstants.PROPERTY_TYPE_NAME);
        return NodeTypes.DATASET.getId().equals(typeId) || NodeTypes.NETWORK.getId().equals(typeId) || NodeTypes.OSS.getId().equals(typeId);
    }
    /**
     * gets name of index.
     * 
     * @param basename - gis name
     * @param propertyName - property name
     * @param type - node type
     * @return luciene key
     */
    public static String getLuceneIndexKeyByProperty(Node basename, String propertyName, INodeType type) {
        return new StringBuilder("Id").append(basename.getId()).append("@").append(type.getId()).append("@").append(propertyName).toString();
    }
    /**
     * Get location index.
     * 
     * @param name - dataset name
     * @return location index
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static MultiPropertyIndex<Double> getLocationIndexProperty(String name) throws IOException {
        return new MultiPropertyIndex<Double>(Utils.getLocationIndexName(name), new String[] {INeoConstants.PROPERTY_LAT_NAME, INeoConstants.PROPERTY_LON_NAME},
                new MultiDoubleConverter(0.001), 10);
    }
}