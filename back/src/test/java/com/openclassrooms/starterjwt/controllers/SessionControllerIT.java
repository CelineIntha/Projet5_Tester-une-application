package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.annotations.IT;
import com.openclassrooms.starterjwt.dto.SessionDto;
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
import com.fasterxml.jackson.databind.ObjectMapper;

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

    @Autowired
    private ObjectMapper objectMapper;

    @AfterEach
    public void tearDown() {
        sessionRepository.deleteAll();
        teacherRepository.deleteAll();
        userRepository.deleteAll();
    }

    /**
     * Teste la récupération d'une session existante par ID.
     * Doit renvoyer 200 OK avec les informations de la session.
     */
    @Test
    @WithMockUser(username = "authenticated_user@email.com")
    public void findById_ShouldReturnSession_WhenSessionExists() throws Exception {
        Teacher teacher = teacherRepository.save(new Teacher(null, "Doe", "John", null, null));
        Session session = sessionRepository.save(new Session(null, "Yoga Session", new Date(), "A relaxing yoga session", teacher, null, null, null));

        mockMvc.perform(get("/api/session/" + session.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Yoga Session"));
    }

    /**
     * Teste la récupération d'une session qui n'existe pas.
     * Doit renvoyer 404 Not Found.
     */
    @Test
    @WithMockUser(username = "authenticated_user@email.com")
    public void findById_ShouldReturnNotFound_WhenSessionDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/session/1234"))
                .andExpect(status().isNotFound());
    }

    /**
     * Teste la récupération de toutes les sessions.
     * Doit renvoyer 200 OK avec une liste de sessions.
     */
    @Test
    @WithMockUser(username = "authenticated_user@email.com")
    public void findAll_ShouldReturnSessionsList() throws Exception {
        Teacher teacher = teacherRepository.save(new Teacher(null, "Doe", "John", null, null));

        sessionRepository.save(new Session(null, "Yoga Session", new Date(), "A relaxing yoga session", teacher, null, null, null));
        sessionRepository.save(new Session(null, "Pilates Session", new Date(), "A pilates class", teacher, null, null, null));

        mockMvc.perform(get("/api/session"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    /**
     * Teste la suppression d'une session existante.
     * Doit renvoyer 200 OK et supprimer la session.
     */
    @Test
    @WithMockUser(username = "authenticated_user@email.com")
    public void delete_ShouldRemoveSession_WhenSessionExists() throws Exception {
        Teacher teacher = teacherRepository.save(new Teacher(null, "Doe", "John", null, null));
        Session session = sessionRepository.save(new Session(null, "Yoga Session", new Date(), "A yoga session", teacher, null, null, null));

        mockMvc.perform(delete("/api/session/" + session.getId()))
                .andExpect(status().isOk());

        Optional<Session> deletedSession = sessionRepository.findById(session.getId());
        assert (deletedSession.isEmpty());
    }

    /**
     * Teste l'ajout d'un utilisateur à une session existante.
     * Doit renvoyer 200 OK.
     */
    @Test
    @WithMockUser(username = "authenticated_user@email.com")
    public void participate_ShouldReturnOk_WhenValid() throws Exception {
        Teacher teacher = teacherRepository.save(new Teacher(null, "Doe", "John", null, null));
        Session session = sessionRepository.save(new Session(null, "Yoga Session", new Date(), "A yoga session", teacher, null, null, null));
        User user = userRepository.save(new User("john.doe@email.com", "Doe", "John", "password", false));

        mockMvc.perform(post("/api/session/" + session.getId() + "/participate/" + user.getId()))
                .andExpect(status().isOk());
    }

    /**
     * Teste la suppression d'un utilisateur d'une session.
     * Doit renvoyer 200 OK.
     */
    @Test
    @WithMockUser(username = "authenticated_user@email.com")
    public void noLongerParticipate_ShouldReturnOk_WhenValid() throws Exception {
        Teacher teacher = teacherRepository.save(new Teacher(null, "Doe", "John", null, null));

        Session session = sessionRepository.save(new Session(null, "Yoga Session", new Date(), "A yoga session", teacher, null, null, null));

        User user = userRepository.save(new User("john.doe@email.com", "Doe", "John", "password", false));

        // J'ajoute l'utilisateur à la session avant de tester la suppression
        mockMvc.perform(post("/api/session/" + session.getId() + "/participate/" + user.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/api/session/" + session.getId() + "/participate/" + user.getId()))
                .andExpect(status().isOk());
    }


    /**
     * Teste la création d'une nouvelle session.
     * Doit renvoyer 200 OK avec les détails de la session créée.
     */
    @Test
    @WithMockUser(username = "authenticated_user@email.com")
    public void create_ShouldReturnSession_WhenValid() throws Exception {
        Teacher teacher = teacherRepository.save(new Teacher(null, "Doe", "John", null, null));

        SessionDto sessionDto = new SessionDto(
                null,
                "New Session",
                new Date(),
                teacher.getId(),
                "A new session description",
                null,
                null,
                null
        );

        mockMvc.perform(post("/api/session")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(sessionDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Session"));
    }


    /**
     * Teste la mise à jour d'une session existante.
     * Doit renvoyer 200 OK avec les nouvelles informations mises à jour.
     */
    @Test
    @WithMockUser(username = "authenticated_user@email.com")
    public void update_ShouldReturnUpdatedSession_WhenValid() throws Exception {
        Teacher teacher = teacherRepository.save(new Teacher(null, "Doe", "John", null, null));
        Session session = sessionRepository.save(new Session(null, "Yoga Session", new Date(), "A yoga session", teacher, null, null, null));

        SessionDto updatedSession = new SessionDto(
                session.getId(),
                "Updated Session",
                new Date(),
                teacher.getId(),
                "Updated description",
                null,
                null,
                null
        );

        mockMvc.perform(put("/api/session/" + session.getId())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(updatedSession)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Session"));
    }

    /**
     * Teste la récupération d'une session avec un ID invalide.
     * Doit renvoyer 400 Bad Request.
     */
    @Test
    @WithMockUser
    public void findById_ShouldReturnBadRequest_WhenIdIsInvalid() throws Exception {
        mockMvc.perform(get("/api/session/invalid-id"))
                .andExpect(status().isBadRequest());
    }

    /**
     * Teste la mise à jour d'une session avec un ID invalide.
     * Doit renvoyer 400 Bad Request.
     */
    @Test
    @WithMockUser
    public void update_ShouldReturnBadRequest_WhenIdIsInvalid() throws Exception {
        Teacher teacher = teacherRepository.save(new Teacher(null, "Doe", "John", null, null));

        SessionDto updatedSession = new SessionDto(
                null,
                "Updated Session",
                new Date(),
                teacher.getId(),
                "Updated description",
                null, null, null
        );

        mockMvc.perform(put("/api/session/invalid-id")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(updatedSession)))
                .andExpect(status().isBadRequest());
    }

    /**
     * Teste la suppression d'une session inexistante.
     * Doit renvoyer 404 Not Found.
     */
    @Test
    @WithMockUser
    public void delete_ShouldReturnNotFound_WhenSessionDoesNotExist() throws Exception {
        mockMvc.perform(delete("/api/session/999"))
                .andExpect(status().isNotFound());
    }

    /**
     * Teste la suppression d'une session avec un ID invalide.
     * Doit renvoyer 400 Bad Request.
     */
    @Test
    @WithMockUser
    public void delete_ShouldReturnBadRequest_WhenIdIsInvalid() throws Exception {
        mockMvc.perform(delete("/api/session/invalid-id"))
                .andExpect(status().isBadRequest());
    }

    /**
     * Teste la participation à une session avec un utilisateur inexistant.
     * Doit renvoyer 404 Not Found.
     */
    @Test
    @WithMockUser
    public void participate_ShouldReturnNotFound_WhenUserDoesNotExist() throws Exception {
        Teacher teacher = teacherRepository.save(new Teacher(null, "Doe", "John", null, null));
        Session session = sessionRepository.save(new Session(null, "Yoga Session", new Date(), "A yoga session", teacher, null, null, null));

        mockMvc.perform(post("/api/session/" + session.getId() + "/participate/12391"))
                .andExpect(status().isNotFound());
    }

    /**
     * Teste la participation à une session avec un ID invalide.
     * Doit renvoyer 400 Bad Request.
     */
    @Test
    @WithMockUser
    public void participate_ShouldReturnBadRequest_WhenIdIsInvalid() throws Exception {
        mockMvc.perform(post("/api/session/invalid-id/participate/invalid-user"))
                .andExpect(status().isBadRequest());
    }

    /**
     * Teste l'annulation de la participation à une session avec un ID invalide.
     * Doit renvoyer 400 Bad Request.
     */
    @Test
    @WithMockUser
    public void noLongerParticipate_ShouldReturnBadRequest_WhenIdIsInvalid() throws Exception {
        mockMvc.perform(delete("/api/session/invalid-id/participate/invalid-user"))
                .andExpect(status().isBadRequest());
    }


}
