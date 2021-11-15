package com.mycompany.twitterbot;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Gene Lo
 */
public class FactInfo {
    public String question;
    public String fact;
    public String bitly;

    
    public FactInfo(String question, String fact, String bitly){
        this.question = question;
        this.fact = fact;
        this.bitly = bitly;
    };
}
