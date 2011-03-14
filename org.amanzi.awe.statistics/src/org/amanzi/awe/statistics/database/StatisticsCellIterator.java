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

package org.amanzi.awe.statistics.database;

import org.amanzi.awe.statistics.database.entity.StatisticsCell;
import org.amanzi.awe.statistics.database.entity.StatisticsRow;
import org.amanzi.awe.statistics.template.Template;
import org.amanzi.neo.services.INeoConstants;
import org.amanzi.neo.services.enums.NodeTypes;
import org.neo4j.graphdb.Node;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Pechko_E
 * @since 1.0.0
 */
public class StatisticsCellIterator extends AbstractStatisticsIterator<StatisticsCell> {
    private StatisticsRow parent;

    public StatisticsCellIterator(Node node, StatisticsRow parent) {
        super(node, NodeTypes.S_CELL);
        this.parent = parent;
    }

    public StatisticsCell next() {
        Node cellNode = nodeIterator.next();
        return new StatisticsCell(cellNode,parent);
    }
}