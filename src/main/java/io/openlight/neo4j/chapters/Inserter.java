package io.openlight.neo4j.chapters;

import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;

import java.util.Random;

public class Inserter {


    public static String createFirstChapter(String bookid, String chapterText, String writer){
        Driver driver = GraphDatabase.driver( System.getenv("neo_url"), AuthTokens.basic( System.getenv("neo_user"), System.getenv("neo_password") ) );
        Session session = driver.session();
        String id = bookid + new Random().toString();
        session.run("CREATE (n:Chapter {id:'"+id+"', text:'"+chapterText+"', writer:'"+writer+"'})");
        session.run("MATCH (a:Book { id: '"+bookid+"' }), (b:Chapter { id: '"+id+"' }) CREATE (b)-[:PROPOSED_FOR]->(a);");
        session.close();
        driver.close();
        return id;

    }
}
