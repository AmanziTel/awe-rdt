package org.amanzi.neo.core.database.nodes;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.enums.SplashRelationshipTypes;
import org.neo4j.api.core.Node;

/**
 * Wrapper of Ruby script node
 * 
 * @author Cinkel_A
 * 
 */
public class RubyScriptNode extends AbstractNode {
	private static final String ATTR_NAME = "ATTR_NAME";

	private static final String SCRIPT_TYPE = "ruby_script";

	/**
	 * Constructor for creating a new node in the database representing this script.
	 * @param wrapped node
	 */
	public RubyScriptNode(Node node, String name) {
		super(node, name, SCRIPT_TYPE);
	}

   /**
     * Constructor for wrapping existing script nodes. To reduce API confusion,
     * this constructor is private, and users should use the factory method instead.
     * @param node
     */
    private RubyScriptNode(Node node) {
        super(node);
        if(!getParameter(INeoConstants.PROPERTY_TYPE_NAME).toString().equals(SCRIPT_TYPE)) throw new RuntimeException("Expected existing Ruby Script Node, but got "+node.toString());
    }
    
    /**
     * Use factory method to ensure clear API different to normal constructor.
     *
     * @param node representing an existing Ruby project
     * @return RubyProjectNode from existing Node
     */
    public static RubyScriptNode fromNode(Node node) {
        return new RubyScriptNode(node);
    }


	/**
	 * Returns name of Ruby script
	 * 
	 * @return name of Ruby script
	 */
	public String getName() {
		return (String) getParameter(ATTR_NAME);
	}

	/**
	 * Sets name of Ruby script
	 * 
	 * @param projectName
	 *            name of Ruby project
	 */
	public void setName(String projectName) {
		setParameter(ATTR_NAME, projectName);
	}

	/**
	 * Adds a Cell to Script
	 * 
	 * @param cellNode
	 *            wrapper
	 */
	public void addCell(CellNode cellNode) {
		addRelationship(SplashRelationshipTypes.SCRIPT_CELL, cellNode
				.getUnderlyingNode());
	}
}
