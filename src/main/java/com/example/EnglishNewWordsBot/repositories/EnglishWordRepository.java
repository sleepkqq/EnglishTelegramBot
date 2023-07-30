package com.example.EnglishNewWordsBot.repositories;

import com.example.EnglishNewWordsBot.models.EnglishWord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnglishWordRepository extends JpaRepository<EnglishWord, Long> {

    List<EnglishWord> findAllByModuleAndLetter(Long module, String letter);

    List<EnglishWord> findAllByModule(Long module);

    List<EnglishWord> findAll();

}
