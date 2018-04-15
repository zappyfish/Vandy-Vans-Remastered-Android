package liamkengineering.vandyvans.data.types;

import java.util.List;

/**
 * Created by Liam on 4/13/2018.
 */

public interface ArrivalTimeListener {
    public void onArrivalUpdate(List<ArrivalData> arrivalData);
}
