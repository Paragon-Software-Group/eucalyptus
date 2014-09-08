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
package com.eucalyptus.reporting.service;

import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import com.eucalyptus.util.EucalyptusCloudException;

/**
 *
 */
public class ReportingException extends EucalyptusCloudException {
  public static final String INTERNAL_SERVER_ERROR = "InternalError";
  public static final String NOT_AUTHORIZED = "NotAuthorized";
  public static final String BAD_REQUEST = "BadRequest";
  private static final long serialVersionUID = 1L;

  private final HttpResponseStatus status;
  private final String error;

  public ReportingException( final HttpResponseStatus status,
                             final String error,
                             final String message ) {
    super( message );
    this.status = status;
    this.error = error;
  }

  public HttpResponseStatus getStatus() {
    return status;
  }

  public String getError() {
    return error;
  }
}
