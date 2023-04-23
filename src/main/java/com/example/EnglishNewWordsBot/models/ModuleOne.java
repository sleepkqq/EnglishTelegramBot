package com.example.EnglishNewWordsBot.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;

@Getter
@Setter
@Entity(name = "moduleOne")
public class ModuleOne {

    @Id
    private Long id;
    private String letter;
    private String english;
    private String translate;
}