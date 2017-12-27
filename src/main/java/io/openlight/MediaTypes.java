package io.openlight;

public enum MediaTypes {

    CORE(""),
    CHAPTER("chapter"),
    BOOK("book"),
    CHAPTERS("chapters"),
    BOOKS("books");

    private String type;
    MediaTypes(String type){
        this.type = "application/vnd.openlight+json"+type;
    }

    public String type(){
        return type;
    }



}
