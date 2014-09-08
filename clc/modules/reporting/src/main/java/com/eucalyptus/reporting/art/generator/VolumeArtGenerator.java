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
 *
 * This file may incorporate work covered under the following copyright
 * and permission notice:
 *
 *   Software License Agreement (BSD License)
 *
 *   Copyright (c) 2008, Regents of the University of California
 *   All rights reserved.
 *
 *   Redistribution and use of this software in source and binary forms,
 *   with or without modification, are permitted provided that the
 *   following conditions are met:
 *
 *     Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *     Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer
 *     in the documentation and/or other materials provided with the
 *     distribution.
 *
 *   THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 *   "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 *   LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 *   FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 *   COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 *   INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 *   BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *   LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 *   CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 *   LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 *   ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 *   POSSIBILITY OF SUCH DAMAGE. USERS OF THIS SOFTWARE ACKNOWLEDGE
 *   THE POSSIBLE PRESENCE OF OTHER OPEN SOURCE LICENSED MATERIAL,
 *   COPYRIGHTED MATERIAL OR PATENTED MATERIAL IN THIS SOFTWARE,
 *   AND IF ANY SUCH MATERIAL IS DISCOVERED THE PARTY DISCOVERING
 *   IT MAY INFORM DR. RICH WOLSKI AT THE UNIVERSITY OF CALIFORNIA,
 *   SANTA BARBARA WHO WILL THEN ASCERTAIN THE MOST APPROPRIATE REMEDY,
 *   WHICH IN THE REGENTS' DISCRETION MAY INCLUDE, WITHOUT LIMITATION,
 *   REPLACEMENT OF THE CODE SO IDENTIFIED, LICENSING OF THE CODE SO
 *   IDENTIFIED, OR WITHDRAWAL OF THE CODE CAPABILITY TO THE EXTENT
 *   NEEDED TO COMPLY WITH ANY SUCH LICENSES OR RIGHTS.
 ************************************************************************/
package com.eucalyptus.reporting.art.generator;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.eucalyptus.reporting.art.entity.AccountArtEntity;
import com.eucalyptus.reporting.art.entity.AvailabilityZoneArtEntity;
import com.eucalyptus.reporting.art.entity.InstanceArtEntity;
import com.eucalyptus.reporting.art.entity.VolumeArtEntity;
import com.eucalyptus.reporting.art.entity.ReportArtEntity;
import com.eucalyptus.reporting.art.entity.UserArtEntity;
import com.eucalyptus.reporting.art.entity.VolumeUsageArtEntity;
import com.eucalyptus.reporting.art.util.AttachDurationCalculator;
import com.eucalyptus.reporting.art.util.DurationCalculator;
import com.eucalyptus.reporting.art.util.StartEndTimes;
import com.eucalyptus.reporting.domain.ReportingUser;
import com.eucalyptus.reporting.event_store.*;
import com.google.common.base.Predicate;
import com.google.common.collect.Maps;

public class VolumeArtGenerator extends AbstractArtGenerator
{
	private static Logger log = Logger.getLogger( VolumeArtGenerator.class );

	@Override
	public ReportArtEntity generateReportArt( final ReportArtEntity report )
	{
		log.debug("Generating report ART");

		// cache for user/account info
		final Map<String,ReportingUser> reportingUsersById = Maps.newHashMap();
		final Map<String,String> accountNamesById = Maps.newHashMap();

		/* Create super-tree of availZones, accounts, users, and volumes;
		 * and create a Map of the volume nodes at the bottom with start times.
		 */
		final Map<String,VolumeArtEntity> volumeEntities = Maps.newHashMap();
		final Map<String,StartEndTimes> volStartEndTimes = Maps.newHashMap();
		foreachReportingVolumeCreateEvent( report.getEndMs(), new Predicate<ReportingVolumeCreateEvent>() {
			@Override
			public boolean apply( final ReportingVolumeCreateEvent createEvent ) {
				if (! report.getZones().containsKey(createEvent.getAvailabilityZone())) {
					report.getZones().put(createEvent.getAvailabilityZone(), new AvailabilityZoneArtEntity());
				}
				final AvailabilityZoneArtEntity zone = report.getZones().get(createEvent.getAvailabilityZone());

				final ReportingUser reportingUser = getUserById( reportingUsersById, createEvent.getUserId() );
				if (reportingUser==null) {
					log.error("No user corresponding to event:" + createEvent.getUserId());
					return true;
				}
				final String accountName = getAccountNameById( accountNamesById, reportingUser.getAccountId() );
				if (accountName==null) {
					log.error("No account corresponding to user:" + reportingUser.getAccountId());
					return true;
				}
				if (! zone.getAccounts().containsKey(accountName)) {
					zone.getAccounts().put(accountName, new AccountArtEntity());
				}
				AccountArtEntity account = zone.getAccounts().get(accountName);
				if (! account.getUsers().containsKey(reportingUser.getName())) {
					account.getUsers().put(reportingUser.getName(), new UserArtEntity());
				}
				UserArtEntity user = account.getUsers().get(reportingUser.getName());
				VolumeArtEntity volume = new VolumeArtEntity(createEvent.getVolumeId());
				volume.getUsage().setSizeGB( createEvent.getSizeGB() );
				long startTime = createEvent.getTimestampMs();
				if (startTime <= report.getEndMs()) {
					user.getVolumes().put(createEvent.getUuid(), volume);
					volStartEndTimes.put(createEvent.getUuid(), new StartEndTimes( startTime, report.getEndMs() ));
					volumeEntities.put(createEvent.getUuid(), volume);
					volume.getUsage().setVolumeCnt(1);
				}
				return true;
			}
		});

		/* Find end times for the volumes
		 */
		foreachReportingVolumeDeleteEvent( report.getEndMs(), new Predicate<ReportingVolumeDeleteEvent>() {
			@Override
			public boolean apply( final ReportingVolumeDeleteEvent deleteEvent ) {
				long endTime = deleteEvent.getTimestampMs();
				if (endTime >= report.getBeginMs() && volStartEndTimes.containsKey(deleteEvent.getUuid())) {
					StartEndTimes startEndTimes = volStartEndTimes.get(deleteEvent.getUuid());
					startEndTimes.setEndTime(endTime);
				} else {
					volumeEntities.remove(deleteEvent.getUuid());
					volStartEndTimes.remove(deleteEvent.getUuid());
				}
				return true;
			}
		} );

		/* Set the duration of each volume
		 */
		for (String uuid: volumeEntities.keySet()) {
			VolumeArtEntity volume = volumeEntities.get(uuid);
			StartEndTimes startEndTimes = volStartEndTimes.get(uuid);
			if (uuid == null) {
				log.error("volume without corresponding start end times:" + uuid);
				continue;
			}
			long duration = DurationCalculator.boundDuration(report.getBeginMs(), report.getEndMs(),
					startEndTimes.getStartTime(), startEndTimes.getEndTime())/1000;
			volume.getUsage().setGBSecs(duration*volume.getUsage().getSizeGB());
		}
		

		/* Scan instance entities so we can get the instance id from the uuid
		 */
		final Map<String,InstanceArtEntity> instanceEntities = new HashMap<String,InstanceArtEntity>();
		foreachInstanceCreateEvent( report.getEndMs(), new Predicate<ReportingInstanceCreateEvent>() {
			@Override
			public boolean apply( final ReportingInstanceCreateEvent createEvent ) {
				InstanceArtEntity instance = new InstanceArtEntity(createEvent.getInstanceType(), createEvent.getInstanceId());
				instanceEntities.put(createEvent.getUuid(), instance);
				return true;
			}
		});

		/* Find attachment end times
		 */
		final AttachDurationCalculator<String,String> durationCalc = new AttachDurationCalculator<String,String>(report.getBeginMs(), report.getEndMs());
		foreachReportingVolumeDetachEvent( report.getEndMs(), new Predicate<ReportingVolumeDetachEvent>() {
			@Override
			public boolean apply( final ReportingVolumeDetachEvent detachEvent ) {
				durationCalc.detach( detachEvent.getInstanceUuid(), detachEvent.getVolumeUuid(),
						detachEvent.getTimestampMs() );
				return true;
			}
		} );

		/* Find attachment end times and set durations
		 */
		foreachReportingVolumeAttachEvent( report.getEndMs(), new Predicate<ReportingVolumeAttachEvent>() {
			@Override
			public boolean apply( final ReportingVolumeAttachEvent attachEvent ) {
				long durationMs = durationCalc.attach(attachEvent.getInstanceUuid(),
						attachEvent.getVolumeUuid(), attachEvent.getTimestampMs());
				if (durationMs==0) return true;
				if (! volumeEntities.containsKey(attachEvent.getVolumeUuid())) return true;
				VolumeArtEntity volume = volumeEntities.get(attachEvent.getVolumeUuid());
				long gbsecs = ((durationMs/1000) * volume.getUsage().getSizeGB());
				/* If a volume is repeatedly attached to and detached from an instance,
				 * add up the total attachment time.
				 */
				if (volume.getInstanceAttachments().containsKey(attachEvent.getInstanceUuid())) {
					gbsecs += volume.getInstanceAttachments().get(attachEvent.getInstanceUuid()).getGBSecs();
				}
				VolumeUsageArtEntity usage = new VolumeUsageArtEntity();
				usage.setGBSecs(gbsecs);
				usage.setSizeGB(volume.getUsage().getSizeGB());
				usage.setVolumeCnt(1);
				if (instanceEntities.keySet().contains(attachEvent.getInstanceUuid())) {
					String instanceId = instanceEntities.get(attachEvent.getInstanceUuid()).getInstanceId();
					volume.getInstanceAttachments().put(instanceId, usage);
				} else {
					log.error("instance uuid in attach events without corresponding instance:" + attachEvent.getInstanceUuid());
				}
				return true;
			}
		} );
		
		/* Perform totals and summations for user, account, and zone
		 */
		for (String zoneName : report.getZones().keySet()) {
			AvailabilityZoneArtEntity zone = report.getZones().get(zoneName);
			for (String accountName : zone.getAccounts().keySet()) {
				AccountArtEntity account = zone.getAccounts().get(accountName);
				for (String userName : account.getUsers().keySet()) {
					UserArtEntity user = account.getUsers().get(userName);
					for (String volumeUuid : user.getVolumes().keySet()) {
						VolumeArtEntity volume = user.getVolumes().get(volumeUuid);
						updateUsageTotals(user.getUsageTotals().getVolumeTotals(), volume.getUsage());
						updateUsageTotals(account.getUsageTotals().getVolumeTotals(), volume.getUsage());
						updateUsageTotals(zone.getUsageTotals().getVolumeTotals(), volume.getUsage());
					}
				}
			}
		}

		return report;
	}

	protected void foreachReportingVolumeCreateEvent( final long endExclusive, final Predicate<ReportingVolumeCreateEvent> callback ) {
		foreach( ReportingVolumeCreateEvent.class,
				before( endExclusive ),
				true,
				validateCreate( callback ) );
	}

	protected void foreachReportingVolumeDeleteEvent( final long endExclusive, final Predicate<ReportingVolumeDeleteEvent> callback ) {
		foreach( ReportingVolumeDeleteEvent.class,
				before( endExclusive ),
				true,
				validateDelete( callback ) );
	}

	protected void foreachReportingVolumeAttachEvent( final long endExclusive, final Predicate<ReportingVolumeAttachEvent> callback ) {
		foreach( ReportingVolumeAttachEvent.class,
				before( endExclusive ),
				true,
				validateAttach( callback ) );
	}

	protected void foreachReportingVolumeDetachEvent( final long endExclusive, final Predicate<ReportingVolumeDetachEvent> callback ) {
		foreach( ReportingVolumeDetachEvent.class,
				before( endExclusive ),
				true,
				validateDetach( callback ) );
	}

	protected void foreachInstanceCreateEvent( final long endExclusive, final Predicate<? super ReportingInstanceCreateEvent> callback ) {
		foreach( ReportingInstanceCreateEvent.class, before( endExclusive ), true, callback );
	}

	private Predicate<ReportingVolumeCreateEvent> validateCreate(
			final Predicate<? super ReportingVolumeCreateEvent> callback ) {
		return new Predicate<ReportingVolumeCreateEvent>(){
			@Override
			public boolean apply( final ReportingVolumeCreateEvent event ) {
				if ( event == null ||
						event.getAvailabilityZone() == null ||
						event.getVolumeId() == null ||
						event.getSizeGB() == null ||
						event.getUserId() == null ||
						event.getUuid() == null ||
						event.getTimestampMs() == null ) {
					log.debug("Ignoring invalid create event: " + event);
					return true;
				}
				return callback.apply( event );
			}
		};
	}

	private Predicate<ReportingVolumeDeleteEvent> validateDelete(
			final Predicate<? super ReportingVolumeDeleteEvent> callback ) {
		return new Predicate<ReportingVolumeDeleteEvent>(){
			@Override
			public boolean apply( final ReportingVolumeDeleteEvent event ) {
				if ( event == null ||
						event.getUuid() == null ||
						event.getTimestampMs() == null ) {
						log.debug("Ignoring invalid delete event: " + event);
					return true;
				}
				return callback.apply( event );
			}
		};
	}

	private Predicate<ReportingVolumeAttachEvent> validateAttach(
			final Predicate<? super ReportingVolumeAttachEvent> callback ) {
		return new Predicate<ReportingVolumeAttachEvent>(){
			@Override
			public boolean apply( final ReportingVolumeAttachEvent event ) {
				if ( event == null ||
						event.getVolumeUuid() == null ||
						event.getInstanceUuid() == null ||
						event.getSizeGB() == null ||
						event.getTimestampMs() == null ) {
					log.debug("Ignoring invalid attach event: " + event);
					return true;
				}
				return callback.apply( event );
			}
		};
	}

	private Predicate<ReportingVolumeDetachEvent> validateDetach(
			final Predicate<? super ReportingVolumeDetachEvent> callback ) {
		return new Predicate<ReportingVolumeDetachEvent>(){
			@Override
			public boolean apply( final ReportingVolumeDetachEvent event ) {
				if ( event == null ||
						event.getVolumeUuid() == null ||
						event.getInstanceUuid() == null ||
						event.getTimestampMs() == null ) {
					log.debug("Ignoring invalid detach event: " + event);
					return true;
				}
				return callback.apply( event );
			}
		};
	}

	private static void updateUsageTotals(VolumeUsageArtEntity totalEntity,
			VolumeUsageArtEntity newEntity)
	{
		
		totalEntity.setGBSecs(totalEntity.getGBSecs()+newEntity.getGBSecs());
		totalEntity.setVolumeCnt(totalEntity.getVolumeCnt()+1);
		totalEntity.setSizeGB(plus(totalEntity.getSizeGB(), newEntity.getSizeGB()));
	}

	/**
	 * Addition with the peculiar semantics for null we need here
	 */
	private static Long plus(Long added, Long defaultVal)
	{
		if (added==null) {
			return defaultVal;
		} else if (defaultVal==null) {
			return added;
		} else {
			return added + defaultVal;
		}
	}
}
