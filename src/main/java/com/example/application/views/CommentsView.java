package com.example.application.views;

import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.*;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.HasUrlParameter;
import com.example.application.data.*;
import com.example.application.services.*;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.*;
import java.util.List;
import java.time.LocalDate;

import com.vaadin.flow.server.StreamResource;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.ArrayList;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.messages.MessageList;
import com.vaadin.flow.component.messages.MessageListItem;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.messages.MessageInput;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.component.notification.Notification;

@Route("commentsview")
public class CommentsView extends AppLayout implements HasUrlParameter<Long> {

    private final CommentService commentService;
    private final UserServices userService;
    private String submittedText;
    private String originalFilename;

    public CommentsView(CommentService commentService, UserServices userService) {
        this.commentService = commentService;
        this.userService = userService;

        addClassName("comments-main");
    }

    @Override
    public void setParameter(BeforeEvent event, Long userId) {
        List<Comment> comments = commentService.getCommentsByUserId(userId);
        displayComments(userId, comments);
    }

    public void displayComments(Long userId, List<Comment> comments){
	TextField input = new TextField();

	User user = userService.getUserById(userId);
	User current = userService.findCurrentUser();

	String email = user.getEmail();

	String modifiedFullName = user.getFullName();

        if(modifiedFullName.endsWith("s")){
            modifiedFullName += "'";
        }else{
            modifiedFullName += "'s";
        }

        H1 name = new H1(modifiedFullName + " comments");
	name.addClassName("comment-name");

	Icon backIcon = new Icon(VaadinIcon.ARROW_BACKWARD);
	backIcon.addClassName("back-icon");
	backIcon.addClickListener(event -> {
	    getUI().ifPresent(ui -> ui.navigate(EmcView.class));
	});

        MessageList list = new MessageList();
        list.addClassName("message-list");

	// Create icons
	Icon icon1 = new Icon(VaadinIcon.SMILEY_O);
	Icon icon2 = new Icon(VaadinIcon.SPECIALIST);
	Icon icon3 = new Icon(VaadinIcon.CAMERA);
	Icon send = new Icon(VaadinIcon.PAPERPLANE);
	send.addClassName("send-icon");
	icon1.addClassName("icons");
	icon2.addClassName("icons");
	icon3.addClassName("icons");

	Upload upload = new Upload(new MemoryBuffer());
        // Set the uploadButton as the upload button
        upload.setUploadButton(icon3);
	upload.addClassName("comments-upload");
        upload.setAcceptedFileTypes("image/*"); /*/*/
        upload.addSucceededListener(event -> {
            MemoryBuffer buffer = (MemoryBuffer) upload.getReceiver();

            try {
                InputStream inputStream = buffer.getInputStream();
                byte[] bytes = inputStream.readAllBytes();
                originalFilename = event.getFileName();
		input.setValue(event.getFileName());
                StreamResource resource = new StreamResource(originalFilename, () -> new ByteArrayInputStream(bytes));

                // Save the uploaded file to the specified directory
                String filePath = "/data/data/com.termux/files/home/MyVaadinProject/src/main/resources/META-INF/resources/comments_images/" + originalFilename;
                FileOutputStream outputStream = new FileOutputStream(filePath);
                outputStream.write(bytes);

            } catch (IOException e) {
                Notification.show("Error uploading artwork image", 3000, Notification.Position.TOP_CENTER);
            }
        });

	HorizontalLayout iconsLayout = new HorizontalLayout(icon2, upload, icon1);
        iconsLayout.addClassName("icons-comments");

	input.getElement().getStyle().set("--lumo-border-radius", "10px");
	input.setPlaceholder("Add comment...");
	// Set the layout as the suffix component of the input field
        input.setSuffixComponent(iconsLayout);

        addToNavbar(true, input, send);

        input.addClassName("input");

        Instant newDateTime = Instant.now();
        String newFullName = current.getFullName();
	String newImage = current.getProfileImage();

        for(Comment comment : comments){
	    String userImage = "./register_images/" + comment.getUserImage();

	     MessageListItem message = new MessageListItem(comment.getComments(),
             	comment.getDateTime(), comment.getFullName(), userImage);

	     message.addThemeNames("message");

	     List<MessageListItem> items = new ArrayList<>(list.getItems());
             items.add(message);
             list.setItems(items);
	 }

         send.addClickListener(submitEvent -> {
             submittedText = input.getValue();

	     input.setValue("");

             // Check if the submitted text length exceeds 28 characters
             if (submittedText.length() > 28) {
                 // Insert newline characters every 28 characters
                 submittedText = submittedText.replaceAll("(.{28})", "$1\n");
             }

	     String newImage2 = "./register_images/" + newImage;
             // Create a new MessageListItem with the modified text
             MessageListItem newMessage = new MessageListItem(submittedText, newDateTime,
			 newFullName, newImage2);

	     newMessage.addThemeNames("message");

             // Get the current list of items
             List<MessageListItem> items = new ArrayList<>(list.getItems());
             // Add the new message to the list
             items.add(newMessage);

             // Update the items in the MessageList
             list.setItems(items);

	     commentService.saveComment(email, newFullName, newDateTime, submittedText,
			newImage);
         });

         VerticalLayout chatLayout = new VerticalLayout(list);
         chatLayout.expand(list);
         chatLayout.addClassName("chatLayout");

         setContent(chatLayout);
         addToNavbar(backIcon, name);
     }
}
