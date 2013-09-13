package com.jplindgren.jpapp;

import android.location.LocationListener;

public interface LocationSubject extends LocationListener{
	void registerSubscriber(LocationSubscriber subscriber);
	void unRegisterSubscriber(LocationSubscriber subscriber);
}
