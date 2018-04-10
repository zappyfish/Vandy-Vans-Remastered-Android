package liamkengineering.vandyvans.data.types;

import java.util.List;

/**
 * Created by Liam on 4/10/2018.
 */

/** Initial data for a particular van color */
public class InitialData {

    private final List<VanStop> mVanStopList;
    private final Route mRoute;
    private final String mColor;

    public InitialData(String color, List<VanStop> vanStopList, Route route) {
        mColor = color;
        mVanStopList = vanStopList;
        mRoute = route;
    }

    public String getColor() {
        return mColor;
    }

    public Route getVanRoute() {
        return mRoute;
    }

    public List<VanStop> getVanStops() {
        return mVanStopList;
    }
}
