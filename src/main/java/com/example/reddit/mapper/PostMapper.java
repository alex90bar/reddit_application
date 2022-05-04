package com.example.reddit.mapper;

import com.example.reddit.dto.PostRequest;
import com.example.reddit.dto.PostResponse;
import com.example.reddit.model.Post;
import com.example.reddit.model.Subreddit;
import com.example.reddit.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PostMapper {

  @Mapping(target = "createdDate", expression = "java(java.time.Instant.now())")
  @Mapping(target = "description", source = "postRequest.description")
  @Mapping(target = "subreddit", source = "subreddit")
  @Mapping(target = "user", source = "user")
  Post map(PostRequest postRequest, Subreddit subreddit, User user);

  @Mapping(target = "id", source = "postId")
  @Mapping(target = "subredditName", source = "subreddit.name")
  @Mapping(target = "userName", source = "user.userName")
  PostResponse mapToDto(Post post);

}