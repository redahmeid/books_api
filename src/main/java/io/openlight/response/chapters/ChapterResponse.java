package io.openlight.response.chapters;

import io.openlight.domain.Chapter;
import io.openlight.response.Link;

import java.util.ArrayList;

public class ChapterResponse {

    public Chapter body;

    public ArrayList<Link> actions;


    public void addLink(Link link){
        if(actions==null) actions = new ArrayList<>();
        actions.add(link);
    }


    public String self;
}