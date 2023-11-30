package com.gpb.web.configuration;

import com.gpb.web.bean.game.Game;
import com.gpb.web.bean.game.GameDto;
import com.gpb.web.bean.game.GameInShop;
import com.gpb.web.bean.game.GameInStoreDto;
import com.gpb.web.bean.game.GameInfoDto;
import com.gpb.web.bean.user.UserDto;
import com.gpb.web.bean.user.WebUser;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MapperConfigTest {

    private static final String USER_ROLE = "ROLE_USER";
    private final ModelMapper modelMapper = new MapperConfig().modelMapper();

    @Test
    public void mapWebUserToUserDtoSuccessfullyShouldReturnUserDto() {
        WebUser user = new WebUser("email", "pass", false, 0, null, USER_ROLE);
        UserDto expected = new UserDto("email", "", "", USER_ROLE);

        UserDto result = modelMapper.map(user, UserDto.class);

        assertEquals(expected, result);
    }

    @Test
    public void mapGameToGameDtoSuccessfullyShouldReturnGameDto() {
        GameInShop gameInShop = GameInShop.builder()
                .price(new BigDecimal(2))
                .discountPrice(new BigDecimal(1))
                .build();
        GameInShop gameInShop2 = GameInShop.builder()
                .price(new BigDecimal(5))
                .discountPrice(new BigDecimal(3))
                .build();
        Game game = Game.builder().gamesInShop((Set.of(gameInShop, gameInShop2))).build();
        GameDto expected = new GameDto();
        expected.setMinPrice(new BigDecimal(1));
        expected.setMaxPrice(new BigDecimal(3));

        GameDto result = modelMapper.map(game, GameDto.class);

        assertEquals(expected, result);
    }

    @Test
    public void mapGameInShopToGameInStoreDtoSuccessfullyShouldReturnGameInStoreDto() {
        GameInShop gameInShop = GameInShop.builder()
                .price(new BigDecimal(2))
                .discountPrice(new BigDecimal(1))
                .build();

        GameInStoreDto result = modelMapper.map(gameInShop, GameInStoreDto.class);

        assertEquals(new GameInStoreDto(gameInShop), result);
    }

    @Test
    public void mapGameToGameInfoDtoSuccessfullyShouldReturnGameInfoDto() {
        GameInShop gameInShop = GameInShop.builder()
                .price(new BigDecimal(2))
                .discountPrice(new BigDecimal(1))
                .build();
        GameInShop gameInShop2 = GameInShop.builder()
                .price(new BigDecimal(5))
                .discountPrice(new BigDecimal(3))
                .build();
        Set<GameInShop> games = new LinkedHashSet<>();
        games.add(gameInShop);
        games.add(gameInShop2);
        Game game = Game.builder().name("name").id(1).gamesInShop(games).build();

        GameInfoDto expected = new GameInfoDto();
        expected.setUserSubscribed(false);
        expected.setAvailable(true);
        expected.setMinPrice(new BigDecimal(1));
        expected.setMaxPrice(new BigDecimal(3));
        expected.setName(game.getName());
        expected.setId(game.getId());
        expected.setGamesInShop(List.of(modelMapper.map(gameInShop, GameInStoreDto.class),
                modelMapper.map(gameInShop2, GameInStoreDto.class)));

        GameInfoDto result = modelMapper.map(game, GameInfoDto.class);

        assertEquals(expected.getGamesInShop(), result.getGamesInShop());
        assertEquals(expected.isUserSubscribed(), result.isUserSubscribed());
        assertEquals(expected.getMaxPrice(), result.getMaxPrice());
        assertEquals(expected.getMinPrice(), result.getMinPrice());
        assertEquals(expected.getName(), result.getName());
        assertEquals(expected.getId(), result.getId());
    }
}