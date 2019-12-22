package com.yourmother.android.worstmessengerever.entities;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class GroupChat implements Serializable {

    private String title;
    private List<String> users;

    public GroupChat() {
    }

    public GroupChat(String title, List<String> users) {
        this.title = title;
        this.users = users;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }

    @Override
    public String toString() {
        return "GroupChat{" +
                "title='" + title + '\'' +
                ", users=" + users +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GroupChat)) return false;
        GroupChat groupChat = (GroupChat) o;
        return Objects.equals(title, groupChat.title) &&
                Objects.equals(users, groupChat.users);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, users);
    }
}
