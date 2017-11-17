package io.openlight.response.books;

import io.openlight.domain.Book;
import io.openlight.response.Link;

import java.util.ArrayList;

public class BookResponse {

    public Book body;

    public ArrayList<Link> links;

    public ArrayList<Book> relatedBooks;

    public void addLink(Link link){
        if(links ==null) links = new ArrayList<>();
        links.add(link);
    }

    public void addBook(Book book){
        if(relatedBooks==null) relatedBooks = new ArrayList<>();
        relatedBooks.add(book);
    }

    public String self;
}