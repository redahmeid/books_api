package io.openlight.response;

import java.util.ArrayList;

public class Links{

    private ArrayList<Link> actions;


    public void addLink(Link link){
        if(actions==null) actions = new ArrayList<>();
        actions.add(link);
    }

    public Link get(int i){
        return actions.get(i);
    }

    public ArrayList<Link> getActions(){
        return actions;
    }

}