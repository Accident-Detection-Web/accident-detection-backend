package com.example.accidentdetectionservice.domain.notify.dto;

public enum NotifyMessage {

    ACCIDENT_DETECTION("CCTV 영상에서 사고가 감지되었습니다.");

    private String message;

    NotifyMessage(String message) {
        this.message = message;
    }

    public String getMessage(){
        return message;
    }
}
