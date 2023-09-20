package com.gpb.web.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.PrimaryKeyJoinColumn;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@PrimaryKeyJoinColumn(name = "userId")
public class WebUser extends BasicUser {

    private String username;

    private String email;

    @JsonIgnore
    private String password;
}
