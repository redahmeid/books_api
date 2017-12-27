package io.openlight.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

import com.google.gson.Gson;
import io.openlight.MediaTypes;
import io.openlight.domain.Book;
import io.openlight.domain.DomainResponse;
import io.openlight.domain.User;
import io.openlight.neo4j.books.BookFinder;
import io.openlight.response.Response;
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

        Response response = new Response();

        DomainResponse domainResponse = BookFinder.getById(book_id);

        Book book = (Book)domainResponse.body;
        String editorLink = "http://sandbox.api.openlight.io/users/"+book.editor;
        book.editor = editorLink;




        Link link = new Link();
        link.url = "http://sandbox.api.openlight.io/books/"+book.self+"/chapters";
        link.rel = "propose_a_chapter";


        response.addAction(link);

        response.self = "http://sandbox.api.openlight.io/books/"+domainResponse.id;
        response.body = book;
        String bookJson = gson.toJson(response);

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", MediaTypes.BOOK.type());

        return new APIGatewayProxyResponseEvent().withBody(bookJson).withHeaders(headers).withStatusCode(200);
    }
}
