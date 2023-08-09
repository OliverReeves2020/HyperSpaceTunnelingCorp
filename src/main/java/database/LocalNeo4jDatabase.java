package database;

import jakarta.servlet.ServletContext;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.neo4j.dbms.api.DatabaseManagementService;
import org.neo4j.graphalgo.BasicEvaluationContext;
import org.neo4j.graphalgo.GraphAlgoFactory;
import org.neo4j.graphalgo.PathFinder;
import org.neo4j.graphalgo.WeightedPath;
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

    private static Node createNodeWithLabelAndProperties(Transaction transaction, String propertyValue, String alias) {
        Node node = transaction.createNode(Label.label("Accelerator"));
        node.setProperty("name", propertyValue);
        node.setProperty("alias", alias);
        return node;
    }

    private static void createRelationship(Node fromNode, Node toNode, int distance) {
        RelationshipType type = RelationshipType.withName("distance");
        Relationship relationship = fromNode.createRelationshipTo(toNode, type);
        relationship.setProperty("distance", distance);
    }

    public void startup(ServletContext servletContext) {
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
            Node SOL = createNodeWithLabelAndProperties(transaction, "SOL", "Sol");
            Node PRX = createNodeWithLabelAndProperties(transaction, "PRX", "Proxima");
            Node SIR = createNodeWithLabelAndProperties(transaction, "SIR", "Sirius");
            Node CAS = createNodeWithLabelAndProperties(transaction, "CAS", "Castor");
            Node PRO = createNodeWithLabelAndProperties(transaction, "PRO", "Procyon");
            Node DEN = createNodeWithLabelAndProperties(transaction, "DEN", "Denebula");
            Node RAN = createNodeWithLabelAndProperties(transaction, "RAN", "Ran");
            Node ARC = createNodeWithLabelAndProperties(transaction, "ARC", "Arcturus");
            Node FOM = createNodeWithLabelAndProperties(transaction, "FOM", "Fomalhaut");
            Node ALT = createNodeWithLabelAndProperties(transaction, "ALT", "Altair");
            Node VEG = createNodeWithLabelAndProperties(transaction, "VEG", "Vega");
            Node ALD = createNodeWithLabelAndProperties(transaction, "ALD", "Aldermain");
            Node ALS = createNodeWithLabelAndProperties(transaction, "ALS", "Alshain");

            // Relationships
            createRelationship(SOL, RAN, 100);
            createRelationship(SOL, PRX, 90);
            createRelationship(SOL, SIR, 100);
            createRelationship(SOL, ARC, 200);
            createRelationship(SOL, ALD, 250);

            createRelationship(PRX, SOL, 90);
            createRelationship(PRX, SIR, 100);
            createRelationship(PRX, ALT, 150);

            createRelationship(SIR, SOL, 80);
            createRelationship(SIR, PRX, 10);
            createRelationship(SIR, CAS, 200);

            createRelationship(CAS, SIR, 200);
            createRelationship(CAS, PRO, 120);

            createRelationship(PRO, CAS, 80);

            createRelationship(DEN, PRO, 5);
            createRelationship(DEN, ARC, 2);
            createRelationship(DEN, FOM, 8);
            createRelationship(DEN, RAN, 100);
            createRelationship(DEN, ALD, 3);

            createRelationship(RAN, SOL, 100);

            createRelationship(ARC, SOL, 500);
            createRelationship(ARC, DEN, 120);

            createRelationship(FOM, PRX, 10);
            createRelationship(FOM, DEN, 20);
            createRelationship(FOM, ALS, 9);

            createRelationship(ALT, FOM, 140);
            createRelationship(ALT, VEG, 220);

            createRelationship(VEG, ARC, 220);
            createRelationship(VEG, ALD, 580);

            createRelationship(ALD, SOL, 200);
            createRelationship(ALD, ALS, 160);
            createRelationship(ALD, VEG, 320);

            createRelationship(ALS, ALT, 1);
            createRelationship(ALS, ALD, 1);

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


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public String findCheapestPath(ServletContext servletContext, String startNodeName, String endNodeName) throws JSONException {

        // Define the relative path to the data directory inside resources
        GraphDatabaseService graphDb = getGraphDatabaseService(servletContext);
        Transaction transaction = graphDb.beginTx();
        JSONObject result = new JSONObject();

        // check that nodes exist
        Node nodeA = transaction.findNode(Label.label("Accelerator"), "name", startNodeName);
        Node nodeB = transaction.findNode(Label.label("Accelerator"), "name", endNodeName);

        PathFinder<WeightedPath> finder = GraphAlgoFactory.dijkstra(new BasicEvaluationContext(transaction, graphDb), PathExpanders.forTypeAndDirection(RelationshipType.withName("distance"), Direction.OUTGOING), "distance");
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

    public String allNodes(ServletContext servletContext) {
        // Define the label you want to query
        Label labelX = Label.label("Accelerator");

        // Create a list to store the nodes with label "X"
        GraphDatabaseService graphDb = getGraphDatabaseService(servletContext);

        try (Transaction transaction = graphDb.beginTx()) {
            // Find all nodes with the specified label
            ResourceIterator<Node> nodeIterator = transaction.findNodes(labelX);

            // Create a JSON array to store the nodes
            JSONArray nodesJsonArray = new JSONArray();

            // Add the nodes to the list
            List<Node> nodesWithLabelX = new ArrayList<>();
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
            return (resultJson.toString());

        } catch (Exception e) {
            // Handle any exceptions that may occur
            e.printStackTrace();
        }
        // Make sure to close the transaction after processing
        return null;
    }

    public String nodeInfo(ServletContext servletContext, String nodeName) {
        // Define the relative path to the data directory inside resources
        GraphDatabaseService graphDb = getGraphDatabaseService(servletContext);
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

    private GraphDatabaseService getGraphDatabaseService(ServletContext servletContext) {
        String resourcePath = "/data/Solar";

        // Get the real path to the resource directory on the server
        String directoryPath = servletContext.getRealPath(resourcePath);

        if (directoryPath == null) {
            // Handle the case where the resource directory is not found
            throw new RuntimeException("Resource directory not found: " + resourcePath);
        }

        File dataDirectory = new File(directoryPath);

        this.managementService = DatabaseManager.getManagementService(dataDirectory);

        return managementService.database(DEFAULT_DATABASE_NAME);
    }

    public boolean validNodeByName(ServletContext servletContext, String nodeName) {

        // Define the relative path to the data directory inside resources
        GraphDatabaseService graphDb = getGraphDatabaseService(servletContext);

        //memory leak caused by transaction needs to be looked into
        try (Transaction transaction = graphDb.beginTx()) {

            boolean result = (transaction.findNode(Label.label("Accelerator"), "name", nodeName)) != null;
            transaction.close();
            return result;
        }


    }

}
