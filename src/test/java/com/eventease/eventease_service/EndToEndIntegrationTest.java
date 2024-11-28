package com.eventease.eventease_service;

import com.eventease.eventease_service.model.RSVP;
import com.eventease.eventease_service.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class EndToEndIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  public void testCreateUserEventAndRSVP() throws Exception {
    // Step 1: Create a caregiver user
    User caregiver = new User();
    caregiver.setFirstName("John");
    caregiver.setLastName("Doe");
    caregiver.setEmail("john.doe@example.com");
    caregiver.setPhoneNumber("1234567890");
    caregiver.setRole(User.Role.CAREGIVER);
    caregiver.setId(999L); // will be replaced with a generated ID

    MvcResult userResult = mockMvc.perform(post("/api/users/add")
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(caregiver)))
        .andExpect(status().isCreated())
        .andReturn();

    Long userId = extractUserId(userResult);

    // Step 2: Create an event
    MockMultipartFile[] images = {
        new MockMultipartFile(
            "images",
            "test.jpg",
            "image/jpeg",
            "test image content".getBytes()
        )
    };

    MvcResult eventResult = mockMvc.perform(multipart("/api/events")
            .file(images[0])
            .param("organizerId", userId.toString())
            .param("name", "Test Event")
            .param("time", LocalTime.now().toString())
            .param("date", LocalDate.now().plusDays(7).toString())
            .param("location", "Test Location")
            .param("description", "Test Description")
            .param("capacity", "50")
            .param("budget", "1000"))
        .andExpect(status().isCreated())
        .andReturn();

    Long eventId = extractEventId(eventResult);

    // Step 3: Create RSVP
    RSVP rsvp = new RSVP();
    rsvp.setStatus("ATTENDING");
    rsvp.setEventRole("PARTICIPANT");

    mockMvc.perform(post("/api/events/" + eventId + "/rsvp/" + userId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(rsvp)))
        .andExpect(status().isCreated());

    // Step 4: Verify RSVP
    MvcResult rsvpResult = mockMvc.perform(get("/api/events/" + eventId + "/attendees"))
        .andExpect(status().isOk())
        .andReturn();

    assertTrue(rsvpResult.getResponse().getContentAsString().contains("ATTENDING"));
  }

  private String asJsonString(Object obj) {
    try {
      return new ObjectMapper().writeValueAsString(obj);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private Long extractUserId(MvcResult result) throws Exception {
    JsonNode root = new ObjectMapper().readTree(result.getResponse().getContentAsString());
    return root.path("data").path("id").asLong();
  }

  private Long extractEventId(MvcResult result) throws Exception {
    JsonNode root = new ObjectMapper().readTree(result.getResponse().getContentAsString());
    return root.path("data").get(0).path("eventId").asLong();
  }
}