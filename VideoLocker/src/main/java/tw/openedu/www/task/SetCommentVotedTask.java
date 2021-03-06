package tw.openedu.www.task;

import android.content.Context;
import android.support.annotation.NonNull;

import tw.openedu.www.discussion.DiscussionComment;

public abstract class SetCommentVotedTask extends Task<DiscussionComment> {
    private final DiscussionComment comment;
    private final boolean voted;

    public SetCommentVotedTask(@NonNull Context context,
                               @NonNull DiscussionComment comment, boolean voted) {
        super(context);
        this.comment = comment;
        this.voted = voted;
    }

    public DiscussionComment call() throws Exception {
        return environment.getDiscussionAPI().setCommentVoted(comment, voted);
    }
}
