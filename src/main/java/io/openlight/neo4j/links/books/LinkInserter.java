package io.openlight.neo4j.links.books;

import io.openlight.response.Link;
import io.openlight.response.Links;
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;

import java.util.Random;

public class LinkInserter {


    public static String addActionToUser(Links newLinks, String username){
        Driver driver = GraphDatabase.driver( System.getenv("neo_url"), AuthTokens.basic( System.getenv("neo_user"), System.getenv("neo_password") ) );
        Session session = driver.session();
        String id = "".replaceAll("\\W", "").toLowerCase();

        newLinks.getActions().parallelStream().forEach(link ->  addActionToUser(link,username));


        session.close();
        driver.close();
        return id;

    }

    private static void addActionToUser(Link link, String username){

        String id = new Random().nextInt()+"";
        Driver driver = GraphDatabase.driver( System.getenv("neo_url"), AuthTokens.basic( System.getenv("neo_user"), System.getenv("neo_password") ) );
        Session session = driver.session();
        session.run("CREATE (n:Request {id:'"+id+"', actual:'"+link.url+"', unique:'http://sandbox.api.openlight.io/books/"+id+"')");
        session.run("MERGE (n:User {username: '"+username+"'})");
        session.run("MATCH (a:Link { username: '"+username+"' }), (b:Book { id: '"+id+"' }) CREATE (a)-[:EDITS]->(b);");


        // CREATE FIRST ACTION
        //MATCH ((user:User{username:'"+username+"'})) CREATE (a:Link { request_id: '"+id+"',actual:'http://sandbox.api.openlight.io/users/reda', unique:'http://sandbox.api.openlight.io/users/1234567'})-[:POSSIBLE_ACTION]->(user)

        // CREATE new action on the last chosen action from the user
        //MATCH ((previous:Link{_id:'1234569'})) CREATE (a:Link { _id: '123456110',actual:'http://sandbox.api.openlight.io/users/reda', unique:'http://sandbox.api.openlight.io/users/123456110/1234569'})-[:POSSIBLE_ACTION]->(previous)

        // create chosen action on the action chosen
        // MATCH ((chosen:Link{_id:'12345678'})-[:POSSIBLE_ACTION]-(previous)) CREATE (previous)-[:CHOSEN_ACTION]->(chosen)



    }
}
