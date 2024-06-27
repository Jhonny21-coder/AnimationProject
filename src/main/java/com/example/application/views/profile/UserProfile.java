package com.example.application.views.profile;

import com.example.application.data.Follower;
import com.example.application.services.FollowerService;
import com.example.application.data.StudentInfo;
import com.example.application.data.Contact;
import com.example.application.services.ContactService;
import com.example.application.data.User;
import com.example.application.services.UserServices;
import com.example.application.data.Artwork;
import com.example.application.services.ArtworkService;
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

import java.io.FileInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import java.text.DecimalFormat;

import com.example.application.services.LikeReactionService;
import com.example.application.services.HeartReactionService;
import com.example.application.services.CommentService;

@Route("profile-section")
public class UserProfile extends AppLayout implements HasUrlParameter<Long> {

    private final ArtworkService artworkService;
    private final ContactService contactService;
    private final FollowerService followerService;
    private final UserServices userService;
    private final HorizontalLayout headerLayout = new HorizontalLayout();

    private final LikeReactionService likeService;
    private final HeartReactionService heartService;
    private final CommentService commentService;

    public UserProfile(ArtworkService artworkService, ContactService contactService,
    	FollowerService followerService, UserServices userService,
    	LikeReactionService likeService, HeartReactionService heartService,
    	CommentService commentService){

	this.artworkService = artworkService;
	this.contactService = contactService;
	this.followerService = followerService;
	this.userService = userService;

	this.likeService = likeService;
        this.heartService = heartService;
        this.commentService = commentService;

	addClassName("profile-app-layout");
    }

    @Override
    public void setParameter(BeforeEvent event, Long userId){
    	User user = userService.getUserById(userId);

	createHeader(user);
    	createFollowerLayout(user);
    }

    private Button createButtonListener(User user, HorizontalLayout layout){
    	List<Follower> followers = followerService.getFollowersByFollowedUserId(user.getId());

        User currentUser = userService.findCurrentUser();

        Follower currentFollower = followerService.getFollowerByFollowedUserIdAndFollowerId(user.getId(), currentUser.getId());
        AtomicBoolean userAlreadyFollowed = new AtomicBoolean(currentFollower != null);

        AtomicLong atomicLong = new AtomicLong(followers.size());

        Button followButton = new Button();
        updateFollowButton(followButton, userAlreadyFollowed.get());
        followButton.addClickListener(event -> {
            if (!userAlreadyFollowed.get()) {
                Follower newFollower = new Follower();
                newFollower.setFollowedUser(user);
                newFollower.setFollower(currentUser);
                newFollower.setFollowed(true);
                followerService.saveFollower(newFollower);
                updateFollowButton(followButton, true);

                atomicLong.incrementAndGet();
                // Update layout dynamically
                refreshFollowerLayout(user, layout, atomicLong.get());
                userAlreadyFollowed.set(true);
            } else {
                followerService.deleteFollowerByFollowedUserId(user.getId(), currentUser.getId());
                updateFollowButton(followButton, false);

                atomicLong.decrementAndGet();
                // Update layout dynamically
                refreshFollowerLayout(user, layout, atomicLong.get());
                userAlreadyFollowed.set(false);
            }
        });

        return followButton;
    }

    private HorizontalLayout createStatistics(User user, HorizontalLayout layout){
    	List<Artwork> artworks = artworkService.getArtworksByUserId(user.getId());
        List<Follower> followers = followerService.getFollowersByFollowedUserId(user.getId());

        User currentUser = userService.findCurrentUser();

        Long userFollowers = followerService.countFollowers(user.getId()) + (long) 999996;

        Span totalFollowers = new Span(formatValue(userFollowers));
        totalFollowers.addClassName("profile-total-followers");

	Long userArtworks = (long) artworks.size() + 999991;

        Span totalArtworks = new Span(formatValue(userArtworks));
        totalArtworks.addClassName("profile-total-artworks");

        Span artworkText = new Span("artworks");
        artworkText.addClassName("profile-text");

        Span followText = new Span("followers");
        followText.addClassName("profile-text");

        Div artworksLayout = new Div(totalArtworks, artworkText);
        artworksLayout.addClassName("profile-artworks-layout");

        Div followLayout = new Div(totalFollowers, followText);
        followLayout.addClassName("profile-follow-layout");

	Button followButton = createButtonListener(user, layout);

        return new HorizontalLayout(artworksLayout, followButton, followLayout);
    }

    private String formatValue(long value) {
        if (value >= 1_000_000) {
            return formatMillions(value);
        } else if (value >= 1_000) {
            return formatThousands(value);
        } else {
            return String.valueOf(value);
        }
    }

    private String formatMillions(long value) {
        String wrapped = String.valueOf(value);
        int length = wrapped.length();
        int significantDigits = length - 6; // Determine significant digits for millions

        if (wrapped.length() > significantDigits + 1 && wrapped.charAt(significantDigits) == '0') {
            return wrapped.substring(0, significantDigits) + "M";
        } else {
            return wrapped.substring(0, significantDigits) + "." + wrapped.charAt(significantDigits) + "M";
        }
    }

    private String formatThousands(long value) {
        String wrapped = String.valueOf(value);
        int length = wrapped.length();
        int significantDigits = length - 3; // Determine significant digits for thousands

        if (wrapped.length() > significantDigits + 1 && wrapped.charAt(significantDigits) == '0') {
            return wrapped.substring(0, significantDigits) + "K";
        } else {
            return wrapped.substring(0, significantDigits) + "." + wrapped.charAt(significantDigits) + "K";
        }
    }

    private VerticalLayout createFollowerHeader(User user){
    	List<Follower> followers = followerService.getFollowersByFollowedUserId(user.getId());

    	Span followerSpan = new Span("Followers");
        followerSpan.addClassName("follower-span");

        Span moreFollowers = new Span("View all");
        moreFollowers.addClassName("view-more");
        moreFollowers.addClickListener(event -> {
             UI.getCurrent().navigate(ViewAllFollowers.class, user.getId());
        });

	DecimalFormat formatter = new DecimalFormat("#,###");

	String formatted = formatter.format(followers.size() + 999996);

        Span totalFollowers = new Span(formatted);
	totalFollowers.addClassName("header-total-followers");

	HorizontalLayout layout = new HorizontalLayout(followerSpan, moreFollowers);
	layout.addClassName("header-followers-layout");

        return new VerticalLayout(layout, totalFollowers);
    }

    private VerticalLayout createInformationLayout(User user){
    	Icon age = new Icon(VaadinIcon.USER_CLOCK);
    	age.addClassName("profile-information");

	Span ageSpan = new Span(String.valueOf(user.getAge()));
	ageSpan.addClassName("profile-span");

    	Icon gender = new Icon(VaadinIcon.MALE);
    	gender.addClassName("profile-information");

	Span genderSpan = new Span(user.getGender());
	genderSpan.addClassName("profile-span");

    	Icon dateOfBirth = new Icon(VaadinIcon.CALENDAR_CLOCK);
    	dateOfBirth.addClassName("profile-information");

	Span dateSpan = new Span(user.getDateOfBirth().toString());
	dateSpan.addClassName("profile-span");

    	Icon placeOfBirth = new Icon(VaadinIcon.WORKPLACE);
    	placeOfBirth.addClassName("profile-information");

	Span placeSpan = new Span(user.getPlaceOfBirth());
	placeSpan.addClassName("profile-span");

	HorizontalLayout moreLayout = createMoreInformation(user);
	moreLayout.addClassName("profile-more-layout");

    	return new VerticalLayout(
   	    new HorizontalLayout(age, new Span("At age of "), ageSpan),
    	    new HorizontalLayout(gender, new Span("Gender "), genderSpan),
    	    new HorizontalLayout(dateOfBirth, new Span("Born in "), dateSpan),
    	    new HorizontalLayout(placeOfBirth, new Span("Lives in "), placeSpan),
    	    moreLayout
    	);
    }

    private HorizontalLayout createMoreInformation(User user){
    	Contact contact = contactService.getContactByUserId(user.getId());

	if(contact != null){
    	   Icon more = new Icon(VaadinIcon.ELLIPSIS_CIRCLE_O);
           more.addClassName("profile-information");
           Span moreSpan = new Span("View more about " + user.getFirstName());

           ConfirmDialog dialog = new ConfirmDialog();
           dialog.setCancelable(false);
           dialog.setConfirmText("Close");
           dialog.setHeader("More information");
	   dialog.addClassName("profile-more-dialog");

	   Span contactText = new Span("Contact Information");
	   contactText.addClassName("profile-contact-text");

	   Span moreText = new Span("More information");
	   moreText.addClassName("profile-contact-text");

	   Image instagramIcon = new Image("./icons/instagram.svg", "Instagram Icon");
           Image tiktokIcon = new Image("./icons/tiktok.svg", "Tiktok Icon");
           Image phoneIcon = new Image("./icons/phone.svg", "Phone Icon");
           Image facebookIcon = new Image("./icons/facebook.svg", "Facebook Icon");

	   Span phoneNumber = new Span(contact.getPhoneNumber());
	   phoneNumber.addClassName("profile-contact");

	   Span facebook = new Span(contact.getFacebook());
	   facebook.addClassName("profile-contact");

	   Span instagram = new Span(contact.getInstagram());
	   instagram.addClassName("profile-contact");

	   Span tiktok = new Span(contact.getTiktok());
	   tiktok.addClassName("profile-contact");

	   StudentInfo studentInfo = user.getStudentInfo();

           Span penName = new Span(studentInfo.getPenName());
           penName.addClassName("profile-contact");

           Span hobby = new Span(studentInfo.getHobby());
           hobby.addClassName("profile-contact");

           Icon penIcon = new Icon(VaadinIcon.PENCIL);
           penIcon.getStyle().set("color", "#0ef");

           Icon hobbyIcon = new Icon(VaadinIcon.GAMEPAD);
           hobbyIcon.getStyle().set("color", "#0ef");

	   dialog.add(
	       new VerticalLayout(
		   contactText,
		   new HorizontalLayout(phoneIcon, phoneNumber),
		   new HorizontalLayout(facebookIcon, facebook),
		   new HorizontalLayout(instagramIcon, instagram),
		   new HorizontalLayout(tiktokIcon, tiktok),
		   moreText,
		   new HorizontalLayout(penIcon, penName),
		   new HorizontalLayout(hobbyIcon, hobby)
	       )
	   );

           moreSpan.addClickListener(event -> dialog.open());

           more.addClickListener(event -> dialog.open());

           return new HorizontalLayout(more, moreSpan);
        }

        return new HorizontalLayout();
    }

    private void createFollowerLayout(User user) {
    	FormLayout formLayout = new FormLayout();

    	List<Follower> followers = followerService.getFollowersByFollowedUserId(user.getId());
    	int maxFollowersToShow = 3;

    	HorizontalLayout layout = new HorizontalLayout();

    	User currentUser = userService.findCurrentUser();

    	for (int i = 0; i < Math.min(followers.size(), maxFollowersToShow); i++) {
            User followerUser = followers.get(i).getFollower();
            Image image = new Image("./register_images/" + followerUser.getProfileImage(), "Image" + (i + 1));
            image.addClassName("follower-image" + (i + 1));

            Span span = new Span(followerUser.getFirstName());

            if (followers.size() < 3) {
            	VerticalLayout verticalLayout = new VerticalLayout(image, span);
            	verticalLayout.addClassName("divdiv" + (i + 1));

            	verticalLayout.addClickListener(event -> {
            	    if(currentUser.getId().equals(followerUser.getId())){
                    	UI.getCurrent().navigate(MainFeed.class);
                    }else{
                    	refreshHeader();
                    	UI.getCurrent().navigate(UserProfile.class, followerUser.getId());
                    }
            	});

            	layout.add(verticalLayout);
            } else {
            	Div div = new Div(image, span);
            	div.addClassName("div" + (i + 1));

            	div.addClickListener(event -> {
                    if(currentUser.getId().equals(followerUser.getId())){
                        UI.getCurrent().navigate(MainFeed.class);
                    }else{
                    	refreshHeader();
                        UI.getCurrent().navigate(UserProfile.class, followerUser.getId());
                    }
            	});

            	layout.add(div);
            }
    	}

        Avatar avatar = new Avatar();
        avatar.addClassName("profile-avatar");

        String avatarFile = "src/main/resources/META-INF/resources/register_images/" + user.getProfileImage();

        try (FileInputStream fis = new FileInputStream(avatarFile)) {
             byte[] bytes = fis.readAllBytes();

             StreamResource resource = new StreamResource(user.getProfileImage(), () -> new ByteArrayInputStream(bytes));

             avatar.setImageResource(resource);
        } catch (Exception e) {
             e.printStackTrace();
        }

        Span nameSpan = new Span(user.getFullName());
        nameSpan.addClassName("profile-name");

        VerticalLayout layout2 = new VerticalLayout(avatar, nameSpan);
        layout2.addClassName("profile-layout");

    	HorizontalLayout buttonLayout = createStatistics(user, layout);
        buttonLayout.addClassName("profile-button-layout");

        VerticalLayout informationLayout = createInformationLayout(user);
        informationLayout.addClassName("profile-information-layout");

        VerticalLayout followerHeader = createFollowerHeader(user);

	Button imageButton = createImageButton(user);

        VerticalLayout followLayout = new VerticalLayout(followerHeader, layout, imageButton);
	followLayout.addClassName("second-follow-layout");

	ProfileFeed feed = new ProfileFeed(artworkService, likeService, heartService, commentService, userService);

	FormLayout profileImageFeed = feed.createFeed(user);

        formLayout.add(layout2, buttonLayout, informationLayout, followLayout, profileImageFeed);

    	setContent(formLayout);
    }

    private Button createImageButton(User user){
    	Button imageButton = new Button("Artworks", new Icon(VaadinIcon.PICTURE));
        imageButton.addClassName("profile-image-button");
        imageButton.addClickListener(event -> {
             UI.getCurrent().navigate(ArtworkImages.class, user.getId());
        });

        return imageButton;
    }

    private void refreshFollowerLayout(User user, HorizontalLayout layout, long followerCount) {
    	layout.removeAll();
    	createFollowerLayout(user);
    }

    private void refreshHeader(){
    	headerLayout.removeAll();
    }

    private void updateFollowButton(Button followButton, boolean isFollowed) {
        if (isFollowed) {
            followButton.setIcon(new Icon(VaadinIcon.USER_CHECK));
            followButton.setText("Unfollow");
            followButton.removeClassName("follow-button");
            followButton.addClassName("unfollow-button");
        } else {
            followButton.setIcon(new Icon(VaadinIcon.PLUS));
            followButton.setText("Follow");
            followButton.removeClassName("unfollow-button");
            followButton.addClassName("follow-button");
        }
    }

    private void createHeader(User user){
    	Icon backButton = new Icon(VaadinIcon.ARROW_BACKWARD);
	backButton.addClassName("profile-back-button");
	backButton.addClickListener(event -> {
	     UI.getCurrent().navigate(MainFeed.class);
	});

	Span fullName = new Span(user.getFullName());
	fullName.addClassName("profile-fullname");

	headerLayout.add(backButton, fullName);

	addToNavbar(headerLayout);
    }
}
