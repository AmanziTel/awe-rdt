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

package org.amanzi.awe.statistics.database.entity;

import java.util.Collection;

import org.amanzi.awe.statistics.engine.IAggregationFunction;
import org.amanzi.awe.statistics.engine.IDatasetService;
import org.amanzi.awe.statistics.engine.IStatisticsHeader;
import org.amanzi.awe.statistics.exceptions.CellIsNotEditableException;
import org.amanzi.awe.statistics.exceptions.IncorrectInputException;
import org.amanzi.awe.statistics.functions.AggregationFunctions;
import org.amanzi.awe.statistics.template.TemplateColumn;
import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.enums.GeoNeoRelationshipTypes;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ReturnableEvaluator;
import org.neo4j.graphdb.StopEvaluator;
import org.neo4j.graphdb.Traverser.Order;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Pechko_E
 * @since 1.0.0
 */
public class StatisticsCell {
    private IStatisticsHeader header;
    private IAggregationFunction function;
    private Node node;
    private boolean isReadOnly;

    public StatisticsCell(Node node, TemplateColumn column) {
        this.header = column.getHeader();
        this.function = column.getFunction().newFunction();
        this.node = node;
        this.isReadOnly = false;
    }

    public StatisticsCell(Node node) {
        this.node = node;
        this.isReadOnly = true;
    }

    /**
     * @return Returns the node.
     */
    public Node getNode() {
        return node;
    }

    public String getName() {
        return node.getProperty(INeoConstants.PROPERTY_NAME_NAME).toString();
    }

    public Number getValue() {
        return (Number)node.getProperty(INeoConstants.PROPERTY_VALUE_NAME);
    }

    public void setName(String name) {
        node.setProperty(INeoConstants.PROPERTY_NAME_NAME, name);
    }

    public void setValue(Number value) {
        node.setProperty(INeoConstants.PROPERTY_VALUE_NAME, value);
    }

    public Number update(Node dataNode, IDatasetService service) {
        if (isReadOnly) {
            throw new CellIsNotEditableException();
        }
        Number value = header.calculate(service, dataNode);
        if (value != null || (value == null && function.acceptsNulls())) {
            node.setProperty(INeoConstants.PROPERTY_VALUE_NAME, function.update(value).getResult());
            return value;
        }
        return null;
    }

    public boolean update(Number value) throws CellIsNotEditableException {
        if (isReadOnly) {
            throw new CellIsNotEditableException();
        }
        if (value != null || (value == null && function.acceptsNulls())) {
            node.setProperty(INeoConstants.PROPERTY_VALUE_NAME, function.update(value).getResult());
            return true;
        }
        return false;
    }

    public Collection<Node> getSources() {
        return node.traverse(Order.BREADTH_FIRST, StopEvaluator.DEPTH_ONE, ReturnableEvaluator.ALL_BUT_START_NODE,
                GeoNeoRelationshipTypes.SOURCE, Direction.OUTGOING).getAllNodes();
    }

    public void addSourceNode(Node sourceNode) {
        node.createRelationshipTo(sourceNode, GeoNeoRelationshipTypes.SOURCE);
    }
}
