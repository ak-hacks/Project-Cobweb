package com.ft.hack.cobweb.dao;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.Index;

public class DBConnectionManager {

	//private static final String DB_PATH = "database/cobweb.db";
	private static final String DB_PATH ="database/cobweb.db";
	private static GraphDatabaseService graphDb;
	private static Index<Node> indexService;
	
	public static GraphDatabaseService getDBService() {
		if (graphDb == null) {
			graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(DB_PATH);
			registerShutdownHook();
		}
		
		return graphDb;
	}
	
    private static void registerShutdownHook()
    {
        // Registers a shutdown hook for the Neo4j and index service instances
        // so that it shuts down nicely when the VM exits (even if you
        // "Ctrl-C" the running example before it's completed)
        Runtime.getRuntime().addShutdownHook( new Thread()
        {
            @Override
            public void run()
            {
                shutdown();
            }
        } );
    }
    
    public static void shutdown()
    {
        DBConnectionManager.getDBService().shutdown();
    } 
}
