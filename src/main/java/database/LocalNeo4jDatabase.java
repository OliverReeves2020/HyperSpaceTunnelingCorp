package database;

import jakarta.servlet.ServletContext;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.neo4j.dbms.api.DatabaseManagementService;
import org.neo4j.graphalgo.*;
import org.neo4j.graphdb.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.neo4j.configuration.GraphDatabaseSettings.DEFAULT_DATABASE_NAME;

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


        public String findCheapestPath(ServletContext servletContext , String startNodeName, String endNodeName) throws JSONException {


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



            JSONObject result = new JSONObject();

            // check that nodes exist
            Node nodeA = transaction.findNode(Label.label("Accelerator"), "name", startNodeName);
            Node nodeB = transaction.findNode(Label.label("Accelerator"), "name", endNodeName);

            PathFinder<WeightedPath> finder = GraphAlgoFactory.dijkstra(
                    new BasicEvaluationContext(transaction, graphDb),
                    PathExpanders.forTypeAndDirection(RelationshipType.withName("distance"), Direction.OUTGOING),
                    "distance"
            );

            WeightedPath path = finder.findSinglePath(nodeA, nodeB);

            // Extract the node names to create the path array
            JSONArray pathArray = new JSONArray();
            for (Node node : path.nodes()) {
                String nodeName = (String) node.getProperty("name");
                pathArray.put(nodeName);
            }

            // Add the weight and path array to the JSON result
            result.put("weight", path.weight());
            result.put("path", pathArray);

            transaction.close();

            return result.toString();
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


    public String allNodes(ServletContext servletContext){
        // Define the label you want to query
        Label labelX = Label.label("Accelerator");

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

            // Create a JSON array to store the nodes
            JSONArray nodesJsonArray = new JSONArray();

            // Add the nodes to the list
            while (nodeIterator.hasNext()) {
                Node node = nodeIterator.next();
                nodesWithLabelX.add(node);

                // Create a JSON object for each node and add its properties
                JSONObject nodeJson = new JSONObject();
                nodeJson.put("ID", node.getProperty("name").toString());
                nodeJson.put("alias", node.getProperty("alias").toString());

                // Add the node JSON object to the JSON array
                nodesJsonArray.put(nodeJson);
            }

            //close the iterator
            nodeIterator.close();

            JSONObject resultJson = new JSONObject();
            resultJson.put("accelerators", nodesJsonArray);
            //return the JSON object containing the "accelerators" array
            return(resultJson.toString());

        } catch (Exception e) {
            // Handle any exceptions that may occur
            e.printStackTrace();
        } finally {
            // Make sure to close the transaction after processing
            transaction.close();
        }
        return null;
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
// Create a JSON object to store the node information
        JSONObject nodeInfo = new JSONObject();

        try (Transaction transaction = graphDb.beginTx()) {
            Node node = transaction.findNode(Label.label("Accelerator"), "name", nodeName);
            if (node != null) {
                // Access and store information from the node
                String nodeAlias = node.getProperty("alias", "").toString();
                nodeInfo.put("alias", nodeAlias); // Adding alias property to the JSON object

                // Create a JSON array to store the relationships
                JSONArray relationshipsArray = new JSONArray();

                // If you want to find relationships as well, you can traverse them
                Iterable<Relationship> relationships = node.getRelationships();
                for (Relationship relationship : relationships) {
                    Node startNode = relationship.getStartNode();
                    String direction = startNode.equals(node) ? "outgoing" : "incoming";
                    Object relatedNode = relationship.getOtherNode(node).getProperty("name");
                    Object distance = relationship.getProperty("distance");

                    // Create a JSON object to store each relationship details
                    JSONObject relationshipInfo = new JSONObject();
                    relationshipInfo.put("direction", direction);
                    relationshipInfo.put("acceleratorID", relatedNode);
                    relationshipInfo.put("distance", distance);

                    // Add the relationship JSON object to the relationships array
                    relationshipsArray.put(relationshipInfo);
                }

                // Add the relationships array to the node information JSON object
                nodeInfo.put("relationships", relationshipsArray);

                transaction.commit();
            } else {
                // Handle the case where the node with the given property value is not found
                System.out.println("Node with name '" + nodeName + "' not found.");
                transaction.commit();
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // Return the JSON object as a string
        return nodeInfo.toString();

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

            Boolean result = (transaction.findNode(Label.label("Accelerator"), "name", nodeName))==null? false:true;
            transaction.close();
            return result;
        }


    }

    }
