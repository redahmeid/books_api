package io.openlight.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import io.openlight.domain.Chapter;
import io.openlight.domain.User;
import io.openlight.neo4j.chapters.ChapterInserter;
import io.openlight.response.ErrorResponse;
import io.openlight.response.Link;
import org.apache.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


public class CreateFirstChapterHandler extends AbstractLambda{

    Gson gson = new Gson();


    @Override
    public APIGatewayProxyResponseEvent handle(APIGatewayProxyRequestEvent input, Context context, Optional<User> user) {
        Chapter chapter = gson.fromJson(input.getBody(),Chapter.class);

        if(!user.isPresent())
            return new APIGatewayProxyResponseEvent().withStatusCode(HttpStatus.SC_UNAUTHORIZED);

        ErrorResponse errorResponse = new ErrorResponse();
        String bookid = input.getPathParameters().get("bookid");


        String id = ChapterInserter.proposeAChapter(bookid, chapter.text,user.get().username);


        Link link = new Link();
        link.location = "http://sandbox.api.openlight.io/books/"+bookid+"/chapters/"+id;
        String linkJson = gson.toJson(link);

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        return new APIGatewayProxyResponseEvent().withBody(linkJson).withHeaders(headers).withStatusCode(201);
    }
}
