package io.openlight.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.openlight.domain.User;

public abstract class AbstractLambda implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {


    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {

        String token = input.getHeaders().get("io.openlight.story.api.auth");
        User user = new User();
        try {
            DecodedJWT jwt = JWT.decode(token);

            user.email = jwt.getClaims().get("email").asString();
            user.username = jwt.getClaim("cognito:username").asString();
            System.out.println("Logged on user is : "+user);

        } catch (JWTDecodeException exception){
            exception.printStackTrace();
        }
        return handle(input,context,user);
    }

    public abstract APIGatewayProxyResponseEvent handle(APIGatewayProxyRequestEvent input, Context context,User user);



}
