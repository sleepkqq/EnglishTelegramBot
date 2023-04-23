package com.example.EnglishNewWordsBot.repositories;

import com.example.EnglishNewWordsBot.models.EnglishWord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface EnglishWordRepository extends JpaRepository<EnglishWord, Long> {
}
