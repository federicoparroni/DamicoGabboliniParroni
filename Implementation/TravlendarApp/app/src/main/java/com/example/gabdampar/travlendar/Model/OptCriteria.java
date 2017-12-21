/**
 * Created by gabdampar on 30/11/2017.
 */

package com.example.gabdampar.travlendar.Model;

public enum OptCriteria {

    OPTIMIZE_TIME,
    OPTIMIZE_COST,
    OPTIMIZE_CARBON;

    @Override
    public String toString() {
        switch(this) {
            case OPTIMIZE_TIME: return "Time optimization";
            case OPTIMIZE_CARBON: return "Carbon optimization";
            case OPTIMIZE_COST: return "Cost optimization";
            default: throw new IllegalArgumentException();
        }
    }
}