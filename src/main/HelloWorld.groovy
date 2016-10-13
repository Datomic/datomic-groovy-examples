import datomic.Peer;

uri = 'datomic:mem://hello';
Peer.createDatabase(uri);
conn = Peer.connect(uri);

tempid = { Peer.tempid(':db.part/user') }

txresult = conn.transact([[':db/add', tempid(), ':db/doc', 'Hello world']]);
txresult.get();

dbval = conn.db();

qresult = Peer.query('''[:find [?e ...]
                         :in $ ?str
                         :where [?e :db/doc ?str]]''',
                     dbval,
                     'Hello world');

ent = dbval.entity(qresult[0]);
ent.touch();

docEnt = dbval.entity(':db/doc');
docEnt.touch();
