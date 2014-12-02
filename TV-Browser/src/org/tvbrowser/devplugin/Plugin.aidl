/*
 * TV-Browser for Android
 * Copyright (C) 2014 René Mach (rene@tvbrowser.org)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to use, copy, modify or merge the Software,
 * furthermore to publish and distribute the Software free of charge without modifications and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR
 * IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.tvbrowser.devplugin;

import org.tvbrowser.devplugin.Channel;
import org.tvbrowser.devplugin.Program;

import java.util.List;

/**
 * Interface for Plugins of the TV-Browser app.
 */
interface Plugin {
    /**
     * Get the version of this Plugin.
     */
	String getVersion();
	
	/**
	 * Gets the name of this Plugin,
	 */
	String getName();

	/**
	 * Get the description of this Plugin.
	 */
	String getDescription();
	
	/**
	 * Get the author of this Plugin.
	 */
	String getAuthor();

    /**
     * Get the license of this Plugin.
     */
    String getLicense();
    /**
     * Get the names for the context menu actions for the given program 
     * @param program The program to get the context menus for.
     * @return A String array with the context menu actions for the given program 
     *         or <code>null</code> if there is no context menu action for the given program.
     */
	String[] getContextMenuActionForProgram(in Program program);
	
	/** 
	 * Called when user selected a context menu of this Plugin
	 * @param program The program the user selected the context menu for
	 * @param contextMenuAction The context menu entry name the menu was selected for
	 * @return <code>true</code> if the program should be marked. <code>false</code> otherwise.
	 */
	boolean onProgramContextMenuSelected(in Program program, in String contextMenuAction);
	
	/**
	 * Gets if this Plugin has preferences.
	 * @return <code>true</code> if this Plugin has preferences, <code>false</code> otherwise. 
	 */
	boolean hasPreferences();
	
	/**
	 * Called when the preferences for this Plugin should be opened.
	 * @param subscribedChannels A list with all currently subscribed channels.
	 */
	void openPreferences(in List<Channel> subscribedChannels);
	
	/**
	 * Called at activation of this Plugin
	 * @return An array of long with all program ids that are marked by this plugin.
	 */
	long[] getMarkedPrograms();
	
	/**
	 * Called at activation of this Plugin to inform about the first currently known
	 * id of the programs. All programs with ids that are smaller than this one don't
	 * exist anymore. 
	 */
	void handleFirstKnownProgramId(long programId);
	
	/**
	 * Called at any activation of this Plugin
	 */
	void onActivation();
	
	/**
	 * Caleld at any deactivation of this Plugin
	 */
	void onDeactivation();
}