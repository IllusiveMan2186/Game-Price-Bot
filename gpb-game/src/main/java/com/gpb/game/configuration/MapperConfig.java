package com.gpb.game.configuration;

import com.gpb.common.entity.game.GameDto;
import com.gpb.common.entity.game.GameInStoreDto;
import com.gpb.common.entity.game.GameInfoDto;
import com.gpb.game.entity.game.Game;
import com.gpb.game.entity.game.GameInShop;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Comparator;

import static org.modelmapper.config.Configuration.AccessLevel.PRIVATE;

/**
 * Configuration class for setting up ModelMapper with custom mappings and converters.
 * <p>
 * This configuration sets ModelMapper to use a strict matching strategy and defines custom converters
 * for mapping {@link Game} to {@link GameDto} and {@link GameInfoDto}. It also creates a type map
 * for mapping {@link GameInShop} to {@link GameInStoreDto}.
 * </p>
 */
@Configuration
public class MapperConfig {

    /**
     * Configures and exposes a {@link ModelMapper} bean with custom type maps and converters.
     *
     * @return a configured {@link ModelMapper} instance.
     */
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setFieldMatchingEnabled(true)
                .setSkipNullEnabled(true)
                .setFieldAccessLevel(PRIVATE);

        TypeMap<Game, GameDto> gameToGameDtoMap = modelMapper.createTypeMap(Game.class, GameDto.class);
        gameToGameDtoMap.addMappings(mapper -> {
            mapper.skip(GameDto::setMinPrice);
            mapper.skip(GameDto::setMaxPrice);
            mapper.skip(GameDto::setAvailable);
        });
        gameToGameDtoMap.setPostConverter(toGameDtoConverter());

        TypeMap<Game, GameInfoDto> gameToGameInfoDtoMap = modelMapper.createTypeMap(Game.class, GameInfoDto.class);
        gameToGameInfoDtoMap.addMappings(mapper -> {
            mapper.skip(GameInfoDto::setMinPrice);
            mapper.skip(GameInfoDto::setMaxPrice);
            mapper.skip(GameInfoDto::setGamesInShop);
            mapper.skip(GameInfoDto::setAvailable);
        });
        gameToGameInfoDtoMap.setPostConverter(toGameInfoDtoConverter());

        modelMapper.createTypeMap(GameInShop.class, GameInStoreDto.class);

        return modelMapper;
    }

    /**
     * Creates a custom converter for mapping {@link Game} to {@link GameDto}.
     * <p>
     * The converter computes and sets the minimum price, maximum price, and availability based on the
     * associated {@link GameInShop} entries.
     * </p>
     *
     * @return a {@link Converter} that post-processes the mapping from {@link Game} to {@link GameDto}.
     */
    public Converter<Game, GameDto> toGameDtoConverter() {
        return context -> {
            Game source = context.getSource();
            GameDto destination = context.getDestination();
            destination.setMinPrice(source.getMinDiscountPriceForSort());
            destination.setMaxPrice(source.getMaxDiscountPriceForSort());
            mapAvailableField(source, destination);
            return destination;
        };
    }

    /**
     * Maps the availability from the game shops to the {@code available} field of the destination.
     *
     * @param source      the source {@link Game} entity.
     * @param destination the destination {@link GameDto} to be populated.
     */
    public void mapAvailableField(Game source, GameDto destination) {
        destination.setAvailable(source != null &&
                source.getGamesInShop().stream().anyMatch(GameInShop::isAvailable));
    }

    /**
     * Creates a custom converter for mapping {@link Game} to {@link GameInfoDto}.
     * <p>
     * This converter extends the mapping for {@link GameDto} by additionally mapping the list of game shop information.
     * </p>
     *
     * @return a {@link Converter} that post-processes the mapping from {@link Game} to {@link GameInfoDto}.
     */
    public Converter<Game, GameInfoDto> toGameInfoDtoConverter() {
        return context -> {
            Game source = context.getSource();
            GameInfoDto destination = context.getDestination();
            destination.setMinPrice(source.getMinDiscountPriceForSort());
            destination.setMaxPrice(source.getMaxDiscountPriceForSort());
            mapGameInShopField(source, destination);
            mapAvailableField(source, destination);
            return destination;
        };
    }

    /**
     * Maps the list of game shop information from the source {@link Game} to the {@code gamesInShop} field
     * of the {@link GameInfoDto}.
     *
     * @param source      the source {@link Game} entity.
     * @param destination the destination {@link GameInfoDto} to be populated.
     */
    public void mapGameInShopField(Game source, GameInfoDto destination) {
        destination.setGamesInShop(source == null ? null :
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
                                .clientType(game.getClientType())
                                .build())
                        .toList());
    }
}
