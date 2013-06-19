import datomic.Peer;
import static datomic.Peer.q;

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

productQuery = '''[:find ?e
                   :in $ ?v
                   :where [?e :product/description ?v]]''';

(chocolate, whisky) = ['Expensive Chocolate', 'Cheap Whisky'].collect {
  q(productQuery, conn.db(), it)[0][0];
}

order = [['order/lineItems': [['lineItem/product': chocolate,
                               'lineItem/quantity': 1,
                               'lineItem/price': 48.00,
                               'orderSystem/note': 'please send original packaging'],
                              ['lineItem/product': whisky,
                               'lineItem/quantity': 2,
                               'lineItem/price': 38.00]],
          'db/id': tempid()]];

conn.transact(order).get();

db = conn.db();
ordersByProductQuery = '''[:find ?e
                           :in $ ?productDesc
                           :where [?e :order/lineItems ?item]
                                  [?item :lineItem/product ?prod]
                                  [?prod :product/description ?productDesc]]''';

// qe = query for entity, finds first matching entity
qe = { query, db, Object[] more ->
  db.entity(q(query, db, *more)[0][0])
}

// lookup the order we just made
chocolateOrder = qe(ordersByProductQuery, db, 'Expensive Chocolate');                                 

// will recursively touch line items, but not products
chocolateOrder.touch();

// meh, cancel that order
conn.transact([[":db.fn/retractEntity", chocolateOrder[":db/id"]]]).get();

db = conn.db();

// all the line items are now gone
q('''[:find ?e
      :where [?e :order/lineItems]]''',
  db).size();

// but the products remain
q('''[:find ?e
      :where [?e :product/description]]''',
  db).size();
