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

import java.io.IOException;
import java.util.Collections;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

public class TestFieldFilteredMultipassPresearcher extends FieldFilterPresearcherComponentTestBase {

  @Override
  protected Presearcher createPresearcher() {
    return new MultipassTermFilteredPresearcher(
        2, 0, TermWeightor.DEFAULT, Collections.emptyList(), Collections.singleton("language"));
  }

  public void testFieldFiltering() throws IOException {

    try (Monitor monitor = newMonitor()) {
      monitor.register(
          new MonitorQuery("1", parse("test"), null, Collections.singletonMap("language", "en")),
          new MonitorQuery("2", parse("test"), null, Collections.singletonMap("language", "de")),
          new MonitorQuery("3", parse("wibble"), null, Collections.singletonMap("language", "en")),
          new MonitorQuery("4", parse("*:*"), null, Collections.singletonMap("language", "de")));

      Document enDoc = new Document();
      enDoc.add(newTextField(TEXTFIELD, "this is a test", Field.Store.NO));
      enDoc.add(newTextField("language", "en", Field.Store.NO));

      MatchingQueries<QueryMatch> en = monitor.match(enDoc, QueryMatch.SIMPLE_MATCHER);
      assertEquals(1, en.getMatchCount());
      assertNotNull(en.matches("1"));
      // Two queries have filter field (language) en, multipass presearcher filters out query 3
      assertEquals(1, en.getQueriesRun());

      Document deDoc = new Document();
      deDoc.add(newTextField(TEXTFIELD, "das ist ein test", Field.Store.NO));
      deDoc.add(newTextField("language", "de", Field.Store.NO));

      MatchingQueries<QueryMatch> de = monitor.match(deDoc, QueryMatch.SIMPLE_MATCHER);
      assertEquals(2, de.getMatchCount());
      // Two queries have filter field (language) de
      assertEquals(2, de.getQueriesRun());
      assertNotNull(de.matches("2"));
      assertNotNull(de.matches("4"));

      Document bothDoc = new Document();
      bothDoc.add(newTextField(TEXTFIELD, "this is ein test", Field.Store.NO));
      bothDoc.add(newTextField("language", "en", Field.Store.NO));
      bothDoc.add(newTextField("language", "de", Field.Store.NO));

      MatchingQueries<QueryMatch> both = monitor.match(bothDoc, QueryMatch.SIMPLE_MATCHER);
      assertEquals(3, both.getMatchCount());
      // All queries have filter field (language) de or en,
      // multipass presearcher can filter out query 3
      assertEquals(3, both.getQueriesRun());
    }
  }
}
