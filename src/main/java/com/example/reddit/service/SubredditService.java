package com.example.reddit.service;

import static java.util.stream.Collectors.toList;

import com.example.reddit.dto.SubredditDto;
import com.example.reddit.exception.SpringRedditException;
import com.example.reddit.mapper.SubredditMapper;
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
  private final SubredditMapper subredditMapper;

  @Transactional
  public SubredditDto save(SubredditDto subredditDto){
    Subreddit subreddit = subredditMapper.mapDtoToSubreddit(subredditDto);
    subredditRepository.save(subreddit);
    subredditDto.setId(subreddit.getId());
    return subredditDto;
  }

  @Transactional(readOnly = true)
  public List<SubredditDto> getAll() {
    return subredditRepository.findAll()
        .stream()
        .map(subredditMapper::mapSubredditToDto)
        .collect(toList());
  }


  public SubredditDto getSubreddit(Long id) {
    Subreddit subreddit = subredditRepository.findById(id)
        .orElseThrow(() -> new SpringRedditException("Sorry, no subreddit found with such id."));
    return subredditMapper.mapSubredditToDto(subreddit);
  }
}
