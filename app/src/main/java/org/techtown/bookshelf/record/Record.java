package org.techtown.bookshelf.record;

import java.util.ArrayList;

public class Record {
    String push;
    String page;
    String line;
    String thought ="";
    ArrayList<String> likeFriendList = new ArrayList<>();

    public ArrayList<String> getLikeFriendList() {
        return likeFriendList;
    }

    public void setLikeFriendList(ArrayList<String> likeFriendList) {
        this.likeFriendList = likeFriendList;
    }

    public Record(String push, String page, String line) {
        this.push = push;
        this.page = page;
        this.line = line;
    }

    public Record(String push, String page, String line, String thought) {
        this.push = push;
        this.page = page;
        this.line = line;
        this.thought = thought;
    }

    public String getPush() {
        return push;
    }

    public void setPush(String push) {
        this.push = push;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public String getThought() {
        return thought;
    }

    public void setThought(String thought) {
        this.thought = thought;
    }
}
