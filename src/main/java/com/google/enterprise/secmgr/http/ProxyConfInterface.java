// Copyright 2013 Google Inc.
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

package com.google.enterprise.secmgr.http;

/**
 * A proxy configuration interface.
 */
public interface ProxyConfInterface {
  /**
   * Gets the proxy for the url.
   *
   * @param urlString The url
   * @return The proxy, or null if there is no match.
   */
  public String getProxy(String urlString);
}
