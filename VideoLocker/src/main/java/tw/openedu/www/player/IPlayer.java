package tw.openedu.www.player;

import android.graphics.Point;
import android.view.View;

import java.io.Serializable;

public interface IPlayer extends Serializable {
    
    static final long serialVersionUID = 5689385691113719237L;

    public void setUri(String uri, int seekTo) throws Exception;
    public void setUriAndPlay(String uri, int seekTo) throws Exception;
    public void restart() throws Exception;
    public void restart(int seekTo) throws Exception;
    public boolean isInError();
    public boolean isPlayingLocally();
    public void start();
    public boolean isPlaying();
    public boolean isPaused();
    public boolean isFrozen();
    public void pause();
    public int getCurrentPosition();
    public void setFullScreen(boolean isFullScreen);
    public boolean isFullScreen();
    public void setPreview(Preview preview);
    public void release();
    public void setPlayerListener(IPlayerListener listener);
    public void setController(PlayerController controller);
    public void freeze();
    public void unfreeze();
    public void setVideoTitle(String title);
    public int getLastFreezePosition();
    public void showController();
    public void hideController();
    public void reset();
    public void setLMSUrl(String url);
    public void setNextPreviousListeners(View.OnClickListener next, View.OnClickListener prev);
    public void callSettings(Point p);
    public void callPlayerSeeked(long previousPos, long nextPos, boolean isRewindClicked);
    public PlayerController getController();
    public boolean isReset();

    // methods from PlayerController.MediaPlayerControl interface
    int     getDuration();
    void    seekTo(int pos);
    int     getBufferPercentage();
    boolean canPause();
    boolean canSeekBackward();
    boolean canSeekForward();
    void    toggleFullScreen();
    void    callLMSServer(String lmsUrl);
}
