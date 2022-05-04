package com.example.reddit.repository;

import com.example.reddit.model.Post;
import com.example.reddit.model.Subreddit;
import com.example.reddit.model.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

  List<Post> findAllBySubreddit(Subreddit subreddit);

  List<Post> findByUser(User user);
}
