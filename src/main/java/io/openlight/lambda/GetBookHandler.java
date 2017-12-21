package io.openlight.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

import com.google.gson.Gson;
import io.openlight.domain.Book;
import io.openlight.domain.User;
import io.openlight.neo4j.books.BookFinder;
import io.openlight.response.books.BookResponse;
import io.openlight.response.Link;
import io.openlight.response.Links;

import java.util.HashMap;
import java.util.Map;


public class GetBookHandler extends AbstractLambda {

    Gson gson = new Gson();


    @Override
    public APIGatewayProxyResponseEvent handle(APIGatewayProxyRequestEvent input, Context context, User user) {
        String book_id = input.getPathParameters().get("bookid");

        BookResponse response = new BookResponse();

        Book book = BookFinder.getById(book_id);

        String editorLink = "http://sandbox.api.openlight.io/users/"+book.editor;
        book.editor = editorLink;




        Link link = new Link();
        link.url = "http://sandbox.api.openlight.io/books/"+book.self+"/chapters";
        link.rel = "propose_a_chapter";

        Links links = new Links();
        links.addLink(link);

        response.addLink(link);

        book.self = "http://sandbox.api.openlight.io/books/"+book.self;
        response.body = book;
        String bookJson = gson.toJson(response);

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        return new APIGatewayProxyResponseEvent().withBody(bookJson).withHeaders(headers).withStatusCode(200);
    }
}
