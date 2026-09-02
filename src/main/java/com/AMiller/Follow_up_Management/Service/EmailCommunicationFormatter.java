package com.AMiller.Follow_up_Management.Service;

import org.springframework.stereotype.Component;

@Component
public class EmailCommunicationFormatter extends BaseCommunicationFormatter implements CommunicationFormatter{

    @Override
    public String formatMessage(String recipientName, String message) {

        return createGreeting(recipientName)
                +"\n\n"
                + message;
    }

}
