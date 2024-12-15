package com.gpb.game.bean.game;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gpb.game.bean.user.BasicUser;
import jakarta.persistence.CascadeType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.Set;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    @Enumerated(EnumType.ORDINAL)
    @ElementCollection(targetClass = Genre.class)
    private List<Genre> genres;

    @Enumerated(EnumType.STRING)
    private ProductType type;

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL)
    private Set<GameInShop> gamesInShop;

    @ManyToMany
    @JoinTable(
            name = "user_game",
            joinColumns = {@JoinColumn(name = "game_id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id")}
    )
    @JsonIgnore
    @ToString.Exclude
    private List<BasicUser> userList;

    private boolean isFollowed;
}