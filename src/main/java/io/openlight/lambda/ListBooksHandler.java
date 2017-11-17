package io.openlight.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import io.openlight.domain.Book;
import io.openlight.neo4j.books.Finder;
import io.openlight.response.Link;
import io.openlight.response.Links;
import io.openlight.response.books.BookResponse;
import io.openlight.response.books.BooksResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;


public class ListBooksHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    Gson gson = new Gson();

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {



        BooksResponse response = new BooksResponse();

        ArrayList<Book> books = Finder.listBooks();


        books.
                parallelStream().
                map(book -> book.self = "http://sandbox.api.openlight.io/books/"+book.self).
                collect(Collectors.toList());



        response.body = books;

        Link link = new Link();
        link.url = "http://sandbox.api.openlight.io/books";
        link.rel = "start_a_book";

        Links links = new Links();
        links.addLink(link);

        response.addLink(link);
        String bookJson = gson.toJson(response);

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        return new APIGatewayProxyResponseEvent().withBody(bookJson).withHeaders(headers).withStatusCode(200);

    }
}
