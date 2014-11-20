// inspired by http://www.lshift.net/blog/2010/08/21/some-relational-algebra-with-datatypes-in-clojure-12
import static datomic.Peer.query;

tuplify = { instance,fields ->
  fields.collect {instance[it]};
}
beansToRelation = { beans, fields ->
  beans.collect { tuplify(it, fields) }
}

class Supplier { def number; def name; def status; def city }
class Part { def number; def name; def colour; def weight; def city }
class Shipment { def supplier; def part; def quantity }

suppliers = [new Supplier(number:'S1', name:'Smith', status:20, city:'London'),
             new Supplier(number:'S2', name:'Jones', status:10, city:'Paris'),
             new Supplier(number:'S3', name:'Blake', status:30, city:'Paris')];

parts = [new Part(number:'P1', name:'Nut', colour:'Red', weight:12.0, city:'London'),
         new Part(number:'P2', name:'Bolt', colour:'Green', weight:17.0, city:'Paris'),
         new Part(number:'P3', name:'Screw', colour:'Blue', weight:17.0, city:'Oslo')];

shipments = [new Shipment(supplier:'S1', part:'P1', quantity:300),
             new Shipment(supplier:'S2', part:'P2', quantity:200),
             new Shipment(supplier:'S2', part:'P3', quantity:400)];

// parameterized query
query('''[:find [?name ...]
          :in $ ?city
          :where [?city ?name]]''',
      beansToRelation(suppliers, ['city', 'name']),
      'Paris');

// query with cross-'database' join
query('''[:find [?name ...]
          :in $suppliers $shipments 
          :where [$suppliers ?sup ?name ?city]
                 [$shipments ?sup]]''',
      beansToRelation(suppliers, ['number', 'name', 'city']),
      beansToRelation(shipments, ['supplier']),
      'Paris');

