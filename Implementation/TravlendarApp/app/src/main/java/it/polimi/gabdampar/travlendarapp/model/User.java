package it.polimi.gabdampar.travlendarapp.model;

/**
 * Created by federico on 09/11/17.
 */

public class User {

    // it contains the email of the user, must be modified when the user makes his registration to the system
    private String userEmail;
    private String password;
    private Boolean hasTravelPass;
    private Boolean hasBike;
    private Boolean hasCar;

    // Costruttore
    /* non ci ho messo gli altri attributi che deve avere in quanto per come abbiamo deciso quelli vengono settati dall'utente
       una volta effettuato il primo login, quindi ho pensato che l'oggetto utente dovesse essere creato prima-
    */
    public User(String userEmail, String password){
        this.userEmail = userEmail;
        this.password = password;
        this.hasTravelPass = Boolean.FALSE;
        this.hasBike = Boolean.FALSE;
        this.hasCar= Boolean.FALSE;
    }

    // metodi SetAttribute()--------------------------------

    public void SetHasTravelPass(Boolean b){
        this.hasTravelPass = b;
    }

    public void SetHasBike(Boolean b){
        this.hasBike = b;
    }

    public void SetHasCar(Boolean b){
        this.hasCar = b;
    }

    //metodi GetAttribute()----------------------------------

    public Boolean GetHasTravelPass(){
        return this.hasTravelPass;
    }

    public Boolean GetHasBike(){
        return this.hasBike;
    }

    public Boolean GetHasCar(){
        return this.GetHasCar();
    }
}