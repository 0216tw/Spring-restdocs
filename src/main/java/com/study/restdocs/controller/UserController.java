package com.study.restdocs.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.study.restdocs.dto.UserDto;
import com.study.restdocs.service.UserService;


@RestController
public class UserController {

	@Autowired
	UserService userService; //아직 구현되지 않은 상황 (테스트에서 Mock객체를 이용해서 임의 데이터를 처리할 것임)
	
	@GetMapping("/users")  //모든 사용자 조회
	public ResponseEntity<List<UserDto>> findAllUser() {  
		return ResponseEntity.ok().body(userService.findAllUser());
	}
	
	@GetMapping("/users/{userId}")  //특정 사용자 조회
	public ResponseEntity<UserDto> findUser(@PathVariable("userId") String userId) {  
		return ResponseEntity.ok().body(userService.findUser(userId)); 		
	}
	
	@PostMapping("/users")   //특정 사용자 등록 
	public ResponseEntity<List<UserDto>> insertUser(@RequestBody UserDto userDto) { 
		return ResponseEntity.ok().body(userService.insertUser(userDto));
	}
	
	
	@PutMapping("/users")  //특정 사용자 수정
	public ResponseEntity<List<UserDto>> updateUser(@RequestBody UserDto userDto) throws Exception {
		return ResponseEntity.ok().body(userService.updateUser(userDto));
	}

	@DeleteMapping("/users/{userId}")  //특정 사용자 삭제
	public ResponseEntity<List<UserDto>> deleteUser(@PathVariable("userId") String userId) throws Exception {
		return ResponseEntity.ok().body(userService.deleteUser(userId));
	}	
}
