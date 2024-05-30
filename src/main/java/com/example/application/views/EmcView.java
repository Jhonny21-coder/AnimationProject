package com.example.application.views;

import com.example.application.views.artworks.ArtworkView;
import com.example.application.repository.*;
import com.example.application.data.*;
import com.example.application.services.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dialog.*;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.messages.MessageInput;
import com.vaadin.flow.component.messages.MessageList;
import com.vaadin.flow.component.messages.MessageListItem;
import com.vaadin.flow.server.StreamResource;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;
import java.util.List;
import java.io.FileInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;

@Route(value = "emc-view", layout=MainLayout.class)
@PageTitle("EmcView | TAG")
public class EmcView extends VerticalLayout {

    private final CommentService commentService;
    private final UserServices userService;
    private final ArtworkService artworkService;
    private final StudentInfoService studentInfoService;
    private String submittedText;
    private final LikeReactionService likeReactionService;
    private final HeartReactionService heartReactionService;

    public EmcView(UserServices userService, StudentInfoService studentInfoService,
		ArtworkService artworkService, CommentService commentService,
		LikeReactionService likeReactionService,
		HeartReactionService heartReactionService) {

        this.userService = userService;
	this.studentInfoService = studentInfoService;
	this.artworkService = artworkService;
	this.commentService = commentService;
	this.likeReactionService = likeReactionService;
	this.heartReactionService = heartReactionService;

        createMainSection();
        addClassName("main-emc");
        setSizeFull();
        setWidthFull();
    }

    // Profile Page
    private Div createUserProfilePage(User user){
    	Avatar avatar = new Avatar();

    	String imageUrl = "./register_images/" + user.getProfileImage();

	final String USER_FILENAME = "src/main/resources/META-INF/resources/register_images/" + user.getProfileImage();

        try (FileInputStream userFis = new FileInputStream(USER_FILENAME)) {
            byte[] userBytes = userFis.readAllBytes();

            StreamResource userResource = new StreamResource(user.getProfileImage(), () -> new ByteArrayInputStream(userBytes));

            avatar.setImageResource(userResource);
            avatar.addClassName("settings-avatar");
	} catch (Exception e) {
            e.printStackTrace();
	}

	Div div = new Div();
        div.add(avatar);
        div.addClickListener(event -> {
           Dialog dialog = new Dialog();

           Button cancelButton = new Button("Close", (e) -> dialog.close());
           cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
           dialog.getFooter().add(cancelButton);
           cancelButton.addClassName("cancel");

           dialog.getElement().getStyle().set("background-color", "transparent");
           dialog.getElement().getStyle().set("box-shadow", "none");
           dialog.setResizable(true);
           dialog.addClassName("dialog");

	   final String DIALOG_FILENAME = "src/main/resources/META-INF/resources/register_images/" + user.getProfileImage();

           Image enlargedImage = new Image();
	   enlargedImage.setWidth("100%");
           enlargedImage.addClassName("image");

           try (FileInputStream DialogFis = new FileInputStream(DIALOG_FILENAME)) {
            	byte[] DialogBytes = DialogFis.readAllBytes();

            	StreamResource resources = new StreamResource(user.getProfileImage(), () -> new ByteArrayInputStream(DialogBytes));
            	enlargedImage.setSrc(resources);
           } catch (Exception e) {
            	e.printStackTrace();
           }

           // Add the image component to the dialog
           dialog.add(enlargedImage);

           // Open the dialog
           dialog.open();
        });

	return div;
    }

    // Pen Name
    private H2 createPenName(User user){
	StudentInfo info = user.getStudentInfo();

	String pen = "";
        if(info == null || info.getPenName() == null){
           pen += "No pen name yet";
        }else{
           pen += info.getPenName();
        }

        H2 penName = new H2(pen);
        penName.addClassName("course");
	penName.addClassNames(LumoUtility.FontSize.SMALL);

	return penName;
   }

   // Profile Icons
   private HorizontalLayout createProfileIcons(Long currentUserId, User user){

	Icon commentButton = new Icon(VaadinIcon.COMMENT_ELLIPSIS_O);
        commentButton.addClickListener(event -> {
           Long selectedUserId = user.getId();
           UI.getCurrent().navigate(CommentsView.class, selectedUserId);
        });
        commentButton.addClassName("comment-button");

        Icon moreIcon = new Icon(VaadinIcon.ELLIPSIS_CIRCLE_O);
	moreIcon.addClassName("more-icon");

	Icon heartIcon = new Icon(VaadinIcon.CAMERA);
	heartIcon.addClassName("heart-icon");

	String modifiedFullName = user.getFullName();

        if(modifiedFullName.endsWith("s")){
           modifiedFullName += "'";
        }else{
           modifiedFullName += "'s";
        }

        ConfirmDialog dialog = new ConfirmDialog();

	if(currentUserId != user.getId()){
           dialog.setHeader(String.format("View %s artworks?", modifiedFullName));
	} else {
	   dialog.setHeader(String.format("View your %s?", "artworks"));
	}

        dialog.setCancelable(true);
        dialog.setConfirmText("Okay");
        dialog.setConfirmButtonTheme("primary");
        dialog.addConfirmListener(event -> {
           Long selectedUserId = user.getId();

           // Navigate to the ViewArtworksView with the selected user's ID
           UI.getCurrent().navigate(ArtworkView.class, selectedUserId);
        });

        dialog.addClassName("view-dialog");

	List<Artwork> existingArtworks = artworkService.getArtworksByUserId(user.getId());

        Icon viewIcon = new Icon(VaadinIcon.PALETTE);
        viewIcon.addClickListener(event -> {
	   if(!existingArtworks.isEmpty()){
              dialog.open();
	   }else{
	      Notification.show("No artwork yet.", 3000, Position.TOP_CENTER)
		.addThemeVariants(NotificationVariant.LUMO_PRIMARY);
	   }
        });

        viewIcon.addClassName("view-icon");

	Span countedComments = createCommentsCount(user);

	Span countedArtworks = createArtworksCount(user);

	HorizontalLayout iconsLayout = new HorizontalLayout(
		viewIcon, countedArtworks,
		commentButton, countedComments,
		heartIcon, moreIcon
	);

        iconsLayout.setSpacing(true);

	return iconsLayout;
   }

   // Total comments
   private Span createCommentsCount(User user){
	int commentCount = commentService.getCommentsCountByUser(user.getEmail());

	Span countedComments = new Span(String.valueOf(commentCount));
        countedComments.addClassName("comments-count");

	return countedComments;
   }

   // Total artworks
   private Span createArtworksCount(User user){
	int artworksCount = artworkService.getArtworksCountByUser(user.getEmail());

	Span countedArtworks = new Span(String.valueOf(artworksCount));
        countedArtworks.addClassName("artworks-count");

	return countedArtworks;
   }

   // Student Name
   private H1 createStudentName(User user){
	H1 studentName = new H1(user.getFullName());
	studentName.addClassName("student-name");
	studentName.addClassNames(LumoUtility.FontSize.LARGE);

	return studentName;
   }

    // Main view
   private void createMainSection() {
        // Get the current user
	User currentUser = userService.findCurrentUser();

	// Get all users
        List<User> users = userService.getAllUsers();

	// Sorts the users by name in ascnding order
	Collections.sort(users, Comparator.comparing(User::getFirstName));

        for (User user : users) {
	    if(currentUser.getId() != user.getId()){
		// Creates the profile page for each user/student
		Div div = createUserProfilePage(user);

		// Creates the pen name
		H2 penName = createPenName(user);

		// Creates the student name
		H1 studentName = createStudentName(user);

		// Add penName and studentName to the layout
            	HorizontalLayout buttonLayout = new HorizontalLayout(div, studentName);

		// Creates the icons and add to layout
                HorizontalLayout iconsLayout = createProfileIcons(currentUser.getId(), user);

            	VerticalLayout studentLayout = new VerticalLayout(buttonLayout, penName, iconsLayout);
		studentLayout.setSizeFull();
		studentLayout.setWidthFull();
            	studentLayout.addClassName("emcview-layout");
            	add(studentLayout);

	    }else{

		Div div = createUserProfilePage(user);

		H2 penName = createPenName(user);

		H1 studentName = createStudentName(user);

                HorizontalLayout buttonLayout = new HorizontalLayout(div, studentName);

		HorizontalLayout iconsLayout = createProfileIcons(currentUser.getId(), user);

                // Create a label to display the artworks count
                VerticalLayout studentLayout = new VerticalLayout(buttonLayout, penName, iconsLayout);

		studentLayout.setSizeFull();
		studentLayout.setWidthFull();
                studentLayout.addClassName("emcview-layout");
                add(studentLayout);
	    }
        }
    }
}
