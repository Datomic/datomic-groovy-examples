import datomic.Peer;
import static datomic.Peer.q;

conn = News.newTutorialConnection();
dbval = conn.db();

allStories = Peer.q("[:find ?e :where [?e :story/url]]", dbval).collect {
  dbval.entity(it[0]);
}

tempid = { Peer.tempid(":db.part/user") }
newUserId = tempid();

// create data to upvote all stories
upvoteAllStories = allStories.collect {
  [":db/add", newUserId, ":user/upVotes", it[":db/id"]]
}

// create data for a new user
newUser = [[":db/id": newUserId,
            ":user/email": "john@example.com",
            ":user/firstName": "John",
            ":user/lastName": "Doe"]];

// compose a transaction from data in hand
upvoteResult = conn.transact(upvoteAllStories + newUser).get()

// change an existing entity via upsert
changeNameResult = conn.transact([[":user/email": "john@example.com",
                                   ":db/id": tempid(),
                                   ":user/firstName": "Johnathan"]]).get();

// qe = query for entity
qe = { query, db, Object[] more ->
  db.entity(q(query, db, *more)[0][0])
}

dbval = conn.db();
john = qe("""[:find ?e
             :in \$ ?email
             :where [?e :user/email ?email]]""",
          dbval,
          "john@example.com");


johnUpvotesGraham = qe("""[:find ?story
                           :in \$ ?user ?title
                           :where [?user :user/upVotes ?story]
                                  [?story :story/url ?title]]""",
                       dbval,
                       john[":db/id"],
                       "http://www.paulgraham.com/avg.html");

//retract that upvote
conn.transact([[":db/retract", 
                john[":db/id"], 
                ":user/upVotes",
                johnUpvotesGraham.get(":db/id")]]).get();

// get more recent view of John
john = conn.db().entity(john[":db/id"]);

// now only two upvotes
john[":user/upVotes"];

// prepare to retract all John's upvotes
// use a query to make transaction data
retractUpvotes = q("""[:find ?op ?e ?a ?v
                       :in \$ ?op ?e ?a
                       :where [?e ?a ?v]]""",
                   conn.db(),
                   ":db/retract",
                   john[":db/id"],
                   ":user/upVotes");

// retract the upvotes
conn.transact(retractUpvotes.asList()).get();

// and now they are gone
john = conn.db().entity(john[":db/id"])[":user/upVotes"];




