package io.openlight.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import io.openlight.domain.Chapter;
import io.openlight.domain.User;
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

        ChapterResponse response = new ChapterResponse();

        Chapter chapter = ChapterFinder.getById(chapterId);
        //String writerLink = "http://sandbox.api.openlight.io/users/"+chapter.writer;
        chapter.self = "http://sandbox.api.openlight.io/books/"+chapter.book +"/chapters/"+chapter.self;
        chapter.book = "http://sandbox.api.openlight.io/books/"+chapter.book;
        chapter.writer = "http://sandbox.api.openlight.io/users/"+chapter.writer;

        response.body = chapter;

        Link link = new Link();
        link.url = "http://sandbox.api.openlight.io/books/"+chapter.book +"/chapters/proposed";
        link.rel = "other_proposed_chapters_for_book";

        Links links = new Links();
        links.addLink(link);

        response.addLink(link);
        String responseJson = gson.toJson(response);

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        return new APIGatewayProxyResponseEvent().withBody(responseJson).withHeaders(headers).withStatusCode(200);
    }
}
