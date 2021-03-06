package com.sismics.reader.ui.adapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.widget.BaseAdapter;

import com.sismics.android.Log;
import com.sismics.android.SismicsHttpResponseHandler;
import com.sismics.reader.listener.ArticlesHelperListener;
import com.sismics.reader.resource.SubscriptionResource;

/**
 * Static helper to use the same articles between multiples adapters.
 * 
 * @author bgamard
 */
public class SharedArticlesAdapterHelper {
    /**
     * Current instance.
     */
    private static SharedArticlesAdapterHelper instance;
    
    /**
     * Shared data.
     */
    private List<JSONObject> articleItems = new ArrayList<JSONObject>();
    
    /**
     * Adapters sharing the same data.
     */
    private Set<Object> adapters = new HashSet<Object>();
    
    /**
     * Listeners on articles loading.
     */
    private Set<ArticlesHelperListener> listeners = new HashSet<ArticlesHelperListener>();
    
    /**
     * API URL.
     */
    private String url;
    
    /**
     * Unread state.
     */
    private boolean unread;
    
    /**
     * Is loading.
     */
    private boolean loading;
    
    /**
     * All articles are loaded.
     */
    private boolean fullyLoaded;
    
    /**
     * Returns an instance.
     * @return
     */
    public static SharedArticlesAdapterHelper getInstance() {
        if (instance == null) {
            instance = new SharedArticlesAdapterHelper();
        }
        return instance;
    }
    
    /**
     * Getter of articleItems.
     * @return
     */
    public List<JSONObject> getArticleItems() {
        return articleItems;
    }
    
    /**
     * Add adapter.
     * @param adapter
     */
    public void addAdapter(Object adapter, ArticlesHelperListener listener) {
        adapters.add(adapter);
        listeners.add(listener);
    }
    
    /**
     * Remove adapter.
     * @param adapter
     */
    public void removeAdapter(Object adapter, ArticlesHelperListener listener) {
        adapters.remove(adapter);
        listeners.remove(listener);
    }
    
    /**
     * Called on data changed.
     */
    public void onDataChanged() {
        for (Object adapter : adapters) {
            if (adapter instanceof BaseAdapter) {
                ((BaseAdapter) adapter).notifyDataSetChanged();
            }
            if (adapter instanceof PagerAdapter) {
                ((PagerAdapter) adapter).notifyDataSetChanged();
            }
        }
    }

    /**
     * Restart shared data on a new context.
     */
    public void restart(String url, boolean unread) {
        adapters.clear();
        articleItems = new ArrayList<JSONObject>();
        this.url = url;
        this.unread = unread;
        this.loading = false;
        this.fullyLoaded = false;
    }
    
    /**
     * Getter of URL.
     * @return URL
     */
    public String getUrl() {
        return url;
    }
    
    /**
     * Load more articles.
     * @param context
     */
    public void load(Context context) {
        final List<JSONObject> items = articleItems;
        
        if (loading || fullyLoaded) {
            return;
        }
        
        loading = true;

        for (ArticlesHelperListener listener : listeners) {
            listener.onStart();
        }
        
        String afterArticleId = null;
        if (items.size() > 0) {
            afterArticleId = items.get(items.size() - 1).optString("id");
        }
        
        SubscriptionResource.feed(context, url, unread, 10, afterArticleId, new SismicsHttpResponseHandler() {
            @Override
            public void onSuccess(JSONObject json) {
                // If reference has not changed, let's update the shared data
                if (items != articleItems) {
                    return;
                }
                
                JSONArray articles = json.optJSONArray("articles");
                for (int i = 0; i < articles.length(); i++) {
                    JSONObject article = articles.optJSONObject(i);
                    
                    // Precompute some data
                    try {
                        String description = article.optString("description");
                        String cleanedDescription = description.replaceAll("\\<.*?>", "").trim();
                        int length = cleanedDescription.length();
                        String summary = cleanedDescription.substring(0, length < 300 ? length : 300);
                        article.put("summary", summary);
                    } catch (JSONException e) {
                        Log.e("ArticlesAdapter", "Cannot precompute article", e);
                    }
                    
                    items.add(article);
                }
                
                if (articles.length() == 0) {
                    fullyLoaded = true;
                }
                
                onDataChanged();
            }
            
            @Override
            public void onFinish() {
                loading = false;
                
                for (ArticlesHelperListener listener : listeners) {
                    listener.onEnd();
                }
            }
        });
    }
}
