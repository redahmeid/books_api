package io.openlight.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import io.openlight.MediaTypes;
import io.openlight.domain.Chapter;
import io.openlight.domain.DomainResponse;
import io.openlight.domain.User;
import io.openlight.neo4j.books.BookFinder;
import io.openlight.neo4j.chapters.ChapterFinder;
import io.openlight.response.Link;
import io.openlight.response.Response;

import java.util.HashMap;
import java.util.Map;


public class GetStoryHandler extends AbstractLambda {

    Gson gson = new Gson();


    @Override
    public APIGatewayProxyResponseEvent handle(APIGatewayProxyRequestEvent input, Context context, User user) {
        String chapterId = input.getPathParameters().get("chapterid");

        Response response = new Response();

        DomainResponse domainResponse = ChapterFinder.getById(chapterId);
        Chapter chapter = (Chapter)domainResponse.body;

        response.self = "http://sandbox.api.openlight.io/books/"+chapter.book +"/chapters/"+domainResponse.id;

        // add some potential actions
        if(BookFinder.getById(chapter.book).body.editor.equals(user.username)){
            System.out.println("User is the editor of this book.");
            Link link = new Link();
            link.url = response.self+"/select";
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
        response.addRelated(previousLink);


        response.body = chapter;

        Link link = new Link();
        link.url = chapter.book +"/chapters/proposed";
        link.rel = "other_proposed_chapters_for_book";
        response.addRelated(link);
        String responseJson = gson.toJson(response);

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", MediaTypes.CHAPTER.type());

        return new APIGatewayProxyResponseEvent().withBody(responseJson).withHeaders(headers).withStatusCode(200);
    }
}
