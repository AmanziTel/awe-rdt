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

package org.amanzi.awe.filters;

import org.amanzi.neo.core.enums.NodeTypes;
import org.amanzi.neo.core.utils.NeoUtils;
import org.neo4j.api.core.NeoService;
import org.neo4j.api.core.Node;
import org.neo4j.api.core.Transaction;
import org.neo4j.api.core.Traverser;

/**
 * <p>
 * Abstract filter
 * </p>
 * 
 * @author Tsinkel_A
 * @since 1.0.0
 */
public abstract class AbstractFilter {
    protected final Node node;
    protected String name;
    protected NodeTypes type;
    protected NeoService service;

    public static AbstractFilter getInstance(Node node, NeoService service) {
        Transaction tx = NeoUtils.beginTx(service);
        try {
            NodeTypes nodeType = NodeTypes.getNodeType(node, service);
            switch (nodeType) {
            case FILTER:
                return new Filter(node, service);
            case FILTER_GROUP:
                return new GroupFilter(node, service);
            case FILTER_CHAIN:
                return new FilterChain(node, service);
            default:
                return null;
            }
        } finally {
            NeoUtils.finishTx(tx);
        }
    }

    /**
     * Constructor
     * 
     * @param node - filter node
     * @param service - NeoService
     */
    protected AbstractFilter(Node node, NeoService service) {
        this.node = node;
        this.service = service;
        name = NeoUtils.getSimpleNodeName(node, "", service);
    }

    /**
     * @param service The service to set.
     */
    public void setService(NeoService service) {
        this.service = service;
    }

//    /**
//     * is group filter?
//     * 
//     * @return
//     */
//    public boolean isGroup() {
//        return type == NodeTypes.FILTER_GROUP;
//    }

    /**
     * get result of filtering
     * 
     * @param node - node to filter
     * @return FilterResult
     */
    public abstract FilterResult filterNode(Node node);

    /**
     * get result of filtering
     * 
     * @param value value to filter
     * @return FilterResult
     */
    public abstract FilterResult filterValue(Object value);

    /**
     * get result of filtering
     * 
     * @param traverser traverser to filter - if one of traversed node is valid the result is valid
     * @return FilterResult
     */
    public abstract FilterResult filterNodesByTraverser(Traverser traverser);

}