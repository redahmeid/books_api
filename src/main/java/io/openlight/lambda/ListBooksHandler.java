package io.openlight.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import io.openlight.MediaTypes;
import io.openlight.domain.Book;
import io.openlight.domain.DomainResponse;
import io.openlight.neo4j.books.BookFinder;
import io.openlight.response.Link;
import io.openlight.response.Links;
import io.openlight.response.Response;
import io.openlight.response.books.BooksResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class ListBooksHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    Gson gson = new Gson();

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {



        Response response = new Response(MediaTypes.BOOKS);

        List<DomainResponse<Book>> books = BookFinder.listBooks();


        List<Response> listOfBooks = books.
                parallelStream().
                map(r -> {
                    Response internalResponse = new Response(MediaTypes.BOOK);
                    internalResponse.self = "http://sandbox.api.openlight.io/books/"+r.id;
                    internalResponse.data = r.data;
                    return internalResponse;
                }).
                collect(Collectors.toList());



        response.data = listOfBooks;

        Link link = new Link();
        link.url = "http://sandbox.api.openlight.io/books";
        link.rel = "start_a_book";



        response.addAction(link);
        String bookJson = gson.toJson(response);

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", response.type.type());

        return new APIGatewayProxyResponseEvent().withBody(bookJson).withHeaders(headers).withStatusCode(200);

    }
}
