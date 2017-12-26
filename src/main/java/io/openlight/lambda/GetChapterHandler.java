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
import io.openlight.response.Response;
import io.openlight.response.chapters.ChapterResponse;

import java.util.HashMap;
import java.util.Map;


public class GetChapterHandler extends AbstractLambda {

    Gson gson = new Gson();


    @Override
    public APIGatewayProxyResponseEvent handle(APIGatewayProxyRequestEvent input, Context context, User user) {
        String chapterId = input.getPathParameters().get("chapterid");



        Links links = new Links();
        Response response = new Response();

        Chapter chapter = ChapterFinder.getById(chapterId);
        chapter.self = "http://sandbox.api.openlight.io/books/"+chapter.book +"/chapters/"+chapter.self;

        // add some potential actions
        if(BookFinder.getById(chapter.book).editor.equals(user.username)){
            System.out.println("User is the editor of this book.");
            Link link = new Link();
            link.url = chapter.self+"/select";
            link.rel = "select_this_chapter";
            response.addAction(link);
        }

        Link alternativeLink = new Link();
        alternativeLink.url = "http://sandbox.api.openlight.io/books/"+chapter.book+"/chapters";
        alternativeLink.rel = "propose_an_alternative_chapter";
        response.addAction(alternativeLink);

        chapter.book = "http://sandbox.api.openlight.io/books/"+chapter.book;
        chapter.writer = "http://sandbox.api.openlight.io/users/"+chapter.writer;
        chapter.previous = null;

        Link previousLink = new Link();
        previousLink.url = chapter.book +"/chapters/"+chapter.previous;
        previousLink.rel = "previous";
        response.addLink(previousLink);


        response.body = chapter;

        Link link = new Link();
        link.url = chapter.book +"/chapters/proposed";
        link.rel = "other_proposed_chapters_for_book";
        response.addLink(link);
        String responseJson = gson.toJson(response);

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/vnd.cts.chapter+json");

        return new APIGatewayProxyResponseEvent().withBody(responseJson).withHeaders(headers).withStatusCode(200);
    }
}
