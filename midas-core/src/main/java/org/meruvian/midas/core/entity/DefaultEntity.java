package org.meruvian.midas.core.entity;

/**
 * Created by ludviantoovandi on 24/07/14.
 */
public class DefaultEntity {
    private String id;
    private LogInformation logInformation = new LogInformation();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LogInformation getLogInformation() {
        return logInformation;
    }

    public void setLogInformation(LogInformation logInformation) {
        this.logInformation = logInformation;
    }
}
