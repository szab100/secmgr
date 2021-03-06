// Copyright 2009 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.enterprise.secmgr.config;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.enterprise.secmgr.json.ProxyTypeAdapter;
import com.google.gson.GsonBuilder;

import java.util.List;
import java.util.Objects;

import javax.annotation.concurrent.Immutable;

/**
 * The configuration of a SAML authentication mechanism.
 */
@Immutable
public final class AuthnMechSaml extends AuthnMechanism {

  public static final String TYPE_NAME = "SAML";
  private static final long DEFAULT_TRUST_DURATION = 30 * 60 * 1000;  // 30 mins

  private final String entityId;
  private final int timeout;
  private final long trustDuration;

  public static AuthnMechSaml make(String name, String entityId, int timeout, long trustDuration) {
    return new AuthnMechSaml(name, entityId, timeout, trustDuration);
  }

  // Presumably the two constructors below are for backwards compatibility
  public static AuthnMechSaml make(String name, String entityId, int timeout) {
    return new AuthnMechSaml(name, entityId, timeout, getDefaultTrustDuration());
  }

  public static AuthnMechSaml make(String name, String entityId) {
    return new AuthnMechSaml(name, entityId, NO_TIME_LIMIT, DEFAULT_TRUST_DURATION);
  }

  private AuthnMechSaml(String name, String entityId, int timeout, long trustDuration) {
    super(name);
    this.entityId = checkString(entityId);
    this.timeout = checkTimeout(timeout);
    this.trustDuration = checkTrustDuration(trustDuration);
  }

  public static AuthnMechSaml makeEmpty() {
    return new AuthnMechSaml();
  }

  private AuthnMechSaml() {
    super();
    this.entityId = null;
    this.timeout = NO_TIME_LIMIT;
    this.trustDuration = getDefaultTrustDuration();
  }

  @Override
  public String getTypeName() {
    return TYPE_NAME;
  }

  /**
   * Get the default trust-duration value.
   */
  public static long getDefaultTrustDuration() {
    return DEFAULT_TRUST_DURATION;
  }

  @Override
  public List<CredentialTransform> getCredentialTransforms() {
    return ImmutableList.of(
        CredentialTransform.make(CredentialTypeSet.NONE, CredentialTypeSet.VERIFIED_PRINCIPAL));
  }

  @Override
  public AuthnMechanism copyWithNewName(String name) {
    return make(name, getEntityId(), getTimeout(), getTrustDuration());
  }

  @Override
  public boolean equals(Object object) {
    if (object == this) { return true; }
    if (!(object instanceof AuthnMechSaml)) { return false; }
    AuthnMechSaml mech = (AuthnMechSaml) object;
    return super.equals(mech)
        && Objects.equals(entityId, mech.getEntityId())
        && Objects.equals(getTimeout(), mech.getTimeout());
  }

  @Override
  public int hashCode() {
    return super.hashCode(entityId, timeout);
  }

  /**
   * Get the SAML entity ID for this authority.  This ID is used as a key when
   * looking up SAML metadata that describes the authority.
   *
   * @return The SAML entity ID as a string, never null or empty, normally a URI.
   */
  public String getEntityId() {
    return entityId;
  }

  @Override
  public int getTimeout() {
    return timeout;
  }

  @Override
  public long getTrustDuration() {
    return trustDuration;
  }

  public static final Predicate<AuthnMechanism> SAML_PREDICATE = new Predicate<AuthnMechanism>() {
    public boolean apply(AuthnMechanism config) {
      return (config instanceof AuthnMechSaml);
    }
  };

  static void registerTypeAdapters(GsonBuilder builder) {
    builder.registerTypeAdapter(AuthnMechSaml.class,
        ProxyTypeAdapter.make(AuthnMechSaml.class, LocalProxy.class));
  }

  private static final class LocalProxy extends MechanismProxy<AuthnMechSaml> {
    String entityId;
    int timeout = NO_TIME_LIMIT;
    long trustDuration = DEFAULT_TRUST_DURATION;

    @SuppressWarnings("unused")
    LocalProxy() {
    }

    @SuppressWarnings("unused")
    LocalProxy(AuthnMechSaml mechanism) {
      super(mechanism);
      entityId = mechanism.getEntityId();
      timeout = mechanism.getTimeout();
      trustDuration = mechanism.getTrustDuration();
    }

    @Override
    public AuthnMechSaml build() {
      return make(name, entityId, timeout, trustDuration);
    }
  }
}
