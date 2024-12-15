package com.gpb.game.bean.user;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class AccountLinker {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String token;


    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn
    private BasicUser user;
}
