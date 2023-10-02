package com.gpb.web.bean.game;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameInShop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String nameInStore;

    private String url;

    private BigDecimal price;

    private boolean isAvailable;

    private int discount;

    private Date discountDate;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn
    @JsonIgnore
    @ToString.Exclude
    private Game game;
}
