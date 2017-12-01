package com.example.gabdampar.travlendar.Controller;

import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.routing.*;

import java.util.List;


/**
 * Created by gabbo on 30/11/2017.
 */

public class MappingServiceAPIWrapper {
    private static final MappingServiceAPIWrapper ourInstance = new MappingServiceAPIWrapper();

    public static MappingServiceAPIWrapper getInstance() {
        return ourInstance;
    }

    private MappingServiceAPIWrapper() {
    }

    public void prova() {
        CoreRouter router = new CoreRouter();

        // Select routing options
        RoutePlan routePlan = new RoutePlan();

        RouteOptions routeOptions = new RouteOptions();
        routeOptions.setTransportMode(RouteOptions.TransportMode.PUBLIC_TRANSPORT);
        routeOptions.setRouteType(RouteOptions.Type.FASTEST);
        routePlan.setRouteOptions(routeOptions);

        // Select Waypoints for your routes
        routePlan.addWaypoint(new RouteWaypoint(new GeoCoordinate(49.1966286, -123.0053635)));
        routePlan.addWaypoint(new RouteWaypoint(new GeoCoordinate(49.1947289, -123.1762924)));
        router.calculateRoute(routePlan, new RouterListener());
    }

        private final class RouterListener implements CoreRouter.Listener {

            // Method defined in Listener
            public void onProgress(int percentage) {
                // Display a message indicating calculation progress
            }

            // Method defined in Listener
            public void onCalculateRouteFinished(List<RouteResult> routeResult, RoutingError error) {

            }
        }

    }

