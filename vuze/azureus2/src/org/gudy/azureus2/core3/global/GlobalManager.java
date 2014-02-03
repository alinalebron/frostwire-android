/*
 * File    : GlobalManager.java
 * Created : 21-Oct-2003
 * By      : stuff
 * 
 * Azureus - a Java Bittorrent client
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details ( see the LICENSE file ).
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.gudy.azureus2.core3.global;

import java.util.List;
import java.util.Map;

import com.aelitis.azureus.core.AzureusCoreComponent;
import com.aelitis.azureus.core.tag.TaggableResolver;

import org.gudy.azureus2.core3.torrent.TOTorrent;
import org.gudy.azureus2.core3.tracker.client.*;
import org.gudy.azureus2.core3.util.HashWrapper;
import org.gudy.azureus2.core3.download.*;

import org.gudy.azureus2.plugins.dht.mainline.MainlineDHTProvider;

/**
 * The GlobalManager contains a list of all the downloads
 * (DownloadManager objects) that Azureus controls. 
 */
public interface GlobalManager extends AzureusCoreComponent, TaggableResolver {
	/**
	 * Create and add a Download Manager to the global list
	 * 
	 * @param file_name location and name of torrent file
	 * @param save_path path to write the data to
	 * 
	 * @return The Downloadmanger based on the supplied information.<br>
	 *          May return an existing DownloadManager if torrent was already
	 *          in GlobalManager. 
	 */
	public DownloadManager addDownloadManager(String file_name, String save_path);

	/**
	 * Create and add a Download Manager to the global list
	 * 
	 * @param fileName location and name of torrent file
	 * @param savePath path to write the data to
	 * @param initialState Initial state of download. See DownloadManager.STATE_*
	 * @param persistent Whether the download should be treated as persistent download
	 * 
	 * @return The Downloadmanger based on the supplied information.<br>
	 *          May return an existing DownloadManager if torrent was already
	 *          in GlobalManager. 
	 */
	public DownloadManager addDownloadManager(String fileName, byte[]	optionalHash,
			String savePath, int initialState, boolean persistent);

	
	/**
	 * Create and add a Download Manager to the global list
	 * 
	 * @param fileName location and name of torrent file
	 * @param savePath path to write the data to
	 * @param safeFile subdirectory or filename to write the data to
	 * @param initialState Initial state of download. See DownloadManager.STATE_*
	 * @param persistent Whether the download should be treated as persistent download
	 * @param for_seeding Whether the manager should assume the torrent is 
	 *                     already complete and ready for seeding. 
	 * 
	 * @return The Downloadmanger based on the supplied information.<br>
	 *          May return an existing DownloadManager if torrent was already
	 *          in GlobalManager. 
	 */
	public DownloadManager addDownloadManager(String fileName,
			byte[] optionalHash, String savePath, String saveFile,
			int initialState, boolean persistent, boolean for_seeding, 
			DownloadManagerInitialisationAdapter adapter );
	
	
	/**
	 * Create and add a Download Manager to the global list
	 * 
	 * @param fileName location and name of torrent file
	 * @param savePath path to write the data to
	 * @param initialState Initial state of download. See DownloadManager.STATE_*
	 * @param persistent Whether the download should be treated as persistent download
	 * @param for_seeding Whether the manager should assume the torrent is 
	 *                     already complete and ready for seeding. 
	 * 
	 * @return The Downloadmanger based on the supplied information.<br>
	 *          May return an existing DownloadManager if torrent was already
	 *          in GlobalManager. 
	 */
	public DownloadManager addDownloadManager(String fileName,
			byte[] optionalHash, String savePath,
			int initialState, boolean persistent, boolean for_seeding, 
			DownloadManagerInitialisationAdapter adapter );

	/**
	 * Removes a DownloadManager from the global list, providing it can be
	 * removed (see {@link #canDownloadManagerBeRemoved(DownloadManager)})
	 * <p>
	 * The DownloadManager will not be stopped if it is running.  Scraping,
	 * however, will be turned off.
	 * 
	 * @param dm DownloadManager to remove
	 * 
	 * @throws GlobalManagerDownloadRemovalVetoException
	 */
	public void removeDownloadManager(DownloadManager dm)
			throws GlobalManagerDownloadRemovalVetoException;

	/**
	 * Determines whether a DownloadManager can be removed
	 * 
	 * @param dm DownloadManager to check
	 * @throws GlobalManagerDownloadRemovalVetoException
	 */
	public void canDownloadManagerBeRemoved(DownloadManager dm,
			boolean remove_torrent, boolean remove_data)
			throws GlobalManagerDownloadRemovalVetoException;

	/**
	 * Retrieve a list of {@link DownloadManager}s that GlobalManager is handling
	 * @return a list of {@link DownloadManager}s
	 */
	public List<DownloadManager> getDownloadManagers();

	/**
	 * Retrieve the DownloadManager associated with a TOTorrent object
	 * 
	 * @param torrent Torrent to search for
	 * @return The DownloadManager associted with the TOTOrrent, or null if
	 *          none found
	 */
	public DownloadManager getDownloadManager(TOTorrent torrent);

	/**
	 * Retrieve the DownloadManager associated with a hash
	 * 
	 * @param hash Hash to search for
	 * @return The DownloadManager associted with the hash, or null if
	 *          none found
	 */
	public DownloadManager getDownloadManager(HashWrapper hash);

	/**
	 * Retrieve the Tracker Scraper management class
	 * 
	 * @return Tracker Scraper management class
	 */
	public TRTrackerScraper getTrackerScraper();

	/**
	 * Retrieve the Global Manager Statistics class
	 * 
	 * @return the Global Manager Statistics class
	 */
	public GlobalManagerStats getStats();

	/**
	 * Puts GlobalManager in a stopped state.<br>
	 * Used when closing down Azureus.
	 */
	public void stopGlobalManager();

	/**
	 * Stops all downloads without removing them
	 *
	 * @author Rene Leonhardt
	 */
	public void stopAllDownloads();

	/**
	 * Starts all downloads
	 */
	public void startAllDownloads();

	/**
	 * Pauses (stops) all running downloads/seedings.
	 */
	public void pauseDownloads();

	/**
	 * pause any non-paused downloads and auto-resume all downloads after n seconds
	 * @param seconds
	 */
	public void pauseDownloadsForPeriod( int seconds );
	
	/**
	 * seconds remaining, 0 if not active
	 * @return
	 */
	
	public int getPauseDownloadPeriodRemaining();
	
	/**
	 * Indicates whether or not there are any downloads that can be paused.
	 * @return true if there is at least one download to pause, false if none
	 */
	public boolean canPauseDownloads();

	/**
	 * Resumes (starts) all downloads paused by the previous pauseDownloads call.
	 */
	public void resumeDownloads();

	/**
	 * Attempt to automatically resume downloads - request may be denied if manual override in effect
	 * @param is_auto_resume
	 * @return whether operation was accepted
	 */
	
	public boolean resumeDownloads( boolean is_auto_resume );

	/**
	 * Indicates whether or not there are any paused downloads to resume.
	 * @return true if there is at least one download to resume, false if none.
	 */
	public boolean canResumeDownloads();

	/**
	 * This reports that a download is being resumed in order to remove it from the paused set
	 * Don't use this to actually resume a download, use resumeDownload !
	 * @param dm
	 * @return
	 */
	public boolean resumingDownload(DownloadManager dm);

	/**
	 * Pause one DownloadManager
	 * @param dm DownloadManager to pause
	 * @return False if DownloadManager was invalid, stopped, or pause failed
	 */
	public boolean pauseDownload(DownloadManager dm);

	/**
	 * Resume a previously paused DownloadManager
	 * @param dm DownloadManager to resume
	 */
	public void resumeDownload(DownloadManager dm);

	public void 
	clearNonPersistentDownloadState(
		byte[] hash );
	
	/**
	 * Retrieve whether a DownloadManager is in a paused state
	 * 
	 * @param dm DownloadManager to query
	 * @return the pause state
	 */
	public boolean isPaused(DownloadManager dm);

	/**
	 * Determines whether we are only seeding, and not currently downloading
	 * anything.
	 * 
	 * @return  Seeding Only State
	 */
	
	public boolean isSeedingOnly();

	/**
	 * As for isSeedingOnly but includes queued seeds
	 * @return
	 */
	
	public boolean isPotentiallySeedingOnly();

	/**
	 * Retrieve the index of a DownloadManager within the GlobalManager
	 * list retrieved via {@link #getDownloadManagers()}.
	 * <P>
	 * This is NOT the DownloadManager's position
	 * 
	 * @param dm  DownloadManger to find the index of
	 * @return index, -1 if not in list
	 * 
	 * @deprecated Should not be used, as indexes may be different than
	 *               when getDownloadManagers() was called.
	 */
	public int getIndexOf(DownloadManager dm);
	
	/**
	 * Retrieve the number of download managers the global manager is managing.
	 * 
	 * @param bCompleted True: Return count of completed downloads<br>
	 *                    False: Return count of incomplete downloads
	 * @return count
	 */
	public int downloadManagerCount(boolean bCompleted);

	/**
	 * Retrieve whether a DownloadManager can move down in the GlobalManager list
	 * @param dm DownloadManager to check
	 * @return True - Can move down
	 */
	public boolean isMoveableDown(DownloadManager dm);

	/**
	 * Retrieve whether a DownloadManager can move up in the GlobalManager list
	 * @param dm DownloadManager to check
	 * @return True - Can move up
	 */
	public boolean isMoveableUp(DownloadManager dm);

	/**
	 * Move a list of DownloadManagers to the top of the GlobalManager list
	 *  
	 * @param dm array list of DownloadManager objects to move
	 */
	public void moveTop(DownloadManager[] dm);

	/**
	 * Move one DownloadManager up in the GlobalManager's list
	 * @param dm DownloadManager to move up
	 */
	public void moveUp(DownloadManager dm);

	/**
	 * Move one DownloadManager down in the GlobalManager's list
	 * @param dm DownloadManager to move down
	 */
	public void moveDown(DownloadManager dm);

	/**
	 * Move a list of DownloadManagers to the end of the GlobalManager list
	 *  
	 * @param dm array list of DownloadManager objects to move
	 */
	public void moveEnd(DownloadManager[] dm);

	/**
	 * Move a Downloadmanager to a new position.
	 * 
	 * @param manager DownloadManager to move
	 * @param newPosition position to place
	 */
	public void moveTo(DownloadManager manager, int newPosition);

	/** 
	 * Verifies the positions of the DownloadManagers, 
	 * filling in gaps and shifting duplicate IDs down if necessary.
	 * <p>
	 * This does not need to be called after MoveXXX, addDownloadManager, or
	 * removeDownloadManager functions.
	 */
	public void fixUpDownloadManagerPositions();

	/**
	 * Add a Global Manager listener
	 * @param l Listener to add
	 */
	public void addListener(GlobalManagerListener l);

	/**
	 * Removes a Global Manager listener
	 * @param l Listener to remove
	 */
	public void removeListener(GlobalManagerListener l);

	/**
	 * Add a listener triggered when Download is about to be removed
	 * @param l Listener to add
	 */
	public void addDownloadWillBeRemovedListener(
			GlobalManagerDownloadWillBeRemovedListener l);

	/**
	 * Remove a listener triggered when Download is about to be removed
	 * @param l Listener to remove
	 */
	public void removeDownloadWillBeRemovedListener(
			GlobalManagerDownloadWillBeRemovedListener l);

	/**
	 * See plugin ConnectionManager.NAT_ constants for return values
	 * @return ConnectionManager.NAT_*
	 */
	public int getNATStatus();
	
		/**
		 * Any adapters added will get a chance to see/set the initial state of downloads as they are
		 * added
		 * @param adapter
		 */
	
	public void
	addDownloadManagerInitialisationAdapter(
		DownloadManagerInitialisationAdapter	adapter );
	
	public void
	removeDownloadManagerInitialisationAdapter(
		DownloadManagerInitialisationAdapter	adapter );

	public void
	addEventListener(
		GlobalManagerEventListener 		listener );
	
	public void
	removeEventListener(
		GlobalManagerEventListener 		listener );

	public void 
	fireGlobalManagerEvent(
		int					type, 
		DownloadManager 	param );
	
	/**
	 * @param listener
	 */
	public void loadExistingTorrentsNow(boolean async);

	/**
	 * @param listener
	 * @param trigger
	 */
	void addListener(GlobalManagerListener listener, boolean trigger);

	/**
	 * @param manager
	 * @param remove_torrent
	 * @param remove_data
	 * @throws GlobalManagerDownloadRemovalVetoException
	 *
	 * @since 3.0.1.7
	 */
	void removeDownloadManager(DownloadManager manager, boolean remove_torrent,
			boolean remove_data) throws GlobalManagerDownloadRemovalVetoException;
	
	/**
	 * Calling this method doesn't <i>prepare</i> Azureus to be DHT-ready, it is
	 * only used to store, or remove, a DHT provider - so that it can be globally
	 * accessible. See the DHT manager classes in the plugin API for a better way
	 * to register a provider.
	 * 
	 * @since 3.0.4.3
	 */
	void setMainlineDHTProvider(MainlineDHTProvider provider);

	/**
	 * @since 3.0.4.3
	 */
	MainlineDHTProvider getMainlineDHTProvider();

	public void 
	statsRequest(
		Map 		request,
		Map			reply );

	/**
	 * @param manager
	 * @return
	 *
	 * @since 4.0.0.5
	 */
	boolean contains(DownloadManager manager);
	
	public void
	saveState();
	
	public Map
	exportDownloadStateToMap(
		DownloadManager		dm );
	
	public DownloadManager
	importDownloadStateFromMap(
		Map		map );
}