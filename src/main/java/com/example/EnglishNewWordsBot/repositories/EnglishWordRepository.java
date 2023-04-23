package com.example.EnglishNewWordsBot.repositories;

import com.example.EnglishNewWordsBot.models.EnglishWord;
import org.springframework.data.repository.CrudRepository;

public interface EnglishWordRepository extends CrudRepository<EnglishWord, Long> {
}
