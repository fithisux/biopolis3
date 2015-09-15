/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package biopolis.headless;

/**
 *
 * @author vanag
 */
import biopolis.exceptions.system.BiopolisGeneralException;
import biopolis.headless.BiopolisGraphManagement;
import org.neo4j.jdbc.Neo4jConnection;
import com.mongodb.*;
import java.net.*;
import java.util.Properties;
import java.sql.*;
import java.io.*;

public class BiopolisPersistencyLayer {

    public static final String dbName = "biopolisdb";
    public static final String collectionNamePlaces = "biopolisplaces";
    public static final String indexName = "geospatialIdx";
    public static final String collectionNameSearches = "biopolisearches";

    private Neo4jConnection connNeo4j = null;
    private MongoClient mongo = null;
    private DBCollection dbcolSearches = null;
    private DBCollection dbcolPlaces = null;
    private int segSZ;

    public BiopolisPersistencyLayer(BiopolisProperties props) throws BiopolisGeneralException, SQLException {
        org.neo4j.jdbc.Driver driver = new org.neo4j.jdbc.Driver();
        this.connNeo4j = driver.connect("jdbc:neo4j://" + props.neo4j_endpoint, new Properties());
        try {
            System.out.println(props.mongo_host);
            MongoClientOptions mco = new MongoClientOptions.Builder()
                    .connectionsPerHost(10)
                    .threadsAllowedToBlockForConnectionMultiplier(10)
                    .build();
            mongo = new MongoClient(new ServerAddress("localhost"), mco); //addresses is a pre-populated List 
        } catch (MongoException e) {
            e.printStackTrace();
            throw new BiopolisGeneralException("Mongo exception problem");
        } catch (UnknownHostException e) {
            e.printStackTrace();
            throw new BiopolisGeneralException("Mongo connection problem");
        }
        DB dbo = mongo.getDB(BiopolisPersistencyLayer.dbName);
        if (dbo.collectionExists(BiopolisPersistencyLayer.collectionNamePlaces)) {
            this.dbcolPlaces = dbo.getCollection(BiopolisPersistencyLayer.collectionNamePlaces);

        } else {
            this.dbcolPlaces = dbo.createCollection(BiopolisPersistencyLayer.collectionNamePlaces, null);
            this.dbcolPlaces.createIndex(new BasicDBObject("loc", "2dsphere"));
        }

        if (dbo.collectionExists(collectionNameSearches)) {
            this.dbcolSearches = dbo.getCollection(collectionNameSearches);

        } else {
            this.dbcolSearches = dbo.createCollection(collectionNameSearches, null);
            this.dbcolSearches.createIndex(new BasicDBObject("biopolisttl", 1),
                    new BasicDBObject("expireAfterSeconds", props.expire_search));
        }
        this.segSZ = props.segment_size;
    }

    public Neo4jConnection getConnNeo4j() {
        return connNeo4j;
    }

    public DBCollection getDbcolSearches() {
        return dbcolSearches;
    }

    public DBCollection getDbcolPlaces() {
        return dbcolPlaces;
    }

    public int getSegSZ() {
        return segSZ;
    }

    public void close() {
        this.mongo.close();
        try {
            this.connNeo4j.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void putJPEGAtLeoFS(byte[] jpeg, String objectName) throws IOException {
        File f=new File("/mnt/leofs/"+objectName);
        java.nio.file.Files.write(f.toPath(),jpeg);        
    }

    public static byte[] getJPEGAtLeoFS(String objectName) throws  IOException {
        File f=new File("/mnt/leofs/"+objectName);
        return java.nio.file.Files.readAllBytes(f.toPath());        
    }
    
    public static boolean deleteJPEGAtLeoFS(String objectName)  {
        File f=new File("/mnt/leofs/"+objectName);
        return f.delete();        
    }

}
