package Model;

import Annotations.Column;
import Annotations.Entity;
import Annotations.Primary;

@Entity(tableName = "games")
public class Game {
    @Primary(keyName = "gameID")
    int gameID;
    @Column(columnName = "title")
    String title;
    @Column(columnName = "publisher")
    String publisher;
    @Column(columnName = "developer")
    String developer;

    public Game(){
        title = "";
        publisher = "";
        developer = "";
    }

    //Getters and Setters
    public int getGameID() {
        return gameID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setGameID(int id){
        this.gameID = id;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getDeveloper() {
        return developer;
    }

    public void setDeveloper(String developer) {
        this.developer = developer;
    }
}
