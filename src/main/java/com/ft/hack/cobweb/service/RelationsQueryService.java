package com.ft.hack.cobweb.service;

import com.ft.hack.cobweb.dao.CobwebDAO;
import com.ft.hack.cobweb.domain.Datanode;
import com.ft.hack.cobweb.domain.SearchResult;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.traversal.Traverser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author anurag.kapur
 *
 */
public class RelationsQueryService {

	private static final Logger LOGGER = Logger.getLogger(RelationsQueryService.class);
	private static String NAME_KEY = "name";
	private static String TYPE_KEY = "type";
	
	public List<List> getRelations(String startingNodeName) {
		List<List> results = new ArrayList<List>();
		List<Datanode> dataNodes = new ArrayList<Datanode>();
		List<SearchResult> searchResults = new ArrayList<SearchResult>();
		FTContentSearchService searchService = new FTContentSearchService();
		
		CobwebDAO dao = new CobwebDAO();
		Traverser traverser = dao.getConnections(startingNodeName);
		Iterable<Node> nodesIterable = traverser.nodes();
		
		try {
			
			Iterator<Node> nodesIterator = nodesIterable.iterator();	
			
			//Process starting node
			Node startingNode = dao.getNode(startingNodeName);
			Datanode startingDataNode = new Datanode();
			startingDataNode.setName((String)startingNode.getProperty(NAME_KEY));
			startingDataNode.setType((String)startingNode.getProperty(TYPE_KEY));

            for (Relationship relationship2 : startingNode.getRelationships()) {
                Datanode otherDataNode = new Datanode();

                Relationship relationship = relationship2;
                Node otherNode = relationship.getOtherNode(startingNode);
                String otherNodeName = (String) otherNode.getProperty(NAME_KEY);
                otherDataNode.setName(otherNodeName);
                otherDataNode.setType((String) otherNode.getProperty(TYPE_KEY));
                startingDataNode.addAssociation(otherDataNode);

                List<SearchResult> intermediateSearchResults = new ArrayList<SearchResult>();

                try {
                    intermediateSearchResults = searchService.search(startingNodeName, otherNodeName);
                } catch (Exception e) {
                    LOGGER.error(e);
                }

                for (SearchResult searchResult : intermediateSearchResults) {
                    searchResults.add(searchResult);
                }
                Thread.sleep(100);
            }
			
			dataNodes.add(startingDataNode);
			
			// Process remaining relationships graph
			while (nodesIterator.hasNext()) {
				Datanode dataNode = new Datanode();
				Node node = nodesIterator.next();
				dataNode.setName((String)node.getProperty(NAME_KEY));
				dataNode.setType((String)node.getProperty(TYPE_KEY));

                for (Relationship relationship1 : node.getRelationships()) {
                    Datanode otherDataNode = new Datanode();

                    Relationship relationship = relationship1;
                    Node otherNode = relationship.getOtherNode(node);
                    otherDataNode.setName((String) otherNode.getProperty(NAME_KEY));
                    otherDataNode.setType((String) otherNode.getProperty(TYPE_KEY));
                    dataNode.addAssociation(otherDataNode);
                }
				dataNodes.add(dataNode);
			}
			
			//Create wrapper list
			results.add(dataNodes);
			results.add(searchResults);
		}catch(Exception e) {
			LOGGER.error(e);
		}
		
		return results;
	}
	
	public static void main(String args[]) {
		RelationsQueryService queryService = new RelationsQueryService();
		queryService.getRelations("Larry Page");
	}
}