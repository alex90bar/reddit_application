package com.example.reddit.exception;


public class SubredditNotFoundException extends RuntimeException {

  public SubredditNotFoundException(String exMessage) {super(exMessage);
  }
}
