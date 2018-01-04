package io.openlight.response.chapters;


import io.openlight.response.Response;
import io.openlight.response.books.Book;
import io.openlight.response.users.User;

public class ChapterResponse {


    public Response<User> writer;
    public Response<Book> book;
    public String text;

}