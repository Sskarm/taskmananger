package gr.medialab.model;

import java.util.UUID;

public class Priority {
        private String id; 
        private String name;

    //default constructor (mostly for jsonManager)
    public Priority(){
        this.id = "";
        this.name = "";
    }

    //Construstor
    public Priority(String name) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
    }

    //getters
    public String getName() {return name;}
    public String getId() {return id;}

    //setters
    public void setName(String name) {this.name = name;}
    public void setId(String id) {this.id = id;}

    //useful functions
    //helps for display
    @Override
    public String toString() {
        return getName();
    }
}
