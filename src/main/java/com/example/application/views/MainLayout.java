package com.example.application.views;

import com.example.application.views.artworks.MainArtworkView;
import com.example.application.views.form.*;
import com.example.application.services.UserServices;
import com.example.application.data.User;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.router.HighlightConditions;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.server.StreamResource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.List;

public class MainLayout extends AppLayout {

    private UserServices userService;

    public MainLayout(UserServices userService) {
        this.userService = userService;
	createDrawer();
        createHeader();
	addClassName("app-layout");
    }

    private void createHeader() {
	User user = userService.findCurrentUser();

	Avatar avatar = new Avatar();
	avatar.addClassName("online");

	final String FILENAME = "src/main/resources/META-INF/resources/register_images/" + user.getProfileImage();

        try (FileInputStream fis = new FileInputStream(FILENAME)) {
            byte[] bytes = fis.readAllBytes();

            StreamResource resource = new StreamResource(user.getProfileImage(), () -> new ByteArrayInputStream(bytes));

            avatar.setImageResource(resource);
        } catch (Exception e) {
            e.printStackTrace();
        }

        H1 logo = new H1("The Animation Guild");
        logo.addClassName("logo");
        logo.addClassNames(
            LumoUtility.FontSize.MEDIUM,
            LumoUtility.Margin.MEDIUM);

        DrawerToggle toggle = new DrawerToggle();
	toggle.setIcon(VaadinIcon.MENU.create());
        toggle.addClassName("toggle");

        HorizontalLayout header = new HorizontalLayout(toggle, logo, avatar);
        header.addClassName("header");
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.expand(logo);
        header.setWidthFull();
        header.addClassNames(
            LumoUtility.Padding.Vertical.NONE,
            LumoUtility.Padding.Horizontal.MEDIUM);

        VerticalLayout viewHeader = new VerticalLayout(header);
        viewHeader.setPadding(false);
        viewHeader.setSpacing(false);
	viewHeader.setSizeFull();

        addToNavbar(header);
    }

    private HorizontalLayout createNavHeader(){
	User user = userService.findCurrentUser();

        H2 userName = new H2(user.getFirstName() + " " + user.getLastName());
        userName.addClassName("nav-username");

        Avatar avatar = new Avatar();
        avatar.addClassName("avatar");

	final String FILENAME = "src/main/resources/META-INF/resources/register_images/" + user.getProfileImage();

        try (FileInputStream fis = new FileInputStream(FILENAME)) {
            byte[] bytes = fis.readAllBytes();

            StreamResource resource = new StreamResource(user.getProfileImage(), () -> new ByteArrayInputStream(bytes));

            avatar.setImageResource(resource);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Span nameLabel = new Span(user.getFullName());
        nameLabel.addClassName("username");

	FlexLayout nameContainer = new FlexLayout();
        nameContainer.setWidth("100%");
        nameContainer.add(nameLabel);

        Button settingsIcon = new Button(new Icon(VaadinIcon.COG));
        settingsIcon.addClassName("settings-icon");
        settingsIcon.addClickListener(event -> {
                getUI().ifPresent(ui -> ui.navigate("profile-settings"));
        });

        // Create a HorizontalLayout to contain the text and icon
        HorizontalLayout headerContent = new HorizontalLayout(avatar, nameContainer, settingsIcon);
        headerContent.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        headerContent.expand(settingsIcon);
        headerContent.setWidthFull();
        headerContent.addClassNames(LumoUtility.Padding.Vertical.NONE, LumoUtility.Padding.Horizontal.MEDIUM);
        headerContent.setSpacing(true);
        headerContent.addClassName("header-content");

	return headerContent;
    }

    private RouterLink createLink(Class routeClass, VaadinIcon icon, String label){
	RouterLink routerLink = new RouterLink();
        routerLink.setHighlightCondition(HighlightConditions.sameLocation());
        routerLink.setRoute(routeClass);
        Icon routerIcon = new Icon(icon);
        routerIcon.addClassName("nav-icon");
        routerLink.add(routerIcon, new Span(label));
        routerLink.addClassName("drawer-link");

	return routerLink;
    }

    private RouterLink createSecondaryLink(Class routeClass, VaadinIcon icon, String label){
        RouterLink routerLink = new RouterLink();
        routerLink.setHighlightCondition(HighlightConditions.sameLocation());
        routerLink.setRoute(routeClass);
        Icon routerIcon = new Icon(icon);
        routerIcon.addClassName("nav-icons");
        routerLink.add(routerIcon, new Span(label));
        routerLink.addClassName("drawer-links");

        return routerLink;
    }

    private void createDrawer(){
    	RouterLink artworkFeedLink = createLink(MainFeed.class, VaadinIcon.GLOBE, "Artwork Feed");
	RouterLink studentsLink = createLink(EmcView.class,VaadinIcon.GROUP,"Students");
	RouterLink artworkLink = createLink(MainArtworkView.class,VaadinIcon.PALETTE,"Artworks");
	RouterLink accessInfoLink = createSecondaryLink(AccessInfo.class,VaadinIcon.USER,"Access Information");
	RouterLink changePasswordLink = createSecondaryLink(ChangePassword.class,VaadinIcon.EDIT,"Change Password");
	RouterLink signoutLink = createLink(LoginView.class,VaadinIcon.SIGN_OUT,"Sign Out");
	RouterLink contactLink = createSecondaryLink(ContactView.class,VaadinIcon.MOBILE,"Contact Information");

	HorizontalLayout navHeader = createNavHeader();
	navHeader.setWidthFull();

	VerticalLayout detailsLayout = new VerticalLayout(accessInfoLink, changePasswordLink, contactLink);
	detailsLayout.setWidthFull();

	Details details = new Details("Information", detailsLayout);
	details.setOpened(false);
	details.addClassName("nav-details");

	VerticalLayout layout = new VerticalLayout(navHeader, artworkFeedLink, studentsLink, artworkLink, details, signoutLink);
	layout.setWidthFull();

	addToDrawer(layout);
     }
}
