package com.gpb.game.entity.game;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gpb.common.entity.game.ClientActivationType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Game in specific store
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class GameInShop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String nameInStore;

    private String url;

    private BigDecimal price;

    private BigDecimal discountPrice;

    private boolean isAvailable;

    private int discount;

    private Date discountDate;

    @Enumerated(EnumType.STRING)
    private ClientActivationType clientType;

    @JoinColumn
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne
    private Game game;
}
