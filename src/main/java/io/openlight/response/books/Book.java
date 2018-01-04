package io.openlight.response.books;

import io.openlight.response.Response;
import io.openlight.response.chapters.Story;
import io.openlight.response.users.User;

public class Book {
    public String title;
    public String image;
    public Response<Story> story;
    public Response<User> editor;
}
