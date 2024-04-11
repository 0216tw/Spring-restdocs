package com.study.restdocs.user;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;


import java.util.ArrayList;
import java.util.List;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.PayloadDocumentation;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import com.study.restdocs.controller.UserController;
import com.study.restdocs.dto.UserDto;
import com.study.restdocs.service.UserService;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;

@WebMvcTest(UserController.class) 
//@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith({ RestDocumentationExtension.class, SpringExtension.class })
@DisplayName("사용자 CRUD 기능으로 restDocs 테스트")
class UserTest {
	
	@Autowired
	private MockMvc mockMvc; 
	
	@MockBean
	private UserService userServiceMockBean; 
	
	private List<UserDto> userRepository ; 

	@BeforeEach
	void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation)
			throws Exception {
	
		this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext) 				
				.addFilters(new CharacterEncodingFilter("UTF-8", true))          
				.apply(documentationConfiguration(restDocumentation)             
						.operationPreprocessors()                                
						.withRequestDefaults(prettyPrint())                      
						.withResponseDefaults(prettyPrint())                     
						)
				.build();
	
		userRepository = new ArrayList<>();
		userRepository.add(new UserDto("user001", "일등일" , "010-1111-1111"));
		userRepository.add(new UserDto("user002", "이등이" , "010-2222-2222"));
		userRepository.add(new UserDto("user003", "삼등삼" , "010-3333-3333"));		
	}
	
	@Test
	@DisplayName("1. 사용자 모두 조회하기")
	void findAllUserTest() throws Exception {
		
		var json = """
				[
					{"userId":"user001","userName":"일등일","phone":"010-1111-1111"},
					{"userId":"user002","userName":"이등이","phone":"010-2222-2222"},
					{"userId":"user003","userName":"삼등삼","phone":"010-3333-3333"}
				]			
				""" ; 

		when(userServiceMockBean.findAllUser()).thenReturn(userRepository);
		
		this.mockMvc.perform(get("/users"))          		                
			.andDo(MockMvcResultHandlers.print())   					    
			.andExpect(MockMvcResultMatchers.status().is2xxSuccessful())     
			.andDo(document("find-all-user-test"  ,                         
					PayloadDocumentation.responseFields(                    
	        		PayloadDocumentation.fieldWithPath("[].userId").description("사용자 아이디"),
		            PayloadDocumentation.fieldWithPath("[].userName").description("사용자 이름"), 
		            PayloadDocumentation.fieldWithPath("[].phone").description("사용자 연락처") 
							)))			
			.andExpect(MockMvcResultMatchers.content().json(json))          
			.andExpect(result -> {
				if (result.getResolvedException() != null) {               
					System.out.println("Error occurred : " + result.getResolvedException());
				}
			});		
	}
	
		
	@Test
	@DisplayName("2. 특정 사용자 조회하기")
	void findUserTest() throws Exception {
		
		String userId = "user001";
		var json = """
				       {"userId":"user001","userName":"일등일","phone":"010-1111-1111"}
				   """ ; 
		
		UserDto expectResult = userRepository.get(0);  		
		when(userServiceMockBean.findUser(userId)).thenReturn(expectResult);
		
		this.mockMvc.perform(get("/users/{userId}" , userId))          		 
			.andDo(MockMvcResultHandlers.print())   					     
			.andExpect(MockMvcResultMatchers.status().is2xxSuccessful())     
			.andDo(document("find-user-test"                               					
					, pathParameters(                                      
	                       parameterWithName("userId").description("사용자아이디(필수)") 
	                )
			        , PayloadDocumentation.responseFields(                
			        		PayloadDocumentation.fieldWithPath("userId").description("사용자 아이디"),
				            PayloadDocumentation.fieldWithPath("userName").description("사용자 이름"), 
				            PayloadDocumentation.fieldWithPath("phone").description("사용자 연락처") 
		            )))			                      
			.andExpect(MockMvcResultMatchers.content().json(json))           
			.andExpect(result -> {
				if (result.getResolvedException() != null) {              
					System.out.println("Error occurred : " + result.getResolvedException());
				}
			});		
	}
	
	
	@Test
	@DisplayName("3. 특정 사용자 등록하기")
	void insertUserTest() throws Exception {
	
		var inputJson = """
				{"userId":"user004","userName":"사등사","phone":"010-4444-4444"}
				""";

		var outputJson = """
				[
					{"userId":"user001","userName":"일등일","phone":"010-1111-1111"},
					{"userId":"user002","userName":"이등이","phone":"010-2222-2222"},
					{"userId":"user003","userName":"삼등삼","phone":"010-3333-3333"}, 
					{"userId":"user004","userName":"사등사","phone":"010-4444-4444"}
				]
				""";
		
	
		UserDto newUser = new UserDto("user004" , "사등사" , "010-4444-4444");
		userRepository.add(newUser);
		
		when(userServiceMockBean.insertUser(any(UserDto.class))).thenReturn(userRepository);  	    
	    
		this.mockMvc.perform(post("/users")          		 
		    .contentType(MediaType.APPLICATION_JSON)        
			.content(inputJson))                            
			.andDo(MockMvcResultHandlers.print())   					     
			.andExpect(MockMvcResultMatchers.status().is2xxSuccessful())     
			.andDo(document("insert-user-test"
					, 		
			        PayloadDocumentation.responseFields(
			                PayloadDocumentation.fieldWithPath("[].userId").description("사용자아이디"),
			                PayloadDocumentation.fieldWithPath("[].userName").description("사용자 이름"), 
			                PayloadDocumentation.fieldWithPath("[].phone").description("사용자 연락처") 				                
			            ),
			            PayloadDocumentation.requestFields(
			                PayloadDocumentation.fieldWithPath("userId").description("사용자아이디(필수)"),
			                PayloadDocumentation.fieldWithPath("userName").description("사용자 이름(필수)"), 
			                PayloadDocumentation.fieldWithPath("phone").description("사용자 연락처(필수)")
			                
			            )))     
			.andExpect(MockMvcResultMatchers.content().json(outputJson))           
			.andExpect(result -> {
				if (result.getResolvedException() != null) {              
					System.out.println("Error occurred : " + result.getResolvedException());
					} 
			});			
	}
	
	@Test
	@DisplayName("4. 특정 사용자 정보 수정하기")
	void updateUserTest() throws Exception {

		var inputJson = """
				{"userId":"user002","userName":"개명함","phone":"010-8282-5959"}
				""";

		var outputJson = """
				[
					{"userId":"user001","userName":"일등일","phone":"010-1111-1111"},
					{"userId":"user002","userName":"개명함","phone":"010-8282-5959"},
					{"userId":"user003","userName":"삼등삼","phone":"010-3333-3333"}
				]
				""";
		 
    	doAnswer(invocation -> {   

    		UserDto newUser = invocation.getArgument(0);           

    		userRepository.forEach(user -> {
	        	if(user.getUserId().equals(newUser.getUserId())) {
	            	user.setUserName(newUser.getUserName());
	            	user.setPhone(newUser.getPhone());
	            }
	        });
	        return userRepository;    
	        
		}).when(userServiceMockBean).updateUser(any(UserDto.class));
 	
		this.mockMvc.perform(put("/users")          		 
		    .contentType(MediaType.APPLICATION_JSON)         
			.content(inputJson))                             
			.andDo(MockMvcResultHandlers.print())   					     
			.andExpect(MockMvcResultMatchers.status().is2xxSuccessful())     
			.andDo(document("update-user-test" , 

					PayloadDocumentation.responseFields(
	                PayloadDocumentation.fieldWithPath("[].userId").description("사용자아이디"),
	                PayloadDocumentation.fieldWithPath("[].userName").description("사용자 이름"), 
	                PayloadDocumentation.fieldWithPath("[].phone").description("사용자 연락처") 				                
	            ),
	            PayloadDocumentation.requestFields(
	                PayloadDocumentation.fieldWithPath("userId").description("사용자아이디(필수)"),
	                PayloadDocumentation.fieldWithPath("userName").description("사용자 이름(필수)"), 
	                PayloadDocumentation.fieldWithPath("phone").description("사용자 연락처(필수)")
	                
	            )))
			.andExpect(MockMvcResultMatchers.content().json(outputJson))           
			.andExpect(result -> {
				if (result.getResolvedException() != null) {              
					System.out.println("Error occurred : " + result.getResolvedException());
					} 
			});			
	}
	
	
	@Test
	@DisplayName("5. 특정 사용자 정보 삭제하기")
	void deleteUserTest() throws Exception {
	
		String userId = "user002"; 

		var outputJson = """
				[
					{"userId":"user001","userName":"일등일","phone":"010-1111-1111"},
					{"userId":"user003","userName":"삼등삼","phone":"010-3333-3333"}
				]
				""";
		
		doAnswer(invocation -> {     		
    		userRepository.removeIf(user-> user.getUserId().equals((String)invocation.getArgument(0)));
	        return userRepository;    	        
		}).when(userServiceMockBean).deleteUser(any(String.class));
				
		this.mockMvc.perform(delete("/users/{userId}" , userId))          
			.andDo(MockMvcResultHandlers.print())   					     
			.andExpect(MockMvcResultMatchers.status().is2xxSuccessful())     
			.andDo(document("delete-user-test" 	,
					pathParameters(                                    
			                   parameterWithName("userId").description("사용자아이디(필수)")  
			              ) , 

						PayloadDocumentation.responseFields(
		                PayloadDocumentation.fieldWithPath("[].userId").description("사용자아이디"),
		                PayloadDocumentation.fieldWithPath("[].userName").description("사용자 이름"), 
		                PayloadDocumentation.fieldWithPath("[].phone").description("사용자 연락처") 				                
		            )))
		        
			.andExpect(MockMvcResultMatchers.content().json(outputJson))           
			.andExpect(result -> {
				if (result.getResolvedException() != null) {              
					System.out.println("Error occurred : " + result.getResolvedException());
					} 
			});			
	}
}
