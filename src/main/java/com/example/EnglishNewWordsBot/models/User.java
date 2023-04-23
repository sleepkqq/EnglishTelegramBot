package com.example.EnglishNewWordsBot.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;

@Getter
@Setter
@Entity(name = "userTable")
public class User {

    @Id
    private Long chatId;
    private Long moduleNow;
    private String letterNow;
}
