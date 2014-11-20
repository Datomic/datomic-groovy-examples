import static datomic.Peer.query;

props = System.getProperties()

query('[:find [?k ...] :in [[?k]]]',
      props);

query('''[:find [?k ...] 
          :in [[?k ?v]]
          :where [(.endsWith ^String ?k "path")]]''',
      props);

query('''[:find ?pathElem 
          :in [[?k ?v]]
          :where [(.endsWith ^String ?k "path")]
                 [(.split ^String ?v ":") [?pathElem ...]]]''',
      props);

query('''[:find ?pathElem 
          :in [[?k ?v]]
          :where [(.endsWith ^String ?k "path")]
                 [(.split ^String ?v ":") [?pathElem ...]]
                 [(.endsWith ^String ?pathElem "jar")]]''',
      props);
