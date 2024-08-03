package com.example.application.views.profile;

import com.example.application.data.Follower;
import com.example.application.services.FollowerService;
import com.example.application.data.User;
import com.example.application.services.UserServices;
import com.example.application.views.MainFeed;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.dialog.Dialog;

import java.io.FileInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import java.util.List;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

@Route("follower-view")
public class FollowerView extends AppLayout {

     private final FollowerService followerService;
     private final UserServices userService;
     private final FormLayout searchLayout = new FormLayout();
     private final VerticalLayout filterLayout = new VerticalLayout();
     private final TextField searchField = new TextField();
     private final Grid<Follower> followerGrid = new Grid<>(Follower.class, false);
     private final Grid<Follower> userGrid = new Grid<>(Follower.class, false);
     private Span totalFollowers = new Span();

     public FollowerView(FollowerService followerService, UserServices userService){
        this.followerService = followerService;
        this.userService = userService;

        addClassName("profile-app-layout");
        followerGrid.addClassName("view-grid");
	totalFollowers.addClassName("view-total-followers");

        User currentUser = userService.findCurrentUser();
        setParameter(currentUser.getId());
    }

    private void setParameter(Long userId){
        User user = userService.getUserById(userId);

        displayAllFollowers(user);
        createHeader(user);
    }

    private void displayAllFollowers(User user) {
        List<Follower> followers = followerService.getFollowersByFollowedUserId(user.getId());

        //Span totalFollowers = new Span(followers.size() + " Followers");
        totalFollowers.setText(followers.size() + " Followers");

        searchField.setPlaceholder("Search followers");
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.addClassName("view-search-field");
        searchField.setClearButtonVisible(true);
        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        searchField.addValueChangeListener(event -> updateList(user));

        Collections.sort(followers, Comparator.comparing(follower -> follower.getFollower().getFirstName()));

        followerGrid.setItems(followers);
        followerGrid.removeAllColumns();
	followerGrid.addThemeVariants(GridVariant.LUMO_NO_ROW_BORDERS);

        followerGrid.addComponentColumn(follower -> {
            User userFollower = follower.getFollower();
	    User currentUser = userService.findCurrentUser();

            Avatar avatar = new Avatar();
            avatar.addClassName("view-avatar");

            String avatarFile = "src/main/resources/META-INF/resources/register_images/" + userFollower.getProfileImage();

            try (FileInputStream fis = new FileInputStream(avatarFile)) {
                byte[] bytes = fis.readAllBytes();

                StreamResource resource = new StreamResource(userFollower.getProfileImage(), () -> new ByteArrayInputStream(bytes));

                avatar.setImageResource(resource);
            } catch (Exception e) {
                e.printStackTrace();
            }

	    Span fullName = new Span(userFollower.getFullName());
	    fullName.addClassName("view-firstname");

            HorizontalLayout avatarDiv = new HorizontalLayout(avatar, fullName);
	    avatarDiv.addClassName("view-avatar-div");
	    avatarDiv.addClickListener(event -> {
	    	if(userFollower.getId().equals(currentUser.getId())){
	    	    UI.getCurrent().navigate(MainFeed.class);
	    	}else{
             	    UI.getCurrent().navigate(UserProfile.class, userFollower.getId());
             	}
            });

            return avatarDiv;
        }).setAutoWidth(false);

        followerGrid.addComponentColumn(follower -> {
            User currentUser = userService.findCurrentUser();
            User followerUser = follower.getFollower();

            Follower currentFollower = followerService.getFollowerByFollowedUserIdAndFollowerId(followerUser.getId(), currentUser.getId());
            AtomicBoolean userAlreadyFollowed = new AtomicBoolean(currentFollower != null);

	    Button followButton = new Button();

	    if(!currentUser.getId().equals(followerUser.getId())){
            	updateFollowButton(followButton, userAlreadyFollowed.get());

            	followButton.addClickListener(event -> {
                    if (!userAlreadyFollowed.get()) {
                    	Follower newFollower = new Follower();
                    	newFollower.setFollowedUser(followerUser);
                    	newFollower.setFollower(currentUser);
                    	newFollower.setFollowed(true);
                    	followerService.saveFollower(newFollower);
                    	updateFollowButton(followButton, true);
                    	userAlreadyFollowed.set(true);
                    } else {
                    	followerService.deleteFollowerByFollowedUserId(followerUser.getId(), currentUser.getId());
                    	updateFollowButton(followButton, false);
                    	userAlreadyFollowed.set(false);
                    }
            	});
            }else{
            	followButton.addClassName("view-exists-button");
            }
            return followButton;
        }).setAutoWidth(false);

        VerticalLayout layout = new VerticalLayout(searchField, totalFollowers);

        searchLayout.add(layout, followerGrid);
        searchLayout.addClassName("view-search-layout");

        setContent(searchLayout);
    }

    // For Follower Grid
    private void updateList(User user) {
        String searchTerm = searchField.getValue();
        List<Follower> followers = followerService.findAllFollowers(user.getId(), searchTerm);
        followerGrid.setItems(followers);
    }

    private void updateFollowButton(Button followButton, boolean isFollowed) {
        if (isFollowed) {
            followButton.setIcon(new Icon(VaadinIcon.USER_CHECK));
            followButton.setText("Unfollow");
            followButton.removeClassName("view-follow-button");
            followButton.addClassName("view-unfollow-button");
        } else {
            followButton.setIcon(new Icon(VaadinIcon.PLUS));
            followButton.setText("Follow");
            followButton.removeClassName("view-unfollow-button");
            followButton.addClassName("view-follow-button");
        }
    }

    private void createHeader(User user){
        Icon backButton = new Icon(VaadinIcon.ARROW_BACKWARD);
        backButton.addClassName("profile-back-button");
        backButton.addClickListener(event -> {
             UI.getCurrent().navigate(MainFeed.class);
        });

        Span text = new Span("Your Followers");
	text.addClassName("view-fullname");

	Icon moreIcon = new Icon(VaadinIcon.ELLIPSIS_DOTS_H);
	moreIcon.addClassName("follower-more-icon");
	moreIcon.addClickListener(event -> {
           Dialog dialog = new Dialog();
           dialog.addClassName("all-users-dialog");
	   dialog.setHeaderTitle("Delete A Follower");

	   createDeleteFollower(dialog, user);
        });

	HorizontalLayout headerLayout = new HorizontalLayout(backButton, text, moreIcon);

        addToNavbar(headerLayout);
    }

    private void createDeleteFollower(Dialog dialog, User user){
        dialog.open();
        List<Follower> followers = followerService.getFollowersByFollowedUserId(user.getId());

        userGrid.setItems(followers);
        userGrid.removeAllColumns();
        userGrid.addClassName("all-users-grid");

        TextField field = new TextField();
        field.setValueChangeMode(ValueChangeMode.EAGER);
        field.setPlaceholder("Find a follower...");
        field.addValueChangeListener(event -> updateFollowerList(user.getId(), field.getValue()));
        dialog.add(field);

        userGrid.addComponentColumn(follower -> {
            Avatar avatar = new Avatar();
            avatar.addClassName("view-avatar");

            String avatarFile = "src/main/resources/META-INF/resources/register_images/" + follower.getFollower().getProfileImage();

            try (FileInputStream fis = new FileInputStream(avatarFile)) {
            	 byte[] bytes = fis.readAllBytes();

                StreamResource resource = new StreamResource(follower.getFollower().getProfileImage(), () -> new ByteArrayInputStream(bytes));

                avatar.setImageResource(resource);
            } catch (Exception e) {
                e.printStackTrace();
            }

            Span fullName = new Span(follower.getFollower().getFullName());
            fullName.addClassName("view-firstname");

            HorizontalLayout  avatarDiv = new HorizontalLayout(avatar, fullName);
            avatarDiv.addClassName("view-avatar-div");

            return avatarDiv;
        }).setAutoWidth(false);

        userGrid.addComponentColumn(follower -> {
            Icon deleteIcon = new Icon(VaadinIcon.TRASH);
            deleteIcon.addClassName("follower-delete-icon");

	    Span following = new Span("You are following " + follower.getFollower().getFirstName());
	    following.addClassName("follower-confirm-text");

	    Span notFollowing = new Span("Not following " + follower.getFollower().getFirstName());
	    notFollowing.addClassName("follower-confirm-text");

	    ConfirmDialog dialogg = new ConfirmDialog();
            dialogg.addClassName("follower-dialog");

	    Long currentFollower = follower.getFollowedUser().getId();
            Long currentFollowed = follower.getFollower().getId();

	    Follower singleFollower = followerService.getFollowerByFollowedUserIdAndFollowerId(currentFollowed, currentFollower);

	    if(singleFollower != null){
	    	dialogg.add(following);
	    }else{
		dialogg.add(notFollowing);
	    }

            dialogg.setCancelable(true);
            dialogg.setConfirmText("Delete");
            dialogg.setHeader("Are you sure you want to delete?");
            dialogg.addConfirmListener(event -> {
                Long userId = follower.getFollowedUser().getId();
                Long followerId = follower.getFollower().getId();

                followerService.deleteFollowerByFollowedUserId(userId, followerId);

                List<Follower> currentFollowers = followerService.getFollowersByFollowedUserId(userId);

		totalFollowers.setText(currentFollowers.size() + " Followers");

        	userGrid.setItems(currentFollowers);
        	followerGrid.setItems(currentFollowers);

            	dialogg.close();
            });

            deleteIcon.addClickListener(event -> {
            	dialogg.open();
            });

            return deleteIcon;
        });

        dialog.add(userGrid);
    }

    // For User Grid
    private void updateFollowerList(Long userId, String searchTerm) {
        List<Follower> followers = followerService.findAllFollowers(userId, searchTerm);

        userGrid.setItems(followers);
    }
}
