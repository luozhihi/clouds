package com.lzh.config;

import com.mongodb.*;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Set;

@Component
public class MongoConfig {
    @Value("${com.lzh.mongodb.host:127.0.0.1}")
    private String host;
    @Value("${com.lzh.mongodb.port:27017}")
    private int port;

    private MongoClient mongoClient;

    @PostConstruct
    public void init() {
        mongoClient = new MongoClient(host, port);
    }

    public void insert(String databaseName,String collectionName, Map<String,Object> param) {
        MongoDatabase database = mongoClient.getDatabase(databaseName);
        MongoCollection<Document> collection = database.getCollection(collectionName);

        Document document = new Document();

        Set<String> set = param.keySet();
        for (String key : set) {
            Object value = param.get(key);
            document.append(key,value);
        }
        collection.insertOne(document);
    }

    public DeleteResult delete(String databaseName,String collectionName,Map<String,Object> queryParam){
        MongoDatabase database = mongoClient.getDatabase(databaseName);
        MongoCollection<Document> collection = database.getCollection(collectionName);

        BasicDBObject query = new BasicDBObject();

        Set<String> keys = queryParam.keySet();
        for(String key:keys){
            query.append(key,queryParam.get(key));
        }

        DeleteResult deleteResult = collection.deleteMany(query);

        return deleteResult;
    }

    public FindIterable<Document> find(String databaseName,String collectionName,Map<String,Object> queryParam){
        MongoDatabase database = mongoClient.getDatabase(databaseName);
        MongoCollection<Document> collection = database.getCollection(collectionName);

        BasicDBObject query = new BasicDBObject();

        Set<String> keys = queryParam.keySet();
        for(String key:keys){
            query.append(key,queryParam.get(key));
        }
        FindIterable<Document> documents = collection.find(query);

        return documents;
    }
    public static void main(String[] args) {
        String host = "111.230.47.161";
        int port = 27017;
        System.out.println(host + ":" + port);
        MongoCredential credential = MongoCredential.createCredential("root", "admin", "root".toCharArray());
        ServerAddress serverAddress = new ServerAddress(host,port);

        MongoClientOptions.Builder builder = new MongoClientOptions.Builder();

        MongoClientOptions mongoClientOptions = builder.build();

        MongoClient mongoClient = new MongoClient(serverAddress,credential,mongoClientOptions);

        MongoDatabase test = mongoClient.getDatabase("test");

        MongoCollection<Document> collections = test.getCollection("test");

        collections.insertOne(new Document().append("root","root"));

        BasicDBObject query = new BasicDBObject();
        query.append("root","root");
        // 调用findOneAndDelete删除一个文档的时候如果存在则删除并返回文档，不存在返回null
        Document oneAndDelete = collections.findOneAndDelete(query);
        // 调用deleteOne删除一个文档的时候，将返回删除的文档数
//        DeleteResult deleteResult = collections.deleteOne(query);
//        System.out.println(oneAndDelete);
        System.out.println(oneAndDelete);
    }
}
