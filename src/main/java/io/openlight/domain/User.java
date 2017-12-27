package io.openlight.domain;

public class User extends Domain{


    public String username;
    public String name;
    public  String email;

    @Override
    public String toString() {
        return username +" "+name+" "+email;
    }
}