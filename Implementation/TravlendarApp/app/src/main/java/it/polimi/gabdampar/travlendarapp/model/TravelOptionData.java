package it.polimi.gabdampar.travlendarapp.model;

import java.util.HashMap;

import it.polimi.gabdampar.travlendarapp.controller.TravelMean.TravelMean;

/**
 * Created by gabbo on 12/11/2017.
 */

public class TravelOptionData {
    int price, carbonEmission; //in euros, in mg/m3
    long duration; //in ms
    HashMap<TravelMean, Integer> distanceTravelledWithMean; //in kms
    String startingLocation, endingLocation;

    // to-do: add directions. to decide how to reprsent that directions, in a textual-parsed form or with the json file

}
