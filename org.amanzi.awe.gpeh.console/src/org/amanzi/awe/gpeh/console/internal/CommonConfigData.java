package org.amanzi.awe.gpeh.console.internal;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import org.amanzi.awe.gpeh.console.interfaces.IConfigurationData;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

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

/**
 * <p>
 * Common config data for file loaders
 * </p>
 *
 * @author TsAr
 * @since 1.0.0
 */
public class CommonConfigData implements IConfigurationData {
    
    /** The root. */
    private File root;
    private String charsetName;
    private Character quoteChar;
    /** The root. */
    private List<File> roots;
    
    /** The file to load. */
    private List<File>fileToLoad;
    
    /** The project name. */
    private String projectName;
    
    /** The db root name. */
    private String dbRootName;
    private FileFilter filter;
    private CoordinateReferenceSystem crs=null;
    
    public CoordinateReferenceSystem getCrs() {
        return crs;
    }

    public void setCrs(CoordinateReferenceSystem crs) {
        this.crs = crs;
    }

    public String getCharsetName() {
        return charsetName;
    }

    public void setCharsetName(String charsetName) {
        this.charsetName = charsetName;
    }

    private final Map<String,Object>additionalProperties=new HashMap<String, Object>();
    
    /**
     * Gets the additional properties.
     *
     * @return the additional properties
     */
    public Map<String, Object> getAdditionalProperties() {
        return additionalProperties;
    }

    /**
     * Gets the root.
     *
     * @return the root
     */
    public File getRoot() {
        return root;
    }
    
    /**
     * Gets the filter.
     *
     * @return the filter
     */
    public FileFilter getFilter() {
        if (filter==null){
            return new FileFilter() {
                
                @Override
                public boolean accept(File pathname) {
                    return true;
                }
            };
        }
        return filter;
    }

    /**
     * Sets the filter.
     *
     * @param filter the new filter
     */
    public void setFilter(FileFilter filter) {
        this.filter = filter;
    }

    /**
     * Sets the root.
     *
     * @param root the new root
     */
    public void setRoot(File root) {
        this.root = root;
    }
    
    /**
     * @param roots the roots to set
     */
    public void setMultiRoots(List<File> roots) {
        this.roots = roots;
    }

    /**
     * @return the roots
     */
    public List<File> getMultiRoots() {
        return roots;
    }
    
    /**
     * Gets the file to load.
     *
     * @return the file to load
     */
    public List<File> getFileToLoad() {
        return fileToLoad;
    }
    
    /**
     * Sets the file to load.
     *
     * @param fileToLoad the new file to load
     */
    public void setFileToLoad(Collection<File> fileToLoad) {
        this.fileToLoad = new ArrayList<File>(fileToLoad);
    }
    
    /**
     * Gets the project name.
     *
     * @return the project name
     */
    public String getProjectName() {
        return projectName;
    }
    
    /**
     * Sets the project name.
     *
     * @param projectName the new project name
     */
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
    
    /**
     * Gets the db root name.
     *
     * @return the db root name
     */
    public String getDbRootName() {
        return dbRootName;
    }
    
    /**
     * Sets the db root name.
     *
     * @param dbRootName the new db root name
     */
    public void setDbRootName(String dbRootName) {
        this.dbRootName = dbRootName;
    }


    /**
     * Gets the all loaded files.
     *
     * @return the all loaded files
     */
    public Collection<File> getAllLoadedFiles() {
        Collection<File> result = getFileToLoad();
       if (result!=null){
           return result;
       }
       result=new LinkedHashSet<File>();
       if (getRoot()!=null){
           result.add(getRoot());
       }
       return result;
    }


    public void setQuoteChar(Character quoteChar) {
        this.quoteChar = quoteChar;
    }

    public Character getQuoteChar() {
        return quoteChar;
    }
    

}