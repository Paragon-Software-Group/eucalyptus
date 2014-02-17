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
package com.eucalyptus.cloudformation.resources.standard.actions;


import com.eucalyptus.cloudformation.resources.ResourceAction;
import com.eucalyptus.cloudformation.resources.ResourceInfo;
import com.eucalyptus.cloudformation.resources.ResourceProperties;
import com.eucalyptus.cloudformation.resources.standard.info.AWSSNSTopicResourceInfo;
import com.eucalyptus.cloudformation.resources.standard.propertytypes.AWSSNSTopicProperties;

/**
 * Created by ethomas on 2/3/14.
 */
public class AWSSNSTopicResourceAction extends ResourceAction {

  private AWSSNSTopicProperties properties = new AWSSNSTopicProperties();
  private AWSSNSTopicResourceInfo info = new AWSSNSTopicResourceInfo();
  @Override
  public ResourceProperties getResourceProperties() {
    return properties;
  }

  @Override
  public void setResourceProperties(ResourceProperties resourceProperties) {
    properties = (AWSSNSTopicProperties) resourceProperties;
  }

  @Override
  public ResourceInfo getResourceInfo() {
    return info;
  }

  @Override
  public void setResourceInfo(ResourceInfo resourceInfo) {
    info = (AWSSNSTopicResourceInfo) resourceInfo;
  }

  @Override
  public void create() throws Exception {
    throw new UnsupportedOperationException();
  }


  @Override
  public void delete() throws Exception {
    throw new UnsupportedOperationException();
  }

  @Override
  public void rollback() throws Exception {
    throw new UnsupportedOperationException();
  }

}


