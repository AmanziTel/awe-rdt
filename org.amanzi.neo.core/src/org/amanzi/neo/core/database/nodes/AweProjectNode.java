package org.amanzi.neo.core.database.nodes;

import java.util.Iterator;

import org.amanzi.neo.core.INeoConstants;
import org.amanzi.neo.core.enums.NetworkRelationshipTypes;
import org.amanzi.neo.core.enums.SplashRelationshipTypes;
import org.neo4j.api.core.Direction;
import org.neo4j.api.core.Node;
import org.neo4j.api.core.ReturnableEvaluator;
import org.neo4j.api.core.StopEvaluator;
import org.neo4j.api.core.Traverser;

/**
 * Wrapper of AweProject node
 * 
 * @author Cinkel_A
 * 
 */
public class AweProjectNode extends AbstractNode {

	private static final String AWE_PROJECT_NODE_TYPE = "awe_project";

	public AweProjectNode(Node node, String projectName) {
		super(node);
		setParameter(INeoConstants.PROPERTY_TYPE_NAME, AWE_PROJECT_NODE_TYPE);
		setParameter(INeoConstants.PROPERTY_NAME_NAME, projectName);
	}

    /**
     * Constructor for wrapping existing Awe project nodes. To reduce API confusion,
     * this constructor is private, and users should use the factory method instead.
     * @param node
     */
    private AweProjectNode(Node node) {
        super(node);
        if(!getParameter(INeoConstants.PROPERTY_TYPE_NAME).toString().equals(AWE_PROJECT_NODE_TYPE)) throw new RuntimeException("Expected existing AweProject Node, but got "+node.toString());
    }
    
    /**
     * Use factory method to ensure clear API different to normal constructor.
     *
     * @param node representing an existing Awe project
     * @return AweProjectNode from existing Node
     */
    public static AweProjectNode fromNode(Node node) {
        return new AweProjectNode(node);
    }

	/**
	 * Returns name of Awe project
	 * 
	 * @return name of Awe project
	 */
	public String getName() {
		return (String) getParameter(INeoConstants.PROPERTY_NAME_NAME);
	}

	/**
	 * Sets name of Awe project
	 * 
	 * @param projectName
	 *            name of Awe project
	 */
	public void setName(String projectName) {
		setParameter(INeoConstants.PROPERTY_NAME_NAME, projectName);
	}

	/**
	 * Adds a Ruby Project 
	 *
	 * @param rubyProjectNode  wrapper
	 */ 
	public void addRubyProject(RubyProjectNode rubyProjectNode) {
		addRelationship(SplashRelationshipTypes.RUBY_PROJECT, rubyProjectNode
				.getUnderlyingNode());
	}
	/**
	 * Returns Iterator with all RubyProject
	 * 
	 * @return all RubyProject
	 */
	public Iterator<RubyProjectNode> getAllProjects() {
		return new AllProjectIterator();
	}

	/**
	 * Iterator for searching all AweProjectNode 
	 * 
	 * 
	 */

	private class AllProjectIterator extends AbstractIterator<RubyProjectNode> {

		/**
		 * Constructor. Creates a Traverser that will look for all RubyProject
		 * 
		 */
		public AllProjectIterator() {
			this.iterator = node.traverse(Traverser.Order.BREADTH_FIRST, 
					StopEvaluator.DEPTH_ONE, 
					ReturnableEvaluator.ALL_BUT_START_NODE,
					SplashRelationshipTypes.RUBY_PROJECT,
					Direction.OUTGOING).iterator();
		}

		@Override
		protected RubyProjectNode wrapNode(Node node) {
			return RubyProjectNode.fromNode(node);
		}

	}

    /**
     *Adds a Network to Project
     * 
     * @param network network node
     */
    public void addNetworkNode(Node network) {
        addRelationship(NetworkRelationshipTypes.CHILD, network);
    }

}
