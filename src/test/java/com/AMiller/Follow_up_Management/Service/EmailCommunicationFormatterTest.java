package com.AMiller.Follow_up_Management.Service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import static org.junit.jupiter.api.Assertions.*;


class EmailCommunicationFormatterTest {

    @Test
    void shouldAddGreeting(){

        EmailCommunicationFormatter emailCommunicationFormatter = new EmailCommunicationFormatter();

        String result = emailCommunicationFormatter.formatMessage("April" , "Please contact the cardiology department.");

        assertEquals(
                "Hello April,\n\nPlease contact the cardiology department.",
                result
        );
    }



}