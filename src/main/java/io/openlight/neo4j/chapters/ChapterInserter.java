package io.openlight.neo4j.chapters;

import io.openlight.domain.Chapter;
import org.neo4j.driver.v1.*;

import java.util.Random;

public class ChapterInserter {


    public static String proposeAChapter(String bookid, String chapterText, String writer){
        Driver driver = GraphDatabase.driver( System.getenv("neo_url"), AuthTokens.basic( System.getenv("neo_user"), System.getenv("neo_password") ) );
        Session session = driver.session();
        String id = "";

        StatementResult numberOfChapters = session.run("match (b:Book{id:'"+bookid+"'}) - [:PROPOSED_NEXT*] -> (c:Chapter) return count(c) as total");

        while ( numberOfChapters.hasNext() ) {

            Record record = numberOfChapters.next();
            id = bookid+"-"+record.get("total").asInt();
        }

        session.run("MERGE (n:User {username: '"+writer+"'})");

        // propose the chapter
        session.run("match (a:Book{id:'"+bookid+"'}) OPTIONAL MATCH path = (a)-[:NEXT*]->(b) WITH  coalesce(last(nodes(path)),a) as begin MATCH (writer:User{username:'"+writer+"'}) CREATE (begin)-[:PROPOSED_NEXT]->(nextChapter:Chapter{id:'"+id+"',text:\""+chapterText+"\"}) MERGE (writer)-[:WROTE]->(nextChapter)");

        session.close();
        driver.close();
        return id;
    }



    public static void selectChapter(String chapterId){
        Driver driver = GraphDatabase.driver( System.getenv("neo_url"), AuthTokens.basic( System.getenv("neo_user"), System.getenv("neo_password") ) );
        Session session = driver.session();

        session.run("MATCH (previous) - [:PROPOSED_NEXT] -> (next:Chapter{id:'"+chapterId+"'}) CREATE (previous) - [:NEXT] -> (next)");

        session.close();
        driver.close();
    }

}
