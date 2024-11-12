package com.example.samuraitravel.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.samuraitravel.entity.Favorite;
import com.example.samuraitravel.entity.House;
import com.example.samuraitravel.entity.User;

public interface FavoriteRepository extends JpaRepository<Favorite, Integer>{
    Page<Favorite> findByUser(User user, Pageable pageable);

	List<Favorite> findByHouseAndUser(House house, User user);

	default boolean favoriteJudge(House house,User user) {
		//Listが空でないかをチェックしてお気に入りが存在するかどうかを判断
		return !findByHouseAndUser(house, user).isEmpty();
	}


}