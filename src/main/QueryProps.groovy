import static datomic.Peer.q;

q('''[:find ?k ?v
      :where [(System/getProperties) [[?k ?v]]]]''');

q('''[:find ?k ?v
      :where [(System/getProperties) [[?k ?v]]]
             [(.endsWith ?k "path")]]''');

q('''[:find ?k ?v
      :where [(System/getProperties) [[?k ?v]]]
             [(.endsWith ^String ?k "path")]]''');
