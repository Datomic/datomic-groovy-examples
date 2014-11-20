import datomic.Peer;
import static datomic.Peer.*;
import static datomic.Connection.*;
import static datomic.Util.*;

conn = News.newTutorialConnection();
db = conn.db();

stuId = query('''[:find ?e .
                  :in $ ?email
                  :where [?e :user/email ?email]]''', 
              db,
              'stuarthalloway@datomic.com');

ecURL = "http://blog.datomic.com/2012/09/elasticache-in-5-minutes.html";

result = conn.transact([[":db/id": tempid(":db.part/user"),
                         ":story/title": "ElastiCache in 6 minutes",
                         ":story/url": ecURL],
                        [":db/id": tempid("db.part/user"),
                         ":story/title": "Keep Chocolate Love Atomic",
                         ":story/url": "http://blog.datomic.com/2012/08/atomic-chocolate.html"],
                        [":db/id": tempid(":db.part/tx"),
                         ":source/user": stuId]]);

db = result.get()[DB_AFTER];
t = db.basisT();
tx = db.entity(Peer.toTx(t)); 

// wall clock time of tx
inst = tx[":db/txInstant"]

editorId = query('''[:find ?e .
              :in $ ?email
              :where [?e :user/email ?email]]''', 
             db,
             'editor@example.com');               

// fix spelling error in title
conn.transact([[":db/id": tempid(":db.part/user"),
                ":story/title": "ElastiCache in 5 minutes",
                ":story/url": ecURL],
               [":db/id": tempid(":db.part/tx"),
                ":source/user": editorId]]);

// what is the title now?
query('''[:find ?v .
          :in $ ?url
          :where [?e :story/title ?v]
                 [?e :story/url ?url]]''',
      conn.db(), ecURL);

// what was the title earlier?
query('''[:find ?v .
          :in $ ?url
          :where [?e :story/title ?v]
                 [?e :story/url ?url]]''',
      conn.db().asOf(inst), ecURL);

// who changed the title, and when?
query('''[:find ?e ?v ?email ?inst ?added
          :in $ ?url
          :where
          [?e :story/title ?v ?tx ?added]
          [?e :story/url ?url]
          [?tx :source/user ?user]
          [?tx :db/txInstant ?inst]
          [?user :user/email ?email]]''',
      conn.db().history(),
      ecURL).sort {a,b ->
        a[3] <=> b[3]
    }

storyId = query('''[:find ?e .
                    :in $ ?url
                    :where [?e :story/url ?url]]''',
                conn.db(), ecURL);

query('''[:find ?aname ?v ?tx ?inst ?added
          :in $ ?e
          :where
          [?e ?a ?v ?tx ?added]
          [?a :db/ident ?aname]
          [?tx :db/txInstant ?inst]]''',
      conn.db().history(),
      storyId).sort {a,b ->
        a[2] <=> b[2]
    }
