import static datomic.Peer.query;

// bind vars
query('''[:find ?first ?last
          :in ?first ?last]''',
      'John', 'Doe');

// bind tuples
query('''[:find ?first ?last
          :in [?first ?last]]''',
      ['John', 'Doe']);

// bind a collection
query('''[:find [?first ...]
          :in [?first ...]]''',
      ['John', 'Jane', 'Phineas']);

// bind a relation
query('''[:find [?first ...]
      :in [[?first ?last]]]''',
  [['John', 'Doe'],
   ['Jane', 'Doe']]);

r = datomic.Util.&read;

// bind a 'database'
query('''[:find [?first ...]
      :where [_ :first-name ?first]]''',
  [[42, r(':first-name'), 'John'],
   [42, r(':last-name'), 'Doe'],
   [43, r(':first-name'), 'Jane'],
   [43, r(':last-name'), 'Doe']]);
