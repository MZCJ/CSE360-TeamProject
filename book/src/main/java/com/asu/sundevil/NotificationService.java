package com.asu.sundevil;

import com.asu.sundevil.model.User;

public class NotificationService {
    public void sendEmail(User user, String subj, String body) {
        System.out.printf("[EMAIL→%s] %s – %s%n",
          user.getUsername(), subj, body);
    }
}
