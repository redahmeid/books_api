package io.openlight.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import io.openlight.domain.Book;
import io.openlight.domain.Chapter;
import io.openlight.domain.User;
import io.openlight.neo4j.chapters.Inserter;
import io.openlight.response.ErrorResponse;
import io.openlight.response.Link;

import java.util.HashMap;
import java.util.Map;


public class CreateFirstChapterHandler extends AbstractLambda{

    Gson gson = new Gson();


    @Override
    public APIGatewayProxyResponseEvent handle(APIGatewayProxyRequestEvent input, Context context, User user) {
        Chapter chapter = gson.fromJson(input.getBody(),Chapter.class);
        ErrorResponse errorResponse = new ErrorResponse();


        if(errorResponse.messages.size()>0){
            return new APIGatewayProxyResponseEvent().withBody(gson.toJson(errorResponse)).withStatusCode(400);
        }


        String id = Inserter.createFirstChapter(chapter.bookid, chapter.text,user.username);


        Link link = new Link();
        link.location = "http://sandbox.api.story.openlight.io/books/"+chapter.bookid+"/chapters/"+id;
        String linkJson = gson.toJson(link);

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        return new APIGatewayProxyResponseEvent().withBody(linkJson).withHeaders(headers).withStatusCode(201);
    }
}
