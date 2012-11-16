/**
 * 
 */
package com.ft.hack.cobweb.dao;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.graphdb.traversal.Traverser;
import org.neo4j.kernel.Traversal;
import org.neo4j.kernel.Uniqueness;

/**
 * @author anurag.kapur
 *
 */
public class CobwebDAO {
	
	private static Index<Node> indexService = DBConnectionManager.getDBService().index().forNodes("nodes");
	private static final Logger LOGGER = Logger.getLogger(CobwebDAO.class);
	private static String NAME_KEY = "name";
	private static String TYPE_KEY = "type";
	
	private static enum RelTypes implements RelationshipType
    {
        CONNECTION
    }
	
    public Node getOrCreateNode(String name, String type) {
    	
        Node node = indexService.get(NAME_KEY, name).getSingle();
        
        if (node == null){
        	System.out.println("Will create node :: " + name);
        	
    		node = DBConnectionManager.getDBService().createNode();
            node.setProperty(NAME_KEY, name);
            node.setProperty(TYPE_KEY, type);
            indexService.add(node, NAME_KEY, name);
            
        }
        return node;
    }
    
    public Node getNode(String name) {
    	Node node = indexService.get(NAME_KEY, name).getSingle();
    	return node;
    }
    
    public void connect(String nodeName1, String nodeName2) {
    	Node node1 = getOrCreateNode(nodeName1, null);
    	Node node2 = getOrCreateNode(nodeName2, null);
    	
    	node1.createRelationshipTo(node2, RelTypes.CONNECTION);
    }

    public void connect(String nodeName1, String nodeType1, String nodeName2, String nodeType2) {
    	Node node1 = getOrCreateNode(nodeName1, nodeType1);
    	Node node2 = getOrCreateNode(nodeName2, nodeType2);
    	
    	node1.createRelationshipTo(node2, RelTypes.CONNECTION);
    }
    
    public Traverser getConnections(String name){
    	
    	Node node = getNode(name);
        TraversalDescription td = Traversal.description()
                .breadthFirst()
                .uniqueness(Uniqueness.NODE_GLOBAL)
                .evaluator(Evaluators.toDepth(3))
                .relationships(RelTypes.CONNECTION, Direction.BOTH)
                .evaluator(Evaluators.excludeStartPosition());
        
        Traverser traverser = td.traverse(node);
        return traverser;
    }
    
    public void insertRecords(List<String[]> records) {
    	Transaction tx = DBConnectionManager.getDBService().beginTx();
    	try {
    	
    		for (Iterator iterator = records.iterator(); iterator.hasNext();) {
				String[] record = (String[]) iterator.next();
				connect(record[0], record[1], record[2], record[3]);
			}
    		tx.success();
    	}finally {
    		tx.finish();
    	}
    }
    
    public static void main(String args[]) {
    	CobwebDAO dao = new CobwebDAO();
    	//Transaction tx = DBConnectionManager.getDBService().beginTx();
    	try {
    		/*
	    	dao.connect("Google", "company", "Sergey Brin", "person");
	    	dao.connect("Google", "company", "Larry Page", "person");
	    	dao.connect("Larry Page", "person", "Apple", "company");
	    	dao.connect("Tim Cook", "person", "Apple", "company");
	    	*/
	    	Traverser traverser = dao.getConnections("Larry Page");
	    	String output = "";
	    	for (Path path : traverser)
	        {
	            output += "At depth " + path.length() + " => " + path.endNode().getProperty( "name" ) + "\n";
	        }
	    	System.out.println(output);
	    	Set<Node> nodes = new LinkedHashSet<Node>();
	    	Iterator<Node> nodesIterator = nodes.iterator();
	    	while (nodesIterator.hasNext()) {
				Node node = (Node) nodesIterator.next();
				nodesIterator.remove();
				nodes.add(node);
			}
	    	//tx.success();
    	}finally {
    		//tx.finish();
    	}
    }
}