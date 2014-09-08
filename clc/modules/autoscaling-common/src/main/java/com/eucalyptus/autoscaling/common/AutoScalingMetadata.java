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
package com.eucalyptus.autoscaling.common;

import com.eucalyptus.auth.policy.PolicyResourceType;
import com.eucalyptus.auth.policy.PolicySpec;
import com.eucalyptus.component.annotation.PolicyVendor;
import com.eucalyptus.util.RestrictedType;

/**
 *
 */
@PolicyVendor( PolicySpec.VENDOR_AUTOSCALING )
public interface AutoScalingMetadata extends RestrictedType {

  public interface AutoScalingMetadataWithResourceName extends AutoScalingMetadata {
    String getArn();
  } 
  
  @PolicyResourceType( "launchconfiguration" )
  public interface LaunchConfigurationMetadata extends AutoScalingMetadataWithResourceName {}

  @PolicyResourceType( "autoscalingroup" )
  public interface AutoScalingGroupMetadata extends AutoScalingMetadataWithResourceName {}

  @PolicyResourceType( "terminationpolicytype" )
  public interface TerminationPolicyTypeMetadata extends AutoScalingMetadata {}

  @PolicyResourceType( "adjustmenttype" )
  public interface AdjustmentTypeMetadata extends AutoScalingMetadata {}

  @PolicyResourceType( "scalingpolicy" )
  public interface ScalingPolicyMetadata extends AutoScalingMetadataWithResourceName {}

  @PolicyResourceType( "instance" )
  public interface AutoScalingInstanceMetadata extends AutoScalingMetadata {}

  @PolicyResourceType( "tag" )
  public interface AutoScalingTagMetadata extends AutoScalingMetadata {}

  @PolicyResourceType( "scalingprocesstype" )
  public interface ScalingProcessTypeMetadata extends AutoScalingMetadata {}

  @PolicyResourceType( "scalingactivity" )
  public interface ScalingActivityMetadata extends AutoScalingMetadata {}

  @PolicyResourceType( "metriccollectiontype" )
  public interface MetricCollectionTypeMetadata extends AutoScalingMetadata {}

}
