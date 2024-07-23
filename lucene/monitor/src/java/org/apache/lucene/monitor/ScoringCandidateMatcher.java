package org.apache.lucene.monitor;

import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Scorable;
import org.apache.lucene.search.ScoreMode;

import java.io.IOException;

public class ScoringCandidateMatcher extends CollectingMatcher<ScoringMatch> {
    public ScoringCandidateMatcher(IndexSearcher searcher) {
        super(searcher, ScoreMode.COMPLETE);
    }

    @Override
    protected ScoringMatch doMatch(String queryId, int doc, Scorable scorer)
            throws IOException {
        float score = scorer.score();
        if (score > 0) return new ScoringMatch(queryId, score);
        return null;
    }

    @Override
    public ScoringMatch resolve(ScoringMatch match1, ScoringMatch match2) {
        return new ScoringMatch(match1.getQueryId(), match1.getScore() + match2.getScore());
    }
}
