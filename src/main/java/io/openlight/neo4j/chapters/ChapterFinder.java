package io.openlight.neo4j.chapters;

import io.openlight.domain.Chapter;
import org.neo4j.driver.v1.*;

public class ChapterFinder {

    public static Chapter getById(String chapterId){
        Driver driver = GraphDatabase.driver( System.getenv("neo_url"), AuthTokens.basic( System.getenv("neo_user"), System.getenv("neo_password") ) );
        Chapter chapter = null;
        Session session = driver.session();

        String cypher = "MATCH (thisChapter:Chapter{id:'"+chapterId+"'}) MATCH (previousChapter) - [:PROPOSED_NEXT] -> (thisChapter) MATCH path = (book:Book)-[:PROPOSED_NEXT*]->(thisChapter) MATCH (writer:User) - [:WROTE] -> (thisChapter) return thisChapter, previousChapter.id as previous, book.id as book, writer.username as writer";
        StatementResult chapterResult = session.run( cypher);
        while ( chapterResult.hasNext() )
        {
            chapter = new Chapter();
            Record record = chapterResult.next();
            chapter.self = record.get("thisChapter").asNode().get("id").asString();
            chapter.previous = record.get("previous").asString();
            chapter.text = record.get("thisChapter").asNode().get("text").asString();
            chapter.writer = record.get("writer").asString();
            chapter.book = record.get("book").asString();
        }



        session.close();
        driver.close();

        return chapter;
    }
}
