package io.openlight.neo4j.chapters;

import io.openlight.domain.Chapter;
import org.neo4j.driver.v1.*;

public class ChapterFinder {

    public static Chapter getById(String chapterId){
        Driver driver = GraphDatabase.driver( System.getenv("neo_url"), AuthTokens.basic( System.getenv("neo_user"), System.getenv("neo_password") ) );
        Chapter chapter = null;
        Session session = driver.session();

        StatementResult chapterResult = session.run("MATCH (n:User)-[WROTE]-(b:Chapter{id: '"+chapterId+"' })-[PROPOSED_FOR]-(c:Book) RETURN n.username AS writer, b.text AS text, b.id AS chapter_id,c.id AS book_id");
        while ( chapterResult.hasNext() )
        {
            chapter = new Chapter();
            Record record = chapterResult.next();
            chapter.id = record.get("chapter_id").asString();
            chapter.text = record.get("text").asString();
            chapter.writer = record.get("writer").asString();
            chapter.book = record.get("book_id").asString();

        }



        session.close();
        driver.close();

        return chapter;
    }
}
