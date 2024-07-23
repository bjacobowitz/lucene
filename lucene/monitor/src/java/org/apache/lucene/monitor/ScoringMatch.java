/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.lucene.monitor;

import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.Similarity;

/** A QueryMatch that reports scores for each match */
public class ScoringMatch extends QueryMatch {

  public static final MatcherFactory<ScoringMatch> matchWithSimilarity(Similarity similarity) {
    return searcher -> {
      searcher.setSimilarity(similarity);
      return new ScoringCandidateMatcher(searcher);
    };
  }

  public static final MatcherFactory<ScoringMatch> DEFAULT_MATCHER =
      matchWithSimilarity(new BM25Similarity());

  private final float score;

  protected ScoringMatch(String queryId, float score) {
    super(queryId);
    this.score = score;
  }

  public float getScore() {
    return score;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ScoringMatch)) return false;
    if (!super.equals(o)) return false;
    ScoringMatch that = (ScoringMatch) o;
    return Float.compare(that.score, score) == 0;
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + (score != +0.0f ? Float.floatToIntBits(score) : 0);
    return result;
  }
}
