package io.openlight.lambda;

import com.amazonaws.services.lambda.runtime.CognitoIdentity;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import io.openlight.domain.Book;
import io.openlight.domain.User;
import io.openlight.neo4j.Finder;
import io.openlight.response.*;
import software.amazon.awssdk.services.cognitoidentity.model.CognitoIdentityProvider;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderAsyncClient;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminGetUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.GetUserRequest;

import java.util.HashMap;
import java.util.Map;


public class GetBookHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    Gson gson = new Gson();

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {


        System.out.println(context.getIdentity().getIdentityPoolId());

        System.out.println(input.getRequestContext().getIdentity().getCognitoIdentityId());

        //CognitoIdentityProviderClient.create().ge
        String book_id = input.getPathParameters().get("bookid");

        BookResponse response = new BookResponse();

        Book book = Finder.getById(book_id);
        String editorLink = "http://api.openlight.io/users/"+book.editor;
        book.editor = editorLink;

        response.body = book;

        Link link = new Link();
        link.url = "http://api.openlight.io/books/"+book.id+"/propose_chapter";
        link.rel = "propose_next_chapter";

        Links links = new Links();
        links.addLink(link);

        response.addLink(link);
        String bookJson = gson.toJson(response);

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        return new APIGatewayProxyResponseEvent().withBody(bookJson).withHeaders(headers).withStatusCode(200);
    }
}
