package edu.udg.exit.heartrate.Interfaces;

import java.util.Date;

public interface IMeasureView {

    void sendHeartrate(Date date, Integer heartrate);

    void showText(String text);

}
