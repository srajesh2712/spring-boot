package com.feb.demo;
import lombok.*;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity 
@Table(name = "users") // This creates a table named 'users' in the DB
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id; // Databases need a Primary Key (ID)
    @Column(name = "name")
    private String name;
    @Column(name = "age")
    private int age;
    @Column(name = "status")
    private String status;

 
    

}