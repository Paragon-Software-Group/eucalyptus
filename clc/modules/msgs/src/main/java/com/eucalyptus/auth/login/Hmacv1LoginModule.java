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

package com.eucalyptus.auth.login;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.net.URLCodec;
import org.apache.log4j.Logger;
import org.apache.xml.security.utils.Base64;
import com.eucalyptus.auth.principal.AccessKey;
import com.eucalyptus.auth.principal.User;
import com.eucalyptus.crypto.Hmac;
import com.eucalyptus.crypto.util.SecurityParameter;
import com.google.common.base.Strings;
import com.google.common.collect.Ordering;

public class Hmacv1LoginModule extends HmacLoginModuleSupport {
  private static Logger LOG = Logger.getLogger( Hmacv1LoginModule.class );

  public Hmacv1LoginModule() {
    super(1);
  }

  @Override
  public boolean authenticate( HmacCredentials credentials ) throws Exception {
    String sig = credentials.getSignature( );
    checkForReplay( sig );
    AccessKey accessKey = lookupAccessKey(credentials);
    User user = accessKey.getUser( );
    String secretKey = accessKey.getSecretKey( );

    String canonicalString = this.makeSubjectString( credentials.getParameters( ) );
    String computedSig = this.getSignature( secretKey, canonicalString, credentials.getSignatureMethod( ) );
    String decodedSig = sanitize( urldecode( sig ) );
    if ( !computedSig.equals( sanitize(sig) ) && !computedSig.equals( decodedSig ) && !computedSig.equals( sig ) ) {
      return false;
    }
    super.setCredential( credentials.getQueryIdCredential( ) );
    super.setPrincipal( user );
    //super.getGroups( ).addAll( Groups.lookupUserGroups( super.getPrincipal( ) ) );
    return true;
  }

  private String makeSubjectString( final Map<String, List<String>> parameters ) throws UnsupportedEncodingException {
    String paramString = "";
    Set<String> sortedKeys = new TreeSet<String>( String.CASE_INSENSITIVE_ORDER );
    sortedKeys.addAll( parameters.keySet( ) );
    sortedKeys.remove( SecurityParameter.Signature.parameter() );
    for ( final String key : sortedKeys ) {
      if ( parameters.get(key).isEmpty() ) {
        paramString = paramString.concat( key ).replaceAll( "\\+", " " );
      } else for ( final String value : Ordering.natural().sortedCopy(parameters.get(key)) ) {
        paramString = paramString.concat( key ).concat( Strings.nullToEmpty( value ).replaceAll("\\+", " ") );
      }
    }
    try {
      return new String(URLCodec.decodeUrl( paramString.getBytes() ) );
    } catch ( DecoderException e ) {
      return paramString;
    }
  }

  public String getSignature( final String queryKey, final String subject, final Hmac mac ) throws AuthenticationException {
    SecretKeySpec signingKey = new SecretKeySpec( queryKey.getBytes( ), mac.toString( ) );
    try {
      Mac digest = mac.getInstance( );
      digest.init( signingKey );
      byte[] rawHmac = digest.doFinal( subject.getBytes( ) );
      return sanitize( Base64.encode( rawHmac ) );
    } catch ( Exception e ) {
      LOG.error( e, e );
      throw new AuthenticationException( "Failed to compute signature" );
    }
  }

}
