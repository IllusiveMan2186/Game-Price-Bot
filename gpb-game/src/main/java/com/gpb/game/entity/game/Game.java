package com.gpb.game.entity.game;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gpb.common.entity.game.Genre;
import com.gpb.common.entity.game.ProductType;
import com.gpb.game.entity.user.BasicUser;
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
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;

import java.util.List;
import java.util.Set;

@Indexed
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @FullTextField
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