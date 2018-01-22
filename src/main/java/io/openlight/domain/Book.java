package io.openlight.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Book {

    public String title;
    public String image;
    public String editor;
    public String id;
    public String category;
    public String synopsis;

    public Optional<List<Chapter>> chapters = Optional.empty();

    public void addChapter(Chapter chapter){
        if(!chapters.isPresent()){
            List<Chapter> chapterList = new ArrayList<>();
            chapters = Optional.of(chapterList);
        }

        chapters.get().add(chapter);
    }
}
