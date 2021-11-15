/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.twitterbot;

import com.fasterxml.jackson.core.JsonProcessingException;
import twitter4j.TwitterException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;






/**
 *
 * @author Gene Lo
 */
public class App {
    	public static void main(String[] args) throws TwitterException, JsonProcessingException {
            Timer timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask(){
            @Override
            public void run(){
                                try {
                    TwitterBotService service = new TwitterBotService();
                    //Get fact and info from google
                    FactInfo factInfo = service.getFactoid();
                    //Post tweet
                    long id = service.TweetAnswer(factInfo);
                    service.TweetQuestion(factInfo, id);
                } catch (IOException | URISyntaxException | InterruptedException | TwitterException ex) {
                    Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            },0,86400000);
            

        }
}
