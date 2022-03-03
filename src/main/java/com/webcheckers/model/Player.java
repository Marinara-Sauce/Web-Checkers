package com.webcheckers.model;

/**
 * This class represents a single player, and is set as an HTTP attribute
 * for the local player to store. This class's most important function is 
 * storing the player name.
 * 
 * The class can also control whether or not the current player is "playable"
 * meaning if they are currently in a game.
 * 
 * @author <a href="mailto:djb1808@rit.edu">Dan Bliss (djb1808)</a>
 */
public class Player {

    private String name; //A unique player name
    private boolean playable; //Can the player play a new game
    private boolean spectating; // is the player spectating a game
    private String rating = "1000"; // in order to get rid of the comma, just parseInt when needed in each eq. method

    /**
     * Players start with just a name, playable is set to true by default
     * This is usually run after a player is created.
     * 
     * @param name the player's name
     */
    public Player(String name)
    {
        this.name = name;
        this.playable = true;
    }

    public boolean isPlayable(){
        return playable;
    }
    public boolean isSpectating() { return spectating; }

    //Needed to set playable to false if in a game already
    public void setPlayable(boolean tof) { this.playable = tof; }
    public void setSpectating(boolean tof) { this.spectating = tof; }

    public String getName()
    {
        return name;
    }

    public String getRating(){ return this.rating; }

    public void changeRating(boolean win){
        int elo = Integer.parseInt(rating);
        if(win){
            elo += 10;
        }
        else{
            elo -= 10;
        }
        this.rating = "" + elo;
    }

    @Override
    public boolean equals(Object o){
        
        if (!(o instanceof Player))
            return false;
        
        Player p = (Player) o;
        
        //All names are unique, so this is the only thing we have to check
        return p.getName().equals(name);
    }

    @Override
    public String toString()
    {
        return name;
    }

    @Override
    public int hashCode(){
        return this.name.hashCode();
    }
}
