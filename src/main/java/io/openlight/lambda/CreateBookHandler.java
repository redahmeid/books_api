package io.openlight.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import io.openlight.domain.Book;
import io.openlight.domain.User;
import io.openlight.neo4j.books.Inserter;
import io.openlight.response.ErrorResponse;
import io.openlight.response.Link;
import org.apache.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


public class CreateBookHandler extends AbstractLambda{

    Gson gson = new Gson();


    @Override
    public APIGatewayProxyResponseEvent handle(APIGatewayProxyRequestEvent input, Context context, Optional<User> user) {
        Book book = gson.fromJson(input.getBody(),Book.class);

        if(!user.isPresent())
            return new APIGatewayProxyResponseEvent().withStatusCode(HttpStatus.SC_UNAUTHORIZED);

        String id = Inserter.createBook(book.title,book.image,user.get().username);

        Link link = new Link();
        link.location = "http://sandbox.api.openlight.io/books/"+id;
        String linkJson = gson.toJson(link);

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        return new APIGatewayProxyResponseEvent().withBody(linkJson).withHeaders(headers).withStatusCode(201);
    }
}
