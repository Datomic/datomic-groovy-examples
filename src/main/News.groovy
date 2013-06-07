import datomic.Peer;
import datomic.Util;

class News {
  static rnd = new Random();

  static findAll(db, attr) {
    Peer.q("[:find ?e :in \$ ?attr :where [?e ?attr]]", db, attr)*.get(0);                
  }
  static randNth(coll) {
    coll.empty ? null : coll[(rnd.nextInt(coll.size))];
  }
  static generateComments(db, n) {
    def storyIds = findAll(db, ":story/url");
    def userIds = findAll(db, ":user/email");
    def commentIds = findAll(db, ":comment/author");
    (0..<n).collect {
      def parentId = randNth(commentIds) ?: randNth(storyIds);
      def commentId = Peer.tempid("db.part/user");
      [[":db/add", parentId, ":comments", commentId],
       [":db/add", commentId, ":comment/author", randNth(userIds)],
       [":db/add", commentId, ":comment/body", "blah"]]
    }.inject([]) {acc,val ->
      acc.addAll(val);
      acc
    }
  }
  static newTutorialConnection() {
    def uri = "datomic:mem://" + Peer.squuid();
    Peer.createDatabase(uri);
    def conn = Peer.connect(uri);
    Peer.classLoader.getResource('day-of-datomic.edn').withReader {
      Util.readAll(it).each { tx ->
        conn.transact(tx);
      }
    }
    4.times {
      conn.transact(generateComments(conn.db(), 5))
    }
    conn
  }
}

  
