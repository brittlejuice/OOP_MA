package com.example.demo.model;

import lombok.*;

import jakarta.persistence.*;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="Comment")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int commentId;
    
    @Column
    private String commentBody;

}
