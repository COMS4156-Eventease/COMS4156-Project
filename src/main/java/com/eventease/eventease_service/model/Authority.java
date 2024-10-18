package com.eventease.eventease_service.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;

/**
 * The Authority class is a model for the authorities of a user, e.g. if it
 * has the permissions to create, organise and manage events.
 */
@Entity
@Table(name = "authority")
public class Authority implements Serializable {
  private static final long serialVersionUID = 100002L;

  @Id
  private Long id;
  private String authority;

  public Authority() {}

  public Authority(Long id, String authority) {
    this.id = id;
    this.authority = authority;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getAuthority() {
    return authority;
  }

  public void setAuthority(String authority) {
    this.authority = authority;
  }
}
