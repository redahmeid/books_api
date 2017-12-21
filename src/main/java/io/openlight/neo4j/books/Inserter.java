package io.openlight.neo4j.books;

import org.neo4j.driver.v1.*;

import java.util.UUID;

public class Inserter {


    public static String createBook(String title, String image, String username){
        Driver driver = GraphDatabase.driver( System.getenv("neo_url"), AuthTokens.basic( System.getenv("neo_user"), System.getenv("neo_password") ) );
        Session session = driver.session();
        String id = title.replaceAll("\\W", "").toLowerCase();
        session.run("CREATE (n:Book {id:\""+id+"\", title:\""+title+"\"})");
        session.run("MERGE (n:User {username: '"+username+"'})");
        session.run("MATCH (a:User { username: '"+username+"' }), (b:Book { id: '"+id+"' }) CREATE (a)-[:EDITS]->(b);");
        session.close();
        driver.close();
        return id;

    }


    // MATCH path = (a)-[:CHOSEN*]->(b:Book{id:'thedeadprojectmanager'}) WITH last(nodes(path)) as end CREATE (nextChapter:Chapter{id:'8974983223',text:"I think this isn't good"})-[:PROPOSED_FOR]->(end)

    // match (a:Book{id:"catch22"}) OPTIONAL MATCH path = (a)-[:NEXT*]->(b) WITH  coalesce(last(nodes(path)),a) as begin CREATE (begin)-[:PROPOSED_NEXT]->(nextChapter:Chapter{id:'7767654544656423',text:"I think this is also good"})
}
