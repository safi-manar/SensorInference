package us.michaelchen.compasslogger.utils;

import android.location.Location;

/**
 * Taken from https://developer.android.com/guide/topics/location/strategies.html
 * Created by ioreyes on 5/31/16.
 */
public class LocationUtils {
    private static final int TWO_MINUTES = 1000 * 60 * 2;

    /** Determines whether one Location reading is better than the current Location fix
     * @param newLoc  The new Location that you want to evaluate
     * @param oldLoc  The current Location fix, to which you want to compare the new one
     */
    public static Location compare(Location newLoc, Location oldLoc) {
        if(newLoc != null && oldLoc != null) {
            // Check whether the new location fix is newer or older
            long timeDelta = newLoc.getTime() - oldLoc.getTime();
            boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
            boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
            boolean isNewer = timeDelta > 0;

            // If it's been more than two minutes since the current location, use the new location
            // because the user has likely moved
            if (isSignificantlyNewer) {
                return newLoc;
                // If the new location is more than two minutes older, it must be worse
            } else if (isSignificantlyOlder) {
                return oldLoc;
            }

            // Check whether the new location fix is more or less accurate
            int accuracyDelta = (int) (newLoc.getAccuracy() - oldLoc.getAccuracy());
            boolean isLessAccurate = accuracyDelta > 0;
            boolean isMoreAccurate = accuracyDelta < 0;
            boolean isSignificantlyLessAccurate = accuracyDelta > 200;

            // Check if the old and new location are from the same provider
            boolean isFromSameProvider = isSameProvider(newLoc.getProvider(),
                    oldLoc.getProvider());

            // Determine location quality using a combination of timeliness and accuracy
            if (isMoreAccurate) {
                return newLoc;
            } else if (isNewer && !isLessAccurate) {
                return newLoc;
            } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
                return newLoc;
            }
            return oldLoc;
        } else if(newLoc != null && oldLoc == null) {
            return newLoc;
        } else if(newLoc == null && oldLoc != null) {
            return oldLoc;
        } else {
            return null;
        }
    }

    /** Checks whether two providers are the same */
    private static boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }
}
