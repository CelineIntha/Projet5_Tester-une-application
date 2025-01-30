package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.annotations.IT;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.Date;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@IT
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class SessionControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    public void tearDown() {
        sessionRepository.deleteAll();
        teacherRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    public void findById_ShouldReturnSession_WhenSessionExists() throws Exception {
        Teacher teacher = teacherRepository.save(new Teacher(null, "Doe", "John", null, null));

        Session session = sessionRepository.save(new Session(null, "Yoga Session", new Date(), "A relaxing yoga session", teacher, null, null, null));

        mockMvc.perform(get("/api/session/" + session.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Yoga Session"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    public void findById_ShouldReturnNotFound_WhenSessionDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/session/1234"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    public void findAll_ShouldReturnSessionsList() throws Exception {
        Teacher teacher = teacherRepository.save(new Teacher(null, "Doe", "John", null, null));

        sessionRepository.save(new Session(null, "Yoga Session", new Date(), "A relaxing yoga session", teacher, null, null, null));
        sessionRepository.save(new Session(null, "Pilates Session", new Date(), "A pilates class", teacher, null, null, null));

        mockMvc.perform(get("/api/session"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    public void delete_ShouldRemoveSession_WhenSessionExists() throws Exception {
        Teacher teacher = teacherRepository.save(new Teacher(null, "Doe", "John", null, null));

        Session session = sessionRepository.save(new Session(null, "Yoga Session", new Date(), "A yoga session", teacher, null, null, null));

        mockMvc.perform(delete("/api/session/" + session.getId()))
                .andExpect(status().isOk());

        Optional<Session> deletedSession = sessionRepository.findById(session.getId());
        assert (deletedSession.isEmpty());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    public void participate_ShouldReturnOk_WhenValid() throws Exception {
        Teacher teacher = teacherRepository.save(new Teacher(null, "Doe", "John", null, null));

        Session session = sessionRepository.save(new Session(null, "Yoga Session", new Date(), "A yoga session", teacher, null, null, null));

        User user = userRepository.save(new User("john.doe@email.com", "Doe", "John", "password", false));

        mockMvc.perform(post("/api/session/" + session.getId() + "/participate/" + user.getId()))
                .andExpect(status().isOk());
    }

}
