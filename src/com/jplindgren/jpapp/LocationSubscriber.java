package com.jplindgren.jpapp;

import android.location.Location;

public interface LocationSubscriber {
	public void locationChanged(Location location);
}
