package io.openlight.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import io.openlight.domain.Book;
import io.openlight.domain.User;
import io.openlight.neo4j.Finder;
import io.openlight.response.BookResponse;
import io.openlight.response.Link;
import io.openlight.response.Links;
import io.openlight.response.BooksResponse;

import java.util.HashMap;
import java.util.Map;


public class GetBookHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    Gson gson = new Gson();

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {


        String book_id = input.getPathParameters().get("book_id");
        BookResponse response = new BookResponse();

        Book book = Finder.getById(book_id);
        String editorLink = "http://api.openlight.io/users"+book.editorUsername;
        book.editor = editorLink;
        //response.body = user;
        //response.self = "http://api.openlight.io/users/"+user.username;


        response.body = book;
        Link link = new Link();
        link.url = "http://api.openlight.io/books/"+book.id+"/proposechapter";
        link.rel = "propose_next_chapter";

        Links links = new Links();
        links.addLink(link);

        response.addLink(link);
        String bookJson = gson.toJson(response);

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        return new APIGatewayProxyResponseEvent().withBody(bookJson).withHeaders(headers).withStatusCode(200);
    }
}
