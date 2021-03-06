package tw.openedu.www.task;

import android.content.Context;
import android.support.annotation.NonNull;

import tw.openedu.www.discussion.DiscussionThread;
import tw.openedu.www.model.Page;

public abstract class SearchThreadListTask extends
        Task<Page<DiscussionThread>> {

    @NonNull
    final String courseId;
    @NonNull
    final String text;
    final int page;

    public SearchThreadListTask(@NonNull Context context, @NonNull String courseId,
                                @NonNull String text, int page) {
        super(context);
        this.courseId = courseId;
        this.text = text;
        this.page = page;
    }

    public Page<DiscussionThread> call() throws Exception {
        return environment.getDiscussionAPI().searchThreadList(courseId, text, page);
    }
}
