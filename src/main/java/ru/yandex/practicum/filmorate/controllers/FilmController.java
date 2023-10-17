package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;


@RestController
@RequestMapping("/films")
@Slf4j

public class FilmController extends BaseController<Film> {
    private static final LocalDate START_TIME = LocalDate.of(1895,12,28);
    @GetMapping
    public List<Film> getAll(){
        log.info("Getting films {}", storage.values());
        return super.getAll();
    }
    @PostMapping
    public Film create(@Valid @RequestBody Film film){
        log.info("Creating film {}", film);
        return super.create(film);
    }
    @PutMapping
    public Film update(@Valid @RequestBody Film film){
        log.info("Updating film {}", film);
        return super.update(film);
    }
    //почему-то через анотации не работает, пришлось добавить проверку в validate
    public void validate(Film film) {
        if(film.getReleaseDate().isBefore(LocalDate.of(1895,12,28)) ||
        film.getName().isEmpty() ||
        film.getDescription().length()>200 ||
        film.getDuration()<0){
            throw new ValidationException("Invalid ReleaseDate or name or description or duration.");
        }
    }

}
