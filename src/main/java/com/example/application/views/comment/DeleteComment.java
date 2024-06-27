package com.example.application.views.comment;

import com.example.application.data.Comment;
import com.example.application.services.CommentService;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;

public class DeleteComment {

    private final CommentService commentService;
    private final Long COMMENT_ID;

    public DeleteComment(CommentService commentService, Long COMMENT_ID){
    	this.commentService = commentService;
    	this.COMMENT_ID = COMMENT_ID;
    }

    /*public Span deleteButton(){
    	Span span = new Span();

    	ConfirmDialog dialog = new ConfirmDialog();
    	dialog.setCancelable(true);
    	dialog.setConfirmText("Yes");
    	dialog.setHeader("Delete comment?");
    	dialog.setText("Are you sure you want to delete this comment?");
    	dialog.addConfirmListener(event -> {
    	    commentService.deleteComment(COMMENT_ID);
    	});

    	span.addClickListener(event -> {
    	    dialog.open();
    	});

    	return span;
    }*/

    /*if(!isReacted.get()){
                commentReactionService.saveCommentReaction(comment, currentUser, "Like");

                atomicLong.incrementAndGet();

                reacts.setText(String.valueOf(atomicLong.get()));

                likeButton.setText("Reacted");
                likeButton.getStyle().set("color", "var(--lumo-primary-color)");

                isReacted.set(true);
            }else{
                Long reactorId = currentUser.getId();
                Long commentId = comment.getId();

                CommentReaction reactor2 = commentReactionService.getCommentReactionByReactorAndCommentId(reactorId, commentId);

                if(reactor2.getReactType().equalsIgnoreCase("Like")){
                   commentReactionService.removeCommentReaction(reactorId, commentId);

                   atomicLong.decrementAndGet();

                   reacts.setText(String.valueOf(atomicLong.get()));

                   likeButton.setText("React");
                   likeButton.getStyle().set("color", "var(--lumo-contrast-80pct)");

                   isReacted.set(false);
                }else{
                   commentReactionService.updateCommentReaction(reactor2, "Like");

                   likeButton.setText("Reacted");
                   likeButton.getStyle().set("color", "var(--lumo-primary-color)");

                   isReacted.set(true);
                }
            }*/
}
