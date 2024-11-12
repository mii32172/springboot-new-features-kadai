package com.example.samuraitravel.service;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.samuraitravel.entity.Favorite;
import com.example.samuraitravel.entity.House;
import com.example.samuraitravel.entity.User;
import com.example.samuraitravel.repository.FavoriteRepository;
import com.example.samuraitravel.repository.HouseRepository;
import com.example.samuraitravel.security.UserDetailsImpl;

@Service
public class FavoriteService {

	private final FavoriteRepository favoriteRepository;
	private final HouseRepository houseRepository;
	
	public FavoriteService(FavoriteRepository favoriteRepository, HouseRepository houseRepository) {
		this.favoriteRepository = favoriteRepository;
		this.houseRepository = houseRepository;
	}
	
	/*お気に入り登録*/
	@Transactional
	public void add(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, Integer houseId) {
		Favorite favorite = new Favorite();
		User user = userDetailsImpl.getUser();
		House house =houseRepository.getReferenceById(houseId);
		
		favorite.setHouse(house);
		favorite.setUser(user);
		
		favoriteRepository.save(favorite);
	}
	
	

}