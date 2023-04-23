package com.example.EnglishNewWordsBot.repositories;

import com.example.EnglishNewWordsBot.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByChatId(Long chatId);
}
