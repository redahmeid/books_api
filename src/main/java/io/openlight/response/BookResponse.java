package io.openlight.response;

import io.openlight.domain.Book;

import java.util.ArrayList;

public class BookResponse {

    public Book body;

    public ArrayList<Link> actions;

    public ArrayList<Book> relatedBooks;

    public void addLink(Link link){
        if(actions==null) actions = new ArrayList<>();
        actions.add(link);
    }

    public void addBook(Book book){
        if(relatedBooks==null) relatedBooks = new ArrayList<>();
        relatedBooks.add(book);
    }

    public String self;
}