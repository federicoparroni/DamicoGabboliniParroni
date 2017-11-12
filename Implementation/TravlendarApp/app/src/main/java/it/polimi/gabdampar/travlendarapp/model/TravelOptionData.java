package it.polimi.gabdampar.travlendarapp.model;

import java.util.HashMap;

import it.polimi.gabdampar.travlendarapp.controller.TravelMean.TravelMean;

/**
 * Created by gabbo on 12/11/2017.
 */

public class TravelOptionData {
    int price, time, carbonEmission;
    HashMap<TravelMean, Integer> distanceTravelledWithMean;

    // to-do: add directions

}
