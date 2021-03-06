package tw.openedu.www.user;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.inject.Inject;

import tw.openedu.www.task.Task;

public abstract class DeleteAccountImageTask extends
        Task<Void> {

    @Inject
    private UserAPI userAPI;

    @NonNull
    private final String username;

    public DeleteAccountImageTask(@NonNull Context context, @NonNull String username) {
        super(context);
        this.username = username;
    }


    public Void call() throws Exception {
        userAPI.deleteProfileImage(username);
        return null;
    }
}
