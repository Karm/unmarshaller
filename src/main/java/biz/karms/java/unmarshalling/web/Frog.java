package biz.karms.java.unmarshalling.web;

import java.io.Serializable;

/**
 * @author Michal Karm Babacek
 */
public class Frog implements Serializable {

    private static final long serialVersionUID = 6035631294441924746L;

    private String name;

    public Frog(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
