package io.openlight.response;

import io.openlight.domain.Domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Response {

    public Domain body;
    public String error;

    public Map<String,String> links;

    public Map<String,String> actions;



    public void addLink(Link link){
        if(links ==null) links = new TreeMap<>();
        links.put(link.rel,link.url);
    }

    public void addAction(Link link){
        if(actions ==null) actions = new TreeMap<>();
        actions.put(link.rel,link.url);
    }
}
