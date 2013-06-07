import static datomic.Peer.q;

// bind vars
q("""[:find ?first ?last
      :in ?first ?last]""",
  "John", "Doe");

// bind tuples
q("""[:find ?first ?last
      :in [?first ?last]]""",
  ["John", "Doe"]);

// bind a collection
q("""[:find ?first
      :in [?first ...]]""",
  ["John", "Jane", "Phineas"]);

// bind a relation
q("""[:find ?first
      :in [[?first ?last]]]""",
  [["John", "Doe"],
   ["Jane", "Doe"]]);

r = datomic.Util.&read;

// bind a 'database'
q("""[:find ?first
      :where [_ :first-name ?first]]""",
  [[42, r(":first-name"), "John"],
   [42, r(":last-name"), "Doe"],
   [43, r(":first-name"), "Jane"],
   [43, r(":last-name"), "Doe"]]);
