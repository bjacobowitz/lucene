package org.apache.lucene.monitor;

import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Scorable;
import org.apache.lucene.search.ScoreMode;

public class SimpleCandidateMatcher extends CollectingMatcher<QueryMatch> {
    public SimpleCandidateMatcher(IndexSearcher searcher) {
        super(searcher, ScoreMode.COMPLETE_NO_SCORES);
    }

    @Override
    public QueryMatch resolve(QueryMatch match1, QueryMatch match2) {
        return match1;
    }

    @Override
    protected QueryMatch doMatch(String queryId, int doc, Scorable scorer) {
        return new QueryMatch(queryId);
    }
}
