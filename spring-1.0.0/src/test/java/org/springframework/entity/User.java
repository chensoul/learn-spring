package org.springframework.entity;

public class User {
  private int id;
  private String name;

  public User() {
  }

  public User(int id) {
    this.id = id;
  }

  public User(int id, String name) {
    System.out.println("(int,String)构造函数---");
    this.id = id;
    this.name = name;
  }

  public User(String name, int id) {
    System.out.println("(String,int)构造函数---");
    this.id = id;
    this.name = name;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return "User{" +
            "id=" + id +
            ", name='" + name + '\'' +
            '}';
  }
}
