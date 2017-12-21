package io.openlight.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import io.openlight.domain.Chapter;
import io.openlight.domain.User;
import io.openlight.neo4j.books.BookFinder;
import io.openlight.neo4j.chapters.ChapterFinder;
import io.openlight.response.Link;
import io.openlight.response.Links;
import io.openlight.response.chapters.ChapterResponse;

import java.util.HashMap;
import java.util.Map;


public class GetChapterHandler extends AbstractLambda {

    Gson gson = new Gson();


    @Override
    public APIGatewayProxyResponseEvent handle(APIGatewayProxyRequestEvent input, Context context, User user) {
        String chapterId = input.getPathParameters().get("chapterid");



        Links links = new Links();
        ChapterResponse response = new ChapterResponse();

        Chapter chapter = ChapterFinder.getById(chapterId);
        chapter.self = "http://sandbox.api.openlight.io/books/"+chapter.book +"/chapters/"+chapter.self;

        if(BookFinder.getById(chapter.book).editor.equals(user.username)){
            System.out.println("User is the editor of this book.");
            Link link = new Link();
            link.url = chapter.self+"/select";
            link.rel = "select_this_chapter";
            response.addLink(link);
        }

        chapter.book = "http://sandbox.api.openlight.io/books/"+chapter.book;
        chapter.writer = "http://sandbox.api.openlight.io/users/"+chapter.writer;

        response.body = chapter;

        Link link = new Link();
        link.url = chapter.book +"/chapters/proposed";
        link.rel = "other_proposed_chapters_for_book";





        response.addLink(link);
        String responseJson = gson.toJson(response);

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        return new APIGatewayProxyResponseEvent().withBody(responseJson).withHeaders(headers).withStatusCode(200);
    }
}
