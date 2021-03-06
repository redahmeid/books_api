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
import io.openlight.response.Link;
import io.openlight.response.Response;
import io.openlight.response.chapters.ChapterResponse;
import org.apache.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


public class GetChapterHandler extends AbstractLambda {

    Gson gson = new Gson();


    @Override
    public APIGatewayProxyResponseEvent handle(APIGatewayProxyRequestEvent input, Context context,Optional<User> user) {
        String chapterId = input.getPathParameters().get("chapterid");

        Optional<Chapter> chapter = ChapterFinder.getById(chapterId);

        return chapter.map(r -> buildResponse(r,user)).orElseGet(() ->new APIGatewayProxyResponseEvent().withStatusCode(HttpStatus.SC_NOT_FOUND));
    }

    private APIGatewayProxyResponseEvent buildResponse(Chapter chapter,Optional<User> user){
        Response response = new Response(MediaTypes.CHAPTER);
        ChapterResponse chapterResponse = new ChapterResponse();
        String baseURL = "https://sandbox.api.openlight.io/books/"+chapter.book;

        response.self = baseURL +"/chapters/"+chapter.id;

        Optional<Book> bookOptional = BookFinder.getById(chapter.book);

        bookOptional
                .flatMap(r -> buildSelectLinkIfOK(response.self,r,user))
                .ifPresent(b -> response.addAction(b));


        buildProposeNewChapterLink(chapter,user)
                .ifPresent(b -> response.addAction(b));


        bookOptional
                .map(r -> buildBookResponse(r)).ifPresent(b -> chapterResponse.book = b);

        io.openlight.response.users.User userAPI = new io.openlight.response.users.User();
        Response<io.openlight.response.users.User> userResponse = new Response<>(MediaTypes.USER);
        userAPI.username = chapter.writer;
        userResponse.self = "http://sandbox.api.openlight.io/users/"+chapter.writer;
        userResponse.data = userAPI;
        chapterResponse.writer = userResponse;


        response.addRelated(
                chapter.previous
                        .map(r -> buildPreviousChapterLink(r,baseURL))
                        .orElseGet(() ->buildPreviousBookLink(baseURL) ));


        response.data = chapterResponse;
        String responseJson = gson.toJson(response);

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", response.type);


        return new APIGatewayProxyResponseEvent().withBody(responseJson).withHeaders(headers).withStatusCode(HttpStatus.SC_OK);
    }
    private Response<io.openlight.response.books.Book> buildBookResponse(Book book){
        io.openlight.response.books.Book bookAPI = new io.openlight.response.books.Book();
        bookAPI.title = book.title;
        Response<io.openlight.response.books.Book> bookResponse = new Response<>(MediaTypes.BOOK);
        bookResponse.data = bookAPI;
        bookResponse.self = "http://sandbox.api.openlight.io/books/"+book.id;

        return bookResponse;
    }

    private Optional<Link> buildSelectLinkIfOK(String url, Book book, Optional<User> user){
        Link link = null;

        if(book.editor.equals(user.map(response -> response.username).orElse(null))){
            link = new Link();
            link.url = url+"/select";
            link.rel = "select_this_chapter";
        }

        return Optional.ofNullable(link);
    }

    private Optional<Link> buildProposeNewChapterLink(Chapter chapter,Optional<User> user){

        Link link = null;

        if(user.isPresent()){
            link = new Link();
            link.url = "http://sandbox.api.openlight.io/books/"+chapter.book+"/chapters";
            link.rel = "propose_an_alternative_chapter";
        }


        return Optional.ofNullable(link);
    }

    private Link buildPreviousChapterLink(String previous, String url){
        Link link = new Link();
        link.rel = "previous";
        link.url = url+"/chapters/"+previous;

        return link;
    }

    private Link buildPreviousBookLink(String url){
        Link link = new Link();
        link.rel = "previous";
        link.url = url;

        return link;
    }



}
