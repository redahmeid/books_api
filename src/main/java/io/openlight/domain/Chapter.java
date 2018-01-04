package io.openlight.domain;

import java.util.Optional;

public class Chapter extends Domain{

    public String text;


    public String id;
    public String writer;
    public String book;
    public Optional<String> previous;
    public Optional<String> next;
}
