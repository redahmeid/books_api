package io.openlight.response.books;

import io.openlight.domain.Book;
import io.openlight.response.Link;

import java.util.ArrayList;
import java.util.List;

public class BooksResponse {

    public List<Book> body;

    public ArrayList<Link> links;


    public void addLink(Link link){
        if(links ==null) links = new ArrayList<>();
        links.add(link);
    }

    public void addBook(Book book){
        if(body==null) body = new ArrayList<>();
        body.add(book);
    }

    public String self;
}