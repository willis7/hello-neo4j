package neo4j

import groovy.util.logging.Slf4j
import org.neo4j.graphdb.Direction
import org.neo4j.graphdb.GraphDatabaseService
import org.neo4j.graphdb.Node
import org.neo4j.graphdb.Relationship
import org.neo4j.graphdb.RelationshipType
import org.neo4j.graphdb.Transaction
import org.neo4j.graphdb.factory.GraphDatabaseFactory

/**
 * Created by willis7 on 21/02/15.
 */
@Slf4j
class HelloWorld {
    public static final String Neo4j_DBPath = "/usr/local/Cellar/neo4j/2.1.7"

    Node first
    Node second
    Relationship relation
    GraphDatabaseService graphDB

    // list of relationships
    private static enum RelTypes implements RelationshipType {
        KNOWS
    }

    public static void main(String[] args) {
        HelloWorld hello = new HelloWorld()

        hello.createDatabase()
        hello.removeData()
        hello.shutdown()
    }

    /**
     * Create and populate the database
     */
    void createDatabase() {
        // GraphDatabaseService
        graphDB = new GraphDatabaseFactory().newEmbeddedDatabase(Neo4j_DBPath)

        // Start transaction
        Transaction transaction = graphDB.beginTx()

        try {
            // Create Node & set the properties
            first = graphDB.createNode()
            first.setProperty("name", "Sion Williams")

            second = graphDB.createNode()
            second.setProperty("name", "Jane Doe")

            // Specify the relationship
            relation = first.createRelationshipTo(second, RelTypes.KNOWS)
            relation.setProperty("relationship-type", "knows")

            log.info first.getProperty("name").toString()
            log.info relation.getProperty("relationship-type").toString()
            log.info second.getProperty("name").toString()

            // Complete successful transaction
            transaction.success()

        } finally {
            // Finish transaction
            transaction.finish()
        }
    }

    /**
     * Clean the database
     */
    void removeData() {
        Transaction transaction = graphDB.beginTx()

        try {
            // Delete
            first.getSingleRelationship(RelTypes.KNOWS, Direction.OUTGOING).delete()

            // Delete Nodes
            first.delete()
            second.delete()
            log.info "Nodes are removed"

            // Successful transaction
            transaction.success()
        } finally {
            // Finish transaction
            transaction.finish()
        }
    }

    void shutdown() {
        // Shutdown database
        graphDB.shutdown()
        log.info "Database is shutdown"
    }
}
