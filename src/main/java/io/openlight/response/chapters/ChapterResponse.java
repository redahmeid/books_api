package io.openlight.response.chapters;

import io.openlight.domain.Chapter;
import io.openlight.response.Link;

import java.util.ArrayList;

public class ChapterResponse {

    public Chapter body;

    public ArrayList<Link> links;


    public void addLink(Link link){
        if(links ==null) links = new ArrayList<>();
        links.add(link);
    }


    public String self;
}