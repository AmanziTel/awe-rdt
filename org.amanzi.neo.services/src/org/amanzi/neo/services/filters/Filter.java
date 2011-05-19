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

package org.amanzi.neo.services.filters;

import java.io.Serializable;

import org.amanzi.neo.services.DatasetService;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.enums.INodeType;
import org.neo4j.graphdb.Node;


/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author gerzog
 * @since 1.0.0
 */
public class Filter implements IFilter {

    /** long serialVersionUID field */
    private static final long serialVersionUID = -8231241185471872716L;

    private FilterType filterType;

    private ExpressionType expressionType;
    
    private INodeType nodeType;
    
    private String propertyName;
    
    private Serializable value;
    
    private IFilter underlyingFilter;

    public Filter(FilterType filterType, ExpressionType expressionType) {
        this.filterType = filterType;
        this.expressionType = expressionType;
    }

    public Filter(FilterType filterType) {
        this(filterType, ExpressionType.AND);
    }

    public Filter(ExpressionType expressionType) {
        this(FilterType.EQUALS, expressionType);
    }

    public Filter() {
        this(FilterType.EQUALS, ExpressionType.AND);
    }
    
    @Override
    public void setExpression(INodeType nodeType, String propertyName, Serializable value) {
        this.nodeType = nodeType;
        this.propertyName = propertyName;
        this.value = value;
    }
    
    @Override
    public void addFilter(IFilter additionalFilter) {
        this.underlyingFilter = additionalFilter;
    }
    
    @Override
    public boolean check(Node node) {
        boolean result = false;
        
        DatasetService datasetService = NeoServiceFactory.getInstance().getDatasetService();
        INodeType currentType = datasetService.getNodeType(node);
        
        if ((currentType != null) && (nodeType != null) &&
            (!currentType.equals(nodeType))) {
            result = false;
        }
        
        //check is node has this property
        if (node.hasProperty(propertyName)) {
            //get property value
            Object propertyValue = node.getProperty(propertyName);
            
            //compare
            switch (filterType) {
            case EQUALS:
                //two variants:
                //1. filter value is NULL - compare both values with null 
                //2. filter value is not NULL - use equals()
                result = ((value == null) && (propertyValue == null)) ||
                         ((value != null) && value.equals(propertyValue));
                break;
            case LIKE:
                result = ((value == null) && (propertyValue == null)) ||
                         (propertyValue.toString().matches(value.toString()));
                break;
            }
        }
        
        if (underlyingFilter != null) {
            switch (expressionType) {
            case AND:
                result = result && underlyingFilter.check(node);
                break;
            case OR:
                result = result || underlyingFilter.check(node);
                break;
            }
        }    
        
        return result;
    }

}