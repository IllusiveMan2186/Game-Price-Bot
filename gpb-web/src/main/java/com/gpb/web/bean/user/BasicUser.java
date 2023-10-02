package com.gpb.web.bean.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gpb.web.bean.game.Game;
import lombok.AllArgsConstructor;
import lombok.Data;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BasicUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToMany
    @JoinTable(
            name = "user_game",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "game_id")}
    )
    @JsonIgnore
    @ToString.Exclude
    private List<Game> gameList;
}
    