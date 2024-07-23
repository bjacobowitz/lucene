package org.apache.lucene.monitor;

import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Matches;
import org.apache.lucene.search.MatchesIterator;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreMode;
import org.apache.lucene.search.Weight;

import java.io.IOException;
import java.util.Map;

public class HighlightsMatchCandidateMatcher extends CandidateMatcher<HighlightsMatch> {

    public HighlightsMatchCandidateMatcher(IndexSearcher searcher) {
        super(searcher);
    }

    @Override
    protected void matchQuery(
            String queryId, Query matchQuery, Map<String, String> metadata) throws IOException {
        Weight w =
                searcher.createWeight(
                        searcher.rewrite(matchQuery), ScoreMode.COMPLETE_NO_SCORES, 1);
        for (LeafReaderContext ctx : searcher.getIndexReader().leaves()) {
            for (int i = 0; i < ctx.reader().maxDoc(); i++) {
                Matches matches = w.matches(ctx, i);
                if (matches != null) {
                    addMatch(buildMatch(matches, queryId), i);
                }
            }
        }
    }

    @Override
    public HighlightsMatch resolve(HighlightsMatch match1, HighlightsMatch match2) {
        return HighlightsMatch.merge(match1.getQueryId(), match1, match2);
    }

    private HighlightsMatch buildMatch(Matches matches, String queryId) throws IOException {
        HighlightsMatch m = new HighlightsMatch(queryId);
        for (String field : matches) {
            MatchesIterator mi = matches.getMatches(field);
            while (mi.next()) {
                MatchesIterator sub = mi.getSubMatches();
                if (sub != null) {
                    while (sub.next()) {
                        m.addHit(
                                field,
                                sub.startPosition(),
                                sub.endPosition(),
                                sub.startOffset(),
                                sub.endOffset());
                    }
                } else {
                    m.addHit(
                            field,
                            mi.startPosition(),
                            mi.endPosition(),
                            mi.startOffset(),
                            mi.endOffset());
                }
            }
        }
        return m;
    }
}
