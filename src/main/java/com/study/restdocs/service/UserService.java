package com.study.restdocs.service;
import java.util.List;
import org.springframework.stereotype.Service;
import com.study.restdocs.dto.UserDto;

@Service
public class UserService {
	
	/*
	 *  아직 Service단의 메소드만 정하고 
	 *  실제 구현을 하지 않은 상황
	 */
	
	public List<UserDto> findAllUser() {
		return null; 
	}

	public UserDto findUser(String userId) {
		return null;
	}
	
	public List<UserDto> insertUser(UserDto userDto) {
		return null; 
	}
	
	public List<UserDto> updateUser(UserDto userDto) throws Exception {
		return null; 
	}
	
	public List<UserDto> deleteUser(String userId) throws Exception {
		return null; 
	}
	
}
