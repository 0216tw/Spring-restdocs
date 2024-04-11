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

/*****
 * 
 * 주석 없는 버전은 UserTest.java 를 참고해주세요. 
 * @author TW
 *
 */

@WebMvcTest(UserController.class) //단위 테스트시 사용 (보통 WebMvcTest는 Controller 관련 빈만 로드하므로 Service까지 대상으로 안할경우 사용) 
//@SpringBootTest //통합테스트시 사용 ( 컨트롤러 , 서비스 등 실제 흐름을 통합적으로 타고 싶을 경우) 
@AutoConfigureMockMvc
@ExtendWith({ RestDocumentationExtension.class, SpringExtension.class })
@DisplayName("사용자 CRUD 기능으로 restDocs 테스트 (필기버전)")
class UserTestPractice {
	
	@Autowired
	private MockMvc mockMvc; //서버 실행 없이 테스트 환경에서 컨트롤러 실행을 하기 위해 모의(Mock) MVC 프레임워크 사용
	
	@MockBean
	private UserService userServiceMockBean; //UserService에 아직 미구현된 기능을 가짜로 만들기 위한 가짜 빈 사용 
	
	private List<UserDto> userRepository ;  //임시 데이터베이스 역할	

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//테스트 전 세팅 
	@BeforeEach
	void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation)
			throws Exception {
		
		//WebApplicationContext : 웹어플리케이션에서 사용하는 빈들을 관리하는 컨테이너 (웹 환경 지원, 스코프 지원 , 웹 자원 로딩 지원 등) 
		//RestDocumentationContextProvider : 테스트 중에 API 문서를 생성하고 관리하기 위한 컨텍스트 제공
		
		this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext) //mockMvc 객체를 설정하고 webApplicationContext 기반으로 함				
				.addFilters(new CharacterEncodingFilter("UTF-8", true))          //필터 추가 (한글깨짐 방지)
				.apply(documentationConfiguration(restDocumentation)             //API문서를 어떻게 구성할지 설정( 문서 생성 및 관리 설정 ) 
						.operationPreprocessors()                                //위에서 만든 문서에 대한 전처리를 세팅 (요청 응답 이쁘게 설정한다거나..)
						.withRequestDefaults(prettyPrint())                      //요청 데이터 보기 좋게 출력하기 
						.withResponseDefaults(prettyPrint())                     //응답 데이터 보기 좋게 출력하기 
						)
				.build();
	
		
		//테스트용 데이터 초기화	
		userRepository = new ArrayList<>();
		userRepository.add(new UserDto("user001", "일등일" , "010-1111-1111"));
		userRepository.add(new UserDto("user002", "이등이" , "010-2222-2222"));
		userRepository.add(new UserDto("user003", "삼등삼" , "010-3333-3333"));		
	}
	
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//테스트구간
//	@Test
	@DisplayName("1. 사용자 모두 조회하기")
	void findAllUserTest() throws Exception {
		
		//예상되는 json 리턴 값
		var json = """
				[
					{"userId":"user001","userName":"일등일","phone":"010-1111-1111"},
					{"userId":"user002","userName":"이등이","phone":"010-2222-2222"},
					{"userId":"user003","userName":"삼등삼","phone":"010-3333-3333"}
				]			
				""" ; 

		//만약 userService의 findAllUser() 메소드가 호출되면
		//실제 서비스 로직을 타는게 아니라 thenReturn 뒤의 값을 결과값으로 출력하겠다.
		//참고로 /users 를 Get 호출할때 Controller 내부에서 userService.findAllUser()를 호출하도록 되어있음
		when(userServiceMockBean.findAllUser()).thenReturn(userRepository);
		
		this.mockMvc.perform(get("/users"))          		                 // users 엔트포인트로 get방식 HTTP요청  
			.andDo(MockMvcResultHandlers.print())   					     //테스트 결과를 콘솔에 출력
			.andExpect(MockMvcResultMatchers.status().is2xxSuccessful())     //기댓값 : 이 요청의 HTTP응답상태코드는 2XX 일거야! 
			.andDo(document("find-all-user-test"  ,                         //★테스트 결과를 통한 AsciiDoc 스니펫을 이 이름으로 생성
					PayloadDocumentation.responseFields(                    //이건 응답필드에 대한 정리임 
	        		PayloadDocumentation.fieldWithPath("[].userId").description("사용자 아이디"),
		            PayloadDocumentation.fieldWithPath("[].userName").description("사용자 이름"), 
		            PayloadDocumentation.fieldWithPath("[].phone").description("사용자 연락처") 
							)))			
			.andExpect(MockMvcResultMatchers.content().json(json))           //기댓값 : 이 요청의 HTTP응답값은 이런 형태일거야! 
			.andExpect(result -> {
				if (result.getResolvedException() != null) {                 //만약 오류가 생기면 콘솔 상에 에러 내용 보여주기
					System.out.println("Error occurred : " + result.getResolvedException());
				}
			});		
	}
	
	
	
//	@Test
	@DisplayName("2. 특정 사용자 조회하기")
	void findUserTest() throws Exception {
		
		//입력값 : user001 
		//예상되는 json 리턴 값
		String userId = "user001";
		var json = """
				       {"userId":"user001","userName":"일등일","phone":"010-1111-1111"}
				   """ ; 
		
		UserDto expectResult = userRepository.get(0);  //user001의 데이터 추출		
		when(userServiceMockBean.findUser(userId)).thenReturn(expectResult);
		
		
		this.mockMvc.perform(get("/users/{userId}" , userId))          		 // users/user001을 get방식 HTTP요청  
			.andDo(MockMvcResultHandlers.print())   					     
			.andExpect(MockMvcResultMatchers.status().is2xxSuccessful())     
			.andDo(document("find-user-test"                               //★테스트 결과를 통한 AsciiDoc 스니펫을 이 이름으로 생성
					
					, pathParameters(                                       //path로 입력값을 받는 경우 
	                       parameterWithName("userId").description("사용자아이디(필수)")  //해당 입력 필드를 정의한다. 
	                )
					
//					, 
//					requestParameters(                                      //이건 쿼리스트링으로 받을 경우임 
//							 									            //ex) /users?type="jumin" 등 
//				            parameterWithName("type").description("요청할 타입 등")
//				        )	
										
			        , PayloadDocumentation.responseFields(                  //이건 응답필드에 대한 정리임 
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
	
	
//	@Test
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
		
		
		
		/* 주의할 점!!!! (3시간 정도 헤맨 거 ㅠㅠ) 
		 * 
		 * 주제 : when..thenReturn.. 을 할 때 클래스 타입 지정을 잘해야 한다.
		 * 
		 * 문제상황 : 
		 *         when(userServiceMockBean.insertUser(newUser)).thenReturn(userRepository);            <- 빈 리스트 리턴 (오류)
		 *         when(userServiceMockBean.insertUser(any(UserDto.class)).thenReturn(userRepository);  <- 정상 리턴 
		 * 
		 * 원인    :
		 *         위처럼 특정 서비스에 대해 Argument를 주는데 첫번째는 newUser라는 객체가 동일해야 하고 
		 *         아래는 UserDto 타입이기만 하면 된다. 
		 *         그러다보니 첫번째 예시는 Mockito가 입력받은 파라미터가 다르다고 판단을 했기에 아무것도 리턴을 하지 않은 것이다.
		 *         두번째 예시는 UserDto 타입을 가진 객체가 들어왔으므로 정상적으로 리턴을 했다. 
		 *  
		 * 결론    :
		 *         인자를 줄 때 class 형식을 이용하자 , 그리고 GPT 를 너무 맹신하지 말자; 
		 *         
		 * */ 

		UserDto newUser = new UserDto("user004" , "사등사" , "010-4444-4444");
		userRepository.add(newUser);
		
		//잘못된 코드
		//when(userServiceMockBean.insertUser(newUser)).thenReturn(userRepository);  

		//정상코드 
		when(userServiceMockBean.insertUser(any(UserDto.class))).thenReturn(userRepository);  

		
		//만약 메소드 호출시 추가적인 처리를 하고 싶다면 아래와 같은 방법도 사용가능 (doAnswer -> 메소드 호출시 추가 액션을 구현할 수 있음) 	
		//doAnswer(invocation -> {
	    //        UserDto newUser = invocation.getArgument(0); // 호출에 사용된 인자값 가져오기 	    	
	    //        userRepository.add(newUser);         // userRepository에 추가 (내경우 userRepositry에 직접 add 해놓음) 
	    //        return userRepository;               // 전체 사용자 목록 반환
	    //   
		//}).when(userServiceMockBean).insertUser(any(UserDto.class));
	    
	    
		this.mockMvc.perform(post("/users")          		 // /users 로 사용자 등록을 위해 POST방식으로 요청  
		    .contentType(MediaType.APPLICATION_JSON)         // 요청 데이터 타입 JSON 
			.content(inputJson))                             // 요청데이터body값
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
	

//	@Test
	@DisplayName("4. 특정 사용자 정보 수정하기")
	void updateUserTest() throws Exception {
	
		//변경하고자 하는 값 
		var inputJson = """
				{"userId":"user002","userName":"개명함","phone":"010-8282-5959"}
				""";

		//예상 출력 값
		var outputJson = """
				[
					{"userId":"user001","userName":"일등일","phone":"010-1111-1111"},
					{"userId":"user002","userName":"개명함","phone":"010-8282-5959"},
					{"userId":"user003","userName":"삼등삼","phone":"010-3333-3333"}
				]
				""";
		
		
		//이번에는 doAnswer를 통해서 메소드 호출시 내부적으로 값을 업데이트 해보도록 함 
		 
    	doAnswer(invocation -> {   //해당 메소드 호출(invocation)이 발생한 경우 

    		UserDto newUser = invocation.getArgument(0); // 호출에 사용된 인자값 가져오기	            

    		userRepository.forEach(user -> {
	        	if(user.getUserId().equals(newUser.getUserId())) {
	            	user.setUserName(newUser.getUserName());
	            	user.setPhone(newUser.getPhone());
	            }
	        });
	        return userRepository;    // 변경 후 사용자 목록 반환
	        
		}).when(userServiceMockBean).updateUser(any(UserDto.class));

    	
		this.mockMvc.perform(put("/users")          		 // /users 로 사용자 수정을 표현하기 위해 put 방식을 사용  
		    .contentType(MediaType.APPLICATION_JSON)         // 요청 데이터 타입 JSON 
			.content(inputJson))                             // 요청데이터body값
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
	

	
//	@Test
	@DisplayName("5. 특정 사용자 정보 삭제하기")
	void deleteUserTest() throws Exception {
	
		//삭제할 아이디
		String userId = "user002"; 

		//예상 출력 값
		var outputJson = """
				[
					{"userId":"user001","userName":"일등일","phone":"010-1111-1111"},
					{"userId":"user003","userName":"삼등삼","phone":"010-3333-3333"}
				]
				""";
		
		doAnswer(invocation -> {   
    		
    		userRepository.removeIf(user-> user.getUserId().equals((String)invocation.getArgument(0)));
	        return userRepository;    //삭제 후 사용자 목록 반환
	        
		}).when(userServiceMockBean).deleteUser(any(String.class));
			
		
		
		this.mockMvc.perform(delete("/users/{userId}" , userId))          		 // 사용자 삭제 표현하기 위해 delete 방식을 사용  
			.andDo(MockMvcResultHandlers.print())   					     
			.andExpect(MockMvcResultMatchers.status().is2xxSuccessful())     
			.andDo(document("delete-user-test" ,  
					
					
					pathParameters(                                       //path로 입력값을 받는 경우 
		                   parameterWithName("userId").description("사용자아이디(필수)")  //해당 입력 필드를 정의한다. 
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
