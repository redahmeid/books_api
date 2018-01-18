package io.openlight.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import io.openlight.domain.Book;
import io.openlight.domain.Chapter;
import io.openlight.domain.User;
import io.openlight.neo4j.books.BookFinder;
import io.openlight.neo4j.chapters.ChapterInserter;
import io.openlight.response.ErrorResponse;
import io.openlight.response.Link;
import org.apache.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


public class SelectChapterHandler extends AbstractLambda{

    Gson gson = new Gson();


    @Override
    public APIGatewayProxyResponseEvent handle(APIGatewayProxyRequestEvent input, Context context,Optional<User> user) {

        ErrorResponse errorResponse = new ErrorResponse();
        String chapterId = input.getPathParameters().get("chapterid");

        String bookId = input.getPathParameters().get("bookid");

        return BookFinder.getById(bookId)
                .map(response -> selectChapter(user,chapterId,response)).get();
    }

    private APIGatewayProxyResponseEvent selectChapter(Optional<User> userOptional, String chapterId,Book book){

        APIGatewayProxyResponseEvent response = null;
        if(isAuthorised(userOptional, book)) {
            ChapterInserter.selectChapter(chapterId);
            Link link = new Link();
            link.location = "http://sandbox.api.openlight.io/books/" + book.id + "/story/" + chapterId;
            String linkJson = gson.toJson(link);
            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json");
            response = new APIGatewayProxyResponseEvent().withBody(linkJson).withHeaders(headers).withStatusCode(201);
        }else{
            response = new APIGatewayProxyResponseEvent().withStatusCode(HttpStatus.SC_UNAUTHORIZED);
        }

        return response;
    }

    private boolean isAuthorised(Optional<User> userOptional, Book book){
        return userOptional.map(r -> book.editor.equals(r.username)).orElse(false);
    }
}
