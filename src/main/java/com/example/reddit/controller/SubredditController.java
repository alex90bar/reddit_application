package com.example.reddit.controller;

import com.example.reddit.dto.SubredditDto;
import com.example.reddit.service.SubredditService;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/api/subreddit")
public class SubredditController {

  private final SubredditService subredditService;

  @PostMapping
  public ResponseEntity<SubredditDto> createSubreddit(@RequestBody SubredditDto subredditDto){
    return ResponseEntity.status(HttpStatus.CREATED).body(subredditService.save(subredditDto));
  }

  @GetMapping
  public ResponseEntity<List<SubredditDto>> getAllSubreddits(){
    return ResponseEntity.status(HttpStatus.OK).body(subredditService.getAll());
  }

}
