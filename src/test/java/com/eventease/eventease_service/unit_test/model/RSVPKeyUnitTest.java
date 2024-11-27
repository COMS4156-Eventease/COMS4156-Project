package com.eventease.eventease_service.unit_test.model;

import com.eventease.eventease_service.model.Event;
import com.eventease.eventease_service.model.RSVPKey;
import com.eventease.eventease_service.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

class RSVPKeyUnitTest {
  private User user1;
  private User user2;
  private Event event1;
  private Event event2;
  private RSVPKey rsvpKey1;
  private RSVPKey rsvpKey2;

  @BeforeEach
  void setUp() {
    user1 = new User();
    user2 = new User();
    event1 = new Event();
    event2 = new Event();
    rsvpKey1 = new RSVPKey(user1, event1);
    rsvpKey2 = new RSVPKey(user2, event2);
  }

  @Test
  @DisplayName("Test equals with same object")
  void testEqualsSameObject() {
    assertEquals(rsvpKey1, rsvpKey1);
  }

  @Test
  @DisplayName("Test equals with null")
  void testEqualsNull() {
    assertNotEquals(null, rsvpKey1);
  }

  @Test
  @DisplayName("Test equals with different class")
  void testEqualsDifferentClass() {
    Object obj = new Object();
    assertNotEquals(rsvpKey1, obj);
  }

  @Test
  @DisplayName("Test equals with identical values")
  void testEqualsIdenticalValues() {
    RSVPKey rsvpKey3 = new RSVPKey(user1, event1);
    assertEquals(rsvpKey1, rsvpKey3);
    assertEquals(rsvpKey1.hashCode(), rsvpKey3.hashCode());
  }

  @Test
  @DisplayName("Test equals with different values")
  void testEqualsDifferentValues() {
    assertNotEquals(rsvpKey1, rsvpKey2);
  }

  @Test
  @DisplayName("Test equals with different user same event")
  void testEqualsDifferentUserSameEvent() {
    RSVPKey rsvpKey3 = new RSVPKey(user2, event1);
    assertNotEquals(rsvpKey1, rsvpKey3);
  }

  @Test
  @DisplayName("Test equals with same user different event")
  void testEqualsSameUserDifferentEvent() {
    RSVPKey rsvpKey3 = new RSVPKey(user1, event2);
    assertNotEquals(rsvpKey1, rsvpKey3);
  }

  @Test
  @DisplayName("Test hashCode consistency")
  void testHashCodeConsistency() {
    int hashCode1 = rsvpKey1.hashCode();
    int hashCode2 = rsvpKey1.hashCode();
    assertEquals(hashCode1, hashCode2);
  }

  @Test
  @DisplayName("Test hashCode with null values")
  void testHashCodeWithNullValues() {
    RSVPKey rsvpKeyNull = new RSVPKey(null, null);
    assertDoesNotThrow(rsvpKeyNull::hashCode);
  }

  @Test
  @DisplayName("Test getters and setters")
  void testGettersAndSetters() {
    RSVPKey rsvpKey = new RSVPKey();
    rsvpKey.setUser(user1);
    rsvpKey.setEvent(event1);

    assertEquals(user1, rsvpKey.getUser());
    assertEquals(event1, rsvpKey.getEvent());
  }
}