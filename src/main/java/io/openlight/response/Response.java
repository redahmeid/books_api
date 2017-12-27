package io.openlight.response;

import java.util.Map;
import java.util.TreeMap;

public class Response {

    public String self;
    public Object body;
    public String error;

    public Map<String,String> related;

    public Map<String,String> actions;



    public void addRelated(Link link){
        if(related ==null) related = new TreeMap<>();
        related.put(link.rel,link.url);
    }

    public void addAction(Link link){
        if(actions ==null) actions = new TreeMap<>();
        actions.put(link.rel,link.url);
    }
}
