package com.example.reddit.controller;

import static org.springframework.http.ResponseEntity.status;

import com.example.reddit.dto.CommentsDto;
import com.example.reddit.service.CommentService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/comments")
@AllArgsConstructor
public class CommentsController {

  private final CommentService commentService;

  @PostMapping
  public ResponseEntity<Void> createComment(@RequestBody CommentsDto commentsDto){
    commentService.save(commentsDto);
    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  @GetMapping("/by-post/{postId}")
  public ResponseEntity<List<CommentsDto>> getAllCommentsForPost(@PathVariable("postId") Long postId) {
    return status(HttpStatus.OK)
        .body(commentService.getAllCommentsByPost(postId));
  }

  @GetMapping("/by-user/{userName}")
  public ResponseEntity<List<CommentsDto>> getAllCommentsByUser(@PathVariable("userName") String userName) {
    return status(HttpStatus.OK).body(commentService.getCommentsByUser(userName));
  }

}
