package com.skillbox.cryptobot.repository;

import com.skillbox.cryptobot.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Transactional
    @Modifying
    @Query(value =
    "update user_telegram set price = :price where chat_id = :chat_id", nativeQuery = true)
    void update(@Param("price")Double price, @Param("chat_id")Long chatId);

    @Query(value =
    "select price from user_telegram where chat_id = :chat_id", nativeQuery = true)
    Double getActiveSubscription(@Param("chat_id")Long chatId);

    @Transactional
    @Modifying
    @Query(value =
    "delete from user_telegram where chat_id = :chat_id", nativeQuery = true)
    void unsubscribe(@Param("chat_id")Long chatId);

    @Query(value =
    "select chat_id from user_telegram where price > :price", nativeQuery = true)
    List<Long> getChatList(@Param("price")Double price);
}
