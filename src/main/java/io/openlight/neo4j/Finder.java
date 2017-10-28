package io.openlight.neo4j;

import io.openlight.domain.Book;
import io.openlight.domain.User;
import org.neo4j.driver.v1.*;

import java.util.UUID;

public class Finder {

    public static Book getById(String book_id){
        Driver driver = GraphDatabase.driver( System.getenv("neo_url"), AuthTokens.basic( System.getenv("neo_user"), System.getenv("neo_password") ) );
        Book book = new Book();
        Session session = driver.session();
        StatementResult result = session.run("MATCH (n:Book { id: '"+book_id+"' }) RETURN n.id AS id, n.title AS title, n.editor AS edito");

        while ( result.hasNext() )
        {
            Record record = result.next();
            book.id = record.get("id").asString();
            book.title = record.get("title").asString();
            book.editorUsername = record.get("editor").asString();

        }

        session.close();
        driver.close();

        return book;
    }
}
