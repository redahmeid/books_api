package io.openlight.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import io.openlight.MediaTypes;
import io.openlight.domain.Book;
import io.openlight.neo4j.books.BookFinder;
import io.openlight.response.Link;
import io.openlight.response.Response;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class ListBooksHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    Gson gson = new Gson();

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {

        long start = new Date().getTime();

        Response response = new Response(MediaTypes.BOOKS);
        response.self = "https://sandbox.api.openlight.io/books";
        List<Book> books = BookFinder.listBooks();


        List<Response> listOfBooks = books.
                parallelStream().
                map(r -> {
                    Response<io.openlight.response.books.Book> internalResponse = new Response(MediaTypes.BOOK);
                    internalResponse.self = "https://sandbox.api.openlight.io/books/"+r.id;
                    internalResponse.data = new io.openlight.response.books.Book();
                    internalResponse.data.title = r.title;
                    return internalResponse;
                }).
                collect(Collectors.toList());



        response.data = listOfBooks;

        Link link = new Link();
        link.url = "https://sandbox.api.openlight.io/books";
        link.rel = "start_a_book";



        response.addAction(link);
        String bookJson = gson.toJson(response);

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", response.type);
        headers.put("access-control-allow-origin","*");

        long end = new Date().getTime();
        System.out.println("Time it takes to get book = "+(end-start));

        return new APIGatewayProxyResponseEvent().withBody(bookJson).withHeaders(headers).withStatusCode(200);

    }
}
