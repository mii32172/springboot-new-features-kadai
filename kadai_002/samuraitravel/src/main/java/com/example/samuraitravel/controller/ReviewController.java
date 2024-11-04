package com.example.samuraitravel.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.samuraitravel.entity.House;
import com.example.samuraitravel.entity.Review;
import com.example.samuraitravel.form.ReviewEditForm;
import com.example.samuraitravel.form.ReviewRegisterForm;
import com.example.samuraitravel.repository.HouseRepository;
import com.example.samuraitravel.repository.ReviewRepository;
import com.example.samuraitravel.security.UserDetailsImpl;
import com.example.samuraitravel.service.ReviewService;

@Controller
@RequestMapping("/review")
public class ReviewController {
	private final ReviewService reviewService;
	private final ReviewRepository reviewRepository;
	private final HouseRepository houseRepository;
	
	public ReviewController(ReviewService reviewService, ReviewRepository reviewRepository, HouseRepository houseReository) {
		this.reviewService = reviewService;
		this.reviewRepository = reviewRepository;
		this.houseRepository = houseReository;
	}
	
	//レビュー一覧への遷移
	@GetMapping("/{id}")
	public String review(@PathVariable(name = "id") Integer id, Model model, @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Direction.DESC) Pageable pageable) {
		Page<Review> reviewPage = reviewRepository.findByHouseId(id, pageable);
		House house = houseRepository.getReferenceById(id);

		model.addAttribute("house", house);
		model.addAttribute("reviewPage", reviewPage); 
		
		return "review/review"; 
	}
	
	@GetMapping("/registerform/{id}")
	public String register(@PathVariable(name = "id") Integer id, Model model) {
		House house = houseRepository.getReferenceById(id);
		Review review = new Review();
		
		model.addAttribute("house", house);
		model.addAttribute("review", review);
		model.addAttribute("reviewRegisterForm", new ReviewRegisterForm());
		
		return "review/reviewRegister";
	}
	
	@PostMapping("/{id}/create")
	public String create(@PathVariable(name = "id") Integer id, @ModelAttribute @Validated ReviewRegisterForm reviewRegisterForm, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model, @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
		House house = houseRepository.getReferenceById(id);
		Review review = new Review();
		
		model.addAttribute("house", house);
		model.addAttribute("review", review);
		
		if(bindingResult.hasErrors()) {

			model.addAttribute("errorMessage", "投稿内容に不備があります。");
			return "review/reviewRegister";
		}
		
		reviewService.create(id, reviewRegisterForm, userDetailsImpl);
		redirectAttributes.addFlashAttribute("successMessage", "レビューを登録しました。");
		
		return "redirect:/houses/{id}";
	}
	
	@GetMapping("/{id}/edit")
	public String edit(@PathVariable(name = "id") Integer id, Model model) {
		Review review = reviewRepository.getReferenceById(id);
		House house = houseRepository.getReferenceById(id);
		ReviewEditForm reviewEditForm = new ReviewEditForm(review.getId(),review.getStar(), review.getExplanation());
		
		model.addAttribute("review", review);		
		model.addAttribute("house", house);		
		model.addAttribute("reviewEditForm", reviewEditForm);
		
		return "review/edit";
	}
	
	@PostMapping("/{id}/update")
	public String update(@PathVariable(name = "id") Integer id, @ModelAttribute @Validated ReviewEditForm reviewEditForm, BindingResult bindingResult, RedirectAttributes redirectAttributes, @AuthenticationPrincipal UserDetailsImpl userDetailsImpl, Model model) {
		Review review = new Review();
		House house = houseRepository.getReferenceById(id);
		
		model.addAttribute("house", house);
		model.addAttribute("review", review);
		if(bindingResult.hasErrors()) {
		
			return "review/edit";
		}
		
		reviewService.update(id, reviewEditForm, userDetailsImpl);
		redirectAttributes.addFlashAttribute("successMessage", "レビューの内容を編集しました。");
		
		return "redirect:/houses";
	}
	
	@PostMapping("/{id}/delete")
	public String delete(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes, Model model) {
		Review review = reviewRepository.getReferenceById(id);
		
		model.addAttribute("review", review);
		reviewRepository.deleteById(id);
		
		redirectAttributes.addFlashAttribute("successMessage", "レビューを削除しました。");
		
		return "redirect:/houses";
	}
	
}