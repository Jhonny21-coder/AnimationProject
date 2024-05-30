package com.example.application.views.artworks;

import com.example.application.views.UploadsI18N;
import com.example.application.views.MainLayout;
import com.example.application.data.StudentInfo;
import com.example.application.services.StudentInfoService;
import com.example.application.data.User;
import com.example.application.services.UserServices;
import com.example.application.data.Artwork;
import com.example.application.services.ArtworkService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.*;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.component.icon.*;
import com.vaadin.flow.dom.DomEventListener;
import com.vaadin.flow.dom.DomEvent;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

@Route("addArtwork")
public class AddArtwork extends AppLayout {

    private final StudentInfoService studentInfoService;
    private final UserServices userService;
    private final ArtworkService artworkService;
    private final Span artworkUrlText = new Span("Artwork Image");
    private final Upload upload = new Upload(new MemoryBuffer());
    private final Image uploadedImage = new Image();
    private final DatePicker dateOfPost = new DatePicker("Date Posted");
    private final TimePicker timePosted = new TimePicker("Time Posted");
    private final TextField title = new TextField("Title");
    private final Button save = new Button("Save");
    private final Button close = new Button("Cancel");
    private String originalFilename;

    public AddArtwork(StudentInfoService studentInfoService, UserServices userService,
			ArtworkService artworkService) {
        this.studentInfoService = studentInfoService;
	this.userService = userService;
	this.artworkService = artworkService;

	addClassName("artwork-main");

	uploadedImage.setVisible(false);

	title.setSuffixComponent(new Icon(VaadinIcon.TEXT_LABEL));

	artworkUrlText.addClassName("add-text");
	upload.addClassName("register-upload");
	dateOfPost.addClassName("date-picker");
	timePosted.addClassName("time-picker");
	title.addClassName("register-field");
	uploadedImage.addClassName("uploaded-image");

	save.addClassName("save-artwork");
	save.setIcon(new Icon(VaadinIcon.CHECK));

	close.addClassName("close-artwork");
        close.setIcon(new Icon(VaadinIcon.CLOSE));

	HorizontalLayout uploadLayout = new HorizontalLayout();
	uploadLayout.setSpacing(false);
        uploadLayout.setAlignItems(FlexComponent.Alignment.CENTER);

	Icon uploadIcon = VaadinIcon.FILE_ADD.create();

        // Create the text
        Span hint = new Span("Accepted images formats: (.png, .jpeg)");
        Span hint2 = new Span(")");
        hint.addClassName("hint");
        hint2.addClassName("hin2");

        Span uploadLabel = new Span("Upload Profile Image (");

        // Add both the icon and text to the HorizontalLayout
        uploadLayout.add(uploadIcon, uploadLabel, hint, hint2);

        // Create the button and set the uploadLayout as its component
        Button uploadButton = new Button();
        uploadButton.getElement().appendChild(uploadLayout.getElement());

        // Set the uploadButton as the upload button
        upload.setUploadButton(uploadButton);

        int maxFileSizeInBytes = 10 * 1024 * 1024; // 10MB

        upload.addFileRejectedListener(event -> {
            String errorMessage = event.getErrorMessage();

            Notification notification = Notification.show(errorMessage, 5000,
                    Notification.Position.MIDDLE);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        });

        UploadsI18N i18n = new UploadsI18N();
        i18n.getError().setFileIsTooBig("The image exceeds the maximum allowed size of 10MB.");
        i18n.getError().setIncorrectFileType("The provided image does not have the correct format (PNG or JPEG).");

        upload.setI18n(i18n);
        upload.setMaxFileSize(maxFileSizeInBytes);
        upload.setAcceptedFileTypes("image/png", "image/jpeg");
        upload.addSucceededListener(event -> {
            MemoryBuffer buffer = (MemoryBuffer) upload.getReceiver();

            try {
                InputStream inputStream = buffer.getInputStream();
                byte[] bytes = inputStream.readAllBytes();
                originalFilename = event.getFileName();
                StreamResource resource = new StreamResource(originalFilename, () -> new ByteArrayInputStream(bytes));
                uploadedImage.setSrc(resource);
                uploadedImage.setVisible(true);
            } catch (IOException e) {
                Notification.show("Error uploading artwork image", 3000, Notification.Position.TOP_CENTER);
            }
        });

	upload.getElement().addEventListener("upload-abort", new DomEventListener() {
	    @Override
            public void handleEvent(DomEvent domEvent) {
                uploadedImage.setSrc("");
                uploadedImage.setVisible(false);
            }
	});

        save.addClickListener(event -> {
            MemoryBuffer buffer = (MemoryBuffer) upload.getReceiver();

            try {
            	InputStream inputStream = buffer.getInputStream();
                byte[] bytes = inputStream.readAllBytes();

                String filePath = "/data/data/com.termux/files/home/MyVaadinProject/src/main/resources/META-INF/resources/artwork_images/" + originalFilename;
                FileOutputStream outputStream = new FileOutputStream(filePath);
                outputStream.write(bytes);
            } catch(Exception e){
            	e.printStackTrace();
            }

            String email = (String) VaadinSession.getCurrent().getAttribute("user");
            User user = userService.findCurrentUser();

            if (user != null) {
                LocalDate dateValue = dateOfPost.getValue();
                String descriptionValue = title.getValue();
		String emailValue = user.getEmail();
		LocalTime timeValue = timePosted.getValue();

                if (originalFilename != null) {
                    artworkService.saveArtwork(emailValue, originalFilename, dateValue,
			timeValue, descriptionValue);

                    Notification.show("Artwork saved successfully", 3000, Notification.Position.TOP_CENTER)
			.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
		    getUI().ifPresent(ui -> ui.navigate("main-artworks"));
                } else {
                    Notification.show("Please fill the missing field", 3000, Notification.Position.TOP_CENTER)
			.addThemeVariants(NotificationVariant.LUMO_ERROR);
                }
            } else {
                Notification.show("Student not found", 3000, Notification.Position.TOP_CENTER)
			.addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });

        close.addClickListener(event -> {
            getUI().ifPresent(ui -> ui.navigate("main-artworks"));
        });

	Div imageDiv = new Div(uploadedImage);
	imageDiv.addClassName("uploaded-image");

	FormLayout formLayout = new FormLayout();
        formLayout.add(artworkUrlText, upload, imageDiv, dateOfPost, timePosted, title, new HorizontalLayout(save, close));
	formLayout.setColspan(title, 3);
        formLayout.setResponsiveSteps(new ResponsiveStep("0", 1),
                new ResponsiveStep("500px", 3));

	VerticalLayout artworksDiv = new VerticalLayout(formLayout);
	setContent(artworksDiv);
   }
}
