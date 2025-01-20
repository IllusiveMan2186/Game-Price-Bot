package com.gpb.game.configuration;

import com.gpb.common.entity.game.GameDto;
import com.gpb.common.entity.game.GameInStoreDto;
import com.gpb.common.entity.game.GameInfoDto;
import com.gpb.game.entity.game.Game;
import com.gpb.game.entity.game.GameInShop;
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
                .addMappings(mapper -> mapper.skip(GameDto::setMaxPrice)).setPostConverter(toGameDtoConverter())
                .addMappings(mapper -> mapper.skip(GameDto::setAvailable)).setPostConverter(toGameDtoConverter());

        modelMapper.createTypeMap(Game.class, GameInfoDto.class)
                .addMappings(mapper -> mapper.skip(GameDto::setMinPrice)).setPostConverter(toGameInfoDtoConverter())
                .addMappings(mapper -> mapper.skip(GameDto::setMaxPrice)).setPostConverter(toGameInfoDtoConverter())
                .addMappings(mapper -> mapper.skip(GameInfoDto::setGamesInShop)).setPostConverter(toGameInfoDtoConverter())
                .addMappings(mapper -> mapper.skip(GameInfoDto::setAvailable)).setPostConverter(toGameInfoDtoConverter());

        modelMapper.createTypeMap(GameInShop.class, GameInStoreDto.class);

        return modelMapper;
    }

    public Converter<Game, GameDto> toGameDtoConverter() {
        return context -> {
            Game source = context.getSource();
            GameDto destination = context.getDestination();
            mapMinPriceField(source, destination);
            mapMaxPriceField(source, destination);
            mapAvailableField(source, destination);
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

    public void mapAvailableField(Game source, GameDto destination) {
        destination.setAvailable(!Objects.isNull(source) && source.getGamesInShop().stream()
                .anyMatch(GameInShop::isAvailable));
    }

    public Converter<Game, GameInfoDto> toGameInfoDtoConverter() {
        return context -> {
            Game source = context.getSource();
            GameInfoDto destination = context.getDestination();
            mapMinPriceField(source, destination);
            mapMaxPriceField(source, destination);
            mapGameInShopField(source, destination);
            mapAvailableField(source, destination);
            return context.getDestination();
        };
    }

    public void mapGameInShopField(Game source, GameInfoDto destination) {
        destination.setGamesInShop(Objects.isNull(source) ? null :
                source.getGamesInShop().stream()
                        .map(game -> GameInStoreDto.builder()
                                .id(game.getId())
                                .nameInStore(game.getNameInStore())
                                .url(game.getUrl())
                                .price(game.getPrice())
                                .discountPrice(game.getDiscountPrice())
                                .discount(game.getDiscount())
                                .discountDate(game.getDiscountDate())
                                .isAvailable(game.isAvailable())
                                .clientType(game.getClientType()).build())
                        .toList());
    }
}
