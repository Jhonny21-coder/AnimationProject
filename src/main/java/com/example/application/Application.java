package com.example.application;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.component.page.Push;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@Push
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@Theme(value = "flowcrmtutorial", variant = Lumo.DARK)
@PWA(
    name = "The Animation Guild",
    shortName = "TAG",
    offlinePath="offline.html",
    offlineResources = { "./images/offline.png"}
)
public class Application implements AppShellConfigurator {

    public static void main(String[] args) {
    	SpringApplication.run(Application.class, args);
    }
}
