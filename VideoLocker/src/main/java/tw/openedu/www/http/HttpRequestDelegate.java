package tw.openedu.www.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import tw.openedu.www.base.MainApplication;
import tw.openedu.www.http.cache.CacheManager;
import tw.openedu.www.logger.Logger;
import tw.openedu.www.util.NetworkUtil;

import java.lang.reflect.Type;
import java.net.HttpURLConnection;

public abstract class HttpRequestDelegate<T> {
    protected final Logger logger = new Logger(getClass().getName());
    protected  IApi api;
    protected CacheManager cacheManager;
    protected HttpRequestEndPoint endPoint;

    public HttpRequestDelegate(IApi api, CacheManager cacheManager, HttpRequestEndPoint endPoint){
        this.api = api;
        this.cacheManager = cacheManager;
        this.endPoint = endPoint;
    }

    public abstract HttpManager.HttpResult invokeHttpCall() throws Exception;

    /**
     * provide a default implementation. subclass can override this
     * method for custom de-serialization
     */
    public T fromJson(String json) throws Exception{
        Gson gson = new GsonBuilder().create();
        try {
            // check if auth error
            Type type = new TypeToken<T>() {}.getType();
           return gson.fromJson(json, type);
        } catch(Exception ex) {
            // nothing to do here
            throw new JsonParseException(ex);
        }
    }

    public T fetchData(OkHttpUtil.REQUEST_CACHE_TYPE requestCacheType) throws Exception{
        String json = null;
        String cacheKey = endPoint.getCacheKey();
        if ( requestCacheType != OkHttpUtil.REQUEST_CACHE_TYPE.IGNORE_CACHE
            || !NetworkUtil.isConnected(MainApplication.instance()) ){
            try {
                json = cacheManager.get(cacheKey);
            } catch (Exception e) {
                logger.error(e);
            }
            if ( json != null ) {
                try {
                    return fromJson(json);
                } catch (Exception e) {
                    logger.error(e);
                }
            }
        }
        if ( requestCacheType == OkHttpUtil.REQUEST_CACHE_TYPE.ONLY_CACHE )
            return null;

        // get data from server
        HttpManager.HttpResult result = invokeHttpCall();
        if ( result.statusCode == HttpURLConnection.HTTP_OK ) {
            try {
                cacheManager.put(cacheKey, result.body);
            } catch ( Exception e) {
               logger.error(e);
            }
            json = result.body;
        }

        return json == null ? null : fromJson(json);
    }


}
