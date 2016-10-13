import datomic.Peer;
import static datomic.Peer.query;

conn = News.newTutorialConnection();
dbval = conn.db();

// use the default partition for data
tempid = { Peer.tempid(':db.part/user') }

// add some products
products = ['Expensive Chocolate', 'Cheap Whisky'].collect {
  [':product/description': it,
   ':db/id': tempid()]
}

conn.transact(products).get();

productQuery = '''[:find [?e ...]
                   :in $ ?v
                   :where [?e :product/description ?v]]''';

(chocolate, whisky) = ['Expensive Chocolate', 'Cheap Whisky'].collect {
  query(productQuery, conn.db(), it).get(0);
}

order = [[':order/lineItems': [[':lineItem/product': chocolate,
                               ':lineItem/quantity': 1,
                               ':lineItem/price': 48.00,
                               ':orderSystem/note': 'please send original packaging'],
                              [':lineItem/product': whisky,
                               ':lineItem/quantity': 2,
                               ':lineItem/price': 38.00]],
          ':db/id': tempid()]];

conn.transact(order).get();

db = conn.db();
ordersByProductQuery = '''[:find [?e ...]
                           :in $ ?productDesc
                           :where [?e :order/lineItems ?item]
                                  [?item :lineItem/product ?prod]
                                  [?prod :product/description ?productDesc]]''';

// qfirste = query for first entity, finds first matching entity
qfirste = { q, db, Object[] more ->
  db.entity(query(q, db, *more).get(0))
}

// lookup the order we just made
order = qfirste(ordersByProductQuery, db, 'Expensive Chocolate');

// will recursively touch line items, but not products
order.touch();

// meh, cancel that order
conn.transact([[":db.fn/retractEntity", order[":db/id"]]]).get();

db = conn.db();

// all the line items are now gone
query('''[:find (count ?e)
          :where [?e :order/lineItems]]''',
      db);

// but the products remain
query('''[:find (count ?e)
          :where [?e :product/description]]''',
      db);
