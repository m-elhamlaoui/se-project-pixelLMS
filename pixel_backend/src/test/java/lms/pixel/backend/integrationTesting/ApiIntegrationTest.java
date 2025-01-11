package lms.pixel.backend.integrationTesting;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.DeserializationFeature;


import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import lms.pixel.backend.model.*;
import lms.pixel.backend.repository.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UserRepository userRepository;

    private static String adminToken;
    private static String userToken;
    private static int testCourseId;
    private static int testTaskId;
    private static int testDiscussionId;

    @BeforeAll
    static void setup(@Autowired UserRepository userRepo) {
        // Ensure test users exist in the database
        if (userRepo.getUserByEmail("test.admin@pixel.com") == null) {
            User admin = new User();
            admin.setEmail("test.admin@pixel.com");
            admin.setName("Test Admin");
            admin.setRole("admin");
            LocalDate d = LocalDate.of(2020, 11, 11);
            admin.setBirthdate(d);
            userRepo.createUser(admin, "testadmin123");
        }

        if (userRepo.getUserByEmail("test.student@pixel.com") == null) {
            User user = new User();
            user.setEmail("test.student@pixel.com");
            user.setName("Test student");
            user.setRole("student");
            LocalDate d = LocalDate.of(2020, 11, 11);
            user.setBirthdate(d);

            userRepo.createUser(user, "teststudent123");
        }
    }

    @BeforeEach
    void setUp() throws Exception {
        if (adminToken == null) {
            adminToken = login("test.admin@pixel.com", "testadmin123");
        }

        if (userToken == null) {
            userToken = login("test.student@pixel.com", "teststudent123");
        }
    }

    private String login(String email, String password) throws Exception {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail(email);
        loginDTO.setPassword(password);

        return mockMvc.perform(post("/security/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @Test
    @Order(1)
    @Transactional
    void testAuthenticationFlow() throws Exception {
        // Test login with valid credentials
        LoginDTO validLogin = new LoginDTO();
        validLogin.setEmail("test.student@pixel.com");
        validLogin.setPassword("teststudent123");
    
        mockMvc.perform(post("/security/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validLogin)))
                .andExpect(status().isOk())
                .andExpect(content().string(not(emptyString())));
    
        // Test login with invalid credentials
        LoginDTO invalidLogin = new LoginDTO();
        invalidLogin.setEmail("wrong@test.com");
        invalidLogin.setPassword("wrong");
    
        mockMvc.perform(post("/security/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidLogin)))
                .andExpect(status().isUnauthorized());
    }
    

    @Test
    @Order(2)
    @Transactional
    void testCourseFlow() throws Exception {
        // Create new course
        Course newCourse = new Course();
        newCourse.setTitle("Integration Test Course");
        newCourse.setCourseid(1);
        newCourse.setDescription("Test Description");
        newCourse.setStatus("Non Commencée");
        mockMvc.perform(post("/api/course")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newCourse)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();


        // Verify course creation
        String response = mockMvc.perform(get("/api/course")
        .header("Authorization", "Bearer " + adminToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1))) // Expecting a list of size 1
        .andReturn()
        .getResponse()
        .getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);  

        List<Course> courses = objectMapper.readValue(response, new TypeReference<List<Course>>() {});
        Course course = courses.get(0) ;
        testCourseId = course.getCourseid();

    }



    @AfterAll
    static void cleanup(@Autowired JdbcTemplate jdbcTemplate) {

    }
}
