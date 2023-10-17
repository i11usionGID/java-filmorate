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
public class Film extends BaseUnit{
    @NotEmpty
    private String name;
    @Size(max = 200)
    @NotEmpty
    private String description;
    @NotEmpty
    private LocalDate releaseDate;
    @NotEmpty
    @Min(0)
    private int duration;
}
