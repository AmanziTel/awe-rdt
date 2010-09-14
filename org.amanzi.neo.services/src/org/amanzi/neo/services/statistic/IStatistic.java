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

package org.amanzi.neo.services.statistic;

/**
 * <p>
 * Interface for statistic property
 * </p>
 * 
 * @author tsinkel_a
 * @since 1.0.0
 */
public interface IStatistic {
    void save();

    boolean indexValue(String rootKey, String nodeType, String propertyName, Object propertyValue);

    Object parseValue(String rootKey, String nodeType, String key, String value);

    void increaseTypeCount(String rootKey, String nodeType, long count);

    long getTotalCount(String rootKey, String nodeType);
}
