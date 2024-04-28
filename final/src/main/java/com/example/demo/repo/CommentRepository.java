package com.example.demo.repo;

import com.example.demo.model.Comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {
    @Modifying
    @Query("UPDATE Comment p SET p.commentBody = :commentBody WHERE p.commentId = :commentId")
    void updateCommentBody(@Param("commentId") int commentId, @Param("commentBody") String commentBody);
}