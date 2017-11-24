package it.polimi.gabdampar.travlendarapp.controller;

import java.util.Date;
import java.util.List;

import it.polimi.gabdampar.travlendarapp.model.Constraint.Constraint;

/**
 * Created by gabbo on 12/11/2017.
 */

public interface ITravelMeanAPIWrapper {

    /**
     given a date, returns the travelmeans that cant be used, every with itm own time slot in which cant be user
     */
    public List<Constraint> strikeInformations(Date date);
}
