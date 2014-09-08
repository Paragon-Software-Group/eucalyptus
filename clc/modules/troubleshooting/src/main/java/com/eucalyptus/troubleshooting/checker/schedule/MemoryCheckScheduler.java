/*************************************************************************
 * Copyright 2009-2012 Eucalyptus Systems, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 *
 * Please contact Eucalyptus Systems, Inc., 6755 Hollister Ave., Goleta
 * CA 93117, USA or visit http://www.eucalyptus.com/licenses/ if you need
 * additional information or have any questions.
 ************************************************************************/

package com.eucalyptus.troubleshooting.checker.schedule;

import java.util.concurrent.ScheduledFuture;

import com.eucalyptus.bootstrap.TroubleshootingBootstrapper;
import com.eucalyptus.component.id.Eucalyptus;
import com.eucalyptus.troubleshooting.checker.MemoryCheck;
import com.eucalyptus.troubleshooting.checker.MemoryCheck.GarbageCollectionChecker;

public class MemoryCheckScheduler {

	private static ScheduledFuture<?> memoryCheckerScheduledFuture = null;
	public static void memoryCheck() {
		String ratioStr = TroubleshootingBootstrapper.MEMORY_CHECK_RATIO; 
		String pollTime = TroubleshootingBootstrapper.MEMORY_CHECK_POLL_TIME;
		if (memoryCheckerScheduledFuture != null) {
			memoryCheckerScheduledFuture.cancel(true);
		}
		GarbageCollectionChecker checker = new GarbageCollectionChecker(Double.parseDouble(ratioStr), Eucalyptus.class, Long.parseLong(pollTime));
		memoryCheckerScheduledFuture = MemoryCheck.start(checker);
	}

}
