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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class GameInShop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    private long id;

    @Getter
    @Setter
    private String nameInStore;

    @Getter
    @Setter
    private String url;

    @Getter
    @Setter
    private BigDecimal price;

    @Getter
    @Setter
    private BigDecimal discountPrice;

    @Getter
    @Setter
    private boolean isAvailable;

    @Getter
    @Setter
    private int discount;

    @Getter
    @Setter
    private Date discountDate;

    @Getter
    @Setter
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Game game;
}
