package com.gpb.web.bean;

import lombok.Data;

import java.util.List;

@Data
public class UserInfo {

    public UserInfo(WebUser user) {
        this.email = user.getEmail();
        this.id = user.getId();
        this.gameList = user.getGameList();
    }

    private long id;

    private String email;

    private List<Game> gameList;
}
