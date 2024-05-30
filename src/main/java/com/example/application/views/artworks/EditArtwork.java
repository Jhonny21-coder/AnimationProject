package com.example.application.views.artworks;

import com.example.application.views.UploadsI18N;
import com.example.application.data.Artwork;
import com.example.application.data.User;
import com.example.application.services.UserServices;
import com.example.application.services.ArtworkService;
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
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;

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

import java.util.List;
import java.util.Locale;

@Route("edit-artwork")
public class EditArtwork extends AppLayout implements HasUrlParameter<Long> {

    private final ArtworkService artworkService;
    private final UserServices userService;
    private StreamResource resource;
    private final Span artworkUrlText = new Span("Upload New Artwork Image");
    private final Span newArtworkText = new Span("New Artwork Image");
    private final Upload upload = new Upload(new MemoryBuffer());
    private final Image newImage = new Image();
    private final TextField title = new TextField("Enter New Title");
    private final Button save = new Button("Save");
    private final Button close = new Button("Close");
    private String newFilename;
    private byte[] bytes;
    private Span currentText = new Span("Current Artwork Image");
    private Image currentImage = new Image();

    public EditArtwork(ArtworkService artworkService, UserServices userService){
    	this.artworkService = artworkService;
        this.userService = userService;

        addClassName("artwork-main");
        artworkUrlText.addClassName("add-text");
        newArtworkText.addClassName("current-text");
        upload.addClassName("register-upload");
        title.addClassName("register-field");
        save.addClassName("save-artwork");
        close.addClassName("close-artwork");
        currentText.addClassName("current-text");
    }

    @Override
    public void setParameter(BeforeEvent event, Long artworkId) {
	Artwork artwork = artworkService.getArtworkById(artworkId);
        editArtwork(artwork);
    }

    public void editArtwork(Artwork artwork){
        save.setIcon(new Icon(VaadinIcon.CHECK));
        close.setIcon(new Icon(VaadinIcon.CLOSE));
        title.setValue(artwork.getDescription());

        title.setSuffixComponent(new Icon(VaadinIcon.TEXT_LABEL));
        title.setPlaceholder("Current title " + artwork.getDescription());
	title.addValueChangeListener(event -> {
	    title.setLabel("New Title");
	});

        currentText.setVisible(true);
	newArtworkText.setVisible(false);

        newImage.addClassName("new-uploaded-image");
	newImage.setVisible(false);

	currentImage.addClassName("current-image");

	String currentFilename = "src/main/resources/META-INF/resources/artwork_images/" + artwork.getArtworkUrl();

	try (FileInputStream currentFis = new FileInputStream(currentFilename)) {
	    byte[] currentBytes = currentFis.readAllBytes();

	    StreamResource currentResources = new StreamResource(artwork.getArtworkUrl(), () -> new ByteArrayInputStream(currentBytes));
	    currentImage.setSrc(currentResources);
	} catch (Exception e) {
	    e.printStackTrace();
	}

        customUpload();

        upload.addSucceededListener(event -> {
            MemoryBuffer buffer = (MemoryBuffer) upload.getReceiver();

            try {
                InputStream inputStream = buffer.getInputStream();
                byte[] bytes = inputStream.readAllBytes();
                newFilename = event.getFileName();
                StreamResource resource = new StreamResource(newFilename, () -> new ByteArrayInputStream(bytes));
                newImage.setSrc(resource);
                newImage.setVisible(true);
                newArtworkText.setVisible(true);
                currentImage.setVisible(false);
                currentText.setVisible(false);
            } catch (IOException e) {
                Notification.show("Error uploading artwork image", 3000, Notification.Position.TOP_CENTER);
            }
        });

        save.addClickListener(event -> {
            // Deleting a file
            String filePathToDelete = "src/main/resources/META-INF/resources/artwork_images/" + artwork.getArtworkUrl();

            File file = new File(filePathToDelete);

            if(file.exists()){
               if(file.delete()){
                  System.out.println("\nFile " + file.getName() + " deleted successfully.");
               }else{
                  System.out.println("\nFailed to delete the file.");
               }
            }else{
               System.out.println("\nFile does not exist. Cannot delete.");
            }

            // Saving a file to a directory
            MemoryBuffer buffer = (MemoryBuffer) upload.getReceiver();

            try {
                InputStream inputStream = buffer.getInputStream();
                byte[] bytes = inputStream.readAllBytes();

                String filePathToSave = "/data/data/com.termux/files/home/MyVaadinProject/src/main/resources/META-INF/resources/artwork_images/" + newFilename;

                FileOutputStream outputStream = new FileOutputStream(filePathToSave);
                outputStream.write(bytes);
            }catch(Exception e){
                Notification.show("Error saving artwork image", 3000, Notification.Position.TOP_CENTER);
            }

            // Saving the artwork to database
            String descriptionValue = title.getValue();

            if(!newFilename.isEmpty() && !descriptionValue.isEmpty()) {
                artwork.setArtworkUrl(newFilename);
                artwork.setDescription(descriptionValue);
                artworkService.updateArtwork(artwork);

		System.out.println("New added file: " + newFilename);

                Notification.show("Artwork edited successfully", 3000, Notification.Position.TOP_CENTER)
                   .addThemeVariants(NotificationVariant.LUMO_SUCCESS);

                getUI().ifPresent(ui -> ui.navigate("main-artworks"));
             } else {
                Notification.show("Please fill the missing field", 3000, Notification.Position.TOP_CENTER)
                   .addThemeVariants(NotificationVariant.LUMO_ERROR);
             }
        });

        close.addClickListener(event -> {
            getUI().ifPresent(ui -> ui.navigate("main-artworks"));
        });

        FormLayout formLayout = new FormLayout();
        formLayout.add(artworkUrlText, upload, newArtworkText, currentText, currentImage, newImage, title, new HorizontalLayout(save, close));
        /*formLayout.setColspan(title, 3);
        formLayout.setResponsiveSteps(new ResponsiveStep("0", 1),
                new ResponsiveStep("500px", 3));*/

        VerticalLayout artworksDiv = new VerticalLayout(formLayout);
        setContent(artworksDiv);
    }

    private void customUpload(){
    	HorizontalLayout uploadLayout = new HorizontalLayout();
        uploadLayout.setSpacing(false);
        uploadLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        Icon uploadIcon = VaadinIcon.FILE_ADD.create();

        // Create the text
        Span hint = new Span("Accepted images formats: (.png, .jpeg)");
        Span hint2 = new Span(")");
        hint.addClassName("hint");
        hint2.addClassName("hin2");

        Span uploadLabel = new Span("Upload New Artwork (");

        // Add both the icon and text to the HorizontalLayout
        uploadLayout.add(uploadIcon, uploadLabel, hint, hint2);

        // Create the button and set the uploadLayout as its component
        Button uploadButton = new Button();
        uploadButton.getElement().appendChild(uploadLayout.getElement());

        // Set the uploadButton as the upload button
        upload.setUploadButton(uploadButton);

        int maxFileSizeInBytes = 10 * 1024 * 1024;

        upload.addFileRejectedListener(event -> {
            String errorMessage = event.getErrorMessage();

            Notification notification = Notification.show(errorMessage, 5000,
                    Notification.Position.MIDDLE);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        });

        UploadsI18N i18n = new UploadsI18N();

        i18n.getError().setFileIsTooBig(
                "The image exceeds the maximum allowed size of 10MB.");
        i18n.getError().setIncorrectFileType(
                "The provided image does not have the correct format (PNG or JPEG).");
        upload.setI18n(i18n);

        upload.setMaxFileSize(maxFileSizeInBytes);
        upload.setAcceptedFileTypes("image/png", "image/jpeg");

        upload.getElement().addEventListener("upload-abort", event -> {
             newImage.setSrc("");
             newImage.setVisible(false);
             newArtworkText.setVisible(false);
             currentImage.setVisible(true);
             currentText.setVisible(true);
        });
    }
}
