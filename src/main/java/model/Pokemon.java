package model;


public class Pokemon {
    private String pokemonName;
    private boolean taken;
    private String userID;

    public Pokemon (String name) {
        this.pokemonName = name;
        this.taken = false;
    }

    public void assignPokemon(String ID) {
        this.userID = ID;
        this.taken = true;
    }

    public String getPokemonName() {
        return this.pokemonName;
    }

    public Boolean isTaken() {
        return this.taken;
    }

}
