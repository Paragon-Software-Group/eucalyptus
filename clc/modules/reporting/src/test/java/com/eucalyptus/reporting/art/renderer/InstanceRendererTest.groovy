/*************************************************************************
 * Copyright 2009-2013 Eucalyptus Systems, Inc.
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
package com.eucalyptus.reporting.art.renderer

import org.junit.Test
import com.eucalyptus.reporting.art.entity.ReportArtEntity
import com.eucalyptus.reporting.art.entity.AccountArtEntity
import com.eucalyptus.reporting.art.entity.UserArtEntity
import com.eucalyptus.reporting.ReportType
import com.eucalyptus.reporting.ReportFormat
import com.eucalyptus.reporting.art.entity.InstanceArtEntity
import com.eucalyptus.reporting.art.entity.AvailabilityZoneArtEntity

/**
 * 
 */
class InstanceRendererTest extends RendererTestSupport {

  @Test
  void testCsvLayout() {
    assertCsvColumns( render( ReportType.INSTANCE, ReportFormat.CSV, art() ) )
  }

  @Test
  void testHtmlLayout() {
    assertHtmlColumns( render( ReportType.INSTANCE, ReportFormat.HTML, art() ) )
  }

  private ReportArtEntity art() {
    ReportArtEntity art = new ReportArtEntity( millis("2012-10-01T00:00:00"), millis("2012-10-10T00:00:00") )
    AccountArtEntity account1Art = new AccountArtEntity()
    AccountArtEntity account2Art = new AccountArtEntity()
    AvailabilityZoneArtEntity zoneArt = new AvailabilityZoneArtEntity()
    art.getZones().put("Zone1", zoneArt )
    zoneArt.getAccounts().put("a1", account1Art)
    zoneArt.getAccounts().put("a2", account2Art)
    UserArtEntity acc1user1Art = new UserArtEntity();
    UserArtEntity acc1user2Art = new UserArtEntity();
    UserArtEntity acc2user1Art = new UserArtEntity();
    account1Art.getUsers().put( "u1", acc1user1Art )
    account1Art.getUsers().put( "u2", acc1user2Art )
    account2Art.getUsers().put( "u1", acc2user1Art )
    acc1user1Art.getInstances().put( "i-0000001", new InstanceArtEntity("m1.small", "i-0000001") )
    acc1user1Art.getInstances().put( "i-0000002", new InstanceArtEntity("m1.small", "i-0000002") )
    acc1user2Art.getInstances().put( "i-0000003", new InstanceArtEntity("m1.small", "i-0000003") )
    acc2user1Art.getInstances().put( "i-0000004", new InstanceArtEntity("c1.medium", "i-0000004") )
    art
  }
}
