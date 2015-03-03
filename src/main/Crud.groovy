import datomic.Peer;
import static datomic.Peer.query;
import datomic.Connection;
import static datomic.Connection.DB_AFTER;

// new database
uri = 'datomic:free://localhost:4334/crud-example';
Peer.createDatabase(uri);
conn = Peer.connect(uri);

// attribute schema for :crud/name
txresult = conn.transact([[':db/id': Peer.tempid(':db.part/db'),
                           ':db/ident': ':crud/name',
                           ':db/valueType': ':db.type/string',
                           ':db/unique': ':db.unique/identity',
                           ':db/cardinality': ':db.cardinality/one',
                           ':db.install/_attribute': ':db.part/db']]);
txresult.get();

// create, awaiting point-in-time-value
txresult = conn.transact([[':db/add',
                           Peer.tempid(':db.part/user'),
                           ':crud/name',
                           'Hello world']]);
dbAfterCreate = txresult.get().get(DB_AFTER)

// read
dbAfterCreate.pull('[*]', [':crud/name', 'Hello world'])

// update
txresult = conn.transact([[':db/add', [':crud/name', 'Hello world'],
                           ':db/doc', 'An entity with only demonstration value']]);

txresult.get().get(DB_AFTER).pull('[*]', [':crud/name', 'Hello world']);

// "delete" adds new information, does not erase old
txresult = conn.transact([[':db.fn/retractEntity', [':crud/name', 'Hello world']]]);
dbAfterDelete = txresult.get().get(DB_AFTER);

// no present value for "deleted" entity
dbAfterDelete.pull('[*]', [':crud/name', 'Hello world']);

// but everything ever said is still there
historyDb = dbAfterDelete.history();

everything = query('''[:find ?e ?a ?v ?tx ?op
                       :in $
                       :where [?e :crud/name "Hello world"]
                              [?e ?a ?v ?tx ?op]]''',
                   historyDb);

// prettyPrint
import static groovy.json.JsonOutput.*;
prettyPrint(toJson(everything));
