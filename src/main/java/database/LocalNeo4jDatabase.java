package database;

import jakarta.servlet.ServletContext;
import org.neo4j.configuration.GraphDatabaseSettings;
import org.neo4j.dbms.api.DatabaseManagementService;
import org.neo4j.dbms.api.DatabaseManagementServiceBuilder;
import org.neo4j.graphalgo.*;
import org.neo4j.graphdb.*;

import java.io.File;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.neo4j.configuration.GraphDatabaseSettings.DEFAULT_DATABASE_NAME;
import static org.neo4j.configuration.GraphDatabaseSettings.transaction_monitor_check_interval;

public class LocalNeo4jDatabase {


        private DatabaseManagementService managementService;
        public static void main(String[] args) {


        }

        public void startup(ServletContext servletContext){
            // Define the relative path to the data directory inside resources
            String resourcePath = "/data/Solar";

            // Get the real path to the resource directory on the server
            String directoryPath = servletContext.getRealPath(resourcePath);

            if (directoryPath == null) {
                // Handle the case where the resource directory is not found
                throw new RuntimeException("Resource directory not found: " + resourcePath);
            }

            // Check if the directory exists
            File dataDirectory = new File(directoryPath);
            if (dataDirectory.exists()) {
                // Directory already exists, you can return or perform any other logic
                return;
            }

            // If the directory doesn't exist, create the DatabaseManagementService
            this.managementService = DatabaseManager.getManagementService(dataDirectory);

            GraphDatabaseService graphDb = managementService.database(DEFAULT_DATABASE_NAME);


            try (Transaction transaction = graphDb.beginTx()) {
                // Nodes
                Node SOL = createNodeWithLabelAndProperties(transaction, "Accelerator", "name", "SOL","Sol");
                Node PRX = createNodeWithLabelAndProperties(transaction, "Accelerator", "name", "PRX","Proxima");
                Node SIR = createNodeWithLabelAndProperties(transaction, "Accelerator", "name", "SIR","Sirius");
                Node CAS = createNodeWithLabelAndProperties(transaction, "Accelerator", "name", "CAS","Castor");
                Node PRO = createNodeWithLabelAndProperties(transaction, "Accelerator", "name", "PRO","Procyon");
                Node DEN = createNodeWithLabelAndProperties(transaction, "Accelerator", "name", "DEN","Denebula");
                Node RAN = createNodeWithLabelAndProperties(transaction, "Accelerator", "name", "RAN","Ran");
                Node ARC = createNodeWithLabelAndProperties(transaction, "Accelerator", "name", "ARC","Arcturus");
                Node FOM = createNodeWithLabelAndProperties(transaction, "Accelerator", "name", "FOM","Fomalhaut");
                Node ALT = createNodeWithLabelAndProperties(transaction, "Accelerator", "name", "ALT","Altair");
                Node VEG = createNodeWithLabelAndProperties(transaction, "Accelerator", "name", "VEG","Vega");
                Node ALD = createNodeWithLabelAndProperties(transaction, "Accelerator", "name", "ALD","Aldermain");
                Node ALS = createNodeWithLabelAndProperties(transaction, "Accelerator", "name", "ALS","Alshain");

                // Relationships
                createRelationship(transaction, SOL, RAN, "distance", 100);
                createRelationship(transaction, SOL, PRX, "distance", 90);
                createRelationship(transaction, SOL, SIR, "distance", 100);
                createRelationship(transaction, SOL, ARC, "distance", 200);
                createRelationship(transaction, SOL, ALD, "distance", 250);

                createRelationship(transaction, PRX, SOL, "distance", 90);
                createRelationship(transaction, PRX, SIR, "distance", 100);
                createRelationship(transaction, PRX, ALT, "distance", 150);

                createRelationship(transaction, SIR, SOL, "distance", 80);
                createRelationship(transaction, SIR, PRX, "distance", 10);
                createRelationship(transaction, SIR, CAS, "distance", 200);

                createRelationship(transaction, CAS, SIR, "distance", 200);
                createRelationship(transaction, CAS, PRO, "distance", 120);

                createRelationship(transaction, PRO, CAS, "distance", 80);

                createRelationship(transaction, DEN, PRO, "distance", 5);
                createRelationship(transaction, DEN, ARC, "distance", 2);
                createRelationship(transaction, DEN, FOM, "distance", 8);
                createRelationship(transaction, DEN, RAN, "distance", 100);
                createRelationship(transaction, DEN, ALD, "distance", 3);

                createRelationship(transaction, RAN, SOL, "distance", 100);

                createRelationship(transaction, ARC, SOL, "distance", 500);
                createRelationship(transaction, ARC, DEN, "distance", 120);

                createRelationship(transaction, FOM, PRX, "distance", 10);
                createRelationship(transaction, FOM, DEN, "distance", 20);
                createRelationship(transaction, FOM, ALS, "distance", 9);

                createRelationship(transaction, ALT, FOM, "distance", 140);
                createRelationship(transaction, ALT, VEG, "distance", 220);

                createRelationship(transaction, VEG, ARC, "distance", 220);
                createRelationship(transaction, VEG, ALD, "distance", 580);

                createRelationship(transaction, ALD, SOL, "distance", 200);
                createRelationship(transaction, ALD, ALS, "distance", 160);
                createRelationship(transaction, ALD, VEG, "distance", 320);

                createRelationship(transaction, ALS, ALT, "distance", 1);
                createRelationship(transaction, ALS, ALD, "distance", 1);


                System.out.println("finished");

                Result labelsResult = transaction.execute("CALL db.labels() YIELD label");
                while (labelsResult.hasNext()) {
                    Map<String, Object> labelsRow = labelsResult.next();
                    String label = (String) labelsRow.get("label");
                    String query = "MATCH (n:" + label + ") RETURN '" + label + "' AS label, COUNT(n) AS nodeCount";
                    Result result = transaction.execute(query);
                    if (result.hasNext()) {
                        Map<String, Object> row = result.next();
                        Long nodeCount = (Long) row.get("nodeCount");
                        System.out.println("Number of nodes with label '" + label + "': " + nodeCount);
                    }
                }
                transaction.commit();
                transaction.terminate();


            }catch (Exception e) {
                e.printStackTrace();
            }




        }


        public void findCheapestPath(ServletContext servletContext , String startNodeName,String endNodeName){


            // Define the relative path to the data directory inside resources
            String resourcePath = "/data/Solar";

            // Get the real path to the resource directory on the server
            String directoryPath = servletContext.getRealPath(resourcePath);

            if (directoryPath == null) {
                // Handle the case where the resource directory is not found
                throw new RuntimeException("Resource directory not found: " + resourcePath);
            }

            File dataDirectory = new File(directoryPath);

            this.managementService = DatabaseManager.getManagementService(dataDirectory);

            GraphDatabaseService graphDb = managementService.database(DEFAULT_DATABASE_NAME);


            Transaction transaction = graphDb.beginTx();



            //check that nodes exist

            Node nodeA = findNodeByName(transaction, startNodeName);
            Node nodeB = findNodeByName(transaction, endNodeName);

            PathFinder<WeightedPath> finder = GraphAlgoFactory.dijkstra( new BasicEvaluationContext( transaction, graphDb ),
                    PathExpanders.forTypeAndDirection( RelationshipType.withName("distance"), Direction.OUTGOING), "distance" );

            WeightedPath path = finder.findSinglePath( nodeA, nodeB );

            // Get the weight for the found path
            for (Node node : path.nodes()) {
                String nodeName = (String) node.getProperty("name");


                // Assuming the node has a property called "name" to store its name
                System.out.println(nodeName);
            }
            System.out.println(path.weight());

            transaction.close();
        }
        private static Node createNodeWithLabelAndProperties(Transaction transaction, String label, String propertyKey, String propertyValue, String alias) {
            Node node = transaction.createNode(Label.label(label));
            node.setProperty(propertyKey, propertyValue);
            node.setProperty("alias",alias);
            return node;
        }

        private static Relationship createRelationship(Transaction transaction, Node fromNode, Node toNode, String relationshipType, int distance) {
            RelationshipType type = RelationshipType.withName(relationshipType);
            Relationship relationship = fromNode.createRelationshipTo(toNode, type);
            relationship.setProperty("distance", distance);
            return relationship;
        }



    public static Node findNodeByName(Transaction transaction, String nodeName) {
        Result result = transaction.execute("MATCH (n {name: $nodeName}) RETURN n", Map.of("nodeName", nodeName));
        if (result.hasNext()) {
            Map<String, Object> row = result.next();

            return (Node) row.get("n");
        }
        return null;
    }

    public void allNodes(ServletContext servletContext){
        // Define the label you want to query
        Label labelX = Label.label("X");

// Create a list to store the nodes with label "X"
        List<Node> nodesWithLabelX = new ArrayList<>();
        String resourcePath = "/data/Solar";

        // Get the real path to the resource directory on the server
        String directoryPath = servletContext.getRealPath(resourcePath);

        if (directoryPath == null) {
            // Handle the case where the resource directory is not found
            throw new RuntimeException("Resource directory not found: " + resourcePath);
        }

        File dataDirectory = new File(directoryPath);

        this.managementService = DatabaseManager.getManagementService(dataDirectory);

        GraphDatabaseService graphDb = managementService.database(DEFAULT_DATABASE_NAME);
        Transaction transaction = graphDb.beginTx();

        try {
            // Find all nodes with the specified label
            ResourceIterator<Node> nodeIterator = transaction.findNodes(labelX);

            // Add the nodes to the list
            while (nodeIterator.hasNext()) {
                Node node = nodeIterator.next();
                nodesWithLabelX.add(node);
            }

            // Make sure to close the iterator
            nodeIterator.close();

            // Process the nodes or return the list as needed
            // For example, you can iterate through the list and perform actions on each node:
            for (Node node : nodesWithLabelX) {
                String nodeName = node.getProperty("name").toString();
                // Access other properties or perform actions with the node as needed
                // ...
                System.out.println("Node with label 'X' and name '" + nodeName + "' found.");
            }

        } catch (Exception e) {
            // Handle any exceptions that may occur during the transaction
            e.printStackTrace();
        } finally {
            // Make sure to close the transaction after processing
            transaction.close();
        }
    }

    public String nodeInfo(ServletContext servletContext, String nodeName){
        // Define the relative path to the data directory inside resources
        String resourcePath = "/data/Solar";

        // Get the real path to the resource directory on the server
        String directoryPath = servletContext.getRealPath(resourcePath);

        if (directoryPath == null) {
            // Handle the case where the resource directory is not found
            throw new RuntimeException("Resource directory not found: " + resourcePath);
        }

        File dataDirectory = new File(directoryPath);

        this.managementService = DatabaseManager.getManagementService(dataDirectory);

        GraphDatabaseService graphDb = managementService.database(DEFAULT_DATABASE_NAME);
        try (Transaction transaction = graphDb.beginTx()) {
            Node node = transaction.findNode(Label.label("Accelerator"), "name", nodeName);
            if (node != null) {
                // Access and store information from the node
                String nodeAlias = node.getProperty("alias", "").toString();
                System.out.println(nodeAlias);
                // If you want to find relationships as well, you can traverse them
                Iterable<Relationship> relationships = node.getRelationships();
                for (Relationship relationship : relationships) {
                    Node startNode = relationship.getStartNode();
                    String direction = startNode.equals(node) ? "out" : "in";
                    Object relatedNode = relationship.getOtherNode(node).getProperty("name");
                    String relationshipType = relationship.getType().name();
                    Object distance =relationship.getProperty("distance");
                    System.out.println(direction+" "+relatedNode+" "+relationshipType+" "+distance);
                }
                transaction.commit();
            } else {
                // Handle the case where the node with the given property value is not found
                System.out.println("Node with name '" + nodeName + "' not found.");
                transaction.commit();
            }
        }

        return "S";

    }
    public boolean validNodeByName(ServletContext servletContext, String nodeName) {

        // Define the relative path to the data directory inside resources
        String resourcePath = "/data/Solar";

        // Get the real path to the resource directory on the server
        String directoryPath = servletContext.getRealPath(resourcePath);

        if (directoryPath == null) {
            // Handle the case where the resource directory is not found
            throw new RuntimeException("Resource directory not found: " + resourcePath);
        }

        File dataDirectory = new File(directoryPath);

        this.managementService = DatabaseManager.getManagementService(dataDirectory);

        GraphDatabaseService graphDb = managementService.database(DEFAULT_DATABASE_NAME);

        //memory leak caused by transaction needs to be looked into
        try (Transaction transaction = graphDb.beginTx()) {
            Result result = transaction.execute("MATCH (n {name: $nodeName}) RETURN n", Map.of("nodeName", nodeName));
            transaction.close();
            return result.hasNext();
        }

    }

    }
