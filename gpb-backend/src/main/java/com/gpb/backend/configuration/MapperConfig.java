package com.gpb.backend.configuration;

import com.gpb.backend.bean.user.UserDto;
import com.gpb.backend.bean.user.WebUser;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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

        modelMapper.createTypeMap(WebUser.class, UserDto.class)
                .setProvider(request -> {
                    WebUser source = WebUser.class.cast(request.getSource());
                    return new UserDto(source.getEmail(), source.getPassword(), "", source.getRole(),"ua");
                });
        return modelMapper;
    }
}
