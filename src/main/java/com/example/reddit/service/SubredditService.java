package com.example.reddit.service;

import static java.util.stream.Collectors.toList;

import com.example.reddit.dto.SubredditDto;
import com.example.reddit.model.Subreddit;
import com.example.reddit.repository.SubredditRepository;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Service
@Slf4j
public class SubredditService {

  private final SubredditRepository subredditRepository;

  @Transactional
  public SubredditDto save(SubredditDto subredditDto){
    Subreddit subreddit = mapSubredditDto(subredditDto);
    subredditRepository.save(subreddit);
    subredditDto.setId(subreddit.getId());
    return subredditDto;
  }

  @Transactional(readOnly = true)
  public List<SubredditDto> getAll() {
    return subredditRepository.findAll()
        .stream()
        .map(this::mapToDto)
        .collect(toList());
  }

  private SubredditDto mapToDto(Subreddit subreddit) {
    return SubredditDto.builder().name(subreddit.getName())
        .id(subreddit.getId())
        .numberOfPosts(subreddit.getPosts().size())
        .build();
  }

  private Subreddit mapSubredditDto(SubredditDto subredditDto) {
    return Subreddit.builder().name(subredditDto.getName())
        .description(subredditDto.getDescription())
        .build();
  }

}
