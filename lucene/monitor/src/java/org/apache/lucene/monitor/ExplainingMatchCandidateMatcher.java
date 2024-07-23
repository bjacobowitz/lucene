package org.apache.lucene.monitor;

import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;

import java.io.IOException;
import java.util.Map;

public class ExplainingMatchCandidateMatcher extends CandidateMatcher<ExplainingMatch> {
    public ExplainingMatchCandidateMatcher(IndexSearcher searcher) {
        super(searcher);
    }

    @Override
    protected void matchQuery(
            String queryId, Query matchQuery, Map<String, String> metadata) throws IOException {
        int maxDocs = searcher.getIndexReader().maxDoc();
        for (int i = 0; i < maxDocs; i++) {
            Explanation explanation = searcher.explain(matchQuery, i);
            if (explanation.isMatch()) addMatch(new ExplainingMatch(queryId, explanation), i);
        }
    }

    @Override
    public ExplainingMatch resolve(ExplainingMatch match1, ExplainingMatch match2) {
        return new ExplainingMatch(
                match1.getQueryId(),
                Explanation.match(
                        match1.getExplanation().getValue().doubleValue()
                                + match2.getExplanation().getValue().doubleValue(),
                        "sum of:",
                        match1.getExplanation(),
                        match2.getExplanation()));
    }
}
