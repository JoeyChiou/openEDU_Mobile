package tw.openedu.www.social;

import android.app.Application.ActivityLifecycleCallbacks;
import android.content.Intent;

import tw.openedu.www.logger.Logger;

public interface ISocial extends ActivityLifecycleCallbacks {

    public final Logger logger = new Logger(ISocial.class.getName());
    
    void login();
    void logout();
    void setCallback(ISocial.Callback callback);
    void onActivityResult(int requestCode, int resultCode, Intent data);

    public static interface Callback {
        void onLogin(String accessToken);
    }
}
