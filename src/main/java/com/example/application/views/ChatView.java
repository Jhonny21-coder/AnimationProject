package com.example.application.views;

import com.example.application.data.User;
import com.example.application.services.UserServices;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.Component;

import com.vaadin.collaborationengine.UserInfo;
import com.vaadin.collaborationengine.CollaborationEngine;
import com.vaadin.collaborationengine.CollaborationMessage;
import com.vaadin.collaborationengine.CollaborationMessageList;
import com.vaadin.collaborationengine.CollaborationAvatarGroup;
import com.vaadin.collaborationengine.CollaborationMessageInput;

@Route("chat-view")
public class ChatView extends VerticalLayout {

    private final UserServices userService;
    private UserInfo userInfo;

    public ChatView(UserServices userService){
	this.userService = userService;

	User user = userService.findCurrentUser();

	userInfo = new UserInfo(user.getFullName(), user.getFullName());

	var content = new HorizontalLayout(getChatLayout());

	setWidth(null);
	setHeightFull();
	add(getHeader(), content);
	expand(content);
    }

    private Component getChatLayout(){
    	var chatLayout = new VerticalLayout();

	var messageList = new CollaborationMessageList(userInfo, "chat");

	var messageInput = new CollaborationMessageInput(messageList);

	chatLayout.add(new H2("Chat"), messageList, messageInput);
	chatLayout.expand(messageList);
	chatLayout.setHeightFull();
	chatLayout.addClassName("bg-contrast-5");
    	return chatLayout;
    }

    private Component getHeader(){
    	var header = new HorizontalLayout();
    	header.setWidthFull();
    	header.setAlignItems(FlexComponent.Alignment.BASELINE);

	var avatars = new CollaborationAvatarGroup(userInfo, "avatars");
	avatars.getStyle().set("width", "unset");

    	var h1 = new H1("Chat App");

    	header.add(h1, avatars);
    	header.expand(h1);

    	return header;
    }
}
