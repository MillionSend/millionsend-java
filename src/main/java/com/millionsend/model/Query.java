package com.millionsend.model;

import java.util.List;
import java.util.Map;

/** Wire query helpers for the option classes: unset params are dropped, lists join with commas. */
final class Query {

  private Query() {}

  static void put(Map<String, String> q, String key, Object value) {
    if (value != null) {
      q.put(key, String.valueOf(value));
    }
  }

  static void csv(Map<String, String> q, String key, List<String> values) {
    if (values != null && !values.isEmpty()) {
      q.put(key, String.join(",", values));
    }
  }
}
