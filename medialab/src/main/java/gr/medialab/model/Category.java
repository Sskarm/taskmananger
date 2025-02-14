package gr.medialab.model;

import java.util.UUID;

public class Category {
    private String id;
    private String name;

    //default constructor (mostly for jsonManager)
    public Category(){
        this.id = "";
        this.name = "";
    }
    
    //Constructor
    public Category(String name) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
    }
    
    //getters
    public String getName() {return name;}
    public String getId() {return id;}

    //setters
    public void setName(String name) {this.name = name;}
    public void setId(String id) {this.id = id;}


    //helps with display
    @Override
    public String toString() {
        return getName();
    }
}
