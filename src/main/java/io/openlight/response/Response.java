package io.openlight.response;

import io.openlight.MediaTypes;

import java.util.Map;
import java.util.TreeMap;

public class Response<E> {

    public String self;
    public MediaTypes type;
    public E data;

    public String error;

    public Response(MediaTypes type){
        this.type = type;
    }

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


    public void addRelated(Link link){
        addRelated(link.rel,link.url);
    }

    public void addAction(Link link){
       addAction(link.rel,link.url);
    }
}
