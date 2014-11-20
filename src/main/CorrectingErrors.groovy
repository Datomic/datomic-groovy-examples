// N.B. This example uses the log and requires a durable db.

import datomic.Peer;
import static datomic.Peer.*;
import datomic.Util;

uri = 'datomic:free://localhost:4334/correcting-errors-example';
createDatabase(uri);
conn = Peer.connect(uri);
Peer.classLoader.getResource('inventory.edn').withReader {
  Util.readAll(it).each { tx ->
    conn.transact(tx).get();
  }
}

conn.transact([[':db/id': tempid(':db.part/user'),
                ':manager/email': 'jdoe@example.com']]).get();

addTribbles = [[':db/id': tempid(':db.part/user'),
                ':item/id': '0042-TRBL',
                ':item/description': 'Tribble: a low maintenance pet.',
                ':item/count': 999]];

conn.transact(addTribbles).get();
db = conn.db();

// entity view of tribble record
db.entity([':item/id', '0042-TRBL']).touch();

// individual datoms
query('''[:find ?a ?v ?tx ?added
          :in $ ?e
          :where [?e ?a ?v ?tx ?added]]''',
  db,
  [':item/id', '0042-TRBL']);

// find the most recent transaction
log = conn.log();
query('''[:find ?e ?aname ?v ?tx ?added
          :in $ ?log ?tx
          :where [(tx-data ?log ?tx) [[?e ?a ?v _ ?added]]]
                 [(not= ?e ?tx)]
                 [?a :db/ident ?aname]]''',
      db,
      log,
      db.basisT());

// error correction example 1:
// correct a single datom, without attribution
errDoc = 'Error correction entry. We do not sell Tribbles.';
weDontSellTribbles = 
[[':db/add', [':item/id', '0042-TRBL'], ':item/count', 0],
 [':db/add', tempid(':db.part/tx'), ':db/doc', errDoc]];
conn.transact(weDontSellTribbles).get();

// error correction example 2:
// retract an entire entity, with attribution
retractTribbles = 
[[':db.fn/retractEntity', [':item/id', '0042-TRBL']],
 [':db/add', tempid(':db.part/tx'), ':corrected/by', [':manager/email', 'jdoe@example.com']]];
conn.transact(retractTribbles).get();

hist = conn.db().history();
// history of tribbles
query('''[:find ?a ?v ?tx ?added
          :in $ ?e
          :where [?e ?a ?v ?tx ?added]]''',
      hist,
      [':item/id', '0042-TRBL']);

// correction txes by jdoe
jdoeTxes = query('''[:find ?tx
                     :in $ ?e
                     :where [?tx :corrected/by ?e]]''',
                 hist,
                 [':manager/email', 'jdoe@example.com']);

// exact datoms corrected
query('''[:find ?e ?aname ?v ?tx ?added
          :in $ ?log [[?tx]]
          :where [(tx-data ?log ?tx) [[?e ?a ?v _ ?added]]]
                 [(not= ?e ?tx)]
                 [?a :db/ident ?aname]]''',
      hist,
      log,
      jdoeTxes);
