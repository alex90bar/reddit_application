package com.example.reddit.service;

import static java.util.stream.Collectors.toList;

import com.example.reddit.dto.PostRequest;
import com.example.reddit.dto.PostResponse;
import com.example.reddit.exception.PostNotFoundException;
import com.example.reddit.exception.UsernameNotFoundException;
import com.example.reddit.mapper.PostMapper;
import com.example.reddit.model.Post;
import com.example.reddit.model.Subreddit;
import com.example.reddit.model.User;
import com.example.reddit.repository.PostRepository;
import com.example.reddit.repository.SubredditRepository;
import com.example.reddit.exception.SubredditNotFoundException;
import com.example.reddit.repository.UserRepository;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class PostService {

  private final SubredditRepository subredditRepository;
  private final AuthService authService;
  private final PostRepository postRepository;
  private final UserRepository userRepository;
  private final PostMapper postMapper;

  public void save(PostRequest postRequest) {
    Subreddit subreddit = subredditRepository.findByName(postRequest.getSubredditName())
        .orElseThrow(() -> new SubredditNotFoundException(postRequest.getSubredditName()));

    postRepository.save(postMapper.map(postRequest, subreddit, authService.getCurrentUser()));
  }

  @Transactional(readOnly = true)
  public PostResponse getPost(Long id) {
    Post post = postRepository.findById(id)
        .orElseThrow(() -> new PostNotFoundException(id.toString()));
    return postMapper.mapToDto(post);
  }

  @Transactional(readOnly = true)
  public List<PostResponse> getAllPosts() {
    return postRepository.findAll()
        .stream()
        .map(postMapper::mapToDto)
        .collect(toList());
  }

  @Transactional(readOnly = true)
  public List<PostResponse> getPostsBySubreddit(Long subredditId) {
    Subreddit subreddit = subredditRepository.findById(subredditId)
        .orElseThrow(() -> new SubredditNotFoundException(subredditId.toString()));
    List<Post> posts = postRepository.findAllBySubreddit(subreddit);
    return posts.stream().map(postMapper::mapToDto).collect(toList());
  }

  @Transactional(readOnly = true)
  public List<PostResponse> getPostsByUsername(String username) {
    User user = userRepository.findByUserName(username)
        .orElseThrow(() -> new UsernameNotFoundException(username));
    return postRepository.findByUser(user)
        .stream()
        .map(postMapper::mapToDto)
        .collect(toList());
  }
}
