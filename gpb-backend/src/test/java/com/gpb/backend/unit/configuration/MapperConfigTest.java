package com.gpb.backend.unit.configuration;

import com.gpb.backend.bean.user.UserDto;
import com.gpb.backend.bean.user.WebUser;
import com.gpb.backend.configuration.MapperConfig;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MapperConfigTest {

    private static final String USER_ROLE = "ROLE_USER";
    private final ModelMapper modelMapper = new MapperConfig().modelMapper();

    @Test
    void mapWebUserToUserDtoSuccessfullyShouldReturnUserDto() {
        WebUser user = new WebUser(0, 1L, "email", "pass", false,
                false, 0, null, USER_ROLE, new Locale("ua"));
        UserDto expected = new UserDto("email", "", "", USER_ROLE, "ua");

        UserDto result = modelMapper.map(user, UserDto.class);

        assertEquals(expected, result);
    }
}