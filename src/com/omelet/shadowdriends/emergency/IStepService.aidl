package com.omelet.shadowdriends.emergency;

import com.omelet.shadowdriends.emergency.IStepServiceCallback;

interface IStepService {
		boolean isRunning();
		void setSensitivity(int sens);
		void registerCallback(IStepServiceCallback cb);
		void unregisterCallback(IStepServiceCallback cb);
}