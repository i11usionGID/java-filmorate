package ru.yandex.practicum.filmorate.model;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
public class User extends BaseUnit{
    @Email
    private String email;
    @NotEmpty
    private String login;
    private String name;
    @NotEmpty
    private LocalDate birthday;
}