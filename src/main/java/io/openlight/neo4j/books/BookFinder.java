package io.openlight.neo4j.books;

import io.openlight.domain.Book;
import io.openlight.domain.Chapter;
import org.neo4j.driver.v1.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class BookFinder {

    public static Optional<Book> getById(String book_id){
        long start = new Date().getTime();
        Driver driver = GraphDatabase.driver( System.getenv("neo_url"), AuthTokens.basic( System.getenv("neo_user"), System.getenv("neo_password") ) );
        Book book = null;
        Session session = driver.session();
        StatementResult findEditor = session.run("MATCH (n:User)-[edits]-(b:Book{id: '"+book_id+"' }) OPTIONAL MATCH (book:Book{id:'"+book_id+"'}) - [:NEXT*] -> (chapter) return chapter.id as chapter_id, n.username AS editor, b.title AS title, b.id AS book_id");
        while ( findEditor.hasNext() )
        {
            if(book==null)book = new Book();
            Record record = findEditor.next();
            book.id = record.get("book_id").asString();
            book.title = record.get("title").asString();
            book.editor = record.get("editor").asString();
            if(!record.get("chapter_id").isNull()) book.addChapter(new Chapter().addId(record.get("chapter_id").asString()));

        }

        session.close();
        driver.close();

        long end = new Date().getTime();
        System.out.println("time it take to retrieve the book from NEO4J = "+(end-start));
        return Optional.ofNullable(book);
    }

    public static List<Book> listBooks(){
        long start = new Date().getTime();
        Driver driver = GraphDatabase.driver( System.getenv("neo_url"), AuthTokens.basic( System.getenv("neo_user"), System.getenv("neo_password") ) );
        Book book = null;
        Session session = driver.session();
        StatementResult result = session.run("MATCH (n:Book) RETURN n.id AS id, n.title AS title");

        List<Book> list = result.list().parallelStream().map(r -> makeBook(r)).collect(Collectors.toList());
        long end = new Date().getTime();
        System.out.println("Retrieving a list of books from neo4j took - "+(end-start));
        return list;

    }

    private static Book makeBook(Record record){

        Book book = new Book();
        book.id = record.get("id").asString();
        book.title = record.get("title").asString();

        return book;
    }
}
