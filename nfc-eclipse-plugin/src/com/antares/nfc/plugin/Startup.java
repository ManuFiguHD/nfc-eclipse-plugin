/***************************************************************************
 *
 * This file is part of the NFC Eclipse Plugin project at
 * http://code.google.com/p/nfc-eclipse-plugin/
 *
 * Copyright (C) 2012 by Thomas Rorvik Skjolberg / Antares Gruppen AS.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 ****************************************************************************/

package com.antares.nfc.plugin;

import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.ui.IStartup;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

/**
 * This is a startup hook for detecting NFC terminals. There should be some option to disable this TODO.
 * 
 * @author thomas
 *
 */

public class Startup implements IStartup {

	public void earlyStartup() {
		
		try {
			com.antares.nfc.terminal.NdefTerminalDetector.initialize();
		} catch(Throwable e) {
			// assume some classloading issue
		}
		
		if(isReaderEnabledPreference()) {
			enable();
		}
	}
	
	public static boolean hasSeenReader() {
		Preferences preferences = ConfigurationScope.INSTANCE.getNode(Activator.class.getPackage().getName());
		Preferences reader = preferences.node("reader");

		return reader.getBoolean("seen", false);
	}

	public static boolean isReaderEnabledPreference() {
		Preferences preferences = ConfigurationScope.INSTANCE.getNode(Activator.class.getPackage().getName());
		Preferences reader = preferences.node("reader");

		return reader.getBoolean("enable", true);
	}

	public static void setReaderEnabledPreference(boolean enabled) {
		Preferences preferences = ConfigurationScope.INSTANCE.getNode(Activator.class.getPackage().getName());
		Preferences reader = preferences.node("reader");

		if(reader.getBoolean("enable", true) != enabled) {
			reader.putBoolean("enable", enabled);
			
			try {
				  // Forces the application to save the preferences
				  preferences.flush();
			} catch (BackingStoreException e) {
				e.printStackTrace();
			}
		}
	}

	public static void setSeenTerminal(boolean seen) {
		Preferences preferences = ConfigurationScope.INSTANCE.getNode(Activator.class.getPackage().getName());
		Preferences reader = preferences.node("reader");

		if(reader.getBoolean("seen", false) != seen) {
			reader.putBoolean("seen", seen);
			
			try {
				  // Forces the application to save the preferences
				  preferences.flush();
			} catch (BackingStoreException e) {
				e.printStackTrace();
			}
		}
	}

	public static void enable() {
		try {
			com.antares.nfc.terminal.NdefTerminalDetector detector = com.antares.nfc.terminal.NdefTerminalDetector.getInstance();
			if(detector != null) {
				detector.startDetecting();
			}
		} catch(Throwable e) {
			// assume some classloading issue
		}
	}
	
	public static void disable() {
		try {
			com.antares.nfc.terminal.NdefTerminalDetector detector = com.antares.nfc.terminal.NdefTerminalDetector.getInstance();
			if(detector != null) {
				detector.stopDetecting();
			}
		} catch(Throwable e) {
			// assume some classloading issue
		}
	}

}
