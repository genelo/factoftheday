package com.mycompany.twitterbot;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;



/**
 *
 * @author Gene Lo
 */
public class TwitterBotService {
    /*
    Consolidates Fact info from google and bitly
    */
    
    private Properties props;
    
    public TwitterBotService(){
        try {
            loadProps();
        } catch (IOException ex) {
            Logger.getLogger(TwitterBotService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public FactInfo getFactoid() throws IOException, URISyntaxException, InterruptedException {

    //Scrape data from search result from Google
    Document doc = Jsoup.connect("https://www.google.com/search?q=im+feeling+curious").get();
    String question = doc.select(".sW6dbe").text();
    String answer = doc.select(".EikfZ").text();
    String link = doc.select(".dEPbOe").text();
    
    String bitly = shortenUrl(link);
    
    return new FactInfo(question, answer, bitly);
  }
/*
    calls bitly API to create shortend url
    */
  public String shortenUrl(final String url) throws JsonProcessingException, IOException, InterruptedException{
      //shorten url with Bitly API
      
        HashMap values = new HashMap<String, String>() {{
            put("long_url", url);
            put("domain", "bit.ly");
            put("group_guid", props.getProperty("GROUP_GUID"));
        }};
      
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper
                .writeValueAsString(values);

      HttpRequest request = HttpRequest.newBuilder(
              URI.create("https://api-ssl.bitly.com/v4/shorten"))
              .header("accept", "application/json")
              .header("Authorization", props.getProperty("BITLY_TOKEN"))
              .POST(BodyPublishers.ofString(requestBody))
              .build();

     HttpClient client = HttpClient.newHttpClient();
     HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
     ObjectMapper mapper = new ObjectMapper();
     BitlyResponse bitlyResponse = mapper.readValue(response.body(), BitlyResponse.class);
     
     return bitlyResponse.link;
     
  }
  
  /*
  Posts Tweet via Twitter API
  */
  public void TweetQuestion(FactInfo factInfo, long id) throws TwitterException{

    ConfigurationBuilder cb = new ConfigurationBuilder();
    cb.setDebugEnabled(true)
            .setOAuthConsumerKey(props.getProperty("API_KEY"))
            .setOAuthConsumerSecret(props.getProperty("SECRET_KEY"))
            .setOAuthAccessToken(props.getProperty("ACCESS_TOKEN"))
            .setOAuthAccessTokenSecret(props.getProperty("ACCESS_TOKEN_SECRET"));
    TwitterFactory tf = new TwitterFactory(cb.build());
    Twitter twitter = tf.getInstance();
    StatusUpdate statusUpdate = new StatusUpdate(factInfo.question);
    statusUpdate.attachmentUrl("https://twitter.com/genelo27/status/"  + String.valueOf(id));
    twitter.updateStatus(statusUpdate);
        
  }

  public long TweetAnswer(FactInfo factInfo) throws UnsupportedEncodingException, JsonProcessingException, IOException, InterruptedException, TwitterException{
        
      StringBuilder answer = new StringBuilder();
      StringBuilder link = new StringBuilder();
      link.append("... For more info: ");
      link.append(factInfo.bitly);
   
      int remainingChar = 279 - link.length();
      if(factInfo.fact.length() > remainingChar){
          answer.append(factInfo.fact.substring(0,remainingChar));
      }else{
          answer.append(factInfo.fact);
      }
      answer.append(link);
      
      String status = answer.toString();
            
      ConfigurationBuilder cb = new ConfigurationBuilder();      
      cb.setDebugEnabled(true)
              .setOAuthConsumerKey(props.getProperty("API_KEY"))
              .setOAuthConsumerSecret(props.getProperty("SECRET_KEY"))
              .setOAuthAccessToken(props.getProperty("ACCESS_TOKEN"))
              .setOAuthAccessTokenSecret(props.getProperty("ACCESS_TOKEN_SECRET"));
      TwitterFactory tf = new TwitterFactory(cb.build());
      Twitter twitter = tf.getInstance();
        
      StatusUpdate statusUpdate = new StatusUpdate(status);
      Status s = twitter.updateStatus(statusUpdate);
      
      return s.getId();
  }

  private void loadProps() throws IOException{
      props = new Properties();
      String propFileName = "application.properties";

      FileReader reader = new FileReader(propFileName);

      props.load(reader);            
  }
}
