package io.openlight.neo4j.books;

import io.openlight.domain.Book;
import io.openlight.domain.User;
import org.neo4j.driver.v1.*;
import org.neo4j.driver.v1.types.Path;

import java.util.ArrayList;
import java.util.UUID;

public class Finder {

    public static Book getById(String book_id){
        Driver driver = GraphDatabase.driver( System.getenv("neo_url"), AuthTokens.basic( System.getenv("neo_user"), System.getenv("neo_password") ) );
        Book book = null;
        Session session = driver.session();
        StatementResult findEditor = session.run("MATCH (n:User)-[edits]-(b:Book{id: '"+book_id+"' }) RETURN n.username AS editor, b.title AS title, b.id AS book_id");
        while ( findEditor.hasNext() )
        {
            book = new Book();
            Record record = findEditor.next();
            book.self = record.get("book_id").asString();
            book.title = record.get("title").asString();
            book.editor = record.get("editor").asString();

        }

        session.close();
        driver.close();

        return book;
    }

    public static ArrayList<Book> listBooks(){
        Driver driver = GraphDatabase.driver( System.getenv("neo_url"), AuthTokens.basic( System.getenv("neo_user"), System.getenv("neo_password") ) );
        Book book = null;
        Session session = driver.session();
        StatementResult result = session.run("MATCH (n:Book) RETURN n.id AS id, n.title AS title");

        ArrayList<Book> list = new ArrayList<>();

        while ( result.hasNext() )
        {
            book = new Book();
            Record record = result.next();
            book.self = record.get("id").asString();
            book.title = record.get("title").asString();

            list.add(book);

        }



        return list;
    }
}
