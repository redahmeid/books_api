package io.openlight.neo4j.chapters;

import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;

import java.util.Random;

public class ChapterInserter {


    public static String createFirstChapter(String bookid, String chapterText, String writer){
        System.out.print("Book ID: "+bookid+", CHAPTER TEXT: "+chapterText+"\n, WRITER: "+writer);
        Driver driver = GraphDatabase.driver( System.getenv("neo_url"), AuthTokens.basic( System.getenv("neo_user"), System.getenv("neo_password") ) );
        Session session = driver.session();
        String id = new Random().nextInt()+"";
        session.run("CREATE (n:Chapter {id:'"+id+"', text:'"+chapterText+"'})");
        session.run("MERGE (n:User {username: '"+writer+"'})");
        session.run("MATCH (a:Book { id: '"+bookid+"' }), (b:Chapter { id: '"+id+"' }) CREATE (b)-[:PROPOSED_FOR]->(a);");
        session.run("MATCH (a:Chapter { id: '"+id+"' }), (b:User { username: '"+writer+"' }) CREATE (b)-[:WROTE]->(a);");
        session.close();
        driver.close();
        return id;

    }
}
