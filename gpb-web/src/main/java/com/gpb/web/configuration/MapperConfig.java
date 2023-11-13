package com.gpb.web.configuration;

import com.gpb.web.bean.game.Game;
import com.gpb.web.bean.game.GameDto;
import com.gpb.web.bean.game.GameInShop;
import com.gpb.web.bean.game.GameInfoDto;
import com.gpb.web.bean.game.GameInStoreDto;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Comparator;
import java.util.Objects;

import static org.modelmapper.config.Configuration.AccessLevel.PRIVATE;

@Configuration
public class MapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setFieldMatchingEnabled(true)
                .setSkipNullEnabled(true)
                .setFieldAccessLevel(PRIVATE);

        modelMapper.createTypeMap(Game.class, GameDto.class)
                .addMappings(mapper -> mapper.skip(GameDto::setMinPrice)).setPostConverter(toGameDtoConverter())
                .addMappings(mapper -> mapper.skip(GameDto::setMaxPrice)).setPostConverter(toGameDtoConverter());

        modelMapper.createTypeMap(Game.class, GameInfoDto.class)
                .addMappings(mapper -> mapper.skip(GameDto::setMinPrice)).setPostConverter(toGameInfoDtoConverter())
                .addMappings(mapper -> mapper.skip(GameDto::setMaxPrice)).setPostConverter(toGameInfoDtoConverter())
                .addMappings(mapper -> mapper.skip(GameInfoDto::setGamesInShop)).setPostConverter(toGameInfoDtoConverter());

        return modelMapper;
    }

    public Converter<Game, GameDto> toGameDtoConverter() {
        return context -> {
            Game source = context.getSource();
            GameDto destination = context.getDestination();
            mapMinPriceField(source, destination);
            mapMaxPriceField(source, destination);
            return context.getDestination();
        };
    }

    public void mapMinPriceField(Game source, GameDto destination) {
        destination.setMinPrice(Objects.isNull(source) ? null :
                source.getGamesInShop().stream()
                .map(GameInShop::getDiscountPrice)
                .min(Comparator.naturalOrder())
                .orElse(null));
    }

    public void mapMaxPriceField(Game source, GameDto destination) {
        destination.setMaxPrice(Objects.isNull(source) ? null :
                source.getGamesInShop().stream()
                        .map(GameInShop::getDiscountPrice)
                        .max(Comparator.naturalOrder())
                        .orElse(null));
    }

    public Converter<Game, GameInfoDto> toGameInfoDtoConverter() {
        return context -> {
            Game source = context.getSource();
            GameInfoDto destination = context.getDestination();
            mapMinPriceField(source, destination);
            mapMaxPriceField(source, destination);
            mapGameInShopField(source, destination);
            return context.getDestination();
        };
    }

    public void mapGameInShopField(Game source, GameInfoDto destination) {
        destination.setGamesInShop(Objects.isNull(source) ? null :
                source.getGamesInShop().stream()
                        .map(GameInStoreDto::new)
                        .toList());
    }
}
