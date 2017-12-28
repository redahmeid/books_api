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
import io.openlight.neo4j.chapters.ChapterFinder;
import io.openlight.response.Response;
import io.openlight.response.books.BookResponse;
import io.openlight.response.Link;
import io.openlight.response.Links;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


public class GetBookHandler extends AbstractLambda {

    Gson gson = new Gson();


    @Override
    public APIGatewayProxyResponseEvent handle(APIGatewayProxyRequestEvent input, Context context, User user) {
        String book_id = input.getPathParameters().get("bookid");

        Optional<DomainResponse<Book>> domainResponse = BookFinder.getById(book_id);


        return domainResponse.map(r -> buildBookResponse(r)).orElseGet(() ->new APIGatewayProxyResponseEvent().withStatusCode(HttpStatus.NOT_FOUND.value()));
    }

    private APIGatewayProxyResponseEvent buildBookResponse(DomainResponse<Book> domainResponse){

        Response response = new Response();

        Book book = domainResponse.data;
        String editorLink = "https://sandbox.api.openlight.io/users/"+book.editor;
        book.editor = editorLink;

        Optional<DomainResponse> firstChapterResponse = ChapterFinder.getFirstChapterIdForBook(domainResponse.id);
        firstChapterResponse.ifPresent(r ->
                response.addRelated("next_chapter","https://sandbox.api.openlight.io/books/"+domainResponse.id+"/chapters/"+r.id));


        Link link = new Link();
        link.url = "http://sandbox.api.openlight.io/books/"+domainResponse.id+"/chapters";
        link.rel = "propose_a_chapter";
        response.addAction(link.rel,link.url);

        response.self = "http://sandbox.api.openlight.io/books/"+domainResponse.id;
        response.data = book;
        String bookJson = gson.toJson(response);


        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", MediaTypes.BOOK.type());

        return new APIGatewayProxyResponseEvent().withBody(bookJson).withHeaders(headers).withStatusCode(200);
    }


}
