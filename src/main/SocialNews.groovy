import datomic.Peer;
import static datomic.Peer.query;

conn = News.newTutorialConnection();
dbval = conn.db();

allStories = query('[:find [?e ...] :where [?e :story/url]]', dbval).collect {
  dbval.entity(it);
}

tempid = { Peer.tempid(':db.part/user') }
newUserId = tempid();

// create data to upvote all stories
upvoteAllStories = allStories.collect {
  [':db/add', newUserId, ':user/upVotes', it[':db/id']]
}

// create data for a new user
newUser = [[':db/id': newUserId,
            ':user/email': 'john@example.com',
            ':user/firstName': 'John',
            ':user/lastName': 'Doe']];

// compose a transaction from data in hand
upvoteResult = conn.transact(upvoteAllStories + newUser).get()

// change an existing entity via upsert
changeNameResult = conn.transact([[':user/email': 'john@example.com',
                                   ':db/id': tempid(),
                                   ':user/firstName': 'Johnathan']]).get();


// find the user we just transacted
dbval = conn.db();
john_id = query('''[:find ?e .
                   :in $ ?email
                   :where [?e :user/email ?email]]''',
                dbval,
                'john@example.com');

johnUpvotesGraham_id = query('''[:find ?story .
                                 :in $ ?user ?title
                                 :where [?user :user/upVotes ?story]
                                        [?story :story/url ?title]]''',
                             dbval,
                             john_id,
                             'http://www.paulgraham.com/avg.html');

johnUpvotesGraham = dbval.entity(johnUpvotesGraham_id)

//retract that upvote
conn.transact([[':db/retract', 
                john_id, 
                ':user/upVotes',
                johnUpvotesGraham.get(':db/id')]]).get();

// get more recent view of John
john = conn.db().entity(john_id);

// now only two upvotes
john.get(':user/upVotes')

// prepare to retract all John's upvotes
// use a query to make transaction data
retractUpvotes = query('''[:find ?op ?e ?a ?v
                           :in $ ?op ?e ?a
                           :where [?e ?a ?v]]''',
                       conn.db(),
                       ':db/retract',
                       john_id,
                       ':user/upVotes');

// retract the upvotes
conn.transact(retractUpvotes.asList()).get();

// and now they are gone
john = conn.db().entity(john_id)[':user/upVotes'];
