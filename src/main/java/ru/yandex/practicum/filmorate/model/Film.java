package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
public class Film extends BaseUnit {
    @NotBlank
    private String name;
    @Size(min = 1, max = 200)
    private String description;
    @NotNull
    private LocalDate releaseDate;
    @Min(1)
    private int duration;
    @NotNull
    private Mpa mpa;
    private Set<Genre> genres = new HashSet<>();
    private int rate = 0;
    private Set<Integer> likesFrom = new HashSet<>();


    public Set<Integer> getLikesFrom() {
        return likesFrom;
    }

    public void addLike(Integer id) {
        likesFrom.add(id);
    }

    public void removeLike(Integer id) {
        likesFrom.remove(id);
    }
}
