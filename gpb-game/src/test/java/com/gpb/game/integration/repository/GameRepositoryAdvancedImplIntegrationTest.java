package com.gpb.game.integration.repository;

import com.gpb.common.entity.game.Genre;
import com.gpb.common.entity.game.ProductType;
import com.gpb.game.entity.game.Game;
import com.gpb.game.entity.game.GameInShop;
import com.gpb.game.entity.game.GameRepositorySearchFilter;
import com.gpb.game.integration.BaseIntegration;
import com.gpb.game.repository.GameRepository;
import com.gpb.game.repository.advanced.impl.GameRepositoryAdvancedImpl;
import jakarta.persistence.EntityManager;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@Sql(value = "classpath:/cleaning_db.sql", executionPhase = BEFORE_TEST_METHOD)
@Transactional
class GameRepositoryAdvancedImplIntegrationTest extends BaseIntegration {

    private static final String DATE_STRING_FORMAT = "dd/MM/yyyy";

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GameRepositoryAdvancedImpl gameRepositoryCustom;

    @BeforeEach
    void saveThreeGames() throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_STRING_FORMAT);

        GameInShop shop1 = GameInShop.builder()
                .url("url1")
                .price(new BigDecimal("100.0"))
                .discount(15)
                .discountPrice(new BigDecimal("85.0"))
                .discountDate(dateFormat.parse("12/12/2021"))
                .isAvailable(true)
                .build();

        Game minecraft = Game.builder()
                .name("Minecraft")
                .type(ProductType.GAME)
                .gamesInShop(Set.of(shop1))
                .isFollowed(true)
                .genres(List.of(Genre.ADVENTURES))
                .build();
        shop1.setGame(minecraft);

        GameInShop shop2 = GameInShop.builder()
                .url("url2")
                .price(new BigDecimal("200.0"))
                .discount(0)
                .discountPrice(new BigDecimal("200.0"))
                .discountDate(null)
                .isAvailable(false)
                .build();

        Game minecraftCurrency = Game.builder()
                .name("Minecraft 220 coins")
                .type(ProductType.CURRENCY)
                .gamesInShop(Set.of(shop2))
                .isFollowed(true)
                .genres(Collections.EMPTY_LIST)
                .build();
        shop2.setGame(minecraftCurrency);

        GameInShop shop3 = GameInShop.builder()
                .url("url3")
                .price(new BigDecimal("500.0"))
                .discount(50)
                .discountPrice(new BigDecimal("250.0"))
                .discountDate(dateFormat.parse("12/12/2021"))
                .isAvailable(true)
                .build();

        Game gta = Game.builder()
                .name("Grand theft auto 7")
                .type(ProductType.GAME)
                .gamesInShop(Set.of(shop3))
                .isFollowed(true)
                .genres(List.of(Genre.ACTION))
                .build();
        shop3.setGame(gta);

        gameRepository.save(minecraft);
        gameRepository.save(minecraftCurrency);
        gameRepository.save(gta);

        entityManager.flush();
    }

    @Test
    void testSearchByNameFullText_whenGamesFound_shouldReturnsTwoGames() {
        prepareSearchSessionForNAmeSearch();

        PageRequest pageable = PageRequest.of(0, 10);
        Page<Game> result = gameRepositoryCustom.searchByNameFullText("mine", pageable);

        assertEquals(2, result.getTotalElements());
        assertThat(result.getContent())
                .extracting(Game::getName)
                .containsExactlyInAnyOrder("Minecraft", "Minecraft 220 coins");
    }

    @Test
    void testSearchByNameFullText_whenGamesNotFound_shouldReturnsEmptyList() {
        prepareSearchSessionForNAmeSearch();

        PageRequest pageable = PageRequest.of(0, 10);
        Page<Game> result = gameRepositoryCustom.searchByNameFullText("Call of duty", pageable);

        assertEquals(0, result.getTotalElements());
    }

    @Test
    void testFindGames_whenNoMatch_shouldReturnsEmptyPage() {
        GameRepositorySearchFilter filter = GameRepositorySearchFilter.builder()
                .genres(List.of(Genre.RPG)).build();
        Pageable pageable = PageRequest.of(0, 10);

        Page<Game> result = gameRepositoryCustom.findGames(filter, pageable);

        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();
    }

    @Test
    void testFindGames_whenByGenres_shouldReturnsGame() {
        GameRepositorySearchFilter filter = GameRepositorySearchFilter.builder()
                .genres(List.of(Genre.ADVENTURES, Genre.ACTION)).build();
        Pageable pageable = PageRequest.of(0, 10);

        Page<Game> result = gameRepositoryCustom.findGames(filter, pageable);

        assertEquals(2, result.getTotalElements());
        assertThat(result.getContent())
                .extracting(Game::getName)
                .containsExactlyInAnyOrder("Minecraft", "Grand theft auto 7");
    }

    @Test
    void testFindGames_whenByTypes_shouldReturnsOneCurrency() {
        GameRepositorySearchFilter filter = GameRepositorySearchFilter.builder()
                .types(List.of(ProductType.CURRENCY)).build();
        Pageable pageable = PageRequest.of(0, 10);

        Page<Game> result = gameRepositoryCustom.findGames(filter, pageable);

        assertEquals(1, result.getTotalElements());
        assertThat(result.getContent())
                .extracting(Game::getName)
                .containsOnly("Minecraft 220 coins");
    }

    @Test
    void testFindGames_whenByPriceRange_shouldReturnsGameInPriceRange() {
        GameRepositorySearchFilter filter = GameRepositorySearchFilter.builder()
                .minPrice(new BigDecimal(1))
                .maxPrice(new BigDecimal(230)).build();
        Pageable pageable = PageRequest.of(0, 10);

        Page<Game> result = gameRepositoryCustom.findGames(filter, pageable);

        assertEquals(2, result.getTotalElements());
        assertThat(result.getContent())
                .extracting(Game::getName)
                .containsExactlyInAnyOrder("Minecraft", "Minecraft 220 coins");
    }

    @Test
    void testFindGames_whenWithSortByNameAscending_shouldReturnsGamesSortedByName() {
        GameRepositorySearchFilter filter = GameRepositorySearchFilter.builder()
                .minPrice(new BigDecimal(1))
                .maxPrice(new BigDecimal(1000))
                .build();
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "name"));

        Page<Game> result = gameRepositoryCustom.findGames(filter, pageable);

        assertEquals(3, result.getTotalElements());
        assertThat(result.getContent())
                .extracting(Game::getName)
                .containsExactly("Grand theft auto 7", "Minecraft", "Minecraft 220 coins");
    }

    @Test
    void testFindGames_whenWithSortByNameDescending_shouldReturnsGamesSortedByName() {
        GameRepositorySearchFilter filter = GameRepositorySearchFilter.builder()
                .minPrice(new BigDecimal(1))
                .maxPrice(new BigDecimal(1000))
                .build();
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "name"));

        Page<Game> result = gameRepositoryCustom.findGames(filter, pageable);

        assertEquals(3, result.getTotalElements());
        assertThat(result.getContent())
                .extracting(Game::getName)
                .containsExactly("Minecraft 220 coins", "Minecraft", "Grand theft auto 7");
    }

    @Test
    void testFindGames_whenWithSortByPriceAscending_shouldReturnsGamesSortedByPrice() {
        GameRepositorySearchFilter filter = GameRepositorySearchFilter.builder()
                .minPrice(new BigDecimal(1))
                .maxPrice(new BigDecimal(1000))
                .build();
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "gamesInShop.discountPrice"));

        Page<Game> result = gameRepositoryCustom.findGames(filter, pageable);

        assertEquals(3, result.getTotalElements());
        assertThat(result.getContent())
                .extracting(Game::getName)
                .containsExactly("Minecraft", "Minecraft 220 coins", "Grand theft auto 7");
    }

    @Test
    void testFindGames_whenWithSortByPriceDescending_shouldReturnsGamesSortedByPrice() {
        GameRepositorySearchFilter filter = GameRepositorySearchFilter.builder()
                .minPrice(new BigDecimal(1))
                .maxPrice(new BigDecimal(1000))
                .build();
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "gamesInShop.discountPrice"));

        Page<Game> result = gameRepositoryCustom.findGames(filter, pageable);

        assertEquals(3, result.getTotalElements());
        assertThat(result.getContent())
                .extracting(Game::getName)
                .containsExactly("Grand theft auto 7", "Minecraft 220 coins", "Minecraft");
    }

    private void prepareSearchSessionForNAmeSearch() {
        // End the current transaction so the rows become visible to other connections
        TestTransaction.flagForCommit();// mark the transaction
        TestTransaction.end();// commit

        TestTransaction.start();


        SearchSession searchSession = Search.session(entityManager);
        try {
            searchSession.massIndexer().startAndWait();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Mass indexing was interrupted", e);
        }
    }
}
