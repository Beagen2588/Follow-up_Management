package com.AMiller.Follow_up_Management.Service;

public class BaseCommunicationFormatter {

    protected String createGreeting(String recipientName){
        return "Hello " + recipientName + ",";
    }
}
