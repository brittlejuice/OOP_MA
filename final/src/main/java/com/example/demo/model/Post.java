package com.example.demo.model;

import lombok.*;

import jakarta.persistence.*;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.Comment;

@Entity
@Table(name="Post")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int postId;

    @Column
    private String postBody;
    
    // @ManyToOne
    // @JoinColumn(name = "user_id")
    //private User user;
    @Column
    private Date date = new Date();

    //private List<CommentClass> comments = new ArrayList<>();

}
