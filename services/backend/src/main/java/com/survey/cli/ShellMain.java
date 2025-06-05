package com.survey.cli;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;

@SpringBootApplication
@Profile("shell")
public class ShellMain {
  public static void main(String[] args) {
    SpringApplication app = new SpringApplication(ShellMain.class);
    app.setWebApplicationType(WebApplicationType.NONE);
    app.run(args);
  }
}
