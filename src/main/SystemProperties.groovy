import static datomic.Peer.q;

q("[:find ?k :in [[?k]]]",
  System.getProperties());

q("""[:find ?k 
      :in [[?k ?v]]
      :where [(.endsWith ^String ?k "path")]]""",
  System.getProperties());

q("""[:find ?pathElem 
      :in [[?k ?v]]
      :where [(.endsWith ^String ?k "path")]
             [(.split ^String ?v ":") [?pathElem ...]]]""",
  System.getProperties());

q("""[:find ?pathElem 
      :in [[?k ?v]]
      :where [(.endsWith ^String ?k "path")]
             [(.split ^String ?v ":") [?pathElem ...]]
             [(.endsWith ^String ?pathElem "jar")]]""",
  System.getProperties());
     
