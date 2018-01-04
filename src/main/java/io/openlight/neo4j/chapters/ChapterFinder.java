package io.openlight.neo4j.chapters;

import io.openlight.domain.Chapter;
import io.openlight.domain.DomainResponse;
import org.neo4j.driver.v1.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ChapterFinder {

    public static Chapter getById(String chapterId){
        Driver driver = GraphDatabase.driver( System.getenv("neo_url"), AuthTokens.basic( System.getenv("neo_user"), System.getenv("neo_password") ) );
        Chapter chapter = null;
        Session session = driver.session();

        String cypher = "MATCH (thisChapter:Chapter{id:'"+chapterId+"'}) MATCH path = (book:Book)-[:PROPOSED_NEXT*]->(thisChapter) MATCH (writer:User) - [:WROTE] -> (thisChapter) OPTIONAL MATCH (previousChapter:Chapter) - [:PROPOSED_NEXT] -> (thisChapter) return thisChapter, previousChapter.id as previous, book.id as book, writer.username as writer";
        StatementResult chapterResult = session.run( cypher);
        while ( chapterResult.hasNext() )
        {
            chapter = new Chapter();
            Record record = chapterResult.next();
            chapter.id = record.get("thisChapter").asNode().get("id").asString();
            chapter.book = record.get("book").asString();
            chapter.previous = record.get("previous").isNull()? Optional.ofNullable(null):Optional.ofNullable(record.get("previous").asString());
            chapter.text = record.get("thisChapter").asNode().get("text").asString();
            chapter.writer = record.get("writer").asString();


        }

        session.close();
        driver.close();

        return chapter;
    }

    public static Optional<DomainResponse> getFirstChapterIdForBook(String bookId){
        Driver driver = GraphDatabase.driver( System.getenv("neo_url"), AuthTokens.basic( System.getenv("neo_user"), System.getenv("neo_password") ) );
        DomainResponse response = null;
        Session session = driver.session();

        String cypher = "MATCH (book:Book{id:'"+bookId+"'}) - [:NEXT] -> (chapter) return chapter.id as id";
        StatementResult chapterResult = session.run( cypher);
        while ( chapterResult.hasNext() )
        {
            response = new DomainResponse();
            Record record = chapterResult.next();
            response.id = record.get("id").asString();

        }

        session.close();
        driver.close();

        return Optional.ofNullable(response);
    }

    public static Optional<List<Chapter>> getStoryForBook(String bookId){
        Driver driver = GraphDatabase.driver( System.getenv("neo_url"), AuthTokens.basic( System.getenv("neo_user"), System.getenv("neo_password") ) );
        List<Chapter> list = null;
        Session session = driver.session();

        String cypher = "MATCH (book:Book{id:'"+bookId+"'}) - [:NEXT] -> (chapter) return chapter.id as id";
        StatementResult chapterResult = session.run( cypher);

        list = chapterResult.list().parallelStream()
                .map(r -> makeChapter(r)).collect(Collectors.toList());

        session.close();
        driver.close();

        return Optional.ofNullable(list);
    }

    private static Chapter makeChapter(Record record) {
        Chapter chapter = new Chapter();
        chapter.id = record.get("id").asString();

        return chapter;
    }

/*
    public static DomainResponse<Chapter> getChaptersByBook(String bookId){
        Driver driver = GraphDatabase.driver( System.getenv("neo_url"), AuthTokens.basic( System.getenv("neo_user"), System.getenv("neo_password") ) );
        Chapter chapter = null;
        DomainResponse response = new DomainResponse();
        Session session = driver.session();

        String cypher = "MATCH (thisChapter:Chapter{id:'"+chapterId+"'}) MATCH (previousChapter) - [:PROPOSED_NEXT] -> (thisChapter) MATCH path = (book:Book)-[:PROPOSED_NEXT*]->(thisChapter) MATCH (writer:User) - [:WROTE] -> (thisChapter) return thisChapter, previousChapter.id as previous, book.id as book, writer.username as writer";
        StatementResult chapterResult = session.run( cypher);
        while ( chapterResult.hasNext() )
        {
            chapter = new Chapter();
            Record record = chapterResult.next();
            response.id = record.get("thisChapter").asNode().get("id").asString();
            chapter.previous = record.get("previous").asString();
            chapter.text = record.get("thisChapter").asNode().get("text").asString();
            chapter.writer = record.get("writer").asString();
            chapter.book = record.get("book").asString();
            response.body = chapter;
        }

        session.close();
        driver.close();

        return response;
    }
*/

}
