package com.mycompany.twitterbot;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.util.Date;
import java.util.List;

/**
 *
 * @author Gene Lo
 */
public class BitlyResponse{
    public boolean archived;
    public List<Object> tags;
    public Date created_at;
    public List<Object> deeplinks;
    public String long_url;
    public References references;
    public List<Object> custom_bitlinks;
    public String link;
    public String id;
}

