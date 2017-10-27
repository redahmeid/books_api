package io.openlight.response;

import io.openlight.domain.Book;
import io.openlight.domain.User;

import java.util.ArrayList;

public class BooksResponse {

    public ArrayList<Book> body;

    public ArrayList<Link> actions;


    public void addLink(Link link){
        if(actions==null) actions = new ArrayList<>();
        actions.add(link);
    }

    public void addBook(Book book){
        if(body==null) body = new ArrayList<>();
        body.add(book);
    }

    public String self;
}