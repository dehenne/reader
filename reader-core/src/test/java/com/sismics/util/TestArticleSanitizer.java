package com.sismics.util;

import com.sismics.reader.core.dao.file.rss.RssReader;
import com.sismics.reader.core.model.jpa.Article;
import com.sismics.reader.core.model.jpa.Feed;
import com.sismics.reader.core.util.sanitizer.ArticleSanitizer;
import junit.framework.Assert;
import org.junit.Test;

import java.io.InputStream;
import java.util.List;

/**
 * Test of the article sanitizer.
 * 
 * @author jtremeaux
 */
public class TestArticleSanitizer {
    /**
     * Tests the article sanitizer.
     * 
     * @throws Exception
     */
    @Test
    public void articleSanitizerImageAkeweaTest() throws Exception {
        // Load a feed
        InputStream is = getClass().getResourceAsStream("/feed/feed_atom_akewea.xml");
        RssReader reader = new RssReader();
        reader.readRssFeed(is);
        Feed feed = reader.getFeed();
        Assert.assertEquals("http://blog.akewea.com/", feed.getUrl());
        List<Article> articleList = reader.getArticleList();
        Assert.assertEquals(20, articleList.size());
        Article article = articleList.get(0);

        // Images: transform relative URLs to absolute
        ArticleSanitizer articleSanitizer = new ArticleSanitizer();
        String baseUrl = UrlUtil.getBaseUri(feed, article);
        String html = articleSanitizer.sanitize(baseUrl, article.getDescription());
        Assert.assertTrue(html.contains("\"http://blog.akewea.com/themes/akewea-4/smilies/redface.png\""));
        Assert.assertTrue(html.contains("\"http://blog.akewea.com/themes/akewea-4/smilies/test.png\""));
    }

    /**
     * Tests the article sanitizer.
     *
     * @throws Exception
     */
    @Test
    public void articleSanitizerImageDilbertTest() throws Exception {
        // Load a feed
        InputStream is = getClass().getResourceAsStream("/feed/feed_rss2_dilbert.xml");
        RssReader reader = new RssReader();
        reader.readRssFeed(is);
        Feed feed = reader.getFeed();
        Assert.assertEquals("http://dilbert.com/blog", feed.getUrl());
        List<Article> articleList = reader.getArticleList();
        Assert.assertEquals(20, articleList.size());
        Article article = articleList.get(0);

        // Images: transform relative URLs to absolute
        ArticleSanitizer articleSanitizer = new ArticleSanitizer();
        String baseUrl = UrlUtil.getBaseUri(feed, article);
        String html = articleSanitizer.sanitize(baseUrl, article.getDescription());
        Assert.assertTrue(html.contains("\"http://dilbert.com/dyn/tiny/File/photo.JPG\""));
    }

    /**
     * Tests the article sanitizer.
     * 
     * @throws Exception
     */
    @Test
    public void articleSanitizerImageXmlBaseTest() throws Exception {
        // Load a feed
        InputStream is = getClass().getResourceAsStream("/feed/feed_atom_marijnhaverbeke.xml");
        RssReader reader = new RssReader();
        reader.readRssFeed(is);
        Feed feed = reader.getFeed();
        Assert.assertEquals("marijnhaverbeke.nl/blog", feed.getTitle());
        List<Article> articleList = reader.getArticleList();
        Assert.assertEquals(26, articleList.size());
        Article article = articleList.get(0);

        // Images: transform relative URLs to absolute
        ArticleSanitizer articleSanitizer = new ArticleSanitizer();
        Assert.assertTrue(article.getDescription().contains("\"res/tern_simple_graph.png\""));
        String html = articleSanitizer.sanitize(article.getBaseUri(), article.getDescription());
        Assert.assertEquals("http://marijnhaverbeke.nl/blog/", article.getBaseUri());
        Assert.assertFalse(html.contains("\"res/tern_simple_graph.png\""));
        Assert.assertTrue(html.contains("\"http://marijnhaverbeke.nl/blog/res/tern_simple_graph.png\""));
    }

    /**
     * Tests the article sanitizer.
     * 
     * @throws Exception
     */
    @Test
    public void articleSanitizerIframeVimeoTest() throws Exception {
        // Load a feed
        InputStream is = getClass().getResourceAsStream("/feed/feed_rss2_fubiz.xml");
        RssReader reader = new RssReader();
        reader.readRssFeed(is);
        Feed feed = reader.getFeed();
        Assert.assertEquals("http://www.fubiz.net", feed.getUrl());
        List<Article> articleList = reader.getArticleList();
        Assert.assertEquals(10, articleList.size());
        Article article = articleList.get(0);

        // Allow iframes to Vimeo
        ArticleSanitizer articleSanitizer = new ArticleSanitizer();
        String html = articleSanitizer.sanitize(feed.getUrl(), article.getDescription());
        Assert.assertTrue(html.contains("Déjà nominé dans la"));
        Assert.assertTrue(html.contains("<iframe src=\"http://player.vimeo.com/video/63898090?title&#61;0&amp;byline&#61;0&amp;portrait&#61;0&amp;color&#61;ffffff\" width=\"640\" height=\"360\">"));
    }

    /**
     * Tests the article sanitizer.
     * 
     * @throws Exception
     */
    @Test
    public void articleSanitizerIframeDailymotionTest() throws Exception {
        // Load a feed
        InputStream is = getClass().getResourceAsStream("/feed/feed_rss2_korben.xml");
        RssReader reader = new RssReader();
        reader.readRssFeed(is);
        Feed feed = reader.getFeed();
        Assert.assertEquals("http://korben.info", feed.getUrl());
        List<Article> articleList = reader.getArticleList();
        Assert.assertEquals(30, articleList.size());
        Article article = articleList.get(21);

        // Allow iframes to Vimeo
        ArticleSanitizer articleSanitizer = new ArticleSanitizer();
        String html = articleSanitizer.sanitize(feed.getUrl(), article.getDescription());
        Assert.assertTrue(html.contains("On ne va pas faire les étonnés, hein"));
        Assert.assertTrue(html.contains("<iframe src=\"http://www.dailymotion.com/embed/video/xy9zdc\" height=\"360\" width=\"640\">"));
    }

    /**
     * Tests the article sanitizer.
     * 
     * @throws Exception
     */
    @Test
    public void articleSanitizerIframeYoutubeTest() throws Exception {
        // Load a feed
        InputStream is = getClass().getResourceAsStream("/feed/feed_rss2_korben.xml");
        RssReader reader = new RssReader();
        reader.readRssFeed(is);
        Feed feed = reader.getFeed();
        Assert.assertEquals("http://korben.info", feed.getUrl());
        List<Article> articleList = reader.getArticleList();
        Assert.assertEquals(30, articleList.size());
        Article article = articleList.get(20);

        // Allow iframes to Youtube
        ArticleSanitizer articleSanitizer = new ArticleSanitizer();
        String html = articleSanitizer.sanitize(feed.getUrl(), article.getDescription());
        Assert.assertTrue(html.contains("Y&#39;a pas que XBMC dans la vie"));
        Assert.assertTrue(html.contains("<iframe src=\"http://www.youtube.com/embed/n2d4c8JIT0E?feature&#61;player_embedded\" height=\"360\" width=\"640\">"));
        
        // Allow iframes to Youtube HTTPS
        article = articleList.get(0);
        articleSanitizer = new ArticleSanitizer();
        html = articleSanitizer.sanitize(feed.getUrl(), article.getDescription());
        Assert.assertTrue(html.contains("La RetroN 5 sera équipée d&#39;une sortie HDMI"));
        Assert.assertTrue(html.contains("<iframe src=\"https://www.youtube.com/embed/5OcNy7t17LA?feature&#61;player_embedded\" height=\"360\" width=\"640\">"));
        
        // Allow iframes to Youtube without protocol
        article = articleList.get(15);
        articleSanitizer = new ArticleSanitizer();
        html = articleSanitizer.sanitize(feed.getUrl(), article.getDescription());
        Assert.assertTrue(html.contains("Si quand vous étiez petit"));
        Assert.assertTrue(html.contains("<iframe src=\"//www.youtube.com/embed/7vIi0U4rSX4?feature&#61;player_embedded\" height=\"360\" width=\"640\">"));
    }
    
    /**
     * Tests the article sanitizer.
     * 
     * @throws Exception
     */
    @Test
    public void articleSanitizerIframeGoogleMapsTest() throws Exception {
        // Load a feed
        InputStream is = getClass().getResourceAsStream("/feed/feed_rss2_korben.xml");
        RssReader reader = new RssReader();
        reader.readRssFeed(is);
        Feed feed = reader.getFeed();
        List<Article> articleList = reader.getArticleList();

        // Allow iframes to Google Maps
        ArticleSanitizer articleSanitizer = new ArticleSanitizer();
        Article article = articleList.get(15);
        articleSanitizer = new ArticleSanitizer();
        String html = articleSanitizer.sanitize(feed.getUrl(), article.getDescription());
        Assert.assertTrue(html.contains("<iframe src=\"https://maps.google.com/?t&#61;m&amp;layer&#61;c&amp;panoid&#61;JkQZAcDH9c2tky4T8irVUg&amp;cbp&#61;13,219.16,,0,41.84&amp;cbll&#61;35.370043,138.739238&amp;ie&#61;UTF8&amp;source&#61;embed&amp;ll&#61;35.336203,138.739128&amp;spn&#61;0.117631,0.216293&amp;z&#61;12&amp;output&#61;svembed\" height=\"420\" width=\"630\">"));
    }
    
    /**
     * Tests the article sanitizer.
     * 
     * @throws Exception
     */
    @Test
    public void articleSanitizerIframeSoundCloudTest() throws Exception {
        // Load a feed
        InputStream is = getClass().getResourceAsStream("/feed/feed_rss2_korben.xml");
        RssReader reader = new RssReader();
        reader.readRssFeed(is);
        Feed feed = reader.getFeed();
        List<Article> articleList = reader.getArticleList();

        // Allow iframes to SoundCloud
        ArticleSanitizer articleSanitizer = new ArticleSanitizer();
        Article article = articleList.get(15);
        articleSanitizer = new ArticleSanitizer();
        String html = articleSanitizer.sanitize(feed.getUrl(), article.getDescription());
        Assert.assertTrue(html.contains("<iframe height=\"166\" src=\"https://w.soundcloud.com/player/?url&#61;http%3A%2F%2Fapi.soundcloud.com%2Ftracks%2F105401675\" width=\"100%\"></iframe>"));
    }

    /**
     * Tests the article sanitizer.
     * 
     * @throws Exception
     */
    @Test
    public void articleSanitizerIframeSlashdotTest() throws Exception {
        // Load a feed
        InputStream is = getClass().getResourceAsStream("/feed/feed_rss2_slashdot.xml");
        RssReader reader = new RssReader();
        reader.readRssFeed(is);
        Feed feed = reader.getFeed();
        Assert.assertEquals("http://slashdot.org/", feed.getUrl());
        List<Article> articleList = reader.getArticleList();
        Assert.assertEquals(25, articleList.size());
        Article article = articleList.get(0);

        // Allow unknown iframes
        ArticleSanitizer articleSanitizer = new ArticleSanitizer();
        String html = articleSanitizer.sanitize(feed.getUrl(), article.getDescription());
        Assert.assertTrue(html.contains("Higgs data and the cosmic microwave background map from the Planck mission"));
        Assert.assertTrue(html.contains("<iframe src=\"http://slashdot.org/slashdot-it.pl?op&#61;discuss&amp;id&#61;3658423&amp;smallembed&#61;1\" style=\"height: 300px; width: 100%;\">"));
    }

    /**
     * Tests the article sanitizer.
     *
     * @throws Exception
     */
    @Test
    public void articleSanitizerIframeWhydTest() throws Exception {
        // Load a feed
        InputStream is = getClass().getResourceAsStream("/feed/feed_rss2_cultiz.xml");
        RssReader reader = new RssReader();
        reader.readRssFeed(is);
        Feed feed = reader.getFeed();
        Assert.assertEquals("http://cultiz.com/blog", feed.getUrl());
        List<Article> articleList = reader.getArticleList();
        Assert.assertEquals(10, articleList.size());
        Article article = articleList.get(2);

        // Allow unknown iframes
        ArticleSanitizer articleSanitizer = new ArticleSanitizer();
        String html = articleSanitizer.sanitize(feed.getUrl(), article.getDescription());
        Assert.assertTrue(html.contains("Quoi de mieux"));
        Assert.assertTrue(html.contains("<iframe src=\"https://whyd.com/u/514ad8737e91c862b2ab7ef1/playlist/6?format&#61;embedV2&amp;embedW&#61;480\" height=\"600\" width=\"600\"></iframe>"));
    }

}
