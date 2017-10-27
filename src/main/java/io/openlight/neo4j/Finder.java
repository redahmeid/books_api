package io.openlight.neo4j;

import io.openlight.domain.User;
import org.neo4j.driver.v1.*;

import java.util.UUID;

public class Finder {

    public static User getByUsername(String username){
        Driver driver = GraphDatabase.driver( System.getenv("neo_url"), AuthTokens.basic( System.getenv("neo_user"), System.getenv("neo_password") ) );
        User user = new User();
        Session session = driver.session();
        StatementResult result = session.run("MATCH (n:User { username: '"+username+"' }) RETURN n.username AS username, n.name AS name, n.email AS email");

        while ( result.hasNext() )
        {
            Record record = result.next();
            user.username = record.get("username").asString();
            user.email = record.get("email").asString();
            user.name = record.get("name").asString();

        }

        session.close();
        driver.close();

        return user;
    }
}
