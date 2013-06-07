import static datomic.Peer.*;
import static datomic.Connection.*;
import static datomic.Util.*;

conn = News.newTutorialConnection();

db = conn.db();

q("[:find ?e :where [?e :user/email]]", db);                

// bind a single query input
q("""[:find ?e
      :in \$ ?email
      :where [?e :user/email ?email]]""",
  db, "editor@example.com");

// collection binding form
q("""[:find ?e
      :in \$ [?email ...]
      :where [?e :user/email ?email]]""",
  db, ["editor@example.com", "stuarthalloway@datomic.com"]);

// join
q("""[:find ?comment
      :in \$ ?email
      :where [?user :user/email ?email]
             [?comment :comment/author ?user]]""",
  db, "editor@example.com")

// aggregration
q("""[:find (count ?comment)
       :in \$ ?email
       :where [?user :user/email ?email]
              [?comment :comment/author ?user]]""",
  db, "editor@example.com")


// no results : there are no comments about people
q("""[:find (count ?comment)
       :where
       [?comment :comment/author]
       [?commentable :comments ?comment]
       [?commentable :user/email]]""",
  db)

// schema query
q("""[:find ?attr-name
       :where
       [?ref :comments]
       [?ref ?attr]
       [?attr :db/ident ?attr-name]]""",
  db)

// entity API
editorId = q("""[:find ?e
                 :in \$ ?email
                 :where [?e :user/email ?email]]""",
             db, "editor@example.com").first().first();
editor = db.entity(editorId)             

editor.keySet()
editor.get(":user/email")
editor.get(":comment/_author")
editor.get(":comment/_author")*.get(":comments")

// reified transactions
txId = q("""[:find ?tx
                :in \$ ?e
                :where [?e :user/firstName _ ?tx]]""",
         db, editorId).first().first();

Peer.toT(txId);

// when was first name asserted?
db.entity(txId).get(":db/txInstant");

// what was it like before that?
olderDb = db.asOf(txId - 1);
olderDb.entity(editorId).get(":user/firstName");

// review entire history of an attribute
hist = db.history();
q("""[:find ?tx ?v ?op ?date
      :in \$ ?e ?attr
      :where [?e ?attr ?v ?tx ?op]
             [?tx :db/txInstant ?date]]""",
  hist, 
  editorId, 
  ":user/firstName").sort { a,b -> 
    a[0] <=> b[0] 
}.each { println it}


