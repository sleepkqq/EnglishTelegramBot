package com.example.EnglishNewWordsBot.models;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity(name = "englishWordsTable")
public class EnglishWord {

    @Id
    private Long id;
    private String english;
    private String translate;
    private String letter;
    private Long module;

}
