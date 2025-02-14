package com.gpb.game.repository.impl;

import com.gpb.game.entity.game.Game;
import com.gpb.game.repository.GameRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.engine.search.query.SearchResult;
import org.springframework.data.domain.Pageable;
import java.util.List;


public class GameRepositoryImpl implements GameRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Game> searchByNameFullText(String name, Pageable pageable) {
        SearchResult<Game> result = Search.session(entityManager)
                .search(Game.class)
                .where(f -> f.match()
                        .fields("name")
                        .matching(name))
                .fetch((int) pageable.getOffset(), pageable.getPageSize());
        return result.hits();
    }

    @Override
    public long countByNameFullText(String name) {
        return Search.session(entityManager)
                .search(Game.class)
                .where(f -> f.match()
                        .fields("name")
                        .matching(name))
                .fetchTotalHitCount();
    }
}
