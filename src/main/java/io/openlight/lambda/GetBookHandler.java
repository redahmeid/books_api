package io.openlight.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import io.openlight.domain.User;
import io.openlight.neo4j.Finder;
import io.openlight.response.Link;
import io.openlight.response.Links;
import io.openlight.response.BooksResponse;

import java.util.HashMap;
import java.util.Map;


public class GetBookHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    Gson gson = new Gson();

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {


        String username = input.getPathParameters().get("username");
        BooksResponse response = new BooksResponse();

        //User user = Finder.getByUsername(username);
        //response.body = user;
        //response.self = "http://api.openlight.io/users/"+user.username;

        Link link = new Link();
        link.url = "http://api.openlight.io/books";
        link.rel = "start_a_book";

        Links links = new Links();
        links.addLink(link);

        response.addLink(link);
        String userJson = gson.toJson(response);

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        return new APIGatewayProxyResponseEvent().withBody(userJson).withHeaders(headers).withStatusCode(200);
    }
}
