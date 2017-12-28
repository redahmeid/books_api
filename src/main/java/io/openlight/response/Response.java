package io.openlight.response;

import java.util.Map;
import java.util.TreeMap;

public class Response {

    public String self;
    public Object data;
    public String error;

    public Map<String,String> related;

    public Map<String,String> actions;

    public void addRelated(String rel, String url){
        if(related ==null) related = new TreeMap<>();
        related.put(rel,url);
    }

    public void addAction(String rel, String url){
        if(actions ==null) actions = new TreeMap<>();
        actions.put(rel,url);
    }


    @Deprecated
    public void addRelated(Link link){
        addRelated(link.rel,link.url);
    }

    @Deprecated
    public void addAction(Link link){
       addAction(link.rel,link.url);
    }
}
