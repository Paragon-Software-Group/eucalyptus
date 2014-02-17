package com.eucalyptus.cloudformation.resources;

/**
 * Created by ethomas on 2/14/14.
 */
public abstract class ResourceAction {
  public abstract ResourceProperties getResourceProperties();
  public abstract void setResourceProperties(ResourceProperties resourceProperties);
  public abstract ResourceInfo getResourceInfo();
  public abstract void setResourceInfo(ResourceInfo resourceInfo);
  public abstract void create() throws Exception;
  public abstract void delete() throws Exception;
  public abstract void rollback() throws Exception;
}
