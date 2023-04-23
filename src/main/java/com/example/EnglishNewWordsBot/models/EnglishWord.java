package com.example.EnglishNewWordsBot.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;

@Getter
@Setter
@Entity(name = "englishWordsTable")
public class EnglishWord {

    @Id
    private Long id;
    private String english;
    private String translate;
    private String letter;
    private Long module;

}
