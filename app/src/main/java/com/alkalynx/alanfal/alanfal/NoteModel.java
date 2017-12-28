package com.alkalynx.alanfal.alanfal;

/**
 * Created by SaputraPC on 03-Dec-17.
 */

public class NoteModel {

    public String name;
    public String title;
    public String content;
    public String like;
    public String comment;

    public NoteModel() {
    }

    public NoteModel(String name, String title, String content, String like, String comment) {
        this.name = name;
        this.title = title;
        this.content = content;
        this.like = like;
        this.comment = comment;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLike() {
        return like;
    }

    public void setLike(String like) {
        this.like = like;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
