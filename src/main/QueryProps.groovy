import static datomic.Peer.query;

query('''[:find ?k ?v
          :where [(System/getProperties) [[?k ?v]]]]''');

query('''[:find ?k ?v
          :where [(System/getProperties) [[?k ?v]]]
                 [(.endsWith ?k "path")]]''');

query('''[:find ?k ?v
          :where [(System/getProperties) [[?k ?v]]]
                 [(.endsWith ^String ?k "path")]]''');
