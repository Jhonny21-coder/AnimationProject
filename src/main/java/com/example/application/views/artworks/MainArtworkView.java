package com.example.application.views.artworks;

import com.example.application.views.EmcView;
import com.example.application.data.Artwork;
import com.example.application.data.LikeReaction;
import com.example.application.data.HeartReaction;
import com.example.application.data.User;
import com.example.application.services.UserServices;
import com.example.application.services.ArtworkService;
import com.example.application.services.LikeReactionService;
import com.example.application.services.HeartReactionService;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.contextmenu.HasMenuItems;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.File;

import java.time.LocalTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import java.util.Locale;
import java.util.List;

import java.net.URLEncoder;

import java.nio.charset.StandardCharsets;

@Route("main-artworks")
public class MainArtworkView extends AppLayout {

    private final ArtworkService artworkService;
    private final UserServices userService;
    private final LikeReactionService likeReactionService;
    private final HeartReactionService heartReactionService;
    private VerticalLayout artworksLayout = new VerticalLayout();
    private StreamResource resource;
    private final Span artworkUrlText = new Span("New Artwork Image");
    private final Upload upload = new Upload(new MemoryBuffer());
    private final TextField title = new TextField("Enter New Title");
    private final Button save = new Button("Save");
    private final Button close = new Button("Close");
    private String newFilename;
    private byte[] bytes;

    public MainArtworkView(ArtworkService artworkService, UserServices userService,
    	LikeReactionService likeReactionService, HeartReactionService heartReactionService){
    	this.artworkService = artworkService;
    	this.userService = userService;
    	this.likeReactionService = likeReactionService;
    	this.heartReactionService = heartReactionService;

    	addClassName("artwork-main");
	artworkUrlText.addClassName("add-text");
        upload.addClassName("register-upload");
        title.addClassName("register-field");
	save.addClassName("save-artwork");
	close.addClassName("close-artwork");

    	displayArtworks();
    }

    public void displayArtworks(){
    	User user = userService.findCurrentUser();
	List<Artwork> artworks = artworkService.getArtworksByUserId(user.getId());

	HorizontalLayout navLayout = new HorizontalLayout();

	Button addButton = new Button("Add New Artwork", new Icon(VaadinIcon.PLUS_CIRCLE_O));
	addButton.addClassName("add-artwork-button");
	addButton.addClickListener(event -> {
	    getUI().ifPresent(ui -> ui.navigate(AddArtwork.class));
	});

	navLayout.add(addButton);

	FormLayout formLayout = new FormLayout();

	if(artworks == null || artworks.isEmpty()){
           Paragraph noArtworkText = new Paragraph("No artworks yet. Click above button to add a new artwork.");
           noArtworkText.getStyle().set("font-family", "serif");
           artworksLayout.add(noArtworkText);
	}else{
    	   for(Artwork artwork : artworks){
    	   	Div seperator = new Div();
                seperator.addClassName("seperator");

		if (artwork.getArtworkUrl().equals(artworks.get(artworks.size() - 1).getArtworkUrl())) {
		    seperator.setVisible(false);
		}

    	   	H1 artworkDescription = new H1(artwork.getDescription());
            	artworkDescription.addClassName("main-artwork-title");

		LocalTime localTime = artwork.getTimeOfPost();
                LocalDate localDate = artwork.getDateOfPost();

                // Define the format pattern for displaying the time and date
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM)
                    .withLocale(Locale.US);
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
                    .withLocale(Locale.US);

                // Format the LocalTime and LocalDate into strings using the defined patterns
                String formattedTime = timeFormatter.format(localTime);
                String formattedDate = dateFormatter.format(localDate);

		Span artworkDateTime = new Span(formattedDate + " " + formattedTime);
		artworkDateTime.addClassName("artwork-date-time");

	    	String filename = "src/main/resources/META-INF/resources/artwork_images/" + artwork.getArtworkUrl();

	    	Image artworkImage = new Image();

	    	try (FileInputStream userFis = new FileInputStream(filename)) {
	    	    byte[] bytes = userFis.readAllBytes();

	    	    StreamResource resources = new StreamResource(artwork.getArtworkUrl(), () -> new ByteArrayInputStream(bytes));
            	    artworkImage.setSrc(resources);
            	} catch (Exception e) {
            	    e.printStackTrace();
            	}

            	ConfirmDialog dialog = new ConfirmDialog();

		HorizontalLayout buttonsLayout = createButtons(dialog, artwork);

		VerticalLayout titleTimeLayout = new VerticalLayout();
		titleTimeLayout.add(artworkDescription, artworkDateTime);

		formLayout.add(titleTimeLayout, artworkImage, buttonsLayout, seperator);
        	formLayout.setResponsiveSteps(new ResponsiveStep("0", 1),
            	    new ResponsiveStep("500px", 3));

    	    	deleteArtwork(dialog, artwork, artworkDescription, artworkImage, formLayout);
    	    }
    	}

	Icon searchIcon = new Icon(VaadinIcon.SEARCH);
        searchIcon.addClassName("search-button");
        searchIcon.addClickListener(event -> {

        });

    	Icon backIcon = new Icon(VaadinIcon.ARROW_BACKWARD);
        backIcon.addClassName("back-icon");
        backIcon.addClickListener(event -> {
            getUI().ifPresent(ui -> ui.navigate(EmcView.class));
        });

	HorizontalLayout iconsLayout = new HorizontalLayout();
	iconsLayout.add(backIcon, addButton, searchIcon);

	addToNavbar(iconsLayout);
    	setContent(formLayout);
    }

    public HorizontalLayout createButtons(ConfirmDialog dialog, Artwork artwork){

	Button deleteButton = new Button("Delete", new Icon(VaadinIcon.TRASH));
	deleteButton.addClassName("artwork-delete");
	deleteButton.addClickListener(event -> {
            dialog.open();
        });

        Button editButton = new Button("Edit", new Icon(VaadinIcon.EDIT));
        editButton.addClassName("artwork-edit");
        editButton.addClickListener(event -> {
           Long artworkId = artwork.getId();
           UI.getCurrent().navigate(EditArtwork.class, artworkId);
        });

	MenuBar menuBar = new MenuBar();
	menuBar.addClassName("menu-bar");

	VaadinIcon moreIcon = VaadinIcon.ELLIPSIS_DOTS_H;
	VaadinIcon shareIcon = VaadinIcon.SHARE;
	VaadinIcon downloadIcon = VaadinIcon.DOWNLOAD;

	MenuItem more = createIconItem(menuBar, moreIcon, "More", null);

	SubMenu moreSubMenu = more.getSubMenu();

	MenuItem share = createIconItem(moreSubMenu, shareIcon, "Share Artwork", null, true);
        MenuItem download = createIconItem(moreSubMenu, downloadIcon, "Download Artwork as PDF", null, true);

        menuIconListener(share, download, artwork);

        HorizontalLayout buttonsLayout = new HorizontalLayout(deleteButton, editButton, menuBar);

        return buttonsLayout;
    }

    private void menuIconListener(MenuItem share, MenuItem download, Artwork artwork){
        share.addClickListener(event -> {
            String imageUrl = "https://summary-spider-internally.ngrok-free.app/shared-artwork/" + artwork.getId();
            //String imageUrl = "http://localhost:8080/shared-artwork/" + artwork.getId();

            openShareDialog(imageUrl);
        });

        download.addClickListener(event -> {
             String imageUrl = artwork.getArtworkUrl();
	     String title = artwork.getDescription();

             openDownloadDialog(imageUrl, title);
        });
    }

    private void openDownloadDialog(String imageUrl, String title) {
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.addClassName("share-dialog");
        dialog.setCancelable(false);
        dialog.setConfirmText("Close");
        dialog.setHeader("Download Artwork");

        String downloadLink = "/download-pdf?imageUrl=" + imageUrl + "&title=" + title;

        Anchor downloadAnchor = new Anchor(downloadLink, "Download PDF");
        downloadAnchor.getElement().setAttribute("download", true);
	downloadAnchor.addClassName("download-anchor");

	Icon downloadIcon = new Icon(VaadinIcon.DOWNLOAD);
	downloadIcon.addClassName("download-icon");

        HorizontalLayout horizontal = new HorizontalLayout(downloadIcon, downloadAnchor);
	horizontal.addClassName("horizontal");

	Span artworkUrl = new Span(title + ".png");
	artworkUrl.addClassName("download-url");

        VerticalLayout vertical = new VerticalLayout(artworkUrl, horizontal);
        dialog.add(vertical);
        dialog.open();
    }

    private MenuItem createIconItem(HasMenuItems menu, VaadinIcon iconName,
            String label, String ariaLabel) {
        return createIconItem(menu, iconName, label, ariaLabel, false);
    }

    private MenuItem createIconItem(HasMenuItems menu, VaadinIcon iconName,
            String label, String ariaLabel, boolean isChild) {

        Icon icon = new Icon(iconName);

        if (isChild) {
            icon.getStyle().set("width", "var(--lumo-icon-size-s)");
            icon.getStyle().set("height", "var(--lumo-icon-size-s)");
            icon.getStyle().set("marginRight", "var(--lumo-space-s)");
        }

        MenuItem item = menu.addItem(icon, e -> {
        });

        if (ariaLabel != null) {
            item.setAriaLabel(ariaLabel);
        }

        if (label != null) {
            item.add(new Text(label));
        }

        return item;
    }

    private void openShareDialog(String imageUrl) {
        ConfirmDialog shareDialog = new ConfirmDialog();
	shareDialog.addClassName("share-dialog");
	shareDialog.setConfirmText("Close");
	shareDialog.setCancelable(false);
        shareDialog.setHeader("Copy & Share");

        TextField urlField = new TextField();
        urlField.addClassName("url-field");
        urlField.setValue(imageUrl);
        urlField.setWidth("100%");
        urlField.setReadOnly(true);

        Button copyButton = new Button("Copy Link", new Icon(VaadinIcon.CLIPBOARD));
        copyButton.addClassName("copy-button");
        copyButton.addClickListener(event -> {
            copyToClipboard(urlField.getValue());
            Notification.show("Link copied to clipboard", 1000, Notification.Position.MIDDLE)
            	.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        });

        VerticalLayout dialogLayout = new VerticalLayout(urlField, copyButton);
        shareDialog.add(dialogLayout);
        shareDialog.open();
    }

    private void copyToClipboard(String text) {
        String jsCode = "navigator.clipboard.writeText('" + text + "').then(function() { "
                + "console.log('Copying to clipboard was successful!'); "
                + "}, function(err) { "
                + "console.error('Could not copy text: ', err); "
                + "});";
        UI.getCurrent().getPage().executeJs(jsCode);
    }

    public void deleteArtwork(ConfirmDialog dialog, Artwork artwork,
    	H1 artworkDescription, Image artworkImage, FormLayout formLayout){

	dialog.setCancelable(true);
	dialog.setConfirmText("Delete");
	dialog.setConfirmButtonTheme("error primary");
	dialog.setHeader("Delete " + artwork.getDescription() + "?");
        dialog.setText("Are you sure you want to delete this artwork?");
        dialog.addConfirmListener(events -> {
            List<LikeReaction> likeReactions = likeReactionService.getReactionForArtworkId(artwork.getId());
            List<HeartReaction> heartReactions = heartReactionService.getReactionForArtworkId(artwork.getId());

            for(LikeReaction likeReaction : likeReactions){
		if(likeReaction != null){
                   likeReactionService.deleteLikeReactions(likeReaction);
                }
	    }

	    for(HeartReaction heartReaction : heartReactions){
		if(heartReaction != null){
                   heartReactionService.deleteHeartReactions(heartReaction);
                }
            }

	    String filePath = "src/main/resources/META-INF/resources/artwork_images/" + artwork.getArtworkUrl();

            File file = new File(filePath);

            if(file.exists()){
               if(file.delete()){
                  System.out.println("File deleted successfully.");
               }else{
                  System.out.println("Failed to delete the file.");
               }
            }else{
               System.out.println("File does not exist. Cannot delete.");
            }

            artworkService.deleteArtwork(artwork);
	});
    }
}
