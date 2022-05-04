package com.example.reddit.service;

import static java.util.stream.Collectors.toList;

import com.example.reddit.dto.CommentsDto;
import com.example.reddit.exception.PostNotFoundException;
import com.example.reddit.exception.UsernameNotFoundException;
import com.example.reddit.mapper.CommentsMapper;
import com.example.reddit.model.Comment;
import com.example.reddit.model.NotificationEmail;
import com.example.reddit.model.Post;
import com.example.reddit.model.User;
import com.example.reddit.repository.CommentRepository;
import com.example.reddit.repository.PostRepository;
import com.example.reddit.repository.UserRepository;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CommentService {

  private final PostRepository postRepository;
  private final UserRepository userRepository;
  private final AuthService authService;
  private final CommentsMapper commentsMapper;
  private final CommentRepository commentRepository;
  private final MailContentBuilder mailContentBuilder;
  private final MailService mailService;

  public void save(CommentsDto commentsDto){
    Post post = postRepository.findById(commentsDto.getPostId())
        .orElseThrow(() -> new PostNotFoundException(commentsDto.getPostId().toString()));
    Comment comment = commentsMapper.map(commentsDto, post, authService.getCurrentUser());
    commentRepository.save(comment);

    //TODO post_url
    String message = mailContentBuilder
        .build(post.getUser().getUserName() + " posted a comment on your post." + "POST_URL");
    sendCommentNotification(message, post.getUser());
  }

  private void sendCommentNotification(String message, User user) {
    mailService.sendMail(new NotificationEmail(user.getUserName() + " Commented on your post", user.getEmail(), message));
  }

  public List<CommentsDto> getAllCommentsByPost(Long postId) {
    Post post = postRepository.findById(postId)
        .orElseThrow(() -> new PostNotFoundException(postId.toString()));
    return commentRepository.findByPost(post)
        .stream()
        .map(commentsMapper::mapToDto)
        .collect(toList());
  }

  public List<CommentsDto> getCommentsByUser(String userName) {
    User user = userRepository.findByUserName(userName)
        .orElseThrow(() -> new UsernameNotFoundException(userName));
    return commentRepository.findAllByUser(user)
        .stream()
        .map(commentsMapper::mapToDto)
        .collect(toList());
  }

}
