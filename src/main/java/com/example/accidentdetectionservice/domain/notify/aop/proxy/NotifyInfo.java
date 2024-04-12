package com.example.accidentdetectionservice.domain.notify.aop.proxy;

import static com.example.accidentdetectionservice.domain.notify.entity.Notify.*;

import com.example.accidentdetectionservice.domain.user.entity.User;

public interface NotifyInfo {
    User getReceiver();
    NotificationType getNotificationType();
}
