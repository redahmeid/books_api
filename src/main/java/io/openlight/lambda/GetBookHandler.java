package io.openlight.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

import com.google.gson.Gson;
import io.openlight.MediaTypes;
import io.openlight.domain.Book;
import io.openlight.domain.Chapter;
import io.openlight.domain.User;
import io.openlight.neo4j.books.BookFinder;
import io.openlight.neo4j.chapters.ChapterFinder;
import io.openlight.response.Response;
import io.openlight.response.Link;
import io.openlight.response.chapters.Story;
import org.apache.http.HttpStatus;

import java.util.*;


public class GetBookHandler extends AbstractLambda {

    Gson gson = new Gson();


    @Override
    public APIGatewayProxyResponseEvent handle(APIGatewayProxyRequestEvent input, Context context, Optional<User> user) {
        long start = new Date().getTime();
        String book_id = input.getPathParameters().get("bookid");

        String baseUrl = "https://sandbox.api.openlight.io/books/"+book_id;
        Optional<Book> book = BookFinder.getById(book_id);

        APIGatewayProxyResponseEvent response = book.map(r -> buildBookResponse(r,baseUrl)).orElseGet(() ->new APIGatewayProxyResponseEvent().withStatusCode(HttpStatus.SC_NOT_FOUND));
        long end = new Date().getTime();
        System.out.println("Time it takes to get book = "+(end-start));
        return response;
    }

    private APIGatewayProxyResponseEvent buildBookResponse(Book book,String baseUrl){

        Response response = new Response(MediaTypes.BOOK);
        io.openlight.response.books.Book bookAPI = new io.openlight.response.books.Book();

        Response<io.openlight.response.users.User> userResponse = new Response<>(MediaTypes.USER);
        io.openlight.response.users.User user = new io.openlight.response.users.User();
        user.username = book.editor;

        userResponse.self = "https://sandbox.api.openlight.io/users/"+book.editor;
        userResponse.data = user;

        bookAPI.editor = userResponse;
        bookAPI.title = book.title;




        book.chapters.ifPresent(r ->
                response.addRelated("next_chapter",baseUrl+"/chapters/"+r.get(0).id));

        book.chapters.map(r -> bookAPI.story = makeEmbeddedChapters(r,baseUrl));


        Link link = new Link();
        link.url = baseUrl+"/chapters";
        link.rel = "propose_a_chapter";
        response.addAction(link.rel,link.url);

        response.self = baseUrl;
        response.data = bookAPI;
        String bookJson = gson.toJson(response);


        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", response.type);
        headers.put("access-control-allow-origin","*");
        return new APIGatewayProxyResponseEvent().withBody(bookJson).withHeaders(headers).withStatusCode(200);
    }



    private Response<Story> makeEmbeddedChapters(List<Chapter> chapters,String baseUrl){

        Story chaptersAPI = new Story();
        Response<Story> chaptersResponse = new Response<>(MediaTypes.CHAPTERS);

        chaptersResponse.self = baseUrl+"/story";

        chaptersAPI.numberOfChapters = chapters.size();

        chaptersResponse.data = chaptersAPI;

        return chaptersResponse;

    }


}
