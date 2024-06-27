package com.example.application.views.comment;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.communication.PushMode;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;

import java.util.concurrent.CopyOnWriteArrayList;

@Route("jaja")
public class ChatView extends AppLayout {

    private static int counter = 0; // Shared counter among all instances
    private static boolean isReacted = true;
    private Span counterLabel;
    private Button incrementButton = new Button("Increment");

    // Shared state to keep track of all UIs
    private static final CopyOnWriteArrayList<UI> uiList = new CopyOnWriteArrayList<>();

    public ChatView() {
        init();
    }

    protected void init() {
        VerticalLayout layout = new VerticalLayout();
        counterLabel = new Span(String.valueOf(counter));

        // Add the current UI to the list
        UI currentUI = UI.getCurrent();
        uiList.add(currentUI);

        incrementButton.addClickListener(e -> {
            // Increment the counter
            counter++;
            // Update the current UI's counter label
            updateCounterLabel(currentUI);

            // Broadcast the update to all other UIs
            broadcastCounterUpdate();
        });

        layout.add(counterLabel, incrementButton);
        setContent(layout);

        // Remove UI from list when detached
        currentUI.addDetachListener(event -> uiList.remove(currentUI));
    }

    private void updateCounterLabel(UI ui) {
        ui.access(() -> {
            if(isReacted){
            	counterLabel.setText(String.valueOf(counter));
            	incrementButton.setText("Add");
            	incrementButton.setIcon(new Icon(VaadinIcon.HEART));
            	isReacted = false;
            }else{
            	counterLabel.setText(String.valueOf(counter));
            	incrementButton.setText("Reacted");
            	incrementButton.setIcon(new Icon(VaadinIcon.THUMBS_UP));

            	isReacted = true;
            }
        });
    }

    private void broadcastCounterUpdate() {
        for (UI ui : uiList) {
            ui.access(() -> {
                // Ensure UI is still attached before updating
                if (ui.isAttached()) {
                    for (ChatView view : ui.getChildren()
                                            .filter(ChatView.class::isInstance)
                                            .map(ChatView.class::cast)
                                            .toList()) {
                        if(isReacted){
                           view.counterLabel.setText(String.valueOf(counter));
                           view.incrementButton.setText("Add");
            		   view.incrementButton.setIcon(new Icon(VaadinIcon.HEART));
            		   isReacted = false;
            		}else{
                	   counterLabel.setText(String.valueOf(counter));
                	   incrementButton.setText("Reacted");
                	   incrementButton.setIcon(new Icon(VaadinIcon.THUMBS_UP));
                	   isReacted = true;
            		}
                    }
                }
            });
        }
    }
}
